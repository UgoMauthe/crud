package com.mauthe.crud;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.mauthe.crud.db.DBRequestHistory;


public class SavedRequest extends Activity {

    ListView mListView;
    ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_request);
        mListView = (ListView) findViewById(R.id.lv_savedRequest);




        final boolean manage = getIntent().getBooleanExtra("MANAGE",true);

        DBRequestHistory db = new DBRequestHistory(this);
        try {

            Cursor c = db.getAllRequests();

            String[] from = {"reqTypeStr", "reqName", "reqUrl"};

            int[] to = {R.id.tvRequestType, R.id.tvRequestName, R.id.tvRequestUrl};


            adapter = new SimpleCursorAdapter(this, R.layout.saved_request_item, c, from, to) {
                @Override
                public void bindView(View view, Context context, Cursor cursor) {

                    super.bindView(view, context, cursor);



                    Button btnDelete = (Button) view.findViewById(R.id.btnDeleteRequest);

                    if (!manage) btnDelete.setVisibility(View.GONE);

                    final int delID = cursor.getInt(0);

                    TextView aTV = (TextView) view.findViewById(R.id.tvRequestType);

                    Integer iObj = new Integer(delID);

                    view.setTag(iObj);

                    switch (cursor.getInt(3)) {
                        case 1:
                            aTV.setBackgroundColor(getResources().getColor(R.color.green1));
                            break;
                        case 2:
                            aTV.setBackgroundColor(getResources().getColor(R.color.orange4));
                            break;
                        case 3:
                            aTV.setBackgroundColor(getResources().getColor(R.color.red1));
                            break;
                        default:
                            aTV.setBackgroundColor(getResources().getColor(R.color.blue25));
                            break;
                    }
                    SimpleCursorAdapter p = this;


                    btnDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DBRequestHistory db = new DBRequestHistory(view.getContext());
                            try {
                                db.deleteRequestById(delID);

                                changeCursor(db.getAllRequests());
                                notifyDataSetChanged();
                            } finally {
                                db.close();

                            }


                        }
                    });

                    utils.changeFontTypeFace(view.getContext(),(ViewGroup) view);


                }
            };


            mListView.setAdapter(adapter);

            if (!manage) {
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Integer iObj = (Integer) view.getTag();
                        Intent intent = new Intent(SavedRequest.this, CrudMain.class);
                        intent.putExtra("LOAD", iObj.intValue());
                        startActivity(intent);
                    }
                });
            }


        }
        finally
        {
            db.close();

        }





    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.saved_request, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
