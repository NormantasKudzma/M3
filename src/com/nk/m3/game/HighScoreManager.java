package com.nk.m3.game;

public class HighScoreManager {
	public interface HighScoreSaver {
		public void save(VariantScores variantScores[]);
		public void load(VariantScores variantScores[]);
		public void init();
	}
	
	public static class HighScore {
		public int score;
		public String name;
	}

	private static final int maxScores = 10;
	
	public static class VariantScores {
		public HighScore scores[];
		public int last;
		public int lastPos;
		public Variant variant;
	}
	
	private static VariantScores variantScores[];
	private static boolean isInitialized;
	public static HighScoreSaver saver;
	
	public static void submit(Variant variant, int score){
		if (!isInitialized){
			isInitialized = true;
			init();
		}

		VariantScores v = getVariantScores(variant);
		v.last = score;
		v.lastPos = -1;

		for (int i = maxScores - 1; i >= 0 && v.last > v.scores[i].score; --i){
			v.lastPos = i;
		}
		
		if (v.lastPos != -1){
			for (int i = maxScores - 1; i > v.lastPos && i > 0; --i){
				v.scores[i].score = v.scores[i - 1].score;
				v.scores[i].name = v.scores[i - 1].name;
			}
			v.scores[v.lastPos].score = v.last;
			v.scores[v.lastPos].name = "";
			save();
		}
	}
	
	public static int last(Variant variant){
		return getVariantScores(variant).last;
	}
	
	public static int lastPos(Variant variant){
		return getVariantScores(variant).lastPos + 1;
	}
	
	public static int getScore(Variant variant, int position){
		if (!isInitialized){
			isInitialized = true;
			init();
		}
		
		VariantScores variantScores = getVariantScores(variant);
		
		if (position <= 0 || position > variantScores.scores.length){
			return 0;
		}
		
		return variantScores.scores[position - 1].score;
	}
	
	private static void init(){
		variantScores = new VariantScores[Variant.values().length];
		
		for (int i = 0; i < variantScores.length; ++i) {
			variantScores[i] = new VariantScores();
			
			VariantScores v = variantScores[i];
			v.last = 0;
			v.lastPos = -1;
			v.scores = new HighScore[maxScores];
			v.variant = Variant.values()[i];
			
			for (int j = 0; j < maxScores; ++j){
				v.scores[j] = new HighScore();
				v.scores[j].name = "";
			}
		}
		
		saver.init();
		load();
	}
	
	private static void save(){
		saver.save(variantScores);
	}
	
	private static void load(){
		saver.load(variantScores);
	}
	
	private static VariantScores getVariantScores(Variant variant) {
		for (int i = 0; i < variantScores.length; ++i) {
			if (variantScores[i].variant == variant) { return variantScores[i]; }
		}
		
		return null;
	}
	
	public static void setName(Variant variant, int pos, String name){
		VariantScores variantScores = getVariantScores(variant);
		variantScores.scores[pos - 1].name = name;
		save();
	}

	public static String getName(Variant variant, int pos){
		VariantScores variantScores = getVariantScores(variant);
		return variantScores.scores[pos - 1].name;
	}
}
