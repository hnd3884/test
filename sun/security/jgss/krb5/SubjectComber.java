package sun.security.jgss.krb5;

import java.util.Set;
import java.util.Iterator;
import sun.security.krb5.KerberosSecrets;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.kerberos.KerberosTicket;
import javax.security.auth.kerberos.KerberosKey;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.kerberos.KeyTab;
import java.util.ArrayList;
import java.util.List;
import javax.security.auth.Subject;

class SubjectComber
{
    private static final boolean DEBUG;
    
    private SubjectComber() {
    }
    
    static <T> T find(final Subject subject, final String s, final String s2, final Class<T> clazz) {
        return clazz.cast(findAux(subject, s, s2, clazz, true));
    }
    
    static <T> List<T> findMany(final Subject subject, final String s, final String s2, final Class<T> clazz) {
        return (List)findAux(subject, s, s2, clazz, false);
    }
    
    private static <T> Object findAux(final Subject subject, String s, String s2, final Class<T> clazz, final boolean b) {
        if (subject == null) {
            return null;
        }
        final ArrayList list = b ? null : new ArrayList();
        if (clazz == KeyTab.class) {
            for (final KeyTab keyTab : subject.getPrivateCredentials(KeyTab.class)) {
                if (s != null && keyTab.isBound()) {
                    final KerberosPrincipal principal = keyTab.getPrincipal();
                    if (principal != null) {
                        if (!s.equals(principal.getName())) {
                            continue;
                        }
                    }
                    else {
                        boolean b2 = false;
                        final Iterator<KerberosPrincipal> iterator2 = subject.getPrincipals(KerberosPrincipal.class).iterator();
                        while (iterator2.hasNext()) {
                            if (iterator2.next().getName().equals(s)) {
                                b2 = true;
                                break;
                            }
                        }
                        if (!b2) {
                            continue;
                        }
                    }
                }
                if (SubjectComber.DEBUG) {
                    System.out.println("Found " + clazz.getSimpleName() + " " + keyTab);
                }
                if (b) {
                    return keyTab;
                }
                list.add(clazz.cast(keyTab));
            }
        }
        else if (clazz == KerberosKey.class) {
            for (final KerberosKey kerberosKey : subject.getPrivateCredentials(KerberosKey.class)) {
                final String name = kerberosKey.getPrincipal().getName();
                if (s == null || s.equals(name)) {
                    if (SubjectComber.DEBUG) {
                        System.out.println("Found " + clazz.getSimpleName() + " for " + name);
                    }
                    if (b) {
                        return kerberosKey;
                    }
                    list.add(clazz.cast(kerberosKey));
                }
            }
        }
        else if (clazz == KerberosTicket.class) {
            final Set<Object> privateCredentials = subject.getPrivateCredentials();
            synchronized (privateCredentials) {
                final Iterator<KerberosTicket> iterator4 = privateCredentials.iterator();
                while (iterator4.hasNext()) {
                    final KerberosTicket next = iterator4.next();
                    if (next instanceof KerberosTicket) {
                        final KerberosTicket kerberosTicket = next;
                        if (SubjectComber.DEBUG) {
                            System.out.println("Found ticket for " + kerberosTicket.getClient() + " to go to " + kerberosTicket.getServer() + " expiring on " + kerberosTicket.getEndTime());
                        }
                        if (!kerberosTicket.isCurrent()) {
                            if (subject.isReadOnly()) {
                                continue;
                            }
                            iterator4.remove();
                            try {
                                kerberosTicket.destroy();
                                if (!SubjectComber.DEBUG) {
                                    continue;
                                }
                                System.out.println("Removed and destroyed the expired Ticket \n" + kerberosTicket);
                            }
                            catch (final DestroyFailedException ex) {
                                if (!SubjectComber.DEBUG) {
                                    continue;
                                }
                                System.out.println("Expired ticket not detroyed successfully. " + ex);
                            }
                        }
                        else {
                            final KerberosPrincipal kerberosTicketGetServerAlias = KerberosSecrets.getJavaxSecurityAuthKerberosAccess().kerberosTicketGetServerAlias(kerberosTicket);
                            if (s != null && !kerberosTicket.getServer().getName().equals(s) && (kerberosTicketGetServerAlias == null || !s.equals(kerberosTicketGetServerAlias.getName()))) {
                                continue;
                            }
                            final KerberosPrincipal kerberosTicketGetClientAlias = KerberosSecrets.getJavaxSecurityAuthKerberosAccess().kerberosTicketGetClientAlias(kerberosTicket);
                            if (s2 != null && !s2.equals(kerberosTicket.getClient().getName()) && (kerberosTicketGetClientAlias == null || !s2.equals(kerberosTicketGetClientAlias.getName()))) {
                                continue;
                            }
                            if (b) {
                                return kerberosTicket;
                            }
                            if (s2 == null) {
                                if (kerberosTicketGetClientAlias == null) {
                                    s2 = kerberosTicket.getClient().getName();
                                }
                                else {
                                    s2 = kerberosTicketGetClientAlias.getName();
                                }
                            }
                            if (s == null) {
                                if (kerberosTicketGetServerAlias == null) {
                                    s = kerberosTicket.getServer().getName();
                                }
                                else {
                                    s = kerberosTicketGetServerAlias.getName();
                                }
                            }
                            list.add(clazz.cast(kerberosTicket));
                        }
                    }
                }
            }
        }
        return list;
    }
    
    static {
        DEBUG = Krb5Util.DEBUG;
    }
}
