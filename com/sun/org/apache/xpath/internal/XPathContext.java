package com.sun.org.apache.xpath.internal;

import javax.xml.transform.TransformerException;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xml.internal.utils.QName;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xpath.internal.objects.XString;
import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeIterator;
import org.w3c.dom.traversal.NodeIterator;
import java.util.Iterator;
import com.sun.org.apache.xpath.internal.objects.DTMXRTreeFrag;
import com.sun.org.apache.xalan.internal.extensions.ExpressionContext;
import com.sun.org.apache.xpath.internal.axes.SubContextList;
import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import com.sun.org.apache.xml.internal.utils.DefaultErrorHandler;
import javax.xml.transform.SourceLocator;
import java.util.Enumeration;
import com.sun.org.apache.xpath.internal.objects.XMLStringFactoryImpl;
import com.sun.org.apache.xpath.internal.axes.OneStepIteratorForward;
import com.sun.org.apache.xml.internal.dtm.DTMFilter;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import org.w3c.dom.Node;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import javax.xml.transform.Source;
import com.sun.org.apache.xml.internal.utils.NodeVector;
import java.util.Stack;
import org.xml.sax.XMLReader;
import javax.xml.transform.URIResolver;
import javax.xml.transform.ErrorListener;
import java.lang.reflect.Method;
import com.sun.org.apache.xml.internal.utils.ObjectStack;
import java.util.HashMap;
import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2RTFDTM;
import java.util.Vector;
import com.sun.org.apache.xml.internal.utils.IntStack;
import com.sun.org.apache.xml.internal.dtm.DTMManager;

public class XPathContext extends DTMManager
{
    IntStack m_last_pushed_rtfdtm;
    private Vector m_rtfdtm_stack;
    private int m_which_rtfdtm;
    private SAX2RTFDTM m_global_rtfdtm;
    private HashMap m_DTMXRTreeFrags;
    private boolean m_isSecureProcessing;
    private boolean m_overrideDefaultParser;
    protected DTMManager m_dtmManager;
    ObjectStack m_saxLocations;
    private Object m_owner;
    private Method m_ownerGetErrorListener;
    private VariableStack m_variableStacks;
    private SourceTreeManager m_sourceTreeManager;
    private ErrorListener m_errorListener;
    private ErrorListener m_defaultErrorListener;
    private URIResolver m_uriResolver;
    public XMLReader m_primaryReader;
    private Stack m_contextNodeLists;
    public static final int RECURSIONLIMIT = 4096;
    private IntStack m_currentNodes;
    private NodeVector m_iteratorRoots;
    private NodeVector m_predicateRoots;
    private IntStack m_currentExpressionNodes;
    private IntStack m_predicatePos;
    private ObjectStack m_prefixResolvers;
    private Stack m_axesIteratorStack;
    XPathExpressionContext expressionContext;
    
    public DTMManager getDTMManager() {
        return this.m_dtmManager;
    }
    
    public void setSecureProcessing(final boolean flag) {
        this.m_isSecureProcessing = flag;
    }
    
    public boolean isSecureProcessing() {
        return this.m_isSecureProcessing;
    }
    
    @Override
    public DTM getDTM(final Source source, final boolean unique, final DTMWSFilter wsfilter, final boolean incremental, final boolean doIndexing) {
        return this.m_dtmManager.getDTM(source, unique, wsfilter, incremental, doIndexing);
    }
    
    @Override
    public DTM getDTM(final int nodeHandle) {
        return this.m_dtmManager.getDTM(nodeHandle);
    }
    
    @Override
    public int getDTMHandleFromNode(final Node node) {
        return this.m_dtmManager.getDTMHandleFromNode(node);
    }
    
    @Override
    public int getDTMIdentity(final DTM dtm) {
        return this.m_dtmManager.getDTMIdentity(dtm);
    }
    
    @Override
    public DTM createDocumentFragment() {
        return this.m_dtmManager.createDocumentFragment();
    }
    
    @Override
    public boolean release(final DTM dtm, final boolean shouldHardDelete) {
        return (this.m_rtfdtm_stack == null || !this.m_rtfdtm_stack.contains(dtm)) && this.m_dtmManager.release(dtm, shouldHardDelete);
    }
    
    @Override
    public DTMIterator createDTMIterator(final Object xpathCompiler, final int pos) {
        return this.m_dtmManager.createDTMIterator(xpathCompiler, pos);
    }
    
