package sun.print;

import javax.print.ServiceUIFactory;
import javax.print.attribute.HashAttributeSet;
import javax.print.attribute.standard.CopiesSupported;
import javax.print.attribute.standard.RequestingUserName;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.standard.Sides;
import javax.print.attribute.standard.SheetCollate;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaPrintableArea;
import java.util.Locale;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.Fidelity;
import javax.print.attribute.standard.Chromaticity;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.AttributeSetUtilities;
import javax.print.attribute.Attribute;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.ColorSupported;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.event.PrintServiceAttributeListener;
import javax.print.DocPrintJob;
import javax.print.DocFlavor;
import java.io.OutputStream;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.StreamPrintService;

public class PSStreamPrintService extends StreamPrintService implements SunPrinterJobService
{
    private static final Class[] suppAttrCats;
    private static int MAXCOPIES;
    private static final MediaSizeName[] mediaSizes;
    
    public PSStreamPrintService(final OutputStream outputStream) {
        super(outputStream);
    }
    
    @Override
    public String getOutputFormat() {
        return "application/postscript";
    }
    
    @Override
    public DocFlavor[] getSupportedDocFlavors() {
        return PSStreamPrinterFactory.getFlavors();
    }
    
    @Override
    public DocPrintJob createPrintJob() {
        return new PSStreamPrintJob(this);
    }
    
    @Override
    public boolean usesClass(final Class clazz) {
        return clazz == PSPrinterJob.class;
    }
    
    @Override
    public String getName() {
        return "Postscript output";
    }
    
    @Override
    public void addPrintServiceAttributeListener(final PrintServiceAttributeListener printServiceAttributeListener) {
    }
    
    @Override
    public void removePrintServiceAttributeListener(final PrintServiceAttributeListener printServiceAttributeListener) {
    }
    
    @Override
    public <T extends PrintServiceAttribute> T getAttribute(final Class<T> clazz) {
        if (clazz == null) {
            throw new NullPointerException("category");
        }
        if (!PrintServiceAttribute.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("Not a PrintServiceAttribute");
        }
        if (clazz == ColorSupported.class) {
            return (T)ColorSupported.SUPPORTED;
        }
        return null;
    }
    
    @Override
    public PrintServiceAttributeSet getAttributes() {
        final HashPrintServiceAttributeSet set = new HashPrintServiceAttributeSet();
        set.add(ColorSupported.SUPPORTED);
        return AttributeSetUtilities.unmodifiableView(set);
    }
    
