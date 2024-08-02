package com.example.agricarea.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import java.util.ArrayList;

import com.example.agricarea.User;


public class DatabaseHelper extends SQLiteOpenHelper {

    private	static final String	DATABASE_NAME = "userDB";
    private	static final String TABLE_CONTACTS = "users";
    private static final String id="id";
    private static final String email = "email";
    private static final String pwd = "pwd";

    private static final String city="city";
    private static final String birth="date";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String	CREATE_CONTACTS_TABLE = "CREATE	TABLE " + TABLE_CONTACTS + "(" +id+ " INTEGER PRIMARY KEY,"+ email + " TEXT ," + pwd + " TEXT," + city + " TEXT," + birth + " TEXT"+")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    public ArrayList<User> listUsers(){
        String sql = "select * from " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<User> storeContacts = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            do{
                Integer id=Integer.parseInt(cursor.getString(0));
                String email = cursor.getString(1);
                String pwd = cursor.getString(2);
                String city = cursor.getString(3);
                String birthd= cursor.getString(4);
                storeContacts.add(new User(id,email, pwd, city,birthd));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return storeContacts;
    }

    public void addUsers(User user){
        ContentValues values = new ContentValues();
        values.put(email, user.getEmail());
        values.put(pwd, user.getPwd());
        values.put(city, user.getCity());
        values.put(birth, user.getBirth());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_CONTACTS, null, values);
    }

    public void updateContacts(User user){
        ContentValues values = new ContentValues();
        values.put(email, user.getEmail());
        values.put(pwd, user.getPwd());
        values.put(city, user.getCity());
        values.put(birth, user.getBirth());
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_CONTACTS, values, id+ "	= ?", new String[] { String.valueOf(user.getId())});
    }

    public User findUsers(String email){
        String query = "SELECT * FROM " + TABLE_CONTACTS + " WHERE " + this.email + " = ?";
        SQLiteDatabase db = this.getWritableDatabase();
        User user = null;
        Cursor cursor = db.rawQuery(query, new String[]{email});
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            String em = cursor.getString(1);
            String pwd = cursor.getString(2);
            String city = cursor.getString(3);
            String birthd = cursor.getString(4);
            user = new User(id, em, pwd, city, birthd);
        }
        cursor.close();
        return user;
    }



    public void delteUser(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, id	+ "	= ?", new String[] { String.valueOf(id)});
    }
    public Boolean checkEmail(String email){
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor cursor = MyDatabase.rawQuery("Select * from users where email = ?", new String[]{email});
        if(cursor.getCount() > 0) {
            return true;
        }else {
            return false;
        }
    }
    public Boolean checkEmailPassword(String email, String password){
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor cursor = MyDatabase.rawQuery("Select * from users where email = ? and pwd = ?", new String[]{email, password});
        if (cursor.getCount() > 0) {
            return true;
        }else {
            return false;
        }
    }
}

