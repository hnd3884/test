package com.sun.xml.internal.txw2;

class StartTag extends Content implements NamespaceResolver
{
    private String uri;
    private final String localName;
    private Attribute firstAtt;
    private Attribute lastAtt;
    private ContainerElement owner;
    private NamespaceDecl firstNs;
    private NamespaceDecl lastNs;
    final Document document;
    
    public StartTag(final ContainerElement owner, final String uri, final String localName) {
        this(owner.document, uri, localName);
        this.owner = owner;
    }
    
    public StartTag(final Document document, final String uri, final String localName) {
        assert uri != null;
        assert localName != null;
        this.uri = uri;
        this.localName = localName;
        this.document = document;
        this.addNamespaceDecl(uri, null, false);
    }
    
    public void addAttribute(final String nsUri, final String localName, final Object arg) {
        this.checkWritable();
        Attribute a;
        for (a = this.firstAtt; a != null && !a.hasName(nsUri, localName); a = a.next) {}
        if (a == null) {
            a = new Attribute(nsUri, localName);
            if (this.lastAtt == null) {
                assert this.firstAtt == null;
                final Attribute attribute = a;
                this.lastAtt = attribute;
                this.firstAtt = attribute;
            }
            else {
                assert this.firstAtt != null;
                this.lastAtt.next = a;
                this.lastAtt = a;
            }
            if (nsUri.length() > 0) {
                this.addNamespaceDecl(nsUri, null, true);
            }
        }
        this.document.writeValue(arg, this, a.value);
    }
    
    public NamespaceDecl addNamespaceDecl(final String uri, String prefix, final boolean requirePrefix) {
        this.checkWritable();
        if (uri == null) {
            throw new IllegalArgumentException();
        }
        if (uri.length() == 0) {
            if (requirePrefix) {
                throw new IllegalArgumentException("The empty namespace cannot have a non-empty prefix");
            }
            if (prefix != null && prefix.length() > 0) {
                throw new IllegalArgumentException("The empty namespace can be only bound to the empty prefix");
            }
            prefix = "";
        }
        for (NamespaceDecl n = this.firstNs; n != null; n = n.next) {
            if (uri.equals(n.uri)) {
                if (prefix == null) {
                    final NamespaceDecl namespaceDecl = n;
                    namespaceDecl.requirePrefix |= requirePrefix;
                    return n;
                }
                if (n.prefix == null) {
                    n.prefix = prefix;
                    final NamespaceDecl namespaceDecl2 = n;
                    namespaceDecl2.requirePrefix |= requirePrefix;
                    return n;
                }
                if (prefix.equals(n.prefix)) {
                    final NamespaceDecl namespaceDecl3 = n;
                    namespaceDecl3.requirePrefix |= requirePrefix;
                    return n;
                }
            }
            if (prefix != null && n.prefix != null && n.prefix.equals(prefix)) {
                throw new IllegalArgumentException("Prefix '" + prefix + "' is already bound to '" + n.uri + '\'');
            }
        }
        final NamespaceDecl ns = new NamespaceDecl(this.document.assignNewId(), uri, prefix, requirePrefix);
        if (this.lastNs == null) {
            assert this.firstNs == null;
            final NamespaceDecl namespaceDecl4 = ns;
            this.lastNs = namespaceDecl4;
            this.firstNs = namespaceDecl4;
        }
        else {
            assert this.firstNs != null;
            this.lastNs.next = ns;
            this.lastNs = ns;
        }
        return ns;
    }
    
    private void checkWritable() {
        if (this.isWritten()) {
            throw new IllegalStateException("The start tag of " + this.localName + " has already been written. If you need out of order writing, see the TypedXmlWriter.block method");
        }
    }
    
    boolean isWritten() {
        return this.uri == null;
    }
    
    @Override
    boolean isReadyToCommit() {
        if (this.owner != null && this.owner.isBlocked()) {
            return false;
        }
        for (Content c = this.getNext(); c != null; c = c.getNext()) {
            if (c.concludesPendingStartTag()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void written() {
        final Attribute attribute = null;
        this.lastAtt = attribute;
        this.firstAtt = attribute;
        this.uri = null;
        if (this.owner != null) {
            assert this.owner.startTag == this;
            this.owner.startTag = null;
        }
    }
    
    @Override
    boolean concludesPendingStartTag() {
        return true;
    }
    
    @Override
    void accept(final ContentVisitor visitor) {
        visitor.onStartTag(this.uri, this.localName, this.firstAtt, this.firstNs);
    }
    
    @Override
    public String getPrefix(final String nsUri) {
        final NamespaceDecl ns = this.addNamespaceDecl(nsUri, null, false);
        if (ns.prefix != null) {
            return ns.prefix;
        }
        return ns.dummyPrefix;
    }
}
