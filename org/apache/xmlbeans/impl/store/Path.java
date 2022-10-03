package org.apache.xmlbeans.impl.store;

import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlDate;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlDecimal;
import org.apache.xmlbeans.XmlFloat;
import org.apache.xmlbeans.XmlLong;
import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.XmlInteger;
import org.apache.xmlbeans.SchemaType;
import java.util.List;
import java.math.BigDecimal;
import java.util.Date;
import org.w3c.dom.Node;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.ConcurrentModificationException;
import java.io.InputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.xmlbeans.impl.common.DefaultClassLoaderResourceLoader;
import java.util.WeakHashMap;
import java.lang.reflect.InvocationTargetException;
import org.apache.xmlbeans.impl.common.XPath;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import org.apache.xmlbeans.XmlOptions;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.lang.reflect.Method;
import java.util.Map;

public abstract class Path
{
    public static final String PATH_DELEGATE_INTERFACE = "PATH_DELEGATE_INTERFACE";
    public static String _useDelegateForXpath;
    public static String _useXdkForXpath;
    public static String _useXqrlForXpath;
    public static String _useXbeanForXpath;
    public static String _forceXqrl2002ForXpathXQuery;
    private static final int USE_XBEAN = 1;
    private static final int USE_XQRL = 2;
    private static final int USE_DELEGATE = 4;
    private static final int USE_XQRL2002 = 8;
    private static final int USE_XDK = 16;
    private static Map _xbeanPathCache;
    private static Map _xdkPathCache;
    private static Map _xqrlPathCache;
    private static Map _xqrl2002PathCache;
    private static Method _xdkCompilePath;
    private static Method _xqrlCompilePath;
    private static Method _xqrl2002CompilePath;
    private static boolean _xdkAvailable;
    private static boolean _xqrlAvailable;
    private static boolean _xqrl2002Available;
    private static final String _delIntfName;
    private static final ReentrantReadWriteLock lock;
    protected final String _pathKey;
    
    Path(final String key) {
        this._pathKey = key;
    }
    
    abstract PathEngine execute(final Cur p0, final XmlOptions p1);
    
    static String getCurrentNodeVar(XmlOptions options) {
        String currentNodeVar = "this";
        options = XmlOptions.maskNull(options);
        if (options.hasOption("XQUERY_CURRENT_NODE_VAR")) {
            currentNodeVar = (String)options.get("XQUERY_CURRENT_NODE_VAR");
            if (currentNodeVar.startsWith("$")) {
                throw new IllegalArgumentException("Omit the '$' prefix for the current node variable");
            }
        }
        return currentNodeVar;
    }
    
    public static Path getCompiledPath(final String pathExpr, XmlOptions options) {
        options = XmlOptions.maskNull(options);
        final int force = options.hasOption(Path._useDelegateForXpath) ? 4 : (options.hasOption(Path._useXqrlForXpath) ? 2 : (options.hasOption(Path._useXdkForXpath) ? 16 : (options.hasOption(Path._useXbeanForXpath) ? 1 : (options.hasOption(Path._forceXqrl2002ForXpathXQuery) ? 8 : 23))));
        final String delIntfName = (String)(options.hasOption("PATH_DELEGATE_INTERFACE") ? options.get("PATH_DELEGATE_INTERFACE") : Path._delIntfName);
        return getCompiledPath(pathExpr, force, getCurrentNodeVar(options), delIntfName);
    }
    
