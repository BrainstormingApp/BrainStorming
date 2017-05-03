package asynctask;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import authenticatorStuff.AccountGeneral;
import authenticatorStuff.AccountManagerUtils;
import authenticatorStuff.ParseComAnswer;
import authenticatorStuff.ParseComServerAuthenticate;
import authenticatorStuff.ServerGeneral;
import authenticatorStuff.User;
import databaseStuff.BrainStormingSQLiteHelper;
import dialogs.SimpleDialogFragment;
import it.pyronaid.brainstorming.AuthenticatorActivity;
import it.pyronaid.brainstorming.R;

/**
 * Created by pyronaid on 30/01/2017.
 */
public class UpdateFieldTask extends AsyncTask<String, Void, Boolean> {
    private BrainStormingSQLiteHelper brainStormingSQLiteHelper;
    private Activity whereTaskHaveToRefer;
    private ProgressDialog dialog;
    private Button[] buttons;
    private ParseComAnswer answer;

    public UpdateFieldTask(Activity activity, BrainStormingSQLiteHelper brainStormingSQLiteHelper, Button[] buttons) {
        this.whereTaskHaveToRefer = activity;
        this.brainStormingSQLiteHelper = brainStormingSQLiteHelper;
        this.dialog = new ProgressDialog(activity, R.style.AppTheme_Dark_Dialog);
        this.buttons = buttons;
    }


    @Override
    protected void onPreExecute(){
        dialog.setMessage("Update, please wait.");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
        for (Button button : buttons) {
            button.setEnabled(false);
        }
    }

    @Override
    protected Boolean doInBackground(String... parameters) {
        String table = parameters[0];
        String type = parameters[1];
        String value = parameters[2];
        String flagDate = null;
        if(parameters.length >= 4) {
            flagDate = parameters[3];
        }
        User u = brainStormingSQLiteHelper.getUser();

        boolean flag = false;

        if(u != null && u.getEmail() != null) {

            Log.d("Brainstorming", "editFiedUserInformation");
            URL url = null;
            try {
                url = new URL(ServerGeneral.URL_EDIT_USER_INFO);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setReadTimeout(3000);
                connection.setConnectTimeout(3000);
                connection.setRequestProperty("Content-Type", "application/json");

                //Create JSONObject here
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("email", URLEncoder.encode(u.getEmail(), "UTF-8"));
                jsonParam.put("type", URLEncoder.encode(type, "UTF-8"));
                jsonParam.put("value", URLEncoder.encode(value, "UTF-8"));
                if(flagDate != null){
                    jsonParam.put("flag", URLEncoder.encode(flagDate, "UTF-8"));
                }

                DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
                //Writes out the string to the underlying output stream as a sequence of bytes
                dStream.writeBytes(jsonParam.toString());
                // Flushes the data output stream.
                dStream.flush();
                // Closing the output stream.
                dStream.close();

                answer = null;
                int responseCode = connection.getResponseCode();
                String responseString = connection.getResponseMessage();
                String answerString = readHttpAnswer(connection);

                answer = new Gson().fromJson(answerString, ParseComAnswer.class);

                if(answer != null && !answer.getError().toUpperCase().equals(AuthenticatorActivity.ERROR_TRUE)) {
                    brainStormingSQLiteHelper.setUser(answer.getUser());
                    flag = true;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (JsonSyntaxException e){
                e.printStackTrace();
            }

            if(flag) {
                brainStormingSQLiteHelper.editField(table, type, value);
            }

        } else{
            SystemClock.sleep(500);
        }

        return flag;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (dialog.isShowing()){ dialog.dismiss(); }
        for (Button button : buttons) {
            button.setEnabled(true);
        }
        if(answer != null && answer.getError().toUpperCase().equals(AuthenticatorActivity.ERROR_TRUE)) {
            FragmentManager fm = whereTaskHaveToRefer.getFragmentManager();
            SimpleDialogFragment simpleDialogFragment = SimpleDialogFragment.newInstance(answer.getTitle(), answer.getError_msg());
            //Show DialogFragment
            simpleDialogFragment.show(fm , answer.getTitle());
        } else if(!result){
            FragmentManager fm = whereTaskHaveToRefer.getFragmentManager();
            SimpleDialogFragment simpleDialogFragment = SimpleDialogFragment.newInstance("Unknown Error", "Something goes wrong in editing user information");
            //Show DialogFragment
            simpleDialogFragment.show(fm , "Unknown Error");
        }
        if(result) {
            Intent data = new Intent();
            brainStormingSQLiteHelper.getUser().setRefreshInfo(true);
            if (whereTaskHaveToRefer.getParent() == null) {
                whereTaskHaveToRefer.setResult(Activity.RESULT_OK, data);
            } else {
                whereTaskHaveToRefer.getParent().setResult(Activity.RESULT_OK, data);
            }
            whereTaskHaveToRefer.finish();
        }
    }


    public String readHttpAnswer(HttpURLConnection c){
        String answer="";

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            answer = sb.toString();
        } catch (MalformedURLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }

        return answer;
    }

}
