package org.apache.xml.security.transforms.implementations;

import java.util.Iterator;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Node;
import java.util.Set;
import org.apache.xml.security.signature.NodeFilter;

class XPath2NodeFilter implements NodeFilter
{
    boolean hasUnionNodes;
    boolean hasSubstractNodes;
    boolean hasIntersectNodes;
    Set unionNodes;
    Set substractNodes;
    Set intersectNodes;
    int inSubstract;
    int inIntersect;
    int inUnion;
    
    XPath2NodeFilter(final Set unionNodes, final Set substractNodes, final Set intersectNodes) {
        this.inSubstract = -1;
        this.inIntersect = -1;
        this.inUnion = -1;
        this.unionNodes = unionNodes;
        this.hasUnionNodes = !unionNodes.isEmpty();
        this.substractNodes = substractNodes;
        this.hasSubstractNodes = !substractNodes.isEmpty();
        this.intersectNodes = intersectNodes;
        this.hasIntersectNodes = !intersectNodes.isEmpty();
    }
    
    public int isNodeInclude(final Node node) {
        int n = 1;
        if (this.hasSubstractNodes && rooted(node, this.substractNodes)) {
            n = -1;
        }
        else if (this.hasIntersectNodes && !rooted(node, this.intersectNodes)) {
            n = 0;
        }
        if (n == 1) {
            return 1;
        }
        if (this.hasUnionNodes) {
            if (rooted(node, this.unionNodes)) {
                return 1;
            }
            n = 0;
        }
        return n;
    }
    
    public int isNodeIncludeDO(final Node node, final int inUnion) {
        int n = 1;
        if (this.hasSubstractNodes) {
            if (this.inSubstract == -1 || inUnion <= this.inSubstract) {
                if (inList(node, this.substractNodes)) {
                    this.inSubstract = inUnion;
                }
                else {
                    this.inSubstract = -1;
                }
            }
            if (this.inSubstract != -1) {
                n = -1;
            }
        }
        if (n != -1 && this.hasIntersectNodes && (this.inIntersect == -1 || inUnion <= this.inIntersect)) {
            if (!inList(node, this.intersectNodes)) {
                this.inIntersect = -1;
                n = 0;
            }
            else {
                this.inIntersect = inUnion;
            }
        }
        if (inUnion <= this.inUnion) {
            this.inUnion = -1;
        }
        if (n == 1) {
            return 1;
        }
        if (this.hasUnionNodes) {
            if (this.inUnion == -1 && inList(node, this.unionNodes)) {
                this.inUnion = inUnion;
            }
            if (this.inUnion != -1) {
                return 1;
            }
            n = 0;
        }
        return n;
    }
    
    static boolean rooted(final Node node, final Set set) {
        if (set.contains(node)) {
            return true;
        }
        final Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            if (XMLUtils.isDescendantOrSelf((Node)iterator.next(), node)) {
                return true;
            }
        }
        return false;
    }
    
    static boolean inList(final Node node, final Set set) {
        return set.contains(node);
    }
}
