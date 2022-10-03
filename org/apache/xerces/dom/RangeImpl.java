package org.apache.xerces.dom;

import org.w3c.dom.CharacterData;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.ranges.RangeException;
import java.util.ArrayList;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.ranges.Range;

public class RangeImpl implements Range
{
    private DocumentImpl fDocument;
    private Node fStartContainer;
    private Node fEndContainer;
    private int fStartOffset;
    private int fEndOffset;
    private boolean fDetach;
    private Node fInsertNode;
    private Node fDeleteNode;
    private Node fSplitNode;
    private boolean fInsertedFromRange;
    private Node fRemoveChild;
    static final int EXTRACT_CONTENTS = 1;
    static final int CLONE_CONTENTS = 2;
    static final int DELETE_CONTENTS = 3;
    
    public RangeImpl(final DocumentImpl fEndContainer) {
        this.fDetach = false;
        this.fInsertNode = null;
        this.fDeleteNode = null;
        this.fSplitNode = null;
        this.fInsertedFromRange = false;
        this.fRemoveChild = null;
        this.fDocument = fEndContainer;
        this.fStartContainer = fEndContainer;
        this.fEndContainer = fEndContainer;
        this.fStartOffset = 0;
        this.fEndOffset = 0;
        this.fDetach = false;
    }
    
    public Node getStartContainer() {
        if (this.fDetach) {
            throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
        }
        return this.fStartContainer;
    }
    
    public int getStartOffset() {
        if (this.fDetach) {
            throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
        }
        return this.fStartOffset;
    }
    
    public Node getEndContainer() {
        if (this.fDetach) {
            throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
        }
        return this.fEndContainer;
    }
    
    public int getEndOffset() {
        if (this.fDetach) {
            throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
        }
        return this.fEndOffset;
    }
    
    public boolean getCollapsed() {
        if (this.fDetach) {
            throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
        }
        return this.fStartContainer == this.fEndContainer && this.fStartOffset == this.fEndOffset;
    }
    
    public Node getCommonAncestorContainer() {
        if (this.fDetach) {
            throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
        }
        final ArrayList list = new ArrayList();
        for (Node node = this.fStartContainer; node != null; node = node.getParentNode()) {
            list.add(node);
        }
        final ArrayList list2 = new ArrayList<Node>();
        for (Node node2 = this.fEndContainer; node2 != null; node2 = node2.getParentNode()) {
            list2.add(node2);
        }
        int n = list.size() - 1;
        int n2 = list2.size() - 1;
        Object value = null;
        while (n >= 0 && n2 >= 0 && list.get(n) == list2.get(n2)) {
            value = list.get(n);
            --n;
            --n2;
        }
        return (Node)value;
    }
    
