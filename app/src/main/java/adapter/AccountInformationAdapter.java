package adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import it.pyronaid.brainstorming.R;

/**
 * Created by pyronaid on 07/03/2017.
 */
public class AccountInformationAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] names;

    static class ViewHolder {
        public TextView text;
        public TextView label;
    }

    public AccountInformationAdapter(Activity context, String[] names) {
        super(context, R.layout.fragment_my_categories, names);
        this.context = context;
        this.names = names;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.fragment_my_categories, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) rowView.findViewById(R.id.text_view_home_frag);
            viewHolder.label = (TextView) rowView.findViewById(R.id.text_view_home_frag);
            rowView.setTag(viewHolder);
        }

        // fill data
        //ViewHolder holder = (ViewHolder) rowView.getTag();
        //String s = names[position];
        //holder.text.setText(s);
        //if (s.startsWith("Windows7") || s.startsWith("iPhone")
        //        || s.startsWith("Solaris")) {
        //    holder.image.setImageResource(R.drawable.no);
        //} else {
        //    holder.image.setImageResource(R.drawable.ok);
        //}

        return rowView;
    }
}
