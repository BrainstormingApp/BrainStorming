package validatorStuff;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;

import it.pyronaid.brainstorming.AuthenticatorActivity;
import it.pyronaid.brainstorming.SignUpActivity;

/**
 * Created by pyronaid on 04/03/2017.
 */
public class ValidatorInputs {

    private final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
    private Pattern pattern;
    private Matcher matcher;

    private ConnectivityManager cm;

    public boolean validateInputLogIn(AuthenticatorActivity activity){
        boolean valid = true;
        pattern = Pattern.compile(PASSWORD_PATTERN);

        String userName = activity.getAccountNameTextView().getText().toString().trim();
        String userPass = activity.getAccountPasswordTextView().getText().toString().trim();

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userName).matches()) {
            activity.getAccountNameTextView().setError("not a valid mail");
            valid = false;
        } else {
            activity.getAccountNameTextView().setError(null);
        }

        matcher = pattern.matcher(userPass);
        if (!matcher.matches()) {
            activity.getAccountPasswordTextView().setError("at least 8 characters with numbers, special char and capitalize letter");
            valid = false;
        } else {
            activity.getAccountPasswordTextView().setError(null);
        }


        return valid;
    }

    public boolean validateInputSignUp(SignUpActivity activity){
        boolean valid = true;
        pattern = Pattern.compile(PASSWORD_PATTERN);

        String name = activity.getNameTextView().getText().toString().trim();
        String accountName = activity.getAccountNameTextView().getText().toString().trim();
        String accountPassword = activity.getAccountPasswordTextView().getText().toString().trim();

        if (name.isEmpty() || name.length() < 5) {
            activity.getNameTextView().setError("at least 5 characters");
            valid = false;
        } else {
            activity.getNameTextView().setError(null);
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(accountName).matches()) {
            activity.getAccountNameTextView().setError("not a valid mail");
            valid = false;
        } else {
            activity.getAccountNameTextView().setError(null);
        }

        matcher = pattern.matcher(accountPassword);
        if (!matcher.matches()) {
            activity.getAccountPasswordTextView().setError("at least 8 characters with numbers, special char and capitalize letter");
            valid = false;
        } else {
            activity.getAccountPasswordTextView().setError(null);
        }

        return valid;
    }

    public boolean isNetworkAvaliable(Activity activity){
        cm = (ConnectivityManager) activity.getSystemService(activity.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}
