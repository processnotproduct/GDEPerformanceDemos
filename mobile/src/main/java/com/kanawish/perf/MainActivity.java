package com.kanawish.perf;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.kanawish.perf.hv.HVIntroActivity;
import com.kanawish.perf.welcome.WelcomeActivity;


public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent ;
				switch(v.getId()) {
					case R.id.welcome_button:
						intent = new Intent(MainActivity.this,WelcomeActivity.class);
						MainActivity.this.startActivity(intent);
						break;
					case R.id.hv_inefficient_button:
						intent = new Intent(MainActivity.this, HVIntroActivity.class);
						MainActivity.this.startActivity(intent);
						break;
					case R.id.hv_efficient_button:
						// TODO: Create this Activity
						break;
					default:
						break;
				}

			}
		};

		((Button) findViewById(R.id.welcome_button)).setOnClickListener(onClickListener);
		((Button) findViewById(R.id.hv_inefficient_button)).setOnClickListener(onClickListener);
	}


}
