package com.nk.m3.game;

import com.ovl.graphics.SimpleFont;
import com.ovl.utils.Vector2;

public class ScoreTracker implements Grid.MatchListener, QuestManager.CompletionListener {
	class AnimatedFont {
		final float maxDuration = 1.2f;
		
		private SimpleFont textObject;
		private float t;
		private boolean isStarted;
		
		AnimatedFont(SimpleFont text){
			t = 0.0f;
			isStarted = false;
			textObject = text;
			textObject.setVisible(false);
		}
		
		void start(final Vector2 pos, final int score){
			t = 0.0f;
			isStarted = true;
			textObject.setPosition(pos);
			textObject.setVisible(true);
			textObject.setText(String.format("+%d", score));
		}
		
		void stop(){
			t = 0.0f;
			isStarted = false;
			textObject.setVisible(false);
		}
		
		void update(final float deltaTime){
			if (isStarted){
				t += deltaTime;
				textObject.setPosition(textObject.getPosition().add(0.0f, deltaTime * 0.15f));
				
				float rgba[] = textObject.getColor().rgba;
				rgba[3] = 1.0f - t / maxDuration;
				
				if (t >= maxDuration){
					stop();
				}
			}
		}
	}

	private SimpleFont scoreText;
	private AnimatedFont animatedScoreText[];
	private int animationIndex;
	private int score;
	private int matchChain;
	private boolean isChainStarted;
	
	public ScoreTracker(SimpleFont scoreText, SimpleFont animatedText[]){
		animatedScoreText = new AnimatedFont[animatedText.length];
		for (int i = 0; i < animatedText.length; ++i){
			animatedScoreText[i] = new AnimatedFont(animatedText[i]);
		}
		animationIndex = 0;
		
		this.scoreText = scoreText;
		reset();
	}
	
	@Override
	public void onMatchChain(Match match) {
		/** 
		 * On chain - increase chain size, but remove up to 3 from consecutive chains to
		 * avoid insanely big chain scores
		 */
		matchChain += match.gems.size(); 
		if (isChainStarted){
			matchChain -= Math.min((int)((float)matchChain * 0.1f + 0.5f), 3);
		}
		
		int amount = (matchChain * 10) + (matchChain - 3) * 10;		
		score += amount;
		
		playAnimatedText(amount);
		
		updateText();
		
		isChainStarted = true;
	}
	
	@Override
	public void onMatchChainEnded(){
		matchChain = 0;
		isChainStarted = false;
	}
	
	public void reset(){
		score = 0;
		matchChain = 0;
		isChainStarted = false;
		
		for (AnimatedFont a : animatedScoreText){
			a.stop();
		}
		updateText();
	}
	
	public void updateText(){
		scoreText.setText(String.format("%d", score));
	}
	
	public void update(final float deltaTime){
		for (AnimatedFont a : animatedScoreText){
			a.update(deltaTime);
		}
	}

	public void onGameFinished(Variant variant) {
		HighScoreManager.submit(variant, score);

		for (AnimatedFont a : animatedScoreText){
			a.stop();
		}
	}

	private void playAnimatedText(int amount){
		animatedScoreText[animationIndex].start(scoreText.getPosition(), amount);
		animationIndex = (animationIndex + 1) % animatedScoreText.length;
	}
	
	@Override
	public void onQuestCompleted(Quest quest) {
		playAnimatedText(quest.getReward());
		score += quest.getReward();
		updateText();
	}
}
