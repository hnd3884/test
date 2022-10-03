package org.tanukisoftware.wrapper;

import java.util.HashMap;
import java.util.Iterator;
import java.io.IOException;
import java.util.Map;
import java.io.File;

public final class WrapperProcessConfig
{
    public static final int POSIX_SPAWN = 1;
    public static final int FORK_EXEC = 2;
    public static final int VFORK_EXEC = 3;
    public static final int DYNAMIC = 4;
    private boolean m_isDetached;
    private boolean m_isInteractive;
    private File m_defdir;
    private int m_startType;
    private Map m_environment;
    private int m_softShutdownTimeout;
    
    private native String[] nativeGetEnv();
    
    private static native boolean isSupportedNative(final int p0);
    
    public WrapperProcessConfig() {
        WrapperManager.assertProfessionalEdition();
        this.m_isDetached = false;
        this.m_defdir = null;
        this.m_startType = 4;
        this.m_environment = null;
        this.m_softShutdownTimeout = 5;
        this.m_isInteractive = false;
    }
    
    public static boolean isSupported(final int startType) throws WrapperLicenseError, IllegalArgumentException {
        WrapperManager.assertProfessionalEdition();
        verifyStartType(startType);
        return WrapperManager.isNativeLibraryOk() && isSupportedNative(startType);
    }
    
    public boolean isDetached() {
        return this.m_isDetached;
    }
    
    public WrapperProcessConfig setDetached(final boolean detached) {
        this.m_isDetached = detached;
        return this;
    }
    
    public int getStartType() {
        return this.m_startType;
    }
    
    public WrapperProcessConfig setStartType(final int startType) throws IllegalArgumentException {
        verifyStartType(startType);
        this.m_startType = startType;
        return this;
    }
    
    public File getWorkingDirectory() {
        return this.m_defdir;
    }
    
    public WrapperProcessConfig setWorkingDirectory(final File workingDirectory) throws IOException {
        if (workingDirectory != null) {
            if (!workingDirectory.exists()) {
                throw new IllegalArgumentException(WrapperManager.getRes().getString("Working directory does not exist."));
            }
            if (!workingDirectory.isDirectory()) {
                throw new IllegalArgumentException(WrapperManager.getRes().getString("Must be a directory."));
            }
        }
        this.m_defdir = workingDirectory.getCanonicalFile();
        return this;
    }
    
    public Map getEnvironment() throws WrapperLicenseError {
        if (this.m_environment == null) {
            this.m_environment = this.getDefaultEnvironment();
        }
        return this.m_environment;
    }
    
    public WrapperProcessConfig setEnvironment(final Map environment) {
        if (environment != null) {
            final Iterator iter = environment.entrySet().iterator();
            while (iter.hasNext()) {
                final Map.Entry entry = iter.next();
                final Object key = entry.getKey();
                if (!(key instanceof String)) {
                    throw new IllegalArgumentException(WrapperManager.getRes().getString("Map entry names must be Strings."));
                }
                if (((String)key).length() <= 0) {
                    throw new IllegalArgumentException(WrapperManager.getRes().getString("Map entry names must not be empty Strings."));
                }
                if (((String)key).indexOf(61) != -1) {
                    throw new IllegalArgumentException(WrapperManager.getRes().getString("Map entry names must not contain an equal sign (''='')."));
                }
                final Object value = entry.getKey();
                if (!(value instanceof String)) {
                    throw new IllegalArgumentException(WrapperManager.getRes().getString("Map entry values must be Strings."));
                }
            }
        }
        this.m_environment = environment;
        return this;
    }
    
    public WrapperProcessConfig setSoftShutdownTimeout(final int softShutdownTimeout) {
        if (softShutdownTimeout < -1) {
            throw new IllegalArgumentException(WrapperManager.getRes().getString("{0} is not a valid value for a timeout.", new Integer(softShutdownTimeout)));
        }
        this.m_softShutdownTimeout = softShutdownTimeout;
        return this;
    }
    
    private static void verifyStartType(final int startType) throws IllegalArgumentException {
        switch (startType) {
            case 1:
            case 2:
            case 3:
            case 4: {
                return;
            }
            default: {
                throw new IllegalArgumentException(WrapperManager.getRes().getString("Unknown start type: {0}", new Integer(startType)));
            }
        }
    }
    
    private Map getDefaultEnvironment() {
        final Map environment = new HashMap();
        if (WrapperManager.isNativeLibraryOk()) {
            final String[] nativeEnv = this.nativeGetEnv();
            for (int i = 0; i < nativeEnv.length; ++i) {
                final int pos = nativeEnv[i].indexOf(61);
                final String name = nativeEnv[i].substring(0, pos);
                final String value = nativeEnv[i].substring(pos + 1);
                environment.put(name, value);
            }
        }
        return environment;
    }
    
    private String[] getNativeEnv() {
        if (this.m_environment != null) {
            final String[] nativeEnv = new String[this.m_environment.size()];
            final Iterator iter = this.m_environment.entrySet().iterator();
            int i = 0;
            while (iter.hasNext()) {
                final Map.Entry pairs = iter.next();
                nativeEnv[i++] = pairs.getKey() + "=" + pairs.getValue();
            }
            return nativeEnv;
        }
        if (WrapperManager.isNativeLibraryOk()) {
            return this.nativeGetEnv();
        }
        return new String[0];
    }
    
    public WrapperProcessConfig setCreateForActiveUser(final boolean isInteractive) {
        if (WrapperManager.isWindows() && WrapperManager.isLaunchedAsService()) {
            this.m_isInteractive = true;
        }
        else {
            this.m_isInteractive = false;
        }
        return this;
    }
    
    public boolean isCreateForActiveUser() {
        return this.m_isInteractive;
    }
}
