package com.zoho.cp;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

public class MultiTxMgr extends TxMgr
{
    public void begin() throws NotSupportedException, SystemException {
        if (this.getStatus() != 6) {
            throw new SystemException("Already associated with a transaction. Cannot begin new transaction");
        }
        final TxMgr.TxDetail details = this.getThreadLocalDetails();
        TxnTimeOutImpl.register(details.txn = new MultiTxMgrTxn(details.transactionTimeout, this));
    }
}
