package javax.print;

import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.security.PrivilegedExceptionAction;
import java.io.OutputStream;
import java.util.ArrayList;
import sun.awt.AppContext;

public abstract class StreamPrintServiceFactory
{
    private static Services getServices() {
        Services services = (Services)AppContext.getAppContext().get(Services.class);
        if (services == null) {
            services = new Services();
            AppContext.getAppContext().put(Services.class, services);
        }
        return services;
    }
    
    private static ArrayList getListOfFactories() {
        return getServices().listOfFactories;
    }
    
    private static ArrayList initListOfFactories() {
        final ArrayList list = new ArrayList();
        getServices().listOfFactories = list;
        return list;
    }
    
    public static StreamPrintServiceFactory[] lookupStreamPrintServiceFactories(final DocFlavor docFlavor, final String s) {
        final ArrayList factories = getFactories(docFlavor, s);
        return factories.toArray(new StreamPrintServiceFactory[factories.size()]);
    }
    
    public abstract String getOutputFormat();
    
    public abstract DocFlavor[] getSupportedDocFlavors();
    
    public abstract StreamPrintService getPrintService(final OutputStream p0);
    
    private static ArrayList getAllFactories() {
        synchronized (StreamPrintServiceFactory.class) {
            final ArrayList listOfFactories = getListOfFactories();
            if (listOfFactories != null) {
                return listOfFactories;
            }
            final ArrayList initListOfFactories = initListOfFactories();
            try {
                AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction() {
                    @Override
                    public Object run() {
                        final Iterator<StreamPrintServiceFactory> iterator = ServiceLoader.load(StreamPrintServiceFactory.class).iterator();
                        final ArrayList access$100 = getListOfFactories();
                        while (iterator.hasNext()) {
                            try {
                                access$100.add(iterator.next());
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
            return initListOfFactories;
        }
    }
    
    private static boolean isMember(final DocFlavor docFlavor, final DocFlavor[] array) {
        for (int i = 0; i < array.length; ++i) {
            if (docFlavor.equals(array[i])) {
                return true;
            }
        }
        return false;
    }
    
    private static ArrayList getFactories(final DocFlavor docFlavor, final String s) {
        if (docFlavor == null && s == null) {
            return getAllFactories();
        }
        final ArrayList list = new ArrayList();
        for (final StreamPrintServiceFactory streamPrintServiceFactory : getAllFactories()) {
            if ((s == null || s.equalsIgnoreCase(streamPrintServiceFactory.getOutputFormat())) && (docFlavor == null || isMember(docFlavor, streamPrintServiceFactory.getSupportedDocFlavors()))) {
                list.add(streamPrintServiceFactory);
            }
        }
        return list;
    }
    
    static class Services
    {
        private ArrayList listOfFactories;
        
        Services() {
            this.listOfFactories = null;
        }
    }
}
