package com.nk.m3.game;

import com.ovl.utils.Vector2;

public class GemMove extends Move {
	public GemPosition rowCol;
	public Gem gem;
	
	public GemMove(final Gem obj, final Vector2 from, final Vector2 to){
		super(obj, from, to);
		gem = obj;
	}
}
