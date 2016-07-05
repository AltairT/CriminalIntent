package com.bignerdranch.android.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.widget.Toast;

import com.bignerdranch.android.criminalintent.database.CrimeBaseHelper;
import com.bignerdranch.android.criminalintent.database.CrimeCursorWrapper;
import com.bignerdranch.android.criminalintent.database.CrimeDbSchema;
import com.bignerdranch.android.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Ting on 2016/6/27.
 */
public class CrimeLab {
    private static CrimeLab sCrimeLab;
    ///private List<Crime> mCrimes;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext)
                .getWritableDatabase();
        ///mCrimes = new ArrayList<>();
//        for(int i = 0;i<100;i++){
//            Crime crime = new Crime();
//            crime.setTitle("不良行为 #"+(i+1));
//            crime.setSolved(i%2 == 0);
//            mCrimes.add(crime);
//        }
    }

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());
        values.put(CrimeTable.Cols.PHONENUM, crime.getPhoneNum());

        return values;
    }

    public List<Crime> getCrimes() {
        ///return mCrimes;
//        return new ArrayList<>();
        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrimes(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return crimes;

    }

    public Crime getCrime(UUID id) {
//      ///  for(Crime crime:mCrimes){
//            if(crime.getId().equals(id)){
//                return crime;
//            }
//
//        }
//        return null;
        CrimeCursorWrapper cursor = queryCrimes(
                CrimeTable.Cols.UUID + " = ?",
                new String[]{id.toString()}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            cursor.close();
        }

    }

    public void addCrime(Crime c) {
        ///mCrimes.add(c);
        ContentValues values = getContentValues(c);
        mDatabase.insert(CrimeTable.NAME, null, values);

    }

    public void delCrime(Crime c) {
        ///mCrimes.remove(c);
        String uuidString = c.getId().toString();
        mDatabase.delete(CrimeTable.NAME, CrimeTable.Cols.UUID + " = ?", new String[]{uuidString});

    }

    public void updateCrime(Crime c) {
        String uuidString = c.getId().toString();
        ContentValues values = getContentValues(c);

        mDatabase.update(CrimeTable.NAME, values,
                CrimeTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    public CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        //public Cursor query (String table,
        // String[] columns,
        // String selection,
        // String[] selectionArgs,
        // String groupBy,
        // String having,
        // String orderBy)
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
//        return  cursor;
        return new CrimeCursorWrapper(cursor);
    }

    public File getPhotoFile(Crime crime) {
        File externalFileDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (externalFileDir == null) {
            Toast.makeText(mContext.getApplicationContext(), "File externalFileDir为空", Toast.LENGTH_SHORT).show();
            return null;
        }

        return new File(externalFileDir, crime.getPhotoName());
    }
}
