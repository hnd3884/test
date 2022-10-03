package com.sun.org.apache.xerces.internal.impl.xs.models;

import java.util.ArrayList;
import java.util.Vector;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaException;
import com.sun.org.apache.xerces.internal.impl.xs.XSConstraints;
import java.util.HashMap;
import com.sun.org.apache.xerces.internal.impl.xs.XSWildcardDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSElementDecl;
import com.sun.org.apache.xerces.internal.impl.xs.SubstitutionGroupHandler;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.impl.dtd.models.CMNode;
import com.sun.org.apache.xerces.internal.impl.dtd.models.CMStateSet;

public class XSDFACM implements XSCMValidator
{
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_VALIDATE_CONTENT = false;
    private Object[] fElemMap;
    private int[] fElemMapType;
    private int[] fElemMapId;
    private int fElemMapSize;
    private boolean[] fFinalStateFlags;
    private CMStateSet[] fFollowList;
    private CMNode fHeadNode;
    private int fLeafCount;
    private XSCMLeaf[] fLeafList;
    private int[] fLeafListType;
    private int[][] fTransTable;
    private Occurence[] fCountingStates;
    private int fTransTableSize;
    private int[] fElemMapCounter;
    private int[] fElemMapCounterLowerBound;
    private int[] fElemMapCounterUpperBound;
    private static long time;
    
    public XSDFACM(final CMNode syntaxTree, final int leafCount) {
        this.fElemMap = null;
        this.fElemMapType = null;
        this.fElemMapId = null;
        this.fElemMapSize = 0;
        this.fFinalStateFlags = null;
        this.fFollowList = null;
        this.fHeadNode = null;
        this.fLeafCount = 0;
        this.fLeafList = null;
        this.fLeafListType = null;
        this.fTransTable = null;
        this.fCountingStates = null;
        this.fTransTableSize = 0;
        this.fLeafCount = leafCount;
        this.buildDFA(syntaxTree);
    }
    
    public boolean isFinalState(final int state) {
        return state >= 0 && this.fFinalStateFlags[state];
    }
    
    @Override
    public Object oneTransition(final QName curElem, final int[] state, final SubstitutionGroupHandler subGroupHandler) {
        final int curState = state[0];
        if (curState == -1 || curState == -2) {
            if (curState == -1) {
                state[0] = -2;
            }
            return this.findMatchingDecl(curElem, subGroupHandler);
        }
        int nextState = 0;
        int elemIndex = 0;
        Object matchingDecl = null;
        while (elemIndex < this.fElemMapSize) {
            nextState = this.fTransTable[curState][elemIndex];
            if (nextState != -1) {
                final int type = this.fElemMapType[elemIndex];
                if (type == 1) {
                    matchingDecl = subGroupHandler.getMatchingElemDecl(curElem, (XSElementDecl)this.fElemMap[elemIndex]);
                    if (matchingDecl != null) {
                        if (this.fElemMapCounter[elemIndex] >= 0) {
                            final int[] fElemMapCounter = this.fElemMapCounter;
                            final int n = elemIndex;
                            ++fElemMapCounter[n];
                            break;
                        }
                        break;
                    }
                }
                else if (type == 2 && ((XSWildcardDecl)this.fElemMap[elemIndex]).allowNamespace(curElem.uri)) {
                    matchingDecl = this.fElemMap[elemIndex];
                    if (this.fElemMapCounter[elemIndex] >= 0) {
                        final int[] fElemMapCounter2 = this.fElemMapCounter;
                        final int n2 = elemIndex;
                        ++fElemMapCounter2[n2];
                        break;
                    }
                    break;
                }
            }
            ++elemIndex;
        }
        if (elemIndex == this.fElemMapSize) {
            state[1] = state[0];
            state[0] = -1;
            return this.findMatchingDecl(curElem, subGroupHandler);
        }
        if (this.fCountingStates != null) {
            Occurence o = this.fCountingStates[curState];
            if (o != null) {
                if (curState == nextState) {
                    if (++state[2] > o.maxOccurs && o.maxOccurs != -1) {
                        return this.findMatchingDecl(curElem, state, subGroupHandler, elemIndex);
                    }
                }
                else {
                    if (state[2] < o.minOccurs) {
                        state[1] = state[0];
                        state[0] = -1;
                        return this.findMatchingDecl(curElem, subGroupHandler);
                    }
                    o = this.fCountingStates[nextState];
                    if (o != null) {
                        state[2] = ((elemIndex == o.elemIndex) ? 1 : 0);
                    }
                }
            }
            else {
                o = this.fCountingStates[nextState];
                if (o != null) {
                    state[2] = ((elemIndex == o.elemIndex) ? 1 : 0);
                }
            }
        }
        state[0] = nextState;
        return matchingDecl;
    }
    
