package org.apache.poi.hssf.record.crypto;

public final class Biff8EncryptionKey
{
    private static final ThreadLocal<String> _userPasswordTLS;
    
    public static void setCurrentUserPassword(final String password) {
        if (password == null) {
            Biff8EncryptionKey._userPasswordTLS.remove();
        }
        else {
            Biff8EncryptionKey._userPasswordTLS.set(password);
        }
    }
    
    public static String getCurrentUserPassword() {
        return Biff8EncryptionKey._userPasswordTLS.get();
    }
    
    static {
        _userPasswordTLS = new ThreadLocal<String>();
    }
}
