package com.yzx.bangbang.model;

/**
 * Created by Administrator on 2018/4/11.
 */

public class Contact {
    public int id, owner, person_id, first_letter;
    public String person_name;

    public Contact(int id, int owner, int first_letter, int person_id,String person_name) {
        this.id = id;
        this.owner = owner;
        this.person_id = person_id;
        this.first_letter = first_letter;
        this.person_name = person_name;
    }
    public Contact(){}
}
