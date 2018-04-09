package com.yzx.bangbang.model;

public class Bid {
    public int id, asm_id, host_id, num_comment, day_time;
    public String host_name;
    public float price, evaluate;

    public Bid(int asm_id,
               int host_id,
               String host_name,
               int num_comment,
               float evaluate,
               int day_time,
               float price) {
        this.asm_id = asm_id;
        this.host_id = host_id;
        this.num_comment = num_comment;
        this.day_time = day_time;
        this.host_name = host_name;
        this.price = price;
        this.evaluate = evaluate;
    }
}
