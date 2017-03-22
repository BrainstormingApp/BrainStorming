package dagger.modules;

import javax.inject.Singleton;
import javax.xml.validation.Validator;

import dagger.Module;
import dagger.Provides;
import validatorStuff.ValidatorInputs;

/**
 * Created by pyronaid on 22/03/2017.
 */
@Module
public class ValidatorInputsModule {
    @Provides
    @Singleton
    ValidatorInputs providesValidator() {
        return new ValidatorInputs();
    }
}
