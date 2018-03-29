package com.yzx.bangbang.model;

public class Notify {
    public static final int CODE_NEW_BID = 0;
    public static final int CODE_WIN_THE_BIDDING = 1;
    public static final int CODE_ACCOMPLISH = 2;
    public static final int CODE_PASS_ACCEPTANCE = 3;

    public int id, target_id, person_id, relate_id, what,read;
    public String person_name, relate_str, date;

}
