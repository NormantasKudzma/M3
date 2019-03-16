package com.nk.m3.game;

import com.ovl.utils.Vector2;

public class QuestMove extends Move {
	public static final int in = 0;
	public static final int out = 1;
	public static final int move = 2;
	
	public Quest quest;
	private int type;
	
	public QuestMove(final Quest quest, final Vector2 from, final Vector2 to, final int type){
		super(quest.getTextObj(), from, to);
		this.quest = quest;
		this.type = type;
	}
	
	@Override
	public void set(float t) {
		super.set(t);
		switch (type){
			case in:{
				quest.getTextObj().getColor().rgba[3] = t;
				break;
			}
			case out:{
				quest.getTextObj().getColor().rgba[3] = 1.0f - t;
				break;
			}
			default:{
				break;
			}
		}
	}
}
