package javax.swing.text.html;

import java.awt.Polygon;
import java.util.StringTokenizer;
import javax.swing.text.AttributeSet;
import java.util.Vector;
import java.io.Serializable;

class Map implements Serializable
{
    private String name;
    private Vector<AttributeSet> areaAttributes;
    private Vector<RegionContainment> areas;
    
    public Map() {
    }
    
    public Map(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void addArea(final AttributeSet set) {
        if (set == null) {
            return;
        }
        if (this.areaAttributes == null) {
            this.areaAttributes = new Vector<AttributeSet>(2);
        }
        this.areaAttributes.addElement(set.copyAttributes());
    }
    
    public void removeArea(final AttributeSet set) {
        if (set != null && this.areaAttributes != null) {
            final int n = (this.areas != null) ? this.areas.size() : 0;
            for (int i = this.areaAttributes.size() - 1; i >= 0; --i) {
                if (this.areaAttributes.elementAt(i).isEqual(set)) {
                    this.areaAttributes.removeElementAt(i);
                    if (i < n) {
                        this.areas.removeElementAt(i);
                    }
                }
            }
        }
    }
    
    public AttributeSet[] getAreas() {
        final int n = (this.areaAttributes != null) ? this.areaAttributes.size() : 0;
        if (n != 0) {
            final AttributeSet[] array = new AttributeSet[n];
            this.areaAttributes.copyInto(array);
            return array;
        }
        return null;
    }
    
    public AttributeSet getArea(final int n, final int n2, final int n3, final int n4) {
        final int n5 = (this.areaAttributes != null) ? this.areaAttributes.size() : 0;
        if (n5 > 0) {
            final int n6 = (this.areas != null) ? this.areas.size() : 0;
            if (this.areas == null) {
                this.areas = new Vector<RegionContainment>(n5);
            }
            for (int i = 0; i < n5; ++i) {
                if (i >= n6) {
                    this.areas.addElement(this.createRegionContainment(this.areaAttributes.elementAt(i)));
                }
                final RegionContainment regionContainment = this.areas.elementAt(i);
                if (regionContainment != null && regionContainment.contains(n, n2, n3, n4)) {
                    return this.areaAttributes.elementAt(i);
                }
            }
        }
        return null;
    }
    
    protected RegionContainment createRegionContainment(final AttributeSet set) {
        Object attribute = set.getAttribute(HTML.Attribute.SHAPE);
        if (attribute == null) {
            attribute = "rect";
        }
        if (attribute instanceof String) {
            final String lowerCase = ((String)attribute).toLowerCase();
            RegionContainment sharedInstance = null;
            try {
                if (lowerCase.equals("rect")) {
                    sharedInstance = new RectangleRegionContainment(set);
                }
                else if (lowerCase.equals("circle")) {
                    sharedInstance = new CircleRegionContainment(set);
                }
                else if (lowerCase.equals("poly")) {
                    sharedInstance = new PolygonRegionContainment(set);
                }
                else if (lowerCase.equals("default")) {
                    sharedInstance = DefaultRegionContainment.sharedInstance();
                }
            }
            catch (final RuntimeException ex) {
                sharedInstance = null;
            }
            return sharedInstance;
        }
        return null;
    }
    
    protected static int[] extractCoords(final Object o) {
        if (o == null || !(o instanceof String)) {
            return null;
        }
        final StringTokenizer stringTokenizer = new StringTokenizer((String)o, ", \t\n\r");
        int[] array = null;
        int n = 0;
        while (stringTokenizer.hasMoreElements()) {
            String s = stringTokenizer.nextToken();
            int n2;
            if (s.endsWith("%")) {
                n2 = -1;
                s = s.substring(0, s.length() - 1);
            }
            else {
                n2 = 1;
            }
            try {
                final int int1 = Integer.parseInt(s);
                if (array == null) {
                    array = new int[4];
                }
                else if (n == array.length) {
                    final int[] array2 = new int[array.length * 2];
                    System.arraycopy(array, 0, array2, 0, array.length);
                    array = array2;
                }
                array[n++] = int1 * n2;
            }
            catch (final NumberFormatException ex) {
                return null;
            }
        }
        if (n > 0 && n != array.length) {
            final int[] array3 = new int[n];
            System.arraycopy(array, 0, array3, 0, n);
            array = array3;
        }
        return array;
    }
    
    static class RectangleRegionContainment implements RegionContainment
    {
        float[] percents;
        int lastWidth;
        int lastHeight;
        int x0;
        int y0;
        int x1;
        int y1;
        
        public RectangleRegionContainment(final AttributeSet set) {
            final int[] coords = Map.extractCoords(set.getAttribute(HTML.Attribute.COORDS));
            this.percents = null;
            if (coords == null || coords.length != 4) {
                throw new RuntimeException("Unable to parse rectangular area");
            }
            this.x0 = coords[0];
            this.y0 = coords[1];
            this.x1 = coords[2];
            this.y1 = coords[3];
            if (this.x0 < 0 || this.y0 < 0 || this.x1 < 0 || this.y1 < 0) {
                this.percents = new float[4];
                final int n = -1;
                this.lastHeight = n;
                this.lastWidth = n;
                for (int i = 0; i < 4; ++i) {
                    if (coords[i] < 0) {
                        this.percents[i] = Math.abs(coords[i]) / 100.0f;
                    }
                    else {
                        this.percents[i] = -1.0f;
                    }
                }
            }
        }
        
        @Override
        public boolean contains(final int n, final int n2, final int lastWidth, final int lastHeight) {
            if (this.percents == null) {
                return this.contains(n, n2);
            }
            if (this.lastWidth != lastWidth || this.lastHeight != lastHeight) {
                this.lastWidth = lastWidth;
                this.lastHeight = lastHeight;
                if (this.percents[0] != -1.0f) {
                    this.x0 = (int)(this.percents[0] * lastWidth);
                }
                if (this.percents[1] != -1.0f) {
                    this.y0 = (int)(this.percents[1] * lastHeight);
                }
                if (this.percents[2] != -1.0f) {
                    this.x1 = (int)(this.percents[2] * lastWidth);
                }
                if (this.percents[3] != -1.0f) {
                    this.y1 = (int)(this.percents[3] * lastHeight);
                }
            }
            return this.contains(n, n2);
        }
        
        public boolean contains(final int n, final int n2) {
            return n >= this.x0 && n <= this.x1 && n2 >= this.y0 && n2 <= this.y1;
        }
    }
    
    static class PolygonRegionContainment extends Polygon implements RegionContainment
    {
        float[] percentValues;
        int[] percentIndexs;
        int lastWidth;
        int lastHeight;
        
        public PolygonRegionContainment(final AttributeSet set) {
            final int[] coords = Map.extractCoords(set.getAttribute(HTML.Attribute.COORDS));
            if (coords == null || coords.length == 0 || coords.length % 2 != 0) {
                throw new RuntimeException("Unable to parse polygon area");
            }
            int n = 0;
            final int n2 = -1;
            this.lastHeight = n2;
            this.lastWidth = n2;
            for (int i = coords.length - 1; i >= 0; --i) {
                if (coords[i] < 0) {
                    ++n;
                }
            }
            if (n > 0) {
                this.percentIndexs = new int[n];
                this.percentValues = new float[n];
                int j = coords.length - 1;
                int n3 = 0;
                while (j >= 0) {
                    if (coords[j] < 0) {
                        this.percentValues[n3] = coords[j] / -100.0f;
                        this.percentIndexs[n3] = j;
                        ++n3;
                    }
                    --j;
                }
            }
            else {
                this.percentIndexs = null;
                this.percentValues = null;
            }
            this.npoints = coords.length / 2;
            this.xpoints = new int[this.npoints];
            this.ypoints = new int[this.npoints];
            for (int k = 0; k < this.npoints; ++k) {
                this.xpoints[k] = coords[k + k];
                this.ypoints[k] = coords[k + k + 1];
            }
        }
        
        @Override
        public boolean contains(final int n, final int n2, final int lastWidth, final int lastHeight) {
            if (this.percentValues == null || (this.lastWidth == lastWidth && this.lastHeight == lastHeight)) {
                return this.contains(n, n2);
            }
            this.bounds = null;
            this.lastWidth = lastWidth;
            this.lastHeight = lastHeight;
            final float n3 = (float)lastWidth;
            final float n4 = (float)lastHeight;
            for (int i = this.percentValues.length - 1; i >= 0; --i) {
                if (this.percentIndexs[i] % 2 == 0) {
                    this.xpoints[this.percentIndexs[i] / 2] = (int)(this.percentValues[i] * n3);
                }
                else {
                    this.ypoints[this.percentIndexs[i] / 2] = (int)(this.percentValues[i] * n4);
                }
            }
            return this.contains(n, n2);
        }
    }
    
    static class CircleRegionContainment implements RegionContainment
    {
        int x;
        int y;
        int radiusSquared;
        float[] percentValues;
        int lastWidth;
        int lastHeight;
        
        public CircleRegionContainment(final AttributeSet set) {
            final int[] coords = Map.extractCoords(set.getAttribute(HTML.Attribute.COORDS));
            if (coords == null || coords.length != 3) {
                throw new RuntimeException("Unable to parse circular area");
            }
            this.x = coords[0];
            this.y = coords[1];
            this.radiusSquared = coords[2] * coords[2];
            if (coords[0] < 0 || coords[1] < 0 || coords[2] < 0) {
                final int n = -1;
                this.lastHeight = n;
                this.lastWidth = n;
                this.percentValues = new float[3];
                for (int i = 0; i < 3; ++i) {
                    if (coords[i] < 0) {
                        this.percentValues[i] = coords[i] / -100.0f;
                    }
                    else {
                        this.percentValues[i] = -1.0f;
                    }
                }
            }
            else {
                this.percentValues = null;
            }
        }
        
        @Override
        public boolean contains(final int n, final int n2, final int lastWidth, final int lastHeight) {
            if (this.percentValues != null && (this.lastWidth != lastWidth || this.lastHeight != lastHeight)) {
                final int n3 = Math.min(lastWidth, lastHeight) / 2;
                this.lastWidth = lastWidth;
                this.lastHeight = lastHeight;
                if (this.percentValues[0] != -1.0f) {
                    this.x = (int)(this.percentValues[0] * lastWidth);
                }
                if (this.percentValues[1] != -1.0f) {
                    this.y = (int)(this.percentValues[1] * lastHeight);
                }
                if (this.percentValues[2] != -1.0f) {
                    this.radiusSquared = (int)(this.percentValues[2] * Math.min(lastWidth, lastHeight));
                    this.radiusSquared *= this.radiusSquared;
                }
            }
            return (n - this.x) * (n - this.x) + (n2 - this.y) * (n2 - this.y) <= this.radiusSquared;
        }
    }
    
    static class DefaultRegionContainment implements RegionContainment
    {
        static DefaultRegionContainment si;
        
        public static DefaultRegionContainment sharedInstance() {
            if (DefaultRegionContainment.si == null) {
                DefaultRegionContainment.si = new DefaultRegionContainment();
            }
            return DefaultRegionContainment.si;
        }
        
        @Override
        public boolean contains(final int n, final int n2, final int n3, final int n4) {
            return n <= n3 && n >= 0 && n2 >= 0 && n2 <= n3;
        }
        
        static {
            DefaultRegionContainment.si = null;
        }
    }
    
    interface RegionContainment
    {
        boolean contains(final int p0, final int p1, final int p2, final int p3);
    }
}
