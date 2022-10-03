package com.zoho.mickey.tools.crypto;

import com.zoho.net.handshake.HandShakeUtil;
import java.sql.SQLException;
import java.sql.Connection;
import com.adventnet.db.api.RelationalAPI;
import com.zoho.mickey.exception.KeyModificationException;
import com.adventnet.persistence.PersistenceInitializer;
import com.adventnet.mfw.ConsoleOut;
import java.util.logging.Logger;

public class ECTagModifier implements KeyModifier
{
    static Logger out;
    
    public void changeKey(final String newTag) throws KeyModificationException {
        try {
            if (newTag == null) {
                ECTagModifier.out.severe(" New ECTag should be given to start the migration!!");
                throw new Exception(" New ECTag should be given to start the migration!!");
            }
            ConsoleOut.println("Going to re-encrypt the data with the new ECTAG");
            this.checkIfServerRunning();
            PersistenceInitializer.loadPersistenceConfigurations();
            if (PersistenceInitializer.getConfigurationValue("ECTag").equals(newTag)) {
                throw new Exception("Existing  ectag and provided ectag are same. Please try with new key!!");
            }
            this.initializeMickey();
            ECTagModifierUtil.modifyECTag(newTag);
            ConsoleOut.println("data reencryption with new ECTAG completed successfully.");
            System.exit(0);
        }
        catch (final Exception e) {
            ConsoleOut.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
            ConsoleOut.println("Please refer logs!!");
            System.exit(-1);
        }
    }
    
    private boolean isDataBasePopulated() throws SQLException {
        try (final Connection con = RelationalAPI.getInstance().getConnection()) {
            return RelationalAPI.getInstance().getDBAdapter().isTablePresentInDB(con, (String)null, "SeqGenState");
        }
    }
    
    private void initializeMickey() throws Exception {
        PersistenceInitializer.initializeDB(System.getProperty("server.conf"));
        if (!this.isDataBasePopulated()) {
            ECTagModifier.out.severe("DataBase is not a populated one. Hence 'ectag' value cannot be modified.");
            throw new RuntimeException("DataBase is not a populated one. Hence 'ectag' value cannot be modified.");
        }
        PersistenceInitializer.initializeMickey(false);
        ECTagModifier.out.info("Mickey is initialized");
    }
    
    private void checkIfServerRunning() throws Exception {
        if (HandShakeUtil.isServerListening()) {
            throw new Exception("Server is running. Kindly shutdown and start the key migration!!");
        }
    }
    
    static {
        ECTagModifier.out = Logger.getLogger(ECTagModifier.class.getName());
    }
}
