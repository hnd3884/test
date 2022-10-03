package com.sun.org.apache.xerces.internal.impl.xs.models;

import com.sun.org.apache.xerces.internal.impl.dtd.models.CMNode;
import com.sun.org.apache.xerces.internal.impl.xs.XSElementDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSModelGroupImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSParticleDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSComplexTypeDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSDeclarationPool;

public class CMBuilder
{
    private XSDeclarationPool fDeclPool;
    private static XSEmptyCM fEmptyCM;
    private int fLeafCount;
    private int fParticleCount;
    private CMNodeFactory fNodeFactory;
    
    public CMBuilder(final CMNodeFactory nodeFactory) {
        this.fDeclPool = null;
        this.fDeclPool = null;
        this.fNodeFactory = nodeFactory;
    }
    
    public void setDeclPool(final XSDeclarationPool declPool) {
        this.fDeclPool = declPool;
    }
    
    public XSCMValidator getContentModel(final XSComplexTypeDecl typeDecl) {
        final short contentType = typeDecl.getContentType();
        if (contentType == 1 || contentType == 0) {
            return null;
        }
        final XSParticleDecl particle = (XSParticleDecl)typeDecl.getParticle();
        if (particle == null) {
            return CMBuilder.fEmptyCM;
        }
        XSCMValidator cmValidator = null;
        if (particle.fType == 3 && ((XSModelGroupImpl)particle.fValue).fCompositor == 103) {
            cmValidator = this.createAllCM(particle);
        }
        else {
            cmValidator = this.createDFACM(particle);
        }
        this.fNodeFactory.resetNodeCount();
        if (cmValidator == null) {
            cmValidator = CMBuilder.fEmptyCM;
        }
        return cmValidator;
    }
    
    XSCMValidator createAllCM(final XSParticleDecl particle) {
        if (particle.fMaxOccurs == 0) {
            return null;
        }
        final XSModelGroupImpl group = (XSModelGroupImpl)particle.fValue;
        final XSAllCM allContent = new XSAllCM(particle.fMinOccurs == 0, group.fParticleCount);
        for (int i = 0; i < group.fParticleCount; ++i) {
            allContent.addElement((XSElementDecl)group.fParticles[i].fValue, group.fParticles[i].fMinOccurs == 0);
        }
        return allContent;
    }
    
    XSCMValidator createDFACM(final XSParticleDecl particle) {
        this.fLeafCount = 0;
        this.fParticleCount = 0;
        final CMNode node = this.useRepeatingLeafNodes(particle) ? this.buildCompactSyntaxTree(particle) : this.buildSyntaxTree(particle, true);
        if (node == null) {
            return null;
        }
        return new XSDFACM(node, this.fLeafCount);
    }
    
    private CMNode buildSyntaxTree(final XSParticleDecl particle, final boolean optimize) {
        final int maxOccurs = particle.fMaxOccurs;
        final int minOccurs = particle.fMinOccurs;
        final short type = particle.fType;
        CMNode nodeRet = null;
        if (type == 2 || type == 1) {
            nodeRet = this.fNodeFactory.getCMLeafNode(particle.fType, particle.fValue, this.fParticleCount++, this.fLeafCount++);
            nodeRet = this.expandContentModel(nodeRet, minOccurs, maxOccurs, optimize);
        }
        else if (type == 3) {
            final XSModelGroupImpl group = (XSModelGroupImpl)particle.fValue;
            CMNode temp = null;
            boolean twoChildren = false;
            for (int i = 0; i < group.fParticleCount; ++i) {
                temp = this.buildSyntaxTree(group.fParticles[i], optimize && minOccurs == 1 && maxOccurs == 1 && (group.fCompositor == 102 || group.fParticleCount == 1));
                if (temp != null) {
                    if (nodeRet == null) {
                        nodeRet = temp;
                    }
                    else {
                        nodeRet = this.fNodeFactory.getCMBinOpNode(group.fCompositor, nodeRet, temp);
                        twoChildren = true;
                    }
                }
            }
            if (nodeRet != null) {
                if (group.fCompositor == 101 && !twoChildren && group.fParticleCount > 1) {
                    nodeRet = this.fNodeFactory.getCMUniOpNode(5, nodeRet);
                }
                nodeRet = this.expandContentModel(nodeRet, minOccurs, maxOccurs, false);
            }
        }
        return nodeRet;
    }
    
