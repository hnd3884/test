package com.me.mdm.server.factory;

import java.util.Properties;
import com.adventnet.sym.server.mdm.queue.CollectionAssociationQueue.CommandQueueObject;

public interface MDMAssociationQueueSerializerAPI
{
    Object serializeObject(final CommandQueueObject p0) throws Exception;
    
    CommandQueueObject deSerializeObject(final Object p0) throws Exception;
    
    Object serializeProperty(final Properties p0) throws Exception;
    
    Properties deserializeProperty(final Object p0) throws Exception;
}
