package org.msgpack.unpacker;

import java.util.NoSuchElementException;
import java.io.EOFException;
import java.io.IOException;
import org.msgpack.packer.Unconverter;
import org.msgpack.type.Value;
import java.util.Iterator;

public class UnpackerIterator implements Iterator<Value>
{
    private final AbstractUnpacker u;
    private final Unconverter uc;
    private IOException exception;
    
    public UnpackerIterator(final AbstractUnpacker u) {
        this.u = u;
        this.uc = new Unconverter(u.msgpack);
    }
    
    @Override
    public boolean hasNext() {
        if (this.uc.getResult() != null) {
            return true;
        }
        try {
            this.u.readValue(this.uc);
        }
        catch (final EOFException ex) {
            return false;
        }
        catch (final IOException ex2) {
            this.exception = ex2;
            return false;
        }
        return this.uc.getResult() != null;
    }
    
    @Override
    public Value next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        final Value v = this.uc.getResult();
        this.uc.resetResult();
        return v;
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    public IOException getException() {
        return this.exception;
    }
}
