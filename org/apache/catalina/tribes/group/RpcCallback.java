package org.apache.catalina.tribes.group;

import org.apache.catalina.tribes.Member;
import java.io.Serializable;

public interface RpcCallback
{
    Serializable replyRequest(final Serializable p0, final Member p1);
    
    void leftOver(final Serializable p0, final Member p1);
}
