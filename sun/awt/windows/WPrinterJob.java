package sun.awt.windows;

import java.awt.event.ActionEvent;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Button;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.Dialog;
import sun.awt.Win32FontManager;
import java.awt.Toolkit;
import java.awt.Window;
import sun.print.SunPageSelection;
import java.net.URISyntaxException;
import java.security.Permission;
import java.io.FilePermission;
import sun.print.Win32MediaTray;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.OrientationRequested;
import java.awt.print.Pageable;
import java.awt.image.IndexColorModel;
import sun.print.PeekMetrics;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import sun.print.PeekGraphics;
import java.awt.print.Paper;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.MediaTray;
import sun.print.SunAlternateMedia;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.SheetCollate;
import javax.print.attribute.standard.PrintQuality;
import javax.print.attribute.standard.PrinterResolution;
import javax.print.attribute.standard.Chromaticity;
import javax.print.attribute.standard.Sides;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.PrintServiceLookup;
import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import java.net.URI;
import javax.print.attribute.Attribute;
import sun.print.ServiceDialog;
import java.io.File;
import java.awt.FileDialog;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.print.attribute.standard.Destination;
import sun.print.DialogOwner;
import java.awt.print.PrinterException;
import sun.print.PrintServiceLookupProvider;
import java.awt.print.Printable;
import java.awt.Frame;
import sun.print.Win32PrintService;
import java.awt.HeadlessException;
import java.awt.GraphicsEnvironment;
import java.awt.print.PageFormat;
import sun.java2d.DisposerRecord;
import sun.java2d.Disposer;
import java.awt.peer.ComponentPeer;
import java.awt.print.PrinterJob;
import java.awt.Color;
import sun.java2d.DisposerTarget;
import sun.print.RasterPrinterJob;

public final class WPrinterJob extends RasterPrinterJob implements DisposerTarget
{
    protected static final long PS_ENDCAP_ROUND = 0L;
    protected static final long PS_ENDCAP_SQUARE = 256L;
    protected static final long PS_ENDCAP_FLAT = 512L;
    protected static final long PS_JOIN_ROUND = 0L;
    protected static final long PS_JOIN_BEVEL = 4096L;
    protected static final long PS_JOIN_MITER = 8192L;
    protected static final int POLYFILL_ALTERNATE = 1;
    protected static final int POLYFILL_WINDING = 2;
    private static final int MAX_WCOLOR = 255;
    private static final int SET_DUP_VERTICAL = 16;
    private static final int SET_DUP_HORIZONTAL = 32;
    private static final int SET_RES_HIGH = 64;
    private static final int SET_RES_LOW = 128;
    private static final int SET_COLOR = 512;
    private static final int SET_ORIENTATION = 16384;
    private static final int SET_COLLATED = 32768;
    private static final int PD_COLLATE = 16;
    private static final int PD_PRINTTOFILE = 32;
    private static final int DM_ORIENTATION = 1;
    private static final int DM_PAPERSIZE = 2;
    private static final int DM_COPIES = 256;
    private static final int DM_DEFAULTSOURCE = 512;
    private static final int DM_PRINTQUALITY = 1024;
    private static final int DM_COLOR = 2048;
    private static final int DM_DUPLEX = 4096;
    private static final int DM_YRESOLUTION = 8192;
    private static final int DM_COLLATE = 32768;
    private static final short DMCOLLATE_FALSE = 0;
    private static final short DMCOLLATE_TRUE = 1;
    private static final short DMORIENT_PORTRAIT = 1;
    private static final short DMORIENT_LANDSCAPE = 2;
    private static final short DMCOLOR_MONOCHROME = 1;
    private static final short DMCOLOR_COLOR = 2;
    private static final short DMRES_DRAFT = -1;
    private static final short DMRES_LOW = -2;
    private static final short DMRES_MEDIUM = -3;
    private static final short DMRES_HIGH = -4;
    private static final short DMDUP_SIMPLEX = 1;
    private static final short DMDUP_VERTICAL = 2;
    private static final short DMDUP_HORIZONTAL = 3;
    private static final int MAX_UNKNOWN_PAGES = 9999;
    private boolean driverDoesMultipleCopies;
    private boolean driverDoesCollation;
    private boolean userRequestedCollation;
    private boolean noDefaultPrinter;
    private HandleRecord handleRecord;
    private int mPrintPaperSize;
    private int mPrintXRes;
    private int mPrintYRes;
    private int mPrintPhysX;
    private int mPrintPhysY;
    private int mPrintWidth;
    private int mPrintHeight;
    private int mPageWidth;
    private int mPageHeight;
    private int mAttSides;
    private int mAttChromaticity;
    private int mAttXRes;
    private int mAttYRes;
    private int mAttQuality;
    private int mAttCollate;
    private int mAttCopies;
    private int mAttMediaSizeName;
    private int mAttMediaTray;
    private String mDestination;
    private Color mLastColor;
    private Color mLastTextColor;
    private String mLastFontFamily;
    private float mLastFontSize;
    private int mLastFontStyle;
    private int mLastRotation;
    private float mLastAwScale;
    private PrinterJob pjob;
    private ComponentPeer dialogOwnerPeer;
    private Object disposerReferent;
    private String lastNativeService;
    private boolean defaultCopies;
    
    public WPrinterJob() {
        this.driverDoesMultipleCopies = false;
        this.driverDoesCollation = false;
        this.userRequestedCollation = false;
        this.noDefaultPrinter = false;
        this.handleRecord = new HandleRecord();
        this.mDestination = null;
        this.dialogOwnerPeer = null;
        this.disposerReferent = new Object();
        this.lastNativeService = null;
        this.defaultCopies = true;
        Disposer.addRecord(this.disposerReferent, this.handleRecord = new HandleRecord());
        this.initAttributeMembers();
    }
    
