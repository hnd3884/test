package sun.net.ftp;

public class FtpProtocolException extends Exception
{
    private static final long serialVersionUID = 5978077070276545054L;
    private final FtpReplyCode code;
    
    public FtpProtocolException(final String s) {
        super(s);
        this.code = FtpReplyCode.UNKNOWN_ERROR;
    }
    
    public FtpProtocolException(final String s, final FtpReplyCode code) {
        super(s);
        this.code = code;
    }
    
    public FtpReplyCode getReplyCode() {
        return this.code;
    }
}
