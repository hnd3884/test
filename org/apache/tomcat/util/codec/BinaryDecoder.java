package org.apache.tomcat.util.codec;

@Deprecated
public interface BinaryDecoder extends Decoder
{
    byte[] decode(final byte[] p0) throws DecoderException;
}
