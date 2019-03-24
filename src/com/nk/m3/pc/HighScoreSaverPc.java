package com.nk.m3.pc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import com.nk.m3.game.HighScoreManager.HighScore;
import com.nk.m3.game.HighScoreManager.HighScoreSaver;
import com.nk.m3.game.HighScoreManager.VariantScores;
import com.nk.m3.game.Variant;

public class HighScoreSaverPc implements HighScoreSaver {
	private static final String separator = "=";
	private File saveFile;

	@Override
	public void save(VariantScores variantScores[]) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(saveFile, false));
			
			for (VariantScores v : variantScores) {
				for (int i = 0; i < v.scores.length; ++i) {
					String data = String.format("%s%s%d%s%s\n", v.variant.name, separator, v.scores[i].score, separator, v.scores[i].name);
					writer.write(data);
				}
			}
			
			writer.close();
		}
		catch (Exception e){
			e.printStackTrace();
			if (writer != null){
				try { writer.close(); } catch (Exception ee){}
			}
		}
	}

	private VariantScores getVariantScores(VariantScores variantScores[], String variant) {
		for (int i = 0; i < variantScores.length; ++i) {
			if (variantScores[i].variant.name.equals(variant)) { return variantScores[i]; }
		}
		
		return null;
	}
	
	@Override
	public void load(VariantScores variantScores[]) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(saveFile));
			String line;
			int lineNum[] = new int[variantScores.length];
			
			while ((line = reader.readLine()) != null){
				String parts[] = line.split(separator);
				if (parts.length < 2) {
					continue;
				}
				
				VariantScores v = getVariantScores(variantScores, parts[0]);
				if (v == null || lineNum[v.variant.ordinal()] >= v.scores.length){
					continue;
				}
				
				v.scores[lineNum[v.variant.ordinal()]].score = Integer.parseInt(parts[1]);
				if (parts.length > 2) {
					v.scores[lineNum[v.variant.ordinal()]].name = parts[2];
				}
				
				++lineNum[v.variant.ordinal()];
			}
			reader.close();
		}
		catch (Exception e){
			e.printStackTrace();
			if (reader != null){
				try { reader.close(); } catch (Exception ee){}
			}
		}
	}

	@Override
	public void init(){
		saveFile = new File(System.getProperty("user.dir") + File.separator + "scores.txt");
		if (!saveFile.exists()){
			try {
				saveFile.createNewFile();
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
	}
}
