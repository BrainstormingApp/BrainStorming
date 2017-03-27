package adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.pyronaid.brainstorming.R;

public class AccountInformationAdapter extends RecyclerView.Adapter<AccountInformationAdapter.ViewHolder> {
    private Context mContext;
    private Cursor mCursor;
    private Map<String,String> mMapOfCursor;
    private List<String> mListKeys;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView value;
        public TextView label;

        public ViewHolder(View v)
        {
            super(v);
            label= (TextView) v.findViewById(R.id.label_of_row_detail);
            value= (TextView) v.findViewById(R.id.value_of_row_detail);
        }
    }

    public AccountInformationAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;

        mMapOfCursor = new HashMap<String, String>();
        mListKeys = new ArrayList<String>();

        //for(mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
        //    mMapOfCursor.put(mCursor.getColumnName(mCursor.getPosition()), mCursor.getString(mCursor.getPosition()));
        //    mListKeys.add(mCursor.getColumnName(mCursor.getPosition()));
        //}
        mCursor.moveToFirst();
        for(int i=0; i < mCursor.getColumnNames().length; i++){
            mMapOfCursor.put(mCursor.getColumnName(i), mCursor.getString(i));
            mListKeys.add(mCursor.getColumnName(i));
        }

    }

    @Override
    public AccountInformationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.label_value_row, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        if(mCursor == null){
            throw new IllegalStateException("couldn't move cursor to position 0");
        }
        String referTo = mListKeys.get(position);

        holder.value.setText(mMapOfCursor.get(referTo));
        holder.label.setText(WordUtils.capitalize(referTo));
    }

    @Override
    public int getItemCount() {
        if(mCursor != null) {
            mCursor.moveToFirst();
            return mCursor.getColumnNames().length;
        }
        return 0;
    }

}