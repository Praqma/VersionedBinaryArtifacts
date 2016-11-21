#include "version.h"


const char * lib_version();

#include "stdio.h"

int main()
{
    const char* libversion = lib_version();
    printf("lib_version = %s\n", libversion);
    printf("app_version = %s\n", PRODUCT_STRING);
}
