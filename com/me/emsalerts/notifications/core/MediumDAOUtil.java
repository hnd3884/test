package com.me.emsalerts.notifications.core;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

public class MediumDAOUtil
{
    private static Logger logger;
    
    public void populateMediumData(final Long templateID, final LinkedHashMap mediumObj) {
        try {
            final Long mediumID = Long.valueOf(String.valueOf(mediumObj.get("mediumID")));
            final Criteria templateIdCrit = new Criteria(Column.getColumn("TemplateToMediumRel", "TEMPLATE_ID"), (Object)templateID, 0);
            final Criteria mediumIdCrit = new Criteria(Column.getColumn("TemplateToMediumRel", "MEDIUM_ID"), (Object)mediumID, 0);
            final DataObject mediumDO = DataAccess.get("TemplateToMediumRel", templateIdCrit.and(mediumIdCrit));
            if (mediumDO.isEmpty()) {
                final DataObject newMediumDO = (DataObject)new WritableDataObject();
                Row mediumRow = new Row("TemplateToMediumRel");
                mediumRow = this.constructSubscriptionRow(templateID, mediumObj, mediumRow);
                newMediumDO.addRow(mediumRow);
                SyMUtil.getPersistence().add(newMediumDO);
            }
            else {
                Row mediumRow2 = mediumDO.getRow("TemplateToMediumRel");
                mediumRow2 = this.constructSubscriptionRow(templateID, mediumObj, mediumRow2);
                mediumDO.updateRow(mediumRow2);
                SyMUtil.getPersistence().update(mediumDO);
            }
        }
        catch (final Exception e) {
            MediumDAOUtil.logger.log(Level.WARNING, "Exception occured while populating medium data ", e);
        }
    }
    
    public Row constructSubscriptionRow(final Long templateID, final LinkedHashMap mediumObj, final Row mediumRow) throws JSONException {
        mediumRow.set("TEMPLATE_ID", (Object)templateID);
        mediumRow.set("MEDIUM_ID", mediumObj.get("mediumID"));
        mediumRow.set("MEDIUM_DATA", mediumObj.get("mediumData"));
        return mediumRow;
    }
    
    public DataObject mediumDataDO(final Long templateID, final Long mediumID) throws DataAccessException {
        final Criteria templateIDCrit = new Criteria(Column.getColumn("TemplateToMediumRel", "TEMPLATE_ID"), (Object)templateID, 0);
        final Criteria mediumIDCrit = new Criteria(Column.getColumn("TemplateToMediumRel", "MEDIUM_ID"), (Object)mediumID, 0);
        final Criteria queryCrit = templateIDCrit.and(mediumIDCrit);
        final DataObject mediumDO = DataAccess.get("TemplateToMediumRel", queryCrit);
        return mediumDO;
    }
    
    public DataObject getMediumDetailsForTemplates(final Long templateID) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("TemplateToMediumRel"));
        final Join mediumJoin = new Join("TemplateToMediumRel", "EventMediums", new String[] { "MEDIUM_ID" }, new String[] { "MEDIUM_ID" }, 2);
        final Criteria templateIDCrit = new Criteria(Column.getColumn("TemplateToMediumRel", "TEMPLATE_ID"), (Object)templateID, 0);
        selectQuery.addJoin(mediumJoin);
        selectQuery.setCriteria(templateIDCrit);
        selectQuery.addSelectColumn(Column.getColumn("TemplateToMediumRel", "TEMPLATE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("TemplateToMediumRel", "MEDIUM_ID"));
        selectQuery.addSelectColumn(Column.getColumn("TemplateToMediumRel", "MEDIUM_DATA"));
        selectQuery.addSelectColumn(Column.getColumn("EventMediums", "MEDIUM_ID"));
        selectQuery.addSelectColumn(Column.getColumn("EventMediums", "MEDIUM_NAME"));
        final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
        return dataObject;
    }
    
    static {
        MediumDAOUtil.logger = Logger.getLogger("EMSAlertsLogger");
    }
}
