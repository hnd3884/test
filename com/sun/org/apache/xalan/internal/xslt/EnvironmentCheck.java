package com.sun.org.apache.xalan.internal.xslt;

import java.util.Collections;
import org.xml.sax.Attributes;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import java.util.StringTokenizer;
import java.io.File;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.Writer;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;

public class EnvironmentCheck
{
    public static final String ERROR = "ERROR.";
    public static final String WARNING = "WARNING.";
    public static final String ERROR_FOUND = "At least one error was found!";
    public static final String VERSION = "version.";
    public static final String FOUNDCLASSES = "foundclasses.";
    public static final String CLASS_PRESENT = "present-unknown-version";
    public static final String CLASS_NOTPRESENT = "not-present";
    public String[] jarNames;
    private static final Map<Long, String> JARVERSIONS;
    protected PrintWriter outWriter;
    
    public EnvironmentCheck() {
        this.jarNames = new String[] { "xalan.jar", "xalansamples.jar", "xalanj1compat.jar", "xalanservlet.jar", "serializer.jar", "xerces.jar", "xercesImpl.jar", "testxsl.jar", "crimson.jar", "lotusxsl.jar", "jaxp.jar", "parser.jar", "dom.jar", "sax.jar", "xml.jar", "xml-apis.jar", "xsltc.jar" };
        this.outWriter = new PrintWriter(System.out, true);
    }
    
    public static void main(final String[] args) {
        PrintWriter sendOutputTo = new PrintWriter(System.out, true);
        for (int i = 0; i < args.length; ++i) {
            if ("-out".equalsIgnoreCase(args[i])) {
                if (++i < args.length) {
                    try {
                        sendOutputTo = new PrintWriter(new FileWriter(args[i], true));
                    }
                    catch (final Exception e) {
                        System.err.println("# WARNING: -out " + args[i] + " threw " + e.toString());
                    }
                }
                else {
                    System.err.println("# WARNING: -out argument should have a filename, output sent to console");
                }
            }
        }
        final EnvironmentCheck app = new EnvironmentCheck();
        app.checkEnvironment(sendOutputTo);
    }
    
    public boolean checkEnvironment(final PrintWriter pw) {
        if (null != pw) {
            this.outWriter = pw;
        }
        final Map<String, Object> hash = this.getEnvironmentHash();
        final boolean environmentHasErrors = this.writeEnvironmentReport(hash);
        if (environmentHasErrors) {
            this.logMsg("# WARNING: Potential problems found in your environment!");
            this.logMsg("#    Check any 'ERROR' items above against the Xalan FAQs");
            this.logMsg("#    to correct potential problems with your classes/jars");
            this.logMsg("#    http://xml.apache.org/xalan-j/faq.html");
            if (null != this.outWriter) {
                this.outWriter.flush();
            }
            return false;
        }
        this.logMsg("# YAHOO! Your environment seems to be OK.");
        if (null != this.outWriter) {
            this.outWriter.flush();
        }
        return true;
    }
    
    public Map<String, Object> getEnvironmentHash() {
        final Map<String, Object> hash = new HashMap<String, Object>();
        this.checkJAXPVersion(hash);
        this.checkProcessorVersion(hash);
        this.checkParserVersion(hash);
        this.checkAntVersion(hash);
        if (!this.checkDOML3(hash)) {
            this.checkDOMVersion(hash);
        }
        this.checkSAXVersion(hash);
        this.checkSystemProperties(hash);
        return hash;
    }
    
    protected boolean writeEnvironmentReport(final Map<String, Object> h) {
        if (null == h) {
            this.logMsg("# ERROR: writeEnvironmentReport called with null Map");
            return false;
        }
        boolean errors = false;
        this.logMsg("#---- BEGIN writeEnvironmentReport($Revision: 1.10 $): Useful stuff found: ----");
        for (final Map.Entry<String, Object> entry : h.entrySet()) {
            final String keyStr = entry.getKey();
            try {
                if (keyStr.startsWith("foundclasses.")) {
                    final List<Map> v = entry.getValue();
                    errors |= this.logFoundJars(v, keyStr);
                }
                else {
                    if (keyStr.startsWith("ERROR.")) {
                        errors = true;
                    }
                    this.logMsg(keyStr + "=" + h.get(keyStr));
                }
            }
            catch (final Exception e) {
                this.logMsg("Reading-" + keyStr + "= threw: " + e.toString());
            }
        }
        this.logMsg("#----- END writeEnvironmentReport: Useful properties found: -----");
        return errors;
    }
    
