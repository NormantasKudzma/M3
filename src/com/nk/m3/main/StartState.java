package com.nk.m3.main;

import com.nk.m3.game.Variant;
import com.ovl.graphics.CustomFont;
import com.ovl.graphics.Layer;
import com.ovl.graphics.SimpleFont;
import com.ovl.graphics.Sprite;
import com.ovl.graphics.UnsortedLayer;
import com.ovl.ui.Button;
import com.ovl.ui.OnClickListener;
import com.ovl.utils.Paths;
import com.ovl.utils.Vector2;

public class StartState implements State {
	private Game game;
	private Layer layer;
	
	private State nextState;
	
	private SimpleFont classicText;
	private SimpleFont shortText;
	
	private Button playClassic;
	private Button playShort;
	private Button highScores;
	
	public StartState(Game game) {
		this.game = game;
	}

	@Override
	public void start() {
		layer = new UnsortedLayer("StartLayer", 10);
		game.addLayer(layer);
		
		CustomFont largeFont = SimpleFont.getDefaultFont().deriveFont(48.0f);
		CustomFont smallFont = SimpleFont.getDefaultFont().deriveFont(24.0f);
		CustomFont versionFont = SimpleFont.getDefaultFont().deriveFont(16.0f);
		
		SimpleFont title = SimpleFont.create("Match3 : super adventure", largeFont);
		title.setPosition(0.0f, 0.7f);
		layer.addObject(title);
		
		SimpleFont version = SimpleFont.create(String.format("v%s.%d", game.getVersion(), game.getBuild()), versionFont);
		version.setPosition(0.0f, 0.57f);
		layer.addObject(version);
		
		// -------------------- CLASSIC VARIANT --------------------------
		Variant classicV = Variant.CLASSIC;
		classicText = SimpleFont.create(String.format("%s : %d rows, %d cols, %.0f seconds", classicV.name, classicV.rows, classicV.cols, classicV.duration), smallFont);
		classicText.setPosition(0.0f, 0.1f);
		layer.addObject(classicText);
		
		playClassic = new Button();
		playClassic.setPosition(0.0f, -0.1f);
		playClassic.setScale(0.4f, 0.4f);
		playClassic.setClickListener(new OnClickListener() {		
			@Override
			public void clickFunction(Vector2 pos) {
				nextState = new GameState(game, Variant.CLASSIC);
				finish();
			}
		});
		layer.addObject(playClassic);
		
		SimpleFont playClassicText = SimpleFont.create("Classic", smallFont);
		playClassicText.setPosition(0.0f, -0.1f);
		layer.addObject(playClassicText);

		// -------------------- SHORT VARIANT --------------------------
		Variant shortV = Variant.SHORT;
		shortText = SimpleFont.create(String.format("%s : %d rows, %d cols, %.0f seconds", shortV.name, shortV.rows, shortV.cols, shortV.duration), smallFont);
		shortText.setPosition(0.0f, -0.5f);
		layer.addObject(shortText);
		
		playShort = new Button();
		playShort.setPosition(0.0f, -0.7f);
		playShort.setScale(0.4f, 0.4f);
		playShort.setClickListener(new OnClickListener() {		
			@Override
			public void clickFunction(Vector2 pos) {
				nextState = new GameState(game, Variant.SHORT);
				finish();
			}
		});
		layer.addObject(playShort);
		
		SimpleFont playShortText = SimpleFont.create("Short", smallFont);
		playShortText.setPosition(0.0f, -0.7f);
		layer.addObject(playShortText);
		
		highScores = new Button();
		highScores.setSprite(new Sprite(Paths.getResources() + "highscores.png"));
		highScores.setPosition(-0.8f, -0.8f);
		highScores.setScale(1.0f, 1.0f);
		highScores.setClickListener(new OnClickListener() {		
			@Override
			public void clickFunction(Vector2 pos) {
				nextState = new EndState(game, Variant.CLASSIC, false);
				finish();
			}
		});
		layer.addObject(highScores);
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
		
	}

	@Override
	public void onClick(Vector2 pos) {
		playClassic.onClick(pos);
		playShort.onClick(pos);
		highScores.onClick(pos);
	}
}
