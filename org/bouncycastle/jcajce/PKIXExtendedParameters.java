package org.bouncycastle.jcajce;

import java.util.Collection;
import java.security.cert.CertSelector;
import java.util.ArrayList;
import java.security.cert.CertStore;
import java.util.HashMap;
import java.util.Collections;
import java.security.cert.TrustAnchor;
import java.util.Set;
import org.bouncycastle.asn1.x509.GeneralName;
import java.util.Map;
import java.util.List;
import java.util.Date;
import java.security.cert.PKIXParameters;
import java.security.cert.CertPathParameters;

public class PKIXExtendedParameters implements CertPathParameters
{
    public static final int PKIX_VALIDITY_MODEL = 0;
    public static final int CHAIN_VALIDITY_MODEL = 1;
    private final PKIXParameters baseParameters;
    private final PKIXCertStoreSelector targetConstraints;
    private final Date date;
    private final List<PKIXCertStore> extraCertStores;
    private final Map<GeneralName, PKIXCertStore> namedCertificateStoreMap;
    private final List<PKIXCRLStore> extraCRLStores;
    private final Map<GeneralName, PKIXCRLStore> namedCRLStoreMap;
    private final boolean revocationEnabled;
    private final boolean useDeltas;
    private final int validityModel;
    private final Set<TrustAnchor> trustAnchors;
    
    private PKIXExtendedParameters(final Builder builder) {
        this.baseParameters = builder.baseParameters;
        this.date = builder.date;
        this.extraCertStores = (List<PKIXCertStore>)Collections.unmodifiableList((List<? extends PKIXCertStore>)builder.extraCertStores);
        this.namedCertificateStoreMap = (Map<GeneralName, PKIXCertStore>)Collections.unmodifiableMap((Map<? extends GeneralName, ? extends PKIXCertStore>)new HashMap<GeneralName, PKIXCertStore>(builder.namedCertificateStoreMap));
        this.extraCRLStores = (List<PKIXCRLStore>)Collections.unmodifiableList((List<? extends PKIXCRLStore>)builder.extraCRLStores);
        this.namedCRLStoreMap = (Map<GeneralName, PKIXCRLStore>)Collections.unmodifiableMap((Map<? extends GeneralName, ? extends PKIXCRLStore>)new HashMap<GeneralName, PKIXCRLStore>(builder.namedCRLStoreMap));
        this.targetConstraints = builder.targetConstraints;
        this.revocationEnabled = builder.revocationEnabled;
        this.useDeltas = builder.useDeltas;
        this.validityModel = builder.validityModel;
        this.trustAnchors = Collections.unmodifiableSet((Set<? extends TrustAnchor>)builder.trustAnchors);
    }
    
    public List<PKIXCertStore> getCertificateStores() {
        return this.extraCertStores;
    }
    
    public Map<GeneralName, PKIXCertStore> getNamedCertificateStoreMap() {
        return this.namedCertificateStoreMap;
    }
    
    public List<PKIXCRLStore> getCRLStores() {
        return this.extraCRLStores;
    }
    
    public Map<GeneralName, PKIXCRLStore> getNamedCRLStoreMap() {
        return this.namedCRLStoreMap;
    }
    
    public Date getDate() {
        return new Date(this.date.getTime());
    }
    
    public boolean isUseDeltasEnabled() {
        return this.useDeltas;
    }
    
    public int getValidityModel() {
        return this.validityModel;
    }
    
    public Object clone() {
        return this;
    }
    
    public PKIXCertStoreSelector getTargetConstraints() {
        return this.targetConstraints;
    }
    
    public Set getTrustAnchors() {
        return this.trustAnchors;
    }
    
    public Set getInitialPolicies() {
        return this.baseParameters.getInitialPolicies();
    }
    
    public String getSigProvider() {
        return this.baseParameters.getSigProvider();
    }
    
    public boolean isExplicitPolicyRequired() {
        return this.baseParameters.isExplicitPolicyRequired();
    }
    
    public boolean isAnyPolicyInhibited() {
        return this.baseParameters.isAnyPolicyInhibited();
    }
    
    public boolean isPolicyMappingInhibited() {
        return this.baseParameters.isPolicyMappingInhibited();
    }
    
    public List getCertPathCheckers() {
        return this.baseParameters.getCertPathCheckers();
    }
    
    public List<CertStore> getCertStores() {
        return this.baseParameters.getCertStores();
    }
    
    public boolean isRevocationEnabled() {
        return this.revocationEnabled;
    }
    