    @Override
    public DTMIterator createDTMIterator(final String xpathString, final PrefixResolver presolver) {
        return this.m_dtmManager.createDTMIterator(xpathString, presolver);
    }
    
    @Override
    public DTMIterator createDTMIterator(final int whatToShow, final DTMFilter filter, final boolean entityReferenceExpansion) {
        return this.m_dtmManager.createDTMIterator(whatToShow, filter, entityReferenceExpansion);
    }
    
    @Override
    public DTMIterator createDTMIterator(final int node) {
        final DTMIterator iter = new OneStepIteratorForward(13);
        iter.setRoot(node, this);
        return iter;
    }
    
    public XPathContext() {
        this(false);
    }
    
    public XPathContext(final boolean overrideDefaultParser) {
        this.m_last_pushed_rtfdtm = new IntStack();
        this.m_rtfdtm_stack = null;
        this.m_which_rtfdtm = -1;
        this.m_global_rtfdtm = null;
        this.m_DTMXRTreeFrags = null;
        this.m_isSecureProcessing = false;
        this.m_dtmManager = null;
        this.m_saxLocations = new ObjectStack(4096);
        this.m_variableStacks = new VariableStack();
        this.m_sourceTreeManager = new SourceTreeManager();
        this.m_contextNodeLists = new Stack();
        this.m_currentNodes = new IntStack(4096);
        this.m_iteratorRoots = new NodeVector();
        this.m_predicateRoots = new NodeVector();
        this.m_currentExpressionNodes = new IntStack(4096);
        this.m_predicatePos = new IntStack();
        this.m_prefixResolvers = new ObjectStack(4096);
        this.m_axesIteratorStack = new Stack();
        this.expressionContext = new XPathExpressionContext();
        this.init(overrideDefaultParser);
    }
    
    public XPathContext(final Object owner) {
        this.m_last_pushed_rtfdtm = new IntStack();
        this.m_rtfdtm_stack = null;
        this.m_which_rtfdtm = -1;
        this.m_global_rtfdtm = null;
        this.m_DTMXRTreeFrags = null;
        this.m_isSecureProcessing = false;
        this.m_dtmManager = null;
        this.m_saxLocations = new ObjectStack(4096);
        this.m_variableStacks = new VariableStack();
        this.m_sourceTreeManager = new SourceTreeManager();
        this.m_contextNodeLists = new Stack();
        this.m_currentNodes = new IntStack(4096);
        this.m_iteratorRoots = new NodeVector();
        this.m_predicateRoots = new NodeVector();
        this.m_currentExpressionNodes = new IntStack(4096);
        this.m_predicatePos = new IntStack();
        this.m_prefixResolvers = new ObjectStack(4096);
        this.m_axesIteratorStack = new Stack();
        this.expressionContext = new XPathExpressionContext();
        this.m_owner = owner;
        try {
            this.m_ownerGetErrorListener = this.m_owner.getClass().getMethod("getErrorListener", (Class<?>[])new Class[0]);
        }
        catch (final NoSuchMethodException ex) {}
        this.init(false);
    }
    
    private void init(final boolean overrideDefaultParser) {
        this.m_prefixResolvers.push(null);
        this.m_currentNodes.push(-1);
        this.m_currentExpressionNodes.push(-1);
        this.m_saxLocations.push(null);
        this.m_overrideDefaultParser = overrideDefaultParser;
        this.m_dtmManager = DTMManager.newInstance(XMLStringFactoryImpl.getFactory());
    }
    
    public void reset() {
        this.releaseDTMXRTreeFrags();
        if (this.m_rtfdtm_stack != null) {
            final Enumeration e = this.m_rtfdtm_stack.elements();
            while (e.hasMoreElements()) {
                this.m_dtmManager.release(e.nextElement(), true);
            }
        }
        this.m_rtfdtm_stack = null;
        this.m_which_rtfdtm = -1;
        if (this.m_global_rtfdtm != null) {
            this.m_dtmManager.release(this.m_global_rtfdtm, true);
        }
        this.m_global_rtfdtm = null;
        this.m_dtmManager = DTMManager.newInstance(XMLStringFactoryImpl.getFactory());
        this.m_saxLocations.removeAllElements();
        this.m_axesIteratorStack.removeAllElements();
        this.m_contextNodeLists.removeAllElements();
        this.m_currentExpressionNodes.removeAllElements();
        this.m_currentNodes.removeAllElements();
        this.m_iteratorRoots.RemoveAllNoClear();
        this.m_predicatePos.removeAllElements();
        this.m_predicateRoots.RemoveAllNoClear();
        this.m_prefixResolvers.removeAllElements();
        this.m_prefixResolvers.push(null);
        this.m_currentNodes.push(-1);
        this.m_currentExpressionNodes.push(-1);
        this.m_saxLocations.push(null);
    }
    
