package com.kanawish.perf.custom;

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
 *
 * This is an example CustomView for tutorials.
 *
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
 * FIXME: Known issue, padding is not being taken into account correctly.
 *
 */
public class FractionCustomView extends View {

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
	private Paint debugPaint;
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

	public FractionCustomView(Context context) {
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
	public FractionCustomView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAttr(context, attrs);
		init();
	}

	public FractionCustomView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initAttr(context, attrs);
		init();
	}

    public int getNumerator() {
        return numerator;
    }

    public void setNumerator(int numerator) {
        this.numerator = numerator;
		if(this.numerator<0) {
			arcPaint.setColor(Color.argb(255,220,0,0));
		} else {
			arcPaint.setColor(Color.argb(255,0,220,0));
		}

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
		debugPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		debugPaint.setStyle(Paint.Style.STROKE);
		debugPaint.setColor(0xFFFF0000);
		debugPaint.setStrokeWidth(1);
		debugPaint.setAntiAlias(true);

		textPaint = createTextPaint() ;
		float textSizeInPixels = dipToPixels(TEXT_DIPS);
		textPaint.setTextSize(textSizeInPixels);

		circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		circlePaint.setStyle(Paint.Style.STROKE);
		circlePaint.setColor(0xFFFFFFFF); // ARGB
		circlePaint.setStrokeWidth(STROKE_WIDTH-6);
		circlePaint.setAntiAlias(true);

		arcPaint = new Paint(circlePaint);
		arcPaint.setColor(Color.GREEN); // Default Green
		arcPaint.setStrokeWidth(STROKE_WIDTH);

		refreshTextLayout();
	}

	void refreshTextLayout() {
		Spanned measuringCharSequence = Html.fromHtml(String.format(FRACTION, numerator, denominator));
		float desiredWidth = StaticLayout.getDesiredWidth(measuringCharSequence, textPaint);

		// NOTE: We use the real numbers now, assuming height will be same as the measuring char sequence.
		Spanned charSequence = Html.fromHtml(String.format(FRACTION, numerator, denominator));
		staticFractionLayout =
				new StaticLayout(charSequence, textPaint, (int) desiredWidth, Alignment.ALIGN_CENTER, 1, 1, true);
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

		// Account for text size, using biggest number, assuming it'll have the most digits, ignoring negative values for now.
		int biggest = numerator > denominator ? numerator : denominator ;
		// The max desired width.
		Spanned measuringCharSequence = Html.fromHtml(String.format(FRACTION, biggest, biggest));
		float desiredLayoutWidth = StaticLayout.getDesiredWidth(measuringCharSequence, textPaint);
		StaticLayout staticFractionLayout =
				new StaticLayout(measuringCharSequence, textPaint, (int) desiredLayoutWidth, Alignment.ALIGN_CENTER, 1, 1, true);

		// Assumption is that the current staticFractionLayout is always representative when we reach this step.
		float biggestValue = staticFractionLayout.getHeight()>staticFractionLayout.getWidth()?
				staticFractionLayout.getHeight():
				staticFractionLayout.getWidth();
		// We want the circle to enclose the text rendering box, so:
		float targetDiameter = (float) Math.sqrt( Math.pow(biggestValue,2) * 2 );

		float xpad = (float)(getPaddingLeft() + getPaddingRight());
		float ypad = (float)(getPaddingTop() + getPaddingBottom());

		int desiredWidth = (int) (targetDiameter+xpad+STROKE_WIDTH);
		int desiredHeight = (int) (targetDiameter+ypad+STROKE_WIDTH);

		int width = resolveSizeAndState(desiredWidth, widthMeasureSpec, 1);
		int height = resolveSizeAndState(desiredHeight, heightMeasureSpec, 0);

		// MUST CALL THIS
		setMeasuredDimension(width, height);
    }

	/**
	 * We're being told what our size is, and was. The call to refresh text layout is important
	 * to assign the right width/diameter to the StaticLayout.
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

		refreshTextLayout();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		float radius = diameter / 2;

		RectF rect = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

		float dx = (diameter - staticFractionLayout.getWidth()) / 2;
		float dy = (diameter - staticFractionLayout.getHeight()) / 2;

		// Visual debugging
		Rect lineBounds = new Rect();
		staticFractionLayout.getLineBounds(0,lineBounds);

		canvas.save();
		canvas.translate(dx+10, dy);
//		canvas.drawRect(lineBounds, debugPaint); // Debug
		staticFractionLayout.draw(canvas);
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
