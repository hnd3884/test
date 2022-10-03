package java.awt.geom;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.awt.Shape;
import java.beans.ConstructorProperties;
import java.io.Serializable;

public class AffineTransform implements Cloneable, Serializable
{
    private static final int TYPE_UNKNOWN = -1;
    public static final int TYPE_IDENTITY = 0;
    public static final int TYPE_TRANSLATION = 1;
    public static final int TYPE_UNIFORM_SCALE = 2;
    public static final int TYPE_GENERAL_SCALE = 4;
    public static final int TYPE_MASK_SCALE = 6;
    public static final int TYPE_FLIP = 64;
    public static final int TYPE_QUADRANT_ROTATION = 8;
    public static final int TYPE_GENERAL_ROTATION = 16;
    public static final int TYPE_MASK_ROTATION = 24;
    public static final int TYPE_GENERAL_TRANSFORM = 32;
    static final int APPLY_IDENTITY = 0;
    static final int APPLY_TRANSLATE = 1;
    static final int APPLY_SCALE = 2;
    static final int APPLY_SHEAR = 4;
    private static final int HI_SHIFT = 3;
    private static final int HI_IDENTITY = 0;
    private static final int HI_TRANSLATE = 8;
    private static final int HI_SCALE = 16;
    private static final int HI_SHEAR = 32;
    double m00;
    double m10;
    double m01;
    double m11;
    double m02;
    double m12;
    transient int state;
    private transient int type;
    private static final int[] rot90conversion;
    private static final long serialVersionUID = 1330973210523860834L;
    
    private AffineTransform(final double m00, final double m2, final double m3, final double m4, final double m5, final double m6, final int state) {
        this.m00 = m00;
        this.m10 = m2;
        this.m01 = m3;
        this.m11 = m4;
        this.m02 = m5;
        this.m12 = m6;
        this.state = state;
        this.type = -1;
    }
    
    public AffineTransform() {
        final double n = 1.0;
        this.m11 = n;
        this.m00 = n;
    }
    
    public AffineTransform(final AffineTransform affineTransform) {
        this.m00 = affineTransform.m00;
        this.m10 = affineTransform.m10;
        this.m01 = affineTransform.m01;
        this.m11 = affineTransform.m11;
        this.m02 = affineTransform.m02;
        this.m12 = affineTransform.m12;
        this.state = affineTransform.state;
        this.type = affineTransform.type;
    }
    
    @ConstructorProperties({ "scaleX", "shearY", "shearX", "scaleY", "translateX", "translateY" })
    public AffineTransform(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        this.m00 = n;
        this.m10 = n2;
        this.m01 = n3;
        this.m11 = n4;
        this.m02 = n5;
        this.m12 = n6;
        this.updateState();
    }
    
    public AffineTransform(final float[] array) {
        this.m00 = array[0];
        this.m10 = array[1];
        this.m01 = array[2];
        this.m11 = array[3];
        if (array.length > 5) {
            this.m02 = array[4];
            this.m12 = array[5];
        }
        this.updateState();
    }
    
    public AffineTransform(final double m00, final double m2, final double m3, final double m4, final double m5, final double m6) {
        this.m00 = m00;
        this.m10 = m2;
        this.m01 = m3;
        this.m11 = m4;
        this.m02 = m5;
        this.m12 = m6;
        this.updateState();
    }
    
    public AffineTransform(final double[] array) {
        this.m00 = array[0];
        this.m10 = array[1];
        this.m01 = array[2];
        this.m11 = array[3];
        if (array.length > 5) {
            this.m02 = array[4];
            this.m12 = array[5];
        }
        this.updateState();
    }
    
    public static AffineTransform getTranslateInstance(final double n, final double n2) {
        final AffineTransform affineTransform = new AffineTransform();
        affineTransform.setToTranslation(n, n2);
        return affineTransform;
    }
    
    public static AffineTransform getRotateInstance(final double toRotation) {
        final AffineTransform affineTransform = new AffineTransform();
        affineTransform.setToRotation(toRotation);
        return affineTransform;
    }
    
    public static AffineTransform getRotateInstance(final double n, final double n2, final double n3) {
        final AffineTransform affineTransform = new AffineTransform();
        affineTransform.setToRotation(n, n2, n3);
        return affineTransform;
    }
    
    public static AffineTransform getRotateInstance(final double n, final double n2) {
        final AffineTransform affineTransform = new AffineTransform();
        affineTransform.setToRotation(n, n2);
        return affineTransform;
    }
    
    public static AffineTransform getRotateInstance(final double n, final double n2, final double n3, final double n4) {
        final AffineTransform affineTransform = new AffineTransform();
        affineTransform.setToRotation(n, n2, n3, n4);
        return affineTransform;
    }
    
    public static AffineTransform getQuadrantRotateInstance(final int toQuadrantRotation) {
        final AffineTransform affineTransform = new AffineTransform();
        affineTransform.setToQuadrantRotation(toQuadrantRotation);
        return affineTransform;
    }
    
    public static AffineTransform getQuadrantRotateInstance(final int n, final double n2, final double n3) {
        final AffineTransform affineTransform = new AffineTransform();
        affineTransform.setToQuadrantRotation(n, n2, n3);
        return affineTransform;
    }
    
    public static AffineTransform getScaleInstance(final double n, final double n2) {
        final AffineTransform affineTransform = new AffineTransform();
        affineTransform.setToScale(n, n2);
        return affineTransform;
    }
    
    public static AffineTransform getShearInstance(final double n, final double n2) {
        final AffineTransform affineTransform = new AffineTransform();
        affineTransform.setToShear(n, n2);
        return affineTransform;
    }
    
    public int getType() {
        if (this.type == -1) {
            this.calculateType();
        }
        return this.type;
    }
    
    private void calculateType() {
        int type = 0;
        this.updateState();
        Label_0530: {
            switch (this.state) {
                default: {
                    this.stateError();
                }
                case 7: {
                    type = 1;
                }
                case 6: {
                    final double m00;
                    final double m2;
                    final double m3;
                    final double m4;
                    if ((m00 = this.m00) * (m2 = this.m01) + (m3 = this.m10) * (m4 = this.m11) != 0.0) {
                        this.type = 32;
                        return;
                    }
                    if (m00 >= 0.0 == m4 >= 0.0) {
                        if (m00 != m4 || m2 != -m3) {
                            type |= 0x14;
                            break Label_0530;
                        }
                        if (m00 * m4 - m2 * m3 != 1.0) {
                            type |= 0x12;
                            break Label_0530;
                        }
                        type |= 0x10;
                        break Label_0530;
                    }
                    else {
                        if (m00 != -m4 || m2 != m3) {
                            type |= 0x54;
                            break Label_0530;
                        }
                        if (m00 * m4 - m2 * m3 != 1.0) {
                            type |= 0x52;
                            break Label_0530;
                        }
                        type |= 0x50;
                        break Label_0530;
                    }
                    break;
                }
                case 5: {
                    type = 1;
                }
                case 4: {
                    final double m5;
                    final double m6;
                    if ((m5 = this.m01) >= 0.0 != (m6 = this.m10) >= 0.0) {
                        if (m5 != -m6) {
                            type |= 0xC;
                            break Label_0530;
                        }
                        if (m5 != 1.0 && m5 != -1.0) {
                            type |= 0xA;
                            break Label_0530;
                        }
                        type |= 0x8;
                        break Label_0530;
                    }
                    else {
                        if (m5 == m6) {
                            type |= 0x4A;
                            break Label_0530;
                        }
                        type |= 0x4C;
                        break Label_0530;
                    }
                    break;
                }
                case 3: {
                    type = 1;
                }
                case 2: {
                    final double m7;
                    final boolean b = (m7 = this.m00) >= 0.0;
                    final double m8;
                    if (b == (m8 = this.m11) >= 0.0) {
                        if (b) {
                            if (m7 == m8) {
                                type |= 0x2;
                                break Label_0530;
                            }
                            type |= 0x4;
                            break Label_0530;
                        }
                        else {
                            if (m7 != m8) {
                                type |= 0xC;
                                break Label_0530;
                            }
                            if (m7 != -1.0) {
                                type |= 0xA;
                                break Label_0530;
                            }
                            type |= 0x8;
                            break Label_0530;
                        }
                    }
                    else {
                        if (m7 != -m8) {
                            type |= 0x44;
                            break Label_0530;
                        }
                        if (m7 == 1.0 || m7 == -1.0) {
                            type |= 0x40;
                            break Label_0530;
                        }
                        type |= 0x42;
                        break Label_0530;
                    }
                    break;
                }
                case 1: {
                    type = 1;
                }
                case 0: {
                    this.type = type;
                }
            }
        }
    }
    
