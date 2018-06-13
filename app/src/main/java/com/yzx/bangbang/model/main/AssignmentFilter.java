package com.yzx.bangbang.model.main;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class AssignmentFilter implements Serializable {
    public boolean freelancer;
    public boolean employer;

    public AssignmentFilter() {
        freelancer = true;
        employer = true;
    }
}
