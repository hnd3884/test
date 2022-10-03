package org.apache.catalina.tribes;

public interface MembershipListener
{
    void memberAdded(final Member p0);
    
    void memberDisappeared(final Member p0);
}