    public double getDeterminant() {
        switch (this.state) {
            default: {
                this.stateError();
                return this.m00 * this.m11 - this.m01 * this.m10;
            }
            case 6:
            case 7: {
                return this.m00 * this.m11 - this.m01 * this.m10;
            }
            case 4:
            case 5: {
                return -(this.m01 * this.m10);
            }
            case 2:
            case 3: {
                return this.m00 * this.m11;
            }
            case 0:
            case 1: {
                return 1.0;
            }
        }
    }
    
    void updateState() {
        if (this.m01 == 0.0 && this.m10 == 0.0) {
            if (this.m00 == 1.0 && this.m11 == 1.0) {
                if (this.m02 == 0.0 && this.m12 == 0.0) {
                    this.state = 0;
                    this.type = 0;
                }
                else {
                    this.state = 1;
                    this.type = 1;
                }
            }
            else if (this.m02 == 0.0 && this.m12 == 0.0) {
                this.state = 2;
                this.type = -1;
            }
            else {
                this.state = 3;
                this.type = -1;
            }
        }
        else if (this.m00 == 0.0 && this.m11 == 0.0) {
            if (this.m02 == 0.0 && this.m12 == 0.0) {
                this.state = 4;
                this.type = -1;
            }
            else {
                this.state = 5;
                this.type = -1;
            }
        }
        else if (this.m02 == 0.0 && this.m12 == 0.0) {
            this.state = 6;
            this.type = -1;
        }
        else {
            this.state = 7;
            this.type = -1;
        }
    }
    
    private void stateError() {
        throw new InternalError("missing case in transform state switch");
    }
    
    public void getMatrix(final double[] array) {
        array[0] = this.m00;
        array[1] = this.m10;
        array[2] = this.m01;
        array[3] = this.m11;
        if (array.length > 5) {
            array[4] = this.m02;
            array[5] = this.m12;
        }
    }
    
    public double getScaleX() {
        return this.m00;
    }
    
    public double getScaleY() {
        return this.m11;
    }
    
    public double getShearX() {
        return this.m01;
    }
    
    public double getShearY() {
        return this.m10;
    }
    
    public double getTranslateX() {
        return this.m02;
    }
    
    public double getTranslateY() {
        return this.m12;
    }
    
    public void translate(final double m02, final double m3) {
        switch (this.state) {
            default: {
                this.stateError();
                return;
            }
            case 7: {
                this.m02 += m02 * this.m00 + m3 * this.m01;
                this.m12 += m02 * this.m10 + m3 * this.m11;
                if (this.m02 == 0.0 && this.m12 == 0.0) {
                    this.state = 6;
                    if (this.type != -1) {
                        --this.type;
                    }
                }
                return;
            }
            case 6: {
                this.m02 = m02 * this.m00 + m3 * this.m01;
                this.m12 = m02 * this.m10 + m3 * this.m11;
                if (this.m02 != 0.0 || this.m12 != 0.0) {
                    this.state = 7;
                    this.type |= 0x1;
                }
                return;
            }
            case 5: {
                this.m02 += m3 * this.m01;
                this.m12 += m02 * this.m10;
                if (this.m02 == 0.0 && this.m12 == 0.0) {
                    this.state = 4;
                    if (this.type != -1) {
                        --this.type;
                    }
                }
                return;
            }
            case 4: {
                this.m02 = m3 * this.m01;
                this.m12 = m02 * this.m10;
                if (this.m02 != 0.0 || this.m12 != 0.0) {
                    this.state = 5;
                    this.type |= 0x1;
                }
                return;
            }
            case 3: {
                this.m02 += m02 * this.m00;
                this.m12 += m3 * this.m11;
                if (this.m02 == 0.0 && this.m12 == 0.0) {
                    this.state = 2;
                    if (this.type != -1) {
                        --this.type;
                    }
                }
                return;
            }
            case 2: {
                this.m02 = m02 * this.m00;
                this.m12 = m3 * this.m11;
                if (this.m02 != 0.0 || this.m12 != 0.0) {
                    this.state = 3;
                    this.type |= 0x1;
                }
                return;
            }
            case 1: {
                this.m02 += m02;
                this.m12 += m3;
                if (this.m02 == 0.0 && this.m12 == 0.0) {
                    this.state = 0;
                    this.type = 0;
                }
                return;
            }
            case 0: {
                this.m02 = m02;
                this.m12 = m3;
                if (m02 != 0.0 || m3 != 0.0) {
                    this.state = 1;
                    this.type = 1;
                }
            }
        }
    }
    
    private final void rotate90() {
        final double m00 = this.m00;
        this.m00 = this.m01;
        this.m01 = -m00;
        final double m2 = this.m10;
        this.m10 = this.m11;
        this.m11 = -m2;
        int state = AffineTransform.rot90conversion[this.state];
        if ((state & 0x6) == 0x2 && this.m00 == 1.0 && this.m11 == 1.0) {
            state -= 2;
        }
        this.state = state;
        this.type = -1;
    }
    
    private final void rotate180() {
        this.m00 = -this.m00;
        this.m11 = -this.m11;
        final int state = this.state;
        if ((state & 0x4) != 0x0) {
            this.m01 = -this.m01;
            this.m10 = -this.m10;
        }
        else if (this.m00 == 1.0 && this.m11 == 1.0) {
            this.state = (state & 0xFFFFFFFD);
        }
        else {
            this.state = (state | 0x2);
        }
        this.type = -1;
    }
    
    private final void rotate270() {
        final double m00 = this.m00;
        this.m00 = -this.m01;
        this.m01 = m00;
        final double m2 = this.m10;
        this.m10 = -this.m11;
        this.m11 = m2;
        int state = AffineTransform.rot90conversion[this.state];
        if ((state & 0x6) == 0x2 && this.m00 == 1.0 && this.m11 == 1.0) {
            state -= 2;
        }
        this.state = state;
        this.type = -1;
    }
    
    public void rotate(final double n) {
        final double sin = Math.sin(n);
        if (sin == 1.0) {
            this.rotate90();
        }
        else if (sin == -1.0) {
            this.rotate270();
        }
        else {
            final double cos = Math.cos(n);
            if (cos == -1.0) {
                this.rotate180();
            }
            else if (cos != 1.0) {
                final double m00 = this.m00;
                final double m2 = this.m01;
                this.m00 = cos * m00 + sin * m2;
                this.m01 = -sin * m00 + cos * m2;
                final double m3 = this.m10;
                final double m4 = this.m11;
                this.m10 = cos * m3 + sin * m4;
                this.m11 = -sin * m3 + cos * m4;
                this.updateState();
            }
        }
    }
    
    public void rotate(final double n, final double n2, final double n3) {
        this.translate(n2, n3);
        this.rotate(n);
        this.translate(-n2, -n3);
    }
    
    public void rotate(final double n, final double n2) {
        if (n2 == 0.0) {
            if (n < 0.0) {
                this.rotate180();
            }
        }
        else if (n == 0.0) {
            if (n2 > 0.0) {
                this.rotate90();
            }
            else {
                this.rotate270();
            }
        }
        else {
            final double sqrt = Math.sqrt(n * n + n2 * n2);
            final double n3 = n2 / sqrt;
            final double n4 = n / sqrt;
            final double m00 = this.m00;
            final double m2 = this.m01;
            this.m00 = n4 * m00 + n3 * m2;
            this.m01 = -n3 * m00 + n4 * m2;
            final double m3 = this.m10;
            final double m4 = this.m11;
            this.m10 = n4 * m3 + n3 * m4;
            this.m11 = -n3 * m3 + n4 * m4;
            this.updateState();
        }
    }
    
    public void rotate(final double n, final double n2, final double n3, final double n4) {
        this.translate(n3, n4);
        this.rotate(n, n2);
        this.translate(-n3, -n4);
    }
    
    public void quadrantRotate(final int n) {
        switch (n & 0x3) {
            case 1: {
                this.rotate90();
                break;
            }
            case 2: {
                this.rotate180();
                break;
            }
            case 3: {
                this.rotate270();
                break;
            }
        }
    }
    