    private CMNode expandContentModel(CMNode node, final int minOccurs, final int maxOccurs, final boolean optimize) {
        CMNode nodeRet = null;
        if (minOccurs == 1 && maxOccurs == 1) {
            nodeRet = node;
        }
        else if (minOccurs == 0 && maxOccurs == 1) {
            nodeRet = this.fNodeFactory.getCMUniOpNode(5, node);
        }
        else if (minOccurs == 0 && maxOccurs == -1) {
            nodeRet = this.fNodeFactory.getCMUniOpNode(4, node);
        }
        else if (minOccurs == 1 && maxOccurs == -1) {
            nodeRet = this.fNodeFactory.getCMUniOpNode(6, node);
        }
        else if ((optimize && node.type() == 1) || node.type() == 2) {
            nodeRet = this.fNodeFactory.getCMUniOpNode((minOccurs == 0) ? 4 : 6, node);
            nodeRet.setUserData(new int[] { minOccurs, maxOccurs });
        }
        else if (maxOccurs == -1) {
            nodeRet = this.fNodeFactory.getCMUniOpNode(6, node);
            nodeRet = this.fNodeFactory.getCMBinOpNode(102, this.multiNodes(node, minOccurs - 1, true), nodeRet);
        }
        else {
            if (minOccurs > 0) {
                nodeRet = this.multiNodes(node, minOccurs, false);
            }
            if (maxOccurs > minOccurs) {
                node = this.fNodeFactory.getCMUniOpNode(5, node);
                if (nodeRet == null) {
                    nodeRet = this.multiNodes(node, maxOccurs - minOccurs, false);
                }
                else {
                    nodeRet = this.fNodeFactory.getCMBinOpNode(102, nodeRet, this.multiNodes(node, maxOccurs - minOccurs, true));
                }
            }
        }
        return nodeRet;
    }
    
    private CMNode multiNodes(final CMNode node, final int num, final boolean copyFirst) {
        if (num == 0) {
            return null;
        }
        if (num == 1) {
            return copyFirst ? this.copyNode(node) : node;
        }
        final int num2 = num / 2;
        return this.fNodeFactory.getCMBinOpNode(102, this.multiNodes(node, num2, copyFirst), this.multiNodes(node, num - num2, true));
    }
    
    private CMNode copyNode(CMNode node) {
        final int type = node.type();
        if (type == 101 || type == 102) {
            final XSCMBinOp bin = (XSCMBinOp)node;
            node = this.fNodeFactory.getCMBinOpNode(type, this.copyNode(bin.getLeft()), this.copyNode(bin.getRight()));
        }
        else if (type == 4 || type == 6 || type == 5) {
            final XSCMUniOp uni = (XSCMUniOp)node;
            node = this.fNodeFactory.getCMUniOpNode(type, this.copyNode(uni.getChild()));
        }
        else if (type == 1 || type == 2) {
            final XSCMLeaf leaf = (XSCMLeaf)node;
            node = this.fNodeFactory.getCMLeafNode(leaf.type(), leaf.getLeaf(), leaf.getParticleId(), this.fLeafCount++);
        }
        return node;
    }
    
