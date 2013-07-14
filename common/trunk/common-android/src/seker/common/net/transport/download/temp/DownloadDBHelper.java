
package seker.common.net.transport.download.temp;

import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.SparseIntArray;

/**
 * 
 * @author Lifeix
 * 
 */
class DownloadDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE = "download.db";
    
    private static final String TABLE    = "download_table";
    
    private static final int    VERSION  = 0;
    
    public DownloadDBHelper(Context context) {
        super(context, DATABASE, null, VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE
                + " (id integer primary key autoincrement, url varchar(200), threadId INTEGER, downedSize INTEGER)";
        db.execSQL(sql);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABLE;
        db.execSQL(sql);
        onCreate(db);
    }
    
    public void add(String path, Map<Integer, Integer> map) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                String sql = "insert into " + TABLE + "(url, threadId, downedSize) values(?,?,?)";
                db.execSQL(sql, new Object[] { path, entry.getKey(), entry.getValue() });
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }
    
    public void delete(String path) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "delete from " + TABLE + " where url = ?";
        db.execSQL(sql, new Object[] { path });
        db.close();
    }
    
    public void update(String url, int threadId, int pos) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        String sql = "update " + TABLE + " set downedSize = ?  where url = ? and threadId = ?";
        db.execSQL(sql, new Object[] { pos, url, threadId });
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }
    
    /**
     * 获取每个线程已经下载的大小
     * 
     * @param path
     * @return
     */
    public SparseIntArray query(String path) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "select threadId, downedSize from " + TABLE + " where url = ?";
        Cursor cursor = db.rawQuery(sql, new String[] { path });
        SparseIntArray data = new SparseIntArray();
        while (cursor.moveToNext()) {
            data.put(cursor.getInt(0), cursor.getInt(1));
        }
        cursor.close();
        db.close();
        return data;
    }
}