    public void quadrantRotate(final int n, final double n2, final double n3) {
        switch (n & 0x3) {
            case 0: {
                return;
            }
            case 1: {
                this.m02 += n2 * (this.m00 - this.m01) + n3 * (this.m01 + this.m00);
                this.m12 += n2 * (this.m10 - this.m11) + n3 * (this.m11 + this.m10);
                this.rotate90();
                break;
            }
            case 2: {
                this.m02 += n2 * (this.m00 + this.m00) + n3 * (this.m01 + this.m01);
                this.m12 += n2 * (this.m10 + this.m10) + n3 * (this.m11 + this.m11);
                this.rotate180();
                break;
            }
            case 3: {
                this.m02 += n2 * (this.m00 + this.m01) + n3 * (this.m01 - this.m00);
                this.m12 += n2 * (this.m10 + this.m11) + n3 * (this.m11 - this.m10);
                this.rotate270();
                break;
            }
        }
        if (this.m02 == 0.0 && this.m12 == 0.0) {
            this.state &= 0xFFFFFFFE;
        }
        else {
            this.state |= 0x1;
        }
    }
    
    public void scale(final double m00, final double m2) {
        final int state = this.state;
        switch (state) {
            default: {
                this.stateError();
            }
            case 6:
            case 7: {
                this.m00 *= m00;
                this.m11 *= m2;
            }
            case 4:
            case 5: {
                this.m01 *= m2;
                this.m10 *= m00;
                if (this.m01 == 0.0 && this.m10 == 0.0) {
                    int state2 = state & 0x1;
                    if (this.m00 == 1.0 && this.m11 == 1.0) {
                        this.type = ((state2 != 0) ? 1 : 0);
                    }
                    else {
                        state2 |= 0x2;
                        this.type = -1;
                    }
                    this.state = state2;
                }
                return;
            }
            case 2:
            case 3: {
                this.m00 *= m00;
                this.m11 *= m2;
                if (this.m00 == 1.0 && this.m11 == 1.0) {
                    this.type = (((this.state = (state & 0x1)) != 0) ? 1 : 0);
                }
                else {
                    this.type = -1;
                }
                return;
            }
            case 0:
            case 1: {
                this.m00 = m00;
                this.m11 = m2;
                if (m00 != 1.0 || m2 != 1.0) {
                    this.state = (state | 0x2);
                    this.type = -1;
                }
            }
        }
    }
    
    public void shear(final double m01, final double m2) {
        final int state = this.state;
        switch (state) {
            default: {
                this.stateError();
                return;
            }
            case 6:
            case 7: {
                final double m3 = this.m00;
                final double m4 = this.m01;
                this.m00 = m3 + m4 * m2;
                this.m01 = m3 * m01 + m4;
                final double m5 = this.m10;
                final double m6 = this.m11;
                this.m10 = m5 + m6 * m2;
                this.m11 = m5 * m01 + m6;
                this.updateState();
                return;
            }
            case 4:
            case 5: {
                this.m00 = this.m01 * m2;
                this.m11 = this.m10 * m01;
                if (this.m00 != 0.0 || this.m11 != 0.0) {
                    this.state = (state | 0x2);
                }
                this.type = -1;
                return;
            }
            case 2:
            case 3: {
                this.m01 = this.m00 * m01;
                this.m10 = this.m11 * m2;
                if (this.m01 != 0.0 || this.m10 != 0.0) {
                    this.state = (state | 0x4);
                }
                this.type = -1;
                return;
            }
            case 0:
            case 1: {
                this.m01 = m01;
                this.m10 = m2;
                if (this.m01 != 0.0 || this.m10 != 0.0) {
                    this.state = (state | 0x2 | 0x4);
                    this.type = -1;
                }
            }
        }
    }
    
    public void setToIdentity() {
        final double n = 1.0;
        this.m11 = n;
        this.m00 = n;
        final double n2 = 0.0;
        this.m12 = n2;
        this.m02 = n2;
        this.m01 = n2;
        this.m10 = n2;
        this.state = 0;
        this.type = 0;
    }
    
    public void setToTranslation(final double m02, final double m3) {
        this.m00 = 1.0;
        this.m10 = 0.0;
        this.m01 = 0.0;
        this.m11 = 1.0;
        this.m02 = m02;
        this.m12 = m3;
        if (m02 != 0.0 || m3 != 0.0) {
            this.state = 1;
            this.type = 1;
        }
        else {
            this.state = 0;
            this.type = 0;
        }
    }
    
    public void setToRotation(final double n) {
        double sin = Math.sin(n);
        double cos;
        if (sin == 1.0 || sin == -1.0) {
            cos = 0.0;
            this.state = 4;
            this.type = 8;
        }
        else {
            cos = Math.cos(n);
            if (cos == -1.0) {
                sin = 0.0;
                this.state = 2;
                this.type = 8;
            }
            else if (cos == 1.0) {
                sin = 0.0;
                this.state = 0;
                this.type = 0;
            }
            else {
                this.state = 6;
                this.type = 16;
            }
        }
        this.m00 = cos;
        this.m10 = sin;
        this.m01 = -sin;
        this.m11 = cos;
        this.m02 = 0.0;
        this.m12 = 0.0;
    }
    
    public void setToRotation(final double toRotation, final double n, final double n2) {
        this.setToRotation(toRotation);
        final double m10 = this.m10;
        final double n3 = 1.0 - this.m00;
        this.m02 = n * n3 + n2 * m10;
        this.m12 = n2 * n3 - n * m10;
        if (this.m02 != 0.0 || this.m12 != 0.0) {
            this.state |= 0x1;
            this.type |= 0x1;
        }
    }
    
    public void setToRotation(final double n, final double n2) {
        double m10;
        double n3;
        if (n2 == 0.0) {
            m10 = 0.0;
            if (n < 0.0) {
                n3 = -1.0;
                this.state = 2;
                this.type = 8;
            }
            else {
                n3 = 1.0;
                this.state = 0;
                this.type = 0;
            }
        }
        else if (n == 0.0) {
            n3 = 0.0;
            m10 = ((n2 > 0.0) ? 1.0 : -1.0);
            this.state = 4;
            this.type = 8;
        }
        else {
            final double sqrt = Math.sqrt(n * n + n2 * n2);
            n3 = n / sqrt;
            m10 = n2 / sqrt;
            this.state = 6;
            this.type = 16;
        }
        this.m00 = n3;
        this.m10 = m10;
        this.m01 = -m10;
        this.m11 = n3;
        this.m02 = 0.0;
        this.m12 = 0.0;
    }
    
    public void setToRotation(final double n, final double n2, final double n3, final double n4) {
        this.setToRotation(n, n2);
        final double m10 = this.m10;
        final double n5 = 1.0 - this.m00;
        this.m02 = n3 * n5 + n4 * m10;
        this.m12 = n4 * n5 - n3 * m10;
        if (this.m02 != 0.0 || this.m12 != 0.0) {
            this.state |= 0x1;
            this.type |= 0x1;
        }
    }
    
    public void setToQuadrantRotation(final int n) {
        switch (n & 0x3) {
            case 0: {
                this.m00 = 1.0;
                this.m10 = 0.0;
                this.m01 = 0.0;
                this.m11 = 1.0;
                this.m02 = 0.0;
                this.m12 = 0.0;
                this.state = 0;
                this.type = 0;
                break;
            }
            case 1: {
                this.m00 = 0.0;
                this.m10 = 1.0;
                this.m01 = -1.0;
                this.m11 = 0.0;
                this.m02 = 0.0;
                this.m12 = 0.0;
                this.state = 4;
                this.type = 8;
                break;
            }
            case 2: {
                this.m00 = -1.0;
                this.m10 = 0.0;
                this.m01 = 0.0;
                this.m11 = -1.0;
                this.m02 = 0.0;
                this.m12 = 0.0;
                this.state = 2;
                this.type = 8;
                break;
            }
            case 3: {
                this.m00 = 0.0;
                this.m10 = -1.0;
                this.m01 = 1.0;
                this.m11 = 0.0;
                this.m02 = 0.0;
                this.m12 = 0.0;
                this.state = 4;
                this.type = 8;
                break;
            }
        }
    }
    
