package com.maverick.ssh.message;

public interface MessageObserver
{
    boolean wantsNotification(final Message p0);
}
