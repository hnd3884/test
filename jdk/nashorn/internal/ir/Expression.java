package jdk.nashorn.internal.ir;

import jdk.nashorn.internal.runtime.UnwarrantedOptimismException;
import jdk.nashorn.internal.codegen.types.Type;

public abstract class Expression extends Node
{
    private static final long serialVersionUID = 1L;
    static final String OPT_IDENTIFIER = "%";
    
    protected Expression(final long token, final int start, final int finish) {
        super(token, start, finish);
    }
    
    Expression(final long token, final int finish) {
        super(token, finish);
    }
    
    Expression(final Expression expr) {
        super(expr);
    }
    
    public abstract Type getType();
    
    public boolean isLocal() {
        return false;
    }
    
    public boolean isSelfModifying() {
        return false;
    }
    
    public Type getWidestOperationType() {
        return Type.OBJECT;
    }
    
    public final boolean isOptimistic() {
        return this.getType().narrowerThan(this.getWidestOperationType());
    }
    
    void optimisticTypeToString(final StringBuilder sb) {
        this.optimisticTypeToString(sb, this.isOptimistic());
    }
    
    void optimisticTypeToString(final StringBuilder sb, final boolean optimistic) {
        sb.append('{');
        final Type type = this.getType();
        final String desc = (type == Type.UNDEFINED) ? "U" : type.getDescriptor();
        sb.append((desc.charAt(desc.length() - 1) == ';') ? "O" : desc);
        if (this.isOptimistic() && optimistic) {
            sb.append("%");
            final int pp = ((Optimistic)this).getProgramPoint();
            if (UnwarrantedOptimismException.isValid(pp)) {
                sb.append('_').append(pp);
            }
        }
        sb.append('}');
    }
    
    public boolean isAlwaysFalse() {
        return false;
    }
    
    public boolean isAlwaysTrue() {
        return false;
    }
    
    public static boolean isAlwaysFalse(final Expression test) {
        return test != null && test.isAlwaysFalse();
    }
    
    public static boolean isAlwaysTrue(final Expression test) {
        return test == null || test.isAlwaysTrue();
    }
}
