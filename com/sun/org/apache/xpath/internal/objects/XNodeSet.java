package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import java.util.Vector;
import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeList;
import org.w3c.dom.NodeList;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeIterator;
import org.w3c.dom.traversal.NodeIterator;
import com.sun.org.apache.xml.internal.utils.FastStringBuffer;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xpath.internal.NodeSetDTM;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xpath.internal.axes.NodeSequence;

public class XNodeSet extends NodeSequence
{
    static final long serialVersionUID = 1916026368035639667L;
    static final LessThanComparator S_LT;
    static final LessThanOrEqualComparator S_LTE;
    static final GreaterThanComparator S_GT;
    static final GreaterThanOrEqualComparator S_GTE;
    static final EqualComparator S_EQ;
    static final NotEqualComparator S_NEQ;
    
    protected XNodeSet() {
    }
    
    public XNodeSet(final DTMIterator val) {
        if (val instanceof XNodeSet) {
            final XNodeSet nodeSet = (XNodeSet)val;
            this.setIter(nodeSet.m_iter);
            this.m_dtmMgr = nodeSet.m_dtmMgr;
            this.m_last = nodeSet.m_last;
            if (!nodeSet.hasCache()) {
                nodeSet.setShouldCacheNodes(true);
            }
            this.setObject(nodeSet.getIteratorCache());
        }
        else {
            this.setIter(val);
        }
    }
    
    public XNodeSet(final XNodeSet val) {
        this.setIter(val.m_iter);
        this.m_dtmMgr = val.m_dtmMgr;
        this.m_last = val.m_last;
        if (!val.hasCache()) {
            val.setShouldCacheNodes(true);
        }
        this.setObject(val.m_obj);
    }
    
    public XNodeSet(final DTMManager dtmMgr) {
        this(-1, dtmMgr);
    }
    
    public XNodeSet(final int n, final DTMManager dtmMgr) {
        super(new NodeSetDTM(dtmMgr));
        this.m_dtmMgr = dtmMgr;
        if (-1 != n) {
            ((NodeSetDTM)this.m_obj).addNode(n);
            this.m_last = 1;
        }
        else {
            this.m_last = 0;
        }
    }
    
    @Override
    public int getType() {
        return 4;
    }
    
    @Override
    public String getTypeString() {
        return "#NODESET";
    }
    
    public double getNumberFromNode(final int n) {
        final XMLString xstr = this.m_dtmMgr.getDTM(n).getStringValue(n);
        return xstr.toDouble();
    }
    
    @Override
    public double num() {
        final int node = this.item(0);
        return (node != -1) ? this.getNumberFromNode(node) : Double.NaN;
    }
    
    @Override
    public double numWithSideEffects() {
        final int node = this.nextNode();
        return (node != -1) ? this.getNumberFromNode(node) : Double.NaN;
    }
    
    @Override
    public boolean bool() {
        return this.item(0) != -1;
    }
    
    @Override
    public boolean boolWithSideEffects() {
        return this.nextNode() != -1;
    }
    
    public XMLString getStringFromNode(final int n) {
        if (-1 != n) {
            return this.m_dtmMgr.getDTM(n).getStringValue(n);
        }
        return XString.EMPTYSTRING;
    }
    
    @Override
    public void dispatchCharactersEvents(final ContentHandler ch) throws SAXException {
        final int node = this.item(0);
        if (node != -1) {
            this.m_dtmMgr.getDTM(node).dispatchCharactersEvents(node, ch, false);
        }
    }
    
    @Override
    public XMLString xstr() {
        final int node = this.item(0);
        return (node != -1) ? this.getStringFromNode(node) : XString.EMPTYSTRING;
    }
    
    @Override
    public void appendToFsb(final FastStringBuffer fsb) {
        final XString xstring = (XString)this.xstr();
        xstring.appendToFsb(fsb);
    }
    
    @Override
    public String str() {
        final int node = this.item(0);
        return (node != -1) ? this.getStringFromNode(node).toString() : "";
    }
    
    @Override
    public Object object() {
        if (null == this.m_obj) {
            return this;
        }
        return this.m_obj;
    }
    
    @Override
    public NodeIterator nodeset() throws TransformerException {
        return new DTMNodeIterator(this.iter());
    }
    
    @Override
    public NodeList nodelist() throws TransformerException {
        final DTMNodeList nodelist = new DTMNodeList(this);
        final XNodeSet clone = (XNodeSet)nodelist.getDTMIterator();
        this.SetVector(clone.getVector());
        return nodelist;
    }
    
    public DTMIterator iterRaw() {
        return this;
    }
    
    public void release(final DTMIterator iter) {
    }
    
