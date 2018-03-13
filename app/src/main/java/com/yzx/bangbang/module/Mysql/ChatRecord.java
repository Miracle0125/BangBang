package com.yzx.bangbang.module.Mysql;

public class ChatRecord {
    public String user_name, obj_user_name, message,convr_id;
    public int user_id, obj_user_id, id,type,record_time;
    public long date;

    public ChatRecord(String user_name, String obj_user_name, String message, int user_id, int obj_user_id, int id,
                      long date,String convr_id,int type,int record_time) {
        this.user_name = user_name;
        this.obj_user_name = obj_user_name;
        this.message = message;
        this.user_id = user_id;
        this.obj_user_id = obj_user_id;
        this.id = id;
        this.date = date;
        this.convr_id = convr_id;
        this.type = type;
        this.record_time = record_time;
    }
}
