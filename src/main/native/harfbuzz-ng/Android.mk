##
## Copyright (C) 2012 The Android Open Source Project
##
## Licensed under the Apache License, Version 2.0 (the "License");
## you may not use this file except in compliance with the License.
## You may obtain a copy of the License at
##
##      http://www.apache.org/licenses/LICENSE-2.0
##
## Unless required by applicable law or agreed to in writing, software
## distributed under the License is distributed on an "AS IS" BASIS,
## WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
## See the License for the specific language governing permissions and
## limitations under the License.
##

BASE_PATH := $(call my-dir)
LOCAL_PATH:= $(call my-dir)

HARFBUZZ_SRC_FILES = \
	src/hb-blob.cc \
	src/hb-buffer-serialize.cc \
	src/hb-buffer.cc \
	src/hb-common.cc \
	src/hb-fallback-shape.cc \
	src/hb-face.cc \
	src/hb-font.cc \
	src/hb-ft.cc \
	src/hb-ot-tag.cc \
	src/hb-set.cc \
	src/hb-shape.cc \
	src/hb-shape-plan.cc \
	src/hb-shaper.cc \
	src/hb-unicode.cc \
	src/hb-tt-font.cc \
	src/ucdn.c \
	src/hb-ucdn.cc \
	src/hb-warning.cc \
	src/hb-ot-layout.cc \
	src/hb-ot-map.cc \
	src/hb-ot-shape.cc \
	src/hb-ot-shape-complex-arabic.cc \
	src/hb-ot-shape-complex-default.cc \
	src/hb-ot-shape-complex-hangul.cc \
	src/hb-ot-shape-complex-hebrew.cc \
	src/hb-ot-shape-complex-indic.cc \
	src/hb-ot-shape-complex-indic-table.cc \
	src/hb-ot-shape-complex-myanmar.cc \
	src/hb-ot-shape-complex-sea.cc \
	src/hb-ot-shape-complex-thai.cc \
	src/hb-ot-shape-complex-tibetan.cc \
	src/hb-ot-shape-normalize.cc \
	src/hb-ot-shape-fallback.cc

include $(CLEAR_VARS)

LOCAL_ARM_MODE := arm
LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES:= \
	$(HARFBUZZ_SRC_FILES)


LOCAL_CPP_EXTENSION     := .cc
LOCAL_STATIC_LIBRARIES  := libft2
#LOCAL_STATIC_LIBRARIES := ft2
LOCAL_SHARED_LIBRARIES += libpng_android
LOCAL_C_INCLUDES        := \
  $(LOCAL_PATH)/src $(LOCAL_PATH)/src/hb-ucdn $(LOCAL_PATH)/../libpng $(LOCAL_PATH)/../freetype/include
LOCAL_CFLAGS += -DHB_NO_MT -DHAVE_OT -DHAVE_UCDN -DHAVE_FREETYPE
LOCAL_LDLIBS += -lpthread

LOCAL_MODULE:= libharfbuzz_ng


include $(BUILD_STATIC_LIBRARY)