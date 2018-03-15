package com.yzx.bangbang.utils.sql;

import com.google.gson.Gson;
import com.yzx.bangbang.model.User;

import model.Assignment;

/**
 * Created by Administrator on 2018/3/16.
 */

public class DAO {
    public static final int TYPE_USER = 0;
    public static final int TYPE_ASSIGNMENT = 1;
    private static final Class[] cls = {User.class, Assignment.class};
    private static final String[] keys = {"user", "assignment"};

    public static Object query(int t) {
        return SqlUtil.queryString(SqlUtil.TABLE_JSON, keys[t]);
    }

    public static void insert(int t, Object o) {
        SqlUtil.insert(SqlUtil.TABLE_JSON, keys[t], new Gson().toJson(o, cls[t]));
    }
}
