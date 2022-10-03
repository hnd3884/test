package org.msgpack.unpacker;

final class ArrayAccept extends Accept
{
    int size;
    
    @Override
    void acceptArray(final int size) {
        this.size = size;
    }
}
