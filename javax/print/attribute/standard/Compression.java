package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.EnumSyntax;

public class Compression extends EnumSyntax implements DocAttribute
{
    private static final long serialVersionUID = -5716748913324997674L;
    public static final Compression NONE;
    public static final Compression DEFLATE;
    public static final Compression GZIP;
    public static final Compression COMPRESS;
    private static final String[] myStringTable;
    private static final Compression[] myEnumValueTable;
    
    protected Compression(final int n) {
        super(n);
    }
    
    @Override
    protected String[] getStringTable() {
        return Compression.myStringTable.clone();
    }
    
    @Override
    protected EnumSyntax[] getEnumValueTable() {
        return Compression.myEnumValueTable.clone();
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return Compression.class;
    }
    
    @Override
    public final String getName() {
        return "compression";
    }
    
    static {
        NONE = new Compression(0);
        DEFLATE = new Compression(1);
        GZIP = new Compression(2);
        COMPRESS = new Compression(3);
        myStringTable = new String[] { "none", "deflate", "gzip", "compress" };
        myEnumValueTable = new Compression[] { Compression.NONE, Compression.DEFLATE, Compression.GZIP, Compression.COMPRESS };
    }
}
