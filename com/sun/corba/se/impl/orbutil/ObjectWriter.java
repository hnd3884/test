package com.sun.corba.se.impl.orbutil;

import java.util.Arrays;

public abstract class ObjectWriter
{
    protected StringBuffer result;
    
    public static ObjectWriter make(final boolean b, final int n, final int n2) {
        if (b) {
            return new IndentingObjectWriter(n, n2);
        }
        return new SimpleObjectWriter();
    }
    
    public abstract void startObject(final Object p0);
    
    public abstract void startElement();
    
    public abstract void endElement();
    
    public abstract void endObject(final String p0);
    
    public abstract void endObject();
    
    @Override
    public String toString() {
        return this.result.toString();
    }
    
    public void append(final boolean b) {
        this.result.append(b);
    }
    
    public void append(final char c) {
        this.result.append(c);
    }
    
    public void append(final short n) {
        this.result.append(n);
    }
    
    public void append(final int n) {
        this.result.append(n);
    }
    
    public void append(final long n) {
        this.result.append(n);
    }
    
    public void append(final float n) {
        this.result.append(n);
    }
    
    public void append(final double n) {
        this.result.append(n);
    }
    
    public void append(final String s) {
        this.result.append(s);
    }
    
    protected ObjectWriter() {
        this.result = new StringBuffer();
    }
    
    protected void appendObjectHeader(final Object o) {
        this.result.append(o.getClass().getName());
        this.result.append("<");
        this.result.append(System.identityHashCode(o));
        this.result.append(">");
        final Class<?> componentType = o.getClass().getComponentType();
        if (componentType != null) {
            this.result.append("[");
            if (componentType == Boolean.TYPE) {
                this.result.append(((boolean[])o).length);
                this.result.append("]");
            }
            else if (componentType == Byte.TYPE) {
                this.result.append(((byte[])o).length);
                this.result.append("]");
            }
            else if (componentType == Short.TYPE) {
                this.result.append(((short[])o).length);
                this.result.append("]");
            }
            else if (componentType == Integer.TYPE) {
                this.result.append(((int[])o).length);
                this.result.append("]");
            }
            else if (componentType == Long.TYPE) {
                this.result.append(((long[])o).length);
                this.result.append("]");
            }
            else if (componentType == Character.TYPE) {
                this.result.append(((char[])o).length);
                this.result.append("]");
            }
            else if (componentType == Float.TYPE) {
                this.result.append(((float[])o).length);
                this.result.append("]");
            }
            else if (componentType == Double.TYPE) {
                this.result.append(((double[])o).length);
                this.result.append("]");
            }
            else {
                this.result.append(((Object[])o).length);
                this.result.append("]");
            }
        }
        this.result.append("(");
    }
    
    private static class IndentingObjectWriter extends ObjectWriter
    {
        private int level;
        private int increment;
        
        public IndentingObjectWriter(final int level, final int increment) {
            this.level = level;
            this.increment = increment;
            this.startLine();
        }
        
        private void startLine() {
            final char[] array = new char[this.level * this.increment];
            Arrays.fill(array, ' ');
            this.result.append(array);
        }
        
        @Override
        public void startObject(final Object o) {
            this.appendObjectHeader(o);
            ++this.level;
        }
        
        @Override
        public void startElement() {
            this.result.append("\n");
            this.startLine();
        }
        
        @Override
        public void endElement() {
        }
        
        @Override
        public void endObject(final String s) {
            --this.level;
            this.result.append(s);
            this.result.append(")");
        }
        
        @Override
        public void endObject() {
            --this.level;
            this.result.append("\n");
            this.startLine();
            this.result.append(")");
        }
    }
    
    private static class SimpleObjectWriter extends ObjectWriter
    {
        @Override
        public void startObject(final Object o) {
            this.appendObjectHeader(o);
            this.result.append(" ");
        }
        
        @Override
        public void startElement() {
            this.result.append(" ");
        }
        
        @Override
        public void endObject(final String s) {
            this.result.append(s);
            this.result.append(")");
        }
        
        @Override
        public void endElement() {
        }
        
        @Override
        public void endObject() {
            this.result.append(")");
        }
    }
}