    protected boolean logFoundJars(final List<Map> v, final String desc) {
        if (null == v || v.size() < 1) {
            return false;
        }
        boolean errors = false;
        this.logMsg("#---- BEGIN Listing XML-related jars in: " + desc + " ----");
        for (final Map<String, String> v2 : v) {
            for (final Map.Entry<String, String> entry : v2.entrySet()) {
                final String keyStr = entry.getKey();
                try {
                    if (keyStr.startsWith("ERROR.")) {
                        errors = true;
                    }
                    this.logMsg(keyStr + "=" + entry.getValue());
                }
                catch (final Exception e) {
                    errors = true;
                    this.logMsg("Reading-" + keyStr + "= threw: " + e.toString());
                }
            }
        }
        this.logMsg("#----- END Listing XML-related jars in: " + desc + " -----");
        return errors;
    }
    
    public void appendEnvironmentReport(final Node container, final Document factory, final Map<String, Object> h) {
        if (null == container || null == factory) {
            return;
        }
        try {
            final Element envCheckNode = factory.createElement("EnvironmentCheck");
            envCheckNode.setAttribute("version", "$Revision: 1.10 $");
            container.appendChild(envCheckNode);
            if (null == h) {
                final Element statusNode = factory.createElement("status");
                statusNode.setAttribute("result", "ERROR");
                statusNode.appendChild(factory.createTextNode("appendEnvironmentReport called with null Map!"));
                envCheckNode.appendChild(statusNode);
                return;
            }
            boolean errors = false;
            final Element hashNode = factory.createElement("environment");
            envCheckNode.appendChild(hashNode);
            for (final Map.Entry<String, Object> entry : h.entrySet()) {
                final String keyStr = entry.getKey();
                try {
                    if (keyStr.startsWith("foundclasses.")) {
                        final List<Map> v = entry.getValue();
                        errors |= this.appendFoundJars(hashNode, factory, v, keyStr);
                    }
                    else {
                        if (keyStr.startsWith("ERROR.")) {
                            errors = true;
                        }
                        final Element node = factory.createElement("item");
                        node.setAttribute("key", keyStr);
                        node.appendChild(factory.createTextNode(h.get(keyStr)));
                        hashNode.appendChild(node);
                    }
                }
                catch (final Exception e) {
                    errors = true;
                    final Element node2 = factory.createElement("item");
                    node2.setAttribute("key", keyStr);
                    node2.appendChild(factory.createTextNode("ERROR. Reading " + keyStr + " threw: " + e.toString()));
                    hashNode.appendChild(node2);
                }
            }
            final Element statusNode2 = factory.createElement("status");
            statusNode2.setAttribute("result", errors ? "ERROR" : "OK");
            envCheckNode.appendChild(statusNode2);
        }
        catch (final Exception e2) {
            System.err.println("appendEnvironmentReport threw: " + e2.toString());
            e2.printStackTrace();
        }
    }
    
    protected boolean appendFoundJars(final Node container, final Document factory, final List<Map> v, final String desc) {
        if (null == v || v.size() < 1) {
            return false;
        }
        boolean errors = false;
        for (final Map<String, String> v2 : v) {
            for (final Map.Entry<String, String> entry : v2.entrySet()) {
                final String keyStr = entry.getKey();
                try {
                    if (keyStr.startsWith("ERROR.")) {
                        errors = true;
                    }
                    final Element node = factory.createElement("foundJar");
                    node.setAttribute("name", keyStr.substring(0, keyStr.indexOf("-")));
                    node.setAttribute("desc", keyStr.substring(keyStr.indexOf("-") + 1));
                    node.appendChild(factory.createTextNode(entry.getValue()));
                    container.appendChild(node);
                }
                catch (final Exception e) {
                    errors = true;
                    final Element node2 = factory.createElement("foundJar");
                    node2.appendChild(factory.createTextNode("ERROR. Reading " + keyStr + " threw: " + e.toString()));
                    container.appendChild(node2);
                }
            }
        }
        return errors;
    }
    
