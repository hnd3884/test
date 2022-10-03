package sun.print;

import java.util.Arrays;
import java.util.Comparator;
import javax.print.MultiDocPrintService;
import javax.print.attribute.PrintRequestAttributeSet;
import java.util.ArrayList;
import javax.print.attribute.standard.PrinterName;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.AttributeSet;
import javax.print.DocFlavor;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.PrintServiceAttributeSet;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

public class PrintServiceLookupProvider extends PrintServiceLookup
{
    private String defaultPrinter;
    private PrintService defaultPrintService;
    private String[] printers;
    private PrintService[] printServices;
    private static final int DEFAULT_REFRESH_TIME = 240;
    private static final int MINIMUM_REFRESH_TIME = 120;
    private static final boolean pollServices;
    private static final int refreshTime;
    private static PrintServiceLookupProvider win32PrintLUS;
    
    private static int getRefreshTime(final String s) {
        try {
            final int int1 = Integer.parseInt(s);
            return (int1 < 120) ? 120 : int1;
        }
        catch (final NumberFormatException ex) {
            return 240;
        }
    }
    
    public static PrintServiceLookupProvider getWin32PrintLUS() {
        if (PrintServiceLookupProvider.win32PrintLUS == null) {
            PrintServiceLookup.lookupDefaultPrintService();
        }
        return PrintServiceLookupProvider.win32PrintLUS;
    }
    
