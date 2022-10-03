package com.sun.org.apache.xalan.internal.xsltc.cmdline;

import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import org.xml.sax.XMLReader;
import javax.xml.parsers.SAXParser;
import org.xml.sax.SAXException;
import java.net.UnknownHostException;
import java.net.MalformedURLException;
import java.io.FileNotFoundException;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.runtime.output.TransletOutputHandlerFactory;
import com.sun.org.apache.xalan.internal.xsltc.runtime.Parameter;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;
import com.sun.org.apache.xalan.internal.xsltc.DOMEnhancedForDTM;
import com.sun.org.apache.xalan.internal.xsltc.dom.DOMWSFilter;
import com.sun.org.apache.xalan.internal.xsltc.StripFilter;
import com.sun.org.apache.xalan.internal.xsltc.dom.XSLTCDTMManager;
import javax.xml.parsers.SAXParserFactory;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import java.util.Vector;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;

public final class Transform
{
    private SerializationHandler _handler;
    private String _fileName;
    private String _className;
    private String _jarFileSrc;
    private boolean _isJarFileSpecified;
    private Vector _params;
    private boolean _uri;
    private boolean _debug;
    private int _iterations;
    
    public Transform(final String className, final String fileName, final boolean uri, final boolean debug, final int iterations) {
        this._isJarFileSpecified = false;
        this._params = null;
        this._fileName = fileName;
        this._className = className;
        this._uri = uri;
        this._debug = debug;
        this._iterations = iterations;
    }
    
    public String getFileName() {
        return this._fileName;
    }
    
    public String getClassName() {
        return this._className;
    }
    
    public void setParameters(final Vector params) {
        this._params = params;
    }
    
    private void setJarFileInputSrc(final boolean flag, final String jarFile) {
        this._isJarFileSpecified = flag;
        this._jarFileSrc = jarFile;
    }
    
