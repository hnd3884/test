package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xpath.internal.patterns.NodeTest;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xml.internal.dtm.DTMFilter;
import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import java.io.IOException;
import java.io.ObjectInputStream;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xml.internal.dtm.DTM;
import java.io.Serializable;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;

public abstract class LocPathIterator extends PredicatedNodeTest implements Cloneable, DTMIterator, Serializable, PathComponent
{
    static final long serialVersionUID = -4602476357268405754L;
    protected boolean m_allowDetach;
    protected transient IteratorPool m_clones;
    protected transient DTM m_cdtm;
    transient int m_stackFrame;
    private boolean m_isTopLevel;
    public transient int m_lastFetched;
    protected transient int m_context;
    protected transient int m_currentContextNode;
    protected transient int m_pos;
    protected transient int m_length;
    private PrefixResolver m_prefixResolver;
    protected transient XPathContext m_execContext;
    
    protected LocPathIterator() {
        this.m_allowDetach = true;
        this.m_clones = new IteratorPool(this);
        this.m_stackFrame = -1;
        this.m_isTopLevel = false;
        this.m_lastFetched = -1;
        this.m_context = -1;
        this.m_currentContextNode = -1;
        this.m_pos = 0;
        this.m_length = -1;
    }
    
    protected LocPathIterator(final PrefixResolver nscontext) {
        this.m_allowDetach = true;
        this.m_clones = new IteratorPool(this);
        this.m_stackFrame = -1;
        this.m_isTopLevel = false;
        this.m_lastFetched = -1;
        this.m_context = -1;
        this.m_currentContextNode = -1;
        this.m_pos = 0;
        this.m_length = -1;
        this.setLocPathIterator(this);
        this.m_prefixResolver = nscontext;
    }
    
    protected LocPathIterator(final Compiler compiler, final int opPos, final int analysis) throws TransformerException {
        this(compiler, opPos, analysis, true);
    }
    
    protected LocPathIterator(final Compiler compiler, final int opPos, final int analysis, final boolean shouldLoadWalkers) throws TransformerException {
        this.m_allowDetach = true;
        this.m_clones = new IteratorPool(this);
        this.m_stackFrame = -1;
        this.m_isTopLevel = false;
        this.m_lastFetched = -1;
        this.m_context = -1;
        this.m_currentContextNode = -1;
        this.m_pos = 0;
        this.m_length = -1;
        this.setLocPathIterator(this);
    }
    
    @Override
    public int getAnalysisBits() {
        final int axis = this.getAxis();
        final int bit = WalkerFactory.getAnalysisBitFromAxes(axis);
        return bit;
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, TransformerException {
        try {
            stream.defaultReadObject();
            this.m_clones = new IteratorPool(this);
        }
        catch (final ClassNotFoundException cnfe) {
            throw new TransformerException(cnfe);
        }
    }
    
    public void setEnvironment(final Object environment) {
    }
    
    @Override
    public DTM getDTM(final int nodeHandle) {
        return this.m_execContext.getDTM(nodeHandle);
    }
    
    @Override
    public DTMManager getDTMManager() {
        return this.m_execContext.getDTMManager();
    }
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        final XNodeSet iter = new XNodeSet(this.m_clones.getInstance());
        iter.setRoot(xctxt.getCurrentNode(), xctxt);
        return iter;
    }
    
    @Override
    public void executeCharsToContentHandler(final XPathContext xctxt, final ContentHandler handler) throws TransformerException, SAXException {
        final LocPathIterator clone = (LocPathIterator)this.m_clones.getInstance();
        final int current = xctxt.getCurrentNode();
        clone.setRoot(current, xctxt);
        final int node = clone.nextNode();
        final DTM dtm = clone.getDTM(node);
        clone.detach();
        if (node != -1) {
            dtm.dispatchCharactersEvents(node, handler, false);
        }
    }
    
    @Override
    public DTMIterator asIterator(final XPathContext xctxt, final int contextNode) throws TransformerException {
        final XNodeSet iter = new XNodeSet(this.m_clones.getInstance());
        iter.setRoot(contextNode, xctxt);
        return iter;
    }
    
    @Override
    public boolean isNodesetExpr() {
        return true;
    }
    
    @Override
    public int asNode(final XPathContext xctxt) throws TransformerException {
        final DTMIterator iter = this.m_clones.getInstance();
        final int current = xctxt.getCurrentNode();
        iter.setRoot(current, xctxt);
        final int next = iter.nextNode();
        iter.detach();
        return next;
    }
    
    @Override
    public boolean bool(final XPathContext xctxt) throws TransformerException {
        return this.asNode(xctxt) != -1;
    }
    
    public void setIsTopLevel(final boolean b) {
        this.m_isTopLevel = b;
    }
    
    public boolean getIsTopLevel() {
        return this.m_isTopLevel;
    }
    
    @Override
    public void setRoot(final int context, final Object environment) {
        this.m_context = context;
        final XPathContext xctxt = (XPathContext)environment;
        this.m_execContext = xctxt;
        this.m_cdtm = xctxt.getDTM(context);
        this.m_currentContextNode = context;
        if (null == this.m_prefixResolver) {
            this.m_prefixResolver = xctxt.getNamespaceContext();
        }
        this.m_lastFetched = -1;
        this.m_foundLast = false;
        this.m_pos = 0;
        this.m_length = -1;
        if (this.m_isTopLevel) {
            this.m_stackFrame = xctxt.getVarStack().getStackFrame();
        }
    }
    
