package cn.nullobject.criminalintent.db;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

import cn.nullobject.criminalintent.model.Crime;

import static cn.nullobject.criminalintent.db.CrimeDbSchema.*;

/**
 * @author xiongda
 * Created on 2019/8/17.
 * Introduction:
 */
public class CrimeCursorWrapper extends CursorWrapper {

    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public CrimeCursorWrapper(final Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        String uuidStr = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        return new Crime(UUID.fromString(uuidStr)).setTitle(title)
                                                  .setDate(new Date(date))
                                                  .setSolved(isSolved != 0);

    }

}
