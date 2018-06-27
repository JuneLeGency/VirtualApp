LOCAL_PATH := $(call my-dir)
MAIN_LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

###########################

include $(CLEAR_VARS)
LOCAL_MODULE := va++

LOCAL_CFLAGS := -Wno-error=format-security -fpermissive -DLOG_TAG=\"VA++\"
LOCAL_CFLAGS += -fno-rtti -fno-exceptions

LOCAL_C_INCLUDES += $(MAIN_LOCAL_PATH)
LOCAL_C_INCLUDES += $(MAIN_LOCAL_PATH)/Foundation
LOCAL_C_INCLUDES += $(MAIN_LOCAL_PATH)/Jni

LOCAL_SRC_FILES := Jni/VAJni.cpp \
				   Foundation/IOUniformer.cpp \
				   Foundation/VMPatch.cpp \
				   Foundation/SymbolFinder.cpp \
				   Foundation/Path.cpp \
				   Foundation/SandboxFs.cpp \
				   Substrate/hde64.c \
                   Substrate/SubstrateDebug.cpp \
                   Substrate/SubstrateHook.cpp \
                   Substrate/SubstratePosixMemory.cpp \
LOCAL_LDLIBS := -llog -latomic
LOCAL_STATIC_LIBRARIES := fb

include $(BUILD_SHARED_LIBRARY)
########################################################
## libproxychainsng
########################################################
#https://www.hi-linux.com/posts/48321.html
include $(CLEAR_VARS)
LOCAL_MODULE:= proxychainsng

LOCAL_C_INCLUDES:= $(LOCAL_PATH)/ProxyChainsNG/src


LOCAL_SRC_FILES := ProxyChainsNG/allocator_thread.c\
                  ProxyChainsNG/common.c\
                  ProxyChainsNG/core.c\
                  ProxyChainsNG/debug.c\
                  ProxyChainsNG/hash.c\
                  ProxyChainsNG/hostsreader.c\
                  ProxyChainsNG/ip_type.c\
                  ProxyChainsNG/libproxychains.c\
                  ProxyChainsNG/nameinfo.c\
                  ProxyChainsNG/version.c\
                  Substrate/hde64.c \
                  Substrate/SubstrateDebug.cpp \
                  Substrate/SubstrateHook.cpp \
                  Substrate/SubstratePosixMemory.cpp \

LOCAL_CFLAGS := -Wno-error=format-security -fpermissive -DLOG_TAG=\"ProxyChainsNG\"
LOCAL_CFLAGS += -fno-rtti -fno-exceptions


LOCAL_LDLIBS := -ldl -llog
include $(BUILD_SHARED_LIBRARY)

###substrate
include $(CLEAR_VARS)
LOCAL_MODULE:= substrate
LOCAL_C_INCLUDES:= $(LOCAL_PATH)/Substrate
LOCAL_SRC_FILES:= Substrate/hde64.c \
                  Substrate/SubstrateDebug.cpp \
                  Substrate/SubstrateHook.cpp \
                  Substrate/SubstratePosixMemory.cpp \
LOCAL_CFLAGS := -O2
include $(BUILD_SHARED_LIBRARY)

###IHook
include $(CLEAR_VARS)
ifdef USE_XHOOK
HOOK_MODULE = xhook
else
HOOK_MODULE = substrate
endif
LOCAL_MODULE:= ihook
LOCAL_C_INCLUDES:= $(LOCAL_PATH)/IHook
LOCAL_SRC_FILES:= IHook/hook.c \
LOCAL_CFLAGS += -O2 -I$(LOCAL_PATH)/IHook
LOCAL_SHARED_LIBRARIES  :=substrate
include $(BUILD_SHARED_LIBRARY)

###hookdebug
include $(CLEAR_VARS)
LOCAL_MODULE:= hookdebug
LOCAL_SRC_FILES := hookdebug/hookimpl.c
LOCAL_SHARED_LIBRARIES := ihook
LOCAL_CFLAGS := -Wno-error=format-security -fpermissive
LOCAL_LDLIBS := -ldl -llog
include $(BUILD_SHARED_LIBRARY)
###
include $(MAIN_LOCAL_PATH)/fb/Android.mk