    protected void checkSystemProperties(Map<String, Object> h) {
        if (null == h) {
            h = new HashMap<String, Object>();
        }
        try {
            final String javaVersion = SecuritySupport.getSystemProperty("java.version");
            h.put("java.version", javaVersion);
        }
        catch (final SecurityException se) {
            h.put("java.version", "WARNING: SecurityException thrown accessing system version properties");
        }
        try {
            final String cp = SecuritySupport.getSystemProperty("java.class.path");
            h.put("java.class.path", cp);
            List<Map> classpathJars = this.checkPathForJars(cp, this.jarNames);
            if (null != classpathJars) {
                h.put("foundclasses.java.class.path", classpathJars);
            }
            String othercp = SecuritySupport.getSystemProperty("sun.boot.class.path");
            if (null != othercp) {
                h.put("sun.boot.class.path", othercp);
                classpathJars = this.checkPathForJars(othercp, this.jarNames);
                if (null != classpathJars) {
                    h.put("foundclasses.sun.boot.class.path", classpathJars);
                }
            }
            othercp = SecuritySupport.getSystemProperty("java.ext.dirs");
            if (null != othercp) {
                h.put("java.ext.dirs", othercp);
                classpathJars = this.checkPathForJars(othercp, this.jarNames);
                if (null != classpathJars) {
                    h.put("foundclasses.java.ext.dirs", classpathJars);
                }
            }
        }
        catch (final SecurityException se2) {
            h.put("java.class.path", "WARNING: SecurityException thrown accessing system classpath properties");
        }
    }
    
    protected List<Map> checkPathForJars(final String cp, final String[] jars) {
        if (null == cp || null == jars || 0 == cp.length() || 0 == jars.length) {
            return null;
        }
        final List<Map> v = new ArrayList<Map>();
        final StringTokenizer st = new StringTokenizer(cp, File.pathSeparator);
        while (st.hasMoreTokens()) {
            final String filename = st.nextToken();
            for (int i = 0; i < jars.length; ++i) {
                if (filename.indexOf(jars[i]) > -1) {
                    final File f = new File(filename);
                    if (f.exists()) {
                        try {
                            final Map<String, String> h = new HashMap<String, String>(2);
                            h.put(jars[i] + "-path", f.getAbsolutePath());
                            if (!"xalan.jar".equalsIgnoreCase(jars[i])) {
                                h.put(jars[i] + "-apparent.version", this.getApparentVersion(jars[i], f.length()));
                            }
                            v.add(h);
                        }
                        catch (final Exception ex) {}
                    }
                    else {
                        final Map<String, String> h = new HashMap<String, String>(2);
                        h.put(jars[i] + "-path", "WARNING. Classpath entry: " + filename + " does not exist");
                        h.put(jars[i] + "-apparent.version", "not-present");
                        v.add(h);
                    }
                }
            }
        }
        return v;
    }
    
    protected String getApparentVersion(final String jarName, final long jarSize) {
        final String foundSize = EnvironmentCheck.JARVERSIONS.get(new Long(jarSize));
        if (null != foundSize && foundSize.startsWith(jarName)) {
            return foundSize;
        }
        if ("xerces.jar".equalsIgnoreCase(jarName) || "xercesImpl.jar".equalsIgnoreCase(jarName)) {
            return jarName + " " + "WARNING." + "present-unknown-version";
        }
        return jarName + " " + "present-unknown-version";
    }
    
    protected void checkJAXPVersion(Map<String, Object> h) {
        if (null == h) {
            h = new HashMap<String, Object>();
        }
        Class clazz = null;
        try {
            final String JAXP1_CLASS = "javax.xml.stream.XMLStreamConstants";
            clazz = ObjectFactory.findProviderClass("javax.xml.stream.XMLStreamConstants", true);
            h.put("version.JAXP", "1.4");
        }
        catch (final Exception e) {
            h.put("ERROR.version.JAXP", "1.3");
            h.put("ERROR.", "At least one error was found!");
        }
    }
    
