package org.apache.xmlbeans.impl.store;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.XmlRuntimeException;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlDate;
import java.util.Date;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlDecimal;
import java.math.BigDecimal;
import org.apache.xmlbeans.XmlFloat;
import org.apache.xmlbeans.XmlLong;
import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.XmlInteger;
import org.apache.xmlbeans.SchemaType;
import java.util.List;
import org.apache.xmlbeans.XmlException;
import org.w3c.dom.Node;
import java.io.InputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.xmlbeans.impl.common.DefaultClassLoaderResourceLoader;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import org.apache.xmlbeans.impl.common.XPath;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import java.lang.reflect.Method;
import java.util.HashMap;

public abstract class Query
{
    public static final String QUERY_DELEGATE_INTERFACE = "QUERY_DELEGATE_INTERFACE";
    public static String _useDelegateForXQuery;
    public static String _useXdkForXQuery;
    private static String _delIntfName;
    private static HashMap _xdkQueryCache;
    private static Method _xdkCompileQuery;
    private static boolean _xdkAvailable;
    private static HashMap _xqrlQueryCache;
    private static Method _xqrlCompileQuery;
    private static boolean _xqrlAvailable;
    private static HashMap _xqrl2002QueryCache;
    private static Method _xqrl2002CompileQuery;
    private static boolean _xqrl2002Available;
    
    abstract XmlObject[] objectExecute(final Cur p0, final XmlOptions p1);
    
    abstract XmlCursor cursorExecute(final Cur p0, final XmlOptions p1);
    
    static XmlObject[] objectExecQuery(final Cur c, final String queryExpr, final XmlOptions options) {
        return getCompiledQuery(queryExpr, options).objectExecute(c, options);
    }
    
    static XmlCursor cursorExecQuery(final Cur c, final String queryExpr, final XmlOptions options) {
        return getCompiledQuery(queryExpr, options).cursorExecute(c, options);
    }
    
    public static synchronized Query getCompiledQuery(final String queryExpr, final XmlOptions options) {
        return getCompiledQuery(queryExpr, Path.getCurrentNodeVar(options), options);
    }
    
    static synchronized Query getCompiledQuery(final String queryExpr, final String currentVar, XmlOptions options) {
        assert queryExpr != null;
        options = XmlOptions.maskNull(options);
        if (options.hasOption(Path._forceXqrl2002ForXpathXQuery)) {
            Query query = Query._xqrl2002QueryCache.get(queryExpr);
            if (query != null) {
                return query;
            }
            query = getXqrl2002CompiledQuery(queryExpr, currentVar);
            if (query != null) {
                Query._xqrl2002QueryCache.put(queryExpr, query);
                return query;
            }
            throw new RuntimeException("No 2002 query engine found.");
        }
        else {
            final Map boundary = new HashMap();
            int boundaryVal = 0;
            try {
                XPath.compileXPath(queryExpr, currentVar, boundary);
            }
            catch (final XPath.XPathCompileException e) {}
            finally {
                boundaryVal = ((boundary.get("$xmlbeans!ns_boundary") == null) ? 0 : boundary.get("$xmlbeans!ns_boundary"));
            }
            if (options.hasOption(Query._useXdkForXQuery)) {
                Query query = Query._xdkQueryCache.get(queryExpr);
                if (query != null) {
                    return query;
                }
                query = createXdkCompiledQuery(queryExpr, currentVar);
                if (query != null) {
                    Query._xdkQueryCache.put(queryExpr, query);
                    return query;
                }
            }
            if (!options.hasOption(Query._useDelegateForXQuery)) {
                Query query = Query._xqrlQueryCache.get(queryExpr);
                if (query != null) {
                    return query;
                }
                query = createXqrlCompiledQuery(queryExpr, currentVar);
                if (query != null) {
                    Query._xqrlQueryCache.put(queryExpr, query);
                    return query;
                }
            }
            final String delIntfName = (String)(options.hasOption("QUERY_DELEGATE_INTERFACE") ? options.get("QUERY_DELEGATE_INTERFACE") : Query._delIntfName);
            Query query = DelegateQueryImpl.createDelegateCompiledQuery(delIntfName, queryExpr, currentVar, boundaryVal, options);
            if (query != null) {
                return query;
            }
            throw new RuntimeException("No query engine found");
        }
    }
    
    public static synchronized String compileQuery(final String queryExpr, final XmlOptions options) {
        getCompiledQuery(queryExpr, options);
        return queryExpr;
    }
    
