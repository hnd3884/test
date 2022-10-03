package com.sun.corba.se.impl.io;

import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.portable.ValueInputStream;
import java.io.ObjectStreamClass;
import org.omg.CORBA_2_3.portable.InputStream;
import java.util.HashMap;
import java.io.StreamCorruptedException;
import java.io.InvalidClassException;
import java.util.Map;
import java.io.NotActiveException;
import java.io.IOException;
import com.sun.corba.se.impl.logging.UtilSystemException;
import com.sun.corba.se.impl.logging.OMGSystemException;
import java.io.ObjectInputStream;

public abstract class InputStreamHook extends ObjectInputStream
{
    static final OMGSystemException omgWrapper;
    static final UtilSystemException utilWrapper;
    protected ReadObjectState readObjectState;
    protected static final ReadObjectState DEFAULT_STATE;
    protected static final ReadObjectState IN_READ_OBJECT_OPT_DATA;
    protected static final ReadObjectState IN_READ_OBJECT_NO_MORE_OPT_DATA;
    protected static final ReadObjectState IN_READ_OBJECT_DEFAULTS_SENT;
    protected static final ReadObjectState NO_READ_OBJECT_DEFAULTS_SENT;
    protected static final ReadObjectState IN_READ_OBJECT_REMOTE_NOT_CUSTOM_MARSHALED;
    protected static final ReadObjectState IN_READ_OBJECT_PAST_DEFAULTS_REMOTE_NOT_CUSTOM;
    
    public InputStreamHook() throws IOException {
        this.readObjectState = InputStreamHook.DEFAULT_STATE;
    }
    
    @Override
    public void defaultReadObject() throws IOException, ClassNotFoundException, NotActiveException {
        this.readObjectState.beginDefaultReadObject(this);
        this.defaultReadObjectDelegate();
        this.readObjectState.endDefaultReadObject(this);
    }
    
    abstract void defaultReadObjectDelegate();
    
    abstract void readFields(final Map p0) throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IOException;
    
    @Override
    public GetField readFields() throws IOException, ClassNotFoundException, NotActiveException {
        final HashMap hashMap = new HashMap();
        this.readFields(hashMap);
        this.readObjectState.endDefaultReadObject(this);
        return new HookGetFields(hashMap);
    }
    
    protected void setState(final ReadObjectState readObjectState) {
        this.readObjectState = readObjectState;
    }
    
    protected abstract byte getStreamFormatVersion();
    
    abstract org.omg.CORBA_2_3.portable.InputStream getOrbStream();
    
    protected void throwOptionalDataIncompatibleException() {
        throw InputStreamHook.omgWrapper.rmiiiopOptionalDataIncompatible2();
    }
    
    static {
        omgWrapper = OMGSystemException.get("rpc.encoding");
        utilWrapper = UtilSystemException.get("rpc.encoding");
        DEFAULT_STATE = new DefaultState();
        IN_READ_OBJECT_OPT_DATA = new InReadObjectOptionalDataState();
        IN_READ_OBJECT_NO_MORE_OPT_DATA = new InReadObjectNoMoreOptionalDataState();
        IN_READ_OBJECT_DEFAULTS_SENT = new InReadObjectDefaultsSentState();
        NO_READ_OBJECT_DEFAULTS_SENT = new NoReadObjectDefaultsSentState();
        IN_READ_OBJECT_REMOTE_NOT_CUSTOM_MARSHALED = new InReadObjectRemoteDidNotUseWriteObjectState();
        IN_READ_OBJECT_PAST_DEFAULTS_REMOTE_NOT_CUSTOM = new InReadObjectPastDefaultsRemoteDidNotUseWOState();
    }
    
    private class HookGetFields extends GetField
    {
        private Map fields;
        
        HookGetFields(final Map fields) {
            this.fields = null;
            this.fields = fields;
        }
        
        @Override
        public ObjectStreamClass getObjectStreamClass() {
            return null;
        }
        
        @Override
        public boolean defaulted(final String s) throws IOException, IllegalArgumentException {
            return !this.fields.containsKey(s);
        }
        
