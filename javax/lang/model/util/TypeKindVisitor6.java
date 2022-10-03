package javax.lang.model.util;

import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.SourceVersion;
import javax.annotation.processing.SupportedSourceVersion;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class TypeKindVisitor6<R, P> extends SimpleTypeVisitor6<R, P>
{
    protected TypeKindVisitor6() {
        super(null);
    }
    
    protected TypeKindVisitor6(final R r) {
        super(r);
    }
    
    @Override
    public R visitPrimitive(final PrimitiveType primitiveType, final P p2) {
        final TypeKind kind = primitiveType.getKind();
        switch (kind) {
            case BOOLEAN: {
                return this.visitPrimitiveAsBoolean(primitiveType, p2);
            }
            case BYTE: {
                return this.visitPrimitiveAsByte(primitiveType, p2);
            }
            case SHORT: {
                return this.visitPrimitiveAsShort(primitiveType, p2);
            }
            case INT: {
                return this.visitPrimitiveAsInt(primitiveType, p2);
            }
            case LONG: {
                return this.visitPrimitiveAsLong(primitiveType, p2);
            }
            case CHAR: {
                return this.visitPrimitiveAsChar(primitiveType, p2);
            }
            case FLOAT: {
                return this.visitPrimitiveAsFloat(primitiveType, p2);
            }
            case DOUBLE: {
                return this.visitPrimitiveAsDouble(primitiveType, p2);
            }
            default: {
                throw new AssertionError((Object)("Bad kind " + kind + " for PrimitiveType" + primitiveType));
            }
        }
    }
    
    public R visitPrimitiveAsBoolean(final PrimitiveType primitiveType, final P p2) {
        return this.defaultAction(primitiveType, p2);
    }
    
    public R visitPrimitiveAsByte(final PrimitiveType primitiveType, final P p2) {
        return this.defaultAction(primitiveType, p2);
    }
    
    public R visitPrimitiveAsShort(final PrimitiveType primitiveType, final P p2) {
        return this.defaultAction(primitiveType, p2);
    }
    
    public R visitPrimitiveAsInt(final PrimitiveType primitiveType, final P p2) {
        return this.defaultAction(primitiveType, p2);
    }
    
    public R visitPrimitiveAsLong(final PrimitiveType primitiveType, final P p2) {
        return this.defaultAction(primitiveType, p2);
    }
    
    public R visitPrimitiveAsChar(final PrimitiveType primitiveType, final P p2) {
        return this.defaultAction(primitiveType, p2);
    }
    
    public R visitPrimitiveAsFloat(final PrimitiveType primitiveType, final P p2) {
        return this.defaultAction(primitiveType, p2);
    }
    
    public R visitPrimitiveAsDouble(final PrimitiveType primitiveType, final P p2) {
        return this.defaultAction(primitiveType, p2);
    }
    
    @Override
    public R visitNoType(final NoType noType, final P p2) {
        final TypeKind kind = noType.getKind();
        switch (kind) {
            case VOID: {
                return this.visitNoTypeAsVoid(noType, p2);
            }
            case PACKAGE: {
                return this.visitNoTypeAsPackage(noType, p2);
            }
            case NONE: {
                return this.visitNoTypeAsNone(noType, p2);
            }
            default: {
                throw new AssertionError((Object)("Bad kind " + kind + " for NoType" + noType));
            }
        }
    }
    
    public R visitNoTypeAsVoid(final NoType noType, final P p2) {
        return this.defaultAction(noType, p2);
    }
    
    public R visitNoTypeAsPackage(final NoType noType, final P p2) {
        return this.defaultAction(noType, p2);
    }
    
    public R visitNoTypeAsNone(final NoType noType, final P p2) {
        return this.defaultAction(noType, p2);
    }
}
