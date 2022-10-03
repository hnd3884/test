package org.ietf.jgss;

public class GSSException extends Exception
{
    private static final long serialVersionUID = -2706218945227726672L;
    public static final int BAD_BINDINGS = 1;
    public static final int BAD_MECH = 2;
    public static final int BAD_NAME = 3;
    public static final int BAD_NAMETYPE = 4;
    public static final int BAD_STATUS = 5;
    public static final int BAD_MIC = 6;
    public static final int CONTEXT_EXPIRED = 7;
    public static final int CREDENTIALS_EXPIRED = 8;
    public static final int DEFECTIVE_CREDENTIAL = 9;
    public static final int DEFECTIVE_TOKEN = 10;
    public static final int FAILURE = 11;
    public static final int NO_CONTEXT = 12;
    public static final int NO_CRED = 13;
    public static final int BAD_QOP = 14;
    public static final int UNAUTHORIZED = 15;
    public static final int UNAVAILABLE = 16;
    public static final int DUPLICATE_ELEMENT = 17;
    public static final int NAME_NOT_MN = 18;
    public static final int DUPLICATE_TOKEN = 19;
    public static final int OLD_TOKEN = 20;
    public static final int UNSEQ_TOKEN = 21;
    public static final int GAP_TOKEN = 22;
    private static String[] messages;
    private int major;
    private int minor;
    private String minorMessage;
    private String majorString;
    
    public GSSException(final int major) {
        this.minor = 0;
        this.minorMessage = null;
        this.majorString = null;
        if (this.validateMajor(major)) {
            this.major = major;
        }
        else {
            this.major = 11;
        }
    }
    
    GSSException(final int major, final String majorString) {
        this.minor = 0;
        this.minorMessage = null;
        this.majorString = null;
        if (this.validateMajor(major)) {
            this.major = major;
        }
        else {
            this.major = 11;
        }
        this.majorString = majorString;
    }
    
    public GSSException(final int major, final int minor, final String minorMessage) {
        this.minor = 0;
        this.minorMessage = null;
        this.majorString = null;
        if (this.validateMajor(major)) {
            this.major = major;
        }
        else {
            this.major = 11;
        }
        this.minor = minor;
        this.minorMessage = minorMessage;
    }
    
    public int getMajor() {
        return this.major;
    }
    
    public int getMinor() {
        return this.minor;
    }
    
    public String getMajorString() {
        if (this.majorString != null) {
            return this.majorString;
        }
        return GSSException.messages[this.major - 1];
    }
    
    public String getMinorString() {
        return this.minorMessage;
    }
    
    public void setMinor(final int minor, final String minorMessage) {
        this.minor = minor;
        this.minorMessage = minorMessage;
    }
    
    @Override
    public String toString() {
        return "GSSException: " + this.getMessage();
    }
    
    @Override
    public String getMessage() {
        if (this.minor == 0) {
            return this.getMajorString();
        }
        return this.getMajorString() + " (Mechanism level: " + this.getMinorString() + ")";
    }
    
    private boolean validateMajor(final int n) {
        return n > 0 && n <= GSSException.messages.length;
    }
    
    static {
        GSSException.messages = new String[] { "Channel binding mismatch", "Unsupported mechanism requested", "Invalid name provided", "Name of unsupported type provided", "Invalid input status selector", "Token had invalid integrity check", "Specified security context expired", "Expired credentials detected", "Defective credential detected", "Defective token detected", "Failure unspecified at GSS-API level", "Security context init/accept not yet called or context deleted", "No valid credentials provided", "Unsupported QOP value", "Operation unauthorized", "Operation unavailable", "Duplicate credential element requested", "Name contains multi-mechanism elements", "The token was a duplicate of an earlier token", "The token's validity period has expired", "A later token has already been processed", "An expected per-message token was not received" };
    }
}
