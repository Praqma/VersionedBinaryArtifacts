#!/bin/bash
set -e
gcc -c lib_test.c -o lib_test.o
ar rcs libtest.a lib_test.o
