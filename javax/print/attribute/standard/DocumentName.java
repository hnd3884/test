package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import java.util.Locale;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.TextSyntax;

public final class DocumentName extends TextSyntax implements DocAttribute
{
    private static final long serialVersionUID = 7883105848533280430L;
    
    public DocumentName(final String s, final Locale locale) {
        super(s, locale);
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof DocumentName;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return DocumentName.class;
    }
    
    @Override
    public final String getName() {
        return "document-name";
    }
}
