package java.lang.invoke;

import sun.invoke.util.Wrapper;

abstract class AbstractValidatingLambdaMetafactory
{
    final Class<?> targetClass;
    final MethodType invokedType;
    final Class<?> samBase;
    final String samMethodName;
    final MethodType samMethodType;
    final MethodHandle implMethod;
    final MethodHandleInfo implInfo;
    final int implKind;
    final boolean implIsInstanceMethod;
    final Class<?> implDefiningClass;
    final MethodType implMethodType;
    final MethodType instantiatedMethodType;
    final boolean isSerializable;
    final Class<?>[] markerInterfaces;
    final MethodType[] additionalBridges;
    
    AbstractValidatingLambdaMetafactory(final MethodHandles.Lookup lookup, final MethodType invokedType, final String samMethodName, final MethodType samMethodType, final MethodHandle implMethod, final MethodType instantiatedMethodType, final boolean isSerializable, final Class<?>[] markerInterfaces, final MethodType[] additionalBridges) throws LambdaConversionException {
        if ((lookup.lookupModes() & 0x2) == 0x0) {
            throw new LambdaConversionException(String.format("Invalid caller: %s", lookup.lookupClass().getName()));
        }
        this.targetClass = lookup.lookupClass();
        this.invokedType = invokedType;
        this.samBase = invokedType.returnType();
        this.samMethodName = samMethodName;
        this.samMethodType = samMethodType;
        this.implMethod = implMethod;
        this.implInfo = lookup.revealDirect(implMethod);
        this.implKind = this.implInfo.getReferenceKind();
        this.implIsInstanceMethod = (this.implKind == 5 || this.implKind == 7 || this.implKind == 9);
        this.implDefiningClass = this.implInfo.getDeclaringClass();
        this.implMethodType = this.implInfo.getMethodType();
        this.instantiatedMethodType = instantiatedMethodType;
        this.isSerializable = isSerializable;
        this.markerInterfaces = markerInterfaces;
        this.additionalBridges = additionalBridges;
        if (!this.samBase.isInterface()) {
            throw new LambdaConversionException(String.format("Functional interface %s is not an interface", this.samBase.getName()));
        }
        for (final Class<?> clazz : markerInterfaces) {
            if (!clazz.isInterface()) {
                throw new LambdaConversionException(String.format("Marker interface %s is not an interface", clazz.getName()));
            }
        }
    }
    
    abstract CallSite buildCallSite() throws LambdaConversionException;
    
