package com.me.devicemanagement.framework.server.redis;

public interface RedisConstants
{
    public static final int REDIS_SERVER_REQUIRED = 1;
    public static final int REDIS_SERVER_NOT_REQUIRED = 2;
    public static final int REDIS_SERVER_INSTALL_HANDLING_REQUIRED = 3;
    public static final int REDIS_SERVER_REVERT_HANDLING_REQUIRED = 4;
    public static final int REDIS_SERVER_INITIAL_INSTALLATION_FAILED = 5;
    public static final String REDIS_ENBALED = "enableRedis";
    public static final String REDIS_INSTALLATION_STATUS = "redisInstallationStatus";
}
