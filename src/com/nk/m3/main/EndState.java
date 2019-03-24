package com.nk.m3.main;


import com.nk.m3.game.EditableText;
import com.nk.m3.game.EditableText.FocusListener;
import com.nk.m3.game.HighScoreManager;
import com.nk.m3.game.Variant;
import com.ovl.graphics.Color;
import com.ovl.graphics.Layer;
import com.ovl.graphics.SimpleFont;
import com.ovl.graphics.Sprite;
import com.ovl.graphics.UnsortedLayer;
import com.ovl.ui.Button;
import com.ovl.ui.OnClickListener;
import com.ovl.utils.Paths;
import com.ovl.utils.Vector2;

public class EndState implements State, FocusListener {
	private Game game;
	private Layer layer;
	private float stateTimer;
	private EditableText nameTextField;
	private Variant variant;
	private boolean showGameOver;
	
	private Button arrowLeft;
	private Button arrowRight;
	private State nextState;
	
	public EndState(Game game, Variant variant, boolean showGameOver){
		this.game = game;
		this.variant = variant;
		this.showGameOver = showGameOver;
	}
	
	@Override
	public void start() {
		layer = new UnsortedLayer("textlayer", 1000);
		game.addLayer(layer);
		
		if (showGameOver) {
			SimpleFont gameOver = SimpleFont.create(String.format("Game over, score %d", HighScoreManager.last(variant)));
			gameOver.setPosition(0.0f, 0.8f);
			layer.addObject(gameOver);
		}
		
		SimpleFont highScores  = SimpleFont.create(String.format("Highscores : %s", variant.name));
		highScores.setPosition(0.0f, 0.6f);
		layer.addObject(highScores);
		
		SimpleFont nameFieldObj = SimpleFont.create("");
		nameFieldObj.setPosition(-0.5f, 0.0f);
		layer.addObject(nameFieldObj);
		nameTextField = new EditableText(nameFieldObj, 3);
		
		for (int i = 1; i <= 10; ++i){
			float y = 0.5f - i * 0.1f;
			
			SimpleFont place = SimpleFont.create(String.format("%d.", i));
			place.setPosition(-0.2f, y);
			layer.addObject(place);
			
			SimpleFont score = SimpleFont.create(String.format("%d", HighScoreManager.getScore(variant, i)));
			score.setPosition(0.0f, y);
			layer.addObject(score);
			
			SimpleFont name = SimpleFont.create(HighScoreManager.getName(variant, i));
			name.setPosition(0.2f, y);
			layer.addObject(name);
			
			if (HighScoreManager.lastPos(variant) == i && showGameOver){
				score.setColor(new Color(1.0f, 0.9f, 0.2f));
				nameTextField.setListener(this);
				nameFieldObj.setPosition(0.2f, y);
				nameTextField.select();
				name.setVisible(false);
			}
		}
		
		if (showGameOver) {
			SimpleFont continueText  = SimpleFont.create("Click to play again..");
			continueText.setPosition(0.0f, -0.9f);
			layer.addObject(continueText);
		}

		arrowLeft = new Button();
		arrowLeft.setSprite(new Sprite(Paths.getResources() + "arrow_left.png"));
		arrowLeft.setPosition(-0.7f, 0.0f);
		arrowLeft.setScale(1.5f, 1.5f);
		arrowLeft.setClickListener(new OnClickListener() {		
			@Override
			public void clickFunction(Vector2 pos) {
				Variant nextV = Variant.values()[ (variant.ordinal() + 1) % Variant.values().length ];
				nextState = new EndState(game, nextV, false);
				finish();
			}
		});
		layer.addObject(arrowLeft);
		
		arrowRight = new Button();
		arrowRight.setSprite(new Sprite(Paths.getResources() + "arrow_right.png"));
		arrowRight.setPosition(0.7f, 0.0f);
		arrowRight.setScale(1.5f, 1.5f);
		arrowRight.setClickListener(new OnClickListener() {		
			@Override
			public void clickFunction(Vector2 pos) {
				Variant nextV = Variant.values()[ (variant.ordinal() + 1) % Variant.values().length ];
				nextState = new EndState(game, nextV, false);
				finish();
			}
		});
		layer.addObject(arrowRight);
		
		stateTimer = 0.0f;
	}

	@Override
	public void finish() {
		game.removeLayer(layer);
		layer.destroy();
		
		game.state = nextState;
		game.state.start();
	}

	@Override
	public void update(float deltaTime) {
		stateTimer += deltaTime;
		nameTextField.update(deltaTime);
	}

	@Override
	public void onClick(Vector2 pos) {
		if (arrowLeft.onClick(pos)) {
			return;
		}
		if (arrowRight.onClick(pos)) {
			return;
		}
		
		if (!showGameOver || stateTimer > 1.0f){
			nameTextField.unselect();
			
			nextState = new StartState(game);
			finish();
		}
	}

	@Override
	public void onFocusChanged(boolean isFocused) {
		if (!isFocused){
			HighScoreManager.setName(variant, HighScoreManager.lastPos(variant), nameTextField.getText());
		}
	}
}
