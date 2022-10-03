package sun.applet;

public class AppletSecurityException extends SecurityException
{
    private String key;
    private Object[] msgobj;
    private static AppletMessageHandler amh;
    
    public AppletSecurityException(final String key) {
        super(key);
        this.key = null;
        this.msgobj = null;
        this.key = key;
    }
    
    public AppletSecurityException(final String s, final String s2) {
        this(s);
        (this.msgobj = new Object[1])[0] = s2;
    }
    
    public AppletSecurityException(final String s, final String s2, final String s3) {
        this(s);
        (this.msgobj = new Object[2])[0] = s2;
        this.msgobj[1] = s3;
    }
    
    @Override
    public String getLocalizedMessage() {
        if (this.msgobj != null) {
            return AppletSecurityException.amh.getMessage(this.key, this.msgobj);
        }
        return AppletSecurityException.amh.getMessage(this.key);
    }
    
    static {
        AppletSecurityException.amh = new AppletMessageHandler("appletsecurityexception");
    }
}
