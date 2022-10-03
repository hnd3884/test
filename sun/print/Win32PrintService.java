package sun.print;

import javax.print.attribute.PrintRequestAttributeSet;
import java.awt.Window;
import java.awt.print.PrinterJob;
import sun.awt.windows.WPrinterJob;
import javax.print.ServiceUIFactory;
import javax.print.attribute.HashAttributeSet;
import javax.print.attribute.standard.CopiesSupported;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.standard.Fidelity;
import javax.print.attribute.standard.SheetCollate;
import javax.print.attribute.standard.RequestingUserName;
import java.net.URISyntaxException;
import java.net.URI;
import java.io.File;
import javax.print.attribute.standard.Destination;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.Chromaticity;
import javax.print.attribute.standard.PrintQuality;
import javax.print.attribute.standard.Sides;
import javax.print.attribute.standard.ColorSupported;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.event.PrintServiceAttributeListener;
import javax.print.attribute.AttributeSetUtilities;
import javax.print.attribute.Attribute;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.DocPrintJob;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.QueuedJobCount;
import javax.print.attribute.standard.Severity;
import javax.print.attribute.standard.PrinterStateReason;
import javax.print.attribute.standard.PrinterStateReasons;
import javax.print.attribute.standard.PrinterState;
import javax.print.attribute.standard.PrinterIsAcceptingJobs;
import java.util.Iterator;
import java.util.Locale;
import java.util.ArrayList;
import java.util.HashMap;
import javax.print.attribute.standard.PrinterResolution;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.PrinterName;
import javax.print.attribute.standard.MediaTray;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.DocFlavor;
import javax.print.attribute.standard.MediaSize;
import javax.print.PrintService;

public class Win32PrintService implements PrintService, AttributeUpdater, SunPrinterJobService
{
    public static MediaSize[] predefMedia;
    private static final DocFlavor[] supportedFlavors;
    private static final Class[] serviceAttrCats;
    private static Class[] otherAttrCats;
    public static final MediaSizeName[] dmPaperToPrintService;
    private static final MediaTray[] dmPaperBinToPrintService;
    private static int DM_PAPERSIZE;
    private static int DM_PRINTQUALITY;
    private static int DM_YRESOLUTION;
    private static final int DMRES_MEDIUM = -3;
    private static final int DMRES_HIGH = -4;
    private static final int DMORIENT_LANDSCAPE = 2;
    private static final int DMDUP_VERTICAL = 2;
    private static final int DMDUP_HORIZONTAL = 3;
    private static final int DMCOLLATE_TRUE = 1;
    private static final int DMCOLOR_MONOCHROME = 1;
    private static final int DMCOLOR_COLOR = 2;
    private static final int DMPAPER_A2 = 66;
    private static final int DMPAPER_A6 = 70;
    private static final int DMPAPER_B6_JIS = 88;
    private static final int DEVCAP_COLOR = 1;
    private static final int DEVCAP_DUPLEX = 2;
    private static final int DEVCAP_COLLATE = 4;
    private static final int DEVCAP_QUALITY = 8;
    private static final int DEVCAP_POSTSCRIPT = 16;
    private String printer;
    private PrinterName name;
    private String port;
    private transient PrintServiceAttributeSet lastSet;
    private transient ServiceNotifier notifier;
    private MediaSizeName[] mediaSizeNames;
    private MediaPrintableArea[] mediaPrintables;
    private MediaTray[] mediaTrays;
    private PrinterResolution[] printRes;
    private HashMap mpaMap;
    private int nCopies;
    private int prnCaps;
    private int[] defaultSettings;
    private boolean gotTrays;
    private boolean gotCopies;
    private boolean mediaInitialized;
    private boolean mpaListInitialized;
    private ArrayList idList;
    private MediaSize[] mediaSizes;
    private boolean isInvalid;
    private Win32DocumentPropertiesUI docPropertiesUI;
    private Win32ServiceUIFactory uiFactory;
    
    Win32PrintService(final String printer) {
        this.notifier = null;
        this.docPropertiesUI = null;
        this.uiFactory = null;
        if (printer == null) {
            throw new IllegalArgumentException("null printer name");
        }
        this.printer = printer;
        this.mediaInitialized = false;
        this.gotTrays = false;
        this.gotCopies = false;
        this.isInvalid = false;
        this.printRes = null;
        this.prnCaps = 0;
        this.defaultSettings = null;
        this.port = null;
    }
    
    public void invalidateService() {
        this.isInvalid = true;
    }
    
    @Override
    public String getName() {
        return this.printer;
    }
    
    private PrinterName getPrinterName() {
        if (this.name == null) {
            this.name = new PrinterName(this.printer, null);
        }
        return this.name;
    }
    
    public int findPaperID(final MediaSizeName mediaSizeName) {
        if (mediaSizeName instanceof Win32MediaSize) {
            return ((Win32MediaSize)mediaSizeName).getDMPaper();
        }
        for (int i = 0; i < Win32PrintService.dmPaperToPrintService.length; ++i) {
            if (Win32PrintService.dmPaperToPrintService[i].equals(mediaSizeName)) {
                return i + 1;
            }
        }
        if (mediaSizeName.equals(MediaSizeName.ISO_A2)) {
            return 66;
        }
        if (mediaSizeName.equals(MediaSizeName.ISO_A6)) {
            return 70;
        }
        if (mediaSizeName.equals(MediaSizeName.JIS_B6)) {
            return 88;
        }
        this.initMedia();
        if (this.idList != null && this.mediaSizes != null && this.idList.size() == this.mediaSizes.length) {
            for (int j = 0; j < this.idList.size(); ++j) {
                if (this.mediaSizes[j].getMediaSizeName() == mediaSizeName) {
                    return (int)this.idList.get(j);
                }
            }
        }
        return 0;
    }
    
    public int findTrayID(final MediaTray mediaTray) {
        this.getMediaTrays();
        if (mediaTray instanceof Win32MediaTray) {
            return ((Win32MediaTray)mediaTray).getDMBinID();
        }
        for (int i = 0; i < Win32PrintService.dmPaperBinToPrintService.length; ++i) {
            if (mediaTray.equals(Win32PrintService.dmPaperBinToPrintService[i])) {
                return i + 1;
            }
        }
        return 0;
    }
    
