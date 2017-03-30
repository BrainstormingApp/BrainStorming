package it.pyronaid.brainstorming;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import javax.inject.Inject;

import adapter.AccountInformationAdapter;
import applications.BrainStormingApplications;
import asynctask.CheckAuthTokenTask;
import asynctask.UpdateFieldTask;
import authenticatorStuff.AccountManagerUtils;
import databaseStuff.BrainStormingSQLiteHelper;
import dialogs.SimpleDialogFragment;

public class ServicesViewActivity extends AppCompatActivity {
    EditText editText;
    String originalValue;
    String originalType;
    String tableName;
    Button cancel;
    Button edit;

    @Inject
    BrainStormingSQLiteHelper brainStormingSQLiteHelper;

    @Inject
    AccountManagerUtils accountManagerUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        editText = (EditText) findViewById(R.id.editText);
        cancel = (Button) findViewById(R.id.button_cancel);
        edit = (Button) findViewById(R.id.button_edit_ok);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        String value = intent.getStringExtra(AccountInformationAdapter.KEY_INTENT_FOR_VALUE);
        originalValue = value;
        originalType = intent.getStringExtra(AccountInformationAdapter.KEY_INTENT_FOR_TYPE);
        tableName = intent.getStringExtra(AccountInformationAdapter.KEY_INTENT_FOR_TABLE_NAME);
        editText.setText(value);

        ((BrainStormingApplications)getApplication()).getUserComponent().inject(this);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(ServicesViewActivity.this);
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                complete_edit();
            }
        });
    }

    private void complete_edit() {
        String newValue = editText.getText().toString();
        //add validator check
        if(!newValue.equals(originalValue)){
            if(originalType != null){
                Button[] buttons = new Button[]{cancel,edit};
                new UpdateFieldTask(this, brainStormingSQLiteHelper, buttons).execute(new String[]{tableName, originalType, newValue});

            } else {
                FragmentManager fm = getFragmentManager();
                SimpleDialogFragment simpleDialogFragment = SimpleDialogFragment.newInstance("Error in changing type", "type mismatch");
                //Show DialogFragment
                simpleDialogFragment.show(fm , "Error in changing type");
            }

        } else {
            FragmentManager fm = getFragmentManager();
            SimpleDialogFragment simpleDialogFragment = SimpleDialogFragment.newInstance("No change", "No change to execute");
            //Show DialogFragment
            simpleDialogFragment.show(fm , "No change");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        new CheckAuthTokenTask(this).execute(accountManagerUtils);
        super.onStart();
    }
}
