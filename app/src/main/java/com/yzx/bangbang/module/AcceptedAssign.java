package com.yzx.bangbang.module;
public class AcceptedAssign {
    public String asm_title, date;
    public int user_id, asm_id, success, fulfill;
    public float price;

    public AcceptedAssign(String asm_title, String date, int user_id, int asm_id, int success, int fulfill,
                          float price) {
        this.asm_title = asm_title;
        this.date = date;
        this.user_id = user_id;
        this.asm_id = asm_id;
        this.success = success;
        this.fulfill = fulfill;
        this.price = price;
    }

    public String getAsmTitle() {
        return asm_title;
    }

    public String getDate() {
        return date;
    }

    public int getUserId() {
        return user_id;
    }

    public int getAsmId() {
        return asm_id;
    }

    public int getSuccess() {
        return success;
    }

    public int getFulfil() {
        return fulfill;
    }

    public float getPrice() {
        return price;
    }
}