    public PrintServiceLookupProvider() {
        if (PrintServiceLookupProvider.win32PrintLUS == null) {
            PrintServiceLookupProvider.win32PrintLUS = this;
            final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("os.name"));
            if (s != null && s.startsWith("Windows 98")) {
                return;
            }
            final PrinterChangeListener printerChangeListener = new PrinterChangeListener();
            printerChangeListener.setDaemon(true);
            printerChangeListener.start();
            if (PrintServiceLookupProvider.pollServices) {
                final RemotePrinterChangeListener remotePrinterChangeListener = new RemotePrinterChangeListener();
                remotePrinterChangeListener.setDaemon(true);
                remotePrinterChangeListener.start();
            }
        }
    }
    
    @Override
    public synchronized PrintService[] getPrintServices() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPrintJobAccess();
        }
        if (this.printServices == null) {
            this.refreshServices();
        }
        return this.printServices;
    }
    
    private synchronized void refreshServices() {
        this.printers = this.getAllPrinterNames();
        if (this.printers == null) {
            this.printServices = new PrintService[0];
            return;
        }
        final PrintService[] printServices = new PrintService[this.printers.length];
        final PrintService defaultPrintService = this.getDefaultPrintService();
        for (int i = 0; i < this.printers.length; ++i) {
            if (defaultPrintService != null && this.printers[i].equals(defaultPrintService.getName())) {
                printServices[i] = defaultPrintService;
            }
            else if (this.printServices == null) {
                printServices[i] = new Win32PrintService(this.printers[i]);
            }
            else {
                int j;
                for (j = 0; j < this.printServices.length; ++j) {
                    if (this.printServices[j] != null && this.printers[i].equals(this.printServices[j].getName())) {
                        printServices[i] = this.printServices[j];
                        this.printServices[j] = null;
                        break;
                    }
                }
                if (j == this.printServices.length) {
                    printServices[i] = new Win32PrintService(this.printers[i]);
                }
            }
        }
        if (this.printServices != null) {
            for (int k = 0; k < this.printServices.length; ++k) {
                if (this.printServices[k] instanceof Win32PrintService && !this.printServices[k].equals(this.defaultPrintService)) {
                    ((Win32PrintService)this.printServices[k]).invalidateService();
                }
            }
        }
        this.printServices = printServices;
    }
    
    public synchronized PrintService getPrintServiceByName(final String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        final PrintService[] printServices = this.getPrintServices();
        for (int i = 0; i < printServices.length; ++i) {
            if (printServices[i].getName().equals(s)) {
                return printServices[i];
            }
        }
        return null;
    }
    
    boolean matchingService(final PrintService printService, final PrintServiceAttributeSet set) {
        if (set != null) {
            final Attribute[] array = set.toArray();
            for (int i = 0; i < array.length; ++i) {
                final PrintServiceAttribute attribute = printService.getAttribute(array[i].getCategory());
                if (attribute == null || !attribute.equals(array[i])) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public PrintService[] getPrintServices(final DocFlavor docFlavor, final AttributeSet set) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPrintJobAccess();
        }
        PrintRequestAttributeSet set2 = null;
        PrintServiceAttributeSet set3 = null;
        if (set != null && !set.isEmpty()) {
            set2 = new HashPrintRequestAttributeSet();
            set3 = new HashPrintServiceAttributeSet();
            final Attribute[] array = set.toArray();
            for (int i = 0; i < array.length; ++i) {
                if (array[i] instanceof PrintRequestAttribute) {
                    set2.add(array[i]);
                }
                else if (array[i] instanceof PrintServiceAttribute) {
                    set3.add(array[i]);
                }
            }
        }
        PrintService[] printServices;
        if (set3 != null && set3.get(PrinterName.class) != null) {
            final PrintService printServiceByName = this.getPrintServiceByName(((PrinterName)set3.get(PrinterName.class)).getValue());
            if (printServiceByName == null || !this.matchingService(printServiceByName, set3)) {
                printServices = new PrintService[0];
            }
            else {
                printServices = new PrintService[] { printServiceByName };
            }
        }
        else {
            printServices = this.getPrintServices();
        }
        if (printServices.length == 0) {
            return printServices;
        }
        final ArrayList<PrintService> list = new ArrayList<PrintService>();
        for (int j = 0; j < printServices.length; ++j) {
            try {
                if (printServices[j].getUnsupportedAttributes(docFlavor, set2) == null) {
                    list.add(printServices[j]);
                }
            }
            catch (final IllegalArgumentException ex) {}
        }
        return list.toArray(new PrintService[list.size()]);
    }
    
    @Override
    public MultiDocPrintService[] getMultiDocPrintServices(final DocFlavor[] array, final AttributeSet set) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPrintJobAccess();
        }
        return new MultiDocPrintService[0];
    }
    
    @Override
    public synchronized PrintService getDefaultPrintService() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPrintJobAccess();
        }
        this.defaultPrinter = this.getDefaultPrinterName();
        if (this.defaultPrinter == null) {
            return null;
        }
        if (this.defaultPrintService != null && this.defaultPrintService.getName().equals(this.defaultPrinter)) {
            return this.defaultPrintService;
        }
        this.defaultPrintService = null;
        if (this.printServices != null) {
            for (int i = 0; i < this.printServices.length; ++i) {
                if (this.defaultPrinter.equals(this.printServices[i].getName())) {
                    this.defaultPrintService = this.printServices[i];
                    break;
                }
            }
        }
        if (this.defaultPrintService == null) {
            this.defaultPrintService = new Win32PrintService(this.defaultPrinter);
        }
        return this.defaultPrintService;
    }
    
    private native String getDefaultPrinterName();
    
    private native String[] getAllPrinterNames();
    
    private native long notifyFirstPrinterChange(final String p0);
    
    private native void notifyClosePrinterChange(final long p0);
    
    private native int notifyPrinterChange(final long p0);
    
    private native String[] getRemotePrintersNames();
    
    static {
        pollServices = !"false".equalsIgnoreCase(AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.java2d.print.polling")));
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.java2d.print.minRefreshTime"));
        refreshTime = ((s != null) ? getRefreshTime(s) : 240);
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                System.loadLibrary("awt");
                return null;
            }
        });
    }
    
    class PrinterChangeListener extends Thread
    {
        long chgObj;
        
        PrinterChangeListener() {
            this.chgObj = PrintServiceLookupProvider.this.notifyFirstPrinterChange(null);
        }
        
        @Override
        public void run() {
            if (this.chgObj != -1L) {
                while (PrintServiceLookupProvider.this.notifyPrinterChange(this.chgObj) != 0) {
                    try {
                        PrintServiceLookupProvider.this.refreshServices();
                        continue;
                    }
                    catch (final SecurityException ex) {
                        return;
                    }
                    break;
                }
                PrintServiceLookupProvider.this.notifyClosePrinterChange(this.chgObj);
            }
        }
    }
    
    class RemotePrinterChangeListener extends Thread implements Comparator<String>
    {
        @Override
        public int compare(final String s, final String s2) {
            return (s == null) ? ((s2 == null) ? 0 : 1) : ((s2 == null) ? -1 : s.compareTo(s2));
        }
        
        @Override
        public void run() {
            String[] access$400 = PrintServiceLookupProvider.this.getRemotePrintersNames();
            if (access$400 != null) {
                Arrays.sort(access$400, this);
            }
            while (true) {
                try {
                    Thread.sleep(PrintServiceLookupProvider.refreshTime * 1000);
                }
                catch (final InterruptedException ex) {
                    break;
                }
                final String[] access$401 = PrintServiceLookupProvider.this.getRemotePrintersNames();
                if (access$401 != null) {
                    Arrays.sort(access$401, this);
                }
                if (!Arrays.equals(access$400, access$401)) {
                    PrintServiceLookupProvider.this.refreshServices();
                    access$400 = access$401;
                }
            }
        }
    }
}
