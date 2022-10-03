package com.adventnet.iam.security;

public class LiveWindowAccessInfoLock extends AccessInfoLock
{
    public LiveWindowAccessInfoLock(final ThrottlesRule violatedThrottlesRule, final ThrottleRule violatedThrottle, final long violatedTimeInMillis) {
        super(violatedThrottlesRule, violatedThrottle, violatedTimeInMillis);
    }
}
