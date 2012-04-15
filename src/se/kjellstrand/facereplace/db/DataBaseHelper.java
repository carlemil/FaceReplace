package se.kjellstrand.facereplace.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by erbsman - 2012-04-15
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String   TAG              = DataBaseHelper.class.getCanonicalName();
    private static final short    DATABASE_VERSION = 1;
    private static final String         databaseName    = "faces";

    private static SQLiteDatabase dataBase         = null;
    private static Context        context;

    static final String           TEXT             = " TEXT, ";
    static final String           INT              = " INT, ";
    static final String           BLOB              = " BLOB, ";
    static final String           REAL             = " TEXT DEFAULT NULL, ";

    public static final String    TRUE             = "true";
    public static final String    FALSE            = "false";


    public DataBaseHelper(Context mContext) {
        super(mContext, databaseName, null, DATABASE_VERSION);
        context = mContext;

        Log.d(TAG, "init of db: " + databaseName + " starting.");
        dataBase = super.getWritableDatabase();
        Log.d(TAG, "init of db: " + databaseName + " complete.");

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            Log.d(TAG, "creating table: " + FaceMetaData.FACE_TABLE_NAME);
            sqLiteDatabase.execSQL(FaceMetaData.CREATE_FACE_SQL);
        }
        catch (Exception e) {
            Log.d(TAG, "Exception while creating tables in DB: " + e.getMessage());
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.d(TAG, "upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        onDestroy(sqLiteDatabase);
        onCreate(sqLiteDatabase);
    }

    public void onDestroy(SQLiteDatabase sqLiteDatabase) {
        try {
            Log.d(TAG, "removing table: " + FaceMetaData.FACE_TABLE_NAME);
            sqLiteDatabase.execSQL(FaceMetaData.DROP_FACE_SQL);
            context.deleteDatabase(databaseName);
        }
        catch (Exception e) {
            Log.d(TAG, "Exception while removing tables in DB: " + e.getMessage());
        }
    }

    public static SQLiteDatabase getWritableDataBase() {
        return dataBase;
    }

    public static long insertOrUpdate(String tableName, ContentValues values, String whereClause, String[] whereArgs) {
        try {
            // when this fails because it exists it throws android.database.sqlite.SQLiteConstraintException: error code 19:
            // constraint failed;
            return dataBase.insertOrThrow(tableName, null, values);
        }
        catch (SQLiteException e) {
            Log.v(TAG, e.getMessage() + " when inserting row, tableName: " + tableName
                    + ", Trying to update instead of inserting.");
            try {
                return dataBase.update(tableName, values, whereClause, whereArgs);
            }
            catch (SQLiteException e2) {
                Log.e(TAG, "SQLiteException in insertOrUpdate, " + e2.getMessage() + e2.toString());
                return -1;
            }
        }
    }

    public static long insertButDontUpdate(String tableName, ContentValues values, String whereClause, String[] strings) {
        Log.d(TAG, "insertButDontUpdate in " + tableName);
        try {
            // when this fails because it exists it throws android.database.sqlite.SQLiteConstraintException: error code 19:
            // constraint failed;
            return dataBase.insertOrThrow(tableName, null, values);
        }
        catch (SQLiteException e) {
            Log.d(TAG, e.getMessage() + " when inserting row, tableName: " + tableName + " values: " + values.toString()
                    + " whereClause: " + whereClause + ".");
            return -1;
        }
    }
}
