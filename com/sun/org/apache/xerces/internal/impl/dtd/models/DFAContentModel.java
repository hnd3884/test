package com.sun.org.apache.xerces.internal.impl.dtd.models;

import java.util.HashMap;
import com.sun.org.apache.xerces.internal.xni.QName;

public class DFAContentModel implements ContentModelValidator
{
    private static String fEpsilonString;
    private static String fEOCString;
    private static final boolean DEBUG_VALIDATE_CONTENT = false;
    private QName[] fElemMap;
    private int[] fElemMapType;
    private int fElemMapSize;
    private boolean fMixed;
    private int fEOCPos;
    private boolean[] fFinalStateFlags;
    private CMStateSet[] fFollowList;
    private CMNode fHeadNode;
    private int fLeafCount;
    private CMLeaf[] fLeafList;
    private int[] fLeafListType;
    private int[][] fTransTable;
    private int fTransTableSize;
    private boolean fEmptyContentIsValid;
    private final QName fQName;
    
    public DFAContentModel(final CMNode syntaxTree, final int leafCount, final boolean mixed) {
        this.fElemMap = null;
        this.fElemMapType = null;
        this.fElemMapSize = 0;
        this.fEOCPos = 0;
        this.fFinalStateFlags = null;
        this.fFollowList = null;
        this.fHeadNode = null;
        this.fLeafCount = 0;
        this.fLeafList = null;
        this.fLeafListType = null;
        this.fTransTable = null;
        this.fTransTableSize = 0;
        this.fEmptyContentIsValid = false;
        this.fQName = new QName();
        this.fLeafCount = leafCount;
        this.fMixed = mixed;
        this.buildDFA(syntaxTree);
    }
    
    @Override
    public int validate(final QName[] children, final int offset, final int length) {
        if (length == 0) {
            return this.fEmptyContentIsValid ? -1 : 0;
        }
        int curState = 0;
        for (int childIndex = 0; childIndex < length; ++childIndex) {
            final QName curElem = children[offset + childIndex];
            if (!this.fMixed || curElem.localpart != null) {
                int elemIndex;
                for (elemIndex = 0; elemIndex < this.fElemMapSize; ++elemIndex) {
                    final int type = this.fElemMapType[elemIndex] & 0xF;
                    if (type == 0) {
                        if (this.fElemMap[elemIndex].rawname == curElem.rawname) {
                            break;
                        }
                    }
                    else if (type == 6) {
                        final String uri = this.fElemMap[elemIndex].uri;
                        if (uri == null) {
                            break;
                        }
                        if (uri == curElem.uri) {
                            break;
                        }
                    }
                    else if (type == 8) {
                        if (curElem.uri == null) {
                            break;
                        }
                    }
                    else if (type == 7 && this.fElemMap[elemIndex].uri != curElem.uri) {
                        break;
                    }
                }
                if (elemIndex == this.fElemMapSize) {
                    return childIndex;
                }
                curState = this.fTransTable[curState][elemIndex];
                if (curState == -1) {
                    return childIndex;
                }
            }
        }
        if (!this.fFinalStateFlags[curState]) {
            return length;
        }
        return -1;
    }
    
