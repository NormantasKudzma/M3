package com.nk.m3.main;


import com.nk.m3.game.EditableText;
import com.nk.m3.game.EditableText.FocusListener;
import com.nk.m3.game.HighScoreManager;
import com.ovl.graphics.Color;
import com.ovl.graphics.Layer;
import com.ovl.graphics.SimpleFont;
import com.ovl.graphics.UnsortedLayer;
import com.ovl.utils.Vector2;

public class EndState implements State, FocusListener {
	private Game game;
	private Layer textLayer;
	private float stateTimer;
	private EditableText nameTextField;
	
	public EndState(Game game){
		this.game = game;
	}
	
	@Override
	public void start() {
		textLayer = new UnsortedLayer("textlayer", 1000);
		game.addLayer(textLayer);
		
		SimpleFont gameOver = SimpleFont.create(String.format("Game over, score %d", HighScoreManager.last()));
		gameOver.setPosition(0.0f, 0.8f);
		textLayer.addObject(gameOver);
		
		SimpleFont highScores  = SimpleFont.create("Highscores");
		highScores.setPosition(0.0f, 0.6f);
		textLayer.addObject(highScores);
		
		SimpleFont nameFieldObj = SimpleFont.create("");
		nameFieldObj.setPosition(-0.5f, 0.0f);
		textLayer.addObject(nameFieldObj);
		nameTextField = new EditableText(nameFieldObj, 3);
		
		for (int i = 1; i <= 10; ++i){
			float y = 0.5f - i * 0.1f;
			
			SimpleFont place = SimpleFont.create(String.format("%d.", i));
			place.setPosition(-0.2f, y);
			textLayer.addObject(place);
			
			SimpleFont score = SimpleFont.create(String.format("%d", HighScoreManager.get(i)));
			score.setPosition(0.0f, y);
			textLayer.addObject(score);
			
			SimpleFont name = SimpleFont.create(HighScoreManager.getName(i));
			name.setPosition(0.2f, y);
			textLayer.addObject(name);
			
			if (HighScoreManager.lastPos() == i){
				score.setColor(new Color(1.0f, 0.9f, 0.2f));
				nameTextField.setListener(this);
				nameFieldObj.setPosition(0.2f, y);
				nameTextField.select();
				name.setVisible(false);
			}
		}
		
		SimpleFont continueText  = SimpleFont.create("Click to play again..");
		continueText.setPosition(0.0f, -0.9f);
		textLayer.addObject(continueText);
		
		stateTimer = 0.0f;
	}

	@Override
	public void finish() {
		game.removeLayer(textLayer);
		textLayer.destroy();
	}

	@Override
	public void update(float deltaTime) {
		stateTimer += deltaTime;
		nameTextField.update(deltaTime);
	}

	@Override
	public void onClick(Vector2 pos) {
		if (stateTimer > 2.0f){
			nameTextField.unselect();
			
			finish();
			
			game.state = new GameState(game);
			game.state.start();
		}
	}

	@Override
	public void onFocusChanged(boolean isFocused) {
		if (!isFocused){
			HighScoreManager.setName(HighScoreManager.lastPos(), nameTextField.getText());
		}
	}
}
