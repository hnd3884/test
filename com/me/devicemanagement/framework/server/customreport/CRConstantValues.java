package com.me.devicemanagement.framework.server.customreport;

import com.adventnet.i18n.I18N;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CRConstantValues
{
    public static final String[] DATEOPERATORLIST;
    public static final String[] DATEOPERATORLISTCR;
    public static final String[] CHATOPERATORLIST;
    public static final String[] INTOPERATORLIST;
    public static final String[] BOOLEANOPERATORLIST;
    public static final String[] BOOLEANVALUELIST;
    public static final String[] LOGICALOPERATORLIST;
    public static final String[] EQUALOPERATORLIST;
    public static final String[] I18N_OPERATOR_LIST;
    public static final String[] I18N_OPERATOR_LIST_I18N;
    public static final String[] CHATOPERATORLIST_I18N;
    public static final String[] INTOPERATORLIST_I18N;
    public static final String[] DATEOPERATORLIST_I18N;
    public static final String[] DATEOPERATORLISTCR_I18N;
    public static final String[] EQUALOPERATORLIST_I18N;
    public static final String[] PATCHDATEOPERATORLIST;
    public static final String[] SCHEDULEDATEOPERATORLIST;
    public static final String[] DCVIEWFILTERDATELIST;
    public static final String[] PATCHCHATOPERATORLIST;
    public static final String[] EQUALLIKEOPERATORLIST;
    public static final String[] NOEQUALCHARTOPERATORLIST;
    public static final String[] EQUALLIKEONLYOPERATORLIST;
    public static final String[] LIKEONLYOPERATORLIST;
    public static final String[] LOGICALOPERATORLIST_I18N;
    public static final String[] EQUALTOPERATORLIST_I18N;
    public static final String[] BOOLEANOPERATORLIST_I18N;
    public static final String[] BOOLEANVALUELIST_I18N;
    public static final String[] DCVIEWFILTERDATELIST_I18N;
    public static final String[] PATCHDATEOPERATORLIST_I18N;
    public static final String[] SCHEDULEDATEOPERATORLIST_I18N;
    public static final String[] PATCHCHATOPERATORLIST_I18N;
    public static final String[] NOEQUALCHATOPERATORLIST_I18N;
    public static final String[] EQUALLIKEOPERATORLIST_I18N;
    public static final String[] EQUALLIKEONLYOPERATORLIST_I18N;
    public static final String[] LIKEONLYOPERATORLIST_I18N;
    public static final String[] DATERESTRICTEDLIST;
    public static final String[] DATERESTRICTEDLIST_I18N;
    
    public static int getOperatorValue(String value) {
        value = value.trim();
        if (value.equalsIgnoreCase("equal") || value.equalsIgnoreCase("empty")) {
            return 0;
        }
        if (value.equalsIgnoreCase("not equal") || value.equalsIgnoreCase("not empty")) {
            return 1;
        }
        if (value.equalsIgnoreCase("greater than")) {
            return 5;
        }
        if (value.equalsIgnoreCase("greater or equal")) {
            return 4;
        }
        if (value.equalsIgnoreCase("less than")) {
            return 7;
        }
        if (value.equalsIgnoreCase("less or equal")) {
            return 6;
        }
        if (value.equalsIgnoreCase("between")) {
            return 14;
        }
        if (value.equalsIgnoreCase("like")) {
            return 2;
        }
        if (value.equalsIgnoreCase("contains")) {
            return 2;
        }
        if (value.equalsIgnoreCase("not like")) {
            return 3;
        }
        if (value.equalsIgnoreCase("starts with")) {
            return 10;
        }
        if (value.equalsIgnoreCase("ends with")) {
            return 11;
        }
        if (value.equalsIgnoreCase("INNER_JOIN")) {
            return 2;
        }
        if (value.equalsIgnoreCase("LEFT_JOIN")) {
            return 1;
        }
        if (value.trim().equalsIgnoreCase("Before")) {
            return 7;
        }
        if (value.trim().equalsIgnoreCase("After")) {
            return 5;
        }
        if (value.equalsIgnoreCase("is") || value.equalsIgnoreCase("in")) {
            return 8;
        }
        if (value.equalsIgnoreCase("not in")) {
            return 9;
        }
        return -1;
    }
    
    public static Object getSearchString(Object searchString, String operatorValue) {
        operatorValue = operatorValue.trim();
        if (operatorValue.equalsIgnoreCase("like")) {
            searchString = "*" + searchString + "*";
        }
        else if (operatorValue.equalsIgnoreCase("not like")) {
            searchString = "*" + searchString + "*";
        }
        else if (operatorValue.equalsIgnoreCase("starts with")) {
            searchString += "*";
        }
        else if (operatorValue.equalsIgnoreCase("ends with")) {
            searchString = "*" + searchString;
        }
        else if (operatorValue.equalsIgnoreCase("contains")) {
            searchString = "*" + searchString + "*";
        }
        return searchString;
    }
    
    public static String getLogicalOperator(String value) {
        value = value.trim();
        if (value.equalsIgnoreCase("AND")) {
            return " AND ".trim();
        }
        if (value.equalsIgnoreCase("OR")) {
            return " OR ".trim();
        }
        return null;
    }
    
    public String[] getI18Nlist(final String[] filterList) {
        final Logger logger = Logger.getLogger("CRConstantValues");
        logger.log(Level.FINEST, "CRConstantValues.getI18Nlist");
        final String[] i18nedList = new String[filterList.length];
        try {
            for (int i = 0; i < filterList.length; ++i) {
                i18nedList[i] = I18N.getMsg(filterList[i], new Object[0]);
            }
        }
        catch (final Exception ex) {
            logger.log(Level.WARNING, "Exception while getting I18N value for filter value  ", ex);
        }
        return i18nedList;
    }
    
    static {
        DATEOPERATORLIST = new String[] { "is", "Before", "After", "Last n Days", "Before n Days", "Next n Days" };
        DATEOPERATORLISTCR = new String[] { "is", "Before", "After", "Last n Days", "Before n Days", "Next n Days", "After n Days", "between" };
        CHATOPERATORLIST = new String[] { "equal", "not equal", "like", "not like", "starts with", "ends with", "empty", "not empty" };
        INTOPERATORLIST = new String[] { "equal", "not equal", "greater than", "greater or equal", "less than", "less or equal" };
        BOOLEANOPERATORLIST = new String[] { "is" };
        BOOLEANVALUELIST = new String[] { "true", "false" };
        LOGICALOPERATORLIST = new String[] { "AND", "OR" };
        EQUALOPERATORLIST = new String[] { "equal" };
        I18N_OPERATOR_LIST = new String[] { "equal", "not equal" };
        I18N_OPERATOR_LIST_I18N = new String[] { "dc.rep.customReport.equal", "dc.rep.customReport.not_equal" };
        CHATOPERATORLIST_I18N = new String[] { "dc.rep.customReport.equal", "dc.rep.customReport.not_equal", "dc.rep.customReport.like", "dc.rep.customReport.not_like", "dc.rep.customReport.starts_with", "dc.rep.customReport.ends_with", "dc.rep.customReport.empty", "dc.rep.customReport.not_empty" };
        INTOPERATORLIST_I18N = new String[] { "dc.rep.customReport.equal", "dc.rep.customReport.not_equal", "dc.rep.customReport.greater_than", "dc.rep.customReport.greater_or_equal", "dc.rep.customReport.less_than", "dc.rep.customReport.less_or_equal", "dc.rep.customReport.empty", "dc.rep.customReport.not_empty" };
        DATEOPERATORLIST_I18N = new String[] { "dc.rep.customReport.IS", "desktopcentral.config.fileFolder.addConfig.before", "dc.common.AFTER", "dc.common.scheduleReport.Last_N_Days", "dc.common.scheduleReport.Before_N_Days", "dc.common.scheduleReport.Next_N_Days" };
        DATEOPERATORLISTCR_I18N = new String[] { "dc.rep.customReport.IS", "desktopcentral.config.fileFolder.addConfig.before", "dc.common.AFTER", "dc.common.scheduleReport.Last_N_Days", "dc.common.scheduleReport.Before_N_Days", "dc.common.scheduleReport.Next_N_Days", "dc.rep.customReport.After_N_Days", "dc.common.BETWEEN" };
        EQUALOPERATORLIST_I18N = new String[] { "dc.rep.customReport.equal" };
        PATCHDATEOPERATORLIST = new String[] { "today", "yesterday", "this_week", "last_week", "this_month", "last_month", "current_quarter", "last_quarter", "custom" };
        SCHEDULEDATEOPERATORLIST = new String[] { "today", "yesterday", "this_week", "last_week", "this_month", "last_month", "current_quarter", "last_quarter" };
        DCVIEWFILTERDATELIST = new String[] { "is", "before", "after", "between", "last_n_days", "before_n_days", "next_n_days" };
        PATCHCHATOPERATORLIST = new String[] { "equal", "not equal", "like", "not like", "starts with", "ends with" };
        EQUALLIKEOPERATORLIST = new String[] { "equal", "not equal", "like", "not like" };
        NOEQUALCHARTOPERATORLIST = new String[] { "like", "not like", "starts with", "ends with" };
        EQUALLIKEONLYOPERATORLIST = new String[] { "equal", "like" };
        LIKEONLYOPERATORLIST = new String[] { "like" };
        LOGICALOPERATORLIST_I18N = new String[] { "dc.common.AND", "dc.rep.customReport.OR" };
        EQUALTOPERATORLIST_I18N = new String[] { "dc.rep.customReport.equal" };
        BOOLEANOPERATORLIST_I18N = new String[] { "dc.rep.customReport.IS" };
        BOOLEANVALUELIST_I18N = new String[] { "dc.conf.usrmgm_det.true", "dc.conf.usrmgm_det.false" };
        DCVIEWFILTERDATELIST_I18N = new String[] { "dc.rep.customReport.IS", "dc.rep.customReport.before", "dc.rep.customReport.after", "dc.xsl.adreports.between", "dc.common.viewFilter.Last_N_Days", "dc.common.scheduleReport.Before_N_Days", "dc.common.scheduleReport.Next_N_Days" };
        PATCHDATEOPERATORLIST_I18N = new String[] { "dc.calendar.today", "dc.calendar.yesterday", "desktopcentral.common.ThisWeek", "desktopcentral.common.LastWeek", "desktopcentral.common.ThisMonth", "desktopcentral.common.LastMonth", "desktopcentral.common.ThisQuarter", "desktopcentral.common.LastQuarter", "dc.mdm.reports.custom" };
        SCHEDULEDATEOPERATORLIST_I18N = new String[] { "dc.calendar.today", "dc.calendar.yesterday", "desktopcentral.common.ThisWeek", "desktopcentral.common.LastWeek", "desktopcentral.common.ThisMonth", "desktopcentral.common.LastMonth", "desktopcentral.common.ThisQuarter", "desktopcentral.common.LastQuarter" };
        PATCHCHATOPERATORLIST_I18N = new String[] { "dc.rep.customReport.equal", "dc.rep.customReport.not_equal", "dc.rep.customReport.like", "dc.rep.customReport.not_like", "dc.rep.customReport.starts_with", "dc.rep.customReport.ends_with" };
        NOEQUALCHATOPERATORLIST_I18N = new String[] { "dc.rep.customReport.like", "dc.rep.customReport.not_like", "dc.rep.customReport.starts_with", "dc.rep.customReport.ends_with" };
        EQUALLIKEOPERATORLIST_I18N = new String[] { "dc.rep.customReport.equal", "dc.rep.customReport.not_equal", "dc.rep.customReport.like", "dc.rep.customReport.not_like" };
        EQUALLIKEONLYOPERATORLIST_I18N = new String[] { "dc.rep.customReport.equal", "dc.rep.customReport.like" };
        LIKEONLYOPERATORLIST_I18N = new String[] { "dc.rep.customReport.like" };
        DATERESTRICTEDLIST = new String[] { "is", "last_n_days" };
        DATERESTRICTEDLIST_I18N = new String[] { "dc.rep.customReport.IS", "dc.common.viewFilter.Last_N_Days" };
    }
}
