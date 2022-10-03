package com.sun.security.auth.module;

import java.security.Principal;
import java.util.Set;
import javax.security.auth.login.LoginException;
import javax.security.auth.login.FailedLoginException;
import com.sun.security.auth.NTNumericCredential;
import com.sun.security.auth.NTSidGroupPrincipal;
import com.sun.security.auth.NTSidPrimaryGroupPrincipal;
import com.sun.security.auth.NTSidDomainPrincipal;
import com.sun.security.auth.NTDomainPrincipal;
import com.sun.security.auth.NTSidUserPrincipal;
import com.sun.security.auth.NTUserPrincipal;
import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.Subject;
import jdk.Exported;
import javax.security.auth.spi.LoginModule;

@Exported
public class NTLoginModule implements LoginModule
{
    private NTSystem ntSystem;
    private Subject subject;
    private CallbackHandler callbackHandler;
    private Map<String, ?> sharedState;
    private Map<String, ?> options;
    private boolean debug;
    private boolean debugNative;
    private boolean succeeded;
    private boolean commitSucceeded;
    private NTUserPrincipal userPrincipal;
    private NTSidUserPrincipal userSID;
    private NTDomainPrincipal userDomain;
    private NTSidDomainPrincipal domainSID;
    private NTSidPrimaryGroupPrincipal primaryGroup;
    private NTSidGroupPrincipal[] groups;
    private NTNumericCredential iToken;
    
    public NTLoginModule() {
        this.debug = false;
        this.debugNative = false;
        this.succeeded = false;
        this.commitSucceeded = false;
    }
    
    @Override
    public void initialize(final Subject subject, final CallbackHandler callbackHandler, final Map<String, ?> sharedState, final Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = sharedState;
        this.options = options;
        this.debug = "true".equalsIgnoreCase((String)options.get("debug"));
        this.debugNative = "true".equalsIgnoreCase((String)options.get("debugNative"));
        if (this.debugNative) {
            this.debug = true;
        }
    }
    
    @Override
    public boolean login() throws LoginException {
        this.succeeded = false;
        this.ntSystem = new NTSystem(this.debugNative);
        if (this.ntSystem == null) {
            if (this.debug) {
                System.out.println("\t\t[NTLoginModule] Failed in NT login");
            }
            throw new FailedLoginException("Failed in attempt to import the underlying NT system identity information");
        }
        if (this.ntSystem.getName() == null) {
            throw new FailedLoginException("Failed in attempt to import the underlying NT system identity information");
        }
        this.userPrincipal = new NTUserPrincipal(this.ntSystem.getName());
        if (this.debug) {
            System.out.println("\t\t[NTLoginModule] succeeded importing info: ");
            System.out.println("\t\t\tuser name = " + this.userPrincipal.getName());
        }
        if (this.ntSystem.getUserSID() != null) {
            this.userSID = new NTSidUserPrincipal(this.ntSystem.getUserSID());
            if (this.debug) {
                System.out.println("\t\t\tuser SID = " + this.userSID.getName());
            }
        }
        if (this.ntSystem.getDomain() != null) {
            this.userDomain = new NTDomainPrincipal(this.ntSystem.getDomain());
            if (this.debug) {
                System.out.println("\t\t\tuser domain = " + this.userDomain.getName());
            }
        }
        if (this.ntSystem.getDomainSID() != null) {
            this.domainSID = new NTSidDomainPrincipal(this.ntSystem.getDomainSID());
            if (this.debug) {
                System.out.println("\t\t\tuser domain SID = " + this.domainSID.getName());
            }
        }
        if (this.ntSystem.getPrimaryGroupID() != null) {
            this.primaryGroup = new NTSidPrimaryGroupPrincipal(this.ntSystem.getPrimaryGroupID());
            if (this.debug) {
                System.out.println("\t\t\tuser primary group = " + this.primaryGroup.getName());
            }
        }
        if (this.ntSystem.getGroupIDs() != null && this.ntSystem.getGroupIDs().length > 0) {
            final String[] groupIDs = this.ntSystem.getGroupIDs();
            this.groups = new NTSidGroupPrincipal[groupIDs.length];
            for (int i = 0; i < groupIDs.length; ++i) {
                this.groups[i] = new NTSidGroupPrincipal(groupIDs[i]);
                if (this.debug) {
                    System.out.println("\t\t\tuser group = " + this.groups[i].getName());
                }
            }
        }
        if (this.ntSystem.getImpersonationToken() != 0L) {
            this.iToken = new NTNumericCredential(this.ntSystem.getImpersonationToken());
            if (this.debug) {
                System.out.println("\t\t\timpersonation token = " + this.ntSystem.getImpersonationToken());
            }
        }
        return this.succeeded = true;
    }
    