    static Path getCompiledPath(final String pathExpr, final int force, final String currentVar, final String delIntfName) {
        Path path = null;
        WeakReference pathWeakRef = null;
        final Map namespaces = ((force & 0x4) != 0x0) ? new HashMap() : null;
        Path.lock.readLock().lock();
        try {
            if ((force & 0x1) != 0x0) {
                pathWeakRef = Path._xbeanPathCache.get(pathExpr);
            }
            if (pathWeakRef == null && (force & 0x2) != 0x0) {
                pathWeakRef = Path._xqrlPathCache.get(pathExpr);
            }
            if (pathWeakRef == null && (force & 0x10) != 0x0) {
                pathWeakRef = Path._xdkPathCache.get(pathExpr);
            }
            if (pathWeakRef == null && (force & 0x8) != 0x0) {
                pathWeakRef = Path._xqrl2002PathCache.get(pathExpr);
            }
            if (pathWeakRef != null) {
                path = (Path)pathWeakRef.get();
            }
            if (path != null) {
                return path;
            }
        }
        finally {
            Path.lock.readLock().unlock();
        }
        Path.lock.writeLock().lock();
        try {
            if ((force & 0x1) != 0x0) {
                pathWeakRef = Path._xbeanPathCache.get(pathExpr);
                if (pathWeakRef != null) {
                    path = (Path)pathWeakRef.get();
                }
                if (path == null) {
                    path = getCompiledPathXbean(pathExpr, currentVar, namespaces);
                }
            }
            if (path == null && (force & 0x2) != 0x0) {
                pathWeakRef = Path._xqrlPathCache.get(pathExpr);
                if (pathWeakRef != null) {
                    path = (Path)pathWeakRef.get();
                }
                if (path == null) {
                    path = getCompiledPathXqrl(pathExpr, currentVar);
                }
            }
            if (path == null && (force & 0x10) != 0x0) {
                pathWeakRef = Path._xdkPathCache.get(pathExpr);
                if (pathWeakRef != null) {
                    path = (Path)pathWeakRef.get();
                }
                if (path == null) {
                    path = getCompiledPathXdk(pathExpr, currentVar);
                }
            }
            if (path == null && (force & 0x4) != 0x0) {
                path = getCompiledPathDelegate(pathExpr, currentVar, namespaces, delIntfName);
            }
            if (path == null && (force & 0x8) != 0x0) {
                pathWeakRef = Path._xqrl2002PathCache.get(pathExpr);
                if (pathWeakRef != null) {
                    path = (Path)pathWeakRef.get();
                }
                if (path == null) {
                    path = getCompiledPathXqrl2002(pathExpr, currentVar);
                }
            }
            if (path == null) {
                final StringBuffer errMessage = new StringBuffer();
                if ((force & 0x1) != 0x0) {
                    errMessage.append(" Trying XBeans path engine...");
                }
                if ((force & 0x2) != 0x0) {
                    errMessage.append(" Trying XQRL...");
                }
                if ((force & 0x10) != 0x0) {
                    errMessage.append(" Trying XDK...");
                }
                if ((force & 0x4) != 0x0) {
                    errMessage.append(" Trying delegated path engine...");
                }
                if ((force & 0x8) != 0x0) {
                    errMessage.append(" Trying XQRL2002...");
                }
                throw new RuntimeException(errMessage.toString() + " FAILED on " + pathExpr);
            }
        }
        finally {
            Path.lock.writeLock().unlock();
        }
        return path;
    }
    
    private static Path getCompiledPathXdk(final String pathExpr, final String currentVar) {
        final Path path = createXdkCompiledPath(pathExpr, currentVar);
        if (path != null) {
            Path._xdkPathCache.put(path._pathKey, new WeakReference(path));
        }
        return path;
    }
    
    private static Path getCompiledPathXqrl(final String pathExpr, final String currentVar) {
        final Path path = createXqrlCompiledPath(pathExpr, currentVar);
        if (path != null) {
            Path._xqrlPathCache.put(path._pathKey, new WeakReference(path));
        }
        return path;
    }
    
    private static Path getCompiledPathXqrl2002(final String pathExpr, final String currentVar) {
        final Path path = createXqrl2002CompiledPath(pathExpr, currentVar);
        if (path != null) {
            Path._xqrl2002PathCache.put(path._pathKey, new WeakReference(path));
        }
        return path;
    }
    
    private static Path getCompiledPathXbean(final String pathExpr, final String currentVar, final Map namespaces) {
        final Path path = XbeanPath.create(pathExpr, currentVar, namespaces);
        if (path != null) {
            Path._xbeanPathCache.put(path._pathKey, new WeakReference(path));
        }
        return path;
    }
    
