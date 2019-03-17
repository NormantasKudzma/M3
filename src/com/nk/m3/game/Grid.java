package com.nk.m3.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ovl.utils.OverloadRandom;
import com.ovl.utils.Vector2;

public class Grid implements MoveBatch.MoveFinishedListener<GemMove>, Timer.FinishListener {
	static interface MatchListener {
		public void onMatchChain(Match match);
		public void onMatchChainEnded();
	}
	
	enum State {
		SWAPBACK, SWAPPING, FALLING, DESTROYING, IDLE
	}

	private final Move.ProgressFunction fallFunction = new Move.ProgressFunction() {
		public float get(final float t) {
			return t;
		};
	};
	private final float destroyDuration = 0.3f;
	
	public Gem gems[][];
	public Rect bounds[][];

	private ArrayList<MatchListener> matchListeners;
	private Match lastMatch;
	private MoveBatch<GemMove> moveBatch;
	private State state;
	private int rows;
	private int cols;
	private float stateTimer;

	public Grid(int rows, int cols) {
		gems = new Gem[rows][cols];
		bounds = new Rect[rows][cols];

		this.rows = rows;
		this.cols = cols;

		matchListeners = new ArrayList<>();
		moveBatch = new MoveBatch<>(this);
		state = State.IDLE;
	}

	public void addMatchListener(final MatchListener listener){
		matchListeners.add(listener);
	}
	
	public Gem onClick(final Vector2 pos) {
		if (state != State.IDLE) {
			return null;
		}

		for (int row = 0; row < rows; ++row) {
			for (int col = 0; col < cols; ++col) {
				if (bounds[row][col].contains(pos)) {
					return gems[row][col];
				}
			}
		}

		return null;
	}

	public boolean swap(final Gem first, final Gem second) {
		GemPosition firstRowCol = findGem(first);
		GemPosition secondRowCol = findGem(second);

		if (!isAdjacent(firstRowCol, secondRowCol)) {
			return false;
		}

		GemMove firstMove = new GemMove(first, first.getPosition(), bounds[secondRowCol.row][secondRowCol.col].center());
		firstMove.rowCol = secondRowCol;
		moveBatch.addMove(firstMove);

		GemMove secondMove = new GemMove(second, second.getPosition(), bounds[firstRowCol.row][firstRowCol.col].center());
		secondMove.rowCol = firstRowCol;
		moveBatch.addMove(secondMove);

		gems[firstRowCol.row][firstRowCol.col] = second;
		gems[secondRowCol.row][secondRowCol.col] = first;

		state = State.SWAPPING;
		return true;
	}

	private boolean isAdjacent(final GemPosition i, final GemPosition j) {
		int dx = j.col - i.col;
		int dy = j.row - i.row;
		return dx <= 1 && dx >= -1 && dy <= 1 && dy >= -1 && (dx & dy) == 0;
	}

	public void update(final float deltaTime) {
		if (state == State.IDLE){
			return;
		}
		
		moveBatch.update(deltaTime);

		stateTimer += deltaTime;

		switch (state) {
			case DESTROYING: {
				if (stateTimer > destroyDuration) {
					if (!lastMatch.gems.isEmpty()) {
						fillGems(lastMatch);
						state = State.FALLING;
						lastMatch = null;
					}
					else {
						onChainEnd();
						state = State.IDLE;
					}
				}
				break;
			}
			default: {
				break;
			}
		}
	}

	@Override
	public void onMovesFinished(final ArrayList<GemMove> moves) {
		switch (state) {
			case SWAPBACK: {
				state = State.IDLE;
				break;
			}
			case SWAPPING: {
				Match match = getMatch(moves);

				if (match.gems.isEmpty()) {
					if (moves.size() < 2) {
						state = State.IDLE;
					}
					else {
						swap(moves.get(0).gem, moves.get(1).gem);
						state = State.SWAPBACK;
					}
				}
				else {
					destroyGems(match);
					onMatch(match);
					stateTimer = 0.0f;
					lastMatch = match;
					state = State.DESTROYING;
				}
				break;
			}
			case FALLING: {
				Match match = getMatch(moves);

				if (!match.gems.isEmpty()) {
					destroyGems(match);
					onMatch(match);
					stateTimer = 0.0f;
					lastMatch = match;
					state = State.DESTROYING;
				}
				else {
					onChainEnd();
					state = State.IDLE;
				}
				break;
			}
			default: {
				state = State.IDLE;
				break;
			}
		}
	}

	private Match getMatch(final ArrayList<GemMove> moves) {
		Match match = new Match();
		for (GemMove m : moves) {
			fillMatch(m.rowCol.row, m.rowCol.col, match);
		}

		return match;
	}

	private void fillMatch(final int row, final int col, final Match match) {
		final GemColor color = gems[row][col].getGemColor();
		ArrayList<Gem> matchedGemsV = new ArrayList<Gem>();
		ArrayList<Gem> matchedGemsH = new ArrayList<Gem>();

		// Look for a vertical match
		int startRow = Math.max(row - 2, 0);
		int endRow = Math.min(row + 2, rows - 1);

		for (int i = startRow; i <= endRow; ++i) {
			if (gems[i][col].getGemColor() == color) {
				matchedGemsV.add(gems[i][col]);
			}
			else
				if (matchedGemsV.size() < 3) {
					matchedGemsV.clear();
				}
				else {
					break;
				}
		}

		// Look for a horizontal match
		int startCol = Math.max(col - 2, 0);
		int endCol = Math.min(col + 2, cols - 1);

		for (int i = startCol; i <= endCol; ++i) {
			if (gems[row][i].getGemColor() == color) {
				matchedGemsH.add(gems[row][i]);
			}
			else
				if (matchedGemsH.size() < 3) {
					matchedGemsH.clear();
				}
				else {
					break;
				}
		}

		if (matchedGemsV.size() >= 3) {
			match.gems.addAll(matchedGemsV);
		}

		if (matchedGemsH.size() >= 3) {
			match.gems.addAll(matchedGemsH);
		}
	}

