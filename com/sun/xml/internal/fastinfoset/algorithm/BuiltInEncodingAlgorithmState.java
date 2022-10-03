package com.sun.xml.internal.fastinfoset.algorithm;

public class BuiltInEncodingAlgorithmState
{
    public static final int INITIAL_LENGTH = 8;
    public boolean[] booleanArray;
    public short[] shortArray;
    public int[] intArray;
    public long[] longArray;
    public float[] floatArray;
    public double[] doubleArray;
    
    public BuiltInEncodingAlgorithmState() {
        this.booleanArray = new boolean[8];
        this.shortArray = new short[8];
        this.intArray = new int[8];
        this.longArray = new long[8];
        this.floatArray = new float[8];
        this.doubleArray = new double[8];
    }
}
