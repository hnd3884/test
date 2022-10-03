package sun.security.tools.policytool;

import java.security.Permission;
import java.util.ListIterator;
import java.security.UnrecoverableKeyException;
import java.security.NoSuchAlgorithmException;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.security.cert.Certificate;
import java.net.URL;
import sun.security.provider.PolicyParser;
import java.security.CodeSource;

class PolicyEntry
{
    private CodeSource codesource;
    private PolicyTool tool;
    private PolicyParser.GrantEntry grantEntry;
    private boolean testing;
    
    PolicyEntry(final PolicyTool tool, final PolicyParser.GrantEntry grantEntry) throws MalformedURLException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException {
        this.testing = false;
        this.tool = tool;
        URL url = null;
        if (grantEntry.codeBase != null) {
            url = new URL(grantEntry.codeBase);
        }
        this.codesource = new CodeSource(url, (Certificate[])null);
        if (this.testing) {
            System.out.println("Adding Policy Entry:");
            System.out.println("    CodeBase = " + url);
            System.out.println("    Signers = " + grantEntry.signedBy);
            System.out.println("    with " + grantEntry.principals.size() + " Principals");
        }
        this.grantEntry = grantEntry;
    }
    
    CodeSource getCodeSource() {
        return this.codesource;
    }
    
    PolicyParser.GrantEntry getGrantEntry() {
        return this.grantEntry;
    }
    
    String headerToString() {
        final String principalsToString = this.principalsToString();
        if (principalsToString.length() == 0) {
            return this.codebaseToString();
        }
        return this.codebaseToString() + ", " + principalsToString;
    }
    
    String codebaseToString() {
        String concat = new String();
        if (this.grantEntry.codeBase != null && !this.grantEntry.codeBase.equals("")) {
            concat = concat.concat("CodeBase \"" + this.grantEntry.codeBase + "\"");
        }
        if (this.grantEntry.signedBy != null && !this.grantEntry.signedBy.equals("")) {
            concat = ((concat.length() > 0) ? concat.concat(", SignedBy \"" + this.grantEntry.signedBy + "\"") : concat.concat("SignedBy \"" + this.grantEntry.signedBy + "\""));
        }
        if (concat.length() == 0) {
            return new String("CodeBase <ALL>");
        }
        return concat;
    }
    
    String principalsToString() {
        String string = "";
        if (this.grantEntry.principals != null && !this.grantEntry.principals.isEmpty()) {
            final StringBuffer sb = new StringBuffer(200);
            final ListIterator<Object> listIterator = this.grantEntry.principals.listIterator();
            while (listIterator.hasNext()) {
                final PolicyParser.PrincipalEntry principalEntry = listIterator.next();
                sb.append(" Principal " + principalEntry.getDisplayClass() + " " + principalEntry.getDisplayName(true));
                if (listIterator.hasNext()) {
                    sb.append(", ");
                }
            }
            string = sb.toString();
        }
        return string;
    }
    
    PolicyParser.PermissionEntry toPermissionEntry(final Permission permission) {
        String actions = null;
        if (permission.getActions() != null && permission.getActions().trim() != "") {
            actions = permission.getActions();
        }
        return new PolicyParser.PermissionEntry(permission.getClass().getName(), permission.getName(), actions);
    }
}
