package com.zoho.security.cache;

import com.adventnet.iam.security.SecurityUtil;
import com.zoho.security.dos.Util;
import com.zoho.jedis.v320.Jedis;

public class RedisLuaScript
{
    public static final RedisLuaScript ROLLING_WINDOW_VALIDATOR_SCRIPT;
    public static final RedisLuaScript FIXED_WINDOW_VALIDATOR_SCRIPT;
    public static final RedisLuaScript SLIDING_WINDOW_VALIDATION_SCRIPT;
    public static final RedisLuaScript LIVE_WINDOW_ACCESS_ENTER_HANDLER_SCRIPT;
    public static final RedisLuaScript LIVE_WINDOW_ACCESS_EXIT_HANDLER_SCRIPT;
    public static final RedisLuaScript LIVE_WINDOW_APP_SERVER_COUNT_CLEANER_SCRIPT;
    private final String script;
    private String scriptSha1Hash;
    
    public RedisLuaScript(final String script) {
        this.scriptSha1Hash = null;
        this.script = script;
    }
    
    public String getScript() {
        return this.script;
    }
    
    public String getScriptSha1Hash() {
        return this.scriptSha1Hash;
    }
    
    public String getScriptSha1Hash(final Jedis jedis) {
        if (this.scriptSha1Hash == null || !jedis.scriptExists(this.scriptSha1Hash)) {
            this.scriptSha1Hash = jedis.scriptLoad(this.script);
        }
        return this.scriptSha1Hash;
    }
    
    @Override
    public String toString() {
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("Script: \"");
        strBuilder.append(this.script);
        strBuilder.append("\", scriptSha1Hash: \"");
        strBuilder.append(this.scriptSha1Hash);
        strBuilder.append("\"");
        return strBuilder.append(".").toString();
    }
    
    static {
        ROLLING_WINDOW_VALIDATOR_SCRIPT = new RedisLuaScript(SecurityUtil.convertInputStreamAsString(Util.class.getClassLoader().getResourceAsStream("com/zoho/security/dos/RollingWindowValidatorScript.lua"), -1L));
        FIXED_WINDOW_VALIDATOR_SCRIPT = new RedisLuaScript(SecurityUtil.convertInputStreamAsString(Util.class.getClassLoader().getResourceAsStream("com/zoho/security/dos/FixedWindowValidatorScript.lua"), -1L));
        SLIDING_WINDOW_VALIDATION_SCRIPT = new RedisLuaScript(SecurityUtil.convertInputStreamAsString(Util.class.getClassLoader().getResourceAsStream("com/zoho/security/dos/SlidingWindowValidatorScript.lua"), -1L));
        LIVE_WINDOW_ACCESS_ENTER_HANDLER_SCRIPT = new RedisLuaScript(SecurityUtil.convertInputStreamAsString(Util.class.getClassLoader().getResourceAsStream("com/zoho/security/dos/LiveWindowAccessEnterHandlerScript.lua"), -1L));
        LIVE_WINDOW_ACCESS_EXIT_HANDLER_SCRIPT = new RedisLuaScript(SecurityUtil.convertInputStreamAsString(Util.class.getClassLoader().getResourceAsStream("com/zoho/security/dos/LiveWindowAccessExitHandlerScript.lua"), -1L));
        LIVE_WINDOW_APP_SERVER_COUNT_CLEANER_SCRIPT = new RedisLuaScript(SecurityUtil.convertInputStreamAsString(Util.class.getClassLoader().getResourceAsStream("com/zoho/security/dos/LiveWindowAppServerCountCleanerScript.lua"), -1L));
    }
}
