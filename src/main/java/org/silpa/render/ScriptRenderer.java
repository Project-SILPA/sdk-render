package org.silpa.render;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;

import org.silpa.sdk.common.CharacterMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

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
     *
     * @param unicodeText text to be render
     * @param xStart      start x
     * @param yBaseLine   baseline y
     * @param charHeight  font size
     * @param lock        lock
     * @param fontPath    path to font file
     * @param language    language of given text
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

    private int xStart, yStart;

    /**
     * Font color
     */
    private int fontColor;

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
    private static final int NUM_SUPPORTED_INDIC_LANGUAGES = 10;

    // LOG TAG
    private static final String LOG_TAG = "Script Renderer";

    /**
     * Initialize langauge codes
     */
    static {
        languageCodes.put("bn_IN", 0);
        languageCodes.put("en_US", 1);
        languageCodes.put("hi_IN", 2);
        languageCodes.put("gu_IN", 3);
        languageCodes.put("kn_IN", 4);
        languageCodes.put("ml_IN", 5);
        languageCodes.put("or_IN", 6);
        languageCodes.put("pa_IN", 7);
        languageCodes.put("ta_IN", 8);
        languageCodes.put("te_IN", 9);
    }


    /**
     * Constructor
     *
     * @param context context
     */
    public ScriptRenderer(Context context) {
        this.mContext = context;
        initFontPaths();
        copyFontFiles();
    }

    /**
     * Initialize font paths
     */
    private void initFontPaths() {
        this.fontPaths = new String[NUM_SUPPORTED_INDIC_LANGUAGES];
        this.fontPaths[0] = this.mContext.getFilesDir() + File.separator + "lohit_bn.ttf";
        this.fontPaths[1] = this.mContext.getFilesDir() + File.separator + "Roboto-Regular.ttf";
        this.fontPaths[2] = this.mContext.getFilesDir() + File.separator + "lohit_hi.ttf";
        this.fontPaths[3] = this.mContext.getFilesDir() + File.separator + "lohit_gu.ttf";
        this.fontPaths[4] = this.mContext.getFilesDir() + File.separator + "lohit_kn.ttf";
        this.fontPaths[5] = this.mContext.getFilesDir() + File.separator + "lohit_ml.ttf";
        this.fontPaths[6] = this.mContext.getFilesDir() + File.separator + "lohit_or.ttf";
        this.fontPaths[7] = this.mContext.getFilesDir() + File.separator + "lohit_pa.ttf";
        this.fontPaths[8] = this.mContext.getFilesDir() + File.separator + "lohit_ta.ttf";
        this.fontPaths[9] = this.mContext.getFilesDir() + File.separator + "lohit_te.ttf";
    }

    /**
     * Set canvas for rendering
     *
     * @param canvas
     */
    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public Bitmap getRenderedBitmap(String text, int fontSize, int fontColor, int bitmapHeight,
                                    int bitmapWidth) {
        Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        this.canvas = new Canvas(bitmap);
        renderIndicText(text, 0, fontSize, fontSize, fontColor);

        for (int i = 0; i < bitmap.getHeight(); i++) {
            for (int j = 0; j < bitmap.getWidth(); j++) {
                if (bitmap.getPixel(j, i) == 0) {
                    bitmap.setPixel(j, i, Color.WHITE);
                }
            }
        }
        return bitmap;
    }

    /**
     * Render text
     *
     * @param text      text to be render
     * @param xStart    start x position
     * @param yBaseLine baseline y position
     * @param fontSize  font size
     * @param fontColor font color
     */
    protected void renderIndicText(String text, int xStart, int yBaseLine, int fontSize, int fontColor) {
        this.fontColor = fontColor;
        this.xStart = xStart;
        this.yStart = yBaseLine;
        try {
            List<String[]> lst = preprocessString(text);
            for (int i = lst.size() - 1; i >= 0; i--) {
                String[] arr = lst.get(i);
                int language = languageCodes.get(arr[1]);
                drawIndicText(arr[0], this.xStart, this.yStart, fontSize,
                        true, fontPaths[language], language);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error : " + e.getMessage());
        }
    }

    private List<String[]> preprocessString(String text) {
        List<String[]> lst = new ArrayList<String[]>();

        char[] characters = text.toCharArray();
        int len = characters.length;
        String str = "" + characters[len - 1];
        String strLang = CharacterMap.getLanguage(characters[len - 1]);
        if (strLang == null) {
            strLang = "en_US";
        }

        for (int i = len - 2; i >= 0; i--) {
            String lang = CharacterMap.getLanguage(characters[i]);
            if (lang == null || lang.equals(strLang)) {
                str = characters[i] + str;
            } else {
                lst.add(new String[]{str, strLang});
                str = "" + characters[i];
                strLang = lang;
            }
        }
        lst.add(new String[]{str, strLang});

        return lst;
    }

    /**
     * Called from native method
     *
     * @param glyphBitmap glyph bitmap
     * @param x           bitmap position x
     * @param y           bitmap position y
     */
    protected void drawGlyph(int glyphBitmap[][], int x, int y) {
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
                    tempBitmap.setPixel(j, i, Color.TRANSPARENT);
                } else {
                    float bitmapFactor = ((float) (glyphBitmap[i][j] & 0xFF)) / 255.0f;
                    tempBitmap.setPixel(j, i, Color.parseColor("#" +
                            Integer.toHexString((int) (bitmapFactor * this.fontColor))));
                }
            }
        }
        this.canvas.drawBitmap(tempBitmap, x, y, null);
        this.xStart = x + width;
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
                System.out.println("Loaded font files");
            } catch (IOException e) {
                Log.e(LOG_TAG, "Failed to copy asset font file: " + filename, e);
            }
        }
    }
}
