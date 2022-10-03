package java.lang.invoke;

import java.util.Arrays;
import java.io.Serializable;

public class LambdaMetafactory
{
    public static final int FLAG_SERIALIZABLE = 1;
    public static final int FLAG_MARKERS = 2;
    public static final int FLAG_BRIDGES = 4;
    private static final Class<?>[] EMPTY_CLASS_ARRAY;
    private static final MethodType[] EMPTY_MT_ARRAY;
    
    public static CallSite metafactory(final MethodHandles.Lookup lookup, final String s, final MethodType methodType, final MethodType methodType2, final MethodHandle methodHandle, final MethodType methodType3) throws LambdaConversionException {
        final InnerClassLambdaMetafactory innerClassLambdaMetafactory = new InnerClassLambdaMetafactory(lookup, methodType, s, methodType2, methodHandle, methodType3, false, LambdaMetafactory.EMPTY_CLASS_ARRAY, LambdaMetafactory.EMPTY_MT_ARRAY);
        innerClassLambdaMetafactory.validateMetafactoryArgs();
        return innerClassLambdaMetafactory.buildCallSite();
    }
    
    public static CallSite altMetafactory(final MethodHandles.Lookup lookup, final String s, final MethodType methodType, final Object... array) throws LambdaConversionException {
        final MethodType methodType2 = (MethodType)array[0];
        final MethodHandle methodHandle = (MethodHandle)array[1];
        final MethodType methodType3 = (MethodType)array[2];
        final int intValue = (int)array[3];
        int n = 4;
        Class<?>[] empty_CLASS_ARRAY;
        if ((intValue & 0x2) != 0x0) {
            final int intValue2 = (int)array[n++];
            empty_CLASS_ARRAY = new Class[intValue2];
            System.arraycopy(array, n, empty_CLASS_ARRAY, 0, intValue2);
            n += intValue2;
        }
        else {
            empty_CLASS_ARRAY = LambdaMetafactory.EMPTY_CLASS_ARRAY;
        }
        MethodType[] empty_MT_ARRAY;
        if ((intValue & 0x4) != 0x0) {
            final int intValue3 = (int)array[n++];
            empty_MT_ARRAY = new MethodType[intValue3];
            System.arraycopy(array, n, empty_MT_ARRAY, 0, intValue3);
        }
        else {
            empty_MT_ARRAY = LambdaMetafactory.EMPTY_MT_ARRAY;
        }
        final boolean b = (intValue & 0x1) != 0x0;
        if (b) {
            boolean assignable = Serializable.class.isAssignableFrom(methodType.returnType());
            final Class<?>[] array2 = empty_CLASS_ARRAY;
            for (int length = array2.length, i = 0; i < length; ++i) {
                assignable |= Serializable.class.isAssignableFrom(array2[i]);
            }
            if (!assignable) {
                empty_CLASS_ARRAY = Arrays.copyOf(empty_CLASS_ARRAY, empty_CLASS_ARRAY.length + 1);
                empty_CLASS_ARRAY[empty_CLASS_ARRAY.length - 1] = Serializable.class;
            }
        }
        final InnerClassLambdaMetafactory innerClassLambdaMetafactory = new InnerClassLambdaMetafactory(lookup, methodType, s, methodType2, methodHandle, methodType3, b, empty_CLASS_ARRAY, empty_MT_ARRAY);
        innerClassLambdaMetafactory.validateMetafactoryArgs();
        return innerClassLambdaMetafactory.buildCallSite();
    }
    
    static {
        EMPTY_CLASS_ARRAY = new Class[0];
        EMPTY_MT_ARRAY = new MethodType[0];
    }
}