    private void doTransform() {
        try {
            final Class clazz = ObjectFactory.findProviderClass(this._className, true);
            final AbstractTranslet translet = clazz.newInstance();
            translet.postInitialization();
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            try {
                factory.setFeature("http://xml.org/sax/features/namespaces", true);
            }
            catch (final Exception e) {
                factory.setNamespaceAware(true);
            }
            final SAXParser parser = factory.newSAXParser();
            final XMLReader reader = parser.getXMLReader();
            final XSLTCDTMManager dtmManager = XSLTCDTMManager.createNewDTMManagerInstance();
            DTMWSFilter wsfilter;
            if (translet != null && translet instanceof StripFilter) {
                wsfilter = new DOMWSFilter(translet);
            }
            else {
                wsfilter = null;
            }
            final DOMEnhancedForDTM dom = (DOMEnhancedForDTM)dtmManager.getDTM(new SAXSource(reader, new InputSource(this._fileName)), false, wsfilter, true, false, translet.hasIdCall());
            dom.setDocumentURI(this._fileName);
            translet.prepassDocument(dom);
            for (int n = this._params.size(), i = 0; i < n; ++i) {
                final Parameter param = this._params.elementAt(i);
                translet.addParameter(param._name, param._value);
            }
            final TransletOutputHandlerFactory tohFactory = TransletOutputHandlerFactory.newInstance();
            tohFactory.setOutputType(0);
            tohFactory.setEncoding(translet._encoding);
            tohFactory.setOutputMethod(translet._method);
            if (this._iterations == -1) {
                translet.transform(dom, tohFactory.getSerializationHandler());
            }
            else if (this._iterations > 0) {
                long mm = System.currentTimeMillis();
                for (int j = 0; j < this._iterations; ++j) {
                    translet.transform(dom, tohFactory.getSerializationHandler());
                }
                mm = System.currentTimeMillis() - mm;
                System.err.println("\n<!--");
                System.err.println("  transform  = " + mm / (double)this._iterations + " ms");
                System.err.println("  throughput = " + 1000.0 / (mm / (double)this._iterations) + " tps");
                System.err.println("-->");
            }
        }
        catch (final TransletException e2) {
            if (this._debug) {
                e2.printStackTrace();
            }
            System.err.println(new ErrorMsg("RUNTIME_ERROR_KEY") + e2.getMessage());
        }
        catch (final RuntimeException e3) {
            if (this._debug) {
                e3.printStackTrace();
            }
            System.err.println(new ErrorMsg("RUNTIME_ERROR_KEY") + e3.getMessage());
        }
        catch (final FileNotFoundException e4) {
            if (this._debug) {
                e4.printStackTrace();
            }
            final ErrorMsg err = new ErrorMsg("FILE_NOT_FOUND_ERR", this._fileName);
            System.err.println(new ErrorMsg("RUNTIME_ERROR_KEY") + err.toString());
        }
        catch (final MalformedURLException e5) {
            if (this._debug) {
                e5.printStackTrace();
            }
            final ErrorMsg err = new ErrorMsg("INVALID_URI_ERR", this._fileName);
            System.err.println(new ErrorMsg("RUNTIME_ERROR_KEY") + err.toString());
        }
        catch (final ClassNotFoundException e6) {
            if (this._debug) {
                e6.printStackTrace();
            }
            final ErrorMsg err = new ErrorMsg("CLASS_NOT_FOUND_ERR", this._className);
            System.err.println(new ErrorMsg("RUNTIME_ERROR_KEY") + err.toString());
        }
        catch (final UnknownHostException e7) {
            if (this._debug) {
                e7.printStackTrace();
            }
            final ErrorMsg err = new ErrorMsg("INVALID_URI_ERR", this._fileName);
            System.err.println(new ErrorMsg("RUNTIME_ERROR_KEY") + err.toString());
        }
        catch (final SAXException e8) {
            final Exception ex = e8.getException();
            if (this._debug) {
                if (ex != null) {
                    ex.printStackTrace();
                }
                e8.printStackTrace();
            }
            System.err.print(new ErrorMsg("RUNTIME_ERROR_KEY"));
            if (ex != null) {
                System.err.println(ex.getMessage());
            }
            else {
                System.err.println(e8.getMessage());
            }
        }
        catch (final Exception e9) {
            if (this._debug) {
                e9.printStackTrace();
            }
            System.err.println(new ErrorMsg("RUNTIME_ERROR_KEY") + e9.getMessage());
        }
    }
    
    public static void printUsage() {
        System.err.println(new ErrorMsg("TRANSFORM_USAGE_STR"));
    }
    
    public static void main(final String[] args) {
        try {
            if (args.length > 0) {
                int iterations = -1;
                boolean uri = false;
                boolean debug = false;
                boolean isJarFileSpecified = false;
                String jarFile = null;
                int i;
                for (i = 0; i < args.length && args[i].charAt(0) == '-'; ++i) {
                    if (args[i].equals("-u")) {
                        uri = true;
                    }
                    else if (args[i].equals("-x")) {
                        debug = true;
                    }
                    else if (args[i].equals("-j")) {
                        isJarFileSpecified = true;
                        jarFile = args[++i];
                    }
                    else if (args[i].equals("-n")) {
                        try {
                            iterations = Integer.parseInt(args[++i]);
                        }
                        catch (final NumberFormatException ex) {}
                    }
                    else {
                        printUsage();
                    }
                }
                if (args.length - i < 2) {
                    printUsage();
                }
                final Transform handler = new Transform(args[i + 1], args[i], uri, debug, iterations);
                handler.setJarFileInputSrc(isJarFileSpecified, jarFile);
                final Vector params = new Vector();
                for (i += 2; i < args.length; ++i) {
                    final int equal = args[i].indexOf(61);
                    if (equal > 0) {
                        final String name = args[i].substring(0, equal);
                        final String value = args[i].substring(equal + 1);
                        params.addElement(new Parameter(name, value));
                    }
                    else {
                        printUsage();
                    }
                }
                if (i == args.length) {
                    handler.setParameters(params);
                    handler.doTransform();
                }
            }
            else {
                printUsage();
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
