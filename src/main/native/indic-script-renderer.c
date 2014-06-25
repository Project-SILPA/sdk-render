
#include <stdio.h>
#include <string.h>
#include <math.h>
#include <jni.h>
#include <android/log.h>
#include <hb-ft.h>

#define NUM_SUPPORTED_INDIC_LANGUAGES 8

const hb_script_t scripts[NUM_SUPPORTED_INDIC_LANGUAGES] = {
    HB_SCRIPT_BENGALI,
    HB_SCRIPT_DEVANAGARI,
    HB_SCRIPT_GUJARATI,
    HB_SCRIPT_KANNADA,
    HB_SCRIPT_MALAYALAM,
    HB_SCRIPT_ORIYA,
    HB_SCRIPT_TAMIL,
    HB_SCRIPT_TELUGU,
};

void draw_bitmap(FT_Bitmap* bitmap, jint xStart, jint yStart, JNIEnv *env,
		jobject jobj, jobject lock) {
	jintArray row;
	jobjectArray ret;
	int i, j;
	int localArrayCopy[1];

	jclass cls;
	jmethodID mid;

	// Create array to send back
	//for table overflow error minimization
	(*env)->PushLocalFrame(env, 256);
	row = (jintArray)(*env)->NewIntArray(env, bitmap->width);
	ret = (jobjectArray)(*env)->NewObjectArray(env, bitmap->rows,
			(*env)->GetObjectClass(env, row), 0);
	(*env)->DeleteLocalRef(env, row);
	for (i = 0; i < bitmap->rows; i++) {
		row = (jintArray)(*env)->NewIntArray(env, bitmap->width);
		for (j = 0; j < bitmap->width; j++) {
			localArrayCopy[0] = (int) bitmap->buffer[i * bitmap->width + j];
			(*env)->SetIntArrayRegion(env, (jintArray) row, (jsize) j,
					(jsize) 1, (jint *) localArrayCopy);
		}
		(*env)->SetObjectArrayElement(env, ret, i, row);
		(*env)->DeleteLocalRef(env, row);
	}

	cls = (*env)->GetObjectClass(env, jobj);
	mid = (*env)->GetMethodID(env, cls, "drawGlyph", "([[III)V");
	if (mid == 0) {
//		__android_log_print(2, "drawIndicText:draw_bitmap", "%s",
//				"Can't find method drawGlyph");
		return;
	}

	(*env)->ExceptionClear(env);
	(*env)->MonitorEnter(env, lock);
	(*env)->CallVoidMethod(env, jobj, mid, ret, xStart, yStart);
	(*env)->MonitorExit(env, lock);
	(*env)->DeleteLocalRef(env, ret);
	(*env)->DeleteLocalRef(env, cls);
	(*env)->PopLocalFrame(env, NULL);

	if ((*env)->ExceptionOccurred(env)) {
		__android_log_print(2, "drawIndicText:draw_bitmap", "%s\n",
				"error occurred copying array back");
		(*env)->ExceptionDescribe(env);
		(*env)->ExceptionClear(env);
	}
	return;
}

void Java_org_silpa_render_IndicEditText_drawIndicText(
		JNIEnv* env, jobject thiz, jstring unicodeText, jint xStart,
		jint yBaseLine, jint charHeight, jobject lock, jstring fontPath, jint language) {
	FT_Library ft_library;
	FT_Face ft_face;

	hb_font_t *font;
	hb_buffer_t *buffer;
	int glyph_count;
	hb_glyph_info_t *glyph_info;
	hb_glyph_position_t *glyph_pos;
	hb_bool_t fail;

	FT_UInt glyph_index;
	FT_GlyphSlot slot;
	FT_Error error;

	char* fontFilePath;
	jboolean iscopy;
	const jchar *text;
	int num_chars, i;

	int pen_x;
	int glyphPosX, glyphPosY;

   	fontFilePath = (*env)->GetStringUTFChars( env, fontPath , NULL ) ;


	text = (*env)->GetStringChars(env, unicodeText, &iscopy);
	num_chars = (*env)->GetStringLength(env, unicodeText);

	error = FT_Init_FreeType(&ft_library); /* initialize library */
	if (error) {
//		__android_log_print(6, "drawIndicText",
//				"Error initializing FreeType library\n");
		return;
	}
//	__android_log_print(2, "drawIndicText",
//			"Successfully initialized FreeType library\n");

	error = FT_New_Face(ft_library, fontFilePath, 0, &ft_face); /* create face object */
	if (error == FT_Err_Unknown_File_Format) {
    	__android_log_print(6, "drawIndicText",
				"The font file could be opened and read, but it appears that its font format is unsupported %s ", fontFilePath);
		return;
	} else if (error) {
		__android_log_print(6, "drawIndicText",
				"The font file could not be opened or read, or it might be broken");
		return;
	}
//	__android_log_print(2, "drawIndicText",
//			"Successfully created font-face object\n");

	font = hb_ft_font_create(ft_face, NULL);

	error = FT_Set_Pixel_Sizes(ft_face, 0, charHeight); /* set character size */

	slot = ft_face->glyph;
	pen_x = xStart;

	/* Create a buffer for harfbuzz to use */
	buffer = hb_buffer_create();

	hb_buffer_set_script(buffer, scripts[language]);

	//hb_buffer_set_language(buffer, hb_language_from_string("ka"));

	/* Layout the text */
	hb_buffer_add_utf16(buffer, text, num_chars, 0, num_chars);
	//__android_log_print(2, "drawIndicText", "Before HarfBuzz shape()\n");
	hb_shape(font, buffer, NULL, 0);
	//__android_log_print(2, "drawIndicText", "After HarfBuzz shape()\n");

	glyph_count = hb_buffer_get_length(buffer);
	glyph_info = hb_buffer_get_glyph_infos(buffer, 0);
	glyph_pos = hb_buffer_get_glyph_positions(buffer, 0);

	for (i = 0; i < glyph_count; i++) {
		glyph_index = glyph_info[i].codepoint;

		//__android_log_print(2, "drawIndicText", "Glyph%d = %x", i, glyph_index);

		error = FT_Load_Glyph(ft_face, glyph_index, FT_LOAD_DEFAULT);
		if (error) {
			continue; /* ignore errors */
		}
		/* convert to an anti-aliased bitmap */
		error = FT_Render_Glyph(ft_face->glyph, FT_RENDER_MODE_NORMAL);
		if (error) {
			continue;
		}

		/* now, draw to our target surface (convert position) */
		draw_bitmap(&slot->bitmap, pen_x + slot->bitmap_left,
				yBaseLine - slot->bitmap_top, env, thiz, lock);

		/* increment pen position */
		pen_x += slot->advance.x >> 6;
	}

	hb_buffer_destroy(buffer);

	(*env)->ReleaseStringChars(env, unicodeText, text);
	(*env)->ReleaseStringChars(env, fontPath, text);
	hb_font_destroy(font);
	FT_Done_Face(ft_face);
	FT_Done_FreeType(ft_library);

	return;
}
