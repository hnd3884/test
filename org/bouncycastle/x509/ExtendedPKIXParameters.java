package org.bouncycastle.x509;

import java.security.cert.X509CertSelector;
import java.security.cert.CertSelector;
import java.util.Collections;
import org.bouncycastle.util.Store;
import java.util.Iterator;
import java.security.cert.CertStore;
import java.util.Collection;
import java.security.InvalidAlgorithmParameterException;
import java.util.HashSet;
import java.util.ArrayList;
import java.security.cert.TrustAnchor;
import java.util.Set;
import org.bouncycastle.util.Selector;
import java.util.List;
import java.security.cert.PKIXParameters;

public class ExtendedPKIXParameters extends PKIXParameters
{
    private List stores;
    private Selector selector;
    private boolean additionalLocationsEnabled;
    private List additionalStores;
    private Set trustedACIssuers;
    private Set necessaryACAttributes;
    private Set prohibitedACAttributes;
    private Set attrCertCheckers;
    public static final int PKIX_VALIDITY_MODEL = 0;
    public static final int CHAIN_VALIDITY_MODEL = 1;
    private int validityModel;
    private boolean useDeltas;
    
    public ExtendedPKIXParameters(final Set set) throws InvalidAlgorithmParameterException {
        super(set);
        this.validityModel = 0;
        this.useDeltas = false;
        this.stores = new ArrayList();
        this.additionalStores = new ArrayList();
        this.trustedACIssuers = new HashSet();
        this.necessaryACAttributes = new HashSet();
        this.prohibitedACAttributes = new HashSet();
        this.attrCertCheckers = new HashSet();
    }
    
    public static ExtendedPKIXParameters getInstance(final PKIXParameters params) {
        ExtendedPKIXParameters extendedPKIXParameters;
        try {
            extendedPKIXParameters = new ExtendedPKIXParameters(params.getTrustAnchors());
        }
        catch (final Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
        extendedPKIXParameters.setParams(params);
        return extendedPKIXParameters;
    }
    
    protected void setParams(final PKIXParameters pkixParameters) {
        this.setDate(pkixParameters.getDate());
        this.setCertPathCheckers(pkixParameters.getCertPathCheckers());
        this.setCertStores(pkixParameters.getCertStores());
        this.setAnyPolicyInhibited(pkixParameters.isAnyPolicyInhibited());
        this.setExplicitPolicyRequired(pkixParameters.isExplicitPolicyRequired());
        this.setPolicyMappingInhibited(pkixParameters.isPolicyMappingInhibited());
        this.setRevocationEnabled(pkixParameters.isRevocationEnabled());
        this.setInitialPolicies(pkixParameters.getInitialPolicies());
        this.setPolicyQualifiersRejected(pkixParameters.getPolicyQualifiersRejected());
        this.setSigProvider(pkixParameters.getSigProvider());
        this.setTargetCertConstraints(pkixParameters.getTargetCertConstraints());
        try {
            this.setTrustAnchors(pkixParameters.getTrustAnchors());
        }
        catch (final Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
        if (pkixParameters instanceof ExtendedPKIXParameters) {
            final ExtendedPKIXParameters extendedPKIXParameters = (ExtendedPKIXParameters)pkixParameters;
            this.validityModel = extendedPKIXParameters.validityModel;
            this.useDeltas = extendedPKIXParameters.useDeltas;
            this.additionalLocationsEnabled = extendedPKIXParameters.additionalLocationsEnabled;
            this.selector = ((extendedPKIXParameters.selector == null) ? null : ((Selector)extendedPKIXParameters.selector.clone()));
            this.stores = new ArrayList(extendedPKIXParameters.stores);
            this.additionalStores = new ArrayList(extendedPKIXParameters.additionalStores);
            this.trustedACIssuers = new HashSet(extendedPKIXParameters.trustedACIssuers);
            this.prohibitedACAttributes = new HashSet(extendedPKIXParameters.prohibitedACAttributes);
            this.necessaryACAttributes = new HashSet(extendedPKIXParameters.necessaryACAttributes);
            this.attrCertCheckers = new HashSet(extendedPKIXParameters.attrCertCheckers);
        }
    }
    
    public boolean isUseDeltasEnabled() {
        return this.useDeltas;
    }
    
    public void setUseDeltasEnabled(final boolean useDeltas) {
        this.useDeltas = useDeltas;
    }
    
    public int getValidityModel() {
        return this.validityModel;
    }
    
    @Override
    public void setCertStores(final List list) {
        if (list != null) {
            final Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                this.addCertStore((CertStore)iterator.next());
            }
        }
    }
    
    public void setStores(final List list) {
        if (list == null) {
            this.stores = new ArrayList();
        }
        else {
            final Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                if (!(iterator.next() instanceof Store)) {
                    throw new ClassCastException("All elements of list must be of type org.bouncycastle.util.Store.");
                }
            }
            this.stores = new ArrayList(list);
        }
    }
    
