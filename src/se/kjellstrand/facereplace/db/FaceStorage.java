package se.kjellstrand.facereplace.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import se.kjellstrand.facereplace.domain.Face;

public final class FaceStorage extends BaseStorage {
    private static final String TAG  = FaceStorage.class.getCanonicalName();

    private FaceStorage() {
    }

    public static long storeFace(Face face) {
        long result = -1;

        DataBaseHelper.getWritableDataBase().beginTransaction();
        try {
            ContentValues values = faceToContentValues(face);
            putContentValuesIfNotNull(TAG, values, FaceMetaData.FaceColumns.IMAGE,
                    face.getImage());
            putContentValuesIfNotNull(TAG, values, FaceMetaData.FaceColumns.TAG,
                    face.getTag());
            result = insertOrUpdate(values);
            DataBaseHelper.getWritableDataBase().setTransactionSuccessful();
        }
        catch (Exception e) {
            Log.d(TAG, "ERROR in storeCollections.");
            e.printStackTrace();
        }
        finally {
            DataBaseHelper.getWritableDataBase().endTransaction();
        }
        return result;
    }

    public static ArrayList<Face> getAllFaces() {
        Cursor cursor = DataBaseHelper.getWritableDataBase().query(FaceMetaData.FACE_TABLE_NAME, null, null,null, null, null, null);
        ArrayList<Face> faces = new ArrayList<Face>();
        try {
            if (cursor.moveToFirst()) {
                do {
                    Face face = new Face();
                    face.setImage(cursor.getBlob(cursor.getColumnIndex(FaceMetaData.FaceColumns.IMAGE)));
                    face.setTag(cursor.getString(cursor.getColumnIndex(FaceMetaData.FaceColumns.TAG)));
                    faces.add(face);
                }
                while (cursor.moveToNext());
            }
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return faces;
    }

    //    public static int removeCollection(int collectionIdToDelete) {
    //        Log.d(TAG, "removeCollection(): " + collectionIdToDelete);
    //        return DataBaseHelper.getWritableDataBase().delete(FaceMetaData.FACE_TABLE_NAME,
    //                FaceMetaData.CollectionColumns.ID + "=?", new String[]{collectionIdToDelete + ""});
    //    }

    public static ContentValues faceToContentValues(Face face){
        ContentValues values = new ContentValues();

        putContentValuesIfNotNull(TAG, values, FaceMetaData.FaceColumns.IMAGE, face.getImage());
        putContentValuesIfNotNull(TAG, values, FaceMetaData.FaceColumns.TAG, face.getTag());

        return values;
    }

    private static long insertOrUpdate(ContentValues values) {
        return DataBaseHelper.insertOrUpdate(FaceMetaData.FACE_TABLE_NAME, values , null, null);
    }
}
