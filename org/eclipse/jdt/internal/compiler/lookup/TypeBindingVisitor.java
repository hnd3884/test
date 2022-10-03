package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.util.SimpleLookupTable;

public class TypeBindingVisitor
{
    private SimpleLookupTable visitedCache;
    
    public void reset() {
        this.visitedCache = null;
    }
    
    public boolean visit(final BaseTypeBinding baseTypeBinding) {
        return true;
    }
    
    public boolean visit(final ArrayBinding arrayBinding) {
        return true;
    }
    
    public boolean visit(final TypeVariableBinding typeVariable) {
        return true;
    }
    
    public boolean visit(final ReferenceBinding referenceBinding) {
        return true;
    }
    
    public boolean visit(final WildcardBinding wildcardBinding) {
        return true;
    }
    
    public boolean visit(final ParameterizedTypeBinding parameterizedTypeBinding) {
        return true;
    }
    
    public boolean visit(final IntersectionTypeBinding18 intersectionTypeBinding18) {
        return true;
    }
    
    public boolean visit(final RawTypeBinding rawTypeBinding) {
        return true;
    }
    
    public boolean visit(final PolyTypeBinding polyTypeBinding) {
        return true;
    }
    
    public static void visit(final TypeBindingVisitor visitor, final ReferenceBinding[] types) {
        for (int i = 0, length = (types == null) ? 0 : types.length; i < length; ++i) {
            visit(visitor, types[i]);
        }
    }
    
    public static void visit(final TypeBindingVisitor visitor, final TypeBinding type) {
        if (type == null) {
            return;
        }
        SimpleLookupTable visitedCache = visitor.visitedCache;
        if (visitedCache == null) {
            visitor.visitedCache = new SimpleLookupTable(3);
            visitedCache = visitor.visitedCache;
        }
        final Object result = visitedCache.get(type);
        if (result == Boolean.TRUE) {
            return;
        }
        visitedCache.put(type, Boolean.TRUE);
        switch (type.kind()) {
            case 4100: {
                final TypeVariableBinding typeVariableBinding = (TypeVariableBinding)type;
                if (visitor.visit(typeVariableBinding)) {
                    visit(visitor, typeVariableBinding.firstBound);
                    visit(visitor, typeVariableBinding.superclass);
                    visit(visitor, typeVariableBinding.superInterfaces);
                    break;
                }
                break;
            }
            case 260: {
                final ParameterizedTypeBinding parameterizedTypeBinding = (ParameterizedTypeBinding)type;
                if (visitor.visit(parameterizedTypeBinding)) {
                    visit(visitor, parameterizedTypeBinding.enclosingType());
                    visit(visitor, parameterizedTypeBinding.arguments);
                    break;
                }
                break;
            }
            case 68: {
                final ArrayBinding arrayBinding = (ArrayBinding)type;
                if (visitor.visit(arrayBinding)) {
                    visit(visitor, arrayBinding.leafComponentType);
                    break;
                }
                break;
            }
            case 516:
            case 8196: {
                final WildcardBinding wildcard = (WildcardBinding)type;
                if (visitor.visit(wildcard) && wildcard.boundKind != 0) {
                    visit(visitor, wildcard.bound);
                    visit(visitor, wildcard.otherBounds);
                    break;
                }
                break;
            }
            case 132: {
                visitor.visit((BaseTypeBinding)type);
                break;
            }
            case 1028: {
                visitor.visit((RawTypeBinding)type);
                break;
            }
            case 4:
            case 2052: {
                final ReferenceBinding referenceBinding = (ReferenceBinding)type;
                if (visitor.visit(referenceBinding)) {
                    visit(visitor, referenceBinding.enclosingType());
                    visit(visitor, referenceBinding.typeVariables());
                    break;
                }
                break;
            }
            case 32772: {
                final IntersectionTypeBinding18 intersectionTypeBinding18 = (IntersectionTypeBinding18)type;
                if (visitor.visit(intersectionTypeBinding18)) {
                    visit(visitor, intersectionTypeBinding18.intersectingTypes);
                    break;
                }
                break;
            }
            case 65540: {
                visitor.visit((PolyTypeBinding)type);
                break;
            }
            default: {
                throw new InternalError("Unexpected binding type");
            }
        }
    }
    
    public static void visit(final TypeBindingVisitor visitor, final TypeBinding[] types) {
        for (int i = 0, length = (types == null) ? 0 : types.length; i < length; ++i) {
            visit(visitor, types[i]);
        }
    }
}
