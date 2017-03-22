package dagger.modules;

import android.content.Context;

import javax.inject.Singleton;

import applications.BrainStormingApplications;
import authenticatorStuff.AccountManagerUtils;
import dagger.Module;
import dagger.Provides;
import databaseStuff.BrainStormingSQLiteHelper;
import validatorStuff.ValidatorInputs;

/**
 * Created by pyronaid on 08/03/2017.
 */
@Module
public class ApplicationModule {
    private final BrainStormingApplications application;

    public ApplicationModule(BrainStormingApplications application) {
        this.application = application;
    }


    @Provides
    @Singleton
    BrainStormingApplications providesApplication() {
        return application;
    }
}
