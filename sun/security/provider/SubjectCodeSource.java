package sun.security.provider;

import java.util.Iterator;
import java.util.ListIterator;
import java.security.Principal;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.security.cert.Certificate;
import java.net.URL;
import sun.security.util.Debug;
import java.util.LinkedList;
import javax.security.auth.Subject;
import java.util.ResourceBundle;
import java.io.Serializable;
import java.security.CodeSource;

class SubjectCodeSource extends CodeSource implements Serializable
{
    private static final long serialVersionUID = 6039418085604715275L;
    private static final ResourceBundle rb;
    private Subject subject;
    private LinkedList<PolicyParser.PrincipalEntry> principals;
    private static final Class<?>[] PARAMS;
    private static final Debug debug;
    private ClassLoader sysClassLoader;
    
    SubjectCodeSource(final Subject subject, final LinkedList<PolicyParser.PrincipalEntry> list, final URL url, final Certificate[] array) {
        super(url, array);
        this.subject = subject;
        this.principals = ((list == null) ? new LinkedList<PolicyParser.PrincipalEntry>() : new LinkedList<PolicyParser.PrincipalEntry>(list));
        this.sysClassLoader = AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run() {
                return ClassLoader.getSystemClassLoader();
            }
        });
    }
    
    LinkedList<PolicyParser.PrincipalEntry> getPrincipals() {
        return this.principals;
    }
    
    Subject getSubject() {
        return this.subject;
    }
    
    @Override
    public boolean implies(final CodeSource codeSource) {
        LinkedList<PolicyParser.PrincipalEntry> list = null;
        if (codeSource == null || !(codeSource instanceof SubjectCodeSource) || !super.implies(codeSource)) {
            if (SubjectCodeSource.debug != null) {
                SubjectCodeSource.debug.println("\tSubjectCodeSource.implies: FAILURE 1");
            }
            return false;
        }
        final SubjectCodeSource subjectCodeSource = (SubjectCodeSource)codeSource;
        if (this.principals == null) {
            if (SubjectCodeSource.debug != null) {
                SubjectCodeSource.debug.println("\tSubjectCodeSource.implies: PASS 1");
            }
            return true;
        }
        if (subjectCodeSource.getSubject() == null || subjectCodeSource.getSubject().getPrincipals().size() == 0) {
            if (SubjectCodeSource.debug != null) {
                SubjectCodeSource.debug.println("\tSubjectCodeSource.implies: FAILURE 2");
            }
            return false;
        }
        final ListIterator<PolicyParser.PrincipalEntry> listIterator = this.principals.listIterator(0);
        while (listIterator.hasNext()) {
            final PolicyParser.PrincipalEntry principalEntry = listIterator.next();
            try {
                final Class<?> forName = Class.forName(principalEntry.principalClass, true, this.sysClassLoader);
                if (!Principal.class.isAssignableFrom(forName)) {
                    throw new ClassCastException(principalEntry.principalClass + " is not a Principal");
                }
                if (!((Principal)forName.getConstructor(SubjectCodeSource.PARAMS).newInstance(principalEntry.principalName)).implies(subjectCodeSource.getSubject())) {
                    if (SubjectCodeSource.debug != null) {
                        SubjectCodeSource.debug.println("\tSubjectCodeSource.implies: FAILURE 3");
                    }
                    return false;
                }
                if (SubjectCodeSource.debug != null) {
                    SubjectCodeSource.debug.println("\tSubjectCodeSource.implies: PASS 2");
                }
                return true;
            }
            catch (final Exception ex) {
                if (list == null) {
                    if (subjectCodeSource.getSubject() == null) {
                        if (SubjectCodeSource.debug != null) {
                            SubjectCodeSource.debug.println("\tSubjectCodeSource.implies: FAILURE 4");
                        }
                        return false;
                    }
                    final Iterator<Principal> iterator = subjectCodeSource.getSubject().getPrincipals().iterator();
                    list = new LinkedList<PolicyParser.PrincipalEntry>();
                    while (iterator.hasNext()) {
                        final Principal principal = iterator.next();
                        list.add(new PolicyParser.PrincipalEntry(principal.getClass().getName(), principal.getName()));
                    }
                }
                if (!this.subjectListImpliesPrincipalEntry(list, principalEntry)) {
                    if (SubjectCodeSource.debug != null) {
                        SubjectCodeSource.debug.println("\tSubjectCodeSource.implies: FAILURE 5");
                    }
                    return false;
                }
                continue;
            }
            break;
        }
        if (SubjectCodeSource.debug != null) {
            SubjectCodeSource.debug.println("\tSubjectCodeSource.implies: PASS 3");
        }
        return true;
    }
    
    private boolean subjectListImpliesPrincipalEntry(final LinkedList<PolicyParser.PrincipalEntry> list, final PolicyParser.PrincipalEntry principalEntry) {
        final ListIterator<PolicyParser.PrincipalEntry> listIterator = list.listIterator(0);
        while (listIterator.hasNext()) {
            final PolicyParser.PrincipalEntry principalEntry2 = listIterator.next();
            if ((principalEntry.getPrincipalClass().equals("WILDCARD_PRINCIPAL_CLASS") || principalEntry.getPrincipalClass().equals(principalEntry2.getPrincipalClass())) && (principalEntry.getPrincipalName().equals("WILDCARD_PRINCIPAL_NAME") || principalEntry.getPrincipalName().equals(principalEntry2.getPrincipalName()))) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!super.equals(o)) {
            return false;
        }
        if (!(o instanceof SubjectCodeSource)) {
            return false;
        }
        final SubjectCodeSource subjectCodeSource = (SubjectCodeSource)o;
        try {
            if (this.getSubject() != subjectCodeSource.getSubject()) {
                return false;
            }
        }
        catch (final SecurityException ex) {
            return false;
        }
        return (this.principals != null || subjectCodeSource.principals == null) && (this.principals == null || subjectCodeSource.principals != null) && (this.principals == null || subjectCodeSource.principals == null || (this.principals.containsAll(subjectCodeSource.principals) && subjectCodeSource.principals.containsAll(this.principals)));
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    @Override
    public String toString() {
        String s = super.toString();
        if (this.getSubject() != null) {
            if (SubjectCodeSource.debug != null) {
                s = s + "\n" + AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
                    final /* synthetic */ Subject val$finalSubject = SubjectCodeSource.this.getSubject();
                    
                    @Override
                    public String run() {
                        return this.val$finalSubject.toString();
                    }
                });
            }
            else {
                s = s + "\n" + this.getSubject().toString();
            }
        }
        if (this.principals != null) {
            final ListIterator<Object> listIterator = this.principals.listIterator();
            while (listIterator.hasNext()) {
                final PolicyParser.PrincipalEntry principalEntry = listIterator.next();
                s = s + SubjectCodeSource.rb.getString("NEWLINE") + principalEntry.getPrincipalClass() + " " + principalEntry.getPrincipalName();
            }
        }
        return s;
    }
    
    static {
        rb = AccessController.doPrivileged((PrivilegedAction<ResourceBundle>)new PrivilegedAction<ResourceBundle>() {
            @Override
            public ResourceBundle run() {
                return ResourceBundle.getBundle("sun.security.util.AuthResources");
            }
        });
        PARAMS = new Class[] { String.class };
        debug = Debug.getInstance("auth", "\t[Auth Access]");
    }
}
