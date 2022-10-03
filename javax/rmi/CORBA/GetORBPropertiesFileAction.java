package javax.rmi.CORBA;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.Properties;
import java.security.AccessController;
import java.security.PrivilegedAction;

class GetORBPropertiesFileAction implements PrivilegedAction
{
    private boolean debug;
    
    public GetORBPropertiesFileAction() {
        this.debug = false;
    }
    
    private String getSystemProperty(final String s) {
        return AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction() {
            @Override
            public Object run() {
                return System.getProperty(s);
            }
        });
    }
    
    private void getPropertiesFromFile(final Properties properties, final String s) {
        try {
            final File file = new File(s);
            if (!file.exists()) {
                return;
            }
            final FileInputStream fileInputStream = new FileInputStream(file);
            try {
                properties.load(fileInputStream);
            }
            finally {
                fileInputStream.close();
            }
        }
        catch (final Exception ex) {
            if (this.debug) {
                System.out.println("ORB properties file " + s + " not found: " + ex);
            }
        }
    }
    
    @Override
    public Object run() {
        final Properties properties = new Properties();
        this.getPropertiesFromFile(properties, this.getSystemProperty("java.home") + File.separator + "lib" + File.separator + "orb.properties");
        final Properties properties2 = new Properties(properties);
        this.getPropertiesFromFile(properties2, this.getSystemProperty("user.home") + File.separator + "orb.properties");
        return properties2;
    }
}
