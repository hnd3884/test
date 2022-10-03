package java.awt.image.renderable;

import java.awt.image.RenderedImage;
import java.util.Vector;
import java.io.Serializable;

public class ParameterBlock implements Cloneable, Serializable
{
    protected Vector<Object> sources;
    protected Vector<Object> parameters;
    
    public ParameterBlock() {
        this.sources = new Vector<Object>();
        this.parameters = new Vector<Object>();
    }
    
    public ParameterBlock(final Vector<Object> sources) {
        this.sources = new Vector<Object>();
        this.parameters = new Vector<Object>();
        this.setSources(sources);
    }
    
    public ParameterBlock(final Vector<Object> sources, final Vector<Object> parameters) {
        this.sources = new Vector<Object>();
        this.parameters = new Vector<Object>();
        this.setSources(sources);
        this.setParameters(parameters);
    }
    
    public Object shallowClone() {
        try {
            return super.clone();
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    public Object clone() {
        ParameterBlock parameterBlock;
        try {
            parameterBlock = (ParameterBlock)super.clone();
        }
        catch (final Exception ex) {
            return null;
        }
        if (this.sources != null) {
            parameterBlock.setSources((Vector<Object>)this.sources.clone());
        }
        if (this.parameters != null) {
            parameterBlock.setParameters((Vector<Object>)this.parameters.clone());
        }
        return parameterBlock;
    }
    
    public ParameterBlock addSource(final Object o) {
        this.sources.addElement(o);
        return this;
    }
    
    public Object getSource(final int n) {
        return this.sources.elementAt(n);
    }
    
    public ParameterBlock setSource(final Object o, final int n) {
        final int size = this.sources.size();
        final int size2 = n + 1;
        if (size < size2) {
            this.sources.setSize(size2);
        }
        this.sources.setElementAt(o, n);
        return this;
    }
    
    public RenderedImage getRenderedSource(final int n) {
        return this.sources.elementAt(n);
    }
    
    public RenderableImage getRenderableSource(final int n) {
        return this.sources.elementAt(n);
    }
    
    public int getNumSources() {
        return this.sources.size();
    }
    
    public Vector<Object> getSources() {
        return this.sources;
    }
    
    public void setSources(final Vector<Object> sources) {
        this.sources = sources;
    }
    
    public void removeSources() {
        this.sources = new Vector<Object>();
    }
    
    public int getNumParameters() {
        return this.parameters.size();
    }
    
    public Vector<Object> getParameters() {
        return this.parameters;
    }
    
    public void setParameters(final Vector<Object> parameters) {
        this.parameters = parameters;
    }
    
    public void removeParameters() {
        this.parameters = new Vector<Object>();
    }
    
    public ParameterBlock add(final Object o) {
        this.parameters.addElement(o);
        return this;
    }
    
    public ParameterBlock add(final byte b) {
        return this.add(new Byte(b));
    }
    
    public ParameterBlock add(final char c) {
        return this.add(new Character(c));
    }
    
    public ParameterBlock add(final short n) {
        return this.add(new Short(n));
    }
    
    public ParameterBlock add(final int n) {
        return this.add(new Integer(n));
    }
    
    public ParameterBlock add(final long n) {
        return this.add(new Long(n));
    }
    
    public ParameterBlock add(final float n) {
        return this.add(new Float(n));
    }
    
    public ParameterBlock add(final double n) {
        return this.add(new Double(n));
    }
    
    public ParameterBlock set(final Object o, final int n) {
        final int size = this.parameters.size();
        final int size2 = n + 1;
        if (size < size2) {
            this.parameters.setSize(size2);
        }
        this.parameters.setElementAt(o, n);
        return this;
    }
    
    public ParameterBlock set(final byte b, final int n) {
        return this.set(new Byte(b), n);
    }
    
    public ParameterBlock set(final char c, final int n) {
        return this.set(new Character(c), n);
    }
    
    public ParameterBlock set(final short n, final int n2) {
        return this.set(new Short(n), n2);
    }
    
    public ParameterBlock set(final int n, final int n2) {
        return this.set(new Integer(n), n2);
    }
    
    public ParameterBlock set(final long n, final int n2) {
        return this.set(new Long(n), n2);
    }
    
    public ParameterBlock set(final float n, final int n2) {
        return this.set(new Float(n), n2);
    }
    
    public ParameterBlock set(final double n, final int n2) {
        return this.set(new Double(n), n2);
    }
    
    public Object getObjectParameter(final int n) {
        return this.parameters.elementAt(n);
    }
    
    public byte getByteParameter(final int n) {
        return this.parameters.elementAt(n);
    }
    
    public char getCharParameter(final int n) {
        return this.parameters.elementAt(n);
    }
    
    public short getShortParameter(final int n) {
        return this.parameters.elementAt(n);
    }
    
    public int getIntParameter(final int n) {
        return this.parameters.elementAt(n);
    }
    
    public long getLongParameter(final int n) {
        return this.parameters.elementAt(n);
    }
    
    public float getFloatParameter(final int n) {
        return this.parameters.elementAt(n);
    }
    
    public double getDoubleParameter(final int n) {
        return this.parameters.elementAt(n);
    }
    
    public Class[] getParamClasses() {
        final int numParameters = this.getNumParameters();
        final Class[] array = new Class[numParameters];
        for (int i = 0; i < numParameters; ++i) {
            final Object objectParameter = this.getObjectParameter(i);
            if (objectParameter instanceof Byte) {
                array[i] = Byte.TYPE;
            }
            else if (objectParameter instanceof Character) {
                array[i] = Character.TYPE;
            }
            else if (objectParameter instanceof Short) {
                array[i] = Short.TYPE;
            }
            else if (objectParameter instanceof Integer) {
                array[i] = Integer.TYPE;
            }
            else if (objectParameter instanceof Long) {
                array[i] = Long.TYPE;
            }
            else if (objectParameter instanceof Float) {
                array[i] = Float.TYPE;
            }
            else if (objectParameter instanceof Double) {
                array[i] = Double.TYPE;
            }
            else {
                array[i] = objectParameter.getClass();
            }
        }
        return array;
    }
}
