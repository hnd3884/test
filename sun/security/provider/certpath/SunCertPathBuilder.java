package sun.security.provider.certpath;

import java.security.cert.CertSelector;
import java.util.Iterator;
import java.security.cert.CertPath;
import java.security.cert.PKIXReason;
import sun.security.x509.PKIXExtensions;
import java.security.cert.CertPathValidatorException;
import java.security.cert.PKIXRevocationChecker;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PolicyQualifierInfo;
import java.util.Set;
import java.util.Collection;
import javax.security.auth.x500.X500Principal;
import java.security.cert.Certificate;
import java.util.Collections;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CertPathBuilderResult;
import java.security.cert.CertPathParameters;
import java.security.cert.CertPathChecker;
import java.security.cert.CertificateException;
import java.security.cert.CertPathBuilderException;
import java.security.PublicKey;
import java.security.cert.TrustAnchor;
import java.security.cert.PolicyNode;
import java.security.cert.CertificateFactory;
import sun.security.util.Debug;
import java.security.cert.CertPathBuilderSpi;

public final class SunCertPathBuilder extends CertPathBuilderSpi
{
    private static final Debug debug;
    private PKIX.BuilderParams buildParams;
    private CertificateFactory cf;
    private boolean pathCompleted;
    private PolicyNode policyTreeResult;
    private TrustAnchor trustAnchor;
    private PublicKey finalPublicKey;
    
    public SunCertPathBuilder() throws CertPathBuilderException {
        this.pathCompleted = false;
        try {
            this.cf = CertificateFactory.getInstance("X.509");
        }
        catch (final CertificateException ex) {
            throw new CertPathBuilderException(ex);
        }
    }
    
    @Override
    public CertPathChecker engineGetRevocationChecker() {
        return new RevocationChecker();
    }
    
    @Override
    public CertPathBuilderResult engineBuild(final CertPathParameters certPathParameters) throws CertPathBuilderException, InvalidAlgorithmParameterException {
        if (SunCertPathBuilder.debug != null) {
            SunCertPathBuilder.debug.println("SunCertPathBuilder.engineBuild(" + certPathParameters + ")");
        }
        this.buildParams = PKIX.checkBuilderParams(certPathParameters);
        return this.build();
    }
    
    private PKIXCertPathBuilderResult build() throws CertPathBuilderException {
        final ArrayList list = new ArrayList();
        PKIXCertPathBuilderResult pkixCertPathBuilderResult = this.buildCertPath(false, list);
        if (pkixCertPathBuilderResult == null) {
            if (SunCertPathBuilder.debug != null) {
                SunCertPathBuilder.debug.println("SunCertPathBuilder.engineBuild: 2nd pass; try building again searching all certstores");
            }
            list.clear();
            pkixCertPathBuilderResult = this.buildCertPath(true, list);
            if (pkixCertPathBuilderResult == null) {
                throw new SunCertPathBuilderException("unable to find valid certification path to requested target", new AdjacencyList(list));
            }
        }
        return pkixCertPathBuilderResult;
    }
    
    private PKIXCertPathBuilderResult buildCertPath(final boolean b, final List<List<Vertex>> list) throws CertPathBuilderException {
        this.pathCompleted = false;
        this.trustAnchor = null;
        this.finalPublicKey = null;
        this.policyTreeResult = null;
        final LinkedList list2 = new LinkedList();
        try {
            this.buildForward(list, list2, b);
        }
        catch (final GeneralSecurityException | IOException ex) {
            if (SunCertPathBuilder.debug != null) {
                SunCertPathBuilder.debug.println("SunCertPathBuilder.engineBuild() exception in build");
                ((Throwable)ex).printStackTrace();
            }
            throw new SunCertPathBuilderException("unable to find valid certification path to requested target", (Throwable)ex, new AdjacencyList(list));
        }
        try {
            if (this.pathCompleted) {
                if (SunCertPathBuilder.debug != null) {
                    SunCertPathBuilder.debug.println("SunCertPathBuilder.engineBuild() pathCompleted");
                }
                Collections.reverse(list2);
                return new SunCertPathBuilderResult(this.cf.generateCertPath(list2), this.trustAnchor, this.policyTreeResult, this.finalPublicKey, new AdjacencyList(list));
            }
        }
        catch (final CertificateException ex2) {
            if (SunCertPathBuilder.debug != null) {
                SunCertPathBuilder.debug.println("SunCertPathBuilder.engineBuild() exception in wrap-up");
                ex2.printStackTrace();
            }
            throw new SunCertPathBuilderException("unable to find valid certification path to requested target", ex2, new AdjacencyList(list));
        }
        return null;
    }
    
    private void buildForward(final List<List<Vertex>> list, final LinkedList<X509Certificate> list2, final boolean b) throws GeneralSecurityException, IOException {
        if (SunCertPathBuilder.debug != null) {
            SunCertPathBuilder.debug.println("SunCertPathBuilder.buildForward()...");
        }
        final ForwardState forwardState = new ForwardState();
        forwardState.initState(this.buildParams.certPathCheckers());
        list.clear();
        list.add(new LinkedList());
        forwardState.untrustedChecker = new UntrustedChecker();
        this.depthFirstSearchForward(this.buildParams.targetSubject(), forwardState, new ForwardBuilder(this.buildParams, b), list, list2);
    }
    
