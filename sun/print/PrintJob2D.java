package sun.print;

import java.util.ArrayList;
import java.security.Permission;
import java.io.FilePermission;
import java.awt.Dimension;
import java.awt.Graphics;
import java.net.URISyntaxException;
import java.net.URI;
import java.awt.print.PrinterException;
import java.util.Locale;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.PrintQuality;
import javax.print.attribute.standard.Chromaticity;
import javax.print.attribute.standard.Sides;
import javax.print.attribute.standard.SheetCollate;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.Destination;
import javax.print.attribute.standard.Copies;
import java.awt.print.Paper;
import javax.print.attribute.Size2DSyntax;
import javax.print.PrintService;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.DialogTypeSelection;
import javax.print.attribute.Attribute;
import java.io.IOException;
import java.io.File;
import java.util.Properties;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import javax.print.attribute.PrintRequestAttributeSet;
import java.awt.JobAttributes;
import java.awt.Frame;
import javax.print.attribute.standard.MediaSizeName;
import java.awt.PageAttributes;
import java.awt.print.Printable;
import java.awt.PrintJob;

public class PrintJob2D extends PrintJob implements Printable, Runnable
{
    private static final PageAttributes.MediaType[] SIZES;
    private static final MediaSizeName[] JAVAXSIZES;
    private static final int[] WIDTHS;
    private static final int[] LENGTHS;
    private Frame frame;
    private String docTitle;
    private JobAttributes jobAttributes;
    private PageAttributes pageAttributes;
    private PrintRequestAttributeSet attributes;
    private PrinterJob printerJob;
    private PageFormat pageFormat;
    private MessageQ graphicsToBeDrawn;
    private MessageQ graphicsDrawn;
    private Graphics2D currentGraphics;
    private int pageIndex;
    private static final String DEST_PROP = "awt.print.destination";
    private static final String PRINTER = "printer";
    private static final String FILE = "file";
    private static final String PRINTER_PROP = "awt.print.printer";
    private static final String FILENAME_PROP = "awt.print.fileName";
    private static final String NUMCOPIES_PROP = "awt.print.numCopies";
    private static final String OPTIONS_PROP = "awt.print.options";
    private static final String ORIENT_PROP = "awt.print.orientation";
    private static final String PORTRAIT = "portrait";
    private static final String LANDSCAPE = "landscape";
    private static final String PAPERSIZE_PROP = "awt.print.paperSize";
    private static final String LETTER = "letter";
    private static final String LEGAL = "legal";
    private static final String EXECUTIVE = "executive";
    private static final String A4 = "a4";
    private Properties props;
    private String options;
    private Thread printerJobThread;
    
    public PrintJob2D(final Frame frame, final String s, final Properties props) {
        this.docTitle = "";
        this.graphicsToBeDrawn = new MessageQ("tobedrawn");
        this.graphicsDrawn = new MessageQ("drawn");
        this.pageIndex = -1;
        this.options = "";
        this.props = props;
        this.jobAttributes = new JobAttributes();
        this.pageAttributes = new PageAttributes();
        this.translateInputProps();
        this.initPrintJob2D(frame, s, this.jobAttributes, this.pageAttributes);
    }
    
    public PrintJob2D(final Frame frame, final String s, final JobAttributes jobAttributes, final PageAttributes pageAttributes) {
        this.docTitle = "";
        this.graphicsToBeDrawn = new MessageQ("tobedrawn");
        this.graphicsDrawn = new MessageQ("drawn");
        this.pageIndex = -1;
        this.options = "";
        this.initPrintJob2D(frame, s, jobAttributes, pageAttributes);
    }
    
