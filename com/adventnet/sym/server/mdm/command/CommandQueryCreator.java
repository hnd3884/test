package com.adventnet.sym.server.mdm.command;

import java.util.HashMap;

public interface CommandQueryCreator
{
    String createCmdQuery(final DeviceCommand p0, final String p1, final Long p2, final HashMap p3) throws Exception;
}
