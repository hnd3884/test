package com.me.mdm.onpremise.util;

import java.util.Properties;
import com.adventnet.sym.server.mdm.queue.CollectionAssociationQueue.AssociationQueueDataSerializer;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.queue.CollectionAssociationQueue.CommandQueueObject;
import com.me.mdm.server.factory.MDMAssociationQueueSerializerAPI;

public class MDMPAssociationQueueSerializerApiImpl implements MDMAssociationQueueSerializerAPI
{
    public Object serializeObject(final CommandQueueObject obj) throws Exception {
        Logger.getLogger(MDMAssociationQueueSerializerAPI.class.getName()).log(Level.INFO, "MDMP serializer is being called");
        return AssociationQueueDataSerializer.getInstance().convertObjectToString(obj);
    }
    
    public CommandQueueObject deSerializeObject(final Object obj) throws Exception {
        Logger.getLogger(MDMAssociationQueueSerializerAPI.class.getName()).log(Level.INFO, "MDMP de-serializer is being called");
        return AssociationQueueDataSerializer.getInstance().convertStringToObject(obj + "");
    }
    
    public Object serializeProperty(final Properties properties) throws Exception {
        Logger.getLogger("MDMProfileDistributionLog").log(Level.INFO, "MDMP serialize object called : with props {0}", new Object[] { properties });
        return AssociationQueueDataSerializer.getInstance().convertPropertiesToString(properties);
    }
    
    public Properties deserializeProperty(final Object object) throws Exception {
        Logger.getLogger("MDMProfileDistributionLog").log(Level.INFO, "MDMP deserialize object called : with props {0}", new Object[] { object });
        return AssociationQueueDataSerializer.getInstance().convertStringToProperties(object + "");
    }
}
