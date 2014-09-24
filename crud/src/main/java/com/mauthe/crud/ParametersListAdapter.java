package com.mauthe.crud;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.List;

/**
 * Created by Ugo on 21/09/2014.
 */

public class ParametersListAdapter extends ArrayAdapter<BasicNameValuePair> {
    private final Activity context;

    public  ParametersListAdapter(Activity context, List<BasicNameValuePair> param) {
        super(context, R.layout.card_title_description,param);
        this.context = context;
    }



    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View rowView= inflater.inflate(R.layout.card_title_description, null, true);
        NameValuePair p = getItem(position);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.txtCardID);
        txtTitle.setText(p.getName());

        TextView txtDescription = (TextView) rowView.findViewById(R.id.txtCardDescription);
        txtDescription.setText(p.getValue());

        utils.changeFontTypeFace(context,(ViewGroup) rowView);

        Button btnDeleteCard = (Button) rowView.findViewById(R.id.btnDeleteCard);

        btnDeleteCard.setTag(p);

        btnDeleteCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final View aView = view;
                new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(context.getString(R.string.app_name))
                        .setMessage(context.getString(R.string.are_you_sure))
                        .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                              remove((BasicNameValuePair) aView.getTag());
                            }

                        })
                        .setNegativeButton(context.getString(R.string.no), null)
                        .show();
            }
        });

        return rowView;
    }


}
