package org.apache.catalina;

import java.io.IOException;
import org.apache.catalina.connector.Response;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.apache.catalina.connector.Request;
import java.security.cert.X509Certificate;
import org.ietf.jgss.GSSContext;
import java.security.Principal;
import java.beans.PropertyChangeListener;

public interface Realm extends Contained
{
    CredentialHandler getCredentialHandler();
    
    void setCredentialHandler(final CredentialHandler p0);
    
    void addPropertyChangeListener(final PropertyChangeListener p0);
    
    Principal authenticate(final String p0);
    
    Principal authenticate(final String p0, final String p1);
    
    Principal authenticate(final String p0, final String p1, final String p2, final String p3, final String p4, final String p5, final String p6, final String p7);
    
    Principal authenticate(final GSSContext p0, final boolean p1);
    
    Principal authenticate(final X509Certificate[] p0);
    
    void backgroundProcess();
    
    SecurityConstraint[] findSecurityConstraints(final Request p0, final Context p1);
    
    boolean hasResourcePermission(final Request p0, final Response p1, final SecurityConstraint[] p2, final Context p3) throws IOException;
    
    boolean hasRole(final Wrapper p0, final Principal p1, final String p2);
    
    boolean hasUserDataPermission(final Request p0, final Response p1, final SecurityConstraint[] p2) throws IOException;
    
    void removePropertyChangeListener(final PropertyChangeListener p0);
    
    @Deprecated
    String[] getRoles(final Principal p0);
    
    boolean isAvailable();
}
