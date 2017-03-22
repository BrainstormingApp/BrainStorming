package applications;

import android.app.Application;

import dagger.components.ApplicationComponent;
import dagger.components.DaggerApplicationComponent;
import dagger.components.DaggerUserComponent;
import dagger.components.UserComponent;
import dagger.modules.AccountManagerUtilsModule;
import dagger.modules.ApplicationModule;
import dagger.modules.BrainStormingSQLiteHelperModule;
import dagger.modules.UserModule;
import dagger.modules.ValidatorInputsModule;


/**
 * Created by pyronaid on 14/01/2017.
 */
public class BrainStormingApplications extends Application{
    private ApplicationComponent applicationComponent;
    private UserComponent userComponent;



    @Override
    public void onCreate(){
        super.onCreate();

         applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .validatorInputsModule(new ValidatorInputsModule())
                .brainStormingSQLiteHelperModule(new BrainStormingSQLiteHelperModule())
                .accountManagerUtilsModule(new AccountManagerUtilsModule())
                .build();

        userComponent = DaggerUserComponent.builder()
                .applicationComponent(applicationComponent)
                .userModule(new UserModule())
                .build();

    }


    public ApplicationComponent getApplicationComponent(){
        return applicationComponent;
    }

    public UserComponent getUserComponent(){
        return userComponent;
    }

}
