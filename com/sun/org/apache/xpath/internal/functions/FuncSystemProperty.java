package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xpath.internal.objects.XString;
import java.util.Properties;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;

public class FuncSystemProperty extends FunctionOneArg
{
    static final long serialVersionUID = 3694874980992204867L;
    static final String XSLT_PROPERTIES = "com/sun/org/apache/xalan/internal/res/XSLTInfo.properties";
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        final String fullName = this.m_arg0.execute(xctxt).str();
        final int indexOfNSSep = fullName.indexOf(58);
        String propName = "";
        final Properties xsltInfo = new Properties();
        this.loadPropertyFile("com/sun/org/apache/xalan/internal/res/XSLTInfo.properties", xsltInfo);
        String result;
        if (indexOfNSSep > 0) {
            final String prefix = (indexOfNSSep >= 0) ? fullName.substring(0, indexOfNSSep) : "";
            final String namespace = xctxt.getNamespaceContext().getNamespaceForPrefix(prefix);
            propName = ((indexOfNSSep < 0) ? fullName : fullName.substring(indexOfNSSep + 1));
            if (namespace.startsWith("http://www.w3.org/XSL/Transform") || namespace.equals("http://www.w3.org/1999/XSL/Transform")) {
                result = xsltInfo.getProperty(propName);
                if (null == result) {
                    this.warn(xctxt, "WG_PROPERTY_NOT_SUPPORTED", new Object[] { fullName });
                    return XString.EMPTYSTRING;
                }
            }
            else {
                this.warn(xctxt, "WG_DONT_DO_ANYTHING_WITH_NS", new Object[] { namespace, fullName });
                try {
                    result = SecuritySupport.getSystemProperty(propName);
                    if (null == result) {
                        return XString.EMPTYSTRING;
                    }
                }
                catch (final SecurityException se) {
                    this.warn(xctxt, "WG_SECURITY_EXCEPTION", new Object[] { fullName });
                    return XString.EMPTYSTRING;
                }
            }
        }
        else {
            try {
                result = SecuritySupport.getSystemProperty(fullName);
                if (null == result) {
                    return XString.EMPTYSTRING;
                }
            }
            catch (final SecurityException se2) {
                this.warn(xctxt, "WG_SECURITY_EXCEPTION", new Object[] { fullName });
                return XString.EMPTYSTRING;
            }
        }
        if (propName.equals("version") && result.length() > 0) {
            try {
                return new XString("1.0");
            }
            catch (final Exception ex) {
                return new XString(result);
            }
        }
        return new XString(result);
    }
    
    public void loadPropertyFile(final String file, final Properties target) {
        try {
            final InputStream is = SecuritySupport.getResourceAsStream(ObjectFactory.findClassLoader(), file);
            final BufferedInputStream bis = new BufferedInputStream(is);
            target.load(bis);
            bis.close();
        }
        catch (final Exception ex) {
            throw new WrappedRuntimeException(ex);
        }
    }
}
