package com.yzx.bangbang.utils.sql;

import android.os.AsyncTask;
import android.util.SparseArray;

import model.Assignment;


//不完善
public class SA {
    public static final int TYPE_USER = 0;
    public static final int TYPE_ASSIGNMENT = 1;
    public static final int TYPE_NOTIFIES = 2;
    public static final int TYPE_FRACTIVITY_FRAGMENT = 3;
    public static final int TYPE_ASSIGNMENT_FILTER = 4;
    //public static final int TYPE_NOTIFY_FLAG = 1;

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

    public static Object query(int type) {
        return sa.get(type);
        //return SqlUtil.queryString(SqlUtil.TABLE_JSON, keys[t]);
    }


    public static void insert(Object o, int type) {
        AsyncTask.execute(() -> {
            int key = type;
            if (type == TYPE_ASSIGNMENT)
                key = ((Assignment) o).getId();
            sa.append(key, o);
        });
    }
}