    private void buildDFA(final CMNode syntaxTree) {
        this.fQName.setValues(null, DFAContentModel.fEOCString, DFAContentModel.fEOCString, null);
        final CMLeaf nodeEOC = new CMLeaf(this.fQName);
        this.fHeadNode = new CMBinOp(5, syntaxTree, nodeEOC);
        this.fEOCPos = this.fLeafCount;
        nodeEOC.setPosition(this.fLeafCount++);
        this.fLeafList = new CMLeaf[this.fLeafCount];
        this.fLeafListType = new int[this.fLeafCount];
        this.postTreeBuildInit(this.fHeadNode, 0);
        this.fFollowList = new CMStateSet[this.fLeafCount];
        for (int index = 0; index < this.fLeafCount; ++index) {
            this.fFollowList[index] = new CMStateSet(this.fLeafCount);
        }
        this.calcFollowList(this.fHeadNode);
        this.fElemMap = new QName[this.fLeafCount];
        this.fElemMapType = new int[this.fLeafCount];
        this.fElemMapSize = 0;
        for (int outIndex = 0; outIndex < this.fLeafCount; ++outIndex) {
            this.fElemMap[outIndex] = new QName();
            QName element;
            int inIndex;
            for (element = this.fLeafList[outIndex].getElement(), inIndex = 0; inIndex < this.fElemMapSize && this.fElemMap[inIndex].rawname != element.rawname; ++inIndex) {}
            if (inIndex == this.fElemMapSize) {
                this.fElemMap[this.fElemMapSize].setValues(element);
                this.fElemMapType[this.fElemMapSize] = this.fLeafListType[outIndex];
                ++this.fElemMapSize;
            }
        }
        final int[] fLeafSorter = new int[this.fLeafCount + this.fElemMapSize];
        int fSortCount = 0;
        for (int elemIndex = 0; elemIndex < this.fElemMapSize; ++elemIndex) {
            for (int leafIndex = 0; leafIndex < this.fLeafCount; ++leafIndex) {
                final QName leaf = this.fLeafList[leafIndex].getElement();
                final QName element2 = this.fElemMap[elemIndex];
                if (leaf.rawname == element2.rawname) {
                    fLeafSorter[fSortCount++] = leafIndex;
                }
            }
            fLeafSorter[fSortCount++] = -1;
        }
        int curArraySize = this.fLeafCount * 4;
        CMStateSet[] statesToDo = new CMStateSet[curArraySize];
        this.fFinalStateFlags = new boolean[curArraySize];
        this.fTransTable = new int[curArraySize][];
        CMStateSet setT = this.fHeadNode.firstPos();
        int unmarkedState = 0;
        int curState = 0;
        this.fTransTable[curState] = this.makeDefStateList();
        statesToDo[curState] = setT;
        ++curState;
        final HashMap stateTable = new HashMap();
        while (unmarkedState < curState) {
            setT = statesToDo[unmarkedState];
            final int[] transEntry = this.fTransTable[unmarkedState];
            this.fFinalStateFlags[unmarkedState] = setT.getBit(this.fEOCPos);
            ++unmarkedState;
            CMStateSet newSet = null;
            int sorterIndex = 0;
            for (int elemIndex2 = 0; elemIndex2 < this.fElemMapSize; ++elemIndex2) {
                if (newSet == null) {
                    newSet = new CMStateSet(this.fLeafCount);
                }
                else {
                    newSet.zeroBits();
                }
                for (int leafIndex2 = fLeafSorter[sorterIndex++]; leafIndex2 != -1; leafIndex2 = fLeafSorter[sorterIndex++]) {
                    if (setT.getBit(leafIndex2)) {
                        newSet.union(this.fFollowList[leafIndex2]);
                    }
                }
                if (!newSet.isEmpty()) {
                    final Integer stateObj = stateTable.get(newSet);
                    final int stateIndex = (stateObj == null) ? curState : stateObj;
                    if (stateIndex == curState) {
                        statesToDo[curState] = newSet;
                        this.fTransTable[curState] = this.makeDefStateList();
                        stateTable.put(newSet, new Integer(curState));
                        ++curState;
                        newSet = null;
                    }
                    transEntry[elemIndex2] = stateIndex;
                    if (curState == curArraySize) {
                        final int newSize = (int)(curArraySize * 1.5);
                        final CMStateSet[] newToDo = new CMStateSet[newSize];
                        final boolean[] newFinalFlags = new boolean[newSize];
                        final int[][] newTransTable = new int[newSize][];
                        System.arraycopy(statesToDo, 0, newToDo, 0, curArraySize);
                        System.arraycopy(this.fFinalStateFlags, 0, newFinalFlags, 0, curArraySize);
                        System.arraycopy(this.fTransTable, 0, newTransTable, 0, curArraySize);
                        curArraySize = newSize;
                        statesToDo = newToDo;
                        this.fFinalStateFlags = newFinalFlags;
                        this.fTransTable = newTransTable;
                    }
                }
            }
        }
        this.fEmptyContentIsValid = ((CMBinOp)this.fHeadNode).getLeft().isNullable();
        this.fHeadNode = null;
        this.fLeafList = null;
        this.fFollowList = null;
    }
    
    private void calcFollowList(final CMNode nodeCur) {
        if (nodeCur.type() == 4) {
            this.calcFollowList(((CMBinOp)nodeCur).getLeft());
            this.calcFollowList(((CMBinOp)nodeCur).getRight());
        }
        else if (nodeCur.type() == 5) {
            this.calcFollowList(((CMBinOp)nodeCur).getLeft());
            this.calcFollowList(((CMBinOp)nodeCur).getRight());
            final CMStateSet last = ((CMBinOp)nodeCur).getLeft().lastPos();
            final CMStateSet first = ((CMBinOp)nodeCur).getRight().firstPos();
            for (int index = 0; index < this.fLeafCount; ++index) {
                if (last.getBit(index)) {
                    this.fFollowList[index].union(first);
                }
            }
        }
        else if (nodeCur.type() == 2 || nodeCur.type() == 3) {
            this.calcFollowList(((CMUniOp)nodeCur).getChild());
            final CMStateSet first2 = nodeCur.firstPos();
            final CMStateSet last2 = nodeCur.lastPos();
            for (int index = 0; index < this.fLeafCount; ++index) {
                if (last2.getBit(index)) {
                    this.fFollowList[index].union(first2);
                }
            }
        }
        else if (nodeCur.type() == 1) {
            this.calcFollowList(((CMUniOp)nodeCur).getChild());
        }
    }
    