    protected void checkProcessorVersion(Map<String, Object> h) {
        if (null == h) {
            h = new HashMap<String, Object>();
        }
        try {
            final String XALAN1_VERSION_CLASS = "com.sun.org.apache.xalan.internal.xslt.XSLProcessorVersion";
            final Class clazz = ObjectFactory.findProviderClass("com.sun.org.apache.xalan.internal.xslt.XSLProcessorVersion", true);
            final StringBuffer buf = new StringBuffer();
            Field f = clazz.getField("PRODUCT");
            buf.append(f.get(null));
            buf.append(';');
            f = clazz.getField("LANGUAGE");
            buf.append(f.get(null));
            buf.append(';');
            f = clazz.getField("S_VERSION");
            buf.append(f.get(null));
            buf.append(';');
            h.put("version.xalan1", buf.toString());
        }
        catch (final Exception e1) {
            h.put("version.xalan1", "not-present");
        }
        try {
            final String XALAN2_VERSION_CLASS = "com.sun.org.apache.xalan.internal.processor.XSLProcessorVersion";
            final Class clazz = ObjectFactory.findProviderClass("com.sun.org.apache.xalan.internal.processor.XSLProcessorVersion", true);
            final StringBuffer buf = new StringBuffer();
            final Field f = clazz.getField("S_VERSION");
            buf.append(f.get(null));
            h.put("version.xalan2x", buf.toString());
        }
        catch (final Exception e2) {
            h.put("version.xalan2x", "not-present");
        }
        try {
            final String XALAN2_2_VERSION_CLASS = "com.sun.org.apache.xalan.internal.Version";
            final String XALAN2_2_VERSION_METHOD = "getVersion";
            final Class[] noArgs = new Class[0];
            final Class clazz2 = ObjectFactory.findProviderClass("com.sun.org.apache.xalan.internal.Version", true);
            final Method method = clazz2.getMethod("getVersion", (Class[])noArgs);
            final Object returnValue = method.invoke(null, new Object[0]);
            h.put("version.xalan2_2", returnValue);
        }
        catch (final Exception e2) {
            h.put("version.xalan2_2", "not-present");
        }
    }
    
    protected void checkParserVersion(Map<String, Object> h) {
        if (null == h) {
            h = new HashMap<String, Object>();
        }
        try {
            final String XERCES1_VERSION_CLASS = "com.sun.org.apache.xerces.internal.framework.Version";
            final Class clazz = ObjectFactory.findProviderClass("com.sun.org.apache.xerces.internal.framework.Version", true);
            final Field f = clazz.getField("fVersion");
            final String parserVersion = (String)f.get(null);
            h.put("version.xerces1", parserVersion);
        }
        catch (final Exception e) {
            h.put("version.xerces1", "not-present");
        }
        try {
            final String XERCES2_VERSION_CLASS = "com.sun.org.apache.xerces.internal.impl.Version";
            final Class clazz = ObjectFactory.findProviderClass("com.sun.org.apache.xerces.internal.impl.Version", true);
            final Field f = clazz.getField("fVersion");
            final String parserVersion = (String)f.get(null);
            h.put("version.xerces2", parserVersion);
        }
        catch (final Exception e) {
            h.put("version.xerces2", "not-present");
        }
        try {
            final String CRIMSON_CLASS = "org.apache.crimson.parser.Parser2";
            final Class clazz = ObjectFactory.findProviderClass("org.apache.crimson.parser.Parser2", true);
            h.put("version.crimson", "present-unknown-version");
        }
        catch (final Exception e) {
            h.put("version.crimson", "not-present");
        }
    }
    
    protected void checkAntVersion(Map<String, Object> h) {
        if (null == h) {
            h = new HashMap<String, Object>();
        }
        try {
            final String ANT_VERSION_CLASS = "org.apache.tools.ant.Main";
            final String ANT_VERSION_METHOD = "getAntVersion";
            final Class[] noArgs = new Class[0];
            final Class clazz = ObjectFactory.findProviderClass("org.apache.tools.ant.Main", true);
            final Method method = clazz.getMethod("getAntVersion", (Class[])noArgs);
            final Object returnValue = method.invoke(null, new Object[0]);
            h.put("version.ant", returnValue);
        }
        catch (final Exception e) {
            h.put("version.ant", "not-present");
        }
    }
    
