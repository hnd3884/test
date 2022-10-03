package sun.security.krb5;

import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.KRBError;

public class KrbException extends Exception
{
    private static final long serialVersionUID = -4993302876451928596L;
    private int returnCode;
    private KRBError error;
    
    public KrbException(final String s) {
        super(s);
    }
    
    public KrbException(final Throwable t) {
        super(t);
    }
    
    public KrbException(final int returnCode) {
        this.returnCode = returnCode;
    }
    
    public KrbException(final int returnCode, final String s) {
        this(s);
        this.returnCode = returnCode;
    }
    
    public KrbException(final KRBError error) {
        this.returnCode = error.getErrorCode();
        this.error = error;
    }
    
    public KrbException(final KRBError error, final String s) {
        this(s);
        this.returnCode = error.getErrorCode();
        this.error = error;
    }
    
    public KRBError getError() {
        return this.error;
    }
    
    public int returnCode() {
        return this.returnCode;
    }
    
    public String returnCodeSymbol() {
        return returnCodeSymbol(this.returnCode);
    }
    
    public static String returnCodeSymbol(final int n) {
        return "not yet implemented";
    }
    
    public String returnCodeMessage() {
        return Krb5.getErrorMessage(this.returnCode);
    }
    
    public static String errorMessage(final int n) {
        return Krb5.getErrorMessage(n);
    }
    
    public String krbErrorMessage() {
        final StringBuffer sb = new StringBuffer("krb_error " + this.returnCode);
        final String message = this.getMessage();
        if (message != null) {
            sb.append(" ");
            sb.append(message);
        }
        return sb.toString();
    }
    
    @Override
    public String getMessage() {
        final StringBuffer sb = new StringBuffer();
        final int returnCode = this.returnCode();
        if (returnCode != 0) {
            sb.append(this.returnCodeMessage());
            sb.append(" (").append(this.returnCode()).append(')');
        }
        final String message = super.getMessage();
        if (message != null && message.length() != 0) {
            if (returnCode != 0) {
                sb.append(" - ");
            }
            sb.append(message);
        }
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return "KrbException: " + this.getMessage();
    }
    
    @Override
    public int hashCode() {
        int n = 37 * 17 + this.returnCode;
        if (this.error != null) {
            n = 37 * n + this.error.hashCode();
        }
        return n;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KrbException)) {
            return false;
        }
        final KrbException ex = (KrbException)o;
        return this.returnCode == ex.returnCode && ((this.error == null) ? (ex.error == null) : this.error.equals(ex.error));
    }
}
