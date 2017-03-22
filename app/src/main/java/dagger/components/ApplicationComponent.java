package dagger.components;

/**
 * Created by pyronaid on 08/03/2017.
 */

import android.content.Context;

import javax.inject.Singleton;

import applications.BrainStormingApplications;
import authenticatorStuff.AccountManagerUtils;
import dagger.Component;
import dagger.modules.AccountManagerUtilsModule;
import dagger.modules.ApplicationModule;
import dagger.modules.BrainStormingSQLiteHelperModule;
import dagger.modules.ValidatorInputsModule;
import databaseStuff.BrainStormingSQLiteHelper;
import it.pyronaid.brainstorming.MainActivity;
import validatorStuff.ValidatorInputs;

@Singleton // Constraints this component to one-per-application or unscoped bindings.
@Component(modules={ApplicationModule.class, ValidatorInputsModule.class, BrainStormingSQLiteHelperModule.class, AccountManagerUtilsModule.class})
public interface ApplicationComponent {

    void inject(MainActivity mainActivity);

    BrainStormingApplications context();
    AccountManagerUtils accountManagerUtils();
    ValidatorInputs validatorInputs();
    BrainStormingSQLiteHelper brainStormingSQLiteHelper();
}
