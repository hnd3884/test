package org.msgpack.template;

import org.msgpack.unpacker.Unpacker;
import java.io.IOException;
import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;
import java.util.Date;

public class DateTemplate extends AbstractTemplate<Date>
{
    static final DateTemplate instance;
    
    private DateTemplate() {
    }
    
    @Override
    public void write(final Packer pk, final Date target, final boolean required) throws IOException {
        if (target != null) {
            pk.write(target.getTime());
            return;
        }
        if (required) {
            throw new MessageTypeException("Attempted to write null");
        }
        pk.writeNil();
    }
    
    @Override
    public Date read(final Unpacker u, final Date to, final boolean required) throws IOException {
        if (!required && u.trySkipNil()) {
            return null;
        }
        final long temp = u.readLong();
        return new Date(temp);
    }
    
    public static DateTemplate getInstance() {
        return DateTemplate.instance;
    }
    
    static {
        instance = new DateTemplate();
    }
}
