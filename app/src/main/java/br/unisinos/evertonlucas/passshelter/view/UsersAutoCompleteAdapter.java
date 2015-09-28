/*
Copyright 2015 Everton Luiz de Resende Lucas

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package br.unisinos.evertonlucas.passshelter.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.unisinos.evertonlucas.passshelter.R;
import br.unisinos.evertonlucas.passshelter.data.ParseData;
import br.unisinos.evertonlucas.passshelter.model.ParseUser;

/**
 * Adapter for DelayAutoCompleteTextView
 * Created by everton on 26/09/15.
 */
public class UsersAutoCompleteAdapter extends BaseAdapter implements Filterable {

    private static final int MAX_RESULTS = 7;
    private Context context;
    private List<ParseUser> parseUsers = new ArrayList<>();

    public UsersAutoCompleteAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return parseUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return parseUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.simple_dropdown_item_1line, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.text1)).setText(((ParseUser)getItem(position)).getEmail() );
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    List<ParseUser> users = findUsers(constraint.toString());
                    filterResults.values = users;
                    filterResults.count = users.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    if (results.values instanceof List) {
                        parseUsers = (List<ParseUser>) results.values;
                        notifyDataSetChanged();
                    }
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    private List<ParseUser> findUsers(String s) {
        ParseData parseData = new ParseData();
        try {
            return parseData.getExternalUsers(s);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
