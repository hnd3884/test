package sun.java2d.loops;

import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import sun.security.action.GetPropertyAction;
import java.lang.reflect.Field;
import java.awt.Rectangle;
import sun.java2d.pipe.Region;
import java.awt.Composite;
import java.awt.AlphaComposite;
import sun.awt.image.BufImgSurfaceData;
import java.awt.image.BufferedImage;
import sun.java2d.SurfaceData;
import java.io.OutputStream;
import java.security.AccessController;
import java.io.FileNotFoundException;
import java.security.PrivilegedAction;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;

public abstract class GraphicsPrimitive
{
    private String methodSignature;
    private int uniqueID;
    private static int unusedPrimID;
    private SurfaceType sourceType;
    private CompositeType compositeType;
    private SurfaceType destType;
    private long pNativePrim;
    static HashMap traceMap;
    public static int traceflags;
    public static String tracefile;
    public static PrintStream traceout;
    public static final int TRACELOG = 1;
    public static final int TRACETIMESTAMP = 2;
    public static final int TRACECOUNTS = 4;
    private String cachedname;
    
    public static final synchronized int makePrimTypeID() {
        if (GraphicsPrimitive.unusedPrimID > 255) {
            throw new InternalError("primitive id overflow");
        }
        return GraphicsPrimitive.unusedPrimID++;
    }
    
    public static final synchronized int makeUniqueID(final int n, final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        return n << 24 | surfaceType2.getUniqueID() << 16 | compositeType.getUniqueID() << 8 | surfaceType.getUniqueID();
    }
    
    protected GraphicsPrimitive(final String methodSignature, final int n, final SurfaceType sourceType, final CompositeType compositeType, final SurfaceType destType) {
        this.methodSignature = methodSignature;
        this.sourceType = sourceType;
        this.compositeType = compositeType;
        this.destType = destType;
        if (sourceType == null || compositeType == null || destType == null) {
            this.uniqueID = n << 24;
        }
        else {
            this.uniqueID = makeUniqueID(n, sourceType, compositeType, destType);
        }
    }
    
    protected GraphicsPrimitive(final long pNativePrim, final String methodSignature, final int n, final SurfaceType sourceType, final CompositeType compositeType, final SurfaceType destType) {
        this.pNativePrim = pNativePrim;
        this.methodSignature = methodSignature;
        this.sourceType = sourceType;
        this.compositeType = compositeType;
        this.destType = destType;
        if (sourceType == null || compositeType == null || destType == null) {
            this.uniqueID = n << 24;
        }
        else {
            this.uniqueID = makeUniqueID(n, sourceType, compositeType, destType);
        }
    }
    
    public final int getUniqueID() {
        return this.uniqueID;
    }
    
    public final String getSignature() {
        return this.methodSignature;
    }
    
    public final int getPrimTypeID() {
        return this.uniqueID >>> 24;
    }
    
    public final long getNativePrim() {
        return this.pNativePrim;
    }
    
    public final SurfaceType getSourceType() {
        return this.sourceType;
    }
    
    public final CompositeType getCompositeType() {
        return this.compositeType;
    }
    
    public final SurfaceType getDestType() {
        return this.destType;
    }
    
    public final boolean satisfies(final String s, SurfaceType superType, CompositeType superType2, SurfaceType superType3) {
        if (s != this.methodSignature) {
            return false;
        }
        while (superType != null) {
            if (superType.equals(this.sourceType)) {
                while (superType2 != null) {
                    if (superType2.equals(this.compositeType)) {
                        while (superType3 != null) {
                            if (superType3.equals(this.destType)) {
                                return true;
                            }
                            superType3 = superType3.getSuperType();
                        }
                        return false;
                    }
                    superType2 = superType2.getSuperType();
                }
                return false;
            }
            superType = superType.getSuperType();
        }
        return false;
    }
    
