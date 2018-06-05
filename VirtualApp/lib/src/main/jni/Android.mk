LOCAL_PATH := $(call my-dir)
MAIN_LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES:= \
       fb/assert.cpp \
       fb/jni/ByteBuffer.cpp \
       fb/jni/Countable.cpp \
       fb/jni/Environment.cpp \
       fb/jni/Exceptions.cpp \
       fb/jni/fbjni.cpp \
       fb/jni/Hybrid.cpp \
       fb/jni/jni_helpers.cpp \
       fb/jni/LocalString.cpp \
       fb/jni/OnLoad.cpp \
       fb/jni/References.cpp \
       fb/jni/WeakReference.cpp \
       fb/log.cpp \
       fb/lyra/lyra.cpp \
       fb/onload.cpp \

LOCAL_C_INCLUDES := $(LOCAL_PATH)/fb/include
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/fb/include

LOCAL_CFLAGS := -DLOG_TAG=\"libfb\" -DDISABLE_CPUCAP -DDISABLE_XPLAT -fexceptions -frtti
LOCAL_CFLAGS += -Wall -Werror
# include/utils/threads.h has unused parameters
LOCAL_CFLAGS += -Wno-unused-parameter
ifeq ($(TOOLCHAIN_PERMISSIVE),true)
  LOCAL_CFLAGS += -Wno-error=unused-but-set-variable
endif
LOCAL_CFLAGS += -DHAVE_POSIX_CLOCKS

CXX11_FLAGS := -std=gnu++11
LOCAL_CFLAGS += $(CXX11_FLAGS)

LOCAL_EXPORT_CPPFLAGS := $(CXX11_FLAGS)

LOCAL_LDLIBS := -llog -ldl -landroid
LOCAL_EXPORT_LDLIBS := -llog

LOCAL_MODULE := libfb

include $(BUILD_STATIC_LIBRARY)
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
