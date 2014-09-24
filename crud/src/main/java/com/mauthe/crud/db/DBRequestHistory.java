package com.mauthe.crud.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mauthe.crud.HttpRequest;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Ugo on 23/09/2014.
 */
public class DBRequestHistory extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "CrudDB";

    public DBRequestHistory(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
         db.execSQL("CREATE TABLE REQUESTS (_id INTEGER PRIMARY KEY AUTOINCREMENT, reqName TEXT, reqUrl TEXT, reqType INTEGER, reqTypeStr TEXT, reqHeaders TEXT, reqBody TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {

    }

    public boolean addRequest(HttpRequest request)

    {

        boolean ris = false;
        try {
            ContentValues values = new ContentValues(6);

            values.put("reqName", request.getName());
            values.put("reqUrl", request.getUrl());
            values.put("reqType", request.getRequestType());
            values.put("reqTypeStr", request.getRequestTypeStr());


            JSONObject jsonHeaders = new JSONObject();
            List<BasicNameValuePair> headers = request.getHeaders();

            if (headers.size() > 0) {
                for (int i = 0; i < headers.size(); i++) {
                    try {
                        jsonHeaders.put(headers.get(i).getName(), headers.get(i).getValue());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                values.put("reqHeaders", jsonHeaders.toString());
            }
            else {
                values.put("reqHeaders", "");
            }

            values.put("reqBody", request.getRequestBody());

            getWritableDatabase().insert("REQUESTS", "reqName", values);
            ris = true;
        }
        catch (Exception e) {
            Log.d("Crud","addRequest " + e.getLocalizedMessage() );
        }

        return ris;


    }

    public Cursor getRequestByName(String name)

    {
        Cursor c = getReadableDatabase().query("REQUESTS", new String[] {"_id","reqName","reqUrl","reqType","reqTypeStr","reqHeaders","reqBody" },"reqName ='"+name+"'",null,null,null,null);
        c.moveToFirst();
        return c;
    }

    public Cursor getRequestByID(int id)

    {
        Cursor c = getReadableDatabase().query("REQUESTS", new String[] {"_id","reqName","reqUrl","reqType","reqTypeStr","reqHeaders","reqBody" },"_id =?",new String[] {""+id},null,null,null);
        c.moveToFirst();
        return c;
    }


    public Cursor getAllRequests()
    {
        Cursor c = getReadableDatabase().query("REQUESTS", new String[] {"_id","reqName","reqUrl","reqType","reqTypeStr","reqHeaders","reqBody" },null,null,null,null,"_id DESC");
        c.moveToFirst();
        return c;

    }

    public boolean deleteRequestByName(String name)

    {
        return getReadableDatabase().delete("REQUESTS","reqName=?",new String[] {name})==1;
    }

    public boolean deleteRequestById(int id)

    {
        return getReadableDatabase().delete("REQUESTS","_id=?",new String[] {""+id})==1;
    }

    public boolean deleteAllRequests()

    {
        return getReadableDatabase().delete("REQUESTS",null,null)==0;
    }




    public HttpRequest cursorToRequest(Cursor cursor) {
        HttpRequest ris = new HttpRequest();
        if (cursor != null) {
            ris.setName(cursor.getString(1));
            ris.setUrl(cursor.getString(2));
            ris.setRequestType(cursor.getInt(3));
            //string 4 of cursor skipped.

            String jString = cursor.getString(5);

            if ((jString != null) &&(jString.length() > 0)) {
                try {
                    JSONObject headers = new JSONObject(jString);
                    for (int i = 0; i < headers.names().length(); i++) {
                        ris.getHeaders().add(new BasicNameValuePair(headers.names().getString(i), headers.getString(headers.names().getString(i))));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ris.setmRequestBody(cursor.getString(6));
        }
        return ris;

    }
}
