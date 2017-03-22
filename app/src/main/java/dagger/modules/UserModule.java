package dagger.modules;

/**
 * Created by pyronaid on 22/03/2017.
 */

import authenticatorStuff.User;
import dagger.Module;
import dagger.Provides;
import dagger.scopes.UserScope;


@Module
public class UserModule {
    @Provides
    @UserScope
        // Application reference must come from AppModule.class
    User providesUser() {
        return new User();
    }
}
