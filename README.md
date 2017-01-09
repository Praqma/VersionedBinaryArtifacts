# VersionedBinaryArtifacts
A gradle plugin to allow a build be defined in a properties file:

````
group=net.praqma
version=1.0.5
artifact=demo-project-${osFamily}
buildWorkingDir=.
buildCmd=${osFamily == 'win32' ? 'build.bat' : './build.sh'}
dependencies=net.praqma:demo-project-${osFamily}:1.0.9-SNAPSHOT
````

In the properties file you can describe:

 - The artifact name
 - The version
 - The group for repository
 - The build command to run (e.g. `make all`)
 - The build directory to run from
 - The dependencies that must be resolved

## Key features:

 - Automatic pre-build generation of a version.h and build.h to use in the build process
 - Creation of a buildInfo.properties:

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

Or all in one:

    ./gradlew pTML && pushd test && ../gradlew pTML && popd



The 'test' project also contains the build.gradle file:

````
buildscript {
    repositories {
      mavenLocal()
    }
    dependencies {
        classpath "net.praqma:VersionedBinaryArtifacts:2.0.5" // 1)
    }
    configurations.classpath.resolutionStrategy.cacheChangingModulesFor 0, 'seconds'
}
apply plugin: 'net.praqma.uber' // 2)

buildproperties.publishing { // 3)
      zip {
          into("lib"){
              from file("libtest.a")
          }
      }
````

This file has three major roles:

1. Deciding the path, name and version of the dependency

2. How to apply the plugin

3. How to select which files to be included in the artifact

## Artifactory setup

Assumes access to Artifactory with standard paths for artifacts

````
libs-snapshot-local
libs-release-local
````
First time gradle wrapper is exectued, gradle creates a .gradle folder located in ~/ . Here you must add the file 'gradle.properties', containing necessary parameters for a successful connection to Artifactory.

 ````
artifactory_user=*artifactUserWithWriteAccess*
artifactory_password=*artifactUserPassword*
artifactory_contextUrl=http://127.0.0.1:8080/artifactory
 ````

The destination repository is determined by the parameter 'release' (default false)

eg:

````
./gradlew publish -P release=true
````
will publish the artifact to libs.release.local

````
./gradlew publish -P release=false || ./gradlew publish
````
will append '-SNAPSHOT' to version number declared in build.properties and publish the artifact to libs.snapshot.local

# Resolving dependencies
The binary dependencies needed for the build is, as mentioned, added to the build.properties file, separated by comma.  

_Snapshot artifacts can be based on both snapshots and release artifacts_
_Release artifacts can only be based on other release artifacts_  

Fetching the dependencies without building the project is done with:

`./gradlew resolveDep`
