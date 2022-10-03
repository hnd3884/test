package sun.security.timestamp;

import sun.security.util.DerValue;
import java.io.IOException;
import sun.security.pkcs.PKCS7;
import sun.security.util.Debug;

public class TSResponse
{
    public static final int GRANTED = 0;
    public static final int GRANTED_WITH_MODS = 1;
    public static final int REJECTION = 2;
    public static final int WAITING = 3;
    public static final int REVOCATION_WARNING = 4;
    public static final int REVOCATION_NOTIFICATION = 5;
    public static final int BAD_ALG = 0;
    public static final int BAD_REQUEST = 2;
    public static final int BAD_DATA_FORMAT = 5;
    public static final int TIME_NOT_AVAILABLE = 14;
    public static final int UNACCEPTED_POLICY = 15;
    public static final int UNACCEPTED_EXTENSION = 16;
    public static final int ADD_INFO_NOT_AVAILABLE = 17;
    public static final int SYSTEM_FAILURE = 25;
    private static final Debug debug;
    private int status;
    private String[] statusString;
    private boolean[] failureInfo;
    private byte[] encodedTsToken;
    private PKCS7 tsToken;
    private TimestampToken tstInfo;
    
    TSResponse(final byte[] array) throws IOException {
        this.statusString = null;
        this.failureInfo = null;
        this.encodedTsToken = null;
        this.tsToken = null;
        this.parse(array);
    }
    
    public int getStatusCode() {
        return this.status;
    }
    
    public String[] getStatusMessages() {
        return this.statusString;
    }
    
    public boolean[] getFailureInfo() {
        return this.failureInfo;
    }
    
    public String getStatusCodeAsText() {
        switch (this.status) {
            case 0: {
                return "the timestamp request was granted.";
            }
            case 1: {
                return "the timestamp request was granted with some modifications.";
            }
            case 2: {
                return "the timestamp request was rejected.";
            }
            case 3: {
                return "the timestamp request has not yet been processed.";
            }
            case 4: {
                return "warning: a certificate revocation is imminent.";
            }
            case 5: {
                return "notification: a certificate revocation has occurred.";
            }
            default: {
                return "unknown status code " + this.status + ".";
            }
        }
    }
    
    private boolean isSet(final int n) {
        return this.failureInfo[n];
    }
    
    public String getFailureCodeAsText() {
        if (this.failureInfo == null) {
            return "";
        }
        try {
            if (this.isSet(0)) {
                return "Unrecognized or unsupported algorithm identifier.";
            }
            if (this.isSet(2)) {
                return "The requested transaction is not permitted or supported.";
            }
            if (this.isSet(5)) {
                return "The data submitted has the wrong format.";
            }
            if (this.isSet(14)) {
                return "The TSA's time source is not available.";
            }
            if (this.isSet(15)) {
                return "The requested TSA policy is not supported by the TSA.";
            }
            if (this.isSet(16)) {
                return "The requested extension is not supported by the TSA.";
            }
            if (this.isSet(17)) {
                return "The additional information requested could not be understood or is not available.";
            }
            if (this.isSet(25)) {
                return "The request cannot be handled due to system failure.";
            }
        }
        catch (final ArrayIndexOutOfBoundsException ex) {}
        return "unknown failure code";
    }
    
    public PKCS7 getToken() {
        return this.tsToken;
    }
    
    public TimestampToken getTimestampToken() {
        return this.tstInfo;
    }
    
    public byte[] getEncodedToken() {
        return this.encodedTsToken;
    }
    
    private void parse(final byte[] array) throws IOException {
        final DerValue derValue = new DerValue(array);
        if (derValue.tag != 48) {
            throw new IOException("Bad encoding for timestamp response");
        }
        final DerValue derValue2 = derValue.data.getDerValue();
        this.status = derValue2.data.getInteger();
        if (TSResponse.debug != null) {
            TSResponse.debug.println("timestamp response: status=" + this.status);
        }
        if (derValue2.data.available() > 0 && (byte)derValue2.data.peekByte() == 48) {
            final DerValue[] sequence = derValue2.data.getSequence(1);
            this.statusString = new String[sequence.length];
            for (int i = 0; i < sequence.length; ++i) {
                this.statusString[i] = sequence[i].getUTF8String();
                if (TSResponse.debug != null) {
                    TSResponse.debug.println("timestamp response: statusString=" + this.statusString[i]);
                }
            }
        }
        if (derValue2.data.available() > 0) {
            this.failureInfo = derValue2.data.getUnalignedBitString().toBooleanArray();
        }
        if (derValue.data.available() > 0) {
            this.encodedTsToken = derValue.data.getDerValue().toByteArray();
            this.tsToken = new PKCS7(this.encodedTsToken);
            this.tstInfo = new TimestampToken(this.tsToken.getContentInfo().getData());
        }
        if (this.status == 0 || this.status == 1) {
            if (this.tsToken == null) {
                throw new TimestampException("Bad encoding for timestamp response: expected a timeStampToken element to be present");
            }
        }
        else if (this.tsToken != null) {
            throw new TimestampException("Bad encoding for timestamp response: expected no timeStampToken element to be present");
        }
    }
    
    static {
        debug = Debug.getInstance("ts");
    }
    
    static final class TimestampException extends IOException
    {
        private static final long serialVersionUID = -1631631794891940953L;
        
        TimestampException(final String s) {
            super(s);
        }
    }
}
