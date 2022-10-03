package com.adventnet.sym.server.mdm.ios.payload.transform;

import java.util.Map;
import java.util.Iterator;
import java.util.HashMap;
import com.dd.plist.NSObject;
import com.dd.plist.Base64;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSArray;
import com.adventnet.persistence.Row;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.ios.payload.MacAccountConfigPayload;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2MacAccountConfigPayload implements DO2Payload
{
    public static Logger logger;
    
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        MacAccountConfigPayload payload = null;
        final MacAccountConfigPayload[] payloadArray = { null };
        try {
            final Row accountConfigRow = dataObject.getFirstRow("MdMacAccountConfigSettings");
            final Boolean skip_acc_creation = (Boolean)accountConfigRow.get("SKIP_ACC_CREATION");
            final Boolean set_regular_account = (Boolean)accountConfigRow.get("SET_REGULAR_ACCOUNT");
            final NSArray accountArray = this.getAccountArray(dataObject);
            payload = new MacAccountConfigPayload();
            payload.setRequestType("AccountConfiguration");
            payload.setSkipUserAccountCreation(skip_acc_creation);
            payload.setCreateRegularAccount(set_regular_account);
            payload.setAutoSetupAdminAccounts(accountArray);
        }
        catch (final Exception e) {
            DO2MacAccountConfigPayload.logger.log(Level.SEVERE, "Unable to create Account Configuration payload", e);
        }
        payloadArray[0] = payload;
        return payloadArray;
    }
    
    private byte[] getPasswordHash(final String digest) throws Exception {
        final NSDictionary passwordDict = new NSDictionary();
        final NSDictionary pbkdf2 = new NSDictionary();
        final String[] hashComponents = digest.split(":");
        final Integer iterations = Integer.parseInt(hashComponents[0]);
        final String saltString = hashComponents[1];
        final String hashString = hashComponents[2];
        passwordDict.put("entropy", (Object)Base64.decode(hashString));
        passwordDict.put("salt", (Object)Base64.decode(saltString));
        passwordDict.put("iterations", (Object)iterations);
        pbkdf2.put("SALTED-SHA512-PBKDF2", (NSObject)passwordDict);
        return pbkdf2.toXMLPropertyList().getBytes();
    }
    
    private NSDictionary getAccountDict(final String shortName, final String fullName, final String passwordHash, final Boolean hidden) throws Exception {
        final NSDictionary root = new NSDictionary();
        root.put("shortName", (Object)shortName);
        root.put("fullName", (Object)fullName);
        root.put("hidden", (Object)hidden);
        root.put("passwordHash", (Object)this.getPasswordHash(passwordHash));
        return root;
    }
    
    private NSArray getAccountArray(final DataObject dataObject) throws Exception {
        Iterator iterator = dataObject.getRows("MdMacAccountToConfig");
        final Map<Long, Boolean> accountMap = new HashMap<Long, Boolean>();
        int accountCount = 0;
        while (iterator.hasNext()) {
            final Row accountToConfigRow = iterator.next();
            final Long accountID = (Long)accountToConfigRow.get("ACCOUNT_ID");
            final Boolean hidden = (Boolean)accountToConfigRow.get("HIDDEN");
            accountMap.put(accountID, hidden);
            ++accountCount;
        }
        iterator = dataObject.getRows("MdComputerAccount");
        final NSArray accountArray = new NSArray(accountCount);
        int counter = 0;
        while (iterator.hasNext()) {
            final Row accountRow = iterator.next();
            final Long accountID2 = (Long)accountRow.get("ACCOUNT_ID");
            final String passwordHash = (String)accountRow.get("PASSWORD_HASH");
            final String shortName = (String)accountRow.get("SHORT_NAME");
            final String fullName = (String)accountRow.get("FULL_NAME");
            final Boolean hidden2 = accountMap.get(accountID2);
            accountArray.setValue(counter++, (Object)this.getAccountDict(shortName, fullName, passwordHash, hidden2));
        }
        return accountArray;
    }
    
    static {
        DO2MacAccountConfigPayload.logger = Logger.getLogger("MDMConfigLogger");
    }
}
