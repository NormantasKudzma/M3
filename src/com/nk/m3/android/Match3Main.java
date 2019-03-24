package com.nk.m3.android;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import com.nk.m3.BuildConfig;
import com.nk.m3.game.HighScoreManager;
import com.nk.m3.main.Game;
import com.ovl.engine.EngineConfig;
import com.ovl.engine.OverloadEngine;
import com.ovl.engine.android.OverloadEngineAndroid;
import com.ovl.utils.Vector2;

public class Match3Main extends Activity {
	OverloadEngineAndroid engine;
	Game game = new Game();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		HighScoreManager.saver = new HighScoreSaverAndroid(this);
		game.platform = new Game.Platform(){
			public void init(){}

			public String getVersion(){
				return BuildConfig.VERSION_NAME;
			}

			public int getBuild(){
				return BuildConfig.VERSION_CODE;
			}
		};
	}
	
	@Override
	protected void onStart() {
		super.onStart();

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);

		getWindow().getDecorView().getRootView().setOnTouchListener(new View.OnTouchListener(){
			
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				int pointerIndex = e.getActionIndex();
				int pointerId = e.getPointerId(pointerIndex);
				
				switch (e.getActionMasked()){
					case MotionEvent.ACTION_POINTER_DOWN:
					case MotionEvent.ACTION_DOWN:{

						OverloadEngine instance = OverloadEngine.getInstance();
						final float ac = instance.aspectRatio / 2.0f;
						final float xc = 2.0f * ac / instance.frameWidth;
						final float yc = -2.0f / instance.frameHeight;

						Vector2 pos = new Vector2(e.getX(pointerIndex), e.getY(pointerIndex));
						pos.mul(xc, yc).add(-ac, 1.0f);
						game.postClick(pos);
						break;
					}
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_POINTER_UP:{
						getWindow().getDecorView().getRootView().performClick();
						break;
					}
				}
				return true;
			}
			
		});
		
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		ConfigurationInfo info = am.getDeviceConfigurationInfo();
		
		if (info.reqGlEsVersion >= 0x20000){
			if (engine == null){
				EngineConfig cfg = new EngineConfig();
				cfg.game = game;
				cfg.isDebug = false;
				engine = new OverloadEngineAndroid(cfg);
				engine.referenceWidth = 960;
				engine.referenceHeight = 640;
			}
			setContentView(engine.getSurfaceView(this));
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		engine.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		engine.onResume();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			getWindow().getDecorView().getRootView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE
							| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
							| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
			);
		}
	}
}
