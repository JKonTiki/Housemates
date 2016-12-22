package com.jeremyfryd.housemates.adapters;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;
import com.jeremyfryd.housemates.R;
import com.jeremyfryd.housemates.models.Roommate;

/**
 * Created by jeremy on 12/21/16.
 */

public class InhabitantViewHolder {
    View mView;
    public TextView mRoommateName;
    public ImageView mHomeStatus;


//    public void bindRoommate(Roommate roommate) {
//        TextView roommateName = (TextView) mView.findViewById(R.id.roommateName);
//        roommateName.setText(roommate.getName());
//        if (roommate.isHome().equals("unavailable")){
//            mHomeStatus = (ImageView) mView.findViewById(R.id.isHomeIconUnavailable);
//        } else if(roommate.isHome().equals("false")){
//            mHomeStatus = (ImageView) mView.findViewById(R.id.isHomeIconFalse);
//        } else if(roommate.isHome().equals("true")){
//            mHomeStatus = (ImageView) mView.findViewById(R.id.isHomeIconTrue);
//        }
//    }
//



}
