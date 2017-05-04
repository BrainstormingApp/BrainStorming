package asynctask;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.view.View;
import android.widget.TextView;

import java.io.File;

import authenticatorStuff.AccountGeneral;
import authenticatorStuff.AccountManagerUtils;
import authenticatorStuff.User;
import databaseStuff.BrainStormingSQLiteHelper;
import it.pyronaid.brainstorming.MainActivity;
import it.pyronaid.brainstorming.R;
import layoutCustomized.CircleImageView;

/**
 * Created by pyronaid on 30/01/2017.
 */
public class CheckAuthTokenTask extends AsyncTask<AccountManagerUtils, Void, Boolean> {
    private AccountManagerUtils accountManagerUtils;
    private Activity whereTaskHaveToRefer;
    private BrainStormingSQLiteHelper brainStormingSQLiteHelper;


    public CheckAuthTokenTask(Activity activity, BrainStormingSQLiteHelper brainStormingSQLiteHelper) {
        this.whereTaskHaveToRefer = activity;
        this.brainStormingSQLiteHelper = brainStormingSQLiteHelper;
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
            if(!addAccount && brainStormingSQLiteHelper.getUser() == null){
                brainStormingSQLiteHelper.autoDefineUser();
            }
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
        } else {
            // Set some information
            User u = brainStormingSQLiteHelper.getUser();
            if(u != null){
                if (whereTaskHaveToRefer instanceof MainActivity && u.getRefreshInfo() == true) {
                    NavigationView nvDrawer = (NavigationView) whereTaskHaveToRefer.findViewById(R.id.nvView);
                    View headerView = nvDrawer.getHeaderView(0);

                    TextView profileNameHeader = (TextView) headerView.findViewById(R.id.profile_name_header);
                    CircleImageView profileImage = (CircleImageView) headerView.findViewById(R.id.profile_image);
                    if(profileNameHeader != null && profileImage != null) {
                        if (!u.getName().isEmpty()) {
                            profileNameHeader.setText(u.getName());
                        }
                        ContextWrapper cw = new ContextWrapper(whereTaskHaveToRefer.getApplicationContext());
                        String directory = cw.getDir("profile", Context.MODE_PRIVATE).getAbsolutePath();
                        File file = new File(directory + "/Profile.jpg");
                        if (file.exists()) {
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                            Bitmap finalBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                            profileImage.setImageBitmap(finalBitmap);
                        }

                        u.setRefreshInfo(false);
                    }
                }
            }
        }
    }




}
