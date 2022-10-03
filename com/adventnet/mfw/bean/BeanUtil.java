package com.adventnet.mfw.bean;

import java.util.concurrent.ConcurrentHashMap;
import com.zoho.conf.AppResources;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.logging.Level;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.persistence.QueryConstructor;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.AccessControl;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class BeanUtil
{
    private static final Logger OUT;
    private static Map beanNameToObject;
    private static HashMap beanNameToDO;
    private static volatile AccessControl accessControl;
    
    public static void addBeans(final DataObject beansDO) throws Exception {
        final Iterator beanIterator = beansDO.getRows("Bean");
        final List tableNames = beansDO.getTableNames();
        tableNames.remove("ConfFile");
        tableNames.remove("UVHValues");
        while (beanIterator.hasNext()) {
            final Row beanRow = beanIterator.next();
            final DataObject beanDO = beansDO.getDataObject(tableNames, beanRow);
            final String beanName = (String)beanRow.get(3);
            BeanUtil.beanNameToDO.put(beanName, beanDO);
        }
    }
    
    public static Object lookup(final String beanName) throws Exception {
        return lookup(beanName, null, 0);
    }
    
    public static Object lookup(final String beanName, final Object dist) throws Exception {
        return lookup(beanName, dist, 0);
    }
    
    public static Object lookup(final String beanName, final Object dist, final int operation) throws Exception {
        if (dist == null && BeanUtil.beanNameToObject.containsKey(beanName)) {
            return BeanUtil.beanNameToObject.get(beanName);
        }
        DataObject beanDO = BeanUtil.beanNameToDO.get(beanName);
        if (beanDO == null) {
            final List personalityList = new ArrayList();
            personalityList.add("Bean");
            final Criteria ct = new Criteria(Column.getColumn("Bean", "BEAN_NAME"), (Object)beanName, 0);
            final SelectQuery beanSQ = QueryConstructor.getForPersonalities(personalityList, personalityList, ct);
            beanSQ.addSortColumn(new SortColumn(Column.getColumn("BeanInterceptor", "ORDER_NO"), true), 0);
            beanDO = DataAccess.get(beanSQ);
            if (beanDO.isEmpty()) {
                throw new RuntimeException("Bean Not found");
            }
        }
        return getBeanProxy(beanName, beanDO, dist, operation);
    }
    
    private static Object getBeanProxy(final String beanName, final DataObject beanDO, final Object dist, final int operation) throws Exception {
        BeanUtil.OUT.log(Level.FINEST, "Bean and Interceptor DataObject for {0} is {1}", new Object[] { beanName, beanDO });
        final String beanClassName = (String)beanDO.getFirstValue("Bean", 4);
        final int txType = (int)beanDO.getFirstValue("Bean", 5);
        final Class beanClass = Class.forName(beanClassName);
        Object beanObject = beanClass.newInstance();
        if (beanObject instanceof Initializable) {
            final Initializable beanObj = (Initializable)beanObject;
            beanObj.initialize(beanDO);
            beanObject = beanObj;
        }
        final ClassLoader cl = beanObject.getClass().getClassLoader();
        final Class[] interfaces = beanObject.getClass().getInterfaces();
        Object beanProxy = null;
        if (txType == 0) {
            beanProxy = beanObject;
        }
        else {
            final BeanProxy bp = createBeanProxy(dist);
            bp.setParams(beanObject, txType, dist, operation);
            beanProxy = Proxy.newProxyInstance(cl, interfaces, bp);
        }
        if (dist == null) {
            BeanUtil.beanNameToObject.put(beanName, beanProxy);
        }
        return beanProxy;
    }
    
    private static BeanProxy createBeanProxy(final Object dist) throws Exception {
        if (dist == null) {
            return new BeanProxy();
        }
        final String className = AppResources.getString(BeanProxy.class.getName(), "com.adventnet.db.segmentation.BeanProxy");
        return (BeanProxy)Class.forName(className).newInstance();
    }
    
    public static AccessControl getAccessControl() throws Exception {
        if (BeanUtil.accessControl != null) {
            return BeanUtil.accessControl;
        }
        final String className = AppResources.getString(AccessControl.class.getName(), DefaultAccessControl.class.getName());
        synchronized (BeanUtil.class) {
            if (BeanUtil.accessControl == null) {
                BeanUtil.accessControl = (AccessControl)Class.forName(className).newInstance();
            }
        }
        return BeanUtil.accessControl;
    }
    
    static {
        OUT = Logger.getLogger(BeanUtil.class.getName());
        BeanUtil.beanNameToObject = new ConcurrentHashMap(300);
        BeanUtil.beanNameToDO = new HashMap();
        BeanUtil.accessControl = null;
    }
    
    public static class DefaultAccessControl implements AccessControl
    {
        public boolean isResourceAllowed(final long curUserId, final long destId, final Object resourceId, final int operation) {
            return true;
        }
    }
}
