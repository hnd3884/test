package com.adventnet.sym.server.mdm.android.payload.transform;

import java.util.Iterator;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.android.payload.AndroidEFRPPayload;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayload;
import com.adventnet.persistence.DataObject;

public class DO2AndroidEFRPPayload implements DO2AndroidPayload
{
    @Override
    public AndroidPayload createPayload(final DataObject dataObject) {
        AndroidEFRPPayload efrpPayload = null;
        try {
            if (dataObject != null) {
                final Iterator iterator = dataObject.getRows("AndroidEFRPPolicy");
                efrpPayload = new AndroidEFRPPayload("1.0", "com.mdm.mobiledevice.EnterpriseFactoryResetSettings", "Enterprise Factory ResetSettings");
                final JSONArray emailUserIds = new JSONArray();
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final long efrpAccId = (long)row.get("EFRP_ACC_ID");
                    final Criteria CfgDataItemCriteria = new Criteria(new Column("EFRPAccDetails", "EFRP_ACC_ID"), (Object)efrpAccId, 0);
                    final Iterator emailDetailIetrator = dataObject.getRows("EFRPAccDetails", CfgDataItemCriteria);
                    while (emailDetailIetrator.hasNext()) {
                        final Row mailDetailRow = emailDetailIetrator.next();
                        emailUserIds.put(mailDetailRow.get("EMAIL_USER_ID"));
                    }
                }
                efrpPayload.setAuthorizedMailIds(emailUserIds);
                efrpPayload.setFRPSwitch(true);
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
        return efrpPayload;
    }
}
