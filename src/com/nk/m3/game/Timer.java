package com.nk.m3.game;

import java.util.ArrayList;

public class Timer {
	public static interface FinishListener {
		public void onTimerFinished();
	}
	
	private float length;
	private float t;
	private ArrayList<FinishListener> finishListeners;
	
	public Timer(float len){
		length = len;
		t = 0.0f;
		finishListeners = new ArrayList<>();
	}
	
	public void addFinishListener(FinishListener listener){
		finishListeners.add(listener);
	}
	
	public void start(){
		t = length;
	}
	
	public float getProgress(){
		return t / length;
	}
	
	public void update(final float deltaTime){
		if (t > 0.0f){
			t -= deltaTime;
			if (t <= 0.0f){
				t = 0.0f;
				for (FinishListener listener : finishListeners){
					listener.onTimerFinished();
				}
			}
		}
	}
}
