package com.example.prm392_finalproject.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.prm392_finalproject.model.Restaurant;
import com.example.prm392_finalproject.model.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "mylibrary.db";
    public static final int DB_VERSION = 3;

    // users
    public static final String T_USERS = "users";
    public static final String U_ID = "id";
    public static final String U_USERNAME = "username";
    public static final String U_EMAIL = "email";
    public static final String U_PASSWORD = "password";

    // restaurants
    public static final String T_RESTAURANTS = "restaurants";
    public static final String R_ID = "id";
    public static final String R_NAME = "name";
    public static final String R_ADDRESS = "address";
    public static final String R_PHONE = "phone";
    public static final String R_NOTES = "notes";
    public static final String R_FAVORITE = "favorite";
    public static final String R_USER_ID = "user_id";
    public static final String R_IMAGE = "image_uri";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsers = "CREATE TABLE " + T_USERS + " (" +
                U_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                U_USERNAME + " TEXT NOT NULL," +
                U_EMAIL + " TEXT UNIQUE NOT NULL," +
                U_PASSWORD + " TEXT NOT NULL" +
                ")";
        String createRestaurants = "CREATE TABLE " + T_RESTAURANTS + " (" +
                R_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                R_NAME + " TEXT NOT NULL," +
                R_ADDRESS + " TEXT," +
                R_PHONE + " TEXT," +
                R_NOTES + " TEXT," +
                R_FAVORITE + " INTEGER DEFAULT 0," +
                R_IMAGE + " TEXT," +
                R_USER_ID + " INTEGER," +
                "FOREIGN KEY(" + R_USER_ID + ") REFERENCES " + T_USERS + "(" + U_ID + ")" +
                ")";
        db.execSQL(createUsers);
        db.execSQL(createRestaurants);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + T_RESTAURANTS);
        db.execSQL("DROP TABLE IF EXISTS books");
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
                new String[] { email, password });
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
                new String[] { String.valueOf(userId) });
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
        int rows = db.update(T_USERS, cv, U_ID + "=?", new String[] { String.valueOf(u.getId()) });
        return rows > 0;
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + U_ID + " FROM " + T_USERS + " WHERE " + U_EMAIL + "=?",
                new String[] { email });
        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }

    // ---------- Restaurant methods ----------
    public long insertRestaurant(Restaurant restaurant) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(R_NAME, restaurant.getName());
        cv.put(R_ADDRESS, restaurant.getAddress());
        cv.put(R_PHONE, restaurant.getPhone());
        cv.put(R_NOTES, restaurant.getNotes());
        cv.put(R_FAVORITE, restaurant.isFavorite() ? 1 : 0);
        cv.put(R_IMAGE, restaurant.getImageUri());
        cv.put(R_USER_ID, restaurant.getUserId());
        return db.insert(T_RESTAURANTS, null, cv);
    }

    public boolean updateRestaurant(Restaurant restaurant) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(R_NAME, restaurant.getName());
        cv.put(R_ADDRESS, restaurant.getAddress());
        cv.put(R_PHONE, restaurant.getPhone());
        cv.put(R_NOTES, restaurant.getNotes());
        cv.put(R_FAVORITE, restaurant.isFavorite() ? 1 : 0);
        cv.put(R_IMAGE, restaurant.getImageUri());
        int rows = db.update(T_RESTAURANTS, cv, R_ID + "=?", new String[] { String.valueOf(restaurant.getId()) });
        return rows > 0;
    }

    public boolean deleteRestaurant(int id) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(T_RESTAURANTS, R_ID + "=?", new String[] { String.valueOf(id) });
        return rows > 0;
    }

    public Restaurant getRestaurantById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + T_RESTAURANTS + " WHERE " + R_ID + "=?",
                new String[] { String.valueOf(id) });
        if (c.moveToFirst()) {
            Restaurant restaurant = new Restaurant();
            restaurant.setId(c.getInt(c.getColumnIndexOrThrow(R_ID)));
            restaurant.setName(c.getString(c.getColumnIndexOrThrow(R_NAME)));
            restaurant.setAddress(c.getString(c.getColumnIndexOrThrow(R_ADDRESS)));
            restaurant.setPhone(c.getString(c.getColumnIndexOrThrow(R_PHONE)));
            restaurant.setNotes(c.getString(c.getColumnIndexOrThrow(R_NOTES)));
            restaurant.setFavorite(c.getInt(c.getColumnIndexOrThrow(R_FAVORITE)) == 1);
            restaurant.setImageUri(c.getString(c.getColumnIndexOrThrow(R_IMAGE)));
            restaurant.setUserId(c.getInt(c.getColumnIndexOrThrow(R_USER_ID)));
            c.close();
            return restaurant;
        }
        c.close();
        return null;
    }

    public List<Restaurant> getRestaurantsByUser(int userId) {
        List<Restaurant> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + T_RESTAURANTS + " WHERE " + R_USER_ID + "=?",
                new String[] { String.valueOf(userId) });
        while (c.moveToNext()) {
            Restaurant restaurant = new Restaurant();
            restaurant.setId(c.getInt(c.getColumnIndexOrThrow(R_ID)));
            restaurant.setName(c.getString(c.getColumnIndexOrThrow(R_NAME)));
            restaurant.setAddress(c.getString(c.getColumnIndexOrThrow(R_ADDRESS)));
            restaurant.setPhone(c.getString(c.getColumnIndexOrThrow(R_PHONE)));
            restaurant.setNotes(c.getString(c.getColumnIndexOrThrow(R_NOTES)));
            restaurant.setFavorite(c.getInt(c.getColumnIndexOrThrow(R_FAVORITE)) == 1);
            restaurant.setImageUri(c.getString(c.getColumnIndexOrThrow(R_IMAGE)));
            restaurant.setUserId(c.getInt(c.getColumnIndexOrThrow(R_USER_ID)));
            list.add(restaurant);
        }
        c.close();
        return list;
    }

    // Lấy danh sách quán ăn yêu thích của user
    public List<Restaurant> getFavoriteRestaurantsByUser(int userId) {
        List<Restaurant> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + T_RESTAURANTS + " WHERE " + R_USER_ID + "=? AND " + R_FAVORITE + "=1",
                new String[] { String.valueOf(userId) });
        while (c.moveToNext()) {
            Restaurant restaurant = new Restaurant();
            restaurant.setId(c.getInt(c.getColumnIndexOrThrow(R_ID)));
            restaurant.setName(c.getString(c.getColumnIndexOrThrow(R_NAME)));
            restaurant.setAddress(c.getString(c.getColumnIndexOrThrow(R_ADDRESS)));
            restaurant.setPhone(c.getString(c.getColumnIndexOrThrow(R_PHONE)));
            restaurant.setNotes(c.getString(c.getColumnIndexOrThrow(R_NOTES)));
            restaurant.setFavorite(c.getInt(c.getColumnIndexOrThrow(R_FAVORITE)) == 1);
            restaurant.setImageUri(c.getString(c.getColumnIndexOrThrow(R_IMAGE)));
            restaurant.setUserId(c.getInt(c.getColumnIndexOrThrow(R_USER_ID)));
            list.add(restaurant);
        }
        c.close();
        return list;
    }

    public boolean updateFavoriteStatus(int restaurantId, boolean isFavorite) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(R_FAVORITE, isFavorite ? 1 : 0);
        int rows = db.update(T_RESTAURANTS, cv, R_ID + "=?", new String[] { String.valueOf(restaurantId) });
        return rows > 0;
    }

    public List<Restaurant> searchRestaurantsByKeyword(int userId, String keyword) {
        List<Restaurant> list = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return list;
        }
        SQLiteDatabase db = getReadableDatabase();
        String like = "%" + keyword.trim().toLowerCase() + "%";
        Cursor c = db.rawQuery("SELECT * FROM " + T_RESTAURANTS + " WHERE " + R_USER_ID + "=? AND (LOWER(" + R_NAME
                + ") LIKE ? OR LOWER(" + R_ADDRESS + ") LIKE ? OR LOWER(" + R_NOTES + ") LIKE ?)",
                new String[] { String.valueOf(userId), like, like, like });
        while (c.moveToNext()) {
            Restaurant restaurant = new Restaurant();
            restaurant.setId(c.getInt(c.getColumnIndexOrThrow(R_ID)));
            restaurant.setName(c.getString(c.getColumnIndexOrThrow(R_NAME)));
            restaurant.setAddress(c.getString(c.getColumnIndexOrThrow(R_ADDRESS)));
            restaurant.setPhone(c.getString(c.getColumnIndexOrThrow(R_PHONE)));
            restaurant.setNotes(c.getString(c.getColumnIndexOrThrow(R_NOTES)));
            restaurant.setFavorite(c.getInt(c.getColumnIndexOrThrow(R_FAVORITE)) == 1);
            restaurant.setImageUri(c.getString(c.getColumnIndexOrThrow(R_IMAGE)));
            restaurant.setUserId(c.getInt(c.getColumnIndexOrThrow(R_USER_ID)));
            list.add(restaurant);
        }
        c.close();
        return list;
    }

    public Restaurant searchRestaurantByName(int userId, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }
        SQLiteDatabase db = getReadableDatabase();
        String likeQuery = "%" + keyword.trim().toLowerCase() + "%";
        Cursor c = db.rawQuery("SELECT * FROM " + T_RESTAURANTS + " WHERE " + R_USER_ID + "=? AND LOWER("
                + R_NAME + ") LIKE ? LIMIT 1", new String[] { String.valueOf(userId), likeQuery });
        if (c.moveToFirst()) {
            Restaurant restaurant = new Restaurant();
            restaurant.setId(c.getInt(c.getColumnIndexOrThrow(R_ID)));
            restaurant.setName(c.getString(c.getColumnIndexOrThrow(R_NAME)));
            restaurant.setAddress(c.getString(c.getColumnIndexOrThrow(R_ADDRESS)));
            restaurant.setPhone(c.getString(c.getColumnIndexOrThrow(R_PHONE)));
            restaurant.setNotes(c.getString(c.getColumnIndexOrThrow(R_NOTES)));
            restaurant.setFavorite(c.getInt(c.getColumnIndexOrThrow(R_FAVORITE)) == 1);
            restaurant.setImageUri(c.getString(c.getColumnIndexOrThrow(R_IMAGE)));
            restaurant.setUserId(c.getInt(c.getColumnIndexOrThrow(R_USER_ID)));
            c.close();
            return restaurant;
        }
        c.close();
        return null;
    }
}
