package org.apache.xerces.impl.xs.models;

import org.apache.xerces.impl.dtd.models.CMNode;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.impl.xs.XSModelGroupImpl;
import org.apache.xerces.impl.xs.XSOpenContentDecl;
import org.apache.xerces.impl.xs.XSParticleDecl;
import org.apache.xerces.impl.xs.XSComplexTypeDecl;
import org.apache.xerces.impl.xs.XSDeclarationPool;

public class CMBuilder
{
    private XSDeclarationPool fDeclPool;
    private static final XSEmptyCM fEmptyCM;
    private int fLeafCount;
    private int fParticleCount;
    private final CMNodeFactory fNodeFactory;
    private short fSchemaVersion;
    
    public CMBuilder(final CMNodeFactory fNodeFactory) {
        this.fDeclPool = null;
        this.fSchemaVersion = 1;
        this.fDeclPool = null;
        this.fNodeFactory = fNodeFactory;
    }
    
    public void setDeclPool(final XSDeclarationPool fDeclPool) {
        this.fDeclPool = fDeclPool;
    }
    
    public void setSchemaVersion(final short fSchemaVersion) {
        this.fSchemaVersion = fSchemaVersion;
    }
    
    public XSCMValidator getContentModel(final XSComplexTypeDecl xsComplexTypeDecl, final boolean b) {
        final short contentType = xsComplexTypeDecl.getContentType();
        if (contentType == 1 || contentType == 0) {
            return null;
        }
        return this.getContentModel((XSParticleDecl)xsComplexTypeDecl.getParticle(), (XSOpenContentDecl)xsComplexTypeDecl.getOpenContent(), b);
    }
    
    public XSCMValidator getContentModel(final XSParticleDecl xsParticleDecl) {
        return this.getContentModel(xsParticleDecl, null, false);
    }
    
    private XSCMValidator getContentModel(final XSParticleDecl xsParticleDecl, final XSOpenContentDecl xsOpenContentDecl, final boolean b) {
        if (xsParticleDecl == null) {
            return CMBuilder.fEmptyCM;
        }
        XSCMValidator xscmValidator;
        if (xsParticleDecl.fType == 3 && ((XSModelGroupImpl)xsParticleDecl.fValue).fCompositor == 103) {
            if (this.fSchemaVersion < 4) {
                xscmValidator = this.createAllCM(xsParticleDecl);
            }
            else {
                xscmValidator = this.createAll11CM(xsParticleDecl, xsOpenContentDecl);
            }
        }
        else {
            xscmValidator = this.createDFACM(xsParticleDecl, b, xsOpenContentDecl);
        }
        this.fNodeFactory.resetNodeCount();
        if (xscmValidator == null) {
            if (xsOpenContentDecl == null) {
                xscmValidator = CMBuilder.fEmptyCM;
            }
            else {
                xscmValidator = new XSEmptyCM(xsOpenContentDecl);
            }
        }
        return xscmValidator;
    }
    
    XSCMValidator createAllCM(final XSParticleDecl xsParticleDecl) {
        if (xsParticleDecl.fMaxOccurs == 0) {
            return null;
        }
        final XSModelGroupImpl xsModelGroupImpl = (XSModelGroupImpl)xsParticleDecl.fValue;
        final XSAllCM xsAllCM = new XSAllCM(xsParticleDecl.fMinOccurs == 0, xsModelGroupImpl.fParticleCount, this.fSchemaVersion);
        for (int i = 0; i < xsModelGroupImpl.fParticleCount; ++i) {
            xsAllCM.addElement((XSElementDecl)xsModelGroupImpl.fParticles[i].fValue, xsModelGroupImpl.fParticles[i].fMinOccurs == 0);
        }
        return xsAllCM;
    }
    
    XSCMValidator createAll11CM(final XSParticleDecl xsParticleDecl, final XSOpenContentDecl xsOpenContentDecl) {
        if (xsParticleDecl.fMaxOccurs == 0) {
            return null;
        }
        final XSModelGroupImpl xsModelGroupImpl = (XSModelGroupImpl)xsParticleDecl.fValue;
        return new XS11AllCM(xsParticleDecl.fMinOccurs == 0, xsModelGroupImpl.fParticleCount, xsModelGroupImpl.fParticles, xsOpenContentDecl);
    }
    
    XSCMValidator createDFACM(final XSParticleDecl xsParticleDecl, final boolean b, final XSOpenContentDecl xsOpenContentDecl) {
        this.fLeafCount = 0;
        this.fParticleCount = 0;
        final CMNode cmNode = this.useRepeatingLeafNodes(xsParticleDecl) ? this.buildCompactSyntaxTree(xsParticleDecl) : this.buildSyntaxTree(xsParticleDecl, b);
        if (cmNode == null) {
            return null;
        }
        return new XSDFACM(cmNode, this.fLeafCount, this.fSchemaVersion, xsOpenContentDecl);
    }
    
