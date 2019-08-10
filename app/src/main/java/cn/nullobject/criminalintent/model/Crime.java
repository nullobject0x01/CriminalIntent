package cn.nullobject.criminalintent.model;

import java.util.Date;
import java.util.UUID;

/**
 * @author xiongda
 * Created on 2019/8/10.
 * Introduction:
 */
public class Crime {

    private final UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;

    public Crime() {
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public Crime setTitle(final String title) {
        mTitle = title;
        return this;
    }

    public Date getDate() {
        return mDate;
    }

    public Crime setDate(final Date date) {
        mDate = date;
        return this;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public Crime setSolved(final boolean solved) {
        mSolved = solved;
        return this;
    }
}
