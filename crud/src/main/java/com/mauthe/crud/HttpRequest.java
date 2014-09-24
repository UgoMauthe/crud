package com.mauthe.crud;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ugo on 21/09/2014.
 */
public class HttpRequest {

    public static int REQUEST_TYPE_GET = 0;
    public static int REQUEST_TYPE_POST = 1;
    public static int REQUEST_TYPE_PUT = 2;
    public static int REQUEST_TYPE_DELETE = 3;

    String mUrl;
    String mName;
    int mRequestType;

    List<BasicNameValuePair> mHeaders;
    String mRequestBody;

    public HttpRequest() {

        mHeaders = new ArrayList<BasicNameValuePair>();
    }

    public void setName(String aValue) {
        mName = aValue;
    }

    public void setUrl(String aValue) {
        mUrl = aValue;
    }

    public void setRequestType(int aValue) {
        mRequestType = Math.min(Math.max(aValue,REQUEST_TYPE_GET),REQUEST_TYPE_DELETE);
    }

    public void setmRequestBody(String aValue) {
        mRequestBody = aValue;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getName() {
        return mName;
    }

    public String getRequestBody() {
        return mRequestBody;
    }

    public int getRequestType() {
        return mRequestType;
    }

    public String getRequestTypeStr() {
        switch (mRequestType) {
            case 1: return "POST";
            case 2: return "PUT";
            case 3: return "DEL";
            default: return "GET";
        }
    }

    public List<BasicNameValuePair> getHeaders() {
        return mHeaders;
    }













}