    Object findMatchingDecl(final QName curElem, final SubstitutionGroupHandler subGroupHandler) {
        Object matchingDecl = null;
        for (int elemIndex = 0; elemIndex < this.fElemMapSize; ++elemIndex) {
            final int type = this.fElemMapType[elemIndex];
            if (type == 1) {
                matchingDecl = subGroupHandler.getMatchingElemDecl(curElem, (XSElementDecl)this.fElemMap[elemIndex]);
                if (matchingDecl != null) {
                    return matchingDecl;
                }
            }
            else if (type == 2 && ((XSWildcardDecl)this.fElemMap[elemIndex]).allowNamespace(curElem.uri)) {
                return this.fElemMap[elemIndex];
            }
        }
        return null;
    }
    
    Object findMatchingDecl(final QName curElem, final int[] state, final SubstitutionGroupHandler subGroupHandler, int elemIndex) {
        final int curState = state[0];
        int nextState = 0;
        Object matchingDecl = null;
        while (++elemIndex < this.fElemMapSize) {
            nextState = this.fTransTable[curState][elemIndex];
            if (nextState == -1) {
                continue;
            }
            final int type = this.fElemMapType[elemIndex];
            if (type == 1) {
                matchingDecl = subGroupHandler.getMatchingElemDecl(curElem, (XSElementDecl)this.fElemMap[elemIndex]);
                if (matchingDecl != null) {
                    break;
                }
                continue;
            }
            else {
                if (type == 2 && ((XSWildcardDecl)this.fElemMap[elemIndex]).allowNamespace(curElem.uri)) {
                    matchingDecl = this.fElemMap[elemIndex];
                    break;
                }
                continue;
            }
        }
        if (elemIndex == this.fElemMapSize) {
            state[1] = state[0];
            state[0] = -1;
            return this.findMatchingDecl(curElem, subGroupHandler);
        }
        state[0] = nextState;
        final Occurence o = this.fCountingStates[nextState];
        if (o != null) {
            state[2] = ((elemIndex == o.elemIndex) ? 1 : 0);
        }
        return matchingDecl;
    }
    
    @Override
    public int[] startContentModel() {
        for (int elemIndex = 0; elemIndex < this.fElemMapSize; ++elemIndex) {
            if (this.fElemMapCounter[elemIndex] != -1) {
                this.fElemMapCounter[elemIndex] = 0;
            }
        }
        return new int[3];
    }
    
