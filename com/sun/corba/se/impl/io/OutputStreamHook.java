package com.sun.corba.se.impl.io;

import org.omg.CORBA.portable.ValueOutputStream;
import java.util.HashMap;
import java.util.Map;
import org.omg.CORBA_2_3.portable.OutputStream;
import java.io.NotActiveException;
import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectOutputStream;

public abstract class OutputStreamHook extends ObjectOutputStream
{
    private HookPutFields putFields;
    protected byte streamFormatVersion;
    protected WriteObjectState writeObjectState;
    protected static final WriteObjectState NOT_IN_WRITE_OBJECT;
    protected static final WriteObjectState IN_WRITE_OBJECT;
    protected static final WriteObjectState WROTE_DEFAULT_DATA;
    protected static final WriteObjectState WROTE_CUSTOM_DATA;
    
    abstract void writeField(final ObjectStreamField p0, final Object p1) throws IOException;
    
    public OutputStreamHook() throws IOException {
        this.putFields = null;
        this.streamFormatVersion = 1;
        this.writeObjectState = OutputStreamHook.NOT_IN_WRITE_OBJECT;
    }
    
    @Override
    public void defaultWriteObject() throws IOException {
        this.writeObjectState.defaultWriteObject(this);
        this.defaultWriteObjectDelegate();
    }
    
    public abstract void defaultWriteObjectDelegate();
    
    @Override
    public PutField putFields() throws IOException {
        if (this.putFields == null) {
            this.putFields = new HookPutFields();
        }
        return this.putFields;
    }
    
    public byte getStreamFormatVersion() {
        return this.streamFormatVersion;
    }
    
    abstract ObjectStreamField[] getFieldsNoCopy();
    
    @Override
    public void writeFields() throws IOException {
        this.writeObjectState.defaultWriteObject(this);
        if (this.putFields != null) {
            this.putFields.write(this);
            return;
        }
        throw new NotActiveException("no current PutField object");
    }
    
    abstract org.omg.CORBA_2_3.portable.OutputStream getOrbStream();
    
    protected abstract void beginOptionalCustomData();
    
    protected void setState(final WriteObjectState writeObjectState) {
        this.writeObjectState = writeObjectState;
    }
    
    static {
        NOT_IN_WRITE_OBJECT = new DefaultState();
        IN_WRITE_OBJECT = new InWriteObjectState();
        WROTE_DEFAULT_DATA = new WroteDefaultDataState();
        WROTE_CUSTOM_DATA = new WroteCustomDataState();
    }
    
    private class HookPutFields extends PutField
    {
        private Map<String, Object> fields;
        
        private HookPutFields() {
            this.fields = new HashMap<String, Object>();
        }
        
        @Override
        public void put(final String s, final boolean b) {
            this.fields.put(s, new Boolean(b));
        }
        
        @Override
        public void put(final String s, final char c) {
            this.fields.put(s, new Character(c));
        }
        
        @Override
        public void put(final String s, final byte b) {
            this.fields.put(s, new Byte(b));
        }
        
        @Override
        public void put(final String s, final short n) {
            this.fields.put(s, new Short(n));
        }
        
        @Override
        public void put(final String s, final int n) {
            this.fields.put(s, new Integer(n));
        }
        
        @Override
        public void put(final String s, final long n) {
            this.fields.put(s, new Long(n));
        }
        
        @Override
        public void put(final String s, final float n) {
            this.fields.put(s, new Float(n));
        }
        
        @Override
        public void put(final String s, final double n) {
            this.fields.put(s, new Double(n));
        }
        
        @Override
        public void put(final String s, final Object o) {
            this.fields.put(s, o);
        }
        
