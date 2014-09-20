package org.silpa.render;

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
        this.mRect = new Rect();
        this.scriptRenderer = new ScriptRenderer(mContext);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int count = getLineCount();
        String text = getText().toString();
        String[] textLines = text.split("\\n");

        if (text == null || text.length() == 0 || textLines == null || textLines.length == 0) {
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
