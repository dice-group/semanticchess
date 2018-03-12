package de.daug.semanticchess.Converter;

import java.io.File;

import de.daug.semanticchess.Configurations;
import de.daug.semanticchess.Converter.Utils.PGNToRDFConverterRanged;
import de.uni_leipzig.informatik.swp13_sc.datamodel.rdf.ChessRDFVocabulary;



/**
 * This class converts PGN files to RDF data.
 * This code is provided by https://github.com/TortugaAttack/CACADUS
 */
public class PgnToRdf {

	/**
	 * converts PGN to RDF
	 */
	public static void main(String[] args) {
		ChessRDFVocabulary rdfv = new ChessRDFVocabulary();
		rdfv.init("http://example.com", "#", "http://example.com/prop/", "http://example.com/res/", "prop", "res",
				true);
		
		PGNToRDFConverterRanged pg = new PGNToRDFConverterRanged();
		
		String fileName = Configurations.GAMES_TEST_FILE;
		pg.setSplitRate(150);
		File f = new File(Configurations.PGN+fileName+".pgn");

		pg.setOutputFormat("TURTLE");
		pg.processToStream(f.getAbsolutePath(), new File(Configurations.RDF+fileName+".ttl").getAbsolutePath());
//		pg.setOutputFormat("RDF/XML");
//		pg.processToStream(f.getAbsolutePath(), new File("src/main/resources/static/games/rdf/"+fileName+".rdf").getAbsolutePath(), true);
//
//		rdfv.init("http://example.com", "#", "http://example.com/prop/", "http://example.com/res/", "prop", "res",
//				false);
//
//		pg.setOutputFormat("TURTLE");
//		pg.processToStream(f.getAbsolutePath(), new File(Configurations.RDF+fileName+"_meta.ttl").getAbsolutePath());
//		pg.setOutputFormat("RDF/XML");
//		pg.processToStream(f.getAbsolutePath(), new File("src/main/resources/static/games/rdf/"+fileName+"_meta.ttl").getAbsolutePath(), true);
		
		
	}
}