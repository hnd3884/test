package com.sun.xml.internal.ws.policy.sourcemodel.wspolicy;

import java.util.Collections;
import java.util.HashMap;
import javax.xml.namespace.QName;
import java.util.Map;

public enum NamespaceVersion
{
    v1_2("http://schemas.xmlsoap.org/ws/2004/09/policy", "wsp1_2", new XmlToken[] { XmlToken.Policy, XmlToken.ExactlyOne, XmlToken.All, XmlToken.PolicyReference, XmlToken.UsingPolicy, XmlToken.Name, XmlToken.Optional, XmlToken.Ignorable, XmlToken.PolicyUris, XmlToken.Uri, XmlToken.Digest, XmlToken.DigestAlgorithm }), 
    v1_5("http://www.w3.org/ns/ws-policy", "wsp", new XmlToken[] { XmlToken.Policy, XmlToken.ExactlyOne, XmlToken.All, XmlToken.PolicyReference, XmlToken.UsingPolicy, XmlToken.Name, XmlToken.Optional, XmlToken.Ignorable, XmlToken.PolicyUris, XmlToken.Uri, XmlToken.Digest, XmlToken.DigestAlgorithm });
    
    private final String nsUri;
    private final String defaultNsPrefix;
    private final Map<XmlToken, QName> tokenToQNameCache;
    
    public static NamespaceVersion resolveVersion(final String uri) {
        for (final NamespaceVersion namespaceVersion : values()) {
            if (namespaceVersion.toString().equalsIgnoreCase(uri)) {
                return namespaceVersion;
            }
        }
        return null;
    }
    
    public static NamespaceVersion resolveVersion(final QName name) {
        return resolveVersion(name.getNamespaceURI());
    }
    
    public static NamespaceVersion getLatestVersion() {
        return NamespaceVersion.v1_5;
    }
    
    public static XmlToken resolveAsToken(final QName name) {
        final NamespaceVersion nsVersion = resolveVersion(name);
        if (nsVersion != null) {
            final XmlToken token = XmlToken.resolveToken(name.getLocalPart());
            if (nsVersion.tokenToQNameCache.containsKey(token)) {
                return token;
            }
        }
        return XmlToken.UNKNOWN;
    }
    
    private NamespaceVersion(final String uri, final String prefix, final XmlToken[] supportedTokens) {
        this.nsUri = uri;
        this.defaultNsPrefix = prefix;
        final Map<XmlToken, QName> temp = new HashMap<XmlToken, QName>();
        for (final XmlToken token : supportedTokens) {
            temp.put(token, new QName(this.nsUri, token.toString()));
        }
        this.tokenToQNameCache = Collections.unmodifiableMap((Map<? extends XmlToken, ? extends QName>)temp);
    }
    
    public String getDefaultNamespacePrefix() {
        return this.defaultNsPrefix;
    }
    
    public QName asQName(final XmlToken token) throws IllegalArgumentException {
        return this.tokenToQNameCache.get(token);
    }
    
    @Override
    public String toString() {
        return this.nsUri;
    }
}
