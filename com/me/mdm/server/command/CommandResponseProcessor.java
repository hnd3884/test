package com.me.mdm.server.command;

import org.json.JSONObject;

public interface CommandResponseProcessor
{
    public interface ImmediateSeqResponseProcessor
    {
        JSONObject processImmediateSeqCommand(final JSONObject p0);
    }
    
    public interface SeqQueuedResponseProcessor
    {
        JSONObject processSeqQueuedCommand(final JSONObject p0);
    }
    
    public interface QueuedResponseProcessor
    {
        JSONObject processQueuedCommand(final JSONObject p0) throws Exception;
    }
}