    @Override
    public Object getDisposerReferent() {
        return this.disposerReferent;
    }
    
    @Override
    public PageFormat pageDialog(final PageFormat pageFormat) throws HeadlessException {
        if (GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException();
        }
        if (!(this.getPrintService() instanceof Win32PrintService)) {
            return super.pageDialog(pageFormat);
        }
        final PageFormat pageFormat2 = (PageFormat)pageFormat.clone();
        final WPageDialog wPageDialog = new WPageDialog((Frame)null, this, pageFormat2, null);
        wPageDialog.setRetVal(false);
        wPageDialog.setVisible(true);
        final boolean retVal = wPageDialog.getRetVal();
        wPageDialog.dispose();
        if (retVal && this.myService != null) {
            final String nativePrintService = this.getNativePrintService();
            if (!this.myService.getName().equals(nativePrintService)) {
                try {
                    this.setPrintService(PrintServiceLookupProvider.getWin32PrintLUS().getPrintServiceByName(nativePrintService));
                }
                catch (final PrinterException ex) {}
            }
            this.updatePageAttributes(this.myService, pageFormat2);
            return pageFormat2;
        }
        return pageFormat;
    }
    
    private boolean displayNativeDialog() {
        if (this.attributes == null) {
            return false;
        }
        final DialogOwner dialogOwner = (DialogOwner)this.attributes.get(DialogOwner.class);
        final Frame frame = (dialogOwner != null) ? dialogOwner.getOwner() : null;
        final WPrintDialog wPrintDialog = new WPrintDialog(frame, this);
        wPrintDialog.setRetVal(false);
        wPrintDialog.setVisible(true);
        final boolean retVal = wPrintDialog.getRetVal();
        wPrintDialog.dispose();
        final Destination destination = (Destination)this.attributes.get(Destination.class);
        if (destination == null || !retVal) {
            return retVal;
        }
        String string = null;
        final ResourceBundle bundle = ResourceBundle.getBundle("sun.print.resources.serviceui");
        try {
            string = bundle.getString("dialog.printtofile");
        }
        catch (final MissingResourceException ex) {}
        final FileDialog fileDialog = new FileDialog(frame, string, 1);
        final URI uri = destination.getURI();
        final String s = (uri != null) ? uri.getSchemeSpecificPart() : null;
        if (s != null) {
            final File file = new File(s);
            fileDialog.setFile(file.getName());
            final File parentFile = file.getParentFile();
            if (parentFile != null) {
                fileDialog.setDirectory(parentFile.getPath());
            }
        }
        else {
            fileDialog.setFile("out.prn");
        }
        fileDialog.setVisible(true);
        final String file2 = fileDialog.getFile();
        if (file2 == null) {
            fileDialog.dispose();
            return false;
        }
        String s2 = fileDialog.getDirectory() + file2;
        File file3 = new File(s2);
        for (File file4 = file3.getParentFile(); (file3.exists() && (!file3.isFile() || !file3.canWrite())) || (file4 != null && (!file4.exists() || (file4.exists() && !file4.canWrite()))); file3 = new File(s2), file4 = file3.getParentFile()) {
            new PrintToFileErrorDialog(frame, ServiceDialog.getMsg("dialog.owtitle"), ServiceDialog.getMsg("dialog.writeerror") + " " + s2, ServiceDialog.getMsg("button.ok")).setVisible(true);
            fileDialog.setVisible(true);
            final String file5 = fileDialog.getFile();
            if (file5 == null) {
                fileDialog.dispose();
                return false;
            }
            s2 = fileDialog.getDirectory() + file5;
        }
        fileDialog.dispose();
        this.attributes.add(new Destination(file3.toURI()));
        return true;
    }
    
    @Override
    public boolean printDialog() throws HeadlessException {
        if (GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException();
        }
        if (this.attributes == null) {
            this.attributes = new HashPrintRequestAttributeSet();
        }
        if (!(this.getPrintService() instanceof Win32PrintService)) {
            return super.printDialog(this.attributes);
        }
        return !this.noDefaultPrinter && this.displayNativeDialog();
    }
    
    @Override
    public void setPrintService(final PrintService printService) throws PrinterException {
        super.setPrintService(printService);
        if (!(printService instanceof Win32PrintService)) {
            return;
        }
        this.driverDoesMultipleCopies = false;
        this.driverDoesCollation = false;
        this.setNativePrintServiceIfNeeded(printService.getName());
    }
    
    private native void setNativePrintService(final String p0) throws PrinterException;
    
    private void setNativePrintServiceIfNeeded(final String s) throws PrinterException {
        if (s != null && !s.equals(this.lastNativeService)) {
            this.setNativePrintService(s);
            this.lastNativeService = s;
        }
    }
    
    @Override
    public PrintService getPrintService() {
        if (this.myService == null) {
            final String nativePrintService = this.getNativePrintService();
            if (nativePrintService != null) {
                this.myService = PrintServiceLookupProvider.getWin32PrintLUS().getPrintServiceByName(nativePrintService);
                if (this.myService != null) {
                    return this.myService;
                }
            }
            this.myService = PrintServiceLookup.lookupDefaultPrintService();
            if (this.myService instanceof Win32PrintService) {
                try {
                    this.setNativePrintServiceIfNeeded(this.myService.getName());
                }
                catch (final Exception ex) {
                    this.myService = null;
                }
            }
        }
        return this.myService;
    }
    
    private native String getNativePrintService();
    
    private void initAttributeMembers() {
        this.mAttSides = 0;
        this.mAttChromaticity = 0;
        this.mAttXRes = 0;
        this.mAttYRes = 0;
        this.mAttQuality = 0;
        this.mAttCollate = -1;
        this.mAttCopies = 0;
        this.mAttMediaTray = 0;
        this.mAttMediaSizeName = 0;
        this.mDestination = null;
    }
    
