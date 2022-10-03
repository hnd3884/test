package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.dtm.DTMException;
import com.sun.org.apache.xml.internal.res.XMLMessages;
import com.sun.org.apache.xml.internal.dtm.Axis;
import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import javax.xml.transform.Source;
import com.sun.org.apache.xml.internal.dtm.DTMManager;

public abstract class DTMDefaultBaseTraversers extends DTMDefaultBase
{
    public DTMDefaultBaseTraversers(final DTMManager mgr, final Source source, final int dtmIdentity, final DTMWSFilter whiteSpaceFilter, final XMLStringFactory xstringfactory, final boolean doIndexing) {
        super(mgr, source, dtmIdentity, whiteSpaceFilter, xstringfactory, doIndexing);
    }
    
    public DTMDefaultBaseTraversers(final DTMManager mgr, final Source source, final int dtmIdentity, final DTMWSFilter whiteSpaceFilter, final XMLStringFactory xstringfactory, final boolean doIndexing, final int blocksize, final boolean usePrevsib, final boolean newNameTable) {
        super(mgr, source, dtmIdentity, whiteSpaceFilter, xstringfactory, doIndexing, blocksize, usePrevsib, newNameTable);
    }
    
    @Override
    public DTMAxisTraverser getAxisTraverser(final int axis) {
        DTMAxisTraverser traverser;
        if (null == this.m_traversers) {
            this.m_traversers = new DTMAxisTraverser[Axis.getNamesLength()];
            traverser = null;
        }
        else {
            traverser = this.m_traversers[axis];
            if (traverser != null) {
                return traverser;
            }
        }
        switch (axis) {
            case 0: {
                traverser = new AncestorTraverser();
                break;
            }
            case 1: {
                traverser = new AncestorOrSelfTraverser();
                break;
            }
            case 2: {
                traverser = new AttributeTraverser();
                break;
            }
            case 3: {
                traverser = new ChildTraverser();
                break;
            }
            case 4: {
                traverser = new DescendantTraverser();
                break;
            }
            case 5: {
                traverser = new DescendantOrSelfTraverser();
                break;
            }
            case 6: {
                traverser = new FollowingTraverser();
                break;
            }
            case 7: {
                traverser = new FollowingSiblingTraverser();
                break;
            }
            case 9: {
                traverser = new NamespaceTraverser();
                break;
            }
            case 8: {
                traverser = new NamespaceDeclsTraverser();
                break;
            }
            case 10: {
                traverser = new ParentTraverser();
                break;
            }
            case 11: {
                traverser = new PrecedingTraverser();
                break;
            }
            case 12: {
                traverser = new PrecedingSiblingTraverser();
                break;
            }
            case 13: {
                traverser = new SelfTraverser();
                break;
            }
            case 16: {
                traverser = new AllFromRootTraverser();
                break;
            }
            case 14: {
                traverser = new AllFromNodeTraverser();
                break;
            }
            case 15: {
                traverser = new PrecedingAndAncestorTraverser();
                break;
            }
            case 17: {
                traverser = new DescendantFromRootTraverser();
                break;
            }
            case 18: {
                traverser = new DescendantOrSelfFromRootTraverser();
                break;
            }
            case 19: {
                traverser = new RootTraverser();
                break;
            }
            case 20: {
                return null;
            }
            default: {
                throw new DTMException(XMLMessages.createXMLMessage("ER_UNKNOWN_AXIS_TYPE", new Object[] { Integer.toString(axis) }));
            }
        }
        if (null == traverser) {
            throw new DTMException(XMLMessages.createXMLMessage("ER_AXIS_TRAVERSER_NOT_SUPPORTED", new Object[] { Axis.getNames(axis) }));
        }
        return this.m_traversers[axis] = traverser;
    }
    
    private class AncestorTraverser extends DTMAxisTraverser
    {
        @Override
        public int next(final int context, final int current) {
            return DTMDefaultBaseTraversers.this.getParent(current);
        }
        
