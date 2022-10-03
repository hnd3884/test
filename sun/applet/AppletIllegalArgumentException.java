package sun.applet;

public class AppletIllegalArgumentException extends IllegalArgumentException
{
    private String key;
    private static AppletMessageHandler amh;
    
    public AppletIllegalArgumentException(final String key) {
        super(key);
        this.key = null;
        this.key = key;
    }
    
    @Override
    public String getLocalizedMessage() {
        return AppletIllegalArgumentException.amh.getMessage(this.key);
    }
    
    static {
        AppletIllegalArgumentException.amh = new AppletMessageHandler("appletillegalargumentexception");
    }
}
