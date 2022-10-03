package java.nio.channels;

import java.util.concurrent.Future;
import java.nio.ByteBuffer;

public interface AsynchronousByteChannel extends AsynchronousChannel
{
     <A> void read(final ByteBuffer p0, final A p1, final CompletionHandler<Integer, ? super A> p2);
    
    Future<Integer> read(final ByteBuffer p0);
    
     <A> void write(final ByteBuffer p0, final A p1, final CompletionHandler<Integer, ? super A> p2);
    
    Future<Integer> write(final ByteBuffer p0);
}
