package org.apache.xmlbeans.impl.xquery.saxon;

import java.util.ListIterator;
import net.sf.saxon.dom.NodeOverNodeInfo;
import net.sf.saxon.om.NodeInfo;
import org.apache.xmlbeans.XmlTokenSource;
import net.sf.saxon.om.Item;
import net.sf.saxon.query.DynamicQueryContext;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Node;
import java.util.List;
import java.util.Iterator;
import javax.xml.transform.TransformerException;
import org.apache.xmlbeans.XmlRuntimeException;
import java.util.Map;
import net.sf.saxon.query.StaticQueryContext;
import org.apache.xmlbeans.XmlOptions;
import net.sf.saxon.Configuration;
import net.sf.saxon.query.XQueryExpression;
import org.apache.xmlbeans.impl.store.QueryDelegate;

public class XBeansXQuery implements QueryDelegate.QueryInterface
{
    private XQueryExpression xquery;
    private String contextVar;
    private Configuration config;
    
    public XBeansXQuery(String query, final String contextVar, final Integer boundary, final XmlOptions xmlOptions) {
        (this.config = new Configuration()).setDOMLevel(2);
        this.config.setTreeModel(0);
        final StaticQueryContext sc = new StaticQueryContext(this.config);
        final Map<String, String> nsMap = (Map<String, String>)xmlOptions.get("LOAD_ADDITIONAL_NAMESPACES");
        if (nsMap != null) {
            for (final Map.Entry<String, String> me : nsMap.entrySet()) {
                sc.declareNamespace((String)me.getKey(), (String)me.getValue());
            }
        }
        this.contextVar = contextVar;
        query = ((boundary == 0) ? ("declare variable $" + contextVar + " external;" + query) : (query.substring(0, boundary) + "declare variable $" + contextVar + " external;" + query.substring(boundary)));
        try {
            this.xquery = sc.compileQuery(query);
        }
        catch (final TransformerException e) {
            throw new XmlRuntimeException(e);
        }
    }
    
    @Override
    public List execQuery(final Object node, final Map variableBindings) {
        try {
            final Node contextNode = (Node)node;
            final NodeInfo contextItem = (NodeInfo)this.config.buildDocument((Source)new DOMSource(contextNode));
            final DynamicQueryContext dc = new DynamicQueryContext(this.config);
            dc.setContextItem((Item)contextItem);
            dc.setParameter(this.contextVar, (Object)contextItem);
            if (variableBindings != null) {
                for (final Map.Entry entry : variableBindings.entrySet()) {
                    final String key = entry.getKey();
                    final Object value = entry.getValue();
                    if (value instanceof XmlTokenSource) {
                        final Node paramObject = ((XmlTokenSource)value).getDomNode();
                        dc.setParameter(key, (Object)paramObject);
                    }
                    else {
                        if (!(value instanceof String)) {
                            continue;
                        }
                        dc.setParameter(key, value);
                    }
                }
            }
            final List saxonNodes = this.xquery.evaluate(dc);
            final ListIterator it2 = saxonNodes.listIterator();
            while (it2.hasNext()) {
                final Object o = it2.next();
                if (o instanceof NodeInfo) {
                    final Node n = (Node)NodeOverNodeInfo.wrap((NodeInfo)o);
                    it2.set(n);
                }
            }
            return saxonNodes;
        }
        catch (final TransformerException e) {
            throw new RuntimeException("Error binding " + this.contextVar, e);
        }
    }
}
