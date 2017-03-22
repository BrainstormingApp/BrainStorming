package it.pyronaid.brainstorming;

import android.accounts.AccountManager;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import applications.BrainStormingApplications;
import dialogs.SimpleDialogFragment;
import authenticatorStuff.AccountGeneral;
import authenticatorStuff.ParseComAnswer;
import validatorStuff.ValidatorInputs;

import static it.pyronaid.brainstorming.AuthenticatorActivity.ARG_ACCOUNT_TYPE;
import static it.pyronaid.brainstorming.AuthenticatorActivity.PARAM_USER_PASS;
import static it.pyronaid.brainstorming.AuthenticatorActivity.KEY_ERROR_MESSAGE;
import static authenticatorStuff.AccountGeneral.sServerAuthenticate;

public class SignUpActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();
    private String mAccountType;

    TextView nameTextView;
    TextView accountNameTextView;
    TextView accountPasswordTextView;
    Button signUpButton;
    TextView logInTextView;

    @Inject
    ValidatorInputs validatorInputs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAccountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);

        setContentView(R.layout.activity_sign_up);

        nameTextView = (TextView) findViewById(R.id.name);
        accountNameTextView = (TextView) findViewById(R.id.accountName);
        accountPasswordTextView = (TextView) findViewById(R.id.accountPassword);
        signUpButton = (Button) findViewById(R.id.submit);
        logInTextView = (TextView) findViewById(R.id.alreadyMember);

        ((BrainStormingApplications)getApplication()).getUserComponent().inject(this);

        logInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validatorInputs.isNetworkAvaliable(SignUpActivity.this)) {
                    if (validatorInputs.validateInputSignUp(SignUpActivity.this)) {
                        createAccount();
                    }
                } else {
                    FragmentManager fm = getFragmentManager();
                    SimpleDialogFragment simpleDialogFragment = SimpleDialogFragment.newInstance("No connection available", "Please check your connection before go on");
                    //Show DialogFragment
                    simpleDialogFragment.show(fm, "No connection available");
                }
            }
        });
    }


    private void createAccount() {

        // Validation!

        new AsyncTask<String, Void, Intent>() {

            private String name = nameTextView.getText().toString().trim();
            private String accountName = accountNameTextView.getText().toString().trim();
            private String accountPassword = accountPasswordTextView.getText().toString().trim();

            private ProgressDialog dialog = new ProgressDialog(SignUpActivity.this, R.style.AppTheme_Dark_Dialog);

            @Override
            protected void onPreExecute(){
                dialog.setMessage("Signup ongoing, please wait.");
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                dialog.show();
                signUpButton.setEnabled(false);
                logInTextView.setEnabled(false);
            }

            @Override
            protected Intent doInBackground(String... params) {

                Log.d("Brainstorming", TAG + "> Started authenticating");

                ParseComAnswer answer;
                String authtoken = null;
                String authType = null;
                String mAuthTokenType;
                mAuthTokenType = getIntent().getStringExtra(AuthenticatorActivity.ARG_AUTH_TYPE);
                if (mAuthTokenType == null) {
                    mAuthTokenType = AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;
                }
                Bundle data = new Bundle();
                try {
                    answer = sServerAuthenticate.userSignUp(name, accountName, accountPassword, mAuthTokenType);

                    if(answer.getError().toUpperCase().equals(AuthenticatorActivity.ERROR_FALSE)) {
                        SystemClock.sleep(2000);
                        authtoken = answer.getUser().getAuthToken();
                        authType = answer.getUser().getAuthType();
                        data.putString(AccountManager.KEY_ACCOUNT_NAME, accountName);
                        data.putString(AccountManager.KEY_ACCOUNT_TYPE, authType);
                        data.putString(AccountManager.KEY_AUTHTOKEN, authtoken);
                        //data.putBoolean(AuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
                        data.putString(PARAM_USER_PASS, accountPassword);
                    } else {
                        data.putString(AuthenticatorActivity.KEY_ERROR_DIALOG, answer.getError());
                        data.putString(AuthenticatorActivity.KEY_ERROR_TITLE, answer.getTitle());
                        data.putString(KEY_ERROR_MESSAGE, answer.getError_msg());
                    }

                } catch (Exception e) {
                    data.putString(KEY_ERROR_MESSAGE, e.getMessage());
                }

                final Intent res = new Intent();
                res.putExtras(data);
                return res;
            }

            @Override
            protected void onPostExecute(Intent intent) {
                if (dialog.isShowing()){ dialog.dismiss(); }
                signUpButton.setEnabled(true);
                logInTextView.setEnabled(true);
                if(intent.hasExtra(AuthenticatorActivity.KEY_ERROR_DIALOG)){
                    FragmentManager fm = getFragmentManager();
                    SimpleDialogFragment simpleDialogFragment = SimpleDialogFragment.newInstance(intent.getStringExtra(AuthenticatorActivity.KEY_ERROR_TITLE), intent.getStringExtra(KEY_ERROR_MESSAGE));
                    //Show DialogFragment
                    simpleDialogFragment.show(fm, intent.getStringExtra(AuthenticatorActivity.KEY_ERROR_TITLE));
                } else if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
                    Toast.makeText(getBaseContext(), intent.getStringExtra(KEY_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
                } else {
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        }.execute();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    public TextView getAccountNameTextView() {
        return accountNameTextView;
    }

    public TextView getAccountPasswordTextView() {
        return accountPasswordTextView;
    }

    public TextView getNameTextView() {
        return nameTextView;
    }
}