    private static Query createXdkCompiledQuery(final String queryExpr, final String currentVar) {
        if (!Query._xdkAvailable) {
            return null;
        }
        if (Query._xdkCompileQuery == null) {
            try {
                final Class xdkImpl = Class.forName("org.apache.xmlbeans.impl.store.OXQXBXqrlImpl");
                Query._xdkCompileQuery = xdkImpl.getDeclaredMethod("compileQuery", String.class, String.class, Boolean.class);
            }
            catch (final ClassNotFoundException e) {
                Query._xdkAvailable = false;
                return null;
            }
            catch (final Exception e2) {
                Query._xdkAvailable = false;
                throw new RuntimeException(e2.getMessage(), e2);
            }
        }
        final Object[] args = { queryExpr, currentVar, new Boolean(true) };
        try {
            return (Query)Query._xdkCompileQuery.invoke(null, args);
        }
        catch (final InvocationTargetException e3) {
            final Throwable t = e3.getCause();
            throw new RuntimeException(t.getMessage(), t);
        }
        catch (final IllegalAccessException e4) {
            throw new RuntimeException(e4.getMessage(), e4);
        }
    }
    
    private static Query createXqrlCompiledQuery(final String queryExpr, final String currentVar) {
        if (!Query._xqrlAvailable) {
            return null;
        }
        if (Query._xqrlCompileQuery == null) {
            try {
                final Class xqrlImpl = Class.forName("org.apache.xmlbeans.impl.store.XqrlImpl");
                Query._xqrlCompileQuery = xqrlImpl.getDeclaredMethod("compileQuery", String.class, String.class, Boolean.class);
            }
            catch (final ClassNotFoundException e) {
                Query._xqrlAvailable = false;
                return null;
            }
            catch (final Exception e2) {
                Query._xqrlAvailable = false;
                throw new RuntimeException(e2.getMessage(), e2);
            }
        }
        final Object[] args = { queryExpr, currentVar, new Boolean(true) };
        try {
            return (Query)Query._xqrlCompileQuery.invoke(null, args);
        }
        catch (final InvocationTargetException e3) {
            final Throwable t = e3.getCause();
            throw new RuntimeException(t.getMessage(), t);
        }
        catch (final IllegalAccessException e4) {
            throw new RuntimeException(e4.getMessage(), e4);
        }
    }
    
    private static Query getXqrl2002CompiledQuery(final String queryExpr, final String currentVar) {
        if (Query._xqrl2002Available && Query._xqrl2002CompileQuery == null) {
            try {
                final Class xqrlImpl = Class.forName("org.apache.xmlbeans.impl.store.Xqrl2002Impl");
                Query._xqrl2002CompileQuery = xqrlImpl.getDeclaredMethod("compileQuery", String.class, String.class, Boolean.class);
            }
            catch (final ClassNotFoundException e) {
                Query._xqrl2002Available = false;
                return null;
            }
            catch (final Exception e2) {
                Query._xqrl2002Available = false;
                throw new RuntimeException(e2.getMessage(), e2);
            }
        }
        final Object[] args = { queryExpr, currentVar, new Boolean(true) };
        try {
            return (Query)Query._xqrl2002CompileQuery.invoke(null, args);
        }
        catch (final InvocationTargetException e3) {
            final Throwable t = e3.getCause();
            throw new RuntimeException(t.getMessage(), t);
        }
        catch (final IllegalAccessException e4) {
            throw new RuntimeException(e4.getMessage(), e4);
        }
    }
    
    static {
        Query._useDelegateForXQuery = "use delegate for xquery";
        Query._useXdkForXQuery = "use xdk for xquery";
        Query._xdkQueryCache = new HashMap();
        Query._xdkAvailable = true;
        Query._xqrlQueryCache = new HashMap();
        Query._xqrlAvailable = true;
        Query._xqrl2002QueryCache = new HashMap();
        Query._xqrl2002Available = true;
        final String id = "META-INF/services/org.apache.xmlbeans.impl.store.QueryDelegate.QueryInterface";
        final InputStream in = new DefaultClassLoaderResourceLoader().getResourceAsStream(id);
        try {
            final BufferedReader br = new BufferedReader(new InputStreamReader(in));
            Query._delIntfName = br.readLine().trim();
            br.close();
        }
        catch (final Exception e) {
            Query._delIntfName = null;
        }
    }
    
    private static final class DelegateQueryImpl extends Query
    {
        private QueryDelegate.QueryInterface _xqueryImpl;
        
        private DelegateQueryImpl(final QueryDelegate.QueryInterface xqueryImpl) {
            this._xqueryImpl = xqueryImpl;
        }
        
        static Query createDelegateCompiledQuery(final String delIntfName, final String queryExpr, final String currentVar, final int boundary, final XmlOptions xmlOptions) {
            assert !currentVar.startsWith(".") && !currentVar.startsWith("..");
            final QueryDelegate.QueryInterface impl = QueryDelegate.createInstance(delIntfName, queryExpr, currentVar, boundary, xmlOptions);
            if (impl == null) {
                return null;
            }
            return new DelegateQueryImpl(impl);
        }
        
        @Override
        XmlObject[] objectExecute(final Cur c, final XmlOptions options) {
            return new DelegateQueryEngine(this._xqueryImpl, c, options).objectExecute();
        }
        
