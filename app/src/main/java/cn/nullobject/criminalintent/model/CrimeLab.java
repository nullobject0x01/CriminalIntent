package cn.nullobject.criminalintent.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.nullobject.criminalintent.db.CrimeBaseHelper;
import cn.nullobject.criminalintent.db.CrimeCursorWrapper;
import cn.nullobject.criminalintent.db.CrimeDbSchema;

import static cn.nullobject.criminalintent.db.CrimeDbSchema.CrimeTable.Cols;
import static cn.nullobject.criminalintent.db.CrimeDbSchema.CrimeTable.NAME;

/**
 * @author xiongda
 * Created on 2019/8/10.
 * Introduction:
 */
public class CrimeLab {

    public static CrimeLab sInstance;
    private final Context mContext;
    private SQLiteDatabase mDatabase;

    public static CrimeLab get(Context context) {
        if (null == sInstance) {
            sInstance = new CrimeLab(context);
        }
        return sInstance;
    }

    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
    }

    public List<Crime> getCrimes() {
        List<Crime> crimeList = new ArrayList<>();

        CrimeCursorWrapper cursor = queryCrimes(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                crimeList.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return crimeList;
    }

    @Nullable
    public Crime getCrime(UUID uuid) {
        CrimeCursorWrapper cursorWrapper = queryCrimes(Cols.UUID + " = ?", new String[]{uuid.toString()});
        try {
            if (cursorWrapper.getCount() == 0) {
                return null;
            }
            cursorWrapper.moveToFirst();
            return cursorWrapper.getCrime();
        } finally {
            cursorWrapper.close();
        }
    }

    public void addCrime(@NonNull Crime crime) {
        mDatabase.insert(NAME, null, getContentValues(crime));
    }

    public void removeCrime(@NonNull Crime crime) {
        mDatabase.delete(NAME, Cols.UUID + " = ?", new String[]{crime.getId().toString()});
    }

    public void updateCrime(@NonNull Crime crime) {
        String uuidStr = crime.getId()
                              .toString();
        ContentValues values = getContentValues(crime);
        mDatabase.update(NAME, values, Cols.UUID + " = ?", new String[]{uuidStr});
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] args) {
        Cursor cursor = mDatabase.query(NAME, null, whereClause, args, null, null, null);
        return new CrimeCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(Crime crime) {
        ContentValues cv = new ContentValues();
        cv.put(Cols.UUID, crime.getId()
                               .toString());
        cv.put(Cols.TITLE, crime.getTitle());
        cv.put(Cols.DATE, crime.getDate()
                               .getTime());
        cv.put(Cols.SOLVED, crime.isSolved());
        return cv;
    }
}
