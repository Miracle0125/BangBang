package com.yzx.bangbang.model;

public class Evaluate {
    public int id, relate_asm, relate_user, host_id;
    public float evaluate;
    public String host_name, content, date;

    public Evaluate(int relate_asm, int relate_user, int host_id, float evaluate, String host_name, String content, String date) {
        this.relate_asm = relate_asm;
        this.relate_user = relate_user;
        this.host_id = host_id;
        this.evaluate = evaluate;
        this.host_name = host_name;
        this.content = content;
        this.date = date;
    }
}
