package com.adventnet.customview;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import java.util.Iterator;
import java.util.Collection;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.HashMap;
import com.adventnet.persistence.DataObject;
import com.adventnet.mfw.bean.BeanUtil;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.customview.service.ServiceProvider;
import java.util.logging.Logger;
import com.adventnet.persistence.Persistence;
import com.adventnet.mfw.bean.Initializable;
import java.io.Serializable;

public class CustomViewManagerImpl implements Serializable, CustomViewManager, Initializable
{
    private static int instanceIdCounter;
    private int instanceId;
    private static byte[] lockObj;
    Persistence persistence;
    private static final Logger OUT;
    protected ServiceProvider serviceProviderToInvoke;
    protected CustomViewManagerContext customViewManagerContext;
    protected List spList;
    
    public CustomViewManagerImpl() {
        this.serviceProviderToInvoke = null;
        this.customViewManagerContext = null;
        this.spList = new ArrayList();
        synchronized (CustomViewManagerImpl.lockObj) {
            this.instanceId = CustomViewManagerImpl.instanceIdCounter++;
        }
        try {
            this.persistence = (Persistence)BeanUtil.lookup("Persistence");
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public void initialize(final DataObject dobj) throws CustomViewException {
        try {
            final Iterator properties = dobj.get("BeanProperties", "PROPERTY");
            final Iterator values = dobj.get("BeanProperties", "VALUE");
            final HashMap propertyMap = new HashMap();
            while (properties.hasNext() && values.hasNext()) {
                propertyMap.put(properties.next(), values.next());
            }
            final String customViewType = propertyMap.get("CustomViewType");
            final int communicationMode = Integer.parseInt(propertyMap.get("CommunicationMode"));
            CustomViewManagerImpl.OUT.log(Level.FINER, " CVMgr[{0}] Getting cvSPs....", new Integer(this.instanceId));
            final SelectQuery sqForGet = getSelectQueryForCVSPs(customViewType, new Integer(communicationMode));
            CustomViewManagerImpl.OUT.log(Level.FINER, " sqForGet : {0}", sqForGet);
            final DataObject cvServiceProvidersDO = this.persistence.get(sqForGet);
            CustomViewManagerImpl.OUT.log(Level.FINER, " cvServiceProvidersDO : {0}", cvServiceProvidersDO);
            final String coreClientService = getCoreServiceProvider(cvServiceProvidersDO, "CLIENT");
            final String coreServerService = getCoreServiceProvider(cvServiceProvidersDO, "SERVER");
            CustomViewManagerImpl.OUT.log(Level.FINER, " CORECLIENTSERVICE :{0}", coreClientService);
            CustomViewManagerImpl.OUT.log(Level.FINER, " CORESERVERSERVICE :{0}", coreServerService);
            final ServiceProvider serverSP = (ServiceProvider)Thread.currentThread().getContextClassLoader().loadClass(coreServerService).newInstance();
            String[] serverServiceProviders = null;
            String[] clientServiceProviders = null;
            final List clientList = getInterceptors(cvServiceProvidersDO, "Client");
            final List serverList = getInterceptors(cvServiceProvidersDO, "Server");
            CustomViewManagerImpl.OUT.log(Level.FINER, " ClientServiceProviders  :{0}", clientList);
            CustomViewManagerImpl.OUT.log(Level.FINER, " ServerServiceProviders  :{0}", serverList);
            CustomViewManagerImpl.OUT.log(Level.FINER, " CVMgr[{0}] DONE with getting cvSPs.", new Integer(this.instanceId));
            if (serverServiceProviders != null) {
                serverList.addAll(Arrays.asList(serverServiceProviders));
            }
            if (clientServiceProviders != null) {
                clientList.addAll(Arrays.asList(clientServiceProviders));
            }
            clientList.add(coreClientService);
            CustomViewManagerImpl.OUT.log(Level.FINER, " CVMgr[{0}] serverList : {1}", new Object[] { new Integer(this.instanceId), serverList });
            CustomViewManagerImpl.OUT.log(Level.FINER, " CVMgr[{0}] clientList : {1}", new Object[] { new Integer(this.instanceId), clientList });
            serverServiceProviders = serverList.toArray(new String[serverList.size()]);
            clientServiceProviders = clientList.toArray(new String[clientList.size()]);
            this.customViewManagerContext = new CustomViewManagerContext(customViewType, communicationMode, this.instanceId, clientServiceProviders);
            this.serviceProviderToInvoke = this.getServiceProviderToInvoke(serverServiceProviders, serverSP, this.customViewManagerContext);
        }
        catch (final Exception e) {
            throw new CustomViewException(e);
        }
    }
    
    private static SelectQuery getSelectQueryForCVSPs(final String cvType, final Integer mode) {
        final SelectQuery sqForGet = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomViewServiceProviders"));
        Criteria cvTypeCriteria = new Criteria(Column.getColumn("CustomViewServiceProviders", "CVTYPE"), (Object)cvType, 0, false);
        sqForGet.addSelectColumn(Column.getColumn("CustomViewServiceProviders", "*"));
        if (mode != null) {
            Criteria cvModeCriteria = new Criteria(Column.getColumn("ClientServiceProviders", "COMM_MODE"), (Object)mode, 0);
            Criteria nullCriteria = new Criteria(Column.getColumn("ClientServiceProviders", "COMM_MODE"), (Object)null, 0);
            cvModeCriteria = cvModeCriteria.or(nullCriteria);
            cvTypeCriteria = cvTypeCriteria.and(cvModeCriteria);
            cvModeCriteria = new Criteria(Column.getColumn("ServerServiceProviders", "COMM_MODE"), (Object)mode, 0);
            nullCriteria = new Criteria(Column.getColumn("ServerServiceProviders", "COMM_MODE"), (Object)null, 0);
            cvModeCriteria = cvModeCriteria.or(nullCriteria);
            cvTypeCriteria = cvTypeCriteria.and(cvModeCriteria);
        }
        final String[] cvspToSP = { "CVTYPEID" };
        sqForGet.addJoin(new Join("CustomViewServiceProviders", "ClientServiceProviders", cvspToSP, cvspToSP, 1));
        sqForGet.addSelectColumn(Column.getColumn("ClientServiceProviders", "*"));
        sqForGet.addSortColumn(new SortColumn("ClientServiceProviders", "SPINDEX", true));
        sqForGet.addJoin(new Join("CustomViewServiceProviders", "ServerServiceProviders", cvspToSP, cvspToSP, 1));
        sqForGet.addSelectColumn(Column.getColumn("ServerServiceProviders", "*"));
        sqForGet.addSortColumn(new SortColumn("ServerServiceProviders", "SPINDEX", true));
        sqForGet.setCriteria(cvTypeCriteria);
        return sqForGet;
    }
    
    private static List getInterceptors(final DataObject cvServiceProvidersDO, final String vm) throws DataAccessException {
        CustomViewManagerImpl.OUT.log(Level.FINER, "\n\n getInterceptors for {0}", vm);
        final Iterator rowIterator = cvServiceProvidersDO.getRows(vm + "ServiceProviders");
        final List interceptors = new ArrayList();
        while (rowIterator.hasNext()) {
            final Row interceptorsRow = rowIterator.next();
            interceptors.add(interceptorsRow.get("SERVICEPROVIDER"));
        }
        return interceptors;
    }
    
    private static String getCoreServiceProvider(final DataObject cvServiceProvidersDO, final String vm) throws DataAccessException {
        CustomViewManagerImpl.OUT.log(Level.FINER, "\n\n getCoreServiceProvider for {0}", vm);
        final String colName = "CORE" + vm + "SERVICE";
        final Row cvSPRow = cvServiceProvidersDO.getFirstRow("CustomViewServiceProviders");
        return (String)cvSPRow.get(colName);
    }
    
    private ServiceProvider getServiceProviderToInvoke(final String[] serviceProviders, ServiceProvider lastServiceProvider, final CustomViewManagerContext cvmContext) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        lastServiceProvider.setCustomViewManagerContext(cvmContext);
        this.spList.add(lastServiceProvider);
        final int size = (serviceProviders != null) ? serviceProviders.length : 0;
        for (int i = size - 1; i > -1; --i) {
            CustomViewManagerImpl.OUT.log(Level.FINER, " CVMgr[{0}] serviceProviders.get({1})  : {2}", new Object[] { new Integer(this.instanceId), new Integer(i), serviceProviders[i] });
            final ServiceProvider sp = (ServiceProvider)Thread.currentThread().getContextClassLoader().loadClass(serviceProviders[i]).newInstance();
            this.spList.add(sp);
            sp.setCustomViewManagerContext(cvmContext);
            sp.setNextServiceProvider(lastServiceProvider);
            lastServiceProvider = sp;
        }
        return lastServiceProvider;
    }
    
    @Override
    public ViewData getData(final CustomViewRequest customViewRequest) throws CustomViewException {
        try {
            CustomViewManagerImpl.OUT.log(Level.FINER, " CVMImpl : Inside getData({0})", customViewRequest);
            final SelectQuery sq = customViewRequest.getSelectQuery();
            if (sq == null) {
                CustomViewManagerImpl.OUT.finer(" CVMImpl : SelectQuery is null..checking out configuration");
                final List listOfPers = new ArrayList();
                listOfPers.add("CustomViewConfiguration");
                listOfPers.add("SelectQuery");
                final List listOfDeepRetPers = listOfPers;
                final Criteria cvCondition = new Criteria(Column.getColumn("CustomViewConfiguration", "CVNAME"), (Object)customViewRequest.getCustomViewConfigurationName(), 0);
                final DataObject customViewConfigurationDO = this.persistence.get(listOfPers, listOfDeepRetPers, cvCondition);
                if (customViewConfigurationDO.isEmpty()) {
                    throw new CustomViewException("No CustomViewConfiguration found for the given CVNAME : " + customViewRequest.getCustomViewConfigurationName());
                }
                customViewRequest.setCustomViewConfiguration(customViewConfigurationDO);
            }
            CustomViewManagerImpl.OUT.log(Level.FINER, " CVMImpl : Invoking {0} with cvRequest : {1}", new Object[] { this.serviceProviderToInvoke, customViewRequest });
            final ViewData toReturn = this.serviceProviderToInvoke.process(customViewRequest);
            CustomViewManagerImpl.OUT.log(Level.FINER, " CVMImpl : returning viewData :{0}", toReturn);
            return toReturn;
        }
        catch (final Exception e) {
            CustomViewManagerImpl.OUT.throwing("CustomViewManagerImpl", "getData", e);
            throw new CustomViewException(e);
        }
    }
    
    @Override
    public CustomViewManagerContext getCustomViewManagerContext() {
        return this.customViewManagerContext;
    }
    
    static {
        CustomViewManagerImpl.instanceIdCounter = 0;
        CustomViewManagerImpl.lockObj = new byte[0];
        OUT = Logger.getLogger(CustomViewManagerImpl.class.getName());
    }
}