    @Override
    public boolean isDocFlavorSupported(final DocFlavor docFlavor) {
        final DocFlavor[] supportedDocFlavors = this.getSupportedDocFlavors();
        for (int i = 0; i < supportedDocFlavors.length; ++i) {
            if (docFlavor.equals(supportedDocFlavors[i])) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Class<?>[] getSupportedAttributeCategories() {
        final Class[] array = new Class[PSStreamPrintService.suppAttrCats.length];
        System.arraycopy(PSStreamPrintService.suppAttrCats, 0, array, 0, array.length);
        return array;
    }
    
    @Override
    public boolean isAttributeCategorySupported(final Class<? extends Attribute> clazz) {
        if (clazz == null) {
            throw new NullPointerException("null category");
        }
        if (!Attribute.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException(clazz + " is not an Attribute");
        }
        for (int i = 0; i < PSStreamPrintService.suppAttrCats.length; ++i) {
            if (clazz == PSStreamPrintService.suppAttrCats[i]) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Object getDefaultAttributeValue(final Class<? extends Attribute> clazz) {
        if (clazz == null) {
            throw new NullPointerException("null category");
        }
        if (!Attribute.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException(clazz + " is not an Attribute");
        }
        if (!this.isAttributeCategorySupported(clazz)) {
            return null;
        }
        if (clazz == Copies.class) {
            return new Copies(1);
        }
        if (clazz == Chromaticity.class) {
            return Chromaticity.COLOR;
        }
        if (clazz == Fidelity.class) {
            return Fidelity.FIDELITY_FALSE;
        }
        if (clazz == Media.class) {
            final String country = Locale.getDefault().getCountry();
            if (country != null && (country.equals("") || country.equals(Locale.US.getCountry()) || country.equals(Locale.CANADA.getCountry()))) {
                return MediaSizeName.NA_LETTER;
            }
            return MediaSizeName.ISO_A4;
        }
        else {
            if (clazz == MediaPrintableArea.class) {
                final String country2 = Locale.getDefault().getCountry();
                final float n = 0.5f;
                float n2;
                float n3;
                if (country2 != null && (country2.equals("") || country2.equals(Locale.US.getCountry()) || country2.equals(Locale.CANADA.getCountry()))) {
                    n2 = MediaSize.NA.LETTER.getX(25400) - 2.0f * n;
                    n3 = MediaSize.NA.LETTER.getY(25400) - 2.0f * n;
                }
                else {
                    n2 = MediaSize.ISO.A4.getX(25400) - 2.0f * n;
                    n3 = MediaSize.ISO.A4.getY(25400) - 2.0f * n;
                }
                return new MediaPrintableArea(n, n, n2, n3, 25400);
            }
            if (clazz == OrientationRequested.class) {
                return OrientationRequested.PORTRAIT;
            }
            if (clazz == PageRanges.class) {
                return new PageRanges(1, Integer.MAX_VALUE);
            }
            if (clazz == SheetCollate.class) {
                return SheetCollate.UNCOLLATED;
            }
            if (clazz == Sides.class) {
                return Sides.ONE_SIDED;
            }
            return null;
        }
    }
    
    @Override
    public Object getSupportedAttributeValues(final Class<? extends Attribute> clazz, final DocFlavor docFlavor, final AttributeSet set) {
        if (clazz == null) {
            throw new NullPointerException("null category");
        }
        if (!Attribute.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException(clazz + " does not implement Attribute");
        }
        if (docFlavor != null && !this.isDocFlavorSupported(docFlavor)) {
            throw new IllegalArgumentException(docFlavor + " is an unsupported flavor");
        }
        if (!this.isAttributeCategorySupported(clazz)) {
            return null;
        }
        if (clazz == Chromaticity.class) {
            return new Chromaticity[] { Chromaticity.COLOR };
        }
        if (clazz == JobName.class) {
            return new JobName("", null);
        }
        if (clazz == RequestingUserName.class) {
            return new RequestingUserName("", null);
        }
        if (clazz == OrientationRequested.class) {
            if (docFlavor == null || docFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) || docFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
                return new OrientationRequested[] { OrientationRequested.PORTRAIT, OrientationRequested.LANDSCAPE, OrientationRequested.REVERSE_LANDSCAPE };
            }
            return null;
        }
        else {
            if (clazz == Copies.class || clazz == CopiesSupported.class) {
                return new CopiesSupported(1, PSStreamPrintService.MAXCOPIES);
            }
            if (clazz == Media.class) {
                final Media[] array = new Media[PSStreamPrintService.mediaSizes.length];
                System.arraycopy(PSStreamPrintService.mediaSizes, 0, array, 0, PSStreamPrintService.mediaSizes.length);
                return array;
            }
            if (clazz == Fidelity.class) {
                return new Fidelity[] { Fidelity.FIDELITY_FALSE, Fidelity.FIDELITY_TRUE };
            }
            if (clazz == MediaPrintableArea.class) {
                if (set == null) {
                    return null;
                }
                MediaSize mediaSizeForName = (MediaSize)set.get(MediaSize.class);
                if (mediaSizeForName == null) {
                    final Media media = (Media)set.get(Media.class);
                    if (media != null && media instanceof MediaSizeName) {
                        mediaSizeForName = MediaSize.getMediaSizeForName((MediaSizeName)media);
                    }
                }
                if (mediaSizeForName == null) {
                    return null;
                }
                final MediaPrintableArea[] array2 = { null };
                final float x = mediaSizeForName.getX(25400);
                final float y = mediaSizeForName.getY(25400);
                float n = 0.5f;
                float n2 = 0.5f;
                if (x < 5.0f) {
                    n = x / 10.0f;
                }
                if (y < 5.0f) {
                    n2 = y / 10.0f;
                }
                array2[0] = new MediaPrintableArea(n, n2, x - 2.0f * n, y - 2.0f * n2, 25400);
                return array2;
            }
            else if (clazz == PageRanges.class) {
                if (docFlavor == null || docFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) || docFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
                    return new PageRanges[] { new PageRanges(1, Integer.MAX_VALUE) };
                }
                return null;
            }
            else if (clazz == SheetCollate.class) {
                if (docFlavor == null || docFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) || docFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
                    return new SheetCollate[] { SheetCollate.UNCOLLATED, SheetCollate.COLLATED };
                }
                return new SheetCollate[] { SheetCollate.UNCOLLATED };
            }
            else {
                if (clazz != Sides.class) {
                    return null;
                }
                if (docFlavor == null || docFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) || docFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
                    return new Sides[] { Sides.ONE_SIDED, Sides.TWO_SIDED_LONG_EDGE, Sides.TWO_SIDED_SHORT_EDGE };
                }
                return null;
            }
        }
    }
    
