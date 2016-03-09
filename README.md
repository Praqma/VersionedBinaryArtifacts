# GradleBuildProperties
A gradle plugin to allow a build be defined in a properties file

## Key features

A simple properties file where you can describe:

 - The artifact name
 - The version
 - The group for repository
 - The build command to run (e.g. `make all`)
 - The build directory to run from
 - The dependencies that must be resolved

Key features:
 - Automatic pre-build generation of a version.h and build.h to use in the build process

 - Creation of a buildInfo.properties

````
BUILD_TIMESTAMP=Wed Nov 04 15:05:20 CET 2015
BUILD_NUMBER=0
BUILD_URL=DEVELOPER
GIT_SHA=b3d9cf1ad981e6597ab31d9132336da623b50de2

VERSION=1.0.5-SNAPSHOT
````

## How to build and test

Build the plugin:

    ./gradlew pTML

There is a test project to test the plugin:

    cd test
    ../gradlew pTML

## Opinionated

Assumes Artifactory

If the version contains SNAPSHOT, then it publishes to the libs-snapshot-local repo
Otherwise it publishes to libs-release-local

All dependencies are

The repository is selected based on the version in the build.properties file:

````
libs-snapshot-local
libs-release-local
````

# Customer wish list

Novelda:

 - Don’t overwrite buildinfo file when there are multiple dependencies
 - Add information about the version of dependencies used in the built to buildinfo file
 - Allow separate build commands per platform
 - Allow to force the platform from the commandline
