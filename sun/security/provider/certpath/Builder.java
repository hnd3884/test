package sun.security.provider.certpath;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetBooleanAction;
import java.util.Iterator;
import java.security.cert.Certificate;
import java.security.cert.CertSelector;
import java.util.Collections;
import java.util.HashSet;
import sun.security.x509.GeneralSubtrees;
import sun.security.x509.GeneralNames;
import sun.security.x509.SubjectAlternativeNameExtension;
import sun.security.x509.X509CertImpl;
import sun.security.x509.NameConstraintsExtension;
import sun.security.x509.X500Name;
import sun.security.x509.GeneralNameInterface;
import java.util.LinkedList;
import java.security.GeneralSecurityException;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertStoreException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.security.cert.CertStore;
import java.util.List;
import java.security.cert.X509CertSelector;
import java.util.Set;
import sun.security.util.Debug;

public abstract class Builder
{
    private static final Debug debug;
    private Set<String> matchingPolicies;
    final PKIX.BuilderParams buildParams;
    final X509CertSelector targetCertConstraints;
    static final boolean USE_AIA;
    
    Builder(final PKIX.BuilderParams buildParams) {
        this.buildParams = buildParams;
        this.targetCertConstraints = (X509CertSelector)buildParams.targetCertConstraints();
    }
    
    abstract Collection<X509Certificate> getMatchingCerts(final State p0, final List<CertStore> p1) throws CertStoreException, CertificateException, IOException;
    
    abstract void verifyCert(final X509Certificate p0, final State p1, final List<X509Certificate> p2) throws GeneralSecurityException;
    
    abstract boolean isPathCompleted(final X509Certificate p0);
    
    abstract void addCertToPath(final X509Certificate p0, final LinkedList<X509Certificate> p1);
    
    abstract void removeFinalCertFromPath(final LinkedList<X509Certificate> p0);
    
    static int distance(final GeneralNameInterface generalNameInterface, final GeneralNameInterface generalNameInterface2, final int n) {
        switch (generalNameInterface.constrains(generalNameInterface2)) {
            case -1: {
                if (Builder.debug != null) {
                    Builder.debug.println("Builder.distance(): Names are different types");
                }
                return n;
            }
            case 3: {
                if (Builder.debug != null) {
                    Builder.debug.println("Builder.distance(): Names are same type but in different subtrees");
                }
                return n;
            }
            case 0: {
                return 0;
            }
            case 2: {
                break;
            }
            case 1: {
                break;
            }
            default: {
                return n;
            }
        }
        return generalNameInterface2.subtreeDepth() - generalNameInterface.subtreeDepth();
    }
    
    static int hops(final GeneralNameInterface generalNameInterface, final GeneralNameInterface generalNameInterface2, final int n) {
        switch (generalNameInterface.constrains(generalNameInterface2)) {
            case -1: {
                if (Builder.debug != null) {
                    Builder.debug.println("Builder.hops(): Names are different types");
                }
                return n;
            }
            case 3: {
                if (generalNameInterface.getType() != 4) {
                    if (Builder.debug != null) {
                        Builder.debug.println("Builder.hops(): hopDistance not implemented for this name type");
                    }
                    return n;
                }
                final X500Name x500Name = (X500Name)generalNameInterface;
                final X500Name x500Name2 = (X500Name)generalNameInterface2;
                final X500Name commonAncestor = x500Name.commonAncestor(x500Name2);
                if (commonAncestor == null) {
                    if (Builder.debug != null) {
                        Builder.debug.println("Builder.hops(): Names are in different namespaces");
                    }
                    return n;
                }
                return x500Name.subtreeDepth() + x500Name2.subtreeDepth() - 2 * commonAncestor.subtreeDepth();
            }
            case 0: {
                return 0;
            }
            case 2: {
                return generalNameInterface2.subtreeDepth() - generalNameInterface.subtreeDepth();
            }
            case 1: {
                return generalNameInterface2.subtreeDepth() - generalNameInterface.subtreeDepth();
            }
            default: {
                return n;
            }
        }
    }
    
