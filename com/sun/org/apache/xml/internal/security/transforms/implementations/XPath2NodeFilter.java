package com.sun.org.apache.xml.internal.security.transforms.implementations;

import java.util.HashSet;
import java.util.Iterator;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.NodeList;
import java.util.List;
import org.w3c.dom.Node;
import java.util.Set;
import com.sun.org.apache.xml.internal.security.signature.NodeFilter;

class XPath2NodeFilter implements NodeFilter
{
    boolean hasUnionFilter;
    boolean hasSubtractFilter;
    boolean hasIntersectFilter;
    Set<Node> unionNodes;
    Set<Node> subtractNodes;
    Set<Node> intersectNodes;
    int inSubtract;
    int inIntersect;
    int inUnion;
    
    XPath2NodeFilter(final List<NodeList> list, final List<NodeList> list2, final List<NodeList> list3) {
        this.inSubtract = -1;
        this.inIntersect = -1;
        this.inUnion = -1;
        this.hasUnionFilter = !list.isEmpty();
        this.unionNodes = convertNodeListToSet(list);
        this.hasSubtractFilter = !list2.isEmpty();
        this.subtractNodes = convertNodeListToSet(list2);
        this.hasIntersectFilter = !list3.isEmpty();
        this.intersectNodes = convertNodeListToSet(list3);
    }
    
    @Override
    public int isNodeInclude(final Node node) {
        int n = 1;
        if (this.hasSubtractFilter && rooted(node, this.subtractNodes)) {
            n = -1;
        }
        else if (this.hasIntersectFilter && !rooted(node, this.intersectNodes)) {
            n = 0;
        }
        if (n == 1) {
            return 1;
        }
        if (this.hasUnionFilter) {
            if (rooted(node, this.unionNodes)) {
                return 1;
            }
            n = 0;
        }
        return n;
    }
    
    @Override
    public int isNodeIncludeDO(final Node node, final int inUnion) {
        int n = 1;
        if (this.hasSubtractFilter) {
            if (this.inSubtract == -1 || inUnion <= this.inSubtract) {
                if (inList(node, this.subtractNodes)) {
                    this.inSubtract = inUnion;
                }
                else {
                    this.inSubtract = -1;
                }
            }
            if (this.inSubtract != -1) {
                n = -1;
            }
        }
        if (n != -1 && this.hasIntersectFilter && (this.inIntersect == -1 || inUnion <= this.inIntersect)) {
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
        if (this.hasUnionFilter) {
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
    
    static boolean rooted(final Node node, final Set<Node> set) {
        if (set.isEmpty()) {
            return false;
        }
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
    
    static boolean inList(final Node node, final Set<Node> set) {
        return set.contains(node);
    }
    
    private static Set<Node> convertNodeListToSet(final List<NodeList> list) {
        final HashSet set = new HashSet();
        for (final NodeList list2 : list) {
            for (int length = list2.getLength(), i = 0; i < length; ++i) {
                set.add(list2.item(i));
            }
        }
        return set;
    }
}