    void validateMetafactoryArgs() throws LambdaConversionException {
        switch (this.implKind) {
            case 5:
            case 6:
            case 7:
            case 8:
            case 9: {
                final int parameterCount = this.implMethodType.parameterCount();
                final int implIsInstanceMethod = this.implIsInstanceMethod ? 1 : 0;
                final int parameterCount2 = this.invokedType.parameterCount();
                final int parameterCount3 = this.samMethodType.parameterCount();
                final int parameterCount4 = this.instantiatedMethodType.parameterCount();
                if (parameterCount + implIsInstanceMethod != parameterCount2 + parameterCount3) {
                    throw new LambdaConversionException(String.format("Incorrect number of parameters for %s method %s; %d captured parameters, %d functional interface method parameters, %d implementation parameters", this.implIsInstanceMethod ? "instance" : "static", this.implInfo, parameterCount2, parameterCount3, parameterCount));
                }
                if (parameterCount4 != parameterCount3) {
                    throw new LambdaConversionException(String.format("Incorrect number of parameters for %s method %s; %d instantiated parameters, %d functional interface method parameters", this.implIsInstanceMethod ? "instance" : "static", this.implInfo, parameterCount4, parameterCount3));
                }
                for (final MethodType methodType : this.additionalBridges) {
                    if (methodType.parameterCount() != parameterCount3) {
                        throw new LambdaConversionException(String.format("Incorrect number of parameters for bridge signature %s; incompatible with %s", methodType, this.samMethodType));
                    }
                }
                int n;
                int n2;
                if (this.implIsInstanceMethod) {
                    Class<?> clazz;
                    if (parameterCount2 == 0) {
                        n = 0;
                        n2 = 1;
                        clazz = this.instantiatedMethodType.parameterType(0);
                    }
                    else {
                        n = 1;
                        n2 = 0;
                        clazz = this.invokedType.parameterType(0);
                    }
                    if (!this.implDefiningClass.isAssignableFrom(clazz)) {
                        throw new LambdaConversionException(String.format("Invalid receiver type %s; not a subtype of implementation type %s", clazz, this.implDefiningClass));
                    }
                    final Class<?> parameterType = this.implMethod.type().parameterType(0);
                    if (parameterType != this.implDefiningClass && !parameterType.isAssignableFrom(clazz)) {
                        throw new LambdaConversionException(String.format("Invalid receiver type %s; not a subtype of implementation receiver type %s", clazz, parameterType));
                    }
                }
                else {
                    n = 0;
                    n2 = 0;
                }
                final int n3 = parameterCount2 - n;
                for (int j = 0; j < n3; ++j) {
                    final Class<?> parameterType2 = this.implMethodType.parameterType(j);
                    final Class<?> parameterType3 = this.invokedType.parameterType(j + n);
                    if (!parameterType3.equals(parameterType2)) {
                        throw new LambdaConversionException(String.format("Type mismatch in captured lambda parameter %d: expecting %s, found %s", j, parameterType3, parameterType2));
                    }
                }
                final int n4 = n2 - n3;
                for (int k = n3; k < parameterCount; ++k) {
                    final Class<?> parameterType4 = this.implMethodType.parameterType(k);
                    final Class<?> parameterType5 = this.instantiatedMethodType.parameterType(k + n4);
                    if (!this.isAdaptableTo(parameterType5, parameterType4, true)) {
                        throw new LambdaConversionException(String.format("Type mismatch for lambda argument %d: %s is not convertible to %s", k, parameterType5, parameterType4));
                    }
                }
                final Class<?> returnType = this.instantiatedMethodType.returnType();
                final Class<?> clazz2 = (this.implKind == 8) ? this.implDefiningClass : this.implMethodType.returnType();
                final Class<?> returnType2 = this.samMethodType.returnType();
                if (!this.isAdaptableToAsReturn(clazz2, returnType)) {
                    throw new LambdaConversionException(String.format("Type mismatch for lambda return: %s is not convertible to %s", clazz2, returnType));
                }
                if (!this.isAdaptableToAsReturnStrict(returnType, returnType2)) {
                    throw new LambdaConversionException(String.format("Type mismatch for lambda expected return: %s is not convertible to %s", returnType, returnType2));
                }
                for (final MethodType methodType2 : this.additionalBridges) {
                    if (!this.isAdaptableToAsReturnStrict(returnType, methodType2.returnType())) {
                        throw new LambdaConversionException(String.format("Type mismatch for lambda expected return: %s is not convertible to %s", returnType, methodType2.returnType()));
                    }
                }
                return;
            }
            default: {
                throw new LambdaConversionException(String.format("Unsupported MethodHandle kind: %s", this.implInfo));
            }
        }
    }
    
    private boolean isAdaptableTo(final Class<?> clazz, final Class<?> clazz2, final boolean b) {
        if (clazz.equals(clazz2)) {
            return true;
        }
        if (clazz.isPrimitive()) {
            final Wrapper forPrimitiveType = Wrapper.forPrimitiveType(clazz);
            if (clazz2.isPrimitive()) {
                return Wrapper.forPrimitiveType(clazz2).isConvertibleFrom(forPrimitiveType);
            }
            return clazz2.isAssignableFrom(forPrimitiveType.wrapperType());
        }
        else {
            if (!clazz2.isPrimitive()) {
                return !b || clazz2.isAssignableFrom(clazz);
            }
            final Wrapper forWrapperType;
            if (Wrapper.isWrapperType(clazz) && (forWrapperType = Wrapper.forWrapperType(clazz)).primitiveType().isPrimitive()) {
                return Wrapper.forPrimitiveType(clazz2).isConvertibleFrom(forWrapperType);
            }
            return !b;
        }
    }
    
    private boolean isAdaptableToAsReturn(final Class<?> clazz, final Class<?> clazz2) {
        return clazz2.equals(Void.TYPE) || (!clazz.equals(Void.TYPE) && this.isAdaptableTo(clazz, clazz2, false));
    }
    
    private boolean isAdaptableToAsReturnStrict(final Class<?> clazz, final Class<?> clazz2) {
        if (clazz.equals(Void.TYPE)) {
            return clazz2.equals(Void.TYPE);
        }
        return this.isAdaptableTo(clazz, clazz2, true);
    }
}
