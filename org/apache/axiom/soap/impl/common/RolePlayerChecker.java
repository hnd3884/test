package org.apache.axiom.soap.impl.common;

import java.util.Iterator;
import java.util.List;
import org.apache.axiom.soap.SOAPVersion;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAP12Version;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.RolePlayer;

public class RolePlayerChecker implements Checker
{
    RolePlayer rolePlayer;
    String namespace;
    
    public RolePlayerChecker(final RolePlayer rolePlayer) {
        this.rolePlayer = rolePlayer;
    }
    
    public RolePlayerChecker(final RolePlayer rolePlayer, final String namespace) {
        this.rolePlayer = rolePlayer;
        this.namespace = namespace;
    }
    
    public boolean checkHeader(final SOAPHeaderBlock header) {
        if (this.namespace != null) {
            final OMNamespace headerNamespace = header.getNamespace();
            if (headerNamespace == null || !this.namespace.equals(headerNamespace.getNamespaceURI())) {
                return false;
            }
        }
        final String role = header.getRole();
        final SOAPVersion version = header.getVersion();
        if (role == null || role.equals("") || (version instanceof SOAP12Version && role.equals("http://www.w3.org/2003/05/soap-envelope/role/ultimateReceiver"))) {
            return this.rolePlayer == null || this.rolePlayer.isUltimateDestination();
        }
        if (role.equals(version.getNextRoleURI())) {
            return true;
        }
        if (version instanceof SOAP12Version && role.equals("http://www.w3.org/2003/05/soap-envelope/role/none")) {
            return false;
        }
        final List roles = (this.rolePlayer == null) ? null : this.rolePlayer.getRoles();
        if (roles != null) {
            for (final String thisRole : roles) {
                if (thisRole.equals(role)) {
                    return true;
                }
            }
        }
        return false;
    }
}
