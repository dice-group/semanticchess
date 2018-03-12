package de.daug.semanticchess.Database;

import java.io.*;
import java.sql.*;

import org.apache.commons.io.FilenameUtils;

import de.daug.semanticchess.Configurations;

//
/**
 * This class is based on https://www.openlinksw.com/vos/main/Main/VirtTipsAndTricksLoadDataInTransactionMode
 * It manages the upload to Virtuoso.
 */
public class LoadData {

	public static void main(String[] args) {
		System.out.println("UPLOAD STARTED");
		System.out.println("==================================");
		try {
			Class.forName("virtuoso.jdbc4.Driver");
			
			Configurations config = new Configurations();
			String urlDB = config.getDB();
			String userDB = config.getDBUser();
			String userPW = config.getDBPassword();

			Connection conn = DriverManager.getConnection(urlDB, userDB, userPW);
			Statement st;

			st = conn.createStatement();
//			st.execute("sparql clear graph <http://www.example.com/>");
//			st.execute("sparql clear graph <http://pcai042.informatik.uni-leipzig.de/~swp13-sc/ChessOntology#ChessOpening");
//			st.execute("http://pcai042.informatik.uni-leipzig.de/~swp13-sc/ChessOntology#ChessMove");
			
			conn.setAutoCommit(false);
			

			File folder = new File(Configurations.RDF);
			File[] listOfFiles = folder.listFiles();
			
//			String fileName = "chessopenings.txt.ttl";
//			String dataOpening = load_ttl("src/main/resources/static/openings/"+fileName+".ttl");
//			if (dataOpening != null) {
//				insert_data(dataOpening, "http://www.example.com/", conn);
//				conn.rollback();
//				System.out.println("Rollback insert");
//			}
			
			for (File f : listOfFiles) {

				
				if (FilenameUtils.getExtension(f.getAbsolutePath()).equals("ttl")) {

					System.out.println("Upload: " + f.getAbsolutePath());
					String data = load_ttl(f.getAbsolutePath());
					// String data =
					// load_ttl("src/test/resources/"+fileName+".ttl");
					// String meta_data =
					// load_ttl("src/test/resources/"+fileName+"_meta.ttl");
					if (data != null) {
						insert_data(data, "http://www.example.com/", conn);
						// insert_data(meta_data, "http://www.example.com/",
						// conn);
						// Commit or Rollback the transaction

						// conn.commit();
						// System.out.println("Commit insert");
						conn.rollback();
						System.out.println("Rollback insert");

						// String query = "sparql SELECT * from
						// <http://www.example.com/> WHERE {?s ?p ?o}";
						// ResultSet rs = st.executeQuery(query);
						// prnRs(rs);
						// rs.close();
					}
				}
			}

			st.close();
			conn.close();

		} catch (Exception e) {
			System.out.println("Ex=" + e);
		}
	}

	public static String load_ttl(String fname) {
		try {
			BufferedReader r = new BufferedReader(new FileReader(fname));
			StringBuilder sb = new StringBuilder();

			String s;
			while ((s = r.readLine()) != null) {
				sb.append(s);
				sb.append(" ");
			}
			r.close();
			return sb.toString();
		} catch (Exception e) {
		}
		return null;
	}

	public static void insert_data(String data, String gr_name, Connection conn) {
		try {
			PreparedStatement ps = conn.prepareStatement("DB.DBA.TTLP_MT (?, ?, ?)");

			ps.setString(1, data);
			ps.setString(2, "");
			ps.setString(3, gr_name);

			System.out.println("Executing insert..");
			ps.execute();
			ps.close();
		} catch (Exception e) {
			System.out.println("Ex:" + e);
		}
	}

	public static void prnRs(ResultSet rs) {
		try {
			ResultSetMetaData rsmd;

			System.out.println(">>>>>>>>");
			rsmd = rs.getMetaData();
			int cnt = rsmd.getColumnCount();

			while (rs.next()) {
				Object o;

				System.out.print("Thread:" + Thread.currentThread().getId() + "  ");
				for (int i = 1; i <= cnt; i++) {
					o = rs.getObject(i);
					if (rs.wasNull())
						System.out.print("<NULL> ");
					else
						System.out.print("[" + o + "] ");
				}
				System.out.println();
			}

		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
		System.out.println(">>>>>>>>");
	}

	public static String create_query(String fname) {
		try {
			BufferedReader r = new BufferedReader(new FileReader(fname));
			StringBuilder sb = new StringBuilder();

			String s;
			while ((s = r.readLine()) != null)
				sb.append(s);

			if (sb.length() > 0){
				r.close();
				return "sparql insert into graph <test> { " + sb.toString() + " } ";
			}
				
		} catch (Exception e) {
		}
		return null;
	}

}