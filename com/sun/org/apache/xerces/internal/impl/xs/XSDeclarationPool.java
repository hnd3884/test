package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.dv.xs.SchemaDVFactoryImpl;
import com.sun.org.apache.xerces.internal.impl.dv.xs.XSSimpleTypeDecl;

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
    private SchemaDVFactoryImpl dvFactory;
    
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
    
    public void setDVFactory(final SchemaDVFactoryImpl dvFactory) {
        this.dvFactory = dvFactory;
    }
    
    public final XSElementDecl getElementDecl() {
        final int chunk = this.fElementDeclIndex >> 8;
        final int index = this.fElementDeclIndex & 0xFF;
        this.ensureElementDeclCapacity(chunk);
        if (this.fElementDecl[chunk][index] == null) {
            this.fElementDecl[chunk][index] = new XSElementDecl();
        }
        else {
            this.fElementDecl[chunk][index].reset();
        }
        ++this.fElementDeclIndex;
        return this.fElementDecl[chunk][index];
    }
    
    public final XSAttributeDecl getAttributeDecl() {
        final int chunk = this.fAttrDeclIndex >> 8;
        final int index = this.fAttrDeclIndex & 0xFF;
        this.ensureAttrDeclCapacity(chunk);
        if (this.fAttrDecl[chunk][index] == null) {
            this.fAttrDecl[chunk][index] = new XSAttributeDecl();
        }
        else {
            this.fAttrDecl[chunk][index].reset();
        }
        ++this.fAttrDeclIndex;
        return this.fAttrDecl[chunk][index];
    }
    
    public final XSAttributeUseImpl getAttributeUse() {
        final int chunk = this.fAttributeUseIndex >> 8;
        final int index = this.fAttributeUseIndex & 0xFF;
        this.ensureAttributeUseCapacity(chunk);
        if (this.fAttributeUse[chunk][index] == null) {
            this.fAttributeUse[chunk][index] = new XSAttributeUseImpl();
        }
        else {
            this.fAttributeUse[chunk][index].reset();
        }
        ++this.fAttributeUseIndex;
        return this.fAttributeUse[chunk][index];
    }
    
    public final XSComplexTypeDecl getComplexTypeDecl() {
        final int chunk = this.fCTDeclIndex >> 8;
        final int index = this.fCTDeclIndex & 0xFF;
        this.ensureCTDeclCapacity(chunk);
        if (this.fCTDecl[chunk][index] == null) {
            this.fCTDecl[chunk][index] = new XSComplexTypeDecl();
        }
        else {
            this.fCTDecl[chunk][index].reset();
        }
        ++this.fCTDeclIndex;
        return this.fCTDecl[chunk][index];
    }
    
    public final XSSimpleTypeDecl getSimpleTypeDecl() {
        final int chunk = this.fSTDeclIndex >> 8;
        final int index = this.fSTDeclIndex & 0xFF;
        this.ensureSTDeclCapacity(chunk);
        if (this.fSTDecl[chunk][index] == null) {
            this.fSTDecl[chunk][index] = this.dvFactory.newXSSimpleTypeDecl();
        }
        else {
            this.fSTDecl[chunk][index].reset();
        }
        ++this.fSTDeclIndex;
        return this.fSTDecl[chunk][index];
    }
    
    public final XSParticleDecl getParticleDecl() {
        final int chunk = this.fParticleDeclIndex >> 8;
        final int index = this.fParticleDeclIndex & 0xFF;
        this.ensureParticleDeclCapacity(chunk);
        if (this.fParticleDecl[chunk][index] == null) {
            this.fParticleDecl[chunk][index] = new XSParticleDecl();
        }
        else {
            this.fParticleDecl[chunk][index].reset();
        }
        ++this.fParticleDeclIndex;
        return this.fParticleDecl[chunk][index];
    }
    
    public final XSModelGroupImpl getModelGroup() {
        final int chunk = this.fModelGroupIndex >> 8;
        final int index = this.fModelGroupIndex & 0xFF;
        this.ensureModelGroupCapacity(chunk);
        if (this.fModelGroup[chunk][index] == null) {
            this.fModelGroup[chunk][index] = new XSModelGroupImpl();
        }
        else {
            this.fModelGroup[chunk][index].reset();
        }
        ++this.fModelGroupIndex;
        return this.fModelGroup[chunk][index];
    }
    
    private boolean ensureElementDeclCapacity(final int chunk) {
        if (chunk >= this.fElementDecl.length) {
            this.fElementDecl = resize(this.fElementDecl, this.fElementDecl.length * 2);
        }
        else if (this.fElementDecl[chunk] != null) {
            return false;
        }
        this.fElementDecl[chunk] = new XSElementDecl[256];
        return true;
    }
    
    private static XSElementDecl[][] resize(final XSElementDecl[][] array, final int newsize) {
        final XSElementDecl[][] newarray = new XSElementDecl[newsize][];
        System.arraycopy(array, 0, newarray, 0, array.length);
        return newarray;
    }
    
    private boolean ensureParticleDeclCapacity(final int chunk) {
        if (chunk >= this.fParticleDecl.length) {
            this.fParticleDecl = resize(this.fParticleDecl, this.fParticleDecl.length * 2);
        }
        else if (this.fParticleDecl[chunk] != null) {
            return false;
        }
        this.fParticleDecl[chunk] = new XSParticleDecl[256];
        return true;
    }
    
    private boolean ensureModelGroupCapacity(final int chunk) {
        if (chunk >= this.fModelGroup.length) {
            this.fModelGroup = resize(this.fModelGroup, this.fModelGroup.length * 2);
        }
        else if (this.fModelGroup[chunk] != null) {
            return false;
        }
        this.fModelGroup[chunk] = new XSModelGroupImpl[256];
        return true;
    }
    
    private static XSParticleDecl[][] resize(final XSParticleDecl[][] array, final int newsize) {
        final XSParticleDecl[][] newarray = new XSParticleDecl[newsize][];
        System.arraycopy(array, 0, newarray, 0, array.length);
        return newarray;
    }
    
    private static XSModelGroupImpl[][] resize(final XSModelGroupImpl[][] array, final int newsize) {
        final XSModelGroupImpl[][] newarray = new XSModelGroupImpl[newsize][];
        System.arraycopy(array, 0, newarray, 0, array.length);
        return newarray;
    }
    
    private boolean ensureAttrDeclCapacity(final int chunk) {
        if (chunk >= this.fAttrDecl.length) {
            this.fAttrDecl = resize(this.fAttrDecl, this.fAttrDecl.length * 2);
        }
        else if (this.fAttrDecl[chunk] != null) {
            return false;
        }
        this.fAttrDecl[chunk] = new XSAttributeDecl[256];
        return true;
    }
    
    private static XSAttributeDecl[][] resize(final XSAttributeDecl[][] array, final int newsize) {
        final XSAttributeDecl[][] newarray = new XSAttributeDecl[newsize][];
        System.arraycopy(array, 0, newarray, 0, array.length);
        return newarray;
    }
    
    private boolean ensureAttributeUseCapacity(final int chunk) {
        if (chunk >= this.fAttributeUse.length) {
            this.fAttributeUse = resize(this.fAttributeUse, this.fAttributeUse.length * 2);
        }
        else if (this.fAttributeUse[chunk] != null) {
            return false;
        }
        this.fAttributeUse[chunk] = new XSAttributeUseImpl[256];
        return true;
    }
    
    private static XSAttributeUseImpl[][] resize(final XSAttributeUseImpl[][] array, final int newsize) {
        final XSAttributeUseImpl[][] newarray = new XSAttributeUseImpl[newsize][];
        System.arraycopy(array, 0, newarray, 0, array.length);
        return newarray;
    }
    
    private boolean ensureSTDeclCapacity(final int chunk) {
        if (chunk >= this.fSTDecl.length) {
            this.fSTDecl = resize(this.fSTDecl, this.fSTDecl.length * 2);
        }
        else if (this.fSTDecl[chunk] != null) {
            return false;
        }
        this.fSTDecl[chunk] = new XSSimpleTypeDecl[256];
        return true;
    }
    
    private static XSSimpleTypeDecl[][] resize(final XSSimpleTypeDecl[][] array, final int newsize) {
        final XSSimpleTypeDecl[][] newarray = new XSSimpleTypeDecl[newsize][];
        System.arraycopy(array, 0, newarray, 0, array.length);
        return newarray;
    }
    
    private boolean ensureCTDeclCapacity(final int chunk) {
        if (chunk >= this.fCTDecl.length) {
            this.fCTDecl = resize(this.fCTDecl, this.fCTDecl.length * 2);
        }
        else if (this.fCTDecl[chunk] != null) {
            return false;
        }
        this.fCTDecl[chunk] = new XSComplexTypeDecl[256];
        return true;
    }
    
    private static XSComplexTypeDecl[][] resize(final XSComplexTypeDecl[][] array, final int newsize) {
        final XSComplexTypeDecl[][] newarray = new XSComplexTypeDecl[newsize][];
        System.arraycopy(array, 0, newarray, 0, array.length);
        return newarray;
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
