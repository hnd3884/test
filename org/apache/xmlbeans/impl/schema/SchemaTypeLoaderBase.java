package org.apache.xmlbeans.impl.schema;

import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.SchemaField;
import java.util.List;
import org.apache.xmlbeans.impl.common.QNameHelper;
import java.util.ArrayList;
import org.apache.xmlbeans.impl.validator.ValidatingXMLInputStream;
import org.w3c.dom.DOMImplementation;
import org.apache.xmlbeans.XmlSaxHandler;
import org.w3c.dom.Node;
import java.io.Reader;
import java.security.DigestInputStream;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import javax.xml.stream.XMLStreamReader;
import org.apache.xmlbeans.xml.stream.XMLStreamException;
import org.apache.xmlbeans.xml.stream.XMLInputStream;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.impl.store.Locale;
import org.apache.xmlbeans.XmlFactoryHook;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.SchemaGlobalAttribute;
import org.apache.xmlbeans.SchemaGlobalElement;
import org.apache.xmlbeans.SchemaAttributeGroup;
import org.apache.xmlbeans.SchemaModelGroup;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.XmlOptions;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.xmlbeans.SchemaTypeLoader;

public abstract class SchemaTypeLoaderBase implements SchemaTypeLoader
{
    private static final String USER_AGENT;
    private static final Method _pathCompiler;
    private static final Method _queryCompiler;
    
    private static Method getMethod(final String className, final String methodName, final Class[] args) {
        try {
            return Class.forName(className).getDeclaredMethod(methodName, (Class<?>[])args);
        }
        catch (final Exception e) {
            throw new IllegalStateException("Cannot find " + className + "." + methodName + ".  verify that xmlstore " + "(from xbean.jar) is on classpath");
        }
    }
    
    private static Object invokeMethod(final Method method, final Object[] args) {
        try {
            return method.invoke(method, args);
        }
        catch (final InvocationTargetException e) {
            final Throwable t = e.getCause();
            final IllegalStateException ise = new IllegalStateException(t.getMessage());
            ise.initCause(t);
            throw ise;
        }
        catch (final Exception e2) {
            final IllegalStateException ise2 = new IllegalStateException(e2.getMessage());
            ise2.initCause(e2);
            throw ise2;
        }
    }
    
    private static String doCompilePath(final String pathExpr, final XmlOptions options) {
        return (String)invokeMethod(SchemaTypeLoaderBase._pathCompiler, new Object[] { pathExpr, options });
    }
    
    private static String doCompileQuery(final String queryExpr, final XmlOptions options) {
        return (String)invokeMethod(SchemaTypeLoaderBase._queryCompiler, new Object[] { queryExpr, options });
    }
    
    @Override
    public SchemaType findType(final QName name) {
        final SchemaType.Ref ref = this.findTypeRef(name);
        if (ref == null) {
            return null;
        }
        final SchemaType result = ref.get();
        assert result != null;
        return result;
    }
    
    @Override
    public SchemaType findDocumentType(final QName name) {
        final SchemaType.Ref ref = this.findDocumentTypeRef(name);
        if (ref == null) {
            return null;
        }
        final SchemaType result = ref.get();
        assert result != null;
        return result;
    }
    
    @Override
    public SchemaType findAttributeType(final QName name) {
        final SchemaType.Ref ref = this.findAttributeTypeRef(name);
        if (ref == null) {
            return null;
        }
        final SchemaType result = ref.get();
        assert result != null;
        return result;
    }
    
    @Override
    public SchemaModelGroup findModelGroup(final QName name) {
        final SchemaModelGroup.Ref ref = this.findModelGroupRef(name);
        if (ref == null) {
            return null;
        }
        final SchemaModelGroup result = ref.get();
        assert result != null;
        return result;
    }
    
    @Override
    public SchemaAttributeGroup findAttributeGroup(final QName name) {
        final SchemaAttributeGroup.Ref ref = this.findAttributeGroupRef(name);
        if (ref == null) {
            return null;
        }
        final SchemaAttributeGroup result = ref.get();
        assert result != null;
        return result;
    }
    
