#!/bin/bash
set -e
gcc main.c -Lbuild/resolvedDep/libtest/lib/ -ltest -o apptest
