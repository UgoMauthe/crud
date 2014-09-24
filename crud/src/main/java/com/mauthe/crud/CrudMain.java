package com.mauthe.crud;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.mauthe.crud.comp.ListViewAutoGrow;
import com.mauthe.crud.db.DBRequestHistory;
import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.ArrayList;


public class CrudMain extends Activity {

    Context mContext;

    HttpRequest mMainHttpRequest;
    ListViewAutoGrow mListViewHeaders;
    ParametersListAdapter mHeadersAdapter;

    TextView mRequestBody;
    TextView mUrl;

    Spinner spinRequestType;

    EditText mTxtResponse;
    EditText mTxtResponseHeaders;
    LinearLayout mLayoutResponse;

    long requstDuration;

    ProgressDialog mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crud_main);


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        mMainHttpRequest = new HttpRequest();

        mContext = this;

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        boolean showSplash = sp.getBoolean("splash",true);

        if (showSplash) {
            Intent intent = new Intent(CrudMain.this,Splash.class);
            startActivity(intent);
            return;
        }


        spinRequestType = (Spinner) findViewById(R.id.requestType);


        ArrayList<String> requestTypes = new ArrayList<String>();

        requestTypes.add(getString(R.string.get));
        requestTypes.add(getString(R.string.post));
        requestTypes.add(getString(R.string.put));
        requestTypes.add(getString(R.string.delete));

        ArrayAdapter<String> requestTypez = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,requestTypes) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                Typeface externalFont=Typeface.createFromAsset(getAssets(), "valera_round.otf");
                ((TextView) v).setTypeface(externalFont);

                return v;
            }


            public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                View v =super.getDropDownView(position, convertView, parent);
                Typeface externalFont=Typeface.createFromAsset(getAssets(), "valera_round.otf");
                ((TextView) v).setTypeface(externalFont);
                v.setPadding(getResources().getDimensionPixelSize(R.dimen.drop_down_list_padding),
                             getResources().getDimensionPixelSize(R.dimen.drop_down_list_padding),
                             getResources().getDimensionPixelSize(R.dimen.drop_down_list_padding),
                             getResources().getDimensionPixelSize(R.dimen.drop_down_list_padding));

                return v;
            }
        };

        ScrollView sv_crud_main = (ScrollView) findViewById(R.id.sv_crud_main);

        spinRequestType.setAdapter(requestTypez);
        utils.changeFontTypeFace(this,(ViewGroup) sv_crud_main);

        final LinearLayout requestBody = (LinearLayout) findViewById(R.id.llRequestBody);


        spinRequestType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0 :  requestBody.setVisibility(View.GONE); break;
                    case 1 :  requestBody.setVisibility(View.VISIBLE); break;
                    case 2 :  requestBody.setVisibility(View.VISIBLE); break;
                    case 3 :  requestBody.setVisibility(View.GONE); break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinRequestType.setSelection(0);

        Button btnAddHeader = (Button) findViewById(R.id.btnAddHeader);

        Button submitRequest = (Button) findViewById(R.id.btnSendRequest);


        mHeadersAdapter = new ParametersListAdapter(this,mMainHttpRequest.getHeaders());
        mListViewHeaders = (ListViewAutoGrow) findViewById(R.id.headerslist);
        mListViewHeaders.setAdapter(mHeadersAdapter);




        btnAddHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addHeaders();
            }
        });



        mRequestBody = (EditText) findViewById(R.id.txtRequestBody);
        mUrl         = (EditText) findViewById(R.id.url);

        mLayoutResponse = (LinearLayout) findViewById(R.id.llresponse);
        mTxtResponse = (EditText) findViewById(R.id.txtResponse);
        mTxtResponseHeaders = (EditText) findViewById(R.id.txtResponseHeaders);

        mLayoutResponse.setVisibility(View.GONE);


        submitRequest.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HttpRequestBase sendMethod;

                String url = mUrl.getText().toString();


                if ((url.length() <= 0) || (!url.startsWith("http")))  {
                    Toast.makeText(mContext, "Please specify an http/https URL", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Creating method (GET,POST,PUT,DELETE)
                sendMethod = createMethod(url);

                //Setting up headers
                for (int i=0; i< mHeadersAdapter.getCount(); i++) {
                    BasicNameValuePair tmp = mHeadersAdapter.getItem(i);
                    sendMethod.setHeader(tmp.getName(), tmp.getValue());
                }

                //Setting up the raw data for post/put method
                if (getMethodRequirePostData()) {
                    String rawEntity = mRequestBody.getText().toString();
                    try {
                        ((HttpEntityEnclosingRequest)sendMethod).setEntity(new StringEntity(rawEntity, "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }

                mLayoutResponse.setVisibility(View.GONE);
                mProgress = ProgressDialog.show(mContext,getString(R.string.wait),getString(R.string.sending_request),true);
                requstDuration = System.currentTimeMillis();
                new Submitter().execute(sendMethod);
            }
        });



        int loadRec = getIntent().getIntExtra("LOAD",0);

        if (loadRec > 0) {

            loadRequest(loadRec);
        }
    }







    private boolean getMethodRequirePostData() {
        //POST OR PUT REQUIRE POSTDATA
        return (spinRequestType.getSelectedItemPosition() > 0) && (spinRequestType.getSelectedItemPosition() < 3);

    }

    private HttpRequestBase createMethod(String url) {

        switch (spinRequestType.getSelectedItemPosition()) {
            case 0:
                return new HttpGet(url);
            case 1:
                return new HttpPost(url);
            case 2:
                return new HttpPut(url);
            case 3:
                return new HttpDelete(url);
            default:
                return null;
        }
    }



    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }

    private void addHeaders() {

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        final View fAddHeadersLayout = inflater.inflate(R.layout.dialog_add_header, (ViewGroup) findViewById(R.id.ll_dialog_add_header));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final AutoCompleteTextView hName = (AutoCompleteTextView) fAddHeadersLayout.findViewById(R.id.edit_name);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                new String[] {"Accept","Accept-Charset","Accept-Encoding","Accept-Language",
                              "Authorization","Cache-Control","Content-Type","Date","If-Match","If-None-Match",
                              "If-Range","Max-Forwards","Pragma","Proxy-Authorization","Range",
                              "Upgrade","User-Agent","Via","Warning","X-Do-Not-Track"}
        );
        hName.setAdapter(adapter);

        builder .setTitle("add header")
                .setView(fAddHeadersLayout)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        EditText hValue = (EditText) fAddHeadersLayout.findViewById(R.id.edit_value);
                        BasicNameValuePair p = new BasicNameValuePair(hName.getText().toString(),hValue.getText().toString());
                        mHeadersAdapter.add(p);
                        mHeadersAdapter.notifyDataSetChanged();
                        dialog.dismiss();

                    }
                })

                .setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.crud_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {

            startActivity(new Intent(CrudMain.this,SettingsActivity.class));

            return true;
        }

        if (id == R.id.action_manageRequest) {

            Intent intent = new Intent(CrudMain.this,SavedRequest.class);
            intent.putExtra("MANAGE",true);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_loaRequest) {

            Intent intent = new Intent(CrudMain.this,SavedRequest.class);
            intent.putExtra("MANAGE",false);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_saveRequest) {


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.specify_name));

            final EditText input = new EditText(this);

            Typeface externalFont=Typeface.createFromAsset(getAssets(), "valera_round.otf");


            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setTypeface(externalFont);
            input.setPadding(getResources().getDimensionPixelSize(R.dimen.card_btn_padding),
                    getResources().getDimensionPixelSize(R.dimen.card_btn_padding),
                    getResources().getDimensionPixelSize(R.dimen.card_btn_padding),
                    getResources().getDimensionPixelSize(R.dimen.card_btn_padding));


            if (android.os.Build.VERSION.SDK_INT >= 16) {
                try {
                    input.setBackground(getResources().getDrawable(R.drawable.edit_text_round_corners));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            builder.setView(input);




            builder.setPositiveButton(getResources().getText(R.string.OK), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveRequest(input.getText().toString());
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(getResources().getText(R.string.Cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private HttpClient buildHttpsClientUntrusted(HttpParams params) {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new SSLUntrusted(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);


            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        }
        catch (Exception e) {
            Log.d("Crud",getString(R.string.unable_to_create_ssl_untrusted_connection) +e.getLocalizedMessage() );
            return null;
        }
    }



    private class Submitter extends AsyncTask<HttpRequestBase, Void, HttpResponse> {

        private String fLastError = "";

        @Override
        protected HttpResponse doInBackground(HttpRequestBase... requests) {


            HttpClient httpClient = null;
            try {

                SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(mContext);
                boolean allowUntrusted = p.getBoolean("allow_untrusted", false);
                int timeout = 10;
                try {
                    timeout = Integer.parseInt(p.getString("connection_timeout", "10")) * 1000;
                } catch (Exception e) {

                }

                HttpParams params = new BasicHttpParams();
                HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
                HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
                HttpConnectionParams.setConnectionTimeout(params, timeout);
                HttpConnectionParams.setSoTimeout(params, timeout);


                if (requests[0].getURI().getScheme().startsWith("https") && allowUntrusted) {
                    try {

                        httpClient = buildHttpsClientUntrusted(params);
                    } catch (Exception e) {

                    }
                }

                if (httpClient == null) httpClient = new DefaultHttpClient(params);


                HttpResponse result = null;
                try {
                    result = httpClient.execute(requests[0]);
                } catch (IOException e) {
                    fLastError = e.getLocalizedMessage();
                    e.printStackTrace();

                }
                return result;
            }
            catch (Exception e) {
                Log.d("Crud",getString(R.string.error_while_connecting)+e.getLocalizedMessage());
                return null;
            }

        }

        @Override
        protected void onPostExecute(HttpResponse result) {

            if (result != null) {
                DrawHttpCodeStatus(result.getStatusLine());

                String s = "";
                try {
                    s = EntityUtils.toString(result.getEntity(), "UTF-8");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mTxtResponse.setText(s);
                Header[] aHeaders = result.getAllHeaders();

                String aHead="";
                for (int i=0; i <aHeaders.length; i++) {
                   aHead += aHeaders[i].getName() + "=" + aHeaders[i].getValue() + "\n";
                }
                mTxtResponseHeaders.setText(aHead);

                mLayoutResponse.setVisibility(View.VISIBLE);
            }
            else {
                DrawHttpCodeStatus(null);
                mTxtResponse.setText("");
                mTxtResponseHeaders.setText("");
                mLayoutResponse.setVisibility(View.VISIBLE);
            }

            if (mProgress != null) {
                mProgress.dismiss();
            }
        }

    private void DrawHttpCodeStatus(StatusLine status) {

        ImageView img = (ImageView) findViewById(R.id.httpStatusImg);
        TextView txt = (TextView) findViewById(R.id.httpStatusText);

        String s = "";

        if (status != null) {
            if (status.getStatusCode() < 200) {
                img.setImageDrawable(getResources().getDrawable(R.drawable.info));
            } else if ((status.getStatusCode() >= 200) && (status.getStatusCode() < 300)) {
                img.setImageDrawable(getResources().getDrawable(R.drawable.success));
            } else if ((status.getStatusCode() >= 300) && (status.getStatusCode() < 400)) {
                img.setImageDrawable(getResources().getDrawable(R.drawable.warning));
            }
            if ((status.getStatusCode() >= 400)) {
                img.setImageDrawable(getResources().getDrawable(R.drawable.error));
            }

            long ms = System.currentTimeMillis() - requstDuration;

            s = status.getProtocolVersion() + " " + status.getStatusCode() + " " + status.getReasonPhrase() + " (" + ms + " ms)";


        } else {
            s = getResources().getString(R.string.connection_error) + "\n" + fLastError;
            img.setImageDrawable(getResources().getDrawable(R.drawable.error));
        }
        txt.setText(s);

    }
    }


    private void saveRequest(String name) {


        DBRequestHistory db = new DBRequestHistory(mContext);
        try {

            HttpRequest request = new HttpRequest();



            request.setName(name);
            String url = mUrl.getText().toString();

            if ((url.length() <= 0) || (!url.startsWith("http")))  {
                Toast.makeText(this, "Please specify an http/https URL", Toast.LENGTH_SHORT).show();
                return;
            }

            request.setUrl(url);
            request.setRequestType(spinRequestType.getSelectedItemPosition());
            for (int i = 0; i < mHeadersAdapter.getCount(); i++) {
                request.getHeaders().add(mHeadersAdapter.getItem(i));
            }
            request.setmRequestBody(mRequestBody.getText().toString());

            if (request.getName().length() <= 0) {
              request.setName(request.getRequestTypeStr() + " " + request.getUrl());
            }

            if (db.addRequest(request)) {
                Toast.makeText(this, "Request saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Fail to save request", Toast.LENGTH_SHORT).show();
            }



        }
        finally {
            db.close();
        }

    }

    private void loadRequest(int id) {

        DBRequestHistory db = new DBRequestHistory(this);
        try {

            Log.d("CRUD", "loading request " + id);

            Cursor c = db.getRequestByID(id);


            HttpRequest hR = db.cursorToRequest(c);

            mUrl.setText(hR.getUrl());
            spinRequestType.setSelection(hR.getRequestType());
            mRequestBody.setText(hR.getRequestBody());
            mHeadersAdapter.clear();
            mHeadersAdapter.addAll(hR.getHeaders());
            mHeadersAdapter.notifyDataSetChanged();
        }
        finally {
            db.close();
        }
    }






}
