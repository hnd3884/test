package sun.print;

import java.net.URI;
import java.io.File;
import javax.print.attribute.standard.Destination;
import javax.print.attribute.standard.Fidelity;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.standard.DocumentName;
import javax.print.attribute.standard.JobName;
import java.util.Locale;
import javax.print.attribute.standard.JobOriginatingUserName;
import javax.print.attribute.standard.RequestingUserName;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import java.awt.print.PrinterException;
import java.awt.print.Paper;
import java.awt.print.PageFormat;
import javax.print.attribute.standard.Media;
import javax.print.attribute.Attribute;
import javax.print.attribute.standard.Copies;
import sun.awt.windows.WPrinterJob;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.awt.print.Pageable;
import java.net.URL;
import java.awt.print.Printable;
import javax.print.DocFlavor;
import javax.print.attribute.standard.PrinterIsAcceptingJobs;
import javax.print.attribute.standard.PrinterStateReason;
import javax.print.attribute.standard.PrinterStateReasons;
import javax.print.attribute.standard.PrinterState;
import javax.print.PrintException;
import javax.print.event.PrintJobAttributeListener;
import javax.print.DocPrintJob;
import javax.print.event.PrintJobEvent;
import java.io.IOException;
import javax.print.event.PrintJobListener;
import javax.print.attribute.AttributeSetUtilities;
import javax.print.attribute.HashPrintJobAttributeSet;
import javax.print.PrintService;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import java.io.Reader;
import java.io.InputStream;
import javax.print.Doc;
import java.awt.print.PrinterJob;
import javax.print.attribute.PrintJobAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import java.util.Vector;
import javax.print.CancelablePrintJob;

public class Win32PrintJob implements CancelablePrintJob
{
    private transient Vector jobListeners;
    private transient Vector attrListeners;
    private transient Vector listenedAttributeSets;
    private Win32PrintService service;
    private boolean fidelity;
    private boolean printing;
    private boolean printReturned;
    private PrintRequestAttributeSet reqAttrSet;
    private PrintJobAttributeSet jobAttrSet;
    private PrinterJob job;
    private Doc doc;
    private String mDestination;
    private InputStream instream;
    private Reader reader;
    private String jobName;
    private int copies;
    private MediaSizeName mediaName;
    private MediaSize mediaSize;
    private OrientationRequested orient;
    private long hPrintJob;
    private static final int PRINTBUFFERLEN = 8192;
    
    Win32PrintJob(final Win32PrintService service) {
        this.printing = false;
        this.printReturned = false;
        this.reqAttrSet = null;
        this.jobAttrSet = null;
        this.mDestination = null;
        this.instream = null;
        this.reader = null;
        this.jobName = "Java Printing";
        this.copies = 0;
        this.mediaName = null;
        this.mediaSize = null;
        this.orient = null;
        this.service = service;
    }
    
    @Override
    public PrintService getPrintService() {
        return this.service;
    }
    
    @Override
    public PrintJobAttributeSet getAttributes() {
        synchronized (this) {
            if (this.jobAttrSet == null) {
                return AttributeSetUtilities.unmodifiableView(new HashPrintJobAttributeSet());
            }
            return this.jobAttrSet;
        }
    }
    
    @Override
    public void addPrintJobListener(final PrintJobListener printJobListener) {
        synchronized (this) {
            if (printJobListener == null) {
                return;
            }
            if (this.jobListeners == null) {
                this.jobListeners = new Vector();
            }
            this.jobListeners.add(printJobListener);
        }
    }
    
    @Override
    public void removePrintJobListener(final PrintJobListener printJobListener) {
        synchronized (this) {
            if (printJobListener == null || this.jobListeners == null) {
                return;
            }
            this.jobListeners.remove(printJobListener);
            if (this.jobListeners.isEmpty()) {
                this.jobListeners = null;
            }
        }
    }
    
    private void closeDataStreams() {
        if (this.doc == null) {
            return;
        }
        Object printData;
        try {
            printData = this.doc.getPrintData();
        }
        catch (final IOException ex) {
            return;
        }
        if (this.instream != null) {
            try {
                this.instream.close();
            }
            catch (final IOException ex2) {}
            finally {
                this.instream = null;
            }
        }
        else if (this.reader != null) {
            try {
                this.reader.close();
            }
            catch (final IOException ex3) {}
            finally {
                this.reader = null;
            }
        }
        else if (printData instanceof InputStream) {
            try {
                ((InputStream)printData).close();
            }
            catch (final IOException ex4) {}
        }
        else if (printData instanceof Reader) {
            try {
                ((Reader)printData).close();
            }
            catch (final IOException ex5) {}
        }
    }
    
