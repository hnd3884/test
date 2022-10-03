package org.apache.tomcat.util.codec;

@Deprecated
public interface BinaryEncoder extends Encoder
{
    byte[] encode(final byte[] p0) throws EncoderException;
}
