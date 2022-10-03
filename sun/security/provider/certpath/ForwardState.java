package sun.security.provider.certpath;

import java.util.ListIterator;
import java.security.cert.CertificateException;
import sun.security.x509.SubjectAlternativeNameExtension;
import java.io.IOException;
import sun.security.x509.GeneralName;
import sun.security.x509.X500Name;
import java.security.cert.X509Certificate;
import java.security.cert.CertPathValidatorException;
import java.util.Iterator;
import java.util.List;
import java.security.cert.PKIXCertPathChecker;
import java.util.ArrayList;
import sun.security.x509.GeneralNameInterface;
import java.util.HashSet;
import sun.security.x509.X509CertImpl;
import javax.security.auth.x500.X500Principal;
import sun.security.util.Debug;

class ForwardState implements State
{
    private static final Debug debug;
    X500Principal issuerDN;
    X509CertImpl cert;
    HashSet<GeneralNameInterface> subjectNamesTraversed;
    int traversedCACerts;
    private boolean init;
    UntrustedChecker untrustedChecker;
    ArrayList<PKIXCertPathChecker> forwardCheckers;
    boolean keyParamsNeededFlag;
    
    ForwardState() {
        this.init = true;
        this.keyParamsNeededFlag = false;
    }
    
    @Override
    public boolean isInitial() {
        return this.init;
    }
    
    @Override
    public boolean keyParamsNeeded() {
        return this.keyParamsNeededFlag;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("State [");
        sb.append("\n  issuerDN of last cert: ").append(this.issuerDN);
        sb.append("\n  traversedCACerts: ").append(this.traversedCACerts);
        sb.append("\n  init: ").append(String.valueOf(this.init));
        sb.append("\n  keyParamsNeeded: ").append(String.valueOf(this.keyParamsNeededFlag));
        sb.append("\n  subjectNamesTraversed: \n").append(this.subjectNamesTraversed);
        sb.append("]\n");
        return sb.toString();
    }
    
    public void initState(final List<PKIXCertPathChecker> list) throws CertPathValidatorException {
        this.subjectNamesTraversed = new HashSet<GeneralNameInterface>();
        this.traversedCACerts = 0;
        this.forwardCheckers = new ArrayList<PKIXCertPathChecker>();
        for (final PKIXCertPathChecker pkixCertPathChecker : list) {
            if (pkixCertPathChecker.isForwardCheckingSupported()) {
                pkixCertPathChecker.init(true);
                this.forwardCheckers.add(pkixCertPathChecker);
            }
        }
        this.init = true;
    }
    
    @Override
    public void updateState(final X509Certificate x509Certificate) throws CertificateException, IOException, CertPathValidatorException {
        if (x509Certificate == null) {
            return;
        }
        final X509CertImpl impl = X509CertImpl.toImpl(x509Certificate);
        if (PKIX.isDSAPublicKeyWithoutParams(impl.getPublicKey())) {
            this.keyParamsNeededFlag = true;
        }
        this.cert = impl;
        this.issuerDN = x509Certificate.getIssuerX500Principal();
        if (!X509CertImpl.isSelfIssued(x509Certificate) && !this.init && x509Certificate.getBasicConstraints() != -1) {
            ++this.traversedCACerts;
        }
        if (this.init || !X509CertImpl.isSelfIssued(x509Certificate)) {
            this.subjectNamesTraversed.add(X500Name.asX500Name(x509Certificate.getSubjectX500Principal()));
            try {
                final SubjectAlternativeNameExtension subjectAlternativeNameExtension = impl.getSubjectAlternativeNameExtension();
                if (subjectAlternativeNameExtension != null) {
                    final Iterator<GeneralName> iterator = subjectAlternativeNameExtension.get("subject_name").names().iterator();
                    while (iterator.hasNext()) {
                        this.subjectNamesTraversed.add(iterator.next().getName());
                    }
                }
            }
            catch (final IOException ex) {
                if (ForwardState.debug != null) {
                    ForwardState.debug.println("ForwardState.updateState() unexpected exception");
                    ex.printStackTrace();
                }
                throw new CertPathValidatorException(ex);
            }
        }
        this.init = false;
    }
    
    @Override
    public Object clone() {
        try {
            final ForwardState forwardState = (ForwardState)super.clone();
            forwardState.forwardCheckers = (ArrayList)this.forwardCheckers.clone();
            final ListIterator<PKIXCertPathChecker> listIterator = forwardState.forwardCheckers.listIterator();
            while (listIterator.hasNext()) {
                final PKIXCertPathChecker pkixCertPathChecker = listIterator.next();
                if (pkixCertPathChecker instanceof Cloneable) {
                    listIterator.set((PKIXCertPathChecker)pkixCertPathChecker.clone());
                }
            }
            forwardState.subjectNamesTraversed = (HashSet)this.subjectNamesTraversed.clone();
            return forwardState;
        }
        catch (final CloneNotSupportedException ex) {
            throw new InternalError(ex.toString(), ex);
        }
    }
    
    static {
        debug = Debug.getInstance("certpath");
    }
}
