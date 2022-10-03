package org.apache.commons.math3.geometry.euclidean.threed;

public final class RotationOrder
{
    public static final RotationOrder XYZ;
    public static final RotationOrder XZY;
    public static final RotationOrder YXZ;
    public static final RotationOrder YZX;
    public static final RotationOrder ZXY;
    public static final RotationOrder ZYX;
    public static final RotationOrder XYX;
    public static final RotationOrder XZX;
    public static final RotationOrder YXY;
    public static final RotationOrder YZY;
    public static final RotationOrder ZXZ;
    public static final RotationOrder ZYZ;
    private final String name;
    private final Vector3D a1;
    private final Vector3D a2;
    private final Vector3D a3;
    
    private RotationOrder(final String name, final Vector3D a1, final Vector3D a2, final Vector3D a3) {
        this.name = name;
        this.a1 = a1;
        this.a2 = a2;
        this.a3 = a3;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    public Vector3D getA1() {
        return this.a1;
    }
    
    public Vector3D getA2() {
        return this.a2;
    }
    
    public Vector3D getA3() {
        return this.a3;
    }
    
    static {
        XYZ = new RotationOrder("XYZ", Vector3D.PLUS_I, Vector3D.PLUS_J, Vector3D.PLUS_K);
        XZY = new RotationOrder("XZY", Vector3D.PLUS_I, Vector3D.PLUS_K, Vector3D.PLUS_J);
        YXZ = new RotationOrder("YXZ", Vector3D.PLUS_J, Vector3D.PLUS_I, Vector3D.PLUS_K);
        YZX = new RotationOrder("YZX", Vector3D.PLUS_J, Vector3D.PLUS_K, Vector3D.PLUS_I);
        ZXY = new RotationOrder("ZXY", Vector3D.PLUS_K, Vector3D.PLUS_I, Vector3D.PLUS_J);
        ZYX = new RotationOrder("ZYX", Vector3D.PLUS_K, Vector3D.PLUS_J, Vector3D.PLUS_I);
        XYX = new RotationOrder("XYX", Vector3D.PLUS_I, Vector3D.PLUS_J, Vector3D.PLUS_I);
        XZX = new RotationOrder("XZX", Vector3D.PLUS_I, Vector3D.PLUS_K, Vector3D.PLUS_I);
        YXY = new RotationOrder("YXY", Vector3D.PLUS_J, Vector3D.PLUS_I, Vector3D.PLUS_J);
        YZY = new RotationOrder("YZY", Vector3D.PLUS_J, Vector3D.PLUS_K, Vector3D.PLUS_J);
        ZXZ = new RotationOrder("ZXZ", Vector3D.PLUS_K, Vector3D.PLUS_I, Vector3D.PLUS_K);
        ZYZ = new RotationOrder("ZYZ", Vector3D.PLUS_K, Vector3D.PLUS_J, Vector3D.PLUS_K);
    }
}
