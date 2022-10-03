package sun.security.rsa;

import java.util.Iterator;
import sun.security.provider.SunEntries;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.security.Provider;
import java.util.LinkedHashSet;

public final class SunRsaSignEntries
{
    private LinkedHashSet<Provider.Service> services;
    
    private void add(final Provider provider, final String s, final String s2, final String s3, final List<String> list, final HashMap<String, String> hashMap) {
        this.services.add(new Provider.Service(provider, s, s2, s3, list, hashMap));
    }
    
    public SunRsaSignEntries(final Provider provider) {
        this.services = new LinkedHashSet<Provider.Service>(20, 0.9f);
        final String s = "1.2.840.113549.1.1";
        final List<String> aliasesWithOid = SunEntries.createAliasesWithOid(s);
        final List<String> aliasesWithOid2 = SunEntries.createAliasesWithOid(s + ".10");
        final String s2 = "1.3.14.3.2.29";
        final HashMap hashMap = new HashMap(3);
        hashMap.put("SupportedKeyClasses", "java.security.interfaces.RSAPublicKey|java.security.interfaces.RSAPrivateKey");
        this.add(provider, "KeyFactory", "RSA", "sun.security.rsa.RSAKeyFactory$Legacy", aliasesWithOid, null);
        this.add(provider, "KeyPairGenerator", "RSA", "sun.security.rsa.RSAKeyPairGenerator$Legacy", aliasesWithOid, null);
        this.add(provider, "Signature", "MD2withRSA", "sun.security.rsa.RSASignature$MD2withRSA", SunEntries.createAliasesWithOid(s + ".2"), hashMap);
        this.add(provider, "Signature", "MD5withRSA", "sun.security.rsa.RSASignature$MD5withRSA", SunEntries.createAliasesWithOid(s + ".4"), hashMap);
        this.add(provider, "Signature", "SHA1withRSA", "sun.security.rsa.RSASignature$SHA1withRSA", SunEntries.createAliasesWithOid(s + ".5", s2), hashMap);
        this.add(provider, "Signature", "SHA224withRSA", "sun.security.rsa.RSASignature$SHA224withRSA", SunEntries.createAliasesWithOid(s + ".14"), hashMap);
        this.add(provider, "Signature", "SHA256withRSA", "sun.security.rsa.RSASignature$SHA256withRSA", SunEntries.createAliasesWithOid(s + ".11"), hashMap);
        this.add(provider, "Signature", "SHA384withRSA", "sun.security.rsa.RSASignature$SHA384withRSA", SunEntries.createAliasesWithOid(s + ".12"), hashMap);
        this.add(provider, "Signature", "SHA512withRSA", "sun.security.rsa.RSASignature$SHA512withRSA", SunEntries.createAliasesWithOid(s + ".13"), hashMap);
        this.add(provider, "Signature", "SHA512/224withRSA", "sun.security.rsa.RSASignature$SHA512_224withRSA", SunEntries.createAliasesWithOid(s + ".15"), hashMap);
        this.add(provider, "Signature", "SHA512/256withRSA", "sun.security.rsa.RSASignature$SHA512_256withRSA", SunEntries.createAliasesWithOid(s + ".16"), hashMap);
        this.add(provider, "KeyFactory", "RSASSA-PSS", "sun.security.rsa.RSAKeyFactory$PSS", aliasesWithOid2, null);
        this.add(provider, "KeyPairGenerator", "RSASSA-PSS", "sun.security.rsa.RSAKeyPairGenerator$PSS", aliasesWithOid2, null);
        this.add(provider, "Signature", "RSASSA-PSS", "sun.security.rsa.RSAPSSSignature", aliasesWithOid2, hashMap);
        this.add(provider, "AlgorithmParameters", "RSASSA-PSS", "sun.security.rsa.PSSParameters", aliasesWithOid2, null);
    }
    
    public Iterator<Provider.Service> iterator() {
        return this.services.iterator();
    }
}