    private static Path getCompiledPathDelegate(final String pathExpr, final String currentVar, Map namespaces, final String delIntfName) {
        Path path = null;
        if (namespaces == null) {
            namespaces = new HashMap();
        }
        try {
            XPath.compileXPath(pathExpr, currentVar, namespaces);
        }
        catch (final XPath.XPathCompileException ex) {}
        final int offset = (int)((namespaces.get("$xmlbeans!ns_boundary") == null) ? 0 : namespaces.get("$xmlbeans!ns_boundary"));
        namespaces.remove("$xmlbeans!ns_boundary");
        path = DelegatePathImpl.create(delIntfName, pathExpr.substring(offset), currentVar, namespaces);
        return path;
    }
    
    public static String compilePath(final String pathExpr, final XmlOptions options) {
        return getCompiledPath(pathExpr, options)._pathKey;
    }
    
    private static Path createXdkCompiledPath(final String pathExpr, final String currentVar) {
        if (!Path._xdkAvailable) {
            return null;
        }
        if (Path._xdkCompilePath == null) {
            try {
                final Class xdkImpl = Class.forName("org.apache.xmlbeans.impl.store.OXQXBXqrlImpl");
                Path._xdkCompilePath = xdkImpl.getDeclaredMethod("compilePath", String.class, String.class, Boolean.class);
            }
            catch (final ClassNotFoundException e) {
                Path._xdkAvailable = false;
                return null;
            }
            catch (final Exception e2) {
                Path._xdkAvailable = false;
                throw new RuntimeException(e2.getMessage(), e2);
            }
        }
        final Object[] args = { pathExpr, currentVar, new Boolean(true) };
        try {
            return (Path)Path._xdkCompilePath.invoke(null, args);
        }
        catch (final InvocationTargetException e3) {
            final Throwable t = e3.getCause();
            throw new RuntimeException(t.getMessage(), t);
        }
        catch (final IllegalAccessException e4) {
            throw new RuntimeException(e4.getMessage(), e4);
        }
    }
    
    private static Path createXqrlCompiledPath(final String pathExpr, final String currentVar) {
        if (!Path._xqrlAvailable) {
            return null;
        }
        if (Path._xqrlCompilePath == null) {
            try {
                final Class xqrlImpl = Class.forName("org.apache.xmlbeans.impl.store.XqrlImpl");
                Path._xqrlCompilePath = xqrlImpl.getDeclaredMethod("compilePath", String.class, String.class, Boolean.class);
            }
            catch (final ClassNotFoundException e) {
                Path._xqrlAvailable = false;
                return null;
            }
            catch (final Exception e2) {
                Path._xqrlAvailable = false;
                throw new RuntimeException(e2.getMessage(), e2);
            }
        }
        final Object[] args = { pathExpr, currentVar, new Boolean(true) };
        try {
            return (Path)Path._xqrlCompilePath.invoke(null, args);
        }
        catch (final InvocationTargetException e3) {
            final Throwable t = e3.getCause();
            throw new RuntimeException(t.getMessage(), t);
        }
        catch (final IllegalAccessException e4) {
            throw new RuntimeException(e4.getMessage(), e4);
        }
    }
    
