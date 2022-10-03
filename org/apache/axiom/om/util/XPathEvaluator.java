package org.apache.axiom.om.util;

import org.jaxen.NamespaceContext;
import org.jaxen.SimpleNamespaceContext;
import org.apache.axiom.om.xpath.AXIOMXPath;
import java.util.List;

public class XPathEvaluator
{
    public List evaluateXpath(final String xpathExpression, final Object element, final String nsURI) throws Exception {
        final AXIOMXPath xpath = new AXIOMXPath(xpathExpression);
        if (nsURI != null) {
            final SimpleNamespaceContext nsContext = new SimpleNamespaceContext();
            nsContext.addNamespace((String)null, nsURI);
            xpath.setNamespaceContext((NamespaceContext)nsContext);
        }
        return xpath.selectNodes(element);
    }
}
