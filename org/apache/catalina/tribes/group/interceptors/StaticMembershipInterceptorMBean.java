package org.apache.catalina.tribes.group.interceptors;

import org.apache.catalina.tribes.Member;

public interface StaticMembershipInterceptorMBean
{
    int getOptionFlag();
    
    Member getLocalMember(final boolean p0);
}
