package Core;

/**
 * Created by PeriklisMaravelias on 11/30/15.
 */
public class Values {
    public static String LDAP_INITIAL_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
    public static String LDAP_PROVIDER_URL = "ldap://172.20.10.8:389/o=CodeMore,c=EL";
    public static String LDAP_SECURITY_AUTHENTICATION = "simple";
    public static String LDAP_SECURITY_PRINCIPAL = "cn=CodeMore,c=EL";
    public static String LDAP_SECURITY_CREDENTIALS = "codemore";

    public static String InternalError = "SW50ZXJuYWxFcnJvcg==";
    public static String CredentialCombinationNotFound = "Q3JlZGVudGlhbENvbWJpbmF0aW9uTm90Rm91bmQ=";
    public static String PasswordsNotMatch = "UGFzc3dvcmRzTm90TWF0Y2g=";

    public static int Pending = 1;
    public static int Received = 2;

    public static String Salt = "eSl^Ivj$(1hrRsUJIdtP2Y6hAZ#-XnKP";

    public static int Folder = 1;
    public static int File = 2;

    public static String ChatDS = "___chat___";
    public static String ProjectDS= "___own___";

}