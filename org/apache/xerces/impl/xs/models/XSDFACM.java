package org.apache.xerces.impl.xs.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.apache.xerces.impl.xs.XMLSchemaException;
import org.apache.xerces.impl.xs.XSConstraints;
import java.util.HashMap;
import org.apache.xerces.impl.xs.XSElementDeclHelper;
import org.apache.xerces.impl.xs.SubstitutionGroupHandler;
import org.apache.xerces.xni.QName;
import org.apache.xerces.impl.xs.XSOpenContentDecl;
import org.apache.xerces.impl.dtd.models.CMNode;
import org.apache.xerces.impl.dtd.models.CMStateSet;
import org.apache.xerces.impl.xs.XSWildcardDecl;
import org.apache.xerces.impl.xs.XSElementDecl;

public class XSDFACM implements XSCMValidator, XS11CMRestriction.XS11CM
{
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_VALIDATE_CONTENT = false;
    private XSElementDecl[] fElements;
    private XSWildcardDecl[] fWildcards;
    private int fNumElements;
    private int fNumTotal;
    private boolean[] fFinalStateFlags;
    private CMStateSet[] fFollowList;
    private CMNode fHeadNode;
    private int fLeafCount;
    private XSCMLeaf[] fLeafList;
    private int[] fLeafListType;
    private int[][] fTransTable;
    private final XSOpenContentDecl fOpenContent;
    private final short fSchemaVersion;
    private Occurence[] fCountingStates;
    private int fTransTableSize;
    private boolean fIsCompactedForUPA;
    private static long time;
    
    public XSDFACM(final CMNode cmNode, final int fLeafCount, final short fSchemaVersion, final XSOpenContentDecl fOpenContent) {
        this.fFinalStateFlags = null;
        this.fFollowList = null;
        this.fHeadNode = null;
        this.fLeafCount = 0;
        this.fLeafList = null;
        this.fLeafListType = null;
        this.fTransTable = null;
        this.fCountingStates = null;
        this.fTransTableSize = 0;
        this.fLeafCount = fLeafCount;
        this.fIsCompactedForUPA = cmNode.isCompactedForUPA();
        this.fSchemaVersion = fSchemaVersion;
        this.fOpenContent = fOpenContent;
        this.buildDFA(cmNode);
    }
    
    public boolean isFinalState(final int n) {
        return n >= 0 && this.fFinalStateFlags[n];
    }
    
    public Object oneTransition(final QName qName, final int[] array, final SubstitutionGroupHandler substitutionGroupHandler, final XSElementDeclHelper xsElementDeclHelper) {
        Object o = this.oneTransition1(qName, array, substitutionGroupHandler, xsElementDeclHelper);
        if (this.fOpenContent != null && o == this.fOpenContent.fWildcard) {
            o = this.fOpenContent;
        }
        return o;
    }
    
