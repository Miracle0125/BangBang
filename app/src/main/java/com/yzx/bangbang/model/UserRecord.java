package com.yzx.bangbang.model;

public class UserRecord {
    public int id, user_id, num_assignment, num_bids, num_accept, num_concerns, num_be_concerned,
            num_collect, num_evaluate, num_complete, num_in_budget, num_in_time;
    public float evaluate;

    public UserRecord(int user_id, int num_asm, int num_accept, int num_concern, int num_follower, int num_coll) {
        this.user_id = user_id;
        this.num_assignment = num_asm;
        this.num_accept = num_accept;
        this.num_concerns = num_concern;
        this.num_collect = num_coll;
        this.num_be_concerned = num_follower;
    }
}
