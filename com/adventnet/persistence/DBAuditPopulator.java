package com.adventnet.persistence;

import java.util.Properties;
import java.security.InvalidKeyException;
import java.sql.Timestamp;
import java.io.IOException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.mfw.modulestartup.ModuleStartStopProcessor;

public class DBAuditPopulator implements ModuleStartStopProcessor
{
    public void initialize() throws Exception {
    }
    
    public void preStartProcess() throws Exception {
    }
    
    public void postStartProcess() throws Exception {
        final String superUserKey = "superuser_pass";
        if (PersistenceInitializer.getConfigurationValue("DBName").equalsIgnoreCase("postgres") && RelationalAPI.getInstance().getDBAdapter().isBundledDB()) {
            final Persistence persistence = (Persistence)BeanUtil.lookup("Persistence");
            final DataObject fetchedDataObject = persistence.get("DBCredentialsAudit", new Criteria(Column.getColumn("DBCredentialsAudit", "USERNAME"), "postgres", 0));
            final Properties dbProps = RelationalAPI.getInstance().getDBAdapter().getDBProps();
            final String superUserPassword = PersistenceUtil.getDBPasswordProvider("postgres").getPassword(dbProps.getProperty(superUserKey));
            if (fetchedDataObject.isEmpty()) {
                if (!dbProps.containsKey(superUserKey)) {
                    throw new IOException("SuperUser Key not found in database_params.conf");
                }
                final Row r = new Row("DBCredentialsAudit");
                r.set("USERNAME", "postgres");
                r.set("PASSWORD", superUserPassword);
                r.set("LAST_MODIFIED_TIME", new Timestamp(System.currentTimeMillis()));
                final DataObject dataObject = new WritableDataObject();
                dataObject.addRow(r);
                persistence.add(dataObject);
                if (!PersistenceUtil.removeKeyInDBConf(superUserKey)) {
                    throw new IOException("SuperUser Key not removed in database_params.conf");
                }
            }
            else if (dbProps.containsKey(superUserKey)) {
                throw new InvalidKeyException("Unknown key specified in database properties");
            }
        }
    }
    
    public void stopProcess() throws Exception {
    }
}