    private void depthFirstSearchForward(final X500Principal x500Principal, final ForwardState forwardState, final ForwardBuilder forwardBuilder, final List<List<Vertex>> list, final LinkedList<X509Certificate> list2) throws GeneralSecurityException, IOException {
        if (SunCertPathBuilder.debug != null) {
            SunCertPathBuilder.debug.println("SunCertPathBuilder.depthFirstSearchForward(" + x500Principal + ", " + forwardState.toString() + ")");
        }
        final List<Vertex> addVertices = addVertices(forwardBuilder.getMatchingCerts(forwardState, this.buildParams.certStores()), list);
        if (SunCertPathBuilder.debug != null) {
            SunCertPathBuilder.debug.println("SunCertPathBuilder.depthFirstSearchForward(): certs.size=" + addVertices.size());
        }
    Label_0117:
        for (final Vertex vertex : addVertices) {
            final ForwardState forwardState2 = (ForwardState)forwardState.clone();
            final X509Certificate certificate = vertex.getCertificate();
            try {
                forwardBuilder.verifyCert(certificate, forwardState2, list2);
            }
            catch (final GeneralSecurityException throwable) {
                if (SunCertPathBuilder.debug != null) {
                    SunCertPathBuilder.debug.println("SunCertPathBuilder.depthFirstSearchForward(): validation failed: " + throwable);
                    throwable.printStackTrace();
                }
                vertex.setThrowable(throwable);
                continue;
            }
            if (forwardBuilder.isPathCompleted(certificate)) {
                if (SunCertPathBuilder.debug != null) {
                    SunCertPathBuilder.debug.println("SunCertPathBuilder.depthFirstSearchForward(): commencing final verification");
                }
                final ArrayList list3 = new ArrayList(list2);
                if (forwardBuilder.trustAnchor.getTrustedCert() == null) {
                    list3.add(0, (Object)certificate);
                }
                final PolicyNodeImpl policyNodeImpl = new PolicyNodeImpl(null, "2.5.29.32.0", null, false, Collections.singleton("2.5.29.32.0"), false);
                final ArrayList list4 = new ArrayList();
                final PolicyChecker policyChecker = new PolicyChecker(this.buildParams.initialPolicies(), list3.size(), this.buildParams.explicitPolicyRequired(), this.buildParams.policyMappingInhibited(), this.buildParams.anyPolicyInhibited(), this.buildParams.policyQualifiersRejected(), policyNodeImpl);
                list4.add(policyChecker);
                list4.add(new AlgorithmChecker(forwardBuilder.trustAnchor, this.buildParams.date(), this.buildParams.variant()));
                BasicChecker basicChecker = null;
                if (forwardState2.keyParamsNeeded()) {
                    PublicKey publicKey = certificate.getPublicKey();
                    if (forwardBuilder.trustAnchor.getTrustedCert() == null) {
                        publicKey = forwardBuilder.trustAnchor.getCAPublicKey();
                        if (SunCertPathBuilder.debug != null) {
                            SunCertPathBuilder.debug.println("SunCertPathBuilder.depthFirstSearchForward using buildParams public key: " + publicKey.toString());
                        }
                    }
                    basicChecker = new BasicChecker(new TrustAnchor(certificate.getSubjectX500Principal(), publicKey, null), this.buildParams.date(), this.buildParams.sigProvider(), true);
                    list4.add(basicChecker);
                }
                this.buildParams.setCertPath(this.cf.generateCertPath((List<? extends Certificate>)list3));
                int n = 0;
                final List<PKIXCertPathChecker> certPathCheckers = this.buildParams.certPathCheckers();
                for (final PKIXCertPathChecker pkixCertPathChecker : certPathCheckers) {
                    if (pkixCertPathChecker instanceof PKIXRevocationChecker) {
                        if (n != 0) {
                            throw new CertPathValidatorException("Only one PKIXRevocationChecker can be specified");
                        }
                        n = 1;
                        if (!(pkixCertPathChecker instanceof RevocationChecker)) {
                            continue;
                        }
                        ((RevocationChecker)pkixCertPathChecker).init(forwardBuilder.trustAnchor, this.buildParams);
                    }
                }
                if (this.buildParams.revocationEnabled() && n == 0) {
                    list4.add(new RevocationChecker(forwardBuilder.trustAnchor, this.buildParams));
                }
                list4.addAll(certPathCheckers);
                for (int i = 0; i < list3.size(); ++i) {
                    final X509Certificate x509Certificate = (X509Certificate)list3.get(i);
                    if (SunCertPathBuilder.debug != null) {
                        SunCertPathBuilder.debug.println("current subject = " + x509Certificate.getSubjectX500Principal());
                    }
                    Object o = x509Certificate.getCriticalExtensionOIDs();
                    if (o == null) {
                        o = Collections.emptySet();
                    }
                    for (final PKIXCertPathChecker pkixCertPathChecker2 : list4) {
                        if (!pkixCertPathChecker2.isForwardCheckingSupported()) {
                            if (i == 0) {
                                pkixCertPathChecker2.init(false);
                                if (pkixCertPathChecker2 instanceof AlgorithmChecker) {
                                    ((AlgorithmChecker)pkixCertPathChecker2).trySetTrustAnchor(forwardBuilder.trustAnchor);
                                }
                            }
                            try {
                                pkixCertPathChecker2.check(x509Certificate, (Collection<String>)o);
                            }
                            catch (final CertPathValidatorException throwable2) {
                                if (SunCertPathBuilder.debug != null) {
                                    SunCertPathBuilder.debug.println("SunCertPathBuilder.depthFirstSearchForward(): final verification failed: " + throwable2);
                                }
                                if (this.buildParams.targetCertConstraints().match(x509Certificate) && throwable2.getReason() == CertPathValidatorException.BasicReason.REVOKED) {
                                    throw throwable2;
                                }
                                vertex.setThrowable(throwable2);
                                continue Label_0117;
                            }
                        }
                    }
                    for (final PKIXCertPathChecker pkixCertPathChecker3 : this.buildParams.certPathCheckers()) {
                        if (pkixCertPathChecker3.isForwardCheckingSupported()) {
                            final Set<String> supportedExtensions = pkixCertPathChecker3.getSupportedExtensions();
                            if (supportedExtensions == null) {
                                continue;
                            }
                            ((Set)o).removeAll(supportedExtensions);
                        }
                    }
                    if (!((Set)o).isEmpty()) {
                        ((Set)o).remove(PKIXExtensions.BasicConstraints_Id.toString());
                        ((Set)o).remove(PKIXExtensions.NameConstraints_Id.toString());
                        ((Set)o).remove(PKIXExtensions.CertificatePolicies_Id.toString());
                        ((Set)o).remove(PKIXExtensions.PolicyMappings_Id.toString());
                        ((Set)o).remove(PKIXExtensions.PolicyConstraints_Id.toString());
                        ((Set)o).remove(PKIXExtensions.InhibitAnyPolicy_Id.toString());
                        ((Set)o).remove(PKIXExtensions.SubjectAlternativeName_Id.toString());
                        ((Set)o).remove(PKIXExtensions.KeyUsage_Id.toString());
                        ((Set)o).remove(PKIXExtensions.ExtendedKeyUsage_Id.toString());
                        if (!((Set)o).isEmpty()) {
                            throw new CertPathValidatorException("unrecognized critical extension(s)", null, null, -1, PKIXReason.UNRECOGNIZED_CRIT_EXT);
                        }
                    }
                }
                if (SunCertPathBuilder.debug != null) {
                    SunCertPathBuilder.debug.println("SunCertPathBuilder.depthFirstSearchForward(): final verification succeeded - path completed!");
                }
                this.pathCompleted = true;
                if (forwardBuilder.trustAnchor.getTrustedCert() == null) {
                    forwardBuilder.addCertToPath(certificate, list2);
                }
                this.trustAnchor = forwardBuilder.trustAnchor;
                if (basicChecker != null) {
                    this.finalPublicKey = basicChecker.getPublicKey();
                }
                else {
                    Certificate trustedCert;
                    if (list2.isEmpty()) {
                        trustedCert = forwardBuilder.trustAnchor.getTrustedCert();
                    }
                    else {
                        trustedCert = list2.getLast();
                    }
                    this.finalPublicKey = trustedCert.getPublicKey();
                }
                this.policyTreeResult = policyChecker.getPolicyTree();
                return;
            }
            forwardBuilder.addCertToPath(certificate, list2);
            forwardState2.updateState(certificate);
            list.add((LinkedList)new LinkedList());
            vertex.setIndex(list.size() - 1);
            this.depthFirstSearchForward(certificate.getIssuerX500Principal(), forwardState2, forwardBuilder, list, list2);
            if (this.pathCompleted) {
                return;
            }
            if (SunCertPathBuilder.debug != null) {
                SunCertPathBuilder.debug.println("SunCertPathBuilder.depthFirstSearchForward(): backtracking");
            }
            forwardBuilder.removeFinalCertFromPath(list2);
        }
    }
    
    private static List<Vertex> addVertices(final Collection<X509Certificate> collection, final List<List<Vertex>> list) {
        final List list2 = list.get(list.size() - 1);
        final Iterator<X509Certificate> iterator = collection.iterator();
        while (iterator.hasNext()) {
            list2.add(new Vertex(iterator.next()));
        }
        return list2;
    }
    
    private static boolean anchorIsTarget(final TrustAnchor trustAnchor, final CertSelector certSelector) {
        final X509Certificate trustedCert = trustAnchor.getTrustedCert();
        return trustedCert != null && certSelector.match(trustedCert);
    }
    
    static {
        debug = Debug.getInstance("certpath");
    }
}