    final boolean satisfiesSameAs(final GraphicsPrimitive graphicsPrimitive) {
        return this.methodSignature == graphicsPrimitive.methodSignature && this.sourceType.equals(graphicsPrimitive.sourceType) && this.compositeType.equals(graphicsPrimitive.compositeType) && this.destType.equals(graphicsPrimitive.destType);
    }
    
    public abstract GraphicsPrimitive makePrimitive(final SurfaceType p0, final CompositeType p1, final SurfaceType p2);
    
    public abstract GraphicsPrimitive traceWrap();
    
    public static boolean tracingEnabled() {
        return GraphicsPrimitive.traceflags != 0;
    }
    
    private static PrintStream getTraceOutputFile() {
        if (GraphicsPrimitive.traceout == null) {
            if (GraphicsPrimitive.tracefile != null) {
                final FileOutputStream fileOutputStream = AccessController.doPrivileged((PrivilegedAction<FileOutputStream>)new PrivilegedAction<FileOutputStream>() {
                    @Override
                    public FileOutputStream run() {
                        try {
                            return new FileOutputStream(GraphicsPrimitive.tracefile);
                        }
                        catch (final FileNotFoundException ex) {
                            return null;
                        }
                    }
                });
                if (fileOutputStream != null) {
                    GraphicsPrimitive.traceout = new PrintStream(fileOutputStream);
                }
                else {
                    GraphicsPrimitive.traceout = System.err;
                }
            }
            else {
                GraphicsPrimitive.traceout = System.err;
            }
        }
        return GraphicsPrimitive.traceout;
    }
    
    public static synchronized void tracePrimitive(final Object o) {
        if ((GraphicsPrimitive.traceflags & 0x4) != 0x0) {
            if (GraphicsPrimitive.traceMap == null) {
                GraphicsPrimitive.traceMap = new HashMap();
                TraceReporter.setShutdownHook();
            }
            int[] value = GraphicsPrimitive.traceMap.get(o);
            if (value == null) {
                value = new int[] { 0 };
                GraphicsPrimitive.traceMap.put(o, value);
            }
            final int[] array = value;
            final int n = 0;
            ++array[n];
        }
        if ((GraphicsPrimitive.traceflags & 0x1) != 0x0) {
            final PrintStream traceOutputFile = getTraceOutputFile();
            if ((GraphicsPrimitive.traceflags & 0x2) != 0x0) {
                traceOutputFile.print(System.currentTimeMillis() + ": ");
            }
            traceOutputFile.println(o);
        }
    }
    
    protected void setupGeneralBinaryOp(final GeneralBinaryOp generalBinaryOp) {
        final int primTypeID = generalBinaryOp.getPrimTypeID();
        final String signature = generalBinaryOp.getSignature();
        final SurfaceType sourceType = generalBinaryOp.getSourceType();
        final CompositeType compositeType = generalBinaryOp.getCompositeType();
        final SurfaceType destType = generalBinaryOp.getDestType();
        final Blit converter = createConverter(sourceType, SurfaceType.IntArgb);
        GraphicsPrimitive graphicsPrimitive = GraphicsPrimitiveMgr.locatePrim(primTypeID, SurfaceType.IntArgb, compositeType, destType);
        Blit converter2;
        Blit converter3;
        if (graphicsPrimitive != null) {
            converter2 = null;
            converter3 = null;
        }
        else {
            graphicsPrimitive = getGeneralOp(primTypeID, compositeType);
            if (graphicsPrimitive == null) {
                throw new InternalError("Cannot construct general op for " + signature + " " + compositeType);
            }
            converter2 = createConverter(destType, SurfaceType.IntArgb);
            converter3 = createConverter(SurfaceType.IntArgb, destType);
        }
        generalBinaryOp.setPrimitives(converter, converter2, graphicsPrimitive, converter3);
    }
    
