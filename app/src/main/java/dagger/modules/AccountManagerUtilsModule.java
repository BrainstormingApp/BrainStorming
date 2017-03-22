package dagger.modules;

import javax.inject.Singleton;

import applications.BrainStormingApplications;
import authenticatorStuff.AccountManagerUtils;
import dagger.Module;
import dagger.Provides;

/**
 * Created by pyronaid on 22/03/2017.
 */
@Module
public class AccountManagerUtilsModule {

    @Provides
    @Singleton
        // Application reference must come from AppModule.class
    AccountManagerUtils providesAutenticationManagerUtils(BrainStormingApplications application) {
        return new AccountManagerUtils(application);
    }
}
