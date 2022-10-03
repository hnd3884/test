package com.me.idps.core.sync.db;

import com.me.idps.core.sync.product.DirectoryProductOpsHandler;
import com.me.idps.core.sync.events.IdpEventConstants;
import com.me.idps.core.sync.product.DirProdImplRequest;
import com.adventnet.ds.query.Criteria;
import java.util.Properties;
import java.sql.Connection;

class DirectoryDuplicateHandler
{
    private static DirectoryDuplicateHandler directoryDuplicateHandler;
    
    static DirectoryDuplicateHandler getInstance() {
        if (DirectoryDuplicateHandler.directoryDuplicateHandler == null) {
            DirectoryDuplicateHandler.directoryDuplicateHandler = new DirectoryDuplicateHandler();
        }
        return DirectoryDuplicateHandler.directoryDuplicateHandler;
    }
    
    void handleDuplicates(final Connection connection, final Properties dmDomainProps, final Long dmDomainID, final Long collationID, final Criteria resCri, final Criteria dirObjRegCri) throws Exception {
        final DirProdImplRequest dirProdImplRequest = new DirProdImplRequest();
        dirProdImplRequest.dmDomainProps = dmDomainProps;
        dirProdImplRequest.eventType = IdpEventConstants.RESOLVE_RESOURCE_DUPLICATES;
        dirProdImplRequest.args = new Object[] { connection, dmDomainID, collationID, resCri, dirObjRegCri };
        DirectoryProductOpsHandler.getInstance().invokeProductImpl(dirProdImplRequest);
    }
    
    static {
        DirectoryDuplicateHandler.directoryDuplicateHandler = null;
    }
}
