package br.eco.wash4me.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "w4mUser.db";
    public static final String TABLE_NAME_ACCOUNT = "saved_credential";
    public static final String TABLE_NAME_LOGGED_USER = "logged_user";
    
    private static final int DATABASE_VERSION = 1;
    
    public static final String ACCOUNT_USERNAME = "username";
    public static final String ACCOUNT_PASSWORD = "password";
    
    public static final String LOGGED_USER_ID = "id";
    public static final String LOGGED_USER_EMAIL = "email";
    public static final String LOGGED_USER_TYPE = "type";
	public static final String LOGGED_USER_NAME = "name";
	public static final String LOGGED_USER_BASE_LATITUDE = "baseLatitude";
	public static final String LOGGED_USER_BASE_LONGITUDE = "baseLongitude";
    
    private static final String DATABASE_CREATE_ACCOUNT = "create table "
		+ TABLE_NAME_ACCOUNT + " (" + ACCOUNT_USERNAME + " text not null, " + ACCOUNT_PASSWORD + " text not null);";
    
    private static final String DATABASE_CREATE_LOGGED_USER = "create table "
    		+ TABLE_NAME_LOGGED_USER + " (" + LOGGED_USER_ID + " int not null, "
								    		+ LOGGED_USER_EMAIL + " text not null, "
    										+ LOGGED_USER_TYPE + " text not null, "
											+ LOGGED_USER_NAME + " text not null"
                                            //+ LOGGED_USER_BASE_LATITUDE + " text not null,"
                                            //+ LOGGED_USER_BASE_LONGITUDE + " text not null"
                                            +");";
    
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE_ACCOUNT);
		db.execSQL(DATABASE_CREATE_LOGGED_USER);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_ACCOUNT);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_LOGGED_USER);
        onCreate(db);
	}
}
