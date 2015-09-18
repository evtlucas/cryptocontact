package br.unisinos.evertonlucas.passshelter.view;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

import br.unisinos.evertonlucas.passshelter.data.ParseData;

/**
 * Created by everton on 14/09/15.
 */
public class AutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {

    private List<String> emails;

    public AutoCompleteAdapter(Context context, int resource) {
        super(context, resource);
        emails = new ArrayList<String>();
    }

    @Override
    public int getCount() {
        return emails.size();
    }

    @Override
    public String getItem(int position) {
        return emails.get(position);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    ParseData parseData = new ParseData();
                    //parseData.getEMailUsers(constraint.toString());
                    filterResults.values = emails;
                    filterResults.count = getCount();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0)
                    notifyDataSetChanged();
                else
                    notifyDataSetInvalidated();
            }
        };
        return filter;
    }
}
