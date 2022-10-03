package com.sun.org.apache.xpath.internal.jaxp;

import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xml.internal.utils.QName;
import com.sun.org.apache.xpath.internal.XPathContext;
import javax.xml.xpath.XPathVariableResolver;
import com.sun.org.apache.xpath.internal.VariableStack;

public class JAXPVariableStack extends VariableStack
{
    private final XPathVariableResolver resolver;
    
    public JAXPVariableStack(final XPathVariableResolver resolver) {
        this.resolver = resolver;
    }
    
    @Override
    public XObject getVariableOrParam(final XPathContext xctxt, final QName qname) throws TransformerException, IllegalArgumentException {
        if (qname == null) {
            final String fmsg = XPATHMessages.createXPATHMessage("ER_ARG_CANNOT_BE_NULL", new Object[] { "Variable qname" });
            throw new IllegalArgumentException(fmsg);
        }
        final javax.xml.namespace.QName name = new javax.xml.namespace.QName(qname.getNamespace(), qname.getLocalPart());
        final Object varValue = this.resolver.resolveVariable(name);
        if (varValue == null) {
            final String fmsg2 = XPATHMessages.createXPATHMessage("ER_RESOLVE_VARIABLE_RETURNS_NULL", new Object[] { name.toString() });
            throw new TransformerException(fmsg2);
        }
        return XObject.create(varValue, xctxt);
    }
}