    @Override
    public SchemaGlobalElement findElement(final QName name) {
        final SchemaGlobalElement.Ref ref = this.findElementRef(name);
        if (ref == null) {
            return null;
        }
        final SchemaGlobalElement result = ref.get();
        assert result != null;
        return result;
    }
    
    @Override
    public SchemaGlobalAttribute findAttribute(final QName name) {
        final SchemaGlobalAttribute.Ref ref = this.findAttributeRef(name);
        if (ref == null) {
            return null;
        }
        final SchemaGlobalAttribute result = ref.get();
        assert result != null;
        return result;
    }
    
    @Override
    public XmlObject newInstance(final SchemaType type, final XmlOptions options) {
        final XmlFactoryHook hook = XmlFactoryHook.ThreadContext.getHook();
        if (hook != null) {
            return hook.newInstance(this, type, options);
        }
        return Locale.newInstance(this, type, options);
    }
    
    @Override
    public XmlObject parse(final String xmlText, final SchemaType type, final XmlOptions options) throws XmlException {
        final XmlFactoryHook hook = XmlFactoryHook.ThreadContext.getHook();
        if (hook != null) {
            return hook.parse(this, xmlText, type, options);
        }
        return Locale.parseToXmlObject(this, xmlText, type, options);
    }
    
    @Override
    @Deprecated
    public XmlObject parse(final XMLInputStream xis, final SchemaType type, final XmlOptions options) throws XmlException, XMLStreamException {
        final XmlFactoryHook hook = XmlFactoryHook.ThreadContext.getHook();
        if (hook != null) {
            return hook.parse(this, xis, type, options);
        }
        return Locale.parseToXmlObject(this, xis, type, options);
    }
    
    @Override
    public XmlObject parse(final XMLStreamReader xsr, final SchemaType type, final XmlOptions options) throws XmlException {
        final XmlFactoryHook hook = XmlFactoryHook.ThreadContext.getHook();
        if (hook != null) {
            return hook.parse(this, xsr, type, options);
        }
        return Locale.parseToXmlObject(this, xsr, type, options);
    }
    
    @Override
    public XmlObject parse(final File file, final SchemaType type, XmlOptions options) throws XmlException, IOException {
        if (options == null) {
            options = new XmlOptions();
            options.put("DOCUMENT_SOURCE_NAME", file.toURI().normalize().toString());
        }
        else if (!options.hasOption("DOCUMENT_SOURCE_NAME")) {
            options = new XmlOptions(options);
            options.put("DOCUMENT_SOURCE_NAME", file.toURI().normalize().toString());
        }
        final InputStream fis = new FileInputStream(file);
        try {
            return this.parse(fis, type, options);
        }
        finally {
            fis.close();
        }
    }
    
    @Override
    public XmlObject parse(URL url, final SchemaType type, XmlOptions options) throws XmlException, IOException {
        if (options == null) {
            options = new XmlOptions();
            options.put("DOCUMENT_SOURCE_NAME", url.toString());
        }
        else if (!options.hasOption("DOCUMENT_SOURCE_NAME")) {
            options = new XmlOptions(options);
            options.put("DOCUMENT_SOURCE_NAME", url.toString());
        }
        URLConnection conn = null;
        InputStream stream = null;
        try {
            boolean redirected = false;
            int count = 0;
            do {
                conn = url.openConnection();
                conn.addRequestProperty("User-Agent", SchemaTypeLoaderBase.USER_AGENT);
                conn.addRequestProperty("Accept", "application/xml, text/xml, */*");
                if (conn instanceof HttpURLConnection) {
                    final HttpURLConnection httpcon = (HttpURLConnection)conn;
                    final int code = httpcon.getResponseCode();
                    redirected = (code == 301 || code == 302);
                    if (redirected && count > 5) {
                        redirected = false;
                    }
                    if (!redirected) {
                        continue;
                    }
                    final String newLocation = httpcon.getHeaderField("Location");
                    if (newLocation == null) {
                        redirected = false;
                    }
                    else {
                        url = new URL(newLocation);
                        ++count;
                    }
                }
            } while (redirected);
            stream = conn.getInputStream();
            return this.parse(stream, type, options);
        }
        finally {
            if (stream != null) {
                stream.close();
            }
        }
    }
    
