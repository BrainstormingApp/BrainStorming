package asynctask;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;

import authenticatorStuff.AccountGeneral;
import authenticatorStuff.AccountManagerUtils;
import it.pyronaid.brainstorming.R;

/**
 * Created by pyronaid on 30/01/2017.
 */
public class CheckAuthTokenTask extends AsyncTask<AccountManagerUtils, Void, Boolean> {
    private AccountManagerUtils accountManagerUtils;
    private Activity whereTaskHaveToRefer;


    public CheckAuthTokenTask(Activity activity) {
        this.whereTaskHaveToRefer = activity;
    }


    @Override
    protected void onPreExecute(){}

    @Override
    protected Boolean doInBackground(AccountManagerUtils... accountManagerUtilsInput) {
        accountManagerUtils = accountManagerUtilsInput[0];
        boolean addAccount = false;
        AccountManager mAccountManager = accountManagerUtils.getmAccountManager();
        final Account availableAccounts[] = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

        if (accountManagerUtils.checkIfSomeoneIsLogged(whereTaskHaveToRefer)) {
            addAccount=true;
        } else {
            addAccount = accountManagerUtils.validateAuthToken(whereTaskHaveToRefer);
        }

        if(addAccount){
            accountManagerUtils.addNewAccount(whereTaskHaveToRefer);
        }

        return addAccount;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if(result) {
            accountManagerUtils.showMessage("Add Account ", whereTaskHaveToRefer);
        }
    }




}
