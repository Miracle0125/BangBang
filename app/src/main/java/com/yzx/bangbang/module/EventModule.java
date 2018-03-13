package com.yzx.bangbang.module;

public class EventModule {

public    String user_name, obj_user_name, asm_title, message, date;
   public int user_id, obj_user_id, asm_id, type, success, chosen, fulfill;
  public  float price;

    public EventModule(String user_name, String obj_user_name, String asm_title, String message, String date,
                       int user_id, int obj_user_id, int asm_id, int type, int success, int chosen, int fulfill, float price) {
        this.user_name = user_name;
        this.obj_user_name = obj_user_name;
        this.asm_title = asm_title;
        this.message = message;
        this.date = date;
        this.user_id = user_id;
        this.obj_user_id = obj_user_id;
        this.asm_id = asm_id;
        this.type = type;
        this.success = success;
        this.chosen = chosen;
        this.fulfill = fulfill;
        this.price = price;

    }

    public String getUserName() {
        return user_name;
    }

    public String getObjUserName() {
        return obj_user_name;
    }

    public String getAsmTitle() {
        return asm_title;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

    public int getUserId() {
        return user_id;
    }

    public int getObjUserId() {
        return obj_user_id;
    }

    public int getAsmId() {
        return asm_id;
    }

    public int getType() {
        return type;
    }

    public int getSuccess() {
        return success;
    }

    public int getChosen() {
        return chosen;
    }

    public int getFulfill() {
        return fulfill;
    }

    public float getPrice(){
        return price;
    }

}