package com.nk.m3.game;

import java.util.ArrayList;

import com.ovl.graphics.Primitive;
import com.ovl.utils.Vector2;

public class TimerAnimation {
	private Primitive primitive;
	private ArrayList<Vector2> topVerts;
	private Timer timer;
	private float maxY;
	private float minY;
	
	public TimerAnimation(Primitive p, Timer timer){
		primitive = p;
		this.timer = timer;
		topVerts = new ArrayList<>();
		
		maxY = 0.0f;
		minY = 2.0f;
		
		for (Vector2 v : p.getVertices()){
			if (v.y > maxY){
				maxY = v.y;
			}
			if (v.y < minY){
				minY = v.y;
			}
		}
		
		for (Vector2 v : p.getVertices()){
			if (v.y >= maxY){
				topVerts.add(v);
			}
		}
	}
	
	public void update(){
		float progress = timer.getProgress();
		float y = (maxY - minY) * progress + minY;
		
		for (Vector2 v : topVerts){
			v.y = y;
		}
		
		float rgba[] = primitive.getColor().rgba;
		rgba[0] = 1.0f - progress;
		rgba[1] = progress;
		rgba[2] = 0.0f;
		
		primitive.refreshVertexData();
	}
}