        @Override
        public boolean get(final String s, final boolean b) throws IOException, IllegalArgumentException {
            if (this.defaulted(s)) {
                return b;
            }
            return this.fields.get(s);
        }
        
        @Override
        public char get(final String s, final char c) throws IOException, IllegalArgumentException {
            if (this.defaulted(s)) {
                return c;
            }
            return this.fields.get(s);
        }
        
        @Override
        public byte get(final String s, final byte b) throws IOException, IllegalArgumentException {
            if (this.defaulted(s)) {
                return b;
            }
            return this.fields.get(s);
        }
        
        @Override
        public short get(final String s, final short n) throws IOException, IllegalArgumentException {
            if (this.defaulted(s)) {
                return n;
            }
            return this.fields.get(s);
        }
        
        @Override
        public int get(final String s, final int n) throws IOException, IllegalArgumentException {
            if (this.defaulted(s)) {
                return n;
            }
            return this.fields.get(s);
        }
        
        @Override
        public long get(final String s, final long n) throws IOException, IllegalArgumentException {
            if (this.defaulted(s)) {
                return n;
            }
            return this.fields.get(s);
        }
        
        @Override
        public float get(final String s, final float n) throws IOException, IllegalArgumentException {
            if (this.defaulted(s)) {
                return n;
            }
            return this.fields.get(s);
        }
        
        @Override
        public double get(final String s, final double n) throws IOException, IllegalArgumentException {
            if (this.defaulted(s)) {
                return n;
            }
            return this.fields.get(s);
        }
        
        @Override
        public Object get(final String s, final Object o) throws IOException, IllegalArgumentException {
            if (this.defaulted(s)) {
                return o;
            }
            return this.fields.get(s);
        }
        
        @Override
        public String toString() {
            return this.fields.toString();
        }
    }
    
    protected static class ReadObjectState
    {
        public void beginUnmarshalCustomValue(final InputStreamHook inputStreamHook, final boolean b, final boolean b2) throws IOException {
        }
        
        public void endUnmarshalCustomValue(final InputStreamHook inputStreamHook) throws IOException {
        }
        
        public void beginDefaultReadObject(final InputStreamHook inputStreamHook) throws IOException {
        }
        
        public void endDefaultReadObject(final InputStreamHook inputStreamHook) throws IOException {
        }
        
        public void readData(final InputStreamHook inputStreamHook) throws IOException {
        }
    }
    
    protected static class DefaultState extends ReadObjectState
    {
        @Override
        public void beginUnmarshalCustomValue(final InputStreamHook inputStreamHook, final boolean b, final boolean b2) throws IOException {
            if (b2) {
                if (b) {
                    inputStreamHook.setState(InputStreamHook.IN_READ_OBJECT_DEFAULTS_SENT);
                }
                else {
                    try {
                        if (inputStreamHook.getStreamFormatVersion() == 2) {
                            ((ValueInputStream)inputStreamHook.getOrbStream()).start_value();
                        }
                    }
                    catch (final Exception ex) {}
                    inputStreamHook.setState(InputStreamHook.IN_READ_OBJECT_OPT_DATA);
                }
            }
            else {
                if (!b) {
                    throw new StreamCorruptedException("No default data sent");
                }
                inputStreamHook.setState(InputStreamHook.NO_READ_OBJECT_DEFAULTS_SENT);
            }
        }
    }
    
    protected static class InReadObjectRemoteDidNotUseWriteObjectState extends ReadObjectState
    {
        @Override
        public void beginUnmarshalCustomValue(final InputStreamHook inputStreamHook, final boolean b, final boolean b2) {
            throw InputStreamHook.utilWrapper.badBeginUnmarshalCustomValue();
        }
        
        @Override
        public void endDefaultReadObject(final InputStreamHook inputStreamHook) {
            inputStreamHook.setState(InputStreamHook.IN_READ_OBJECT_PAST_DEFAULTS_REMOTE_NOT_CUSTOM);
        }
        
        @Override
        public void readData(final InputStreamHook inputStreamHook) {
            inputStreamHook.throwOptionalDataIncompatibleException();
        }
    }
    
