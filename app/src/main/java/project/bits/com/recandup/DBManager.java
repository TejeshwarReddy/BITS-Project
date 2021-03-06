package project.bits.com.recandup;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by tejeshwar on 27/1/17.
 */

/**
 * A class for DB Transactions:
 *      stores the name of the video files which are to be
 *      uploaded and deletes them after they are uploaded.
 */

public class DBManager extends SQLiteOpenHelper {

    //DB PARAMETERS
    private static final String DBNAME = "db";
    private static final String TABLE = "videos";
    private static final int DBVERSION = 1;

    //column names
    private static final String KEY_ID = "_id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_UPLOADED = "uploaded";
    private static final String KEY_DELETED = "deleted";
    private static final String KEY_TIME = "time";

    //CREATE TABLE
    private static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_TITLE + " TEXT, " +
            KEY_DELETED + " INTEGER NOT NULL DEFAULT 0, " +
            KEY_UPLOADED + " INTEGER NOT NULL DEFAULT 0, " +
            KEY_TIME + " INTEGER UNIQUE NOT NULL);";

    private SQLiteDatabase db;
    private long success = 0;

    //adds video
    public long addVideoAddress(String title,long time){
        db=this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_TITLE,title);
        cv.put(KEY_TIME,time);
        success = db.insert(TABLE,null,cv);
        db.close();
        return success;
    }

    public long feedUploadSuccess(String title){
        db=this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_UPLOADED,1);
        success = db.update(TABLE,cv,KEY_TITLE+"= '"+title+"'",null);
        db.close();
        return success;
    }

    public long feedDeletedSuccess(String title){
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_DELETED,1);
        success = db.update(TABLE,cv,KEY_TITLE+" = '"+title+"'",null);
        db.close();
        return success;
    }

    public ArrayList<String> getNotUploadedVideos(){
        ArrayList<String> notUploaded = new ArrayList<>();
        db = this.getWritableDatabase();
        long time = System.currentTimeMillis()-40000;
        Cursor cursor = db.rawQuery("SELECT " + KEY_TITLE + " FROM " + TABLE + " WHERE " + KEY_UPLOADED +" = 0 AND "+KEY_TIME+" <= '"+ time +"' ORDER BY "+ KEY_TIME , null);
        if (cursor.moveToFirst()){
            do {
                notUploaded.add(cursor.getString(0));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return notUploaded;
    }

    public ArrayList<String> getToBeDeleted(){
        ArrayList<String> toDelete = new ArrayList<>();
        db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + KEY_TITLE + " FROM " + TABLE + " WHERE " + KEY_UPLOADED + "= 1", null);
        if (cursor.moveToFirst()){
            do {
                toDelete.add(cursor.getString(0));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return toDelete;
    }

    public ArrayList<String> deleteMemory(){
        ArrayList<String> deleteMemory = new ArrayList<>();
        db = this.getWritableDatabase();
        long time = System.currentTimeMillis()-24*60*60*1000;
        Cursor cursor = db.rawQuery("SELECT " + KEY_TITLE + " FROM " + TABLE + " WHERE " + KEY_TIME + " <= "+time,null);
        if (cursor.moveToFirst()){
            do {
                deleteMemory.add(cursor.getString(0));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return deleteMemory;
    }

    public long deleteRecords(){
        db = this.getWritableDatabase();
        success = db.delete(TABLE,KEY_DELETED + " = 1",null);
        db.close();
        return success;
    }

    public DBManager(Context context) {
        super(context, DBNAME, null, DBVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE);
        onCreate(sqLiteDatabase);
    }
}
