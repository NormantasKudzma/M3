package com.nk.m3.game;

public class GemPosition {
	public int row;
	public int col;
	
	public GemPosition(){
		row = -1;
		col = -1;
	}
	
	public GemPosition(final int r, final int c){
		row = r;
		col = c;
	}
}
