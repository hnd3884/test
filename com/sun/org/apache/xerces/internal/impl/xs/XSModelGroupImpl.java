package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSAnnotation;
import com.sun.org.apache.xerces.internal.xs.XSObject;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSModelGroup;

public class XSModelGroupImpl implements XSModelGroup
{
    public static final short MODELGROUP_CHOICE = 101;
    public static final short MODELGROUP_SEQUENCE = 102;
    public static final short MODELGROUP_ALL = 103;
    public short fCompositor;
    public XSParticleDecl[] fParticles;
    public int fParticleCount;
    public XSObjectList fAnnotations;
    private String fDescription;
    
    public XSModelGroupImpl() {
        this.fParticles = null;
        this.fParticleCount = 0;
        this.fAnnotations = null;
        this.fDescription = null;
    }
    
    public boolean isEmpty() {
        for (int i = 0; i < this.fParticleCount; ++i) {
            if (!this.fParticles[i].isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    public int minEffectiveTotalRange() {
        if (this.fCompositor == 101) {
            return this.minEffectiveTotalRangeChoice();
        }
        return this.minEffectiveTotalRangeAllSeq();
    }
    
    private int minEffectiveTotalRangeAllSeq() {
        int total = 0;
        for (int i = 0; i < this.fParticleCount; ++i) {
            total += this.fParticles[i].minEffectiveTotalRange();
        }
        return total;
    }
    
    private int minEffectiveTotalRangeChoice() {
        int min = 0;
        if (this.fParticleCount > 0) {
            min = this.fParticles[0].minEffectiveTotalRange();
        }
        for (int i = 1; i < this.fParticleCount; ++i) {
            final int one = this.fParticles[i].minEffectiveTotalRange();
            if (one < min) {
                min = one;
            }
        }
        return min;
    }
    
    public int maxEffectiveTotalRange() {
        if (this.fCompositor == 101) {
            return this.maxEffectiveTotalRangeChoice();
        }
        return this.maxEffectiveTotalRangeAllSeq();
    }
    
    private int maxEffectiveTotalRangeAllSeq() {
        int total = 0;
        for (int i = 0; i < this.fParticleCount; ++i) {
            final int one = this.fParticles[i].maxEffectiveTotalRange();
            if (one == -1) {
                return -1;
            }
            total += one;
        }
        return total;
    }
    
    private int maxEffectiveTotalRangeChoice() {
        int max = 0;
        if (this.fParticleCount > 0) {
            max = this.fParticles[0].maxEffectiveTotalRange();
            if (max == -1) {
                return -1;
            }
        }
        for (int i = 1; i < this.fParticleCount; ++i) {
            final int one = this.fParticles[i].maxEffectiveTotalRange();
            if (one == -1) {
                return -1;
            }
            if (one > max) {
                max = one;
            }
        }
        return max;
    }
    
    @Override
    public String toString() {
        if (this.fDescription == null) {
            final StringBuffer buffer = new StringBuffer();
            if (this.fCompositor == 103) {
                buffer.append("all(");
            }
            else {
                buffer.append('(');
            }
            if (this.fParticleCount > 0) {
                buffer.append(this.fParticles[0].toString());
            }
            for (int i = 1; i < this.fParticleCount; ++i) {
                if (this.fCompositor == 101) {
                    buffer.append('|');
                }
                else {
                    buffer.append(',');
                }
                buffer.append(this.fParticles[i].toString());
            }
            buffer.append(')');
            this.fDescription = buffer.toString();
        }
        return this.fDescription;
    }
    
    public void reset() {
        this.fCompositor = 102;
        this.fParticles = null;
        this.fParticleCount = 0;
        this.fDescription = null;
        this.fAnnotations = null;
    }
    
    @Override
    public short getType() {
        return 7;
    }
    
    @Override
    public String getName() {
        return null;
    }
    
    @Override
    public String getNamespace() {
        return null;
    }
    
    @Override
    public short getCompositor() {
        if (this.fCompositor == 101) {
            return 2;
        }
        if (this.fCompositor == 102) {
            return 1;
        }
        return 3;
    }
    
    @Override
    public XSObjectList getParticles() {
        return new XSObjectListImpl(this.fParticles, this.fParticleCount);
    }
    
    @Override
    public XSAnnotation getAnnotation() {
        return (this.fAnnotations != null) ? ((XSAnnotation)this.fAnnotations.item(0)) : null;
    }
    
    @Override
    public XSObjectList getAnnotations() {
        return (this.fAnnotations != null) ? this.fAnnotations : XSObjectListImpl.EMPTY_LIST;
    }
    
    @Override
    public XSNamespaceItem getNamespaceItem() {
        return null;
    }
}
