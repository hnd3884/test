package com.adventnet.sym.webclient.reports;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.sym.server.util.SyMUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.List;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;

public class CustomReportUtil
{
    static String className;
    static Logger out;
    
    public SelectQuery modifySelectQuery(final SelectQuery sq, final String submodule) {
        final List listofTable = sq.getTableList();
        Integer computerType = new Integer(0);
        final ArrayList computerTypeList = new ArrayList();
        if (submodule.equals("Computer")) {
            for (int i = 0; i < listofTable.size(); ++i) {
                final String tablename = listofTable.get(i) + "".trim();
                if (tablename.equals("InvBios AS InvBios")) {
                    computerType = this.getComponentType("BIOS");
                }
                else if (tablename.equals("InvDiskDrive AS InvDiskDrive")) {
                    computerType = this.getComponentType("Hard Drive");
                }
                if (computerType != 0) {
                    computerTypeList.add(computerType);
                }
            }
            if (computerTypeList.size() > 0) {
                Criteria componentCri = new Criteria(Column.getColumn("InvHW", "COMPONENT_TYPE"), (Object)computerTypeList.toArray(), 8);
                final Criteria existingCri = sq.getCriteria();
                if (existingCri != null) {
                    componentCri = componentCri.and(existingCri);
                }
                sq.setCriteria(componentCri);
            }
        }
        if (submodule.equals("Hardware")) {
            for (int i = 0; i < listofTable.size(); ++i) {
                final String tablename = listofTable.get(i) + "".trim();
                if (tablename.equals("InvPhysicalMemorySlot AS InvPhysicalMemorySlot")) {
                    Criteria componentCri2 = new Criteria(Column.getColumn("InvPhysicalMemorySlot", "MEMORY_TYPE"), (Object)5, 1);
                    final Criteria existingCri2 = sq.getCriteria();
                    if (existingCri2 != null) {
                        componentCri2 = componentCri2.and(existingCri2);
                    }
                    sq.setCriteria(componentCri2);
                }
            }
        }
        return sq;
    }
    
    private Integer getComponentType(final String componentType) {
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("InvHWComponent"));
        Integer compid = new Integer(0);
        sq.addSelectColumn(Column.getColumn((String)null, "*"));
        final Join hwjoin = new Join("InvHWComponent", "InvHW", new String[] { "HW_ID" }, new String[] { "HW_ID" }, 1);
        sq.addJoin(hwjoin);
        final Join componentjoin = new Join("InvHW", "InvComponentType", new String[] { "COMPONENT_TYPE" }, new String[] { "COMPONENT_TYPE" }, 1);
        sq.addJoin(componentjoin);
        final Criteria componentCriteria = new Criteria(Column.getColumn("InvComponentType", "COMPONENT_LABEL"), (Object)componentType, 0);
        sq.setCriteria(componentCriteria);
        try {
            final DataObject queryDO = SyMUtil.getPersistence().get(sq);
            CustomReportUtil.out.log(Level.INFO, "QueryDO is {0}", queryDO);
            if (!queryDO.isEmpty()) {
                final Row invHWRow = queryDO.getRow("InvHW");
                compid = (Integer)invHWRow.get("COMPONENT_TYPE");
            }
        }
        catch (final Exception ee) {
            CustomReportUtil.out.log(Level.WARNING, "Exception is ", ee);
        }
        if (compid != 0) {
            return compid;
        }
        return 0;
    }
    
    static {
        CustomReportUtil.className = CustomReportUtil.class.getName();
        CustomReportUtil.out = Logger.getLogger(CustomReportUtil.className);
    }
}
