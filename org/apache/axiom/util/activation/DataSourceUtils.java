package org.apache.axiom.util.activation;

import javax.activation.FileDataSource;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import org.apache.axiom.ext.activation.SizeAwareDataSource;
import javax.activation.DataSource;

public class DataSourceUtils
{
    private static final Class byteArrayDataSourceClass;
    
    public static long getSize(final DataSource ds) {
        if (ds instanceof SizeAwareDataSource) {
            return ((SizeAwareDataSource)ds).getSize();
        }
        if (DataSourceUtils.byteArrayDataSourceClass != null && DataSourceUtils.byteArrayDataSourceClass.isInstance(ds)) {
            try {
                return ((ByteArrayInputStream)ds.getInputStream()).available();
            }
            catch (final IOException ex) {
                return -1L;
            }
        }
        if (ds instanceof FileDataSource) {
            return ((FileDataSource)ds).getFile().length();
        }
        return -1L;
    }
    
    static {
        Class clazz;
        try {
            clazz = Class.forName("javax.mail.util.ByteArrayDataSource");
        }
        catch (final ClassNotFoundException e) {
            clazz = null;
        }
        byteArrayDataSourceClass = clazz;
    }
}
