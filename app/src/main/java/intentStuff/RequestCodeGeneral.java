package intentStuff;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;

import it.pyronaid.brainstorming.AuthenticatorActivity;
import it.pyronaid.brainstorming.MainActivity;

/**
 * Created by pyronaid on 02/04/2017.
 */

public class RequestCodeGeneral {
    //MainActivity -> MyProfileFragment
    public final static int PICK_IMAGE_REQUEST = 3;

    //MainActivity -> MyProfileFragment
    public final static int CROP_IMAGE_REQUEST = 4;

    //AuthenticatorActivity
    public final static int REQ_SIGNUP = 1;

    //MainActivity -> MyProfileFragment
    public final static int REQ_EDIT = 2;



    public static final String KEY_INTENT_FOR_VALUE = "Value";
    public static final String KEY_INTENT_FOR_TYPE = "Type";
    public static final String KEY_INTENT_FOR_TABLE_NAME = "TableName";

    public static final String KEY_INTENT_URI = "Uri";


}