    @Override
    protected void setAttributes(final PrintRequestAttributeSet attributes) throws PrinterException {
        this.initAttributeMembers();
        super.setAttributes(attributes);
        this.mAttCopies = this.getCopiesInt();
        this.mDestination = this.destinationAttr;
        if (attributes == null) {
            return;
        }
        final Attribute[] array = attributes.toArray();
        for (int i = 0; i < array.length; ++i) {
            Attribute media = array[i];
            try {
                if (media.getCategory() == Sides.class) {
                    this.setSidesAttrib(media);
                }
                else if (media.getCategory() == Chromaticity.class) {
                    this.setColorAttrib(media);
                }
                else if (media.getCategory() == PrinterResolution.class) {
                    this.setResolutionAttrib(media);
                }
                else if (media.getCategory() == PrintQuality.class) {
                    this.setQualityAttrib(media);
                }
                else if (media.getCategory() == SheetCollate.class) {
                    this.setCollateAttrib(media);
                }
                else if (media.getCategory() == Media.class || media.getCategory() == SunAlternateMedia.class) {
                    if (media.getCategory() == SunAlternateMedia.class) {
                        final Media media2 = (Media)attributes.get(Media.class);
                        if (media2 == null || !(media2 instanceof MediaTray)) {
                            media = ((SunAlternateMedia)media).getMedia();
                        }
                    }
                    if (media instanceof MediaSizeName) {
                        this.setWin32MediaAttrib(media);
                    }
                    if (media instanceof MediaTray) {
                        this.setMediaTrayAttrib(media);
                    }
                }
            }
            catch (final ClassCastException ex) {}
        }
    }
    
    private native void getDefaultPage(final PageFormat p0);
    
    @Override
    public PageFormat defaultPage(final PageFormat pageFormat) {
        final PageFormat pageFormat2 = (PageFormat)pageFormat.clone();
        this.getDefaultPage(pageFormat2);
        return pageFormat2;
    }
    
    @Override
    protected native void validatePaper(final Paper p0, final Paper p1);
    
    @Override
    protected Graphics2D createPathGraphics(final PeekGraphics peekGraphics, final PrinterJob printerJob, final Printable printable, final PageFormat pageFormat, final int n) {
        final PeekMetrics metrics = peekGraphics.getMetrics();
        Graphics2D graphics2D;
        if (!WPrinterJob.forcePDL && (WPrinterJob.forceRaster || metrics.hasNonSolidColors() || metrics.hasCompositing())) {
            graphics2D = null;
        }
        else {
            graphics2D = new WPathGraphics(new BufferedImage(8, 8, 1).createGraphics(), printerJob, printable, pageFormat, n, !peekGraphics.getAWTDrawingOnly());
        }
        return graphics2D;
    }
    
    @Override
    protected double getXRes() {
        if (this.mAttXRes != 0) {
            return this.mAttXRes;
        }
        return this.mPrintXRes;
    }
    
    @Override
    protected double getYRes() {
        if (this.mAttYRes != 0) {
            return this.mAttYRes;
        }
        return this.mPrintYRes;
    }
    
    @Override
    protected double getPhysicalPrintableX(final Paper paper) {
        return this.mPrintPhysX;
    }
    
    @Override
    protected double getPhysicalPrintableY(final Paper paper) {
        return this.mPrintPhysY;
    }
    
    @Override
    protected double getPhysicalPrintableWidth(final Paper paper) {
        return this.mPrintWidth;
    }
    
    @Override
    protected double getPhysicalPrintableHeight(final Paper paper) {
        return this.mPrintHeight;
    }
    
    @Override
    protected double getPhysicalPageWidth(final Paper paper) {
        return this.mPageWidth;
    }
    
    @Override
    protected double getPhysicalPageHeight(final Paper paper) {
        return this.mPageHeight;
    }
    
    @Override
    protected boolean isCollated() {
        return this.userRequestedCollation;
    }
    
    @Override
    protected int getCollatedCopies() {
        this.debug_println("driverDoesMultipleCopies=" + this.driverDoesMultipleCopies + " driverDoesCollation=" + this.driverDoesCollation);
        if (super.isCollated() && !this.driverDoesCollation) {
            this.mAttCollate = 0;
            this.mAttCopies = 1;
            return this.getCopies();
        }
        return 1;
    }
    
    @Override
    protected int getNoncollatedCopies() {
        if (this.driverDoesMultipleCopies || super.isCollated()) {
            return 1;
        }
        return this.getCopies();
    }
    
    private long getPrintDC() {
        return this.handleRecord.mPrintDC;
    }
    
    private void setPrintDC(final long n) {
        this.handleRecord.mPrintDC = n;
    }
    
    private long getDevMode() {
        return this.handleRecord.mPrintHDevMode;
    }
    
    private void setDevMode(final long n) {
        this.handleRecord.mPrintHDevMode = n;
    }
    
    private long getDevNames() {
        return this.handleRecord.mPrintHDevNames;
    }
    
    private void setDevNames(final long n) {
        this.handleRecord.mPrintHDevNames = n;
    }
    
    protected void beginPath() {
        this.beginPath(this.getPrintDC());
    }
    
    protected void endPath() {
        this.endPath(this.getPrintDC());
    }
    
    protected void closeFigure() {
        this.closeFigure(this.getPrintDC());
    }
    
    protected void fillPath() {
        this.fillPath(this.getPrintDC());
    }
    
    protected void moveTo(final float n, final float n2) {
        this.moveTo(this.getPrintDC(), n, n2);
    }
    
    protected void lineTo(final float n, final float n2) {
        this.lineTo(this.getPrintDC(), n, n2);
    }
    
    protected void polyBezierTo(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        this.polyBezierTo(this.getPrintDC(), n, n2, n3, n4, n5, n6);
    }
    
    protected void setPolyFillMode(final int n) {
        this.setPolyFillMode(this.getPrintDC(), n);
    }
    
