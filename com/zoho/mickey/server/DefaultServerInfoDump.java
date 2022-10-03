package com.zoho.mickey.server;

import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import com.zoho.mickey.db.RunningQueries;
import com.zoho.conf.ThreadInformation;
import com.adventnet.ds.DSUtil;

public class DefaultServerInfoDump implements ServerInfoDump
{
    public void dump(final String... information) {
        if (information.length == 0) {
            DSUtil.dumpInUseConnections();
            ThreadInformation.takeThreadDump();
            RunningQueries.dumpInformation();
        }
        else {
            final List<String> options = new ArrayList<String>(information.length);
            for (final String option : information) {
                if (option != null) {
                    options.add(option.toLowerCase(Locale.ENGLISH));
                }
            }
            if (options.contains("runningqueries")) {
                RunningQueries.dumpInformation();
            }
            if (options.contains("connection")) {
                DSUtil.dumpInUseConnections();
            }
            if (options.contains("thread")) {
                ThreadInformation.takeThreadDump();
            }
        }
    }
}
