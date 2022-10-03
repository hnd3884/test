package org.msgpack.unpacker;

final class MapAccept extends Accept
{
    int size;
    
    @Override
    void acceptMap(final int size) {
        this.size = size;
    }
}