        @Override
        public int next(final int context, int current, final int expandedTypeID) {
            current = DTMDefaultBaseTraversers.this.makeNodeIdentity(current);
            while (-1 != (current = DTMDefaultBaseTraversers.this.m_parent.elementAt(current))) {
                if (DTMDefaultBaseTraversers.this.m_exptype.elementAt(current) == expandedTypeID) {
                    return DTMDefaultBaseTraversers.this.makeNodeHandle(current);
                }
            }
            return -1;
        }
    }
    
    private class AncestorOrSelfTraverser extends AncestorTraverser
    {
        @Override
        public int first(final int context) {
            return context;
        }
        
        @Override
        public int first(final int context, final int expandedTypeID) {
            return (DTMDefaultBaseTraversers.this.getExpandedTypeID(context) == expandedTypeID) ? context : this.next(context, context, expandedTypeID);
        }
    }
    
    private class AttributeTraverser extends DTMAxisTraverser
    {
        @Override
        public int next(final int context, final int current) {
            return (context == current) ? DTMDefaultBaseTraversers.this.getFirstAttribute(context) : DTMDefaultBaseTraversers.this.getNextAttribute(current);
        }
        
        @Override
        public int next(final int context, int current, final int expandedTypeID) {
            current = ((context == current) ? DTMDefaultBaseTraversers.this.getFirstAttribute(context) : DTMDefaultBaseTraversers.this.getNextAttribute(current));
            while (DTMDefaultBaseTraversers.this.getExpandedTypeID(current) != expandedTypeID) {
                if (-1 == (current = DTMDefaultBaseTraversers.this.getNextAttribute(current))) {
                    return -1;
                }
            }
            return current;
        }
    }
    
    private class ChildTraverser extends DTMAxisTraverser
    {
        protected int getNextIndexed(final int axisRoot, int nextPotential, final int expandedTypeID) {
            final int nsIndex = DTMDefaultBaseTraversers.this.m_expandedNameTable.getNamespaceID(expandedTypeID);
            final int lnIndex = DTMDefaultBaseTraversers.this.m_expandedNameTable.getLocalNameID(expandedTypeID);
            while (true) {
                final int nextID = DTMDefaultBaseTraversers.this.findElementFromIndex(nsIndex, lnIndex, nextPotential);
                if (-2 != nextID) {
                    int parentID = DTMDefaultBaseTraversers.this.m_parent.elementAt(nextID);
                    if (parentID == axisRoot) {
                        return nextID;
                    }
                    if (parentID < axisRoot) {
                        return -1;
                    }
                    do {
                        parentID = DTMDefaultBaseTraversers.this.m_parent.elementAt(parentID);
                        if (parentID < axisRoot) {
                            return -1;
                        }
                    } while (parentID > axisRoot);
                    nextPotential = nextID + 1;
                }
                else {
                    DTMDefaultBaseTraversers.this.nextNode();
                    if (DTMDefaultBaseTraversers.this.m_nextsib.elementAt(axisRoot) != -2) {
                        return -1;
                    }
                    continue;
                }
            }
        }
        
        @Override
        public int first(final int context) {
            return DTMDefaultBaseTraversers.this.getFirstChild(context);
        }
        
        @Override
        public int first(final int context, final int expandedTypeID) {
            final int identity = DTMDefaultBaseTraversers.this.makeNodeIdentity(context);
            final int firstMatch = this.getNextIndexed(identity, DTMDefaultBaseTraversers.this._firstch(identity), expandedTypeID);
            return DTMDefaultBaseTraversers.this.makeNodeHandle(firstMatch);
        }
        
        @Override
        public int next(final int context, final int current) {
            return DTMDefaultBaseTraversers.this.getNextSibling(current);
        }
        
