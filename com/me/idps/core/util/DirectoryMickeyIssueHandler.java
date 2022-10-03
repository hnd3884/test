package com.me.idps.core.util;

import com.adventnet.ds.query.Column;
import java.util.List;

public class DirectoryMickeyIssueHandler
{
    private static DirectoryMickeyIssueHandler directoryMickeyIssueHandler;
    
    public static DirectoryMickeyIssueHandler getInstance() {
        if (DirectoryMickeyIssueHandler.directoryMickeyIssueHandler == null) {
            DirectoryMickeyIssueHandler.directoryMickeyIssueHandler = new DirectoryMickeyIssueHandler();
        }
        return DirectoryMickeyIssueHandler.directoryMickeyIssueHandler;
    }
    
    public String rectifyQuery(String selectQuery) {
        selectQuery = selectQuery.replace("innerDT.innerDNB", "innerDT.\"innerDNB\"");
        selectQuery = selectQuery.replace("innerDT.innerCust", "innerDT.\"innerCust\"");
        selectQuery = selectQuery.replace("innerDT.innerEmail", "innerDT.\"innerEmail\"");
        selectQuery = selectQuery.replace("innerDT.innerDomain", "innerDT.\"innerDomain\"");
        selectQuery = selectQuery.replace("outerDT.max_RES_ID", "outerDT.\"max_RES_ID\"");
        selectQuery = selectQuery.replace("innerDT.dmCustIDcol", "innerDT.\"dmCustIDcol\"");
        selectQuery = selectQuery.replace("dt11.query1_1_DEV_COUNT", "dt11.\"query1_1_DEV_COUNT\"");
        selectQuery = selectQuery.replace("cgMemberRelDT.cgMemberRelGcol", "cgMemberRelDT.\"cgMemberRelGcol\"");
        selectQuery = selectQuery.replace("cgMemberRelDT.cgMemberRelMcol", "cgMemberRelDT.\"cgMemberRelMcol\"");
        selectQuery = selectQuery.replace("innerDT.DOMAIN_SYNC_FAILED_COUNT", "innerDT.\"DOMAIN_SYNC_FAILED_COUNT\"");
        selectQuery = selectQuery.replace("dt22.query2_2_RESOURCE_RESOURCE_ID", "dt22.\"query2_2_RESOURCE_RESOURCE_ID\"");
        selectQuery = selectQuery.replace("outerDT.MAX_DIROBJTEMP_MAX_ADDED_AT", "outerDT.\"MAX_DIROBJTEMP_MAX_ADDED_AT\"");
        selectQuery = selectQuery.replace("innerTagDocDT.DOC_TAG_COUNT", "innerTagDocDT.\"DOC_TAG_COUNT\"");
        selectQuery = selectQuery.replace("innerUserDocDT.DOC_USR_COUNT", "innerUserDocDT.\"DOC_USR_COUNT\"");
        selectQuery = selectQuery.replace("innerGroupDocDT.DOC_GRP_COUNT", "innerGroupDocDT.\"DOC_GRP_COUNT\"");
        selectQuery = selectQuery.replace("innerDeviceDocDT.DOC_DVC_COUNT", "innerDeviceDocDT.\"DOC_DVC_COUNT\"");
        selectQuery = selectQuery.replace("innerPolicyDocDT.POLICY_DOC_COUNT", "innerPolicyDocDT.\"POLICY_DOC_COUNT\"");
        selectQuery = selectQuery.replace("innerTagDocDT.TAG_DOC_ID", "innerTagDocDT.\"TAG_DOC_ID\"");
        selectQuery = selectQuery.replace("innerUserDocDT.USR_DOC_ID", "innerUserDocDT.\"USR_DOC_ID\"");
        selectQuery = selectQuery.replace("innerGroupDocDT.GRP_DOC_ID", "innerGroupDocDT.\"GRP_DOC_ID\"");
        selectQuery = selectQuery.replace("innerDeviceDocDT.DVC_DOC_ID", "innerDeviceDocDT.\"DVC_DOC_ID\"");
        selectQuery = selectQuery.replace("dt12.rank_two", "dt12.\"rank_two\"");
        selectQuery = selectQuery.replace("dt11.rank_one", "dt11.\"rank_one\"");
        selectQuery = selectQuery.replace("dt12.dir_grp_name", "dt12.\"dir_grp_name\"");
        selectQuery = selectQuery.replace("dt11.res_grp_name", "dt11.\"res_grp_name\"");
        selectQuery = selectQuery.replace("dt2.resIdTobeMapped", "dt2.\"resIdTobeMapped\"");
        selectQuery = selectQuery.replace("dt2.resIdTimeStampCol", "dt2.\"resIdTimeStampCol\"");
        selectQuery = selectQuery.replace("dt1.res_id", "dt1.\"res_id\"");
        selectQuery = selectQuery.replace("dt1.rank_one", "dt1.\"rank_one\"");
        selectQuery = selectQuery.replace("dt1.modifiedAt", "dt1.\"modifiedAt\"");
        return selectQuery;
    }
    
    public String rectifyDT(String updateSql, final List<Column> selectColumns) {
        for (int i = 0; i < selectColumns.size(); ++i) {
            final String selColAlias = selectColumns.get(i).getColumnAlias();
            updateSql = updateSql.replace("=opInnerTable." + selColAlias, "=opInnerTable.\"" + selColAlias + "\"");
        }
        return updateSql;
    }
    
    static {
        DirectoryMickeyIssueHandler.directoryMickeyIssueHandler = null;
    }
}
