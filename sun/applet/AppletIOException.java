package sun.applet;

import java.io.IOException;

public class AppletIOException extends IOException
{
    private String key;
    private Object msgobj;
    private static AppletMessageHandler amh;
    
    public AppletIOException(final String key) {
        super(key);
        this.key = null;
        this.msgobj = null;
        this.key = key;
    }
    
    public AppletIOException(final String s, final Object msgobj) {
        this(s);
        this.msgobj = msgobj;
    }
    
    @Override
    public String getLocalizedMessage() {
        if (this.msgobj != null) {
            return AppletIOException.amh.getMessage(this.key, this.msgobj);
        }
        return AppletIOException.amh.getMessage(this.key);
    }
    
    static {
        AppletIOException.amh = new AppletMessageHandler("appletioexception");
    }
}
