package br.eco.wash4me.data;

import br.eco.wash4me.entity.Account;
import br.eco.wash4me.entity.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBAdapter {
	private SQLiteDatabase database;
	private DBHelper dbHelper;
	private Context context;
	
	public DBAdapter(Context context) {
		this.context = context;
	}
	
	public DBAdapter open() {
		dbHelper = new DBHelper(context);
		database = dbHelper.getWritableDatabase();
		return this;
	}
 
	public void close() {
		dbHelper.close();
        database.close();
	}
	
	public Account getSavedAccount() {
		Account credentials = null;
		
		Cursor cursor = database.query(DBHelper.TABLE_NAME_ACCOUNT, new String[] { DBHelper.ACCOUNT_USERNAME,
				DBHelper.ACCOUNT_PASSWORD }, null, null, null, null, null);
		if(cursor != null && cursor.moveToFirst()) {
			credentials = new Account();
			credentials.setUsername(cursor.getString(cursor.getColumnIndex(DBHelper.ACCOUNT_USERNAME)));
			credentials.setPassword(cursor.getString(cursor.getColumnIndex(DBHelper.ACCOUNT_PASSWORD)));
			cursor.close();
		}
		
		return credentials;
	}
	
	public void saveAccount(Account credentials) {
		forgetAccount();
		
		ContentValues values = new ContentValues();
		values.put(DBHelper.ACCOUNT_USERNAME, credentials.getUsername());
		values.put(DBHelper.ACCOUNT_PASSWORD, credentials.getPassword());
		database.insert(DBHelper.TABLE_NAME_ACCOUNT, null, values);
	}
	
	public void forgetAccount() {
		database.delete(DBHelper.TABLE_NAME_ACCOUNT, null, null);
	}
	
	public void saveLoggedUser(User user) {
		forgetLoggedUser();
		
		ContentValues values = new ContentValues();
		values.put(DBHelper.LOGGED_USER_ID, user.getId());
		values.put(DBHelper.LOGGED_USER_EMAIL, user.getUsername());
		values.put(DBHelper.LOGGED_USER_TYPE, user.getType().toString());
		values.put(DBHelper.LOGGED_USER_NAME, user.getName());
		//values.put(DBHelper.LOGGED_USER_BASE_LATITUDE, Double.valueOf(user.getBaseLocation().getLatitude()).toString());
		//values.put(DBHelper.LOGGED_USER_BASE_LONGITUDE, Double.valueOf(user.getBaseLocation().getLongitude()).toString());
		
		database.insert(DBHelper.TABLE_NAME_LOGGED_USER, null, values);
	}
	
	public User getLoggedUser() {
		User user = null;
		
		Cursor cursor = database.query(DBHelper.TABLE_NAME_LOGGED_USER, new String[] { 
				DBHelper.LOGGED_USER_ID,
				DBHelper.LOGGED_USER_EMAIL,
				DBHelper.LOGGED_USER_TYPE,
				DBHelper.LOGGED_USER_NAME,
                DBHelper.LOGGED_USER_BASE_LATITUDE,
                DBHelper.LOGGED_USER_BASE_LONGITUDE
			}, null, null, null, null, null);
		if(cursor != null && cursor.moveToFirst()) {
			user = new User();
			user.setId(cursor.getInt(cursor.getColumnIndex(DBHelper.LOGGED_USER_ID)));
			user.setUsername(cursor.getString(cursor.getColumnIndex(DBHelper.LOGGED_USER_EMAIL)));
			user.setName(cursor.getString(cursor.getColumnIndex(DBHelper.LOGGED_USER_NAME)));
			user.setType("DRIVER".equals(cursor.getString(cursor.getColumnIndex(DBHelper.LOGGED_USER_TYPE))) ? User.Type.VISITOR : User.Type.MEMBER);
            //Location baseLocation = new Location("");
            //baseLocation.setLatitude(cursor.getDouble(cursor.getColumnIndex(DBHelper.LOGGED_USER_BASE_LATITUDE)));
            //baseLocation.setLongitude(cursor.getDouble(cursor.getColumnIndex(DBHelper.LOGGED_USER_BASE_LONGITUDE)));
            //user.setBaseLocation(baseLocation);
			cursor.close();
		}
		
		return user;
	}
	
	public void forgetLoggedUser() {
		database.delete(DBHelper.TABLE_NAME_LOGGED_USER, null, null);
	}
}