    protected void setNextPosition(final int next) {
        this.assertion(false, "setNextPosition not supported in this iterator!");
    }
    
    @Override
    public final int getCurrentPos() {
        return this.m_pos;
    }
    
    @Override
    public void setShouldCacheNodes(final boolean b) {
        this.assertion(false, "setShouldCacheNodes not supported by this iterater!");
    }
    
    @Override
    public boolean isMutable() {
        return false;
    }
    
    @Override
    public void setCurrentPos(final int i) {
        this.assertion(false, "setCurrentPos not supported by this iterator!");
    }
    
    public void incrementCurrentPos() {
        ++this.m_pos;
    }
    
    public int size() {
        this.assertion(false, "size() not supported by this iterator!");
        return 0;
    }
    
    @Override
    public int item(final int index) {
        this.assertion(false, "item(int index) not supported by this iterator!");
        return 0;
    }
    
    @Override
    public void setItem(final int node, final int index) {
        this.assertion(false, "setItem not supported by this iterator!");
    }
    
    @Override
    public int getLength() {
        final boolean isPredicateTest = this == this.m_execContext.getSubContextList();
        final int predCount = this.getPredicateCount();
        if (-1 != this.m_length && isPredicateTest && this.m_predicateIndex < 1) {
            return this.m_length;
        }
        if (this.m_foundLast) {
            return this.m_pos;
        }
        int pos = (this.m_predicateIndex >= 0) ? this.getProximityPosition() : this.m_pos;
        LocPathIterator clone;
        try {
            clone = (LocPathIterator)this.clone();
        }
        catch (final CloneNotSupportedException cnse) {
            return -1;
        }
        if (predCount > 0 && isPredicateTest) {
            clone.m_predCount = this.m_predicateIndex;
        }
        int next;
        while (-1 != (next = clone.nextNode())) {
            ++pos;
        }
        if (isPredicateTest && this.m_predicateIndex < 1) {
            this.m_length = pos;
        }
        return pos;
    }
    
    @Override
    public boolean isFresh() {
        return this.m_pos == 0;
    }
    
    @Override
    public int previousNode() {
        throw new RuntimeException(XPATHMessages.createXPATHMessage("ER_NODESETDTM_CANNOT_ITERATE", null));
    }
    
    @Override
    public int getWhatToShow() {
        return -17;
    }
    
    public DTMFilter getFilter() {
        return null;
    }
    
    @Override
    public int getRoot() {
        return this.m_context;
    }
    
    @Override
    public boolean getExpandEntityReferences() {
        return true;
    }
    
    @Override
    public void allowDetachToRelease(final boolean allowRelease) {
        this.m_allowDetach = allowRelease;
    }
    
    @Override
    public void detach() {
        if (this.m_allowDetach) {
            this.m_execContext = null;
            this.m_cdtm = null;
            this.m_length = -1;
            this.m_pos = 0;
            this.m_lastFetched = -1;
            this.m_context = -1;
            this.m_currentContextNode = -1;
            this.m_clones.freeInstance(this);
        }
    }
    
    @Override
    public void reset() {
        this.assertion(false, "This iterator can not reset!");
    }
    
    @Override
    public DTMIterator cloneWithReset() throws CloneNotSupportedException {
        final LocPathIterator clone = (LocPathIterator)this.m_clones.getInstanceOrThrow();
        clone.m_execContext = this.m_execContext;
        clone.m_cdtm = this.m_cdtm;
        clone.m_context = this.m_context;
        clone.m_currentContextNode = this.m_currentContextNode;
        clone.m_stackFrame = this.m_stackFrame;
        return clone;
    }
    
    @Override
    public abstract int nextNode();
    
    protected int returnNextNode(final int nextNode) {
        if (-1 != nextNode) {
            ++this.m_pos;
        }
        if (-1 == (this.m_lastFetched = nextNode)) {
            this.m_foundLast = true;
        }
        return nextNode;
    }
    
    @Override
    public int getCurrentNode() {
        return this.m_lastFetched;
    }
    
    @Override
    public void runTo(final int index) {
        if (this.m_foundLast || (index >= 0 && index <= this.getCurrentPos())) {
            return;
        }
        if (-1 == index) {
            int n;
            while (-1 != (n = this.nextNode())) {}
        }
        else {
            int n;
            while (-1 != (n = this.nextNode()) && this.getCurrentPos() < index) {}
        }
    }
    
    public final boolean getFoundLast() {
        return this.m_foundLast;
    }
    
    public final XPathContext getXPathContext() {
        return this.m_execContext;
    }
    
    public final int getContext() {
        return this.m_context;
    }
    
    public final int getCurrentContextNode() {
        return this.m_currentContextNode;
    }
    
    public final void setCurrentContextNode(final int n) {
        this.m_currentContextNode = n;
    }
    
    public final PrefixResolver getPrefixResolver() {
        if (null == this.m_prefixResolver) {
            this.m_prefixResolver = (PrefixResolver)this.getExpressionOwner();
        }
        return this.m_prefixResolver;
    }
    
    @Override
    public void callVisitors(final ExpressionOwner owner, final XPathVisitor visitor) {
        if (visitor.visitLocationPath(owner, this)) {
            visitor.visitStep(owner, this);
            this.callPredicateVisitors(visitor);
        }
    }
    
    @Override
    public boolean isDocOrdered() {
        return true;
    }
    
    @Override
    public int getAxis() {
        return -1;
    }
    
    @Override
    public int getLastPos(final XPathContext xctxt) {
        return this.getLength();
    }
}
