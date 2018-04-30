package net.praqma

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import net.praqma.tasks.FileTemplateTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Zip


@CompileStatic
class BuildPublishing {

    private final BuildExtension extension

    BuildPublishing(BuildExtension extension) {
        this.extension = extension
    }

    @CompileDynamic
    void zip(Closure closure) {
        String pubName = 'defaultPub'
        Project project = extension.project
        String depDir = extension.resolvedDepDir

        FileTemplateTask createBuildInfoTask = project.createBuildInfo
        Task executeBuildTask = project.executeBuildCommand


        Task task = project.tasks.create(name: "create${pubName.capitalize()}", type: Zip) {
            dependsOn executeBuildTask
            with project.copySpec(closure)

            into ("dep"){
              from createBuildInfoTask
              from {
                project.fileTree(dir: depDir, include: '**/dep/*.properties', exclude: ['**/bin/', '**/include/', '**/hex/', '**/lib/', '**/lib/', '**/doc/', '**/src/' ])
              }
            }
        }

        project.publishing.publications {
                "${pubName}"(MavenPublication) {
                    groupId extension.group
                    version extension.version
                    artifactId extension.artifact

                    artifact task
                }
        }
    }
}
