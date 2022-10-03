package org.msgpack.template;

import org.msgpack.unpacker.Unpacker;
import java.io.IOException;
import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;
import java.math.BigDecimal;

public class BigDecimalTemplate extends AbstractTemplate<BigDecimal>
{
    static final BigDecimalTemplate instance;
    
    private BigDecimalTemplate() {
    }
    
    @Override
    public void write(final Packer pk, final BigDecimal target, final boolean required) throws IOException {
        if (target != null) {
            pk.write(target.toString());
            return;
        }
        if (required) {
            throw new MessageTypeException("Attempted to write null");
        }
        pk.writeNil();
    }
    
    @Override
    public BigDecimal read(final Unpacker u, final BigDecimal to, final boolean required) throws IOException {
        if (!required && u.trySkipNil()) {
            return null;
        }
        final String temp = u.readString();
        return new BigDecimal(temp);
    }
    
    public static BigDecimalTemplate getInstance() {
        return BigDecimalTemplate.instance;
    }
    
    static {
        instance = new BigDecimalTemplate();
    }
}
