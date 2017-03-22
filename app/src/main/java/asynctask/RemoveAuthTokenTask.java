package asynctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.SystemClock;

import authenticatorStuff.AccountManagerUtils;
import it.pyronaid.brainstorming.R;

/**
 * Created by pyronaid on 30/01/2017.
 */
public class RemoveAuthTokenTask extends AsyncTask<AccountManagerUtils, Void, Void> {
    private AccountManagerUtils accountManagerUtils;
    private Activity whereTaskHaveToRefer;
    private ProgressDialog dialog;


    public RemoveAuthTokenTask(Activity activity) {
        this.whereTaskHaveToRefer = activity;
        this.dialog = new ProgressDialog(activity, R.style.AppTheme_Dark_Dialog);
    }
    //SessionManager sessionManager;


    @Override
    protected void onPreExecute(){
        dialog.setMessage("Removing account, please wait.");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected Void doInBackground(AccountManagerUtils... accountManagerUtils) {
        this.accountManagerUtils = accountManagerUtils[0];
        this.accountManagerUtils.removeAccount(whereTaskHaveToRefer);
        SystemClock.sleep(2000);
        return null;
    }

    @Override
    protected void onPostExecute(Void nothing) {
        if (dialog.isShowing()){ dialog.dismiss(); }
        accountManagerUtils.goToMainActivity(whereTaskHaveToRefer);
    }




}
