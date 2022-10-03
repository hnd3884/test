package java.awt.print;

import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.Attribute;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.Media;
import javax.print.attribute.PrintRequestAttributeSet;
import java.awt.HeadlessException;
import javax.print.StreamPrintServiceFactory;
import javax.print.attribute.AttributeSet;
import javax.print.PrintServiceLookup;
import javax.print.DocFlavor;
import javax.print.PrintService;
import java.security.AccessController;
import java.awt.AWTError;
import java.security.PrivilegedAction;

public abstract class PrinterJob
{
    public static PrinterJob getPrinterJob() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPrintJobAccess();
        }
        return AccessController.doPrivileged((PrivilegedAction<PrinterJob>)new PrivilegedAction() {
            @Override
            public Object run() {
                final String property = System.getProperty("java.awt.printerjob", null);
                try {
                    return (PrinterJob)Class.forName(property).newInstance();
                }
                catch (final ClassNotFoundException ex) {
                    throw new AWTError("PrinterJob not found: " + property);
                }
                catch (final InstantiationException ex2) {
                    throw new AWTError("Could not instantiate PrinterJob: " + property);
                }
                catch (final IllegalAccessException ex3) {
                    throw new AWTError("Could not access PrinterJob: " + property);
                }
            }
        });
    }
    
    public static PrintService[] lookupPrintServices() {
        return PrintServiceLookup.lookupPrintServices(DocFlavor.SERVICE_FORMATTED.PAGEABLE, null);
    }
    
    public static StreamPrintServiceFactory[] lookupStreamPrintServices(final String s) {
        return StreamPrintServiceFactory.lookupStreamPrintServiceFactories(DocFlavor.SERVICE_FORMATTED.PAGEABLE, s);
    }
    
    public PrintService getPrintService() {
        return null;
    }
    
    public void setPrintService(final PrintService printService) throws PrinterException {
        throw new PrinterException("Setting a service is not supported on this class");
    }
    
    public abstract void setPrintable(final Printable p0);
    
    public abstract void setPrintable(final Printable p0, final PageFormat p1);
    
    public abstract void setPageable(final Pageable p0) throws NullPointerException;
    
    public abstract boolean printDialog() throws HeadlessException;
    
    public boolean printDialog(final PrintRequestAttributeSet set) throws HeadlessException {
        if (set == null) {
            throw new NullPointerException("attributes");
        }
        return this.printDialog();
    }
    
    public abstract PageFormat pageDialog(final PageFormat p0) throws HeadlessException;
    
    public PageFormat pageDialog(final PrintRequestAttributeSet set) throws HeadlessException {
        if (set == null) {
            throw new NullPointerException("attributes");
        }
        return this.pageDialog(this.defaultPage());
    }
    
    public abstract PageFormat defaultPage(final PageFormat p0);
    
    public PageFormat defaultPage() {
        return this.defaultPage(new PageFormat());
    }
    
    public PageFormat getPageFormat(final PrintRequestAttributeSet set) {
        final PrintService printService = this.getPrintService();
        final PageFormat defaultPage = this.defaultPage();
        if (printService == null || set == null) {
            return defaultPage;
        }
        final Media media = (Media)set.get(Media.class);
        MediaPrintableArea mediaPrintableArea = (MediaPrintableArea)set.get(MediaPrintableArea.class);
        final OrientationRequested orientationRequested = (OrientationRequested)set.get(OrientationRequested.class);
        if (media == null && mediaPrintableArea == null && orientationRequested == null) {
            return defaultPage;
        }
        final Paper paper = defaultPage.getPaper();
        if (mediaPrintableArea == null && media != null && printService.isAttributeCategorySupported(MediaPrintableArea.class)) {
            final Object supportedAttributeValues = printService.getSupportedAttributeValues(MediaPrintableArea.class, null, set);
            if (supportedAttributeValues instanceof MediaPrintableArea[] && ((MediaPrintableArea[])supportedAttributeValues).length > 0) {
                mediaPrintableArea = ((MediaPrintableArea[])supportedAttributeValues)[0];
            }
        }
        if (media != null && printService.isAttributeValueSupported(media, null, set) && media instanceof MediaSizeName) {
            final MediaSize mediaSizeForName = MediaSize.getMediaSizeForName((MediaSizeName)media);
            if (mediaSizeForName != null) {
                final double n = 72.0;
                final double n2 = mediaSizeForName.getX(25400) * n;
                final double n3 = mediaSizeForName.getY(25400) * n;
                paper.setSize(n2, n3);
                if (mediaPrintableArea == null) {
                    paper.setImageableArea(n, n, n2 - 2.0 * n, n3 - 2.0 * n);
                }
            }
        }
        if (mediaPrintableArea != null && printService.isAttributeValueSupported(mediaPrintableArea, null, set)) {
            final float[] printableArea = mediaPrintableArea.getPrintableArea(25400);
            for (int i = 0; i < printableArea.length; ++i) {
                printableArea[i] *= 72.0f;
            }
            paper.setImageableArea(printableArea[0], printableArea[1], printableArea[2], printableArea[3]);
        }
        if (orientationRequested != null && printService.isAttributeValueSupported(orientationRequested, null, set)) {
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
            defaultPage.setOrientation(orientation);
        }
        defaultPage.setPaper(paper);
        return this.validatePage(defaultPage);
    }
    
    public abstract PageFormat validatePage(final PageFormat p0);
    
    public abstract void print() throws PrinterException;
    
    public void print(final PrintRequestAttributeSet set) throws PrinterException {
        this.print();
    }
    
    public abstract void setCopies(final int p0);
    
    public abstract int getCopies();
    
    public abstract String getUserName();
    
    public abstract void setJobName(final String p0);
    
    public abstract String getJobName();
    
    public abstract void cancel();
    
    public abstract boolean isCancelled();
}
