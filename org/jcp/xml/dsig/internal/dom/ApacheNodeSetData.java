package org.jcp.xml.dsig.internal.dom;

import org.apache.xml.security.signature.NodeFilter;
import org.w3c.dom.Node;
import java.util.LinkedHashSet;
import org.apache.xml.security.utils.XMLUtils;
import java.util.List;
import java.util.Set;
import java.util.Collections;
import java.util.Iterator;
import org.apache.xml.security.signature.XMLSignatureInput;
import javax.xml.crypto.NodeSetData;

public class ApacheNodeSetData implements ApacheData, NodeSetData
{
    private XMLSignatureInput xi;
    
    public ApacheNodeSetData(final XMLSignatureInput xi) {
        this.xi = xi;
    }
    
    public Iterator iterator() {
        if (this.xi.getNodeFilters() != null) {
            return Collections.unmodifiableSet((Set<?>)this.getNodeSet(this.xi.getNodeFilters())).iterator();
        }
        try {
            return Collections.unmodifiableSet((Set<?>)this.xi.getNodeSet()).iterator();
        }
        catch (final Exception ex) {
            throw new RuntimeException("unrecoverable error retrieving nodeset", ex);
        }
    }
    
    public XMLSignatureInput getXMLSignatureInput() {
        return this.xi;
    }
    
    private Set getNodeSet(final List list) {
        if (this.xi.isNeedsToBeExpanded()) {
            XMLUtils.circumventBug2650(XMLUtils.getOwnerDocument(this.xi.getSubNode()));
        }
        final LinkedHashSet set = new LinkedHashSet();
        XMLUtils.getSet(this.xi.getSubNode(), set, null, !this.xi.isExcludeComments());
        final LinkedHashSet set2 = new LinkedHashSet();
        final Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            final Node node = (Node)iterator.next();
            Iterator iterator2;
            int n;
            for (iterator2 = list.iterator(), n = 0; iterator2.hasNext() && n == 0; n = 1) {
                if (((NodeFilter)iterator2.next()).isNodeInclude(node) != 1) {}
            }
            if (n == 0) {
                set2.add(node);
            }
        }
        return set2;
    }
}
