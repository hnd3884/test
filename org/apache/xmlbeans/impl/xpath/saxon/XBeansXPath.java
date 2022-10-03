package org.apache.xmlbeans.impl.xpath.saxon;

import java.util.ListIterator;
import net.sf.saxon.sxpath.XPathDynamicContext;
import net.sf.saxon.sxpath.XPathExpression;
import net.sf.saxon.sxpath.XPathVariable;
import javax.xml.transform.TransformerException;
import net.sf.saxon.value.Value;
import net.sf.saxon.om.VirtualNode;
import net.sf.saxon.dom.NodeWrapper;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.ValueRepresentation;
import net.sf.saxon.om.Item;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import net.sf.saxon.sxpath.XPathStaticContext;
import net.sf.saxon.sxpath.IndependentContext;
import net.sf.saxon.Configuration;
import net.sf.saxon.sxpath.XPathEvaluator;
import org.w3c.dom.Node;
import java.util.List;
import java.util.Map;
import org.apache.xmlbeans.impl.store.PathDelegate;

public class XBeansXPath implements PathDelegate.SelectPathInterface
{
    private Object[] namespaceMap;
    private String path;
    private String contextVar;
    private String defaultNS;
    
    public XBeansXPath(final String path, final String contextVar, final Map namespaceMap, final String defaultNS) {
        this.path = path;
        this.contextVar = contextVar;
        this.defaultNS = defaultNS;
        this.namespaceMap = namespaceMap.entrySet().toArray();
    }
    
    public List selectNodes(final Object node) {
        try {
            final Node contextNode = (Node)node;
            final XPathEvaluator xpe = new XPathEvaluator();
            final Configuration config = new Configuration();
            config.setDOMLevel(2);
            config.setTreeModel(0);
            final IndependentContext sc = new IndependentContext(config);
            if (this.defaultNS != null) {
                sc.setDefaultElementNamespace(this.defaultNS);
            }
            for (int i = 0; i < this.namespaceMap.length; ++i) {
                final Map.Entry entry = (Map.Entry)this.namespaceMap[i];
                sc.declareNamespace((String)entry.getKey(), (String)entry.getValue());
            }
            xpe.setStaticContext((XPathStaticContext)sc);
            final XPathVariable thisVar = xpe.declareVariable("", this.contextVar);
            final XPathExpression xpath = xpe.createExpression(this.path);
            final NodeInfo contextItem = config.unravel((Source)new DOMSource(contextNode));
            final XPathDynamicContext dc = xpath.createDynamicContext((Item)null);
            dc.setContextItem((Item)contextItem);
            dc.setVariable(thisVar, (ValueRepresentation)contextItem);
            final List saxonNodes = xpath.evaluate(dc);
            final ListIterator it = saxonNodes.listIterator();
            while (it.hasNext()) {
                final Object o = it.next();
                if (o instanceof NodeInfo) {
                    if (o instanceof NodeWrapper) {
                        final Node n = getUnderlyingNode((VirtualNode)o);
                        it.set(n);
                    }
                    else {
                        it.set(((NodeInfo)o).getStringValue());
                    }
                }
                else {
                    if (!(o instanceof Item)) {
                        continue;
                    }
                    it.set(Value.convertToJava((Item)o));
                }
            }
            return saxonNodes;
        }
        catch (final TransformerException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public List selectPath(final Object node) {
        return this.selectNodes(node);
    }
    
    private static Node getUnderlyingNode(final VirtualNode v) {
        Object o;
        for (o = v; o instanceof VirtualNode; o = ((VirtualNode)o).getUnderlyingNode()) {}
        return (Node)o;
    }
}
