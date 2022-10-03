package com.sun.demo.jvmti.hprof;

public class Tracker
{
    private static int engaged;
    
    private static native void nativeObjectInit(final Object p0, final Object p1);
    
    public static void ObjectInit(final Object o) {
        if (Tracker.engaged != 0) {
            if (o == null) {
                throw new IllegalArgumentException("Null object.");
            }
            nativeObjectInit(Thread.currentThread(), o);
        }
    }
    
    private static native void nativeNewArray(final Object p0, final Object p1);
    
    public static void NewArray(final Object o) {
        if (Tracker.engaged != 0) {
            if (o == null) {
                throw new IllegalArgumentException("Null object.");
            }
            nativeNewArray(Thread.currentThread(), o);
        }
    }
    
    private static native void nativeCallSite(final Object p0, final int p1, final int p2);
    
    public static void CallSite(final int n, final int n2) {
        if (Tracker.engaged != 0) {
            if (n < 0) {
                throw new IllegalArgumentException("Negative class index");
            }
            if (n2 < 0) {
                throw new IllegalArgumentException("Negative method index");
            }
            nativeCallSite(Thread.currentThread(), n, n2);
        }
    }
    
    private static native void nativeReturnSite(final Object p0, final int p1, final int p2);
    
    public static void ReturnSite(final int n, final int n2) {
        if (Tracker.engaged != 0) {
            if (n < 0) {
                throw new IllegalArgumentException("Negative class index");
            }
            if (n2 < 0) {
                throw new IllegalArgumentException("Negative method index");
            }
            nativeReturnSite(Thread.currentThread(), n, n2);
        }
    }
    
    static {
        Tracker.engaged = 0;
    }
}
