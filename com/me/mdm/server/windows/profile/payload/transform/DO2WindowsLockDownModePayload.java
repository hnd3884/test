package com.me.mdm.server.windows.profile.payload.transform;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccessException;
import javax.xml.bind.JAXBException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import com.me.mdm.core.lockdown.windows.WindowsLockdownHandler;
import com.me.mdm.core.lockdown.data.LockdownPolicy;
import com.me.mdm.server.windows.profile.payload.WindowsLockDownModePayload;
import com.me.mdm.server.windows.profile.payload.WindowsPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2WindowsLockDownModePayload extends DO2WindowsPayload
{
    public Logger logger;
    
    public DO2WindowsLockDownModePayload() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public WindowsPayload createPayload(final DataObject dataObject) {
        final WindowsLockDownModePayload lockdownModePayload = new WindowsLockDownModePayload();
        final LockdownPolicy lockdownPolicy = new LockdownPolicy();
        try {
            final Row row = dataObject.getFirstRow("LockdownPolicy");
            lockdownPolicy.policyID = (Long)row.get("POLICY_ID");
            lockdownPolicy.populateLockdownDataFromDO(dataObject);
            lockdownModePayload.setConfigurationXML(new WindowsLockdownHandler().getLockDownXML(lockdownPolicy, false));
        }
        catch (final SyMException e) {
            this.logger.log(Level.WARNING, "Failed to create lockdown policy ", (Throwable)e);
        }
        catch (final ClassNotFoundException e2) {
            this.logger.log(Level.WARNING, "Failed to create lockdown policy ", e2);
        }
        catch (final JAXBException e3) {
            this.logger.log(Level.WARNING, "Failed to create lockdown policy ", e3);
        }
        catch (final DataAccessException e4) {
            this.logger.log(Level.WARNING, "Failed to create lockdown policy ", (Throwable)e4);
        }
        catch (final Exception e5) {
            this.logger.log(Level.WARNING, "Failed to create lockdown policy ", e5);
        }
        return lockdownModePayload;
    }
    
    @Override
    public WindowsPayload createRemoveProfilePayload(final DataObject dataObject) {
        final WindowsLockDownModePayload windowsLockDownModePayload = new WindowsLockDownModePayload();
        windowsLockDownModePayload.setConfigurationDelete();
        return windowsLockDownModePayload;
    }
}
