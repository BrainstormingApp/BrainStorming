package dagger.modules;

import android.app.Application;

import javax.inject.Singleton;

import applications.BrainStormingApplications;
import dagger.Module;
import dagger.Provides;
import databaseStuff.BrainStormingSQLiteHelper;

/**
 * Created by pyronaid on 22/03/2017.
 */

@Module
public class BrainStormingSQLiteHelperModule {

    @Provides
    @Singleton
    // Application reference must come from AppModule.class
    BrainStormingSQLiteHelper providesSQLiteHelperModule(BrainStormingApplications application) {
        return new BrainStormingSQLiteHelper(application);
    }
}
