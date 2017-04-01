package dagger.components;

/**
 * Created by pyronaid on 08/03/2017.
 */

import dagger.Component;
import dagger.modules.UserModule;
import dagger.scopes.UserScope;
import it.pyronaid.brainstorming.AuthenticatorActivity;
import it.pyronaid.brainstorming.ServicesViewDatePickerActivity;
import it.pyronaid.brainstorming.ServicesViewEditTextActivity;
import it.pyronaid.brainstorming.SignUpActivity;

@UserScope // using the previously defined scope, note that @Singleton will not work
@Component(dependencies = ApplicationComponent.class, modules = UserModule.class)
public interface UserComponent {
    void inject(AuthenticatorActivity activity);
    void inject(SignUpActivity activity);
    void inject(ServicesViewEditTextActivity activity);
    void inject(ServicesViewDatePickerActivity activity);
}
