package javax.print;

import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.security.PrivilegedExceptionAction;
import java.util.Iterator;
import javax.print.attribute.AttributeSet;
import java.util.ArrayList;
import sun.awt.AppContext;

public abstract class PrintServiceLookup
{
    private static Services getServicesForContext() {
        Services services = (Services)AppContext.getAppContext().get(Services.class);
        if (services == null) {
            services = new Services();
            AppContext.getAppContext().put(Services.class, services);
        }
        return services;
    }
    
    private static ArrayList getListOfLookupServices() {
        return getServicesForContext().listOfLookupServices;
    }
    
    private static ArrayList initListOfLookupServices() {
        final ArrayList list = new ArrayList();
        getServicesForContext().listOfLookupServices = list;
        return list;
    }
    
    private static ArrayList getRegisteredServices() {
        return getServicesForContext().registeredServices;
    }
    
    private static ArrayList initRegisteredServices() {
        final ArrayList list = new ArrayList();
        getServicesForContext().registeredServices = list;
        return list;
    }
    
    public static final PrintService[] lookupPrintServices(final DocFlavor docFlavor, final AttributeSet set) {
        final ArrayList services = getServices(docFlavor, set);
        return services.toArray(new PrintService[services.size()]);
    }
    
    public static final MultiDocPrintService[] lookupMultiDocPrintServices(final DocFlavor[] array, final AttributeSet set) {
        final ArrayList multiDocServices = getMultiDocServices(array, set);
        return multiDocServices.toArray(new MultiDocPrintService[multiDocServices.size()]);
    }
    
    public static final PrintService lookupDefaultPrintService() {
        final Iterator iterator = getAllLookupServices().iterator();
        while (iterator.hasNext()) {
            try {
                final PrintService defaultPrintService = ((PrintServiceLookup)iterator.next()).getDefaultPrintService();
                if (defaultPrintService != null) {
                    return defaultPrintService;
                }
                continue;
            }
            catch (final Exception ex) {}
        }
        return null;
    }
    
    public static boolean registerServiceProvider(final PrintServiceLookup printServiceLookup) {
        synchronized (PrintServiceLookup.class) {
            final Iterator iterator = getAllLookupServices().iterator();
            while (iterator.hasNext()) {
                try {
                    if (iterator.next().getClass() == printServiceLookup.getClass()) {
                        return false;
                    }
                    continue;
                }
                catch (final Exception ex) {}
            }
            getListOfLookupServices().add(printServiceLookup);
            return true;
        }
    }
    
    public static boolean registerService(final PrintService printService) {
        synchronized (PrintServiceLookup.class) {
            if (printService instanceof StreamPrintService) {
                return false;
            }
            ArrayList list = getRegisteredServices();
            if (list == null) {
                list = initRegisteredServices();
            }
            else if (list.contains(printService)) {
                return false;
            }
            list.add(printService);
            return true;
        }
    }
    
    public abstract PrintService[] getPrintServices(final DocFlavor p0, final AttributeSet p1);
    
    public abstract PrintService[] getPrintServices();
    
    public abstract MultiDocPrintService[] getMultiDocPrintServices(final DocFlavor[] p0, final AttributeSet p1);
    
    public abstract PrintService getDefaultPrintService();
    
