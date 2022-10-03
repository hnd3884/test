package com.zoho.mickey.server;

import java.util.concurrent.TimeUnit;

public interface ServerInfoDump
{
    default void repeatDump(final String... information) {
        for (int i = 0; i < 3; ++i) {
            this.dump(information);
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(3L));
            }
            catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    void dump(final String... p0);
}
