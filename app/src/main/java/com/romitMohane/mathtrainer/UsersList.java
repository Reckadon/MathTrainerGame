package com.romitMohane.mathtrainer;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


public class UsersList extends ArrayAdapter<User> {
    private Activity context;
    List<User> usersList;

    public UsersList(Activity context, List<User> usersList) {
        super(context, R.layout.leaderboard_layout, usersList);
        this.context = context;
        this.usersList = usersList;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.leaderboard_layout, null, true);

        TextView tvName = listViewItem.findViewById(R.id.tvName);
        TextView tvEmail = listViewItem.findViewById(R.id.tvEmail);
        TextView tvScore= listViewItem.findViewById(R.id.score);
        TextView tvTotal=listViewItem.findViewById(R.id.total);
        TextView tvPos=listViewItem.findViewById(R.id.tvPos);

        User user = usersList.get(position);
        tvName.setText(user.getUsername());
        tvEmail.setText(user.getEmail());
        tvScore.setText(user.getMax());
        tvTotal.setText(user.getTotal());
        tvPos.setText(String.valueOf(position+1));

        return listViewItem;
    }
}