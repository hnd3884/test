package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.EnumSyntax;

public class MultipleDocumentHandling extends EnumSyntax implements PrintRequestAttribute, PrintJobAttribute
{
    private static final long serialVersionUID = 8098326460746413466L;
    public static final MultipleDocumentHandling SINGLE_DOCUMENT;
    public static final MultipleDocumentHandling SEPARATE_DOCUMENTS_UNCOLLATED_COPIES;
    public static final MultipleDocumentHandling SEPARATE_DOCUMENTS_COLLATED_COPIES;
    public static final MultipleDocumentHandling SINGLE_DOCUMENT_NEW_SHEET;
    private static final String[] myStringTable;
    private static final MultipleDocumentHandling[] myEnumValueTable;
    
    protected MultipleDocumentHandling(final int n) {
        super(n);
    }
    
    @Override
    protected String[] getStringTable() {
        return MultipleDocumentHandling.myStringTable.clone();
    }
    
    @Override
    protected EnumSyntax[] getEnumValueTable() {
        return MultipleDocumentHandling.myEnumValueTable.clone();
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return MultipleDocumentHandling.class;
    }
    
    @Override
    public final String getName() {
        return "multiple-document-handling";
    }
    
    static {
        SINGLE_DOCUMENT = new MultipleDocumentHandling(0);
        SEPARATE_DOCUMENTS_UNCOLLATED_COPIES = new MultipleDocumentHandling(1);
        SEPARATE_DOCUMENTS_COLLATED_COPIES = new MultipleDocumentHandling(2);
        SINGLE_DOCUMENT_NEW_SHEET = new MultipleDocumentHandling(3);
        myStringTable = new String[] { "single-document", "separate-documents-uncollated-copies", "separate-documents-collated-copies", "single-document-new-sheet" };
        myEnumValueTable = new MultipleDocumentHandling[] { MultipleDocumentHandling.SINGLE_DOCUMENT, MultipleDocumentHandling.SEPARATE_DOCUMENTS_UNCOLLATED_COPIES, MultipleDocumentHandling.SEPARATE_DOCUMENTS_COLLATED_COPIES, MultipleDocumentHandling.SINGLE_DOCUMENT_NEW_SHEET };
    }
}