    protected static class InReadObjectPastDefaultsRemoteDidNotUseWOState extends ReadObjectState
    {
        @Override
        public void beginUnmarshalCustomValue(final InputStreamHook inputStreamHook, final boolean b, final boolean b2) {
            throw InputStreamHook.utilWrapper.badBeginUnmarshalCustomValue();
        }
        
        @Override
        public void beginDefaultReadObject(final InputStreamHook inputStreamHook) throws IOException {
            throw new StreamCorruptedException("Default data already read");
        }
        
        @Override
        public void readData(final InputStreamHook inputStreamHook) {
            inputStreamHook.throwOptionalDataIncompatibleException();
        }
    }
    
    protected static class InReadObjectDefaultsSentState extends ReadObjectState
    {
        @Override
        public void beginUnmarshalCustomValue(final InputStreamHook inputStreamHook, final boolean b, final boolean b2) {
            throw InputStreamHook.utilWrapper.badBeginUnmarshalCustomValue();
        }
        
        @Override
        public void endUnmarshalCustomValue(final InputStreamHook inputStreamHook) {
            if (inputStreamHook.getStreamFormatVersion() == 2) {
                ((ValueInputStream)inputStreamHook.getOrbStream()).start_value();
                ((ValueInputStream)inputStreamHook.getOrbStream()).end_value();
            }
            inputStreamHook.setState(InputStreamHook.DEFAULT_STATE);
        }
        
        @Override
        public void endDefaultReadObject(final InputStreamHook inputStreamHook) throws IOException {
            if (inputStreamHook.getStreamFormatVersion() == 2) {
                ((ValueInputStream)inputStreamHook.getOrbStream()).start_value();
            }
            inputStreamHook.setState(InputStreamHook.IN_READ_OBJECT_OPT_DATA);
        }
        
        @Override
        public void readData(final InputStreamHook inputStreamHook) throws IOException {
            final org.omg.CORBA.ORB orb = inputStreamHook.getOrbStream().orb();
            if (orb == null || !(orb instanceof ORB)) {
                throw new StreamCorruptedException("Default data must be read first");
            }
            final ORBVersion orbVersion = ((ORB)orb).getORBVersion();
            if (ORBVersionFactory.getPEORB().compareTo(orbVersion) <= 0 || orbVersion.equals(ORBVersionFactory.getFOREIGN())) {
                throw new StreamCorruptedException("Default data must be read first");
            }
        }
    }
    
    protected static class InReadObjectOptionalDataState extends ReadObjectState
    {
        @Override
        public void beginUnmarshalCustomValue(final InputStreamHook inputStreamHook, final boolean b, final boolean b2) {
            throw InputStreamHook.utilWrapper.badBeginUnmarshalCustomValue();
        }
        
        @Override
        public void endUnmarshalCustomValue(final InputStreamHook inputStreamHook) throws IOException {
            if (inputStreamHook.getStreamFormatVersion() == 2) {
                ((ValueInputStream)inputStreamHook.getOrbStream()).end_value();
            }
            inputStreamHook.setState(InputStreamHook.DEFAULT_STATE);
        }
        
        @Override
        public void beginDefaultReadObject(final InputStreamHook inputStreamHook) throws IOException {
            throw new StreamCorruptedException("Default data not sent or already read/passed");
        }
    }
    
    protected static class InReadObjectNoMoreOptionalDataState extends InReadObjectOptionalDataState
    {
        @Override
        public void readData(final InputStreamHook inputStreamHook) throws IOException {
            inputStreamHook.throwOptionalDataIncompatibleException();
        }
    }
    
    protected static class NoReadObjectDefaultsSentState extends ReadObjectState
    {
        @Override
        public void endUnmarshalCustomValue(final InputStreamHook inputStreamHook) throws IOException {
            if (inputStreamHook.getStreamFormatVersion() == 2) {
                ((ValueInputStream)inputStreamHook.getOrbStream()).start_value();
                ((ValueInputStream)inputStreamHook.getOrbStream()).end_value();
            }
            inputStreamHook.setState(InputStreamHook.DEFAULT_STATE);
        }
    }
}