    public MediaTray findMediaTray(final int n) {
        if (n >= 1 && n <= Win32PrintService.dmPaperBinToPrintService.length) {
            return Win32PrintService.dmPaperBinToPrintService[n - 1];
        }
        final MediaTray[] mediaTrays = this.getMediaTrays();
        if (mediaTrays != null) {
            for (int i = 0; i < mediaTrays.length; ++i) {
                if (mediaTrays[i] instanceof Win32MediaTray) {
                    final Win32MediaTray win32MediaTray = (Win32MediaTray)mediaTrays[i];
                    if (win32MediaTray.winID == n) {
                        return win32MediaTray;
                    }
                }
            }
        }
        return Win32MediaTray.AUTO;
    }
    
    public MediaSizeName findWin32Media(final int n) {
        if (n >= 1 && n <= Win32PrintService.dmPaperToPrintService.length) {
            return Win32PrintService.dmPaperToPrintService[n - 1];
        }
        switch (n) {
            case 66: {
                return MediaSizeName.ISO_A2;
            }
            case 70: {
                return MediaSizeName.ISO_A6;
            }
            case 88: {
                return MediaSizeName.JIS_B6;
            }
            default: {
                return null;
            }
        }
    }
    
    private boolean addToUniqueList(final ArrayList list, final MediaSizeName mediaSizeName) {
        for (int i = 0; i < list.size(); ++i) {
            if (list.get(i) == mediaSizeName) {
                return false;
            }
        }
        list.add(mediaSizeName);
        return true;
    }
    
    private synchronized void initMedia() {
        if (this.mediaInitialized) {
            return;
        }
        this.mediaInitialized = true;
        final int[] allMediaIDs = this.getAllMediaIDs(this.printer, this.getPort());
        if (allMediaIDs == null) {
            return;
        }
        final ArrayList list = new ArrayList();
        final ArrayList list2 = new ArrayList();
        final ArrayList list3 = new ArrayList();
        this.idList = new ArrayList();
        for (int i = 0; i < allMediaIDs.length; ++i) {
            this.idList.add(allMediaIDs[i]);
        }
        final ArrayList list4 = new ArrayList();
        this.mediaSizes = this.getMediaSizes(this.idList, allMediaIDs, list4);
        for (int j = 0; j < this.idList.size(); ++j) {
            MediaSizeName mediaSizeName = this.findWin32Media(this.idList.get(j));
            if (mediaSizeName != null && this.idList.size() == this.mediaSizes.length) {
                final MediaSize mediaSizeForName = MediaSize.getMediaSizeForName(mediaSizeName);
                final MediaSize mediaSize = this.mediaSizes[j];
                final int n = 2540;
                if (Math.abs(mediaSizeForName.getX(1) - mediaSize.getX(1)) > n || Math.abs(mediaSizeForName.getY(1) - mediaSize.getY(1)) > n) {
                    mediaSizeName = null;
                }
            }
            final boolean b = mediaSizeName != null;
            if (mediaSizeName == null && this.idList.size() == this.mediaSizes.length) {
                mediaSizeName = this.mediaSizes[j].getMediaSizeName();
            }
            boolean addToUniqueList = false;
            if (mediaSizeName != null) {
                addToUniqueList = this.addToUniqueList(list, mediaSizeName);
            }
            if ((!b || !addToUniqueList) && this.idList.size() == list4.size()) {
                Win32MediaSize mediaName = Win32MediaSize.findMediaName(list4.get(j));
                if (mediaName == null && this.idList.size() == this.mediaSizes.length) {
                    mediaName = new Win32MediaSize(list4.get(j), this.idList.get(j));
                    this.mediaSizes[j] = new MediaSize(this.mediaSizes[j].getX(1000), this.mediaSizes[j].getY(1000), 1000, mediaName);
                }
                if (mediaName != null && mediaName != mediaSizeName) {
                    if (!addToUniqueList) {
                        this.addToUniqueList(list, mediaName);
                    }
                    else {
                        list2.add(mediaName);
                    }
                }
            }
        }
        final Iterator iterator = list2.iterator();
        while (iterator.hasNext()) {
            this.addToUniqueList(list, (MediaSizeName)iterator.next());
        }
        list.toArray(this.mediaSizeNames = new MediaSizeName[list.size()]);
    }
    
    private synchronized MediaPrintableArea[] getMediaPrintables(final MediaSizeName mediaSizeName) {
        if (mediaSizeName == null) {
            if (this.mpaListInitialized) {
                return this.mediaPrintables;
            }
        }
        else if (this.mpaMap != null && this.mpaMap.get(mediaSizeName) != null) {
            return new MediaPrintableArea[] { this.mpaMap.get(mediaSizeName) };
        }
        this.initMedia();
        if (this.mediaSizeNames == null || this.mediaSizeNames.length == 0) {
            return null;
        }
        MediaSizeName[] mediaSizeNames;
        if (mediaSizeName != null) {
            mediaSizeNames = new MediaSizeName[] { mediaSizeName };
        }
        else {
            mediaSizeNames = this.mediaSizeNames;
        }
        if (this.mpaMap == null) {
            this.mpaMap = new HashMap();
        }
        for (int i = 0; i < mediaSizeNames.length; ++i) {
            final MediaSizeName mediaSizeName2 = mediaSizeNames[i];
            if (this.mpaMap.get(mediaSizeName2) == null) {
                if (mediaSizeName2 != null) {
                    final int paperID = this.findPaperID(mediaSizeName2);
                    final float[] array = (float[])((paperID != 0) ? this.getMediaPrintableArea(this.printer, paperID) : null);
                    if (array != null) {
                        try {
                            this.mpaMap.put(mediaSizeName2, new MediaPrintableArea(array[0], array[1], array[2], array[3], 25400));
                        }
                        catch (final IllegalArgumentException ex) {}
                    }
                    else {
                        final MediaSize mediaSizeForName = MediaSize.getMediaSizeForName(mediaSizeName2);
                        if (mediaSizeForName != null) {
                            try {
                                this.mpaMap.put(mediaSizeName2, new MediaPrintableArea(0.0f, 0.0f, mediaSizeForName.getX(25400), mediaSizeForName.getY(25400), 25400));
                            }
                            catch (final IllegalArgumentException ex2) {}
                        }
                    }
                }
            }
        }
        if (this.mpaMap.size() == 0) {
            return null;
        }
        if (mediaSizeName == null) {
            this.mediaPrintables = (MediaPrintableArea[])this.mpaMap.values().toArray(new MediaPrintableArea[0]);
            this.mpaListInitialized = true;
            return this.mediaPrintables;
        }
        if (this.mpaMap.get(mediaSizeName) == null) {
            return null;
        }
        return new MediaPrintableArea[] { this.mpaMap.get(mediaSizeName) };
    }
    
