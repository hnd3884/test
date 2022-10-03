package com.sun.xml.internal.txw2;

import java.lang.reflect.Proxy;
import javax.xml.namespace.QName;
import com.sun.xml.internal.txw2.annotation.XmlNamespace;
import com.sun.xml.internal.txw2.annotation.XmlCDATA;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.txw2.annotation.XmlValue;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;

final class ContainerElement implements InvocationHandler, TypedXmlWriter
{
    final Document document;
    StartTag startTag;
    final EndTag endTag;
    private final String nsUri;
    private Content tail;
    private ContainerElement prevOpen;
    private ContainerElement nextOpen;
    private final ContainerElement parent;
    private ContainerElement lastOpenChild;
    private boolean blocked;
    
    public ContainerElement(final Document document, final ContainerElement parent, final String nsUri, final String localName) {
        this.endTag = new EndTag();
        this.parent = parent;
        this.document = document;
        this.nsUri = nsUri;
        this.startTag = new StartTag(this, nsUri, localName);
        this.tail = this.startTag;
        if (this.isRoot()) {
            document.setFirstContent(this.startTag);
        }
    }
    
    private boolean isRoot() {
        return this.parent == null;
    }
    
    private boolean isCommitted() {
        return this.tail == null;
    }
    
    @Override
    public Document getDocument() {
        return this.document;
    }
    
    boolean isBlocked() {
        return this.blocked && !this.isCommitted();
    }
    
    @Override
    public void block() {
        this.blocked = true;
    }
    
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        Label_0033: {
            if (method.getDeclaringClass() != TypedXmlWriter.class) {
                if (method.getDeclaringClass() != Object.class) {
                    break Label_0033;
                }
            }
            try {
                return method.invoke(this, args);
            }
            catch (final InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
        final XmlAttribute xa = method.getAnnotation(XmlAttribute.class);
        final XmlValue xv = method.getAnnotation(XmlValue.class);
        final XmlElement xe = method.getAnnotation(XmlElement.class);
        if (xa != null) {
            if (xv != null || xe != null) {
                throw new IllegalAnnotationException(method.toString());
            }
            this.addAttribute(xa, method, args);
            return proxy;
        }
        else {
            if (xv == null) {
                return this.addElement(xe, method, args);
            }
            if (xe != null) {
                throw new IllegalAnnotationException(method.toString());
            }
            this._pcdata(args);
            return proxy;
        }
    }
    
    private void addAttribute(final XmlAttribute xa, final Method method, final Object[] args) {
        assert xa != null;
        this.checkStartTag();
        String localName = xa.value();
        if (xa.value().length() == 0) {
            localName = method.getName();
        }
        this._attribute(xa.ns(), localName, args);
    }
    
    private void checkStartTag() {
        if (this.startTag == null) {
            throw new IllegalStateException("start tag has already been written");
        }
    }
    
    private Object addElement(final XmlElement e, final Method method, final Object[] args) {
        final Class<?> rt = method.getReturnType();
        String nsUri = "##default";
        String localName = method.getName();
        if (e != null) {
            if (e.value().length() != 0) {
                localName = e.value();
            }
            nsUri = e.ns();
        }
        if (nsUri.equals("##default")) {
            final Class<?> c = method.getDeclaringClass();
            final XmlElement ce = c.getAnnotation(XmlElement.class);
            if (ce != null) {
                nsUri = ce.ns();
            }
            if (nsUri.equals("##default")) {
                nsUri = this.getNamespace(c.getPackage());
            }
        }
        if (rt == Void.TYPE) {
            final boolean isCDATA = method.getAnnotation(XmlCDATA.class) != null;
            final StartTag st = new StartTag(this.document, nsUri, localName);
            this.addChild(st);
            for (final Object arg : args) {
                Text text;
                if (isCDATA) {
                    text = new Cdata(this.document, st, arg);
                }
                else {
                    text = new Pcdata(this.document, st, arg);
                }
                this.addChild(text);
            }
            this.addChild(new EndTag());
            return null;
        }
        if (TypedXmlWriter.class.isAssignableFrom(rt)) {
            return this._element(nsUri, localName, rt);
        }
        throw new IllegalSignatureException("Illegal return type: " + rt);
    }
    
    private String getNamespace(final Package pkg) {
        if (pkg == null) {
            return "";
        }
        final XmlNamespace ns = pkg.getAnnotation(XmlNamespace.class);
        String nsUri;
        if (ns != null) {
            nsUri = ns.value();
        }
        else {
            nsUri = "";
        }
        return nsUri;
    }
    
    private void addChild(final Content child) {
        this.tail.setNext(this.document, child);
        this.tail = child;
    }
    
    @Override
    public void commit() {
        this.commit(true);
    }
    
    @Override
    public void commit(final boolean includingAllPredecessors) {
        this._commit(includingAllPredecessors);
        this.document.flush();
    }
    
    private void _commit(final boolean includingAllPredecessors) {
        if (this.isCommitted()) {
            return;
        }
        this.addChild(this.endTag);
        if (this.isRoot()) {
            this.addChild(new EndDocument());
        }
        this.tail = null;
        if (includingAllPredecessors) {
            for (ContainerElement e = this; e != null; e = e.parent) {
                while (e.prevOpen != null) {
                    e.prevOpen._commit(false);
                }
            }
        }
        while (this.lastOpenChild != null) {
            this.lastOpenChild._commit(false);
        }
        if (this.parent != null) {
            if (this.parent.lastOpenChild == this) {
                assert this.nextOpen == null : "this must be the last one";
                this.parent.lastOpenChild = this.prevOpen;
            }
            else {
                assert this.nextOpen.prevOpen == this;
                this.nextOpen.prevOpen = this.prevOpen;
            }
            if (this.prevOpen != null) {
                assert this.prevOpen.nextOpen == this;
                this.prevOpen.nextOpen = this.nextOpen;
            }
        }
        this.nextOpen = null;
        this.prevOpen = null;
    }
    
    @Override
    public void _attribute(final String localName, final Object value) {
        this._attribute("", localName, value);
    }
    
    @Override
    public void _attribute(final String nsUri, final String localName, final Object value) {
        this.checkStartTag();
        this.startTag.addAttribute(nsUri, localName, value);
    }
    
    @Override
    public void _attribute(final QName attributeName, final Object value) {
        this._attribute(attributeName.getNamespaceURI(), attributeName.getLocalPart(), value);
    }
    
    @Override
    public void _namespace(final String uri) {
        this._namespace(uri, false);
    }
    
    @Override
    public void _namespace(final String uri, final String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException();
        }
        this.checkStartTag();
        this.startTag.addNamespaceDecl(uri, prefix, false);
    }
    
