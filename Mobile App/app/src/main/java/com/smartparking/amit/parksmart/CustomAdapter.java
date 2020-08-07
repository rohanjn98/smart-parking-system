package com.smartparking.amit.parksmart;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends ArrayAdapter<customHistory>{

        public CustomAdapter(Context context, ArrayList<customHistory> words) {
            super(context, 0, words);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Check if an existing view is being reused, otherwise inflate the view
            View listItemView = convertView;
            if (listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(
                        R.layout.item, parent, false);
            }

            customHistory currentHistory = getItem(position);
            TextView sysName =  listItemView.findViewById(R.id.Name);
            sysName.setText(currentHistory.getmSystemName());
            TextView date = listItemView.findViewById(R.id.MyDate);
            date.setText(currentHistory.getmDate());
            TextView bill = listItemView.findViewById(R.id.Bill);
            bill.setText( Double.toString(currentHistory.getmBill()));
            TextView status = listItemView.findViewById(R.id.Status);
            status.setText(

                    currentHistory.getstatus());
            return listItemView;
        }

}


