package com.nk.m3.main;

import java.util.ArrayList;
import java.util.HashMap;

import com.nk.m3.game.Gem;
import com.nk.m3.game.GemColor;
import com.nk.m3.game.Grid;
import com.nk.m3.game.QuestManager;
import com.nk.m3.game.Rect;
import com.nk.m3.game.ScoreTracker;
import com.nk.m3.game.Timer;
import com.nk.m3.game.TimerAnimation;
import com.ovl.engine.OverloadEngine;
import com.ovl.engine.ParamSetter;
import com.ovl.engine.ParamSetterFactory;
import com.ovl.engine.Renderer;
import com.ovl.engine.Shader;
import com.ovl.engine.Vbo;
import com.ovl.game.GameObject;
import com.ovl.graphics.Color;
import com.ovl.graphics.CustomFont;
import com.ovl.graphics.Layer;
import com.ovl.graphics.Primitive;
import com.ovl.graphics.SimpleFont;
import com.ovl.graphics.SortedLayer;
import com.ovl.graphics.Sprite;
import com.ovl.graphics.UnsortedLayer;
import com.ovl.utils.Pair;
import com.ovl.utils.Paths;
import com.ovl.utils.Vector2;

public class GameState implements State, Timer.FinishListener {	
	private final int numRows = 9;
	private final int numCols = 9;
	private final float gameDuration = 300.0f;
	
	private Game game;
	private ArrayList<Gem> gems;
	private Grid grid;
	private Gem selectedGem;
	private ScoreTracker scoreTracker;
	private Timer gameTimer;
	private TimerAnimation timerAnimation;
	private QuestManager questManager;

	public Layer textLayer;
	public Layer gemLayer;
	
	public GameState(Game game){
		this.game = game;
	}
	
