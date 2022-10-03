package org.msgpack.unpacker;

import java.util.Iterator;
import org.msgpack.template.Template;
import org.msgpack.type.Value;
import org.msgpack.packer.Unconverter;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.msgpack.MessagePack;

public abstract class AbstractUnpacker implements Unpacker
{
    protected MessagePack msgpack;
    protected int rawSizeLimit;
    protected int arraySizeLimit;
    protected int mapSizeLimit;
    
    protected AbstractUnpacker(final MessagePack msgpack) {
        this.rawSizeLimit = 134217728;
        this.arraySizeLimit = 4194304;
        this.mapSizeLimit = 2097152;
        this.msgpack = msgpack;
    }
    
    @Override
    public ByteBuffer readByteBuffer() throws IOException {
        return ByteBuffer.wrap(this.readByteArray());
    }
    
    @Override
    public void readArrayEnd() throws IOException {
        this.readArrayEnd(false);
    }
    
    @Override
    public void readMapEnd() throws IOException {
        this.readMapEnd(false);
    }
    
    @Override
    public UnpackerIterator iterator() {
        return new UnpackerIterator(this);
    }
    
    protected abstract void readValue(final Unconverter p0) throws IOException;
    
    @Override
    public Value readValue() throws IOException {
        final Unconverter uc = new Unconverter(this.msgpack);
        this.readValue(uc);
        return uc.getResult();
    }
    
    protected abstract boolean tryReadNil() throws IOException;
    
    @Override
    public <T> T read(final Class<T> klass) throws IOException {
        if (this.tryReadNil()) {
            return null;
        }
        final Template<T> tmpl = this.msgpack.lookup(klass);
        return tmpl.read(this, null);
    }
    
    @Override
    public <T> T read(final T to) throws IOException {
        if (this.tryReadNil()) {
            return null;
        }
        final Template<T> tmpl = this.msgpack.lookup(to.getClass());
        return tmpl.read(this, to);
    }
    
    @Override
    public <T> T read(final Template<T> tmpl) throws IOException {
        if (this.tryReadNil()) {
            return null;
        }
        return tmpl.read(this, null);
    }
    
    @Override
    public <T> T read(final T to, final Template<T> tmpl) throws IOException {
        if (this.tryReadNil()) {
            return null;
        }
        return tmpl.read(this, to);
    }
    
    @Override
    public int getReadByteCount() {
        throw new UnsupportedOperationException("Not implemented");
    }
    
    @Override
    public void resetReadByteCount() {
        throw new UnsupportedOperationException("Not implemented");
    }
    
    @Override
    public void setRawSizeLimit(final int size) {
        if (size < 32) {
            this.rawSizeLimit = 32;
        }
        else {
            this.rawSizeLimit = size;
        }
    }
    
    @Override
    public void setArraySizeLimit(final int size) {
        if (size < 16) {
            this.arraySizeLimit = 16;
        }
        else {
            this.arraySizeLimit = size;
        }
    }
    
    @Override
    public void setMapSizeLimit(final int size) {
        if (size < 16) {
            this.mapSizeLimit = 16;
        }
        else {
            this.mapSizeLimit = size;
        }
    }
}
