package com.gditc.notepad.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * sqlite数据库的创建/打开/关闭， 及对数据的增、 删、 改、 查
 * @author Cryhelyxx
 *
 */
public class MyDbHelper {

	//SQLiteOpenHelper实例对象
	private DatabaseHelper myDbHelper;
	//数据库实例对象
	private SQLiteDatabase myDb;
	//数据库调用实例
	private static MyDbHelper openHelper = null;
	//定义数据库名称
	private static final String DATABASE_NAME = "notepad.db3";
	//定义数据库版本
	private static int DATABASE_VERSION = 1;
	//表名
	private static String TableNames[];
	//字段名
	private static String FieldNames[][];
	//字段类型
	private static String FieldTypes[][];
	//上下文实例
	private final Context mCtx;
	
	public MyDbHelper(Context ctx) {
		this.mCtx = ctx;
	}
	
	public static MyDbHelper getInstance(Context context){
		if(openHelper == null){
			openHelper = new MyDbHelper(context);
			TableNames = MyDbInfo.getTableNames();
			FieldNames = MyDbInfo.getFieldNames();
			FieldTypes = MyDbInfo.getFieldTypes();
		}
		return openHelper;
	}

	//数据库辅助类
	private static class DatabaseHelper extends SQLiteOpenHelper{
		
		//构造方法
		public DatabaseHelper(Context context) {
			
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		/**
		 * 创建数据库后，对数据库的操作
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			if(TableNames == null){
				return;
			}
			for(int i = 0; i < TableNames.length; i++){
				String sql = "CREATE TABLE " + TableNames[i] + " (";
				for(int j = 0; j < FieldNames[i].length; j++){
					sql += FieldNames[i][j] + " " + FieldTypes[i][j] + ",";
				}
				sql = sql.substring(0, sql.length() - 1);		//去年最后的","
				sql += ")";
				db.execSQL(sql);
			}
		}

		/**
		 * 更改数据库版本的操作
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			for(int i = 0; i < TableNames[i].length(); i++){
				String sql = "DROP TABLE IF EXISTS " + TableNames[i];
				db.execSQL(sql);
			}
			onCreate(db);
		}

		/**
		 * 打开数据库后首先被执行
		 */
		@Override
		public void onOpen(SQLiteDatabase db) {
			
			super.onOpen(db);
		}
	}

	/**
	 * 打开数据库
	 * @return
	 * @throws SQLException
	 */
	public MyDbHelper open() throws SQLException {
		myDbHelper = new DatabaseHelper(mCtx);
		myDb = myDbHelper.getWritableDatabase();
		return this;
	}
	
	/**
	 * 关闭数据库
	 */
	public void close() {
		myDbHelper.close();
	}
	
	public void execSQL(String sql) throws java.sql.SQLException {
		myDb.execSQL(sql);
	}
	
	/**
	 * sql查询语句
	 * @param sql
	 * @param selectionArgs
	 * @return
	 */
	public Cursor rawQuery(String sql, String[] selectionArgs) {
		Cursor cursor = myDb.rawQuery(sql, selectionArgs);
		return cursor;
	}
	
	/**
	 * 查询数据
	 * @param table
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return
	 */
	public Cursor select(String table, String[] columns,
			String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy) {
		Cursor cursor = myDb.query(
				table, columns, selection, selectionArgs,
				groupBy, having, orderBy);
		return cursor;
	}
	
	/**
	 * 添加数据
	 * @param table
	 * @param fields
	 * @param values
	 * @return
	 */
	public long insert(String table, String fields[], String values[]) {
		ContentValues cv = new ContentValues();
		for (int i = 0; i < fields.length; i++) {
			cv.put(fields[i], values[i]);
		}
		return myDb.insert(table, null, cv);
	}
	
	/**
	 * 删除数据
	 * @param table
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 */
	public int delete(String table, String whereClause, String[] whereArgs) {
		return myDb.delete(table, whereClause, whereArgs);
	}
	
	/**
	 * 更新数据
	 * @param table
	 * @param updateFields
	 * @param updateValues
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 */
	public int update(String table, String updateFields[], String updateValues[],
			String whereClause, String[] whereArgs) {
		ContentValues cv = new ContentValues();
		for (int i = 0; i < updateFields.length; i++) {
			cv.put(updateFields[i], updateValues[i]);
		}
		return myDb.update(table, cv, whereClause, whereArgs);
	}
}
