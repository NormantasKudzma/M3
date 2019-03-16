package com.nk.m3.game;

public class HighScoreManager {
	public interface HighScoreSaver {
		public void save(HighScore scores[]);
		public void load(HighScore scores[]);
		public void init();
	}
	
	public static class HighScore {
		public int score;
		public String name;
	}
	
	private static final int maxScores = 10;
	private static int last;
	private static int lastPos;
	private static HighScore scores[];
	private static boolean isInitialized;
	public static HighScoreSaver saver;
	
	public static void submit(int score){
		if (!isInitialized){
			isInitialized = true;
			init();
		}
		
		last = score;
		lastPos = -1;

		for (int i = maxScores - 1; i >= 0 && last > scores[i].score; --i){
			lastPos = i;
		}
		
		if (lastPos != -1){
			for (int i = maxScores - 1; i > lastPos && i > 0; --i){
				scores[i].score = scores[i - 1].score;
				scores[i].name = scores[i - 1].name;
			}
			scores[lastPos].score = last;
			scores[lastPos].name = "";
			save();
		}
	}
	
	public static int last(){
		return last;
	}
	
	public static int lastPos(){
		return lastPos + 1;
	}
	
	public static int get(int position){
		if (!isInitialized){
			isInitialized = true;
			init();
		}
		
		if (position <= 0 || position > scores.length){
			return 0;
		}
		
		return scores[position - 1].score;
	}
	
	private static void init(){
		lastPos = -1;
		scores = new HighScore[maxScores];
		for (int i = 0; i < maxScores; ++i){
			scores[i] = new HighScore();
			scores[i].name = "";
		}
		
		saver.init();
		load();
	}
	
	private static void save(){
		saver.save(scores);
	}
	
	private static void load(){
		saver.load(scores);
	}
	
	public static void setName(int pos, String name){
		scores[pos - 1].name = name;
		save();
	}

	public static String getName(int pos){
		return scores[pos - 1].name;
	}
}
