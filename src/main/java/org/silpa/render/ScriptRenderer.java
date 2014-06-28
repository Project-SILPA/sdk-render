package org.silpa.render;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import org.silpa.sdk.common.LanguageDetect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sujith on 27/6/14.
 */
public class ScriptRenderer {

    static {
        System.loadLibrary("png_android");
        System.loadLibrary("indic_script_renderer");
    }

    /**
     * This is native method defined in indic-script-renderer.c
     * @param unicodeText text to be render
     * @param xStart start x
     * @param yBaseLine baseline y
     * @param charHeight font size
     * @param lock lock
     * @param fontPath path to font file
     * @param language language of given text
     */
    public native void drawIndicText(String unicodeText, int xStart,
                                     int yBaseLine, int charHeight, Boolean lock, String fontPath,
                                     int language);

    /**
     * Context of application
     */
    private Context mContext;

    /**
     * Canvas used for rendering
     */
    private Canvas canvas;

    /**
     * Font color
     */
    private int fontColor;

    /**
     * Background color
     */
    private int backgroundFontColor;

    /**
     * Paths to font data
     */
    private String[] fontPaths;

    /**
     * Language codes for mapping
     */
    private static Map<String, Integer> languageCodes = new HashMap<String, Integer>();

    /**
     * Number of supported indic language.
     * If adding more langauge increment this,
     * find language code, add font.ttf to assets,
     * add to fontPaths,
     * add harfbuzz script in indic-script-renderer
     */
    private static final int NUM_SUPPORTED_INDIC_LANGUAGES = 9;

    // LOG TAG
    private static final String LOG_TAG = "Script Renderer";

    /**
     * Initialize langauge codes
      */
    static {
        languageCodes.put("bn_IN", 0);
        languageCodes.put("hi_IN", 1);
        languageCodes.put("gu_IN", 2);
        languageCodes.put("kn_IN", 3);
        languageCodes.put("ml_IN", 4);
        languageCodes.put("or_IN", 5);
        languageCodes.put("pa_IN", 6);
        languageCodes.put("ta_IN", 7);
        languageCodes.put("te_IN", 8);
    }


    /**
     * Constructor
     * @param context context
     */
    public ScriptRenderer(Context context) {
        this.mContext = context;
        this.backgroundFontColor = Color.TRANSPARENT;
        initFontPaths();
        copyFontFiles();
    }

    /**
     * Initialize font paths
     */
    private void initFontPaths() {
        this.fontPaths = new String[NUM_SUPPORTED_INDIC_LANGUAGES];
        this.fontPaths[0] = this.mContext.getFilesDir() + File.separator + "lohit_bn.ttf";
        this.fontPaths[1] = this.mContext.getFilesDir() + File.separator + "lohit_hi.ttf";
        this.fontPaths[2] = this.mContext.getFilesDir() + File.separator + "lohit_gu.ttf";
        this.fontPaths[3] = this.mContext.getFilesDir() + File.separator + "lohit_kn.ttf";
        this.fontPaths[4] = this.mContext.getFilesDir() + File.separator + "lohit_ml.ttf";
        this.fontPaths[5] = this.mContext.getFilesDir() + File.separator + "lohit_or.ttf";
        this.fontPaths[6] = this.mContext.getFilesDir() + File.separator + "lohit_pa.ttf";
        this.fontPaths[7] = this.mContext.getFilesDir() + File.separator + "lohit_ta.ttf";
        this.fontPaths[8] = this.mContext.getFilesDir() + File.separator + "lohit_te.ttf";
    }

    /**
     * Set canvas for rendering
     * @param canvas
     */
    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    /**
     * Render text
     * @param text text to be render
     * @param xStart start x position
     * @param yBaseLine baseline y position
     * @param fontSize font size
     * @param fontColor font color
     */
    protected void renderIndicText(String text, int xStart, int yBaseLine, int fontSize, int fontColor) {
        this.fontColor = fontColor;
        try {
            String word = (text.split(" "))[0];
            String lang = LanguageDetect.detectLanguage(word.split(" ")[0]).get(word.split(" ")[0]);
            int language = languageCodes.get(lang);
            drawIndicText(text, xStart, yBaseLine, fontSize, true, fontPaths[language], language);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error : " + e.getMessage());
        }
    }

    /**
     * Called from native method
     * @param glyphBitmap glyph bitmap
     * @param x bitmap position x
     * @param y bitmap position y
     */
    public void drawGlyph(int glyphBitmap[][], int x, int y) {
        if (glyphBitmap == null) {
            return;
        }
        int height = glyphBitmap.length;
        if (height == 0) {
            return;
        }
        int width = glyphBitmap[0].length;
        Bitmap tempBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (glyphBitmap[i][j] == 0) {
                    tempBitmap.setPixel(j, i, backgroundFontColor);
                } else {
                    float bitmapFactor = ((float) (glyphBitmap[i][j] & 0xFF)) / 255.0f;
                    tempBitmap.setPixel(j, i, Color.parseColor("#" +
                            Integer.toHexString((int) (bitmapFactor * this.fontColor))));
                }
            }
        }
        this.canvas.drawBitmap(tempBitmap, x, y, null);
    }

    /**
     * This function is to load font files
     */
    private void copyFontFiles() {

        String[] files = null;
        AssetManager assetManager = this.mContext.getAssets();

        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed to get asset file list.", e);
        }
        for (String filename : files) {
            if (!(filename.endsWith(".ttf"))) {
                continue;
            }
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(filename);
                File outFile = new File(this.mContext.getFilesDir() + File.separator + filename);
                if (outFile.exists()) {
                    continue;
                }
                out = new FileOutputStream(outFile);
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch (IOException e) {
                Log.e(LOG_TAG, "Failed to copy asset font file: " + filename, e);
            }
        }
    }
}
