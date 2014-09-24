package com.mauthe.crud.comp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

/**
 * Created by Ugo on 21/09/2014.
 */
public class ListViewAutoGrow extends ListView {


        public ListViewAutoGrow(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public ListViewAutoGrow  (Context context) {
            super(context);
        }

        public ListViewAutoGrow  (Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                    View.MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
        }

}
