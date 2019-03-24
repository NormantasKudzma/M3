package com.nk.m3.game;

public enum Variant {
	CLASSIC("Classic", 9, 9, 300.0f),
	SHORT("Short", 8, 10, 120.0f);
	
	private Variant(String name, int rows, int cols, float duration) {
		this.name = name;
		this.rows = rows;
		this.cols = cols;
		this.duration = duration;
	}
	
	public final String name;
	public final int rows;
	public final int cols;
	public final float duration;
}