        @Override
        XmlCursor cursorExecute(final Cur c, final XmlOptions options) {
            return new DelegateQueryEngine(this._xqueryImpl, c, options).cursorExecute();
        }
        
        private static class DelegateQueryEngine
        {
            private Cur _cur;
            private QueryDelegate.QueryInterface _engine;
            private long _version;
            private XmlOptions _options;
            
            public DelegateQueryEngine(final QueryDelegate.QueryInterface xqImpl, final Cur c, final XmlOptions opt) {
                this._engine = xqImpl;
                this._version = c._locale.version();
                this._cur = c.weakCur(this);
                this._options = opt;
            }
            
            public XmlObject[] objectExecute() {
                if (this._cur == null || this._version != this._cur._locale.version()) {}
                final Map bindings = (Map)XmlOptions.maskNull(this._options).get("XQUERY_VARIABLE_MAP");
                final List resultsList = this._engine.execQuery(this._cur.getDom(), bindings);
                assert resultsList.size() > -1;
                final XmlObject[] result = new XmlObject[resultsList.size()];
                for (int i = 0; i < resultsList.size(); ++i) {
                    final Locale l = Locale.getLocale(this._cur._locale._schemaTypeLoader, this._options);
                    l.enter();
                    final Object node = resultsList.get(i);
                    Cur res = null;
                    try {
                        if (!(node instanceof Node)) {
                            res = l.load("<xml-fragment/>").tempCur();
                            res.setValue(node.toString());
                            final SchemaType type = this.getType(node);
                            Locale.autoTypeDocument(res, type, null);
                            result[i] = res.getObject();
                        }
                        else {
                            res = this.loadNode(l, (Node)node);
                        }
                        result[i] = res.getObject();
                    }
                    catch (final XmlException e) {
                        throw new RuntimeException(e);
                    }
                    finally {
                        l.exit();
                    }
                    res.release();
                }
                this.release();
                this._engine = null;
                return result;
            }
            
            private SchemaType getType(final Object node) {
                SchemaType type;
                if (node instanceof Integer) {
                    type = XmlInteger.type;
                }
                else if (node instanceof Double) {
                    type = XmlDouble.type;
                }
                else if (node instanceof Long) {
                    type = XmlLong.type;
                }
                else if (node instanceof Float) {
                    type = XmlFloat.type;
                }
                else if (node instanceof BigDecimal) {
                    type = XmlDecimal.type;
                }
                else if (node instanceof Boolean) {
                    type = XmlBoolean.type;
                }
                else if (node instanceof String) {
                    type = XmlString.type;
                }
                else if (node instanceof Date) {
                    type = XmlDate.type;
                }
                else {
                    type = XmlAnySimpleType.type;
                }
                return type;
            }
            
            public XmlCursor cursorExecute() {
                if (this._cur == null || this._version != this._cur._locale.version()) {}
                final Map bindings = (Map)XmlOptions.maskNull(this._options).get("XQUERY_VARIABLE_MAP");
                final List resultsList = this._engine.execQuery(this._cur.getDom(), bindings);
                assert resultsList.size() > -1;
                this._engine = null;
                final Locale locale = Locale.getLocale(this._cur._locale._schemaTypeLoader, this._options);
                locale.enter();
                final Locale.LoadContext _context = new Cur.CurLoadContext(locale, this._options);
                Cursor resultCur = null;
                try {
                    for (int i = 0; i < resultsList.size(); ++i) {
                        this.loadNodeHelper(locale, resultsList.get(i), _context);
                    }
                    final Cur c = _context.finish();
                    Locale.associateSourceName(c, this._options);
                    Locale.autoTypeDocument(c, null, this._options);
                    resultCur = new Cursor(c);
                }
                catch (final Exception e) {}
                finally {
                    locale.exit();
                }
                this.release();
                return resultCur;
            }
            
            public void release() {
                if (this._cur != null) {
                    this._cur.release();
                    this._cur = null;
                }
            }
            
            private Cur loadNode(final Locale locale, final Node node) {
                final Locale.LoadContext context = new Cur.CurLoadContext(locale, this._options);
                try {
                    this.loadNodeHelper(locale, node, context);
                    final Cur c = context.finish();
                    Locale.associateSourceName(c, this._options);
                    Locale.autoTypeDocument(c, null, this._options);
                    return c;
                }
                catch (final Exception e) {
                    throw new XmlRuntimeException(e.getMessage(), e);
                }
            }
            
            private void loadNodeHelper(final Locale locale, final Node node, final Locale.LoadContext context) {
                if (node.getNodeType() == 2) {
                    final QName attName = new QName(node.getNamespaceURI(), node.getLocalName(), node.getPrefix());
                    context.attr(attName, node.getNodeValue());
                }
                else {
                    locale.loadNode(node, context);
                }
            }
        }
    }
}
