package com.yzx.bangbang.model;

public class Comment {

    public String poster_name, receiver_name, date, content;
    public int id, asm_id, poster_id, receiver_id, parent, pos, floors;

    public Comment(int id, int asm_id, int poster_id, String poster_name, int receiver_id, String receiver_name, String content, String date,
                   int parent, int pos, int floors) {
        this.poster_name = poster_name;
        this.receiver_name = receiver_name;
        this.date = date;
        this.id = id;
        this.asm_id = asm_id;
        this.poster_id = poster_id;
        this.receiver_id = receiver_id;
        this.parent = parent;
        this.pos = pos;
        this.content = content;
        this.floors = floors;
    }

    public String getPosterName() {
        return poster_name;
    }

    public String getReceiverName() {
        return receiver_name;
    }

    public String getDate() {
        return date;
    }

    public int getId() {
        return id;
    }

    public int getAsmId() {
        return asm_id;
    }

    public int getPosterId() {
        return poster_id;
    }

    public int getReceiverId() {
        return receiver_id;
    }

    public int getParent() {
        return parent;
    }

    public int getPos() {
        return pos;
    }

    public String getContent() {
        return content;
    }
}