package org.apache.xerces.impl.xs;

import org.apache.xerces.impl.dv.xs.BaseSchemaDVFactory;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;

public final class XSDeclarationPool
{
    private static final int CHUNK_SHIFT = 8;
    private static final int CHUNK_SIZE = 256;
    private static final int CHUNK_MASK = 255;
    private static final int INITIAL_CHUNK_COUNT = 4;
    private XSElementDecl[][] fElementDecl;
    private int fElementDeclIndex;
    private XSParticleDecl[][] fParticleDecl;
    private int fParticleDeclIndex;
    private XSModelGroupImpl[][] fModelGroup;
    private int fModelGroupIndex;
    private XSAttributeDecl[][] fAttrDecl;
    private int fAttrDeclIndex;
    private XSComplexTypeDecl[][] fCTDecl;
    private int fCTDeclIndex;
    private XSSimpleTypeDecl[][] fSTDecl;
    private int fSTDeclIndex;
    private XSAttributeUseImpl[][] fAttributeUse;
    private int fAttributeUseIndex;
    private BaseSchemaDVFactory dvFactory;
    
    public XSDeclarationPool() {
        this.fElementDecl = new XSElementDecl[4][];
        this.fElementDeclIndex = 0;
        this.fParticleDecl = new XSParticleDecl[4][];
        this.fParticleDeclIndex = 0;
        this.fModelGroup = new XSModelGroupImpl[4][];
        this.fModelGroupIndex = 0;
        this.fAttrDecl = new XSAttributeDecl[4][];
        this.fAttrDeclIndex = 0;
        this.fCTDecl = new XSComplexTypeDecl[4][];
        this.fCTDeclIndex = 0;
        this.fSTDecl = new XSSimpleTypeDecl[4][];
        this.fSTDeclIndex = 0;
        this.fAttributeUse = new XSAttributeUseImpl[4][];
        this.fAttributeUseIndex = 0;
    }
    
    public void setDVFactory(final BaseSchemaDVFactory dvFactory) {
        this.dvFactory = dvFactory;
    }
    
    public final XSElementDecl getElementDecl() {
        final int n = this.fElementDeclIndex >> 8;
        final int n2 = this.fElementDeclIndex & 0xFF;
        this.ensureElementDeclCapacity(n);
        if (this.fElementDecl[n][n2] == null) {
            this.fElementDecl[n][n2] = new XSElementDecl();
        }
        else {
            this.fElementDecl[n][n2].reset();
        }
        ++this.fElementDeclIndex;
        return this.fElementDecl[n][n2];
    }
    
    public final XSAttributeDecl getAttributeDecl() {
        final int n = this.fAttrDeclIndex >> 8;
        final int n2 = this.fAttrDeclIndex & 0xFF;
        this.ensureAttrDeclCapacity(n);
        if (this.fAttrDecl[n][n2] == null) {
            this.fAttrDecl[n][n2] = new XSAttributeDecl();
        }
        else {
            this.fAttrDecl[n][n2].reset();
        }
        ++this.fAttrDeclIndex;
        return this.fAttrDecl[n][n2];
    }
    
    public final XSAttributeUseImpl getAttributeUse() {
        final int n = this.fAttributeUseIndex >> 8;
        final int n2 = this.fAttributeUseIndex & 0xFF;
        this.ensureAttributeUseCapacity(n);
        if (this.fAttributeUse[n][n2] == null) {
            this.fAttributeUse[n][n2] = new XSAttributeUseImpl();
        }
        else {
            this.fAttributeUse[n][n2].reset();
        }
        ++this.fAttributeUseIndex;
        return this.fAttributeUse[n][n2];
    }
    
    public final XSComplexTypeDecl getComplexTypeDecl() {
        final int n = this.fCTDeclIndex >> 8;
        final int n2 = this.fCTDeclIndex & 0xFF;
        this.ensureCTDeclCapacity(n);
        if (this.fCTDecl[n][n2] == null) {
            this.fCTDecl[n][n2] = new XSComplexTypeDecl();
        }
        else {
            this.fCTDecl[n][n2].reset();
        }
        ++this.fCTDeclIndex;
        return this.fCTDecl[n][n2];
    }
    
    public final XSSimpleTypeDecl getSimpleTypeDecl() {
        final int n = this.fSTDeclIndex >> 8;
        final int n2 = this.fSTDeclIndex & 0xFF;
        this.ensureSTDeclCapacity(n);
        if (this.fSTDecl[n][n2] == null) {
            this.fSTDecl[n][n2] = this.dvFactory.newXSSimpleTypeDecl();
        }
        else {
            this.fSTDecl[n][n2].reset();
        }
        ++this.fSTDeclIndex;
        return this.fSTDecl[n][n2];
    }
    
    public final XSParticleDecl getParticleDecl() {
        final int n = this.fParticleDeclIndex >> 8;
        final int n2 = this.fParticleDeclIndex & 0xFF;
        this.ensureParticleDeclCapacity(n);
        if (this.fParticleDecl[n][n2] == null) {
            this.fParticleDecl[n][n2] = new XSParticleDecl();
        }
        else {
            this.fParticleDecl[n][n2].reset();
        }
        ++this.fParticleDeclIndex;
        return this.fParticleDecl[n][n2];
    }
    