    @Override
    public XmlObject parse(InputStream jiois, final SchemaType type, final XmlOptions options) throws XmlException, IOException {
        final XmlFactoryHook hook = XmlFactoryHook.ThreadContext.getHook();
        DigestInputStream digestStream = null;
        Label_0051: {
            if (options != null && options.hasOption("LOAD_MESSAGE_DIGEST")) {
                MessageDigest sha;
                try {
                    sha = MessageDigest.getInstance("SHA");
                }
                catch (final NoSuchAlgorithmException e) {
                    break Label_0051;
                }
                digestStream = (DigestInputStream)(jiois = new DigestInputStream(jiois, sha));
            }
        }
        if (hook != null) {
            return hook.parse(this, jiois, type, options);
        }
        final XmlObject result = Locale.parseToXmlObject(this, jiois, type, options);
        if (digestStream != null) {
            result.documentProperties().setMessageDigest(digestStream.getMessageDigest().digest());
        }
        return result;
    }
    
    @Override
    public XmlObject parse(final Reader jior, final SchemaType type, final XmlOptions options) throws XmlException, IOException {
        final XmlFactoryHook hook = XmlFactoryHook.ThreadContext.getHook();
        if (hook != null) {
            return hook.parse(this, jior, type, options);
        }
        return Locale.parseToXmlObject(this, jior, type, options);
    }
    
    @Override
    public XmlObject parse(final Node node, final SchemaType type, final XmlOptions options) throws XmlException {
        final XmlFactoryHook hook = XmlFactoryHook.ThreadContext.getHook();
        if (hook != null) {
            return hook.parse(this, node, type, options);
        }
        return Locale.parseToXmlObject(this, node, type, options);
    }
    
    @Override
    public XmlSaxHandler newXmlSaxHandler(final SchemaType type, final XmlOptions options) {
        final XmlFactoryHook hook = XmlFactoryHook.ThreadContext.getHook();
        if (hook != null) {
            return hook.newXmlSaxHandler(this, type, options);
        }
        return Locale.newSaxHandler(this, type, options);
    }
    
    @Override
    public DOMImplementation newDomImplementation(final XmlOptions options) {
        return Locale.newDomImplementation(this, options);
    }
    