    @Override
    public void _namespace(final String uri, final boolean requirePrefix) {
        this.checkStartTag();
        this.startTag.addNamespaceDecl(uri, null, requirePrefix);
    }
    
    @Override
    public void _pcdata(final Object value) {
        this.addChild(new Pcdata(this.document, this.startTag, value));
    }
    
    @Override
    public void _cdata(final Object value) {
        this.addChild(new Cdata(this.document, this.startTag, value));
    }
    
    @Override
    public void _comment(final Object value) throws UnsupportedOperationException {
        this.addChild(new Comment(this.document, this.startTag, value));
    }
    
    @Override
    public <T extends TypedXmlWriter> T _element(final String localName, final Class<T> contentModel) {
        return this._element(this.nsUri, localName, contentModel);
    }
    
    @Override
    public <T extends TypedXmlWriter> T _element(final QName tagName, final Class<T> contentModel) {
        return this._element(tagName.getNamespaceURI(), tagName.getLocalPart(), contentModel);
    }
    
    @Override
    public <T extends TypedXmlWriter> T _element(final Class<T> contentModel) {
        return this._element(TXW.getTagName(contentModel), contentModel);
    }
    
    @Override
    public <T extends TypedXmlWriter> T _cast(final Class<T> facadeType) {
        return facadeType.cast(Proxy.newProxyInstance(facadeType.getClassLoader(), new Class[] { facadeType }, this));
    }
    
    @Override
    public <T extends TypedXmlWriter> T _element(final String nsUri, final String localName, final Class<T> contentModel) {
        final ContainerElement child = new ContainerElement(this.document, this, nsUri, localName);
        this.addChild(child.startTag);
        this.tail = child.endTag;
        if (this.lastOpenChild != null) {
            assert this.lastOpenChild.parent == this;
            assert child.prevOpen == null;
            assert child.nextOpen == null;
            child.prevOpen = this.lastOpenChild;
            assert this.lastOpenChild.nextOpen == null;
            this.lastOpenChild.nextOpen = child;
        }
        this.lastOpenChild = child;
        return child._cast(contentModel);
    }
}