    public void setToQuadrantRotation(final int n, final double n2, final double n3) {
        switch (n & 0x3) {
            case 0: {
                this.m00 = 1.0;
                this.m10 = 0.0;
                this.m01 = 0.0;
                this.m11 = 1.0;
                this.m02 = 0.0;
                this.m12 = 0.0;
                this.state = 0;
                this.type = 0;
                break;
            }
            case 1: {
                this.m00 = 0.0;
                this.m10 = 1.0;
                this.m01 = -1.0;
                this.m11 = 0.0;
                this.m02 = n2 + n3;
                this.m12 = n3 - n2;
                if (this.m02 == 0.0 && this.m12 == 0.0) {
                    this.state = 4;
                    this.type = 8;
                    break;
                }
                this.state = 5;
                this.type = 9;
                break;
            }
            case 2: {
                this.m00 = -1.0;
                this.m10 = 0.0;
                this.m01 = 0.0;
                this.m11 = -1.0;
                this.m02 = n2 + n2;
                this.m12 = n3 + n3;
                if (this.m02 == 0.0 && this.m12 == 0.0) {
                    this.state = 2;
                    this.type = 8;
                    break;
                }
                this.state = 3;
                this.type = 9;
                break;
            }
            case 3: {
                this.m00 = 0.0;
                this.m10 = -1.0;
                this.m01 = 1.0;
                this.m11 = 0.0;
                this.m02 = n2 - n3;
                this.m12 = n3 + n2;
                if (this.m02 == 0.0 && this.m12 == 0.0) {
                    this.state = 4;
                    this.type = 8;
                    break;
                }
                this.state = 5;
                this.type = 9;
                break;
            }
        }
    }
    
    public void setToScale(final double m00, final double m2) {
        this.m00 = m00;
        this.m10 = 0.0;
        this.m01 = 0.0;
        this.m11 = m2;
        this.m02 = 0.0;
        this.m12 = 0.0;
        if (m00 != 1.0 || m2 != 1.0) {
            this.state = 2;
            this.type = -1;
        }
        else {
            this.state = 0;
            this.type = 0;
        }
    }
    
    public void setToShear(final double m01, final double m2) {
        this.m00 = 1.0;
        this.m01 = m01;
        this.m10 = m2;
        this.m11 = 1.0;
        this.m02 = 0.0;
        this.m12 = 0.0;
        if (m01 != 0.0 || m2 != 0.0) {
            this.state = 6;
            this.type = -1;
        }
        else {
            this.state = 0;
            this.type = 0;
        }
    }
    
    public void setTransform(final AffineTransform affineTransform) {
        this.m00 = affineTransform.m00;
        this.m10 = affineTransform.m10;
        this.m01 = affineTransform.m01;
        this.m11 = affineTransform.m11;
        this.m02 = affineTransform.m02;
        this.m12 = affineTransform.m12;
        this.state = affineTransform.state;
        this.type = affineTransform.type;
    }
    
    public void setTransform(final double m00, final double m2, final double m3, final double m4, final double m5, final double m6) {
        this.m00 = m00;
        this.m10 = m2;
        this.m01 = m3;
        this.m11 = m4;
        this.m02 = m5;
        this.m12 = m6;
        this.updateState();
    }
    