    private void notifyEvent(final int n) {
        switch (n) {
            case 101:
            case 102:
            case 103:
            case 105:
            case 106: {
                this.closeDataStreams();
                break;
            }
        }
        synchronized (this) {
            if (this.jobListeners != null) {
                final PrintJobEvent printJobEvent = new PrintJobEvent(this, n);
                for (int i = 0; i < this.jobListeners.size(); ++i) {
                    final PrintJobListener printJobListener = this.jobListeners.elementAt(i);
                    switch (n) {
                        case 102: {
                            printJobListener.printJobCompleted(printJobEvent);
                            break;
                        }
                        case 101: {
                            printJobListener.printJobCanceled(printJobEvent);
                            break;
                        }
                        case 103: {
                            printJobListener.printJobFailed(printJobEvent);
                            break;
                        }
                        case 106: {
                            printJobListener.printDataTransferCompleted(printJobEvent);
                            break;
                        }
                        case 105: {
                            printJobListener.printJobNoMoreEvents(printJobEvent);
                            break;
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void addPrintJobAttributeListener(final PrintJobAttributeListener printJobAttributeListener, PrintJobAttributeSet set) {
        synchronized (this) {
            if (printJobAttributeListener == null) {
                return;
            }
            if (this.attrListeners == null) {
                this.attrListeners = new Vector();
                this.listenedAttributeSets = new Vector();
            }
            this.attrListeners.add(printJobAttributeListener);
            if (set == null) {
                set = new HashPrintJobAttributeSet();
            }
            this.listenedAttributeSets.add(set);
        }
    }
    
    @Override
    public void removePrintJobAttributeListener(final PrintJobAttributeListener printJobAttributeListener) {
        synchronized (this) {
            if (printJobAttributeListener == null || this.attrListeners == null) {
                return;
            }
            final int index = this.attrListeners.indexOf(printJobAttributeListener);
            if (index == -1) {
                return;
            }
            this.attrListeners.remove(index);
            this.listenedAttributeSets.remove(index);
            if (this.attrListeners.isEmpty()) {
                this.attrListeners = null;
                this.listenedAttributeSets = null;
            }
        }
    }
    
    @Override
    public void print(final Doc doc, final PrintRequestAttributeSet set) throws PrintException {
        synchronized (this) {
            if (this.printing) {
                throw new PrintException("already printing");
            }
            this.printing = true;
        }
        if (this.service.getAttribute(PrinterState.class) == PrinterState.STOPPED) {
            final PrinterStateReasons printerStateReasons = this.service.getAttribute(PrinterStateReasons.class);
            if (printerStateReasons != null && printerStateReasons.containsKey(PrinterStateReason.SHUTDOWN)) {
                throw new PrintException("PrintService is no longer available.");
            }
        }
        if (this.service.getAttribute(PrinterIsAcceptingJobs.class) == PrinterIsAcceptingJobs.NOT_ACCEPTING_JOBS) {
            throw new PrintException("Printer is not accepting job.");
        }
        this.doc = doc;
        final DocFlavor docFlavor = doc.getDocFlavor();
        Object printData;
        try {
            printData = doc.getPrintData();
        }
        catch (final IOException ex) {
            this.notifyEvent(103);
            throw new PrintException("can't get print data: " + ex.toString());
        }
        if (printData == null) {
            throw new PrintException("Null print data.");
        }
        if (docFlavor == null || !this.service.isDocFlavorSupported(docFlavor)) {
            this.notifyEvent(103);
            throw new PrintJobFlavorException("invalid flavor", docFlavor);
        }
        this.initializeAttributeSets(doc, set);
        this.getAttributeValues(docFlavor);
        final String representationClassName = docFlavor.getRepresentationClassName();
        Label_0416: {
            if (!docFlavor.equals(DocFlavor.INPUT_STREAM.GIF) && !docFlavor.equals(DocFlavor.INPUT_STREAM.JPEG) && !docFlavor.equals(DocFlavor.INPUT_STREAM.PNG) && !docFlavor.equals(DocFlavor.BYTE_ARRAY.GIF) && !docFlavor.equals(DocFlavor.BYTE_ARRAY.JPEG)) {
                if (!docFlavor.equals(DocFlavor.BYTE_ARRAY.PNG)) {
                    break Label_0416;
                }
            }
            try {
                this.instream = doc.getStreamForBytes();
                if (this.instream == null) {
                    this.notifyEvent(103);
                    throw new PrintException("No stream for data");
                }
                this.printableJob(new ImagePrinter(this.instream));
                this.service.wakeNotifier();
                return;
            }
            catch (final ClassCastException ex2) {
                this.notifyEvent(103);
                throw new PrintException(ex2);
            }
            catch (final IOException ex3) {
                this.notifyEvent(103);
                throw new PrintException(ex3);
            }
        }
        Label_0491: {
            if (!docFlavor.equals(DocFlavor.URL.GIF) && !docFlavor.equals(DocFlavor.URL.JPEG)) {
                if (!docFlavor.equals(DocFlavor.URL.PNG)) {
                    break Label_0491;
                }
            }
            try {
                this.printableJob(new ImagePrinter((URL)printData));
                this.service.wakeNotifier();
                return;
            }
            catch (final ClassCastException ex4) {
                this.notifyEvent(103);
                throw new PrintException(ex4);
            }
        }
        if (representationClassName.equals("java.awt.print.Pageable")) {
            try {
                this.pageableJob((Pageable)doc.getPrintData());
                this.service.wakeNotifier();
                return;
            }
            catch (final ClassCastException ex5) {
                this.notifyEvent(103);
                throw new PrintException(ex5);
            }
            catch (final IOException ex6) {
                this.notifyEvent(103);
                throw new PrintException(ex6);
            }
        }
        if (representationClassName.equals("java.awt.print.Printable")) {
            try {
                this.printableJob((Printable)doc.getPrintData());
                this.service.wakeNotifier();
                return;
            }
            catch (final ClassCastException ex7) {
                this.notifyEvent(103);
                throw new PrintException(ex7);
            }
            catch (final IOException ex8) {
                this.notifyEvent(103);
                throw new PrintException(ex8);
            }
        }
        if (!representationClassName.equals("[B") && !representationClassName.equals("java.io.InputStream") && !representationClassName.equals("java.net.URL")) {
            this.notifyEvent(103);
            throw new PrintException("unrecognized class: " + representationClassName);
        }
        if (representationClassName.equals("java.net.URL")) {
            final URL url = (URL)printData;
            try {
                this.instream = url.openStream();
            }
            catch (final IOException ex9) {
                this.notifyEvent(103);
                throw new PrintException(ex9.toString());
            }
        }
        else {
            try {
                this.instream = doc.getStreamForBytes();
            }
            catch (final IOException ex10) {
                this.notifyEvent(103);
                throw new PrintException(ex10.toString());
            }
        }
        if (this.instream == null) {
            this.notifyEvent(103);
            throw new PrintException("No stream for data");
        }
        if (this.mDestination != null) {
            try {
                final FileOutputStream fileOutputStream = new FileOutputStream(this.mDestination);
                final byte[] array = new byte[1024];
                int read;
                while ((read = this.instream.read(array, 0, array.length)) >= 0) {
                    fileOutputStream.write(array, 0, read);
                }
                fileOutputStream.flush();
                fileOutputStream.close();
            }
            catch (final FileNotFoundException ex11) {
                this.notifyEvent(103);
                throw new PrintException(ex11.toString());
            }
            catch (final IOException ex12) {
                this.notifyEvent(103);
                throw new PrintException(ex12.toString());
            }
            this.notifyEvent(106);
            this.notifyEvent(102);
            this.service.wakeNotifier();
            return;
        }
        if (!this.startPrintRawData(this.service.getName(), this.jobName)) {
            this.notifyEvent(103);
            throw new PrintException("Print job failed to start.");
        }
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(this.instream);
        try {
            final byte[] array2 = new byte[8192];
            int read2;
            while ((read2 = bufferedInputStream.read(array2, 0, 8192)) >= 0) {
                if (!this.printRawData(array2, read2)) {
                    bufferedInputStream.close();
                    this.notifyEvent(103);
                    throw new PrintException("Problem while spooling data");
                }
            }
            bufferedInputStream.close();
            if (!this.endPrintRawData()) {
                this.notifyEvent(103);
                throw new PrintException("Print job failed to close properly.");
            }
            this.notifyEvent(106);
        }
        catch (final IOException ex13) {
            this.notifyEvent(103);
            throw new PrintException(ex13.toString());
        }
        finally {
            this.notifyEvent(105);
        }
        this.service.wakeNotifier();
    }
    
    public void printableJob(final Printable printable) throws PrintException {
        try {
            synchronized (this) {
                if (this.job != null) {
                    throw new PrintException("already printing");
                }
                this.job = new WPrinterJob();
            }
            final PrintService printService = this.getPrintService();
            this.job.setPrintService(printService);
            if (this.copies == 0) {
                this.copies = ((Copies)printService.getDefaultAttributeValue(Copies.class)).getValue();
            }
            if (this.mediaName == null) {
                final Object defaultAttributeValue = printService.getDefaultAttributeValue(Media.class);
                if (defaultAttributeValue instanceof MediaSizeName) {
                    this.mediaName = (MediaSizeName)defaultAttributeValue;
                    this.mediaSize = MediaSize.getMediaSizeForName(this.mediaName);
                }
            }
            if (this.orient == null) {
                this.orient = (OrientationRequested)printService.getDefaultAttributeValue(OrientationRequested.class);
            }
            this.job.setCopies(this.copies);
            this.job.setJobName(this.jobName);
            final PageFormat pageFormat = new PageFormat();
            if (this.mediaSize != null) {
                final Paper paper = new Paper();
                paper.setSize(this.mediaSize.getX(25400) * 72.0, this.mediaSize.getY(25400) * 72.0);
                paper.setImageableArea(72.0, 72.0, paper.getWidth() - 144.0, paper.getHeight() - 144.0);
                pageFormat.setPaper(paper);
            }
            if (this.orient == OrientationRequested.REVERSE_LANDSCAPE) {
                pageFormat.setOrientation(2);
            }
            else if (this.orient == OrientationRequested.LANDSCAPE) {
                pageFormat.setOrientation(0);
            }
            this.job.setPrintable(printable, pageFormat);
            this.job.print(this.reqAttrSet);
            this.notifyEvent(106);
        }
        catch (final PrinterException ex) {
            this.notifyEvent(103);
            throw new PrintException(ex);
        }
        finally {
            this.printReturned = true;
            this.notifyEvent(105);
        }
    }
    
    public void pageableJob(final Pageable pageable) throws PrintException {
        try {
            synchronized (this) {
                if (this.job != null) {
                    throw new PrintException("already printing");
                }
                this.job = new WPrinterJob();
            }
            final PrintService printService = this.getPrintService();
            this.job.setPrintService(printService);
            if (this.copies == 0) {
                this.copies = ((Copies)printService.getDefaultAttributeValue(Copies.class)).getValue();
            }
            this.job.setCopies(this.copies);
            this.job.setJobName(this.jobName);
            this.job.setPageable(pageable);
            this.job.print(this.reqAttrSet);
            this.notifyEvent(106);
        }
        catch (final PrinterException ex) {
            this.notifyEvent(103);
            throw new PrintException(ex);
        }
        finally {
            this.printReturned = true;
            this.notifyEvent(105);
        }
    }
    
    private synchronized void initializeAttributeSets(final Doc doc, final PrintRequestAttributeSet set) {
        this.reqAttrSet = new HashPrintRequestAttributeSet();
        this.jobAttrSet = new HashPrintJobAttributeSet();
        if (set != null) {
            this.reqAttrSet.addAll(set);
            final Attribute[] array = set.toArray();
            for (int i = 0; i < array.length; ++i) {
                if (array[i] instanceof PrintJobAttribute) {
                    this.jobAttrSet.add(array[i]);
                }
            }
        }
        final DocAttributeSet attributes = doc.getAttributes();
        if (attributes != null) {
            final Attribute[] array2 = attributes.toArray();
            for (int j = 0; j < array2.length; ++j) {
                if (array2[j] instanceof PrintRequestAttribute) {
                    this.reqAttrSet.add(array2[j]);
                }
                if (array2[j] instanceof PrintJobAttribute) {
                    this.jobAttrSet.add(array2[j]);
                }
            }
        }
        String property = "";
        try {
            property = System.getProperty("user.name");
        }
        catch (final SecurityException ex) {}
        if (property == null || property.equals("")) {
            final RequestingUserName requestingUserName = (RequestingUserName)set.get(RequestingUserName.class);
            if (requestingUserName != null) {
                this.jobAttrSet.add(new JobOriginatingUserName(requestingUserName.getValue(), requestingUserName.getLocale()));
            }
            else {
                this.jobAttrSet.add(new JobOriginatingUserName("", null));
            }
        }
        else {
            this.jobAttrSet.add(new JobOriginatingUserName(property, null));
        }
        if (this.jobAttrSet.get(JobName.class) == null) {
            if (attributes != null && attributes.get(DocumentName.class) != null) {
                final DocumentName documentName = (DocumentName)attributes.get(DocumentName.class);
                this.jobAttrSet.add(new JobName(documentName.getValue(), documentName.getLocale()));
            }
            else {
                String s = "JPS Job:" + doc;
                try {
                    if (doc.getPrintData() instanceof URL) {
                        s = ((URL)doc.getPrintData()).toString();
                    }
                }
                catch (final IOException ex2) {}
                this.jobAttrSet.add(new JobName(s, null));
            }
        }
        this.jobAttrSet = AttributeSetUtilities.unmodifiableView(this.jobAttrSet);
    }
    
    private void getAttributeValues(final DocFlavor docFlavor) throws PrintException {
        if (this.reqAttrSet.get(Fidelity.class) == Fidelity.FIDELITY_TRUE) {
            this.fidelity = true;
        }
        else {
            this.fidelity = false;
        }
        final Attribute[] array = this.reqAttrSet.toArray();
        for (int i = 0; i < array.length; ++i) {
            final Attribute attribute = array[i];
            final Class<? extends Attribute> category = attribute.getCategory();
            if (this.fidelity) {
                if (!this.service.isAttributeCategorySupported(category)) {
                    this.notifyEvent(103);
                    throw new PrintJobAttributeException("unsupported category: " + category, category, null);
                }
                if (!this.service.isAttributeValueSupported(attribute, docFlavor, null)) {
                    this.notifyEvent(103);
                    throw new PrintJobAttributeException("unsupported attribute: " + attribute, null, attribute);
                }
            }
            if (category == Destination.class) {
                final URI uri = ((Destination)attribute).getURI();
                if (!"file".equals(uri.getScheme())) {
                    this.notifyEvent(103);
                    throw new PrintException("Not a file: URI");
                }
                try {
                    this.mDestination = new File(uri).getPath();
                }
                catch (final Exception ex) {
                    throw new PrintException(ex);
                }
                final SecurityManager securityManager = System.getSecurityManager();
                if (securityManager != null) {
                    try {
                        securityManager.checkWrite(this.mDestination);
                    }
                    catch (final SecurityException ex2) {
                        this.notifyEvent(103);
                        throw new PrintException(ex2);
                    }
                }
            }
            else if (category == JobName.class) {
                this.jobName = ((JobName)attribute).getValue();
            }
            else if (category == Copies.class) {
                this.copies = ((Copies)attribute).getValue();
            }
            else if (category == Media.class) {
                if (attribute instanceof MediaSizeName) {
                    this.mediaName = (MediaSizeName)attribute;
                    if (!this.service.isAttributeValueSupported(attribute, null, null)) {
                        this.mediaSize = MediaSize.getMediaSizeForName(this.mediaName);
                    }
                }
            }
            else if (category == OrientationRequested.class) {
                this.orient = (OrientationRequested)attribute;
            }
        }
    }
    
    private native boolean startPrintRawData(final String p0, final String p1);
    
    private native boolean printRawData(final byte[] p0, final int p1);
    
    private native boolean endPrintRawData();
    
    @Override
    public void cancel() throws PrintException {
        synchronized (this) {
            if (!this.printing) {
                throw new PrintException("Job is not yet submitted.");
            }
            if (this.job != null && !this.printReturned) {
                this.job.cancel();
                this.notifyEvent(101);
                return;
            }
            throw new PrintException("Job could not be cancelled.");
        }
    }
}
