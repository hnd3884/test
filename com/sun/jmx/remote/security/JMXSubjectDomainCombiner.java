package com.sun.jmx.remote.security;

import java.security.Principal;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.DomainCombiner;
import java.security.AccessController;
import java.security.AccessControlContext;
import javax.security.auth.Subject;
import java.security.ProtectionDomain;
import java.security.CodeSource;
import javax.security.auth.SubjectDomainCombiner;

public class JMXSubjectDomainCombiner extends SubjectDomainCombiner
{
    private static final CodeSource nullCodeSource;
    private static final ProtectionDomain pdNoPerms;
    
    public JMXSubjectDomainCombiner(final Subject subject) {
        super(subject);
    }
    
    @Override
    public ProtectionDomain[] combine(final ProtectionDomain[] array, final ProtectionDomain[] array2) {
        ProtectionDomain[] array3;
        if (array == null || array.length == 0) {
            array3 = new ProtectionDomain[] { JMXSubjectDomainCombiner.pdNoPerms };
        }
        else {
            array3 = new ProtectionDomain[array.length + 1];
            for (int i = 0; i < array.length; ++i) {
                array3[i] = array[i];
            }
            array3[array.length] = JMXSubjectDomainCombiner.pdNoPerms;
        }
        return super.combine(array3, array2);
    }
    
    public static AccessControlContext getContext(final Subject subject) {
        return new AccessControlContext(AccessController.getContext(), new JMXSubjectDomainCombiner(subject));
    }
    
    public static AccessControlContext getDomainCombinerContext(final Subject subject) {
        return new AccessControlContext(new AccessControlContext(new ProtectionDomain[0]), new JMXSubjectDomainCombiner(subject));
    }
    
    static {
        nullCodeSource = new CodeSource(null, (Certificate[])null);
        pdNoPerms = new ProtectionDomain(JMXSubjectDomainCombiner.nullCodeSource, new Permissions(), null, null);
    }
}
