package org.apache.xerces.impl.xs;

import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSModelGroup;

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
        int n = 0;
        for (int i = 0; i < this.fParticleCount; ++i) {
            n += this.fParticles[i].minEffectiveTotalRange();
        }
        return n;
    }
    
    private int minEffectiveTotalRangeChoice() {
        int minEffectiveTotalRange = 0;
        if (this.fParticleCount > 0) {
            minEffectiveTotalRange = this.fParticles[0].minEffectiveTotalRange();
        }
        for (int i = 1; i < this.fParticleCount; ++i) {
            final int minEffectiveTotalRange2 = this.fParticles[i].minEffectiveTotalRange();
            if (minEffectiveTotalRange2 < minEffectiveTotalRange) {
                minEffectiveTotalRange = minEffectiveTotalRange2;
            }
        }
        return minEffectiveTotalRange;
    }
    
    public int maxEffectiveTotalRange() {
        if (this.fCompositor == 101) {
            return this.maxEffectiveTotalRangeChoice();
        }
        return this.maxEffectiveTotalRangeAllSeq();
    }
    
    private int maxEffectiveTotalRangeAllSeq() {
        int n = 0;
        for (int i = 0; i < this.fParticleCount; ++i) {
            final int maxEffectiveTotalRange = this.fParticles[i].maxEffectiveTotalRange();
            if (maxEffectiveTotalRange == -1) {
                return -1;
            }
            n += maxEffectiveTotalRange;
        }
        return n;
    }
    
    private int maxEffectiveTotalRangeChoice() {
        int maxEffectiveTotalRange = 0;
        if (this.fParticleCount > 0) {
            maxEffectiveTotalRange = this.fParticles[0].maxEffectiveTotalRange();
            if (maxEffectiveTotalRange == -1) {
                return -1;
            }
        }
        for (int i = 1; i < this.fParticleCount; ++i) {
            final int maxEffectiveTotalRange2 = this.fParticles[i].maxEffectiveTotalRange();
            if (maxEffectiveTotalRange2 == -1) {
                return -1;
            }
            if (maxEffectiveTotalRange2 > maxEffectiveTotalRange) {
                maxEffectiveTotalRange = maxEffectiveTotalRange2;
            }
        }
        return maxEffectiveTotalRange;
    }
    
    public String toString() {
        if (this.fDescription == null) {
            final StringBuffer sb = new StringBuffer();
            if (this.fCompositor == 103) {
                sb.append("all(");
            }
            else {
                sb.append('(');
            }
            if (this.fParticleCount > 0) {
                sb.append(this.fParticles[0].toString());
            }
            for (int i = 1; i < this.fParticleCount; ++i) {
                if (this.fCompositor == 101) {
                    sb.append('|');
                }
                else {
                    sb.append(',');
                }
                sb.append(this.fParticles[i].toString());
            }
            sb.append(')');
            this.fDescription = sb.toString();
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
    
    public short getType() {
        return 7;
    }
    
    public String getName() {
        return null;
    }
    
    public String getNamespace() {
        return null;
    }
    
    public short getCompositor() {
        if (this.fCompositor == 101) {
            return 2;
        }
        if (this.fCompositor == 102) {
            return 1;
        }
        return 3;
    }
    
    public XSObjectList getParticles() {
        return new XSObjectListImpl(this.fParticles, this.fParticleCount);
    }
    
    public XSAnnotation getAnnotation() {
        return (this.fAnnotations != null) ? ((XSAnnotation)this.fAnnotations.item(0)) : null;
    }
    
    public XSObjectList getAnnotations() {
        return (this.fAnnotations != null) ? this.fAnnotations : XSObjectListImpl.EMPTY_LIST;
    }
    
    public XSNamespaceItem getNamespaceItem() {
        return null;
    }
}
