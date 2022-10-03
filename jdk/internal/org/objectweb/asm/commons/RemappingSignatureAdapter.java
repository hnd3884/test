package jdk.internal.org.objectweb.asm.commons;

import jdk.internal.org.objectweb.asm.signature.SignatureVisitor;

public class RemappingSignatureAdapter extends SignatureVisitor
{
    private final SignatureVisitor v;
    private final Remapper remapper;
    private String className;
    
    public RemappingSignatureAdapter(final SignatureVisitor signatureVisitor, final Remapper remapper) {
        this(327680, signatureVisitor, remapper);
    }
    
    protected RemappingSignatureAdapter(final int n, final SignatureVisitor v, final Remapper remapper) {
        super(n);
        this.v = v;
        this.remapper = remapper;
    }
    
    @Override
    public void visitClassType(final String className) {
        this.className = className;
        this.v.visitClassType(this.remapper.mapType(className));
    }
    
    @Override
    public void visitInnerClassType(final String s) {
        final String string = this.remapper.mapType(this.className) + '$';
        this.className = this.className + '$' + s;
        final String mapType = this.remapper.mapType(this.className);
        this.v.visitInnerClassType(mapType.substring(mapType.startsWith(string) ? string.length() : (mapType.lastIndexOf(36) + 1)));
    }
    
    @Override
    public void visitFormalTypeParameter(final String s) {
        this.v.visitFormalTypeParameter(s);
    }
    
    @Override
    public void visitTypeVariable(final String s) {
        this.v.visitTypeVariable(s);
    }
    
    @Override
    public SignatureVisitor visitArrayType() {
        this.v.visitArrayType();
        return this;
    }
    
    @Override
    public void visitBaseType(final char c) {
        this.v.visitBaseType(c);
    }
    
    @Override
    public SignatureVisitor visitClassBound() {
        this.v.visitClassBound();
        return this;
    }
    
    @Override
    public SignatureVisitor visitExceptionType() {
        this.v.visitExceptionType();
        return this;
    }
    
    @Override
    public SignatureVisitor visitInterface() {
        this.v.visitInterface();
        return this;
    }
    
    @Override
    public SignatureVisitor visitInterfaceBound() {
        this.v.visitInterfaceBound();
        return this;
    }
    
    @Override
    public SignatureVisitor visitParameterType() {
        this.v.visitParameterType();
        return this;
    }
    
    @Override
    public SignatureVisitor visitReturnType() {
        this.v.visitReturnType();
        return this;
    }
    
    @Override
    public SignatureVisitor visitSuperclass() {
        this.v.visitSuperclass();
        return this;
    }
    
    @Override
    public void visitTypeArgument() {
        this.v.visitTypeArgument();
    }
    
    @Override
    public SignatureVisitor visitTypeArgument(final char c) {
        this.v.visitTypeArgument(c);
        return this;
    }
    
    @Override
    public void visitEnd() {
        this.v.visitEnd();
    }
}
