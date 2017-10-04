package de.daug.semanticchess.Converter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.fasterxml.jackson.databind.ObjectMapper;

//json: https://github.com/niklasf/eco/
public class EcoJsonToEcoTxt {

	private static String jsonDir = "src/main/resources/static/openings/chessopenings.json";
	private static String txtDir = "src/main/resources/static/openings/chessopenings.txt";

	public EcoJsonToEcoTxt() {

	}

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

				// System.out.println(eco + "\t" + name + "\t" +
				// moves);
			}

			bw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		EcoJsonToEcoTxt.convert();
	}

}