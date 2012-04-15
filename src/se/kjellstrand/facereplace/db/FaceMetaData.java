package se.kjellstrand.facereplace.db;

import android.provider.BaseColumns;

public final class FaceMetaData {

    public static final String FACE_TABLE_NAME = "FACE";

    private FaceMetaData() {
    }

    public static final class FaceColumns implements BaseColumns {

        public static final String IMAGE             = "image";
        public static final String TAG             = "tag";

        private FaceColumns() {
        }
    }

    public static final String CREATE_FACE_SQL = "CREATE TABLE " + FACE_TABLE_NAME + " ( " + FaceColumns._ID
                                                             + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                                                             + FaceColumns.IMAGE + DataBaseHelper.BLOB
                                                             + FaceColumns.TAG + DataBaseHelper.TEXT+");";

    public static final String DROP_FACE_SQL   = "DROP TABLE IF EXISTS " + FACE_TABLE_NAME;

}
