#!/bin/bash
set -e
gcc main.c -L dihr2/libtest/lib/ -ltest -o apptest
