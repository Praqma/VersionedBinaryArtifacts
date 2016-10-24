package net.praqma.gradle

import groovy.text.SimpleTemplateEngine
import groovy.transform.CompileStatic
import groovy.transform.Memoized
import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.GradleException

@CompileStatic
class Utils {

    private static final String SNAPSHOT_VERSION_SUFFIX = '-SNAPSHOT'
    private static final String PRE_RELEASE_SEPARATOR = "-"

    /**
     * Split a version string into major, minor, and patch version. Also include a flag if it
     * is a snapshot version or not.
     */
    static Map<String, Object> versionParts(String completeVersion) {
        String version = completeVersion

        boolean isSnapshot = completeVersion.endsWith(SNAPSHOT_VERSION_SUFFIX)
        boolean isPreRelease = version.contains(PRE_RELEASE_SEPARATOR)
        String preReleaseVersion = ""
        if (isPreRelease) {
            int sepIdx = version.indexOf(PRE_RELEASE_SEPARATOR)
            version = version[0..sepIdx-1]
            preReleaseVersion = completeVersion[sepIdx..-1]
        }

        String[] parts = version.split("\\.")
        if (parts.size() != 3) {
            throw new GradleException("Version must have 3 parts. Version is: ${version}")
        }
        return [
                majorVersion: parts[0] as Integer,
                minorVersion: parts[1] as Integer,
                patchVersion: parts[2] as Integer,
                version     : completeVersion,
                snapshot    : isSnapshot,
                preReleaseVersion : preReleaseVersion
        ]
    }

    @Memoized
    static String gitSha() {
        'git rev-parse --verify HEAD'.execute().text // TODO handle failure
    }
    @Memoized
    static String gitBranch(){
      'git rev-parse --symbolic-full-name HEAD'.execute().text
    }

    static String templateExpand(Map expansions, String input) {
        templateExpand(input, expansions)
    }

    static String templateExpand(String input, Map expansions) {
        SimpleTemplateEngine engine = new SimpleTemplateEngine()
        return engine.createTemplate(input).make(expansions).toString()
    }

    static String osFamily() {
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            return 'win32'
        }
        if (Os.isFamily(Os.FAMILY_MAC)) {
            return 'osx'
        }
        if (Os.isFamily(Os.FAMILY_UNIX)){
            return 'unix'
        }
        throw new RuntimeException("Unable to determine OS family")
    }

    static String buildCmd(String os, Properties props) {

        String lookup = "buildCmd." + os
        return props.get(lookup, props.get("buildCmd"))
    }
}
