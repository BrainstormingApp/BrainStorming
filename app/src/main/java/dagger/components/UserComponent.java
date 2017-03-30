package dagger.components;

/**
 * Created by pyronaid on 08/03/2017.
 */

import android.content.Context;

import javax.inject.Singleton;

import authenticatorStuff.AccountManagerUtils;
import dagger.Component;
import dagger.modules.AccountManagerUtilsModule;
import dagger.modules.ApplicationModule;
import dagger.modules.BrainStormingSQLiteHelperModule;
import dagger.modules.UserModule;
import dagger.modules.ValidatorInputsModule;
import dagger.scopes.UserScope;
import databaseStuff.BrainStormingSQLiteHelper;
import it.pyronaid.brainstorming.AuthenticatorActivity;
import it.pyronaid.brainstorming.MainActivity;
import it.pyronaid.brainstorming.ServicesViewActivity;
import it.pyronaid.brainstorming.SignUpActivity;
import validatorStuff.ValidatorInputs;

@UserScope // using the previously defined scope, note that @Singleton will not work
@Component(dependencies = ApplicationComponent.class, modules = UserModule.class)
public interface UserComponent {
    void inject(AuthenticatorActivity activity);
    void inject(SignUpActivity activity);
    void inject(ServicesViewActivity activity);
}