    public void setSAXLocator(final SourceLocator location) {
        this.m_saxLocations.setTop(location);
    }
    
    public void pushSAXLocator(final SourceLocator location) {
        this.m_saxLocations.push(location);
    }
    
    public void pushSAXLocatorNull() {
        this.m_saxLocations.push(null);
    }
    
    public void popSAXLocator() {
        this.m_saxLocations.pop();
    }
    
    public SourceLocator getSAXLocator() {
        return (SourceLocator)this.m_saxLocations.peek();
    }
    
    public Object getOwnerObject() {
        return this.m_owner;
    }
    
    public final VariableStack getVarStack() {
        return this.m_variableStacks;
    }
    
    public final void setVarStack(final VariableStack varStack) {
        this.m_variableStacks = varStack;
    }
    
    public final SourceTreeManager getSourceTreeManager() {
        return this.m_sourceTreeManager;
    }
    
    public void setSourceTreeManager(final SourceTreeManager mgr) {
        this.m_sourceTreeManager = mgr;
    }
    
    public final ErrorListener getErrorListener() {
        if (null != this.m_errorListener) {
            return this.m_errorListener;
        }
        ErrorListener retval = null;
        try {
            if (null != this.m_ownerGetErrorListener) {
                retval = (ErrorListener)this.m_ownerGetErrorListener.invoke(this.m_owner, new Object[0]);
            }
        }
        catch (final Exception ex) {}
        if (null == retval) {
            if (null == this.m_defaultErrorListener) {
                this.m_defaultErrorListener = new DefaultErrorHandler();
            }
            retval = this.m_defaultErrorListener;
        }
        return retval;
    }
    
    public void setErrorListener(final ErrorListener listener) throws IllegalArgumentException {
        if (listener == null) {
            throw new IllegalArgumentException(XPATHMessages.createXPATHMessage("ER_NULL_ERROR_HANDLER", null));
        }
        this.m_errorListener = listener;
    }
    
    public final URIResolver getURIResolver() {
        return this.m_uriResolver;
    }
    
    public void setURIResolver(final URIResolver resolver) {
        this.m_uriResolver = resolver;
    }
    
    public final XMLReader getPrimaryReader() {
        return this.m_primaryReader;
    }
    
    public void setPrimaryReader(final XMLReader reader) {
        this.m_primaryReader = reader;
    }
    
    public Stack getContextNodeListsStack() {
        return this.m_contextNodeLists;
    }
    
    public void setContextNodeListsStack(final Stack s) {
        this.m_contextNodeLists = s;
    }
    
    public final DTMIterator getContextNodeList() {
        if (this.m_contextNodeLists.size() > 0) {
            return this.m_contextNodeLists.peek();
        }
        return null;
    }
    
    public final void pushContextNodeList(final DTMIterator nl) {
        this.m_contextNodeLists.push(nl);
    }
    
    public final void popContextNodeList() {
        if (this.m_contextNodeLists.isEmpty()) {
            System.err.println("Warning: popContextNodeList when stack is empty!");
        }
        else {
            this.m_contextNodeLists.pop();
        }
    }
    
    public IntStack getCurrentNodeStack() {
        return this.m_currentNodes;
    }
    
    public void setCurrentNodeStack(final IntStack nv) {
        this.m_currentNodes = nv;
    }
    
    public final int getCurrentNode() {
        return this.m_currentNodes.peek();
    }
    
    public final void pushCurrentNodeAndExpression(final int cn, final int en) {
        this.m_currentNodes.push(cn);
        this.m_currentExpressionNodes.push(cn);
    }
    
    public final void popCurrentNodeAndExpression() {
        this.m_currentNodes.quickPop(1);
        this.m_currentExpressionNodes.quickPop(1);
    }
    
    public final void pushExpressionState(final int cn, final int en, final PrefixResolver nc) {
        this.m_currentNodes.push(cn);
        this.m_currentExpressionNodes.push(cn);
        this.m_prefixResolvers.push(nc);
    }
    
    public final void popExpressionState() {
        this.m_currentNodes.quickPop(1);
        this.m_currentExpressionNodes.quickPop(1);
        this.m_prefixResolvers.pop();
    }
    
