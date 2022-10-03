package com.azul.crs.com.fasterxml.jackson.databind.node;

import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.IOException;
import java.io.Externalizable;
import java.io.Serializable;

class NodeSerialization implements Serializable, Externalizable
{
    private static final long serialVersionUID = 1L;
    public byte[] json;
    
    public NodeSerialization() {
    }
    
    public NodeSerialization(final byte[] b) {
        this.json = b;
    }
    
    protected Object readResolve() {
        try {
            return InternalNodeMapper.bytesToNode(this.json);
        }
        catch (final IOException e) {
            throw new IllegalArgumentException("Failed to JDK deserialize `JsonNode` value: " + e.getMessage(), e);
        }
    }
    
    public static NodeSerialization from(final Object o) {
        try {
            return new NodeSerialization(InternalNodeMapper.valueToBytes(o));
        }
        catch (final IOException e) {
            throw new IllegalArgumentException("Failed to JDK serialize `" + o.getClass().getSimpleName() + "` value: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeInt(this.json.length);
        out.write(this.json);
    }
    
    @Override
    public void readExternal(final ObjectInput in) throws IOException {
        final int len = in.readInt();
        in.readFully(this.json = new byte[len], 0, len);
    }
}