    private CMNode buildCompactSyntaxTree(final XSParticleDecl particle) {
        final int maxOccurs = particle.fMaxOccurs;
        final int minOccurs = particle.fMinOccurs;
        final short type = particle.fType;
        CMNode nodeRet = null;
        if (type == 2 || type == 1) {
            return this.buildCompactSyntaxTree2(particle, minOccurs, maxOccurs);
        }
        if (type == 3) {
            final XSModelGroupImpl group = (XSModelGroupImpl)particle.fValue;
            if (group.fParticleCount == 1 && (minOccurs != 1 || maxOccurs != 1)) {
                return this.buildCompactSyntaxTree2(group.fParticles[0], minOccurs, maxOccurs);
            }
            CMNode temp = null;
            int count = 0;
            for (int i = 0; i < group.fParticleCount; ++i) {
                temp = this.buildCompactSyntaxTree(group.fParticles[i]);
                if (temp != null) {
                    ++count;
                    if (nodeRet == null) {
                        nodeRet = temp;
                    }
                    else {
                        nodeRet = this.fNodeFactory.getCMBinOpNode(group.fCompositor, nodeRet, temp);
                    }
                }
            }
            if (nodeRet != null && group.fCompositor == 101 && count < group.fParticleCount) {
                nodeRet = this.fNodeFactory.getCMUniOpNode(5, nodeRet);
            }
        }
        return nodeRet;
    }
    
    private CMNode buildCompactSyntaxTree2(final XSParticleDecl particle, final int minOccurs, final int maxOccurs) {
        CMNode nodeRet = null;
        if (minOccurs == 1 && maxOccurs == 1) {
            nodeRet = this.fNodeFactory.getCMLeafNode(particle.fType, particle.fValue, this.fParticleCount++, this.fLeafCount++);
        }
        else if (minOccurs == 0 && maxOccurs == 1) {
            nodeRet = this.fNodeFactory.getCMLeafNode(particle.fType, particle.fValue, this.fParticleCount++, this.fLeafCount++);
            nodeRet = this.fNodeFactory.getCMUniOpNode(5, nodeRet);
        }
        else if (minOccurs == 0 && maxOccurs == -1) {
            nodeRet = this.fNodeFactory.getCMLeafNode(particle.fType, particle.fValue, this.fParticleCount++, this.fLeafCount++);
            nodeRet = this.fNodeFactory.getCMUniOpNode(4, nodeRet);
        }
        else if (minOccurs == 1 && maxOccurs == -1) {
            nodeRet = this.fNodeFactory.getCMLeafNode(particle.fType, particle.fValue, this.fParticleCount++, this.fLeafCount++);
            nodeRet = this.fNodeFactory.getCMUniOpNode(6, nodeRet);
        }
        else {
            nodeRet = this.fNodeFactory.getCMRepeatingLeafNode(particle.fType, particle.fValue, minOccurs, maxOccurs, this.fParticleCount++, this.fLeafCount++);
            if (minOccurs == 0) {
                nodeRet = this.fNodeFactory.getCMUniOpNode(4, nodeRet);
            }
            else {
                nodeRet = this.fNodeFactory.getCMUniOpNode(6, nodeRet);
            }
        }
        return nodeRet;
    }
    
    private boolean useRepeatingLeafNodes(final XSParticleDecl particle) {
        final int maxOccurs = particle.fMaxOccurs;
        final int minOccurs = particle.fMinOccurs;
        final short type = particle.fType;
        if (type == 3) {
            final XSModelGroupImpl group = (XSModelGroupImpl)particle.fValue;
            if (minOccurs != 1 || maxOccurs != 1) {
                if (group.fParticleCount == 1) {
                    final XSParticleDecl particle2 = group.fParticles[0];
                    final short type2 = particle2.fType;
                    return (type2 == 1 || type2 == 2) && particle2.fMinOccurs == 1 && particle2.fMaxOccurs == 1;
                }
                return group.fParticleCount == 0;
            }
            else {
                for (int i = 0; i < group.fParticleCount; ++i) {
                    if (!this.useRepeatingLeafNodes(group.fParticles[i])) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    static {
        CMBuilder.fEmptyCM = new XSEmptyCM();
    }
}
