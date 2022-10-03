package java.security.cert;

import java.util.HashSet;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.Map;
import java.util.List;
import java.net.URI;

public abstract class PKIXRevocationChecker extends PKIXCertPathChecker
{
    private URI ocspResponder;
    private X509Certificate ocspResponderCert;
    private List<Extension> ocspExtensions;
    private Map<X509Certificate, byte[]> ocspResponses;
    private Set<Option> options;
    
    protected PKIXRevocationChecker() {
        this.ocspExtensions = Collections.emptyList();
        this.ocspResponses = Collections.emptyMap();
        this.options = Collections.emptySet();
    }
    
    public void setOcspResponder(final URI ocspResponder) {
        this.ocspResponder = ocspResponder;
    }
    
    public URI getOcspResponder() {
        return this.ocspResponder;
    }
    
    public void setOcspResponderCert(final X509Certificate ocspResponderCert) {
        this.ocspResponderCert = ocspResponderCert;
    }
    
    public X509Certificate getOcspResponderCert() {
        return this.ocspResponderCert;
    }
    
    public void setOcspExtensions(final List<Extension> list) {
        this.ocspExtensions = ((list == null) ? Collections.emptyList() : new ArrayList<Extension>(list));
    }
    
    public List<Extension> getOcspExtensions() {
        return Collections.unmodifiableList((List<? extends Extension>)this.ocspExtensions);
    }
    
    public void setOcspResponses(final Map<X509Certificate, byte[]> map) {
        if (map == null) {
            this.ocspResponses = Collections.emptyMap();
        }
        else {
            final HashMap ocspResponses = new HashMap(map.size());
            for (final Map.Entry entry : map.entrySet()) {
                ocspResponses.put(entry.getKey(), ((byte[])entry.getValue()).clone());
            }
            this.ocspResponses = ocspResponses;
        }
    }
    
    public Map<X509Certificate, byte[]> getOcspResponses() {
        final HashMap hashMap = new HashMap(this.ocspResponses.size());
        for (final Map.Entry entry : this.ocspResponses.entrySet()) {
            hashMap.put(entry.getKey(), ((byte[])entry.getValue()).clone());
        }
        return hashMap;
    }
    
    public void setOptions(final Set<Option> set) {
        this.options = ((set == null) ? Collections.emptySet() : new HashSet<Option>(set));
    }
    
    public Set<Option> getOptions() {
        return Collections.unmodifiableSet((Set<? extends Option>)this.options);
    }
    
    public abstract List<CertPathValidatorException> getSoftFailExceptions();
    
    @Override
    public PKIXRevocationChecker clone() {
        final PKIXRevocationChecker pkixRevocationChecker = (PKIXRevocationChecker)super.clone();
        pkixRevocationChecker.ocspExtensions = new ArrayList<Extension>(this.ocspExtensions);
        pkixRevocationChecker.ocspResponses = new HashMap<X509Certificate, byte[]>(this.ocspResponses);
        for (final Map.Entry entry : pkixRevocationChecker.ocspResponses.entrySet()) {
            entry.setValue(((byte[])entry.getValue()).clone());
        }
        pkixRevocationChecker.options = new HashSet<Option>(this.options);
        return pkixRevocationChecker;
    }
    
    public enum Option
    {
        ONLY_END_ENTITY, 
        PREFER_CRLS, 
        NO_FALLBACK, 
        SOFT_FAIL;
    }
}
