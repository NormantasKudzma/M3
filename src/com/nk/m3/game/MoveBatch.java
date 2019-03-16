package com.nk.m3.game;

import java.util.ArrayList;

public class MoveBatch<T extends Move> {
	static interface MoveFinishedListener<T extends Move> {
		public void onMovesFinished(final ArrayList<T> moves);
	}
	
	public ArrayList<T> moves;
	private float t;
	private ArrayList<MoveFinishedListener<T>> listeners;
	private ArrayList<MoveFinishedListener<T>> removedListeners;
	
	public MoveBatch(){
		this(null);
	}
	
	public MoveBatch(MoveFinishedListener<T> listener){
		t = 0.0f;
		moves = new ArrayList<T>();
		listeners = new ArrayList<>();
		removedListeners = new ArrayList<>();
		if (listener != null){
			listeners.add(listener);
		}
	}
	
	public void addListener(MoveFinishedListener<T> listener){
		listeners.add(listener);
	}
	
	public void removeListener(MoveFinishedListener<T> listener){
		removedListeners.add(listener);
	}
	
	public void addMove(final T m){
		moves.add(m);
	}
	
	public void update(final float deltaTime){
		if (!removedListeners.isEmpty()){
			listeners.removeAll(removedListeners);
			removedListeners.clear();
		}
		
		if (moves.isEmpty()){
			return;
		}
		
		t = Math.min(t + deltaTime * 5.0f, 1.0f);
		
		for (Move m : moves){
			m.set(t);
		}
		
		if (t >= 1.0f){
			ArrayList<T> copy = new ArrayList<T>(moves);
			moves.clear();
			for (MoveFinishedListener<T> l : listeners){
				l.onMovesFinished(copy);
			}
			t = 0.0f;
		}
	}
}
