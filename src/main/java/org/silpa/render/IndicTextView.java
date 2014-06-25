package org.silpa.render;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import org.silpa.sdk.common.LanguageDetect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sujith on 25/6/14.
 */
public class IndicTextView extends TextView {

    static {
        System.loadLibrary("png_android");
        System.loadLibrary("indic_script_renderer");
    }

    public native void drawIndicText(String unicodeText, int xStart,
                                     int yBaseLine, int charHeight, Boolean lock, String fontPath,
                                     int language);

    private Context mContext;
    private AssetManager assetManager;
    private Canvas savedCanvas;
    private Rect mRect;
    private boolean lock;
    private String[] fontPaths;

    private static Map<String, Integer> languageCodes = new HashMap<String, Integer>();

    static {
        languageCodes.put("bn_IN", 0);
        languageCodes.put("hi_IN", 1);
        languageCodes.put("gu_IN", 2);
        languageCodes.put("kn_IN", 3);
        languageCodes.put("ml_IN", 4);
        languageCodes.put("or_IN", 5);
        languageCodes.put("ta_IN", 6);
        languageCodes.put("te_IN", 7);
    }

    private static final int NUM_SUPPORTED_INDIC_LANGUAGES = 8;
    private static final String LOG_TAG = "IndicEditText";

    /**
     * Constructor
     *
     * @param context context of application
     */
    public IndicTextView(Context context) {
        super(context);
        init(null, 0);
    }

    /**
     * Constructor
     *
     * @param context context of application
     * @param attrs   attribute set
     */
    public IndicTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    /**
     * Constructor
     *
     * @param context  context of application
     * @param attrs    attribute set
     * @param defStyle default style
     */
    public IndicTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    /**
     * Initialize data members
     *
     * @param attrs    attribute set
     * @param defStyle default style
     */
    private void init(AttributeSet attrs, int defStyle) {
        this.mContext = getContext();
        this.assetManager = this.mContext.getAssets();
        this.lock = true;
        this.mRect = new Rect();
        initFontPaths();
        copyAssets();
    }

    private void initFontPaths() {
        this.fontPaths = new String[NUM_SUPPORTED_INDIC_LANGUAGES];
        this.fontPaths[0] = this.mContext.getFilesDir() + File.separator + "lohit_bn.ttf";
        this.fontPaths[1] = this.mContext.getFilesDir() + File.separator + "lohit_hi.ttf";
        this.fontPaths[2] = this.mContext.getFilesDir() + File.separator + "lohit_gu.ttf";
        this.fontPaths[3] = this.mContext.getFilesDir() + File.separator + "lohit_kn.ttf";
        this.fontPaths[4] = this.mContext.getFilesDir() + File.separator + "lohit_ml.ttf";
        this.fontPaths[5] = this.mContext.getFilesDir() + File.separator + "lohit_or.ttf";
        this.fontPaths[6] = this.mContext.getFilesDir() + File.separator + "lohit_ta.ttf";
        this.fontPaths[7] = this.mContext.getFilesDir() + File.separator + "lohit_te.ttf";
    }

    public void drawGlyph(int glyphBitmap[][], int x, int y) {

        if (glyphBitmap == null) {
            return;
        }
        int height = glyphBitmap.length;
        if (height == 0) {
            return;
        }
        int width = glyphBitmap[0].length;
        Bitmap tempBitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (glyphBitmap[i][j] == 0) {
                    tempBitmap.setPixel(j, i, Color.TRANSPARENT);
                } else {
                    float bitmapFactor = ((float) (glyphBitmap[i][j] & 0xFF)) / 255.0f;
                    tempBitmap.setPixel(j, i, Color.parseColor("#" + Integer.toHexString((int) (bitmapFactor * getCurrentTextColor()))));

                }
            }
        }
        savedCanvas.drawBitmap(tempBitmap, x, y, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            super.onDraw(canvas);
            return;
        }

        int count = getLineCount();
        String[] textLines = getText().toString().split("\\n");

        String word = (textLines[0].split(" "))[0];
        String lang = LanguageDetect.detectLanguage(word.split(" ")[0]).get(word.split(" ")[0]);

        if (lang == null || lang.equals("en_US")) {
            super.onDraw(canvas);
            return;
        }
        int language = languageCodes.get(lang);

        Rect r = mRect;
        // Paint paint = mPaint;
        for (int i = 0; i < count; i++) {
            int baseline = getLineBounds(i, r);
            savedCanvas = canvas;
            String currentText = i < textLines.length ? textLines[i] : "";
            drawIndicText(currentText, r.left, baseline, (int) getTextSize(), lock, fontPaths[language], language);
        }
    }

    private void copyAssets() {

        String[] files = null;

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
                copyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch (IOException e) {
                Log.e(LOG_TAG, "Failed to copy asset file: " + filename, e);
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}
