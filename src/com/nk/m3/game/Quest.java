package com.nk.m3.game;

import java.util.ArrayList;

import com.nk.m3.game.QuestManager.CompletionListener;
import com.nk.m3.game.QuestManager.QuestType;
import com.ovl.graphics.SimpleFont;

public class Quest implements MoveBatch.MoveFinishedListener<QuestMove> {
	public SimpleFont textObj;
	public QuestType type;
	public GemColor color;
	public String format;
	public int comboSize;
	public int matchCount;
	public int count;
	public int reward;
	public int lastMatchSize;
	private CompletionListener listener;
	private MoveBatch<QuestMove> moveBatch;
	private QuestManager manager;
	private boolean isFinished;
	
	public Quest(SimpleFont text, CompletionListener listener, MoveBatch<QuestMove> moveBatch, QuestManager manager){
		textObj = text;
		this.listener = listener;
		this.moveBatch = moveBatch;
		this.manager = manager;
	}
	
	public SimpleFont getTextObj(){
		return textObj;
	}
	
	public int getReward(){
		return reward;
	}
	
	public void onMatchChain(final Match match){
		if (isFinished){
			return;
		}
		
		boolean update = false;
		switch (type){
			case MakeCombos:{
				++matchCount;
				break;
			}
			case MatchColors:{
				for (Gem g : match.gems){
					if (g.getGemColor() == color){
						--count;
						update = true;
					}
				}
				break;
			}
			case MatchSizes:{
				if (match.gems.size() == comboSize){
					--count;
					update = true;
				}
				break;
			}
			case MatchSizesInARow:{
				if (match.gems.size() == comboSize){
					if (lastMatchSize == comboSize){
						--count;
						update = true;
					}
					else {
						lastMatchSize = comboSize;
					}
				}
				else {
					lastMatchSize = 0;
				}
				break;
			}
		}
		
		if (update){
			if (count <= 0){
				finish();
			}
			updateText();
		}
	}
	
	public void onMatchChainEnded(){
		if (isFinished){
			return;
		}
		
		boolean update = false;
		switch (type){
			case MakeCombos:{
				if (matchCount > 1){
					--count;
					update = true;
				}
				matchCount = 0;
				break;
			}
			default:{
				break;
			}
		}
		
		if (update){
			if (count <= 0){
				finish();
			}
			updateText();
		}
	}
	
	public void updateText(){
		switch (type){
			case MakeCombos:{
				textObj.setText(String.format(format, count, reward));
				break;
			}
			case MatchColors:{
				textObj.setText(String.format(format, color.toString(), count, reward));
				break;
			}
			case MatchSizesInARow:
			case MatchSizes:{
				textObj.setText(String.format(format, comboSize, count, reward));
				break;
			}
		}
	}
	
	private void finish(){
		isFinished = true;
		moveBatch.addMove(new QuestMove(this, textObj.getPosition(), textObj.getPosition().copy().add(0.25f, 0.0f), QuestMove.out));
		moveBatch.addListener(this);
	}

	@Override
	public void onMovesFinished(ArrayList<QuestMove> moves) {
		for (QuestMove qm : moves){
			if (qm.quest == this){
				if (listener != null){
					listener.onQuestCompleted(this);
				}
				textObj.setVisible(false);
				moveBatch.removeListener(this);
				manager.removeQuest(this);
				break;
			}
		}
	}
}
