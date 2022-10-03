package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.CharacterData;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.ranges.RangeException;
import java.util.Vector;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.ranges.Range;

public class RangeImpl implements Range
{
    DocumentImpl fDocument;
    Node fStartContainer;
    Node fEndContainer;
    int fStartOffset;
    int fEndOffset;
    boolean fIsCollapsed;
    boolean fDetach;
    Node fInsertNode;
    Node fDeleteNode;
    Node fSplitNode;
    boolean fInsertedFromRange;
    Node fRemoveChild;
    static final int EXTRACT_CONTENTS = 1;
    static final int CLONE_CONTENTS = 2;
    static final int DELETE_CONTENTS = 3;
    
    public RangeImpl(final DocumentImpl document) {
        this.fDetach = false;
        this.fInsertNode = null;
        this.fDeleteNode = null;
        this.fSplitNode = null;
        this.fInsertedFromRange = false;
        this.fRemoveChild = null;
        this.fDocument = document;
        this.fStartContainer = document;
        this.fEndContainer = document;
        this.fStartOffset = 0;
        this.fEndOffset = 0;
        this.fDetach = false;
    }
    
    @Override
    public Node getStartContainer() {
        if (this.fDetach) {
            throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
        }
        return this.fStartContainer;
    }
    
    @Override
    public int getStartOffset() {
        if (this.fDetach) {
            throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
        }
        return this.fStartOffset;
    }
    
    @Override
    public Node getEndContainer() {
        if (this.fDetach) {
            throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
        }
        return this.fEndContainer;
    }
    
    @Override
    public int getEndOffset() {
        if (this.fDetach) {
            throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
        }
        return this.fEndOffset;
    }
    
    @Override
    public boolean getCollapsed() {
        if (this.fDetach) {
            throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
        }
        return this.fStartContainer == this.fEndContainer && this.fStartOffset == this.fEndOffset;
    }
    
    @Override
    public Node getCommonAncestorContainer() {
        if (this.fDetach) {
            throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
        }
        final Vector startV = new Vector();
        for (Node node = this.fStartContainer; node != null; node = node.getParentNode()) {
            startV.addElement(node);
        }
        final Vector endV = new Vector();
        for (Node node = this.fEndContainer; node != null; node = node.getParentNode()) {
            endV.addElement(node);
        }
        int s = startV.size() - 1;
        int e = endV.size() - 1;
        Object result = null;
        while (s >= 0 && e >= 0 && startV.elementAt(s) == endV.elementAt(e)) {
            result = startV.elementAt(s);
            --s;
            --e;
        }
        return (Node)result;
    }
    
