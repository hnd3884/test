package com.sun.corba.se.impl.resolver;

import java.io.IOException;
import java.io.FileNotFoundException;
import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import org.omg.CORBA.Object;
import java.util.Properties;
import java.io.File;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.resolver.Resolver;

public class FileResolverImpl implements Resolver
{
    private ORB orb;
    private File file;
    private Properties savedProps;
    private long fileModified;
    
    public FileResolverImpl(final ORB orb, final File file) {
        this.fileModified = 0L;
        this.orb = orb;
        this.file = file;
        this.savedProps = new Properties();
    }
    
    @Override
    public org.omg.CORBA.Object resolve(final String s) {
        this.check();
        final String property = this.savedProps.getProperty(s);
        if (property == null) {
            return null;
        }
        return this.orb.string_to_object(property);
    }
    
    @Override
    public Set list() {
        this.check();
        final HashSet set = new HashSet();
        final Enumeration<?> propertyNames = this.savedProps.propertyNames();
        while (propertyNames.hasMoreElements()) {
            set.add(propertyNames.nextElement());
        }
        return set;
    }
    
    private void check() {
        if (this.file == null) {
            return;
        }
        final long lastModified = this.file.lastModified();
        if (lastModified > this.fileModified) {
            try {
                final FileInputStream fileInputStream = new FileInputStream(this.file);
                this.savedProps.clear();
                this.savedProps.load(fileInputStream);
                fileInputStream.close();
                this.fileModified = lastModified;
            }
            catch (final FileNotFoundException ex) {
                System.err.println(CorbaResourceUtil.getText("bootstrap.filenotfound", this.file.getAbsolutePath()));
            }
            catch (final IOException ex2) {
                System.err.println(CorbaResourceUtil.getText("bootstrap.exception", this.file.getAbsolutePath(), ex2.toString()));
            }
        }
    }
}
