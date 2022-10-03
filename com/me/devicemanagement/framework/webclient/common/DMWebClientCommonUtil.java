package com.me.devicemanagement.framework.webclient.common;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import com.me.devicemanagement.framework.server.util.Utils;
import com.adventnet.client.components.web.TransformerContext;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import com.adventnet.idioms.tablenavigator.TableNavigatorModel;
import com.adventnet.client.components.table.web.TableViewModel;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;

public class DMWebClientCommonUtil
{
    private static Logger logger;
    
    public List getColumnValues(final ViewContext viewContext, final String columnName) {
        final List columnValueList = new ArrayList();
        try {
            final TableViewModel tableViewModel = (TableViewModel)viewContext.getViewModel();
            final TableNavigatorModel tableModel = (TableNavigatorModel)tableViewModel.getTableModel();
            final int rowCount = tableModel.getRowCount();
            final int colCount = tableModel.getColumnCount();
            int flag = 0;
            int columnIndex;
            for (columnIndex = 0; columnIndex < colCount; ++columnIndex) {
                if (tableModel.getColumnName(columnIndex).equalsIgnoreCase(columnName)) {
                    flag = 1;
                    break;
                }
            }
            if (flag == 1) {
                for (int rowIndex = 0; rowIndex < rowCount; ++rowIndex) {
                    final Object value = tableModel.getValueAt(rowIndex, columnIndex);
                    columnValueList.add(value);
                }
            }
        }
        catch (final Exception e) {
            DMWebClientCommonUtil.logger.log(Level.WARNING, "exception while fetching column values: " + e);
        }
        return columnValueList;
    }
    
    public void setNextExecTimeinViewContext(final ViewContext viewContext) {
        final List scheduleList = this.getColumnValues(viewContext, "ScheduledTaskDetails.SCHEDULER_CLASS_ID");
        final HashMap nextExecTimeMap = ApiFactoryProvider.getSchedulerAPI().fetchNextExecTimeForSchedules(scheduleList);
        viewContext.getRequest().setAttribute("nextExecTimeMap", (Object)nextExecTimeMap);
    }
    
    public String getNextExecTime(final TransformerContext tableContext) {
        String time = "--";
        final Long schedulerID = (Long)tableContext.getAssociatedPropertyValue("ScheduledTaskDetails.SCHEDULER_CLASS_ID");
        final ViewContext viewContext = tableContext.getViewContext();
        final HashMap nextExecTimeList = (HashMap)viewContext.getRequest().getAttribute("nextExecTimeMap");
        final Long nextExecTime = nextExecTimeList.get(schedulerID);
        if (nextExecTime != null && nextExecTime != -1L) {
            time = Utils.getTime(nextExecTime);
        }
        return time;
    }
    
    public static String getWebappsContextPath(final HttpServletRequest request) {
        return getWebappsContextPath((ServletRequest)request);
    }
    
    public static String getWebappsContextPath(final ServletRequest request) {
        return request.getServletContext().getRealPath("/");
    }
    
    static {
        DMWebClientCommonUtil.logger = Logger.getLogger(DMWebClientCommonUtil.class.getName());
    }
}