    @Override
    @Deprecated
    public XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final SchemaType type, final XmlOptions options) throws XmlException, XMLStreamException {
        return new ValidatingXMLInputStream(xis, this, type, options);
    }
    
    public String compilePath(final String pathExpr) {
        return this.compilePath(pathExpr, null);
    }
    
    @Override
    public String compilePath(final String pathExpr, final XmlOptions options) {
        return doCompilePath(pathExpr, options);
    }
    
    public String compileQuery(final String queryExpr) {
        return this.compileQuery(queryExpr, null);
    }
    
    @Override
    public String compileQuery(final String queryExpr, final XmlOptions options) {
        return doCompileQuery(queryExpr, options);
    }
    
    @Override
    public SchemaType typeForSignature(final String signature) {
        int end = signature.indexOf(64);
        String uri;
        if (end < 0) {
            uri = "";
            end = signature.length();
        }
        else {
            uri = signature.substring(end + 1);
        }
        final List parts = new ArrayList();
        int next;
        for (int index = 0; index < end; index = next + 1) {
            final int nextc = signature.indexOf(58, index);
            final int nextd = signature.indexOf(124, index);
            next = ((nextc < 0) ? nextd : ((nextd < 0) ? nextc : ((nextc < nextd) ? nextc : nextd)));
            if (next < 0 || next > end) {
                next = end;
            }
            final String part = signature.substring(index, next);
            parts.add(part);
        }
        SchemaType curType = null;
    Label_0924:
        for (int i = parts.size() - 1; i >= 0; --i) {
            final String part2 = parts.get(i);
            if (part2.length() < 1) {
                throw new IllegalArgumentException();
            }
            final int offset = (part2.length() >= 2 && part2.charAt(1) == '=') ? 2 : 1;
            switch (part2.charAt(0)) {
                case 'T': {
                    if (curType != null) {
                        throw new IllegalArgumentException();
                    }
                    curType = this.findType(QNameHelper.forLNS(part2.substring(offset), uri));
                    if (curType == null) {
                        return null;
                    }
                    break;
                }
                case 'D': {
                    if (curType != null) {
                        throw new IllegalArgumentException();
                    }
                    curType = this.findDocumentType(QNameHelper.forLNS(part2.substring(offset), uri));
                    if (curType == null) {
                        return null;
                    }
                    break;
                }
                case 'C':
                case 'R': {
                    if (curType != null) {
                        throw new IllegalArgumentException();
                    }
                    curType = this.findAttributeType(QNameHelper.forLNS(part2.substring(offset), uri));
                    if (curType == null) {
                        return null;
                    }
                    break;
                }
                case 'E':
                case 'U': {
                    if (curType != null) {
                        if (curType.getContentType() < 3) {
                            return null;
                        }
                        final SchemaType[] subTypes = curType.getAnonymousTypes();
                        final String localName = part2.substring(offset);
                        for (int j = 0; j < subTypes.length; ++j) {
                            final SchemaField field = subTypes[j].getContainerField();
                            if (field != null && !field.isAttribute() && field.getName().getLocalPart().equals(localName)) {
                                curType = subTypes[j];
                                continue Label_0924;
                            }
                        }
                        return null;
                    }
                    else {
                        final SchemaGlobalElement elt = this.findElement(QNameHelper.forLNS(part2.substring(offset), uri));
                        if (elt == null) {
                            return null;
                        }
                        curType = elt.getType();
                        break;
                    }
                    break;
                }
                case 'A':
                case 'Q': {
                    if (curType != null) {
                        if (curType.isSimpleType()) {
                            return null;
                        }
                        final SchemaType[] subTypes = curType.getAnonymousTypes();
                        final String localName = part2.substring(offset);
                        for (int j = 0; j < subTypes.length; ++j) {
                            final SchemaField field = subTypes[j].getContainerField();
                            if (field != null && field.isAttribute() && field.getName().getLocalPart().equals(localName)) {
                                curType = subTypes[j];
                                continue Label_0924;
                            }
                        }
                        return null;
                    }
                    else {
                        final SchemaGlobalAttribute attr = this.findAttribute(QNameHelper.forLNS(part2.substring(offset), uri));
                        if (attr == null) {
                            return null;
                        }
                        curType = attr.getType();
                        break;
                    }
                    break;
                }
                case 'B': {
                    if (curType == null) {
                        throw new IllegalArgumentException();
                    }
                    if (curType.getSimpleVariety() != 1) {
                        return null;
                    }
                    final SchemaType[] subTypes = curType.getAnonymousTypes();
                    if (subTypes.length != 1) {
                        return null;
                    }
                    curType = subTypes[0];
                    break;
                }
                case 'I': {
                    if (curType == null) {
                        throw new IllegalArgumentException();
                    }
                    if (curType.getSimpleVariety() != 3) {
                        return null;
                    }
                    final SchemaType[] subTypes = curType.getAnonymousTypes();
                    if (subTypes.length != 1) {
                        return null;
                    }
                    curType = subTypes[0];
                    break;
                }
                case 'M': {
                    if (curType == null) {
                        throw new IllegalArgumentException();
                    }
                    int index2;
                    try {
                        index2 = Integer.parseInt(part2.substring(offset));
                    }
                    catch (final Exception e) {
                        throw new IllegalArgumentException();
                    }
                    if (curType.getSimpleVariety() != 2) {
                        return null;
                    }
                    final SchemaType[] subTypes2 = curType.getAnonymousTypes();
                    if (subTypes2.length <= index2) {
                        return null;
                    }
                    curType = subTypes2[index2];
                    break;
                }
                default: {
                    throw new IllegalArgumentException();
                }
            }
        }
        return curType;
    }
    
    static {
        USER_AGENT = "XMLBeans/" + XmlBeans.getVersion() + " (" + XmlBeans.getTitle() + ")";
        _pathCompiler = getMethod("org.apache.xmlbeans.impl.store.Path", "compilePath", new Class[] { String.class, XmlOptions.class });
        _queryCompiler = getMethod("org.apache.xmlbeans.impl.store.Query", "compileQuery", new Class[] { String.class, XmlOptions.class });
    }
}
