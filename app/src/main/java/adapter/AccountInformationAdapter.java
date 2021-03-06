package adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v4.app.Fragment;
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

import databaseStuff.BrainStormingSQLiteHelper;
import intentStuff.RequestCodeGeneral;
import it.pyronaid.brainstorming.R;
import it.pyronaid.brainstorming.ServicesViewDatePickerActivity;
import it.pyronaid.brainstorming.ServicesViewEditTextActivity;

public class AccountInformationAdapter extends RecyclerView.Adapter<AccountInformationAdapter.ViewHolder> {
    private Context mContext;
    private Fragment fragment;
    private Cursor mCursor;
    private Map<String,String> mMapOfCursor;
    private List<String> mListKeys;
    private DataSetObserver mDataSetObserver;

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

    public AccountInformationAdapter(Fragment fragment, Cursor cursor) {
        this.mContext = fragment.getActivity();
        this.mCursor = cursor;
        this.fragment = fragment;
        mDataSetObserver = new NotifyingDataSetObserver();

        mMapOfCursor = new HashMap<String, String>();
        mListKeys = new ArrayList<String>();

        //for(mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
        //    mMapOfCursor.put(mCursor.getColumnName(mCursor.getPosition()), mCursor.getString(mCursor.getPosition()));
        //    mListKeys.add(mCursor.getColumnName(mCursor.getPosition()));
        //}
        if(mCursor != null) {
            mCursor.registerDataSetObserver(mDataSetObserver);
            mCursor.moveToFirst();
            for (int i = 0; i < mCursor.getColumnNames().length; i++) {
                mMapOfCursor.put(mCursor.getColumnName(i), mCursor.getString(i));
                mListKeys.add(mCursor.getColumnName(i));
            }
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

        switch (classifyTypeOfEdit(referTo)) {
            case 1:
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(mContext, ServicesViewEditTextActivity.class);
                        i.putExtra(RequestCodeGeneral.KEY_INTENT_FOR_VALUE, valueOfView);
                        i.putExtra(RequestCodeGeneral.KEY_INTENT_FOR_TYPE, referTo);
                        i.putExtra(RequestCodeGeneral.KEY_INTENT_FOR_TABLE_NAME, BrainStormingSQLiteHelper.TABLE_ACCOUNT_NAME);
                        fragment.startActivityForResult(i, RequestCodeGeneral.REQ_EDIT);
                    }
                });
                break;
            case 2:
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(mContext, ServicesViewDatePickerActivity.class);
                        i.putExtra(RequestCodeGeneral.KEY_INTENT_FOR_VALUE, valueOfView);
                        i.putExtra(RequestCodeGeneral.KEY_INTENT_FOR_TYPE, referTo);
                        i.putExtra(RequestCodeGeneral.KEY_INTENT_FOR_TABLE_NAME, BrainStormingSQLiteHelper.TABLE_ACCOUNT_NAME);
                        fragment.startActivityForResult(i, RequestCodeGeneral.REQ_EDIT);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        if(mCursor != null) {
            mCursor.moveToFirst();
            return mCursor.getColumnNames().length;
        }
        return 0;
    }

    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        final Cursor oldCursor = mCursor;
        if (oldCursor != null && mDataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        mCursor = newCursor;
        if (mCursor != null) {
            if (mDataSetObserver != null) {
                mCursor.registerDataSetObserver(mDataSetObserver);
            }

            mMapOfCursor.clear();
            mListKeys.clear();
            mCursor.moveToFirst();
            for (int i = 0; i < mCursor.getColumnNames().length; i++) {
                mMapOfCursor.put(mCursor.getColumnName(i), mCursor.getString(i));
                mListKeys.add(mCursor.getColumnName(i));
            }

        }

        notifyDataSetChanged();
        //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        return oldCursor;
    }

    private class NotifyingDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
    }


    private int classifyTypeOfEdit(String type){
        if(type.toLowerCase().equals(BrainStormingSQLiteHelper.COLUMN_BIRTHDAY)){
            return 2;
        }

        return 1;
    }

}