package authenticatorStuff;

/**
 * Created by pyronaid on 22/11/2016.
 */
public interface ServerAuthenticate {
    public ParseComAnswer userSignUp(final String name, final String email, final String pass, String authType) throws Exception;
    public ParseComAnswer userSignIn(final String user, final String pass, String authType) throws Exception;
}
