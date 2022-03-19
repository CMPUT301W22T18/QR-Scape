/*
 * UserAdapter
 *
 * March 18 2022
 *
 * Version 1
 *
 * Copyright 2022 Dallin Dmytryk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

/**
 * UserAdapter
 * Class to adapt User objects to a Listview
 * @author Dallin Dmytryk
 * @version 1
 */
public class UserAdapter extends ArrayAdapter<User> {

    private ArrayList<User> users;
    private Context context;

    public UserAdapter(Context context, ArrayList<User> users) {
        super(context, 0, users);
        this.users = users;
        this.context = context;
    }

    /**
     * Gets view, inflates listview with player_list_content
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
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
