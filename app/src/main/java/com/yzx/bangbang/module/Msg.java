package com.yzx.bangbang.module;

public     class Msg {
   public String receiver_name, poster_name, asm_title, date;
    public int owner_id, receiver_id, poster_id, asm_id, read;

  public   Msg(String receiver_name, String poster_name, String asm_title,
        String date, int owner_id, int receiver_id, int poster_id,
        int asm_id, int read) {
        this.receiver_name = receiver_name;
        this.poster_name = poster_name;
        this.asm_title = asm_title;
        this.date = date;
        this.owner_id = owner_id;
        this.receiver_id = receiver_id;
        this.poster_id = poster_id;
        this.asm_id = asm_id;
        this.read = read;
    }
}
