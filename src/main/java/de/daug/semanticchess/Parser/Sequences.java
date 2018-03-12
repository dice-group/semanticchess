package de.daug.semanticchess.Parser;

/**
 * This class contains SPARQL templates.
 */
public class Sequences {

	// 0 classes, 1 entity, 0 unions
	static String _010 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1.  FILTER }";

	// 0 classes, 2 entities, 0 unions
	static String _020 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2.  FILTER }";

	// 0 classes, 3 entities, 0 unions
	static String _030 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3.  FILTER }";

	// 0 classes, 4 entities, 0 unions
	static String _040 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. FILTER }";
	
	// 0 classes, 4 entities, 0 unions
	static String _050 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. R_5 P_5 E_5. FILTER }";
	
	// 0 classes, 4 entities, 0 unions
	static String _060 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. R_5 P_5 E_5. R_6 P_6 E_6. FILTER }";
	
	// 0 classes, 4 entities, 0 unions
	static String _070 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. R_5 P_5 E_5. R_6 P_6 E_6. R_7 P_7 E_7. FILTER }";

	// 1 class, 0 entities, 0 unions
	static String _100 = "SELECT DISTINCT * WHERE { S_1 D_1 C_1. FILTER }";

	// 1 class, 1 entity, 0 unions
	static String _110 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. S_1 D_1 C_1. FILTER }";

	// 1 class, 2 entities, 0 unions
	static String _120 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. S_1 D_1 C_1. FILTER }";

	// 1 class, 3 entities, 0 unions
	static String _130 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. S_1 D_1 C_1. FILTER }";

	// 1 class, 4 entities, 0 unions
	static String _140 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. S_1 D_1 C_1. FILTER }";
	
	// 1 class, 5 entities, 0 unions
	static String _150 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. R_5 P_5 E_5. S_1 D_1 C_1. FILTER }";
	
	// 1 class, 6 entities, 0 unions
	static String _160 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. R_5 P_5 E_5. R_6 P_6 E_6. S_1 D_1 C_1. FILTER }";
	
	// 1 class, 7 entities, 0 unions
	static String _170 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. R_5 P_5 E_5. R_6 P_6 E_6. R_7 P_7 E_7. S_1 D_1 C_1. FILTER }";

	// 2 classes, 0 entities, 0 unions
	static String _200 = "SELECT DISTINCT * WHERE { S_1 D_1 C_1. S_2 D_2 C_2. FILTER }";

	// 2 classes, 1 entity, 0 unions
	static String _210 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. S_1 D_1 C_1. S_2 D_2 C_2. FILTER }";

	// 2 classes, 2 entities, 0 unions
	static String _220 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. S_1 D_1 C_1. S_2 D_2 C_2. FILTER }";

	// 2 classes, 3 entities, 0 unions
	static String _230 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. S_1 D_1 C_1. S_2 D_2 C_2. FILTER }";

	// 2 classes, 4 entities, 0 unions
	static String _240 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. S_1 D_1 C_1. S_2 D_2 C_2. FILTER }";
	
	// 2 classes, 5 entities, 0 unions
	static String _250 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. R_5 P_5 E_5. S_1 D_1 C_1. S_2 D_2 C_2. FILTER }";
	
	// 2 classes, 6 entities, 0 unions
	static String _260 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. R_5 P_5 E_5. R_6 P_6 E_6. S_1 D_1 C_1. S_2 D_2 C_2. FILTER }";
	
	// 2 classes, 7 entities, 0 unions
	static String _270 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. R_5 P_5 E_5. R_6 P_6 E_6. R_7 P_7 E_7. S_1 D_1 C_1. S_2 D_2 C_2. FILTER }";
	
	// 2 classes, 0 entities, 0 unions
	static String _300 = "SELECT DISTINCT * WHERE { S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3. FILTER }";

	// 3 classes, 1 entity, 0 unions
	static String _310 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3. FILTER }";

	// 3 classes, 2 entities, 0 unions
	static String _320 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3. FILTER }";

	// 3 classes, 3 entities, 0 unions
	static String _330 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3. FILTER }";

	// 3 classes, 4 entities, 0 unions
	static String _340 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3. FILTER }";
	
	// 3 classes, 5 entities, 0 unions
	static String _350 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. R_5 P_5 E_5. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3. FILTER }";
	
	// 3 classes, 6 entities, 0 unions
	static String _360 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. R_5 P_5 E_5. R_6 P_6 E_6. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3. FILTER }";
	
	// 3 classes, 7 entities, 0 unions
	static String _370 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. R_5 P_5 E_5. R_6 P_6 E_6. R_7 P_7 E_7. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3. FILTER }";

	// 4 classes, 0 entities, 0 unions
	static String _400 = "SELECT DISTINCT * WHERE { S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3. S_4 D_4 C_4. FILTER }";

	// 4 classes, 1 entity, 0 unions
	static String _410 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3. S_4 D_4 C_4. FILTER }";

	// 4 classes, 2 entities, 0 unions
	static String _420 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.  S_4 D_4 C_4. FILTER }";

	// 4 classes, 3 entities, 0 unions
	static String _430 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.  S_4 D_4 C_4. FILTER }";

	// 4 classes, 4 entities, 0 unions
	static String _440 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.  S_4 D_4 C_4. FILTER }";
	
	// 4 classes, 5 entities, 0 unions
	static String _450 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. R_5 P_5 E_5. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.  S_4 D_4 C_4. FILTER }";
	
	// 4 classes, 6 entities, 0 unions
	static String _460 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. R_5 P_5 E_5. R_6 P_6 E_6. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.  S_4 D_4 C_4. FILTER }";
	
	// 4 classes, 7 entities, 0 unions
	static String _470 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. R_5 P_5 E_5. R_6 P_6 E_6. R_7 P_7 E_7. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.  S_4 D_4 C_4. FILTER }";
	
	// 5 classes, 0 entities, 0 unions
	static String _500 = "SELECT DISTINCT * WHERE { S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3. S_4 D_4 C_4. S_5 D_5 C_5. FILTER }";

	// 5 classes, 1 entity, 0 unions
	static String _510 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3. S_4 D_4 C_4. S_5 D_5 C_5. FILTER }";

	// 5 classes, 2 entities, 0 unions
	static String _520 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.  S_4 D_4 C_4. S_5 D_5 C_5. FILTER }";

	// 5 classes, 3 entities, 0 unions
	static String _530 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.  S_4 D_4 C_4. S_5 D_5 C_5. FILTER }";

	// 5 classes, 4 entities, 0 unions
	static String _540 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.  S_4 D_4 C_4. S_5 D_5 C_5. FILTER }";
	
	// 5 classes, 5 entities, 0 unions
	static String _550 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. R_5 P_5 E_5. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.  S_4 D_4 C_4. S_5 D_5 C_5. FILTER }";
	
	// 5 classes, 6 entities, 0 unions
	static String _560 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. R_5 P_5 E_5. R_6 P_6 E_6. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.  S_4 D_4 C_4. S_5 D_5 C_5. FILTER }";
	
	// 5 classes, 7 entities, 0 unions
	static String _570 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. R_5 P_5 E_5. R_6 P_6 E_6. R_7 P_7 E_7. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.  S_4 D_4 C_4. S_5 D_5 C_5. FILTER }";
	
	// 6 classes, 0 entities, 0 unions
	static String _600 = "SELECT DISTINCT * WHERE { S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3. S_4 D_4 C_4. S_5 D_5 C_5. S_6 D_6 C_6. FILTER }";

	// 6 classes, 1 entity, 0 unions
	static String _610 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3. S_4 D_4 C_4. S_5 D_5 C_5. S_6 D_6 C_6. FILTER }";

	// 6 classes, 2 entities, 0 unions
	static String _620 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.  S_4 D_4 C_4. S_5 D_5 C_5. S_6 D_6 C_6. FILTER }";

	// 6 classes, 3 entities, 0 unions
	static String _630 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.  S_4 D_4 C_4. S_5 D_5 C_5. S_6 D_6 C_6. FILTER }";

	// 6 classes, 4 entities, 0 unions
	static String _640 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.  S_4 D_4 C_4. S_5 D_5 C_5. S_6 D_6 C_6. FILTER }";
	
	// 6 classes, 5 entities, 0 unions
	static String _650 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. R_5 P_5 E_5. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.  S_4 D_4 C_4. S_5 D_5 C_5. S_6 D_6 C_6. FILTER }";
	
	// 6 classes, 6 entities, 0 unions
	static String _660 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. R_5 P_5 E_5. R_6 P_6 E_6. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.  S_4 D_4 C_4. S_5 D_5 C_5. S_6 D_6 C_6. FILTER }";
	
	// 6 classes, 7 entities, 0 unions
	static String _670 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. R_5 P_5 E_5. R_6 P_6 E_6. R_7 P_7 E_7. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.  S_4 D_4 C_4. S_5 D_5 C_5. S_6 D_6 C_6.FILTER }";

	// 6 classes, 8 entities, 0 unions
	static String _680 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. R_5 P_5 E_5. R_6 P_6 E_6. R_7 P_7 E_7. R_8 P_8 E_8. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.  S_4 D_4 C_4. S_5 D_5 C_5. S_6 D_6 C_6.FILTER }";
	
	// 7 classes, 0 entities, 0 unions
	static String _700 = "SELECT DISTINCT * WHERE { S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3. S_4 D_4 C_4. S_5 D_5 C_5. S_6 D_6 C_6. S_7 D_7 C_7. FILTER }";

	// 7 classes, 1 entity, 0 unions
	static String _710 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3. S_4 D_4 C_4. S_5 D_5 C_5. S_6 D_6 C_6. S_7 D_7 C_7. FILTER }";

	// 7 classes, 2 entities, 0 unions
	static String _720 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.  S_4 D_4 C_4. S_5 D_5 C_5. S_6 D_6 C_6. S_7 D_7 C_7. FILTER }";

	// 7 classes, 3 entities, 0 unions
	static String _730 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.  S_4 D_4 C_4. S_5 D_5 C_5. S_6 D_6 C_6. S_7 D_7 C_7. FILTER }";

	// 7 classes, 4 entities, 0 unions
	static String _740 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.  S_4 D_4 C_4. S_5 D_5 C_5. S_6 D_6 C_6. S_7 D_7 C_7. FILTER }";
	
	// 7 classes, 5 entities, 0 unions
	static String _750 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. R_5 P_5 E_5. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.  S_4 D_4 C_4. S_5 D_5 C_5. S_6 D_6 C_6. S_7 D_7 C_7. FILTER }";
	
	// 7 classes, 6 entities, 0 unions
	static String _760 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. R_5 P_5 E_5. R_6 P_6 E_6. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.  S_4 D_4 C_4. S_5 D_5 C_5. S_6 D_6 C_6. S_7 D_7 C_7. FILTER }";
	
	// 7 classes, 7 entities, 0 unions
	static String _770 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. R_5 P_5 E_5. R_6 P_6 E_6. R_7 P_7 E_7. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.  S_4 D_4 C_4. S_5 D_5 C_5. S_6 D_6 C_6. S_7 D_7 C_7. FILTER }";

	// 7 classes, 8 entities, 0 unions
	static String _780 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. R_5 P_5 E_5. R_6 P_6 E_6. R_7 P_7 E_7. R_8 P_8 E_8. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.  S_4 D_4 C_4. S_5 D_5 C_5. S_6 D_6 C_6. S_7 D_7 C_7. FILTER }";

	// 7 classes, 9 entities, 0 unions
	static String _790 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. R_5 P_5 E_5. R_6 P_6 E_6. R_7 P_7 E_7. R_8 P_8 E_8. R_9 P_9 E_9. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.  S_4 D_4 C_4. S_5 D_5 C_5. S_6 D_6 C_6. S_7 D_7 C_7. FILTER }";

	
	// 0 classes, 2 entities, 1 union
	static String _021 = "SELECT DISTINCT * WHERE { {R_1 P_1 E_1.} UNION {R_2 P_2 E_2.} FILTER }";

	// 0 classes, 4 entities, 1 union
	static String _041 = "SELECT DISTINCT * WHERE { {R_1 P_1 E_1. R_2 P_2 E_2.} UNION {R_3 P_3 E_3. R_4 P_4 E_4.} FILTER }";

	// 0 classes, 6 entities, 1 union
	static String _061 = "SELECT DISTINCT * WHERE { {R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3.} UNION { R_4 P_4 E_4. R_5 P_5 E_5. R_6 P_6 E_6.} FILTER }";
	
	// 0 classes, 8 entities, 1 union
	static String _081 = "SELECT DISTINCT * WHERE { {R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4.} UNION { R_5 P_5 E_5. R_6 P_6 E_6. R_7 P_7 E_7. R_8 P_8 E_8.} FILTER }";
	
	// 0 classes, 10 entities, 1 union
	static String _0101 = "SELECT DISTINCT * WHERE { {R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. R_5 P_5 E_5.} UNION {R_6 P_6 E_6. R_7 P_7 E_7. R_8 P_8 E_8. R_9 P_9 E_9. R_10 P_10 E_10.} FILTER }";
	
	// 3 classes, 8 entities, 1 union
	static String _121 = "SELECT DISTINCT * WHERE { S_1 D_1 C_1. {R_1 P_1 E_1.} UNION {R_2 P_2 E_2.}  FILTER }";
	
	// 3 classes, 8 entities, 1 union
	static String _141 = "SELECT DISTINCT * WHERE { S_1 D_1 C_1. {R_1 P_1 E_1. R_2 P_2 E_2. } UNION {R_3 P_3 E_3. R_4 P_4 E_4. }  FILTER }";
	
	// 3 classes, 8 entities, 1 union
	static String _161 = "SELECT DISTINCT * WHERE { S_1 D_1 C_1. {R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3.} UNION {R_4 P_4 E_4. R_5 P_5 E_5. R_6 P_6 E_6. }  FILTER }";
	
	// 3 classes, 8 entities, 1 union
	static String _181 = "SELECT DISTINCT * WHERE { S_1 D_1 C_1. {R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. } UNION {R_5 P_5 E_5. R_6 P_6 E_6. R_7 P_7 E_7. R_8 P_8 E_8. }  FILTER }";

	// 3 classes, 10 entities, 1 union
	static String _1101 = "SELECT DISTINCT * WHERE { S_1 D_1 C_1. {R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. R_5 P_5 E_5. } UNION {R_6 P_6 E_6. R_7 P_7 E_7. R_8 P_8 E_8. R_9 P_9 E_9. R_10 P_10 E_10.}  FILTER }";
	
	// 2 classes, 0 entities, 1 union
	static String _201 = "SELECT DISTINCT * WHERE { {S_1 D_1 C_1.} UNION {S_2 D_2 C_2.} FILTER }";

	// 2 classes, 2 entities, 1 union
	static String _221 = "SELECT DISTINCT * WHERE { {R_1 P_1 E_1. S_1 D_1 C_1. } UNION {R_2 P_2 E_2. S_2 D_2 C_2. } FILTER }";

	// 2 classes, 4 entities, 1 union
	static String _241 = "SELECT DISTINCT * WHERE { {R_1 P_1 E_1. R_2 P_2 E_2. S_1 D_1 C_1. } UNION {R_3 P_3 E_3. R_4 P_4 E_4. S_2 D_2 C_2. }  FILTER }";

	// 2 classes, 6 entities, 1 union
	static String _261 = "SELECT DISTINCT * WHERE { {R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. S_1 D_1 C_1. } UNION {R_4 P_4 E_4. R_5 P_5 E_5. R_6 P_6 E_6. S_2 D_2 C_2.}  FILTER }";
	
	// 2 classes, 8 entities, 1 union
	static String _281 = "SELECT DISTINCT * WHERE { {R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. S_1 D_1 C_1. } UNION {R_5 P_5 E_5. R_6 P_6 E_6. R_7 P_7 E_7. R_8 P_8 E_8. S_2 D_2 C_2.}  FILTER }";

	// 2 classes, 10 entities, 1 union
	static String _2101 = "SELECT DISTINCT * WHERE { {R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. R_5 P_5 E_5. S_1 D_1 C_1. } UNION {R_6 P_6 E_6. R_7 P_7 E_7. R_8 P_8 E_8. R_9 P_9 E_9. R_10 P_10 E_10. S_2 D_2 C_2.}  FILTER }";
	
	// 3 classes, 8 entities, 1 union
	static String _321 = "SELECT DISTINCT * WHERE { S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3. {R_1 P_1 E_1.} UNION {R_2 P_2 E_2.}  FILTER }";
	
	// 3 classes, 8 entities, 1 union
	static String _341 = "SELECT DISTINCT * WHERE { S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3. {R_1 P_1 E_1. R_2 P_2 E_2. } UNION {R_3 P_3 E_3. R_4 P_4 E_4. }  FILTER }";
	
	// 3 classes, 8 entities, 1 union
	static String _361 = "SELECT DISTINCT * WHERE { S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3. {R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3.} UNION {R_4 P_4 E_4. R_5 P_5 E_5. R_6 P_6 E_6. }  FILTER }";
	
	// 3 classes, 8 entities, 1 union
	static String _381 = "SELECT DISTINCT * WHERE { S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3. {R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. } UNION {R_5 P_5 E_5. R_6 P_6 E_6. R_7 P_7 E_7. R_8 P_8 E_8. }  FILTER }";

	// 3 classes, 10 entities, 1 union
	static String _3101 = "SELECT DISTINCT * WHERE { S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3. {R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. R_5 P_5 E_5. } UNION {R_6 P_6 E_6. R_7 P_7 E_7. R_8 P_8 E_8. R_9 P_9 E_9. R_10 P_10 E_10.}  FILTER }";
	
	// 2 classes, 0 entities, 1 union
	static String _401 = "SELECT DISTINCT * WHERE { {S_1 D_1 C_1. S_2 D_2 C_2.} UNION {S_3 D_3 C_3. S_4 D_4 C_4.} FILTER }";

	// 4 classes, 2 entities, 1 union
	static String _421 = "SELECT DISTINCT * WHERE { {R_1 P_1 E_1. S_1 D_1 C_1. S_2 D_2 C_2. } UNION {R_2 P_2 E_2. S_3 D_3 C_3. S_4 D_4 C_4. } FILTER }";

	// 4 classes, 4 entities, 1 union
	static String _441 = "SELECT DISTINCT * WHERE { {R_1 P_1 E_1. R_2 P_2 E_2. S_1 D_1 C_1. S_2 D_2 C_2. } UNION {R_3 P_3 E_3. R_4 P_4 E_4. S_3 D_3 C_3. S_4 D_4 C_4. }  FILTER }";
	
	// 4 classes, 6 entities, 1 union
	static String _461 = "SELECT DISTINCT * WHERE { {R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. S_1 D_1 C_1. S_2 D_2 C_2. } UNION {R_4 P_4 E_4. R_5 P_5 E_5. R_6 P_6 E_6. S_3 D_3 C_3. S_4 D_4 C_4. }  FILTER }";
	
	// 4 classes, 8 entities, 1 union
	static String _481 = "SELECT DISTINCT * WHERE { {R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. S_1 D_1 C_1. S_2 D_2 C_2.} UNION {R_5 P_5 E_5. R_6 P_6 E_6. R_7 P_7 E_7. R_8 P_8 E_8. S_3 D_3 C_3. S_4 D_4 C_4.}  FILTER }";
	
	// 4 classes, 10 entities, 1 union
	static String _4101 = "SELECT DISTINCT * WHERE { {R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. R_5 P_5 E_5. S_1 D_1 C_1. S_2 D_2 C_2.} UNION {R_6 P_6 E_6. R_7 P_7 E_7. R_8 P_8 E_8. R_9 P_9 E_9. R_10 P_10 E_10. S_3 D_3 C_3. S_4 D_4 C_4.}  FILTER }";

	// 6 classes, 0 entities, 1 union
	static String _601 = "SELECT DISTINCT * WHERE { {S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.} UNION {S_4 D_4 C_4. S_5 D_5 C_5. S_6 D_6 C_6.} FILTER }";

	// 6 classes, 2 entities, 1 union
	static String _621 = "SELECT DISTINCT * WHERE { {R_1 P_1 E_1. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.} UNION {R_2 P_2 E_2. S_4 D_4 C_4. S_5 D_5 C_5. S_6 D_6 C_6.} FILTER }";

	// 6 classes, 4 entities, 1 union
	static String _641 = "SELECT DISTINCT * WHERE { {R_1 P_1 E_1. R_2 P_2 E_2. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.} UNION {R_3 P_3 E_3. R_4 P_4 E_4. S_4 D_4 C_4. S_5 D_5 C_5. S_6 D_6 C_6.}  FILTER }";
	
	// 6 classes, 6 entities, 1 union
	static String _661 = "SELECT DISTINCT * WHERE { {R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.} UNION {R_4 P_4 E_4. R_5 P_5 E_5. R_6 P_6 E_6. S_4 D_4 C_4. S_5 D_5 C_5. S_6 D_6 C_6.}  FILTER }";
	
	// 6 classes, 8 entities, 1 union
	static String _681 = "SELECT DISTINCT * WHERE { {R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.} UNION {R_5 P_5 E_5. R_6 P_6 E_6. R_7 P_7 E_7. R_8 P_8 E_8. S_4 D_4 C_4. S_5 D_5 C_5. S_6 D_6 C_6.}  FILTER }";
	
	// 6 classes, 10 entities, 1 union
	static String _6101 = "SELECT DISTINCT * WHERE { {R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. R_5 P_5 E_5. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.} UNION {R_6 P_6 E_6. R_7 P_7 E_7. R_8 P_8 E_8. R_9 P_9 E_9. R_10 P_10 E_10. S_4 D_4 C_4. S_5 D_5 C_5. S_6 D_6 C_6.}  FILTER }";

	// 7 classes, 0 entities, 1 union
	static String _701 = "SELECT DISTINCT * WHERE { {S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.} UNION {S_4 D_4 C_4. S_5 D_5 C_5. S_6 D_6 C_6. S_7 D_7 C_7.} FILTER }";

	// 7 classes, 2 entities, 1 union
	static String _721 = "SELECT DISTINCT * WHERE { {R_1 P_1 E_1. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.} UNION {R_2 P_2 E_2. S_4 D_4 C_4. S_5 D_5 C_5. S_6 D_6 C_6. S_7 D_7 C_7.} FILTER }";

	// 7 classes, 4 entities, 1 union
	static String _741 = "SELECT DISTINCT * WHERE { {R_1 P_1 E_1. R_2 P_2 E_2. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.} UNION {R_3 P_3 E_3. R_4 P_4 E_4. S_4 D_4 C_4. S_5 D_5 C_5. S_6 D_6 C_6. S_7 D_7 C_7.}  FILTER }";
	
	// 7 classes, 6 entities, 1 union
	static String _761 = "SELECT DISTINCT * WHERE { {R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.} UNION {R_4 P_4 E_4. R_5 P_5 E_5. R_6 P_6 E_6. S_4 D_4 C_4. S_5 D_5 C_5. S_6 D_6 C_6. S_7 D_7 C_7.}  FILTER }";
	
	// 7 classes, 8 entities, 1 union
	static String _781 = "SELECT DISTINCT * WHERE { {R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.} UNION {R_5 P_5 E_5. R_6 P_6 E_6. R_7 P_7 E_7. R_8 P_8 E_8. S_4 D_4 C_4. S_5 D_5 C_5. S_6 D_6 C_6. S_7 D_7 C_7.}  FILTER }";
	
	// 6 classes, 10 entities, 1 union
	static String _7101 = "SELECT DISTINCT * WHERE { {R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. R_5 P_5 E_5. S_1 D_1 C_1. S_2 D_2 C_2. S_3 D_3 C_3.} UNION {R_6 P_6 E_6. R_7 P_7 E_7. R_8 P_8 E_8. R_9 P_9 E_9. R_10 P_10 E_10. S_4 D_4 C_4. S_5 D_5 C_5. S_6 D_6 C_6. S_7 D_7 C_7.}  FILTER }";

	
}