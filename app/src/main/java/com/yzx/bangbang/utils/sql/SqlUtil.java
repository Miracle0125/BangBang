package com.yzx.bangbang.utils.sql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2018/3/15.
 */

public class SqlUtil {

    public static final int TABLE_JSON = 0;
    private static final String[] table_map = {"json"};
    private static final String[] map_create_sql = {"create table json(_id integer primary key autoincrement,key text,body text)"};
    private static final String[] map_insert_sql = {"insert into json(key,body) values('?','?')"};
    private static final String[] map_query_sql = {};

    public static void init() {
        SQLiteDatabase db = inst();
        for (int i = 0; i < table_map.length; i++) {
            if (!is_table_exist(table_map[i]))
                db.execSQL(map_create_sql[i]);
        }
        db.close();
    }

    private static SQLiteDatabase inst() {
        return SQLiteDatabase.openOrCreateDatabase("/data/data/com.yzx.bangbang/databases/bb.db", null);
    }

    public static void insert(int table, String key, String val) {
        SQLiteDatabase db = inst();
        db.execSQL(ps(map_insert_sql[table], key, val));
        db.close();
    }

    public static String queryString(int table, String key) {
        SQLiteDatabase db = inst();
        String res = "";
        Cursor cursor = db.query(table_map[table], null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.move(i);
                if (cursor.getString(1).equals(key)) {
                    res = cursor.getString(2);
                    break;
                }
            }
        }
        cursor.close();
        db.close();
        return res;
    }

    private static boolean is_table_exist(String tabName) {
        boolean result = false;
        if (tabName == null) return false;
        try {
            SQLiteDatabase db = inst();//此this是继承SQLiteOpenHelper类得到的
            Cursor cursor = db.rawQuery("select count(*) as c from sqlite_master where type ='table' and name ='" + tabName.trim() + "' ", null);
            if (cursor.moveToNext())
                if (cursor.getInt(0) > 0)
                    result = true;
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String ps(String sql, String... val) {
        for (int i = 0; i < val.length; i++) {
            sql.replaceFirst("\\?", val[i]);
        }
        return sql;
    }

}
