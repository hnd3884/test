package com.adventnet.management.transaction;

import java.sql.Statement;
import java.util.Enumeration;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Date;
import com.adventnet.management.scheduler.Scheduler;
import java.util.Vector;

class ConnectionMaintainer implements Runnable
{
    private Vector conVect;
    Scheduler sch;
    private boolean firstTime;
    private String sqlstr;
    
    public ConnectionMaintainer() {
        this.sch = null;
        this.firstTime = true;
        this.sqlstr = "select id from Event where id = 1";
    }
    
    public ConnectionMaintainer(final Vector conVect, final Scheduler sch, final String sqlstr) {
        this.sch = null;
        this.firstTime = true;
        this.sqlstr = "select id from Event where id = 1";
        this.conVect = conVect;
        this.sch = sch;
        this.sqlstr = sqlstr;
    }
    
    public void run() {
        try {
            if (this.conVect == null) {
                return;
            }
            if (!this.firstTime) {
                this.doDummyQuery(this.conVect);
            }
            else {
                this.firstTime = false;
            }
        }
        catch (final Exception ex) {
            System.out.println(" Exception in ConnectionMaintainer" + ex.getMessage());
            ex.printStackTrace();
        }
        this.sch.scheduleTask(this, new Date(new Date().getTime() + 14400000L));
    }
    
    private void doDummyQuery(final Vector vector) {
        final Enumeration elements = vector.elements();
        while (elements.hasMoreElements()) {
            Statement statement = null;
            final Connection connection = (Connection)elements.nextElement();
            try {
                statement = connection.createStatement();
                statement.execute(this.sqlstr);
            }
            catch (final SQLException ex) {}
            finally {
                if (statement != null) {
                    try {
                        statement.close();
                    }
                    catch (final SQLException ex2) {}
                }
            }
        }
    }
}
