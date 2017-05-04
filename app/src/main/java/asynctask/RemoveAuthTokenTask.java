package asynctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContextWrapper;
import android.os.AsyncTask;
import android.os.SystemClock;

import authenticatorStuff.AccountManagerUtils;
import authenticatorStuff.ParseComServerAuthenticate;
import databaseStuff.BrainStormingSQLiteHelper;
import it.pyronaid.brainstorming.R;

/**
 * Created by pyronaid on 30/01/2017.
 */
public class RemoveAuthTokenTask extends AsyncTask<AccountManagerUtils, Void, Void> {
    private AccountManagerUtils accountManagerUtils;
    private Activity whereTaskHaveToRefer;
    private ProgressDialog dialog;
    private BrainStormingSQLiteHelper brainStormingSQLiteHelper;


    public RemoveAuthTokenTask(Activity activity, BrainStormingSQLiteHelper brainStormingSQLiteHelper) {
        this.whereTaskHaveToRefer = activity;
        this.brainStormingSQLiteHelper = brainStormingSQLiteHelper;
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
        if(brainStormingSQLiteHelper != null){
            brainStormingSQLiteHelper.dropAll();
        }
        this.accountManagerUtils.removeAccount(whereTaskHaveToRefer);

        //remove all the profile pictures
        ContextWrapper cw = new ContextWrapper(whereTaskHaveToRefer.getApplicationContext());
        ParseComServerAuthenticate.deleteImageFromFile(cw);

        SystemClock.sleep(2000);
        return null;
    }

    @Override
    protected void onPostExecute(Void nothing) {
        if (dialog.isShowing()){ dialog.dismiss(); }
        accountManagerUtils.goToMainActivity(whereTaskHaveToRefer);
    }




}
