package com.kanawish.perf.welcome;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.kanawish.perf.R;
import com.kanawish.perf.hv.FractionCustomView;

/**
 * Created by kanawish on 2015-01-28.
 */
public class FractionPageFragment extends Fragment {

	private static final String ARG_NUMERATOR = "numerator";
	private static final String ARG_DENOMINATOR = "denominator";

	// STATE
	private int denominator;
	private int numerator;
	// This can be animated.
	private int backgroundColor = Color.argb(255,255,255,0);

	// VIEWS
	private FractionCustomView fractionCustomView;
	private RelativeLayout rootLayout;
	private View referenceView;

	public static FractionPageFragment newInstance(int numerator, int denominator) {
		FractionPageFragment fragment = new FractionPageFragment();

		// This is a good example of how to use bundles to pass arguments around.
		Bundle args = new Bundle();
		args.putInt(ARG_NUMERATOR, numerator);
		args.putInt(ARG_DENOMINATOR, denominator);

		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// NOTE: For beginners, note that this is getArguments() set at instantiation, and not the savedInstanceState bundle.
		Bundle args = getArguments();
		if( args != null ) {
			numerator = args.getInt(ARG_NUMERATOR);
			denominator = args.getInt(ARG_DENOMINATOR);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.welcome_animated_fraction,container,false);

		rootLayout = (RelativeLayout) rootView.findViewById(R.id.rootRelativeLayout);
		rootLayout.setBackgroundColor(backgroundColor);

		fractionCustomView = (FractionCustomView) rootView.findViewById(R.id.fractionCustomView);
		fractionCustomView.setNumerator(numerator);
		fractionCustomView.setDenominator(denominator);

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public void setBackgroundColor(int backgroundColor) {
		this.backgroundColor = backgroundColor;
		rootLayout.setBackgroundColor(this.backgroundColor);
	}


}