    private static ArrayList getAllLookupServices() {
        synchronized (PrintServiceLookup.class) {
            final ArrayList listOfLookupServices = getListOfLookupServices();
            if (listOfLookupServices != null) {
                return listOfLookupServices;
            }
            final ArrayList initListOfLookupServices = initListOfLookupServices();
            try {
                AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction() {
                    @Override
                    public Object run() {
                        final Iterator<PrintServiceLookup> iterator = ServiceLoader.load(PrintServiceLookup.class).iterator();
                        final ArrayList access$200 = getListOfLookupServices();
                        while (iterator.hasNext()) {
                            try {
                                access$200.add(iterator.next());
                            }
                            catch (final ServiceConfigurationError serviceConfigurationError) {
                                if (System.getSecurityManager() == null) {
                                    throw serviceConfigurationError;
                                }
                                serviceConfigurationError.printStackTrace();
                            }
                        }
                        return null;
                    }
                });
            }
            catch (final PrivilegedActionException ex) {}
            return initListOfLookupServices;
        }
    }
    
    private static ArrayList getServices(final DocFlavor docFlavor, final AttributeSet set) {
        final ArrayList list = new ArrayList();
        final Iterator iterator = getAllLookupServices().iterator();
        while (iterator.hasNext()) {
            try {
                final PrintServiceLookup printServiceLookup = (PrintServiceLookup)iterator.next();
                PrintService[] array = null;
                if (docFlavor == null && set == null) {
                    try {
                        array = printServiceLookup.getPrintServices();
                    }
                    catch (final Throwable t) {}
                }
                else {
                    array = printServiceLookup.getPrintServices(docFlavor, set);
                }
                if (array == null) {
                    continue;
                }
                for (int i = 0; i < array.length; ++i) {
                    list.add(array[i]);
                }
            }
            catch (final Exception ex) {}
        }
        ArrayList registeredServices = null;
        try {
            final SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null) {
                securityManager.checkPrintJobAccess();
            }
            registeredServices = getRegisteredServices();
        }
        catch (final SecurityException ex2) {}
        if (registeredServices != null) {
            final PrintService[] array2 = (PrintService[])registeredServices.toArray(new PrintService[registeredServices.size()]);
            for (int j = 0; j < array2.length; ++j) {
                if (!list.contains(array2[j])) {
                    if (docFlavor == null && set == null) {
                        list.add(array2[j]);
                    }
                    else if (((docFlavor != null && array2[j].isDocFlavorSupported(docFlavor)) || docFlavor == null) && null == array2[j].getUnsupportedAttributes(docFlavor, set)) {
                        list.add(array2[j]);
                    }
                }
            }
        }
        return list;
    }
    
    private static ArrayList getMultiDocServices(final DocFlavor[] array, final AttributeSet set) {
        final ArrayList list = new ArrayList();
        final Iterator iterator = getAllLookupServices().iterator();
        while (iterator.hasNext()) {
            try {
                final MultiDocPrintService[] multiDocPrintServices = ((PrintServiceLookup)iterator.next()).getMultiDocPrintServices(array, set);
                if (multiDocPrintServices == null) {
                    continue;
                }
                for (int i = 0; i < multiDocPrintServices.length; ++i) {
                    list.add(multiDocPrintServices[i]);
                }
            }
            catch (final Exception ex) {}
        }
        ArrayList registeredServices = null;
        try {
            final SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null) {
                securityManager.checkPrintJobAccess();
            }
            registeredServices = getRegisteredServices();
        }
        catch (final Exception ex2) {}
        if (registeredServices != null) {
            final PrintService[] array2 = (PrintService[])registeredServices.toArray(new PrintService[registeredServices.size()]);
            for (int j = 0; j < array2.length; ++j) {
                if (array2[j] instanceof MultiDocPrintService && !list.contains(array2[j])) {
                    if (array == null || array.length == 0) {
                        list.add(array2[j]);
                    }
                    else {
                        boolean b = true;
                        for (int k = 0; k < array.length; ++k) {
                            if (!array2[j].isDocFlavorSupported(array[k])) {
                                b = false;
                                break;
                            }
                            if (array2[j].getUnsupportedAttributes(array[k], set) != null) {
                                b = false;
                                break;
                            }
                        }
                        if (b) {
                            list.add(array2[j]);
                        }
                    }
                }
            }
        }
        return list;
    }
    
    static class Services
    {
        private ArrayList listOfLookupServices;
        private ArrayList registeredServices;
        
        Services() {
            this.listOfLookupServices = null;
            this.registeredServices = null;
        }
    }
}
