package sun.security.jgss.krb5;

import java.io.IOException;
import sun.security.krb5.KrbException;
import sun.security.krb5.Credentials;
import sun.security.krb5.EncryptionKey;
import java.util.ArrayList;
import sun.security.krb5.PrincipalName;
import java.util.Iterator;
import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosTicket;
import javax.security.auth.kerberos.KerberosKey;
import javax.security.auth.kerberos.KeyTab;
import java.util.List;
import java.util.Set;
import javax.security.auth.kerberos.KerberosPrincipal;

public final class ServiceCreds
{
    private KerberosPrincipal kp;
    private Set<KerberosPrincipal> allPrincs;
    private List<KeyTab> ktabs;
    private List<KerberosKey> kk;
    private KerberosTicket tgt;
    private boolean destroyed;
    
    private ServiceCreds() {
    }
    
    public static ServiceCreds getInstance(final Subject subject, String name) {
        final ServiceCreds serviceCreds = new ServiceCreds();
        serviceCreds.allPrincs = subject.getPrincipals(KerberosPrincipal.class);
        final Iterator<KerberosKey> iterator = SubjectComber.findMany(subject, name, null, KerberosKey.class).iterator();
        while (iterator.hasNext()) {
            serviceCreds.allPrincs.add(iterator.next().getPrincipal());
        }
        if (name != null) {
            serviceCreds.kp = new KerberosPrincipal(name);
        }
        else if (serviceCreds.allPrincs.size() == 1) {
            boolean b = false;
            final Iterator<KeyTab> iterator2 = SubjectComber.findMany(subject, null, null, KeyTab.class).iterator();
            while (iterator2.hasNext()) {
                if (!iterator2.next().isBound()) {
                    b = true;
                    break;
                }
            }
            if (!b) {
                serviceCreds.kp = serviceCreds.allPrincs.iterator().next();
                name = serviceCreds.kp.getName();
            }
        }
        serviceCreds.ktabs = SubjectComber.findMany(subject, name, null, KeyTab.class);
        serviceCreds.kk = SubjectComber.findMany(subject, name, null, KerberosKey.class);
        serviceCreds.tgt = SubjectComber.find(subject, null, name, KerberosTicket.class);
        if (serviceCreds.ktabs.isEmpty() && serviceCreds.kk.isEmpty() && serviceCreds.tgt == null) {
            return null;
        }
        serviceCreds.destroyed = false;
        return serviceCreds;
    }
    
    public String getName() {
        if (this.destroyed) {
            throw new IllegalStateException("This object is destroyed");
        }
        return (this.kp == null) ? null : this.kp.getName();
    }
    
    public KerberosKey[] getKKeys() {
        if (this.destroyed) {
            throw new IllegalStateException("This object is destroyed");
        }
        KerberosPrincipal kp = this.kp;
        if (kp == null && !this.allPrincs.isEmpty()) {
            kp = this.allPrincs.iterator().next();
        }
        if (kp == null) {
            final Iterator<KeyTab> iterator = this.ktabs.iterator();
            while (iterator.hasNext()) {
                final PrincipalName oneName = Krb5Util.snapshotFromJavaxKeyTab(iterator.next()).getOneName();
                if (oneName != null) {
                    kp = new KerberosPrincipal(oneName.getName());
                    break;
                }
            }
        }
        if (kp != null) {
            return this.getKKeys(kp);
        }
        return new KerberosKey[0];
    }
    
    public KerberosKey[] getKKeys(final KerberosPrincipal kerberosPrincipal) {
        if (this.destroyed) {
            throw new IllegalStateException("This object is destroyed");
        }
        final ArrayList list = new ArrayList();
        if (this.kp != null && !kerberosPrincipal.equals(this.kp)) {
            return new KerberosKey[0];
        }
        for (final KerberosKey kerberosKey : this.kk) {
            if (kerberosKey.getPrincipal().equals(kerberosPrincipal)) {
                list.add(kerberosKey);
            }
        }
        for (final KeyTab keyTab : this.ktabs) {
            if (keyTab.getPrincipal() == null && keyTab.isBound() && !this.allPrincs.contains(kerberosPrincipal)) {
                continue;
            }
            final KerberosKey[] keys = keyTab.getKeys(kerberosPrincipal);
            for (int length = keys.length, i = 0; i < length; ++i) {
                list.add(keys[i]);
            }
        }
        return list.toArray(new KerberosKey[list.size()]);
    }
    
    public EncryptionKey[] getEKeys(final PrincipalName principalName) {
        if (this.destroyed) {
            throw new IllegalStateException("This object is destroyed");
        }
        KerberosKey[] array = this.getKKeys(new KerberosPrincipal(principalName.getName()));
        if (array.length == 0) {
            array = this.getKKeys();
        }
        final EncryptionKey[] array2 = new EncryptionKey[array.length];
        for (int i = 0; i < array2.length; ++i) {
            array2[i] = new EncryptionKey(array[i].getEncoded(), array[i].getKeyType(), new Integer(array[i].getVersionNumber()));
        }
        return array2;
    }
    
    public Credentials getInitCred() {
        if (this.destroyed) {
            throw new IllegalStateException("This object is destroyed");
        }
        if (this.tgt == null) {
            return null;
        }
        try {
            return Krb5Util.ticketToCreds(this.tgt);
        }
        catch (final KrbException | IOException ex) {
            return null;
        }
    }
    
    public void destroy() {
        this.destroyed = true;
        this.kp = null;
        this.ktabs.clear();
        this.kk.clear();
        this.tgt = null;
    }
}
