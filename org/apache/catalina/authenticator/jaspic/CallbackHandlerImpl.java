package org.apache.catalina.authenticator.jaspic;

import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import org.apache.catalina.realm.GenericPrincipal;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import javax.security.auth.Subject;
import java.security.Principal;
import javax.security.auth.message.callback.PasswordValidationCallback;
import javax.security.auth.message.callback.GroupPrincipalCallback;
import javax.security.auth.message.callback.CallerPrincipalCallback;
import javax.security.auth.callback.Callback;
import org.apache.juli.logging.LogFactory;
import org.apache.catalina.Container;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.res.StringManager;
import org.apache.catalina.Contained;
import javax.security.auth.callback.CallbackHandler;

public class CallbackHandlerImpl implements CallbackHandler, Contained
{
    private static final StringManager sm;
    private final Log log;
    private Container container;
    
    public CallbackHandlerImpl() {
        this.log = LogFactory.getLog((Class)CallbackHandlerImpl.class);
    }
    
    @Override
    public void handle(final Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        String name = null;
        Principal principal = null;
        Subject subject = null;
        String[] groups = null;
        if (callbacks != null) {
            for (final Callback callback : callbacks) {
                if (callback instanceof CallerPrincipalCallback) {
                    final CallerPrincipalCallback cpc = (CallerPrincipalCallback)callback;
                    name = cpc.getName();
                    principal = cpc.getPrincipal();
                    subject = cpc.getSubject();
                }
                else if (callback instanceof GroupPrincipalCallback) {
                    final GroupPrincipalCallback gpc = (GroupPrincipalCallback)callback;
                    groups = gpc.getGroups();
                }
                else if (callback instanceof PasswordValidationCallback) {
                    if (this.container == null) {
                        this.log.warn((Object)CallbackHandlerImpl.sm.getString("callbackHandlerImpl.containerMissing", new Object[] { callback.getClass().getName() }));
                    }
                    else if (this.container.getRealm() == null) {
                        this.log.warn((Object)CallbackHandlerImpl.sm.getString("callbackHandlerImpl.realmMissing", new Object[] { callback.getClass().getName(), this.container.getName() }));
                    }
                    else {
                        final PasswordValidationCallback pvc = (PasswordValidationCallback)callback;
                        principal = this.container.getRealm().authenticate(pvc.getUsername(), String.valueOf(pvc.getPassword()));
                        pvc.setResult(principal != null);
                        subject = pvc.getSubject();
                    }
                }
                else {
                    this.log.error((Object)CallbackHandlerImpl.sm.getString("callbackHandlerImpl.jaspicCallbackMissing", new Object[] { callback.getClass().getName() }));
                }
            }
            final Principal gp = this.getPrincipal(principal, name, groups);
            if (subject != null && gp != null) {
                subject.getPrivateCredentials().add(gp);
            }
        }
    }
    
    private Principal getPrincipal(final Principal principal, String name, final String[] groups) {
        if (principal instanceof GenericPrincipal) {
            return principal;
        }
        if (name == null && principal != null) {
            name = principal.getName();
        }
        if (name == null) {
            return null;
        }
        List<String> roles;
        if (groups == null || groups.length == 0) {
            roles = Collections.emptyList();
        }
        else {
            roles = Arrays.asList(groups);
        }
        return new GenericPrincipal(name, null, roles, principal);
    }
    
    @Override
    public Container getContainer() {
        return this.container;
    }
    
    @Override
    public void setContainer(final Container container) {
        this.container = container;
    }
    
    static {
        sm = StringManager.getManager((Class)CallbackHandlerImpl.class);
    }
}
