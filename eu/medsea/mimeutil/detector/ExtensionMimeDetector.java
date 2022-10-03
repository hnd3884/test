package eu.medsea.mimeutil.detector;

import java.util.Iterator;
import java.util.Enumeration;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil;
import java.util.HashSet;
import java.net.URL;
import eu.medsea.mimeutil.MimeException;
import java.util.Collection;
import java.io.File;
import java.util.Map;
import eu.medsea.mimeutil.MimeUtil2;

public class ExtensionMimeDetector extends MimeDetector
{
    private static final MimeUtil2.MimeLogger log;
    private static Map extMimeTypes;
    
    public ExtensionMimeDetector() {
        initMimeTypes();
    }
    
    @Override
    public String getDescription() {
        return "Get the mime types of file extensions";
    }
    
    public Collection getMimeTypesFile(final File file) throws MimeException {
        return this.getMimeTypesFileName(file.getName());
    }
    
    public Collection getMimeTypesURL(final URL url) throws MimeException {
        return this.getMimeTypesFileName(url.getPath());
    }
    
    public Collection getMimeTypesFileName(final String fileName) throws MimeException {
        final Collection mimeTypes = new HashSet();
        for (String fileExtension = MimeUtil.getExtension(fileName); fileExtension.length() != 0; fileExtension = MimeUtil.getExtension(fileExtension)) {
            String types = null;
            types = ExtensionMimeDetector.extMimeTypes.get(fileExtension);
            if (types != null) {
                final String[] mimeTypeArray = types.split(",");
                for (int i = 0; i < mimeTypeArray.length; ++i) {
                    mimeTypes.add(new MimeType(mimeTypeArray[i]));
                }
                return mimeTypes;
            }
            if (mimeTypes.isEmpty()) {
                types = ExtensionMimeDetector.extMimeTypes.get(fileExtension.toLowerCase());
                if (types != null) {
                    final String[] mimeTypeArray = types.split(",");
                    for (int i = 0; i < mimeTypeArray.length; ++i) {
                        mimeTypes.add(new MimeType(mimeTypeArray[i]));
                    }
                    return mimeTypes;
                }
            }
        }
        return mimeTypes;
    }
    
    private static void initMimeTypes() {
        InputStream is = null;
        ExtensionMimeDetector.extMimeTypes = new Properties();
        try {
            try {
                is = MimeUtil.class.getClassLoader().getResourceAsStream("eu/medsea/mimeutil/mime-types.properties");
                if (is != null) {
                    ((Properties)ExtensionMimeDetector.extMimeTypes).load(is);
                }
            }
            catch (final Exception e) {
                ExtensionMimeDetector.log.error("Error loading internal mime-types.properties", e);
            }
            finally {
                is = MimeDetector.closeStream(is);
            }
            try {
                final File f = new File(System.getProperty("user.home") + File.separator + ".mime-types.properties");
                if (f.exists()) {
                    is = new FileInputStream(f);
                    if (is != null) {
                        ExtensionMimeDetector.log.debug("Found a custom .mime-types.properties file in the users home directory.");
                        final Properties props = new Properties();
                        props.load(is);
                        if (props.size() > 0) {
                            ExtensionMimeDetector.extMimeTypes.putAll(props);
                        }
                        ExtensionMimeDetector.log.debug("Successfully parsed .mime-types.properties from users home directory.");
                    }
                }
            }
            catch (final Exception e) {
                ExtensionMimeDetector.log.error("Failed to parse .magic.mime file from users home directory. File will be ignored.", e);
            }
            finally {
                is = MimeDetector.closeStream(is);
            }
            try {
                final Enumeration e2 = MimeUtil.class.getClassLoader().getResources("mime-types.properties");
                while (e2.hasMoreElements()) {
                    final URL url = e2.nextElement();
                    if (ExtensionMimeDetector.log.isDebugEnabled()) {
                        ExtensionMimeDetector.log.debug("Found custom mime-types.properties file on the classpath [" + url + "].");
                    }
                    final Properties props2 = new Properties();
                    try {
                        is = url.openStream();
                        if (is == null) {
                            continue;
                        }
                        props2.load(is);
                        if (props2.size() <= 0) {
                            continue;
                        }
                        ExtensionMimeDetector.extMimeTypes.putAll(props2);
                        if (!ExtensionMimeDetector.log.isDebugEnabled()) {
                            continue;
                        }
                        ExtensionMimeDetector.log.debug("Successfully loaded custome mime-type.properties file [" + url + "] from classpath.");
                    }
                    catch (final Exception ex) {
                        ExtensionMimeDetector.log.error("Failed while loading custom mime-type.properties file [" + url + "] from classpath. File will be ignored.");
                    }
                }
            }
            catch (final Exception e) {
                ExtensionMimeDetector.log.error("Problem while processing mime-types.properties files(s) from classpath. Files will be ignored.", e);
            }
            finally {
                is = MimeDetector.closeStream(is);
            }
            try {
                final String fname = System.getProperty("mime-mappings");
                if (fname != null && fname.length() != 0) {
                    is = new FileInputStream(fname);
                    if (is != null) {
                        if (ExtensionMimeDetector.log.isDebugEnabled()) {
                            ExtensionMimeDetector.log.debug("Found a custom mime-mappings property defined by the property -Dmime-mappings [" + System.getProperty("mime-mappings") + "].");
                        }
                        final Properties props = new Properties();
                        props.load(is);
                        if (props.size() > 0) {
                            ExtensionMimeDetector.extMimeTypes.putAll(props);
                        }
                        ExtensionMimeDetector.log.debug("Successfully loaded the mime mappings file from property -Dmime-mappings [" + System.getProperty("mime-mappings") + "].");
                    }
                }
            }
            catch (final Exception ex2) {
                ExtensionMimeDetector.log.error("Failed to load the mime-mappings file defined by the property -Dmime-mappings [" + System.getProperty("mime-mappings") + "].");
            }
            finally {
                is = MimeDetector.closeStream(is);
            }
        }
        finally {
            final Iterator it = ExtensionMimeDetector.extMimeTypes.values().iterator();
            while (it.hasNext()) {
                final String[] types = it.next().split(",");
                for (int i = 0; i < types.length; ++i) {
                    MimeUtil.addKnownMimeType(types[i]);
                }
            }
        }
    }
    
    public Collection getMimeTypesInputStream(final InputStream in) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("This MimeDetector does not support detection from streams.");
    }
    
    public Collection getMimeTypesByteArray(final byte[] data) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("This MimeDetector does not support detection from byte arrays.");
    }
    
    static {
        log = new MimeUtil2.MimeLogger(ExtensionMimeDetector.class.getName());
    }
}
