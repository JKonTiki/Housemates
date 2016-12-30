package com.jeremyfryd.housemates.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeremyfryd.housemates.R;
import com.jeremyfryd.housemates.models.Roommate;

import java.util.List;

/**
 * Created by jeremy on 12/22/16.
 */

public class InhabitantListAdapter extends ArrayAdapter<Roommate> {
    LayoutInflater mInflater;
    List<Roommate> mRoommates;
    public InhabitantListAdapter(Context context, List<Roommate> roommates) {
        super(context, 0, roommates);
        mRoommates = roommates;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        InhabitantViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.from(getContext()).inflate(R.layout.roommate_list_item, parent, false);
            holder = new InhabitantViewHolder();
            convertView.setTag(holder);
        }
        else {
            holder = (InhabitantViewHolder) convertView.getTag();
        }

        Roommate roommate = mRoommates.get(position);

        holder.mRoommateName = (TextView) convertView.findViewById(R.id.roommateName);
        holder.mRoommateName.setText(roommate.getName());
        if (holder.mHomeStatus!=null){
            holder.mHomeStatus.setVisibility(View.GONE);
        }
        if (roommate.getAtHome().equals("unavailable")){
            holder.mHomeStatus = (ImageView) convertView.findViewById(R.id.isHomeIconUnavailable);
        } else if(roommate.getAtHome().equals("false")){
            holder.mHomeStatus = (ImageView) convertView.findViewById(R.id.isHomeIconFalse);
        } else if(roommate.getAtHome().equals("true")){
            holder.mHomeStatus = (ImageView) convertView.findViewById(R.id.isHomeIconTrue);
        }
        holder.mHomeStatus.setVisibility(View.VISIBLE);

        return convertView;
    }
}