    @Override
    public DTMIterator iter() {
        try {
            if (this.hasCache()) {
                return this.cloneWithReset();
            }
            return this;
        }
        catch (final CloneNotSupportedException cnse) {
            throw new RuntimeException(cnse.getMessage());
        }
    }
    
    @Override
    public XObject getFresh() {
        try {
            if (this.hasCache()) {
                return (XObject)this.cloneWithReset();
            }
            return this;
        }
        catch (final CloneNotSupportedException cnse) {
            throw new RuntimeException(cnse.getMessage());
        }
    }
    
    @Override
    public NodeSetDTM mutableNodeset() {
        NodeSetDTM mnl;
        if (this.m_obj instanceof NodeSetDTM) {
            mnl = (NodeSetDTM)this.m_obj;
        }
        else {
            mnl = new NodeSetDTM(this.iter());
            this.setObject(mnl);
            this.setCurrentPos(0);
        }
        return mnl;
    }
    
    public boolean compare(final XObject obj2, final Comparator comparator) throws TransformerException {
        boolean result = false;
        final int type = obj2.getType();
        if (4 == type) {
            final DTMIterator list1 = this.iterRaw();
            final DTMIterator list2 = ((XNodeSet)obj2).iterRaw();
            Vector node2Strings = null;
            int node1;
            while (-1 != (node1 = list1.nextNode())) {
                final XMLString s1 = this.getStringFromNode(node1);
                if (null == node2Strings) {
                    int node2;
                    while (-1 != (node2 = list2.nextNode())) {
                        final XMLString s2 = this.getStringFromNode(node2);
                        if (comparator.compareStrings(s1, s2)) {
                            result = true;
                            break;
                        }
                        if (null == node2Strings) {
                            node2Strings = new Vector();
                        }
                        node2Strings.addElement(s2);
                    }
                }
                else {
                    for (int n = node2Strings.size(), i = 0; i < n; ++i) {
                        if (comparator.compareStrings(s1, node2Strings.elementAt(i))) {
                            result = true;
                            break;
                        }
                    }
                }
            }
            list1.reset();
            list2.reset();
        }
        else if (1 == type) {
            final double num1 = this.bool() ? 1.0 : 0.0;
            final double num2 = obj2.num();
            result = comparator.compareNumbers(num1, num2);
        }
        else if (2 == type) {
            final DTMIterator list1 = this.iterRaw();
            final double num3 = obj2.num();
            int node3;
            while (-1 != (node3 = list1.nextNode())) {
                final double num4 = this.getNumberFromNode(node3);
                if (comparator.compareNumbers(num4, num3)) {
                    result = true;
                    break;
                }
            }
            list1.reset();
        }
        else if (5 == type) {
            final XMLString s3 = obj2.xstr();
            final DTMIterator list3 = this.iterRaw();
            int node4;
            while (-1 != (node4 = list3.nextNode())) {
                final XMLString s4 = this.getStringFromNode(node4);
                if (comparator.compareStrings(s4, s3)) {
                    result = true;
                    break;
                }
            }
            list3.reset();
        }
        else if (3 == type) {
            final XMLString s3 = obj2.xstr();
            final DTMIterator list3 = this.iterRaw();
            int node4;
            while (-1 != (node4 = list3.nextNode())) {
                final XMLString s4 = this.getStringFromNode(node4);
                if (comparator.compareStrings(s4, s3)) {
                    result = true;
                    break;
                }
            }
            list3.reset();
        }
        else {
            result = comparator.compareNumbers(this.num(), obj2.num());
        }
        return result;
    }
    
    @Override
    public boolean lessThan(final XObject obj2) throws TransformerException {
        return this.compare(obj2, XNodeSet.S_LT);
    }
    
    @Override
    public boolean lessThanOrEqual(final XObject obj2) throws TransformerException {
        return this.compare(obj2, XNodeSet.S_LTE);
    }
    
    @Override
    public boolean greaterThan(final XObject obj2) throws TransformerException {
        return this.compare(obj2, XNodeSet.S_GT);
    }
    
    @Override
    public boolean greaterThanOrEqual(final XObject obj2) throws TransformerException {
        return this.compare(obj2, XNodeSet.S_GTE);
    }
    
    @Override
    public boolean equals(final XObject obj2) {
        try {
            return this.compare(obj2, XNodeSet.S_EQ);
        }
        catch (final TransformerException te) {
            throw new WrappedRuntimeException(te);
        }
    }
    
    @Override
    public boolean notEquals(final XObject obj2) throws TransformerException {
        return this.compare(obj2, XNodeSet.S_NEQ);
    }
    
    static {
        S_LT = new LessThanComparator();
        S_LTE = new LessThanOrEqualComparator();
        S_GT = new GreaterThanComparator();
        S_GTE = new GreaterThanOrEqualComparator();
        S_EQ = new EqualComparator();
        S_NEQ = new NotEqualComparator();
    }
}
