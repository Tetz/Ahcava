package com.ver1.avacha;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "user_db";
	private static final String CREATE_TABLE = "CREATE TABLE users (" + "key INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "my_hair TEXT," + "my_face TEXT," + "my_body TEXT" + ")";
	private static final String CREATE_TABLE_LOGIN = "CREATE TABLE login_status (" + "key INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "sex TEXT"+")";
	private static final String CREATE_TABLE_COUNT = "CREATE TABLE count (" + "key INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "good_count TEXT"+")";
	private static final String CREATE_TABLE_MESSAGES = "CREATE TABLE messages (" + "key INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "message TEXT," + "from_uuid TEXT" +")";
	private static final String CREATE_TABLE_MY_MESSAGES = "CREATE TABLE my_messages (" + "key INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "my_message TEXT" + ")";
	
	private static final String SELECT_MY_AVATAR = "SELECT my_hair,my_face,my_body FROM users ORDER BY key DESC LIMIT 1";
	private static final String SELECT_MY_SEX = "SELECT sex FROM login_status ORDER BY key DESC LIMIT 1";
	private static final String SELECT_GOOD_COUNT = "SELECT good_count FROM count ORDER BY key DESC LIMIT 1";
	

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, 1);	
	}
	
	
	public static SQLiteDatabase getDb(Context context){
		DatabaseHelper databaseHelper = new DatabaseHelper(context);
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		return db;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE);
		db.execSQL(CREATE_TABLE_LOGIN);
		db.execSQL(CREATE_TABLE_COUNT);
		db.execSQL(CREATE_TABLE_MESSAGES);
		db.execSQL(CREATE_TABLE_MY_MESSAGES);
	    insert_count(db);	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS users");
		db.execSQL("DROP TABLE IF EXISTS login_status");
		db.execSQL("DROP TABLE IF EXISTS messages");
		db.execSQL("DROP TABLE IF EXISTS my_messages");
		onCreate(db);
	}

	public static void insert(SQLiteDatabase db, String hair, String face, String body) {
		ContentValues cv = new ContentValues();
		cv.put("my_hair", hair);
		cv.put("my_face", face);
		cv.put("my_body", body);
		db.insert("users",null ,cv);
	}
   
	public static void insert_login_status(SQLiteDatabase db, String sex) {
		ContentValues cv = new ContentValues();
		cv.put("sex", sex);
		db.insert("login_status", null, cv);
	}

	public static void insert_message(SQLiteDatabase db, String to_uuid, String message){
		ContentValues cv = new ContentValues();
		cv.put("from_uuid", to_uuid);
		cv.put("message", message);
		db.insert("messages", null, cv);
	}
	
	public static void insert_my_message(SQLiteDatabase db, String my_message){
		ContentValues cv = new ContentValues();
		cv.put("my_message", my_message);
		db.insert("my_messages", null, cv);
	}
	
	public static void insert_count(SQLiteDatabase db){
		ContentValues cv = new ContentValues();
		cv.put("good_count", 0);
		db.insert("count", null, cv);
	}
	
	public static Cursor select_messages(SQLiteDatabase db){
		Cursor cursor = db.rawQuery("SELECT message FROM messages ORDER BY key DESC LIMIT 50", null);
		return cursor;
	}
	
	//Backup
	public static Cursor select_from_messages(SQLiteDatabase db, String from_uuid){
		Cursor cursor = db.rawQuery("SELECT message, from_uuid FROM messages WHERE from_uuid=" + "\"" + from_uuid + "\"" + "ORDER BY key DESC LIMIT 50", null);
		return cursor;
	}
	
	public static Cursor select_my_messages(SQLiteDatabase db){
		Cursor cursor = db.rawQuery("SELECT my_message FROM my_messages ORDER BY key DESC LIMIT 50", null);
		return cursor;
	}
	
	public static Cursor select_img(SQLiteDatabase db){	
		Cursor cursor = db.rawQuery(SELECT_MY_AVATAR, null);
		return cursor;
	}
	
	public static Cursor select_sex(SQLiteDatabase db){		
		Cursor cursor = db.rawQuery(SELECT_MY_SEX, null);
		return cursor;
	}
	
	public static Cursor select_good_count(SQLiteDatabase db){		
		Cursor cursor = db.rawQuery(SELECT_GOOD_COUNT, null);
		return cursor;
	}
	
	public static void Close(SQLiteDatabase db){
		db.close();
	}
	
	
}