    private Object oneTransition1(final QName qName, final int[] array, final SubstitutionGroupHandler substitutionGroupHandler, final XSElementDeclHelper xsElementDeclHelper) {
        final int n = array[0];
        if (n == -1 || n == -2) {
            if (n == -1) {
                array[0] = -2;
            }
            return this.findMatchingDecl(qName, substitutionGroupHandler);
        }
        int n2 = 0;
        int i = 0;
        Object matchingElemDecl = null;
        while (i < this.fNumElements) {
            n2 = this.fTransTable[n][i];
            if (n2 != -1) {
                matchingElemDecl = substitutionGroupHandler.getMatchingElemDecl(qName, this.fElements[i], this.fSchemaVersion);
                if (matchingElemDecl != null) {
                    break;
                }
            }
            ++i;
        }
        if (matchingElemDecl == null) {
            while (i < this.fNumTotal) {
                n2 = this.fTransTable[n][i];
                if (n2 != -1) {
                    if (this.fSchemaVersion < 4) {
                        if (this.fWildcards[i].allowNamespace(qName.uri)) {
                            matchingElemDecl = this.fWildcards[i];
                            break;
                        }
                    }
                    else if (this.allowExpandedName(this.fWildcards[i], qName, substitutionGroupHandler, xsElementDeclHelper)) {
                        matchingElemDecl = this.fWildcards[i];
                        break;
                    }
                }
                ++i;
            }
        }
        if (matchingElemDecl == null) {
            array[1] = array[0];
            array[0] = -1;
            return this.findMatchingDecl(qName, substitutionGroupHandler);
        }
        array[0] = n2;
        if (this.fCountingStates == null) {
            return matchingElemDecl;
        }
        if (this.fOpenContent != null && this.fOpenContent.fWildcard == matchingElemDecl && this.fOpenContent.fMode == 1) {
            return matchingElemDecl;
        }
        final Occurence occurence = this.fCountingStates[n];
        if (occurence != null) {
            if (n == n2) {
                if (++array[2] > occurence.maxOccurs && occurence.maxOccurs != -1) {
                    return this.findMatchingDecl(qName, array, substitutionGroupHandler, ++i, xsElementDeclHelper);
                }
            }
            else {
                if (array[2] < occurence.minOccurs) {
                    array[1] = array[0];
                    array[0] = -1;
                    return this.findMatchingDecl(qName, substitutionGroupHandler);
                }
                final Occurence occurence2 = this.fCountingStates[n2];
                if (occurence2 != null) {
                    array[2] = ((i == occurence2.elemIndex) ? 1 : 0);
                }
            }
        }
        else {
            final Occurence occurence3 = this.fCountingStates[n2];
            if (occurence3 != null) {
                array[2] = ((i == occurence3.elemIndex) ? 1 : 0);
            }
        }
        return matchingElemDecl;
    }
    
    Object findMatchingDecl(final QName qName, final SubstitutionGroupHandler substitutionGroupHandler) {
        for (int i = 0; i < this.fNumElements; ++i) {
            final XSElementDecl matchingElemDecl = substitutionGroupHandler.getMatchingElemDecl(qName, this.fElements[i], this.fSchemaVersion);
            if (matchingElemDecl != null) {
                return matchingElemDecl;
            }
        }
        for (int j = this.fNumElements; j < this.fNumTotal; ++j) {
            if (this.fWildcards[j].allowQName(qName)) {
                return this.fWildcards[j];
            }
        }
        return null;
    }
    
    Object findMatchingDecl(final QName qName, final int[] array, final SubstitutionGroupHandler substitutionGroupHandler, int i, final XSElementDeclHelper xsElementDeclHelper) {
        final int n = array[0];
        int n2 = 0;
        Object matchingElemDecl = null;
        while (i < this.fNumElements) {
            n2 = this.fTransTable[n][i];
            if (n2 != -1) {
                matchingElemDecl = substitutionGroupHandler.getMatchingElemDecl(qName, this.fElements[i], this.fSchemaVersion);
                if (matchingElemDecl != null) {
                    break;
                }
            }
            ++i;
        }
        if (matchingElemDecl == null) {
            while (i < this.fNumTotal) {
                if (this.fSchemaVersion < 4) {
                    if (this.fWildcards[i].allowNamespace(qName.uri)) {
                        matchingElemDecl = this.fWildcards[i];
                        break;
                    }
                }
                else if (this.allowExpandedName(this.fWildcards[i], qName, substitutionGroupHandler, xsElementDeclHelper)) {
                    matchingElemDecl = this.fWildcards[i];
                    break;
                }
                ++i;
            }
        }
        if (matchingElemDecl == null) {
            array[1] = array[0];
            array[0] = -1;
            return this.findMatchingDecl(qName, substitutionGroupHandler);
        }
        array[0] = n2;
        final Occurence occurence = this.fCountingStates[n2];
        if (occurence != null) {
            array[2] = ((i == occurence.elemIndex) ? 1 : 0);
        }
        return matchingElemDecl;
    }
    
    public XSElementDecl findMatchingElemDecl(final QName qName, final SubstitutionGroupHandler substitutionGroupHandler) {
        for (int i = 0; i < this.fNumElements; ++i) {
            final XSElementDecl matchingElemDecl = substitutionGroupHandler.getMatchingElemDecl(qName, this.fElements[i], this.fSchemaVersion);
            if (matchingElemDecl != null) {
                return matchingElemDecl;
            }
        }
        return null;
    }
    
