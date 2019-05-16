package com.example.sony.myapplication;

        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.SQLException;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.util.Log;

        import java.util.Arrays;

public class DBAdapter {
    public static final String KEY_ROWID = "id";
    public static final String KEY_STRING = "value";
 //   public static final String KEY_EMAIL = "email";
    private static final String TAG = "DBAdapter";
    private static final String DATABASE_NAME = "MyDB";
    private static final String DATABASE_TABLE = "sample";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_CREATE =
            "create table sample (id integer primary key autoincrement, "
                    + "value text not null);";
    private static final String DATABASE_DELETE =
            "drop table if exists sample ";
    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try {
                db.execSQL(DATABASE_CREATE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            //db.execSQL("DROP TABLE IF EXISTS sample");
            onCreate(db);
        }
    }


    //---opens the database---
    public DBAdapter open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public String setJournalMode(String jmode, String smode)
    {
        Cursor c = db.rawQuery("PRAGMA journal_mode="+jmode, null);
        String s="";
        if(c.moveToFirst()){
              //  c.moveToNext();
               s=s+ c.getString(0) ;
               c.close();
        }
        else {
            c.close();
            return null;
        }


        Cursor c2 = db.rawQuery("PRAGMA synchronous="+smode, null);
       // String s="";
        if(c2.moveToFirst()){
            c2.close();
            return s;
        }
        else
            return null;

    }




    public String setjournalsize(String jsize)
    {
        db.rawQuery("PRAGMA journal_size_limit="+jsize, null);
        Cursor c2 = db.rawQuery("PRAGMA journal_size_limit", null);

        if(c2.moveToFirst()){
            //  c.moveToNext();
            String s=c2.getString(0);
            c2.close();
            return s ;
        }
        else{
            c2.close();
            return null;
        }

    }





    public void start(){
        db.beginTransaction();
    }

    public void end(){
        db.endTransaction();
    }

    public void commit(){
        db.setTransactionSuccessful();
    }

    public int setpage(){
        Cursor c=db.rawQuery("PRAGMA page_size = 4096",null);
        c.moveToFirst();
        c.close();
        return 1;
    }


    public String checkpoint()
    {
        Cursor c = db.rawQuery("PRAGMA wal_checkpoint",null);
       c.moveToFirst();
       c.close();
       return " ";
    }


    //---closes the database---
    public void close()
    {
        DBHelper.close();
    }


    //---insert a contact into the database---
    public void insertRow()
    {
        ContentValues initialValues = new ContentValues();

        char[] chars=new char[3072];
        Arrays.fill(chars,'i');
        String insert_str=new String(chars);
        //String insert_str="aaaaabbbbbcccccdddddeeeeefffff";
        String sql = " insert into "+DATABASE_TABLE+"(value) VALUES("+"'"+insert_str+"'"+")" ;
        db.execSQL(sql);
    }


    //---deletes a particular contact---
    public void deleteRow(long rowId)
    {
        String sql = " delete from "+DATABASE_TABLE+" where id = "+ rowId  ;
        db.execSQL(sql);
       // return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }


    //---retrieves all the contacts---
    public Cursor getAllRows()
    {
        return db.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_STRING},
                null, null, null, null, null);
    }


    //---retrieves a particular contact---
    public Cursor getRow(long rowId) throws SQLException
    {
        Cursor mCursor =db.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                        KEY_STRING}, KEY_ROWID + "=" + rowId, null,
                null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }


    //---updates a contact---
    public void updateRow(long rowId)
    {
        ContentValues args = new ContentValues();
        char[] chars=new char[3072];
        Arrays.fill(chars,'u');
        String update_str=new String(chars);
       // String update_str="uuuuuvvvvvbbbbbbrrrrfffffyyyyy";
        String sql = " update "+DATABASE_TABLE+" set value = "+ "'"+update_str+"'"+" where id="+ rowId  ;
        db.execSQL(sql);

    }
}
