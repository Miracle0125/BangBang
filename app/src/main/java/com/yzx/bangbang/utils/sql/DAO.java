package com.yzx.bangbang.utils.sql;

import android.os.AsyncTask;
import android.util.SparseArray;

import model.Assignment;

/**
 * Created by Administrator on 2018/3/16.
 */

public class DAO {
    public static final int TYPE_USER = 0;
    //public static final int TYPE_ASSIGNMENT = 1;
  //  private static Map<Class, Integer> map;
  //  private static final Class[] cls = {User.class, Assignment.class};
  //  private static final String[] keys = {"user", "assignment"};
    private static SparseArray sa;

    public static void init() {
        sa = new SparseArray();
//        map = new HashMap<>();
//        for (int i = 0; i < cls.length; i++)
//            map.put(cls[i], i);
    }

    public static Object query(int t) {
        return sa.get(t);
        //return SqlUtil.queryString(SqlUtil.TABLE_JSON, keys[t]);
    }
    @SuppressWarnings("unchecked")
    public static void insert(Object o) {
        AsyncTask.execute(()->{
            int key = TYPE_USER;
            if (o instanceof Assignment) key = ((Assignment) o).getId();
            sa.append(key, o);
        });
        //SqlUtil.insert(SqlUtil.TABLE_JSON, keys[t], new Gson().toJson(o, cls[t]));
    }
}
