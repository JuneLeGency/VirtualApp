//
// Created by legency on 2018/5/29.
//
#include "hook.h"
#ifdef USE_XHOOK
#include <xHook/libxhook/jni/xhook.h>
#include <android/log.h>
#else
#include <Substrate/CydiaSubstrate.h>
#endif //USE_XHOOK

void i_hook_inner(void *symbol, void *replace, void **result) {
#ifdef USE_XHOOK
    int r = xhook_register(".*\\.so$", symbol, replace, result);
#else
    MSHookFunction(symbol, replace, result);
#endif //USE_XHOOK
}

void i_hook_done() {
#ifdef USE_XHOOK
    xhook_enable_debug(ANDROID_LOG_VERBOSE);
    xhook_refresh(1);
#endif
}