package sun.nio.fs;

import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.security.Permission;
import java.io.IOException;
import java.nio.file.attribute.UserPrincipal;

class WindowsUserPrincipals
{
    private WindowsUserPrincipals() {
    }
    
    static UserPrincipal fromSid(final long n) throws IOException {
        String convertSidToStringSid;
        try {
            convertSidToStringSid = WindowsNativeDispatcher.ConvertSidToStringSid(n);
            if (convertSidToStringSid == null) {
                throw new AssertionError();
            }
        }
        catch (final WindowsException ex) {
            throw new IOException("Unable to convert SID to String: " + ex.errorString());
        }
        WindowsNativeDispatcher.Account lookupAccountSid = null;
        String string;
        try {
            lookupAccountSid = WindowsNativeDispatcher.LookupAccountSid(n);
            string = lookupAccountSid.domain() + "\\" + lookupAccountSid.name();
        }
        catch (final WindowsException ex2) {
            string = convertSidToStringSid;
        }
        final int n2 = (lookupAccountSid == null) ? 8 : lookupAccountSid.use();
        if (n2 == 2 || n2 == 5 || n2 == 4) {
            return new Group(convertSidToStringSid, n2, string);
        }
        return new User(convertSidToStringSid, n2, string);
    }
    
    static UserPrincipal lookup(final String s) throws IOException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new RuntimePermission("lookupUserInformation"));
        }
        int lookupAccountName;
        try {
            lookupAccountName = WindowsNativeDispatcher.LookupAccountName(s, 0L, 0);
        }
        catch (final WindowsException ex) {
            if (ex.lastError() == 1332) {
                throw new UserPrincipalNotFoundException(s);
            }
            throw new IOException(s + ": " + ex.errorString());
        }
        assert lookupAccountName > 0;
        final NativeBuffer nativeBuffer = NativeBuffers.getNativeBuffer(lookupAccountName);
        try {
            if (WindowsNativeDispatcher.LookupAccountName(s, nativeBuffer.address(), lookupAccountName) != lookupAccountName) {
                throw new AssertionError((Object)"SID change during lookup");
            }
            return fromSid(nativeBuffer.address());
        }
        catch (final WindowsException ex2) {
            throw new IOException(s + ": " + ex2.errorString());
        }
        finally {
            nativeBuffer.release();
        }
    }
    
    static class User implements UserPrincipal
    {
        private final String sidString;
        private final int sidType;
        private final String accountName;
        
        User(final String sidString, final int sidType, final String accountName) {
            this.sidString = sidString;
            this.sidType = sidType;
            this.accountName = accountName;
        }
        
        String sidString() {
            return this.sidString;
        }
        
        @Override
        public String getName() {
            return this.accountName;
        }
        
        @Override
        public String toString() {
            String s = null;
            switch (this.sidType) {
                case 1: {
                    s = "User";
                    break;
                }
                case 2: {
                    s = "Group";
                    break;
                }
                case 3: {
                    s = "Domain";
                    break;
                }
                case 4: {
                    s = "Alias";
                    break;
                }
                case 5: {
                    s = "Well-known group";
                    break;
                }
                case 6: {
                    s = "Deleted";
                    break;
                }
                case 7: {
                    s = "Invalid";
                    break;
                }
                case 9: {
                    s = "Computer";
                    break;
                }
                default: {
                    s = "Unknown";
                    break;
                }
            }
            return this.accountName + " (" + s + ")";
        }
        
        @Override
        public boolean equals(final Object o) {
            return o == this || (o instanceof User && this.sidString.equals(((User)o).sidString));
        }
        
        @Override
        public int hashCode() {
            return this.sidString.hashCode();
        }
    }
    
    static class Group extends User implements GroupPrincipal
    {
        Group(final String s, final int n, final String s2) {
            super(s, n, s2);
        }
    }
}
