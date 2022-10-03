package com.adventnet.authentication.service;

import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.persistence.Persistence;
import java.util.logging.Level;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;
import com.adventnet.mfw.service.Service;

public class AuthenticationService implements Service
{
    private static final Logger LOGGER;
    
    public void create(final DataObject serviceDO) throws Exception {
        AuthenticationService.LOGGER.log(Level.FINER, "authenticationservice.createService invoked");
    }
    
    public void start() throws Exception {
        AuthenticationService.LOGGER.log(Level.FINER, "authenticationservice.startService invoked");
        this.checkAndCloseSessions(false);
    }
    
    public void stop() throws Exception {
        AuthenticationService.LOGGER.log(Level.FINER, "authenticationservice.stopService invoked");
        this.checkAndCloseSessions(true);
    }
    
    public void destroy() throws Exception {
        AuthenticationService.LOGGER.log(Level.FINER, "authenticationservice.destroy invoked");
    }
    
    private void checkAndCloseSessions(final boolean stop) throws Exception {
        final Persistence pers = (Persistence)BeanUtil.lookup("Persistence");
        final Criteria cri = new Criteria(Column.getColumn("AaaAccHttpSession", "SESSION_ID"), (Object)null, 1);
        pers.delete(cri);
        final Criteria ct = new Criteria(Column.getColumn("AaaAccSession", "STATUS"), (Object)"ACTIVE", 0);
        final UpdateQuery uq = (UpdateQuery)new UpdateQueryImpl("AaaAccSession");
        uq.setUpdateColumn("STATUS", (Object)"CLOSED");
        uq.setCriteria(ct);
        if (stop) {
            uq.setUpdateColumn("CLOSETIME", (Object)System.currentTimeMillis());
        }
        pers.update(uq);
    }
    
    static {
        LOGGER = Logger.getLogger(AuthenticationService.class.getName());
    }
}
