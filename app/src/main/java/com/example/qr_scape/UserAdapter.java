package com.example.qr_scape;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class UserAdapter extends ArrayAdapter<User> {

    private ArrayList<User> users;
    private Context context;

    public UserAdapter(Context context, ArrayList<User> users) {
        super(context, 0, users);
        this.users = users;
        this.context = context;
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.player_list_content, parent, false);
        }

        User user = users.get(position);

        TextView userName = view.findViewById(R.id.playerlist_profile_id);
        userName.setText(user.getName());

        return view;
    }
}