    private static Path createXqrl2002CompiledPath(final String pathExpr, final String currentVar) {
        if (!Path._xqrl2002Available) {
            return null;
        }
        if (Path._xqrl2002CompilePath == null) {
            try {
                final Class xqrlImpl = Class.forName("org.apache.xmlbeans.impl.store.Xqrl2002Impl");
                Path._xqrl2002CompilePath = xqrlImpl.getDeclaredMethod("compilePath", String.class, String.class, Boolean.class);
            }
            catch (final ClassNotFoundException e) {
                Path._xqrl2002Available = false;
                return null;
            }
            catch (final Exception e2) {
                Path._xqrl2002Available = false;
                throw new RuntimeException(e2.getMessage(), e2);
            }
        }
        final Object[] args = { pathExpr, currentVar, new Boolean(true) };
        try {
            return (Path)Path._xqrl2002CompilePath.invoke(null, args);
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
        Path._useDelegateForXpath = "use delegate for xpath";
        Path._useXdkForXpath = "use xdk for xpath";
        Path._useXqrlForXpath = "use xqrl for xpath";
        Path._useXbeanForXpath = "use xbean for xpath";
        Path._forceXqrl2002ForXpathXQuery = "use xqrl-2002 for xpath";
        Path._xbeanPathCache = new WeakHashMap();
        Path._xdkPathCache = new WeakHashMap();
        Path._xqrlPathCache = new WeakHashMap();
        Path._xqrl2002PathCache = new WeakHashMap();
        Path._xdkAvailable = true;
        Path._xqrlAvailable = true;
        Path._xqrl2002Available = true;
        lock = new ReentrantReadWriteLock();
        final String id = "META-INF/services/org.apache.xmlbeans.impl.store.PathDelegate.SelectPathInterface";
        final InputStream in = new DefaultClassLoaderResourceLoader().getResourceAsStream(id);
        String name = null;
        if (in != null) {
            try {
                final BufferedReader br = new BufferedReader(new InputStreamReader(in));
                name = br.readLine().trim();
                br.close();
            }
            catch (final Exception ex) {}
        }
        _delIntfName = name;
    }
    
    private static final class XbeanPath extends Path
    {
        private final String _currentVar;
        private final XPath _compiledPath;
        public Map namespaces;
        
        static Path create(final String pathExpr, final String currentVar, final Map namespaces) {
            try {
                return new XbeanPath(pathExpr, currentVar, XPath.compileXPath(pathExpr, currentVar, namespaces));
            }
            catch (final XPath.XPathCompileException e) {
                return null;
            }
        }
        
        private XbeanPath(final String pathExpr, final String currentVar, final XPath xpath) {
            super(pathExpr);
            this._currentVar = currentVar;
            this._compiledPath = xpath;
        }
        
        @Override
        PathEngine execute(final Cur c, XmlOptions options) {
            options = XmlOptions.maskNull(options);
            final String delIntfName = (String)(options.hasOption("PATH_DELEGATE_INTERFACE") ? options.get("PATH_DELEGATE_INTERFACE") : Path._delIntfName);
            if (!c.isContainer() || this._compiledPath.sawDeepDot()) {
                final int force = 22;
                return Path.getCompiledPath(this._pathKey, force, this._currentVar, delIntfName).execute(c, options);
            }
            return new XbeanPathEngine(this._compiledPath, c);
        }
    }
    
    private static final class XbeanPathEngine extends XPath.ExecutionContext implements PathEngine
    {
        private final long _version;
        private Cur _cur;
        
        XbeanPathEngine(final XPath xpath, final Cur c) {
            assert c.isContainer();
            this._version = c._locale.version();
            (this._cur = c.weakCur(this)).push();
            this.init(xpath);
            final int ret = this.start();
            if ((ret & 0x1) != 0x0) {
                c.addToSelection();
            }
            this.doAttrs(ret, c);
            if ((ret & 0x2) == 0x0 || !Locale.toFirstChildElement(this._cur)) {
                this.release();
            }
        }
        
        private void advance(final Cur c) {
            assert this._cur != null;
            if (this._cur.isFinish()) {
                if (this._cur.isAtEndOfLastPush()) {
                    this.release();
                }
                else {
                    this.end();
                    this._cur.next();
                }
            }
            else if (this._cur.isElem()) {
                final int ret = this.element(this._cur.getName());
                if ((ret & 0x1) != 0x0) {
                    c.addToSelection(this._cur);
                }
                this.doAttrs(ret, c);
                if ((ret & 0x2) == 0x0 || !Locale.toFirstChildElement(this._cur)) {
                    this.end();
                    this._cur.skip();
                }
            }
            else {
                do {
                    this._cur.next();
                } while (!this._cur.isContainerOrFinish());
            }
        }
        
        private void doAttrs(final int ret, final Cur c) {
            assert this._cur.isContainer();
            if ((ret & 0x4) != 0x0 && this._cur.toFirstAttr()) {
                do {
                    if (this.attr(this._cur.getName())) {
                        c.addToSelection(this._cur);
                    }
                } while (this._cur.toNextAttr());
                this._cur.toParent();
            }
        }
        
        @Override
        public boolean next(final Cur c) {
            if (this._cur != null && this._version != this._cur._locale.version()) {
                throw new ConcurrentModificationException("Document changed during select");
            }
            final int startCount = c.selectionCount();
            while (this._cur != null) {
                this.advance(c);
                if (startCount != c.selectionCount()) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public void release() {
            if (this._cur != null) {
                this._cur.release();
                this._cur = null;
            }
        }
    }
    
    private static final class DelegatePathImpl extends Path
    {
        private PathDelegate.SelectPathInterface _xpathImpl;
        
        static Path create(final String implClassName, final String pathExpr, final String currentNodeVar, final Map namespaceMap) {
            assert !currentNodeVar.startsWith("$");
            final PathDelegate.SelectPathInterface impl = PathDelegate.createInstance(implClassName, pathExpr, currentNodeVar, namespaceMap);
            if (impl == null) {
                return null;
            }
            return new DelegatePathImpl(impl, pathExpr);
        }
        
        private DelegatePathImpl(final PathDelegate.SelectPathInterface xpathImpl, final String pathExpr) {
            super(pathExpr);
            this._xpathImpl = xpathImpl;
        }
        
        protected PathEngine execute(final Cur c, final XmlOptions options) {
            return new DelegatePathEngine(this._xpathImpl, c);
        }
        
        private static class DelegatePathEngine extends XPath.ExecutionContext implements PathEngine
        {
            private final DateFormat xmlDateFormat;
            private Cur _cur;
            private PathDelegate.SelectPathInterface _engine;
            private boolean _firstCall;
            private long _version;
            
            DelegatePathEngine(final PathDelegate.SelectPathInterface xpathImpl, final Cur c) {
                this.xmlDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                this._firstCall = true;
                this._engine = xpathImpl;
                this._version = c._locale.version();
                this._cur = c.weakCur(this);
            }
            
            @Override
            public boolean next(final Cur c) {
                if (!this._firstCall) {
                    return false;
                }
                this._firstCall = false;
                if (this._cur != null && this._version != this._cur._locale.version()) {
                    throw new ConcurrentModificationException("Document changed during select");
                }
                final Object context_node = this._cur.getDom();
                final List resultsList = this._engine.selectPath(context_node);
                for (int i = 0; i < resultsList.size(); ++i) {
                    final Object node = resultsList.get(i);
                    Cur pos = null;
                    if (!(node instanceof Node)) {
                        final Object obj = resultsList.get(i);
                        String value;
                        if (obj instanceof Date) {
                            value = this.xmlDateFormat.format((Date)obj);
                        }
                        else if (obj instanceof BigDecimal) {
                            value = ((BigDecimal)obj).toPlainString();
                        }
                        else {
                            value = obj.toString();
                        }
                        final Locale l = c._locale;
                        try {
                            pos = l.load("<xml-fragment/>").tempCur();
                            pos.setValue(value);
                            final SchemaType type = this.getType(node);
                            Locale.autoTypeDocument(pos, type, null);
                            pos.next();
                        }
                        catch (final Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else {
                        assert node instanceof DomImpl.Dom : "New object created in XPATH!";
                        pos = ((DomImpl.Dom)node).tempCur();
                    }
                    c.addToSelection(pos);
                    pos.release();
                }
                this.release();
                this._engine = null;
                return true;
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
            
            @Override
            public void release() {
                if (this._cur != null) {
                    this._cur.release();
                    this._cur = null;
                }
            }
        }
    }
    
    interface PathEngine
    {
        void release();
        
        boolean next(final Cur p0);
    }
}