    public static class Builder
    {
        private final PKIXParameters baseParameters;
        private final Date date;
        private PKIXCertStoreSelector targetConstraints;
        private List<PKIXCertStore> extraCertStores;
        private Map<GeneralName, PKIXCertStore> namedCertificateStoreMap;
        private List<PKIXCRLStore> extraCRLStores;
        private Map<GeneralName, PKIXCRLStore> namedCRLStoreMap;
        private boolean revocationEnabled;
        private int validityModel;
        private boolean useDeltas;
        private Set<TrustAnchor> trustAnchors;
        
        public Builder(final PKIXParameters pkixParameters) {
            this.extraCertStores = new ArrayList<PKIXCertStore>();
            this.namedCertificateStoreMap = new HashMap<GeneralName, PKIXCertStore>();
            this.extraCRLStores = new ArrayList<PKIXCRLStore>();
            this.namedCRLStoreMap = new HashMap<GeneralName, PKIXCRLStore>();
            this.validityModel = 0;
            this.useDeltas = false;
            this.baseParameters = (PKIXParameters)pkixParameters.clone();
            final CertSelector targetCertConstraints = pkixParameters.getTargetCertConstraints();
            if (targetCertConstraints != null) {
                this.targetConstraints = new PKIXCertStoreSelector.Builder(targetCertConstraints).build();
            }
            final Date date = pkixParameters.getDate();
            this.date = ((date == null) ? new Date() : date);
            this.revocationEnabled = pkixParameters.isRevocationEnabled();
            this.trustAnchors = pkixParameters.getTrustAnchors();
        }
        
        public Builder(final PKIXExtendedParameters pkixExtendedParameters) {
            this.extraCertStores = new ArrayList<PKIXCertStore>();
            this.namedCertificateStoreMap = new HashMap<GeneralName, PKIXCertStore>();
            this.extraCRLStores = new ArrayList<PKIXCRLStore>();
            this.namedCRLStoreMap = new HashMap<GeneralName, PKIXCRLStore>();
            this.validityModel = 0;
            this.useDeltas = false;
            this.baseParameters = pkixExtendedParameters.baseParameters;
            this.date = pkixExtendedParameters.date;
            this.targetConstraints = pkixExtendedParameters.targetConstraints;
            this.extraCertStores = new ArrayList<PKIXCertStore>(pkixExtendedParameters.extraCertStores);
            this.namedCertificateStoreMap = new HashMap<GeneralName, PKIXCertStore>(pkixExtendedParameters.namedCertificateStoreMap);
            this.extraCRLStores = new ArrayList<PKIXCRLStore>(pkixExtendedParameters.extraCRLStores);
            this.namedCRLStoreMap = new HashMap<GeneralName, PKIXCRLStore>(pkixExtendedParameters.namedCRLStoreMap);
            this.useDeltas = pkixExtendedParameters.useDeltas;
            this.validityModel = pkixExtendedParameters.validityModel;
            this.revocationEnabled = pkixExtendedParameters.isRevocationEnabled();
            this.trustAnchors = pkixExtendedParameters.getTrustAnchors();
        }
        
        public Builder addCertificateStore(final PKIXCertStore pkixCertStore) {
            this.extraCertStores.add(pkixCertStore);
            return this;
        }
        
        public Builder addNamedCertificateStore(final GeneralName generalName, final PKIXCertStore pkixCertStore) {
            this.namedCertificateStoreMap.put(generalName, pkixCertStore);
            return this;
        }
        
        public Builder addCRLStore(final PKIXCRLStore pkixcrlStore) {
            this.extraCRLStores.add(pkixcrlStore);
            return this;
        }
        
        public Builder addNamedCRLStore(final GeneralName generalName, final PKIXCRLStore pkixcrlStore) {
            this.namedCRLStoreMap.put(generalName, pkixcrlStore);
            return this;
        }
        
        public Builder setTargetConstraints(final PKIXCertStoreSelector targetConstraints) {
            this.targetConstraints = targetConstraints;
            return this;
        }
        
        public Builder setUseDeltasEnabled(final boolean useDeltas) {
            this.useDeltas = useDeltas;
            return this;
        }
        
        public Builder setValidityModel(final int validityModel) {
            this.validityModel = validityModel;
            return this;
        }
        
        public Builder setTrustAnchor(final TrustAnchor trustAnchor) {
            this.trustAnchors = Collections.singleton(trustAnchor);
            return this;
        }
        
        public Builder setTrustAnchors(final Set<TrustAnchor> trustAnchors) {
            this.trustAnchors = trustAnchors;
            return this;
        }
        
        public void setRevocationEnabled(final boolean revocationEnabled) {
            this.revocationEnabled = revocationEnabled;
        }
        
        public PKIXExtendedParameters build() {
            return new PKIXExtendedParameters(this, null);
        }
    }
}
