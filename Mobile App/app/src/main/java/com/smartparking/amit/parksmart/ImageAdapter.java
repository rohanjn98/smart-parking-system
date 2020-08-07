package com.smartparking.amit.parksmart;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;

  //  private int mRows,mColumns;
    private ArrayList<Integer> mThumbIds;


    public ImageAdapter(Context c, ArrayList<Integer> mThumbIds) {
        this.mContext = c;
        this.mThumbIds = mThumbIds;
       /* this.mRows = mRows;
        this.mColumns = mColumns;*/

    }
  //  private int[] mThumbIds = new int[mRows + mColumns];


/*
    public void setGridSize(int x, int y){
        this.mRows = x;
        this.mColumns = y;
    }

    public ImageAdapter(Context c) {
        this.mContext = c;
    }
*/
    public int getCount() {
        Log.d("Length","Length of array " + mThumbIds.size() );
        return mThumbIds.size();

    }

    public Object getItem(int position) {
        return mThumbIds.get(position);
    }

    public long getItemId(int position) {
        return position;
    }



    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
       // ImageView imageView;
        /*for(int j = 0; j< mRows + mColumns;j++){
            mThumbIds.[j] = R.drawable.white;
            Log.d("Value of j", "j = " + j);
        }*/
       // Log.d("Message", "rows " + mRows + "columns " + mColumns);


        Log.d("Hello getView", "Called!");

        if (convertView == null) {


            // if it's not recycled, initialize some attributes
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.custom_image_layout, null);

            ImageView myImageView = convertView.findViewById(R.id.myImage);
            myImageView.setImageResource(mThumbIds.get(position));
           // myImageView.setImageResource(mThumbIds[0]);
            /*imageView = new ImageView(mContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(10, 10));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }*/
        }

        return convertView;
    }

    // references to our images

    /*private Integer[] mThumbIds = {

             R.drawable.white,R.drawable.grey
            // R.drawable.sample_2, R.drawable.sample_3,


    };*/

}
