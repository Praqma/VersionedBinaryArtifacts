package net.praqma.gradle

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import net.praqma.gradle.tasks.FileTemplateTask
import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.*
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Sync

@CompileStatic
class UberPlugin implements Plugin<Project> {

    private Project project

    @Override
    void apply(Project project) {
        this.project = project

        project.apply(plugin: 'base')
        project.apply(plugin: 'maven-publish')

        BuildExtension extension = project.extensions.create('buildproperties', BuildExtension, project) as BuildExtension

        File buildDefFile = project.file('build.properties')

        if (!buildDefFile.exists()) {
            throw new GradleException("No build definition file found. File expected: ${buildDefFile}")
        }
        buildDefFile.withInputStream {
            parseBuildDefinition(it, extension)
        }

        defineRepositoryForDependencies(extension)

        handleDependencies(project)

        createTasks(extension)

        setupPublishing(extension)
    }

    /**
     * Parse build definition, store info in the extension
     */
    private void parseBuildDefinition(InputStream input, BuildExtension buildExtension) {
        Properties props = new Properties()
        props.load(input)

        // Set properties not subject to template expansion
        buildExtension.with {
            version = props.version
            group = props.group ?: 'net.praqma'
        }
        Map expansions = buildExtension.expansions
        def x = { s -> s ? Utils.templateExpand(s as String, expansions) : null }
        // Set properties subject to expansions
        buildExtension.with {
            artifact = x(props.artifact)
            dependencies = (x(props.dependencies))?.tokenize(',')
            //buildCmd = props.buildCmd // buildCmd will be expanded later
            buildCmd = Utils.buildCmd(Utils.osFamily(), props)
            buildWorkingDir = project.file(x(props.buildWorkingDir) ?: project.projectDir)
            versionDir = props.versionDir ? project.file(x(props.versionDir)) : null
            productName = props.productName ?: 'demo_project' // Default value for backward capability, should probably be mandatory
        }

        project.version = buildExtension.version
        project.group = buildExtension.group

        project.gradle.projectsEvaluated {
            project.configurations.all { Configuration c -> c.resolutionStrategy.cacheChangingModulesFor 0, 'seconds' }
        }
    }

    @CompileDynamic
    private void defineRepositoryForDependencies(BuildExtension buildExtension) {
        boolean isSnapshot = buildExtension.isSnapshotVersion()
        String contextUrl = project.ext.properties.artifactory_contextUrl
        project.repositories {
            println "Artifactory URL   ==============    : ${contextUrl}"
            if (isSnapshot) {
                maven {
                    url contextUrl + buildExtension.dependencySnapshotRepoPath
                }
                mavenLocal()
            }
            maven {
                url contextUrl + buildExtension.dependencyReleaseRepoPath
            }
            mavenLocal()
        }
    }

    @CompileDynamic
    private void handleDependencies(Project project) {
        project.configurations.create('_lib')

        project.dependencies {
            project.buildproperties.dependencies.each {
                _lib it
            }
        }
    }

    @CompileDynamic
    private void createTasks(BuildExtension buildExtension) {
        Task t1, t2
        if (buildExtension.versionDir) {
            t1 = createBuildTask('createBuildHeaderFile', FileTemplateTask) {
                template '/templates/build.h'
                targetDir buildExtension.versionDir.path
            }
            t2 = createBuildTask('createVersionHeaderFile', FileTemplateTask) {
                template '/templates/version.h'
                targetDir buildExtension.versionDir.path
            }
        }

        createBuildTask('createBuildInfo', FileTemplateTask) {
            template '/templates/buildInfo.properties'
        }

        Sync t3 = createBuildTask('resolveDependencies', Sync) {
          project.configurations._lib.collect{
            from {project.zipTree(it)}
            into buildExtension.resolveDepDir+"/mydir"
          }
            //from(project.configurations._lib.collect { project.zipTree(it) })
            //project.logger.lifecycle project.configurations._lib
            //into buildExtension.resolveDepDir
        }

        String taskName = 'executeBuildCommand'
        if (project.buildproperties.buildCmd) {
            createBuildTask(taskName, Exec) {
                Map expansions = [includeDir: new File(t3.destinationDir, 'include').path] + project.buildproperties.expansions
                String command = Utils.templateExpand(project.buildproperties.buildCmd, expansions)

                description "Execute the external build command (${command})"
                boolean isWindows = Os.isFamily(Os.FAMILY_WINDOWS)

                workingDir = buildExtension.buildWorkingDir
                if (isWindows) {
                    executable 'cmd'
                    args '/C', command
                } else {
                    executable 'sh'
                    args '-c', command
                }

                doFirst {
                    boolean created = buildExtension.buildWorkingDir.mkdirs()
                    if (created) {
                        project.logger.info "Created working directory: ${buildExtension.buildWorkingDir}"
                    }
                }
            }
        } else {
            // Create a dummy build command that doesn't do anything
            createBuildTask(taskName, DefaultTask) {}
        }

        // Add dependencies regardless if build cmd is set or not
        project.executeBuildCommand {
            if (t1 != null) {
                dependsOn t1, t2
            }
            dependsOn t3
        }

        createBuildTask('buildInfo', DefaultTask) {
            description "List build properties specific information about the project"
            doLast {
                println "\n=== Project Info ===\n"
                project.buildproperties.with {
                    println "version        : ${version}"
                    println ""
                    println "buildCmd       : ${buildCmd}"
                    println "buildWorkingDir: ${buildWorkingDir}"
                    println ""
                    println "Dependencies:\n${dependencies.collect { "\t${it}\n" }.join()}"
                }
                println "\n\n"
            }
        }

        FileTemplateTask helpTask = createBuildTask('buildpropertiesHelp', FileTemplateTask) {
            description "Show build properties specific help about how to use Gradle"
            template '/buildpropertiesHelp.txt'
            outputs.upToDateWhen { false }
        }
        // The task writes Novelda help to a file. Now write that to stdout
        helpTask.doLast {
            println helpTask.destinationFile.text
        }
        // Make it the default task
        project.defaultTasks 'buildpropertiesHelp'
    }

    @CompileDynamic
    void setupPublishing(BuildExtension extension) {
        String repoUser = project.ext.properties.artifactory_user
        String repoPassword = project.ext.properties.artifactory_password
        String contextUrl = project.ext.properties.artifactory_contextUrl
        if (repoUser == null || repoPassword == null) {
            project.logger.lifecycle "Incomplete credentials for artifact repository. No publishing"
        } else {
            boolean isSnapshot = extension.isSnapshotVersion()
            String repoUrl = "${contextUrl}${isSnapshot ? extension.publishingSnapshotRepoPath : extension.publishingReleaseRepoPath}"
            project.publishing {
                repositories {
                    maven {
                        url repoUrl
                        credentials {
                            username repoUser
                            password repoPassword
                        }
                    }
                }
            }
        }
    }

    public <T extends Task> T createBuildTask(String name, Class<T> type, Closure closure) {
        Task t = project.tasks.create(name: name, type: type, group: 'BuildProperties', closure)
        return t
    }

}
