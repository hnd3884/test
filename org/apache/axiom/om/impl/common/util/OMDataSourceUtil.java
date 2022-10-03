package org.apache.axiom.om.impl.common.util;

import org.apache.axiom.om.OMDataSourceExt;
import org.apache.axiom.om.ds.AbstractPushOMDataSource;
import org.apache.axiom.om.ds.AbstractPullOMDataSource;
import org.apache.axiom.om.OMDataSource;

public final class OMDataSourceUtil
{
    private OMDataSourceUtil() {
    }
    
    public static boolean isPullDataSource(final OMDataSource dataSource) {
        return dataSource instanceof AbstractPullOMDataSource;
    }
    
    public static boolean isPushDataSource(final OMDataSource dataSource) {
        return dataSource instanceof AbstractPushOMDataSource || dataSource.getClass().getName().equals("org.apache.axis2.jaxws.message.databinding.impl.JAXBBlockImpl");
    }
    
    public static boolean isDestructiveWrite(final OMDataSource dataSource) {
        return !(dataSource instanceof OMDataSourceExt) || ((OMDataSourceExt)dataSource).isDestructiveWrite();
    }
    
    public static boolean isDestructiveRead(final OMDataSource dataSource) {
        return dataSource instanceof OMDataSourceExt && ((OMDataSourceExt)dataSource).isDestructiveRead();
    }
}
