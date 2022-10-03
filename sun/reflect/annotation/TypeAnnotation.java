package sun.reflect.annotation;

import java.lang.annotation.AnnotationFormatError;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.AnnotatedElement;
import java.lang.annotation.Annotation;

public final class TypeAnnotation
{
    private final TypeAnnotationTargetInfo targetInfo;
    private final LocationInfo loc;
    private final Annotation annotation;
    private final AnnotatedElement baseDeclaration;
    
    public TypeAnnotation(final TypeAnnotationTargetInfo targetInfo, final LocationInfo loc, final Annotation annotation, final AnnotatedElement baseDeclaration) {
        this.targetInfo = targetInfo;
        this.loc = loc;
        this.annotation = annotation;
        this.baseDeclaration = baseDeclaration;
    }
    
    public TypeAnnotationTargetInfo getTargetInfo() {
        return this.targetInfo;
    }
    
    public Annotation getAnnotation() {
        return this.annotation;
    }
    
    public AnnotatedElement getBaseDeclaration() {
        return this.baseDeclaration;
    }
    
    public LocationInfo getLocationInfo() {
        return this.loc;
    }
    
    public static List<TypeAnnotation> filter(final TypeAnnotation[] array, final TypeAnnotationTarget typeAnnotationTarget) {
        final ArrayList list = new ArrayList(array.length);
        for (final TypeAnnotation typeAnnotation : array) {
            if (typeAnnotation.getTargetInfo().getTarget() == typeAnnotationTarget) {
                list.add(typeAnnotation);
            }
        }
        list.trimToSize();
        return list;
    }
    
    @Override
    public String toString() {
        return this.annotation.toString() + " with Targetnfo: " + this.targetInfo.toString() + " on base declaration: " + this.baseDeclaration.toString();
    }
    
    public enum TypeAnnotationTarget
    {
        CLASS_TYPE_PARAMETER, 
        METHOD_TYPE_PARAMETER, 
        CLASS_EXTENDS, 
        CLASS_IMPLEMENTS, 
        CLASS_TYPE_PARAMETER_BOUND, 
        METHOD_TYPE_PARAMETER_BOUND, 
        FIELD, 
        METHOD_RETURN, 
        METHOD_RECEIVER, 
        METHOD_FORMAL_PARAMETER, 
        THROWS;
    }
    
    public static final class TypeAnnotationTargetInfo
    {
        private final TypeAnnotationTarget target;
        private final int count;
        private final int secondaryIndex;
        private static final int UNUSED_INDEX = -2;
        
        public TypeAnnotationTargetInfo(final TypeAnnotationTarget typeAnnotationTarget) {
            this(typeAnnotationTarget, -2, -2);
        }
        
        public TypeAnnotationTargetInfo(final TypeAnnotationTarget typeAnnotationTarget, final int n) {
            this(typeAnnotationTarget, n, -2);
        }
        
        public TypeAnnotationTargetInfo(final TypeAnnotationTarget target, final int count, final int secondaryIndex) {
            this.target = target;
            this.count = count;
            this.secondaryIndex = secondaryIndex;
        }
        
        public TypeAnnotationTarget getTarget() {
            return this.target;
        }
        
        public int getCount() {
            return this.count;
        }
        
        public int getSecondaryIndex() {
            return this.secondaryIndex;
        }
        
        @Override
        public String toString() {
            return "" + this.target + ": " + this.count + ", " + this.secondaryIndex;
        }
    }
    
    public static final class LocationInfo
    {
        private final int depth;
        private final Location[] locations;
        public static final LocationInfo BASE_LOCATION;
        
        private LocationInfo() {
            this(0, new Location[0]);
        }
        
        private LocationInfo(final int depth, final Location[] locations) {
            this.depth = depth;
            this.locations = locations;
        }
        
        public static LocationInfo parseLocationInfo(final ByteBuffer byteBuffer) {
            final int n = byteBuffer.get() & 0xFF;
            if (n == 0) {
                return LocationInfo.BASE_LOCATION;
            }
            final Location[] array = new Location[n];
            for (int i = 0; i < n; ++i) {
                final byte value = byteBuffer.get();
                final short n2 = (short)(byteBuffer.get() & 0xFF);
                if (value != 0 && !(value == 1 | value == 2) && value != 3) {
                    throw new AnnotationFormatError("Bad Location encoding in Type Annotation");
                }
                if (value != 3 && n2 != 0) {
                    throw new AnnotationFormatError("Bad Location encoding in Type Annotation");
                }
                array[i] = new Location(value, n2);
            }
            return new LocationInfo(n, array);
        }
        
        public LocationInfo pushArray() {
            return this.pushLocation((byte)0, (short)0);
        }
        
        public LocationInfo pushInner() {
            return this.pushLocation((byte)1, (short)0);
        }
        
        public LocationInfo pushWildcard() {
            return this.pushLocation((byte)2, (short)0);
        }
        
        public LocationInfo pushTypeArg(final short n) {
            return this.pushLocation((byte)3, n);
        }
        
        public LocationInfo pushLocation(final byte b, final short n) {
            final int n2 = this.depth + 1;
            final Location[] array = new Location[n2];
            System.arraycopy(this.locations, 0, array, 0, this.depth);
            array[n2 - 1] = new Location(b, (short)(n & 0xFF));
            return new LocationInfo(n2, array);
        }
        
        public TypeAnnotation[] filter(final TypeAnnotation[] array) {
            final ArrayList list = new ArrayList(array.length);
            for (final TypeAnnotation typeAnnotation : array) {
                if (this.isSameLocationInfo(typeAnnotation.getLocationInfo())) {
                    list.add(typeAnnotation);
                }
            }
            return list.toArray(new TypeAnnotation[0]);
        }
        
        boolean isSameLocationInfo(final LocationInfo locationInfo) {
            if (this.depth != locationInfo.depth) {
                return false;
            }
            for (int i = 0; i < this.depth; ++i) {
                if (!this.locations[i].isSameLocation(locationInfo.locations[i])) {
                    return false;
                }
            }
            return true;
        }
        
        static {
            BASE_LOCATION = new LocationInfo();
        }
        
        public static final class Location
        {
            public final byte tag;
            public final short index;
            
            boolean isSameLocation(final Location location) {
                return this.tag == location.tag && this.index == location.index;
            }
            
            public Location(final byte tag, final short index) {
                this.tag = tag;
                this.index = index;
            }
        }
    }
}
