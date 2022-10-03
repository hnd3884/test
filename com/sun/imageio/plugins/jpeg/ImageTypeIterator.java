package com.sun.imageio.plugins.jpeg;

import java.util.NoSuchElementException;
import javax.imageio.ImageTypeSpecifier;
import java.util.Iterator;

class ImageTypeIterator implements Iterator<ImageTypeSpecifier>
{
    private Iterator<ImageTypeProducer> producers;
    private ImageTypeSpecifier theNext;
    
    public ImageTypeIterator(final Iterator<ImageTypeProducer> producers) {
        this.theNext = null;
        this.producers = producers;
    }
    
    @Override
    public boolean hasNext() {
        if (this.theNext != null) {
            return true;
        }
        if (!this.producers.hasNext()) {
            return false;
        }
        do {
            this.theNext = this.producers.next().getType();
        } while (this.theNext == null && this.producers.hasNext());
        return this.theNext != null;
    }
    
    @Override
    public ImageTypeSpecifier next() {
        if (this.theNext != null || this.hasNext()) {
            final ImageTypeSpecifier theNext = this.theNext;
            this.theNext = null;
            return theNext;
        }
        throw new NoSuchElementException();
    }
    
    @Override
    public void remove() {
        this.producers.remove();
    }
}