    public void concatenate(final AffineTransform affineTransform) {
        final int state = this.state;
        final int state2 = affineTransform.state;
        switch (state2 << 3 | state) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7: {
                return;
            }
            case 56: {
                this.m01 = affineTransform.m01;
                this.m10 = affineTransform.m10;
            }
            case 24: {
                this.m00 = affineTransform.m00;
                this.m11 = affineTransform.m11;
            }
            case 8: {
                this.m02 = affineTransform.m02;
                this.m12 = affineTransform.m12;
                this.state = state2;
                this.type = affineTransform.type;
                return;
            }
            case 48: {
                this.m01 = affineTransform.m01;
                this.m10 = affineTransform.m10;
            }
            case 16: {
                this.m00 = affineTransform.m00;
                this.m11 = affineTransform.m11;
                this.state = state2;
                this.type = affineTransform.type;
                return;
            }
            case 40: {
                this.m02 = affineTransform.m02;
                this.m12 = affineTransform.m12;
            }
            case 32: {
                this.m01 = affineTransform.m01;
                this.m10 = affineTransform.m10;
                final double n = 0.0;
                this.m11 = n;
                this.m00 = n;
                this.state = state2;
                this.type = affineTransform.type;
                return;
            }
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15: {
                this.translate(affineTransform.m02, affineTransform.m12);
                return;
            }
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23: {
                this.scale(affineTransform.m00, affineTransform.m11);
                return;
            }
            case 38:
            case 39: {
                final double m01 = affineTransform.m01;
                final double m2 = affineTransform.m10;
                final double m3 = this.m00;
                this.m00 = this.m01 * m2;
                this.m01 = m3 * m01;
                final double m4 = this.m10;
                this.m10 = this.m11 * m2;
                this.m11 = m4 * m01;
                this.type = -1;
                return;
            }
            case 36:
            case 37: {
                this.m00 = this.m01 * affineTransform.m10;
                this.m01 = 0.0;
                this.m11 = this.m10 * affineTransform.m01;
                this.m10 = 0.0;
                this.state = (state ^ 0x6);
                this.type = -1;
                return;
            }
            case 34:
            case 35: {
                this.m01 = this.m00 * affineTransform.m01;
                this.m00 = 0.0;
                this.m10 = this.m11 * affineTransform.m10;
                this.m11 = 0.0;
                this.state = (state ^ 0x6);
                this.type = -1;
                return;
            }
            case 33: {
                this.m00 = 0.0;
                this.m01 = affineTransform.m01;
                this.m10 = affineTransform.m10;
                this.m11 = 0.0;
                this.state = 5;
                this.type = -1;
                return;
            }
            default: {
                final double m5 = affineTransform.m00;
                final double m6 = affineTransform.m01;
                final double m7 = affineTransform.m02;
                final double m8 = affineTransform.m10;
                final double m9 = affineTransform.m11;
                final double m10 = affineTransform.m12;
                switch (state) {
                    default: {
                        this.stateError();
                    }
                    case 6: {
                        this.state = (state | state2);
                    }
                    case 7: {
                        final double m11 = this.m00;
                        final double m12 = this.m01;
                        this.m00 = m5 * m11 + m8 * m12;
                        this.m01 = m6 * m11 + m9 * m12;
                        this.m02 += m7 * m11 + m10 * m12;
                        final double m13 = this.m10;
                        final double m14 = this.m11;
                        this.m10 = m5 * m13 + m8 * m14;
                        this.m11 = m6 * m13 + m9 * m14;
                        this.m12 += m7 * m13 + m10 * m14;
                        this.type = -1;
                        return;
                    }
                    case 4:
                    case 5: {
                        final double m15 = this.m01;
                        this.m00 = m8 * m15;
                        this.m01 = m9 * m15;
                        this.m02 += m10 * m15;
                        final double m16 = this.m10;
                        this.m10 = m5 * m16;
                        this.m11 = m6 * m16;
                        this.m12 += m7 * m16;
                        break;
                    }
                    case 2:
                    case 3: {
                        final double m17 = this.m00;
                        this.m00 = m5 * m17;
                        this.m01 = m6 * m17;
                        this.m02 += m7 * m17;
                        final double m18 = this.m11;
                        this.m10 = m8 * m18;
                        this.m11 = m9 * m18;
                        this.m12 += m10 * m18;
                        break;
                    }
                    case 1: {
                        this.m00 = m5;
                        this.m01 = m6;
                        this.m02 += m7;
                        this.m10 = m8;
                        this.m11 = m9;
                        this.m12 += m10;
                        this.state = (state2 | 0x1);
                        this.type = -1;
                        return;
                    }
                }
                this.updateState();
            }
        }
    }
    
    public void preConcatenate(final AffineTransform affineTransform) {
        int state = this.state;
        final int state2 = affineTransform.state;
        switch (state2 << 3 | state) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7: {
                return;
            }
            case 8:
            case 10:
            case 12:
            case 14: {
                this.m02 = affineTransform.m02;
                this.m12 = affineTransform.m12;
                this.state = (state | 0x1);
                this.type |= 0x1;
                return;
            }
            case 9:
            case 11:
            case 13:
            case 15: {
                this.m02 += affineTransform.m02;
                this.m12 += affineTransform.m12;
                return;
            }
            case 16:
            case 17: {
                this.state = (state | 0x2);
            }
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23: {
                final double m00 = affineTransform.m00;
                final double m2 = affineTransform.m11;
                if ((state & 0x4) != 0x0) {
                    this.m01 *= m00;
                    this.m10 *= m2;
                    if ((state & 0x2) != 0x0) {
                        this.m00 *= m00;
                        this.m11 *= m2;
                    }
                }
                else {
                    this.m00 *= m00;
                    this.m11 *= m2;
                }
                if ((state & 0x1) != 0x0) {
                    this.m02 *= m00;
                    this.m12 *= m2;
                }
                this.type = -1;
                return;
            }
            case 36:
            case 37: {
                state |= 0x2;
            }
            case 32:
            case 33:
            case 34:
            case 35: {
                this.state = (state ^ 0x4);
            }
            case 38:
            case 39: {
                final double m3 = affineTransform.m01;
                final double m4 = affineTransform.m10;
                final double m5 = this.m00;
                this.m00 = this.m10 * m3;
                this.m10 = m5 * m4;
                final double m6 = this.m01;
                this.m01 = this.m11 * m3;
                this.m11 = m6 * m4;
                final double m7 = this.m02;
                this.m02 = this.m12 * m3;
                this.m12 = m7 * m4;
                this.type = -1;
                return;
            }
            default: {
                final double m8 = affineTransform.m00;
                final double m9 = affineTransform.m01;
                double m10 = affineTransform.m02;
                final double m11 = affineTransform.m10;
                final double m12 = affineTransform.m11;
                double m13 = affineTransform.m12;
                switch (state) {
                    default: {
                        this.stateError();
                    }
                    case 7: {
                        final double m14 = this.m02;
                        final double m15 = this.m12;
                        m10 += m14 * m8 + m15 * m9;
                        m13 += m14 * m11 + m15 * m12;
                    }
                    case 6: {
                        this.m02 = m10;
                        this.m12 = m13;
                        final double m16 = this.m00;
                        final double m17 = this.m10;
                        this.m00 = m16 * m8 + m17 * m9;
                        this.m10 = m16 * m11 + m17 * m12;
                        final double m18 = this.m01;
                        final double m19 = this.m11;
                        this.m01 = m18 * m8 + m19 * m9;
                        this.m11 = m18 * m11 + m19 * m12;
                        break;
                    }
                    case 5: {
                        final double m20 = this.m02;
                        final double m21 = this.m12;
                        m10 += m20 * m8 + m21 * m9;
                        m13 += m20 * m11 + m21 * m12;
                    }
                    case 4: {
                        this.m02 = m10;
                        this.m12 = m13;
                        final double m22 = this.m10;
                        this.m00 = m22 * m9;
                        this.m10 = m22 * m12;
                        final double m23 = this.m01;
                        this.m01 = m23 * m8;
                        this.m11 = m23 * m11;
                        break;
                    }
                    case 3: {
                        final double m24 = this.m02;
                        final double m25 = this.m12;
                        m10 += m24 * m8 + m25 * m9;
                        m13 += m24 * m11 + m25 * m12;
                    }
                    case 2: {
                        this.m02 = m10;
                        this.m12 = m13;
                        final double m26 = this.m00;
                        this.m00 = m26 * m8;
                        this.m10 = m26 * m11;
                        final double m27 = this.m11;
                        this.m01 = m27 * m9;
                        this.m11 = m27 * m12;
                        break;
                    }
                    case 1: {
                        final double m28 = this.m02;
                        final double m29 = this.m12;
                        m10 += m28 * m8 + m29 * m9;
                        m13 += m28 * m11 + m29 * m12;
                    }
                    case 0: {
                        this.m02 = m10;
                        this.m12 = m13;
                        this.m00 = m8;
                        this.m10 = m11;
                        this.m01 = m9;
                        this.m11 = m12;
                        this.state = (state | state2);
                        this.type = -1;
                        return;
                    }
                }
                this.updateState();
            }
        }
    }
    
    public AffineTransform createInverse() throws NoninvertibleTransformException {
        switch (this.state) {
            default: {
                this.stateError();
                return null;
            }
            case 7: {
                final double n = this.m00 * this.m11 - this.m01 * this.m10;
                if (Math.abs(n) <= Double.MIN_VALUE) {
                    throw new NoninvertibleTransformException("Determinant is " + n);
                }
                return new AffineTransform(this.m11 / n, -this.m10 / n, -this.m01 / n, this.m00 / n, (this.m01 * this.m12 - this.m11 * this.m02) / n, (this.m10 * this.m02 - this.m00 * this.m12) / n, 7);
            }
            case 6: {
                final double n2 = this.m00 * this.m11 - this.m01 * this.m10;
                if (Math.abs(n2) <= Double.MIN_VALUE) {
                    throw new NoninvertibleTransformException("Determinant is " + n2);
                }
                return new AffineTransform(this.m11 / n2, -this.m10 / n2, -this.m01 / n2, this.m00 / n2, 0.0, 0.0, 6);
            }
            case 5: {
                if (this.m01 == 0.0 || this.m10 == 0.0) {
                    throw new NoninvertibleTransformException("Determinant is 0");
                }
                return new AffineTransform(0.0, 1.0 / this.m01, 1.0 / this.m10, 0.0, -this.m12 / this.m10, -this.m02 / this.m01, 5);
            }
            case 4: {
                if (this.m01 == 0.0 || this.m10 == 0.0) {
                    throw new NoninvertibleTransformException("Determinant is 0");
                }
                return new AffineTransform(0.0, 1.0 / this.m01, 1.0 / this.m10, 0.0, 0.0, 0.0, 4);
            }
            case 3: {
                if (this.m00 == 0.0 || this.m11 == 0.0) {
                    throw new NoninvertibleTransformException("Determinant is 0");
                }
                return new AffineTransform(1.0 / this.m00, 0.0, 0.0, 1.0 / this.m11, -this.m02 / this.m00, -this.m12 / this.m11, 3);
            }
            case 2: {
                if (this.m00 == 0.0 || this.m11 == 0.0) {
                    throw new NoninvertibleTransformException("Determinant is 0");
                }
                return new AffineTransform(1.0 / this.m00, 0.0, 0.0, 1.0 / this.m11, 0.0, 0.0, 2);
            }
            case 1: {
                return new AffineTransform(1.0, 0.0, 0.0, 1.0, -this.m02, -this.m12, 1);
            }
            case 0: {
                return new AffineTransform();
            }
        }
    }
    
    public void invert() throws NoninvertibleTransformException {
        switch (this.state) {
            default: {
                this.stateError();
                return;
            }
            case 7: {
                final double m00 = this.m00;
                final double m2 = this.m01;
                final double m3 = this.m02;
                final double m4 = this.m10;
                final double m5 = this.m11;
                final double m6 = this.m12;
                final double n = m00 * m5 - m2 * m4;
                if (Math.abs(n) <= Double.MIN_VALUE) {
                    throw new NoninvertibleTransformException("Determinant is " + n);
                }
                this.m00 = m5 / n;
                this.m10 = -m4 / n;
                this.m01 = -m2 / n;
                this.m11 = m00 / n;
                this.m02 = (m2 * m6 - m5 * m3) / n;
                this.m12 = (m4 * m3 - m00 * m6) / n;
                return;
            }
            case 6: {
                final double m7 = this.m00;
                final double m8 = this.m01;
                final double m9 = this.m10;
                final double m10 = this.m11;
                final double n2 = m7 * m10 - m8 * m9;
                if (Math.abs(n2) <= Double.MIN_VALUE) {
                    throw new NoninvertibleTransformException("Determinant is " + n2);
                }
                this.m00 = m10 / n2;
                this.m10 = -m9 / n2;
                this.m01 = -m8 / n2;
                this.m11 = m7 / n2;
                return;
            }
            case 5: {
                final double m11 = this.m01;
                final double m12 = this.m02;
                final double m13 = this.m10;
                final double m14 = this.m12;
                if (m11 == 0.0 || m13 == 0.0) {
                    throw new NoninvertibleTransformException("Determinant is 0");
                }
                this.m10 = 1.0 / m11;
                this.m01 = 1.0 / m13;
                this.m02 = -m14 / m13;
                this.m12 = -m12 / m11;
                return;
            }
            case 4: {
                final double m15 = this.m01;
                final double m16 = this.m10;
                if (m15 == 0.0 || m16 == 0.0) {
                    throw new NoninvertibleTransformException("Determinant is 0");
                }
                this.m10 = 1.0 / m15;
                this.m01 = 1.0 / m16;
                return;
            }
            case 3: {
                final double m17 = this.m00;
                final double m18 = this.m02;
                final double m19 = this.m11;
                final double m20 = this.m12;
                if (m17 == 0.0 || m19 == 0.0) {
                    throw new NoninvertibleTransformException("Determinant is 0");
                }
                this.m00 = 1.0 / m17;
                this.m11 = 1.0 / m19;
                this.m02 = -m18 / m17;
                this.m12 = -m20 / m19;
                return;
            }
            case 2: {
                final double m21 = this.m00;
                final double m22 = this.m11;
                if (m21 == 0.0 || m22 == 0.0) {
                    throw new NoninvertibleTransformException("Determinant is 0");
                }
                this.m00 = 1.0 / m21;
                this.m11 = 1.0 / m22;
                return;
            }
            case 1: {
                this.m02 = -this.m02;
                this.m12 = -this.m12;
            }
            case 0: {}
        }
    }
    
    public Point2D transform(final Point2D point2D, Point2D point2D2) {
        if (point2D2 == null) {
            if (point2D instanceof Point2D.Double) {
                point2D2 = new Point2D.Double();
            }
            else {
                point2D2 = new Point2D.Float();
            }
        }
        final double x = point2D.getX();
        final double y = point2D.getY();
        switch (this.state) {
            default: {
                this.stateError();
                return null;
            }
            case 7: {
                point2D2.setLocation(x * this.m00 + y * this.m01 + this.m02, x * this.m10 + y * this.m11 + this.m12);
                return point2D2;
            }
            case 6: {
                point2D2.setLocation(x * this.m00 + y * this.m01, x * this.m10 + y * this.m11);
                return point2D2;
            }
            case 5: {
                point2D2.setLocation(y * this.m01 + this.m02, x * this.m10 + this.m12);
                return point2D2;
            }
            case 4: {
                point2D2.setLocation(y * this.m01, x * this.m10);
                return point2D2;
            }
            case 3: {
                point2D2.setLocation(x * this.m00 + this.m02, y * this.m11 + this.m12);
                return point2D2;
            }
            case 2: {
                point2D2.setLocation(x * this.m00, y * this.m11);
                return point2D2;
            }
            case 1: {
                point2D2.setLocation(x + this.m02, y + this.m12);
                return point2D2;
            }
            case 0: {
                point2D2.setLocation(x, y);
                return point2D2;
            }
        }
    }
    
    public void transform(final Point2D[] array, int n, final Point2D[] array2, int n2, int n3) {
        final int state = this.state;
        while (--n3 >= 0) {
            final Point2D point2D = array[n++];
            final double x = point2D.getX();
            final double y = point2D.getY();
            Point2D point2D2 = array2[n2++];
            if (point2D2 == null) {
                if (point2D instanceof Point2D.Double) {
                    point2D2 = new Point2D.Double();
                }
                else {
                    point2D2 = new Point2D.Float();
                }
                array2[n2 - 1] = point2D2;
            }
            switch (state) {
                default: {
                    this.stateError();
                    return;
                }
                case 7: {
                    point2D2.setLocation(x * this.m00 + y * this.m01 + this.m02, x * this.m10 + y * this.m11 + this.m12);
                    continue;
                }
                case 6: {
                    point2D2.setLocation(x * this.m00 + y * this.m01, x * this.m10 + y * this.m11);
                    continue;
                }
                case 5: {
                    point2D2.setLocation(y * this.m01 + this.m02, x * this.m10 + this.m12);
                    continue;
                }
                case 4: {
                    point2D2.setLocation(y * this.m01, x * this.m10);
                    continue;
                }
                case 3: {
                    point2D2.setLocation(x * this.m00 + this.m02, y * this.m11 + this.m12);
                    continue;
                }
                case 2: {
                    point2D2.setLocation(x * this.m00, y * this.m11);
                    continue;
                }
                case 1: {
                    point2D2.setLocation(x + this.m02, y + this.m12);
                    continue;
                }
                case 0: {
                    point2D2.setLocation(x, y);
                    continue;
                }
            }
        }
    }
    
    public void transform(final float[] array, int n, final float[] array2, int n2, int n3) {
        if (array2 == array && n2 > n && n2 < n + n3 * 2) {
            System.arraycopy(array, n, array2, n2, n3 * 2);
            n = n2;
        }
        switch (this.state) {
            default: {
                this.stateError();
                return;
            }
            case 7: {
                final double m00 = this.m00;
                final double m2 = this.m01;
                final double m3 = this.m02;
                final double m4 = this.m10;
                final double m5 = this.m11;
                final double m6 = this.m12;
                while (--n3 >= 0) {
                    final double n4 = array[n++];
                    final double n5 = array[n++];
                    array2[n2++] = (float)(m00 * n4 + m2 * n5 + m3);
                    array2[n2++] = (float)(m4 * n4 + m5 * n5 + m6);
                }
                return;
            }
            case 6: {
                final double m7 = this.m00;
                final double m8 = this.m01;
                final double m9 = this.m10;
                final double m10 = this.m11;
                while (--n3 >= 0) {
                    final double n6 = array[n++];
                    final double n7 = array[n++];
                    array2[n2++] = (float)(m7 * n6 + m8 * n7);
                    array2[n2++] = (float)(m9 * n6 + m10 * n7);
                }
                return;
            }
            case 5: {
                final double m11 = this.m01;
                final double m12 = this.m02;
                final double m13 = this.m10;
                final double m14 = this.m12;
                while (--n3 >= 0) {
                    final double n8 = array[n++];
                    array2[n2++] = (float)(m11 * array[n++] + m12);
                    array2[n2++] = (float)(m13 * n8 + m14);
                }
                return;
            }
            case 4: {
                final double m15 = this.m01;
                final double m16 = this.m10;
                while (--n3 >= 0) {
                    final double n9 = array[n++];
                    array2[n2++] = (float)(m15 * array[n++]);
                    array2[n2++] = (float)(m16 * n9);
                }
                return;
            }
            case 3: {
                final double m17 = this.m00;
                final double m18 = this.m02;
                final double m19 = this.m11;
                final double m20 = this.m12;
                while (--n3 >= 0) {
                    array2[n2++] = (float)(m17 * array[n++] + m18);
                    array2[n2++] = (float)(m19 * array[n++] + m20);
                }
                return;
            }
            case 2: {
                final double m21 = this.m00;
                final double m22 = this.m11;
                while (--n3 >= 0) {
                    array2[n2++] = (float)(m21 * array[n++]);
                    array2[n2++] = (float)(m22 * array[n++]);
                }
                return;
            }
            case 1: {
                final double m23 = this.m02;
                final double m24 = this.m12;
                while (--n3 >= 0) {
                    array2[n2++] = (float)(array[n++] + m23);
                    array2[n2++] = (float)(array[n++] + m24);
                }
                return;
            }
            case 0: {
                if (array != array2 || n != n2) {
                    System.arraycopy(array, n, array2, n2, n3 * 2);
                }
            }
        }
    }
    
    public void transform(final double[] array, int n, final double[] array2, int n2, int n3) {
        if (array2 == array && n2 > n && n2 < n + n3 * 2) {
            System.arraycopy(array, n, array2, n2, n3 * 2);
            n = n2;
        }
        switch (this.state) {
            default: {
                this.stateError();
                return;
            }
            case 7: {
                final double m00 = this.m00;
                final double m2 = this.m01;
                final double m3 = this.m02;
                final double m4 = this.m10;
                final double m5 = this.m11;
                final double m6 = this.m12;
                while (--n3 >= 0) {
                    final double n4 = array[n++];
                    final double n5 = array[n++];
                    array2[n2++] = m00 * n4 + m2 * n5 + m3;
                    array2[n2++] = m4 * n4 + m5 * n5 + m6;
                }
                return;
            }
            case 6: {
                final double m7 = this.m00;
                final double m8 = this.m01;
                final double m9 = this.m10;
                final double m10 = this.m11;
                while (--n3 >= 0) {
                    final double n6 = array[n++];
                    final double n7 = array[n++];
                    array2[n2++] = m7 * n6 + m8 * n7;
                    array2[n2++] = m9 * n6 + m10 * n7;
                }
                return;
            }
            case 5: {
                final double m11 = this.m01;
                final double m12 = this.m02;
                final double m13 = this.m10;
                final double m14 = this.m12;
                while (--n3 >= 0) {
                    final double n8 = array[n++];
                    array2[n2++] = m11 * array[n++] + m12;
                    array2[n2++] = m13 * n8 + m14;
                }
                return;
            }
            case 4: {
                final double m15 = this.m01;
                final double m16 = this.m10;
                while (--n3 >= 0) {
                    final double n9 = array[n++];
                    array2[n2++] = m15 * array[n++];
                    array2[n2++] = m16 * n9;
                }
                return;
            }
            case 3: {
                final double m17 = this.m00;
                final double m18 = this.m02;
                final double m19 = this.m11;
                final double m20 = this.m12;
                while (--n3 >= 0) {
                    array2[n2++] = m17 * array[n++] + m18;
                    array2[n2++] = m19 * array[n++] + m20;
                }
                return;
            }
            case 2: {
                final double m21 = this.m00;
                final double m22 = this.m11;
                while (--n3 >= 0) {
                    array2[n2++] = m21 * array[n++];
                    array2[n2++] = m22 * array[n++];
                }
                return;
            }
            case 1: {
                final double m23 = this.m02;
                final double m24 = this.m12;
                while (--n3 >= 0) {
                    array2[n2++] = array[n++] + m23;
                    array2[n2++] = array[n++] + m24;
                }
                return;
            }
            case 0: {
                if (array != array2 || n != n2) {
                    System.arraycopy(array, n, array2, n2, n3 * 2);
                }
            }
        }
    }
    
    public void transform(final float[] array, int n, final double[] array2, int n2, int n3) {
        switch (this.state) {
            default: {
                this.stateError();
                return;
            }
            case 7: {
                final double m00 = this.m00;
                final double m2 = this.m01;
                final double m3 = this.m02;
                final double m4 = this.m10;
                final double m5 = this.m11;
                final double m6 = this.m12;
                while (--n3 >= 0) {
                    final double n4 = array[n++];
                    final double n5 = array[n++];
                    array2[n2++] = m00 * n4 + m2 * n5 + m3;
                    array2[n2++] = m4 * n4 + m5 * n5 + m6;
                }
                return;
            }
            case 6: {
                final double m7 = this.m00;
                final double m8 = this.m01;
                final double m9 = this.m10;
                final double m10 = this.m11;
                while (--n3 >= 0) {
                    final double n6 = array[n++];
                    final double n7 = array[n++];
                    array2[n2++] = m7 * n6 + m8 * n7;
                    array2[n2++] = m9 * n6 + m10 * n7;
                }
                return;
            }
            case 5: {
                final double m11 = this.m01;
                final double m12 = this.m02;
                final double m13 = this.m10;
                final double m14 = this.m12;
                while (--n3 >= 0) {
                    final double n8 = array[n++];
                    array2[n2++] = m11 * array[n++] + m12;
                    array2[n2++] = m13 * n8 + m14;
                }
                return;
            }
            case 4: {
                final double m15 = this.m01;
                final double m16 = this.m10;
                while (--n3 >= 0) {
                    final double n9 = array[n++];
                    array2[n2++] = m15 * array[n++];
                    array2[n2++] = m16 * n9;
                }
                return;
            }
            case 3: {
                final double m17 = this.m00;
                final double m18 = this.m02;
                final double m19 = this.m11;
                final double m20 = this.m12;
                while (--n3 >= 0) {
                    array2[n2++] = m17 * array[n++] + m18;
                    array2[n2++] = m19 * array[n++] + m20;
                }
                return;
            }
            case 2: {
                final double m21 = this.m00;
                final double m22 = this.m11;
                while (--n3 >= 0) {
                    array2[n2++] = m21 * array[n++];
                    array2[n2++] = m22 * array[n++];
                }
                return;
            }
            case 1: {
                final double m23 = this.m02;
                final double m24 = this.m12;
                while (--n3 >= 0) {
                    array2[n2++] = array[n++] + m23;
                    array2[n2++] = array[n++] + m24;
                }
                return;
            }
            case 0: {
                while (--n3 >= 0) {
                    array2[n2++] = array[n++];
                    array2[n2++] = array[n++];
                }
            }
        }
    }
    
    public void transform(final double[] array, int n, final float[] array2, int n2, int n3) {
        switch (this.state) {
            default: {
                this.stateError();
                return;
            }
            case 7: {
                final double m00 = this.m00;
                final double m2 = this.m01;
                final double m3 = this.m02;
                final double m4 = this.m10;
                final double m5 = this.m11;
                final double m6 = this.m12;
                while (--n3 >= 0) {
                    final double n4 = array[n++];
                    final double n5 = array[n++];
                    array2[n2++] = (float)(m00 * n4 + m2 * n5 + m3);
                    array2[n2++] = (float)(m4 * n4 + m5 * n5 + m6);
                }
                return;
            }
            case 6: {
                final double m7 = this.m00;
                final double m8 = this.m01;
                final double m9 = this.m10;
                final double m10 = this.m11;
                while (--n3 >= 0) {
                    final double n6 = array[n++];
                    final double n7 = array[n++];
                    array2[n2++] = (float)(m7 * n6 + m8 * n7);
                    array2[n2++] = (float)(m9 * n6 + m10 * n7);
                }
                return;
            }
            case 5: {
                final double m11 = this.m01;
                final double m12 = this.m02;
                final double m13 = this.m10;
                final double m14 = this.m12;
                while (--n3 >= 0) {
                    final double n8 = array[n++];
                    array2[n2++] = (float)(m11 * array[n++] + m12);
                    array2[n2++] = (float)(m13 * n8 + m14);
                }
                return;
            }
            case 4: {
                final double m15 = this.m01;
                final double m16 = this.m10;
                while (--n3 >= 0) {
                    final double n9 = array[n++];
                    array2[n2++] = (float)(m15 * array[n++]);
                    array2[n2++] = (float)(m16 * n9);
                }
                return;
            }
            case 3: {
                final double m17 = this.m00;
                final double m18 = this.m02;
                final double m19 = this.m11;
                final double m20 = this.m12;
                while (--n3 >= 0) {
                    array2[n2++] = (float)(m17 * array[n++] + m18);
                    array2[n2++] = (float)(m19 * array[n++] + m20);
                }
                return;
            }
            case 2: {
                final double m21 = this.m00;
                final double m22 = this.m11;
                while (--n3 >= 0) {
                    array2[n2++] = (float)(m21 * array[n++]);
                    array2[n2++] = (float)(m22 * array[n++]);
                }
                return;
            }
            case 1: {
                final double m23 = this.m02;
                final double m24 = this.m12;
                while (--n3 >= 0) {
                    array2[n2++] = (float)(array[n++] + m23);
                    array2[n2++] = (float)(array[n++] + m24);
                }
                return;
            }
            case 0: {
                while (--n3 >= 0) {
                    array2[n2++] = (float)array[n++];
                    array2[n2++] = (float)array[n++];
                }
            }
        }
    }
    
    public Point2D inverseTransform(final Point2D point2D, Point2D point2D2) throws NoninvertibleTransformException {
        if (point2D2 == null) {
            if (point2D instanceof Point2D.Double) {
                point2D2 = new Point2D.Double();
            }
            else {
                point2D2 = new Point2D.Float();
            }
        }
        double x = point2D.getX();
        double y = point2D.getY();
        switch (this.state) {
            default: {
                this.stateError();
            }
            case 7: {
                x -= this.m02;
                y -= this.m12;
            }
            case 6: {
                final double n = this.m00 * this.m11 - this.m01 * this.m10;
                if (Math.abs(n) <= Double.MIN_VALUE) {
                    throw new NoninvertibleTransformException("Determinant is " + n);
                }
                point2D2.setLocation((x * this.m11 - y * this.m01) / n, (y * this.m00 - x * this.m10) / n);
                return point2D2;
            }
            case 5: {
                x -= this.m02;
                y -= this.m12;
            }
            case 4: {
                if (this.m01 == 0.0 || this.m10 == 0.0) {
                    throw new NoninvertibleTransformException("Determinant is 0");
                }
                point2D2.setLocation(y / this.m10, x / this.m01);
                return point2D2;
            }
            case 3: {
                x -= this.m02;
                y -= this.m12;
            }
            case 2: {
                if (this.m00 == 0.0 || this.m11 == 0.0) {
                    throw new NoninvertibleTransformException("Determinant is 0");
                }
                point2D2.setLocation(x / this.m00, y / this.m11);
                return point2D2;
            }
            case 1: {
                point2D2.setLocation(x - this.m02, y - this.m12);
                return point2D2;
            }
            case 0: {
                point2D2.setLocation(x, y);
                return point2D2;
            }
        }
    }
    
    public void inverseTransform(final double[] array, int n, final double[] array2, int n2, int n3) throws NoninvertibleTransformException {
        if (array2 == array && n2 > n && n2 < n + n3 * 2) {
            System.arraycopy(array, n, array2, n2, n3 * 2);
            n = n2;
        }
        switch (this.state) {
            default: {
                this.stateError();
                return;
            }
            case 7: {
                final double m00 = this.m00;
                final double m2 = this.m01;
                final double m3 = this.m02;
                final double m4 = this.m10;
                final double m5 = this.m11;
                final double m6 = this.m12;
                final double n4 = m00 * m5 - m2 * m4;
                if (Math.abs(n4) <= Double.MIN_VALUE) {
                    throw new NoninvertibleTransformException("Determinant is " + n4);
                }
                while (--n3 >= 0) {
                    final double n5 = array[n++] - m3;
                    final double n6 = array[n++] - m6;
                    array2[n2++] = (n5 * m5 - n6 * m2) / n4;
                    array2[n2++] = (n6 * m00 - n5 * m4) / n4;
                }
                return;
            }
            case 6: {
                final double m7 = this.m00;
                final double m8 = this.m01;
                final double m9 = this.m10;
                final double m10 = this.m11;
                final double n7 = m7 * m10 - m8 * m9;
                if (Math.abs(n7) <= Double.MIN_VALUE) {
                    throw new NoninvertibleTransformException("Determinant is " + n7);
                }
                while (--n3 >= 0) {
                    final double n8 = array[n++];
                    final double n9 = array[n++];
                    array2[n2++] = (n8 * m10 - n9 * m8) / n7;
                    array2[n2++] = (n9 * m7 - n8 * m9) / n7;
                }
                return;
            }
            case 5: {
                final double m11 = this.m01;
                final double m12 = this.m02;
                final double m13 = this.m10;
                final double m14 = this.m12;
                if (m11 == 0.0 || m13 == 0.0) {
                    throw new NoninvertibleTransformException("Determinant is 0");
                }
                while (--n3 >= 0) {
                    final double n10 = array[n++] - m12;
                    array2[n2++] = (array[n++] - m14) / m13;
                    array2[n2++] = n10 / m11;
                }
                return;
            }
            case 4: {
                final double m15 = this.m01;
                final double m16 = this.m10;
                if (m15 == 0.0 || m16 == 0.0) {
                    throw new NoninvertibleTransformException("Determinant is 0");
                }
                while (--n3 >= 0) {
                    final double n11 = array[n++];
                    array2[n2++] = array[n++] / m16;
                    array2[n2++] = n11 / m15;
                }
                return;
            }
            case 3: {
                final double m17 = this.m00;
                final double m18 = this.m02;
                final double m19 = this.m11;
                final double m20 = this.m12;
                if (m17 == 0.0 || m19 == 0.0) {
                    throw new NoninvertibleTransformException("Determinant is 0");
                }
                while (--n3 >= 0) {
                    array2[n2++] = (array[n++] - m18) / m17;
                    array2[n2++] = (array[n++] - m20) / m19;
                }
                return;
            }
            case 2: {
                final double m21 = this.m00;
                final double m22 = this.m11;
                if (m21 == 0.0 || m22 == 0.0) {
                    throw new NoninvertibleTransformException("Determinant is 0");
                }
                while (--n3 >= 0) {
                    array2[n2++] = array[n++] / m21;
                    array2[n2++] = array[n++] / m22;
                }
                return;
            }
            case 1: {
                final double m23 = this.m02;
                final double m24 = this.m12;
                while (--n3 >= 0) {
                    array2[n2++] = array[n++] - m23;
                    array2[n2++] = array[n++] - m24;
                }
                return;
            }
            case 0: {
                if (array != array2 || n != n2) {
                    System.arraycopy(array, n, array2, n2, n3 * 2);
                }
            }
        }
    }
    
    public Point2D deltaTransform(final Point2D point2D, Point2D point2D2) {
        if (point2D2 == null) {
            if (point2D instanceof Point2D.Double) {
                point2D2 = new Point2D.Double();
            }
            else {
                point2D2 = new Point2D.Float();
            }
        }
        final double x = point2D.getX();
        final double y = point2D.getY();
        switch (this.state) {
            default: {
                this.stateError();
                return null;
            }
            case 6:
            case 7: {
                point2D2.setLocation(x * this.m00 + y * this.m01, x * this.m10 + y * this.m11);
                return point2D2;
            }
            case 4:
            case 5: {
                point2D2.setLocation(y * this.m01, x * this.m10);
                return point2D2;
            }
            case 2:
            case 3: {
                point2D2.setLocation(x * this.m00, y * this.m11);
                return point2D2;
            }
            case 0:
            case 1: {
                point2D2.setLocation(x, y);
                return point2D2;
            }
        }
    }
    
    public void deltaTransform(final double[] array, int n, final double[] array2, int n2, int n3) {
        if (array2 == array && n2 > n && n2 < n + n3 * 2) {
            System.arraycopy(array, n, array2, n2, n3 * 2);
            n = n2;
        }
        switch (this.state) {
            default: {
                this.stateError();
                return;
            }
            case 6:
            case 7: {
                final double m00 = this.m00;
                final double m2 = this.m01;
                final double m3 = this.m10;
                final double m4 = this.m11;
                while (--n3 >= 0) {
                    final double n4 = array[n++];
                    final double n5 = array[n++];
                    array2[n2++] = n4 * m00 + n5 * m2;
                    array2[n2++] = n4 * m3 + n5 * m4;
                }
                return;
            }
            case 4:
            case 5: {
                final double m5 = this.m01;
                final double m6 = this.m10;
                while (--n3 >= 0) {
                    final double n6 = array[n++];
                    array2[n2++] = array[n++] * m5;
                    array2[n2++] = n6 * m6;
                }
                return;
            }
            case 2:
            case 3: {
                final double m7 = this.m00;
                final double m8 = this.m11;
                while (--n3 >= 0) {
                    array2[n2++] = array[n++] * m7;
                    array2[n2++] = array[n++] * m8;
                }
                return;
            }
            case 0:
            case 1: {
                if (array != array2 || n != n2) {
                    System.arraycopy(array, n, array2, n2, n3 * 2);
                }
            }
        }
    }
    
    public Shape createTransformedShape(final Shape shape) {
        if (shape == null) {
            return null;
        }
        return new Path2D.Double(shape, this);
    }
    
    private static double _matround(final double n) {
        return Math.rint(n * 1.0E15) / 1.0E15;
    }
    
    @Override
    public String toString() {
        return "AffineTransform[[" + _matround(this.m00) + ", " + _matround(this.m01) + ", " + _matround(this.m02) + "], [" + _matround(this.m10) + ", " + _matround(this.m11) + ", " + _matround(this.m12) + "]]";
    }
    
    public boolean isIdentity() {
        return this.state == 0 || this.getType() == 0;
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            throw new InternalError(ex);
        }
    }
    
    @Override
    public int hashCode() {
        final long n = ((((Double.doubleToLongBits(this.m00) * 31L + Double.doubleToLongBits(this.m01)) * 31L + Double.doubleToLongBits(this.m02)) * 31L + Double.doubleToLongBits(this.m10)) * 31L + Double.doubleToLongBits(this.m11)) * 31L + Double.doubleToLongBits(this.m12);
        return (int)n ^ (int)(n >> 32);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof AffineTransform)) {
            return false;
        }
        final AffineTransform affineTransform = (AffineTransform)o;
        return this.m00 == affineTransform.m00 && this.m01 == affineTransform.m01 && this.m02 == affineTransform.m02 && this.m10 == affineTransform.m10 && this.m11 == affineTransform.m11 && this.m12 == affineTransform.m12;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws ClassNotFoundException, IOException {
        objectOutputStream.defaultWriteObject();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        objectInputStream.defaultReadObject();
        this.updateState();
    }
    
    static {
        rot90conversion = new int[] { 4, 5, 4, 5, 2, 3, 6, 7 };
    }
}