    private void initPrintJob2D(final Frame frame, final String s, final JobAttributes jobAttributes, final PageAttributes pageAttributes) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPrintJobAccess();
        }
        if (frame == null && (jobAttributes == null || jobAttributes.getDialog() == JobAttributes.DialogType.NATIVE)) {
            throw new NullPointerException("Frame must not be null");
        }
        this.frame = frame;
        this.docTitle = ((s == null) ? "" : s);
        this.jobAttributes = ((jobAttributes != null) ? jobAttributes : new JobAttributes());
        this.pageAttributes = ((pageAttributes != null) ? pageAttributes : new PageAttributes());
        final int[][] pageRanges = this.jobAttributes.getPageRanges();
        final int fromPage = pageRanges[0][0];
        final int toPage = pageRanges[pageRanges.length - 1][1];
        this.jobAttributes.setPageRanges(new int[][] { { fromPage, toPage } });
        this.jobAttributes.setToPage(toPage);
        this.jobAttributes.setFromPage(fromPage);
        final int[] printerResolution = this.pageAttributes.getPrinterResolution();
        if (printerResolution[0] != printerResolution[1]) {
            throw new IllegalArgumentException("Differing cross feed and feed resolutions not supported.");
        }
        if (this.jobAttributes.getDestination() == JobAttributes.DestinationType.FILE) {
            this.throwPrintToFile();
            final String fileName = jobAttributes.getFileName();
            if (fileName != null && jobAttributes.getDialog() == JobAttributes.DialogType.NONE) {
                final File file = new File(fileName);
                try {
                    if (file.createNewFile()) {
                        file.delete();
                    }
                }
                catch (final IOException ex) {
                    throw new IllegalArgumentException("Cannot write to file:" + fileName);
                }
                catch (final SecurityException ex2) {}
                final File parentFile = file.getParentFile();
                if ((file.exists() && (!file.isFile() || !file.canWrite())) || (parentFile != null && (!parentFile.exists() || (parentFile.exists() && !parentFile.canWrite())))) {
                    throw new IllegalArgumentException("Cannot write to file:" + fileName);
                }
            }
        }
    }
    
    public boolean printDialog() {
        this.printerJob = PrinterJob.getPrinterJob();
        if (this.printerJob == null) {
            return false;
        }
        final JobAttributes.DialogType dialog = this.jobAttributes.getDialog();
        final PrintService printService = this.printerJob.getPrintService();
        if (printService == null && dialog == JobAttributes.DialogType.NONE) {
            return false;
        }
        this.copyAttributes(printService);
        final JobAttributes.DefaultSelectionType defaultSelection = this.jobAttributes.getDefaultSelection();
        if (defaultSelection == JobAttributes.DefaultSelectionType.RANGE) {
            this.attributes.add(SunPageSelection.RANGE);
        }
        else if (defaultSelection == JobAttributes.DefaultSelectionType.SELECTION) {
            this.attributes.add(SunPageSelection.SELECTION);
        }
        else {
            this.attributes.add(SunPageSelection.ALL);
        }
        if (this.frame != null) {
            this.attributes.add(new DialogOwner(this.frame));
        }
        boolean printDialog;
        if (dialog == JobAttributes.DialogType.NONE) {
            printDialog = true;
        }
        else {
            if (dialog == JobAttributes.DialogType.NATIVE) {
                this.attributes.add(DialogTypeSelection.NATIVE);
            }
            else {
                this.attributes.add(DialogTypeSelection.COMMON);
            }
            if (printDialog = this.printerJob.printDialog(this.attributes)) {
                if (printService == null && this.printerJob.getPrintService() == null) {
                    return false;
                }
                this.updateAttributes();
                this.translateOutputProps();
            }
        }
        if (printDialog) {
            final JobName jobName = (JobName)this.attributes.get(JobName.class);
            if (jobName != null) {
                this.printerJob.setJobName(jobName.toString());
            }
            this.pageFormat = new PageFormat();
            final Media media = (Media)this.attributes.get(Media.class);
            Size2DSyntax mediaSizeForName = null;
            if (media != null && media instanceof MediaSizeName) {
                mediaSizeForName = MediaSize.getMediaSizeForName((MediaSizeName)media);
            }
            final Paper paper = this.pageFormat.getPaper();
            if (mediaSizeForName != null) {
                paper.setSize(mediaSizeForName.getX(25400) * 72.0, mediaSizeForName.getY(25400) * 72.0);
            }
            if (this.pageAttributes.getOrigin() == PageAttributes.OriginType.PRINTABLE) {
                paper.setImageableArea(18.0, 18.0, paper.getWidth() - 36.0, paper.getHeight() - 36.0);
            }
            else {
                paper.setImageableArea(0.0, 0.0, paper.getWidth(), paper.getHeight());
            }
            this.pageFormat.setPaper(paper);
            final OrientationRequested orientationRequested = (OrientationRequested)this.attributes.get(OrientationRequested.class);
            if (orientationRequested != null && orientationRequested == OrientationRequested.REVERSE_LANDSCAPE) {
                this.pageFormat.setOrientation(2);
            }
            else if (orientationRequested == OrientationRequested.LANDSCAPE) {
                this.pageFormat.setOrientation(0);
            }
            else {
                this.pageFormat.setOrientation(1);
            }
            this.printerJob.setPrintable(this, this.pageFormat);
        }
        return printDialog;
    }
    
    private void updateAttributes() {
        this.jobAttributes.setCopies(((Copies)this.attributes.get(Copies.class)).getValue());
        final SunPageSelection sunPageSelection = (SunPageSelection)this.attributes.get(SunPageSelection.class);
        if (sunPageSelection == SunPageSelection.RANGE) {
            this.jobAttributes.setDefaultSelection(JobAttributes.DefaultSelectionType.RANGE);
        }
        else if (sunPageSelection == SunPageSelection.SELECTION) {
            this.jobAttributes.setDefaultSelection(JobAttributes.DefaultSelectionType.SELECTION);
        }
        else {
            this.jobAttributes.setDefaultSelection(JobAttributes.DefaultSelectionType.ALL);
        }
        final Destination destination = (Destination)this.attributes.get(Destination.class);
        if (destination != null) {
            this.jobAttributes.setDestination(JobAttributes.DestinationType.FILE);
            this.jobAttributes.setFileName(destination.getURI().getPath());
        }
        else {
            this.jobAttributes.setDestination(JobAttributes.DestinationType.PRINTER);
        }
        final PrintService printService = this.printerJob.getPrintService();
        if (printService != null) {
            this.jobAttributes.setPrinter(printService.getName());
        }
        this.jobAttributes.setPageRanges(((PageRanges)this.attributes.get(PageRanges.class)).getMembers());
        if (this.attributes.get(SheetCollate.class) == SheetCollate.COLLATED) {
            this.jobAttributes.setMultipleDocumentHandling(JobAttributes.MultipleDocumentHandlingType.SEPARATE_DOCUMENTS_COLLATED_COPIES);
        }
        else {
            this.jobAttributes.setMultipleDocumentHandling(JobAttributes.MultipleDocumentHandlingType.SEPARATE_DOCUMENTS_UNCOLLATED_COPIES);
        }
        final Sides sides = (Sides)this.attributes.get(Sides.class);
        if (sides == Sides.TWO_SIDED_LONG_EDGE) {
            this.jobAttributes.setSides(JobAttributes.SidesType.TWO_SIDED_LONG_EDGE);
        }
        else if (sides == Sides.TWO_SIDED_SHORT_EDGE) {
            this.jobAttributes.setSides(JobAttributes.SidesType.TWO_SIDED_SHORT_EDGE);
        }
        else {
            this.jobAttributes.setSides(JobAttributes.SidesType.ONE_SIDED);
        }
        if (this.attributes.get(Chromaticity.class) == Chromaticity.COLOR) {
            this.pageAttributes.setColor(PageAttributes.ColorType.COLOR);
        }
        else {
            this.pageAttributes.setColor(PageAttributes.ColorType.MONOCHROME);
        }
        if (this.attributes.get(OrientationRequested.class) == OrientationRequested.LANDSCAPE) {
            this.pageAttributes.setOrientationRequested(PageAttributes.OrientationRequestedType.LANDSCAPE);
        }
        else {
            this.pageAttributes.setOrientationRequested(PageAttributes.OrientationRequestedType.PORTRAIT);
        }
        final PrintQuality printQuality = (PrintQuality)this.attributes.get(PrintQuality.class);
        if (printQuality == PrintQuality.DRAFT) {
            this.pageAttributes.setPrintQuality(PageAttributes.PrintQualityType.DRAFT);
        }
        else if (printQuality == PrintQuality.HIGH) {
            this.pageAttributes.setPrintQuality(PageAttributes.PrintQualityType.HIGH);
        }
        else {
            this.pageAttributes.setPrintQuality(PageAttributes.PrintQualityType.NORMAL);
        }
        final Media media = (Media)this.attributes.get(Media.class);
        if (media != null && media instanceof MediaSizeName) {
            final PageAttributes.MediaType unMapMedia = unMapMedia((MediaSizeName)media);
            if (unMapMedia != null) {
                this.pageAttributes.setMedia(unMapMedia);
            }
        }
        this.debugPrintAttributes(false, false);
    }
    
    private void debugPrintAttributes(final boolean b, final boolean b2) {
        if (b) {
            System.out.println("new Attributes\ncopies = " + this.jobAttributes.getCopies() + "\nselection = " + this.jobAttributes.getDefaultSelection() + "\ndest " + this.jobAttributes.getDestination() + "\nfile " + this.jobAttributes.getFileName() + "\nfromPage " + this.jobAttributes.getFromPage() + "\ntoPage " + this.jobAttributes.getToPage() + "\ncollation " + this.jobAttributes.getMultipleDocumentHandling() + "\nPrinter " + this.jobAttributes.getPrinter() + "\nSides2 " + this.jobAttributes.getSides());
        }
        if (b2) {
            System.out.println("new Attributes\ncolor = " + this.pageAttributes.getColor() + "\norientation = " + this.pageAttributes.getOrientationRequested() + "\nquality " + this.pageAttributes.getPrintQuality() + "\nMedia2 " + this.pageAttributes.getMedia());
        }
    }
    
    private void copyAttributes(final PrintService printService) {
        (this.attributes = new HashPrintRequestAttributeSet()).add(new JobName(this.docTitle, null));
        PrintService printService2 = printService;
        final String printer = this.jobAttributes.getPrinter();
        if (printer != null && printer != "" && !printer.equals(printService2.getName())) {
            final PrintService[] lookupPrintServices = PrinterJob.lookupPrintServices();
            try {
                for (int i = 0; i < lookupPrintServices.length; ++i) {
                    if (printer.equals(lookupPrintServices[i].getName())) {
                        this.printerJob.setPrintService(lookupPrintServices[i]);
                        printService2 = lookupPrintServices[i];
                        break;
                    }
                }
            }
            catch (final PrinterException ex) {}
        }
        if (this.jobAttributes.getDestination() == JobAttributes.DestinationType.FILE && printService2.isAttributeCategorySupported(Destination.class)) {
            String fileName = this.jobAttributes.getFileName();
            final Destination destination;
            if (fileName == null && (destination = (Destination)printService2.getDefaultAttributeValue(Destination.class)) != null) {
                this.attributes.add(destination);
            }
            else {
                URI uri = null;
                try {
                    if (fileName != null) {
                        if (fileName.equals("")) {
                            fileName = ".";
                        }
                    }
                    else {
                        fileName = "out.prn";
                    }
                    uri = new File(fileName).toURI();
                }
                catch (final SecurityException ex2) {
                    try {
                        uri = new URI("file:" + fileName.replace('\\', '/'));
                    }
                    catch (final URISyntaxException ex3) {}
                }
                if (uri != null) {
                    this.attributes.add(new Destination(uri));
                }
            }
        }
        this.attributes.add(new SunMinMaxPage(this.jobAttributes.getMinPage(), this.jobAttributes.getMaxPage()));
        final JobAttributes.SidesType sides = this.jobAttributes.getSides();
        if (sides == JobAttributes.SidesType.TWO_SIDED_LONG_EDGE) {
            this.attributes.add(Sides.TWO_SIDED_LONG_EDGE);
        }
        else if (sides == JobAttributes.SidesType.TWO_SIDED_SHORT_EDGE) {
            this.attributes.add(Sides.TWO_SIDED_SHORT_EDGE);
        }
        else if (sides == JobAttributes.SidesType.ONE_SIDED) {
            this.attributes.add(Sides.ONE_SIDED);
        }
        if (this.jobAttributes.getMultipleDocumentHandling() == JobAttributes.MultipleDocumentHandlingType.SEPARATE_DOCUMENTS_COLLATED_COPIES) {
            this.attributes.add(SheetCollate.COLLATED);
        }
        else {
            this.attributes.add(SheetCollate.UNCOLLATED);
        }
        this.attributes.add(new Copies(this.jobAttributes.getCopies()));
        this.attributes.add(new PageRanges(this.jobAttributes.getFromPage(), this.jobAttributes.getToPage()));
        if (this.pageAttributes.getColor() == PageAttributes.ColorType.COLOR) {
            this.attributes.add(Chromaticity.COLOR);
        }
        else {
            this.attributes.add(Chromaticity.MONOCHROME);
        }
        this.pageFormat = this.printerJob.defaultPage();
        if (this.pageAttributes.getOrientationRequested() == PageAttributes.OrientationRequestedType.LANDSCAPE) {
            this.pageFormat.setOrientation(0);
            this.attributes.add(OrientationRequested.LANDSCAPE);
        }
        else {
            this.pageFormat.setOrientation(1);
            this.attributes.add(OrientationRequested.PORTRAIT);
        }
        final MediaSizeName mapMedia = mapMedia(this.pageAttributes.getMedia());
        if (mapMedia != null) {
            this.attributes.add(mapMedia);
        }
        final PageAttributes.PrintQualityType printQuality = this.pageAttributes.getPrintQuality();
        if (printQuality == PageAttributes.PrintQualityType.DRAFT) {
            this.attributes.add(PrintQuality.DRAFT);
        }
        else if (printQuality == PageAttributes.PrintQualityType.NORMAL) {
            this.attributes.add(PrintQuality.NORMAL);
        }
        else if (printQuality == PageAttributes.PrintQualityType.HIGH) {
            this.attributes.add(PrintQuality.HIGH);
        }
    }
    
    @Override
    public Graphics getGraphics() {
        Graphics graphics = null;
        synchronized (this) {
            ++this.pageIndex;
            if (this.pageIndex == 0 && !this.graphicsToBeDrawn.isClosed()) {
                this.startPrinterJobThread();
            }
            this.notify();
        }
        if (this.currentGraphics != null) {
            this.graphicsDrawn.append(this.currentGraphics);
            this.currentGraphics = null;
        }
        this.currentGraphics = this.graphicsToBeDrawn.pop();
        if (this.currentGraphics instanceof PeekGraphics) {
            ((PeekGraphics)this.currentGraphics).setAWTDrawingOnly();
            this.graphicsDrawn.append(this.currentGraphics);
            this.currentGraphics = this.graphicsToBeDrawn.pop();
        }
        if (this.currentGraphics != null) {
            this.currentGraphics.translate(this.pageFormat.getImageableX(), this.pageFormat.getImageableY());
            final double n = 72.0 / this.getPageResolutionInternal();
            this.currentGraphics.scale(n, n);
            graphics = new ProxyPrintGraphics(this.currentGraphics.create(), this);
        }
        return graphics;
    }
    
    @Override
    public Dimension getPageDimension() {
        double n;
        double n2;
        if (this.pageAttributes != null && this.pageAttributes.getOrigin() == PageAttributes.OriginType.PRINTABLE) {
            n = this.pageFormat.getImageableWidth();
            n2 = this.pageFormat.getImageableHeight();
        }
        else {
            n = this.pageFormat.getWidth();
            n2 = this.pageFormat.getHeight();
        }
        final double n3 = this.getPageResolutionInternal() / 72.0;
        return new Dimension((int)(n * n3), (int)(n2 * n3));
    }
    
    private double getPageResolutionInternal() {
        if (this.pageAttributes == null) {
            return 72.0;
        }
        final int[] printerResolution = this.pageAttributes.getPrinterResolution();
        if (printerResolution[2] == 3) {
            return printerResolution[0];
        }
        return printerResolution[0] * 2.54;
    }
    
    @Override
    public int getPageResolution() {
        return (int)this.getPageResolutionInternal();
    }
    
    @Override
    public boolean lastPageFirst() {
        return false;
    }
    
    @Override
    public synchronized void end() {
        this.graphicsToBeDrawn.close();
        if (this.currentGraphics != null) {
            this.graphicsDrawn.append(this.currentGraphics);
        }
        this.graphicsDrawn.closeWhenEmpty();
        if (this.printerJobThread != null && this.printerJobThread.isAlive()) {
            try {
                this.printerJobThread.join();
            }
            catch (final InterruptedException ex) {}
        }
    }
    
    @Override
    public void finalize() {
        this.end();
    }
    
    @Override
    public int print(final Graphics graphics, final PageFormat pageFormat, final int n) throws PrinterException {
        this.graphicsToBeDrawn.append((Graphics2D)graphics);
        return (this.graphicsDrawn.pop() == null) ? 1 : 0;
    }
    
    private void startPrinterJobThread() {
        (this.printerJobThread = new Thread(this, "printerJobThread")).start();
    }
    
    @Override
    public void run() {
        try {
            this.printerJob.print(this.attributes);
        }
        catch (final PrinterException ex) {}
        this.graphicsToBeDrawn.closeWhenEmpty();
        this.graphicsDrawn.close();
    }
    
    private static int[] getSize(final PageAttributes.MediaType mediaType) {
        final int[] array = { 612, 792 };
        for (int i = 0; i < PrintJob2D.SIZES.length; ++i) {
            if (PrintJob2D.SIZES[i] == mediaType) {
                array[0] = PrintJob2D.WIDTHS[i];
                array[1] = PrintJob2D.LENGTHS[i];
                break;
            }
        }
        return array;
    }
    
    public static MediaSizeName mapMedia(final PageAttributes.MediaType mediaType) {
        MediaSizeName mediaSizeName = null;
        final int min = Math.min(PrintJob2D.SIZES.length, PrintJob2D.JAVAXSIZES.length);
        int i = 0;
        while (i < min) {
            if (PrintJob2D.SIZES[i] == mediaType) {
                if (PrintJob2D.JAVAXSIZES[i] != null && MediaSize.getMediaSizeForName(PrintJob2D.JAVAXSIZES[i]) != null) {
                    mediaSizeName = PrintJob2D.JAVAXSIZES[i];
                    break;
                }
                mediaSizeName = new CustomMediaSizeName(PrintJob2D.SIZES[i].toString());
                final float n = (float)Math.rint(PrintJob2D.WIDTHS[i] / 72.0);
                final float n2 = (float)Math.rint(PrintJob2D.LENGTHS[i] / 72.0);
                if (n > 0.0 && n2 > 0.0) {
                    new MediaSize(n, n2, 25400, mediaSizeName);
                    break;
                }
                break;
            }
            else {
                ++i;
            }
        }
        return mediaSizeName;
    }
    
    public static PageAttributes.MediaType unMapMedia(final MediaSizeName mediaSizeName) {
        PageAttributes.MediaType mediaType = null;
        for (int min = Math.min(PrintJob2D.SIZES.length, PrintJob2D.JAVAXSIZES.length), i = 0; i < min; ++i) {
            if (PrintJob2D.JAVAXSIZES[i] == mediaSizeName && PrintJob2D.SIZES[i] != null) {
                mediaType = PrintJob2D.SIZES[i];
                break;
            }
        }
        return mediaType;
    }
    
    private void translateInputProps() {
        if (this.props == null) {
            return;
        }
        final String property = this.props.getProperty("awt.print.destination");
        if (property != null) {
            if (property.equals("printer")) {
                this.jobAttributes.setDestination(JobAttributes.DestinationType.PRINTER);
            }
            else if (property.equals("file")) {
                this.jobAttributes.setDestination(JobAttributes.DestinationType.FILE);
            }
        }
        final String property2 = this.props.getProperty("awt.print.printer");
        if (property2 != null) {
            this.jobAttributes.setPrinter(property2);
        }
        final String property3 = this.props.getProperty("awt.print.fileName");
        if (property3 != null) {
            this.jobAttributes.setFileName(property3);
        }
        final String property4 = this.props.getProperty("awt.print.numCopies");
        if (property4 != null) {
            this.jobAttributes.setCopies(Integer.parseInt(property4));
        }
        this.options = this.props.getProperty("awt.print.options", "");
        final String property5 = this.props.getProperty("awt.print.orientation");
        if (property5 != null) {
            if (property5.equals("portrait")) {
                this.pageAttributes.setOrientationRequested(PageAttributes.OrientationRequestedType.PORTRAIT);
            }
            else if (property5.equals("landscape")) {
                this.pageAttributes.setOrientationRequested(PageAttributes.OrientationRequestedType.LANDSCAPE);
            }
        }
        final String property6 = this.props.getProperty("awt.print.paperSize");
        if (property6 != null) {
            if (property6.equals("letter")) {
                this.pageAttributes.setMedia(PrintJob2D.SIZES[PageAttributes.MediaType.LETTER.hashCode()]);
            }
            else if (property6.equals("legal")) {
                this.pageAttributes.setMedia(PrintJob2D.SIZES[PageAttributes.MediaType.LEGAL.hashCode()]);
            }
            else if (property6.equals("executive")) {
                this.pageAttributes.setMedia(PrintJob2D.SIZES[PageAttributes.MediaType.EXECUTIVE.hashCode()]);
            }
            else if (property6.equals("a4")) {
                this.pageAttributes.setMedia(PrintJob2D.SIZES[PageAttributes.MediaType.A4.hashCode()]);
            }
        }
    }
    
    private void translateOutputProps() {
        if (this.props == null) {
            return;
        }
        this.props.setProperty("awt.print.destination", (this.jobAttributes.getDestination() == JobAttributes.DestinationType.PRINTER) ? "printer" : "file");
        final String printer = this.jobAttributes.getPrinter();
        if (printer != null && !printer.equals("")) {
            this.props.setProperty("awt.print.printer", printer);
        }
        final String fileName = this.jobAttributes.getFileName();
        if (fileName != null && !fileName.equals("")) {
            this.props.setProperty("awt.print.fileName", fileName);
        }
        final int copies = this.jobAttributes.getCopies();
        if (copies > 0) {
            this.props.setProperty("awt.print.numCopies", "" + copies);
        }
        final String options = this.options;
        if (options != null && !options.equals("")) {
            this.props.setProperty("awt.print.options", options);
        }
        this.props.setProperty("awt.print.orientation", (this.pageAttributes.getOrientationRequested() == PageAttributes.OrientationRequestedType.PORTRAIT) ? "portrait" : "landscape");
        final PageAttributes.MediaType mediaType = PrintJob2D.SIZES[this.pageAttributes.getMedia().hashCode()];
        String string;
        if (mediaType == PageAttributes.MediaType.LETTER) {
            string = "letter";
        }
        else if (mediaType == PageAttributes.MediaType.LEGAL) {
            string = "legal";
        }
        else if (mediaType == PageAttributes.MediaType.EXECUTIVE) {
            string = "executive";
        }
        else if (mediaType == PageAttributes.MediaType.A4) {
            string = "a4";
        }
        else {
            string = mediaType.toString();
        }
        this.props.setProperty("awt.print.paperSize", string);
    }
    
    private void throwPrintToFile() {
        final SecurityManager securityManager = System.getSecurityManager();
        Permission permission = null;
        if (securityManager != null) {
            if (permission == null) {
                permission = new FilePermission("<<ALL FILES>>", "read,write");
            }
            securityManager.checkPermission(permission);
        }
    }
    
    static {
        SIZES = new PageAttributes.MediaType[] { PageAttributes.MediaType.ISO_4A0, PageAttributes.MediaType.ISO_2A0, PageAttributes.MediaType.ISO_A0, PageAttributes.MediaType.ISO_A1, PageAttributes.MediaType.ISO_A2, PageAttributes.MediaType.ISO_A3, PageAttributes.MediaType.ISO_A4, PageAttributes.MediaType.ISO_A5, PageAttributes.MediaType.ISO_A6, PageAttributes.MediaType.ISO_A7, PageAttributes.MediaType.ISO_A8, PageAttributes.MediaType.ISO_A9, PageAttributes.MediaType.ISO_A10, PageAttributes.MediaType.ISO_B0, PageAttributes.MediaType.ISO_B1, PageAttributes.MediaType.ISO_B2, PageAttributes.MediaType.ISO_B3, PageAttributes.MediaType.ISO_B4, PageAttributes.MediaType.ISO_B5, PageAttributes.MediaType.ISO_B6, PageAttributes.MediaType.ISO_B7, PageAttributes.MediaType.ISO_B8, PageAttributes.MediaType.ISO_B9, PageAttributes.MediaType.ISO_B10, PageAttributes.MediaType.JIS_B0, PageAttributes.MediaType.JIS_B1, PageAttributes.MediaType.JIS_B2, PageAttributes.MediaType.JIS_B3, PageAttributes.MediaType.JIS_B4, PageAttributes.MediaType.JIS_B5, PageAttributes.MediaType.JIS_B6, PageAttributes.MediaType.JIS_B7, PageAttributes.MediaType.JIS_B8, PageAttributes.MediaType.JIS_B9, PageAttributes.MediaType.JIS_B10, PageAttributes.MediaType.ISO_C0, PageAttributes.MediaType.ISO_C1, PageAttributes.MediaType.ISO_C2, PageAttributes.MediaType.ISO_C3, PageAttributes.MediaType.ISO_C4, PageAttributes.MediaType.ISO_C5, PageAttributes.MediaType.ISO_C6, PageAttributes.MediaType.ISO_C7, PageAttributes.MediaType.ISO_C8, PageAttributes.MediaType.ISO_C9, PageAttributes.MediaType.ISO_C10, PageAttributes.MediaType.ISO_DESIGNATED_LONG, PageAttributes.MediaType.EXECUTIVE, PageAttributes.MediaType.FOLIO, PageAttributes.MediaType.INVOICE, PageAttributes.MediaType.LEDGER, PageAttributes.MediaType.NA_LETTER, PageAttributes.MediaType.NA_LEGAL, PageAttributes.MediaType.QUARTO, PageAttributes.MediaType.A, PageAttributes.MediaType.B, PageAttributes.MediaType.C, PageAttributes.MediaType.D, PageAttributes.MediaType.E, PageAttributes.MediaType.NA_10X15_ENVELOPE, PageAttributes.MediaType.NA_10X14_ENVELOPE, PageAttributes.MediaType.NA_10X13_ENVELOPE, PageAttributes.MediaType.NA_9X12_ENVELOPE, PageAttributes.MediaType.NA_9X11_ENVELOPE, PageAttributes.MediaType.NA_7X9_ENVELOPE, PageAttributes.MediaType.NA_6X9_ENVELOPE, PageAttributes.MediaType.NA_NUMBER_9_ENVELOPE, PageAttributes.MediaType.NA_NUMBER_10_ENVELOPE, PageAttributes.MediaType.NA_NUMBER_11_ENVELOPE, PageAttributes.MediaType.NA_NUMBER_12_ENVELOPE, PageAttributes.MediaType.NA_NUMBER_14_ENVELOPE, PageAttributes.MediaType.INVITE_ENVELOPE, PageAttributes.MediaType.ITALY_ENVELOPE, PageAttributes.MediaType.MONARCH_ENVELOPE, PageAttributes.MediaType.PERSONAL_ENVELOPE };
        JAVAXSIZES = new MediaSizeName[] { null, null, MediaSizeName.ISO_A0, MediaSizeName.ISO_A1, MediaSizeName.ISO_A2, MediaSizeName.ISO_A3, MediaSizeName.ISO_A4, MediaSizeName.ISO_A5, MediaSizeName.ISO_A6, MediaSizeName.ISO_A7, MediaSizeName.ISO_A8, MediaSizeName.ISO_A9, MediaSizeName.ISO_A10, MediaSizeName.ISO_B0, MediaSizeName.ISO_B1, MediaSizeName.ISO_B2, MediaSizeName.ISO_B3, MediaSizeName.ISO_B4, MediaSizeName.ISO_B5, MediaSizeName.ISO_B6, MediaSizeName.ISO_B7, MediaSizeName.ISO_B8, MediaSizeName.ISO_B9, MediaSizeName.ISO_B10, MediaSizeName.JIS_B0, MediaSizeName.JIS_B1, MediaSizeName.JIS_B2, MediaSizeName.JIS_B3, MediaSizeName.JIS_B4, MediaSizeName.JIS_B5, MediaSizeName.JIS_B6, MediaSizeName.JIS_B7, MediaSizeName.JIS_B8, MediaSizeName.JIS_B9, MediaSizeName.JIS_B10, MediaSizeName.ISO_C0, MediaSizeName.ISO_C1, MediaSizeName.ISO_C2, MediaSizeName.ISO_C3, MediaSizeName.ISO_C4, MediaSizeName.ISO_C5, MediaSizeName.ISO_C6, null, null, null, null, MediaSizeName.ISO_DESIGNATED_LONG, MediaSizeName.EXECUTIVE, MediaSizeName.FOLIO, MediaSizeName.INVOICE, MediaSizeName.LEDGER, MediaSizeName.NA_LETTER, MediaSizeName.NA_LEGAL, MediaSizeName.QUARTO, MediaSizeName.A, MediaSizeName.B, MediaSizeName.C, MediaSizeName.D, MediaSizeName.E, MediaSizeName.NA_10X15_ENVELOPE, MediaSizeName.NA_10X14_ENVELOPE, MediaSizeName.NA_10X13_ENVELOPE, MediaSizeName.NA_9X12_ENVELOPE, MediaSizeName.NA_9X11_ENVELOPE, MediaSizeName.NA_7X9_ENVELOPE, MediaSizeName.NA_6X9_ENVELOPE, MediaSizeName.NA_NUMBER_9_ENVELOPE, MediaSizeName.NA_NUMBER_10_ENVELOPE, MediaSizeName.NA_NUMBER_11_ENVELOPE, MediaSizeName.NA_NUMBER_12_ENVELOPE, MediaSizeName.NA_NUMBER_14_ENVELOPE, null, MediaSizeName.ITALY_ENVELOPE, MediaSizeName.MONARCH_ENVELOPE, MediaSizeName.PERSONAL_ENVELOPE };
        WIDTHS = new int[] { 4768, 3370, 2384, 1684, 1191, 842, 595, 420, 298, 210, 147, 105, 74, 2835, 2004, 1417, 1001, 709, 499, 354, 249, 176, 125, 88, 2920, 2064, 1460, 1032, 729, 516, 363, 258, 181, 128, 91, 2599, 1837, 1298, 918, 649, 459, 323, 230, 162, 113, 79, 312, 522, 612, 396, 792, 612, 612, 609, 612, 792, 1224, 1584, 2448, 720, 720, 720, 648, 648, 504, 432, 279, 297, 324, 342, 360, 624, 312, 279, 261 };
        LENGTHS = new int[] { 6741, 4768, 3370, 2384, 1684, 1191, 842, 595, 420, 298, 210, 147, 105, 4008, 2835, 2004, 1417, 1001, 729, 499, 354, 249, 176, 125, 4127, 2920, 2064, 1460, 1032, 729, 516, 363, 258, 181, 128, 3677, 2599, 1837, 1298, 918, 649, 459, 323, 230, 162, 113, 624, 756, 936, 612, 1224, 792, 1008, 780, 792, 1224, 1584, 2448, 3168, 1080, 1008, 936, 864, 792, 648, 648, 639, 684, 747, 792, 828, 624, 652, 540, 468 };
    }
    
    private class MessageQ
    {
        private String qid;
        private ArrayList queue;
        
        MessageQ(final String qid) {
            this.qid = "noname";
            this.queue = new ArrayList();
            this.qid = qid;
        }
        
        synchronized void closeWhenEmpty() {
            while (this.queue != null && this.queue.size() > 0) {
                try {
                    this.wait(1000L);
                }
                catch (final InterruptedException ex) {}
            }
            this.queue = null;
            this.notifyAll();
        }
        
        synchronized void close() {
            this.queue = null;
            this.notifyAll();
        }
        
        synchronized boolean append(final Graphics2D graphics2D) {
            boolean b = false;
            if (this.queue != null) {
                this.queue.add(graphics2D);
                b = true;
                this.notify();
            }
            return b;
        }
        
        synchronized Graphics2D pop() {
            Graphics2D graphics2D = null;
            while (graphics2D == null && this.queue != null) {
                if (this.queue.size() > 0) {
                    graphics2D = this.queue.remove(0);
                    this.notify();
                }
                else {
                    try {
                        this.wait(2000L);
                    }
                    catch (final InterruptedException ex) {}
                }
            }
            return graphics2D;
        }
        
        synchronized boolean isClosed() {
            return this.queue == null;
        }
    }
}
