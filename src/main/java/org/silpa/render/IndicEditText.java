package org.silpa.render;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.EditText;

import org.silpa.sdk.common.LanguageDetect;

/**
 * Created by sujith on 24/6/14.
 */
public class IndicEditText extends EditText {

    /**
     * Context of application
     */
    private Context mContext;

    /**
     * Rectangle
     */
    private Rect mRect;

    /**
     * Script renderer object for rendering
     */
    private ScriptRenderer scriptRenderer;

    /**
     * Constructor
     *
     * @param context context of application
     */
    public IndicEditText(Context context) {
        super(context);
        init(null, 0);
    }

    /**
     * Constructor
     *
     * @param context context of application
     * @param attrs   attribute set
     */
    public IndicEditText(Context context, AttributeSet attrs) {
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
    public IndicEditText(Context context, AttributeSet attrs, int defStyle) {
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
        this.mRect = new Rect();
        this.scriptRenderer = new ScriptRenderer(mContext);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int count = getLineCount();
        String[] textLines = getText().toString().split("\\n");

        if (textLines == null || textLines.length == 0) {
            super.onDraw(canvas);
            return;
        }
        String word = (textLines[0].trim().split(" "))[0];
        String lang = LanguageDetect.detectLanguage(word.split(" ")[0]).get(word.split(" ")[0]);

        if (lang == null) {
            super.onDraw(canvas);
            return;
        }

        Rect r = mRect;
        for (int i = 0; i < count; i++) {
            int baseline = getLineBounds(i, r);
            String currentText = i < textLines.length ? textLines[i] : "";

            scriptRenderer.setCanvas(canvas);
            scriptRenderer.renderIndicText(currentText, r.left, baseline, (int) getTextSize(),
                    getCurrentTextColor());
        }
    }
}
