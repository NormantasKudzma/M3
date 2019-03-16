package com.nk.m3.main;

import java.util.ArrayList;

import com.ovl.game.BaseGame;
import com.ovl.utils.Vector2;

public class Game extends BaseGame {
	public static interface PlatformInit {
		public void init();
	}
	
	public State state;
	public PlatformInit platformInit;
	private ArrayList<Vector2> clickList;
	
	@Override
	public void init() {
		super.init();
		
		clickList = new ArrayList<>();
		state = new GameState(this);
		state.start();
		
		if (platformInit != null){
			platformInit.init();
		}
	}
	
	public void postClick(Vector2 pos){
		synchronized (clickList){
			clickList.add(pos);
		}
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		synchronized (clickList){
			for (Vector2 click : clickList){
				state.onClick(click);
			}
			clickList.clear();
		}
		
		state.update(deltaTime);
	}
}
