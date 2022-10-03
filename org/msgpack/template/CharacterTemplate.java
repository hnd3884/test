package org.msgpack.template;

import org.msgpack.unpacker.Unpacker;
import java.io.IOException;
import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;

public class CharacterTemplate extends AbstractTemplate<Character>
{
    static final CharacterTemplate instance;
    
    private CharacterTemplate() {
    }
    
    @Override
    public void write(final Packer pk, final Character target, final boolean required) throws IOException {
        if (target != null) {
            pk.write((int)target);
            return;
        }
        if (required) {
            throw new MessageTypeException("Attempted to write null");
        }
        pk.writeNil();
    }
    
    @Override
    public Character read(final Unpacker u, final Character to, final boolean required) throws IOException {
        if (!required && u.trySkipNil()) {
            return null;
        }
        return (char)u.readInt();
    }
    
    public static CharacterTemplate getInstance() {
        return CharacterTemplate.instance;
    }
    
    static {
        instance = new CharacterTemplate();
    }
}