    protected void selectSolidBrush(final Color mLastColor) {
        if (!mLastColor.equals(this.mLastColor)) {
            this.mLastColor = mLastColor;
            final float[] rgbColorComponents = mLastColor.getRGBColorComponents(null);
            this.selectSolidBrush(this.getPrintDC(), (int)(rgbColorComponents[0] * 255.0f), (int)(rgbColorComponents[1] * 255.0f), (int)(rgbColorComponents[2] * 255.0f));
        }
    }
    
    protected int getPenX() {
        return this.getPenX(this.getPrintDC());
    }
    
    protected int getPenY() {
        return this.getPenY(this.getPrintDC());
    }
    
    protected void selectClipPath() {
        this.selectClipPath(this.getPrintDC());
    }
    
    protected void frameRect(final float n, final float n2, final float n3, final float n4) {
        this.frameRect(this.getPrintDC(), n, n2, n3, n4);
    }
    
    protected void fillRect(final float n, final float n2, final float n3, final float n4, final Color color) {
        final float[] rgbColorComponents = color.getRGBColorComponents(null);
        this.fillRect(this.getPrintDC(), n, n2, n3, n4, (int)(rgbColorComponents[0] * 255.0f), (int)(rgbColorComponents[1] * 255.0f), (int)(rgbColorComponents[2] * 255.0f));
    }
    
    protected void selectPen(final float n, final Color color) {
        final float[] rgbColorComponents = color.getRGBColorComponents(null);
        this.selectPen(this.getPrintDC(), n, (int)(rgbColorComponents[0] * 255.0f), (int)(rgbColorComponents[1] * 255.0f), (int)(rgbColorComponents[2] * 255.0f));
    }
    
    protected boolean selectStylePen(final int n, final int n2, final float n3, final Color color) {
        final float[] rgbColorComponents = color.getRGBColorComponents(null);
        long n4 = 0L;
        switch (n) {
            case 0: {
                n4 = 512L;
                break;
            }
            case 1: {
                n4 = 0L;
                break;
            }
            default: {
                n4 = 256L;
                break;
            }
        }
        long n5 = 0L;
        switch (n2) {
            case 2: {
                n5 = 4096L;
                break;
            }
            default: {
                n5 = 8192L;
                break;
            }
            case 1: {
                n5 = 0L;
                break;
            }
        }
        return this.selectStylePen(this.getPrintDC(), n4, n5, n3, (int)(rgbColorComponents[0] * 255.0f), (int)(rgbColorComponents[1] * 255.0f), (int)(rgbColorComponents[2] * 255.0f));
    }
    
    protected boolean setFont(final String mLastFontFamily, final float mLastFontSize, final int mLastFontStyle, final int mLastRotation, final float mLastAwScale) {
        boolean setFont = true;
        if (!mLastFontFamily.equals(this.mLastFontFamily) || mLastFontSize != this.mLastFontSize || mLastFontStyle != this.mLastFontStyle || mLastRotation != this.mLastRotation || mLastAwScale != this.mLastAwScale) {
            setFont = this.setFont(this.getPrintDC(), mLastFontFamily, mLastFontSize, (mLastFontStyle & 0x1) != 0x0, (mLastFontStyle & 0x2) != 0x0, mLastRotation, mLastAwScale);
            if (setFont) {
                this.mLastFontFamily = mLastFontFamily;
                this.mLastFontSize = mLastFontSize;
                this.mLastFontStyle = mLastFontStyle;
                this.mLastRotation = mLastRotation;
                this.mLastAwScale = mLastAwScale;
            }
        }
        return setFont;
    }
    
    protected void setTextColor(final Color mLastTextColor) {
        if (!mLastTextColor.equals(this.mLastTextColor)) {
            this.mLastTextColor = mLastTextColor;
            final float[] rgbColorComponents = mLastTextColor.getRGBColorComponents(null);
            this.setTextColor(this.getPrintDC(), (int)(rgbColorComponents[0] * 255.0f), (int)(rgbColorComponents[1] * 255.0f), (int)(rgbColorComponents[2] * 255.0f));
        }
    }
    
    @Override
    protected String removeControlChars(final String s) {
        return super.removeControlChars(s);
    }
    
    protected void textOut(final String s, final float n, final float n2, final float[] array) {
        final String removeControlChars = this.removeControlChars(s);
        assert removeControlChars.length() == s.length();
        if (removeControlChars.length() == 0) {
            return;
        }
        this.textOut(this.getPrintDC(), removeControlChars, removeControlChars.length(), false, n, n2, array);
    }
    
    protected void glyphsOut(final int[] array, final float n, final float n2, final float[] array2) {
        final char[] array3 = new char[array.length];
        for (int i = 0; i < array.length; ++i) {
            array3[i] = (char)(array[i] & 0xFFFF);
        }
        this.textOut(this.getPrintDC(), new String(array3), array.length, true, n, n2, array2);
    }
    
    protected int getGDIAdvance(String removeControlChars) {
        removeControlChars = this.removeControlChars(removeControlChars);
        if (removeControlChars.length() == 0) {
            return 0;
        }
        return this.getGDIAdvance(this.getPrintDC(), removeControlChars);
    }
    
    protected void drawImage3ByteBGR(final byte[] array, final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8) {
        this.drawDIBImage(this.getPrintDC(), array, n, n2, n3, n4, n5, n6, n7, n8, 24, null);
    }
    