    @Override
    public boolean endContentModel(final int[] state) {
        final int curState = state[0];
        if (this.fFinalStateFlags[curState]) {
            if (this.fCountingStates != null) {
                final Occurence o = this.fCountingStates[curState];
                if (o != null && state[2] < o.minOccurs) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    private void buildDFA(final CMNode syntaxTree) {
        final int EOCPos = this.fLeafCount;
        final XSCMLeaf nodeEOC = new XSCMLeaf(1, null, -1, this.fLeafCount++);
        this.fHeadNode = new XSCMBinOp(102, syntaxTree, nodeEOC);
        this.fLeafList = new XSCMLeaf[this.fLeafCount];
        this.fLeafListType = new int[this.fLeafCount];
        this.postTreeBuildInit(this.fHeadNode);
        this.fFollowList = new CMStateSet[this.fLeafCount];
        for (int index = 0; index < this.fLeafCount; ++index) {
            this.fFollowList[index] = new CMStateSet(this.fLeafCount);
        }
        this.calcFollowList(this.fHeadNode);
        this.fElemMap = new Object[this.fLeafCount];
        this.fElemMapType = new int[this.fLeafCount];
        this.fElemMapId = new int[this.fLeafCount];
        this.fElemMapCounter = new int[this.fLeafCount];
        this.fElemMapCounterLowerBound = new int[this.fLeafCount];
        this.fElemMapCounterUpperBound = new int[this.fLeafCount];
        this.fElemMapSize = 0;
        Occurence[] elemOccurenceMap = null;
        for (int outIndex = 0; outIndex < this.fLeafCount; ++outIndex) {
            this.fElemMap[outIndex] = null;
            int inIndex;
            int id;
            for (inIndex = 0, id = this.fLeafList[outIndex].getParticleId(); inIndex < this.fElemMapSize && id != this.fElemMapId[inIndex]; ++inIndex) {}
            if (inIndex == this.fElemMapSize) {
                final XSCMLeaf leaf = this.fLeafList[outIndex];
                this.fElemMap[this.fElemMapSize] = leaf.getLeaf();
                if (leaf instanceof XSCMRepeatingLeaf) {
                    if (elemOccurenceMap == null) {
                        elemOccurenceMap = new Occurence[this.fLeafCount];
                    }
                    elemOccurenceMap[this.fElemMapSize] = new Occurence((XSCMRepeatingLeaf)leaf, this.fElemMapSize);
                }
                this.fElemMapType[this.fElemMapSize] = this.fLeafListType[outIndex];
                this.fElemMapId[this.fElemMapSize] = id;
                final int[] bounds = (int[])leaf.getUserData();
                if (bounds != null) {
                    this.fElemMapCounter[this.fElemMapSize] = 0;
                    this.fElemMapCounterLowerBound[this.fElemMapSize] = bounds[0];
                    this.fElemMapCounterUpperBound[this.fElemMapSize] = bounds[1];
                }
                else {
                    this.fElemMapCounter[this.fElemMapSize] = -1;
                    this.fElemMapCounterLowerBound[this.fElemMapSize] = -1;
                    this.fElemMapCounterUpperBound[this.fElemMapSize] = -1;
                }
                ++this.fElemMapSize;
            }
        }
        --this.fElemMapSize;
        final int[] fLeafSorter = new int[this.fLeafCount + this.fElemMapSize];
        int fSortCount = 0;
        for (int elemIndex = 0; elemIndex < this.fElemMapSize; ++elemIndex) {
            final int id2 = this.fElemMapId[elemIndex];
            for (int leafIndex = 0; leafIndex < this.fLeafCount; ++leafIndex) {
                if (id2 == this.fLeafList[leafIndex].getParticleId()) {
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
            this.fFinalStateFlags[unmarkedState] = setT.getBit(EOCPos);
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
        if (elemOccurenceMap != null) {
            this.fCountingStates = new Occurence[curState];
            for (int i = 0; i < curState; ++i) {
                final int[] transitions = this.fTransTable[i];
                for (int j = 0; j < transitions.length; ++j) {
                    if (i == transitions[j]) {
                        this.fCountingStates[i] = elemOccurenceMap[j];
                        break;
                    }
                }
            }
        }
        this.fHeadNode = null;
        this.fLeafList = null;
        this.fFollowList = null;
        this.fLeafListType = null;
        this.fElemMapId = null;
    }
    
    private void calcFollowList(final CMNode nodeCur) {
        if (nodeCur.type() == 101) {
            this.calcFollowList(((XSCMBinOp)nodeCur).getLeft());
            this.calcFollowList(((XSCMBinOp)nodeCur).getRight());
        }
        else if (nodeCur.type() == 102) {
            this.calcFollowList(((XSCMBinOp)nodeCur).getLeft());
            this.calcFollowList(((XSCMBinOp)nodeCur).getRight());
            final CMStateSet last = ((XSCMBinOp)nodeCur).getLeft().lastPos();
            final CMStateSet first = ((XSCMBinOp)nodeCur).getRight().firstPos();
            for (int index = 0; index < this.fLeafCount; ++index) {
                if (last.getBit(index)) {
                    this.fFollowList[index].union(first);
                }
            }
        }
        else if (nodeCur.type() == 4 || nodeCur.type() == 6) {
            this.calcFollowList(((XSCMUniOp)nodeCur).getChild());
            final CMStateSet first2 = nodeCur.firstPos();
            final CMStateSet last2 = nodeCur.lastPos();
            for (int index = 0; index < this.fLeafCount; ++index) {
                if (last2.getBit(index)) {
                    this.fFollowList[index].union(first2);
                }
            }
        }
        else if (nodeCur.type() == 5) {
            this.calcFollowList(((XSCMUniOp)nodeCur).getChild());
        }
    }
    
    private void dumpTree(final CMNode nodeCur, final int level) {
        for (int index = 0; index < level; ++index) {
            System.out.print("   ");
        }
        final int type = nodeCur.type();
        switch (type) {
            case 101:
            case 102: {
                if (type == 101) {
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
                this.dumpTree(((XSCMBinOp)nodeCur).getLeft(), level + 1);
                this.dumpTree(((XSCMBinOp)nodeCur).getRight(), level + 1);
                break;
            }
            case 4:
            case 5:
            case 6: {
                System.out.print("Rep Node ");
                if (nodeCur.isNullable()) {
                    System.out.print("Nullable ");
                }
                System.out.print("firstPos=");
                System.out.print(nodeCur.firstPos().toString());
                System.out.print(" lastPos=");
                System.out.println(nodeCur.lastPos().toString());
                this.dumpTree(((XSCMUniOp)nodeCur).getChild(), level + 1);
                break;
            }
            case 1: {
                System.out.print("Leaf: (pos=" + ((XSCMLeaf)nodeCur).getPosition() + "), (elemIndex=" + ((XSCMLeaf)nodeCur).getLeaf() + ") ");
                if (nodeCur.isNullable()) {
                    System.out.print(" Nullable ");
                }
                System.out.print("firstPos=");
                System.out.print(nodeCur.firstPos().toString());
                System.out.print(" lastPos=");
                System.out.println(nodeCur.lastPos().toString());
                break;
            }
            case 2: {
                System.out.print("Any Node: ");
                System.out.print("firstPos=");
                System.out.print(nodeCur.firstPos().toString());
                System.out.print(" lastPos=");
                System.out.println(nodeCur.lastPos().toString());
                break;
            }
            default: {
                throw new RuntimeException("ImplementationMessages.VAL_NIICM");
            }
        }
    }
    
    private int[] makeDefStateList() {
        final int[] retArray = new int[this.fElemMapSize];
        for (int index = 0; index < this.fElemMapSize; ++index) {
            retArray[index] = -1;
        }
        return retArray;
    }
    
    private void postTreeBuildInit(final CMNode nodeCur) throws RuntimeException {
        nodeCur.setMaxStates(this.fLeafCount);
        XSCMLeaf leaf = null;
        int pos = 0;
        if (nodeCur.type() == 2) {
            leaf = (XSCMLeaf)nodeCur;
            pos = leaf.getPosition();
            this.fLeafList[pos] = leaf;
            this.fLeafListType[pos] = 2;
        }
        else if (nodeCur.type() == 101 || nodeCur.type() == 102) {
            this.postTreeBuildInit(((XSCMBinOp)nodeCur).getLeft());
            this.postTreeBuildInit(((XSCMBinOp)nodeCur).getRight());
        }
        else if (nodeCur.type() == 4 || nodeCur.type() == 6 || nodeCur.type() == 5) {
            this.postTreeBuildInit(((XSCMUniOp)nodeCur).getChild());
        }
        else {
            if (nodeCur.type() != 1) {
                throw new RuntimeException("ImplementationMessages.VAL_NIICM");
            }
            leaf = (XSCMLeaf)nodeCur;
            pos = leaf.getPosition();
            this.fLeafList[pos] = leaf;
            this.fLeafListType[pos] = 1;
        }
    }
    
    @Override
    public boolean checkUniqueParticleAttribution(final SubstitutionGroupHandler subGroupHandler) throws XMLSchemaException {
        final byte[][] conflictTable = new byte[this.fElemMapSize][this.fElemMapSize];
        for (int i = 0; i < this.fTransTable.length && this.fTransTable[i] != null; ++i) {
            for (int j = 0; j < this.fElemMapSize; ++j) {
                for (int k = j + 1; k < this.fElemMapSize; ++k) {
                    if (this.fTransTable[i][j] != -1 && this.fTransTable[i][k] != -1 && conflictTable[j][k] == 0) {
                        if (XSConstraints.overlapUPA(this.fElemMap[j], this.fElemMap[k], subGroupHandler)) {
                            if (this.fCountingStates != null) {
                                final Occurence o = this.fCountingStates[i];
                                if (o != null && (this.fTransTable[i][j] == i ^ this.fTransTable[i][k] == i) && o.minOccurs == o.maxOccurs) {
                                    conflictTable[j][k] = -1;
                                    continue;
                                }
                            }
                            conflictTable[j][k] = 1;
                        }
                        else {
                            conflictTable[j][k] = -1;
                        }
                    }
                }
            }
        }
        for (int i = 0; i < this.fElemMapSize; ++i) {
            for (int j = 0; j < this.fElemMapSize; ++j) {
                if (conflictTable[i][j] == 1) {
                    throw new XMLSchemaException("cos-nonambig", new Object[] { this.fElemMap[i].toString(), this.fElemMap[j].toString() });
                }
            }
        }
        for (int i = 0; i < this.fElemMapSize; ++i) {
            if (this.fElemMapType[i] == 2) {
                final XSWildcardDecl wildcard = (XSWildcardDecl)this.fElemMap[i];
                if (wildcard.fType == 3 || wildcard.fType == 2) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public Vector whatCanGoHere(final int[] state) {
        int curState = state[0];
        if (curState < 0) {
            curState = state[1];
        }
        final Occurence o = (this.fCountingStates != null) ? this.fCountingStates[curState] : null;
        final int count = state[2];
        final Vector ret = new Vector();
        for (int elemIndex = 0; elemIndex < this.fElemMapSize; ++elemIndex) {
            final int nextState = this.fTransTable[curState][elemIndex];
            if (nextState != -1) {
                if (o != null) {
                    if (curState == nextState) {
                        if (count >= o.maxOccurs && o.maxOccurs != -1) {
                            continue;
                        }
                    }
                    else if (count < o.minOccurs) {
                        continue;
                    }
                }
                ret.addElement(this.fElemMap[elemIndex]);
            }
        }
        return ret;
    }
    
    @Override
    public ArrayList checkMinMaxBounds() {
        ArrayList result = null;
        for (int elemIndex = 0; elemIndex < this.fElemMapSize; ++elemIndex) {
            final int count = this.fElemMapCounter[elemIndex];
            if (count != -1) {
                final int minOccurs = this.fElemMapCounterLowerBound[elemIndex];
                final int maxOccurs = this.fElemMapCounterUpperBound[elemIndex];
                if (count < minOccurs) {
                    if (result == null) {
                        result = new ArrayList();
                    }
                    result.add("cvc-complex-type.2.4.b");
                    result.add("{" + this.fElemMap[elemIndex] + "}");
                }
                if (maxOccurs != -1 && count > maxOccurs) {
                    if (result == null) {
                        result = new ArrayList();
                    }
                    result.add("cvc-complex-type.2.4.e");
                    result.add("{" + this.fElemMap[elemIndex] + "}");
                }
            }
        }
        return result;
    }
    
    static {
        XSDFACM.time = 0L;
    }
    
    static final class Occurence
    {
        final int minOccurs;
        final int maxOccurs;
        final int elemIndex;
        
        public Occurence(final XSCMRepeatingLeaf leaf, final int elemIndex) {
            this.minOccurs = leaf.getMinOccurs();
            this.maxOccurs = leaf.getMaxOccurs();
            this.elemIndex = elemIndex;
        }
        
        @Override
        public String toString() {
            return "minOccurs=" + this.minOccurs + ";maxOccurs=" + ((this.maxOccurs != -1) ? Integer.toString(this.maxOccurs) : "unbounded");
        }
    }
}
