package org.apache.xerces.impl.xs;

import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSParticle;

public class XSParticleDecl implements XSParticle
{
    public static final short PARTICLE_EMPTY = 0;
    public static final short PARTICLE_ELEMENT = 1;
    public static final short PARTICLE_WILDCARD = 2;
    public static final short PARTICLE_MODELGROUP = 3;
    public static final short PARTICLE_ZERO_OR_MORE = 4;
    public static final short PARTICLE_ZERO_OR_ONE = 5;
    public static final short PARTICLE_ONE_OR_MORE = 6;
    public short fType;
    public XSTerm fValue;
    public int fMinOccurs;
    public int fMaxOccurs;
    public XSObjectList fAnnotations;
    private String fDescription;
    
    public XSParticleDecl() {
        this.fType = 0;
        this.fValue = null;
        this.fMinOccurs = 1;
        this.fMaxOccurs = 1;
        this.fAnnotations = null;
        this.fDescription = null;
    }
    
    public XSParticleDecl makeClone() {
        final XSParticleDecl xsParticleDecl = new XSParticleDecl();
        xsParticleDecl.fType = this.fType;
        xsParticleDecl.fMinOccurs = this.fMinOccurs;
        xsParticleDecl.fMaxOccurs = this.fMaxOccurs;
        xsParticleDecl.fDescription = this.fDescription;
        xsParticleDecl.fValue = this.fValue;
        xsParticleDecl.fAnnotations = this.fAnnotations;
        return xsParticleDecl;
    }
    
    public boolean emptiable() {
        return this.minEffectiveTotalRange() == 0;
    }
    
    public boolean isEmpty() {
        return this.fType == 0 || (this.fType != 1 && this.fType != 2 && ((XSModelGroupImpl)this.fValue).isEmpty());
    }
    
    public int minEffectiveTotalRange() {
        if (this.fType == 0) {
            return 0;
        }
        if (this.fType == 3) {
            return ((XSModelGroupImpl)this.fValue).minEffectiveTotalRange() * this.fMinOccurs;
        }
        return this.fMinOccurs;
    }
    
    public int maxEffectiveTotalRange() {
        if (this.fType == 0) {
            return 0;
        }
        if (this.fType != 3) {
            return this.fMaxOccurs;
        }
        final int maxEffectiveTotalRange = ((XSModelGroupImpl)this.fValue).maxEffectiveTotalRange();
        if (maxEffectiveTotalRange == -1) {
            return -1;
        }
        if (maxEffectiveTotalRange != 0 && this.fMaxOccurs == -1) {
            return -1;
        }
        return maxEffectiveTotalRange * this.fMaxOccurs;
    }
    
    public String toString() {
        if (this.fDescription == null) {
            final StringBuffer sb = new StringBuffer();
            this.appendParticle(sb);
            if ((this.fMinOccurs != 0 || this.fMaxOccurs != 0) && (this.fMinOccurs != 1 || this.fMaxOccurs != 1)) {
                sb.append('{').append(this.fMinOccurs);
                if (this.fMaxOccurs == -1) {
                    sb.append("-UNBOUNDED");
                }
                else if (this.fMinOccurs != this.fMaxOccurs) {
                    sb.append('-').append(this.fMaxOccurs);
                }
                sb.append('}');
            }
            this.fDescription = sb.toString();
        }
        return this.fDescription;
    }
    
    void appendParticle(final StringBuffer sb) {
        switch (this.fType) {
            case 0: {
                sb.append("EMPTY");
                break;
            }
            case 1: {
                sb.append(this.fValue.toString());
                break;
            }
            case 2: {
                sb.append('(');
                sb.append(this.fValue.toString());
                sb.append(')');
                break;
            }
            case 3: {
                sb.append(this.fValue.toString());
                break;
            }
        }
    }
    
    public void reset() {
        this.fType = 0;
        this.fValue = null;
        this.fMinOccurs = 1;
        this.fMaxOccurs = 1;
        this.fDescription = null;
        this.fAnnotations = null;
    }
    
    public short getType() {
        return 8;
    }
    
    public String getName() {
        return null;
    }
    
    public String getNamespace() {
        return null;
    }
    
    public int getMinOccurs() {
        return this.fMinOccurs;
    }
    
    public boolean getMaxOccursUnbounded() {
        return this.fMaxOccurs == -1;
    }
    
    public int getMaxOccurs() {
        return this.fMaxOccurs;
    }
    
    public XSTerm getTerm() {
        return this.fValue;
    }
    
    public XSNamespaceItem getNamespaceItem() {
        return null;
    }
    
    public XSObjectList getAnnotations() {
        return (this.fAnnotations != null) ? this.fAnnotations : XSObjectListImpl.EMPTY_LIST;
    }
}
