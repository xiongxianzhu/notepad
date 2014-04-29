package com.gditc.notepad.db;
/**
 * sqlite数据库信息
 * @author Cryhelyxx
 *
 */
public class MyDbInfo {
	//表名， 使用一维数组， 使用全局变量(static)
	private static String TableNames[] = {
		"tbl_type", 
		"tbl_note"
	};
	
	//字段名， 为表示每个表table的字段， 使用二维数组， 使用全局变量(static)
	private static String FieldNames[][] = {
		{"_id", "type_name", "note_num"},
		{"_id", "note_title", "note_type", "note_content"}
	};
	
	//字段类型
	private static String FieldTypes[][] = {
		{"INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL", "TEXT CONSTRAINT UC1 UNIQUE"
			, "INTEGER"},
		{"INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL", "TEXT CONSTRAINT FK1 "
				+  "REFERENCES " + TableNames[0]
				+ "(" + FieldNames[0][1]
				+ ") ON DELETE CASCADE ON UPDATE CASCADE", "TEXT", "TEXT"}
	};

	public static String[] getTableNames() {
		return TableNames;
	}

	public static String[][] getFieldNames() {
		return FieldNames;
	}

	public static String[][] getFieldTypes() {
		return FieldTypes;
	}
}
