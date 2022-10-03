package sun.print;

import sun.security.action.GetPropertyAction;
import java.security.Permission;
import java.awt.Paint;
import sun.awt.image.ByteInterleavedRaster;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.print.PrinterAbortException;
import java.io.IOException;
import javax.print.attribute.standard.PrinterIsAcceptingJobs;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.Doc;
import javax.print.attribute.standard.RequestingUserName;
import javax.print.attribute.standard.JobSheets;
import javax.print.attribute.standard.SheetCollate;
import javax.print.attribute.standard.Fidelity;
import java.io.File;
import javax.print.attribute.standard.Destination;
import java.util.Locale;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.Copies;
import javax.print.StreamPrintServiceFactory;
import javax.print.ServiceUI;
import java.io.OutputStream;
import java.awt.Rectangle;
import java.awt.Frame;
import javax.print.attribute.standard.DialogTypeSelection;
import java.security.AccessController;
import java.awt.GraphicsConfiguration;
import java.security.PrivilegedAction;
import java.awt.HeadlessException;
import java.awt.GraphicsEnvironment;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.Media;
import javax.print.attribute.Attribute;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PrinterStateReason;
import javax.print.attribute.standard.PrinterStateReasons;
import javax.print.attribute.standard.PrinterState;
import javax.print.StreamPrintService;
import javax.print.attribute.AttributeSet;
import javax.print.DocFlavor;
import javax.print.PrintServiceLookup;
import java.awt.geom.Rectangle2D;
import java.awt.Shape;
import java.awt.print.PrinterException;
import java.awt.print.Printable;
import java.awt.print.PageFormat;
import java.awt.print.Book;
import java.awt.geom.AffineTransform;
import javax.print.PrintService;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Sides;
import javax.print.attribute.standard.PageRanges;
import java.util.ArrayList;
import java.io.FilePermission;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterJob;

public abstract class RasterPrinterJob extends PrinterJob
{
    protected static final int PRINTER = 0;
    protected static final int FILE = 1;
    protected static final int STREAM = 2;
    protected static final int MAX_UNKNOWN_PAGES = 9999;
    protected static final int PD_ALLPAGES = 0;
    protected static final int PD_SELECTION = 1;
    protected static final int PD_PAGENUMS = 2;
    protected static final int PD_NOSELECTION = 4;
    private static final int MAX_BAND_SIZE = 4194304;
    private static final float DPI = 72.0f;
    private static final String FORCE_PIPE_PROP = "sun.java2d.print.pipeline";
    private static final String FORCE_RASTER = "raster";
    private static final String FORCE_PDL = "pdl";
    private static final String SHAPE_TEXT_PROP = "sun.java2d.print.shapetext";
    public static boolean forcePDL;
    public static boolean forceRaster;
    public static boolean shapeTextProp;
    private int cachedBandWidth;
    private int cachedBandHeight;
    private BufferedImage cachedBand;
    private int mNumCopies;
    private boolean mCollate;
    private int mFirstPage;
    private int mLastPage;
    private Paper previousPaper;
    protected Pageable mDocument;
    private String mDocName;
    protected boolean performingPrinting;
    protected boolean userCancelled;
    private FilePermission printToFilePermission;
    private ArrayList redrawList;
    private int copiesAttr;
    private String jobNameAttr;
    private String userNameAttr;
    private PageRanges pageRangesAttr;
    protected Sides sidesAttr;
    protected String destinationAttr;
    protected boolean noJobSheet;
    protected int mDestType;
    protected String mDestination;
    protected boolean collateAttReq;
    protected boolean landscapeRotates270;
    protected PrintRequestAttributeSet attributes;
    protected PrintService myService;
    public static boolean debugPrint;
    private int deviceWidth;
    private int deviceHeight;
    private AffineTransform defaultDeviceTransform;
    private PrinterGraphicsConfig pgConfig;
    private DialogOnTop onTop;
    private long parentWindowID;
    
    public RasterPrinterJob() {
        this.cachedBandWidth = 0;
        this.cachedBandHeight = 0;
        this.cachedBand = null;
        this.mNumCopies = 1;
        this.mCollate = false;
        this.mFirstPage = -1;
        this.mLastPage = -1;
        this.mDocument = new Book();
        this.mDocName = "Java Printing";
        this.performingPrinting = false;
        this.userCancelled = false;
        this.redrawList = new ArrayList();
        this.noJobSheet = false;
        this.mDestType = 1;
        this.mDestination = "";
        this.collateAttReq = false;
        this.landscapeRotates270 = false;
        this.attributes = null;
        this.onTop = null;
        this.parentWindowID = 0L;
    }
    
    protected abstract double getXRes();
    
    protected abstract double getYRes();
    
    protected abstract double getPhysicalPrintableX(final Paper p0);
    
    protected abstract double getPhysicalPrintableY(final Paper p0);
    
    protected abstract double getPhysicalPrintableWidth(final Paper p0);
    
    protected abstract double getPhysicalPrintableHeight(final Paper p0);
    
    protected abstract double getPhysicalPageWidth(final Paper p0);
    
    protected abstract double getPhysicalPageHeight(final Paper p0);
    
    protected abstract void startPage(final PageFormat p0, final Printable p1, final int p2, final boolean p3) throws PrinterException;
    
    protected abstract void endPage(final PageFormat p0, final Printable p1, final int p2) throws PrinterException;
    
    protected abstract void printBand(final byte[] p0, final int p1, final int p2, final int p3, final int p4) throws PrinterException;
    
    public void saveState(final AffineTransform theTransform, final Shape theClip, final Rectangle2D region, final double sx, final double sy) {
        final GraphicsState graphicsState = new GraphicsState();
        graphicsState.theTransform = theTransform;
        graphicsState.theClip = theClip;
        graphicsState.region = region;
        graphicsState.sx = sx;
        graphicsState.sy = sy;
        this.redrawList.add(graphicsState);
    }
    
