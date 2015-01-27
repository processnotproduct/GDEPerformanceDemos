package com.kanawish.perf.hv;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Html;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.kanawish.perf.R;

import static android.text.Layout.Alignment;

/**
 * Some useful links if you're looking to create a custom view:
 *
 * http://developer.android.com/training/custom-views/index.html
 *
 *
 * NOTE: Supported tags for HTML-in-char-sequence stylings:
 *
 * from https://groups.google.com/forum/#!topic/android-developers/5Mcl4k_ZNcY
 *
 * How to use text Html attributes with your TextView:

	 <quote>
	 I thought you are using a textview. I don't know if the canvas
	 actually supports it.

	 do something like

	 myTextView.setText(Html.fromHtml(myHtmlString))

	 by the way, I found the list of supported tags:

	 •br •p •div •em •b •strong •cite •dfn •i •big •small •font •blockquote •tt •monospace •a •u •sup •sub
	</quote>

 *
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
	public static final int TEXT_DIPS = 42;
	public static final float STEP_SIZE = 0.5f;

	// Styling attributes
	private Paint circlePaint;
	private Paint arcPaint;
	private TextPaint textPaint;
	private float diameter;
	private float centerY;
	private float centerX;

	StaticLayout staticFractionLayout;

	/**
	 * Value attributes: see attrs.xml, these are defined in a way that allows customization via .xml attributes
	 */
    private int numerator = 0 ;
	private int denominator = 1 ;

	public CustomView(Context context) {
		super(context);
		init();
	}

	/**
	 * From android SDK docs:
	 *
	 * To allow the Android Developer Tools to interact with your view, at a minimum you must provide a constructor that takes
	 * a Context and an AttributeSet object as parameters. This constructor allows the layout editor to create and edit an
	 * instance of your view.
	 */
	public CustomView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAttr(context, attrs);
		init();
	}

	public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initAttr(context, attrs);
		init();
	}

    public int getNumerator() {
        return numerator;
    }

    public void setNumerator(int numerator) {
        this.numerator = numerator;
		invalidate();
		requestLayout();
		refreshTextLayout();
    }

    public int getDenominator() {
        return denominator;
    }

    public void setDenominator(int denominator) {
        this.denominator = denominator;
		invalidate();
		requestLayout();
		refreshTextLayout();
    }

	/**
	 * Initialize the attributes, default to 1/3 for demo purposes.
	 */
	private void initAttr(Context context, AttributeSet attrs) {
		TypedArray typedArray = context.getTheme().obtainStyledAttributes(
				attrs,
				R.styleable.com_kanawish_perf_hv_CustomView,
				0, 0);
		try {
			numerator = typedArray.getInteger(R.styleable.com_kanawish_perf_hv_CustomView_numerator,1);
			denominator = typedArray.getInteger(R.styleable.com_kanawish_perf_hv_CustomView_denominator,3);
		} finally {
			typedArray.recycle();
		}

	}

	/**
	 * Optimization: It's important to initialize the various 'paints' here, as opposed to doing
	 * it on-the-fly when drawing, for example.
	 */
	private void init() {
		textPaint = createTextPaint() ;
		float textSizeInPixels = dipToPixels(TEXT_DIPS);
		textPaint.setTextSize(textSizeInPixels);

		circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		circlePaint.setStyle(Paint.Style.STROKE);
		circlePaint.setColor(0xFFFFFFFF); // ARGB
		circlePaint.setStrokeWidth(STROKE_WIDTH-6);
		circlePaint.setAntiAlias(true);

		arcPaint = new Paint(circlePaint);
		arcPaint.setColor(0xFF00AA00); // Green
		arcPaint.setStrokeWidth(STROKE_WIDTH);

		// The initial call. Will be called again when nominator/denominator change.
		refreshTextLayout();
	}

	void refreshTextLayout() {
		/* Easy optimization
		Spanned charSequence = Html.fromHtml(String.format(FRACTION, numerator, denominator));
		staticFractionLayout =
				new StaticLayout(charSequence, textPaint, (int) diameter, Alignment.ALIGN_CENTER, 1, 1, true);

		*/
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
	 *
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		// Account for text size, using biggest number, assuming no negative values.
		int biggest = numerator > denominator ? numerator : denominator ;

		// TODO: We should measure off of StaticLayout instance to get proper needed width.
		Spanned charSequence = Html.fromHtml(String.format(FRACTION, biggest, biggest));
		float measuredWidth = textPaint.measureText(charSequence, 0, charSequence.length());
		// We want the circle to enclose the text rendering box, so:
		measuredWidth = (float) Math.sqrt( Math.pow(measuredWidth,2) * 2 );

		float xpad = (float)(getPaddingLeft() + getPaddingRight());
		float ypad = (float)(getPaddingTop() + getPaddingBottom());

		int desiredWidth = (int) (measuredWidth+xpad+STROKE_WIDTH);
		int desiredHeight = (int) (measuredWidth+ypad+STROKE_WIDTH);

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int width;
		int height;

		// Measure Width
		if (widthMode == MeasureSpec.EXACTLY) {
			// Must be this size
			width = widthSize;
		} else if (widthMode == MeasureSpec.AT_MOST) {
			// Can't be bigger than...
			width = Math.min(desiredWidth, widthSize);
		} else {
			// Be whatever you want
			width = desiredWidth;
		}

		// Measure Height
		if (heightMode == MeasureSpec.EXACTLY) {
			// Must be this size
			height = heightSize;
		} else if (heightMode == MeasureSpec.AT_MOST) {
			// Can't be bigger than...
			height = Math.min(desiredHeight, heightSize);
		} else {
			// Be whatever you want
			height = desiredHeight;
		}

		// MUST CALL THIS
		setMeasuredDimension(width, height);
    }

	/**
	 * We're being told what our size is, and was.
	 */
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

		RectF rect = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

		// TODO: Optimize
		Spanned charSequence = Html.fromHtml(String.format(FRACTION, numerator, denominator));
		StaticLayout staticLayout =
			new StaticLayout(charSequence, textPaint, (int) diameter, Alignment.ALIGN_CENTER, 1, 1, true);

		// Visual debugging
		Rect lineBounds = new Rect();
		staticLayout.getLineBounds(0,lineBounds);

		float dy = (diameter - staticLayout.getHeight()) / 2;

		canvas.save();
		canvas.translate(centerX - radius, dy);
		// canvas.drawRect(lineBounds, circlePaint); // Debug
		staticLayout.draw(canvas);
		canvas.restore();

        canvas.save();
        canvas.drawCircle(centerX, centerY, radius, circlePaint);
		canvas.drawArc(rect, -90, -360 * (numerator / (float) denominator), false, arcPaint);
        canvas.restore();

	}

    private TextPaint createTextPaint() {
        TextPaint textPaint = new TextPaint();
        textPaint.setColor(Color.WHITE);
        textPaint.setShadowLayer(3, 1, 1, Color.BLACK);
        textPaint.setAntiAlias(true);

        return textPaint;
    }

    private int dipToPixels(int dipValue) {
        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, r.getDisplayMetrics());
        return px;
    }
}
