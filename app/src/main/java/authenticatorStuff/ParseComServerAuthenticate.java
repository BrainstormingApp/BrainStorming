package authenticatorStuff;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import static authenticatorStuff.ServerGeneral.URL_USER_PIC_BASE_FOLDER;


/**
 * Created by pyronaid on 22/11/2016.
 */
public class ParseComServerAuthenticate implements ServerAuthenticate{

    @Override
    public ParseComAnswer userSignUp(String name, String email, String pass, String authType) throws Exception {
        Log.d("Brainstorming", "userSignUp");
        URL url = new URL(ServerGeneral.URL_SIGNUP);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setReadTimeout(3000);
        connection.setConnectTimeout(3000);
        connection.setRequestProperty("Content-Type","application/json");

        //Create JSONObject here
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("name", URLEncoder.encode(name,"UTF-8"));
        jsonParam.put("email", URLEncoder.encode(email,"UTF-8"));
        jsonParam.put("password", URLEncoder.encode(pass,"UTF-8"));
        jsonParam.put("authType", URLEncoder.encode(authType,"UTF-8"));

        DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
        //Writes out the string to the underlying output stream as a sequence of bytes
        dStream.writeBytes(jsonParam.toString());
        // Flushes the data output stream.
        dStream.flush();
        // Closing the output stream.
        dStream.close();

        ParseComAnswer answer = null;
        try {
            // getting the response code
            int responseCode = connection.getResponseCode();
            String responseString = connection.getResponseMessage();
            String answerString = readHttpAnswer(connection);

            answer = new Gson().fromJson(answerString, ParseComAnswer.class);
            //throw new Exception("Error signing-in ["+error.error_code+"] - " + error.error_msg);
            //ParseComAnswer error = new Gson().fromJson(responseString, ParseComAnswer.class);
            //throw new Exception("Error creating user["+error.getError_code()+"] - " + error.getError_msg());

        } catch (JsonSyntaxException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return answer;
    }


    @Override
    public ParseComAnswer userSignIn(String mail, String pass, String authType) throws Exception {

        Log.d("Brainstorming", "userSignIn");
        URL url = new URL(ServerGeneral.URL_SIGNIN);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setReadTimeout(3000);
        connection.setConnectTimeout(3000);
        connection.setRequestProperty("Content-Type","application/json");

        //Create JSONObject here
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("email", URLEncoder.encode(mail,"UTF-8"));
        jsonParam.put("password", URLEncoder.encode(pass,"UTF-8"));

        DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
        //Writes out the string to the underlying output stream as a sequence of bytes
        dStream.writeBytes(jsonParam.toString());
        // Flushes the data output stream.
        dStream.flush();
        // Closing the output stream.
        dStream.close();

        ParseComAnswer answer = null;
        try {
            // getting the response code
            int responseCode = connection.getResponseCode();
            String responseString = connection.getResponseMessage();
            String answerString = readHttpAnswer(connection);

            answer = new Gson().fromJson(answerString, ParseComAnswer.class);
            //throw new Exception("Error signing-in ["+error.error_code+"] - " + error.error_msg);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e){
            e.printStackTrace();
        }

        return answer;
    }

    @Override
    public File userSavePic(String uidUser, ContextWrapper cw) throws Exception{
        String src = URL_USER_PIC_BASE_FOLDER + uidUser + "/Profile.jpg";
        java.net.URL url = new java.net.URL(src);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.connect();
        InputStream input = connection.getInputStream();
        return saveimagetoFile(BitmapFactory.decodeStream(input), cw);
    }

    public static File saveimagetoFile(Bitmap bitmap, ContextWrapper cw) {
        File file = null;
        try {
            File directory = cw.getDir("profile", Context.MODE_PRIVATE);
            if (!directory.exists()) {
                directory.mkdir();
            }
            file = new File(directory, "Profile.jpg");

            FileOutputStream fOut = null;
            fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush(); // Not really required
            fOut.close(); // do not forget to close the stream
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    public static void deleteImageFromFile(ContextWrapper cw){
        File file = null;
        File directory = cw.getDir("profile", Context.MODE_PRIVATE);
        if (directory.exists()) {
            file = new File(directory, "Profile.jpg");
            if (file.exists()) {
                file.delete();
            }
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