    @Override
    public void setStart(final Node refNode, final int offset) throws RangeException, DOMException {
        if (this.fDocument.errorChecking) {
            if (this.fDetach) {
                throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
            }
            if (!this.isLegalContainer(refNode)) {
                throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null));
            }
            if (this.fDocument != refNode.getOwnerDocument() && this.fDocument != refNode) {
                throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
            }
        }
        this.checkIndex(refNode, offset);
        this.fStartContainer = refNode;
        this.fStartOffset = offset;
        if (this.getCommonAncestorContainer() == null || (this.fStartContainer == this.fEndContainer && this.fEndOffset < this.fStartOffset)) {
            this.collapse(true);
        }
    }
    
    @Override
    public void setEnd(final Node refNode, final int offset) throws RangeException, DOMException {
        if (this.fDocument.errorChecking) {
            if (this.fDetach) {
                throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
            }
            if (!this.isLegalContainer(refNode)) {
                throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null));
            }
            if (this.fDocument != refNode.getOwnerDocument() && this.fDocument != refNode) {
                throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
            }
        }
        this.checkIndex(refNode, offset);
        this.fEndContainer = refNode;
        this.fEndOffset = offset;
        if (this.getCommonAncestorContainer() == null || (this.fStartContainer == this.fEndContainer && this.fEndOffset < this.fStartOffset)) {
            this.collapse(false);
        }
    }
    
    @Override
    public void setStartBefore(final Node refNode) throws RangeException {
        if (this.fDocument.errorChecking) {
            if (this.fDetach) {
                throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
            }
            if (!this.hasLegalRootContainer(refNode) || !this.isLegalContainedNode(refNode)) {
                throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null));
            }
            if (this.fDocument != refNode.getOwnerDocument() && this.fDocument != refNode) {
                throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
            }
        }
        this.fStartContainer = refNode.getParentNode();
        int i = 0;
        for (Node n = refNode; n != null; n = n.getPreviousSibling()) {
            ++i;
        }
        this.fStartOffset = i - 1;
        if (this.getCommonAncestorContainer() == null || (this.fStartContainer == this.fEndContainer && this.fEndOffset < this.fStartOffset)) {
            this.collapse(true);
        }
    }
    
    @Override
    public void setStartAfter(final Node refNode) throws RangeException {
        if (this.fDocument.errorChecking) {
            if (this.fDetach) {
                throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
            }
            if (!this.hasLegalRootContainer(refNode) || !this.isLegalContainedNode(refNode)) {
                throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null));
            }
            if (this.fDocument != refNode.getOwnerDocument() && this.fDocument != refNode) {
                throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
            }
        }
        this.fStartContainer = refNode.getParentNode();
        int i = 0;
        for (Node n = refNode; n != null; n = n.getPreviousSibling()) {
            ++i;
        }
        this.fStartOffset = i;
        if (this.getCommonAncestorContainer() == null || (this.fStartContainer == this.fEndContainer && this.fEndOffset < this.fStartOffset)) {
            this.collapse(true);
        }
    }
    
    @Override
    public void setEndBefore(final Node refNode) throws RangeException {
        if (this.fDocument.errorChecking) {
            if (this.fDetach) {
                throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
            }
            if (!this.hasLegalRootContainer(refNode) || !this.isLegalContainedNode(refNode)) {
                throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null));
            }
            if (this.fDocument != refNode.getOwnerDocument() && this.fDocument != refNode) {
                throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
            }
        }
        this.fEndContainer = refNode.getParentNode();
        int i = 0;
        for (Node n = refNode; n != null; n = n.getPreviousSibling()) {
            ++i;
        }
        this.fEndOffset = i - 1;
        if (this.getCommonAncestorContainer() == null || (this.fStartContainer == this.fEndContainer && this.fEndOffset < this.fStartOffset)) {
            this.collapse(false);
        }
    }
    
    @Override
    public void setEndAfter(final Node refNode) throws RangeException {
        if (this.fDocument.errorChecking) {
            if (this.fDetach) {
                throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
            }
            if (!this.hasLegalRootContainer(refNode) || !this.isLegalContainedNode(refNode)) {
                throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null));
            }
            if (this.fDocument != refNode.getOwnerDocument() && this.fDocument != refNode) {
                throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
            }
        }
        this.fEndContainer = refNode.getParentNode();
        int i = 0;
        for (Node n = refNode; n != null; n = n.getPreviousSibling()) {
            ++i;
        }
        this.fEndOffset = i;
        if (this.getCommonAncestorContainer() == null || (this.fStartContainer == this.fEndContainer && this.fEndOffset < this.fStartOffset)) {
            this.collapse(false);
        }
    }
    
    @Override
    public void collapse(final boolean toStart) {
        if (this.fDetach) {
            throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
        }
        if (toStart) {
            this.fEndContainer = this.fStartContainer;
            this.fEndOffset = this.fStartOffset;
        }
        else {
            this.fStartContainer = this.fEndContainer;
            this.fStartOffset = this.fEndOffset;
        }
    }
    
    @Override
    public void selectNode(final Node refNode) throws RangeException {
        if (this.fDocument.errorChecking) {
            if (this.fDetach) {
                throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
            }
            if (!this.isLegalContainer(refNode.getParentNode()) || !this.isLegalContainedNode(refNode)) {
                throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null));
            }
            if (this.fDocument != refNode.getOwnerDocument() && this.fDocument != refNode) {
                throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
            }
        }
        final Node parent = refNode.getParentNode();
        if (parent != null) {
            this.fStartContainer = parent;
            this.fEndContainer = parent;
            int i = 0;
            for (Node n = refNode; n != null; n = n.getPreviousSibling()) {
                ++i;
            }
            this.fStartOffset = i - 1;
            this.fEndOffset = this.fStartOffset + 1;
        }
    }
    
    @Override
    public void selectNodeContents(final Node refNode) throws RangeException {
        if (this.fDocument.errorChecking) {
            if (this.fDetach) {
                throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
            }
            if (!this.isLegalContainer(refNode)) {
                throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null));
            }
            if (this.fDocument != refNode.getOwnerDocument() && this.fDocument != refNode) {
                throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
            }
        }
        this.fStartContainer = refNode;
        this.fEndContainer = refNode;
        final Node first = refNode.getFirstChild();
        this.fStartOffset = 0;
        if (first == null) {
            this.fEndOffset = 0;
        }
        else {
            int i = 0;
            for (Node n = first; n != null; n = n.getNextSibling()) {
                ++i;
            }
            this.fEndOffset = i;
        }
    }
    
    @Override
    public short compareBoundaryPoints(final short how, final Range sourceRange) throws DOMException {
        if (this.fDocument.errorChecking) {
            if (this.fDetach) {
                throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
            }
            if ((this.fDocument != sourceRange.getStartContainer().getOwnerDocument() && this.fDocument != sourceRange.getStartContainer() && sourceRange.getStartContainer() != null) || (this.fDocument != sourceRange.getEndContainer().getOwnerDocument() && this.fDocument != sourceRange.getEndContainer() && sourceRange.getStartContainer() != null)) {
                throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
            }
        }
        Node endPointA;
        Node endPointB;
        int offsetA;
        int offsetB;
        if (how == 0) {
            endPointA = sourceRange.getStartContainer();
            endPointB = this.fStartContainer;
            offsetA = sourceRange.getStartOffset();
            offsetB = this.fStartOffset;
        }
        else if (how == 1) {
            endPointA = sourceRange.getStartContainer();
            endPointB = this.fEndContainer;
            offsetA = sourceRange.getStartOffset();
            offsetB = this.fEndOffset;
        }
        else if (how == 3) {
            endPointA = sourceRange.getEndContainer();
            endPointB = this.fStartContainer;
            offsetA = sourceRange.getEndOffset();
            offsetB = this.fStartOffset;
        }
        else {
            endPointA = sourceRange.getEndContainer();
            endPointB = this.fEndContainer;
            offsetA = sourceRange.getEndOffset();
            offsetB = this.fEndOffset;
        }
        if (endPointA != endPointB) {
            Node c = endPointB;
            Node p = c.getParentNode();
            while (p != null) {
                if (p == endPointA) {
                    final int index = this.indexOf(c, endPointA);
                    if (offsetA <= index) {
                        return 1;
                    }
                    return -1;
                }
                else {
                    c = p;
                    p = p.getParentNode();
                }
            }
            c = endPointA;
            p = c.getParentNode();
            while (p != null) {
                if (p == endPointB) {
                    final int index = this.indexOf(c, endPointB);
                    if (index < offsetB) {
                        return 1;
                    }
                    return -1;
                }
                else {
                    c = p;
                    p = p.getParentNode();
                }
            }
            int depthDiff = 0;
            for (Node n = endPointA; n != null; n = n.getParentNode()) {
                ++depthDiff;
            }
            for (Node n = endPointB; n != null; n = n.getParentNode()) {
                --depthDiff;
            }
            while (depthDiff > 0) {
                endPointA = endPointA.getParentNode();
                --depthDiff;
            }
            while (depthDiff < 0) {
                endPointB = endPointB.getParentNode();
                ++depthDiff;
            }
            for (Node pA = endPointA.getParentNode(), pB = endPointB.getParentNode(); pA != pB; pA = pA.getParentNode(), pB = pB.getParentNode()) {
                endPointA = pA;
                endPointB = pB;
            }
            for (Node n = endPointA.getNextSibling(); n != null; n = n.getNextSibling()) {
                if (n == endPointB) {
                    return 1;
                }
            }
            return -1;
        }
        if (offsetA < offsetB) {
            return 1;
        }
        if (offsetA == offsetB) {
            return 0;
        }
        return -1;
    }
    
    @Override
    public void deleteContents() throws DOMException {
        this.traverseContents(3);
    }
    
    @Override
    public DocumentFragment extractContents() throws DOMException {
        return this.traverseContents(1);
    }
    
    @Override
    public DocumentFragment cloneContents() throws DOMException {
        return this.traverseContents(2);
    }
    
    @Override
    public void insertNode(final Node newNode) throws DOMException, RangeException {
        if (newNode == null) {
            return;
        }
        final int type = newNode.getNodeType();
        if (this.fDocument.errorChecking) {
            if (this.fDetach) {
                throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
            }
            if (this.fDocument != newNode.getOwnerDocument()) {
                throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
            }
            if (type == 2 || type == 6 || type == 12 || type == 9) {
                throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null));
            }
        }
        int currentChildren = 0;
        this.fInsertedFromRange = true;
        if (this.fStartContainer.getNodeType() == 3) {
            final Node parent = this.fStartContainer.getParentNode();
            currentChildren = parent.getChildNodes().getLength();
            final Node cloneCurrent = this.fStartContainer.cloneNode(false);
            ((TextImpl)cloneCurrent).setNodeValueInternal(cloneCurrent.getNodeValue().substring(this.fStartOffset));
            ((TextImpl)this.fStartContainer).setNodeValueInternal(this.fStartContainer.getNodeValue().substring(0, this.fStartOffset));
            final Node next = this.fStartContainer.getNextSibling();
            if (next != null) {
                if (parent != null) {
                    parent.insertBefore(newNode, next);
                    parent.insertBefore(cloneCurrent, next);
                }
            }
            else if (parent != null) {
                parent.appendChild(newNode);
                parent.appendChild(cloneCurrent);
            }
            if (this.fEndContainer == this.fStartContainer) {
                this.fEndContainer = cloneCurrent;
                this.fEndOffset -= this.fStartOffset;
            }
            else if (this.fEndContainer == parent) {
                this.fEndOffset += parent.getChildNodes().getLength() - currentChildren;
            }
            this.signalSplitData(this.fStartContainer, cloneCurrent, this.fStartOffset);
        }
        else {
            if (this.fEndContainer == this.fStartContainer) {
                currentChildren = this.fEndContainer.getChildNodes().getLength();
            }
            Node current;
            int i;
            for (current = this.fStartContainer.getFirstChild(), i = 0, i = 0; i < this.fStartOffset && current != null; current = current.getNextSibling(), ++i) {}
            if (current != null) {
                this.fStartContainer.insertBefore(newNode, current);
            }
            else {
                this.fStartContainer.appendChild(newNode);
            }
            if (this.fEndContainer == this.fStartContainer && this.fEndOffset != 0) {
                this.fEndOffset += this.fEndContainer.getChildNodes().getLength() - currentChildren;
            }
        }
        this.fInsertedFromRange = false;
    }
    
    @Override
    public void surroundContents(final Node newParent) throws DOMException, RangeException {
        if (newParent == null) {
            return;
        }
        final int type = newParent.getNodeType();
        if (this.fDocument.errorChecking) {
            if (this.fDetach) {
                throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
            }
            if (type == 2 || type == 6 || type == 12 || type == 10 || type == 9 || type == 11) {
                throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null));
            }
        }
        Node realStart = this.fStartContainer;
        Node realEnd = this.fEndContainer;
        if (this.fStartContainer.getNodeType() == 3) {
            realStart = this.fStartContainer.getParentNode();
        }
        if (this.fEndContainer.getNodeType() == 3) {
            realEnd = this.fEndContainer.getParentNode();
        }
        if (realStart != realEnd) {
            throw new RangeExceptionImpl((short)1, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "BAD_BOUNDARYPOINTS_ERR", null));
        }
        final DocumentFragment frag = this.extractContents();
        this.insertNode(newParent);
        newParent.appendChild(frag);
        this.selectNode(newParent);
    }
    
    @Override
    public Range cloneRange() {
        if (this.fDetach) {
            throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
        }
        final Range range = this.fDocument.createRange();
        range.setStart(this.fStartContainer, this.fStartOffset);
        range.setEnd(this.fEndContainer, this.fEndOffset);
        return range;
    }
    
    @Override
    public String toString() {
        if (this.fDetach) {
            throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
        }
        Node node = this.fStartContainer;
        Node stopNode = this.fEndContainer;
        final StringBuffer sb = new StringBuffer();
        if (this.fStartContainer.getNodeType() == 3 || this.fStartContainer.getNodeType() == 4) {
            if (this.fStartContainer == this.fEndContainer) {
                sb.append(this.fStartContainer.getNodeValue().substring(this.fStartOffset, this.fEndOffset));
                return sb.toString();
            }
            sb.append(this.fStartContainer.getNodeValue().substring(this.fStartOffset));
            node = this.nextNode(node, true);
        }
        else {
            node = node.getFirstChild();
            if (this.fStartOffset > 0) {
                for (int counter = 0; counter < this.fStartOffset && node != null; node = node.getNextSibling(), ++counter) {}
            }
            if (node == null) {
                node = this.nextNode(this.fStartContainer, false);
            }
        }
        if (this.fEndContainer.getNodeType() != 3 && this.fEndContainer.getNodeType() != 4) {
            int i;
            for (i = this.fEndOffset, stopNode = this.fEndContainer.getFirstChild(); i > 0 && stopNode != null; --i, stopNode = stopNode.getNextSibling()) {}
            if (stopNode == null) {
                stopNode = this.nextNode(this.fEndContainer, false);
            }
        }
        while (node != stopNode && node != null) {
            if (node.getNodeType() == 3 || node.getNodeType() == 4) {
                sb.append(node.getNodeValue());
            }
            node = this.nextNode(node, true);
        }
        if (this.fEndContainer.getNodeType() == 3 || this.fEndContainer.getNodeType() == 4) {
            sb.append(this.fEndContainer.getNodeValue().substring(0, this.fEndOffset));
        }
        return sb.toString();
    }
    
    @Override
    public void detach() {
        if (this.fDetach) {
            throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
        }
        this.fDetach = true;
        this.fDocument.removeRange(this);
    }
    
    void signalSplitData(final Node node, final Node newNode, final int offset) {
        this.fSplitNode = node;
        this.fDocument.splitData(node, newNode, offset);
        this.fSplitNode = null;
    }
    
    void receiveSplitData(final Node node, final Node newNode, final int offset) {
        if (node == null || newNode == null) {
            return;
        }
        if (this.fSplitNode == node) {
            return;
        }
        if (node == this.fStartContainer && this.fStartContainer.getNodeType() == 3 && this.fStartOffset > offset) {
            this.fStartOffset -= offset;
            this.fStartContainer = newNode;
        }
        if (node == this.fEndContainer && this.fEndContainer.getNodeType() == 3 && this.fEndOffset > offset) {
            this.fEndOffset -= offset;
            this.fEndContainer = newNode;
        }
    }
    
    void deleteData(final CharacterData node, final int offset, final int count) {
        ((CharacterData)(this.fDeleteNode = node)).deleteData(offset, count);
        this.fDeleteNode = null;
    }
    
    void receiveDeletedText(final Node node, final int offset, final int count) {
        if (node == null) {
            return;
        }
        if (this.fDeleteNode == node) {
            return;
        }
        if (node == this.fStartContainer && this.fStartContainer.getNodeType() == 3) {
            if (this.fStartOffset > offset + count) {
                this.fStartOffset = offset + (this.fStartOffset - (offset + count));
            }
            else if (this.fStartOffset > offset) {
                this.fStartOffset = offset;
            }
        }
        if (node == this.fEndContainer && this.fEndContainer.getNodeType() == 3) {
            if (this.fEndOffset > offset + count) {
                this.fEndOffset = offset + (this.fEndOffset - (offset + count));
            }
            else if (this.fEndOffset > offset) {
                this.fEndOffset = offset;
            }
        }
    }
    
    void insertData(final CharacterData node, final int index, final String insert) {
        ((CharacterData)(this.fInsertNode = node)).insertData(index, insert);
        this.fInsertNode = null;
    }
    
    void receiveInsertedText(final Node node, final int index, final int len) {
        if (node == null) {
            return;
        }
        if (this.fInsertNode == node) {
            return;
        }
        if (node == this.fStartContainer && this.fStartContainer.getNodeType() == 3 && index < this.fStartOffset) {
            this.fStartOffset += len;
        }
        if (node == this.fEndContainer && this.fEndContainer.getNodeType() == 3 && index < this.fEndOffset) {
            this.fEndOffset += len;
        }
    }
    
    void receiveReplacedText(final Node node) {
        if (node == null) {
            return;
        }
        if (node == this.fStartContainer && this.fStartContainer.getNodeType() == 3) {
            this.fStartOffset = 0;
        }
        if (node == this.fEndContainer && this.fEndContainer.getNodeType() == 3) {
            this.fEndOffset = 0;
        }
    }
    
    public void insertedNodeFromDOM(final Node node) {
        if (node == null) {
            return;
        }
        if (this.fInsertNode == node) {
            return;
        }
        if (this.fInsertedFromRange) {
            return;
        }
        final Node parent = node.getParentNode();
        if (parent == this.fStartContainer) {
            final int index = this.indexOf(node, this.fStartContainer);
            if (index < this.fStartOffset) {
                ++this.fStartOffset;
            }
        }
        if (parent == this.fEndContainer) {
            final int index = this.indexOf(node, this.fEndContainer);
            if (index < this.fEndOffset) {
                ++this.fEndOffset;
            }
        }
    }
    
    Node removeChild(final Node parent, final Node child) {
        this.fRemoveChild = child;
        final Node n = parent.removeChild(child);
        this.fRemoveChild = null;
        return n;
    }
    
    void removeNode(final Node node) {
        if (node == null) {
            return;
        }
        if (this.fRemoveChild == node) {
            return;
        }
        final Node parent = node.getParentNode();
        if (parent == this.fStartContainer) {
            final int index = this.indexOf(node, this.fStartContainer);
            if (index < this.fStartOffset) {
                --this.fStartOffset;
            }
        }
        if (parent == this.fEndContainer) {
            final int index = this.indexOf(node, this.fEndContainer);
            if (index < this.fEndOffset) {
                --this.fEndOffset;
            }
        }
        if (parent != this.fStartContainer || parent != this.fEndContainer) {
            if (this.isAncestorOf(node, this.fStartContainer)) {
                this.fStartContainer = parent;
                this.fStartOffset = this.indexOf(node, parent);
            }
            if (this.isAncestorOf(node, this.fEndContainer)) {
                this.fEndContainer = parent;
                this.fEndOffset = this.indexOf(node, parent);
            }
        }
    }
    
    private DocumentFragment traverseContents(final int how) throws DOMException {
        if (this.fStartContainer == null || this.fEndContainer == null) {
            return null;
        }
        if (this.fDetach) {
            throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
        }
        if (this.fStartContainer == this.fEndContainer) {
            return this.traverseSameContainer(how);
        }
        int endContainerDepth = 0;
        Node c = this.fEndContainer;
        for (Node p = c.getParentNode(); p != null; p = p.getParentNode()) {
            if (p == this.fStartContainer) {
                return this.traverseCommonStartContainer(c, how);
            }
            ++endContainerDepth;
            c = p;
        }
        int startContainerDepth = 0;
        Node c2 = this.fStartContainer;
        for (Node p2 = c2.getParentNode(); p2 != null; p2 = p2.getParentNode()) {
            if (p2 == this.fEndContainer) {
                return this.traverseCommonEndContainer(c2, how);
            }
            ++startContainerDepth;
            c2 = p2;
        }
        int depthDiff = startContainerDepth - endContainerDepth;
        Node startNode = this.fStartContainer;
        while (depthDiff > 0) {
            startNode = startNode.getParentNode();
            --depthDiff;
        }
        Node endNode = this.fEndContainer;
        while (depthDiff < 0) {
            endNode = endNode.getParentNode();
            ++depthDiff;
        }
        for (Node sp = startNode.getParentNode(), ep = endNode.getParentNode(); sp != ep; sp = sp.getParentNode(), ep = ep.getParentNode()) {
            startNode = sp;
            endNode = ep;
        }
        return this.traverseCommonAncestors(startNode, endNode, how);
    }
    
    private DocumentFragment traverseSameContainer(final int how) {
        DocumentFragment frag = null;
        if (how != 3) {
            frag = this.fDocument.createDocumentFragment();
        }
        if (this.fStartOffset == this.fEndOffset) {
            return frag;
        }
        if (this.fStartContainer.getNodeType() != 3) {
            Node n = this.getSelectedNode(this.fStartContainer, this.fStartOffset);
            Node sibling;
            for (int cnt = this.fEndOffset - this.fStartOffset; cnt > 0; --cnt, n = sibling) {
                sibling = n.getNextSibling();
                final Node xferNode = this.traverseFullySelected(n, how);
                if (frag != null) {
                    frag.appendChild(xferNode);
                }
            }
            if (how != 2) {
                this.collapse(true);
            }
            return frag;
        }
        final String s = this.fStartContainer.getNodeValue();
        final String sub = s.substring(this.fStartOffset, this.fEndOffset);
        if (how != 2) {
            ((TextImpl)this.fStartContainer).deleteData(this.fStartOffset, this.fEndOffset - this.fStartOffset);
            this.collapse(true);
        }
        if (how == 3) {
            return null;
        }
        frag.appendChild(this.fDocument.createTextNode(sub));
        return frag;
    }
    
    private DocumentFragment traverseCommonStartContainer(final Node endAncestor, final int how) {
        DocumentFragment frag = null;
        if (how != 3) {
            frag = this.fDocument.createDocumentFragment();
        }
        Node n = this.traverseRightBoundary(endAncestor, how);
        if (frag != null) {
            frag.appendChild(n);
        }
        final int endIdx = this.indexOf(endAncestor, this.fStartContainer);
        int cnt = endIdx - this.fStartOffset;
        if (cnt <= 0) {
            if (how != 2) {
                this.setEndBefore(endAncestor);
                this.collapse(false);
            }
            return frag;
        }
        n = endAncestor.getPreviousSibling();
        while (cnt > 0) {
            final Node sibling = n.getPreviousSibling();
            final Node xferNode = this.traverseFullySelected(n, how);
            if (frag != null) {
                frag.insertBefore(xferNode, frag.getFirstChild());
            }
            --cnt;
            n = sibling;
        }
        if (how != 2) {
            this.setEndBefore(endAncestor);
            this.collapse(false);
        }
        return frag;
    }
    
    private DocumentFragment traverseCommonEndContainer(final Node startAncestor, final int how) {
        DocumentFragment frag = null;
        if (how != 3) {
            frag = this.fDocument.createDocumentFragment();
        }
        Node n = this.traverseLeftBoundary(startAncestor, how);
        if (frag != null) {
            frag.appendChild(n);
        }
        int startIdx = this.indexOf(startAncestor, this.fEndContainer);
        ++startIdx;
        int cnt = this.fEndOffset - startIdx;
        n = startAncestor.getNextSibling();
        while (cnt > 0) {
            final Node sibling = n.getNextSibling();
            final Node xferNode = this.traverseFullySelected(n, how);
            if (frag != null) {
                frag.appendChild(xferNode);
            }
            --cnt;
            n = sibling;
        }
        if (how != 2) {
            this.setStartAfter(startAncestor);
            this.collapse(true);
        }
        return frag;
    }
    
    private DocumentFragment traverseCommonAncestors(final Node startAncestor, final Node endAncestor, final int how) {
        DocumentFragment frag = null;
        if (how != 3) {
            frag = this.fDocument.createDocumentFragment();
        }
        Node n = this.traverseLeftBoundary(startAncestor, how);
        if (frag != null) {
            frag.appendChild(n);
        }
        final Node commonParent = startAncestor.getParentNode();
        int startOffset = this.indexOf(startAncestor, commonParent);
        final int endOffset = this.indexOf(endAncestor, commonParent);
        ++startOffset;
        int cnt = endOffset - startOffset;
        Node sibling = startAncestor.getNextSibling();
        while (cnt > 0) {
            final Node nextSibling = sibling.getNextSibling();
            n = this.traverseFullySelected(sibling, how);
            if (frag != null) {
                frag.appendChild(n);
            }
            sibling = nextSibling;
            --cnt;
        }
        n = this.traverseRightBoundary(endAncestor, how);
        if (frag != null) {
            frag.appendChild(n);
        }
        if (how != 2) {
            this.setStartAfter(startAncestor);
            this.collapse(true);
        }
        return frag;
    }
    
    private Node traverseRightBoundary(final Node root, final int how) {
        Node next = this.getSelectedNode(this.fEndContainer, this.fEndOffset - 1);
        boolean isFullySelected = next != this.fEndContainer;
        if (next == root) {
            return this.traverseNode(next, isFullySelected, false, how);
        }
        Node parent = next.getParentNode();
        Node clonedParent = this.traverseNode(parent, false, false, how);
        while (parent != null) {
            while (next != null) {
                final Node prevSibling = next.getPreviousSibling();
                final Node clonedChild = this.traverseNode(next, isFullySelected, false, how);
                if (how != 3) {
                    clonedParent.insertBefore(clonedChild, clonedParent.getFirstChild());
                }
                isFullySelected = true;
                next = prevSibling;
            }
            if (parent == root) {
                return clonedParent;
            }
            next = parent.getPreviousSibling();
            parent = parent.getParentNode();
            final Node clonedGrandParent = this.traverseNode(parent, false, false, how);
            if (how != 3) {
                clonedGrandParent.appendChild(clonedParent);
            }
            clonedParent = clonedGrandParent;
        }
        return null;
    }
    
    private Node traverseLeftBoundary(final Node root, final int how) {
        Node next = this.getSelectedNode(this.getStartContainer(), this.getStartOffset());
        boolean isFullySelected = next != this.getStartContainer();
        if (next == root) {
            return this.traverseNode(next, isFullySelected, true, how);
        }
        Node parent = next.getParentNode();
        Node clonedParent = this.traverseNode(parent, false, true, how);
        while (parent != null) {
            while (next != null) {
                final Node nextSibling = next.getNextSibling();
                final Node clonedChild = this.traverseNode(next, isFullySelected, true, how);
                if (how != 3) {
                    clonedParent.appendChild(clonedChild);
                }
                isFullySelected = true;
                next = nextSibling;
            }
            if (parent == root) {
                return clonedParent;
            }
            next = parent.getNextSibling();
            parent = parent.getParentNode();
            final Node clonedGrandParent = this.traverseNode(parent, false, true, how);
            if (how != 3) {
                clonedGrandParent.appendChild(clonedParent);
            }
            clonedParent = clonedGrandParent;
        }
        return null;
    }
    
    private Node traverseNode(final Node n, final boolean isFullySelected, final boolean isLeft, final int how) {
        if (isFullySelected) {
            return this.traverseFullySelected(n, how);
        }
        if (n.getNodeType() == 3) {
            return this.traverseTextNode(n, isLeft, how);
        }
        return this.traversePartiallySelected(n, how);
    }
    
    private Node traverseFullySelected(final Node n, final int how) {
        switch (how) {
            case 2: {
                return n.cloneNode(true);
            }
            case 1: {
                if (n.getNodeType() == 10) {
                    throw new DOMException((short)3, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null));
                }
                return n;
            }
            case 3: {
                n.getParentNode().removeChild(n);
                return null;
            }
            default: {
                return null;
            }
        }
    }
    
    private Node traversePartiallySelected(final Node n, final int how) {
        switch (how) {
            case 3: {
                return null;
            }
            case 1:
            case 2: {
                return n.cloneNode(false);
            }
            default: {
                return null;
            }
        }
    }
    
    private Node traverseTextNode(final Node n, final boolean isLeft, final int how) {
        final String txtValue = n.getNodeValue();
        String newNodeValue;
        String oldNodeValue;
        if (isLeft) {
            final int offset = this.getStartOffset();
            newNodeValue = txtValue.substring(offset);
            oldNodeValue = txtValue.substring(0, offset);
        }
        else {
            final int offset = this.getEndOffset();
            newNodeValue = txtValue.substring(0, offset);
            oldNodeValue = txtValue.substring(offset);
        }
        if (how != 2) {
            n.setNodeValue(oldNodeValue);
        }
        if (how == 3) {
            return null;
        }
        final Node newNode = n.cloneNode(false);
        newNode.setNodeValue(newNodeValue);
        return newNode;
    }
    
    void checkIndex(final Node refNode, final int offset) throws DOMException {
        if (offset < 0) {
            throw new DOMException((short)1, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", null));
        }
        final int type = refNode.getNodeType();
        if (type == 3 || type == 4 || type == 8 || type == 7) {
            if (offset > refNode.getNodeValue().length()) {
                throw new DOMException((short)1, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", null));
            }
        }
        else if (offset > refNode.getChildNodes().getLength()) {
            throw new DOMException((short)1, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", null));
        }
    }
    
    private Node getRootContainer(Node node) {
        if (node == null) {
            return null;
        }
        while (node.getParentNode() != null) {
            node = node.getParentNode();
        }
        return node;
    }
    
    private boolean isLegalContainer(Node node) {
        if (node == null) {
            return false;
        }
        while (node != null) {
            switch (node.getNodeType()) {
                case 6:
                case 10:
                case 12: {
                    return false;
                }
                default: {
                    node = node.getParentNode();
                    continue;
                }
            }
        }
        return true;
    }
    
    private boolean hasLegalRootContainer(final Node node) {
        if (node == null) {
            return false;
        }
        final Node rootContainer = this.getRootContainer(node);
        switch (rootContainer.getNodeType()) {
            case 2:
            case 9:
            case 11: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private boolean isLegalContainedNode(final Node node) {
        if (node == null) {
            return false;
        }
        switch (node.getNodeType()) {
            case 2:
            case 6:
            case 9:
            case 11:
            case 12: {
                return false;
            }
            default: {
                return true;
            }
        }
    }
    
    Node nextNode(final Node node, final boolean visitChildren) {
        if (node == null) {
            return null;
        }
        if (visitChildren) {
            final Node result = node.getFirstChild();
            if (result != null) {
                return result;
            }
        }
        Node result = node.getNextSibling();
        if (result != null) {
            return result;
        }
        for (Node parent = node.getParentNode(); parent != null && parent != this.fDocument; parent = parent.getParentNode()) {
            result = parent.getNextSibling();
            if (result != null) {
                return result;
            }
        }
        return null;
    }
    
    boolean isAncestorOf(final Node a, final Node b) {
        for (Node node = b; node != null; node = node.getParentNode()) {
            if (node == a) {
                return true;
            }
        }
        return false;
    }
    
    int indexOf(final Node child, final Node parent) {
        if (child.getParentNode() != parent) {
            return -1;
        }
        int i = 0;
        for (Node node = parent.getFirstChild(); node != child; node = node.getNextSibling()) {
            ++i;
        }
        return i;
    }
    
    private Node getSelectedNode(final Node container, int offset) {
        if (container.getNodeType() == 3) {
            return container;
        }
        if (offset < 0) {
            return container;
        }
        Node child;
        for (child = container.getFirstChild(); child != null && offset > 0; --offset, child = child.getNextSibling()) {}
        if (child != null) {
            return child;
        }
        return container;
    }
}
