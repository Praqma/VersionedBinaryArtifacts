# VersionedBinaryArtifacts

[![Build Status](https://travis-ci.org/Praqma/VersionedBinaryArtifacts.svg?branch=develop)](https://travis-ci.org/Praqma/VersionedBinaryArtifacts)

Gradle plugin: https://plugins.gradle.org/plugin/net.praqma.vba

Define your build in a configurations file with the VBA gradle plugin

````
group=net.praqma
version=1.0.5
publishRepo=libs-release-local
artifact=demo-project-${osFamily}
resolvedDepDir=path/to/your/download/folder
buildCmd=${osFamily == 'win32' ? 'build.bat' : './build.sh'}
dependencies=net.praqma:demo-project-${osFamily}:1.0.9-SNAPSHOT
````

In the properties file you can describe:

 - The artifact name
 - The version
 - The repository of your binary manager (e.g. nexus, artifactory)
 - The group (e.g. net.praqma)
 - The build command to run (e.g. `make all`)
 - The build directory to run from
 - The dependencies that must be resolved
 - The folder your dependencies will end up in

## Key features:

 - Automatic pre-build generation of a version.h and build.h to use in the build process
 - Creation of a buildInfo.properties:

buildInfo.properties uses build_number and build_url from Jenkins build environment.

````
BUILD_TIMESTAMP=Wed Nov 04 15:05:20 CET 2015
BUILD_NUMBER=0
BUILD_URL=DEVELOPER
GIT_SHA=b3d9cf1ad981e6597ab31d9132336da623b50de2

VERSION=1.0.5-SNAPSHOT
````


## How to use the plugin

https://plugins.gradle.org/plugin/net.praqma.vba

in build.gradle file add:

````

plugins {
  id "net.praqma.vba" version "<version>"
}
````

and
````
buildproperties.publishing {
      zip {
          <the files you want uploaded>
      }
}
 ````


This plugin will then be downloaded from plugins.gradle.org

## Artifactory/Nexus setup


First time gradle wrapper is exectued, gradle creates a .gradle folder located in ~/ . Here you must add the file 'gradle.properties', containing necessary parameters for a successful connection to the binary repository manager.

 ````
repositoryManagerUsername=*artifactUserWithWriteAccess*
repositoryManagerPassword=*artifactUserPassword*
repositoryManagerUrl=http://127.0.0.1:8080/artifactory
 ````

The destination repository is determined by the 'publishRepo' in the build.properties file (default is libs-release-local)

eg:

````
./gradlew publish
````
will publish the artifact to libs.release.local


# Resolving dependencies
The binary dependencies needed for the build is, as mentioned, added to the build.properties file, separated by comma.  

_Snapshot artifacts can be based on both snapshots and release artifacts_
_Release artifacts can only be based on other release artifacts_  

Fetching the dependencies without building the project is done with:

`./gradlew resolveDep`