    protected static PrintService lookupDefaultPrintService() {
        final PrintService lookupDefaultPrintService = PrintServiceLookup.lookupDefaultPrintService();
        if (lookupDefaultPrintService != null && lookupDefaultPrintService.isDocFlavorSupported(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && lookupDefaultPrintService.isDocFlavorSupported(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
            return lookupDefaultPrintService;
        }
        final PrintService[] lookupPrintServices = PrintServiceLookup.lookupPrintServices(DocFlavor.SERVICE_FORMATTED.PAGEABLE, null);
        if (lookupPrintServices.length > 0) {
            return lookupPrintServices[0];
        }
        return null;
    }
    
    @Override
    public PrintService getPrintService() {
        if (this.myService == null) {
            final PrintService lookupDefaultPrintService = PrintServiceLookup.lookupDefaultPrintService();
            if (lookupDefaultPrintService != null && lookupDefaultPrintService.isDocFlavorSupported(DocFlavor.SERVICE_FORMATTED.PAGEABLE)) {
                try {
                    this.setPrintService(lookupDefaultPrintService);
                    this.myService = lookupDefaultPrintService;
                }
                catch (final PrinterException ex) {}
            }
            if (this.myService == null) {
                final PrintService[] lookupPrintServices = PrintServiceLookup.lookupPrintServices(DocFlavor.SERVICE_FORMATTED.PAGEABLE, null);
                if (lookupPrintServices.length > 0) {
                    try {
                        this.setPrintService(lookupPrintServices[0]);
                        this.myService = lookupPrintServices[0];
                    }
                    catch (final PrinterException ex2) {}
                }
            }
        }
        return this.myService;
    }
    
    @Override
    public void setPrintService(final PrintService myService) throws PrinterException {
        if (myService == null) {
            throw new PrinterException("Service cannot be null");
        }
        if (!(myService instanceof StreamPrintService) && myService.getName() == null) {
            throw new PrinterException("Null PrintService name.");
        }
        if (myService.getAttribute(PrinterState.class) == PrinterState.STOPPED) {
            final PrinterStateReasons printerStateReasons = myService.getAttribute(PrinterStateReasons.class);
            if (printerStateReasons != null && printerStateReasons.containsKey(PrinterStateReason.SHUTDOWN)) {
                throw new PrinterException("PrintService is no longer available.");
            }
        }
        if (myService.isDocFlavorSupported(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && myService.isDocFlavorSupported(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
            this.myService = myService;
            return;
        }
        throw new PrinterException("Not a 2D print service: " + myService);
    }
    
    private PageFormat attributeToPageFormat(final PrintService printService, final PrintRequestAttributeSet set) {
        final PageFormat defaultPage = this.defaultPage();
        if (printService == null) {
            return defaultPage;
        }
        OrientationRequested orientationRequested = (OrientationRequested)set.get(OrientationRequested.class);
        if (orientationRequested == null) {
            orientationRequested = (OrientationRequested)printService.getDefaultAttributeValue(OrientationRequested.class);
        }
        if (orientationRequested == OrientationRequested.REVERSE_LANDSCAPE) {
            defaultPage.setOrientation(2);
        }
        else if (orientationRequested == OrientationRequested.LANDSCAPE) {
            defaultPage.setOrientation(0);
        }
        else {
            defaultPage.setOrientation(1);
        }
        final MediaSize mediaSize = this.getMediaSize((Media)set.get(Media.class), printService, defaultPage);
        final Paper paper = new Paper();
        final float[] size = mediaSize.getSize(1);
        final double rint = Math.rint(size[0] * 72.0 / 25400.0);
        final double rint2 = Math.rint(size[1] * 72.0 / 25400.0);
        paper.setSize(rint, rint2);
        MediaPrintableArea defaultPrintableArea = (MediaPrintableArea)set.get(MediaPrintableArea.class);
        if (defaultPrintableArea == null) {
            defaultPrintableArea = this.getDefaultPrintableArea(defaultPage, rint, rint2);
        }
        paper.setImageableArea(Math.rint(defaultPrintableArea.getX(25400) * 72.0f), Math.rint(defaultPrintableArea.getY(25400) * 72.0f), Math.rint(defaultPrintableArea.getWidth(25400) * 72.0f), Math.rint(defaultPrintableArea.getHeight(25400) * 72.0f));
        defaultPage.setPaper(paper);
        return defaultPage;
    }
    
    protected MediaSize getMediaSize(Media na_LETTER, final PrintService printService, final PageFormat pageFormat) {
        if (na_LETTER == null) {
            na_LETTER = (Media)printService.getDefaultAttributeValue(Media.class);
        }
        if (!(na_LETTER instanceof MediaSizeName)) {
            na_LETTER = MediaSizeName.NA_LETTER;
        }
        final MediaSize mediaSizeForName = MediaSize.getMediaSizeForName((MediaSizeName)na_LETTER);
        return (mediaSizeForName != null) ? mediaSizeForName : MediaSize.NA.LETTER;
    }
    
    protected MediaPrintableArea getDefaultPrintableArea(final PageFormat pageFormat, final double n, final double n2) {
        double n3;
        double n4;
        if (n >= 432.0) {
            n3 = 72.0;
            n4 = n - 144.0;
        }
        else {
            n3 = n / 6.0;
            n4 = n * 0.75;
        }
        double n5;
        double n6;
        if (n2 >= 432.0) {
            n5 = 72.0;
            n6 = n2 - 144.0;
        }
        else {
            n5 = n2 / 6.0;
            n6 = n2 * 0.75;
        }
        return new MediaPrintableArea((float)(n3 / 72.0), (float)(n5 / 72.0), (float)(n4 / 72.0), (float)(n6 / 72.0), 25400);
    }
    
    protected void updatePageAttributes(final PrintService printService, final PageFormat pageFormat) {
        if (this.attributes == null) {
            this.attributes = new HashPrintRequestAttributeSet();
        }
        this.updateAttributesWithPageFormat(printService, pageFormat, this.attributes);
    }
    
    protected void updateAttributesWithPageFormat(final PrintService printService, final PageFormat pageFormat, final PrintRequestAttributeSet set) {
        if (printService == null || pageFormat == null || set == null) {
            return;
        }
        final float n = (float)Math.rint(pageFormat.getPaper().getWidth() * 25400.0 / 72.0) / 25400.0f;
        final float n2 = (float)Math.rint(pageFormat.getPaper().getHeight() * 25400.0 / 72.0) / 25400.0f;
        final Media[] array = (Media[])printService.getSupportedAttributeValues(Media.class, null, null);
        Media media = null;
        try {
            media = CustomMediaSizeName.findMedia(array, n, n2, 25400);
        }
        catch (final IllegalArgumentException ex) {}
        if (media == null || !printService.isAttributeValueSupported(media, null, null)) {
            media = (Media)printService.getDefaultAttributeValue(Media.class);
        }
        OrientationRequested orientationRequested = null;
        switch (pageFormat.getOrientation()) {
            case 0: {
                orientationRequested = OrientationRequested.LANDSCAPE;
                break;
            }
            case 2: {
                orientationRequested = OrientationRequested.REVERSE_LANDSCAPE;
                break;
            }
            default: {
                orientationRequested = OrientationRequested.PORTRAIT;
                break;
            }
        }
        if (media != null) {
            set.add(media);
        }
        set.add(orientationRequested);
        float n3 = (float)(pageFormat.getPaper().getImageableX() / 72.0);
        final float n4 = (float)(pageFormat.getPaper().getImageableWidth() / 72.0);
        float n5 = (float)(pageFormat.getPaper().getImageableY() / 72.0);
        final float n6 = (float)(pageFormat.getPaper().getImageableHeight() / 72.0);
        if (n3 < 0.0f) {
            n3 = 0.0f;
        }
        if (n5 < 0.0f) {
            n5 = 0.0f;
        }
        try {
            set.add(new MediaPrintableArea(n3, n5, n4, n6, 25400));
        }
        catch (final IllegalArgumentException ex2) {}
    }
    
    @Override
    public PageFormat pageDialog(final PageFormat pageFormat) throws HeadlessException {
        if (GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException();
        }
        final PrintService printService = AccessController.doPrivileged((PrivilegedAction<PrintService>)new PrivilegedAction() {
            final /* synthetic */ GraphicsConfiguration val$gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
            
            @Override
            public Object run() {
                final PrintService printService = RasterPrinterJob.this.getPrintService();
                if (printService == null) {
                    ServiceDialog.showNoPrintService(this.val$gc);
                    return null;
                }
                return printService;
            }
        });
        if (printService == null) {
            return pageFormat;
        }
        this.updatePageAttributes(printService, pageFormat);
        PageFormat pageFormat2;
        if (this.attributes.get(DialogTypeSelection.class) == DialogTypeSelection.NATIVE) {
            this.attributes.remove(DialogTypeSelection.class);
            pageFormat2 = this.pageDialog(this.attributes);
            this.attributes.add(DialogTypeSelection.NATIVE);
        }
        else {
            pageFormat2 = this.pageDialog(this.attributes);
        }
        if (pageFormat2 == null) {
            return pageFormat;
        }
        return pageFormat2;
    }
    
    @Override
    public PageFormat pageDialog(final PrintRequestAttributeSet parentWindowID) throws HeadlessException {
        if (GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException();
        }
        if (parentWindowID.get(DialogTypeSelection.class) == DialogTypeSelection.NATIVE) {
            final PrintService printService = this.getPrintService();
            final PageFormat attributeToPageFormat = this.attributeToPageFormat(printService, parentWindowID);
            this.setParentWindowID(parentWindowID);
            final PageFormat pageDialog = this.pageDialog(attributeToPageFormat);
            this.clearParentWindowID();
            if (pageDialog == attributeToPageFormat) {
                return null;
            }
            this.updateAttributesWithPageFormat(printService, pageDialog, parentWindowID);
            return pageDialog;
        }
        else {
            final GraphicsConfiguration defaultConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
            final Rectangle bounds = defaultConfiguration.getBounds();
            final int n = bounds.x + bounds.width / 3;
            final int n2 = bounds.y + bounds.height / 3;
            final PrintService printService2 = AccessController.doPrivileged((PrivilegedAction<PrintService>)new PrivilegedAction() {
                @Override
                public Object run() {
                    final PrintService printService = RasterPrinterJob.this.getPrintService();
                    if (printService == null) {
                        ServiceDialog.showNoPrintService(defaultConfiguration);
                        return null;
                    }
                    return printService;
                }
            });
            if (printService2 == null) {
                return null;
            }
            if (this.onTop != null) {
                parentWindowID.add(this.onTop);
            }
            final ServiceDialog serviceDialog = new ServiceDialog(defaultConfiguration, n, n2, printService2, DocFlavor.SERVICE_FORMATTED.PAGEABLE, parentWindowID, (Frame)null);
            serviceDialog.show();
            if (serviceDialog.getStatus() == 1) {
                final PrintRequestAttributeSet attributes = serviceDialog.getAttributes();
                final Class<SunAlternateMedia> clazz = SunAlternateMedia.class;
                if (parentWindowID.containsKey(clazz) && !attributes.containsKey(clazz)) {
                    parentWindowID.remove(clazz);
                }
                parentWindowID.addAll(attributes);
                return this.attributeToPageFormat(printService2, parentWindowID);
            }
            return null;
        }
    }
    
    protected PageFormat getPageFormatFromAttributes() {
        final Pageable pageable;
        if (this.attributes == null || this.attributes.isEmpty() || !((pageable = this.getPageable()) instanceof OpenBook)) {
            return null;
        }
        final PageFormat attributeToPageFormat = this.attributeToPageFormat(this.getPrintService(), this.attributes);
        final PageFormat pageFormat;
        if ((pageFormat = pageable.getPageFormat(0)) != null) {
            if (this.attributes.get(OrientationRequested.class) == null) {
                attributeToPageFormat.setOrientation(pageFormat.getOrientation());
            }
            final Paper paper = attributeToPageFormat.getPaper();
            final Paper paper2 = pageFormat.getPaper();
            boolean b = false;
            if (this.attributes.get(MediaSizeName.class) == null) {
                paper.setSize(paper2.getWidth(), paper2.getHeight());
                b = true;
            }
            if (this.attributes.get(MediaPrintableArea.class) == null) {
                paper.setImageableArea(paper2.getImageableX(), paper2.getImageableY(), paper2.getImageableWidth(), paper2.getImageableHeight());
                b = true;
            }
            if (b) {
                attributeToPageFormat.setPaper(paper);
            }
        }
        return attributeToPageFormat;
    }
    
    @Override
    public boolean printDialog(final PrintRequestAttributeSet set) throws HeadlessException {
        if (GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException();
        }
        if (set.get(DialogTypeSelection.class) == DialogTypeSelection.NATIVE) {
            this.attributes = set;
            try {
                this.debug_println("calling setAttributes in printDialog");
                this.setAttributes(set);
            }
            catch (final PrinterException ex) {}
            this.setParentWindowID(set);
            final boolean printDialog = this.printDialog();
            this.clearParentWindowID();
            this.attributes = set;
            return printDialog;
        }
        final GraphicsConfiguration defaultConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        final PrintService printService = AccessController.doPrivileged((PrivilegedAction<PrintService>)new PrivilegedAction() {
            @Override
            public Object run() {
                final PrintService printService = RasterPrinterJob.this.getPrintService();
                if (printService == null) {
                    ServiceDialog.showNoPrintService(defaultConfiguration);
                    return null;
                }
                return printService;
            }
        });
        if (printService == null) {
            return false;
        }
        PrintService[] array;
        if (printService instanceof StreamPrintService) {
            final StreamPrintServiceFactory[] lookupStreamPrintServices = PrinterJob.lookupStreamPrintServices(null);
            array = new StreamPrintService[lookupStreamPrintServices.length];
            for (int i = 0; i < lookupStreamPrintServices.length; ++i) {
                array[i] = lookupStreamPrintServices[i].getPrintService(null);
            }
        }
        else {
            array = AccessController.doPrivileged((PrivilegedAction<StreamPrintService[]>)new PrivilegedAction() {
                @Override
                public Object run() {
                    return PrinterJob.lookupPrintServices();
                }
            });
            if (array == null || array.length == 0) {
                array = new PrintService[] { printService };
            }
        }
        final Rectangle bounds = defaultConfiguration.getBounds();
        final int n = bounds.x + bounds.width / 3;
        final int n2 = bounds.y + bounds.height / 3;
        set.add(new PrinterJobWrapper(this));
        PrintService printService2;
        try {
            printService2 = ServiceUI.printDialog(defaultConfiguration, n, n2, array, printService, DocFlavor.SERVICE_FORMATTED.PAGEABLE, set);
        }
        catch (final IllegalArgumentException ex2) {
            printService2 = ServiceUI.printDialog(defaultConfiguration, n, n2, array, array[0], DocFlavor.SERVICE_FORMATTED.PAGEABLE, set);
        }
        set.remove(PrinterJobWrapper.class);
        if (printService2 == null) {
            return false;
        }
        if (!printService.equals(printService2)) {
            try {
                this.setPrintService(printService2);
            }
            catch (final PrinterException ex3) {
                this.myService = printService2;
            }
        }
        return true;
    }
    
    @Override
    public boolean printDialog() throws HeadlessException {
        if (GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException();
        }
        final HashPrintRequestAttributeSet set = new HashPrintRequestAttributeSet();
        set.add(new Copies(this.getCopies()));
        set.add(new JobName(this.getJobName(), null));
        final boolean printDialog = this.printDialog(set);
        if (printDialog) {
            final JobName jobName = (JobName)set.get(JobName.class);
            if (jobName != null) {
                this.setJobName(jobName.getValue());
            }
            final Copies copies = (Copies)set.get(Copies.class);
            if (copies != null) {
                this.setCopies(copies.getValue());
            }
            final Destination destination = (Destination)set.get(Destination.class);
            if (destination != null) {
                try {
                    this.mDestType = 1;
                    this.mDestination = new File(destination.getURI()).getPath();
                }
                catch (final Exception ex) {
                    this.mDestination = "out.prn";
                    final PrintService printService = this.getPrintService();
                    if (printService != null) {
                        final Destination destination2 = (Destination)printService.getDefaultAttributeValue(Destination.class);
                        if (destination2 != null) {
                            this.mDestination = new File(destination2.getURI()).getPath();
                        }
                    }
                }
            }
            else {
                this.mDestType = 0;
                final PrintService printService2 = this.getPrintService();
                if (printService2 != null) {
                    this.mDestination = printService2.getName();
                }
            }
        }
        return printDialog;
    }
    
    @Override
    public void setPrintable(final Printable printable) {
        this.setPageable(new OpenBook(this.defaultPage(new PageFormat()), printable));
    }
    
    @Override
    public void setPrintable(final Printable printable, final PageFormat pageFormat) {
        this.setPageable(new OpenBook(pageFormat, printable));
        this.updatePageAttributes(this.getPrintService(), pageFormat);
    }
    
    @Override
    public void setPageable(final Pageable mDocument) throws NullPointerException {
        if (mDocument != null) {
            this.mDocument = mDocument;
            return;
        }
        throw new NullPointerException();
    }
    
    protected void initPrinter() {
    }
    
    protected boolean isSupportedValue(final Attribute attribute, final PrintRequestAttributeSet set) {
        final PrintService printService = this.getPrintService();
        return attribute != null && printService != null && printService.isAttributeValueSupported(attribute, DocFlavor.SERVICE_FORMATTED.PAGEABLE, set);
    }
    
    protected void setAttributes(final PrintRequestAttributeSet attributes) throws PrinterException {
        this.setCollated(false);
        this.sidesAttr = null;
        this.pageRangesAttr = null;
        this.copiesAttr = 0;
        this.jobNameAttr = null;
        this.userNameAttr = null;
        this.destinationAttr = null;
        this.collateAttReq = false;
        final PrintService printService = this.getPrintService();
        if (attributes == null || printService == null) {
            return;
        }
        boolean b = false;
        final Fidelity fidelity = (Fidelity)attributes.get(Fidelity.class);
        if (fidelity != null && fidelity == Fidelity.FIDELITY_TRUE) {
            b = true;
        }
        if (b && printService.getUnsupportedAttributes(DocFlavor.SERVICE_FORMATTED.PAGEABLE, attributes) != null) {
            throw new PrinterException("Fidelity cannot be satisfied");
        }
        final SheetCollate sheetCollate = (SheetCollate)attributes.get(SheetCollate.class);
        if (this.isSupportedValue(sheetCollate, attributes)) {
            this.setCollated(sheetCollate == SheetCollate.COLLATED);
        }
        this.sidesAttr = (Sides)attributes.get(Sides.class);
        if (!this.isSupportedValue(this.sidesAttr, attributes)) {
            this.sidesAttr = Sides.ONE_SIDED;
        }
        this.pageRangesAttr = (PageRanges)attributes.get(PageRanges.class);
        if (!this.isSupportedValue(this.pageRangesAttr, attributes)) {
            this.pageRangesAttr = null;
        }
        else if (attributes.get(SunPageSelection.class) == SunPageSelection.RANGE) {
            final int[][] members = this.pageRangesAttr.getMembers();
            this.setPageRange(members[0][0] - 1, members[0][1] - 1);
        }
        else {
            this.setPageRange(-1, -1);
        }
        final Copies copies = (Copies)attributes.get(Copies.class);
        if (this.isSupportedValue(copies, attributes) || (!b && copies != null)) {
            this.setCopies(this.copiesAttr = copies.getValue());
        }
        else {
            this.copiesAttr = this.getCopies();
        }
        final Destination destination = (Destination)attributes.get(Destination.class);
        if (this.isSupportedValue(destination, attributes)) {
            try {
                this.destinationAttr = "" + new File(destination.getURI().getSchemeSpecificPart());
            }
            catch (final Exception ex) {
                final Destination destination2 = (Destination)printService.getDefaultAttributeValue(Destination.class);
                if (destination2 != null) {
                    this.destinationAttr = "" + new File(destination2.getURI().getSchemeSpecificPart());
                }
            }
        }
        final JobSheets jobSheets = (JobSheets)attributes.get(JobSheets.class);
        if (jobSheets != null) {
            this.noJobSheet = (jobSheets == JobSheets.NONE);
        }
        final JobName jobName = (JobName)attributes.get(JobName.class);
        if (this.isSupportedValue(jobName, attributes) || (!b && jobName != null)) {
            this.setJobName(this.jobNameAttr = jobName.getValue());
        }
        else {
            this.jobNameAttr = this.getJobName();
        }
        final RequestingUserName requestingUserName = (RequestingUserName)attributes.get(RequestingUserName.class);
        if (this.isSupportedValue(requestingUserName, attributes) || (!b && requestingUserName != null)) {
            this.userNameAttr = requestingUserName.getValue();
        }
        else {
            try {
                this.userNameAttr = this.getUserName();
            }
            catch (final SecurityException ex2) {
                this.userNameAttr = "";
            }
        }
        final Media media = (Media)attributes.get(Media.class);
        final OrientationRequested orientationRequested = (OrientationRequested)attributes.get(OrientationRequested.class);
        MediaPrintableArea mediaPrintableArea = (MediaPrintableArea)attributes.get(MediaPrintableArea.class);
        if ((orientationRequested != null || media != null || mediaPrintableArea != null) && this.getPageable() instanceof OpenBook) {
            final Pageable pageable = this.getPageable();
            final Printable printable = pageable.getPrintable(0);
            final PageFormat pageFormat = (PageFormat)pageable.getPageFormat(0).clone();
            final Paper paper = pageFormat.getPaper();
            if (mediaPrintableArea == null && media != null && printService.isAttributeCategorySupported(MediaPrintableArea.class)) {
                final Object supportedAttributeValues = printService.getSupportedAttributeValues(MediaPrintableArea.class, null, attributes);
                if (supportedAttributeValues instanceof MediaPrintableArea[] && ((MediaPrintableArea[])supportedAttributeValues).length > 0) {
                    mediaPrintableArea = ((MediaPrintableArea[])supportedAttributeValues)[0];
                }
            }
            if (this.isSupportedValue(orientationRequested, attributes) || (!b && orientationRequested != null)) {
                int orientation;
                if (orientationRequested.equals(OrientationRequested.REVERSE_LANDSCAPE)) {
                    orientation = 2;
                }
                else if (orientationRequested.equals(OrientationRequested.LANDSCAPE)) {
                    orientation = 0;
                }
                else {
                    orientation = 1;
                }
                pageFormat.setOrientation(orientation);
            }
            if ((this.isSupportedValue(media, attributes) || (!b && media != null)) && media instanceof MediaSizeName) {
                final MediaSize mediaSizeForName = MediaSize.getMediaSizeForName((MediaSizeName)media);
                if (mediaSizeForName != null) {
                    final float n = mediaSizeForName.getX(25400) * 72.0f;
                    final float n2 = mediaSizeForName.getY(25400) * 72.0f;
                    paper.setSize(n, n2);
                    if (mediaPrintableArea == null) {
                        paper.setImageableArea(72.0, 72.0, n - 144.0, n2 - 144.0);
                    }
                }
            }
            if (this.isSupportedValue(mediaPrintableArea, attributes) || (!b && mediaPrintableArea != null)) {
                final float[] printableArea = mediaPrintableArea.getPrintableArea(25400);
                for (int i = 0; i < printableArea.length; ++i) {
                    printableArea[i] *= 72.0f;
                }
                paper.setImageableArea(printableArea[0], printableArea[1], printableArea[2], printableArea[3]);
            }
            pageFormat.setPaper(paper);
            this.setPrintable(printable, this.validatePage(pageFormat));
        }
        else {
            this.attributes = attributes;
        }
    }
    
    protected void spoolToService(final PrintService printService, PrintRequestAttributeSet set) throws PrinterException {
        if (printService == null) {
            throw new PrinterException("No print service found.");
        }
        final DocPrintJob printJob = printService.createPrintJob();
        final PageableDoc pageableDoc = new PageableDoc(this.getPageable());
        if (set == null) {
            set = new HashPrintRequestAttributeSet();
        }
        try {
            printJob.print(pageableDoc, set);
        }
        catch (final PrintException ex) {
            throw new PrinterException(ex.toString());
        }
    }
    
    @Override
    public void print() throws PrinterException {
        this.print(this.attributes);
    }
    
    protected void debug_println(final String s) {
        if (RasterPrinterJob.debugPrint) {
            System.out.println("RasterPrinterJob " + s + " " + this);
        }
    }
    
    @Override
    public void print(final PrintRequestAttributeSet attributes) throws PrinterException {
        final PrintService printService = this.getPrintService();
        this.debug_println("psvc = " + printService);
        if (printService == null) {
            throw new PrinterException("No print service found.");
        }
        if (printService.getAttribute(PrinterState.class) == PrinterState.STOPPED) {
            final PrinterStateReasons printerStateReasons = printService.getAttribute(PrinterStateReasons.class);
            if (printerStateReasons != null && printerStateReasons.containsKey(PrinterStateReason.SHUTDOWN)) {
                throw new PrinterException("PrintService is no longer available.");
            }
        }
        if (printService.getAttribute(PrinterIsAcceptingJobs.class) == PrinterIsAcceptingJobs.NOT_ACCEPTING_JOBS) {
            throw new PrinterException("Printer is not accepting job.");
        }
        if (!(printService instanceof SunPrinterJobService) || !((SunPrinterJobService)printService).usesClass(this.getClass())) {
            this.spoolToService(printService, attributes);
            return;
        }
        this.setAttributes(attributes);
        if (this.destinationAttr != null) {
            this.validateDestination(this.destinationAttr);
        }
        this.initPrinter();
        final int collatedCopies = this.getCollatedCopies();
        final int noncollatedCopies = this.getNoncollatedCopies();
        this.debug_println("getCollatedCopies()  " + collatedCopies + " getNoncollatedCopies() " + noncollatedCopies);
        if (this.mDocument.getNumberOfPages() == 0) {
            return;
        }
        final int firstPage = this.getFirstPage();
        int lastPage = this.getLastPage();
        if (lastPage == -1 && this.mDocument.getNumberOfPages() != -1) {
            lastPage = this.mDocument.getNumberOfPages() - 1;
        }
        try {
            synchronized (this) {
                this.performingPrinting = true;
                this.userCancelled = false;
            }
            this.startDoc();
            if (this.isCancelled()) {
                this.cancelDoc();
            }
            boolean b = true;
            if (attributes != null) {
                final SunPageSelection sunPageSelection = (SunPageSelection)attributes.get(SunPageSelection.class);
                if (sunPageSelection != null && sunPageSelection != SunPageSelection.RANGE) {
                    b = false;
                }
            }
            this.debug_println("after startDoc rangeSelected? " + b + " numNonCollatedCopies " + noncollatedCopies);
            for (int i = 0; i < collatedCopies; ++i) {
                for (int n = firstPage, printPage = 0; (n <= lastPage || lastPage == -1) && printPage == 0; ++n) {
                    if (this.pageRangesAttr != null && b) {
                        final int next = this.pageRangesAttr.next(n);
                        if (next == -1) {
                            break;
                        }
                        if (next != n + 1) {
                            continue;
                        }
                    }
                    for (int n2 = 0; n2 < noncollatedCopies && printPage == 0; printPage = this.printPage(this.mDocument, n), ++n2) {
                        if (this.isCancelled()) {
                            this.cancelDoc();
                        }
                        this.debug_println("printPage " + n);
                    }
                }
            }
            if (this.isCancelled()) {
                this.cancelDoc();
            }
        }
        finally {
            this.previousPaper = null;
            synchronized (this) {
                if (this.performingPrinting) {
                    this.endDoc();
                }
                this.performingPrinting = false;
                this.notify();
            }
        }
    }
    
    protected void validateDestination(final String s) throws PrinterException {
        if (s == null) {
            return;
        }
        final File file = new File(s);
        try {
            if (file.createNewFile()) {
                file.delete();
            }
        }
        catch (final IOException ex) {
            throw new PrinterException("Cannot write to file:" + s);
        }
        catch (final SecurityException ex2) {}
        final File parentFile = file.getParentFile();
        if ((file.exists() && (!file.isFile() || !file.canWrite())) || (parentFile != null && (!parentFile.exists() || (parentFile.exists() && !parentFile.canWrite())))) {
            throw new PrinterException("Cannot write to file:" + s);
        }
    }
    
    protected void validatePaper(final Paper paper, final Paper paper2) {
        if (paper == null || paper2 == null) {
            return;
        }
        final double width = paper.getWidth();
        final double height = paper.getHeight();
        final double imageableX = paper.getImageableX();
        final double imageableY = paper.getImageableY();
        final double imageableWidth = paper.getImageableWidth();
        final double imageableHeight = paper.getImageableHeight();
        final Paper paper3 = new Paper();
        final double n = (width > 0.0) ? width : paper3.getWidth();
        final double n2 = (height > 0.0) ? height : paper3.getHeight();
        double n3 = (imageableX > 0.0) ? imageableX : paper3.getImageableX();
        double n4 = (imageableY > 0.0) ? imageableY : paper3.getImageableY();
        double n5 = (imageableWidth > 0.0) ? imageableWidth : paper3.getImageableWidth();
        double n6 = (imageableHeight > 0.0) ? imageableHeight : paper3.getImageableHeight();
        if (n5 > n) {
            n5 = n;
        }
        if (n6 > n2) {
            n6 = n2;
        }
        if (n3 + n5 > n) {
            n3 = n - n5;
        }
        if (n4 + n6 > n2) {
            n4 = n2 - n6;
        }
        paper2.setSize(n, n2);
        paper2.setImageableArea(n3, n4, n5, n6);
    }
    
    @Override
    public PageFormat defaultPage(final PageFormat pageFormat) {
        final PageFormat pageFormat2 = (PageFormat)pageFormat.clone();
        pageFormat2.setOrientation(1);
        final Paper paper = new Paper();
        final double n = 72.0;
        final PrintService printService = this.getPrintService();
        if (printService != null) {
            final Media media = (Media)printService.getDefaultAttributeValue(Media.class);
            final MediaSize mediaSizeForName;
            if (media instanceof MediaSizeName && (mediaSizeForName = MediaSize.getMediaSizeForName((MediaSizeName)media)) != null) {
                final double n2 = mediaSizeForName.getX(25400) * n;
                final double n3 = mediaSizeForName.getY(25400) * n;
                paper.setSize(n2, n3);
                paper.setImageableArea(n, n, n2 - 2.0 * n, n3 - 2.0 * n);
                pageFormat2.setPaper(paper);
                return pageFormat2;
            }
        }
        final String country = Locale.getDefault().getCountry();
        if (!Locale.getDefault().equals(Locale.ENGLISH) && country != null && !country.equals(Locale.US.getCountry()) && !country.equals(Locale.CANADA.getCountry())) {
            final double n4 = 25.4;
            final double rint = Math.rint(210.0 * n / n4);
            final double rint2 = Math.rint(297.0 * n / n4);
            paper.setSize(rint, rint2);
            paper.setImageableArea(n, n, rint - 2.0 * n, rint2 - 2.0 * n);
        }
        pageFormat2.setPaper(paper);
        return pageFormat2;
    }
    
    @Override
    public PageFormat validatePage(final PageFormat pageFormat) {
        final PageFormat pageFormat2 = (PageFormat)pageFormat.clone();
        final Paper paper = new Paper();
        this.validatePaper(pageFormat2.getPaper(), paper);
        pageFormat2.setPaper(paper);
        return pageFormat2;
    }
    
    @Override
    public void setCopies(final int mNumCopies) {
        this.mNumCopies = mNumCopies;
    }
    
    @Override
    public int getCopies() {
        return this.mNumCopies;
    }
    
    protected int getCopiesInt() {
        return (this.copiesAttr > 0) ? this.copiesAttr : this.getCopies();
    }
    
    @Override
    public String getUserName() {
        return System.getProperty("user.name");
    }
    
    protected String getUserNameInt() {
        if (this.userNameAttr != null) {
            return this.userNameAttr;
        }
        try {
            return this.getUserName();
        }
        catch (final SecurityException ex) {
            return "";
        }
    }
    
    @Override
    public void setJobName(final String mDocName) {
        if (mDocName != null) {
            this.mDocName = mDocName;
            return;
        }
        throw new NullPointerException();
    }
    
    @Override
    public String getJobName() {
        return this.mDocName;
    }
    
    protected String getJobNameInt() {
        return (this.jobNameAttr != null) ? this.jobNameAttr : this.getJobName();
    }
    
    protected void setPageRange(final int mFirstPage, final int mLastPage) {
        if (mFirstPage >= 0 && mLastPage >= 0) {
            this.mFirstPage = mFirstPage;
            this.mLastPage = mLastPage;
            if (this.mLastPage < this.mFirstPage) {
                this.mLastPage = this.mFirstPage;
            }
        }
        else {
            this.mFirstPage = -1;
            this.mLastPage = -1;
        }
    }
    
    protected int getFirstPage() {
        return (this.mFirstPage == -1) ? 0 : this.mFirstPage;
    }
    
    protected int getLastPage() {
        return this.mLastPage;
    }
    
    protected void setCollated(final boolean mCollate) {
        this.mCollate = mCollate;
        this.collateAttReq = true;
    }
    
    protected boolean isCollated() {
        return this.mCollate;
    }
    
    protected final int getSelectAttrib() {
        if (this.attributes != null) {
            final SunPageSelection sunPageSelection = (SunPageSelection)this.attributes.get(SunPageSelection.class);
            if (sunPageSelection == SunPageSelection.RANGE) {
                return 2;
            }
            if (sunPageSelection == SunPageSelection.SELECTION) {
                return 1;
            }
            if (sunPageSelection == SunPageSelection.ALL) {
                return 0;
            }
        }
        return 4;
    }
    
    protected final int getFromPageAttrib() {
        if (this.attributes != null) {
            final PageRanges pageRanges = (PageRanges)this.attributes.get(PageRanges.class);
            if (pageRanges != null) {
                return pageRanges.getMembers()[0][0];
            }
        }
        return this.getMinPageAttrib();
    }
    
    protected final int getToPageAttrib() {
        if (this.attributes != null) {
            final PageRanges pageRanges = (PageRanges)this.attributes.get(PageRanges.class);
            if (pageRanges != null) {
                final int[][] members = pageRanges.getMembers();
                return members[members.length - 1][1];
            }
        }
        return this.getMaxPageAttrib();
    }
    
    protected final int getMinPageAttrib() {
        if (this.attributes != null) {
            final SunMinMaxPage sunMinMaxPage = (SunMinMaxPage)this.attributes.get(SunMinMaxPage.class);
            if (sunMinMaxPage != null) {
                return sunMinMaxPage.getMin();
            }
        }
        return 1;
    }
    
    protected final int getMaxPageAttrib() {
        if (this.attributes != null) {
            final SunMinMaxPage sunMinMaxPage = (SunMinMaxPage)this.attributes.get(SunMinMaxPage.class);
            if (sunMinMaxPage != null) {
                return sunMinMaxPage.getMax();
            }
        }
        final Pageable pageable = this.getPageable();
        if (pageable != null) {
            int numberOfPages = pageable.getNumberOfPages();
            if (numberOfPages <= -1) {
                numberOfPages = 9999;
            }
            return (numberOfPages == 0) ? 1 : numberOfPages;
        }
        return Integer.MAX_VALUE;
    }
    
    protected abstract void startDoc() throws PrinterException;
    
    protected abstract void endDoc() throws PrinterException;
    
    protected abstract void abortDoc();
    
    protected void cancelDoc() throws PrinterAbortException {
        this.abortDoc();
        synchronized (this) {
            this.userCancelled = false;
            this.performingPrinting = false;
            this.notify();
        }
        throw new PrinterAbortException();
    }
    
    protected int getCollatedCopies() {
        return this.isCollated() ? this.getCopiesInt() : 1;
    }
    
    protected int getNoncollatedCopies() {
        return this.isCollated() ? 1 : this.getCopiesInt();
    }
    
    synchronized void setGraphicsConfigInfo(final AffineTransform defaultDeviceTransform, final double n, final double n2) {
        final Point2D.Double double1 = new Point2D.Double(n, n2);
        defaultDeviceTransform.transform(double1, double1);
        if (this.pgConfig == null || this.defaultDeviceTransform == null || !defaultDeviceTransform.equals(this.defaultDeviceTransform) || this.deviceWidth != (int)double1.getX() || this.deviceHeight != (int)double1.getY()) {
            this.deviceWidth = (int)double1.getX();
            this.deviceHeight = (int)double1.getY();
            this.defaultDeviceTransform = defaultDeviceTransform;
            this.pgConfig = null;
        }
    }
    
    synchronized PrinterGraphicsConfig getPrinterGraphicsConfig() {
        if (this.pgConfig != null) {
            return this.pgConfig;
        }
        String string = "Printer Device";
        final PrintService printService = this.getPrintService();
        if (printService != null) {
            string = printService.toString();
        }
        return this.pgConfig = new PrinterGraphicsConfig(string, this.defaultDeviceTransform, this.deviceWidth, this.deviceHeight);
    }
    
    protected int printPage(final Pageable pageable, final int n) throws PrinterException {
        PageFormat pageFormat;
        PageFormat pageFormat2;
        Printable printable;
        try {
            pageFormat = pageable.getPageFormat(n);
            pageFormat2 = (PageFormat)pageFormat.clone();
            printable = pageable.getPrintable(n);
        }
        catch (final Exception ex) {
            final PrinterException ex2 = new PrinterException("Error getting page or printable.[ " + ex + " ]");
            ex2.initCause(ex);
            throw ex2;
        }
        final Paper paper = pageFormat2.getPaper();
        if (pageFormat2.getOrientation() != 1 && this.landscapeRotates270) {
            final double imageableX = paper.getImageableX();
            final double imageableY = paper.getImageableY();
            final double imageableWidth = paper.getImageableWidth();
            final double imageableHeight = paper.getImageableHeight();
            paper.setImageableArea(paper.getWidth() - imageableX - imageableWidth, paper.getHeight() - imageableY - imageableHeight, imageableWidth, imageableHeight);
            pageFormat2.setPaper(paper);
            if (pageFormat2.getOrientation() == 0) {
                pageFormat2.setOrientation(2);
            }
            else {
                pageFormat2.setOrientation(0);
            }
        }
        final double n2 = this.getXRes() / 72.0;
        final double n3 = this.getYRes() / 72.0;
        final Rectangle2D.Double double1 = new Rectangle2D.Double(paper.getImageableX() * n2, paper.getImageableY() * n3, paper.getImageableWidth() * n2, paper.getImageableHeight() * n3);
        final AffineTransform transform = new AffineTransform();
        final AffineTransform affineTransform = new AffineTransform();
        affineTransform.scale(n2, n3);
        int cachedBandWidth = (int)double1.getWidth();
        if (cachedBandWidth % 4 != 0) {
            cachedBandWidth += 4 - cachedBandWidth % 4;
        }
        if (cachedBandWidth <= 0) {
            throw new PrinterException("Paper's imageable width is too small.");
        }
        final int n4 = (int)double1.getHeight();
        if (n4 <= 0) {
            throw new PrinterException("Paper's imageable height is too small.");
        }
        final int cachedBandHeight = 4194304 / cachedBandWidth / 3;
        final int n5 = (int)Math.rint(paper.getImageableX() * n2);
        final int n6 = (int)Math.rint(paper.getImageableY() * n3);
        final AffineTransform affineTransform2 = new AffineTransform();
        affineTransform2.translate(-n5, n6);
        affineTransform2.translate(0.0, cachedBandHeight);
        affineTransform2.scale(1.0, -1.0);
        final PeekGraphics peekGraphics = this.createPeekGraphics(new BufferedImage(1, 1, 5).createGraphics(), this);
        final Rectangle2D.Double double2 = new Rectangle2D.Double(pageFormat2.getImageableX(), pageFormat2.getImageableY(), pageFormat2.getImageableWidth(), pageFormat2.getImageableHeight());
        peekGraphics.transform(affineTransform);
        peekGraphics.translate(-this.getPhysicalPrintableX(paper) / n2, -this.getPhysicalPrintableY(paper) / n3);
        peekGraphics.transform(new AffineTransform(pageFormat2.getMatrix()));
        this.initPrinterGraphics(peekGraphics, double2);
        final AffineTransform transform2 = peekGraphics.getTransform();
        this.setGraphicsConfigInfo(affineTransform, paper.getWidth(), paper.getHeight());
        final int print = printable.print(peekGraphics, pageFormat, n);
        this.debug_println("pageResult " + print);
        if (print == 0) {
            this.debug_println("startPage " + n);
            final Paper paper2 = pageFormat2.getPaper();
            final boolean b = this.previousPaper == null || paper2.getWidth() != this.previousPaper.getWidth() || paper2.getHeight() != this.previousPaper.getHeight();
            this.previousPaper = paper2;
            this.startPage(pageFormat2, printable, n, b);
            final Graphics2D pathGraphics = this.createPathGraphics(peekGraphics, this, printable, pageFormat2, n);
            if (pathGraphics != null) {
                pathGraphics.transform(affineTransform);
                pathGraphics.translate(-this.getPhysicalPrintableX(paper) / n2, -this.getPhysicalPrintableY(paper) / n3);
                pathGraphics.transform(new AffineTransform(pageFormat2.getMatrix()));
                this.initPrinterGraphics(pathGraphics, double2);
                this.redrawList.clear();
                final AffineTransform transform3 = pathGraphics.getTransform();
                printable.print(pathGraphics, pageFormat, n);
                for (int i = 0; i < this.redrawList.size(); ++i) {
                    final GraphicsState graphicsState = this.redrawList.get(i);
                    pathGraphics.setTransform(transform3);
                    ((PathGraphics)pathGraphics).redrawRegion(graphicsState.region, graphicsState.sx, graphicsState.sy, graphicsState.theClip, graphicsState.theTransform);
                }
            }
            else {
                BufferedImage cachedBand = this.cachedBand;
                if (this.cachedBand == null || cachedBandWidth != this.cachedBandWidth || cachedBandHeight != this.cachedBandHeight) {
                    cachedBand = new BufferedImage(cachedBandWidth, cachedBandHeight, 5);
                    this.cachedBand = cachedBand;
                    this.cachedBandWidth = cachedBandWidth;
                    this.cachedBandHeight = cachedBandHeight;
                }
                final Graphics2D graphics = cachedBand.createGraphics();
                this.initPrinterGraphics(graphics, new Rectangle2D.Double(0.0, 0.0, cachedBandWidth, cachedBandHeight));
                final ProxyGraphics2D proxyGraphics2D = new ProxyGraphics2D(graphics, this);
                final Graphics2D graphics2 = cachedBand.createGraphics();
                graphics2.setColor(Color.white);
                final byte[] dataStorage = ((ByteInterleavedRaster)cachedBand.getRaster()).getDataStorage();
                final int n7 = (int)this.getPhysicalPrintableX(paper);
                final int n8 = (int)this.getPhysicalPrintableY(paper);
                for (int j = 0; j <= n4; j += cachedBandHeight) {
                    graphics2.fillRect(0, 0, cachedBandWidth, cachedBandHeight);
                    graphics.setTransform(transform);
                    graphics.transform(affineTransform2);
                    affineTransform2.translate(0.0, -cachedBandHeight);
                    graphics.transform(affineTransform);
                    graphics.transform(new AffineTransform(pageFormat2.getMatrix()));
                    final Rectangle bounds = transform2.createTransformedShape(graphics.getClipBounds()).getBounds();
                    if (bounds == null || (peekGraphics.hitsDrawingArea(bounds) && cachedBandWidth > 0 && cachedBandHeight > 0)) {
                        int n9 = n5 - n7;
                        if (n9 < 0) {
                            graphics.translate(n9 / n2, 0.0);
                            n9 = 0;
                        }
                        int n10 = n6 + j - n8;
                        if (n10 < 0) {
                            graphics.translate(0.0, n10 / n3);
                            n10 = 0;
                        }
                        proxyGraphics2D.setDelegate((Graphics2D)graphics.create());
                        printable.print(proxyGraphics2D, pageFormat, n);
                        proxyGraphics2D.dispose();
                        this.printBand(dataStorage, n9, n10, cachedBandWidth, cachedBandHeight);
                    }
                }
                graphics2.dispose();
                graphics.dispose();
            }
            this.debug_println("calling endPage " + n);
            this.endPage(pageFormat2, printable, n);
        }
        return print;
    }
    
    @Override
    public void cancel() {
        synchronized (this) {
            if (this.performingPrinting) {
                this.userCancelled = true;
            }
            this.notify();
        }
    }
    
    @Override
    public boolean isCancelled() {
        boolean b = false;
        synchronized (this) {
            b = (this.performingPrinting && this.userCancelled);
            this.notify();
        }
        return b;
    }
    
    protected Pageable getPageable() {
        return this.mDocument;
    }
    
    protected Graphics2D createPathGraphics(final PeekGraphics peekGraphics, final PrinterJob printerJob, final Printable printable, final PageFormat pageFormat, final int n) {
        return null;
    }
    
    protected PeekGraphics createPeekGraphics(final Graphics2D graphics2D, final PrinterJob printerJob) {
        return new PeekGraphics(graphics2D, printerJob);
    }
    
    protected void initPrinterGraphics(final Graphics2D graphics2D, final Rectangle2D clip) {
        graphics2D.setClip(clip);
        graphics2D.setPaint(Color.black);
    }
    
    public boolean checkAllowedToPrintToFile() {
        try {
            this.throwPrintToFile();
            return true;
        }
        catch (final SecurityException ex) {
            return false;
        }
    }
    
    private void throwPrintToFile() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            if (this.printToFilePermission == null) {
                this.printToFilePermission = new FilePermission("<<ALL FILES>>", "read,write");
            }
            securityManager.checkPermission(this.printToFilePermission);
        }
    }
    
    protected String removeControlChars(final String s) {
        final char[] charArray = s.toCharArray();
        final int length = charArray.length;
        final char[] array = new char[length];
        int n = 0;
        for (final char c : charArray) {
            if (c > '\r' || c < '\t' || c == '\u000b' || c == '\f') {
                array[n++] = c;
            }
        }
        if (n == length) {
            return s;
        }
        return new String(array, 0, n);
    }
    
    private long getParentWindowID() {
        return this.parentWindowID;
    }
    
    private void clearParentWindowID() {
        this.parentWindowID = 0L;
        this.onTop = null;
    }
    
    private void setParentWindowID(final PrintRequestAttributeSet set) {
        this.parentWindowID = 0L;
        this.onTop = (DialogOnTop)set.get(DialogOnTop.class);
        if (this.onTop != null) {
            this.parentWindowID = this.onTop.getID();
        }
    }
    
    static {
        RasterPrinterJob.forcePDL = false;
        RasterPrinterJob.forceRaster = false;
        RasterPrinterJob.shapeTextProp = false;
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.java2d.print.pipeline"));
        if (s != null) {
            if (s.equalsIgnoreCase("pdl")) {
                RasterPrinterJob.forcePDL = true;
            }
            else if (s.equalsIgnoreCase("raster")) {
                RasterPrinterJob.forceRaster = true;
            }
        }
        if (AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.java2d.print.shapetext")) != null) {
            RasterPrinterJob.shapeTextProp = true;
        }
        RasterPrinterJob.debugPrint = false;
    }
    
    private class GraphicsState
    {
        Rectangle2D region;
        Shape theClip;
        AffineTransform theTransform;
        double sx;
        double sy;
    }
}