    private synchronized MediaTray[] getMediaTrays() {
        if (this.gotTrays && this.mediaTrays != null) {
            return this.mediaTrays;
        }
        final String port = this.getPort();
        final int[] allMediaTrays = this.getAllMediaTrays(this.printer, port);
        final String[] allMediaTrayNames = this.getAllMediaTrayNames(this.printer, port);
        if (allMediaTrays == null || allMediaTrayNames == null) {
            return null;
        }
        int n = 0;
        for (int i = 0; i < allMediaTrays.length; ++i) {
            if (allMediaTrays[i] > 0) {
                ++n;
            }
        }
        final MediaTray[] mediaTrays = new MediaTray[n];
        int j = 0;
        int n2 = 0;
        while (j < Math.min(allMediaTrays.length, allMediaTrayNames.length)) {
            final int n3 = allMediaTrays[j];
            if (n3 > 0) {
                if (n3 > Win32PrintService.dmPaperBinToPrintService.length || Win32PrintService.dmPaperBinToPrintService[n3 - 1] == null) {
                    mediaTrays[n2++] = new Win32MediaTray(n3, allMediaTrayNames[j]);
                }
                else {
                    mediaTrays[n2++] = Win32PrintService.dmPaperBinToPrintService[n3 - 1];
                }
            }
            ++j;
        }
        this.mediaTrays = mediaTrays;
        this.gotTrays = true;
        return this.mediaTrays;
    }
    
    private boolean isSameSize(final float n, final float n2, final float n3, final float n4) {
        final float n5 = n - n3;
        final float n6 = n2 - n4;
        final float n7 = n - n4;
        final float n8 = n2 - n3;
        return (Math.abs(n5) <= 1.0f && Math.abs(n6) <= 1.0f) || (Math.abs(n7) <= 1.0f && Math.abs(n8) <= 1.0f);
    }
    
    public MediaSizeName findMatchingMediaSizeNameMM(final float n, final float n2) {
        if (Win32PrintService.predefMedia != null) {
            for (int i = 0; i < Win32PrintService.predefMedia.length; ++i) {
                if (Win32PrintService.predefMedia[i] != null) {
                    if (this.isSameSize(Win32PrintService.predefMedia[i].getX(1000), Win32PrintService.predefMedia[i].getY(1000), n, n2)) {
                        return Win32PrintService.predefMedia[i].getMediaSizeName();
                    }
                }
            }
        }
        return null;
    }
    
    private MediaSize[] getMediaSizes(final ArrayList list, final int[] array, ArrayList<String> list2) {
        if (list2 == null) {
            list2 = new ArrayList<String>();
        }
        final String port = this.getPort();
        final int[] allMediaSizes = this.getAllMediaSizes(this.printer, port);
        final String[] allMediaNames = this.getAllMediaNames(this.printer, port);
        MediaSize mediaSizeForName = null;
        if (allMediaSizes == null || allMediaNames == null) {
            return null;
        }
        final int n = allMediaSizes.length / 2;
        final ArrayList<MediaSize> list3 = new ArrayList<MediaSize>();
        for (int i = 0; i < n; ++i, mediaSizeForName = null) {
            final float n2 = allMediaSizes[i * 2] / 10.0f;
            final float n3 = allMediaSizes[i * 2 + 1] / 10.0f;
            if (n2 <= 0.0f || n3 <= 0.0f) {
                if (n == array.length) {
                    list.remove(list.indexOf(array[i]));
                }
            }
            else {
                final MediaSizeName matchingMediaSizeNameMM = this.findMatchingMediaSizeNameMM(n2, n3);
                if (matchingMediaSizeNameMM != null) {
                    mediaSizeForName = MediaSize.getMediaSizeForName(matchingMediaSizeNameMM);
                }
                if (mediaSizeForName != null) {
                    list3.add(mediaSizeForName);
                    list2.add(allMediaNames[i]);
                }
                else {
                    Win32MediaSize mediaName = Win32MediaSize.findMediaName(allMediaNames[i]);
                    if (mediaName == null) {
                        mediaName = new Win32MediaSize(allMediaNames[i], array[i]);
                    }
                    try {
                        list3.add(new MediaSize(n2, n3, 1000, mediaName));
                        list2.add(allMediaNames[i]);
                    }
                    catch (final IllegalArgumentException ex) {
                        if (n == array.length) {
                            list.remove(list.indexOf(array[i]));
                        }
                    }
                }
            }
        }
        final MediaSize[] array2 = new MediaSize[list3.size()];
        list3.toArray(array2);
        return array2;
    }
    
    private PrinterIsAcceptingJobs getPrinterIsAcceptingJobs() {
        if (this.getJobStatus(this.printer, 2) != 1) {
            return PrinterIsAcceptingJobs.NOT_ACCEPTING_JOBS;
        }
        return PrinterIsAcceptingJobs.ACCEPTING_JOBS;
    }
    
    private PrinterState getPrinterState() {
        if (this.isInvalid) {
            return PrinterState.STOPPED;
        }
        return null;
    }
    
    private PrinterStateReasons getPrinterStateReasons() {
        if (this.isInvalid) {
            final PrinterStateReasons printerStateReasons = new PrinterStateReasons();
            printerStateReasons.put(PrinterStateReason.SHUTDOWN, Severity.ERROR);
            return printerStateReasons;
        }
        return null;
    }
    
    private QueuedJobCount getQueuedJobCount() {
        final int jobStatus = this.getJobStatus(this.printer, 1);
        if (jobStatus != -1) {
            return new QueuedJobCount(jobStatus);
        }
        return new QueuedJobCount(0);
    }
    
    private boolean isSupportedCopies(final Copies copies) {
        synchronized (this) {
            if (!this.gotCopies) {
                this.nCopies = this.getCopiesSupported(this.printer, this.getPort());
                this.gotCopies = true;
            }
        }
        final int value = copies.getValue();
        return value > 0 && value <= this.nCopies;
    }
    