    protected void setupGeneralUnaryOp(final GeneralUnaryOp generalUnaryOp) {
        final int primTypeID = generalUnaryOp.getPrimTypeID();
        generalUnaryOp.getSignature();
        final CompositeType compositeType = generalUnaryOp.getCompositeType();
        final SurfaceType destType = generalUnaryOp.getDestType();
        final Blit converter = createConverter(destType, SurfaceType.IntArgb);
        final GraphicsPrimitive generalOp = getGeneralOp(primTypeID, compositeType);
        final Blit converter2 = createConverter(SurfaceType.IntArgb, destType);
        if (converter == null || generalOp == null || converter2 == null) {
            throw new InternalError("Cannot construct binary op for " + compositeType + " " + destType);
        }
        generalUnaryOp.setPrimitives(converter, generalOp, converter2);
    }
    
    protected static Blit createConverter(final SurfaceType surfaceType, final SurfaceType surfaceType2) {
        if (surfaceType.equals(surfaceType2)) {
            return null;
        }
        final Blit fromCache = Blit.getFromCache(surfaceType, CompositeType.SrcNoEa, surfaceType2);
        if (fromCache == null) {
            throw new InternalError("Cannot construct converter for " + surfaceType + "=>" + surfaceType2);
        }
        return fromCache;
    }
    
    protected static SurfaceData convertFrom(final Blit blit, final SurfaceData surfaceData, final int n, final int n2, final int n3, final int n4, final SurfaceData surfaceData2) {
        return convertFrom(blit, surfaceData, n, n2, n3, n4, surfaceData2, 2);
    }
    
    protected static SurfaceData convertFrom(final Blit blit, final SurfaceData surfaceData, final int n, final int n2, final int n3, final int n4, SurfaceData data, final int n5) {
        if (data != null) {
            final Rectangle bounds = data.getBounds();
            if (n3 > bounds.width || n4 > bounds.height) {
                data = null;
            }
        }
        if (data == null) {
            data = BufImgSurfaceData.createData(new BufferedImage(n3, n4, n5));
        }
        blit.Blit(surfaceData, data, AlphaComposite.Src, null, n, n2, 0, 0, n3, n4);
        return data;
    }
    
    protected static void convertTo(final Blit blit, final SurfaceData surfaceData, final SurfaceData surfaceData2, final Region region, final int n, final int n2, final int n3, final int n4) {
        if (blit != null) {
            blit.Blit(surfaceData, surfaceData2, AlphaComposite.Src, region, 0, 0, n, n2, n3, n4);
        }
    }
    
    protected static GraphicsPrimitive getGeneralOp(final int n, final CompositeType compositeType) {
        return GraphicsPrimitiveMgr.locatePrim(n, SurfaceType.IntArgb, compositeType, SurfaceType.IntArgb);
    }
    
    public static String simplename(final Field[] array, final Object o) {
        for (int i = 0; i < array.length; ++i) {
            final Field field = array[i];
            try {
                if (o == field.get(null)) {
                    return field.getName();
                }
            }
            catch (final Exception ex) {}
        }
        return "\"" + o.toString() + "\"";
    }
    
    public static String simplename(final SurfaceType surfaceType) {
        return simplename(SurfaceType.class.getDeclaredFields(), surfaceType);
    }
    
    public static String simplename(final CompositeType compositeType) {
        return simplename(CompositeType.class.getDeclaredFields(), compositeType);
    }
    
    @Override
    public String toString() {
        if (this.cachedname == null) {
            String s = this.methodSignature;
            final int index = s.indexOf(40);
            if (index >= 0) {
                s = s.substring(0, index);
            }
            this.cachedname = this.getClass().getName() + "::" + s + "(" + simplename(this.sourceType) + ", " + simplename(this.compositeType) + ", " + simplename(this.destType) + ")";
        }
        return this.cachedname;
    }
    