    public final void pushCurrentNode(final int n) {
        this.m_currentNodes.push(n);
    }
    
    public final void popCurrentNode() {
        this.m_currentNodes.quickPop(1);
    }
    
    public final void pushPredicateRoot(final int n) {
        this.m_predicateRoots.push(n);
    }
    
    public final void popPredicateRoot() {
        this.m_predicateRoots.popQuick();
    }
    
    public final int getPredicateRoot() {
        return this.m_predicateRoots.peepOrNull();
    }
    
    public final void pushIteratorRoot(final int n) {
        this.m_iteratorRoots.push(n);
    }
    
    public final void popIteratorRoot() {
        this.m_iteratorRoots.popQuick();
    }
    
    public final int getIteratorRoot() {
        return this.m_iteratorRoots.peepOrNull();
    }
    
    public IntStack getCurrentExpressionNodeStack() {
        return this.m_currentExpressionNodes;
    }
    
    public void setCurrentExpressionNodeStack(final IntStack nv) {
        this.m_currentExpressionNodes = nv;
    }
    
    public final int getPredicatePos() {
        return this.m_predicatePos.peek();
    }
    
    public final void pushPredicatePos(final int n) {
        this.m_predicatePos.push(n);
    }
    
    public final void popPredicatePos() {
        this.m_predicatePos.pop();
    }
    
    public final int getCurrentExpressionNode() {
        return this.m_currentExpressionNodes.peek();
    }
    
    public final void pushCurrentExpressionNode(final int n) {
        this.m_currentExpressionNodes.push(n);
    }
    
    public final void popCurrentExpressionNode() {
        this.m_currentExpressionNodes.quickPop(1);
    }
    
    public final PrefixResolver getNamespaceContext() {
        return (PrefixResolver)this.m_prefixResolvers.peek();
    }
    
    public final void setNamespaceContext(final PrefixResolver pr) {
        this.m_prefixResolvers.setTop(pr);
    }
    
    public final void pushNamespaceContext(final PrefixResolver pr) {
        this.m_prefixResolvers.push(pr);
    }
    
    public final void pushNamespaceContextNull() {
        this.m_prefixResolvers.push(null);
    }
    
    public final void popNamespaceContext() {
        this.m_prefixResolvers.pop();
    }
    
    public Stack getAxesIteratorStackStacks() {
        return this.m_axesIteratorStack;
    }
    
    public void setAxesIteratorStackStacks(final Stack s) {
        this.m_axesIteratorStack = s;
    }
    
    public final void pushSubContextList(final SubContextList iter) {
        this.m_axesIteratorStack.push(iter);
    }
    
    public final void popSubContextList() {
        this.m_axesIteratorStack.pop();
    }
    
    public SubContextList getSubContextList() {
        return this.m_axesIteratorStack.isEmpty() ? null : this.m_axesIteratorStack.peek();
    }
    
    public SubContextList getCurrentNodeList() {
        return this.m_axesIteratorStack.isEmpty() ? null : ((SubContextList)this.m_axesIteratorStack.elementAt(0));
    }
    
    public final int getContextNode() {
        return this.getCurrentNode();
    }
    
    public final DTMIterator getContextNodes() {
        try {
            final DTMIterator cnl = this.getContextNodeList();
            if (null != cnl) {
                return cnl.cloneWithReset();
            }
            return null;
        }
        catch (final CloneNotSupportedException cnse) {
            return null;
        }
    }
    
    public ExpressionContext getExpressionContext() {
        return this.expressionContext;
    }
    
    public DTM getGlobalRTFDTM() {
        if (this.m_global_rtfdtm == null || this.m_global_rtfdtm.isTreeIncomplete()) {
            this.m_global_rtfdtm = (SAX2RTFDTM)this.m_dtmManager.getDTM(null, true, null, false, false);
        }
        return this.m_global_rtfdtm;
    }
    
