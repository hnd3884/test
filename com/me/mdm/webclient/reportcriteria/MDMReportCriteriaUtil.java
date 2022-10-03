package com.me.mdm.webclient.reportcriteria;

import org.json.JSONArray;
import com.me.devicemanagement.framework.server.customreport.CRConstantValues;
import com.me.devicemanagement.framework.webclient.reportcriteria.ReportCriteriaUtil;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

public class MDMReportCriteriaUtil
{
    private static Logger logger;
    
    public void getCriteriaList(final HttpServletRequest request) {
        MDMReportCriteriaUtil.logger.log(Level.FINEST, "ReportCriteriaUtil.getCriteriaList");
        String reportId = request.getParameter("reportId");
        if (reportId == null) {
            reportId = (String)request.getAttribute("reportId");
        }
        final ReportCriteriaUtil reportcriteriautil = ReportCriteriaUtil.getInstance();
        final CRConstantValues crconstantvalues = new CRConstantValues();
        final String reportName = reportcriteriautil.getReportName(reportId);
        final String returnMethod = request.getParameter("returnMethod");
        request.setAttribute("charlist", (Object)CRConstantValues.CHATOPERATORLIST);
        request.setAttribute("intlist", (Object)CRConstantValues.INTOPERATORLIST);
        request.setAttribute("i18nlist", (Object)CRConstantValues.I18N_OPERATOR_LIST);
        request.setAttribute("datelist", (Object)CRConstantValues.DATEOPERATORLIST);
        request.setAttribute("booleanOperatorList", (Object)CRConstantValues.BOOLEANOPERATORLIST);
        request.setAttribute("booleanValueList", (Object)CRConstantValues.BOOLEANVALUELIST);
        request.setAttribute("equalOnlyList", (Object)CRConstantValues.EQUALOPERATORLIST);
        final String[] charList = crconstantvalues.getI18Nlist(CRConstantValues.CHATOPERATORLIST_I18N);
        request.setAttribute("charlist_I18N", (Object)charList);
        final String[] intList = crconstantvalues.getI18Nlist(CRConstantValues.INTOPERATORLIST_I18N);
        request.setAttribute("intlist_I18N", (Object)intList);
        final String[] dateList = crconstantvalues.getI18Nlist(CRConstantValues.DATEOPERATORLIST_I18N);
        request.setAttribute("datelist_I18N", (Object)dateList);
        final String[] booleanListI18N = crconstantvalues.getI18Nlist(CRConstantValues.BOOLEANOPERATORLIST_I18N);
        request.setAttribute("booleanOperatorList_I18N", (Object)booleanListI18N);
        final String[] booleanValListI18N = crconstantvalues.getI18Nlist(CRConstantValues.BOOLEANVALUELIST_I18N);
        request.setAttribute("booleanValueList_I18N", (Object)booleanValListI18N);
        final String[] equalOnlyListI18n = crconstantvalues.getI18Nlist(CRConstantValues.EQUALOPERATORLIST_I18N);
        request.setAttribute("equalOnlyList_i18n", (Object)equalOnlyListI18n);
        final String[] i18nListI18n = crconstantvalues.getI18Nlist(CRConstantValues.I18N_OPERATOR_LIST_I18N);
        request.setAttribute("i18nList_I18n", (Object)i18nListI18n);
        JSONArray criteriaCols = new JSONArray();
        criteriaCols = reportcriteriautil.buildColumnListJson(reportId);
        request.setAttribute("criteriaCols", (Object)criteriaCols);
        request.setAttribute("reportId", (Object)reportId);
        request.setAttribute("returnMethod", (Object)returnMethod);
        request.setAttribute("reportName", (Object)reportName);
    }
    
    static {
        MDMReportCriteriaUtil.logger = Logger.getLogger("ScheduleReportLogger");
    }
}
