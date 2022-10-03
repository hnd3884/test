package com.adventnet.mfw.service;

import java.util.ArrayList;
import java.util.ListIterator;
import com.zoho.mickey.startup.MEServer;
import com.adventnet.persistence.PersistenceInitializer;
import com.adventnet.mfw.Starter;
import com.adventnet.mfw.ConsoleOut;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class ServiceStarter
{
    private static final Logger LOG;
    private static List serviceList;
    public static HashMap serviceMap;
    private static List serviceDOList;
    private static List createdServices;
    private static List startedServices;
    private static int sizeOfService;
    private static Service webService;
    
    private ServiceStarter() {
    }
    
    public static void addServices(final DataObject servicesDO) throws Exception {
        final List tableNames = servicesDO.getTableNames();
        tableNames.remove("ConfFile");
        tableNames.remove("UVHValues");
        final Iterator serviceIterator = servicesDO.getRows("Service");
        while (serviceIterator.hasNext()) {
            final Row serviceRow = serviceIterator.next();
            final DataObject serviceDO = servicesDO.getDataObject(tableNames, serviceRow);
            ServiceStarter.serviceDOList.add(serviceDO);
            final String serviceName = (String)serviceRow.get(4);
            final Service service = (Service)Class.forName((String)serviceRow.get(5)).newInstance();
            ServiceStarter.serviceList.add(serviceName);
            ServiceStarter.serviceMap.put(serviceName, service);
        }
    }
    
    public static void fetchServicesFromDB() throws Exception {
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("Service"));
        sq.addSelectColumn(Column.getColumn("Service", "*"));
        sq.addSelectColumn(Column.getColumn("ServiceProperties", "*"));
        final Join j1 = new Join("Service", "ServiceProperties", new String[] { "SERVICE_ID" }, new String[] { "SERVICE_ID" }, 1);
        final Join j2 = new Join("Service", "Module", new String[] { "MODULE_ID" }, new String[] { "MODULE_ID" }, 1);
        sq.addJoin(j1);
        sq.addJoin(j2);
        sq.addSortColumn(new SortColumn(Column.getColumn("Module", "MODULEORDER"), true));
        sq.addSortColumn(new SortColumn(Column.getColumn("Service", "ORDER_ID"), true));
        final DataObject services = DataAccess.get(sq);
        final Iterator it = services.getRows("Service");
        ServiceStarter.sizeOfService = services.size("Service");
        while (it.hasNext()) {
            final Row serviceRow = it.next();
            final String serviceName = (String)serviceRow.get(4);
            final DataObject serviceDO = services.getDataObject(services.getTableNames(), serviceRow);
            Object service = null;
            try {
                service = Class.forName((String)serviceRow.get(5)).newInstance();
            }
            catch (final Exception e) {
                ServiceStarter.serviceList.clear();
                ServiceStarter.LOG.log(Level.SEVERE, "Exception occured while creating Service", e);
                throw new Exception(e);
            }
            ServiceStarter.serviceMap.put(serviceName, service);
            ServiceStarter.serviceDOList.add(serviceDO);
            ServiceStarter.serviceList.add(serviceName);
        }
    }
    
    private static void initSafeServices() throws Exception {
        ConsoleOut.println("");
        ConsoleOut.println("Creating WebService in SafeMode");
        (ServiceStarter.webService = (Service)Class.forName("com.adventnet.mfw.service.WebService").newInstance()).create(null);
        print("WebService", " CREATED ");
        ConsoleOut.println("");
        ConsoleOut.println("Starting WebService in SafeMode");
        ServiceStarter.webService.start();
        print("WebService", " STARTED ");
    }
    
    public static void initServices() throws Exception {
        if (Starter.isSafeStart()) {
            initSafeServices();
            return;
        }
        if (!PersistenceInitializer.onSAS()) {
            fetchServicesFromDB();
        }
        try {
            ConsoleOut.println("");
            ConsoleOut.println("Creating Services");
            ListIterator li = ServiceStarter.serviceList.listIterator(0);
            final Iterator serviceDOIterator = ServiceStarter.serviceDOList.iterator();
            int progressRate = 25 / ServiceStarter.sizeOfService;
            int progress = 60;
            while (li.hasNext()) {
                final String serviceName = li.next();
                final Service service = ServiceStarter.serviceMap.get(serviceName);
                try {
                    final DataObject serviceDO = serviceDOIterator.next();
                    service.create(serviceDO);
                    if (!Boolean.valueOf(PersistenceInitializer.getConfigurationValue("onSAS"))) {
                        ((MEServer)Starter.getNewServerClassInstanceForWar()).setSplashMessage("Initializing the " + serviceName + " ...", progress);
                    }
                    ServiceStarter.createdServices.add(serviceName);
                    ServiceStarter.LOG.log(Level.FINEST, "DataObject passed to create () is {0}", serviceDO);
                    print(serviceName, " CREATED ");
                }
                catch (final Exception e) {
                    print(serviceName, " FAILED ");
                    li.remove();
                    throw e;
                }
                progress += progressRate;
            }
            ConsoleOut.println("");
            ConsoleOut.println("Starting Services");
            li = ServiceStarter.serviceList.listIterator(0);
            progressRate = 15 / ServiceStarter.sizeOfService;
            while (li.hasNext()) {
                final String serviceName = li.next();
                final Service service = ServiceStarter.serviceMap.get(serviceName);
                try {
                    if (!(boolean)PersistenceInitializer.onSAS()) {
                        ((MEServer)Starter.getNewServerClassInstanceForWar()).setSplashMessage("Starting the " + serviceName + " ...", progress);
                    }
                    service.start();
                    print(serviceName, " STARTED ");
                }
                catch (final Exception e) {
                    print(serviceName, " FAILED ");
                    li.remove();
                    throw e;
                }
                ServiceStarter.startedServices.add(serviceName);
                progress += progressRate;
            }
        }
        finally {
            System.out.println("initServices :: createdServices :: " + ServiceStarter.createdServices);
            System.out.println("initServices :: startedServices :: " + ServiceStarter.startedServices);
        }
    }
    
    private static boolean destroySafeServices() {
        ConsoleOut.println("");
        ConsoleOut.println("Stopping WebService in SafeMode");
        boolean serviceStopped = true;
        try {
            ServiceStarter.webService.stop();
            print("WebService", " STOPPED ");
        }
        catch (final Exception e) {
            e.printStackTrace();
            serviceStopped = false;
        }
        ConsoleOut.println("");
        ConsoleOut.println("Destroying WebService in SafeMode");
        try {
            ServiceStarter.webService.destroy();
            print("WebService", "DESTROYED");
        }
        catch (final Exception e) {
            e.printStackTrace();
            serviceStopped = false;
        }
        return serviceStopped;
    }
    
    public static boolean destroyServices() {
        if (Starter.isSafeStart()) {
            return destroySafeServices();
        }
        boolean serviceStopped = true;
        ListIterator li = ServiceStarter.startedServices.listIterator(ServiceStarter.startedServices.size());
        final boolean hasServices = li.hasPrevious();
        if (hasServices) {
            ConsoleOut.println("Stopping Services");
        }
        while (li.hasPrevious()) {
            final String serviceName = li.previous();
            final Service service = ServiceStarter.serviceMap.get(serviceName);
            try {
                service.stop();
            }
            catch (final Exception e) {
                e.printStackTrace();
                serviceStopped = false;
                continue;
            }
            print(serviceName, " STOPPED ");
        }
        if (hasServices) {
            ConsoleOut.println("Destroying Services");
        }
        li = ServiceStarter.createdServices.listIterator(ServiceStarter.createdServices.size());
        while (li.hasPrevious()) {
            final String serviceName = li.previous();
            final Service service = ServiceStarter.serviceMap.get(serviceName);
            try {
                li.remove();
                service.destroy();
            }
            catch (final Exception e) {
                e.printStackTrace();
                serviceStopped = false;
                continue;
            }
            print(serviceName, "DESTROYED");
        }
        return serviceStopped;
    }
    
    private static void print(final String serviceName, final String status) {
        ConsoleOut.print(serviceName);
        for (int i = serviceName.length(); i < 50; ++i) {
            ConsoleOut.print(" ");
        }
        ConsoleOut.println("[" + status + "]");
    }
    
    public static void main(final String[] args) throws Exception {
        System.out.println("::");
        final ServiceStarter ss = new ServiceStarter();
        initServices();
        destroyServices();
    }
    
    static {
        LOG = Logger.getLogger(ServiceStarter.class.getName());
        ServiceStarter.serviceList = new ArrayList();
        ServiceStarter.serviceMap = new HashMap();
        ServiceStarter.serviceDOList = new ArrayList();
        ServiceStarter.createdServices = new ArrayList();
        ServiceStarter.startedServices = new ArrayList();
        ServiceStarter.sizeOfService = 7;
        ServiceStarter.webService = null;
    }
}