    static int targetDistance(NameConstraintsExtension nameConstraintsExtension, final X509Certificate x509Certificate, final GeneralNameInterface generalNameInterface) throws IOException {
        if (nameConstraintsExtension != null && !nameConstraintsExtension.verify(x509Certificate)) {
            throw new IOException("certificate does not satisfy existing name constraints");
        }
        X509CertImpl impl;
        try {
            impl = X509CertImpl.toImpl(x509Certificate);
        }
        catch (final CertificateException ex) {
            throw new IOException("Invalid certificate", ex);
        }
        if (X500Name.asX500Name(impl.getSubjectX500Principal()).equals(generalNameInterface)) {
            return 0;
        }
        final SubjectAlternativeNameExtension subjectAlternativeNameExtension = impl.getSubjectAlternativeNameExtension();
        if (subjectAlternativeNameExtension != null) {
            final GeneralNames value = subjectAlternativeNameExtension.get("subject_name");
            if (value != null) {
                for (int i = 0; i < value.size(); ++i) {
                    if (value.get(i).getName().equals(generalNameInterface)) {
                        return 0;
                    }
                }
            }
        }
        final NameConstraintsExtension nameConstraintsExtension2 = impl.getNameConstraintsExtension();
        if (nameConstraintsExtension2 == null) {
            return -1;
        }
        if (nameConstraintsExtension != null) {
            nameConstraintsExtension.merge(nameConstraintsExtension2);
        }
        else {
            nameConstraintsExtension = (NameConstraintsExtension)nameConstraintsExtension2.clone();
        }
        if (Builder.debug != null) {
            Builder.debug.println("Builder.targetDistance() merged constraints: " + String.valueOf(nameConstraintsExtension));
        }
        final GeneralSubtrees value2 = nameConstraintsExtension.get("permitted_subtrees");
        final GeneralSubtrees value3 = nameConstraintsExtension.get("excluded_subtrees");
        if (value2 != null) {
            value2.reduce(value3);
        }
        if (Builder.debug != null) {
            Builder.debug.println("Builder.targetDistance() reduced constraints: " + value2);
        }
        if (!nameConstraintsExtension.verify(generalNameInterface)) {
            throw new IOException("New certificate not allowed to sign certificate for target");
        }
        if (value2 == null) {
            return -1;
        }
        for (int j = 0; j < value2.size(); ++j) {
            final int distance = distance(value2.get(j).getName().getName(), generalNameInterface, -1);
            if (distance >= 0) {
                return distance + 1;
            }
        }
        return -1;
    }
    
    Set<String> getMatchingPolicies() {
        if (this.matchingPolicies != null) {
            final Set<String> initialPolicies = this.buildParams.initialPolicies();
            if (!initialPolicies.isEmpty() && !initialPolicies.contains("2.5.29.32.0") && this.buildParams.policyMappingInhibited()) {
                (this.matchingPolicies = new HashSet<String>(initialPolicies)).add("2.5.29.32.0");
            }
            else {
                this.matchingPolicies = Collections.emptySet();
            }
        }
        return this.matchingPolicies;
    }
    
    boolean addMatchingCerts(final X509CertSelector x509CertSelector, final Collection<CertStore> collection, final Collection<X509Certificate> collection2, final boolean b) {
        final X509Certificate certificate = x509CertSelector.getCertificate();
        if (certificate == null) {
            boolean b2 = false;
            for (final CertStore certStore : collection) {
                try {
                    for (final Certificate certificate2 : certStore.getCertificates(x509CertSelector)) {
                        if (!X509CertImpl.isSelfSigned((X509Certificate)certificate2, this.buildParams.sigProvider()) && collection2.add((X509Certificate)certificate2)) {
                            b2 = true;
                        }
                    }
                    if (!b && b2) {
                        return true;
                    }
                    continue;
                }
                catch (final CertStoreException ex) {
                    if (Builder.debug == null) {
                        continue;
                    }
                    Builder.debug.println("Builder.addMatchingCerts, non-fatal exception retrieving certs: " + ex);
                    ex.printStackTrace();
                }
            }
            return b2;
        }
        if (x509CertSelector.match(certificate) && !X509CertImpl.isSelfSigned(certificate, this.buildParams.sigProvider())) {
            if (Builder.debug != null) {
                Builder.debug.println("Builder.addMatchingCerts: adding target cert\n  SN: " + Debug.toHexString(certificate.getSerialNumber()) + "\n  Subject: " + certificate.getSubjectX500Principal() + "\n  Issuer: " + certificate.getIssuerX500Principal());
            }
            return collection2.add(certificate);
        }
        return false;
    }
    
    static {
        debug = Debug.getInstance("certpath");
        USE_AIA = AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("com.sun.security.enableAIAcaIssuers"));
    }
}
