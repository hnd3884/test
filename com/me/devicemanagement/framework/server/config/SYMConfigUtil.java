package com.me.devicemanagement.framework.server.config;

import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import java.util.logging.Logger;

public class SYMConfigUtil
{
    protected static Logger out;
    private static String sourceClass;
    
    protected SYMConfigUtil() {
    }
    
    public static void addorUpdateCollectionMetaData(final Long collectionId, final String collectionPath, final String domainNBName) throws SyMException {
        try {
            final Row collnMetaRow = new Row("CollectionMetaData");
            collnMetaRow.set("COLLECTION_ID", (Object)collectionId);
            collnMetaRow.set("DOMAIN_NETBIOS_NAME", (Object)domainNBName);
            collnMetaRow.set("COLLECTION_FILE_PATH", (Object)collectionPath);
            final Criteria collIDCrit = new Criteria(new Column("CollectionMetaData", "COLLECTION_ID"), (Object)collectionId, 0);
            final Criteria nbCrit = new Criteria(new Column("CollectionMetaData", "DOMAIN_NETBIOS_NAME"), (Object)domainNBName, 0, false);
            final DataObject resultDO = SyMUtil.getPersistence().get("CollectionMetaData", collIDCrit.and(nbCrit));
            if (resultDO.isEmpty()) {
                resultDO.addRow(collnMetaRow);
                SyMUtil.getPersistence().add(resultDO);
            }
            else {
                resultDO.updateRow(collnMetaRow);
                SyMUtil.getPersistence().update(resultDO);
            }
        }
        catch (final Exception ex) {
            SYMConfigUtil.out.log(Level.SEVERE, "Caught exception while add/updating CollectionMetaData for given collectionId: " + collectionId + "\t with domainNetBIOSName: " + domainNBName, ex);
            throw new SyMException(1002, ex.getMessage(), ex.fillInStackTrace());
        }
    }
    
    static {
        SYMConfigUtil.out = Logger.getLogger("ConfigLogger");
        SYMConfigUtil.sourceClass = "SYMConfigUtil";
    }
}
