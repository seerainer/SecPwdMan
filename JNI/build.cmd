@echo off

call "%PROGRAMFILES%\Microsoft Visual Studio\2022\Community\VC\Auxiliary\Build\vcvars64.bat"

@REM gcc -c -Os -flto -o window_affinity_stub.o window_affinity_stub.c

cl /c /O1 window_affinity_stub.c
