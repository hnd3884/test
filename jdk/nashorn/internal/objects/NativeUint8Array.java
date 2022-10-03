package jdk.nashorn.internal.objects;

import jdk.nashorn.internal.codegen.CompilerConstants;
import java.lang.invoke.MethodHandles;
import jdk.nashorn.internal.runtime.JSType;
import jdk.nashorn.internal.runtime.arrays.ArrayData;
import java.lang.invoke.MethodHandle;
import jdk.nashorn.internal.runtime.Property;
import java.util.Collection;
import java.util.Collections;
import jdk.nashorn.internal.runtime.arrays.TypedArrayData;
import java.nio.ByteBuffer;
import jdk.nashorn.internal.runtime.ScriptObject;
import jdk.nashorn.internal.runtime.PropertyMap;

public final class NativeUint8Array extends ArrayBufferView
{
    public static final int BYTES_PER_ELEMENT = 1;
    private static PropertyMap $nasgenmap$;
    private static final Factory FACTORY;
    
    public static NativeUint8Array constructor(final boolean newObj, final Object self, final Object... args) {
        return (NativeUint8Array)ArrayBufferView.constructorImpl(newObj, args, NativeUint8Array.FACTORY);
    }
    
    NativeUint8Array(final NativeArrayBuffer buffer, final int byteOffset, final int length) {
        super(buffer, byteOffset, length);
    }
    
    @Override
    protected Factory factory() {
        return NativeUint8Array.FACTORY;
    }
    
    protected static Object set(final Object self, final Object array, final Object offset) {
        return ArrayBufferView.setImpl(self, array, offset);
    }
    
    protected static NativeUint8Array subarray(final Object self, final Object begin, final Object end) {
        return (NativeUint8Array)ArrayBufferView.subarrayImpl(self, begin, end);
    }
    
    @Override
    protected ScriptObject getPrototype(final Global global) {
        return global.getUint8ArrayPrototype();
    }
    
    static {
        FACTORY = new Factory(1) {
            @Override
            public ArrayBufferView construct(final NativeArrayBuffer buffer, final int byteOffset, final int length) {
                return new NativeUint8Array(buffer, byteOffset, length);
            }
            
            @Override
            public Uint8ArrayData createArrayData(final ByteBuffer nb, final int start, final int end) {
                return new Uint8ArrayData(nb, start, end);
            }
            
            @Override
            public String getClassName() {
                return "Uint8Array";
            }
        };
        $clinit$();
    }
    
    public static void $clinit$() {
        NativeUint8Array.$nasgenmap$ = PropertyMap.newMap(Collections.EMPTY_LIST);
    }
    
    private static final class Uint8ArrayData extends TypedArrayData<ByteBuffer>
    {
        private static final MethodHandle GET_ELEM;
        private static final MethodHandle SET_ELEM;
        
        private Uint8ArrayData(final ByteBuffer nb, final int start, final int end) {
            super(((ByteBuffer)nb.position(start).limit(end)).slice(), end - start);
        }
        
        @Override
        protected MethodHandle getGetElem() {
            return Uint8ArrayData.GET_ELEM;
        }
        
        @Override
        protected MethodHandle getSetElem() {
            return Uint8ArrayData.SET_ELEM;
        }
        
        private int getElem(final int index) {
            try {
                return ((ByteBuffer)this.nb).get(index) & 0xFF;
            }
            catch (final IndexOutOfBoundsException e) {
                throw new ClassCastException();
            }
        }
        
        private void setElem(final int index, final int elem) {
            try {
                if (index < this.nb.limit()) {
                    ((ByteBuffer)this.nb).put(index, (byte)elem);
                }
            }
            catch (final IndexOutOfBoundsException e) {
                throw new ClassCastException();
            }
        }
        
        @Override
        public boolean isUnsigned() {
            return true;
        }
        
        @Override
        public Class<?> getElementType() {
            return Integer.TYPE;
        }
        
        @Override
        public Class<?> getBoxedElementType() {
            return Integer.class;
        }
        
        @Override
        public int getInt(final int index) {
            return this.getElem(index);
        }
        
        @Override
        public int getIntOptimistic(final int index, final int programPoint) {
            return this.getElem(index);
        }
        
        @Override
        public double getDouble(final int index) {
            return this.getInt(index);
        }
        
        @Override
        public double getDoubleOptimistic(final int index, final int programPoint) {
            return this.getElem(index);
        }
        
        @Override
        public Object getObject(final int index) {
            return this.getInt(index);
        }
        
        @Override
        public ArrayData set(final int index, final Object value, final boolean strict) {
            return this.set(index, JSType.toInt32(value), strict);
        }
        
        @Override
        public ArrayData set(final int index, final int value, final boolean strict) {
            this.setElem(index, value);
            return this;
        }
        
        @Override
        public ArrayData set(final int index, final double value, final boolean strict) {
            return this.set(index, (int)value, strict);
        }
        
        static {
            GET_ELEM = CompilerConstants.specialCall(MethodHandles.lookup(), Uint8ArrayData.class, "getElem", Integer.TYPE, Integer.TYPE).methodHandle();
            SET_ELEM = CompilerConstants.specialCall(MethodHandles.lookup(), Uint8ArrayData.class, "setElem", Void.TYPE, Integer.TYPE, Integer.TYPE).methodHandle();
        }
    }
}