    protected void drawDIBImage(final byte[] array, final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final int n9, final IndexColorModel indexColorModel) {
        int n10 = 24;
        byte[] array2 = null;
        if (indexColorModel != null) {
            n10 = n9;
            array2 = new byte[(1 << indexColorModel.getPixelSize()) * 4];
            for (int i = 0; i < indexColorModel.getMapSize(); ++i) {
                array2[i * 4 + 0] = (byte)(indexColorModel.getBlue(i) & 0xFF);
                array2[i * 4 + 1] = (byte)(indexColorModel.getGreen(i) & 0xFF);
                array2[i * 4 + 2] = (byte)(indexColorModel.getRed(i) & 0xFF);
            }
        }
        this.drawDIBImage(this.getPrintDC(), array, n, n2, n3, n4, n5, n6, n7, n8, n10, array2);
    }
    
    @Override
    protected void startPage(final PageFormat pageFormat, final Printable printable, final int n, final boolean b) {
        this.invalidateCachedState();
        this.deviceStartPage(pageFormat, printable, n, b);
    }
    
    @Override
    protected void endPage(final PageFormat pageFormat, final Printable printable, final int n) {
        this.deviceEndPage(pageFormat, printable, n);
    }
    
    private void invalidateCachedState() {
        this.mLastColor = null;
        this.mLastTextColor = null;
        this.mLastFontFamily = null;
    }
    
    @Override
    public void setCopies(final int n) {
        super.setCopies(n);
        this.defaultCopies = false;
        this.setNativeCopies(this.mAttCopies = n);
    }
    
    private native void setNativeCopies(final int p0);
    
    private native boolean jobSetup(final Pageable p0, final boolean p1);
    
    @Override
    protected native void initPrinter();
    
    private native boolean _startDoc(final String p0, final String p1) throws PrinterException;
    
    @Override
    protected void startDoc() throws PrinterException {
        if (!this._startDoc(this.mDestination, this.getJobName())) {
            this.cancel();
        }
    }
    
    @Override
    protected native void endDoc();
    
    @Override
    protected native void abortDoc();
    
    private static native void deleteDC(final long p0, final long p1, final long p2);
    
    protected native void deviceStartPage(final PageFormat p0, final Printable p1, final int p2, final boolean p3);
    
    protected native void deviceEndPage(final PageFormat p0, final Printable p1, final int p2);
    
    @Override
    protected native void printBand(final byte[] p0, final int p1, final int p2, final int p3, final int p4);
    
    protected native void beginPath(final long p0);
    
    protected native void endPath(final long p0);
    
    protected native void closeFigure(final long p0);
    
    protected native void fillPath(final long p0);
    
    protected native void moveTo(final long p0, final float p1, final float p2);
    
    protected native void lineTo(final long p0, final float p1, final float p2);
    
    protected native void polyBezierTo(final long p0, final float p1, final float p2, final float p3, final float p4, final float p5, final float p6);
    
    protected native void setPolyFillMode(final long p0, final int p1);
    
    protected native void selectSolidBrush(final long p0, final int p1, final int p2, final int p3);
    
    protected native int getPenX(final long p0);
    
    protected native int getPenY(final long p0);
    
    protected native void selectClipPath(final long p0);
    
    protected native void frameRect(final long p0, final float p1, final float p2, final float p3, final float p4);
    
    protected native void fillRect(final long p0, final float p1, final float p2, final float p3, final float p4, final int p5, final int p6, final int p7);
    
    protected native void selectPen(final long p0, final float p1, final int p2, final int p3, final int p4);
    
    protected native boolean selectStylePen(final long p0, final long p1, final long p2, final float p3, final int p4, final int p5, final int p6);
    
    protected native boolean setFont(final long p0, final String p1, final float p2, final boolean p3, final boolean p4, final int p5, final float p6);
    
    protected native void setTextColor(final long p0, final int p1, final int p2, final int p3);
    
    protected native void textOut(final long p0, final String p1, final int p2, final boolean p3, final float p4, final float p5, final float[] p6);
    
    private native int getGDIAdvance(final long p0, final String p1);
    
    private native void drawDIBImage(final long p0, final byte[] p1, final float p2, final float p3, final float p4, final float p5, final float p6, final float p7, final float p8, final float p9, final int p10, final byte[] p11);
    
    private final String getPrinterAttrib() {
        final PrintService printService = this.getPrintService();
        return (printService != null) ? printService.getName() : null;
    }
    
    private final int getCollateAttrib() {
        return this.mAttCollate;
    }
    
    private void setCollateAttrib(final Attribute attribute) {
        if (attribute == SheetCollate.COLLATED) {
            this.mAttCollate = 1;
        }
        else {
            this.mAttCollate = 0;
        }
    }
    
    private void setCollateAttrib(final Attribute collateAttrib, final PrintRequestAttributeSet set) {
        this.setCollateAttrib(collateAttrib);
        set.add(collateAttrib);
    }
    
    private final int getOrientAttrib() {
        int n = 1;
        OrientationRequested orientationRequested = (this.attributes == null) ? null : ((OrientationRequested)this.attributes.get(OrientationRequested.class));
        if (orientationRequested == null) {
            orientationRequested = (OrientationRequested)this.myService.getDefaultAttributeValue(OrientationRequested.class);
        }
        if (orientationRequested != null) {
            if (orientationRequested == OrientationRequested.REVERSE_LANDSCAPE) {
                n = 2;
            }
            else if (orientationRequested == OrientationRequested.LANDSCAPE) {
                n = 0;
            }
        }
        return n;
    }
    
    private void setOrientAttrib(final Attribute attribute, final PrintRequestAttributeSet set) {
        if (set != null) {
            set.add(attribute);
        }
    }
    
    private final int getCopiesAttrib() {
        if (this.defaultCopies) {
            return 0;
        }
        return this.getCopiesInt();
    }
    
    private final void setRangeCopiesAttribute(final int n, final int n2, final boolean b, final int n3) {
        if (this.attributes != null) {
            if (b) {
                this.attributes.add(new PageRanges(n, n2));
                this.setPageRange(n, n2);
            }
            this.defaultCopies = false;
            this.attributes.add(new Copies(n3));
            super.setCopies(n3);
            this.mAttCopies = n3;
        }
    }
    
