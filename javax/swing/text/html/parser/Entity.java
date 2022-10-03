package javax.swing.text.html.parser;

import java.util.Hashtable;

public final class Entity implements DTDConstants
{
    public String name;
    public int type;
    public char[] data;
    static Hashtable<String, Integer> entityTypes;
    
    public Entity(final String name, final int type, final char[] data) {
        this.name = name;
        this.type = type;
        this.data = data;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getType() {
        return this.type & 0xFFFF;
    }
    
    public boolean isParameter() {
        return (this.type & 0x40000) != 0x0;
    }
    
    public boolean isGeneral() {
        return (this.type & 0x10000) != 0x0;
    }
    
    public char[] getData() {
        return this.data;
    }
    
    public String getString() {
        return new String(this.data, 0, this.data.length);
    }
    
    public static int name2type(final String s) {
        final Integer n = Entity.entityTypes.get(s);
        return (n == null) ? 1 : n;
    }
    
    static {
        (Entity.entityTypes = new Hashtable<String, Integer>()).put("PUBLIC", 10);
        Entity.entityTypes.put("CDATA", 1);
        Entity.entityTypes.put("SDATA", 11);
        Entity.entityTypes.put("PI", 12);
        Entity.entityTypes.put("STARTTAG", 13);
        Entity.entityTypes.put("ENDTAG", 14);
        Entity.entityTypes.put("MS", 15);
        Entity.entityTypes.put("MD", 16);
        Entity.entityTypes.put("SYSTEM", 17);
    }
}
