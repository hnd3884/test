package org.apache.xmlbeans.impl.values;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlCursor;
import java.lang.reflect.Proxy;
import org.apache.xmlbeans.xml.stream.StartElement;
import org.apache.xmlbeans.XmlObject;
import java.util.Map;
import org.apache.xmlbeans.impl.common.PrefixResolver;

public class NamespaceContext implements PrefixResolver
{
    private static final int TYPE_STORE = 1;
    private static final int XML_OBJECT = 2;
    private static final int MAP = 3;
    private static final int START_ELEMENT = 4;
    private static final int RESOLVER = 5;
    private Object _obj;
    private int _code;
    private static ThreadLocal tl_namespaceContextStack;
    
    public NamespaceContext(final Map prefixToUriMap) {
        this._code = 3;
        this._obj = prefixToUriMap;
    }
    
    public NamespaceContext(final TypeStore typeStore) {
        this._code = 1;
        this._obj = typeStore;
    }
    
    public NamespaceContext(final XmlObject xmlObject) {
        this._code = 2;
        this._obj = xmlObject;
    }
    
    public NamespaceContext(final StartElement start) {
        this._code = 4;
        this._obj = start;
    }
    
    public NamespaceContext(final PrefixResolver resolver) {
        this._code = 5;
        this._obj = resolver;
    }
    
    public static void clearThreadLocals() {
        NamespaceContext.tl_namespaceContextStack.remove();
    }
    
    private static NamespaceContextStack getNamespaceContextStack() {
        NamespaceContextStack namespaceContextStack = NamespaceContext.tl_namespaceContextStack.get();
        if (namespaceContextStack == null) {
            namespaceContextStack = new NamespaceContextStack();
            NamespaceContext.tl_namespaceContextStack.set(namespaceContextStack);
        }
        return namespaceContextStack;
    }
    
    public static void push(final NamespaceContext next) {
        getNamespaceContextStack().push(next);
    }
    
    public static void pop() {
        final NamespaceContextStack nsContextStack = getNamespaceContextStack();
        nsContextStack.pop();
        if (nsContextStack.stack.size() == 0) {
            NamespaceContext.tl_namespaceContextStack.set(null);
        }
    }
    
    public static PrefixResolver getCurrent() {
        return getNamespaceContextStack().current;
    }
    
    @Override
    public String getNamespaceForPrefix(final String prefix) {
        if (prefix != null && prefix.equals("xml")) {
            return "http://www.w3.org/XML/1998/namespace";
        }
        switch (this._code) {
            case 2: {
                Object obj = this._obj;
                if (Proxy.isProxyClass(obj.getClass())) {
                    obj = Proxy.getInvocationHandler(obj);
                }
                if (obj instanceof TypeStoreUser) {
                    return ((TypeStoreUser)obj).get_store().getNamespaceForPrefix(prefix);
                }
                final XmlCursor cur = ((XmlObject)this._obj).newCursor();
                if (cur != null) {
                    if (cur.currentTokenType() == XmlCursor.TokenType.ATTR) {
                        cur.toParent();
                    }
                    try {
                        return cur.namespaceForPrefix(prefix);
                    }
                    finally {
                        cur.dispose();
                    }
                    return ((Map)this._obj).get(prefix);
                }
                return ((Map)this._obj).get(prefix);
            }
            case 3: {
                return ((Map)this._obj).get(prefix);
            }
            case 1: {
                return ((TypeStore)this._obj).getNamespaceForPrefix(prefix);
            }
            case 4: {
                return ((StartElement)this._obj).getNamespaceUri(prefix);
            }
            case 5: {
                return ((PrefixResolver)this._obj).getNamespaceForPrefix(prefix);
            }
            default: {
                assert false : "Improperly initialized NamespaceContext.";
                return null;
            }
        }
    }
    
    static {
        NamespaceContext.tl_namespaceContextStack = new ThreadLocal();
    }
    
    private static final class NamespaceContextStack
    {
        NamespaceContext current;
        ArrayList stack;
        
        private NamespaceContextStack() {
            this.stack = new ArrayList();
        }
        
        final void push(final NamespaceContext next) {
            this.stack.add(this.current);
            this.current = next;
        }
        
        final void pop() {
            this.current = this.stack.get(this.stack.size() - 1);
            this.stack.remove(this.stack.size() - 1);
        }
    }
}
