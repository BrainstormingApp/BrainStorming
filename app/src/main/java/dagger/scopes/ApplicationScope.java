package dagger.scopes;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by pyronaid on 22/03/2017.
 */
@Scope
@Documented
@Retention(value= RetentionPolicy.RUNTIME)
public @interface ApplicationScope
{
}