    private void dumpTree(final CMNode nodeCur, final int level) {
        for (int index = 0; index < level; ++index) {
            System.out.print("   ");
        }
        final int type = nodeCur.type();
        if (type == 4 || type == 5) {
            if (type == 4) {
                System.out.print("Choice Node ");
            }
            else {
                System.out.print("Seq Node ");
            }
            if (nodeCur.isNullable()) {
                System.out.print("Nullable ");
            }
            System.out.print("firstPos=");
            System.out.print(nodeCur.firstPos().toString());
            System.out.print(" lastPos=");
            System.out.println(nodeCur.lastPos().toString());
            this.dumpTree(((CMBinOp)nodeCur).getLeft(), level + 1);
            this.dumpTree(((CMBinOp)nodeCur).getRight(), level + 1);
        }
        else if (nodeCur.type() == 2) {
            System.out.print("Rep Node ");
            if (nodeCur.isNullable()) {
                System.out.print("Nullable ");
            }
            System.out.print("firstPos=");
            System.out.print(nodeCur.firstPos().toString());
            System.out.print(" lastPos=");
            System.out.println(nodeCur.lastPos().toString());
            this.dumpTree(((CMUniOp)nodeCur).getChild(), level + 1);
        }
        else {
            if (nodeCur.type() != 0) {
                throw new RuntimeException("ImplementationMessages.VAL_NIICM");
            }
            System.out.print("Leaf: (pos=" + ((CMLeaf)nodeCur).getPosition() + "), " + ((CMLeaf)nodeCur).getElement() + "(elemIndex=" + ((CMLeaf)nodeCur).getElement() + ") ");
            if (nodeCur.isNullable()) {
                System.out.print(" Nullable ");
            }
            System.out.print("firstPos=");
            System.out.print(nodeCur.firstPos().toString());
            System.out.print(" lastPos=");
            System.out.println(nodeCur.lastPos().toString());
        }
    }
    
    private int[] makeDefStateList() {
        final int[] retArray = new int[this.fElemMapSize];
        for (int index = 0; index < this.fElemMapSize; ++index) {
            retArray[index] = -1;
        }
        return retArray;
    }
    
    private int postTreeBuildInit(final CMNode nodeCur, int curIndex) {
        nodeCur.setMaxStates(this.fLeafCount);
        if ((nodeCur.type() & 0xF) == 0x6 || (nodeCur.type() & 0xF) == 0x8 || (nodeCur.type() & 0xF) == 0x7) {
            final QName qname = new QName(null, null, null, ((CMAny)nodeCur).getURI());
            this.fLeafList[curIndex] = new CMLeaf(qname, ((CMAny)nodeCur).getPosition());
            this.fLeafListType[curIndex] = nodeCur.type();
            ++curIndex;
        }
        else if (nodeCur.type() == 4 || nodeCur.type() == 5) {
            curIndex = this.postTreeBuildInit(((CMBinOp)nodeCur).getLeft(), curIndex);
            curIndex = this.postTreeBuildInit(((CMBinOp)nodeCur).getRight(), curIndex);
        }
        else if (nodeCur.type() == 2 || nodeCur.type() == 3 || nodeCur.type() == 1) {
            curIndex = this.postTreeBuildInit(((CMUniOp)nodeCur).getChild(), curIndex);
        }
        else {
            if (nodeCur.type() != 0) {
                throw new RuntimeException("ImplementationMessages.VAL_NIICM: type=" + nodeCur.type());
            }
            final QName node = ((CMLeaf)nodeCur).getElement();
            if (node.localpart != DFAContentModel.fEpsilonString) {
                this.fLeafList[curIndex] = (CMLeaf)nodeCur;
                this.fLeafListType[curIndex] = 0;
                ++curIndex;
            }
        }
        return curIndex;
    }
    
    static {
        DFAContentModel.fEpsilonString = "<<CMNODE_EPSILON>>";
        DFAContentModel.fEOCString = "<<CMNODE_EOC>>";
        DFAContentModel.fEpsilonString = DFAContentModel.fEpsilonString.intern();
        DFAContentModel.fEOCString = DFAContentModel.fEOCString.intern();
    }
}