        @Override
        public int next(final int context, int current, final int expandedTypeID) {
            for (current = DTMDefaultBaseTraversers.this._nextsib(DTMDefaultBaseTraversers.this.makeNodeIdentity(current)); -1 != current; current = DTMDefaultBaseTraversers.this._nextsib(current)) {
                if (DTMDefaultBaseTraversers.this.m_exptype.elementAt(current) == expandedTypeID) {
                    return DTMDefaultBaseTraversers.this.makeNodeHandle(current);
                }
            }
            return -1;
        }
    }
    
    private abstract class IndexedDTMAxisTraverser extends DTMAxisTraverser
    {
        protected final boolean isIndexed(final int expandedTypeID) {
            return DTMDefaultBaseTraversers.this.m_indexing && 1 == DTMDefaultBaseTraversers.this.m_expandedNameTable.getType(expandedTypeID);
        }
        
        protected abstract boolean isAfterAxis(final int p0, final int p1);
        
        protected abstract boolean axisHasBeenProcessed(final int p0);
        
        protected int getNextIndexed(final int axisRoot, final int nextPotential, final int expandedTypeID) {
            final int nsIndex = DTMDefaultBaseTraversers.this.m_expandedNameTable.getNamespaceID(expandedTypeID);
            final int lnIndex = DTMDefaultBaseTraversers.this.m_expandedNameTable.getLocalNameID(expandedTypeID);
            while (true) {
                final int next = DTMDefaultBaseTraversers.this.findElementFromIndex(nsIndex, lnIndex, nextPotential);
                if (-2 != next) {
                    if (this.isAfterAxis(axisRoot, next)) {
                        return -1;
                    }
                    return next;
                }
                else {
                    if (this.axisHasBeenProcessed(axisRoot)) {
                        return -1;
                    }
                    DTMDefaultBaseTraversers.this.nextNode();
                }
            }
        }
    }
    
    private class DescendantTraverser extends IndexedDTMAxisTraverser
    {
        protected int getFirstPotential(final int identity) {
            return identity + 1;
        }
        
        @Override
        protected boolean axisHasBeenProcessed(final int axisRoot) {
            return DTMDefaultBaseTraversers.this.m_nextsib.elementAt(axisRoot) != -2;
        }
        
        protected int getSubtreeRoot(final int handle) {
            return DTMDefaultBaseTraversers.this.makeNodeIdentity(handle);
        }
        
        protected boolean isDescendant(final int subtreeRootIdentity, final int identity) {
            return DTMDefaultBaseTraversers.this._parent(identity) >= subtreeRootIdentity;
        }
        