    private final boolean getDestAttrib() {
        return this.mDestination != null;
    }
    
    private final int getQualityAttrib() {
        return this.mAttQuality;
    }
    
    private void setQualityAttrib(final Attribute attribute) {
        if (attribute == PrintQuality.HIGH) {
            this.mAttQuality = -4;
        }
        else if (attribute == PrintQuality.NORMAL) {
            this.mAttQuality = -3;
        }
        else {
            this.mAttQuality = -2;
        }
    }
    
    private void setQualityAttrib(final Attribute qualityAttrib, final PrintRequestAttributeSet set) {
        this.setQualityAttrib(qualityAttrib);
        set.add(qualityAttrib);
    }
    
    private final int getColorAttrib() {
        return this.mAttChromaticity;
    }
    
    private void setColorAttrib(final Attribute attribute) {
        if (attribute == Chromaticity.COLOR) {
            this.mAttChromaticity = 2;
        }
        else {
            this.mAttChromaticity = 1;
        }
    }
    
    private void setColorAttrib(final Attribute colorAttrib, final PrintRequestAttributeSet set) {
        this.setColorAttrib(colorAttrib);
        set.add(colorAttrib);
    }
    
    private final int getSidesAttrib() {
        return this.mAttSides;
    }
    
    private void setSidesAttrib(final Attribute attribute) {
        if (attribute == Sides.TWO_SIDED_LONG_EDGE) {
            this.mAttSides = 2;
        }
        else if (attribute == Sides.TWO_SIDED_SHORT_EDGE) {
            this.mAttSides = 3;
        }
        else {
            this.mAttSides = 1;
        }
    }
    
    private void setSidesAttrib(final Attribute sidesAttrib, final PrintRequestAttributeSet set) {
        this.setSidesAttrib(sidesAttrib);
        set.add(sidesAttrib);
    }
    
    private final int[] getWin32MediaAttrib() {
        final int[] array = { 0, 0 };
        if (this.attributes != null) {
            final Media media = (Media)this.attributes.get(Media.class);
            if (media instanceof MediaSizeName) {
                final MediaSize mediaSizeForName = MediaSize.getMediaSizeForName((MediaSizeName)media);
                if (mediaSizeForName != null) {
                    array[0] = (int)(mediaSizeForName.getX(25400) * 72.0);
                    array[1] = (int)(mediaSizeForName.getY(25400) * 72.0);
                }
            }
        }
        return array;
    }
    
    private void setWin32MediaAttrib(final Attribute attribute) {
        if (!(attribute instanceof MediaSizeName)) {
            return;
        }
        this.mAttMediaSizeName = ((Win32PrintService)this.myService).findPaperID((MediaSizeName)attribute);
    }
    
    private void addPaperSize(final PrintRequestAttributeSet set, final int n, final int n2, final int n3) {
        if (set == null) {
            return;
        }
        MediaSizeName mediaSizeName = ((Win32PrintService)this.myService).findWin32Media(n);
        if (mediaSizeName == null) {
            mediaSizeName = ((Win32PrintService)this.myService).findMatchingMediaSizeNameMM((float)n2, (float)n3);
        }
        if (mediaSizeName != null) {
            set.add(mediaSizeName);
        }
    }
    
    private void setWin32MediaAttrib(final int mAttMediaSizeName, final int n, final int n2) {
        this.addPaperSize(this.attributes, mAttMediaSizeName, n, n2);
        this.mAttMediaSizeName = mAttMediaSizeName;
    }
    
    private void setMediaTrayAttrib(final Attribute attribute) {
        if (attribute == MediaTray.BOTTOM) {
            this.mAttMediaTray = 2;
        }
        else if (attribute == MediaTray.ENVELOPE) {
            this.mAttMediaTray = 5;
        }
        else if (attribute == MediaTray.LARGE_CAPACITY) {
            this.mAttMediaTray = 11;
        }
        else if (attribute == MediaTray.MAIN) {
            this.mAttMediaTray = 1;
        }
        else if (attribute == MediaTray.MANUAL) {
            this.mAttMediaTray = 4;
        }
        else if (attribute == MediaTray.MIDDLE) {
            this.mAttMediaTray = 3;
        }
        else if (attribute == MediaTray.SIDE) {
            this.mAttMediaTray = 7;
        }
        else if (attribute == MediaTray.TOP) {
            this.mAttMediaTray = 1;
        }
        else if (attribute instanceof Win32MediaTray) {
            this.mAttMediaTray = ((Win32MediaTray)attribute).winID;
        }
        else {
            this.mAttMediaTray = 1;
        }
    }
    
    private void setMediaTrayAttrib(final int mAttMediaTray) {
        this.mAttMediaTray = mAttMediaTray;
        ((Win32PrintService)this.myService).findMediaTray(mAttMediaTray);
    }
    
    private int getMediaTrayAttrib() {
        return this.mAttMediaTray;
    }
    
