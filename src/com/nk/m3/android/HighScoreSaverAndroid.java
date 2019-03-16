package com.nk.m3.android;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.nk.m3.game.HighScoreManager.HighScoreSaver;
import com.nk.m3.game.HighScoreManager.HighScore;

public class HighScoreSaverAndroid implements HighScoreSaver {
	private SharedPreferences preferences;

	public HighScoreSaverAndroid(Activity activity){
		preferences = activity.getPreferences(Context.MODE_PRIVATE);
	}
	
	@Override
	public void save(HighScore[] scores) {
		SharedPreferences.Editor editor = preferences.edit();
		for (int i = 1; i <= scores.length; ++i){
			editor.putInt("score" + i, scores[i - 1].score);
		}
		editor.apply();
	}

	@Override
	public void load(HighScore[] scores) {
		int defaultValue = -1;
		for (int i = 1; i <= scores.length; ++i){
			int value = preferences.getInt("score" + i, defaultValue);
			if (value == defaultValue){
				break;
			}
			scores[i - 1].score = value;
		}
	}

	@Override
	public void init() {
		
	}
}
