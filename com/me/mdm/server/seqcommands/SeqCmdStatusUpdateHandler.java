package com.me.mdm.server.seqcommands;

import java.util.List;

public interface SeqCmdStatusUpdateHandler
{
    void makeStatusUpdateforSubCommand(final Long p0, final Long p1, final Long p2) throws Exception;
    
    void makeStatusUpdateforSubCommand(final List<SequentialSubCommand> p0) throws Exception;
}