	private void fillGems(final Match match) {
		ArrayList<GemPosition> oldPositions = new ArrayList<>();
		for (Gem gem : match.gems) {
			GemPosition pos = findGem(gem);
			gems[pos.row][pos.col] = null;
			randomizeColor(gem);
			gem.unshrink();
			gem.setPosition(gem.getPosition().add(0.0f, 2.5f));
			oldPositions.add(pos);
		}

		for (int col = 0; col < cols; ++col) {
			for (int row = 0; row < rows; ++row) {
				if (gems[row][col] == null) {
					for (int rowAbove = row + 1; rowAbove < rows; ++rowAbove) {
						if (gems[rowAbove][col] != null) {
							Vector2 from = bounds[rowAbove][col].center();
							Vector2 to = bounds[row][col].center();

							GemMove move = new GemMove(gems[rowAbove][col], from, to);
							move.rowCol = new GemPosition(row, col);
							move.function = fallFunction;
							moveBatch.addMove(move);

							gems[row][col] = gems[rowAbove][col];
							gems[rowAbove][col] = null;
							break;
						}
					}
				}
			}
		}

		int i = 0;
		for (Gem gem : match.gems) {
			int col = oldPositions.get(i).col;
			for (int row = 0; row < rows; ++row) {
				if (gems[row][col] == null) {
					Vector2 from = gem.getPosition();
					Vector2 to = bounds[row][col].center();
					GemMove move = new GemMove(gem, from, to);
					move.rowCol = new GemPosition(row, col);
					move.function = fallFunction;
					moveBatch.addMove(move);

					gems[row][col] = gem;
					break;
				}
			}

			++i;
		}
	}

	private GemPosition findGem(final Gem gem) {
		for (int row = 0; row < rows; ++row) {
			for (int col = 0; col < cols; ++col) {
				if (gems[row][col] == gem) {
					return new GemPosition(row, col);
				}
			}
		}

		return null;
	}

	public void randomizeColor(final Gem gem) {
		final GemColor[] gemColors = GemColor.values();
		gem.setGemColor(gemColors[OverloadRandom.next(gemColors.length)]);
	}
	
	private void randomizeColor(final Gem gem, final List<GemColor> subset){
		gem.setGemColor(subset.get(OverloadRandom.next(subset.size())));
	}

	private ArrayList<GemColor> getNeighbourColors(int row, int col){
		ArrayList<GemColor> colors = new ArrayList<GemColor>();
		
		if (row > 0) {
			colors.add(gems[row - 1][col].getGemColor());
		}
		
		if (row < rows - 1) {
			colors.add(gems[row + 1][col].getGemColor());
		}
		
		if (col > 0) {
			colors.add(gems[row][col - 1].getGemColor());
		}

		if (col < cols - 1) {
			colors.add(gems[row][col + 1].getGemColor());
		}
		
		return colors;
	}
	
	public void initializeGrid(){
		for (int row = 0; row < rows; ++row){
			for (int col = 0; col < cols; ++col){
				randomizeColor(gems[row][col]);
				gems[row][col].setPosition(bounds[row][col].center());
				gems[row][col].reset();
			}
		}
		
		Gem gem = null;
		
		int sameCounter = 0;
		GemColor lastColor = gems[0][0].getGemColor();
		
		// Horizontal pass
		for (int row = 0; row < rows; ++row){
			sameCounter = 0;
			lastColor = gems[row][0].getGemColor();
			
			for (int col = 0; col < cols; ++col){
				gem = gems[row][col];
				if (gem.getGemColor() == lastColor) {
					++sameCounter;
					if (sameCounter >= 2) {
						ArrayList<GemColor> subset = new ArrayList<>(Arrays.asList(GemColor.values()));
						subset.removeAll(getNeighbourColors(row, col));
						
						randomizeColor(gem, subset);
						sameCounter = 0;
						lastColor = gem.getGemColor();
					}
				}
				else
				{
					lastColor = gem.getGemColor();
					sameCounter = 0;
				}
			}
		}

		// Vertical pass
		for (int col = 0; col < cols; ++col){
			sameCounter = 0;
			lastColor = gems[0][col].getGemColor();
			
			for (int row = 0; row < rows; ++row){
				gem = gems[row][col];
				if (gem.getGemColor() == lastColor) {
					++sameCounter;
					if (sameCounter >= 2) {
						ArrayList<GemColor> subset = new ArrayList<>(Arrays.asList(GemColor.values()));
						subset.removeAll(getNeighbourColors(row, col));
						
						randomizeColor(gem, subset);
						sameCounter = 0;
						lastColor = gem.getGemColor();
					}
				}
				else
				{
					lastColor = gem.getGemColor();
					sameCounter = 0;
				}
			}
		}
	}
	
	private void destroyGems(final Match match){
		for (Gem gem : match.gems){
			gem.shrink(destroyDuration);
		}
	}
	
	private void onMatch(final Match match){
		for (MatchListener listener : matchListeners){
			listener.onMatchChain(match);
		}
	}
	
	private void onChainEnd(){
		for (MatchListener listener : matchListeners){
			listener.onMatchChainEnded();
		}
	}
	
	public void onTimerFinished(){
		state = State.IDLE;
	}
}
