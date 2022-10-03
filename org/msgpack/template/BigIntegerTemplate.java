package org.msgpack.template;

import org.msgpack.unpacker.Unpacker;
import java.io.IOException;
import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;
import java.math.BigInteger;

public class BigIntegerTemplate extends AbstractTemplate<BigInteger>
{
    static final BigIntegerTemplate instance;
    
    private BigIntegerTemplate() {
    }
    
    @Override
    public void write(final Packer pk, final BigInteger target, final boolean required) throws IOException {
        if (target != null) {
            pk.write(target);
            return;
        }
        if (required) {
            throw new MessageTypeException("Attempted to write null");
        }
        pk.writeNil();
    }
    
    @Override
    public BigInteger read(final Unpacker u, final BigInteger to, final boolean required) throws IOException {
        if (!required && u.trySkipNil()) {
            return null;
        }
        return u.readBigInteger();
    }
    
    public static BigIntegerTemplate getInstance() {
        return BigIntegerTemplate.instance;
    }
    
    static {
        instance = new BigIntegerTemplate();
    }
}
