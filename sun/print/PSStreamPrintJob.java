package sun.print;

import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.Fidelity;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.standard.DocumentName;
import javax.print.attribute.standard.JobName;
import java.util.Locale;
import javax.print.attribute.Attribute;
import javax.print.attribute.standard.JobOriginatingUserName;
import javax.print.attribute.standard.RequestingUserName;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import java.awt.print.PrinterException;
import java.awt.print.Paper;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.net.URL;
import java.awt.print.Printable;
import javax.print.DocFlavor;
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
import java.io.Reader;
import java.io.InputStream;
import javax.print.Doc;
import java.awt.print.PrinterJob;
import javax.print.attribute.PrintJobAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import java.util.Vector;
import javax.print.CancelablePrintJob;

public class PSStreamPrintJob implements CancelablePrintJob
{
    private transient Vector jobListeners;
    private transient Vector attrListeners;
    private transient Vector listenedAttributeSets;
    private PSStreamPrintService service;
    private boolean fidelity;
    private boolean printing;
    private boolean printReturned;
    private PrintRequestAttributeSet reqAttrSet;
    private PrintJobAttributeSet jobAttrSet;
    private PrinterJob job;
    private Doc doc;
    private InputStream instream;
    private Reader reader;
    private String jobName;
    private int copies;
    private MediaSize mediaSize;
    private OrientationRequested orient;
    
    PSStreamPrintJob(final PSStreamPrintService service) {
        this.printing = false;
        this.printReturned = false;
        this.reqAttrSet = null;
        this.jobAttrSet = null;
        this.instream = null;
        this.reader = null;
        this.jobName = "Java Printing";
        this.copies = 1;
        this.mediaSize = MediaSize.NA.LETTER;
        this.orient = OrientationRequested.PORTRAIT;
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
        synchronized (this) {
            if (this.jobListeners != null) {
                final PrintJobEvent printJobEvent = new PrintJobEvent(this, n);
                for (int i = 0; i < this.jobListeners.size(); ++i) {
                    final PrintJobListener printJobListener = this.jobListeners.elementAt(i);
                    switch (n) {
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
                        case 102: {
                            printJobListener.printJobCompleted(printJobEvent);
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
        if (docFlavor == null || !this.service.isDocFlavorSupported(docFlavor)) {
            this.notifyEvent(103);
            throw new PrintJobFlavorException("invalid flavor", docFlavor);
        }
        this.initializeAttributeSets(doc, set);
        this.getAttributeValues(docFlavor);
        final String representationClassName = docFlavor.getRepresentationClassName();
        Label_0275: {
            if (!docFlavor.equals(DocFlavor.INPUT_STREAM.GIF) && !docFlavor.equals(DocFlavor.INPUT_STREAM.JPEG) && !docFlavor.equals(DocFlavor.INPUT_STREAM.PNG) && !docFlavor.equals(DocFlavor.BYTE_ARRAY.GIF) && !docFlavor.equals(DocFlavor.BYTE_ARRAY.JPEG)) {
                if (!docFlavor.equals(DocFlavor.BYTE_ARRAY.PNG)) {
                    break Label_0275;
                }
            }
            try {
                this.instream = doc.getStreamForBytes();
                this.printableJob(new ImagePrinter(this.instream), this.reqAttrSet);
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
        Label_0344: {
            if (!docFlavor.equals(DocFlavor.URL.GIF) && !docFlavor.equals(DocFlavor.URL.JPEG)) {
                if (!docFlavor.equals(DocFlavor.URL.PNG)) {
                    break Label_0344;
                }
            }
            try {
                this.printableJob(new ImagePrinter((URL)printData), this.reqAttrSet);
                return;
            }
            catch (final ClassCastException ex4) {
                this.notifyEvent(103);
                throw new PrintException(ex4);
            }
        }
        if (representationClassName.equals("java.awt.print.Pageable")) {
            try {
                this.pageableJob((Pageable)doc.getPrintData(), this.reqAttrSet);
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
                this.printableJob((Printable)doc.getPrintData(), this.reqAttrSet);
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
        this.notifyEvent(103);
        throw new PrintException("unrecognized class: " + representationClassName);
    }
    
    public void printableJob(final Printable printable, final PrintRequestAttributeSet set) throws PrintException {
        try {
            synchronized (this) {
                if (this.job != null) {
                    throw new PrintException("already printing");
                }
                this.job = new PSPrinterJob();
            }
            this.job.setPrintService(this.getPrintService());
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
            this.job.print(set);
            this.notifyEvent(102);
        }
        catch (final PrinterException ex) {
            this.notifyEvent(103);
            throw new PrintException(ex);
        }
        finally {
            this.printReturned = true;
        }
    }
    
    public void pageableJob(final Pageable pageable, final PrintRequestAttributeSet set) throws PrintException {
        try {
            synchronized (this) {
                if (this.job != null) {
                    throw new PrintException("already printing");
                }
                this.job = new PSPrinterJob();
            }
            this.job.setPrintService(this.getPrintService());
            this.job.setPageable(pageable);
            this.job.print(set);
            this.notifyEvent(102);
        }
        catch (final PrinterException ex) {
            this.notifyEvent(103);
            throw new PrintException(ex);
        }
        finally {
            this.printReturned = true;
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
            if (category == JobName.class) {
                this.jobName = ((JobName)attribute).getValue();
            }
            else if (category == Copies.class) {
                this.copies = ((Copies)attribute).getValue();
            }
            else if (category == Media.class) {
                if (attribute instanceof MediaSizeName && this.service.isAttributeValueSupported(attribute, null, null)) {
                    this.mediaSize = MediaSize.getMediaSizeForName((MediaSizeName)attribute);
                }
            }
            else if (category == OrientationRequested.class) {
                this.orient = (OrientationRequested)attribute;
            }
        }
    }
    
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
