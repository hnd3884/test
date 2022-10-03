package com.unboundid.util.ssl.cert;

import com.unboundid.util.StaticUtils;
import java.security.Permission;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
final class ManageCertificatesSecurityManager extends SecurityManager
{
    private volatile boolean exitCalledWithNonZeroStatus;
    private volatile boolean exitCalledWithZeroStatus;
    private final SecurityManager delegateSecurityManager;
    
    ManageCertificatesSecurityManager() {
        this.delegateSecurityManager = System.getSecurityManager();
        this.exitCalledWithZeroStatus = false;
        this.exitCalledWithNonZeroStatus = false;
    }
    
    @Override
    public void checkExit(final int status) throws SecurityException {
        if (status == 0) {
            this.exitCalledWithZeroStatus = true;
        }
        else {
            this.exitCalledWithNonZeroStatus = true;
        }
        throw new SecurityException(CertMessages.ERR_MANAGE_CERTS_SECURITY_MANAGER_EXIT_NOT_ALLOWED.get());
    }
    
    @Override
    public void checkPermission(final Permission permission) throws SecurityException {
        if (permission == null || permission.getName() == null) {
            if (this.delegateSecurityManager != null) {
                this.delegateSecurityManager.checkPermission(permission);
            }
            return;
        }
        final String permissionName = StaticUtils.toLowerCase(permission.getName());
        if (permissionName.equals("exitvm") || permissionName.equals("exitvm.0")) {
            this.exitCalledWithZeroStatus = true;
            throw new SecurityException(CertMessages.ERR_MANAGE_CERTS_SECURITY_MANAGER_EXIT_NOT_ALLOWED.get());
        }
        if (permissionName.startsWith("exitvm.")) {
            this.exitCalledWithNonZeroStatus = true;
            throw new SecurityException(CertMessages.ERR_MANAGE_CERTS_SECURITY_MANAGER_EXIT_NOT_ALLOWED.get());
        }
        if (this.delegateSecurityManager != null) {
            this.delegateSecurityManager.checkPermission(permission);
        }
    }
    
    boolean exitCalledWithZeroStatus() {
        return this.exitCalledWithZeroStatus;
    }
    
    boolean exitCalledWithNonZeroStatus() {
        return this.exitCalledWithNonZeroStatus;
    }
}
