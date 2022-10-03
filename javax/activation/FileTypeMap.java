package javax.activation;

import java.io.File;

public abstract class FileTypeMap
{
    private static FileTypeMap defaultMap;
    static /* synthetic */ Class class$javax$activation$FileTypeMap;
    
    static {
        FileTypeMap.defaultMap = null;
    }
    
    static /* synthetic */ Class class$(final String s) {
        try {
            return Class.forName(s);
        }
        catch (final ClassNotFoundException ex) {
            throw new NoClassDefFoundError(ex.getMessage());
        }
    }
    
    public abstract String getContentType(final File p0);
    
    public abstract String getContentType(final String p0);
    
    public static FileTypeMap getDefaultFileTypeMap() {
        if (FileTypeMap.defaultMap == null) {
            FileTypeMap.defaultMap = new MimetypesFileTypeMap();
        }
        return FileTypeMap.defaultMap;
    }
    
    public static void setDefaultFileTypeMap(final FileTypeMap defaultMap) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            try {
                securityManager.checkSetFactory();
            }
            catch (final SecurityException ex) {
                if (((FileTypeMap.class$javax$activation$FileTypeMap != null) ? FileTypeMap.class$javax$activation$FileTypeMap : (FileTypeMap.class$javax$activation$FileTypeMap = class$("javax.activation.FileTypeMap"))).getClassLoader() != defaultMap.getClass().getClassLoader()) {
                    throw ex;
                }
            }
        }
        FileTypeMap.defaultMap = defaultMap;
    }
}
