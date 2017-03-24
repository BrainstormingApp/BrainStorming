package authenticatorStuff;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import it.pyronaid.brainstorming.MainActivity;

/**
 * Created by pyronaid on 21/11/2016.
 */
@Singleton
public class AccountManagerUtils {

    // Context
    private Context _context;
    private AccountManager mAccountManager;

    // Constructor
    public AccountManagerUtils(Context context){
        this._context = context;
        this.mAccountManager = AccountManager.get(context);
    }

    public AccountManager getmAccountManager(){
        return mAccountManager;
    }


    public boolean checkIfSomeoneIsLogged(Activity activity){
        final Account availableAccounts[] = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

        if (availableAccounts.length == 0) {
            return true;
        } else{
            return false;
        }
    }

    public void addNewAccount(final Activity activity){
        final AccountManagerFuture<Bundle> future = mAccountManager.addAccount(AccountGeneral.ACCOUNT_TYPE, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, null, activity, new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                try {
                    Bundle bnd = future.getResult();
                    showMessage("Account was created",activity);
                    //Log.d("udinic", "AddNewAccount Bundle is " + bnd);

                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(e.getMessage(),activity);
                }
            }
        }, null);
    }

    public boolean verifyIfTokenIsNotExpired(String authtoken) {
        return true;
    }

    public boolean validateAuthToken(Activity activity){
        boolean addAccount = false;

        final Account availableAccounts[] = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(availableAccounts[0], AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, activity, null, null);
        try {
            Bundle bnd = future.getResult();

            final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);

            if (!verifyIfTokenIsNotExpired(authtoken)){
                addAccount=true;
            } else {
                //showMessage((authtoken != null) ? "SUCCESS!\ntoken: " + authtoken : "FAIL", activity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(e.getMessage(), activity);
        }

        return addAccount;
    }


    public void removeAccount(final Activity activity){

        final Account availableAccounts[] = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
        if (availableAccounts.length != 0) {
            for (int index = 0; index < availableAccounts.length; index++) {
                final AccountManagerFuture<Bundle> token = mAccountManager.getAuthToken(availableAccounts[index], AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, activity, null,null);
                try {
                    Bundle bnd = token.getResult();
                    final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                    mAccountManager.clearPassword(availableAccounts[index]);
                    mAccountManager.invalidateAuthToken(AccountGeneral.ACCOUNT_TYPE,
                                                        authtoken);
                                                        /*
                                                        ,
                                                        AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS,
                                                        null,
                                                        true,
                                                        new AccountManagerCallback<Bundle>() {
                                                            @Override
                                                            public void run(AccountManagerFuture<Bundle> future) {
                                                                try {
                                                                    Log.d("invalidateAuthToken", future.getResult().toString());
                                                                    showMessage("Account was removed", activity);
                                                                } catch (android.accounts.OperationCanceledException | AuthenticatorException | IOException e) {
                                                                    e.printStackTrace();
                                                                    showMessage(e.getMessage(), activity);
                                                                }
                                                            }
                                                        }, null);*/
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(e.getMessage(), activity);
                }
            }
            if (Build.VERSION.SDK_INT < 23) { // use deprecated method
                for (int index = 0; index < availableAccounts.length; index++) {
                    mAccountManager.removeAccount(availableAccounts[index],
                            new AccountManagerCallback<Boolean>() {
                                @Override
                                public void run(AccountManagerFuture<Boolean> future) {
                                    try {
                                        if (future.getResult()) {
                                            Log.d("Deprecated REMOVAL", future.getResult().toString());
                                            showMessage("Account was removed", activity);
                                        }

                                    } catch (android.accounts.OperationCanceledException | IOException | AuthenticatorException e) {
                                        e.printStackTrace();
                                        showMessage(e.getMessage(), activity);
                                    }
                                }
                    }, null);
                }
            } else {
                for (int index = 0; index < availableAccounts.length; index++) {
                    mAccountManager.removeAccount(availableAccounts[index], (Activity) _context,
                            new AccountManagerCallback<Bundle>() {
                                @Override
                                public void run(AccountManagerFuture<Bundle> future) {
                                    try {
                                        if (future.getResult() != null) {
                                            Log.d("ACCOUNT REMOVAL", "ACCOUNT REMOVED");
                                            showMessage("Account was removed", activity);
                                        }
                                    } catch (android.accounts.OperationCanceledException | AuthenticatorException | IOException e) {
                                        e.printStackTrace();
                                        showMessage(e.getMessage(), activity);
                                    }
                                }
                    }, null);
                }
            }
        }

    }


    public void goToMainActivity(Activity activity){
        // user is not logged in redirect him to Login Activity
        Intent i = new Intent(activity, MainActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        (activity).startActivity(i);
    }

    public void showMessage(final String msg, final Activity activity) {
        if (TextUtils.isEmpty(msg))
            return;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity.getBaseContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
