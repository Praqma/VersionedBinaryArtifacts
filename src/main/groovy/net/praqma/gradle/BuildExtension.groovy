package net.praqma.gradle

import groovy.transform.CompileStatic
import org.gradle.api.GradleException
import org.gradle.api.Project

@CompileStatic
class BuildExtension {

    Project project

    BuildExtension(Project project) {
        this.project = project
    }

    String version
    String branch
    String group
    String artifact
    String productName

    String buildCmd
    File buildWorkingDir

    File versionDir

    @Lazy
    String dependencySnapshotRepoPath = "/libs-snapshot-local"
    @Lazy
    String dependencyReleaseRepoPath = "/libs-release-local"
    @Lazy
    String publishingSnapshotRepoPath = "/libs-snapshot-local"
    @Lazy
    String publishingReleaseRepoPath = "/libs-release-local"

    Collection<String> dependencies

    final BuildPublishing publishing = new BuildPublishing(this)

    BuildPublishing publishing(Closure closure) {
        publishing.with(closure)
        publishing
    }

    boolean isSnapshotVersion() {
        Utils.versionParts(getVersion()).snapshot
    }

    String getVersion() {
        if (this.@version == null) {
            throw new GradleException("'version' must be specified in build definition")
        }
        this.@version
    }

    /**
     * Expansions use for templating
     */
    Map<String, Object> getExpansions() {
        [
                sha           : Utils.gitSha(),
                branch        : Utils.gitBranch(),
                submodule     : Utils.gitSubmodule(),
                osFamily      : osFamily,
                targetArch    :
                    project.hasProperty("targetArch") ?
                         project.properties.targetArch :
                         osFamily,
                buildNumber   : buildNumber,
                buildUrl      : buildUrl,
                resolvedDepDir: resolveDepDir,
                productName   : productName
        ] + (Utils.versionParts(version) as Map)
    }

    // Some read-only properties
    final String osFamily = Utils.osFamily()

    final String buildNumber = System.getenv('BUILD_NUMBER') ?: '0'

    final String buildUrl = System.getenv('BUILD_URL') ?: "DEVELOPER"

    String getResolveDepDir() {
        "${project.buildDir}/resolvedDep"
    }
}
