package com.example.prm392_finalproject.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.prm392_finalproject.model.Book;
import com.example.prm392_finalproject.model.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "mylibrary.db";
    public static final int DB_VERSION = 2;

    // users
    public static final String T_USERS = "users";
    public static final String U_ID = "id";
    public static final String U_USERNAME = "username";
    public static final String U_EMAIL = "email";
    public static final String U_PASSWORD = "password";

    // books
    public static final String T_BOOKS = "books";
    public static final String B_ID = "id";
    public static final String B_TITLE = "title";
    public static final String B_AUTHOR = "author";
    public static final String B_YEAR = "year";
    public static final String B_NOTES = "notes";
    public static final String B_FAVORITE = "favorite";
    public static final String B_USER_ID = "user_id";
    public static final String B_IMAGE = "image_uri";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsers =
                "CREATE TABLE " + T_USERS + " (" +
                        U_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        U_USERNAME + " TEXT NOT NULL," +
                        U_EMAIL + " TEXT UNIQUE NOT NULL," +
                        U_PASSWORD + " TEXT NOT NULL" +
                        ")";
        String createBooks =
                "CREATE TABLE " + T_BOOKS + " (" +
                        B_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        B_TITLE + " TEXT NOT NULL," +
                        B_AUTHOR + " TEXT," +
                        B_YEAR + " INTEGER," +
                        B_NOTES + " TEXT," +
                        B_FAVORITE + " INTEGER DEFAULT 0," +
                        B_IMAGE + " TEXT," +
                        B_USER_ID + " INTEGER," +
                        "FOREIGN KEY(" + B_USER_ID + ") REFERENCES " + T_USERS + "(" + U_ID + ")" +
                        ")";
        db.execSQL(createUsers);
        db.execSQL(createBooks);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + T_BOOKS);
        db.execSQL("DROP TABLE IF EXISTS " + T_USERS);
        onCreate(db);
    }

    // ---------- User methods ----------
    public boolean registerUser(User u) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(U_USERNAME, u.getUsername());
        cv.put(U_EMAIL, u.getEmail());
        cv.put(U_PASSWORD, u.getPassword());
        long id = db.insert(T_USERS, null, cv);
        return id != -1;
    }
    public User loginUser(String email, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + U_ID + "," + U_USERNAME + "," + U_EMAIL +
                        " FROM " + T_USERS + " WHERE " + U_EMAIL + "=? AND " + U_PASSWORD + "=?",
                new String[]{email, password});
        if (c.moveToFirst()) {
            User u = new User();
            u.setId(c.getInt(0));
            u.setUsername(c.getString(1));
            u.setEmail(c.getString(2));
            c.close();
            return u;
        }
        c.close();
        return null;
    }

    // Lấy thông tin user theo ID
    public User getUserById(int userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + T_USERS + " WHERE " + U_ID + "=?",
                new String[]{String.valueOf(userId)});
        if (c.moveToFirst()) {
            User u = new User();
            u.setId(c.getInt(c.getColumnIndexOrThrow(U_ID)));
            u.setUsername(c.getString(c.getColumnIndexOrThrow(U_USERNAME)));
            u.setEmail(c.getString(c.getColumnIndexOrThrow(U_EMAIL)));
            u.setPassword(c.getString(c.getColumnIndexOrThrow(U_PASSWORD)));
            c.close();
            return u;
        }
        c.close();
        return null;
    }

    // Cập nhật thông tin user
    public boolean updateUser(User u) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(U_USERNAME, u.getUsername());
        cv.put(U_EMAIL, u.getEmail());
        cv.put(U_PASSWORD, u.getPassword());
        int rows = db.update(T_USERS, cv, U_ID + "=?", new String[]{String.valueOf(u.getId())});
        return rows > 0;
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + U_ID + " FROM " + T_USERS + " WHERE " + U_EMAIL + "=?", new String[]{email});
        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }

    // ---------- Book methods ----------
    public long insertBook(Book b) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(B_TITLE, b.getTitle());
        cv.put(B_AUTHOR, b.getAuthor());
        cv.put(B_YEAR, b.getYear());
        cv.put(B_NOTES, b.getNotes());
        cv.put(B_FAVORITE, b.isFavorite() ? 1 : 0);
        cv.put(B_IMAGE, b.getImageUri());
        cv.put(B_USER_ID, b.getUserId());
        return db.insert(T_BOOKS, null, cv);
    }

    public boolean updateBook(Book b) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(B_TITLE, b.getTitle());
        cv.put(B_AUTHOR, b.getAuthor());
        cv.put(B_YEAR, b.getYear());
        cv.put(B_NOTES, b.getNotes());
        cv.put(B_FAVORITE, b.isFavorite() ? 1 : 0);
        cv.put(B_IMAGE, b.getImageUri());
        int rows = db.update(T_BOOKS, cv, B_ID + "=?", new String[]{String.valueOf(b.getId())});
        return rows > 0;
    }

    public boolean deleteBook(int id) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(T_BOOKS, B_ID + "=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    public Book getBookById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + T_BOOKS + " WHERE " + B_ID + "=?", new String[]{String.valueOf(id)});
        if (c.moveToFirst()) {
            Book b = new Book();
            b.setId(c.getInt(c.getColumnIndexOrThrow(B_ID)));
            b.setTitle(c.getString(c.getColumnIndexOrThrow(B_TITLE)));
            b.setAuthor(c.getString(c.getColumnIndexOrThrow(B_AUTHOR)));
            b.setYear(c.getInt(c.getColumnIndexOrThrow(B_YEAR)));
            b.setNotes(c.getString(c.getColumnIndexOrThrow(B_NOTES)));
            b.setFavorite(c.getInt(c.getColumnIndexOrThrow(B_FAVORITE)) == 1);
            b.setUserId(c.getInt(c.getColumnIndexOrThrow(B_USER_ID)));
            c.close();
            return b;
        }
        c.close();
        return null;
    }

    public List<Book> getBooksByUser(int userId) {
        List<Book> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + T_BOOKS + " WHERE " + B_USER_ID + "=?", new String[]{String.valueOf(userId)});
        while (c.moveToNext()) {
            Book b = new Book();
            b.setId(c.getInt(c.getColumnIndexOrThrow(B_ID)));
            b.setTitle(c.getString(c.getColumnIndexOrThrow(B_TITLE)));
            b.setAuthor(c.getString(c.getColumnIndexOrThrow(B_AUTHOR)));
            b.setYear(c.getInt(c.getColumnIndexOrThrow(B_YEAR)));
            b.setNotes(c.getString(c.getColumnIndexOrThrow(B_NOTES)));
            b.setFavorite(c.getInt(c.getColumnIndexOrThrow(B_FAVORITE)) == 1);
            b.setImageUri(c.getString(c.getColumnIndexOrThrow(B_IMAGE)));
            b.setUserId(c.getInt(c.getColumnIndexOrThrow(B_USER_ID)));
            list.add(b);
        }
        c.close();
        return list;
    }
}
