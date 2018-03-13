package com.yzx.bangbang.module;


public class UserRecord {
   public int user_id, num_asm, num_accept, num_concern, num_coll,num_follower;

    public UserRecord(int user_id, int num_asm, int num_accept, int num_concern,int num_follower, int num_coll) {
        this.user_id = user_id;
        this.num_asm = num_asm;
        this.num_accept = num_accept;
        this.num_concern = num_concern;
        this.num_coll = num_coll;
        this.num_follower = num_follower;
    }
}