    protected boolean checkDOML3(Map<String, Object> h) {
        if (null == h) {
            h = new HashMap<String, Object>();
        }
        final String DOM_CLASS = "org.w3c.dom.Document";
        final String DOM_LEVEL3_METHOD = "getDoctype";
        try {
            final Class clazz = ObjectFactory.findProviderClass("org.w3c.dom.Document", true);
            final Method method = clazz.getMethod("getDoctype", (Class[])null);
            h.put("version.DOM", "3.0");
            return true;
        }
        catch (final Exception e) {
            return false;
        }
    }
    
    protected void checkDOMVersion(Map<String, Object> h) {
        if (null == h) {
            h = new HashMap<String, Object>();
        }
        final String DOM_LEVEL2_CLASS = "org.w3c.dom.Document";
        final String DOM_LEVEL2_METHOD = "createElementNS";
        final String DOM_LEVEL3_METHOD = "getDoctype";
        final String DOM_LEVEL2WD_CLASS = "org.w3c.dom.Node";
        final String DOM_LEVEL2WD_METHOD = "supported";
        final String DOM_LEVEL2FD_CLASS = "org.w3c.dom.Node";
        final String DOM_LEVEL2FD_METHOD = "isSupported";
        final Class[] twoStringArgs = { String.class, String.class };
        try {
            Class clazz = ObjectFactory.findProviderClass("org.w3c.dom.Document", true);
            Method method = clazz.getMethod("createElementNS", (Class[])twoStringArgs);
            h.put("version.DOM", "2.0");
            try {
                clazz = ObjectFactory.findProviderClass("org.w3c.dom.Node", true);
                method = clazz.getMethod("supported", (Class[])twoStringArgs);
                h.put("ERROR.version.DOM.draftlevel", "2.0wd");
                h.put("ERROR.", "At least one error was found!");
            }
            catch (final Exception e2) {
                try {
                    clazz = ObjectFactory.findProviderClass("org.w3c.dom.Node", true);
                    method = clazz.getMethod("isSupported", (Class[])twoStringArgs);
                    h.put("version.DOM.draftlevel", "2.0fd");
                }
                catch (final Exception e3) {
                    h.put("ERROR.version.DOM.draftlevel", "2.0unknown");
                    h.put("ERROR.", "At least one error was found!");
                }
            }
        }
        catch (final Exception e4) {
            h.put("ERROR.version.DOM", "ERROR attempting to load DOM level 2 class: " + e4.toString());
            h.put("ERROR.", "At least one error was found!");
        }
    }
    
    protected void checkSAXVersion(Map<String, Object> h) {
        if (null == h) {
            h = new HashMap<String, Object>();
        }
        final String SAX_VERSION1_CLASS = "org.xml.sax.Parser";
        final String SAX_VERSION1_METHOD = "parse";
        final String SAX_VERSION2_CLASS = "org.xml.sax.XMLReader";
        final String SAX_VERSION2_METHOD = "parse";
        final String SAX_VERSION2BETA_CLASSNF = "org.xml.sax.helpers.AttributesImpl";
        final String SAX_VERSION2BETA_METHODNF = "setAttributes";
        final Class[] oneStringArg = { String.class };
        final Class[] attributesArg = { Attributes.class };
        try {
            final Class clazz = ObjectFactory.findProviderClass("org.xml.sax.helpers.AttributesImpl", true);
            final Method method = clazz.getMethod("setAttributes", (Class[])attributesArg);
            h.put("version.SAX", "2.0");
        }
        catch (final Exception e) {
            h.put("ERROR.version.SAX", "ERROR attempting to load SAX version 2 class: " + e.toString());
            h.put("ERROR.", "At least one error was found!");
            try {
                final Class clazz2 = ObjectFactory.findProviderClass("org.xml.sax.XMLReader", true);
                final Method method2 = clazz2.getMethod("parse", (Class[])oneStringArg);
                h.put("version.SAX-backlevel", "2.0beta2-or-earlier");
            }
            catch (final Exception e2) {
                h.put("ERROR.version.SAX", "ERROR attempting to load SAX version 2 class: " + e.toString());
                h.put("ERROR.", "At least one error was found!");
                try {
                    final Class clazz3 = ObjectFactory.findProviderClass("org.xml.sax.Parser", true);
                    final Method method3 = clazz3.getMethod("parse", (Class[])oneStringArg);
                    h.put("version.SAX-backlevel", "1.0");
                }
                catch (final Exception e3) {
                    h.put("ERROR.version.SAX-backlevel", "ERROR attempting to load SAX version 1 class: " + e3.toString());
                }
            }
        }
    }
    
