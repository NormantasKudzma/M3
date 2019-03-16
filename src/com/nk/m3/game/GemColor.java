package com.nk.m3.game;

import com.ovl.graphics.Sprite;

public enum GemColor {
	RED,
	GREEN,
	BLUE,
	WHITE,
	PURPLE,
	YELLOW;
	
	private Sprite sprite;
	
	public Sprite getSprite(){
		return sprite;
	}
	
	public void setSprite(Sprite sprite){
		this.sprite = sprite;
	}
}
