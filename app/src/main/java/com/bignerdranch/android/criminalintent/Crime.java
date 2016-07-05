package com.bignerdranch.android.criminalintent;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Ting on 2016/6/26.
 */
public class Crime {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private String mSuspect;
    private String mPhoneNum;

    public Crime() {
//        mId = UUID.randomUUID();
//        mDate = new Date();
        this(UUID.randomUUID());

    }

    public Crime(UUID id) {
        mId = id;
        mDate = new Date();
    }

    public String getPhotoName() {
        return "IMG_" + getId().toString() + ".jpg";
    }

    public String getPhoneNum() {
        return mPhoneNum;
    }

    public void setPhoneNum(String mPhoneNum) {
        this.mPhoneNum = mPhoneNum;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String mSuspect) {
        this.mSuspect = mSuspect;
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        this.mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        this.mSolved = solved;
    }
}
