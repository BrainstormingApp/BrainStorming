package authenticatorStuff;

/**
 * Created by pyronaid on 22/11/2016.
 */
public class AccountGeneral {
    /**
     * Account type id
     */
    public static final String ACCOUNT_TYPE = "it.pyronaid.brainstorming";

    /**
     * Account name
     */
    public static final String ACCOUNT_NAME = "Brainstorming";

    /**
     * Auth token types
     */
    public static final String AUTHTOKEN_TYPE_READ_ONLY = "READ_ONLY";
    public static final String AUTHTOKEN_TYPE_READ_ONLY_LABEL = "Read only access to an Brainstorming account";

    public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "FULL_ACCESS";
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS_LABEL = "Full access to an Brainstorming account";

    public static final ServerAuthenticate sServerAuthenticate = new ParseComServerAuthenticate();
}
