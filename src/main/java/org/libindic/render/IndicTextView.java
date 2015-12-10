package org.libindic.render;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by sujith on 25/6/14.
 */
public class IndicTextView extends TextView {

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
    public IndicTextView(Context context) {
        super(context);
        init(context, null, 0);
    }

    /**
     * Constructor
     *
     * @param context context of application
     * @param attrs   attribute set
     */
    public IndicTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
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
        init(context, attrs, defStyle);
    }

    /**
     * @param context  context of application
     * @param attrs    attribute set
     * @param defStyle default style
     */
    private void init(Context context, AttributeSet attrs, int defStyle) {
        this.mRect = new Rect();
        this.scriptRenderer = new ScriptRenderer(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int count = getLineCount();
        String text = getText().toString();
        String[] textLines = text.split("\\n");

        if (text == null || text.length() == 0) {
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
