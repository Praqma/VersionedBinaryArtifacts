#include "version.h"

const char * lib_version()
{
    static char * product_verion = PRODUCT_STRING;
    return product_verion;
}
