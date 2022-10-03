package sun.java2d.loops;

public class GraphicsPrimitiveProxy extends GraphicsPrimitive
{
    private Class owner;
    private String relativeClassName;
    
    public GraphicsPrimitiveProxy(final Class owner, final String relativeClassName, final String s, final int n, final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(s, n, surfaceType, compositeType, surfaceType2);
        this.owner = owner;
        this.relativeClassName = relativeClassName;
    }
    
    @Override
    public GraphicsPrimitive makePrimitive(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        throw new InternalError("makePrimitive called on a Proxy!");
    }
    
    GraphicsPrimitive instantiate() {
        final String string = getPackageName(this.owner.getName()) + "." + this.relativeClassName;
        try {
            final GraphicsPrimitive graphicsPrimitive = (GraphicsPrimitive)Class.forName(string).newInstance();
            if (!this.satisfiesSameAs(graphicsPrimitive)) {
                throw new RuntimeException("Primitive " + graphicsPrimitive + " incompatible with proxy for " + string);
            }
            return graphicsPrimitive;
        }
        catch (final ClassNotFoundException ex) {
            throw new RuntimeException(ex.toString());
        }
        catch (final InstantiationException ex2) {
            throw new RuntimeException(ex2.toString());
        }
        catch (final IllegalAccessException ex3) {
            throw new RuntimeException(ex3.toString());
        }
    }
    
    private static String getPackageName(final String s) {
        final int lastIndex = s.lastIndexOf(46);
        if (lastIndex < 0) {
            return s;
        }
        return s.substring(0, lastIndex);
    }
    
    @Override
    public GraphicsPrimitive traceWrap() {
        return this.instantiate().traceWrap();
    }
}
