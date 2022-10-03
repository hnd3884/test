package java.awt;

public final class JobAttributes implements Cloneable
{
    private int copies;
    private DefaultSelectionType defaultSelection;
    private DestinationType destination;
    private DialogType dialog;
    private String fileName;
    private int fromPage;
    private int maxPage;
    private int minPage;
    private MultipleDocumentHandlingType multipleDocumentHandling;
    private int[][] pageRanges;
    private int prFirst;
    private int prLast;
    private String printer;
    private SidesType sides;
    private int toPage;
    
    public JobAttributes() {
        this.setCopiesToDefault();
        this.setDefaultSelection(DefaultSelectionType.ALL);
        this.setDestination(DestinationType.PRINTER);
        this.setDialog(DialogType.NATIVE);
        this.setMaxPage(Integer.MAX_VALUE);
        this.setMinPage(1);
        this.setMultipleDocumentHandlingToDefault();
        this.setSidesToDefault();
    }
    
    public JobAttributes(final JobAttributes jobAttributes) {
        this.set(jobAttributes);
    }
    
    public JobAttributes(final int copies, final DefaultSelectionType defaultSelection, final DestinationType destination, final DialogType dialog, final String fileName, final int maxPage, final int minPage, final MultipleDocumentHandlingType multipleDocumentHandling, final int[][] pageRanges, final String printer, final SidesType sides) {
        this.setCopies(copies);
        this.setDefaultSelection(defaultSelection);
        this.setDestination(destination);
        this.setDialog(dialog);
        this.setFileName(fileName);
        this.setMaxPage(maxPage);
        this.setMinPage(minPage);
        this.setMultipleDocumentHandling(multipleDocumentHandling);
        this.setPageRanges(pageRanges);
        this.setPrinter(printer);
        this.setSides(sides);
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            throw new InternalError(ex);
        }
    }
    
    public void set(final JobAttributes jobAttributes) {
        this.copies = jobAttributes.copies;
        this.defaultSelection = jobAttributes.defaultSelection;
        this.destination = jobAttributes.destination;
        this.dialog = jobAttributes.dialog;
        this.fileName = jobAttributes.fileName;
        this.fromPage = jobAttributes.fromPage;
        this.maxPage = jobAttributes.maxPage;
        this.minPage = jobAttributes.minPage;
        this.multipleDocumentHandling = jobAttributes.multipleDocumentHandling;
        this.pageRanges = jobAttributes.pageRanges;
        this.prFirst = jobAttributes.prFirst;
        this.prLast = jobAttributes.prLast;
        this.printer = jobAttributes.printer;
        this.sides = jobAttributes.sides;
        this.toPage = jobAttributes.toPage;
    }
    
    public int getCopies() {
        return this.copies;
    }
    
    public void setCopies(final int copies) {
        if (copies <= 0) {
            throw new IllegalArgumentException("Invalid value for attribute copies");
        }
        this.copies = copies;
    }
    
    public void setCopiesToDefault() {
        this.setCopies(1);
    }
    
    public DefaultSelectionType getDefaultSelection() {
        return this.defaultSelection;
    }
    
    public void setDefaultSelection(final DefaultSelectionType defaultSelection) {
        if (defaultSelection == null) {
            throw new IllegalArgumentException("Invalid value for attribute defaultSelection");
        }
        this.defaultSelection = defaultSelection;
    }
    
    public DestinationType getDestination() {
        return this.destination;
    }
    
    public void setDestination(final DestinationType destination) {
        if (destination == null) {
            throw new IllegalArgumentException("Invalid value for attribute destination");
        }
        this.destination = destination;
    }
    
    public DialogType getDialog() {
        return this.dialog;
    }
    
    public void setDialog(final DialogType dialog) {
        if (dialog == null) {
            throw new IllegalArgumentException("Invalid value for attribute dialog");
        }
        this.dialog = dialog;
    }
    
    public String getFileName() {
        return this.fileName;
    }
    
    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }
    
    public int getFromPage() {
        if (this.fromPage != 0) {
            return this.fromPage;
        }
        if (this.toPage != 0) {
            return this.getMinPage();
        }
        if (this.pageRanges != null) {
            return this.prFirst;
        }
        return this.getMinPage();
    }
    
    public void setFromPage(final int fromPage) {
        if (fromPage <= 0 || (this.toPage != 0 && fromPage > this.toPage) || fromPage < this.minPage || fromPage > this.maxPage) {
            throw new IllegalArgumentException("Invalid value for attribute fromPage");
        }
        this.fromPage = fromPage;
    }
    
    public int getMaxPage() {
        return this.maxPage;
    }
    
    public void setMaxPage(final int maxPage) {
        if (maxPage <= 0 || maxPage < this.minPage) {
            throw new IllegalArgumentException("Invalid value for attribute maxPage");
        }
        this.maxPage = maxPage;
    }
    
    public int getMinPage() {
        return this.minPage;
    }
    
    public void setMinPage(final int minPage) {
        if (minPage <= 0 || minPage > this.maxPage) {
            throw new IllegalArgumentException("Invalid value for attribute minPage");
        }
        this.minPage = minPage;
    }
    
    public MultipleDocumentHandlingType getMultipleDocumentHandling() {
        return this.multipleDocumentHandling;
    }
    
    public void setMultipleDocumentHandling(final MultipleDocumentHandlingType multipleDocumentHandling) {
        if (multipleDocumentHandling == null) {
            throw new IllegalArgumentException("Invalid value for attribute multipleDocumentHandling");
        }
        this.multipleDocumentHandling = multipleDocumentHandling;
    }
    
    public void setMultipleDocumentHandlingToDefault() {
        this.setMultipleDocumentHandling(MultipleDocumentHandlingType.SEPARATE_DOCUMENTS_UNCOLLATED_COPIES);
    }
    
    public int[][] getPageRanges() {
        if (this.pageRanges != null) {
            final int[][] array = new int[this.pageRanges.length][2];
            for (int i = 0; i < this.pageRanges.length; ++i) {
                array[i][0] = this.pageRanges[i][0];
                array[i][1] = this.pageRanges[i][1];
            }
            return array;
        }
        if (this.fromPage != 0 || this.toPage != 0) {
            return new int[][] { { this.getFromPage(), this.getToPage() } };
        }
        final int minPage = this.getMinPage();
        return new int[][] { { minPage, minPage } };
    }
    
    public void setPageRanges(final int[][] array) {
        final String s = "Invalid value for attribute pageRanges";
        int prFirst = 0;
        int prLast = 0;
        if (array == null) {
            throw new IllegalArgumentException(s);
        }
        for (int i = 0; i < array.length; ++i) {
            if (array[i] == null || array[i].length != 2 || array[i][0] <= prLast || array[i][1] < array[i][0]) {
                throw new IllegalArgumentException(s);
            }
            prLast = array[i][1];
            if (prFirst == 0) {
                prFirst = array[i][0];
            }
        }
        if (prFirst < this.minPage || prLast > this.maxPage) {
            throw new IllegalArgumentException(s);
        }
        final int[][] pageRanges = new int[array.length][2];
        for (int j = 0; j < array.length; ++j) {
            pageRanges[j][0] = array[j][0];
            pageRanges[j][1] = array[j][1];
        }
        this.pageRanges = pageRanges;
        this.prFirst = prFirst;
        this.prLast = prLast;
    }
    
    public String getPrinter() {
        return this.printer;
    }
    
    public void setPrinter(final String printer) {
        this.printer = printer;
    }
    
    public SidesType getSides() {
        return this.sides;
    }
    
    public void setSides(final SidesType sides) {
        if (sides == null) {
            throw new IllegalArgumentException("Invalid value for attribute sides");
        }
        this.sides = sides;
    }
    
    public void setSidesToDefault() {
        this.setSides(SidesType.ONE_SIDED);
    }
    
    public int getToPage() {
        if (this.toPage != 0) {
            return this.toPage;
        }
        if (this.fromPage != 0) {
            return this.fromPage;
        }
        if (this.pageRanges != null) {
            return this.prLast;
        }
        return this.getMinPage();
    }
    
    public void setToPage(final int toPage) {
        if (toPage <= 0 || (this.fromPage != 0 && toPage < this.fromPage) || toPage < this.minPage || toPage > this.maxPage) {
            throw new IllegalArgumentException("Invalid value for attribute toPage");
        }
        this.toPage = toPage;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof JobAttributes)) {
            return false;
        }
        final JobAttributes jobAttributes = (JobAttributes)o;
        if (this.fileName == null) {
            if (jobAttributes.fileName != null) {
                return false;
            }
        }
        else if (!this.fileName.equals(jobAttributes.fileName)) {
            return false;
        }
        if (this.pageRanges == null) {
            if (jobAttributes.pageRanges != null) {
                return false;
            }
        }
        else {
            if (jobAttributes.pageRanges == null || this.pageRanges.length != jobAttributes.pageRanges.length) {
                return false;
            }
            for (int i = 0; i < this.pageRanges.length; ++i) {
                if (this.pageRanges[i][0] != jobAttributes.pageRanges[i][0] || this.pageRanges[i][1] != jobAttributes.pageRanges[i][1]) {
                    return false;
                }
            }
        }
        if (this.printer == null) {
            if (jobAttributes.printer != null) {
                return false;
            }
        }
        else if (!this.printer.equals(jobAttributes.printer)) {
            return false;
        }
        return this.copies == jobAttributes.copies && this.defaultSelection == jobAttributes.defaultSelection && this.destination == jobAttributes.destination && this.dialog == jobAttributes.dialog && this.fromPage == jobAttributes.fromPage && this.maxPage == jobAttributes.maxPage && this.minPage == jobAttributes.minPage && this.multipleDocumentHandling == jobAttributes.multipleDocumentHandling && this.prFirst == jobAttributes.prFirst && this.prLast == jobAttributes.prLast && this.sides == jobAttributes.sides && this.toPage == jobAttributes.toPage;
    }
    
    @Override
    public int hashCode() {
        int n = (this.copies + this.fromPage + this.maxPage + this.minPage + this.prFirst + this.prLast + this.toPage) * 31 << 21;
        if (this.pageRanges != null) {
            int n2 = 0;
            for (int i = 0; i < this.pageRanges.length; ++i) {
                n2 += this.pageRanges[i][0] + this.pageRanges[i][1];
            }
            n ^= n2 * 31 << 11;
        }
        if (this.fileName != null) {
            n ^= this.fileName.hashCode();
        }
        if (this.printer != null) {
            n ^= this.printer.hashCode();
        }
        return this.defaultSelection.hashCode() << 6 ^ this.destination.hashCode() << 5 ^ this.dialog.hashCode() << 3 ^ this.multipleDocumentHandling.hashCode() << 2 ^ this.sides.hashCode() ^ n;
    }
    
    @Override
    public String toString() {
        final int[][] pageRanges = this.getPageRanges();
        String s = "[";
        int n = 1;
        for (int i = 0; i < pageRanges.length; ++i) {
            if (n != 0) {
                n = 0;
            }
            else {
                s += ",";
            }
            s = s + pageRanges[i][0] + ":" + pageRanges[i][1];
        }
        return "copies=" + this.getCopies() + ",defaultSelection=" + this.getDefaultSelection() + ",destination=" + this.getDestination() + ",dialog=" + this.getDialog() + ",fileName=" + this.getFileName() + ",fromPage=" + this.getFromPage() + ",maxPage=" + this.getMaxPage() + ",minPage=" + this.getMinPage() + ",multiple-document-handling=" + this.getMultipleDocumentHandling() + ",page-ranges=" + (s + "]") + ",printer=" + this.getPrinter() + ",sides=" + this.getSides() + ",toPage=" + this.getToPage();
    }
    
    public static final class DefaultSelectionType extends AttributeValue
    {
        private static final int I_ALL = 0;
        private static final int I_RANGE = 1;
        private static final int I_SELECTION = 2;
        private static final String[] NAMES;
        public static final DefaultSelectionType ALL;
        public static final DefaultSelectionType RANGE;
        public static final DefaultSelectionType SELECTION;
        
        private DefaultSelectionType(final int n) {
            super(n, DefaultSelectionType.NAMES);
        }
        
        static {
            NAMES = new String[] { "all", "range", "selection" };
            ALL = new DefaultSelectionType(0);
            RANGE = new DefaultSelectionType(1);
            SELECTION = new DefaultSelectionType(2);
        }
    }
    
    public static final class DestinationType extends AttributeValue
    {
        private static final int I_FILE = 0;
        private static final int I_PRINTER = 1;
        private static final String[] NAMES;
        public static final DestinationType FILE;
        public static final DestinationType PRINTER;
        
        private DestinationType(final int n) {
            super(n, DestinationType.NAMES);
        }
        
        static {
            NAMES = new String[] { "file", "printer" };
            FILE = new DestinationType(0);
            PRINTER = new DestinationType(1);
        }
    }
    
    public static final class DialogType extends AttributeValue
    {
        private static final int I_COMMON = 0;
        private static final int I_NATIVE = 1;
        private static final int I_NONE = 2;
        private static final String[] NAMES;
        public static final DialogType COMMON;
        public static final DialogType NATIVE;
        public static final DialogType NONE;
        
        private DialogType(final int n) {
            super(n, DialogType.NAMES);
        }
        
        static {
            NAMES = new String[] { "common", "native", "none" };
            COMMON = new DialogType(0);
            NATIVE = new DialogType(1);
            NONE = new DialogType(2);
        }
    }
    
    public static final class MultipleDocumentHandlingType extends AttributeValue
    {
        private static final int I_SEPARATE_DOCUMENTS_COLLATED_COPIES = 0;
        private static final int I_SEPARATE_DOCUMENTS_UNCOLLATED_COPIES = 1;
        private static final String[] NAMES;
        public static final MultipleDocumentHandlingType SEPARATE_DOCUMENTS_COLLATED_COPIES;
        public static final MultipleDocumentHandlingType SEPARATE_DOCUMENTS_UNCOLLATED_COPIES;
        
        private MultipleDocumentHandlingType(final int n) {
            super(n, MultipleDocumentHandlingType.NAMES);
        }
        
        static {
            NAMES = new String[] { "separate-documents-collated-copies", "separate-documents-uncollated-copies" };
            SEPARATE_DOCUMENTS_COLLATED_COPIES = new MultipleDocumentHandlingType(0);
            SEPARATE_DOCUMENTS_UNCOLLATED_COPIES = new MultipleDocumentHandlingType(1);
        }
    }
    
    public static final class SidesType extends AttributeValue
    {
        private static final int I_ONE_SIDED = 0;
        private static final int I_TWO_SIDED_LONG_EDGE = 1;
        private static final int I_TWO_SIDED_SHORT_EDGE = 2;
        private static final String[] NAMES;
        public static final SidesType ONE_SIDED;
        public static final SidesType TWO_SIDED_LONG_EDGE;
        public static final SidesType TWO_SIDED_SHORT_EDGE;
        
        private SidesType(final int n) {
            super(n, SidesType.NAMES);
        }
        
        static {
            NAMES = new String[] { "one-sided", "two-sided-long-edge", "two-sided-short-edge" };
            ONE_SIDED = new SidesType(0);
            TWO_SIDED_LONG_EDGE = new SidesType(1);
            TWO_SIDED_SHORT_EDGE = new SidesType(2);
        }
    }
}
