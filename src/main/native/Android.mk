LOCAL_PATH := $(call my-dir)

MY_LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_ARM_MODE := arm

LOCAL_MODULE := indic_text_renderer

LOCAL_C_INCLUDES := $(LOCAL_PATH)/libpng $(LOCAL_PATH)/freetype/include $(LOCAL_PATH)/harfbuzz/src

LOCAL_SRC_FILES := indic-text-renderer.c

LOCAL_STATIC_LIBRARIES := ft2 harfbuzz

LOCAL_SHARED_LIBRARIES += libpng_android

LOCAL_LDLIBS := -llog

include $(BUILD_SHARED_LIBRARY)

include $(MY_LOCAL_PATH)/libpng/Android.mk

include $(MY_LOCAL_PATH)/freetype/Android.mk

include $(MY_LOCAL_PATH)/harfbuzz/Android.mk
