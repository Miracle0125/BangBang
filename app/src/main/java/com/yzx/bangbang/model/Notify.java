package com.yzx.bangbang.model;

public class Notify {
    public static final int CODE_NEW_BID = 0;
    public static final int CODE_WIN_THE_BIDDING = 1;
    public static final int CODE_ACCOMPLISH = 2;
    public static final int CODE_CHECK_QUALIFIED = 3;
    public static final int CODE_CHECK_UNQUALIFIED = 4;
    public static final int CODE_UNABLE_TO_FINISH = 5;
    public static final int CODE_NEW_CONCERN = 6;
    public static final int CODE_CONCERN_NEW_ASSIGNMENT = 7;
    public static final int CODE_COMMENT_REPLIED = 8;
    public int id, target_id, person_id, relate_id, what, read;
    public String person_name, relate_str, date;

}
