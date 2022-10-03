package org.tanukisoftware.wrapper;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

public final class WrapperResources
{
    private static WrapperPrintStream m_outError;
    private static boolean m_validateResourceKeys;
    private static final Object[] EMPTY_OBJECT_ARRAY;
    private long m_Id;
    
    protected WrapperResources() {
    }
    
    protected void finalize() throws Throwable {
        try {
            if (WrapperManager.isLoggingFinalizers()) {
                System.out.println("WrapperResources.finalize Id=" + this.m_Id);
            }
            if (this.m_Id != 0L && WrapperManager.isNativeLibraryOk()) {
                this.nativeDestroyResource();
            }
        }
        finally {
            super.finalize();
        }
    }
    
    private native String nativeGetLocalizedString(final String p0);
    
    private native void nativeDestroyResource();
    
    private void validateResourceKey(final String str, final boolean localized) {
        int pos = 0;
        final int len = str.length();
        do {
            pos = str.indexOf(39, pos);
            if (pos < 0) {
                break;
            }
            if (++pos < len && str.charAt(pos) == '\'') {
                continue;
            }
            if (localized) {
                WrapperResources.m_outError.println(WrapperManager.getRes().getString("Localized resource string''s single quotes not escaped correctly: {0}", str));
                break;
            }
            WrapperResources.m_outError.println(WrapperManager.getRes().getString("Resource key''s single quotes not escaped correctly: {0}", str));
            break;
        } while (++pos < len);
    }
    
    private String getStringInner(final String key) {
        if (WrapperResources.m_validateResourceKeys) {
            this.validateResourceKey(key, false);
        }
        if (this.m_Id != 0L && WrapperManager.isNativeLibraryOk()) {
            final String str = this.nativeGetLocalizedString(key);
            if (!str.equals(key) && WrapperResources.m_validateResourceKeys) {
                this.validateResourceKey(str, true);
            }
            return str;
        }
        return key;
    }
    
    public String getString(final String key) {
        return MessageFormat.format(this.getStringInner(key), WrapperResources.EMPTY_OBJECT_ARRAY);
    }
    
    public String getString(final String key, final Object[] arguments) {
        return MessageFormat.format(this.getStringInner(key), arguments);
    }
    
    public String getString(final String key, final Object arg0) {
        return MessageFormat.format(this.getStringInner(key), arg0);
    }
    
    public String getString(final String key, final Object arg0, final Object arg1) {
        return MessageFormat.format(this.getStringInner(key), arg0, arg1);
    }
    
    public String getString(final String key, final Object arg0, final Object arg1, final Object arg2) {
        return MessageFormat.format(this.getStringInner(key), arg0, arg1, arg2);
    }
    
    public String getString(final String key, final Object arg0, final Object arg1, final Object arg2, final Object arg3) {
        return MessageFormat.format(this.getStringInner(key), arg0, arg1, arg2, arg3);
    }
    
    public String getString(final String key, final Object arg0, final Object arg1, final Object arg2, final Object arg3, final Object arg4) {
        return MessageFormat.format(this.getStringInner(key), arg0, arg1, arg2, arg3, arg4);
    }
    
    static {
        EMPTY_OBJECT_ARRAY = new Object[0];
        boolean streamSet = false;
        if ("true".equals(System.getProperty("wrapper.use_sun_encoding"))) {
            final String sunStdoutEncoding = System.getProperty("sun.stdout.encoding");
            if (sunStdoutEncoding != null && !sunStdoutEncoding.equals(System.getProperty("file.encoding"))) {
                try {
                    WrapperResources.m_outError = new WrapperPrintStream(System.out, false, sunStdoutEncoding, "WrapperResources Error: ");
                    streamSet = true;
                }
                catch (final UnsupportedEncodingException e) {
                    System.out.println(WrapperManager.getRes().getString("Failed to set the encoding '{0}' when creating a WrapperPrintStream.\n Make sure the value of sun.stdout.encoding is correct.", sunStdoutEncoding));
                }
            }
        }
        if (!streamSet) {
            WrapperResources.m_outError = new WrapperPrintStream(System.out, "WrapperResources Error: ");
        }
        WrapperResources.m_validateResourceKeys = WrapperSystemPropertyUtil.getBooleanProperty(WrapperResources.class.getName() + ".validateResourceKeys", false);
    }
}