	@Override
	public void start() {
		float aspectRatio = OverloadEngine.getInstance().aspectRatio;
		
		gemLayer = new SortedLayer("gemlayer", 500);
		game.addLayer(gemLayer);
		
		Sprite sheet = new Sprite(Paths.getResources() + "gems.png");
		
		GemColor.RED.setSprite(Sprite.getSpriteFromSheet(0, 0, 64, 64, sheet));
		GemColor.GREEN.setSprite(Sprite.getSpriteFromSheet(64, 0, 64, 64, sheet));
		GemColor.BLUE.setSprite(Sprite.getSpriteFromSheet(128, 0, 64, 64, sheet));
		GemColor.WHITE.setSprite(Sprite.getSpriteFromSheet(0, 64, 64, 64, sheet));
		GemColor.PURPLE.setSprite(Sprite.getSpriteFromSheet(64, 64, 64, 64, sheet));
		GemColor.YELLOW.setSprite(Sprite.getSpriteFromSheet(128, 64, 64, 64, sheet));

		Vector2 gemSize = Vector2.pixelCoordsToNormal(new Vector2(64.0f, 64.0f));
		Vector2 padding = Vector2.pixelCoordsToNormal(new Vector2(2.0f, 2.0f));
		float startX = -(numCols * (gemSize.x + padding.x) * 0.5f) + gemSize.x * 0.5f;
		float startY = -(numRows * (gemSize.y + padding.y) * 0.5f);
		float x = startX;
		float y = startY + gemSize.y * 0.5f;
		
		GameObject gridBg = new GameObject();
		gridBg.setSprite(new Primitive(new Vector2[]{
			new Vector2(startX - 0.5f * gemSize.x - padding.x, startY),
			new Vector2(startX + (numCols - 0.5f) * (gemSize.x + padding.x), startY),
			new Vector2(startX + (numCols - 0.5f) * (gemSize.x + padding.x), startY + numRows * (gemSize.y + padding.y)),
			new Vector2(startX - 0.5f * gemSize.x - padding.x, startY + numRows * (gemSize.y + padding.y)),
		}, Renderer.PrimitiveType.Polygon));
		gridBg.setColor(new Color(0.1f, 0.1f, 0.1f));
		gemLayer.addObject(gridBg);

		gems = new ArrayList<Gem>();
		grid = new Grid(numRows, numCols);
		
		for (int row = 0; row < numRows; ++row){
			for (int col = 0; col < numCols; ++col){
				Gem g = new Gem();
				grid.gems[row][col] = g;
				gems.add(g);
				g.setPosition(x, y);
				gemLayer.addObject(g);
				
				grid.bounds[row][col] = new Rect(x, y, gemSize);
				
				x += gemSize.x + padding.x;
			}
			x = startX;
			y += gemSize.y + padding.y;
		}
		
		grid.initializeGrid();
		
		textLayer = new UnsortedLayer("textlayer", 1000);
		game.addLayer(textLayer);
		
		SimpleFont animatedScore[] = new SimpleFont[10];
		for (int i = 0; i < animatedScore.length; ++i){
			animatedScore[i] = SimpleFont.create("0");
			textLayer.addObject(animatedScore[i]);
		}
		
		float leftPanelX = ((-aspectRatio / 2.0f) + startX - gemSize.x * 0.5f) * 0.5f;
		float rightPanelX = -leftPanelX;
		
		SimpleFont score = SimpleFont.create("0");
		score.setPosition(leftPanelX, -0.8f);
		textLayer.addObject(score);
		scoreTracker = new ScoreTracker(score, animatedScore);
		grid.addMatchListener(scoreTracker);

		CustomFont smallFont = SimpleFont.getDefaultFont().deriveFont(24.0f);
		
		SimpleFont scoreText = SimpleFont.create("Score", smallFont);
		scoreText.setPosition(leftPanelX, -0.9f);
		textLayer.addObject(scoreText);
		
		gameTimer = new Timer(gameDuration);
		gameTimer.start();
		gameTimer.addFinishListener(grid);
		gameTimer.addFinishListener(scoreTracker);
		gameTimer.addFinishListener(this);

		float border = 0.01f * aspectRatio;
						
		GameObject timerBgObject = new GameObject();
		timerBgObject.setSprite(new Primitive(new Vector2[]{
			new Vector2(leftPanelX - 0.05f, -0.5f - border),
			new Vector2(leftPanelX + 0.05f, -0.5f - border),
			new Vector2(leftPanelX + 0.05f, 0.8f),
			new Vector2(leftPanelX - 0.05f, 0.8f),
		}, Renderer.PrimitiveType.Polygon));
		timerBgObject.setColor(new Color(0.3f, 0.3f, 0.3f));
		textLayer.addObject(timerBgObject);
		
		border = 0.0075f * aspectRatio;
		
		GameObject timerTextBg = new GameObject();
		timerTextBg.setSprite(new Primitive(new Vector2[]{
			new Vector2(leftPanelX - 0.045f, 0.7f + border),
			new Vector2(leftPanelX + 0.045f, 0.7f + border),
			new Vector2(leftPanelX + 0.045f, 0.8f - border),
			new Vector2(leftPanelX - 0.045f, 0.8f - border),
		}, Renderer.PrimitiveType.Polygon));
		timerTextBg.setColor(new Color(0.0f, 0.0f, 0.0f));
		textLayer.addObject(timerTextBg);
		
		SimpleFont timeleft = SimpleFont.create("Time", smallFont);
		timeleft.setPosition(leftPanelX, 0.75f);
		textLayer.addObject(timeleft);
		
		Primitive timerPrimitive = new Primitive(new Vector2[]{
			new Vector2(leftPanelX - 0.04f, -0.5f),
			new Vector2(leftPanelX + 0.04f, -0.5f),
			new Vector2(leftPanelX + 0.04f, 0.7f),
			new Vector2(leftPanelX - 0.04f, 0.7f),
		}, Renderer.PrimitiveType.Polygon);
		GameObject timerObject = new GameObject();
		timerObject.setSprite(timerPrimitive);
		textLayer.addObject(timerObject);
		timerAnimation = new TimerAnimation(timerPrimitive, gameTimer);
		
		SimpleFont quests = SimpleFont.create("Quests");
		quests.setPosition(rightPanelX, 0.75f);
		textLayer.addObject(quests);
		
		CustomFont verySmallFont = SimpleFont.getDefaultFont().deriveFont(16.0f);
		
		SimpleFont questTexts[] = new SimpleFont[5];
		for (int i = 0; i < questTexts.length; ++i){
			questTexts[i] = SimpleFont.create("quest placeholder", verySmallFont);
			questTexts[i].setPosition(rightPanelX, 0.55f - i * 0.25f);
			questTexts[i].setVisible(false);
			textLayer.addObject(questTexts[i]);
		}

		questManager = new QuestManager(questTexts);
		questManager.setListener(scoreTracker);
		grid.addMatchListener(questManager);
	}

	@Override
	public void finish() {
		game.removeLayer(gemLayer);
		gemLayer.destroy();
		
		game.removeLayer(textLayer);
		textLayer.destroy();
	}

	@Override
	public void update(float deltaTime) {
		timerAnimation.update();
		grid.update(deltaTime);
		scoreTracker.update(deltaTime);
		gameTimer.update(deltaTime);
		questManager.update(deltaTime);
	}

	@Override
	public void onClick(Vector2 pos) {
		Gem clicked = grid.onClick(pos);
		if (clicked != null){
			if (selectedGem == null){
				selectedGem = clicked;
				clicked.select();
			}
			else {
				if (selectedGem != clicked){
					boolean swapped = grid.swap(clicked, selectedGem);

					selectedGem.unselect();
					selectedGem = null;
					
					if (!swapped){
						selectedGem = clicked;
						clicked.select();
					}
				}
				else {
					selectedGem.unselect();
					selectedGem = null;
				}
			}
		}
		else if (selectedGem != null) {
			selectedGem.unselect();
			selectedGem = null;
		}
	}

	public void onTimerFinished(){
		finish();
		
		game.state = new EndState(game);
		game.state.start();
	}
}