    public DTM getRTFDTM() {
        SAX2RTFDTM rtfdtm;
        if (this.m_rtfdtm_stack == null) {
            this.m_rtfdtm_stack = new Vector();
            rtfdtm = (SAX2RTFDTM)this.m_dtmManager.getDTM(null, true, null, false, false);
            this.m_rtfdtm_stack.addElement(rtfdtm);
            ++this.m_which_rtfdtm;
        }
        else if (this.m_which_rtfdtm < 0) {
            rtfdtm = this.m_rtfdtm_stack.elementAt(++this.m_which_rtfdtm);
        }
        else {
            rtfdtm = this.m_rtfdtm_stack.elementAt(this.m_which_rtfdtm);
            if (rtfdtm.isTreeIncomplete()) {
                if (++this.m_which_rtfdtm < this.m_rtfdtm_stack.size()) {
                    rtfdtm = this.m_rtfdtm_stack.elementAt(this.m_which_rtfdtm);
                }
                else {
                    rtfdtm = (SAX2RTFDTM)this.m_dtmManager.getDTM(null, true, null, false, false);
                    this.m_rtfdtm_stack.addElement(rtfdtm);
                }
            }
        }
        return rtfdtm;
    }
    
    public void pushRTFContext() {
        this.m_last_pushed_rtfdtm.push(this.m_which_rtfdtm);
        if (null != this.m_rtfdtm_stack) {
            ((SAX2RTFDTM)this.getRTFDTM()).pushRewindMark();
        }
    }
    
    public void popRTFContext() {
        final int previous = this.m_last_pushed_rtfdtm.pop();
        if (null == this.m_rtfdtm_stack) {
            return;
        }
        if (this.m_which_rtfdtm == previous) {
            if (previous >= 0) {
                this.m_rtfdtm_stack.elementAt(previous).popRewindMark();
            }
        }
        else {
            while (this.m_which_rtfdtm != previous) {
                final boolean isEmpty = this.m_rtfdtm_stack.elementAt(this.m_which_rtfdtm).popRewindMark();
                --this.m_which_rtfdtm;
            }
        }
    }
    
    public DTMXRTreeFrag getDTMXRTreeFrag(final int dtmIdentity) {
        if (this.m_DTMXRTreeFrags == null) {
            this.m_DTMXRTreeFrags = new HashMap();
        }
        if (this.m_DTMXRTreeFrags.containsKey(new Integer(dtmIdentity))) {
            return this.m_DTMXRTreeFrags.get(new Integer(dtmIdentity));
        }
        final DTMXRTreeFrag frag = new DTMXRTreeFrag(dtmIdentity, this);
        this.m_DTMXRTreeFrags.put(new Integer(dtmIdentity), frag);
        return frag;
    }
    
    private final void releaseDTMXRTreeFrags() {
        if (this.m_DTMXRTreeFrags == null) {
            return;
        }
        final Iterator iter = this.m_DTMXRTreeFrags.values().iterator();
        while (iter.hasNext()) {
            final DTMXRTreeFrag frag = iter.next();
            frag.destruct();
            iter.remove();
        }
        this.m_DTMXRTreeFrags = null;
    }
    
    public class XPathExpressionContext implements ExpressionContext
    {
        @Override
        public XPathContext getXPathContext() {
            return XPathContext.this;
        }
        
        public DTMManager getDTMManager() {
            return XPathContext.this.m_dtmManager;
        }
        
        @Override
        public Node getContextNode() {
            final int context = XPathContext.this.getCurrentNode();
            return XPathContext.this.getDTM(context).getNode(context);
        }
        
        @Override
        public NodeIterator getContextNodes() {
            return new DTMNodeIterator(XPathContext.this.getContextNodeList());
        }
        
        @Override
        public ErrorListener getErrorListener() {
            return XPathContext.this.getErrorListener();
        }
        
        public boolean overrideDefaultParser() {
            return XPathContext.this.m_overrideDefaultParser;
        }
        
        public void setOverrideDefaultParser(final boolean flag) {
            XPathContext.this.m_overrideDefaultParser = flag;
        }
        
        @Override
        public double toNumber(final Node n) {
            final int nodeHandle = XPathContext.this.getDTMHandleFromNode(n);
            final DTM dtm = XPathContext.this.getDTM(nodeHandle);
            final XString xobj = (XString)dtm.getStringValue(nodeHandle);
            return xobj.num();
        }
        
        @Override
        public String toString(final Node n) {
            final int nodeHandle = XPathContext.this.getDTMHandleFromNode(n);
            final DTM dtm = XPathContext.this.getDTM(nodeHandle);
            final XMLString strVal = dtm.getStringValue(nodeHandle);
            return strVal.toString();
        }
        
        @Override
        public final XObject getVariableOrParam(final QName qname) throws TransformerException {
            return XPathContext.this.m_variableStacks.getVariableOrParam(XPathContext.this, qname);
        }
    }
}
