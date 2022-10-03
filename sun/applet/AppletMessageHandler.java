package sun.applet;

import java.util.MissingResourceException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

class AppletMessageHandler
{
    private static ResourceBundle rb;
    private String baseKey;
    
    AppletMessageHandler(final String baseKey) {
        this.baseKey = null;
        this.baseKey = baseKey;
    }
    
    String getMessage(final String s) {
        return AppletMessageHandler.rb.getString(this.getQualifiedKey(s));
    }
    
    String getMessage(final String s, Object o) {
        final MessageFormat messageFormat = new MessageFormat(AppletMessageHandler.rb.getString(this.getQualifiedKey(s)));
        final Object[] array = { null };
        if (o == null) {
            o = "null";
        }
        array[0] = o;
        return messageFormat.format(array);
    }
    
    String getMessage(final String s, Object o, Object o2) {
        final MessageFormat messageFormat = new MessageFormat(AppletMessageHandler.rb.getString(this.getQualifiedKey(s)));
        final Object[] array = new Object[2];
        if (o == null) {
            o = "null";
        }
        if (o2 == null) {
            o2 = "null";
        }
        array[0] = o;
        array[1] = o2;
        return messageFormat.format(array);
    }
    
    String getMessage(final String s, Object o, Object o2, Object o3) {
        final MessageFormat messageFormat = new MessageFormat(AppletMessageHandler.rb.getString(this.getQualifiedKey(s)));
        final Object[] array = new Object[3];
        if (o == null) {
            o = "null";
        }
        if (o2 == null) {
            o2 = "null";
        }
        if (o3 == null) {
            o3 = "null";
        }
        array[0] = o;
        array[1] = o2;
        array[2] = o3;
        return messageFormat.format(array);
    }
    
    String getMessage(final String s, final Object[] array) {
        return new MessageFormat(AppletMessageHandler.rb.getString(this.getQualifiedKey(s))).format(array);
    }
    
    String getQualifiedKey(final String s) {
        return this.baseKey + "." + s;
    }
    
    static {
        try {
            AppletMessageHandler.rb = ResourceBundle.getBundle("sun.applet.resources.MsgAppletViewer");
        }
        catch (final MissingResourceException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }
}
