package com.sun.org.apache.bcel.internal;

public interface ExceptionConstants
{
    public static final Class THROWABLE = Throwable.class;
    public static final Class RUNTIME_EXCEPTION = RuntimeException.class;
    public static final Class LINKING_EXCEPTION = LinkageError.class;
    public static final Class CLASS_CIRCULARITY_ERROR = ClassCircularityError.class;
    public static final Class CLASS_FORMAT_ERROR = ClassFormatError.class;
    public static final Class EXCEPTION_IN_INITIALIZER_ERROR = ExceptionInInitializerError.class;
    public static final Class INCOMPATIBLE_CLASS_CHANGE_ERROR = IncompatibleClassChangeError.class;
    public static final Class ABSTRACT_METHOD_ERROR = AbstractMethodError.class;
    public static final Class ILLEGAL_ACCESS_ERROR = IllegalAccessError.class;
    public static final Class INSTANTIATION_ERROR = InstantiationError.class;
    public static final Class NO_SUCH_FIELD_ERROR = NoSuchFieldError.class;
    public static final Class NO_SUCH_METHOD_ERROR = NoSuchMethodError.class;
    public static final Class NO_CLASS_DEF_FOUND_ERROR = NoClassDefFoundError.class;
    public static final Class UNSATISFIED_LINK_ERROR = UnsatisfiedLinkError.class;
    public static final Class VERIFY_ERROR = VerifyError.class;
    public static final Class NULL_POINTER_EXCEPTION = NullPointerException.class;
    public static final Class ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION = ArrayIndexOutOfBoundsException.class;
    public static final Class ARITHMETIC_EXCEPTION = ArithmeticException.class;
    public static final Class NEGATIVE_ARRAY_SIZE_EXCEPTION = NegativeArraySizeException.class;
    public static final Class CLASS_CAST_EXCEPTION = ClassCastException.class;
    public static final Class ILLEGAL_MONITOR_STATE = IllegalMonitorStateException.class;
    public static final Class[] EXCS_CLASS_AND_INTERFACE_RESOLUTION = { ExceptionConstants.NO_CLASS_DEF_FOUND_ERROR, ExceptionConstants.CLASS_FORMAT_ERROR, ExceptionConstants.VERIFY_ERROR, ExceptionConstants.ABSTRACT_METHOD_ERROR, ExceptionConstants.EXCEPTION_IN_INITIALIZER_ERROR, ExceptionConstants.ILLEGAL_ACCESS_ERROR };
    public static final Class[] EXCS_FIELD_AND_METHOD_RESOLUTION = { ExceptionConstants.NO_SUCH_FIELD_ERROR, ExceptionConstants.ILLEGAL_ACCESS_ERROR, ExceptionConstants.NO_SUCH_METHOD_ERROR };
    public static final Class[] EXCS_INTERFACE_METHOD_RESOLUTION = new Class[0];
    public static final Class[] EXCS_STRING_RESOLUTION = new Class[0];
    public static final Class[] EXCS_ARRAY_EXCEPTION = { ExceptionConstants.NULL_POINTER_EXCEPTION, ExceptionConstants.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION };
}