    private CMNode buildSyntaxTree(final XSParticleDecl xsParticleDecl, final boolean b) {
        int fMaxOccurs = xsParticleDecl.fMaxOccurs;
        int fMinOccurs = xsParticleDecl.fMinOccurs;
        boolean b2 = false;
        if (b) {
            if (fMinOccurs > 1) {
                if (fMaxOccurs > fMinOccurs || xsParticleDecl.getMaxOccursUnbounded()) {
                    fMinOccurs = 1;
                    b2 = true;
                }
                else {
                    fMinOccurs = 2;
                    b2 = true;
                }
            }
            if (fMaxOccurs > 1) {
                fMaxOccurs = 2;
                b2 = true;
            }
        }
        final short fType = xsParticleDecl.fType;
        CMNode cmNode = null;
        if (fType == 2 || fType == 1) {
            cmNode = this.expandContentModel(this.fNodeFactory.getCMLeafNode(xsParticleDecl.fType, xsParticleDecl.fValue, this.fParticleCount++, this.fLeafCount++), fMinOccurs, fMaxOccurs);
            if (cmNode != null) {
                cmNode.setIsCompactUPAModel(b2);
            }
        }
        else if (fType == 3) {
            final XSModelGroupImpl xsModelGroupImpl = (XSModelGroupImpl)xsParticleDecl.fValue;
            int n = 0;
            for (int i = 0; i < xsModelGroupImpl.fParticleCount; ++i) {
                final CMNode buildSyntaxTree = this.buildSyntaxTree(xsModelGroupImpl.fParticles[i], b);
                if (buildSyntaxTree != null) {
                    b2 |= buildSyntaxTree.isCompactedForUPA();
                    ++n;
                    if (cmNode == null) {
                        cmNode = buildSyntaxTree;
                    }
                    else {
                        cmNode = this.fNodeFactory.getCMBinOpNode(xsModelGroupImpl.fCompositor, cmNode, buildSyntaxTree);
                    }
                }
            }
            if (cmNode != null) {
                if (xsModelGroupImpl.fCompositor == 101 && n < xsModelGroupImpl.fParticleCount) {
                    cmNode = this.fNodeFactory.getCMUniOpNode(5, cmNode);
                }
                cmNode = this.expandContentModel(cmNode, fMinOccurs, fMaxOccurs);
                cmNode.setIsCompactUPAModel(b2);
            }
        }
        return cmNode;
    }
    
    private CMNode expandContentModel(CMNode cmUniOpNode, final int n, final int n2) {
        CMNode cmNode = null;
        if (n == 1 && n2 == 1) {
            cmNode = cmUniOpNode;
        }
        else if (n == 0 && n2 == 1) {
            cmNode = this.fNodeFactory.getCMUniOpNode(5, cmUniOpNode);
        }
        else if (n == 0 && n2 == -1) {
            cmNode = this.fNodeFactory.getCMUniOpNode(4, cmUniOpNode);
        }
        else if (n == 1 && n2 == -1) {
            cmNode = this.fNodeFactory.getCMUniOpNode(6, cmUniOpNode);
        }
        else if (n2 == -1) {
            cmNode = this.fNodeFactory.getCMBinOpNode(102, this.multiNodes(cmUniOpNode, n - 1, true), this.fNodeFactory.getCMUniOpNode(6, cmUniOpNode));
        }
        else {
            if (n > 0) {
                cmNode = this.multiNodes(cmUniOpNode, n, false);
            }
            if (n2 > n) {
                cmUniOpNode = this.fNodeFactory.getCMUniOpNode(5, cmUniOpNode);
                if (cmNode == null) {
                    cmNode = this.multiNodes(cmUniOpNode, n2 - n, false);
                }
                else {
                    cmNode = this.fNodeFactory.getCMBinOpNode(102, cmNode, this.multiNodes(cmUniOpNode, n2 - n, true));
                }
            }
        }
        return cmNode;
    }
    
    private CMNode multiNodes(final CMNode cmNode, final int n, final boolean b) {
        if (n == 0) {
            return null;
        }
        if (n == 1) {
            return b ? this.copyNode(cmNode) : cmNode;
        }
        final int n2 = n / 2;
        return this.fNodeFactory.getCMBinOpNode(102, this.multiNodes(cmNode, n2, b), this.multiNodes(cmNode, n - n2, true));
    }
    
    private CMNode copyNode(CMNode cmNode) {
        final int type = cmNode.type();
        if (type == 101 || type == 102) {
            final XSCMBinOp xscmBinOp = (XSCMBinOp)cmNode;
            cmNode = this.fNodeFactory.getCMBinOpNode(type, this.copyNode(xscmBinOp.getLeft()), this.copyNode(xscmBinOp.getRight()));
        }
        else if (type == 4 || type == 6 || type == 5) {
            cmNode = this.fNodeFactory.getCMUniOpNode(type, this.copyNode(((XSCMUniOp)cmNode).getChild()));
        }
        else if (type == 1 || type == 2) {
            final XSCMLeaf xscmLeaf = (XSCMLeaf)cmNode;
            cmNode = this.fNodeFactory.getCMLeafNode(xscmLeaf.type(), xscmLeaf.getLeaf(), xscmLeaf.getParticleId(), this.fLeafCount++);
        }
        return cmNode;
    }
    
