package com.adventnet.db.adapter.postgres;

import java.io.IOException;
import java.util.List;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class PostgresDBInitializer extends DefaultPostgresDBInitializer
{
    private static final Logger OUT;
    
    @Override
    public boolean startDBServer(final int port, final String host, final String userName, final String password) throws IOException {
        PostgresDBInitializer.OUT.info("Going to start DB server using startDB script");
        final List commandList = new ArrayList();
        commandList.add(String.valueOf(port));
        final int size = commandList.size();
        final String[] startBatchFileArgs = commandList.toArray(new String[size]);
        this.startDBServer(startBatchFileArgs);
        if (!(this.isDaemonStarted = this.isServerStarted(port, host, userName))) {
            throw new ConnectException("Trying to start PostgresSQL server failed ");
        }
        return this.isDaemonStarted;
    }
    
    @Override
    public void stopDBServer(final int port, final String host, final String userName, final String password) throws IOException {
        PostgresDBInitializer.OUT.info("Going to stop DB server using startDB script");
        if (this.isDaemonStarted) {
            final List commandList = new ArrayList();
            commandList.add(String.valueOf(port));
            final int size = commandList.size();
            final String[] stopBatchFileArgs = commandList.toArray(new String[size]);
            this.stopDBServer(stopBatchFileArgs);
        }
    }
    
    static {
        OUT = Logger.getLogger(PostgresDBInitializer.class.getName());
    }
}
