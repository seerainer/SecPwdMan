@echo off

call "%PROGRAMFILES%\Microsoft Visual Studio\2022\Community\VC\Auxiliary\Build\vcvars64.bat"

@REM gcc -c -Os -flto -o window_affinity.o window_affinity.c

cl /c /O1 window_affinity.c
