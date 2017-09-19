package de.daug.semanticchess.Parser;

/**
 * sparql templates
 */
public class Sequences {

	// 0 classes, 1 entity, 0 unions
	static String _010 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1.  FILTER }";

	// 0 classes, 2 entities, 0 unions
	static String _020 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2.  FILTER }";

	// 0 classes, 3 entities, 0 unions
	static String _030 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3.  FILTER }";

	// 0 classes, 4 entities, 0 unions
	static String _040 = "SELECT DISTINCT * WHERE { R_1 P_1 E_1. R_1 P_2 E_2. R_1 P_3 E_3. R_1 P_4 E_4. FILTER }";

	// 1 class, 0 entities, 0 unions
	static String _100 = "SELECT DISTINCT * WHERE { S_1 D_1 C_1. FILTER }";

	// 1 class, 1 entity, 0 unions
	static String _110 = "SELECT DISTINCT * WHERE { S_1 D_1 C_1. R_1 P_1 E_1. FILTER }";

	// 1 class, 2 entities, 0 unions
	static String _120 = "SELECT DISTINCT * WHERE { S_1 D_1 C_1. R_1 P_1 E_1. R_2 P_2 E_2. FILTER }";

	// 1 class, 3 entities, 0 unions
	static String _130 = "SELECT DISTINCT * WHERE { S_1 D_1 C_1. R_1 P_1 E_1. R_3 P_2 E_2. R_3 P_3 E_3. FILTER }";

	// 1 class, 4 entities, 0 unions
	static String _140 = "SELECT DISTINCT * WHERE { S_1 D_1 C_1. R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. FILTER }";

	// 2 classes, 0 entities, 0 unions
	static String _200 = "SELECT DISTINCT * WHERE { S_1 D_1 C_1. S_2 D_2 C_2. FILTER }";

	// 2 classes, 1 entity, 0 unions
	static String _210 = "SELECT DISTINCT * WHERE { S_1 D_1 C_1. S_2 D_2 C_2. R_1 P_1 E_1. FILTER }";

	// 2 classes, 2 entities, 0 unions
	static String _220 = "SELECT DISTINCT * WHERE { S_1 D_1 C_1. S_2 D_2 C_2. R_1 P_1 E_1. R_2 P_2 E_2. FILTER }";

	// 2 classes, 3 entities, 0 unions
	static String _230 = "SELECT DISTINCT * WHERE { S_1 D_1 C_1. S_2 D_2 C_2. R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. FILTER }";

	// 2 classes, 4 entities, 0 unions
	static String _240 = "SELECT DISTINCT * WHERE { S_1 D_1 C_1. S_2 D_2 C_2. R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3. R_4 P_4 E_4. FILTER }";

	// 0 classes, 2 entities, 1 union
	static String _021 = "SELECT DISTINCT * WHERE { {R_1 P_1 E_1.} UNION {R_2 P_2 E_2.}  FILTER }";

	// 0 classes, 4 entities, 1 union
	static String _041 = "SELECT DISTINCT * WHERE { {R_1 P_1 E_1. R_2 P_2 E_2.} UNION {R_3 P_3 E_3. R_4 P_4 E_4.} FILTER }";

	// 0 classes, 6 entities, 1 union
	static String _061 = "SELECT DISTINCT * WHERE { {R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3.} UNION { R_4 P_4 E_4. R_5 P_5 E_5. R_6 P_6 E_6.} FILTER }";

	// 2 classes, 0 entities, 1 union
	static String _201 = "SELECT DISTINCT * WHERE { {S_1 D_1 C_1.} UNION {S_2 D_2 C_2.} FILTER }";

	// 2 classes, 2 entities, 1 union
	static String _221 = "SELECT DISTINCT * WHERE { {S_1 D_1 C_1. R_1 P_1 E_1.} UNION {S_2 D_2 C_2. R_2 P_2 E_2. } FILTER }";

	// 2 classes, 4 entities, 1 union
	static String _241 = "SELECT DISTINCT * WHERE { {S_1 D_1 C_1. R_1 P_1 E_1. R_2 P_2 E_2. } UNION {S_2 D_2 C_2. R_3 P_3 E_3. R_4 P_4 E_4.}  FILTER }";

	// 2 classes, 6 entities, 1 union
	static String _261 = "SELECT DISTINCT * WHERE { {S_1 D_1 C_1. R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3.} UNION {R_2 D_2 C_2. R_4 P_4 E_4. R_5 P_5 E_5. R_6 P_6 E_6.}  FILTER }";

	// 2 classes, 0 entities, 1 union
	static String _401 = "SELECT DISTINCT * WHERE { {S_1 D_1 C_1. S_2 D_2 C_2.} UNION {S_3 D_3 C_3. S_4 D_4 C_4.} FILTER }";

	// 2 classes, 2 entities, 1 union
	static String _421 = "SELECT DISTINCT * WHERE { {S_1 D_1 C_1. S_2 D_2 C_2. R_1 P_1 E_1.} UNION {S_3 D_3 C_3. S_4 D_4 C_4. R_2 P_2 E_2. } FILTER }";

	// 2 classes, 4 entities, 1 union
	static String _441 = "SELECT DISTINCT * WHERE { {S_1 D_1 C_1. S_2 D_2 C_2. R_1 P_1 E_1. R_2 P_2 E_2. } UNION {S_3 D_3 C_3. S_4 D_4 C_4. R_3 P_3 E_3. R_4 P_4 E_4.}  FILTER }";
	
	// 2 classes, 6 entities, 1 union
	static String _461 = "SELECT DISTINCT * WHERE { {S_1 D_1 C_1. S_2 D_2 C_2. R_1 P_1 E_1. R_2 P_2 E_2. R_3 P_3 E_3.} UNION {S_3 D_3 C_3. S_4 D_4 C_4. R_4 P_4 E_4. R_5 P_5 E_5. R_6 P_6 E_6.}  FILTER }";

}