    private final boolean getPrintToFileEnabled() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            final FilePermission filePermission = new FilePermission("<<ALL FILES>>", "read,write");
            try {
                securityManager.checkPermission(filePermission);
            }
            catch (final SecurityException ex) {
                return false;
            }
        }
        return true;
    }
    
    private final void setNativeAttributes(final int n, final int n2, final int n3) {
        if (this.attributes == null) {
            return;
        }
        if ((n & 0x20) != 0x0) {
            if (this.attributes.get(Destination.class) == null) {
                try {
                    this.attributes.add(new Destination(new File("./out.prn").toURI()));
                }
                catch (final SecurityException ex) {
                    try {
                        this.attributes.add(new Destination(new URI("file:out.prn")));
                    }
                    catch (final URISyntaxException ex2) {}
                }
            }
        }
        else {
            this.attributes.remove(Destination.class);
        }
        if ((n & 0x10) != 0x0) {
            this.setCollateAttrib(SheetCollate.COLLATED, this.attributes);
        }
        else {
            this.setCollateAttrib(SheetCollate.UNCOLLATED, this.attributes);
        }
        if ((n & 0x2) != 0x0) {
            this.attributes.add(SunPageSelection.RANGE);
        }
        else if ((n & 0x1) != 0x0) {
            this.attributes.add(SunPageSelection.SELECTION);
        }
        else {
            this.attributes.add(SunPageSelection.ALL);
        }
        if ((n2 & 0x1) != 0x0) {
            if ((n3 & 0x4000) != 0x0) {
                this.setOrientAttrib(OrientationRequested.LANDSCAPE, this.attributes);
            }
            else {
                this.setOrientAttrib(OrientationRequested.PORTRAIT, this.attributes);
            }
        }
        if ((n2 & 0x800) != 0x0) {
            if ((n3 & 0x200) != 0x0) {
                this.setColorAttrib(Chromaticity.COLOR, this.attributes);
            }
            else {
                this.setColorAttrib(Chromaticity.MONOCHROME, this.attributes);
            }
        }
        if ((n2 & 0x400) != 0x0) {
            PrintQuality printQuality;
            if ((n3 & 0x80) != 0x0) {
                printQuality = PrintQuality.DRAFT;
            }
            else if ((n2 & 0x40) != 0x0) {
                printQuality = PrintQuality.HIGH;
            }
            else {
                printQuality = PrintQuality.NORMAL;
            }
            this.setQualityAttrib(printQuality, this.attributes);
        }
        if ((n2 & 0x1000) != 0x0) {
            Sides sides;
            if ((n3 & 0x10) != 0x0) {
                sides = Sides.TWO_SIDED_LONG_EDGE;
            }
            else if ((n3 & 0x20) != 0x0) {
                sides = Sides.TWO_SIDED_SHORT_EDGE;
            }
            else {
                sides = Sides.ONE_SIDED;
            }
            this.setSidesAttrib(sides, this.attributes);
        }
    }
    
    private void getDevModeValues(final PrintRequestAttributeSet set, final DevModeValues devModeValues) {
        final Copies copies = (Copies)set.get(Copies.class);
        if (copies != null) {
            devModeValues.dmFields |= 0x100;
            devModeValues.copies = (short)copies.getValue();
        }
        final SheetCollate sheetCollate = (SheetCollate)set.get(SheetCollate.class);
        if (sheetCollate != null) {
            devModeValues.dmFields |= 0x8000;
            devModeValues.collate = (short)((sheetCollate == SheetCollate.COLLATED) ? 1 : 0);
        }
        final Chromaticity chromaticity = (Chromaticity)set.get(Chromaticity.class);
        if (chromaticity != null) {
            devModeValues.dmFields |= 0x800;
            if (chromaticity == Chromaticity.COLOR) {
                devModeValues.color = 2;
            }
            else {
                devModeValues.color = 1;
            }
        }
        final Sides sides = (Sides)set.get(Sides.class);
        if (sides != null) {
            devModeValues.dmFields |= 0x1000;
            if (sides == Sides.TWO_SIDED_LONG_EDGE) {
                devModeValues.duplex = 2;
            }
            else if (sides == Sides.TWO_SIDED_SHORT_EDGE) {
                devModeValues.duplex = 3;
            }
            else {
                devModeValues.duplex = 1;
            }
        }
        final OrientationRequested orientationRequested = (OrientationRequested)set.get(OrientationRequested.class);
        if (orientationRequested != null) {
            devModeValues.dmFields |= 0x1;
            devModeValues.orient = (short)((orientationRequested == OrientationRequested.LANDSCAPE) ? 2 : 1);
        }
        final Media media = (Media)set.get(Media.class);
        if (media instanceof MediaSizeName) {
            devModeValues.dmFields |= 0x2;
            devModeValues.paper = (short)((Win32PrintService)this.myService).findPaperID((MediaSizeName)media);
        }
        MediaTray mediaTray = null;
        if (media instanceof MediaTray) {
            mediaTray = (MediaTray)media;
        }
        if (mediaTray == null) {
            final SunAlternateMedia sunAlternateMedia = (SunAlternateMedia)set.get(SunAlternateMedia.class);
            if (sunAlternateMedia != null && sunAlternateMedia.getMedia() instanceof MediaTray) {
                mediaTray = (MediaTray)sunAlternateMedia.getMedia();
            }
        }
        if (mediaTray != null) {
            devModeValues.dmFields |= 0x200;
            devModeValues.bin = (short)((Win32PrintService)this.myService).findTrayID(mediaTray);
        }
        final PrintQuality printQuality = (PrintQuality)set.get(PrintQuality.class);
        if (printQuality != null) {
            devModeValues.dmFields |= 0x400;
            if (printQuality == PrintQuality.DRAFT) {
                devModeValues.xres_quality = -1;
            }
            else if (printQuality == PrintQuality.HIGH) {
                devModeValues.xres_quality = -4;
            }
            else {
                devModeValues.xres_quality = -3;
            }
        }
        final PrinterResolution printerResolution = (PrinterResolution)set.get(PrinterResolution.class);
        if (printerResolution != null) {
            devModeValues.dmFields |= 0x2400;
            devModeValues.xres_quality = (short)printerResolution.getCrossFeedResolution(100);
            devModeValues.yres = (short)printerResolution.getFeedResolution(100);
        }
    }
    
    private final void setJobAttributes(final PrintRequestAttributeSet set, final int n, final int n2, final short n3, final short n4, final short n5, final short n6, final short n7, final short n8, final short n9) {
        if (set == null) {
            return;
        }
        if ((n & 0x100) != 0x0) {
            set.add(new Copies(n3));
        }
        if ((n & 0x8000) != 0x0) {
            if ((n2 & 0x8000) != 0x0) {
                set.add(SheetCollate.COLLATED);
            }
            else {
                set.add(SheetCollate.UNCOLLATED);
            }
        }
        if ((n & 0x1) != 0x0) {
            if ((n2 & 0x4000) != 0x0) {
                set.add(OrientationRequested.LANDSCAPE);
            }
            else {
                set.add(OrientationRequested.PORTRAIT);
            }
        }
        if ((n & 0x800) != 0x0) {
            if ((n2 & 0x200) != 0x0) {
                set.add(Chromaticity.COLOR);
            }
            else {
                set.add(Chromaticity.MONOCHROME);
            }
        }
        if ((n & 0x400) != 0x0) {
            if (n8 < 0) {
                PrintQuality printQuality;
                if ((n2 & 0x80) != 0x0) {
                    printQuality = PrintQuality.DRAFT;
                }
                else if ((n & 0x40) != 0x0) {
                    printQuality = PrintQuality.HIGH;
                }
                else {
                    printQuality = PrintQuality.NORMAL;
                }
                set.add(printQuality);
            }
            else if (n8 > 0 && n9 > 0) {
                set.add(new PrinterResolution(n8, n9, 100));
            }
        }
        if ((n & 0x1000) != 0x0) {
            Sides sides;
            if ((n2 & 0x10) != 0x0) {
                sides = Sides.TWO_SIDED_LONG_EDGE;
            }
            else if ((n2 & 0x20) != 0x0) {
                sides = Sides.TWO_SIDED_SHORT_EDGE;
            }
            else {
                sides = Sides.ONE_SIDED;
            }
            set.add(sides);
        }
        if ((n & 0x2) != 0x0) {
            this.addPaperSize(set, n4, n5, n6);
        }
        if ((n & 0x200) != 0x0) {
            set.add(new SunAlternateMedia(((Win32PrintService)this.myService).findMediaTray(n7)));
        }
    }
    
    private native boolean showDocProperties(final long p0, final PrintRequestAttributeSet p1, final int p2, final short p3, final short p4, final short p5, final short p6, final short p7, final short p8, final short p9, final short p10, final short p11);
    
    public PrintRequestAttributeSet showDocumentProperties(final Window window, final PrintService printService, final PrintRequestAttributeSet set) {
        try {
            this.setNativePrintServiceIfNeeded(printService.getName());
        }
        catch (final PrinterException ex) {}
        final long hWnd = ((WWindowPeer)window.getPeer()).getHWnd();
        final DevModeValues devModeValues = new DevModeValues();
        this.getDevModeValues(set, devModeValues);
        if (this.showDocProperties(hWnd, set, devModeValues.dmFields, devModeValues.copies, devModeValues.collate, devModeValues.color, devModeValues.duplex, devModeValues.orient, devModeValues.paper, devModeValues.bin, devModeValues.xres_quality, devModeValues.yres)) {
            return set;
        }
        return null;
    }
    
    private final void setResolutionDPI(final int mAttXRes, final int mAttYRes) {
        if (this.attributes != null) {
            this.attributes.add(new PrinterResolution(mAttXRes, mAttYRes, 100));
        }
        this.mAttXRes = mAttXRes;
        this.mAttYRes = mAttYRes;
    }
    
    private void setResolutionAttrib(final Attribute attribute) {
        final PrinterResolution printerResolution = (PrinterResolution)attribute;
        this.mAttXRes = printerResolution.getCrossFeedResolution(100);
        this.mAttYRes = printerResolution.getFeedResolution(100);
    }
    
    private void setPrinterNameAttrib(final String s) {
        final PrintService printService = this.getPrintService();
        if (s == null) {
            return;
        }
        if (printService != null && s.equals(printService.getName())) {
            return;
        }
        final PrintService[] lookupPrintServices = PrinterJob.lookupPrintServices();
        for (int i = 0; i < lookupPrintServices.length; ++i) {
            if (s.equals(lookupPrintServices[i].getName())) {
                try {
                    this.setPrintService(lookupPrintServices[i]);
                }
                catch (final PrinterException ex) {}
                return;
            }
        }
    }
    
    private static native void initIDs();
    
    static {
        Toolkit.getDefaultToolkit();
        initIDs();
        Win32FontManager.registerJREFontsForPrinting();
    }
    
    static class HandleRecord implements DisposerRecord
    {
        private long mPrintDC;
        private long mPrintHDevMode;
        private long mPrintHDevNames;
        
        @Override
        public void dispose() {
            deleteDC(this.mPrintDC, this.mPrintHDevMode, this.mPrintHDevNames);
        }
    }
    
    private static final class DevModeValues
    {
        int dmFields;
        short copies;
        short collate;
        short color;
        short duplex;
        short orient;
        short paper;
        short bin;
        short xres_quality;
        short yres;
    }
    
    class PrintToFileErrorDialog extends Dialog implements ActionListener
    {
        public PrintToFileErrorDialog(final Frame frame, final String s, final String s2, final String s3) {
            super(frame, s, true);
            this.init(frame, s, s2, s3);
        }
        
        public PrintToFileErrorDialog(final Dialog dialog, final String s, final String s2, final String s3) {
            super(dialog, s, true);
            this.init(dialog, s, s2, s3);
        }
        
        private void init(final Component component, final String s, final String s2, final String s3) {
            final Panel panel = new Panel();
            this.add("Center", new Label(s2));
            final Button button = new Button(s3);
            button.addActionListener(this);
            panel.add(button);
            this.add("South", panel);
            this.pack();
            final Dimension size = this.getSize();
            if (component != null) {
                final Rectangle bounds = component.getBounds();
                this.setLocation(bounds.x + (bounds.width - size.width) / 2, bounds.y + (bounds.height - size.height) / 2);
            }
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            this.setVisible(false);
            this.dispose();
        }
    }
}
