package com.nk.m3.game;

import com.ovl.game.GameObject;
import com.ovl.utils.Vector2;

public abstract class Move {
	public static interface ProgressFunction {
		public float get(final float t);
	}
	
	private static final ProgressFunction defaultFunction = new ProgressFunction(){
		public float get(float t) {
			return t * t * t;
		};
	};
	
	public Vector2 from;
	public Vector2 to;
	public GameObject obj;
	public ProgressFunction function;
	
	public Move(final GameObject obj, final Vector2 from, final Vector2 to){
		this.from = from;
		this.to = to;
		this.obj = obj;
	}
	
	public void set(final float t){
		float progress = function != null ? function.get(t) : defaultFunction.get(t);			
		obj.setPosition(from.x + (to.x - from.x) * progress, from.y + (to.y - from.y) * progress);
	}
}
