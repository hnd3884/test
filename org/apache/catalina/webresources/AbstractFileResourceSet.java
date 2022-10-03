package org.apache.catalina.webresources;

import org.apache.juli.logging.LogFactory;
import org.apache.catalina.LifecycleException;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.tomcat.util.http.RequestUtil;
import java.io.IOException;
import org.apache.tomcat.util.compat.JrePlatform;
import java.io.File;
import org.apache.juli.logging.Log;

public abstract class AbstractFileResourceSet extends AbstractResourceSet
{
    private static final Log log;
    protected static final String[] EMPTY_STRING_ARRAY;
    private File fileBase;
    private String absoluteBase;
    private String canonicalBase;
    private boolean readOnly;
    
    protected AbstractFileResourceSet(final String internalPath) {
        this.readOnly = false;
        this.setInternalPath(internalPath);
    }
    
    protected final File getFileBase() {
        return this.fileBase;
    }
    
    @Override
    public void setReadOnly(final boolean readOnly) {
        this.readOnly = readOnly;
    }
    
    @Override
    public boolean isReadOnly() {
        return this.readOnly;
    }
    
    protected final File file(String name, final boolean mustExist) {
        if (name.equals("/")) {
            name = "";
        }
        final File file = new File(this.fileBase, name);
        if (name.endsWith("/") && file.isFile()) {
            return null;
        }
        if (mustExist && !file.canRead()) {
            return null;
        }
        if (this.getRoot().getAllowLinking()) {
            return file;
        }
        if (JrePlatform.IS_WINDOWS && this.isInvalidWindowsFilename(name)) {
            return null;
        }
        String canPath = null;
        try {
            canPath = file.getCanonicalPath();
        }
        catch (final IOException ex) {}
        if (canPath == null || !canPath.startsWith(this.canonicalBase)) {
            return null;
        }
        String absPath = this.normalize(file.getAbsolutePath());
        if (this.absoluteBase.length() > absPath.length()) {
            return null;
        }
        absPath = absPath.substring(this.absoluteBase.length());
        canPath = canPath.substring(this.canonicalBase.length());
        if (canPath.length() > 0) {
            canPath = this.normalize(canPath);
        }
        if (!canPath.equals(absPath)) {
            if (!canPath.equalsIgnoreCase(absPath)) {
                this.logIgnoredSymlink(this.getRoot().getContext().getName(), absPath, canPath);
            }
            return null;
        }
        return file;
    }
    
    protected void logIgnoredSymlink(final String contextPath, final String absPath, final String canPath) {
        final String msg = AbstractFileResourceSet.sm.getString("abstractFileResourceSet.canonicalfileCheckFailed", new Object[] { contextPath, absPath, canPath });
        if (absPath.startsWith("/META-INF/") || absPath.startsWith("/WEB-INF/")) {
            AbstractFileResourceSet.log.error((Object)msg);
        }
        else {
            AbstractFileResourceSet.log.warn((Object)msg);
        }
    }
    
    private boolean isInvalidWindowsFilename(final String name) {
        final int len = name.length();
        if (len == 0) {
            return false;
        }
        for (int i = 0; i < len; ++i) {
            final char c = name.charAt(i);
            if (c == '\"' || c == '<' || c == '>' || c == ':') {
                return true;
            }
        }
        return name.charAt(len - 1) == ' ';
    }
    
    private String normalize(final String path) {
        return RequestUtil.normalize(path, File.separatorChar == '\\');
    }
    
    @Override
    public URL getBaseUrl() {
        try {
            return this.getFileBase().toURI().toURL();
        }
        catch (final MalformedURLException e) {
            return null;
        }
    }
    
    @Override
    public void gc() {
    }
    
    @Override
    protected void initInternal() throws LifecycleException {
        this.checkType(this.fileBase = new File(this.getBase(), this.getInternalPath()));
        this.absoluteBase = this.normalize(this.fileBase.getAbsolutePath());
        try {
            this.canonicalBase = this.fileBase.getCanonicalPath();
        }
        catch (final IOException e) {
            throw new IllegalArgumentException(e);
        }
        if ("/".equals(this.absoluteBase)) {
            this.absoluteBase = "";
        }
        if ("/".equals(this.canonicalBase)) {
            this.canonicalBase = "";
        }
    }
    
    protected abstract void checkType(final File p0);
    
    static {
        log = LogFactory.getLog((Class)AbstractFileResourceSet.class);
        EMPTY_STRING_ARRAY = new String[0];
    }
}
