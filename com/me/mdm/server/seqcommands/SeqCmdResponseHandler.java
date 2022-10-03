package com.me.mdm.server.seqcommands;

import org.json.JSONObject;

public interface SeqCmdResponseHandler
{
    Long onSuccess(final JSONObject p0) throws Exception;
    
    Long onFailure(final JSONObject p0) throws Exception;
    
    Long retry(final JSONObject p0) throws Exception;
    
    JSONObject processLater(final JSONObject p0) throws Exception;
    
    boolean setParams(final Long p0, final JSONObject p1);
    
    boolean subCommandPreProcessor(final Long p0, final Long p1, final SequentialSubCommand p2);
    
    Long notNow(final JSONObject p0) throws Exception;
}
