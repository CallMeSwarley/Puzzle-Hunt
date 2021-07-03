package com.socialgaming.androidtutorial.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.socialgaming.androidtutorial.Models.User;
import com.socialgaming.androidtutorial.R;

import java.util.ArrayList;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.RecyclerItemViewHolder> {
        private ArrayList<User> myList;
        int mLastPosition = 0;

        public LeaderboardAdapter(ArrayList<User> myList) {
            this.myList = myList;
        }
        public RecyclerItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_row, parent, false);
            RecyclerItemViewHolder holder = new RecyclerItemViewHolder(view);
            return holder;
        }
        @Override
        public void onBindViewHolder(RecyclerItemViewHolder holder, final int position) {
            Log.d("onBindViewHoler ", myList.size() + "");
            holder.etPlaceTextView.setText(Integer.toString(position+1));
            holder.etNameTextView.setText(myList.get(position).getName().toString());
            holder.etXPTextView.setText(myList.get(position).getXP().toString());
            holder.crossImage.setImageResource(R.drawable.profile_pic1);
            mLastPosition =position;
        }
        @Override
        public int getItemCount() {
            return(null != myList?myList.size():0);
        }
        public void notifyData(ArrayList<User> myList) {
            Log.d("notifyData ", myList.size() + "");
            this.myList = myList;
            notifyDataSetChanged();
        }
        public class RecyclerItemViewHolder extends RecyclerView.ViewHolder {
            private final TextView etPlaceTextView;
            private final TextView etNameTextView;
            private final TextView etXPTextView;
            private CardView mainLayout;
            public ImageView crossImage;
            public RecyclerItemViewHolder(final View parent) {
                super(parent);
                etPlaceTextView = (TextView) parent.findViewById(R.id.place_textView);
                etNameTextView = (TextView) parent.findViewById(R.id.name_textView2);
                etXPTextView = (TextView) parent.findViewById(R.id.xp_textView2);
                crossImage = (ImageView) parent.findViewById(R.id.user_pic_imageView);
                mainLayout = (CardView) parent.findViewById(R.id.user_CardView);
                /*mainLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(itemView.getContext(), "Position:" + Integer.toString(getPosition()), Toast.LENGTH_SHORT).show();
                    }
                });*/

            }
        }
    }

