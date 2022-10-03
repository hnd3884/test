package sun.nio.fs;

import java.nio.file.ProviderMismatchException;
import java.nio.file.attribute.AclEntry;
import java.util.List;
import java.nio.file.attribute.UserPrincipal;
import java.io.IOException;
import java.security.Permission;

class WindowsAclFileAttributeView extends AbstractAclFileAttributeView
{
    private static final short SIZEOF_SECURITY_DESCRIPTOR = 20;
    private final WindowsPath file;
    private final boolean followLinks;
    
    WindowsAclFileAttributeView(final WindowsPath file, final boolean followLinks) {
        this.file = file;
        this.followLinks = followLinks;
    }
    
    private void checkAccess(final WindowsPath windowsPath, final boolean b, final boolean b2) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            if (b) {
                securityManager.checkRead(windowsPath.getPathForPermissionCheck());
            }
            if (b2) {
                securityManager.checkWrite(windowsPath.getPathForPermissionCheck());
            }
            securityManager.checkPermission(new RuntimePermission("accessUserInformation"));
        }
    }
    
    static NativeBuffer getFileSecurity(final String s, final int n) throws IOException {
        int getFileSecurity = 0;
        try {
            getFileSecurity = WindowsNativeDispatcher.GetFileSecurity(s, n, 0L, 0);
        }
        catch (final WindowsException ex) {
            ex.rethrowAsIOException(s);
        }
        assert getFileSecurity > 0;
        NativeBuffer nativeBuffer = NativeBuffers.getNativeBuffer(getFileSecurity);
        try {
            while (true) {
                final int getFileSecurity2 = WindowsNativeDispatcher.GetFileSecurity(s, n, nativeBuffer.address(), getFileSecurity);
                if (getFileSecurity2 <= getFileSecurity) {
                    break;
                }
                nativeBuffer.release();
                nativeBuffer = NativeBuffers.getNativeBuffer(getFileSecurity2);
                getFileSecurity = getFileSecurity2;
            }
            return nativeBuffer;
        }
        catch (final WindowsException ex2) {
            nativeBuffer.release();
            ex2.rethrowAsIOException(s);
            return null;
        }
    }
    
    @Override
    public UserPrincipal getOwner() throws IOException {
        this.checkAccess(this.file, true, false);
        final NativeBuffer fileSecurity = getFileSecurity(WindowsLinkSupport.getFinalPath(this.file, this.followLinks), 1);
        try {
            final long getSecurityDescriptorOwner = WindowsNativeDispatcher.GetSecurityDescriptorOwner(fileSecurity.address());
            if (getSecurityDescriptorOwner == 0L) {
                throw new IOException("no owner");
            }
            return WindowsUserPrincipals.fromSid(getSecurityDescriptorOwner);
        }
        catch (final WindowsException ex) {
            ex.rethrowAsIOException(this.file);
            return null;
        }
        finally {
            fileSecurity.release();
        }
    }
    
    @Override
    public List<AclEntry> getAcl() throws IOException {
        this.checkAccess(this.file, true, false);
        final NativeBuffer fileSecurity = getFileSecurity(WindowsLinkSupport.getFinalPath(this.file, this.followLinks), 4);
        try {
            return WindowsSecurityDescriptor.getAcl(fileSecurity.address());
        }
        finally {
            fileSecurity.release();
        }
    }
    
    @Override
    public void setOwner(final UserPrincipal userPrincipal) throws IOException {
        if (userPrincipal == null) {
            throw new NullPointerException("'owner' is null");
        }
        if (!(userPrincipal instanceof WindowsUserPrincipals.User)) {
            throw new ProviderMismatchException();
        }
        final WindowsUserPrincipals.User user = (WindowsUserPrincipals.User)userPrincipal;
        this.checkAccess(this.file, false, true);
        final String finalPath = WindowsLinkSupport.getFinalPath(this.file, this.followLinks);
        long convertStringSidToSid;
        try {
            convertStringSidToSid = WindowsNativeDispatcher.ConvertStringSidToSid(user.sidString());
        }
        catch (final WindowsException ex) {
            throw new IOException("Failed to get SID for " + user.getName() + ": " + ex.errorString());
        }
        try {
            final NativeBuffer nativeBuffer = NativeBuffers.getNativeBuffer(20);
            try {
                WindowsNativeDispatcher.InitializeSecurityDescriptor(nativeBuffer.address());
                WindowsNativeDispatcher.SetSecurityDescriptorOwner(nativeBuffer.address(), convertStringSidToSid);
                final WindowsSecurity.Privilege enablePrivilege = WindowsSecurity.enablePrivilege("SeRestorePrivilege");
                try {
                    WindowsNativeDispatcher.SetFileSecurity(finalPath, 1, nativeBuffer.address());
                }
                finally {
                    enablePrivilege.drop();
                }
            }
            catch (final WindowsException ex2) {
                ex2.rethrowAsIOException(this.file);
            }
            finally {
                nativeBuffer.release();
            }
        }
        finally {
            WindowsNativeDispatcher.LocalFree(convertStringSidToSid);
        }
    }
    
    @Override
    public void setAcl(final List<AclEntry> list) throws IOException {
        this.checkAccess(this.file, false, true);
        final String finalPath = WindowsLinkSupport.getFinalPath(this.file, this.followLinks);
        final WindowsSecurityDescriptor create = WindowsSecurityDescriptor.create(list);
        try {
            WindowsNativeDispatcher.SetFileSecurity(finalPath, 4, create.address());
        }
        catch (final WindowsException ex) {
            ex.rethrowAsIOException(this.file);
        }
        finally {
            create.release();
        }
    }
}
