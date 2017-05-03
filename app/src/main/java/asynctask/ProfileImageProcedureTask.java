package asynctask;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import authenticatorStuff.ParseComAnswer;
import authenticatorStuff.ParseComServerAuthenticate;
import authenticatorStuff.ServerGeneral;
import authenticatorStuff.User;
import databaseStuff.BrainStormingSQLiteHelper;
import dialogs.SimpleDialogFragment;
import id.zelory.compressor.Compressor;
import it.pyronaid.brainstorming.AuthenticatorActivity;
import it.pyronaid.brainstorming.MainActivity;
import it.pyronaid.brainstorming.R;
import layoutCustomized.DynamicImageView;

import static authenticatorStuff.ServerGeneral.boundary;
import static authenticatorStuff.ServerGeneral.lineEnd;
import static authenticatorStuff.ServerGeneral.twoHyphens;

/**
 * Created by pyronaid on 30/01/2017.
 */
public class ProfileImageProcedureTask extends AsyncTask<Uri, Void, Boolean> {
    //private BrainStormingSQLiteHelper brainStormingSQLiteHelper;
    private Fragment fragment;
    private Activity whereTaskHaveToRefer;
    private BrainStormingSQLiteHelper brainStormingSQLiteHelper;
    private ProgressDialog dialog;
    private File compressedImage;
    private DynamicImageView dynamicImageView;
    private Bitmap finalBitmap = null;


    public ProfileImageProcedureTask(Fragment fragment, DynamicImageView dynamicImageView) {
        this.whereTaskHaveToRefer = fragment.getActivity();
        this.fragment = fragment;
        this.dynamicImageView = dynamicImageView;
        this.brainStormingSQLiteHelper = ((MainActivity) whereTaskHaveToRefer).getBrainStormingSQLiteHelper();
        this.dialog = new ProgressDialog(whereTaskHaveToRefer, R.style.AppTheme_Dark_Dialog);
    }


    @Override
    protected void onPreExecute(){
        dialog.setMessage("Update, please wait.");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected Boolean doInBackground(Uri... parameters) {

        boolean flag = true;


        //SCALING PHASE
        try {
            File f = new File(parameters[0].getPath());
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to
            final int IMAGE_MAX_SIZE=200 * 1024 * 1024;
            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = (int)Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
                        (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }
            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            finalBitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            flag = false;
        }


        //SAVING PHASE
        File file = null;
        if(flag) {
            ContextWrapper cw = new ContextWrapper(whereTaskHaveToRefer.getApplicationContext());
            file = ParseComServerAuthenticate.saveimagetoFile(finalBitmap, cw);
            if(file == null){ flag = false; }
        }


        //UPLOAD PHASE PHASE
        if(flag) {
            User u = brainStormingSQLiteHelper.getUser();
            if(u != null && u.getEmail() != null) {
                URL url = null;
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 1 * 1024 * 1024;
                try {
                    FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());

                    url = new URL(ServerGeneral.URL_UPLOAD_USER_PIC);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);
                    connection.setRequestMethod("POST");
                    connection.setReadTimeout(3000);
                    connection.setConnectTimeout(3000);
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                    connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    connection.setRequestProperty("uploaded_file", file.getAbsolutePath());


                    DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
                    //Writes out the string to the underlying output stream as a sequence of bytes
                    dStream.writeBytes(twoHyphens + boundary + lineEnd);
                    dStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + file.getAbsolutePath() + "\"" + lineEnd);
                    dStream.writeBytes("Content-Type: image/jpeg" + lineEnd);
                    dStream.writeBytes("Content-Length: " + file.length() + lineEnd);
                    dStream.writeBytes(lineEnd);

                    // create a buffer of  maximum size
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];
                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    while (bytesRead > 0) {

                        dStream.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    }
                    // send multipart form data necesssary after file data...
                    dStream.writeBytes(lineEnd);
                    dStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    //write more parameters other than the file
                    dStream.writeBytes(twoHyphens + boundary + lineEnd);
                    dStream.writeBytes("Content-Disposition: form-data; name=\"email\"" + lineEnd);
                    dStream.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd);
                    dStream.writeBytes("Content-Length: " + u.getEmail().length() + lineEnd);
                    dStream.writeBytes(lineEnd);
                    dStream.writeBytes(u.getEmail() + lineEnd);
                    dStream.writeBytes(twoHyphens + boundary + lineEnd);


                    // Close stream.
                    fileInputStream.close();
                    dStream.flush();
                    dStream.close();

                    ParseComAnswer answer = null;
                    int responseCode = connection.getResponseCode();
                    String responseString = connection.getResponseMessage();
                    String answerString = readHttpAnswer(connection);
                    answer = new Gson().fromJson(answerString, ParseComAnswer.class);
                    if(answer == null || answer.getError().toUpperCase().equals(AuthenticatorActivity.ERROR_TRUE)) {
                        flag = false;
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (JsonSyntaxException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                flag = false;
            }
        }
        /*
        User u = brainStormingSQLiteHelper.getUser();



        if(u != null && u.getEmail() != null) {

            Log.d("Brainstorming", "editFiedUserInformation");
                //Create JSONObject here
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("email", URLEncoder.encode(u.getEmail(), "UTF-8"));
                jsonParam.put("type", URLEncoder.encode(type, "UTF-8"));
                jsonParam.put("value", URLEncoder.encode(value, "UTF-8"));
                if(flagDate != null){
                    jsonParam.put("flag", URLEncoder.encode(flagDate, "UTF-8"));
                }


        */
        SystemClock.sleep(500);
        return flag;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (dialog.isShowing()){ dialog.dismiss(); }
        if(!result){
            FragmentManager fm = whereTaskHaveToRefer.getFragmentManager();
            SimpleDialogFragment simpleDialogFragment = SimpleDialogFragment.newInstance("Unknown Error", "Something goes wrong in managing image");
            //Show DialogFragment
            simpleDialogFragment.show(fm , "Unknown Error");
        } else{
            //UPLOAD IN GUI
            brainStormingSQLiteHelper.getUser().setRefreshInfo(true);
            dynamicImageView.setImageBitmap(finalBitmap);
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