    public void addStore(final Store store) {
        if (store != null) {
            this.stores.add(store);
        }
    }
    
    @Deprecated
    public void addAdditionalStore(final Store store) {
        if (store != null) {
            this.additionalStores.add(store);
        }
    }
    
    @Deprecated
    public void addAddionalStore(final Store store) {
        this.addAdditionalStore(store);
    }
    
    public List getAdditionalStores() {
        return Collections.unmodifiableList((List<?>)this.additionalStores);
    }
    
    public List getStores() {
        return Collections.unmodifiableList((List<?>)new ArrayList<Object>(this.stores));
    }
    
    public void setValidityModel(final int validityModel) {
        this.validityModel = validityModel;
    }
    
    @Override
    public Object clone() {
        ExtendedPKIXParameters extendedPKIXParameters;
        try {
            extendedPKIXParameters = new ExtendedPKIXParameters(this.getTrustAnchors());
        }
        catch (final Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
        extendedPKIXParameters.setParams(this);
        return extendedPKIXParameters;
    }
    
    public boolean isAdditionalLocationsEnabled() {
        return this.additionalLocationsEnabled;
    }
    
    public void setAdditionalLocationsEnabled(final boolean additionalLocationsEnabled) {
        this.additionalLocationsEnabled = additionalLocationsEnabled;
    }
    
    public Selector getTargetConstraints() {
        if (this.selector != null) {
            return (Selector)this.selector.clone();
        }
        return null;
    }
    
    public void setTargetConstraints(final Selector selector) {
        if (selector != null) {
            this.selector = (Selector)selector.clone();
        }
        else {
            this.selector = null;
        }
    }
    
    @Override
    public void setTargetCertConstraints(final CertSelector targetCertConstraints) {
        super.setTargetCertConstraints(targetCertConstraints);
        if (targetCertConstraints != null) {
            this.selector = X509CertStoreSelector.getInstance((X509CertSelector)targetCertConstraints);
        }
        else {
            this.selector = null;
        }
    }
    
    public Set getTrustedACIssuers() {
        return Collections.unmodifiableSet((Set<?>)this.trustedACIssuers);
    }
    
    public void setTrustedACIssuers(final Set set) {
        if (set == null) {
            this.trustedACIssuers.clear();
            return;
        }
        final Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            if (!(iterator.next() instanceof TrustAnchor)) {
                throw new ClassCastException("All elements of set must be of type " + TrustAnchor.class.getName() + ".");
            }
        }
        this.trustedACIssuers.clear();
        this.trustedACIssuers.addAll(set);
    }
    
    public Set getNecessaryACAttributes() {
        return Collections.unmodifiableSet((Set<?>)this.necessaryACAttributes);
    }
    
    public void setNecessaryACAttributes(final Set set) {
        if (set == null) {
            this.necessaryACAttributes.clear();
            return;
        }
        final Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            if (!(iterator.next() instanceof String)) {
                throw new ClassCastException("All elements of set must be of type String.");
            }
        }
        this.necessaryACAttributes.clear();
        this.necessaryACAttributes.addAll(set);
    }
    
    public Set getProhibitedACAttributes() {
        return Collections.unmodifiableSet((Set<?>)this.prohibitedACAttributes);
    }
    
    public void setProhibitedACAttributes(final Set set) {
        if (set == null) {
            this.prohibitedACAttributes.clear();
            return;
        }
        final Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            if (!(iterator.next() instanceof String)) {
                throw new ClassCastException("All elements of set must be of type String.");
            }
        }
        this.prohibitedACAttributes.clear();
        this.prohibitedACAttributes.addAll(set);
    }
    
    public Set getAttrCertCheckers() {
        return Collections.unmodifiableSet((Set<?>)this.attrCertCheckers);
    }
    
    public void setAttrCertCheckers(final Set set) {
        if (set == null) {
            this.attrCertCheckers.clear();
            return;
        }
        final Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            if (!(iterator.next() instanceof PKIXAttrCertChecker)) {
                throw new ClassCastException("All elements of set must be of type " + PKIXAttrCertChecker.class.getName() + ".");
            }
        }
        this.attrCertCheckers.clear();
        this.attrCertCheckers.addAll(set);
    }
}
