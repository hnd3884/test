package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.EnumSyntax;

public class ReferenceUriSchemesSupported extends EnumSyntax implements Attribute
{
    private static final long serialVersionUID = -8989076942813442805L;
    public static final ReferenceUriSchemesSupported FTP;
    public static final ReferenceUriSchemesSupported HTTP;
    public static final ReferenceUriSchemesSupported HTTPS;
    public static final ReferenceUriSchemesSupported GOPHER;
    public static final ReferenceUriSchemesSupported NEWS;
    public static final ReferenceUriSchemesSupported NNTP;
    public static final ReferenceUriSchemesSupported WAIS;
    public static final ReferenceUriSchemesSupported FILE;
    private static final String[] myStringTable;
    private static final ReferenceUriSchemesSupported[] myEnumValueTable;
    
    protected ReferenceUriSchemesSupported(final int n) {
        super(n);
    }
    
    @Override
    protected String[] getStringTable() {
        return ReferenceUriSchemesSupported.myStringTable.clone();
    }
    
    @Override
    protected EnumSyntax[] getEnumValueTable() {
        return ReferenceUriSchemesSupported.myEnumValueTable.clone();
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return ReferenceUriSchemesSupported.class;
    }
    
    @Override
    public final String getName() {
        return "reference-uri-schemes-supported";
    }
    
    static {
        FTP = new ReferenceUriSchemesSupported(0);
        HTTP = new ReferenceUriSchemesSupported(1);
        HTTPS = new ReferenceUriSchemesSupported(2);
        GOPHER = new ReferenceUriSchemesSupported(3);
        NEWS = new ReferenceUriSchemesSupported(4);
        NNTP = new ReferenceUriSchemesSupported(5);
        WAIS = new ReferenceUriSchemesSupported(6);
        FILE = new ReferenceUriSchemesSupported(7);
        myStringTable = new String[] { "ftp", "http", "https", "gopher", "news", "nntp", "wais", "file" };
        myEnumValueTable = new ReferenceUriSchemesSupported[] { ReferenceUriSchemesSupported.FTP, ReferenceUriSchemesSupported.HTTP, ReferenceUriSchemesSupported.HTTPS, ReferenceUriSchemesSupported.GOPHER, ReferenceUriSchemesSupported.NEWS, ReferenceUriSchemesSupported.NNTP, ReferenceUriSchemesSupported.WAIS, ReferenceUriSchemesSupported.FILE };
    }
}
