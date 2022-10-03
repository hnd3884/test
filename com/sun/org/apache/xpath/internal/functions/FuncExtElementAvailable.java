package com.sun.org.apache.xpath.internal.functions;

import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.ExtensionsProvider;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;

public class FuncExtElementAvailable extends FunctionOneArg
{
    static final long serialVersionUID = -472533699257968546L;
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        final String fullName = this.m_arg0.execute(xctxt).str();
        final int indexOfNSSep = fullName.indexOf(58);
        String namespace;
        String methName;
        if (indexOfNSSep < 0) {
            final String prefix = "";
            namespace = "http://www.w3.org/1999/XSL/Transform";
            methName = fullName;
        }
        else {
            final String prefix = fullName.substring(0, indexOfNSSep);
            namespace = xctxt.getNamespaceContext().getNamespaceForPrefix(prefix);
            if (null == namespace) {
                return XBoolean.S_FALSE;
            }
            methName = fullName.substring(indexOfNSSep + 1);
        }
        if (namespace.equals("http://www.w3.org/1999/XSL/Transform") || namespace.equals("http://xml.apache.org/xalan")) {
            return XBoolean.S_FALSE;
        }
        final ExtensionsProvider extProvider = (ExtensionsProvider)xctxt.getOwnerObject();
        return extProvider.elementAvailable(namespace, methName) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
    }
}
