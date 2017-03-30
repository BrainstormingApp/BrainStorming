package adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import databaseStuff.BrainStormingSQLiteHelper;
import it.pyronaid.brainstorming.R;
import it.pyronaid.brainstorming.ServicesViewActivity;

public class AccountInformationAdapter extends RecyclerView.Adapter<AccountInformationAdapter.ViewHolder> {
    public static final String KEY_INTENT_FOR_VALUE = "Value";
    public static final String KEY_INTENT_FOR_TYPE = "Type";
    public static final String KEY_INTENT_FOR_TABLE_NAME = "TableName";
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
        final String referTo = mListKeys.get(position);
        final String valueOfView = mMapOfCursor.get(referTo);

        holder.value.setText(valueOfView);
        holder.label.setText(WordUtils.capitalize(referTo));
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(mContext, ServicesViewActivity.class);
                i.putExtra(KEY_INTENT_FOR_VALUE, valueOfView);
                i.putExtra(KEY_INTENT_FOR_TYPE, referTo);
                i.putExtra(KEY_INTENT_FOR_TABLE_NAME, BrainStormingSQLiteHelper.TABLE_ACCOUNT_NAME);
                mContext.startActivity(i);
            }
        });
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