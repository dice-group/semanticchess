package de.daug.semanticchess.Converter.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import de.daug.semanticchess.Configurations;

/**
 * This class transfers the chessopening.json to a .txt file.
 */
public class EcoJsonToEcoTxt {

	private static String jsonDir = Configurations.JSON_CHESSOPENINGS;
	private static String txtDir = Configurations.TXT_CHESSOPENINGS;

	public EcoJsonToEcoTxt() {
	}
	
	/**
	 * does the converting process
	 */
	public static void convert() {
		JSONParser parser = new JSONParser();

		try {
			Object obj = parser.parse(new FileReader(jsonDir));
			JSONArray jsonArray = (JSONArray) obj;
			File fout = new File(txtDir);
			FileOutputStream fos = new FileOutputStream(fout);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject opening = (JSONObject) jsonArray.get(i);
				String eco = opening.get("eco").toString();
				String name = opening.get("name").toString();
				String moves = opening.get("moves").toString().replaceAll("\\. ", ".");

				bw.write(eco + " 	" + name + " 	" + moves);
				bw.newLine();
			}

			bw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * main method for testing
	 * @param args
	 */
	public static void main(String[] args) {
		EcoJsonToEcoTxt.convert();
	}

}