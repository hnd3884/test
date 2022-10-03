package org.apache.catalina.tribes.group;

import org.apache.catalina.tribes.Member;
import java.io.Serializable;

public interface ExtendedRpcCallback extends RpcCallback
{
    void replyFailed(final Serializable p0, final Serializable p1, final Member p2, final Exception p3);
    
    void replySucceeded(final Serializable p0, final Serializable p1, final Member p2);
}
