package com.me.devicemanagement.framework.server.license;

import com.adventnet.persistence.Row;
import java.util.List;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.logging.Logger;

public class LicenseFactoryImpl
{
    static Logger logger;
    public static ArrayList emsProductsList;
    public static ArrayList classList;
    public static HashMap productMap;
    
    private static void setProductMap() {
        try {
            final DataObject dataObject = DataAccess.get("ProductLicenseHandler", (Criteria)null);
            final ArrayList productCodeList = (ArrayList)getColumnValuesAsList(dataObject.getRows("ProductLicenseHandler"), "EMS_PRODUCT_CODE");
            final ArrayList classnameList = (ArrayList)getColumnValuesAsList(dataObject.getRows("ProductLicenseHandler"), "CLASS_NAME");
            final Iterator iterator = productCodeList.iterator();
            final Iterator iterator2 = classnameList.iterator();
            while (iterator.hasNext()) {
                if (iterator2.hasNext()) {
                    LicenseFactoryImpl.productMap.put(iterator.next(), iterator2.next());
                }
            }
        }
        catch (final DataAccessException e) {
            LicenseFactoryImpl.logger.log(Level.SEVERE, null, (Throwable)e);
        }
    }
    
    private static List getColumnValuesAsList(final Iterator itr, final String columnName) {
        final ArrayList list = new ArrayList();
        while (itr.hasNext()) {
            final Row row = itr.next();
            final Object key = row.get(columnName);
            list.add(key);
        }
        return list;
    }
    
    public ServiceHandler getLicenseObject(final String productCode) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (LicenseFactoryImpl.productMap.containsKey(productCode)) {
            final String className = LicenseFactoryImpl.productMap.get(productCode);
            return (ServiceHandler)Class.forName(className).newInstance();
        }
        return null;
    }
    
    static {
        LicenseFactoryImpl.logger = Logger.getLogger(LicenseFactoryImpl.class.getName());
        LicenseFactoryImpl.emsProductsList = new ArrayList();
        LicenseFactoryImpl.classList = new ArrayList();
        LicenseFactoryImpl.productMap = new HashMap();
        setProductMap();
    }
}
