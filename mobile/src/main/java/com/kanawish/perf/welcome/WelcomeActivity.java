package com.kanawish.perf.welcome;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kanawish.perf.R;

/**
 * Based off Android ViewPager template.
 *
 * TODO: Use native Fragments vs SupportFragment...
 *
 * http://stackoverflow.com/questions/17553374/android-app-fragments-vs-android-support-v4-app-using-viewpager
 *
 */
public class WelcomeActivity extends FragmentActivity {

	private static final String TAG = WelcomeActivity.class.getSimpleName();

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will keep every
	 * loaded fragment in memory. If this becomes too memory intensive, it
	 * may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	private SectionsPagerAdapter sectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	private ViewPager viewPager;

	private FractionPageFragment fractionPageFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome_main);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// To be animated, will be hard-coded as page 2 in this example.
		fractionPageFragment = FractionPageFragment.newInstance(1, 100);

		ViewPager.PageTransformer pageTransformer = new ViewPager.PageTransformer() {
			@Override
			public void transformPage(View page, float position) {
				if( fractionPageFragment.getView() == page ) {
					// Somewhat wrong since it always will animate, but good for demo purposes.
					int green = (int) (128f + (127f * position));

					Log.d(TAG, String.format("Animating green: %d",green)); // Logging here causes a bit lag.
					fractionPageFragment.setBackgroundColor(Color.argb(255, 255, green, 0));
				}
			}
		};

		// Set up the ViewPager with the sections adapter.
		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(sectionsPagerAdapter);
		viewPager.setPageTransformer(false,pageTransformer);
	}

	/**
	 * Mostly added this as an enum example for people new to Java.
	 */
	enum WelcomePage {
		PAGE_1(R.layout.welcome_page1),
		PAGE_2(R.layout.welcome_page2),
		PAGE_3(R.layout.welcome_page3);

		private final int layoutId;

		WelcomePage(int layoutId) {
			this.layoutId = layoutId;
		}
	};

	/**
	 * This class is responsible for feeding Fragments to the ViewPager as needed, as
	 * well as letting it know how many pages are available.
	 */
	private class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// Replace page 2
			if( position == 1 ) {
				return fractionPageFragment;
			}
			return PageFragment.newInstance(WelcomePage.values()[position].layoutId);
		}

		@Override
		public int getCount() {
			return WelcomePage.values().length;
		}
	}

	/**
	 * Fragments are a fairly complex subject when starting out on the platform.
	 *
	 * Here's a couple of useful links to start with:
	 *
	 * http://developer.android.com/guide/components/fragments.html
	 * http://stackoverflow.com/questions/10450348/do-fragments-really-need-an-empty-constructor
	 *
	 */
	private static class PageFragment extends Fragment {

		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_LAYOUT_ID = "layout_id";

		/**
		 * Returns a new instance of this fragment for the given section
		 * number.
		 */
		public static PageFragment newInstance(int layoutId) {
			PageFragment fragment = new PageFragment();

			// This is a good example of how to use bundles to pass arguments around.
			Bundle args = new Bundle();
			args.putInt(ARG_LAYOUT_ID, layoutId);
			fragment.setArguments(args);

			return fragment;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
								 Bundle savedInstanceState) {
			Bundle arguments = getArguments();
			View rootView = inflater.inflate(arguments.getInt(ARG_LAYOUT_ID), container, false);
			return rootView;
		}

	}

}
