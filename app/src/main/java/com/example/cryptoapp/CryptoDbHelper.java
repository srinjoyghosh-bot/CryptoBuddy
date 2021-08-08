package com.example.cryptoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class CryptoDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="cryptos.db";
    private static final int DATABASE_VERSION=1;
    public static final String TABLE_NAME="cryptocurrencies";
    public static final String COLUMN_ID="_id";
    public static final String COLUMN_CRYPTONAME="name";
    public static final String COLUMN_CRYPTOID="cryptoId";
    public static final String COLUMN_CRYPTO="isCrypto";
    public static final String COLUMN_FAVOURITE="isFavourite";

    public CryptoDbHelper(@Nullable Context context,  @Nullable SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE="CREATE TABLE "+TABLE_NAME+" ("+COLUMN_ID+" INTEGER PRIMARY KEY,"+COLUMN_CRYPTONAME+" TEXT,"+COLUMN_CRYPTOID+" TEXT,"+COLUMN_CRYPTO+" INTEGER,"+COLUMN_FAVOURITE+" INTEGER"+")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
    public void addCrypto(CryptoLive cryptoLive)
    {
        ContentValues values=new ContentValues();
        values.put(COLUMN_CRYPTONAME,cryptoLive.getName());
        values.put(COLUMN_CRYPTOID,cryptoLive.getAsset_Id());
        values.put(COLUMN_CRYPTO,cryptoLive.getType());
        values.put(COLUMN_FAVOURITE,0);

        SQLiteDatabase database=this.getWritableDatabase();
        try
        {
            long result=database.insert(TABLE_NAME,null,values);
        }
        catch (SQLiteConstraintException e)
        {
            Log.e("addCrypto sqlite",e.getMessage());
        }

    }
    public boolean checkIfFavourite(String cryptoName)
    {
        String query="SELECT * FROM "+TABLE_NAME+" WHERE "+COLUMN_CRYPTONAME+"= \""+cryptoName+"\"";
        SQLiteDatabase database=this.getWritableDatabase();
        Cursor cursor= database.rawQuery(query,null);
        if (cursor.getCount()>0)
        {
            cursor.moveToFirst();
            int isFav=cursor.getInt(cursor.getColumnIndex(COLUMN_FAVOURITE));
            if (isFav==1)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }
    public void updateToFavourite(String cryptoName,String cryptoId)
    {
        SQLiteDatabase database=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(COLUMN_CRYPTONAME,cryptoName);
        values.put(COLUMN_CRYPTOID,cryptoId);
        values.put(COLUMN_CRYPTO,1);
        values.put(COLUMN_FAVOURITE,1);
        database.update(TABLE_NAME,values,COLUMN_CRYPTONAME+"=?",new String[]{cryptoName});
    }
    public void removeFromFavourite(String cryptoName,String cryptoId)
    {
        SQLiteDatabase database=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(COLUMN_CRYPTONAME,cryptoName);
        values.put(COLUMN_CRYPTOID,cryptoId);
        values.put(COLUMN_CRYPTO,1);
        values.put(COLUMN_FAVOURITE,0);
        database.update(TABLE_NAME,values,COLUMN_CRYPTONAME+"=?",new String[]{cryptoName});
    }


}