    static {
        GraphicsPrimitive.unusedPrimID = 1;
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.java2d.trace"));
        if (s != null) {
            boolean b = false;
            int traceflags = 0;
            final StringTokenizer stringTokenizer = new StringTokenizer(s, ",");
            while (stringTokenizer.hasMoreTokens()) {
                final String nextToken = stringTokenizer.nextToken();
                if (nextToken.equalsIgnoreCase("count")) {
                    traceflags |= 0x4;
                }
                else if (nextToken.equalsIgnoreCase("log")) {
                    traceflags |= 0x1;
                }
                else if (nextToken.equalsIgnoreCase("timestamp")) {
                    traceflags |= 0x2;
                }
                else if (nextToken.equalsIgnoreCase("verbose")) {
                    b = true;
                }
                else if (nextToken.regionMatches(true, 0, "out:", 0, 4)) {
                    GraphicsPrimitive.tracefile = nextToken.substring(4);
                }
                else {
                    if (!nextToken.equalsIgnoreCase("help")) {
                        System.err.println("unrecognized token: " + nextToken);
                    }
                    System.err.println("usage: -Dsun.java2d.trace=[log[,timestamp]],[count],[out:<filename>],[help],[verbose]");
                }
            }
            if (b) {
                System.err.print("GraphicsPrimitive logging ");
                if ((traceflags & 0x1) != 0x0) {
                    System.err.println("enabled");
                    System.err.print("GraphicsPrimitive timetamps ");
                    if ((traceflags & 0x2) != 0x0) {
                        System.err.println("enabled");
                    }
                    else {
                        System.err.println("disabled");
                    }
                }
                else {
                    System.err.println("[and timestamps] disabled");
                }
                System.err.print("GraphicsPrimitive invocation counts ");
                if ((traceflags & 0x4) != 0x0) {
                    System.err.println("enabled");
                }
                else {
                    System.err.println("disabled");
                }
                System.err.print("GraphicsPrimitive trace output to ");
                if (GraphicsPrimitive.tracefile == null) {
                    System.err.println("System.err");
                }
                else {
                    System.err.println("file '" + GraphicsPrimitive.tracefile + "'");
                }
            }
            GraphicsPrimitive.traceflags = traceflags;
        }
    }
    
    public static class TraceReporter extends Thread
    {
        public static void setShutdownHook() {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    final TraceReporter traceReporter = new TraceReporter();
                    traceReporter.setContextClassLoader(null);
                    Runtime.getRuntime().addShutdownHook(traceReporter);
                    return null;
                }
            });
        }
        
        @Override
        public void run() {
            final PrintStream access$000 = getTraceOutputFile();
            final Iterator iterator = GraphicsPrimitive.traceMap.entrySet().iterator();
            long n = 0L;
            int n2 = 0;
            while (iterator.hasNext()) {
                final Map.Entry entry = (Map.Entry)iterator.next();
                final Object key = entry.getKey();
                final int[] array = (int[])entry.getValue();
                if (array[0] == 1) {
                    access$000.print("1 call to ");
                }
                else {
                    access$000.print(array[0] + " calls to ");
                }
                access$000.println(key);
                ++n2;
                n += array[0];
            }
            if (n2 == 0) {
                access$000.println("No graphics primitives executed");
            }
            else if (n2 > 1) {
                access$000.println(n + " total calls to " + n2 + " different primitives");
            }
        }
    }
    
    protected interface GeneralUnaryOp
    {
        void setPrimitives(final Blit p0, final GraphicsPrimitive p1, final Blit p2);
        
        CompositeType getCompositeType();
        
        SurfaceType getDestType();
        
        String getSignature();
        
        int getPrimTypeID();
    }
    
    protected interface GeneralBinaryOp
    {
        void setPrimitives(final Blit p0, final Blit p1, final GraphicsPrimitive p2, final Blit p3);
        
        SurfaceType getSourceType();
        
        CompositeType getCompositeType();
        
        SurfaceType getDestType();
        
        String getSignature();
        
        int getPrimTypeID();
    }
}
