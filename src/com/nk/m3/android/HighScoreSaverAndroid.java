package com.nk.m3.android;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.nk.m3.game.HighScoreManager;
import com.nk.m3.game.HighScoreManager.VariantScores;
import com.nk.m3.game.HighScoreManager.HighScoreSaver;
import com.nk.m3.game.HighScoreManager.HighScore;

public class HighScoreSaverAndroid implements HighScoreSaver {
	private SharedPreferences preferences;

	public HighScoreSaverAndroid(Activity activity){
		preferences = activity.getPreferences(Context.MODE_PRIVATE);
	}
	
	@Override
	public void save(VariantScores variantScores[]) {
		SharedPreferences.Editor editor = preferences.edit();

		for (VariantScores v : variantScores){
			for (int i = 1; i <= v.scores.length; ++i){
				editor.putInt(v.variant.name + "_score" + i, v.scores[i - 1].score);
			}
		}

		editor.apply();
	}

	@Override
	public void load(VariantScores variantScores[]) {
		final int defaultValue = -1;

		for (VariantScores v : variantScores){
			for (int i = 1; i <= v.scores.length; ++i){
				int value = preferences.getInt(v.variant.name +"_score" + i, defaultValue);
				if (value == defaultValue){
					break;
				}

				v.scores[i - 1].score = value;
			}
		}
	}

	@Override
	public void init() {
		
	}
}
