package com.nk.m3.pc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import com.nk.m3.game.HighScoreManager.HighScore;
import com.nk.m3.game.HighScoreManager.HighScoreSaver;

public class HighScoreSaverPc implements HighScoreSaver {
	private static final String separator = ";";
	private File saveFile;

	@Override
	public void save(HighScore[] scores) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(saveFile, false));
			for (int i = 0; i < scores.length; ++i){
				writer.write(String.format("%d%s%s\n", scores[i].score, separator, scores[i].name));
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

	@Override
	public void load(HighScore[] scores) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(saveFile));
			String line;
			int lineNum = 0;
			while ((line = reader.readLine()) != null && lineNum < scores.length){
				String parts[] = line.split(separator);
				if (parts.length > 0){
					scores[lineNum].score = Integer.parseInt(parts[0]);
				}
				if (parts.length > 1){
					scores[lineNum].name = parts[1];
				}
				++lineNum;
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
