package sun.misc;

import java.util.Hashtable;
import java.util.jar.Manifest;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.io.IOException;
import java.util.Iterator;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;

public class VMSupport
{
    private static Properties agentProps;
    
    public static synchronized Properties getAgentProperties() {
        if (VMSupport.agentProps == null) {
            initAgentProperties(VMSupport.agentProps = new Properties());
        }
        return VMSupport.agentProps;
    }
    
    private static native Properties initAgentProperties(final Properties p0);
    
    private static byte[] serializePropertiesToByteArray(final Properties properties) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(4096);
        final Properties properties2 = new Properties();
        for (final String s : properties.stringPropertyNames()) {
            ((Hashtable<String, String>)properties2).put(s, properties.getProperty(s));
        }
        properties2.store(byteArrayOutputStream, null);
        return byteArrayOutputStream.toByteArray();
    }
    
    public static byte[] serializePropertiesToByteArray() throws IOException {
        return serializePropertiesToByteArray(System.getProperties());
    }
    
    public static byte[] serializeAgentPropertiesToByteArray() throws IOException {
        return serializePropertiesToByteArray(getAgentProperties());
    }
    
    public static boolean isClassPathAttributePresent(final String s) {
        try {
            final Manifest manifest = new JarFile(s).getManifest();
            return manifest != null && manifest.getMainAttributes().getValue(Attributes.Name.CLASS_PATH) != null;
        }
        catch (final IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
    
    public static native String getVMTemporaryDirectory();
    
    static {
        VMSupport.agentProps = null;
    }
}