    protected void logMsg(final String s) {
        this.outWriter.println(s);
    }
    
    static {
        final Map<Long, String> jarVersions = new HashMap<Long, String>();
        jarVersions.put(new Long(857192L), "xalan.jar from xalan-j_1_1");
        jarVersions.put(new Long(440237L), "xalan.jar from xalan-j_1_2");
        jarVersions.put(new Long(436094L), "xalan.jar from xalan-j_1_2_1");
        jarVersions.put(new Long(426249L), "xalan.jar from xalan-j_1_2_2");
        jarVersions.put(new Long(702536L), "xalan.jar from xalan-j_2_0_0");
        jarVersions.put(new Long(720930L), "xalan.jar from xalan-j_2_0_1");
        jarVersions.put(new Long(732330L), "xalan.jar from xalan-j_2_1_0");
        jarVersions.put(new Long(872241L), "xalan.jar from xalan-j_2_2_D10");
        jarVersions.put(new Long(882739L), "xalan.jar from xalan-j_2_2_D11");
        jarVersions.put(new Long(923866L), "xalan.jar from xalan-j_2_2_0");
        jarVersions.put(new Long(905872L), "xalan.jar from xalan-j_2_3_D1");
        jarVersions.put(new Long(906122L), "xalan.jar from xalan-j_2_3_0");
        jarVersions.put(new Long(906248L), "xalan.jar from xalan-j_2_3_1");
        jarVersions.put(new Long(983377L), "xalan.jar from xalan-j_2_4_D1");
        jarVersions.put(new Long(997276L), "xalan.jar from xalan-j_2_4_0");
        jarVersions.put(new Long(1031036L), "xalan.jar from xalan-j_2_4_1");
        jarVersions.put(new Long(596540L), "xsltc.jar from xalan-j_2_2_0");
        jarVersions.put(new Long(590247L), "xsltc.jar from xalan-j_2_3_D1");
        jarVersions.put(new Long(589914L), "xsltc.jar from xalan-j_2_3_0");
        jarVersions.put(new Long(589915L), "xsltc.jar from xalan-j_2_3_1");
        jarVersions.put(new Long(1306667L), "xsltc.jar from xalan-j_2_4_D1");
        jarVersions.put(new Long(1328227L), "xsltc.jar from xalan-j_2_4_0");
        jarVersions.put(new Long(1344009L), "xsltc.jar from xalan-j_2_4_1");
        jarVersions.put(new Long(1348361L), "xsltc.jar from xalan-j_2_5_D1");
        jarVersions.put(new Long(1268634L), "xsltc.jar-bundled from xalan-j_2_3_0");
        jarVersions.put(new Long(100196L), "xml-apis.jar from xalan-j_2_2_0 or xalan-j_2_3_D1");
        jarVersions.put(new Long(108484L), "xml-apis.jar from xalan-j_2_3_0, or xalan-j_2_3_1 from xml-commons-1.0.b2");
        jarVersions.put(new Long(109049L), "xml-apis.jar from xalan-j_2_4_0 from xml-commons RIVERCOURT1 branch");
        jarVersions.put(new Long(113749L), "xml-apis.jar from xalan-j_2_4_1 from factoryfinder-build of xml-commons RIVERCOURT1");
        jarVersions.put(new Long(124704L), "xml-apis.jar from tck-jaxp-1_2_0 branch of xml-commons");
        jarVersions.put(new Long(124724L), "xml-apis.jar from tck-jaxp-1_2_0 branch of xml-commons, tag: xml-commons-external_1_2_01");
        jarVersions.put(new Long(194205L), "xml-apis.jar from head branch of xml-commons, tag: xml-commons-external_1_3_02");
        jarVersions.put(new Long(424490L), "xalan.jar from Xerces Tools releases - ERROR:DO NOT USE!");
        jarVersions.put(new Long(1591855L), "xerces.jar from xalan-j_1_1 from xerces-1...");
        jarVersions.put(new Long(1498679L), "xerces.jar from xalan-j_1_2 from xerces-1_2_0.bin");
        jarVersions.put(new Long(1484896L), "xerces.jar from xalan-j_1_2_1 from xerces-1_2_1.bin");
        jarVersions.put(new Long(804460L), "xerces.jar from xalan-j_1_2_2 from xerces-1_2_2.bin");
        jarVersions.put(new Long(1499244L), "xerces.jar from xalan-j_2_0_0 from xerces-1_2_3.bin");
        jarVersions.put(new Long(1605266L), "xerces.jar from xalan-j_2_0_1 from xerces-1_3_0.bin");
        jarVersions.put(new Long(904030L), "xerces.jar from xalan-j_2_1_0 from xerces-1_4.bin");
        jarVersions.put(new Long(904030L), "xerces.jar from xerces-1_4_0.bin");
        jarVersions.put(new Long(1802885L), "xerces.jar from xerces-1_4_2.bin");
        jarVersions.put(new Long(1734594L), "xerces.jar from Xerces-J-bin.2.0.0.beta3");
        jarVersions.put(new Long(1808883L), "xerces.jar from xalan-j_2_2_D10,D11,D12 or xerces-1_4_3.bin");
        jarVersions.put(new Long(1812019L), "xerces.jar from xalan-j_2_2_0");
        jarVersions.put(new Long(1720292L), "xercesImpl.jar from xalan-j_2_3_D1");
        jarVersions.put(new Long(1730053L), "xercesImpl.jar from xalan-j_2_3_0 or xalan-j_2_3_1 from xerces-2_0_0");
        jarVersions.put(new Long(1728861L), "xercesImpl.jar from xalan-j_2_4_D1 from xerces-2_0_1");
        jarVersions.put(new Long(972027L), "xercesImpl.jar from xalan-j_2_4_0 from xerces-2_1");
        jarVersions.put(new Long(831587L), "xercesImpl.jar from xalan-j_2_4_1 from xerces-2_2");
        jarVersions.put(new Long(891817L), "xercesImpl.jar from xalan-j_2_5_D1 from xerces-2_3");
        jarVersions.put(new Long(895924L), "xercesImpl.jar from xerces-2_4");
        jarVersions.put(new Long(1010806L), "xercesImpl.jar from Xerces-J-bin.2.6.2");
        jarVersions.put(new Long(1203860L), "xercesImpl.jar from Xerces-J-bin.2.7.1");
        jarVersions.put(new Long(37485L), "xalanj1compat.jar from xalan-j_2_0_0");
        jarVersions.put(new Long(38100L), "xalanj1compat.jar from xalan-j_2_0_1");
        jarVersions.put(new Long(18779L), "xalanservlet.jar from xalan-j_2_0_0");
        jarVersions.put(new Long(21453L), "xalanservlet.jar from xalan-j_2_0_1");
        jarVersions.put(new Long(24826L), "xalanservlet.jar from xalan-j_2_3_1 or xalan-j_2_4_1");
        jarVersions.put(new Long(24831L), "xalanservlet.jar from xalan-j_2_4_1");
        jarVersions.put(new Long(5618L), "jaxp.jar from jaxp1.0.1");
        jarVersions.put(new Long(136133L), "parser.jar from jaxp1.0.1");
        jarVersions.put(new Long(28404L), "jaxp.jar from jaxp-1.1");
        jarVersions.put(new Long(187162L), "crimson.jar from jaxp-1.1");
        jarVersions.put(new Long(801714L), "xalan.jar from jaxp-1.1");
        jarVersions.put(new Long(196399L), "crimson.jar from crimson-1.1.1");
        jarVersions.put(new Long(33323L), "jaxp.jar from crimson-1.1.1 or jakarta-ant-1.4.1b1");
        jarVersions.put(new Long(152717L), "crimson.jar from crimson-1.1.2beta2");
        jarVersions.put(new Long(88143L), "xml-apis.jar from crimson-1.1.2beta2");
        jarVersions.put(new Long(206384L), "crimson.jar from crimson-1.1.3 or jakarta-ant-1.4.1b1");
        jarVersions.put(new Long(136198L), "parser.jar from jakarta-ant-1.3 or 1.2");
        jarVersions.put(new Long(5537L), "jaxp.jar from jakarta-ant-1.3 or 1.2");
        JARVERSIONS = Collections.unmodifiableMap((Map<? extends Long, ? extends String>)jarVersions);
    }
}
