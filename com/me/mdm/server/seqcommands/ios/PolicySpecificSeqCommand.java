package com.me.mdm.server.seqcommands.ios;

import org.json.JSONObject;
import java.util.List;

public interface PolicySpecificSeqCommand
{
    JSONObject getSequentialCommandForPolicy(final List p0, final JSONObject p1) throws Exception;
}
