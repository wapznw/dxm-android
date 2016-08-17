package com.db;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "maoandroid.db";
	public static final String TABLE_NAME = "sms";
	private static final String SMS_ID = "_id";
	private static final String SMS_PHONE = "sms_phone";
	private static final String SMS_TEXT = "sms_text";
	private static final String SMS_STATUS = "sms_status";
	private static final String SMS_RESULT = "sms_result";
	private static final String SMS_TIME = "sms_time";

	private static final int DATABASE_VERSION = 1;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public DatabaseHelper(Activity context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + SMS_ID
				+ " integer primary key autoincrement," + SMS_PHONE
				+ " varchar(12)," + SMS_TEXT + " varchar(255),"+ SMS_RESULT
				+ " text," + SMS_STATUS 
				+ " int(2)," + SMS_TIME
				+ " timestamp default(DATETIME('now', 'localtime')));");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS notes");
		onCreate(db);
	}

	public  long insert(Context context, String s) {
		long insertId = -1;
		try {
			DatabaseHelper mOpenHelper = new DatabaseHelper(context);
			ContentValues values = new ContentValues();
			if(s.indexOf(",")!=-1){
				String[] str = s.split(",");
				for (int i = 0; i < str.length; i++) {
					String[] strs = str[i].split("=");
					values.put(strs[0], strs[1]);
				}
			}else{
				String[] strs = s.split("=");
				values.put(strs[0], strs[1]);
			}
			insertId = mOpenHelper.getReadableDatabase().insert(TABLE_NAME, SMS_ID,
					values);
			mOpenHelper.getReadableDatabase().close();
		} catch (Exception e) {
			//Log.v("插入数据失败", e.toString());
			e.printStackTrace();
		}
		return insertId;
	}

	public Cursor select(Context context) {
		try {
			DatabaseHelper mOpenHelper = new DatabaseHelper(context);
 
			String[] columns = new String[] { SMS_ID,SMS_PHONE,SMS_TEXT,SMS_STATUS,SMS_RESULT,SMS_TIME }; 
			String orderBy = SMS_ID+" desc";

			Cursor c = mOpenHelper.getReadableDatabase().query(TABLE_NAME, columns,
					null, null, null, null, orderBy, null);
			Log.v("数据条数：", c.getCount()+"");  
			mOpenHelper.getReadableDatabase().close(); 
			return c;
		} catch (Exception e) {
			Log.v("查询数据错误", e.toString()); 
			return null;
		}
	}
	
	public int delete(Context context,String id){
		try {
			DatabaseHelper mOpenHelper = new DatabaseHelper(context);
			String where = SMS_ID+"=?";
			String[] whereValue={id};
			int c = mOpenHelper.getWritableDatabase().delete(TABLE_NAME, where, whereValue); 
			mOpenHelper.getWritableDatabase().close();  
			//Log.v("delete","id="+id+" "+c);  
			return c;
		} catch (Exception e) {
			//Log.v("delete error", e.toString());  
			return 0;
		}
	}
	
	public int delete(Context context){
		try {
			DatabaseHelper mOpenHelper = new DatabaseHelper(context);
			int c = mOpenHelper.getWritableDatabase().delete(TABLE_NAME, null, null); 
			mOpenHelper.getWritableDatabase().close();  
			return c;
		} catch (Exception e) {
			//Log.v("delete error", e.toString());  
			return 0;
		}
	}
	public int update(Context context,String s,long id)
	{
		return update(context, s,Long.toString(id));
	}
	public int update(Context context,String s,String id){
		try {
			DatabaseHelper mOpenHelper = new DatabaseHelper(context);
			ContentValues values = new ContentValues();
			if(s.indexOf(",")!=-1){
				String[] str = s.split(",");
				for (int i = 0; i < str.length; i++) {
					String[] strs = str[i].split("=");
					values.put(strs[0], strs[1]);
				}
			}else{
				String[] strs = s.split("=");
				values.put(strs[0], strs[1]);
			}
			String where;
			if(id.indexOf("and")!=-1){
				where = id;
			}else{
				where = SMS_ID+"="+id;  
			}
			int c = mOpenHelper.getWritableDatabase().update(TABLE_NAME,values, where, null);  
			mOpenHelper.getWritableDatabase().close();  
			//Log.v("update", id+"updata ok!"+c+" "+values.toString());  
			return c;
		} catch (Exception e) {
			//Log.v("update error", e.toString());  
			return 0;
		}
	}

}