    private boolean isSupportedCopies(final Copies copies) {
        final int value = copies.getValue();
        return value > 0 && value < PSStreamPrintService.MAXCOPIES;
    }
    
    private boolean isSupportedMedia(final MediaSizeName mediaSizeName) {
        for (int i = 0; i < PSStreamPrintService.mediaSizes.length; ++i) {
            if (mediaSizeName.equals(PSStreamPrintService.mediaSizes[i])) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isAttributeValueSupported(final Attribute attribute, final DocFlavor docFlavor, final AttributeSet set) {
        if (attribute == null) {
            throw new NullPointerException("null attribute");
        }
        if (docFlavor != null && !this.isDocFlavorSupported(docFlavor)) {
            throw new IllegalArgumentException(docFlavor + " is an unsupported flavor");
        }
        if (!this.isAttributeCategorySupported(attribute.getCategory())) {
            return false;
        }
        if (attribute.getCategory() == Chromaticity.class) {
            return attribute == Chromaticity.COLOR;
        }
        if (attribute.getCategory() == Copies.class) {
            return this.isSupportedCopies((Copies)attribute);
        }
        if (attribute.getCategory() == Media.class && attribute instanceof MediaSizeName) {
            return this.isSupportedMedia((MediaSizeName)attribute);
        }
        if (attribute.getCategory() == OrientationRequested.class) {
            if (attribute == OrientationRequested.REVERSE_PORTRAIT || (docFlavor != null && !docFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && !docFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE))) {
                return false;
            }
        }
        else if (attribute.getCategory() == PageRanges.class) {
            if (docFlavor != null && !docFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && !docFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
                return false;
            }
        }
        else if (attribute.getCategory() == SheetCollate.class) {
            if (docFlavor != null && !docFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && !docFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
                return false;
            }
        }
        else if (attribute.getCategory() == Sides.class && docFlavor != null && !docFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && !docFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
            return false;
        }
        return true;
    }
    
    @Override
    public AttributeSet getUnsupportedAttributes(final DocFlavor docFlavor, final AttributeSet set) {
        if (docFlavor != null && !this.isDocFlavorSupported(docFlavor)) {
            throw new IllegalArgumentException("flavor " + docFlavor + "is not supported");
        }
        if (set == null) {
            return null;
        }
        final HashAttributeSet set2 = new HashAttributeSet();
        final Attribute[] array = set.toArray();
        for (int i = 0; i < array.length; ++i) {
            try {
                final Attribute attribute = array[i];
                if (!this.isAttributeCategorySupported(attribute.getCategory())) {
                    set2.add(attribute);
                }
                else if (!this.isAttributeValueSupported(attribute, docFlavor, set)) {
                    set2.add(attribute);
                }
            }
            catch (final ClassCastException ex) {}
        }
        if (set2.isEmpty()) {
            return null;
        }
        return set2;
    }
    
    @Override
    public ServiceUIFactory getServiceUIFactory() {
        return null;
    }
    
    @Override
    public String toString() {
        return "PSStreamPrintService: " + this.getName();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof PSStreamPrintService && ((PSStreamPrintService)o).getName().equals(this.getName()));
    }
    
    @Override
    public int hashCode() {
        return this.getClass().hashCode() + this.getName().hashCode();
    }
    
    static {
        suppAttrCats = new Class[] { Chromaticity.class, Copies.class, Fidelity.class, JobName.class, Media.class, MediaPrintableArea.class, OrientationRequested.class, PageRanges.class, RequestingUserName.class, SheetCollate.class, Sides.class };
        PSStreamPrintService.MAXCOPIES = 1000;
        mediaSizes = new MediaSizeName[] { MediaSizeName.NA_LETTER, MediaSizeName.TABLOID, MediaSizeName.LEDGER, MediaSizeName.NA_LEGAL, MediaSizeName.EXECUTIVE, MediaSizeName.ISO_A3, MediaSizeName.ISO_A4, MediaSizeName.ISO_A5, MediaSizeName.ISO_B4, MediaSizeName.ISO_B5 };
    }
}
