package sun.nio.cs.ext;

import java.nio.charset.CoderResult;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;

interface DelegatableDecoder
{
    CoderResult decodeLoop(final ByteBuffer p0, final CharBuffer p1);
    
    void implReset();
    
    CoderResult implFlush(final CharBuffer p0);
}