    private boolean isSupportedMedia(final MediaSizeName mediaSizeName) {
        this.initMedia();
        if (this.mediaSizeNames != null) {
            for (int i = 0; i < this.mediaSizeNames.length; ++i) {
                if (mediaSizeName.equals(this.mediaSizeNames[i])) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean isSupportedMediaPrintableArea(final MediaPrintableArea mediaPrintableArea) {
        this.getMediaPrintables(null);
        if (this.mediaPrintables != null) {
            for (int i = 0; i < this.mediaPrintables.length; ++i) {
                if (mediaPrintableArea.equals(this.mediaPrintables[i])) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean isSupportedMediaTray(final MediaTray mediaTray) {
        final MediaTray[] mediaTrays = this.getMediaTrays();
        if (mediaTrays != null) {
            for (int i = 0; i < mediaTrays.length; ++i) {
                if (mediaTray.equals(mediaTrays[i])) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private int getPrinterCapabilities() {
        if (this.prnCaps == 0) {
            this.prnCaps = this.getCapabilities(this.printer, this.getPort());
        }
        return this.prnCaps;
    }
    
    private String getPort() {
        if (this.port == null) {
            this.port = this.getPrinterPort(this.printer);
        }
        return this.port;
    }
    
    private int[] getDefaultPrinterSettings() {
        if (this.defaultSettings == null) {
            this.defaultSettings = this.getDefaultSettings(this.printer, this.getPort());
        }
        return this.defaultSettings;
    }
    
    private PrinterResolution[] getPrintResolutions() {
        if (this.printRes == null) {
            final int[] allResolutions = this.getAllResolutions(this.printer, this.getPort());
            if (allResolutions == null) {
                this.printRes = new PrinterResolution[0];
            }
            else {
                final int n = allResolutions.length / 2;
                final ArrayList list = new ArrayList();
                for (int i = 0; i < n; ++i) {
                    try {
                        list.add(new PrinterResolution(allResolutions[i * 2], allResolutions[i * 2 + 1], 100));
                    }
                    catch (final IllegalArgumentException ex) {}
                }
                this.printRes = list.toArray(new PrinterResolution[list.size()]);
            }
        }
        return this.printRes;
    }
    
    private boolean isSupportedResolution(final PrinterResolution printerResolution) {
        final PrinterResolution[] printResolutions = this.getPrintResolutions();
        if (printResolutions != null) {
            for (int i = 0; i < printResolutions.length; ++i) {
                if (printerResolution.equals(printResolutions[i])) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public DocPrintJob createPrintJob() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPrintJobAccess();
        }
        return new Win32PrintJob(this);
    }
    
    private PrintServiceAttributeSet getDynamicAttributes() {
        final HashPrintServiceAttributeSet set = new HashPrintServiceAttributeSet();
        set.add(this.getPrinterIsAcceptingJobs());
        set.add(this.getQueuedJobCount());
        return set;
    }
    
    @Override
    public PrintServiceAttributeSet getUpdatedAttributes() {
        final PrintServiceAttributeSet dynamicAttributes = this.getDynamicAttributes();
        if (this.lastSet == null) {
            this.lastSet = dynamicAttributes;
            return AttributeSetUtilities.unmodifiableView(dynamicAttributes);
        }
        final HashPrintServiceAttributeSet set = new HashPrintServiceAttributeSet();
        final Attribute[] array = dynamicAttributes.toArray();
        for (int i = 0; i < array.length; ++i) {
            final Attribute attribute = array[i];
            if (!this.lastSet.containsValue(attribute)) {
                set.add(attribute);
            }
        }
        this.lastSet = dynamicAttributes;
        return AttributeSetUtilities.unmodifiableView(set);
    }
    
    public void wakeNotifier() {
        synchronized (this) {
            if (this.notifier != null) {
                this.notifier.wake();
            }
        }
    }
    
    @Override
    public void addPrintServiceAttributeListener(final PrintServiceAttributeListener printServiceAttributeListener) {
        synchronized (this) {
            if (printServiceAttributeListener == null) {
                return;
            }
            if (this.notifier == null) {
                this.notifier = new ServiceNotifier(this);
            }
            this.notifier.addListener(printServiceAttributeListener);
        }
    }
    
    @Override
    public void removePrintServiceAttributeListener(final PrintServiceAttributeListener printServiceAttributeListener) {
        synchronized (this) {
            if (printServiceAttributeListener == null || this.notifier == null) {
                return;
            }
            this.notifier.removeListener(printServiceAttributeListener);
            if (this.notifier.isEmpty()) {
                this.notifier.stopNotifier();
                this.notifier = null;
            }
        }
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
            if ((this.getPrinterCapabilities() & 0x1) != 0x0) {
                return (T)ColorSupported.SUPPORTED;
            }
            return (T)ColorSupported.NOT_SUPPORTED;
        }
        else {
            if (clazz == PrinterName.class) {
                return (T)this.getPrinterName();
            }
            if (clazz == PrinterState.class) {
                return (T)this.getPrinterState();
            }
            if (clazz == PrinterStateReasons.class) {
                return (T)this.getPrinterStateReasons();
            }
            if (clazz == QueuedJobCount.class) {
                return (T)this.getQueuedJobCount();
            }
            if (clazz == PrinterIsAcceptingJobs.class) {
                return (T)this.getPrinterIsAcceptingJobs();
            }
            return null;
        }
    }
    
    @Override
    public PrintServiceAttributeSet getAttributes() {
        final HashPrintServiceAttributeSet set = new HashPrintServiceAttributeSet();
        set.add(this.getPrinterName());
        set.add(this.getPrinterIsAcceptingJobs());
        final PrinterState printerState = this.getPrinterState();
        if (printerState != null) {
            set.add(printerState);
        }
        final PrinterStateReasons printerStateReasons = this.getPrinterStateReasons();
        if (printerStateReasons != null) {
            set.add(printerStateReasons);
        }
        set.add(this.getQueuedJobCount());
        if ((this.getPrinterCapabilities() & 0x1) != 0x0) {
            set.add(ColorSupported.SUPPORTED);
        }
        else {
            set.add(ColorSupported.NOT_SUPPORTED);
        }
        return AttributeSetUtilities.unmodifiableView(set);
    }
    
    @Override
    public DocFlavor[] getSupportedDocFlavors() {
        final int length = Win32PrintService.supportedFlavors.length;
        DocFlavor[] array;
        if ((this.getPrinterCapabilities() & 0x10) != 0x0) {
            array = new DocFlavor[length + 3];
            System.arraycopy(Win32PrintService.supportedFlavors, 0, array, 0, length);
            array[length] = DocFlavor.BYTE_ARRAY.POSTSCRIPT;
            array[length + 1] = DocFlavor.INPUT_STREAM.POSTSCRIPT;
            array[length + 2] = DocFlavor.URL.POSTSCRIPT;
        }
        else {
            array = new DocFlavor[length];
            System.arraycopy(Win32PrintService.supportedFlavors, 0, array, 0, length);
        }
        return array;
    }
    
    @Override
    public boolean isDocFlavorSupported(final DocFlavor docFlavor) {
        DocFlavor[] array;
        if (this.isPostScriptFlavor(docFlavor)) {
            array = this.getSupportedDocFlavors();
        }
        else {
            array = Win32PrintService.supportedFlavors;
        }
        for (int i = 0; i < array.length; ++i) {
            if (docFlavor.equals(array[i])) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Class<?>[] getSupportedAttributeCategories() {
        final ArrayList list = new ArrayList(Win32PrintService.otherAttrCats.length + 3);
        for (int i = 0; i < Win32PrintService.otherAttrCats.length; ++i) {
            list.add(Win32PrintService.otherAttrCats[i]);
        }
        final int printerCapabilities = this.getPrinterCapabilities();
        if ((printerCapabilities & 0x2) != 0x0) {
            list.add(Sides.class);
        }
        if ((printerCapabilities & 0x8) != 0x0) {
            final int[] defaultPrinterSettings = this.getDefaultPrinterSettings();
            if (defaultPrinterSettings[3] >= -4 && defaultPrinterSettings[3] < 0) {
                list.add(PrintQuality.class);
            }
        }
        final PrinterResolution[] printResolutions = this.getPrintResolutions();
        if (printResolutions != null && printResolutions.length > 0) {
            list.add(PrinterResolution.class);
        }
        return list.toArray(new Class[list.size()]);
    }
    
    @Override
    public boolean isAttributeCategorySupported(final Class<? extends Attribute> clazz) {
        if (clazz == null) {
            throw new NullPointerException("null category");
        }
        if (!Attribute.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException(clazz + " is not an Attribute");
        }
        final Class<?>[] supportedAttributeCategories = this.getSupportedAttributeCategories();
        for (int i = 0; i < supportedAttributeCategories.length; ++i) {
            if (clazz.equals(supportedAttributeCategories[i])) {
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
        final int[] defaultPrinterSettings = this.getDefaultPrinterSettings();
        int paperID = defaultPrinterSettings[0];
        final int n = defaultPrinterSettings[2];
        final int n2 = defaultPrinterSettings[3];
        final int n3 = defaultPrinterSettings[4];
        final int n4 = defaultPrinterSettings[5];
        final int n5 = defaultPrinterSettings[6];
        final int n6 = defaultPrinterSettings[7];
        final int n7 = defaultPrinterSettings[8];
        if (clazz == Copies.class) {
            if (n3 > 0) {
                return new Copies(n3);
            }
            return new Copies(1);
        }
        else if (clazz == Chromaticity.class) {
            if (n7 == 2) {
                return Chromaticity.COLOR;
            }
            return Chromaticity.MONOCHROME;
        }
        else {
            if (clazz == JobName.class) {
                return new JobName("Java Printing", null);
            }
            if (clazz == OrientationRequested.class) {
                if (n4 == 2) {
                    return OrientationRequested.LANDSCAPE;
                }
                return OrientationRequested.PORTRAIT;
            }
            else {
                if (clazz == PageRanges.class) {
                    return new PageRanges(1, Integer.MAX_VALUE);
                }
                if (clazz == Media.class) {
                    MediaSizeName win32Media = this.findWin32Media(paperID);
                    if (win32Media != null) {
                        if (!this.isSupportedMedia(win32Media) && this.mediaSizeNames != null) {
                            win32Media = this.mediaSizeNames[0];
                            this.findPaperID(win32Media);
                        }
                        return win32Media;
                    }
                    this.initMedia();
                    if (this.mediaSizeNames != null && this.mediaSizeNames.length > 0) {
                        if (this.idList != null && this.mediaSizes != null && this.idList.size() == this.mediaSizes.length) {
                            final int index = this.idList.indexOf(paperID);
                            if (index >= 0 && index < this.mediaSizes.length) {
                                return this.mediaSizes[index].getMediaSizeName();
                            }
                        }
                        return this.mediaSizeNames[0];
                    }
                }
                else if (clazz == MediaPrintableArea.class) {
                    final MediaSizeName win32Media2 = this.findWin32Media(paperID);
                    if (win32Media2 != null && !this.isSupportedMedia(win32Media2) && this.mediaSizeNames != null) {
                        paperID = this.findPaperID(this.mediaSizeNames[0]);
                    }
                    final float[] mediaPrintableArea = this.getMediaPrintableArea(this.printer, paperID);
                    if (mediaPrintableArea != null) {
                        Object o = null;
                        try {
                            o = new MediaPrintableArea(mediaPrintableArea[0], mediaPrintableArea[1], mediaPrintableArea[2], mediaPrintableArea[3], 25400);
                        }
                        catch (final IllegalArgumentException ex) {}
                        return o;
                    }
                    return null;
                }
                else {
                    if (clazz == SunAlternateMedia.class) {
                        return null;
                    }
                    if (clazz == Destination.class) {
                        try {
                            return new Destination(new File("out.prn").toURI());
                        }
                        catch (final SecurityException ex2) {
                            try {
                                return new Destination(new URI("file:out.prn"));
                            }
                            catch (final URISyntaxException ex3) {
                                return null;
                            }
                        }
                    }
                    if (clazz == Sides.class) {
                        switch (n5) {
                            case 2: {
                                return Sides.TWO_SIDED_LONG_EDGE;
                            }
                            case 3: {
                                return Sides.TWO_SIDED_SHORT_EDGE;
                            }
                            default: {
                                return Sides.ONE_SIDED;
                            }
                        }
                    }
                    else if (clazz == PrinterResolution.class) {
                        final int n8 = n;
                        final int n9 = n2;
                        if (n9 >= 0 && n8 >= 0) {
                            return new PrinterResolution(n9, n8, 100);
                        }
                        final int n10 = (n8 > n9) ? n8 : n9;
                        if (n10 > 0) {
                            return new PrinterResolution(n10, n10, 100);
                        }
                    }
                    else if (clazz == ColorSupported.class) {
                        if ((this.getPrinterCapabilities() & 0x1) != 0x0) {
                            return ColorSupported.SUPPORTED;
                        }
                        return ColorSupported.NOT_SUPPORTED;
                    }
                    else if (clazz == PrintQuality.class) {
                        if (n2 < 0 && n2 >= -4) {
                            switch (n2) {
                                case -4: {
                                    return PrintQuality.HIGH;
                                }
                                case -3: {
                                    return PrintQuality.NORMAL;
                                }
                                default: {
                                    return PrintQuality.DRAFT;
                                }
                            }
                        }
                    }
                    else {
                        if (clazz == RequestingUserName.class) {
                            String property = "";
                            try {
                                property = System.getProperty("user.name", "");
                            }
                            catch (final SecurityException ex4) {}
                            return new RequestingUserName(property, null);
                        }
                        if (clazz == SheetCollate.class) {
                            if (n6 == 1) {
                                return SheetCollate.COLLATED;
                            }
                            return SheetCollate.UNCOLLATED;
                        }
                        else if (clazz == Fidelity.class) {
                            return Fidelity.FIDELITY_FALSE;
                        }
                    }
                }
                return null;
            }
        }
    }
    
    private boolean isPostScriptFlavor(final DocFlavor docFlavor) {
        return docFlavor.equals(DocFlavor.BYTE_ARRAY.POSTSCRIPT) || docFlavor.equals(DocFlavor.INPUT_STREAM.POSTSCRIPT) || docFlavor.equals(DocFlavor.URL.POSTSCRIPT);
    }
    
    private boolean isPSDocAttr(final Class clazz) {
        return clazz == OrientationRequested.class || clazz == Copies.class;
    }
    
    private boolean isAutoSense(final DocFlavor docFlavor) {
        return docFlavor.equals(DocFlavor.BYTE_ARRAY.AUTOSENSE) || docFlavor.equals(DocFlavor.INPUT_STREAM.AUTOSENSE) || docFlavor.equals(DocFlavor.URL.AUTOSENSE);
    }
    
    @Override
    public Object getSupportedAttributeValues(final Class<? extends Attribute> clazz, final DocFlavor docFlavor, final AttributeSet set) {
        if (clazz == null) {
            throw new NullPointerException("null category");
        }
        if (!Attribute.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException(clazz + " does not implement Attribute");
        }
        if (docFlavor != null) {
            if (!this.isDocFlavorSupported(docFlavor)) {
                throw new IllegalArgumentException(docFlavor + " is an unsupported flavor");
            }
            if (this.isAutoSense(docFlavor) || (this.isPostScriptFlavor(docFlavor) && this.isPSDocAttr(clazz))) {
                return null;
            }
        }
        if (!this.isAttributeCategorySupported(clazz)) {
            return null;
        }
        if (clazz == JobName.class) {
            return new JobName("Java Printing", null);
        }
        if (clazz == RequestingUserName.class) {
            String property = "";
            try {
                property = System.getProperty("user.name", "");
            }
            catch (final SecurityException ex) {}
            return new RequestingUserName(property, null);
        }
        if (clazz == ColorSupported.class) {
            if ((this.getPrinterCapabilities() & 0x1) != 0x0) {
                return ColorSupported.SUPPORTED;
            }
            return ColorSupported.NOT_SUPPORTED;
        }
        else if (clazz == Chromaticity.class) {
            if (docFlavor != null && !docFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && !docFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE) && !docFlavor.equals(DocFlavor.BYTE_ARRAY.GIF) && !docFlavor.equals(DocFlavor.INPUT_STREAM.GIF) && !docFlavor.equals(DocFlavor.URL.GIF) && !docFlavor.equals(DocFlavor.BYTE_ARRAY.JPEG) && !docFlavor.equals(DocFlavor.INPUT_STREAM.JPEG) && !docFlavor.equals(DocFlavor.URL.JPEG) && !docFlavor.equals(DocFlavor.BYTE_ARRAY.PNG) && !docFlavor.equals(DocFlavor.INPUT_STREAM.PNG) && !docFlavor.equals(DocFlavor.URL.PNG)) {
                return null;
            }
            if ((this.getPrinterCapabilities() & 0x1) == 0x0) {
                return new Chromaticity[] { Chromaticity.MONOCHROME };
            }
            return new Chromaticity[] { Chromaticity.MONOCHROME, Chromaticity.COLOR };
        }
        else {
            if (clazz == Destination.class) {
                try {
                    return new Destination(new File("out.prn").toURI());
                }
                catch (final SecurityException ex2) {
                    try {
                        return new Destination(new URI("file:out.prn"));
                    }
                    catch (final URISyntaxException ex3) {
                        return null;
                    }
                }
            }
            if (clazz == OrientationRequested.class) {
                if (docFlavor == null || docFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) || docFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE) || docFlavor.equals(DocFlavor.INPUT_STREAM.GIF) || docFlavor.equals(DocFlavor.INPUT_STREAM.JPEG) || docFlavor.equals(DocFlavor.INPUT_STREAM.PNG) || docFlavor.equals(DocFlavor.BYTE_ARRAY.GIF) || docFlavor.equals(DocFlavor.BYTE_ARRAY.JPEG) || docFlavor.equals(DocFlavor.BYTE_ARRAY.PNG) || docFlavor.equals(DocFlavor.URL.GIF) || docFlavor.equals(DocFlavor.URL.JPEG) || docFlavor.equals(DocFlavor.URL.PNG)) {
                    return new OrientationRequested[] { OrientationRequested.PORTRAIT, OrientationRequested.LANDSCAPE, OrientationRequested.REVERSE_LANDSCAPE };
                }
                return null;
            }
            else {
                if (clazz == Copies.class || clazz == CopiesSupported.class) {
                    synchronized (this) {
                        if (!this.gotCopies) {
                            this.nCopies = this.getCopiesSupported(this.printer, this.getPort());
                            this.gotCopies = true;
                        }
                    }
                    return new CopiesSupported(1, this.nCopies);
                }
                if (clazz == Media.class) {
                    this.initMedia();
                    final int n = (this.mediaSizeNames == null) ? 0 : this.mediaSizeNames.length;
                    final MediaTray[] mediaTrays = this.getMediaTrays();
                    final int n2 = n + ((mediaTrays == null) ? 0 : mediaTrays.length);
                    final Media[] array = new Media[n2];
                    if (this.mediaSizeNames != null) {
                        System.arraycopy(this.mediaSizeNames, 0, array, 0, this.mediaSizeNames.length);
                    }
                    if (mediaTrays != null) {
                        System.arraycopy(mediaTrays, 0, array, n2 - mediaTrays.length, mediaTrays.length);
                    }
                    return array;
                }
                if (clazz == MediaPrintableArea.class) {
                    Media media = null;
                    if (set != null && (media = (Media)set.get(Media.class)) != null && !(media instanceof MediaSizeName)) {
                        media = null;
                    }
                    final MediaPrintableArea[] mediaPrintables = this.getMediaPrintables((MediaSizeName)media);
                    if (mediaPrintables != null) {
                        final MediaPrintableArea[] array2 = new MediaPrintableArea[mediaPrintables.length];
                        System.arraycopy(mediaPrintables, 0, array2, 0, mediaPrintables.length);
                        return array2;
                    }
                    return null;
                }
                else {
                    if (clazz == SunAlternateMedia.class) {
                        return new SunAlternateMedia((Media)this.getDefaultAttributeValue(Media.class));
                    }
                    if (clazz == PageRanges.class) {
                        if (docFlavor == null || docFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) || docFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
                            return new PageRanges[] { new PageRanges(1, Integer.MAX_VALUE) };
                        }
                        return null;
                    }
                    else if (clazz == PrinterResolution.class) {
                        final PrinterResolution[] printResolutions = this.getPrintResolutions();
                        if (printResolutions == null) {
                            return null;
                        }
                        final PrinterResolution[] array3 = new PrinterResolution[printResolutions.length];
                        System.arraycopy(printResolutions, 0, array3, 0, printResolutions.length);
                        return array3;
                    }
                    else if (clazz == Sides.class) {
                        if (docFlavor == null || docFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) || docFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
                            return new Sides[] { Sides.ONE_SIDED, Sides.TWO_SIDED_LONG_EDGE, Sides.TWO_SIDED_SHORT_EDGE };
                        }
                        return null;
                    }
                    else {
                        if (clazz == PrintQuality.class) {
                            return new PrintQuality[] { PrintQuality.DRAFT, PrintQuality.HIGH, PrintQuality.NORMAL };
                        }
                        if (clazz == SheetCollate.class) {
                            if (docFlavor == null || docFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) || docFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
                                return new SheetCollate[] { SheetCollate.COLLATED, SheetCollate.UNCOLLATED };
                            }
                            return null;
                        }
                        else {
                            if (clazz == Fidelity.class) {
                                return new Fidelity[] { Fidelity.FIDELITY_FALSE, Fidelity.FIDELITY_TRUE };
                            }
                            return null;
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public boolean isAttributeValueSupported(final Attribute attribute, final DocFlavor docFlavor, final AttributeSet set) {
        if (attribute == null) {
            throw new NullPointerException("null attribute");
        }
        final Class<? extends Attribute> category = attribute.getCategory();
        if (docFlavor != null) {
            if (!this.isDocFlavorSupported(docFlavor)) {
                throw new IllegalArgumentException(docFlavor + " is an unsupported flavor");
            }
            if (this.isAutoSense(docFlavor) || (this.isPostScriptFlavor(docFlavor) && this.isPSDocAttr(category))) {
                return false;
            }
        }
        if (!this.isAttributeCategorySupported(category)) {
            return false;
        }
        if (category == Chromaticity.class) {
            return (docFlavor == null || docFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) || docFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE) || docFlavor.equals(DocFlavor.BYTE_ARRAY.GIF) || docFlavor.equals(DocFlavor.INPUT_STREAM.GIF) || docFlavor.equals(DocFlavor.URL.GIF) || docFlavor.equals(DocFlavor.BYTE_ARRAY.JPEG) || docFlavor.equals(DocFlavor.INPUT_STREAM.JPEG) || docFlavor.equals(DocFlavor.URL.JPEG) || docFlavor.equals(DocFlavor.BYTE_ARRAY.PNG) || docFlavor.equals(DocFlavor.INPUT_STREAM.PNG) || docFlavor.equals(DocFlavor.URL.PNG)) && ((this.getPrinterCapabilities() & 0x1) != 0x0 || attribute == Chromaticity.MONOCHROME);
        }
        if (category == Copies.class) {
            return this.isSupportedCopies((Copies)attribute);
        }
        if (category == Destination.class) {
            final URI uri = ((Destination)attribute).getURI();
            return "file".equals(uri.getScheme()) && !uri.getSchemeSpecificPart().equals("");
        }
        if (category == Media.class) {
            if (attribute instanceof MediaSizeName) {
                return this.isSupportedMedia((MediaSizeName)attribute);
            }
            if (attribute instanceof MediaTray) {
                return this.isSupportedMediaTray((MediaTray)attribute);
            }
        }
        else {
            if (category == MediaPrintableArea.class) {
                return this.isSupportedMediaPrintableArea((MediaPrintableArea)attribute);
            }
            if (category == SunAlternateMedia.class) {
                return this.isAttributeValueSupported(((SunAlternateMedia)attribute).getMedia(), docFlavor, set);
            }
            if (category == PageRanges.class || category == SheetCollate.class || category == Sides.class) {
                if (docFlavor != null && !docFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && !docFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
                    return false;
                }
            }
            else if (category == PrinterResolution.class) {
                if (attribute instanceof PrinterResolution) {
                    return this.isSupportedResolution((PrinterResolution)attribute);
                }
            }
            else if (category == OrientationRequested.class) {
                if (attribute == OrientationRequested.REVERSE_PORTRAIT || (docFlavor != null && !docFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && !docFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE) && !docFlavor.equals(DocFlavor.INPUT_STREAM.GIF) && !docFlavor.equals(DocFlavor.INPUT_STREAM.JPEG) && !docFlavor.equals(DocFlavor.INPUT_STREAM.PNG) && !docFlavor.equals(DocFlavor.BYTE_ARRAY.GIF) && !docFlavor.equals(DocFlavor.BYTE_ARRAY.JPEG) && !docFlavor.equals(DocFlavor.BYTE_ARRAY.PNG) && !docFlavor.equals(DocFlavor.URL.GIF) && !docFlavor.equals(DocFlavor.URL.JPEG) && !docFlavor.equals(DocFlavor.URL.PNG))) {
                    return false;
                }
            }
            else if (category == ColorSupported.class) {
                final boolean b = (this.getPrinterCapabilities() & 0x1) != 0x0;
                if ((!b && attribute == ColorSupported.SUPPORTED) || (b && attribute == ColorSupported.NOT_SUPPORTED)) {
                    return false;
                }
            }
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
    
    private synchronized DocumentPropertiesUI getDocumentPropertiesUI() {
        return new Win32DocumentPropertiesUI(this);
    }
    
    @Override
    public synchronized ServiceUIFactory getServiceUIFactory() {
        if (this.uiFactory == null) {
            this.uiFactory = new Win32ServiceUIFactory(this);
        }
        return this.uiFactory;
    }
    
    @Override
    public String toString() {
        return "Win32 Printer : " + this.getName();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof Win32PrintService && ((Win32PrintService)o).getName().equals(this.getName()));
    }
    
    @Override
    public int hashCode() {
        return this.getClass().hashCode() + this.getName().hashCode();
    }
    
    @Override
    public boolean usesClass(final Class clazz) {
        return clazz == WPrinterJob.class;
    }
    
    private native int[] getAllMediaIDs(final String p0, final String p1);
    
    private native int[] getAllMediaSizes(final String p0, final String p1);
    
    private native int[] getAllMediaTrays(final String p0, final String p1);
    
    private native float[] getMediaPrintableArea(final String p0, final int p1);
    
    private native String[] getAllMediaNames(final String p0, final String p1);
    
    private native String[] getAllMediaTrayNames(final String p0, final String p1);
    
    private native int getCopiesSupported(final String p0, final String p1);
    
    private native int[] getAllResolutions(final String p0, final String p1);
    
    private native int getCapabilities(final String p0, final String p1);
    
    private native int[] getDefaultSettings(final String p0, final String p1);
    
    private native int getJobStatus(final String p0, final int p1);
    
    private native String getPrinterPort(final String p0);
    
    static {
        Win32PrintService.predefMedia = Win32MediaSize.getPredefMedia();
        supportedFlavors = new DocFlavor[] { DocFlavor.BYTE_ARRAY.GIF, DocFlavor.INPUT_STREAM.GIF, DocFlavor.URL.GIF, DocFlavor.BYTE_ARRAY.JPEG, DocFlavor.INPUT_STREAM.JPEG, DocFlavor.URL.JPEG, DocFlavor.BYTE_ARRAY.PNG, DocFlavor.INPUT_STREAM.PNG, DocFlavor.URL.PNG, DocFlavor.SERVICE_FORMATTED.PAGEABLE, DocFlavor.SERVICE_FORMATTED.PRINTABLE, DocFlavor.BYTE_ARRAY.AUTOSENSE, DocFlavor.URL.AUTOSENSE, DocFlavor.INPUT_STREAM.AUTOSENSE };
        serviceAttrCats = new Class[] { PrinterName.class, PrinterIsAcceptingJobs.class, QueuedJobCount.class, ColorSupported.class };
        Win32PrintService.otherAttrCats = new Class[] { JobName.class, RequestingUserName.class, Copies.class, Destination.class, OrientationRequested.class, PageRanges.class, Media.class, MediaPrintableArea.class, Fidelity.class, SheetCollate.class, SunAlternateMedia.class, Chromaticity.class };
        dmPaperToPrintService = new MediaSizeName[] { MediaSizeName.NA_LETTER, MediaSizeName.NA_LETTER, MediaSizeName.TABLOID, MediaSizeName.LEDGER, MediaSizeName.NA_LEGAL, MediaSizeName.INVOICE, MediaSizeName.EXECUTIVE, MediaSizeName.ISO_A3, MediaSizeName.ISO_A4, MediaSizeName.ISO_A4, MediaSizeName.ISO_A5, MediaSizeName.JIS_B4, MediaSizeName.JIS_B5, MediaSizeName.FOLIO, MediaSizeName.QUARTO, MediaSizeName.NA_10X14_ENVELOPE, MediaSizeName.B, MediaSizeName.NA_LETTER, MediaSizeName.NA_NUMBER_9_ENVELOPE, MediaSizeName.NA_NUMBER_10_ENVELOPE, MediaSizeName.NA_NUMBER_11_ENVELOPE, MediaSizeName.NA_NUMBER_12_ENVELOPE, MediaSizeName.NA_NUMBER_14_ENVELOPE, MediaSizeName.C, MediaSizeName.D, MediaSizeName.E, MediaSizeName.ISO_DESIGNATED_LONG, MediaSizeName.ISO_C5, MediaSizeName.ISO_C3, MediaSizeName.ISO_C4, MediaSizeName.ISO_C6, MediaSizeName.ITALY_ENVELOPE, MediaSizeName.ISO_B4, MediaSizeName.ISO_B5, MediaSizeName.ISO_B6, MediaSizeName.ITALY_ENVELOPE, MediaSizeName.MONARCH_ENVELOPE, MediaSizeName.PERSONAL_ENVELOPE, MediaSizeName.NA_10X15_ENVELOPE, MediaSizeName.NA_9X12_ENVELOPE, MediaSizeName.FOLIO, MediaSizeName.ISO_B4, MediaSizeName.JAPANESE_POSTCARD, MediaSizeName.NA_9X11_ENVELOPE };
        dmPaperBinToPrintService = new MediaTray[] { MediaTray.TOP, MediaTray.BOTTOM, MediaTray.MIDDLE, MediaTray.MANUAL, MediaTray.ENVELOPE, Win32MediaTray.ENVELOPE_MANUAL, Win32MediaTray.AUTO, Win32MediaTray.TRACTOR, Win32MediaTray.SMALL_FORMAT, Win32MediaTray.LARGE_FORMAT, MediaTray.LARGE_CAPACITY, null, null, MediaTray.MAIN, Win32MediaTray.FORMSOURCE };
        Win32PrintService.DM_PAPERSIZE = 2;
        Win32PrintService.DM_PRINTQUALITY = 1024;
        Win32PrintService.DM_YRESOLUTION = 8192;
    }
    
    private static class Win32DocumentPropertiesUI extends DocumentPropertiesUI
    {
        Win32PrintService service;
        
        private Win32DocumentPropertiesUI(final Win32PrintService service) {
            this.service = service;
        }
        
        @Override
        public PrintRequestAttributeSet showDocumentProperties(final PrinterJob printerJob, final Window window, final PrintService printService, final PrintRequestAttributeSet set) {
            if (!(printerJob instanceof WPrinterJob)) {
                return null;
            }
            return ((WPrinterJob)printerJob).showDocumentProperties(window, printService, set);
        }
    }
    
    private static class Win32ServiceUIFactory extends ServiceUIFactory
    {
        Win32PrintService service;
        
        Win32ServiceUIFactory(final Win32PrintService service) {
            this.service = service;
        }
        
        @Override
        public Object getUI(final int n, final String s) {
            if (n <= 3) {
                return null;
            }
            if (n == 199 && DocumentPropertiesUI.DOCPROPERTIESCLASSNAME.equals(s)) {
                return this.service.getDocumentPropertiesUI();
            }
            throw new IllegalArgumentException("Unsupported role");
        }
        
        @Override
        public String[] getUIClassNamesForRole(final int n) {
            if (n <= 3) {
                return null;
            }
            if (n == 199) {
                final String[] array = new String[0];
                array[0] = DocumentPropertiesUI.DOCPROPERTIESCLASSNAME;
                return array;
            }
            throw new IllegalArgumentException("Unsupported role");
        }
    }
}