    public final XSModelGroupImpl getModelGroup() {
        final int n = this.fModelGroupIndex >> 8;
        final int n2 = this.fModelGroupIndex & 0xFF;
        this.ensureModelGroupCapacity(n);
        if (this.fModelGroup[n][n2] == null) {
            this.fModelGroup[n][n2] = new XSModelGroupImpl();
        }
        else {
            this.fModelGroup[n][n2].reset();
        }
        ++this.fModelGroupIndex;
        return this.fModelGroup[n][n2];
    }
    
    private boolean ensureElementDeclCapacity(final int n) {
        if (n >= this.fElementDecl.length) {
            this.fElementDecl = resize(this.fElementDecl, this.fElementDecl.length * 2);
        }
        else if (this.fElementDecl[n] != null) {
            return false;
        }
        this.fElementDecl[n] = new XSElementDecl[256];
        return true;
    }
    
    private static XSElementDecl[][] resize(final XSElementDecl[][] array, final int n) {
        final XSElementDecl[][] array2 = new XSElementDecl[n][];
        System.arraycopy(array, 0, array2, 0, array.length);
        return array2;
    }
    
    private boolean ensureParticleDeclCapacity(final int n) {
        if (n >= this.fParticleDecl.length) {
            this.fParticleDecl = resize(this.fParticleDecl, this.fParticleDecl.length * 2);
        }
        else if (this.fParticleDecl[n] != null) {
            return false;
        }
        this.fParticleDecl[n] = new XSParticleDecl[256];
        return true;
    }
    
    private boolean ensureModelGroupCapacity(final int n) {
        if (n >= this.fModelGroup.length) {
            this.fModelGroup = resize(this.fModelGroup, this.fModelGroup.length * 2);
        }
        else if (this.fModelGroup[n] != null) {
            return false;
        }
        this.fModelGroup[n] = new XSModelGroupImpl[256];
        return true;
    }
    
    private static XSParticleDecl[][] resize(final XSParticleDecl[][] array, final int n) {
        final XSParticleDecl[][] array2 = new XSParticleDecl[n][];
        System.arraycopy(array, 0, array2, 0, array.length);
        return array2;
    }
    
    private static XSModelGroupImpl[][] resize(final XSModelGroupImpl[][] array, final int n) {
        final XSModelGroupImpl[][] array2 = new XSModelGroupImpl[n][];
        System.arraycopy(array, 0, array2, 0, array.length);
        return array2;
    }
    
    private boolean ensureAttrDeclCapacity(final int n) {
        if (n >= this.fAttrDecl.length) {
            this.fAttrDecl = resize(this.fAttrDecl, this.fAttrDecl.length * 2);
        }
        else if (this.fAttrDecl[n] != null) {
            return false;
        }
        this.fAttrDecl[n] = new XSAttributeDecl[256];
        return true;
    }
    
    private static XSAttributeDecl[][] resize(final XSAttributeDecl[][] array, final int n) {
        final XSAttributeDecl[][] array2 = new XSAttributeDecl[n][];
        System.arraycopy(array, 0, array2, 0, array.length);
        return array2;
    }
    
    private boolean ensureAttributeUseCapacity(final int n) {
        if (n >= this.fAttributeUse.length) {
            this.fAttributeUse = resize(this.fAttributeUse, this.fAttributeUse.length * 2);
        }
        else if (this.fAttributeUse[n] != null) {
            return false;
        }
        this.fAttributeUse[n] = new XSAttributeUseImpl[256];
        return true;
    }
    
    private static XSAttributeUseImpl[][] resize(final XSAttributeUseImpl[][] array, final int n) {
        final XSAttributeUseImpl[][] array2 = new XSAttributeUseImpl[n][];
        System.arraycopy(array, 0, array2, 0, array.length);
        return array2;
    }
    
    private boolean ensureSTDeclCapacity(final int n) {
        if (n >= this.fSTDecl.length) {
            this.fSTDecl = resize(this.fSTDecl, this.fSTDecl.length * 2);
        }
        else if (this.fSTDecl[n] != null) {
            return false;
        }
        this.fSTDecl[n] = new XSSimpleTypeDecl[256];
        return true;
    }
    
    private static XSSimpleTypeDecl[][] resize(final XSSimpleTypeDecl[][] array, final int n) {
        final XSSimpleTypeDecl[][] array2 = new XSSimpleTypeDecl[n][];
        System.arraycopy(array, 0, array2, 0, array.length);
        return array2;
    }
    
    private boolean ensureCTDeclCapacity(final int n) {
        if (n >= this.fCTDecl.length) {
            this.fCTDecl = resize(this.fCTDecl, this.fCTDecl.length * 2);
        }
        else if (this.fCTDecl[n] != null) {
            return false;
        }
        this.fCTDecl[n] = new XSComplexTypeDecl[256];
        return true;
    }
    
    private static XSComplexTypeDecl[][] resize(final XSComplexTypeDecl[][] array, final int n) {
        final XSComplexTypeDecl[][] array2 = new XSComplexTypeDecl[n][];
        System.arraycopy(array, 0, array2, 0, array.length);
        return array2;
    }
    
    public void reset() {
        this.fElementDeclIndex = 0;
        this.fParticleDeclIndex = 0;
        this.fModelGroupIndex = 0;
        this.fSTDeclIndex = 0;
        this.fCTDeclIndex = 0;
        this.fAttrDeclIndex = 0;
        this.fAttributeUseIndex = 0;
    }
}
