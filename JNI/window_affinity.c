#include <windows.h>

__declspec(dllexport)
int my_SetWindowDisplayAffinity(HWND hwnd, int affinity) {
    return SetWindowDisplayAffinity(hwnd, (DWORD)affinity);
}