    public boolean allowExpandedName(final XSWildcardDecl xsWildcardDecl, final QName qName, final SubstitutionGroupHandler substitutionGroupHandler, final XSElementDeclHelper xsElementDeclHelper) {
        return xsWildcardDecl.allowQName(qName) && (!xsWildcardDecl.fDisallowedSibling || this.findMatchingElemDecl(qName, substitutionGroupHandler) == null) && (!xsWildcardDecl.fDisallowedDefined || xsElementDeclHelper.getGlobalElementDecl(qName) == null);
    }
    
    public int[] startContentModel() {
        return new int[3];
    }
    
    public boolean endContentModel(final int[] array) {
        final int n = array[0];
        if (this.fFinalStateFlags[n]) {
            if (this.fCountingStates != null) {
                final Occurence occurence = this.fCountingStates[n];
                if (occurence != null && array[2] < occurence.minOccurs) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    private void buildDFA(final CMNode cmNode) {
        final int fLeafCount = this.fLeafCount;
        this.fHeadNode = new XSCMBinOp(102, cmNode, new XSCMLeaf(1, null, -1, this.fLeafCount++));
        this.fLeafList = new XSCMLeaf[this.fLeafCount];
        this.fLeafListType = new int[this.fLeafCount];
        this.postTreeBuildInit(this.fHeadNode);
        this.fFollowList = new CMStateSet[this.fLeafCount];
        for (int i = 0; i < this.fLeafCount; ++i) {
            this.fFollowList[i] = new CMStateSet(this.fLeafCount);
        }
        this.calcFollowList(this.fHeadNode);
        final Object[] array = new Object[this.fLeafCount];
        final int[] array2 = new int[this.fLeafCount];
        final int[] array3 = new int[this.fLeafCount];
        int fNumTotal = 0;
        Occurence[] array4 = null;
        int fNumElements = 0;
        for (int j = 0; j < this.fLeafCount; ++j) {
            array[j] = null;
            int n;
            int particleId;
            for (n = 0, particleId = this.fLeafList[j].getParticleId(); n < fNumTotal && particleId != array3[n]; ++n) {}
            if (n == fNumTotal) {
                final XSCMLeaf xscmLeaf = this.fLeafList[j];
                array[fNumTotal] = xscmLeaf.getLeaf();
                if (xscmLeaf instanceof XSCMRepeatingLeaf) {
                    if (array4 == null) {
                        array4 = new Occurence[this.fLeafCount];
                    }
                    array4[fNumTotal] = new Occurence((XSCMRepeatingLeaf)xscmLeaf, fNumTotal);
                }
                array2[fNumTotal] = this.fLeafListType[j];
                array3[fNumTotal] = particleId;
                if (array2[fNumTotal] == 1) {
                    ++fNumElements;
                }
                ++fNumTotal;
            }
        }
        --fNumTotal;
        --fNumElements;
        this.fNumTotal = fNumTotal;
        if (this.fOpenContent != null) {
            ++this.fNumTotal;
        }
        int elemIndex = 0;
        int elemIndex2 = fNumTotal - 1;
        while (true) {
            if (elemIndex <= elemIndex2 && array2[elemIndex] == 1) {
                ++elemIndex;
            }
            else {
                while (elemIndex2 >= elemIndex && array2[elemIndex2] == 2) {
                    --elemIndex2;
                }
                if (elemIndex >= elemIndex2) {
                    break;
                }
                final Object o = array[elemIndex];
                array[elemIndex] = array[elemIndex2];
                array[elemIndex2] = o;
                final int n2 = array3[elemIndex];
                array3[elemIndex] = array3[elemIndex2];
                array3[elemIndex2] = n2;
                if (array4 != null) {
                    final Occurence occurence = array4[elemIndex];
                    array4[elemIndex] = array4[elemIndex2];
                    array4[elemIndex2] = occurence;
                    if (array4[elemIndex] != null) {
                        array4[elemIndex].elemIndex = elemIndex;
                    }
                    if (array4[elemIndex2] != null) {
                        array4[elemIndex2].elemIndex = elemIndex2;
                    }
                }
                ++elemIndex;
                --elemIndex2;
            }
        }
        final int[] array5 = new int[this.fLeafCount + fNumTotal];
        int n3 = 0;
        for (final int n4 : array3) {
            for (int l = 0; l < this.fLeafCount; ++l) {
                if (n4 == this.fLeafList[l].getParticleId()) {
                    array5[n3++] = l;
                }
            }
            array5[n3++] = -1;
        }
        int n5 = this.fLeafCount * 4;
        CMStateSet[] array6 = new CMStateSet[n5];
        this.fFinalStateFlags = new boolean[n5];
        this.fTransTable = new int[n5][];
        final CMStateSet firstPos = this.fHeadNode.firstPos();
        int n6 = 0;
        int fTransTableSize = 0;
        this.fTransTable[fTransTableSize] = this.makeDefStateList();
        array6[fTransTableSize] = firstPos;
        ++fTransTableSize;
        final HashMap<CMStateSet, Integer> hashMap = new HashMap<CMStateSet, Integer>();
        while (n6 < fTransTableSize) {
            final CMStateSet set = array6[n6];
            final int[] array7 = this.fTransTable[n6];
            this.fFinalStateFlags[n6] = set.getBit(fLeafCount);
            ++n6;
            CMStateSet set2 = null;
            int n7 = 0;
            for (int n8 = 0; n8 < fNumTotal; ++n8) {
                if (set2 == null) {
                    set2 = new CMStateSet(this.fLeafCount);
                }
                else {
                    set2.zeroBits();
                }
                for (int n9 = array5[n7++]; n9 != -1; n9 = array5[n7++]) {
                    if (set.getBit(n9)) {
                        set2.union(this.fFollowList[n9]);
                    }
                }
                if (!set2.isEmpty()) {
                    final Integer n10 = hashMap.get(set2);
                    final int n11 = (n10 == null) ? fTransTableSize : n10;
                    if (n11 == fTransTableSize) {
                        array6[fTransTableSize] = set2;
                        this.fTransTable[fTransTableSize] = this.makeDefStateList();
                        hashMap.put(set2, new Integer(fTransTableSize));
                        ++fTransTableSize;
                        set2 = null;
                    }
                    array7[n8] = n11;
                    if (fTransTableSize == n5) {
                        final int n12 = (int)(n5 * 1.5);
                        final CMStateSet[] array8 = new CMStateSet[n12];
                        final boolean[] fFinalStateFlags = new boolean[n12];
                        final int[][] fTransTable = new int[n12][];
                        System.arraycopy(array6, 0, array8, 0, n5);
                        System.arraycopy(this.fFinalStateFlags, 0, fFinalStateFlags, 0, n5);
                        System.arraycopy(this.fTransTable, 0, fTransTable, 0, n5);
                        n5 = n12;
                        array6 = array8;
                        this.fFinalStateFlags = fFinalStateFlags;
                        this.fTransTable = fTransTable;
                    }
                }
            }
        }
        if (array4 != null) {
            this.fCountingStates = new Occurence[fTransTableSize];
            for (int n13 = 0; n13 < fTransTableSize; ++n13) {
                final int[] array9 = this.fTransTable[n13];
                for (int n14 = 0; n14 < array9.length; ++n14) {
                    if (n13 == array9[n14]) {
                        this.fCountingStates[n13] = array4[n14];
                        break;
                    }
                }
            }
        }
        this.fTransTableSize = fTransTableSize;
        this.fHeadNode = null;
        this.fLeafList = null;
        this.fFollowList = null;
        this.fLeafListType = null;
        if (this.fOpenContent != null) {
            array[fNumTotal] = this.fOpenContent.fWildcard;
            if (this.fOpenContent.fMode == 1) {
                for (int n15 = 0; n15 < this.fTransTableSize; ++n15) {
                    this.fTransTable[n15][fNumTotal] = n15;
                }
            }
            else {
                for (int n16 = 0; n16 < this.fTransTableSize; ++n16) {
                    if (this.fFinalStateFlags[n16]) {
                        this.fTransTable[n16][fNumTotal] = this.fTransTableSize;
                    }
                }
                (this.fTransTable[this.fTransTableSize] = this.makeDefStateList())[fNumTotal] = this.fTransTableSize;
                this.fFinalStateFlags[this.fTransTableSize] = true;
                ++this.fTransTableSize;
            }
            ++fNumTotal;
        }
        if ((this.fNumElements = fNumElements) > 0) {
            this.fElements = new XSElementDecl[fNumElements];
        }
        if (this.fNumTotal > fNumElements) {
            this.fWildcards = new XSWildcardDecl[this.fNumTotal];
        }
        for (int n17 = 0; n17 < fNumElements; ++n17) {
            this.fElements[n17] = (XSElementDecl)array[n17];
        }
        for (int n18 = fNumElements; n18 < this.fNumTotal; ++n18) {
            this.fWildcards[n18] = (XSWildcardDecl)array[n18];
        }
    }
    
    private void calcFollowList(final CMNode cmNode) {
        if (cmNode.type() == 101) {
            this.calcFollowList(((XSCMBinOp)cmNode).getLeft());
            this.calcFollowList(((XSCMBinOp)cmNode).getRight());
        }
        else if (cmNode.type() == 102) {
            this.calcFollowList(((XSCMBinOp)cmNode).getLeft());
            this.calcFollowList(((XSCMBinOp)cmNode).getRight());
            final CMStateSet lastPos = ((XSCMBinOp)cmNode).getLeft().lastPos();
            final CMStateSet firstPos = ((XSCMBinOp)cmNode).getRight().firstPos();
            for (int i = 0; i < this.fLeafCount; ++i) {
                if (lastPos.getBit(i)) {
                    this.fFollowList[i].union(firstPos);
                }
            }
        }
        else if (cmNode.type() == 4 || cmNode.type() == 6) {
            this.calcFollowList(((XSCMUniOp)cmNode).getChild());
            final CMStateSet firstPos2 = cmNode.firstPos();
            final CMStateSet lastPos2 = cmNode.lastPos();
            for (int j = 0; j < this.fLeafCount; ++j) {
                if (lastPos2.getBit(j)) {
                    this.fFollowList[j].union(firstPos2);
                }
            }
        }
        else if (cmNode.type() == 5) {
            this.calcFollowList(((XSCMUniOp)cmNode).getChild());
        }
    }
    
    private void dumpTree(final CMNode cmNode, final int n) {
        for (int i = 0; i < n; ++i) {
            System.out.print("   ");
        }
        final int type = cmNode.type();
        switch (type) {
            case 101:
            case 102: {
                if (type == 101) {
                    System.out.print("Choice Node ");
                }
                else {
                    System.out.print("Seq Node ");
                }
                if (cmNode.isNullable()) {
                    System.out.print("Nullable ");
                }
                System.out.print("firstPos=");
                System.out.print(cmNode.firstPos().toString());
                System.out.print(" lastPos=");
                System.out.println(cmNode.lastPos().toString());
                this.dumpTree(((XSCMBinOp)cmNode).getLeft(), n + 1);
                this.dumpTree(((XSCMBinOp)cmNode).getRight(), n + 1);
                break;
            }
            case 4:
            case 5:
            case 6: {
                System.out.print("Rep Node ");
                if (cmNode.isNullable()) {
                    System.out.print("Nullable ");
                }
                System.out.print("firstPos=");
                System.out.print(cmNode.firstPos().toString());
                System.out.print(" lastPos=");
                System.out.println(cmNode.lastPos().toString());
                this.dumpTree(((XSCMUniOp)cmNode).getChild(), n + 1);
                break;
            }
            case 1: {
                System.out.print("Leaf: (pos=" + ((XSCMLeaf)cmNode).getPosition() + "), " + "(elemIndex=" + ((XSCMLeaf)cmNode).getLeaf() + ") ");
                if (cmNode.isNullable()) {
                    System.out.print(" Nullable ");
                }
                System.out.print("firstPos=");
                System.out.print(cmNode.firstPos().toString());
                System.out.print(" lastPos=");
                System.out.println(cmNode.lastPos().toString());
                break;
            }
            case 2: {
                System.out.print("Any Node: ");
                System.out.print("firstPos=");
                System.out.print(cmNode.firstPos().toString());
                System.out.print(" lastPos=");
                System.out.println(cmNode.lastPos().toString());
                break;
            }
            default: {
                throw new RuntimeException("ImplementationMessages.VAL_NIICM");
            }
        }
    }
    
    private int[] makeDefStateList() {
        final int[] array = new int[this.fNumTotal];
        for (int i = 0; i < this.fNumTotal; ++i) {
            array[i] = -1;
        }
        return array;
    }
    
    private void postTreeBuildInit(final CMNode cmNode) throws RuntimeException {
        cmNode.setMaxStates(this.fLeafCount);
        if (cmNode.type() == 2) {
            final XSCMLeaf xscmLeaf = (XSCMLeaf)cmNode;
            final int position = xscmLeaf.getPosition();
            this.fLeafList[position] = xscmLeaf;
            this.fLeafListType[position] = 2;
        }
        else if (cmNode.type() == 101 || cmNode.type() == 102) {
            this.postTreeBuildInit(((XSCMBinOp)cmNode).getLeft());
            this.postTreeBuildInit(((XSCMBinOp)cmNode).getRight());
        }
        else if (cmNode.type() == 4 || cmNode.type() == 6 || cmNode.type() == 5) {
            this.postTreeBuildInit(((XSCMUniOp)cmNode).getChild());
        }
        else {
            if (cmNode.type() != 1) {
                throw new RuntimeException("ImplementationMessages.VAL_NIICM");
            }
            final XSCMLeaf xscmLeaf2 = (XSCMLeaf)cmNode;
            final int position2 = xscmLeaf2.getPosition();
            this.fLeafList[position2] = xscmLeaf2;
            this.fLeafListType[position2] = 1;
        }
    }
    
    public boolean checkUniqueParticleAttribution(final SubstitutionGroupHandler substitutionGroupHandler, final XSConstraints xsConstraints) throws XMLSchemaException {
        final int n = (this.fOpenContent != null) ? (this.fNumTotal - 1) : this.fNumTotal;
        final byte[][] array = new byte[n][n];
        for (int i = 0; i < this.fTransTableSize; ++i) {
            for (int j = 0; j < n; ++j) {
                for (int k = j + 1; k < n; ++k) {
                    if (this.fTransTable[i][j] != -1 && this.fTransTable[i][k] != -1 && array[j][k] == 0) {
                        if (xsConstraints.overlapUPA((j < this.fNumElements) ? this.fElements[j] : this.fWildcards[j], (k < this.fNumElements) ? this.fElements[k] : this.fWildcards[k], substitutionGroupHandler)) {
                            if (this.fCountingStates != null) {
                                final Occurence occurence = this.fCountingStates[i];
                                if (occurence != null && (this.fTransTable[i][j] == i ^ this.fTransTable[i][k] == i) && occurence.minOccurs == occurence.maxOccurs) {
                                    array[j][k] = -1;
                                    continue;
                                }
                            }
                            array[j][k] = 1;
                        }
                        else {
                            array[j][k] = -1;
                        }
                    }
                }
            }
        }
        for (int l = 0; l < n; ++l) {
            for (int n2 = 0; n2 < n; ++n2) {
                if (array[l][n2] == 1) {
                    throw new XMLSchemaException("cos-nonambig", new Object[] { (l < this.fNumElements) ? this.fElements[l] : this.fWildcards[l], (n2 < this.fNumElements) ? this.fElements[n2] : this.fWildcards[n2] });
                }
            }
        }
        for (int fNumElements = this.fNumElements; fNumElements < n; ++fNumElements) {
            final XSWildcardDecl xsWildcardDecl = this.fWildcards[fNumElements];
            if (xsWildcardDecl.fType == 3 || xsWildcardDecl.fType == 2) {
                return true;
            }
        }
        return false;
    }
    
    public Vector whatCanGoHere(final int[] array) {
        final int n = (this.fOpenContent != null) ? (this.fNumTotal - 1) : this.fNumTotal;
        int n2 = array[0];
        if (n2 < 0) {
            n2 = array[1];
        }
        final Occurence occurence = (this.fCountingStates != null) ? this.fCountingStates[n2] : null;
        final int n3 = array[2];
        final Vector<Object> vector = new Vector<Object>();
        for (int i = 0; i < n; ++i) {
            final int n4 = this.fTransTable[n2][i];
            if (n4 != -1) {
                if (occurence != null) {
                    if (n2 == n4) {
                        if (n3 >= occurence.maxOccurs && occurence.maxOccurs != -1) {
                            continue;
                        }
                    }
                    else if (n3 < occurence.minOccurs) {
                        continue;
                    }
                }
                vector.addElement((i < this.fNumElements) ? this.fElements[i] : this.fWildcards[i]);
            }
        }
        return vector;
    }
    
    public int[] occurenceInfo(final int[] array) {
        if (this.fCountingStates != null) {
            int n = array[0];
            if (n < 0) {
                n = array[1];
            }
            final Occurence occurence = this.fCountingStates[n];
            if (occurence != null) {
                return new int[] { occurence.minOccurs, occurence.maxOccurs, array[2], occurence.elemIndex };
            }
        }
        return null;
    }
    
    public String getTermName(final int n) {
        final Object o = (n < this.fNumElements) ? this.fElements[n] : this.fWildcards[n];
        return (o != null) ? o.toString() : null;
    }
    
    public boolean isCompactedForUPA() {
        return this.fIsCompactedForUPA;
    }
    
    public XSElementDecl nextElementTransition(final int[] array, final int[] array2, final int[] array3) {
        for (int i = array3[0] + 1; i < this.fNumElements; ++i) {
            if (this.isAllowedTransition(array, array2, i)) {
                array3[0] = i;
                return this.fElements[i];
            }
        }
        array3[0] = -1;
        return null;
    }
    
    public XSWildcardDecl nextWildcardTransition(final int[] array, final int[] array2, final int[] array3) {
        for (int i = (array3[0] == -1) ? this.fNumElements : (array3[0] + 1); i < this.fNumTotal; ++i) {
            if (this.isAllowedTransition(array, array2, i)) {
                array3[0] = i;
                return this.fWildcards[i];
            }
        }
        array3[0] = -1;
        return null;
    }
    
    private boolean isAllowedTransition(final int[] array, final int[] array2, final int n) {
        final int n2 = this.fTransTable[array[0]][n];
        if (n2 == -1) {
            return false;
        }
        if (array2 != null) {
            array2[0] = n2;
        }
        if (this.fCountingStates == null) {
            return true;
        }
        if (n == this.fNumTotal - 1 && this.fOpenContent != null && this.fOpenContent.fMode == 1) {
            return true;
        }
        final Occurence occurence = this.fCountingStates[array[0]];
        if (occurence != null) {
            if (array[0] == n2) {
                if (array[2] == occurence.maxOccurs) {
                    return false;
                }
                if (array2 != null) {
                    array2[2] = array[2];
                    if (array2[2] == 0 || array2[2] < occurence.minOccurs || occurence.maxOccurs != -1) {
                        final int n3 = 2;
                        ++array2[n3];
                    }
                }
            }
            else {
                if (array[2] < occurence.minOccurs) {
                    return false;
                }
                final Occurence occurence2 = this.fCountingStates[n2];
                if (occurence2 != null && array2 != null) {
                    array2[2] = ((n == occurence2.elemIndex) ? 1 : 0);
                }
            }
        }
        else {
            final Occurence occurence3 = this.fCountingStates[n2];
            if (occurence3 != null && array2 != null) {
                array2[2] = ((n == occurence3.elemIndex) ? 1 : 0);
            }
        }
        return true;
    }
    
    public boolean isOpenContent(final XSWildcardDecl xsWildcardDecl) {
        return this.fOpenContent != null && this.fOpenContent.fWildcard == xsWildcardDecl;
    }
    
    public List getDefinedNames(final SubstitutionGroupHandler substitutionGroupHandler) {
        final ArrayList list = new ArrayList();
        for (int i = 0; i < this.fNumElements; ++i) {
            final XSElementDecl xsElementDecl = this.fElements[i];
            list.add(xsElementDecl.fTargetNamespace);
            list.add(xsElementDecl.fName);
            if (xsElementDecl.fScope == 1) {
                final XSElementDecl[] substitutionGroup = substitutionGroupHandler.getSubstitutionGroup(xsElementDecl, this.fSchemaVersion);
                for (int j = 0; j < substitutionGroup.length; ++j) {
                    list.add(substitutionGroup[j].fTargetNamespace);
                    list.add(substitutionGroup[j].fName);
                }
            }
        }
        return list;
    }
    
    public void optimizeStates(final XS11CMRestriction.XS11CM xs11CM, final int[] array, final int[] array2, final int n) {
        if (this.fCountingStates == null || this.fCountingStates[array2[0]] == null) {
            return;
        }
        if (array2[2] <= 0) {
            return;
        }
        int n2 = 0;
        if (array2[2] < this.fCountingStates[array2[0]].minOccurs) {
            n2 = this.fCountingStates[array2[0]].minOccurs - array2[2];
        }
        else if (array2[2] > this.fCountingStates[array2[0]].minOccurs && array2[2] < this.fCountingStates[array2[0]].maxOccurs) {
            n2 = this.fCountingStates[array2[0]].maxOccurs - array2[2];
        }
        if (n2 == 0) {
            return;
        }
        if (xs11CM instanceof XSDFACM) {
            this.optimizeForDFABase((XSDFACM)xs11CM, array, array2, n2);
        }
        else if (xs11CM instanceof XS11AllCM) {
            this.optimizeForAllBase((XS11AllCM)xs11CM, array, array2, n2, n);
        }
    }
    
    private void optimizeForDFABase(final XSDFACM xsdfacm, final int[] array, final int[] array2, int n) {
        if (xsdfacm.fCountingStates == null || xsdfacm.fCountingStates[array[0]] == null) {
            return;
        }
        if (array[2] <= 0) {
            return;
        }
        if (xsdfacm.fCountingStates[array2[0]] != null) {
            if (xsdfacm.fCountingStates[array2[0]].maxOccurs == -1) {
                final int n2 = 2;
                array2[n2] += n;
                if (array[2] + n > xsdfacm.fCountingStates[array2[0]].minOccurs) {
                    array[2] = xsdfacm.fCountingStates[array2[0]].minOccurs;
                }
                else {
                    final int n3 = 2;
                    array[n3] += n;
                }
            }
            else {
                if (n > xsdfacm.fCountingStates[array2[0]].maxOccurs - array[2]) {
                    n = xsdfacm.fCountingStates[array2[0]].maxOccurs - array[2];
                }
                final int n4 = 2;
                array[n4] += n;
                final int n5 = 2;
                array2[n5] += n;
            }
        }
    }
    
    private void optimizeForAllBase(final XS11AllCM xs11AllCM, final int[] array, final int[] array2, int n, final int n2) {
        if (array[n2] <= 0) {
            return;
        }
        if (xs11AllCM.maxOccurs(n2) == -1) {
            final int n3 = 2;
            array2[n3] += n;
            if (array[n2] + n > xs11AllCM.minOccurs(n2)) {
                array[n2] = xs11AllCM.minOccurs(n2);
            }
            else {
                array[n2] += n;
            }
        }
        else {
            if (n > xs11AllCM.maxOccurs(n2) - array[n2]) {
                n = xs11AllCM.maxOccurs(n2) - array[n2];
            }
            array[n2] += n;
            final int n4 = 2;
            array2[n4] += n;
        }
    }
    
    static {
        XSDFACM.time = 0L;
    }
    
    static final class Occurence
    {
        final int minOccurs;
        final int maxOccurs;
        int elemIndex;
        
        public Occurence(final XSCMRepeatingLeaf xscmRepeatingLeaf, final int elemIndex) {
            this.minOccurs = xscmRepeatingLeaf.getMinOccurs();
            this.maxOccurs = xscmRepeatingLeaf.getMaxOccurs();
            this.elemIndex = elemIndex;
        }
        
        public String toString() {
            return "minOccurs=" + this.minOccurs + ";maxOccurs=" + ((this.maxOccurs != -1) ? Integer.toString(this.maxOccurs) : "unbounded");
        }
    }
}
