package org.apache.axiom.core;

public interface ElementMatcher<T extends CoreElement>
{
    public static final ElementMatcher<CoreElement> ANY = new ElementMatcher<CoreElement>() {
        public boolean matches(final CoreElement element, final String namespaceURI, final String name) {
            return true;
        }
    };
    public static final ElementMatcher<CoreNSAwareElement> BY_QNAME = new ElementMatcher<CoreNSAwareElement>() {
        public boolean matches(final CoreNSAwareElement element, final String namespaceURI, final String name) {
            return name.equals(element.coreGetLocalName()) && namespaceURI.equals(element.coreGetNamespaceURI());
        }
    };
    public static final ElementMatcher<CoreNSAwareElement> BY_NAMESPACE_URI = new ElementMatcher<CoreNSAwareElement>() {
        public boolean matches(final CoreNSAwareElement element, final String namespaceURI, final String name) {
            return namespaceURI.equals(element.coreGetNamespaceURI());
        }
    };
    public static final ElementMatcher<CoreNSAwareElement> BY_LOCAL_NAME = new ElementMatcher<CoreNSAwareElement>() {
        public boolean matches(final CoreNSAwareElement element, final String namespaceURI, final String name) {
            return name.equals(element.coreGetLocalName());
        }
    };
    public static final ElementMatcher<CoreElement> BY_NAME = new ElementMatcher<CoreElement>() {
        public boolean matches(final CoreElement element, final String namespaceURI, final String name) {
            if (element instanceof CoreNSUnawareElement) {
                return name.equals(CoreNSUnawareNamedNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreNSUnawareNamedNodeSupport$org_apache_axiom_core_CoreNSUnawareNamedNode$coreGetName((CoreNSUnawareNamedNode)element));
            }
            final CoreNSAwareElement nsAwareElement = (CoreNSAwareElement)element;
            final String prefix = nsAwareElement.coreGetPrefix();
            final int prefixLength = prefix.length();
            final String localName = nsAwareElement.coreGetLocalName();
            if (prefixLength == 0) {
                return name.equals(localName);
            }
            final int localNameLength = localName.length();
            if (prefixLength + localNameLength + 1 != name.length()) {
                return false;
            }
            if (name.charAt(prefixLength) != ':') {
                return false;
            }
            for (int i = 0; i < localNameLength; ++i) {
                if (name.charAt(prefixLength + i + 1) != localName.charAt(i)) {
                    return false;
                }
            }
            for (int i = 0; i < prefix.length(); ++i) {
                if (name.charAt(i) != prefix.charAt(i)) {
                    return false;
                }
            }
            return true;
        }
    };
    
    boolean matches(final T p0, final String p1, final String p2);
}
