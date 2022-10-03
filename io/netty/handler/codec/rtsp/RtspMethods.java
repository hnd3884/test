package io.netty.handler.codec.rtsp;

import java.util.HashMap;
import io.netty.util.internal.ObjectUtil;
import java.util.Map;
import io.netty.handler.codec.http.HttpMethod;

public final class RtspMethods
{
    public static final HttpMethod OPTIONS;
    public static final HttpMethod DESCRIBE;
    public static final HttpMethod ANNOUNCE;
    public static final HttpMethod SETUP;
    public static final HttpMethod PLAY;
    public static final HttpMethod PAUSE;
    public static final HttpMethod TEARDOWN;
    public static final HttpMethod GET_PARAMETER;
    public static final HttpMethod SET_PARAMETER;
    public static final HttpMethod REDIRECT;
    public static final HttpMethod RECORD;
    private static final Map<String, HttpMethod> methodMap;
    
    public static HttpMethod valueOf(String name) {
        name = ObjectUtil.checkNonEmptyAfterTrim(name, "name").toUpperCase();
        final HttpMethod result = RtspMethods.methodMap.get(name);
        if (result != null) {
            return result;
        }
        return HttpMethod.valueOf(name);
    }
    
    private RtspMethods() {
    }
    
    static {
        OPTIONS = HttpMethod.OPTIONS;
        DESCRIBE = HttpMethod.valueOf("DESCRIBE");
        ANNOUNCE = HttpMethod.valueOf("ANNOUNCE");
        SETUP = HttpMethod.valueOf("SETUP");
        PLAY = HttpMethod.valueOf("PLAY");
        PAUSE = HttpMethod.valueOf("PAUSE");
        TEARDOWN = HttpMethod.valueOf("TEARDOWN");
        GET_PARAMETER = HttpMethod.valueOf("GET_PARAMETER");
        SET_PARAMETER = HttpMethod.valueOf("SET_PARAMETER");
        REDIRECT = HttpMethod.valueOf("REDIRECT");
        RECORD = HttpMethod.valueOf("RECORD");
        (methodMap = new HashMap<String, HttpMethod>()).put(RtspMethods.DESCRIBE.toString(), RtspMethods.DESCRIBE);
        RtspMethods.methodMap.put(RtspMethods.ANNOUNCE.toString(), RtspMethods.ANNOUNCE);
        RtspMethods.methodMap.put(RtspMethods.GET_PARAMETER.toString(), RtspMethods.GET_PARAMETER);
        RtspMethods.methodMap.put(RtspMethods.OPTIONS.toString(), RtspMethods.OPTIONS);
        RtspMethods.methodMap.put(RtspMethods.PAUSE.toString(), RtspMethods.PAUSE);
        RtspMethods.methodMap.put(RtspMethods.PLAY.toString(), RtspMethods.PLAY);
        RtspMethods.methodMap.put(RtspMethods.RECORD.toString(), RtspMethods.RECORD);
        RtspMethods.methodMap.put(RtspMethods.REDIRECT.toString(), RtspMethods.REDIRECT);
        RtspMethods.methodMap.put(RtspMethods.SETUP.toString(), RtspMethods.SETUP);
        RtspMethods.methodMap.put(RtspMethods.SET_PARAMETER.toString(), RtspMethods.SET_PARAMETER);
        RtspMethods.methodMap.put(RtspMethods.TEARDOWN.toString(), RtspMethods.TEARDOWN);
    }
}