        @Override
        public void write(final ObjectOutput objectOutput) throws IOException {
            final OutputStreamHook outputStreamHook = (OutputStreamHook)objectOutput;
            final ObjectStreamField[] fieldsNoCopy = outputStreamHook.getFieldsNoCopy();
            for (int i = 0; i < fieldsNoCopy.length; ++i) {
                outputStreamHook.writeField(fieldsNoCopy[i], this.fields.get(fieldsNoCopy[i].getName()));
            }
        }
    }
    
    protected static class WriteObjectState
    {
        public void enterWriteObject(final OutputStreamHook outputStreamHook) throws IOException {
        }
        
        public void exitWriteObject(final OutputStreamHook outputStreamHook) throws IOException {
        }
        
        public void defaultWriteObject(final OutputStreamHook outputStreamHook) throws IOException {
        }
        
        public void writeData(final OutputStreamHook outputStreamHook) throws IOException {
        }
    }
    
    protected static class DefaultState extends WriteObjectState
    {
        @Override
        public void enterWriteObject(final OutputStreamHook outputStreamHook) throws IOException {
            outputStreamHook.setState(OutputStreamHook.IN_WRITE_OBJECT);
        }
    }
    
    protected static class InWriteObjectState extends WriteObjectState
    {
        @Override
        public void enterWriteObject(final OutputStreamHook outputStreamHook) throws IOException {
            throw new IOException("Internal state failure: Entered writeObject twice");
        }
        
        @Override
        public void exitWriteObject(final OutputStreamHook outputStreamHook) throws IOException {
            outputStreamHook.getOrbStream().write_boolean(false);
            if (outputStreamHook.getStreamFormatVersion() == 2) {
                outputStreamHook.getOrbStream().write_long(0);
            }
            outputStreamHook.setState(OutputStreamHook.NOT_IN_WRITE_OBJECT);
        }
        
        @Override
        public void defaultWriteObject(final OutputStreamHook outputStreamHook) throws IOException {
            outputStreamHook.getOrbStream().write_boolean(true);
            outputStreamHook.setState(OutputStreamHook.WROTE_DEFAULT_DATA);
        }
        
        @Override
        public void writeData(final OutputStreamHook outputStreamHook) throws IOException {
            outputStreamHook.getOrbStream().write_boolean(false);
            outputStreamHook.beginOptionalCustomData();
            outputStreamHook.setState(OutputStreamHook.WROTE_CUSTOM_DATA);
        }
    }
    
    protected static class WroteDefaultDataState extends InWriteObjectState
    {
        @Override
        public void exitWriteObject(final OutputStreamHook outputStreamHook) throws IOException {
            if (outputStreamHook.getStreamFormatVersion() == 2) {
                outputStreamHook.getOrbStream().write_long(0);
            }
            outputStreamHook.setState(OutputStreamHook.NOT_IN_WRITE_OBJECT);
        }
        
        @Override
        public void defaultWriteObject(final OutputStreamHook outputStreamHook) throws IOException {
            throw new IOException("Called defaultWriteObject/writeFields twice");
        }
        
        @Override
        public void writeData(final OutputStreamHook outputStreamHook) throws IOException {
            outputStreamHook.beginOptionalCustomData();
            outputStreamHook.setState(OutputStreamHook.WROTE_CUSTOM_DATA);
        }
    }
    
    protected static class WroteCustomDataState extends InWriteObjectState
    {
        @Override
        public void exitWriteObject(final OutputStreamHook outputStreamHook) throws IOException {
            if (outputStreamHook.getStreamFormatVersion() == 2) {
                ((ValueOutputStream)outputStreamHook.getOrbStream()).end_value();
            }
            outputStreamHook.setState(OutputStreamHook.NOT_IN_WRITE_OBJECT);
        }
        
        @Override
        public void defaultWriteObject(final OutputStreamHook outputStreamHook) throws IOException {
            throw new IOException("Cannot call defaultWriteObject/writeFields after writing custom data in RMI-IIOP");
        }
        
        @Override
        public void writeData(final OutputStreamHook outputStreamHook) throws IOException {
        }
    }
}
