package com.yzx.bangbang.module.Mysql;

/**
 * Created by Administrator on 2016/12/6.
 */

//  为了提高开发速度， 接受需求、收藏需求、关注用户这三种行为都储存在数据库event表中，后期有需要可以解耦.type字段定义行为类型，定义在PARAMS
public class ModuleEvent {
    int id;
    int user_id;
    String user_name;
    int obj_user_id;
    String obj_user_name;
    int asm_id;
    String asm_title;
    String message;
    float price;
    String date;
    int type;
    int success;
    int chosen;
    int fulfill;

    public ModuleEvent(int id, int user_id, String user_name, int obj_user_id, String obj_user_name, int asm_id, String asm_title, String message, float price, String date, int type, int success, int chosen, int fulfill) {

    }
}
