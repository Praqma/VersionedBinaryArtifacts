// Generated Fri Nov 18 12:11:41 GMT 2016

#ifndef _VERSION_H_
#define _VERSION_H_

#define PRODUCT_NAME  "default-name"
#define MAJOR_VERSION 0
#define MINOR_VERSION 0
#define PATCH_VERSION 0
#define PRE_RELEASE_VERSION "-SNAPSHOT"

#define xstr(s) str(s)
#define str(s) #s

#define VERSION_STRING xstr(MAJOR_VERSION) "."  xstr(MINOR_VERSION) "." xstr(PATCH_VERSION) PRE_RELEASE_VERSION
#include "build.h"
#define BUILD_STRING VERSION_STRING "+" xstr(BUILD_NUMBER)  ".sha." xstr(BUILD_SHA)
#define PRODUCT_STRING PRODUCT_NAME "-" BUILD_STRING


#ifdef GENERATE_MAIN
#include <stdio.h>
int main()
{
        printf("VERSION_STRING=%s\n", VERSION_STRING);
        printf("BUILD_STRING=%s\n", BUILD_STRING);
        printf("PRODUCT_STRING=%s\n", PRODUCT_STRING);
}
#endif //GENERATE_MAIN
#endif //_VERSION_H_
