package cn.nullobject.criminalintent.model;

import java.util.Date;
import java.util.UUID;

import androidx.annotation.Nullable;

/**
 * @author xiongda
 * Created on 2019/8/10.
 * Introduction:
 */
public class Crime {

    private final UUID mId;
    private String mTitle = "";
    private Date mDate;
    private boolean mSolved;

    private String mSuspect;

    public Crime(UUID id) {
        mId = id;
        mDate = new Date(System.currentTimeMillis());
    }

    public Crime() {
        this(UUID.randomUUID());
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

    public String getSuspect() {
        return mSuspect;
    }

    public Crime setSuspect(final String suspect) {
        mSuspect = suspect;
        return this;
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (obj instanceof Crime) {
            return mId.equals(((Crime) obj).mId);
        }
        return false;
    }
}