    private CMNode buildCompactSyntaxTree(final XSParticleDecl xsParticleDecl) {
        final int fMaxOccurs = xsParticleDecl.fMaxOccurs;
        final int fMinOccurs = xsParticleDecl.fMinOccurs;
        final short fType = xsParticleDecl.fType;
        CMNode cmNode = null;
        if (fType == 2 || fType == 1) {
            return this.buildCompactSyntaxTree2(xsParticleDecl, fMinOccurs, fMaxOccurs);
        }
        if (fType == 3) {
            final XSModelGroupImpl xsModelGroupImpl = (XSModelGroupImpl)xsParticleDecl.fValue;
            if (xsModelGroupImpl.fParticleCount == 1 && (fMinOccurs != 1 || fMaxOccurs != 1)) {
                return this.buildCompactSyntaxTree2(xsModelGroupImpl.fParticles[0], fMinOccurs, fMaxOccurs);
            }
            int n = 0;
            for (int i = 0; i < xsModelGroupImpl.fParticleCount; ++i) {
                final CMNode buildCompactSyntaxTree = this.buildCompactSyntaxTree(xsModelGroupImpl.fParticles[i]);
                if (buildCompactSyntaxTree != null) {
                    ++n;
                    if (cmNode == null) {
                        cmNode = buildCompactSyntaxTree;
                    }
                    else {
                        cmNode = this.fNodeFactory.getCMBinOpNode(xsModelGroupImpl.fCompositor, cmNode, buildCompactSyntaxTree);
                    }
                }
            }
            if (cmNode != null && xsModelGroupImpl.fCompositor == 101 && n < xsModelGroupImpl.fParticleCount) {
                cmNode = this.fNodeFactory.getCMUniOpNode(5, cmNode);
            }
        }
        return cmNode;
    }
    
    private CMNode buildCompactSyntaxTree2(final XSParticleDecl xsParticleDecl, final int n, final int n2) {
        CMNode cmNode;
        if (n == 1 && n2 == 1) {
            cmNode = this.fNodeFactory.getCMLeafNode(xsParticleDecl.fType, xsParticleDecl.fValue, this.fParticleCount++, this.fLeafCount++);
        }
        else if (n == 0 && n2 == 1) {
            cmNode = this.fNodeFactory.getCMUniOpNode(5, this.fNodeFactory.getCMLeafNode(xsParticleDecl.fType, xsParticleDecl.fValue, this.fParticleCount++, this.fLeafCount++));
        }
        else if (n == 0 && n2 == -1) {
            cmNode = this.fNodeFactory.getCMUniOpNode(4, this.fNodeFactory.getCMLeafNode(xsParticleDecl.fType, xsParticleDecl.fValue, this.fParticleCount++, this.fLeafCount++));
        }
        else if (n == 1 && n2 == -1) {
            cmNode = this.fNodeFactory.getCMUniOpNode(6, this.fNodeFactory.getCMLeafNode(xsParticleDecl.fType, xsParticleDecl.fValue, this.fParticleCount++, this.fLeafCount++));
        }
        else {
            final CMNode cmRepeatingLeafNode = this.fNodeFactory.getCMRepeatingLeafNode(xsParticleDecl.fType, xsParticleDecl.fValue, n, n2, this.fParticleCount++, this.fLeafCount++);
            if (n == 0) {
                cmNode = this.fNodeFactory.getCMUniOpNode(4, cmRepeatingLeafNode);
            }
            else {
                cmNode = this.fNodeFactory.getCMUniOpNode(6, cmRepeatingLeafNode);
            }
        }
        return cmNode;
    }
    
    private boolean useRepeatingLeafNodes(final XSParticleDecl xsParticleDecl) {
        final int fMaxOccurs = xsParticleDecl.fMaxOccurs;
        final int fMinOccurs = xsParticleDecl.fMinOccurs;
        if (xsParticleDecl.fType == 3) {
            final XSModelGroupImpl xsModelGroupImpl = (XSModelGroupImpl)xsParticleDecl.fValue;
            if (fMinOccurs != 1 || fMaxOccurs != 1) {
                if (xsModelGroupImpl.fParticleCount == 1) {
                    final XSParticleDecl xsParticleDecl2 = xsModelGroupImpl.fParticles[0];
                    final short fType = xsParticleDecl2.fType;
                    return (fType == 1 || fType == 2) && xsParticleDecl2.fMinOccurs == 1 && xsParticleDecl2.fMaxOccurs == 1;
                }
                return xsModelGroupImpl.fParticleCount == 0;
            }
            else {
                for (int i = 0; i < xsModelGroupImpl.fParticleCount; ++i) {
                    if (!this.useRepeatingLeafNodes(xsModelGroupImpl.fParticles[i])) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    void testOccurrences(final int n) {
        this.fNodeFactory.testOccurrences(n);
    }
    
    static {
        fEmptyCM = new XSEmptyCM();
    }
}