    @Override
    public boolean commit() throws LoginException {
        if (!this.succeeded) {
            if (this.debug) {
                System.out.println("\t\t[NTLoginModule]: did not add any Principals to Subject because own authentication failed.");
            }
            return false;
        }
        if (this.subject.isReadOnly()) {
            throw new LoginException("Subject is ReadOnly");
        }
        final Set<Principal> principals = this.subject.getPrincipals();
        if (!principals.contains(this.userPrincipal)) {
            principals.add(this.userPrincipal);
        }
        if (this.userSID != null && !principals.contains(this.userSID)) {
            principals.add(this.userSID);
        }
        if (this.userDomain != null && !principals.contains(this.userDomain)) {
            principals.add(this.userDomain);
        }
        if (this.domainSID != null && !principals.contains(this.domainSID)) {
            principals.add(this.domainSID);
        }
        if (this.primaryGroup != null && !principals.contains(this.primaryGroup)) {
            principals.add(this.primaryGroup);
        }
        for (int n = 0; this.groups != null && n < this.groups.length; ++n) {
            if (!principals.contains(this.groups[n])) {
                principals.add(this.groups[n]);
            }
        }
        final Set<Object> publicCredentials = this.subject.getPublicCredentials();
        if (this.iToken != null && !publicCredentials.contains(this.iToken)) {
            publicCredentials.add(this.iToken);
        }
        return this.commitSucceeded = true;
    }
    
    @Override
    public boolean abort() throws LoginException {
        if (this.debug) {
            System.out.println("\t\t[NTLoginModule]: aborted authentication attempt");
        }
        if (!this.succeeded) {
            return false;
        }
        if (this.succeeded && !this.commitSucceeded) {
            this.ntSystem = null;
            this.userPrincipal = null;
            this.userSID = null;
            this.userDomain = null;
            this.domainSID = null;
            this.primaryGroup = null;
            this.groups = null;
            this.iToken = null;
            this.succeeded = false;
        }
        else {
            this.logout();
        }
        return this.succeeded;
    }
    
    @Override
    public boolean logout() throws LoginException {
        if (this.subject.isReadOnly()) {
            throw new LoginException("Subject is ReadOnly");
        }
        final Set<Principal> principals = this.subject.getPrincipals();
        if (principals.contains(this.userPrincipal)) {
            principals.remove(this.userPrincipal);
        }
        if (principals.contains(this.userSID)) {
            principals.remove(this.userSID);
        }
        if (principals.contains(this.userDomain)) {
            principals.remove(this.userDomain);
        }
        if (principals.contains(this.domainSID)) {
            principals.remove(this.domainSID);
        }
        if (principals.contains(this.primaryGroup)) {
            principals.remove(this.primaryGroup);
        }
        for (int n = 0; this.groups != null && n < this.groups.length; ++n) {
            if (principals.contains(this.groups[n])) {
                principals.remove(this.groups[n]);
            }
        }
        final Set<Object> publicCredentials = this.subject.getPublicCredentials();
        if (publicCredentials.contains(this.iToken)) {
            publicCredentials.remove(this.iToken);
        }
        this.succeeded = false;
        this.commitSucceeded = false;
        this.userPrincipal = null;
        this.userDomain = null;
        this.userSID = null;
        this.domainSID = null;
        this.groups = null;
        this.primaryGroup = null;
        this.iToken = null;
        this.ntSystem = null;
        if (this.debug) {
            System.out.println("\t\t[NTLoginModule] completed logout processing");
        }
        return true;
    }
}
