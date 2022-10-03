package org.omg.IOP;

import org.omg.IOP.CodecFactoryPackage.UnknownEncoding;

public interface CodecFactoryOperations
{
    Codec create_codec(final Encoding p0) throws UnknownEncoding;
}