    public void setStart(final Node fStartContainer, final int fStartOffset) throws RangeException, DOMException {
        if (this.fDocument.errorChecking) {
            if (this.fDetach) {
                throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
            }
            if (!this.isLegalContainer(fStartContainer)) {
                throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null));
            }
            if (this.fDocument != fStartContainer.getOwnerDocument() && this.fDocument != fStartContainer) {
                throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
            }
        }
        this.checkIndex(fStartContainer, fStartOffset);
        this.fStartContainer = fStartContainer;
        this.fStartOffset = fStartOffset;
        if (this.getCommonAncestorContainer() == null || (this.fStartContainer == this.fEndContainer && this.fEndOffset < this.fStartOffset)) {
            this.collapse(true);
        }
    }
    
    public void setEnd(final Node fEndContainer, final int fEndOffset) throws RangeException, DOMException {
        if (this.fDocument.errorChecking) {
            if (this.fDetach) {
                throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
            }
            if (!this.isLegalContainer(fEndContainer)) {
                throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null));
            }
            if (this.fDocument != fEndContainer.getOwnerDocument() && this.fDocument != fEndContainer) {
                throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
            }
        }
        this.checkIndex(fEndContainer, fEndOffset);
        this.fEndContainer = fEndContainer;
        this.fEndOffset = fEndOffset;
        if (this.getCommonAncestorContainer() == null || (this.fStartContainer == this.fEndContainer && this.fEndOffset < this.fStartOffset)) {
            this.collapse(false);
        }
    }
    
    public void setStartBefore(final Node node) throws RangeException {
        if (this.fDocument.errorChecking) {
            if (this.fDetach) {
                throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
            }
            if (!this.hasLegalRootContainer(node) || !this.isLegalContainedNode(node)) {
                throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null));
            }
            if (this.fDocument != node.getOwnerDocument() && this.fDocument != node) {
                throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
            }
        }
        this.fStartContainer = node.getParentNode();
        int n = 0;
        for (Node previousSibling = node; previousSibling != null; previousSibling = previousSibling.getPreviousSibling()) {
            ++n;
        }
        this.fStartOffset = n - 1;
        if (this.getCommonAncestorContainer() == null || (this.fStartContainer == this.fEndContainer && this.fEndOffset < this.fStartOffset)) {
            this.collapse(true);
        }
    }
    
    public void setStartAfter(final Node node) throws RangeException {
        if (this.fDocument.errorChecking) {
            if (this.fDetach) {
                throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
            }
            if (!this.hasLegalRootContainer(node) || !this.isLegalContainedNode(node)) {
                throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null));
            }
            if (this.fDocument != node.getOwnerDocument() && this.fDocument != node) {
                throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
            }
        }
        this.fStartContainer = node.getParentNode();
        int fStartOffset = 0;
        for (Node previousSibling = node; previousSibling != null; previousSibling = previousSibling.getPreviousSibling()) {
            ++fStartOffset;
        }
        this.fStartOffset = fStartOffset;
        if (this.getCommonAncestorContainer() == null || (this.fStartContainer == this.fEndContainer && this.fEndOffset < this.fStartOffset)) {
            this.collapse(true);
        }
    }
    
    public void setEndBefore(final Node node) throws RangeException {
        if (this.fDocument.errorChecking) {
            if (this.fDetach) {
                throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
            }
            if (!this.hasLegalRootContainer(node) || !this.isLegalContainedNode(node)) {
                throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null));
            }
            if (this.fDocument != node.getOwnerDocument() && this.fDocument != node) {
                throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
            }
        }
        this.fEndContainer = node.getParentNode();
        int n = 0;
        for (Node previousSibling = node; previousSibling != null; previousSibling = previousSibling.getPreviousSibling()) {
            ++n;
        }
        this.fEndOffset = n - 1;
        if (this.getCommonAncestorContainer() == null || (this.fStartContainer == this.fEndContainer && this.fEndOffset < this.fStartOffset)) {
            this.collapse(false);
        }
    }
    
    public void setEndAfter(final Node node) throws RangeException {
        if (this.fDocument.errorChecking) {
            if (this.fDetach) {
                throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
            }
            if (!this.hasLegalRootContainer(node) || !this.isLegalContainedNode(node)) {
                throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null));
            }
            if (this.fDocument != node.getOwnerDocument() && this.fDocument != node) {
                throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
            }
        }
        this.fEndContainer = node.getParentNode();
        int fEndOffset = 0;
        for (Node previousSibling = node; previousSibling != null; previousSibling = previousSibling.getPreviousSibling()) {
            ++fEndOffset;
        }
        this.fEndOffset = fEndOffset;
        if (this.getCommonAncestorContainer() == null || (this.fStartContainer == this.fEndContainer && this.fEndOffset < this.fStartOffset)) {
            this.collapse(false);
        }
    }
    
    public void collapse(final boolean b) {
        if (this.fDetach) {
            throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
        }
        if (b) {
            this.fEndContainer = this.fStartContainer;
            this.fEndOffset = this.fStartOffset;
        }
        else {
            this.fStartContainer = this.fEndContainer;
            this.fStartOffset = this.fEndOffset;
        }
    }
    
    public void selectNode(final Node node) throws RangeException {
        if (this.fDocument.errorChecking) {
            if (this.fDetach) {
                throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
            }
            if (!this.isLegalContainer(node.getParentNode()) || !this.isLegalContainedNode(node)) {
                throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null));
            }
            if (this.fDocument != node.getOwnerDocument() && this.fDocument != node) {
                throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
            }
        }
        final Node parentNode = node.getParentNode();
        if (parentNode != null) {
            this.fStartContainer = parentNode;
            this.fEndContainer = parentNode;
            int n = 0;
            for (Node previousSibling = node; previousSibling != null; previousSibling = previousSibling.getPreviousSibling()) {
                ++n;
            }
            this.fStartOffset = n - 1;
            this.fEndOffset = this.fStartOffset + 1;
        }
    }
    
    public void selectNodeContents(final Node node) throws RangeException {
        if (this.fDocument.errorChecking) {
            if (this.fDetach) {
                throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
            }
            if (!this.isLegalContainer(node)) {
                throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null));
            }
            if (this.fDocument != node.getOwnerDocument() && this.fDocument != node) {
                throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
            }
        }
        this.fStartContainer = node;
        this.fEndContainer = node;
        final Node firstChild = node.getFirstChild();
        this.fStartOffset = 0;
        if (firstChild == null) {
            this.fEndOffset = 0;
        }
        else {
            int fEndOffset = 0;
            for (Node nextSibling = firstChild; nextSibling != null; nextSibling = nextSibling.getNextSibling()) {
                ++fEndOffset;
            }
            this.fEndOffset = fEndOffset;
        }
    }
    
    public short compareBoundaryPoints(final short n, final Range range) throws DOMException {
        if (this.fDocument.errorChecking) {
            if (this.fDetach) {
                throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
            }
            if ((this.fDocument != range.getStartContainer().getOwnerDocument() && this.fDocument != range.getStartContainer() && range.getStartContainer() != null) || (this.fDocument != range.getEndContainer().getOwnerDocument() && this.fDocument != range.getEndContainer() && range.getStartContainer() != null)) {
                throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
            }
        }
        Node node;
        Node node2;
        int n2;
        int n3;
        if (n == 0) {
            node = range.getStartContainer();
            node2 = this.fStartContainer;
            n2 = range.getStartOffset();
            n3 = this.fStartOffset;
        }
        else if (n == 1) {
            node = range.getStartContainer();
            node2 = this.fEndContainer;
            n2 = range.getStartOffset();
            n3 = this.fEndOffset;
        }
        else if (n == 3) {
            node = range.getEndContainer();
            node2 = this.fStartContainer;
            n2 = range.getEndOffset();
            n3 = this.fStartOffset;
        }
        else {
            node = range.getEndContainer();
            node2 = this.fEndContainer;
            n2 = range.getEndOffset();
            n3 = this.fEndOffset;
        }
        if (node != node2) {
            Node node3 = node2;
            Node node4 = node3.getParentNode();
            while (node4 != null) {
                if (node4 == node) {
                    if (n2 <= this.indexOf(node3, node)) {
                        return 1;
                    }
                    return -1;
                }
                else {
                    node3 = node4;
                    node4 = node4.getParentNode();
                }
            }
            Node node5 = node;
            Node node6 = node5.getParentNode();
            while (node6 != null) {
                if (node6 == node2) {
                    if (this.indexOf(node5, node2) < n3) {
                        return 1;
                    }
                    return -1;
                }
                else {
                    node5 = node6;
                    node6 = node6.getParentNode();
                }
            }
            int i = 0;
            for (Node parentNode = node; parentNode != null; parentNode = parentNode.getParentNode()) {
                ++i;
            }
            for (Node parentNode2 = node2; parentNode2 != null; parentNode2 = parentNode2.getParentNode()) {
                --i;
            }
            while (i > 0) {
                node = node.getParentNode();
                --i;
            }
            while (i < 0) {
                node2 = node2.getParentNode();
                ++i;
            }
            for (Node node7 = node.getParentNode(), node8 = node2.getParentNode(); node7 != node8; node7 = node7.getParentNode(), node8 = node8.getParentNode()) {
                node = node7;
                node2 = node8;
            }
            for (Node node9 = node.getNextSibling(); node9 != null; node9 = node9.getNextSibling()) {
                if (node9 == node2) {
                    return 1;
                }
            }
            return -1;
        }
        if (n2 < n3) {
            return 1;
        }
        if (n2 == n3) {
            return 0;
        }
        return -1;
    }
    
    public void deleteContents() throws DOMException {
        this.traverseContents(3);
    }
    
    public DocumentFragment extractContents() throws DOMException {
        return this.traverseContents(1);
    }
    
    public DocumentFragment cloneContents() throws DOMException {
        return this.traverseContents(2);
    }
    
    public void insertNode(final Node node) throws DOMException, RangeException {
        if (node == null) {
            return;
        }
        final short nodeType = node.getNodeType();
        if (this.fDocument.errorChecking) {
            if (this.fDetach) {
                throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
            }
            if (this.fDocument != node.getOwnerDocument()) {
                throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
            }
            if (nodeType == 2 || nodeType == 6 || nodeType == 12 || nodeType == 9) {
                throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null));
            }
        }
        int length = 0;
        this.fInsertedFromRange = true;
        if (this.fStartContainer.getNodeType() == 3) {
            final Node parentNode = this.fStartContainer.getParentNode();
            final int length2 = parentNode.getChildNodes().getLength();
            final Node cloneNode = this.fStartContainer.cloneNode(false);
            ((TextImpl)cloneNode).setNodeValueInternal(cloneNode.getNodeValue().substring(this.fStartOffset));
            ((TextImpl)this.fStartContainer).setNodeValueInternal(this.fStartContainer.getNodeValue().substring(0, this.fStartOffset));
            final Node nextSibling = this.fStartContainer.getNextSibling();
            if (nextSibling != null) {
                if (parentNode != null) {
                    parentNode.insertBefore(node, nextSibling);
                    parentNode.insertBefore(cloneNode, nextSibling);
                }
            }
            else if (parentNode != null) {
                parentNode.appendChild(node);
                parentNode.appendChild(cloneNode);
            }
            if (this.fEndContainer == this.fStartContainer) {
                this.fEndContainer = cloneNode;
                this.fEndOffset -= this.fStartOffset;
            }
            else if (this.fEndContainer == parentNode) {
                this.fEndOffset += parentNode.getChildNodes().getLength() - length2;
            }
            this.signalSplitData(this.fStartContainer, cloneNode, this.fStartOffset);
        }
        else {
            if (this.fEndContainer == this.fStartContainer) {
                length = this.fEndContainer.getChildNodes().getLength();
            }
            Node node2 = this.fStartContainer.getFirstChild();
            for (int n = 0; n < this.fStartOffset && node2 != null; node2 = node2.getNextSibling(), ++n) {}
            if (node2 != null) {
                this.fStartContainer.insertBefore(node, node2);
            }
            else {
                this.fStartContainer.appendChild(node);
            }
            if (this.fEndContainer == this.fStartContainer && this.fEndOffset != 0) {
                this.fEndOffset += this.fEndContainer.getChildNodes().getLength() - length;
            }
        }
        this.fInsertedFromRange = false;
    }
    
    public void surroundContents(final Node node) throws DOMException, RangeException {
        if (node == null) {
            return;
        }
        final short nodeType = node.getNodeType();
        if (this.fDocument.errorChecking) {
            if (this.fDetach) {
                throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
            }
            if (nodeType == 2 || nodeType == 6 || nodeType == 12 || nodeType == 10 || nodeType == 9 || nodeType == 11) {
                throw new RangeExceptionImpl((short)2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null));
            }
        }
        Node node2 = this.fStartContainer;
        Node node3 = this.fEndContainer;
        if (this.fStartContainer.getNodeType() == 3) {
            node2 = this.fStartContainer.getParentNode();
        }
        if (this.fEndContainer.getNodeType() == 3) {
            node3 = this.fEndContainer.getParentNode();
        }
        if (node2 != node3) {
            throw new RangeExceptionImpl((short)1, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "BAD_BOUNDARYPOINTS_ERR", null));
        }
        final DocumentFragment contents = this.extractContents();
        this.insertNode(node);
        node.appendChild(contents);
        this.selectNode(node);
    }
    
    public Range cloneRange() {
        if (this.fDetach) {
            throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
        }
        final Range range = this.fDocument.createRange();
        range.setStart(this.fStartContainer, this.fStartOffset);
        range.setEnd(this.fEndContainer, this.fEndOffset);
        return range;
    }
    
    public String toString() {
        if (this.fDetach) {
            throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
        }
        final Node fStartContainer = this.fStartContainer;
        Node node = this.fEndContainer;
        final StringBuffer sb = new StringBuffer();
        Node node2;
        if (this.fStartContainer.getNodeType() == 3 || this.fStartContainer.getNodeType() == 4) {
            if (this.fStartContainer == this.fEndContainer) {
                sb.append(this.fStartContainer.getNodeValue().substring(this.fStartOffset, this.fEndOffset));
                return sb.toString();
            }
            sb.append(this.fStartContainer.getNodeValue().substring(this.fStartOffset));
            node2 = this.nextNode(fStartContainer, true);
        }
        else {
            node2 = fStartContainer.getFirstChild();
            if (this.fStartOffset > 0) {
                for (int n = 0; n < this.fStartOffset && node2 != null; node2 = node2.getNextSibling(), ++n) {}
            }
            if (node2 == null) {
                node2 = this.nextNode(this.fStartContainer, false);
            }
        }
        if (this.fEndContainer.getNodeType() != 3 && this.fEndContainer.getNodeType() != 4) {
            int fEndOffset;
            for (fEndOffset = this.fEndOffset, node = this.fEndContainer.getFirstChild(); fEndOffset > 0 && node != null; --fEndOffset, node = node.getNextSibling()) {}
            if (node == null) {
                node = this.nextNode(this.fEndContainer, false);
            }
        }
        while (node2 != node && node2 != null) {
            if (node2.getNodeType() == 3 || node2.getNodeType() == 4) {
                sb.append(node2.getNodeValue());
            }
            node2 = this.nextNode(node2, true);
        }
        if (this.fEndContainer.getNodeType() == 3 || this.fEndContainer.getNodeType() == 4) {
            sb.append(this.fEndContainer.getNodeValue().substring(0, this.fEndOffset));
        }
        return sb.toString();
    }
    
    public void detach() {
        if (this.fDetach) {
            throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
        }
        this.fDetach = true;
        this.fDocument.removeRange(this);
    }
    
    void signalSplitData(final Node fSplitNode, final Node node, final int n) {
        this.fSplitNode = fSplitNode;
        this.fDocument.splitData(fSplitNode, node, n);
        this.fSplitNode = null;
    }
    
    void receiveSplitData(final Node node, final Node node2, final int n) {
        if (node == null || node2 == null) {
            return;
        }
        if (this.fSplitNode == node) {
            return;
        }
        if (node == this.fStartContainer && this.fStartContainer.getNodeType() == 3 && this.fStartOffset > n) {
            this.fStartOffset -= n;
            this.fStartContainer = node2;
        }
        if (node == this.fEndContainer && this.fEndContainer.getNodeType() == 3 && this.fEndOffset > n) {
            this.fEndOffset -= n;
            this.fEndContainer = node2;
        }
    }
    
    void deleteData(final CharacterData fDeleteNode, final int n, final int n2) {
        ((CharacterData)(this.fDeleteNode = fDeleteNode)).deleteData(n, n2);
        this.fDeleteNode = null;
    }
    
    void receiveDeletedText(final CharacterDataImpl characterDataImpl, final int n, final int n2) {
        if (characterDataImpl == null) {
            return;
        }
        if (this.fDeleteNode == characterDataImpl) {
            return;
        }
        if (characterDataImpl == this.fStartContainer) {
            if (this.fStartOffset > n + n2) {
                this.fStartOffset = n + (this.fStartOffset - (n + n2));
            }
            else if (this.fStartOffset > n) {
                this.fStartOffset = n;
            }
        }
        if (characterDataImpl == this.fEndContainer) {
            if (this.fEndOffset > n + n2) {
                this.fEndOffset = n + (this.fEndOffset - (n + n2));
            }
            else if (this.fEndOffset > n) {
                this.fEndOffset = n;
            }
        }
    }
    
    void insertData(final CharacterData fInsertNode, final int n, final String s) {
        ((CharacterData)(this.fInsertNode = fInsertNode)).insertData(n, s);
        this.fInsertNode = null;
    }
    
    void receiveInsertedText(final CharacterDataImpl characterDataImpl, final int n, final int n2) {
        if (characterDataImpl == null) {
            return;
        }
        if (this.fInsertNode == characterDataImpl) {
            return;
        }
        if (characterDataImpl == this.fStartContainer && n < this.fStartOffset) {
            this.fStartOffset += n2;
        }
        if (characterDataImpl == this.fEndContainer && n < this.fEndOffset) {
            this.fEndOffset += n2;
        }
    }
    
    void receiveReplacedText(final CharacterDataImpl characterDataImpl) {
        if (characterDataImpl == null) {
            return;
        }
        if (characterDataImpl == this.fStartContainer) {
            this.fStartOffset = 0;
        }
        if (characterDataImpl == this.fEndContainer) {
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
        final Node parentNode = node.getParentNode();
        if (parentNode == this.fStartContainer && this.indexOf(node, this.fStartContainer) < this.fStartOffset) {
            ++this.fStartOffset;
        }
        if (parentNode == this.fEndContainer && this.indexOf(node, this.fEndContainer) < this.fEndOffset) {
            ++this.fEndOffset;
        }
    }
    
    Node removeChild(final Node node, final Node fRemoveChild) {
        this.fRemoveChild = fRemoveChild;
        final Node removeChild = node.removeChild(fRemoveChild);
        this.fRemoveChild = null;
        return removeChild;
    }
    
    void removeNode(final Node node) {
        if (node == null) {
            return;
        }
        if (this.fRemoveChild == node) {
            return;
        }
        final Node parentNode = node.getParentNode();
        if (parentNode == this.fStartContainer && this.indexOf(node, this.fStartContainer) < this.fStartOffset) {
            --this.fStartOffset;
        }
        if (parentNode == this.fEndContainer && this.indexOf(node, this.fEndContainer) < this.fEndOffset) {
            --this.fEndOffset;
        }
        if (parentNode != this.fStartContainer || parentNode != this.fEndContainer) {
            if (this.isAncestorOf(node, this.fStartContainer)) {
                this.fStartContainer = parentNode;
                this.fStartOffset = this.indexOf(node, parentNode);
            }
            if (this.isAncestorOf(node, this.fEndContainer)) {
                this.fEndContainer = parentNode;
                this.fEndOffset = this.indexOf(node, parentNode);
            }
        }
    }
    
    private DocumentFragment traverseContents(final int n) throws DOMException {
        if (this.fStartContainer == null || this.fEndContainer == null) {
            return null;
        }
        if (this.fDetach) {
            throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
        }
        if (this.fStartContainer == this.fEndContainer) {
            return this.traverseSameContainer(n);
        }
        int n2 = 0;
        Node fEndContainer = this.fEndContainer;
        for (Node node = fEndContainer.getParentNode(); node != null; node = node.getParentNode()) {
            if (node == this.fStartContainer) {
                return this.traverseCommonStartContainer(fEndContainer, n);
            }
            ++n2;
            fEndContainer = node;
        }
        int n3 = 0;
        Node fStartContainer = this.fStartContainer;
        for (Node node2 = fStartContainer.getParentNode(); node2 != null; node2 = node2.getParentNode()) {
            if (node2 == this.fEndContainer) {
                return this.traverseCommonEndContainer(fStartContainer, n);
            }
            ++n3;
            fStartContainer = node2;
        }
        int i = n3 - n2;
        Node node3 = this.fStartContainer;
        while (i > 0) {
            node3 = node3.getParentNode();
            --i;
        }
        Node node4 = this.fEndContainer;
        while (i < 0) {
            node4 = node4.getParentNode();
            ++i;
        }
        for (Node node5 = node3.getParentNode(), node6 = node4.getParentNode(); node5 != node6; node5 = node5.getParentNode(), node6 = node6.getParentNode()) {
            node3 = node5;
            node4 = node6;
        }
        return this.traverseCommonAncestors(node3, node4, n);
    }
    
    private DocumentFragment traverseSameContainer(final int n) {
        DocumentFragment documentFragment = null;
        if (n != 3) {
            documentFragment = this.fDocument.createDocumentFragment();
        }
        if (this.fStartOffset == this.fEndOffset) {
            return documentFragment;
        }
        final short nodeType = this.fStartContainer.getNodeType();
        if (nodeType != 3 && nodeType != 4 && nodeType != 8 && nodeType != 7) {
            Node selectedNode = this.getSelectedNode(this.fStartContainer, this.fStartOffset);
            Node nextSibling;
            for (int i = this.fEndOffset - this.fStartOffset; i > 0; --i, selectedNode = nextSibling) {
                nextSibling = selectedNode.getNextSibling();
                final Node traverseFullySelected = this.traverseFullySelected(selectedNode, n);
                if (documentFragment != null) {
                    documentFragment.appendChild(traverseFullySelected);
                }
            }
            if (n != 2) {
                this.collapse(true);
            }
            return documentFragment;
        }
        final String substring = this.fStartContainer.getNodeValue().substring(this.fStartOffset, this.fEndOffset);
        if (n != 2) {
            ((CharacterDataImpl)this.fStartContainer).deleteData(this.fStartOffset, this.fEndOffset - this.fStartOffset);
            this.collapse(true);
        }
        if (n == 3) {
            return null;
        }
        if (nodeType == 3) {
            documentFragment.appendChild(this.fDocument.createTextNode(substring));
        }
        else if (nodeType == 4) {
            documentFragment.appendChild(this.fDocument.createCDATASection(substring));
        }
        else if (nodeType == 8) {
            documentFragment.appendChild(this.fDocument.createComment(substring));
        }
        else {
            documentFragment.appendChild(this.fDocument.createProcessingInstruction(this.fStartContainer.getNodeName(), substring));
        }
        return documentFragment;
    }
    
    private DocumentFragment traverseCommonStartContainer(final Node node, final int n) {
        DocumentFragment documentFragment = null;
        if (n != 3) {
            documentFragment = this.fDocument.createDocumentFragment();
        }
        final Node traverseRightBoundary = this.traverseRightBoundary(node, n);
        if (documentFragment != null) {
            documentFragment.appendChild(traverseRightBoundary);
        }
        int i = this.indexOf(node, this.fStartContainer) - this.fStartOffset;
        if (i <= 0) {
            if (n != 2) {
                this.setEndBefore(node);
                this.collapse(false);
            }
            return documentFragment;
        }
        Node previousSibling = node.getPreviousSibling();
        while (i > 0) {
            final Node previousSibling2 = previousSibling.getPreviousSibling();
            final Node traverseFullySelected = this.traverseFullySelected(previousSibling, n);
            if (documentFragment != null) {
                documentFragment.insertBefore(traverseFullySelected, documentFragment.getFirstChild());
            }
            --i;
            previousSibling = previousSibling2;
        }
        if (n != 2) {
            this.setEndBefore(node);
            this.collapse(false);
        }
        return documentFragment;
    }
    
    private DocumentFragment traverseCommonEndContainer(final Node startAfter, final int n) {
        DocumentFragment documentFragment = null;
        if (n != 3) {
            documentFragment = this.fDocument.createDocumentFragment();
        }
        final Node traverseLeftBoundary = this.traverseLeftBoundary(startAfter, n);
        if (documentFragment != null) {
            documentFragment.appendChild(traverseLeftBoundary);
        }
        int index = this.indexOf(startAfter, this.fEndContainer);
        ++index;
        int i = this.fEndOffset - index;
        Node nextSibling = startAfter.getNextSibling();
        while (i > 0) {
            final Node nextSibling2 = nextSibling.getNextSibling();
            final Node traverseFullySelected = this.traverseFullySelected(nextSibling, n);
            if (documentFragment != null) {
                documentFragment.appendChild(traverseFullySelected);
            }
            --i;
            nextSibling = nextSibling2;
        }
        if (n != 2) {
            this.setStartAfter(startAfter);
            this.collapse(true);
        }
        return documentFragment;
    }
    
    private DocumentFragment traverseCommonAncestors(final Node startAfter, final Node node, final int n) {
        DocumentFragment documentFragment = null;
        if (n != 3) {
            documentFragment = this.fDocument.createDocumentFragment();
        }
        final Node traverseLeftBoundary = this.traverseLeftBoundary(startAfter, n);
        if (documentFragment != null) {
            documentFragment.appendChild(traverseLeftBoundary);
        }
        final Node parentNode = startAfter.getParentNode();
        int index = this.indexOf(startAfter, parentNode);
        final int index2 = this.indexOf(node, parentNode);
        ++index;
        int i = index2 - index;
        Node nextSibling = startAfter.getNextSibling();
        while (i > 0) {
            final Node nextSibling2 = nextSibling.getNextSibling();
            final Node traverseFullySelected = this.traverseFullySelected(nextSibling, n);
            if (documentFragment != null) {
                documentFragment.appendChild(traverseFullySelected);
            }
            nextSibling = nextSibling2;
            --i;
        }
        final Node traverseRightBoundary = this.traverseRightBoundary(node, n);
        if (documentFragment != null) {
            documentFragment.appendChild(traverseRightBoundary);
        }
        if (n != 2) {
            this.setStartAfter(startAfter);
            this.collapse(true);
        }
        return documentFragment;
    }
    
    private Node traverseRightBoundary(final Node node, final int n) {
        Node node2 = this.getSelectedNode(this.fEndContainer, this.fEndOffset - 1);
        boolean b = node2 != this.fEndContainer;
        if (node2 == node) {
            return this.traverseNode(node2, b, false, n);
        }
        Node node3 = node2.getParentNode();
        Node traverseNode = this.traverseNode(node3, false, false, n);
        while (node3 != null) {
            while (node2 != null) {
                final Node previousSibling = node2.getPreviousSibling();
                final Node traverseNode2 = this.traverseNode(node2, b, false, n);
                if (n != 3) {
                    traverseNode.insertBefore(traverseNode2, traverseNode.getFirstChild());
                }
                b = true;
                node2 = previousSibling;
            }
            if (node3 == node) {
                return traverseNode;
            }
            node2 = node3.getPreviousSibling();
            node3 = node3.getParentNode();
            final Node traverseNode3 = this.traverseNode(node3, false, false, n);
            if (n != 3) {
                traverseNode3.appendChild(traverseNode);
            }
            traverseNode = traverseNode3;
        }
        return null;
    }
    
    private Node traverseLeftBoundary(final Node node, final int n) {
        Node node2 = this.getSelectedNode(this.getStartContainer(), this.getStartOffset());
        boolean b = node2 != this.getStartContainer();
        if (node2 == node) {
            return this.traverseNode(node2, b, true, n);
        }
        Node node3 = node2.getParentNode();
        Node traverseNode = this.traverseNode(node3, false, true, n);
        while (node3 != null) {
            while (node2 != null) {
                final Node nextSibling = node2.getNextSibling();
                final Node traverseNode2 = this.traverseNode(node2, b, true, n);
                if (n != 3) {
                    traverseNode.appendChild(traverseNode2);
                }
                b = true;
                node2 = nextSibling;
            }
            if (node3 == node) {
                return traverseNode;
            }
            node2 = node3.getNextSibling();
            node3 = node3.getParentNode();
            final Node traverseNode3 = this.traverseNode(node3, false, true, n);
            if (n != 3) {
                traverseNode3.appendChild(traverseNode);
            }
            traverseNode = traverseNode3;
        }
        return null;
    }
    
    private Node traverseNode(final Node node, final boolean b, final boolean b2, final int n) {
        if (b) {
            return this.traverseFullySelected(node, n);
        }
        final short nodeType = node.getNodeType();
        if (nodeType == 3 || nodeType == 4 || nodeType == 8 || nodeType == 7) {
            return this.traverseCharacterDataNode(node, b2, n);
        }
        return this.traversePartiallySelected(node, n);
    }
    
    private Node traverseFullySelected(final Node node, final int n) {
        switch (n) {
            case 2: {
                return node.cloneNode(true);
            }
            case 1: {
                if (node.getNodeType() == 10) {
                    throw new DOMException((short)3, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null));
                }
                return node;
            }
            case 3: {
                node.getParentNode().removeChild(node);
                return null;
            }
            default: {
                return null;
            }
        }
    }
    
    private Node traversePartiallySelected(final Node node, final int n) {
        switch (n) {
            case 3: {
                return null;
            }
            case 1:
            case 2: {
                return node.cloneNode(false);
            }
            default: {
                return null;
            }
        }
    }
    
    private Node traverseCharacterDataNode(final Node node, final boolean b, final int n) {
        final String nodeValue = node.getNodeValue();
        String nodeValue2;
        String nodeValue3;
        if (b) {
            final int startOffset = this.getStartOffset();
            nodeValue2 = nodeValue.substring(startOffset);
            nodeValue3 = nodeValue.substring(0, startOffset);
        }
        else {
            final int endOffset = this.getEndOffset();
            nodeValue2 = nodeValue.substring(0, endOffset);
            nodeValue3 = nodeValue.substring(endOffset);
        }
        if (n != 2) {
            node.setNodeValue(nodeValue3);
        }
        if (n == 3) {
            return null;
        }
        final Node cloneNode = node.cloneNode(false);
        cloneNode.setNodeValue(nodeValue2);
        return cloneNode;
    }
    
    void checkIndex(final Node node, final int n) throws DOMException {
        if (n < 0) {
            throw new DOMException((short)1, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", null));
        }
        final short nodeType = node.getNodeType();
        if (nodeType == 3 || nodeType == 4 || nodeType == 8 || nodeType == 7) {
            if (n > node.getNodeValue().length()) {
                throw new DOMException((short)1, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", null));
            }
        }
        else if (n > node.getChildNodes().getLength()) {
            throw new DOMException((short)1, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", null));
        }
    }
    
    private Node getRootContainer(Node parentNode) {
        if (parentNode == null) {
            return null;
        }
        while (parentNode.getParentNode() != null) {
            parentNode = parentNode.getParentNode();
        }
        return parentNode;
    }
    
    private boolean isLegalContainer(Node parentNode) {
        if (parentNode == null) {
            return false;
        }
        while (parentNode != null) {
            switch (parentNode.getNodeType()) {
                case 6:
                case 10:
                case 12: {
                    return false;
                }
                default: {
                    parentNode = parentNode.getParentNode();
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
        switch (this.getRootContainer(node).getNodeType()) {
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
    
    Node nextNode(final Node node, final boolean b) {
        if (node == null) {
            return null;
        }
        if (b) {
            final Node firstChild = node.getFirstChild();
            if (firstChild != null) {
                return firstChild;
            }
        }
        final Node nextSibling = node.getNextSibling();
        if (nextSibling != null) {
            return nextSibling;
        }
        for (Node node2 = node.getParentNode(); node2 != null && node2 != this.fDocument; node2 = node2.getParentNode()) {
            final Node nextSibling2 = node2.getNextSibling();
            if (nextSibling2 != null) {
                return nextSibling2;
            }
        }
        return null;
    }
    
    boolean isAncestorOf(final Node node, final Node node2) {
        for (Node parentNode = node2; parentNode != null; parentNode = parentNode.getParentNode()) {
            if (parentNode == node) {
                return true;
            }
        }
        return false;
    }
    
    int indexOf(final Node node, final Node node2) {
        if (node.getParentNode() != node2) {
            return -1;
        }
        int n = 0;
        for (Node node3 = node2.getFirstChild(); node3 != node; node3 = node3.getNextSibling()) {
            ++n;
        }
        return n;
    }
    
    private Node getSelectedNode(final Node node, int n) {
        if (node.getNodeType() == 3) {
            return node;
        }
        if (n < 0) {
            return node;
        }
        Node node2;
        for (node2 = node.getFirstChild(); node2 != null && n > 0; --n, node2 = node2.getNextSibling()) {}
        if (node2 != null) {
            return node2;
        }
        return node;
    }
}
