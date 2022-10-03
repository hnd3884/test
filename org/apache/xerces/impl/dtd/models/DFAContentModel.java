package org.apache.xerces.impl.dtd.models;

import java.util.HashMap;
import org.apache.xerces.xni.QName;

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
    
    public DFAContentModel(final CMNode cmNode, final int fLeafCount, final boolean fMixed) {
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
        this.fLeafCount = fLeafCount;
        this.fMixed = fMixed;
        this.buildDFA(cmNode);
    }
    
    public int validate(final QName[] array, final int n, final int n2) {
        if (n2 == 0) {
            return this.fEmptyContentIsValid ? -1 : 0;
        }
        int n3 = 0;
        for (int i = 0; i < n2; ++i) {
            final QName qName = array[n + i];
            if (!this.fMixed || qName.localpart != null) {
                int j;
                for (j = 0; j < this.fElemMapSize; ++j) {
                    final int n4 = this.fElemMapType[j] & 0xF;
                    if (n4 == 0) {
                        if (this.fElemMap[j].rawname == qName.rawname) {
                            break;
                        }
                    }
                    else if (n4 == 6) {
                        final String uri = this.fElemMap[j].uri;
                        if (uri == null) {
                            break;
                        }
                        if (uri == qName.uri) {
                            break;
                        }
                    }
                    else if (n4 == 8) {
                        if (qName.uri == null) {
                            break;
                        }
                    }
                    else if (n4 == 7 && this.fElemMap[j].uri != qName.uri) {
                        break;
                    }
                }
                if (j == this.fElemMapSize) {
                    return i;
                }
                n3 = this.fTransTable[n3][j];
                if (n3 == -1) {
                    return i;
                }
            }
        }
        if (!this.fFinalStateFlags[n3]) {
            return n2;
        }
        return -1;
    }
    
    private void buildDFA(final CMNode cmNode) {
        this.fQName.setValues(null, DFAContentModel.fEOCString, DFAContentModel.fEOCString, null);
        final CMLeaf cmLeaf = new CMLeaf(this.fQName);
        this.fHeadNode = new CMBinOp(5, cmNode, cmLeaf);
        this.fEOCPos = this.fLeafCount;
        cmLeaf.setPosition(this.fLeafCount++);
        this.fLeafList = new CMLeaf[this.fLeafCount];
        this.fLeafListType = new int[this.fLeafCount];
        this.postTreeBuildInit(this.fHeadNode, 0);
        this.fFollowList = new CMStateSet[this.fLeafCount];
        for (int i = 0; i < this.fLeafCount; ++i) {
            this.fFollowList[i] = new CMStateSet(this.fLeafCount);
        }
        this.calcFollowList(this.fHeadNode);
        this.fElemMap = new QName[this.fLeafCount];
        this.fElemMapType = new int[this.fLeafCount];
        this.fElemMapSize = 0;
        for (int j = 0; j < this.fLeafCount; ++j) {
            this.fElemMap[j] = new QName();
            QName element;
            int n;
            for (element = this.fLeafList[j].getElement(), n = 0; n < this.fElemMapSize && this.fElemMap[n].rawname != element.rawname; ++n) {}
            if (n == this.fElemMapSize) {
                this.fElemMap[this.fElemMapSize].setValues(element);
                this.fElemMapType[this.fElemMapSize] = this.fLeafListType[j];
                ++this.fElemMapSize;
            }
        }
        final int[] array = new int[this.fLeafCount + this.fElemMapSize];
        int n2 = 0;
        for (int k = 0; k < this.fElemMapSize; ++k) {
            for (int l = 0; l < this.fLeafCount; ++l) {
                if (this.fLeafList[l].getElement().rawname == this.fElemMap[k].rawname) {
                    array[n2++] = l;
                }
            }
            array[n2++] = -1;
        }
        int n3 = this.fLeafCount * 4;
        CMStateSet[] array2 = new CMStateSet[n3];
        this.fFinalStateFlags = new boolean[n3];
        this.fTransTable = new int[n3][];
        final CMStateSet firstPos = this.fHeadNode.firstPos();
        int n4 = 0;
        int n5 = 0;
        this.fTransTable[n5] = this.makeDefStateList();
        array2[n5] = firstPos;
        ++n5;
        final HashMap hashMap = new HashMap();
        while (n4 < n5) {
            final CMStateSet set = array2[n4];
            final int[] array3 = this.fTransTable[n4];
            this.fFinalStateFlags[n4] = set.getBit(this.fEOCPos);
            ++n4;
            CMStateSet set2 = null;
            int n6 = 0;
            for (int n7 = 0; n7 < this.fElemMapSize; ++n7) {
                if (set2 == null) {
                    set2 = new CMStateSet(this.fLeafCount);
                }
                else {
                    set2.zeroBits();
                }
                for (int n8 = array[n6++]; n8 != -1; n8 = array[n6++]) {
                    if (set.getBit(n8)) {
                        set2.union(this.fFollowList[n8]);
                    }
                }
                if (!set2.isEmpty()) {
                    final Integer n9 = hashMap.get(set2);
                    final int n10 = (n9 == null) ? n5 : n9;
                    if (n10 == n5) {
                        array2[n5] = set2;
                        this.fTransTable[n5] = this.makeDefStateList();
                        hashMap.put(set2, new Integer(n5));
                        ++n5;
                        set2 = null;
                    }
                    array3[n7] = n10;
                    if (n5 == n3) {
                        final int n11 = (int)(n3 * 1.5);
                        final CMStateSet[] array4 = new CMStateSet[n11];
                        final boolean[] fFinalStateFlags = new boolean[n11];
                        final int[][] fTransTable = new int[n11][];
                        System.arraycopy(array2, 0, array4, 0, n3);
                        System.arraycopy(this.fFinalStateFlags, 0, fFinalStateFlags, 0, n3);
                        System.arraycopy(this.fTransTable, 0, fTransTable, 0, n3);
                        n3 = n11;
                        array2 = array4;
                        this.fFinalStateFlags = fFinalStateFlags;
                        this.fTransTable = fTransTable;
                    }
                }
            }
        }
        this.fEmptyContentIsValid = ((CMBinOp)this.fHeadNode).getLeft().isNullable();
        this.fHeadNode = null;
        this.fLeafList = null;
        this.fFollowList = null;
    }
    
    private void calcFollowList(final CMNode cmNode) {
        if (cmNode.type() == 4) {
            this.calcFollowList(((CMBinOp)cmNode).getLeft());
            this.calcFollowList(((CMBinOp)cmNode).getRight());
        }
        else if (cmNode.type() == 5) {
            this.calcFollowList(((CMBinOp)cmNode).getLeft());
            this.calcFollowList(((CMBinOp)cmNode).getRight());
            final CMStateSet lastPos = ((CMBinOp)cmNode).getLeft().lastPos();
            final CMStateSet firstPos = ((CMBinOp)cmNode).getRight().firstPos();
            for (int i = 0; i < this.fLeafCount; ++i) {
                if (lastPos.getBit(i)) {
                    this.fFollowList[i].union(firstPos);
                }
            }
        }
        else if (cmNode.type() == 2 || cmNode.type() == 3) {
            this.calcFollowList(((CMUniOp)cmNode).getChild());
            final CMStateSet firstPos2 = cmNode.firstPos();
            final CMStateSet lastPos2 = cmNode.lastPos();
            for (int j = 0; j < this.fLeafCount; ++j) {
                if (lastPos2.getBit(j)) {
                    this.fFollowList[j].union(firstPos2);
                }
            }
        }
        else if (cmNode.type() == 1) {
            this.calcFollowList(((CMUniOp)cmNode).getChild());
        }
    }
    
    private void dumpTree(final CMNode cmNode, final int n) {
        for (int i = 0; i < n; ++i) {
            System.out.print("   ");
        }
        final int type = cmNode.type();
        if (type == 4 || type == 5) {
            if (type == 4) {
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
            this.dumpTree(((CMBinOp)cmNode).getLeft(), n + 1);
            this.dumpTree(((CMBinOp)cmNode).getRight(), n + 1);
        }
        else if (cmNode.type() == 2) {
            System.out.print("Rep Node ");
            if (cmNode.isNullable()) {
                System.out.print("Nullable ");
            }
            System.out.print("firstPos=");
            System.out.print(cmNode.firstPos().toString());
            System.out.print(" lastPos=");
            System.out.println(cmNode.lastPos().toString());
            this.dumpTree(((CMUniOp)cmNode).getChild(), n + 1);
        }
        else {
            if (cmNode.type() != 0) {
                throw new RuntimeException("ImplementationMessages.VAL_NIICM");
            }
            System.out.print("Leaf: (pos=" + ((CMLeaf)cmNode).getPosition() + "), " + ((CMLeaf)cmNode).getElement() + "(elemIndex=" + ((CMLeaf)cmNode).getElement() + ") ");
            if (cmNode.isNullable()) {
                System.out.print(" Nullable ");
            }
            System.out.print("firstPos=");
            System.out.print(cmNode.firstPos().toString());
            System.out.print(" lastPos=");
            System.out.println(cmNode.lastPos().toString());
        }
    }
    
    private int[] makeDefStateList() {
        final int[] array = new int[this.fElemMapSize];
        for (int i = 0; i < this.fElemMapSize; ++i) {
            array[i] = -1;
        }
        return array;
    }
    
    private int postTreeBuildInit(final CMNode cmNode, int n) {
        cmNode.setMaxStates(this.fLeafCount);
        if ((cmNode.type() & 0xF) == 0x6 || (cmNode.type() & 0xF) == 0x8 || (cmNode.type() & 0xF) == 0x7) {
            this.fLeafList[n] = new CMLeaf(new QName(null, null, null, ((CMAny)cmNode).getURI()), ((CMAny)cmNode).getPosition());
            this.fLeafListType[n] = cmNode.type();
            ++n;
        }
        else if (cmNode.type() == 4 || cmNode.type() == 5) {
            n = this.postTreeBuildInit(((CMBinOp)cmNode).getLeft(), n);
            n = this.postTreeBuildInit(((CMBinOp)cmNode).getRight(), n);
        }
        else if (cmNode.type() == 2 || cmNode.type() == 3 || cmNode.type() == 1) {
            n = this.postTreeBuildInit(((CMUniOp)cmNode).getChild(), n);
        }
        else {
            if (cmNode.type() != 0) {
                throw new RuntimeException("ImplementationMessages.VAL_NIICM: type=" + cmNode.type());
            }
            if (((CMLeaf)cmNode).getElement().localpart != DFAContentModel.fEpsilonString) {
                this.fLeafList[n] = (CMLeaf)cmNode;
                this.fLeafListType[n] = 0;
                ++n;
            }
        }
        return n;
    }
    
    static {
        DFAContentModel.fEpsilonString = "<<CMNODE_EPSILON>>";
        DFAContentModel.fEOCString = "<<CMNODE_EOC>>";
        DFAContentModel.fEpsilonString = DFAContentModel.fEpsilonString.intern();
        DFAContentModel.fEOCString = DFAContentModel.fEOCString.intern();
    }
}
