package com.smartparking.amit.parksmart;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<LevelParams> levelParams;
    private Activity activity;

    public RecyclerAdapter(Activity activity, List<LevelParams> levelParams) {
        this.levelParams = levelParams;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        //inflate your layout and pass it to view holder
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.item_recycler, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder viewHolder, int position) {

        //setting data to view holder elements
        viewHolder.level_number.setText("Level " + position);
        viewHolder.total_slots.setText("Hola Amigos!");


        //set on click listener for each element
        viewHolder.container.setOnClickListener(onClickListener(position));
    }

   /* private void setDataToView(TextView level_number, TextView total_slots, int position) {
        level_number.setText(levelParams.get(position).getRows());
        total_slots.setText(levelParams.get(position).getColumns());
    }
*/
    @Override
    public int getItemCount() {
        return (null != levelParams ? levelParams.size() : 0);
    }

    private View.OnClickListener onClickListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Context context = v.getContext();

                Intent intent = new Intent(context,SlotViewingActivity.class);
                intent.putExtra("MyPosition", position);
                context.startActivity(intent);


                /*MyViewFragment myViewFragment = new MyViewFragment();
                Context context = v.getContext();
                Activity activity = (Activity) context;
                FragmentManager fragmentManager = activity.getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.mainId,myViewFragment);*/


                /*  Intent intent = new Intent();
                intent.putExtra("MyPosition",position);*/

                /*final Dialog dialog = new Dialog(activity);
                dialog.setContentView(R.layout.item_recycler);
                dialog.setTitle("Position " + position);
                dialog.setCancelable(true); // dismiss when touching outside Dialog

                set the custom dialog components - texts and image
                TextView level_number = dialog.findViewById(R.id.level_number);
                TextView total_slots = dialog.findViewById(R.id.total_slots);

                setDataToView(level_number,total_slots, position);

                dialog.show();*/
            }
        };
    }

    /**
     * View holder to display each RecylerView item
     */
    protected class ViewHolder extends RecyclerView.ViewHolder {
        private TextView level_number;
        private TextView total_slots;
        private View container;

        public ViewHolder(View view) {
            super(view);
            level_number = view.findViewById(R.id.level_number);
            total_slots = view.findViewById(R.id.total_slots);
            container = view.findViewById(R.id.card_view);
        }
    }
}