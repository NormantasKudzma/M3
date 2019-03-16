package com.nk.m3.game;

import com.ovl.utils.Vector2;

public class Rect {
	float l, r, t, b;
	
	public Rect(float x, float y, Vector2 size){
		Vector2 halfSize = size.copy().mul(0.5f);
		
		l = x - halfSize.x;
		r = x + halfSize.x;
		b = y - halfSize.y;
		t = y + halfSize.y;
	}
	
	public Vector2 center(){
		return new Vector2((r + l) * 0.5f, (b + t) * 0.5f);
	}
	
	public boolean contains(final Vector2 pos){
		return pos.x > l && pos.x < r && pos.y > b && pos.y < t;
	}
}
