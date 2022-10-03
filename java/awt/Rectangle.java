package java.awt;

import java.beans.Transient;
import java.io.Serializable;
import java.awt.geom.Rectangle2D;

public class Rectangle extends Rectangle2D implements Shape, Serializable
{
    public int x;
    public int y;
    public int width;
    public int height;
    private static final long serialVersionUID = -4345857070255674764L;
    
    private static native void initIDs();
    
    public Rectangle() {
        this(0, 0, 0, 0);
    }
    
    public Rectangle(final Rectangle rectangle) {
        this(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }
    
    public Rectangle(final int x, final int y, final int width, final int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public Rectangle(final int n, final int n2) {
        this(0, 0, n, n2);
    }
    
    public Rectangle(final Point point, final Dimension dimension) {
        this(point.x, point.y, dimension.width, dimension.height);
    }
    
    public Rectangle(final Point point) {
        this(point.x, point.y, 0, 0);
    }
    
    public Rectangle(final Dimension dimension) {
        this(0, 0, dimension.width, dimension.height);
    }
    
    @Override
    public double getX() {
        return this.x;
    }
    
    @Override
    public double getY() {
        return this.y;
    }
    
    @Override
    public double getWidth() {
        return this.width;
    }
    
    @Override
    public double getHeight() {
        return this.height;
    }
    
    @Transient
    @Override
    public Rectangle getBounds() {
        return new Rectangle(this.x, this.y, this.width, this.height);
    }
    
    @Override
    public Rectangle2D getBounds2D() {
        return new Rectangle(this.x, this.y, this.width, this.height);
    }
    
    public void setBounds(final Rectangle rectangle) {
        this.setBounds(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }
    
    public void setBounds(final int n, final int n2, final int n3, final int n4) {
        this.reshape(n, n2, n3, n4);
    }
    
    @Override
    public void setRect(final double n, final double n2, double n3, double n4) {
        int clip;
        int clip2;
        if (n > 4.294967294E9) {
            clip = Integer.MAX_VALUE;
            clip2 = -1;
        }
        else {
            clip = clip(n, false);
            if (n3 >= 0.0) {
                n3 += n - clip;
            }
            clip2 = clip(n3, n3 >= 0.0);
        }
        int clip3;
        int clip4;
        if (n2 > 4.294967294E9) {
            clip3 = Integer.MAX_VALUE;
            clip4 = -1;
        }
        else {
            clip3 = clip(n2, false);
            if (n4 >= 0.0) {
                n4 += n2 - clip3;
            }
            clip4 = clip(n4, n4 >= 0.0);
        }
        this.reshape(clip, clip3, clip2, clip4);
    }
    
    private static int clip(final double n, final boolean b) {
        if (n <= -2.147483648E9) {
            return Integer.MIN_VALUE;
        }
        if (n >= 2.147483647E9) {
            return Integer.MAX_VALUE;
        }
        return (int)(b ? Math.ceil(n) : Math.floor(n));
    }
    
    @Deprecated
    public void reshape(final int x, final int y, final int width, final int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public Point getLocation() {
        return new Point(this.x, this.y);
    }
    
    public void setLocation(final Point point) {
        this.setLocation(point.x, point.y);
    }
    
    public void setLocation(final int n, final int n2) {
        this.move(n, n2);
    }
    
    @Deprecated
    public void move(final int x, final int y) {
        this.x = x;
        this.y = y;
    }
    
    public void translate(final int n, final int n2) {
        final int x = this.x;
        int x2 = x + n;
        if (n < 0) {
            if (x2 > x) {
                if (this.width >= 0) {
                    this.width += x2 - Integer.MIN_VALUE;
                }
                x2 = Integer.MIN_VALUE;
            }
        }
        else if (x2 < x) {
            if (this.width >= 0) {
                this.width += x2 - Integer.MAX_VALUE;
                if (this.width < 0) {
                    this.width = Integer.MAX_VALUE;
                }
            }
            x2 = Integer.MAX_VALUE;
        }
        this.x = x2;
        final int y = this.y;
        int y2 = y + n2;
        if (n2 < 0) {
            if (y2 > y) {
                if (this.height >= 0) {
                    this.height += y2 - Integer.MIN_VALUE;
                }
                y2 = Integer.MIN_VALUE;
            }
        }
        else if (y2 < y) {
            if (this.height >= 0) {
                this.height += y2 - Integer.MAX_VALUE;
                if (this.height < 0) {
                    this.height = Integer.MAX_VALUE;
                }
            }
            y2 = Integer.MAX_VALUE;
        }
        this.y = y2;
    }
    
    public Dimension getSize() {
        return new Dimension(this.width, this.height);
    }
    
    public void setSize(final Dimension dimension) {
        this.setSize(dimension.width, dimension.height);
    }
    
    public void setSize(final int n, final int n2) {
        this.resize(n, n2);
    }
    
    @Deprecated
    public void resize(final int width, final int height) {
        this.width = width;
        this.height = height;
    }
    
    public boolean contains(final Point point) {
        return this.contains(point.x, point.y);
    }
    
    public boolean contains(final int n, final int n2) {
        return this.inside(n, n2);
    }
    
    public boolean contains(final Rectangle rectangle) {
        return this.contains(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }
    
    public boolean contains(final int n, final int n2, int n3, int n4) {
        final int width = this.width;
        final int height = this.height;
        if ((width | height | n3 | n4) < 0) {
            return false;
        }
        final int x = this.x;
        final int y = this.y;
        if (n < x || n2 < y) {
            return false;
        }
        final int n5 = width + x;
        n3 += n;
        if (n3 <= n) {
            if (n5 >= x || n3 > n5) {
                return false;
            }
        }
        else if (n5 >= x && n3 > n5) {
            return false;
        }
        final int n6 = height + y;
        n4 += n2;
        if (n4 <= n2) {
            if (n6 >= y || n4 > n6) {
                return false;
            }
        }
        else if (n6 >= y && n4 > n6) {
            return false;
        }
        return true;
    }
    
    @Deprecated
    public boolean inside(final int n, final int n2) {
        final int width = this.width;
        final int height = this.height;
        if ((width | height) < 0) {
            return false;
        }
        final int x = this.x;
        final int y = this.y;
        if (n < x || n2 < y) {
            return false;
        }
        final int n3 = width + x;
        final int n4 = height + y;
        return (n3 < x || n3 > n) && (n4 < y || n4 > n2);
    }
    
    public boolean intersects(final Rectangle rectangle) {
        final int width = this.width;
        final int height = this.height;
        final int width2 = rectangle.width;
        final int height2 = rectangle.height;
        if (width2 <= 0 || height2 <= 0 || width <= 0 || height <= 0) {
            return false;
        }
        final int x = this.x;
        final int y = this.y;
        final int x2 = rectangle.x;
        final int y2 = rectangle.y;
        final int n = width2 + x2;
        final int n2 = height2 + y2;
        final int n3 = width + x;
        final int n4 = height + y;
        return (n < x2 || n > x) && (n2 < y2 || n2 > y) && (n3 < x || n3 > x2) && (n4 < y || n4 > y2);
    }
    
    public Rectangle intersection(final Rectangle rectangle) {
        int x = this.x;
        int y = this.y;
        final int x2 = rectangle.x;
        final int y2 = rectangle.y;
        long n = x + (long)this.width;
        long n2 = y + (long)this.height;
        final long n3 = x2 + (long)rectangle.width;
        final long n4 = y2 + (long)rectangle.height;
        if (x < x2) {
            x = x2;
        }
        if (y < y2) {
            y = y2;
        }
        if (n > n3) {
            n = n3;
        }
        if (n2 > n4) {
            n2 = n4;
        }
        long n5 = n - x;
        long n6 = n2 - y;
        if (n5 < -2147483648L) {
            n5 = -2147483648L;
        }
        if (n6 < -2147483648L) {
            n6 = -2147483648L;
        }
        return new Rectangle(x, y, (int)n5, (int)n6);
    }
    
    public Rectangle union(final Rectangle rectangle) {
        final long n = this.width;
        final long n2 = this.height;
        if ((n | n2) < 0L) {
            return new Rectangle(rectangle);
        }
        final long n3 = rectangle.width;
        final long n4 = rectangle.height;
        if ((n3 | n4) < 0L) {
            return new Rectangle(this);
        }
        int x = this.x;
        int y = this.y;
        long n5 = n + x;
        long n6 = n2 + y;
        final int x2 = rectangle.x;
        final int y2 = rectangle.y;
        final long n7 = n3 + x2;
        final long n8 = n4 + y2;
        if (x > x2) {
            x = x2;
        }
        if (y > y2) {
            y = y2;
        }
        if (n5 < n7) {
            n5 = n7;
        }
        if (n6 < n8) {
            n6 = n8;
        }
        long n9 = n5 - x;
        long n10 = n6 - y;
        if (n9 > 2147483647L) {
            n9 = 2147483647L;
        }
        if (n10 > 2147483647L) {
            n10 = 2147483647L;
        }
        return new Rectangle(x, y, (int)n9, (int)n10);
    }
    
    public void add(final int x, final int y) {
        if ((this.width | this.height) < 0) {
            this.x = x;
            this.y = y;
            final int n = 0;
            this.height = n;
            this.width = n;
            return;
        }
        int x2 = this.x;
        int y2 = this.y;
        final long n2 = this.width;
        final long n3 = this.height;
        long n4 = n2 + x2;
        long n5 = n3 + y2;
        if (x2 > x) {
            x2 = x;
        }
        if (y2 > y) {
            y2 = y;
        }
        if (n4 < x) {
            n4 = x;
        }
        if (n5 < y) {
            n5 = y;
        }
        long n6 = n4 - x2;
        long n7 = n5 - y2;
        if (n6 > 2147483647L) {
            n6 = 2147483647L;
        }
        if (n7 > 2147483647L) {
            n7 = 2147483647L;
        }
        this.reshape(x2, y2, (int)n6, (int)n7);
    }
    
    public void add(final Point point) {
        this.add(point.x, point.y);
    }
    
    public void add(final Rectangle rectangle) {
        final long n = this.width;
        final long n2 = this.height;
        if ((n | n2) < 0L) {
            this.reshape(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        }
        final long n3 = rectangle.width;
        final long n4 = rectangle.height;
        if ((n3 | n4) < 0L) {
            return;
        }
        int x = this.x;
        int y = this.y;
        long n5 = n + x;
        long n6 = n2 + y;
        final int x2 = rectangle.x;
        final int y2 = rectangle.y;
        final long n7 = n3 + x2;
        final long n8 = n4 + y2;
        if (x > x2) {
            x = x2;
        }
        if (y > y2) {
            y = y2;
        }
        if (n5 < n7) {
            n5 = n7;
        }
        if (n6 < n8) {
            n6 = n8;
        }
        long n9 = n5 - x;
        long n10 = n6 - y;
        if (n9 > 2147483647L) {
            n9 = 2147483647L;
        }
        if (n10 > 2147483647L) {
            n10 = 2147483647L;
        }
        this.reshape(x, y, (int)n9, (int)n10);
    }
    
    public void grow(final int n, final int n2) {
        final long n3 = this.x;
        final long n4 = this.y;
        final long n5 = this.width;
        final long n6 = this.height;
        final long n7 = n5 + n3;
        final long n8 = n6 + n4;
        long n9 = n3 - n;
        long n10 = n4 - n2;
        final long n11 = n7 + n;
        final long n12 = n8 + n2;
        long n13;
        if (n11 < n9) {
            n13 = n11 - n9;
            if (n13 < -2147483648L) {
                n13 = -2147483648L;
            }
            if (n9 < -2147483648L) {
                n9 = -2147483648L;
            }
            else if (n9 > 2147483647L) {
                n9 = 2147483647L;
            }
        }
        else {
            if (n9 < -2147483648L) {
                n9 = -2147483648L;
            }
            else if (n9 > 2147483647L) {
                n9 = 2147483647L;
            }
            n13 = n11 - n9;
            if (n13 < -2147483648L) {
                n13 = -2147483648L;
            }
            else if (n13 > 2147483647L) {
                n13 = 2147483647L;
            }
        }
        long n14;
        if (n12 < n10) {
            n14 = n12 - n10;
            if (n14 < -2147483648L) {
                n14 = -2147483648L;
            }
            if (n10 < -2147483648L) {
                n10 = -2147483648L;
            }
            else if (n10 > 2147483647L) {
                n10 = 2147483647L;
            }
        }
        else {
            if (n10 < -2147483648L) {
                n10 = -2147483648L;
            }
            else if (n10 > 2147483647L) {
                n10 = 2147483647L;
            }
            n14 = n12 - n10;
            if (n14 < -2147483648L) {
                n14 = -2147483648L;
            }
            else if (n14 > 2147483647L) {
                n14 = 2147483647L;
            }
        }
        this.reshape((int)n9, (int)n10, (int)n13, (int)n14);
    }
    
    @Override
    public boolean isEmpty() {
        return this.width <= 0 || this.height <= 0;
    }
    
    @Override
    public int outcode(final double n, final double n2) {
        int n3 = 0;
        if (this.width <= 0) {
            n3 |= 0x5;
        }
        else if (n < this.x) {
            n3 |= 0x1;
        }
        else if (n > this.x + (double)this.width) {
            n3 |= 0x4;
        }
        if (this.height <= 0) {
            n3 |= 0xA;
        }
        else if (n2 < this.y) {
            n3 |= 0x2;
        }
        else if (n2 > this.y + (double)this.height) {
            n3 |= 0x8;
        }
        return n3;
    }
    
    @Override
    public Rectangle2D createIntersection(final Rectangle2D rectangle2D) {
        if (rectangle2D instanceof Rectangle) {
            return this.intersection((Rectangle)rectangle2D);
        }
        final Double double1 = new Double();
        Rectangle2D.intersect(this, rectangle2D, double1);
        return double1;
    }
    
    @Override
    public Rectangle2D createUnion(final Rectangle2D rectangle2D) {
        if (rectangle2D instanceof Rectangle) {
            return this.union((Rectangle)rectangle2D);
        }
        final Double double1 = new Double();
        Rectangle2D.union(this, rectangle2D, double1);
        return double1;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof Rectangle) {
            final Rectangle rectangle = (Rectangle)o;
            return this.x == rectangle.x && this.y == rectangle.y && this.width == rectangle.width && this.height == rectangle.height;
        }
        return super.equals(o);
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "[x=" + this.x + ",y=" + this.y + ",width=" + this.width + ",height=" + this.height + "]";
    }
    
    static {
        Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
    }
}
