package it.pyronaid.brainstorming;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import applications.BrainStormingApplications;
import authenticatorStuff.AccountManagerUtils;
import databaseStuff.BrainStormingSQLiteHelper;
import dialogs.SimpleDialogFragment;
import authenticatorStuff.AccountAuthenticatorActivity;
import authenticatorStuff.AccountGeneral;
import authenticatorStuff.ParseComAnswer;
import validatorStuff.ValidatorInputs;

import static authenticatorStuff.AccountGeneral.sServerAuthenticate;

public class AuthenticatorActivity extends AccountAuthenticatorActivity {
    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

    public final static String KEY_ERROR_DIALOG = "ERR_DIALOG";
    public final static String KEY_ERROR_MESSAGE = "ERR_MSG";
    public final static String KEY_ERROR_TITLE = "ERR_TITLE";
    public final static String ERROR_TRUE = "TRUE";
    public final static String ERROR_FALSE = "FALSE";

    public final static String PARAM_USER_PASS = "USER_PASS";

    private final int REQ_SIGNUP = 1;

    private final String TAG = this.getClass().getSimpleName();

    private AccountManager mAccountManager;
    private String mAuthTokenType;
    private TextView accountNameTextView;
    private TextView accountPasswordTextView;
    private Button logInButton;
    private TextView signUpTextView;

    @Inject
    ValidatorInputs validatorInputs;

    @Inject
    BrainStormingSQLiteHelper brainStormingSQLiteHelper;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_authenticator);

        mAccountManager = AccountManager.get(this.getApplicationContext());

        String accountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);
        mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);
        if (mAuthTokenType == null) {
            mAuthTokenType = AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;
        }
        if (accountName != null) {
            ((TextView)findViewById(R.id.accountName)).setText(accountName);
        }


        accountNameTextView = (TextView) findViewById(R.id.accountName);
        accountPasswordTextView = (TextView) findViewById(R.id.accountPassword);
        logInButton = (Button) findViewById(R.id.submit);
        signUpTextView = (TextView) findViewById(R.id.signUp);

        ((BrainStormingApplications)getApplication()).getUserComponent().inject(this);

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validatorInputs.isNetworkAvaliable(AuthenticatorActivity.this)) {
                    if (validatorInputs.validateInputLogIn(AuthenticatorActivity.this)) {
                        submit();
                    }
                } else {
                    FragmentManager fm = getFragmentManager();
                    SimpleDialogFragment simpleDialogFragment = SimpleDialogFragment.newInstance("No connection available", "Please check your connection before go on");
                    //Show DialogFragment
                    simpleDialogFragment.show(fm, "No connection available");
                }
            }
        });

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Since there can only be one AuthenticatorActivity, we call the sign up activity, get his results,
                // and return them in setAccountAuthenticatorResult(). See finishLogin().
                Intent signup = new Intent(getApplicationContext(), SignUpActivity.class);
                if(getIntent().getExtras() != null) {
                    signup.putExtras(getIntent().getExtras());
                } else {

                    signup.putExtra(ARG_ACCOUNT_TYPE, savedInstanceState.getString(ARG_ACCOUNT_TYPE));
                    signup.putExtra(ARG_AUTH_TYPE, savedInstanceState.getString(ARG_AUTH_TYPE));
                    signup.putExtra(ARG_IS_ADDING_NEW_ACCOUNT, savedInstanceState.getString(ARG_IS_ADDING_NEW_ACCOUNT));
                    signup.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, savedInstanceState.getString(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE));

                }
                startActivityForResult(signup, REQ_SIGNUP);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(ARG_ACCOUNT_TYPE, getIntent().getStringExtra(ARG_ACCOUNT_TYPE));
        savedInstanceState.putString(ARG_AUTH_TYPE, getIntent().getStringExtra(ARG_AUTH_TYPE));
        savedInstanceState.putString(ARG_IS_ADDING_NEW_ACCOUNT, getIntent().getStringExtra(ARG_IS_ADDING_NEW_ACCOUNT));
        savedInstanceState.putString(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, getIntent().getStringExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE));

        super.onSaveInstanceState(savedInstanceState);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // The sign up activity returned that the user has successfully created an account
        if (requestCode == REQ_SIGNUP && resultCode == RESULT_OK) {
            finishLogin(data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public void submit() {
        new AsyncTask<String, Void, Intent>() {
            private String userName = accountNameTextView.getText().toString().trim();
            private String userPass = accountPasswordTextView.getText().toString().trim();
            private String accountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);
            private ProgressDialog dialog = new ProgressDialog(AuthenticatorActivity.this, R.style.AppTheme_Dark_Dialog);

            @Override
            protected void onPreExecute(){
                dialog.setMessage("Login ongoing, please wait.");
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                dialog.show();
                logInButton.setEnabled(false);
                signUpTextView.setEnabled(false);
            }

            @Override
            protected Intent doInBackground(String... params) {

                Log.d("Brainstorming", TAG + "> Started authenticating");

                String authtoken = null;
                ParseComAnswer answer;
                Bundle data = new Bundle();
                try {
                    answer = sServerAuthenticate.userSignIn(userName, userPass, mAuthTokenType);

                    if(answer.getError().toUpperCase().equals(ERROR_FALSE)) {
                        SystemClock.sleep(2000);
                        authtoken = answer.getUser().getAuthToken();
                        data.putString(AccountManager.KEY_ACCOUNT_NAME, userName);
                        data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                        data.putString(AccountManager.KEY_AUTHTOKEN, authtoken);
                        data.putString(PARAM_USER_PASS, userPass);
                    } else {
                        data.putString(KEY_ERROR_DIALOG, answer.getError());
                        data.putString(KEY_ERROR_TITLE, answer.getTitle());
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
                logInButton.setEnabled(true);
                signUpTextView.setEnabled(true);
                if(intent.hasExtra(KEY_ERROR_DIALOG)){
                    FragmentManager fm = getFragmentManager();
                    SimpleDialogFragment simpleDialogFragment = SimpleDialogFragment.newInstance(intent.getStringExtra(KEY_ERROR_TITLE), intent.getStringExtra(KEY_ERROR_MESSAGE));
                    //Show DialogFragment
                    simpleDialogFragment.show(fm, intent.getStringExtra(KEY_ERROR_TITLE));
                }
                else if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
                    Toast.makeText(getBaseContext(), intent.getStringExtra(KEY_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
                } else {
                    finishLogin(intent);
                }
            }
        }.execute();
    }

    private void finishLogin(Intent intent) {
        Log.d("Brainstorming", TAG + "> finishLogin");

        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
        final Account account = new Account(accountName, AccountGeneral.ACCOUNT_TYPE);

        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            Log.d("Brainstorming", TAG + "> finishLogin > addAccountExplicitly");
            String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String authtokenType = intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);

            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            mAccountManager.addAccountExplicitly(account, accountPassword, null);
            mAccountManager.setAuthToken(account, authtokenType, authtoken);
        } else {
            Log.d("Brainstorming", TAG + "> finishLogin > setPassword");
            mAccountManager.setPassword(account, accountPassword);
        }


        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }


    @Override
    public void onBackPressed() {
        Intent result = new Intent();
        Bundle b = new Bundle();
        result.putExtras(b);

        setAccountAuthenticatorResult(null); // null means the user cancelled the authorization processs
        setResult(RESULT_OK, result);
        finish();
    }

    public TextView getAccountPasswordTextView() {
        return accountPasswordTextView;
    }

    public TextView getAccountNameTextView() {
        return accountNameTextView;
    }
}
