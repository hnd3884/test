package sun.reflect.generics.scope;

import java.lang.reflect.TypeVariable;
import java.lang.reflect.GenericDeclaration;

public abstract class AbstractScope<D extends GenericDeclaration> implements Scope
{
    private final D recvr;
    private volatile Scope enclosingScope;
    
    protected AbstractScope(final D recvr) {
        this.recvr = recvr;
    }
    
    protected D getRecvr() {
        return this.recvr;
    }
    
    protected abstract Scope computeEnclosingScope();
    
    protected Scope getEnclosingScope() {
        Scope enclosingScope = this.enclosingScope;
        if (enclosingScope == null) {
            enclosingScope = this.computeEnclosingScope();
            this.enclosingScope = enclosingScope;
        }
        return enclosingScope;
    }
    
    @Override
    public TypeVariable<?> lookup(final String s) {
        for (final TypeVariable<?> typeVariable : this.getRecvr().getTypeParameters()) {
            if (typeVariable.getName().equals(s)) {
                return typeVariable;
            }
        }
        return this.getEnclosingScope().lookup(s);
    }
}
