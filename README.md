# GradleBuildProperties
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

## Opinionated

Assumes Artifactory with standard paths for artifacts

````
libs-snapshot-local
libs-release-local
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
will append '-SNAPSHOT' to version number in build.properties and publish the artifact to libs.snapshot.local



