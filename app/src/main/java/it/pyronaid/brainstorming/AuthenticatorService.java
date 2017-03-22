package it.pyronaid.brainstorming;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import authenticatorStuff.AccountAutenticator;

/**
 * Created by pyronaid on 23/11/2016.
 */
public class AuthenticatorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {

        AccountAutenticator authenticator = new AccountAutenticator(this);
        return authenticator.getIBinder();
    }
}
