package com.adventnet.sym.server.mdm.certificates.templates.digicert;

import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.certificates.templates.MdmCertTemplate;
import java.util.logging.Logger;

public class DigicertTemplateDbHandler
{
    public static Logger logger;
    
    public static void addOrUpdateMdmTemplateToCertificateOid(final MdmCertTemplate certTemplate, final long templateId) throws DataAccessException {
        DigicertTemplateDbHandler.logger.log(Level.INFO, "Adding Digicert specific details: {0}", new Object[] { certTemplate });
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdmTemplateToCertificateOID"));
        selectQuery.addSelectColumn(new Column("MdmTemplateToCertificateOID", "*"));
        selectQuery.setCriteria(getTemplateIDCriteria(templateId));
        final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
        if (dataObject.isEmpty()) {
            final Row row = new Row("MdmTemplateToCertificateOID");
            row.set("TEMPLATE_ID", (Object)templateId);
            row.set("CERTIFICATE_OID", (Object)((DigicertCertTemplate)certTemplate).certificateOID);
            dataObject.addRow(row);
            SyMUtil.getPersistence().add(dataObject);
            DigicertTemplateDbHandler.logger.log(Level.INFO, "Template added");
        }
        else {
            final Row row = dataObject.getFirstRow("MdmTemplateToCertificateOID");
            row.set("CERTIFICATE_OID", (Object)((DigicertCertTemplate)certTemplate).certificateOID);
            dataObject.updateRow(row);
            SyMUtil.getPersistence().update(dataObject);
            DigicertTemplateDbHandler.logger.log(Level.INFO, "Template updated");
        }
    }
    
    public static void getTemplateToCertificateOIDSelectQuery(final SelectQuery selectQuery) {
        final Join join = new Join("MdmCertificateTemplate", "MdmTemplateToCertificateOID", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2);
        selectQuery.addSelectColumn(new Column("MdmCertificateTemplate", "*"));
        selectQuery.addSelectColumn(new Column("MdmTemplateToCertificateOID", "*"));
        selectQuery.addJoin(join);
    }
    
    public static Criteria getTemplateIDCriteria(final Long templateId) {
        return new Criteria(new Column("MdmTemplateToCertificateOID", "TEMPLATE_ID"), (Object)templateId, 0);
    }
    
    static {
        DigicertTemplateDbHandler.logger = Logger.getLogger("MdmCertificateIntegLogger");
    }
}
