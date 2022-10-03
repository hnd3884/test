package com.zoho.mickey.ha.db.postgres;

import com.zoho.mickey.ha.HAException;
import com.zoho.mickey.ha.db.DataBaseHAUtil;
import com.zoho.mickey.ha.HAConfig;
import com.zoho.mickey.ha.TakeOverHandler;

public class PostgresTakeOverHandler implements TakeOverHandler
{
    private HAConfig config;
    
    public PostgresTakeOverHandler() {
        this.config = null;
    }
    
    @Override
    public void initialize(final HAConfig config) {
        this.config = config;
    }
    
    @Override
    public void onTakeover() throws HAException {
        DataBaseHAUtil.promoteDB(this.config);
    }
}
