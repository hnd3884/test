package com.adventnet.sym.webclient.mdm.config;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.List;
import com.adventnet.sym.server.mdm.config.MDMConfigHandler;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;

public class MDMConfigRequestHandler
{
    private Logger logger;
    
    public MDMConfigRequestHandler() {
        this.logger = Logger.getLogger(MDMConfigRequestHandler.class.getName());
    }
    
    public Properties handleRequest(final int requestType, final Properties props) throws SyMException {
        this.logger.log(Level.INFO, "handleRequest invoked with request type: {0}", requestType);
        this.logger.log(Level.FINE, "handleRequest Properties: {0}", props);
        Properties returnHash = null;
        final Integer profileType = ((Hashtable<K, Integer>)props).get("PROFILE_TYPE");
        switch (requestType) {
            case 1: {
                if (MDMConfigHandler.getInstance().isMobileConfig(props)) {
                    returnHash = MDMConfigHandler.getInstance(profileType).persistCollection(props);
                    break;
                }
                break;
            }
            case 3: {
                returnHash = MDMConfigHandler.getInstance().persistModifiedCollection(props);
                break;
            }
            case 6: {
                final Long collectionId = ((Hashtable<K, Long>)props).get("collectionId");
                returnHash = MDMConfigHandler.getInstance().deleteCollection(collectionId);
                break;
            }
            case 5: {
                final Long collectionId = ((Hashtable<K, Long>)props).get("collectionId");
                final String userName = ((Hashtable<K, String>)props).get("userName");
                returnHash = MDMConfigHandler.getInstance().deployCollection(collectionId, userName);
                break;
            }
            case 2: {
                if (MDMConfigHandler.getInstance().isMobileConfig(props)) {
                    returnHash = MDMConfigHandler.getInstance().persistAndDeployCollection(props);
                    break;
                }
                break;
            }
            case 4: {
                if (MDMConfigHandler.getInstance().isMobileConfig(props)) {
                    returnHash = MDMConfigHandler.getInstance().persistAndDeployModifiedCollection(props);
                }
            }
            case 8: {
                final Long collectionId = ((Hashtable<K, Long>)props).get("collectionId");
                returnHash = MDMConfigHandler.getInstance().resumeCollection(collectionId);
                break;
            }
            case 7: {
                final Long collectionId = ((Hashtable<K, Long>)props).get("collectionId");
                returnHash = MDMConfigHandler.getInstance().suspendCollection(collectionId);
                break;
            }
            case 9: {
                final List collectionIds = ((Hashtable<K, List>)props).get("collectionIds");
                final Long userID = ((Hashtable<K, Long>)props).get("userID");
                MDMConfigHandler.getInstance().moveCollectionsToTrash(collectionIds, userID);
                returnHash = new Properties();
                break;
            }
        }
        return returnHash;
    }
}
