package com.kanawish.perf.hv;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Html;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import static android.text.Layout.Alignment;

/**
 * NOTES: Supported tags for HTML-in-char-sequence stylings:
 *
 * from https://groups.google.com/forum/#!topic/android-developers/5Mcl4k_ZNcY
 *
<quote>
 I thought you are using a textview. I don't know if the canvas
 actually supports it.

 do something like

 myTextView.setText(Html.fromHtml(myHtmlString))

 by the way, I found the list of supported tags:

 •br
 •p
 •div
 •em
 •b
 •strong
 •cite
 •dfn
 •i
 •big
 •small
 •font
 •blockquote
 •tt
 •monospace
 •a
 •u
 •sup
 •sub
</quote>

 * Created by etiennecaron on 2014-05-03.
 */
public class CustomView extends View {

	public static final String NAMESPACE = "http://schemas.android.com/apk/res-auto";

	public static final float STROKE_WIDTH = 15f;

	// Just a baseline for tests.
	public static final float MIN_RADIUS = 30f;

    // XML visible attributes
	public static final String DENOMINATOR = "denominator";
	public static final String NUMERATOR = "numerator";

    public static final String FRACTION = "<sup>%s</sup>/<sub>%s</sub>";

    // Styling attributes
	private Paint circlePaint;
	private Paint arcPaint;
	private TextPaint textPaint;
	private float diameter;
	private float centerY;
	private float centerX;

    // Value attributes
    private int numerator = 1 ;
	private int denominator = 10 ;

	public CustomView(Context context) {
		super(context);
		init();
	}

	public CustomView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAttr(attrs);
		init();
	}
	public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initAttr(attrs);
		init();
	}

    public int getNumerator() {
        return numerator;
    }

    public void setNumerator(int numerator) {
        this.numerator = numerator;
    }

    public int getDenominator() {
        return denominator;
    }

    public void setDenominator(int denominator) {
        this.denominator = denominator;
    }

	private void initAttr(AttributeSet attrs) {
		numerator = attrs.getAttributeIntValue(NAMESPACE, NUMERATOR, numerator);
		denominator = attrs.getAttributeIntValue(NAMESPACE, DENOMINATOR, denominator);
	}

	private void init() {
//		Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Pacifico.ttf");

		textPaint = createTextPaint() ;

		circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		circlePaint.setStyle(Paint.Style.STROKE);
		circlePaint.setColor(0xFFFFFFFF); // ARGB
		circlePaint.setStrokeWidth(STROKE_WIDTH-6);
		circlePaint.setAntiAlias(true);

		arcPaint = new Paint(circlePaint);
		arcPaint.setColor(0xFF00AA00); // Green
		arcPaint.setStrokeWidth(STROKE_WIDTH);
	}

	/**
	 * To allow external to override some settings.
     *
     * NOTE: Changing font can sometimes 'break' fraction rendering.
     *
	 * @return textPaint that can be styled (change font, etc.)
	 */
	public Paint getTextPaint() {
		return textPaint;
	}

	/**
	 * http://stackoverflow.com/questions/12266899/onmeasure-custom-view-explanation
	 * http://stackoverflow.com/questions/7423082/authorative-way-to-override-onmeasure
	 *
	 * @param widthMeasureSpec
	 * @param heightMeasureSpec
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int desiredWidth = 100;
		int desiredHeight = 100;

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int width;
		int height;

		//Measure Width
		if (widthMode == MeasureSpec.EXACTLY) {
			//Must be this size
			width = widthSize;
		} else if (widthMode == MeasureSpec.AT_MOST) {
			//Can't be bigger than...
			width = Math.min(desiredWidth, widthSize);
		} else {
			//Be whatever you want
			width = desiredWidth;
		}

		//Measure Height
		if (heightMode == MeasureSpec.EXACTLY) {
			//Must be this size
			height = heightSize;
		} else if (heightMode == MeasureSpec.AT_MOST) {
			//Can't be bigger than...
			height = Math.min(desiredHeight, heightSize);
		} else {
			//Be whatever you want
			height = desiredHeight;
		}

		//MUST CALL THIS
		setMeasuredDimension(width, height);
    }

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// Account for padding
		float xpad = (float)(getPaddingLeft() + getPaddingRight());
		float ypad = (float)(getPaddingTop() + getPaddingBottom());

		float ww = (float)w - xpad - STROKE_WIDTH; // ?
		float hh = (float)h - ypad - STROKE_WIDTH;

		// Figure out how big we can make the circle.
		diameter = Math.min(ww, hh);
		centerX = (w / 2) + getPaddingLeft();
		centerY = (h / 2) + getPaddingTop();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		float radius = diameter / 2;

        Spanned charSequence = Html.fromHtml(String.format(FRACTION, numerator, denominator));
        drawText(canvas, centerX - radius, centerY - radius, diameter, diameter, charSequence, textPaint, 42, 0.5f);

        canvas.save();

        canvas.drawCircle(centerX, centerY, radius, circlePaint);

        RectF rect = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
//        canvas.drawRect(rect,circlePaint); // Debug

		canvas.drawArc(rect, -90, -360 * (numerator / (float) denominator), false, arcPaint);

        canvas.restore();
	}

    // Below is example code from http://andrdev.blogspot.ca/2012/04/drawing-text-on-canvas.html
    private void drawText(Canvas canvas, float xStart, float yStart,
                                 float xWidth, float yHeigth, CharSequence textToDisplay,
                                 TextPaint paintToUse, float startTextSizeInDips,
                                 float stepSizeForTextSizeSteps) {

        // Text view line spacing multiplier
        float mSpacingMult = 1.0f;
        // Text view additional line spacing
        float mSpacingAdd = 1.0f;

        float startTextSizeInPixels = dipToPixels((int) startTextSizeInDips);
        StaticLayout l = null;
        do {
            paintToUse.setTextSize(startTextSizeInPixels);
            l = new StaticLayout(textToDisplay, paintToUse, (int) xWidth,
                    Alignment.ALIGN_CENTER, mSpacingMult, mSpacingAdd, false);
            startTextSizeInPixels -= stepSizeForTextSizeSteps;
        } while (l.getHeight() > yHeigth);


        float textCenterX = xStart ;//+ (xWidth / 2);
        float textCenterY = (yHeigth - l.getHeight()) / 2;

        RectF rectF = new RectF();
        rectF.set(xStart,yStart,xStart+xWidth,yStart+yHeigth);

        canvas.save();
//        canvas.drawRect(rectF,arcPaint); // Debug
//        canvas.translate(rectF.left, rectF.top); // Debug
        canvas.translate(textCenterX, textCenterY);
        l.draw(canvas);
        canvas.restore();
    }

    private TextPaint createTextPaint() {
        TextPaint textPaint = new TextPaint();
        textPaint.setColor(Color.WHITE);
        textPaint.setShadowLayer(3, 1, 1, Color.BLACK);
//        textPaint.setTextAlign(Align.CENTER); // Caused issues!
        textPaint.setAntiAlias(true);

        return textPaint;
    }

    private int dipToPixels(int dipValue) {
        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, r.getDisplayMetrics());
        return px;
    }
}