        @Override
        protected boolean isAfterAxis(final int axisRoot, int identity) {
            while (identity != axisRoot) {
                identity = DTMDefaultBaseTraversers.this.m_parent.elementAt(identity);
                if (identity < axisRoot) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public int first(final int context, final int expandedTypeID) {
            if (this.isIndexed(expandedTypeID)) {
                final int identity = this.getSubtreeRoot(context);
                final int firstPotential = this.getFirstPotential(identity);
                return DTMDefaultBaseTraversers.this.makeNodeHandle(this.getNextIndexed(identity, firstPotential, expandedTypeID));
            }
            return this.next(context, context, expandedTypeID);
        }
        
        @Override
        public int next(final int context, int current) {
            final int subtreeRootIdent = this.getSubtreeRoot(context);
            current = DTMDefaultBaseTraversers.this.makeNodeIdentity(current) + 1;
            while (true) {
                final int type = DTMDefaultBaseTraversers.this._type(current);
                if (!this.isDescendant(subtreeRootIdent, current)) {
                    return -1;
                }
                if (2 != type && 13 != type) {
                    return DTMDefaultBaseTraversers.this.makeNodeHandle(current);
                }
                ++current;
            }
        }
        
        @Override
        public int next(final int context, int current, final int expandedTypeID) {
            final int subtreeRootIdent = this.getSubtreeRoot(context);
            current = DTMDefaultBaseTraversers.this.makeNodeIdentity(current) + 1;
            if (this.isIndexed(expandedTypeID)) {
                return DTMDefaultBaseTraversers.this.makeNodeHandle(this.getNextIndexed(subtreeRootIdent, current, expandedTypeID));
            }
            while (true) {
                final int exptype = DTMDefaultBaseTraversers.this._exptype(current);
                if (!this.isDescendant(subtreeRootIdent, current)) {
                    return -1;
                }
                if (exptype == expandedTypeID) {
                    return DTMDefaultBaseTraversers.this.makeNodeHandle(current);
                }
                ++current;
            }
        }
    }
    
    private class DescendantOrSelfTraverser extends DescendantTraverser
    {
        @Override
        protected int getFirstPotential(final int identity) {
            return identity;
        }
        
        @Override
        public int first(final int context) {
            return context;
        }
    }
    
    private class AllFromNodeTraverser extends DescendantOrSelfTraverser
    {
        @Override
        public int next(final int context, int current) {
            final int subtreeRootIdent = DTMDefaultBaseTraversers.this.makeNodeIdentity(context);
            current = DTMDefaultBaseTraversers.this.makeNodeIdentity(current) + 1;
            DTMDefaultBaseTraversers.this._exptype(current);
            if (!this.isDescendant(subtreeRootIdent, current)) {
                return -1;
            }
            return DTMDefaultBaseTraversers.this.makeNodeHandle(current);
        }
    }
    
    private class FollowingTraverser extends DescendantTraverser
    {
        @Override
        public int first(int context) {
            context = DTMDefaultBaseTraversers.this.makeNodeIdentity(context);
            final int type = DTMDefaultBaseTraversers.this._type(context);
            if (2 == type || 13 == type) {
                context = DTMDefaultBaseTraversers.this._parent(context);
                final int first = DTMDefaultBaseTraversers.this._firstch(context);
                if (-1 != first) {
                    return DTMDefaultBaseTraversers.this.makeNodeHandle(first);
                }
            }
            int first;
            do {
                first = DTMDefaultBaseTraversers.this._nextsib(context);
                if (-1 == first) {
                    context = DTMDefaultBaseTraversers.this._parent(context);
                }
            } while (-1 == first && -1 != context);
            return DTMDefaultBaseTraversers.this.makeNodeHandle(first);
        }
        
        @Override
        public int first(int context, final int expandedTypeID) {
            final int type = DTMDefaultBaseTraversers.this.getNodeType(context);
            if (2 == type || 13 == type) {
                context = DTMDefaultBaseTraversers.this.getParent(context);
                final int first = DTMDefaultBaseTraversers.this.getFirstChild(context);
                if (-1 != first) {
                    if (DTMDefaultBaseTraversers.this.getExpandedTypeID(first) == expandedTypeID) {
                        return first;
                    }
                    return this.next(context, first, expandedTypeID);
                }
            }
            int first;
            do {
                first = DTMDefaultBaseTraversers.this.getNextSibling(context);
                if (-1 == first) {
                    context = DTMDefaultBaseTraversers.this.getParent(context);
                }
                else {
                    if (DTMDefaultBaseTraversers.this.getExpandedTypeID(first) == expandedTypeID) {
                        return first;
                    }
                    return this.next(context, first, expandedTypeID);
                }
            } while (-1 == first && -1 != context);
            return first;
        }
        
        @Override
        public int next(final int context, int current) {
            current = DTMDefaultBaseTraversers.this.makeNodeIdentity(current);
            while (true) {
                ++current;
                final int type = DTMDefaultBaseTraversers.this._type(current);
                if (-1 == type) {
                    return -1;
                }
                if (2 == type) {
                    continue;
                }
                if (13 == type) {
                    continue;
                }
                return DTMDefaultBaseTraversers.this.makeNodeHandle(current);
            }
        }
        
        @Override
        public int next(final int context, int current, final int expandedTypeID) {
            current = DTMDefaultBaseTraversers.this.makeNodeIdentity(current);
            while (true) {
                ++current;
                final int etype = DTMDefaultBaseTraversers.this._exptype(current);
                if (-1 == etype) {
                    return -1;
                }
                if (etype != expandedTypeID) {
                    continue;
                }
                return DTMDefaultBaseTraversers.this.makeNodeHandle(current);
            }
        }
    }
    
    private class FollowingSiblingTraverser extends DTMAxisTraverser
    {
        @Override
        public int next(final int context, final int current) {
            return DTMDefaultBaseTraversers.this.getNextSibling(current);
        }
        
        @Override
        public int next(final int context, int current, final int expandedTypeID) {
            while (-1 != (current = DTMDefaultBaseTraversers.this.getNextSibling(current))) {
                if (DTMDefaultBaseTraversers.this.getExpandedTypeID(current) == expandedTypeID) {
                    return current;
                }
            }
            return -1;
        }
    }
    
    private class NamespaceDeclsTraverser extends DTMAxisTraverser
    {
        @Override
        public int next(final int context, final int current) {
            return (context == current) ? DTMDefaultBaseTraversers.this.getFirstNamespaceNode(context, false) : DTMDefaultBaseTraversers.this.getNextNamespaceNode(context, current, false);
        }
        
        @Override
        public int next(final int context, int current, final int expandedTypeID) {
            current = ((context == current) ? DTMDefaultBaseTraversers.this.getFirstNamespaceNode(context, false) : DTMDefaultBaseTraversers.this.getNextNamespaceNode(context, current, false));
            while (DTMDefaultBaseTraversers.this.getExpandedTypeID(current) != expandedTypeID) {
                if (-1 == (current = DTMDefaultBaseTraversers.this.getNextNamespaceNode(context, current, false))) {
                    return -1;
                }
            }
            return current;
        }
    }
    
    private class NamespaceTraverser extends DTMAxisTraverser
    {
        @Override
        public int next(final int context, final int current) {
            return (context == current) ? DTMDefaultBaseTraversers.this.getFirstNamespaceNode(context, true) : DTMDefaultBaseTraversers.this.getNextNamespaceNode(context, current, true);
        }
        
        @Override
        public int next(final int context, int current, final int expandedTypeID) {
            current = ((context == current) ? DTMDefaultBaseTraversers.this.getFirstNamespaceNode(context, true) : DTMDefaultBaseTraversers.this.getNextNamespaceNode(context, current, true));
            while (DTMDefaultBaseTraversers.this.getExpandedTypeID(current) != expandedTypeID) {
                if (-1 == (current = DTMDefaultBaseTraversers.this.getNextNamespaceNode(context, current, true))) {
                    return -1;
                }
            }
            return current;
        }
    }
    
    private class ParentTraverser extends DTMAxisTraverser
    {
        @Override
        public int first(final int context) {
            return DTMDefaultBaseTraversers.this.getParent(context);
        }
        
        @Override
        public int first(int current, final int expandedTypeID) {
            current = DTMDefaultBaseTraversers.this.makeNodeIdentity(current);
            while (-1 != (current = DTMDefaultBaseTraversers.this.m_parent.elementAt(current))) {
                if (DTMDefaultBaseTraversers.this.m_exptype.elementAt(current) == expandedTypeID) {
                    return DTMDefaultBaseTraversers.this.makeNodeHandle(current);
                }
            }
            return -1;
        }
        
        @Override
        public int next(final int context, final int current) {
            return -1;
        }
        
        @Override
        public int next(final int context, final int current, final int expandedTypeID) {
            return -1;
        }
    }
    
    private class PrecedingTraverser extends DTMAxisTraverser
    {
        protected boolean isAncestor(int contextIdent, final int currentIdent) {
            for (contextIdent = DTMDefaultBaseTraversers.this.m_parent.elementAt(contextIdent); -1 != contextIdent; contextIdent = DTMDefaultBaseTraversers.this.m_parent.elementAt(contextIdent)) {
                if (contextIdent == currentIdent) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public int next(final int context, int current) {
            final int subtreeRootIdent = DTMDefaultBaseTraversers.this.makeNodeIdentity(context);
            short type;
            for (current = DTMDefaultBaseTraversers.this.makeNodeIdentity(current) - 1; current >= 0; --current) {
                type = DTMDefaultBaseTraversers.this._type(current);
                if (2 != type && 13 != type && !this.isAncestor(subtreeRootIdent, current)) {
                    return DTMDefaultBaseTraversers.this.makeNodeHandle(current);
                }
            }
            return -1;
        }
        
        @Override
        public int next(final int context, int current, final int expandedTypeID) {
            final int subtreeRootIdent = DTMDefaultBaseTraversers.this.makeNodeIdentity(context);
            int exptype;
            for (current = DTMDefaultBaseTraversers.this.makeNodeIdentity(current) - 1; current >= 0; --current) {
                exptype = DTMDefaultBaseTraversers.this.m_exptype.elementAt(current);
                if (exptype == expandedTypeID && !this.isAncestor(subtreeRootIdent, current)) {
                    return DTMDefaultBaseTraversers.this.makeNodeHandle(current);
                }
            }
            return -1;
        }
    }
    
    private class PrecedingAndAncestorTraverser extends DTMAxisTraverser
    {
        @Override
        public int next(final int context, int current) {
            final int subtreeRootIdent = DTMDefaultBaseTraversers.this.makeNodeIdentity(context);
            short type;
            for (current = DTMDefaultBaseTraversers.this.makeNodeIdentity(current) - 1; current >= 0; --current) {
                type = DTMDefaultBaseTraversers.this._type(current);
                if (2 != type && 13 != type) {
                    return DTMDefaultBaseTraversers.this.makeNodeHandle(current);
                }
            }
            return -1;
        }
        
        @Override
        public int next(final int context, int current, final int expandedTypeID) {
            final int subtreeRootIdent = DTMDefaultBaseTraversers.this.makeNodeIdentity(context);
            int exptype;
            for (current = DTMDefaultBaseTraversers.this.makeNodeIdentity(current) - 1; current >= 0; --current) {
                exptype = DTMDefaultBaseTraversers.this.m_exptype.elementAt(current);
                if (exptype == expandedTypeID) {
                    return DTMDefaultBaseTraversers.this.makeNodeHandle(current);
                }
            }
            return -1;
        }
    }
    
    private class PrecedingSiblingTraverser extends DTMAxisTraverser
    {
        @Override
        public int next(final int context, final int current) {
            return DTMDefaultBaseTraversers.this.getPreviousSibling(current);
        }
        
        @Override
        public int next(final int context, int current, final int expandedTypeID) {
            while (-1 != (current = DTMDefaultBaseTraversers.this.getPreviousSibling(current))) {
                if (DTMDefaultBaseTraversers.this.getExpandedTypeID(current) == expandedTypeID) {
                    return current;
                }
            }
            return -1;
        }
    }
    
    private class SelfTraverser extends DTMAxisTraverser
    {
        @Override
        public int first(final int context) {
            return context;
        }
        
        @Override
        public int first(final int context, final int expandedTypeID) {
            return (DTMDefaultBaseTraversers.this.getExpandedTypeID(context) == expandedTypeID) ? context : -1;
        }
        
        @Override
        public int next(final int context, final int current) {
            return -1;
        }
        
        @Override
        public int next(final int context, final int current, final int expandedTypeID) {
            return -1;
        }
    }
    
    private class AllFromRootTraverser extends AllFromNodeTraverser
    {
        @Override
        public int first(final int context) {
            return DTMDefaultBaseTraversers.this.getDocumentRoot(context);
        }
        
        @Override
        public int first(final int context, final int expandedTypeID) {
            return (DTMDefaultBaseTraversers.this.getExpandedTypeID(DTMDefaultBaseTraversers.this.getDocumentRoot(context)) == expandedTypeID) ? context : this.next(context, context, expandedTypeID);
        }
        
        @Override
        public int next(final int context, int current) {
            final int subtreeRootIdent = DTMDefaultBaseTraversers.this.makeNodeIdentity(context);
            current = DTMDefaultBaseTraversers.this.makeNodeIdentity(current) + 1;
            final int type = DTMDefaultBaseTraversers.this._type(current);
            if (type == -1) {
                return -1;
            }
            return DTMDefaultBaseTraversers.this.makeNodeHandle(current);
        }
        
        @Override
        public int next(final int context, int current, final int expandedTypeID) {
            final int subtreeRootIdent = DTMDefaultBaseTraversers.this.makeNodeIdentity(context);
            current = DTMDefaultBaseTraversers.this.makeNodeIdentity(current) + 1;
            while (true) {
                final int exptype = DTMDefaultBaseTraversers.this._exptype(current);
                if (exptype == -1) {
                    return -1;
                }
                if (exptype == expandedTypeID) {
                    return DTMDefaultBaseTraversers.this.makeNodeHandle(current);
                }
                ++current;
            }
        }
    }
    
    private class RootTraverser extends AllFromRootTraverser
    {
        @Override
        public int first(final int context, final int expandedTypeID) {
            final int root = DTMDefaultBaseTraversers.this.getDocumentRoot(context);
            return (DTMDefaultBaseTraversers.this.getExpandedTypeID(root) == expandedTypeID) ? root : -1;
        }
        
        @Override
        public int next(final int context, final int current) {
            return -1;
        }
        
        @Override
        public int next(final int context, final int current, final int expandedTypeID) {
            return -1;
        }
    }
    
    private class DescendantOrSelfFromRootTraverser extends DescendantTraverser
    {
        @Override
        protected int getFirstPotential(final int identity) {
            return identity;
        }
        
        @Override
        protected int getSubtreeRoot(final int handle) {
            return DTMDefaultBaseTraversers.this.makeNodeIdentity(DTMDefaultBaseTraversers.this.getDocument());
        }
        
        @Override
        public int first(final int context) {
            return DTMDefaultBaseTraversers.this.getDocumentRoot(context);
        }
        
        @Override
        public int first(final int context, final int expandedTypeID) {
            if (this.isIndexed(expandedTypeID)) {
                final int identity = 0;
                final int firstPotential = this.getFirstPotential(identity);
                return DTMDefaultBaseTraversers.this.makeNodeHandle(this.getNextIndexed(identity, firstPotential, expandedTypeID));
            }
            final int root = this.first(context);
            return this.next(root, root, expandedTypeID);
        }
    }
    
    private class DescendantFromRootTraverser extends DescendantTraverser
    {
        @Override
        protected int getFirstPotential(final int identity) {
            return DTMDefaultBaseTraversers.this._firstch(0);
        }
        
        @Override
        protected int getSubtreeRoot(final int handle) {
            return 0;
        }
        
        @Override
        public int first(final int context) {
            return DTMDefaultBaseTraversers.this.makeNodeHandle(DTMDefaultBaseTraversers.this._firstch(0));
        }
        
        @Override
        public int first(final int context, final int expandedTypeID) {
            if (this.isIndexed(expandedTypeID)) {
                final int identity = 0;
                final int firstPotential = this.getFirstPotential(identity);
                return DTMDefaultBaseTraversers.this.makeNodeHandle(this.getNextIndexed(identity, firstPotential, expandedTypeID));
            }
            final int root = DTMDefaultBaseTraversers.this.getDocumentRoot(context);
            return this.next(root, root, expandedTypeID);
        }
    }
}
