package com.adventnet.iam.security;

import org.json.JSONObject;
import org.json.JSONArray;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import com.zoho.security.ProxyInfo;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.io.IOException;
import com.zoho.security.scheduler.LiveWindowCleaner;
import java.util.Calendar;
import com.zoho.security.dos.Util;
import com.zoho.security.cache.RedisCacheAPI;
import com.zoho.security.cache.CacheConfiguration;
import com.zoho.security.cache.CacheConstants;
import java.net.UnknownHostException;
import java.net.InetAddress;
import com.zoho.security.eventfw.pojos.log.ZSEC_PERFORMANCE_ANOMALY;
import com.zoho.security.eventfw.ExecutionTimer;
import java.util.logging.Level;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.Queue;
import java.util.Map;
import java.util.List;
import java.util.logging.Logger;

public class DoSController
{
    static final Logger LOGGER;
    static final ThreadLocal<List<AccessInfo>> URL_ACCESSINFO_LIST;
    static final ThreadLocal<Map<ThrottlesRule.Windows, List<ThrottlesRule>>> DYNAMIC_THROTTLES_RULE_MAP;
    static final ThrottlesRule.Windows[] FRS_WINDOWS;
    static final ThrottlesRule.Windows[] LIVE_WINDOW;
    private static String host_local_address;
    private static boolean liveWindowThrottlesInitialized;
    private static boolean liveWindowCleanerTaskSchedulingCheckCompleted;
    
    public static String PRINT(final Queue<Long> value) {
        String vv = "";
        for (final long v : value) {
            vv = vv + " " + v;
        }
        return vv;
    }
    
    private static boolean hasDosCookie(final HttpServletRequest request, final String name) {
        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static void setDosCookie(final HttpServletRequest request, final HttpServletResponse response) {
        if (request.getAttribute("dos-cookie") != null) {
            response.addCookie((Cookie)request.getAttribute("dos-cookie"));
        }
    }
    
    private static void addDosCookie(final HttpServletRequest request, final HttpServletResponse response, final String name, final String value, final String path, final int remember, final boolean secure) {
        response.setHeader("P3P", "CP=\"CAO PSA OUR\"");
        String domain = null;
        if (SecurityFilterProperties.getInstance(request).isAuthenticationProviderConfigured()) {
            domain = SecurityFilterProperties.getInstance(request).getAuthenticationProvider().getIAMCookieDomain();
        }
        final HttpCookie c = new HttpCookie(name, value);
        DoSController.LOGGER.log(Level.FINE, "Domian : {0} Name : {1} Value : {2}  Secure : {3}", new Object[] { domain, name, value, secure });
        if (remember != -1) {
            c.setMaxAge(remember);
        }
        if (domain != null) {
            c.setDomain(domain);
        }
        c.setPath(path);
        c.setSecure(secure);
        response.addHeader("Set-Cookie", c.generateCookie());
        request.setAttribute("dos-cookie", (Object)c);
    }
    
    public static void controlDoS(final HttpServletRequest request, final HttpServletResponse response, final ActionRule actionRule) throws IAMSecurityException {
        final ExecutionTimer dosTimer = ExecutionTimer.startInstance();
        if (!actionRule.isThrottlingEnabled()) {
            ZSEC_PERFORMANCE_ANOMALY.pushControlDos(request.getRequestURI(), dosTimer);
            return;
        }
        final long currentRequestArrivalTimeInMillis = System.currentTimeMillis();
        try {
            doThrottle(request, response, actionRule, currentRequestArrivalTimeInMillis);
            ZSEC_PERFORMANCE_ANOMALY.pushControlDos(request.getRequestURI(), dosTimer);
        }
        catch (final IAMSecurityException e) {
            ZSEC_PERFORMANCE_ANOMALY.pushControlDos(request.getRequestURI(), dosTimer);
            throw e;
        }
        finally {
            final SecurityFilterProperties securityFilterProperties = SecurityFilterProperties.getInstance(request);
            final List<AccessInfo> accessInfos = getAccessInfoListFromThreadLocal();
            if (actionRule.isAPI() && securityFilterProperties.isSetAPIRateLimitResponseHeader() && accessInfos != null) {
                addAPIRateLimitResponseHeader(request, response, securityFilterProperties, actionRule, accessInfos, currentRequestArrivalTimeInMillis);
            }
            final long totalTime = System.currentTimeMillis() - currentRequestArrivalTimeInMillis;
            if (totalTime > 5000L) {
                DoSController.LOGGER.log(Level.INFO, "Time taken for URL throttling is {0} ms.", new Object[] { totalTime });
            }
        }
    }
    
    private static String getHostLocalAddress() {
        if (DoSController.host_local_address == null) {
            try {
                DoSController.host_local_address = InetAddress.getLocalHost().getHostAddress();
            }
            catch (final UnknownHostException e) {
                DoSController.LOGGER.log(Level.SEVERE, "Exception occurred while getting localhost address. Exception: {0}", new Object[] { e.getMessage() });
            }
        }
        return DoSController.host_local_address;
    }
    
    protected static void initLiveWindowThrottles(final SecurityFilterProperties filterProperties) {
        final List<CacheConfiguration> cacheConfigurations = filterProperties.getCacheConfigurationList(CacheConstants.PoolNames.LIVE.name());
        if (!SecurityUtil.isValidList(cacheConfigurations)) {
            return;
        }
        if (!DoSController.liveWindowThrottlesInitialized) {
            initLiveWindowThrottles(cacheConfigurations);
        }
        if (!DoSController.liveWindowCleanerTaskSchedulingCheckCompleted) {
            scheduleLiveWindowCleaner(filterProperties.isStartLiveWindowCleanerScheduler(), cacheConfigurations);
        }
    }
    
    private static void initLiveWindowThrottles(final List<CacheConfiguration> cacheConfigurations) {
        try {
            LiveWindowThrottler.init(SecurityFilterProperties.getServiceName(), getHostLocalAddress(), cacheConfigurations);
        }
        catch (final Exception e) {
            DoSController.LOGGER.log(Level.WARNING, "Exception occurred while initializing LiveWindowThrottles. Exception: {0}", new Object[] { e.getMessage() });
            return;
        }
        DoSController.liveWindowThrottlesInitialized = true;
    }
    
    private static void scheduleLiveWindowCleaner(final boolean isStartLiveWindowCleanerScheduler, final List<CacheConfiguration> cacheConfigurations) {
        if (isStartLiveWindowCleanerScheduler) {
            try {
                final CacheConfiguration firstCacheConfiguration = cacheConfigurations.get(0);
                final long status = RedisCacheAPI.putDataIntoMapIfNotExists("LW_CSM", SecurityFilterProperties.getServiceName(), getHostLocalAddress(), firstCacheConfiguration);
                if (status == 1L || getHostLocalAddress().equals(RedisCacheAPI.getDataFromMap("LW_CSM", SecurityFilterProperties.getServiceName(), firstCacheConfiguration))) {
                    final long period = Util.TimeUnitInMillis.ONE_HOUR.getTime();
                    final long delay = Util.getFloorHours(Calendar.getInstance()).getTime() + period - System.currentTimeMillis();
                    LiveWindowCleaner.schedule(delay, period, SecurityFilterProperties.getServiceName(), cacheConfigurations);
                }
            }
            catch (final Exception e) {
                DoSController.LOGGER.log(Level.WARNING, "Exception occurred while scheduling LiveWindowCleaner timertask. Exception: {0}", new Object[] { e.getMessage() });
                return;
            }
        }
        DoSController.liveWindowCleanerTaskSchedulingCheckCompleted = true;
    }
    
    static void cleanLiveWindowCount(final SecurityRequestWrapper sReqWrapper, final SecurityResponseWrapper sResWrapper) throws IOException {
        final String appHostIp = sReqWrapper.getParameter("app-host-ip");
        final List<CacheConfiguration> cacheConfigurations = SecurityFilterProperties.getInstance((HttpServletRequest)sReqWrapper).getCacheConfigurationList(CacheConstants.PoolNames.LIVE.name());
        if (cacheConfigurations == null) {
            DoSController.LOGGER.log(Level.INFO, "CacheConfiguration is null");
        }
        else {
            try {
                final long cleanedLiveWindowCount = LiveWindowThrottler.cleanAppServerLiveWindowCountFromAllCache(SecurityFilterProperties.getServiceName(), appHostIp, cacheConfigurations);
                RedisCacheAPI.removeDataFromSet("LW_SAHS_" + SecurityFilterProperties.getServiceName(), new String[] { appHostIp }, cacheConfigurations.get(0));
                DoSController.LOGGER.log(Level.INFO, "AppHostIp: {0}, CleanedLiveWindowCount: {1}", new Object[] { appHostIp, cleanedLiveWindowCount });
            }
            catch (final Exception e) {
                DoSController.LOGGER.log(Level.WARNING, "Exception occurred while cleaning the live window count, Exception: {0}, AppHostIp: {1}", new Object[] { e.getMessage(), appHostIp });
                sResWrapper.getWriter().write("FAILED");
                return;
            }
        }
        sResWrapper.getWriter().write("SUCCESS");
    }
    
    public static void doLiveThrottle(final HttpServletRequest request, final boolean isRequestEnter) {
        final ExecutionTimer lthrottleTimer = ExecutionTimer.startInstance();
        final SecurityFilterProperties filterProperties = SecurityFilterProperties.getInstance(request);
        if (isRequestEnter) {
            if (!DoSController.liveWindowThrottlesInitialized || !DoSController.liveWindowCleanerTaskSchedulingCheckCompleted) {
                initLiveWindowThrottles(SecurityFilterProperties.getInstance(request));
            }
            final Map<String, ThrottlesRule> throttlesKeyVsRuleMap = getRequestThrottlesMapByWindow(request, DoSController.LIVE_WINDOW);
            if (throttlesKeyVsRuleMap.isEmpty()) {
                ZSEC_PERFORMANCE_ANOMALY.pushLiveThrottle(request.getRequestURI(), lthrottleTimer);
                return;
            }
            ((SecurityRequestWrapper)request).setThrottledFlagAsTrue();
            for (final Map.Entry<String, ThrottlesRule> entry : throttlesKeyVsRuleMap.entrySet()) {
                final String reqThrottlesKey = entry.getKey();
                final ThrottlesRule throttlesRule = entry.getValue();
                final LiveWindowThrottleRule liveWindowThrottleRule = throttlesRule.getThrottleRuleMap().get(0L);
                CacheConfiguration cacheConfiguration = null;
                AccessInfo accessInfo = null;
                if (throttlesRule.getScope() == ThrottlesRule.Scopes.APPSERVER) {
                    accessInfo = new LiveAccessInfo(reqThrottlesKey, throttlesRule);
                    accessInfo.tryLock();
                }
                else {
                    cacheConfiguration = getCacheConfiguration(CacheConstants.PoolNames.LIVE);
                    try {
                        accessInfo = new DbCacheLiveAccessInfo(SecurityFilterProperties.getServiceName(), getHostLocalAddress(), reqThrottlesKey, throttlesRule, cacheConfiguration);
                        accessInfo.tryLock();
                    }
                    catch (final Exception e) {
                        DoSController.LOGGER.log(Level.SEVERE, "The throttles check is skipped due to the Exception. CacheConfiguration: {0}, ThrottlesRule: {1}", new Object[0]);
                        if (filterProperties.isBlockRequestOnDosCacheException()) {
                            throw e;
                        }
                        continue;
                    }
                }
                addAccessInfoToThreadLocal(accessInfo);
                if (accessInfo.getLock() != null) {
                    ZSEC_PERFORMANCE_ANOMALY.pushLiveThrottle(request.getRequestURI(), lthrottleTimer);
                    DoSController.LOGGER.log(Level.SEVERE, "The request is locked for throttle rule violation. ThrottlesRule:: {0}, ViolatedThrottleRule:: {1}", new Object[] { throttlesRule, liveWindowThrottleRule });
                    throw new IAMSecurityException("URL_LIVE_THROTTLES_LIMIT_EXCEEDED", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), accessInfo, accessInfo.getLock());
                }
            }
        }
        else {
            final List<AccessInfo> accessInfos = getAccessInfoListFromThreadLocal();
            if (accessInfos == null) {
                ZSEC_PERFORMANCE_ANOMALY.pushLiveThrottle(request.getRequestURI(), lthrottleTimer);
                return;
            }
            for (final AccessInfo accessInfo2 : accessInfos) {
                if (accessInfo2.getThrottlesWindow() != ThrottlesRule.Windows.LIVE) {
                    continue;
                }
                if (accessInfo2.getThrottlesRule().getScope() == ThrottlesRule.Scopes.APPSERVER) {
                    ((LiveAccessInfo)accessInfo2).accessExit();
                }
                else {
                    try {
                        ((DbCacheLiveAccessInfo)accessInfo2).accessExit();
                    }
                    catch (final Exception e2) {
                        DoSController.LOGGER.log(Level.SEVERE, "The throttles check is skipped due to the exception. CacheConfiguration: {0}, ThrottlesRule: {1}", new Object[0]);
                        if (filterProperties.isBlockRequestOnDosCacheException()) {
                            throw e2;
                        }
                        continue;
                    }
                }
            }
        }
        ZSEC_PERFORMANCE_ANOMALY.pushLiveThrottle(request.getRequestURI(), lthrottleTimer);
    }
    
    public static void putLiveWindowThrottlesKey(final HttpServletRequest request, final String throttlesKey, final CacheConfiguration cacheConfiguration) {
        final Object mapObj = request.getAttribute(SecurityRequestWrapper.ZsecAttributes.live_window_throttles_key_map.name());
        Map<String, CacheConfiguration> throttlesKeyVsCacheConfigurationMap = null;
        if (mapObj == null) {
            throttlesKeyVsCacheConfigurationMap = new HashMap<String, CacheConfiguration>();
            request.setAttribute(SecurityRequestWrapper.ZsecAttributes.live_window_throttles_key_map.name(), (Object)throttlesKeyVsCacheConfigurationMap);
        }
        else {
            throttlesKeyVsCacheConfigurationMap = (Map)mapObj;
        }
        throttlesKeyVsCacheConfigurationMap.put(throttlesKey, cacheConfiguration);
    }
    
    private static void doThrottle(final HttpServletRequest request, final HttpServletResponse response, final ActionRule actionRule, final long currentRequestArrivalTimeInMillis) throws IAMSecurityException {
        final Map<String, ThrottlesRule> requestThrottlesKeyVsRuleMap = getRequestThrottlesMapByWindow(request, DoSController.FRS_WINDOWS);
        if (requestThrottlesKeyVsRuleMap.isEmpty()) {
            return;
        }
        ((SecurityRequestWrapper)request).setThrottledFlagAsTrue();
        final SecurityFilterProperties filterProperties = SecurityFilterProperties.getInstance(request);
        for (final Map.Entry<String, ThrottlesRule> entry : requestThrottlesKeyVsRuleMap.entrySet()) {
            final String requestThrottlesKey = entry.getKey();
            final ThrottlesRule throttlesRule = entry.getValue();
            AccessInfo accessInfo = null;
            if (throttlesRule.getScope() == ThrottlesRule.Scopes.APPSERVER) {
                accessInfo = AccessInfo.getAppServerScopeInstance(requestThrottlesKey, throttlesRule);
                final long accessInfoExpiryScheduleInterval = SecurityFilterProperties.getInstance(request).getAccessInfoExpiryScheduleInterval();
                if (accessInfoExpiryScheduleInterval != -1L && !AccessInfoExpiryHandler.isScheduled()) {
                    AccessInfoExpiryHandler.schedule(accessInfoExpiryScheduleInterval);
                }
            }
            else {
                accessInfo = AccessInfo.getServiceScopeInstance(requestThrottlesKey, throttlesRule, getCacheConfiguration(CacheConstants.PoolNames.valueOf(throttlesRule.getWindow().name())));
            }
            addAccessInfoToThreadLocal(accessInfo);
            try {
                final AccessInfoLock lock = accessInfo.getLock();
                if (lock == null) {
                    continue;
                }
                throwError(request, response, currentRequestArrivalTimeInMillis, accessInfo, lock, actionRule);
            }
            catch (final IAMSecurityException e) {
                throw e;
            }
            catch (final Exception e2) {
                DoSController.LOGGER.log(Level.SEVERE, "The throttles check is skipped due to the exception. CacheConfiguration: {0}, ThrottlesRule: {1}", new Object[] { ((DbCacheAccessInfo)accessInfo).getCacheConfiguration(), accessInfo.getThrottlesRule() });
                if (filterProperties.isBlockRequestOnDosCacheException()) {
                    throw e2;
                }
                ((DbCacheAccessInfo)accessInfo).setCacheException(e2);
            }
        }
        for (final AccessInfo accessInfo2 : getAccessInfoListFromThreadLocal()) {
            try {
                final AccessInfoLock lock2 = accessInfo2.tryLock(currentRequestArrivalTimeInMillis);
                if (lock2 == null) {
                    continue;
                }
                throwError(request, response, currentRequestArrivalTimeInMillis, accessInfo2, lock2, actionRule);
            }
            catch (final IAMSecurityException e3) {
                throw e3;
            }
            catch (final Exception e4) {
                DoSController.LOGGER.log(Level.SEVERE, "The throttles check is skipped due to the exception. CacheConfiguration: {0}, ThrottlesRule: {1}", new Object[] { ((DbCacheAccessInfo)accessInfo2).getCacheConfiguration(), accessInfo2.getThrottlesRule() });
                if (filterProperties.isBlockRequestOnDosCacheException()) {
                    throw e4;
                }
                ((DbCacheAccessInfo)accessInfo2).setCacheException(e4);
            }
        }
    }
    
    private static CacheConfiguration getCacheConfiguration(final CacheConstants.PoolNames poolName) {
        final HttpServletRequest request = SecurityUtil.getCurrentRequest();
        final SecurityFilterProperties securityFilterProperties = SecurityFilterProperties.getInstance(request);
        CacheConfiguration cacheConfiguration = null;
        final List<CacheConfiguration> cacheConfigurationList = securityFilterProperties.getCacheConfigurationList(poolName.name());
        final String zuidStr = (securityFilterProperties.getAuthenticationProvider() == null) ? null : securityFilterProperties.getAuthenticationProvider().getAuthenticatedUserId((SecurityRequestWrapper)request, ThrottlesRule.UserKeysName.ZUID);
        if (cacheConfigurationList.size() == 1 || !Util.isValidString(zuidStr) || (cacheConfiguration = getCacheConfiguration(cacheConfigurationList, Long.parseLong(zuidStr))) == null) {
            cacheConfiguration = cacheConfigurationList.get(0);
        }
        return cacheConfiguration;
    }
    
    private static CacheConfiguration getCacheConfiguration(final List<CacheConfiguration> cacheConfigurationList, final long zuid) {
        for (int i = 1; i < cacheConfigurationList.size(); ++i) {
            if (cacheConfigurationList.get(i).isInRange(zuid)) {
                return cacheConfigurationList.get(i);
            }
        }
        return null;
    }
    
    private static Map<String, ThrottlesRule> getRequestThrottlesMapByWindow(final HttpServletRequest request, final ThrottlesRule.Windows[] windows) {
        final Map<String, ThrottlesRule> resultMap = new LinkedHashMap<String, ThrottlesRule>();
        final ActionRule actionRule = (ActionRule)request.getAttribute(SecurityRequestWrapper.ZsecAttributes.urlrule.name());
        if (actionRule.isDynamicThrottlesEnabled() && DoSController.DYNAMIC_THROTTLES_RULE_MAP.get() == null) {
            try {
                final List<ThrottlesRule> throttlesList = SecurityFilterProperties.getInstance(request).getSecurityProvider().getDynamicThrottlesRuleList(request, actionRule);
                if (SecurityUtil.isValidList(throttlesList)) {
                    DoSController.DYNAMIC_THROTTLES_RULE_MAP.set(sortAndValidateThrottlesByWindow(throttlesList));
                }
            }
            catch (final Exception e) {
                DoSController.LOGGER.log(Level.WARNING, "Exception occurred while getting dynamic throttles rule list. Error-msg: \"{0}\", {1}", new Object[] { e.getMessage(), actionRule });
            }
        }
        for (final ThrottlesRule.Windows window : windows) {
            List<ThrottlesRule> throttlesList2 = actionRule.getThrottlesRuleByWindow(window);
            if (SecurityUtil.isValidList(throttlesList2)) {
                addRequestThrottlesToResultMap(request, actionRule, throttlesList2, resultMap);
            }
            throttlesList2 = getDynamicThrottlesRuleFromThreadLocal(window);
            if (SecurityUtil.isValidList(throttlesList2)) {
                addRequestThrottlesToResultMap(request, actionRule, throttlesList2, resultMap);
            }
        }
        return resultMap;
    }
    
    private static void addRequestThrottlesToResultMap(final HttpServletRequest request, final ActionRule actionRule, final List<ThrottlesRule> throttlesList, final Map<String, ThrottlesRule> resultMap) {
        final SecurityFilterProperties securityFilterProperties = SecurityFilterProperties.getInstance(request);
        for (ThrottlesRule throttlesRule : throttlesList) {
            final String requestThrottlesKey = constructRequestThrottleKey(request, throttlesRule);
            if (requestThrottlesKey == null) {
                continue;
            }
            if (throttlesRule.isDynamicThrottleEnabled()) {
                try {
                    final List<ThrottleRule> dynamicThrottleRuleList = securityFilterProperties.getSecurityProvider().getDynamicThrottleRuleList(request, actionRule, throttlesRule, throttlesRule.getName());
                    if (SecurityUtil.isValidList(dynamicThrottleRuleList)) {
                        throttlesRule = ThrottlesRule.createDynamicThrottlesRule(throttlesRule);
                        throttlesRule.addThrottleRules(dynamicThrottleRuleList);
                    }
                }
                catch (final Exception e) {
                    DoSController.LOGGER.log(Level.WARNING, "Exception occurred while getting dynamic throttle rule list. Error-msg: \"{0}\", {1}", new Object[] { e.getMessage(), throttlesRule });
                }
            }
            resultMap.put(requestThrottlesKey, throttlesRule);
        }
    }
    
    private static Map<ThrottlesRule.Windows, List<ThrottlesRule>> sortAndValidateThrottlesByWindow(final List<ThrottlesRule> throttlesList) {
        final Map<ThrottlesRule.Windows, List<ThrottlesRule>> dynamicThrottlesMap = new HashMap<ThrottlesRule.Windows, List<ThrottlesRule>>();
        for (final ThrottlesRule throttles : throttlesList) {
            if (!SecurityUtil.isValidMap(throttles.getThrottleRuleMap())) {
                throw new RuntimeException("Dynamic Throttles rule must have Throttle configuration.");
            }
            List<ThrottlesRule> windowThrottles = dynamicThrottlesMap.get(throttles.getWindow());
            if (windowThrottles == null) {
                dynamicThrottlesMap.put(throttles.getWindow(), windowThrottles = new ArrayList<ThrottlesRule>());
            }
            windowThrottles.add(throttles);
        }
        return dynamicThrottlesMap;
    }
    
    private static String constructRequestThrottleKey(final HttpServletRequest request, final ThrottlesRule throttlesRule) {
        final StringBuilder requestThrottleKeyBuilder = new StringBuilder();
        if (throttlesRule.getScope() == ThrottlesRule.Scopes.SERVICE) {
            requestThrottleKeyBuilder.append(SecurityFilterProperties.getServiceName() + "_" + throttlesRule.getWindow().ordinal());
        }
        final ActionRule actionRule = (ActionRule)request.getAttribute(SecurityRequestWrapper.ZsecAttributes.urlrule.name());
        final SecurityFilterProperties securityFilterProperties = SecurityFilterProperties.getInstance(request);
        final Map<ThrottlesRule.KeyType, List<String>> throttlesKeyMap = throttlesRule.getKeyMap();
        for (final ThrottlesRule.KeyType keyType : ThrottlesRule.KeyType.values()) {
            final List<String> keyNames = throttlesKeyMap.get(keyType);
            if (keyNames != null) {
                for (final String keyName : throttlesKeyMap.get(keyType)) {
                    String requestThrottleKey = null;
                    requestThrottleKeyBuilder.append("_");
                    requestThrottleKeyBuilder.append(keyType.getAliasName());
                    requestThrottleKeyBuilder.append(".");
                    switch (keyType) {
                        case PARAM: {
                            requestThrottleKey = request.getParameter(keyName);
                            requestThrottleKeyBuilder.append(keyName);
                            requestThrottleKeyBuilder.append("=");
                            break;
                        }
                        case HEADER: {
                            requestThrottleKey = request.getHeader(keyName);
                            requestThrottleKeyBuilder.append(keyName);
                            requestThrottleKeyBuilder.append("=");
                            break;
                        }
                        case URL: {
                            final ThrottlesRule.URLKeysName urlKeyName = ThrottlesRule.URLKeysName.valueOf(keyName);
                            requestThrottleKeyBuilder.append(urlKeyName.getAliasName());
                            requestThrottleKeyBuilder.append("=");
                            switch (urlKeyName) {
                                case PATH: {
                                    if (actionRule.hasOldThrottleConfiguration) {
                                        try {
                                            requestThrottleKey = securityFilterProperties.getSecurityProvider().getURLUniqueString(request, actionRule);
                                        }
                                        catch (final Exception e) {
                                            DoSController.LOGGER.log(Level.WARNING, "Exception occurred while getting dynamic unique url path, {0}", throttlesRule);
                                        }
                                    }
                                    if (!SecurityUtil.isValid(requestThrottleKey)) {
                                        requestThrottleKey = actionRule.getUrlUniquePath(SecurityUtil.getRequestPath(request));
                                        break;
                                    }
                                    break;
                                }
                                case DYNAMIC_KEY: {
                                    try {
                                        requestThrottleKey = securityFilterProperties.getSecurityProvider().getDynamicThrottlesKey(request, actionRule, throttlesRule, throttlesRule.getName());
                                    }
                                    catch (final Exception e) {
                                        DoSController.LOGGER.log(Level.WARNING, "Exception occurred while getting dynamic throttle key, Throttle key: {0}", throttlesRule);
                                    }
                                    break;
                                }
                            }
                            break;
                        }
                        case USER: {
                            final ThrottlesRule.UserKeysName userKeyName = ThrottlesRule.UserKeysName.valueOf(keyName);
                            requestThrottleKeyBuilder.append(userKeyName.getAliasName());
                            requestThrottleKeyBuilder.append("=");
                            switch (userKeyName) {
                                case REMOTEIP: {
                                    requestThrottleKey = ((SecurityRequestWrapper)request).getRemoteUserIPAddr();
                                    break;
                                }
                                case AUTHTOKEN:
                                case OAUTHTOKEN:
                                case ZUID: {
                                    requestThrottleKey = (securityFilterProperties.isAuthenticationProviderConfigured() ? securityFilterProperties.getAuthenticationProvider().getAuthenticatedUserId((SecurityRequestWrapper)request, userKeyName) : null);
                                    if (!SecurityUtil.isValid(requestThrottleKey) && actionRule.isOptionalURL()) {
                                        requestThrottleKey = ((SecurityRequestWrapper)request).getRemoteUserIPAddr();
                                        break;
                                    }
                                    break;
                                }
                            }
                            break;
                        }
                        case DYNAMICKEY: {
                            requestThrottleKeyBuilder.append(keyName);
                            requestThrottleKeyBuilder.append("=");
                            requestThrottleKey = securityFilterProperties.getSecurityProvider().getDynamicThrottlesKey(request, actionRule, throttlesRule, keyName, keyType);
                            break;
                        }
                        case PROXY: {
                            final ThrottlesRule.ProxyKeysName proxyKeyName = ThrottlesRule.ProxyKeysName.valueOf(keyName);
                            requestThrottleKeyBuilder.append(proxyKeyName.getAliasName());
                            requestThrottleKeyBuilder.append("=");
                            final ProxyInfo proxyInfo = ((SecurityRequestWrapper)request).getProxyInfo();
                            if (proxyInfo != null) {
                                switch (proxyKeyName) {
                                    case IP: {
                                        requestThrottleKey = proxyInfo.getProxyIP();
                                        break;
                                    }
                                    case NAME: {
                                        requestThrottleKey = proxyInfo.getProxyName();
                                        break;
                                    }
                                }
                                break;
                            }
                            break;
                        }
                    }
                    if (requestThrottleKey == null) {
                        return null;
                    }
                    requestThrottleKeyBuilder.append(requestThrottleKey);
                }
            }
        }
        return requestThrottleKeyBuilder.toString();
    }
    
    private static List<ThrottlesRule> getDynamicThrottlesRuleFromThreadLocal(final ThrottlesRule.Windows window) {
        return (DoSController.DYNAMIC_THROTTLES_RULE_MAP.get() != null) ? ((List)DoSController.DYNAMIC_THROTTLES_RULE_MAP.get().get(window)) : null;
    }
    
    private static void addAccessInfoToThreadLocal(final AccessInfo accessInfo) {
        if (accessInfo != null) {
            List<AccessInfo> urlAccessInfos = DoSController.URL_ACCESSINFO_LIST.get();
            if (urlAccessInfos == null) {
                urlAccessInfos = new ArrayList<AccessInfo>();
            }
            urlAccessInfos.add(accessInfo);
            DoSController.URL_ACCESSINFO_LIST.set(urlAccessInfos);
        }
    }
    
    public static List<AccessInfo> getAccessInfoListFromThreadLocal() {
        return DoSController.URL_ACCESSINFO_LIST.get();
    }
    
    private static void addRequestThrottleKeyList(final HttpServletRequest request, final String throttleKey, final String window) {
        final Object throttleKeyObj = request.getAttribute(window);
        final List<String> throttleKeys = (throttleKeyObj == null) ? new ArrayList<String>() : ((List)throttleKeyObj);
        throttleKeys.add(throttleKey);
        request.setAttribute(window, (Object)throttleKeys);
    }
    
    private static void throwError(final HttpServletRequest request, final HttpServletResponse response, final long currentRequestArrivalTimeInMillis, final AccessInfo accessInfo, final AccessInfoLock lock, final ActionRule actionRule) throws IAMSecurityException {
        if (hasDosCookie(request, "dck_" + SecurityUtil.getRequestPath(request))) {
            lock.logError(currentRequestArrivalTimeInMillis);
            throw new IAMSecurityException("URL_ROLLING_THROTTLES_LIMIT_EXCEEDED", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), accessInfo, lock);
        }
        if (actionRule.isDosCookieRequired() && lock.getLockType() == ThrottleRule.LockType.TIME) {
            final int cookieMaxAge = (lock.getViolatedThrottleWindow() == ThrottlesRule.Windows.FIXED) ? ((int)((FixedWindowAccessInfoLock)lock).getLockPeriodInMillis() / 1000) : ((int)((RollingWindowAccessInfoLock)lock).getLockPeriodInMillis() / 1000);
            addDosCookie(request, response, "dck_" + SecurityUtil.getRequestPath(request), "true", SecurityUtil.getRequestPath(request), cookieMaxAge, false);
        }
        switch (lock.getViolatedThrottleWindow()) {
            case FIXED: {
                if (!accessInfo.isLockPeriodExpired(currentRequestArrivalTimeInMillis, lock)) {
                    logAndThrowError(request, accessInfo, lock, "URL_FIXED_THROTTLES_LIMIT_EXCEEDED", currentRequestArrivalTimeInMillis);
                    break;
                }
                break;
            }
            case ROLLING: {
                if (lock.getLockType() == ThrottleRule.LockType.TIME) {
                    if (!accessInfo.isLockPeriodExpired(currentRequestArrivalTimeInMillis, lock)) {
                        logAndThrowError(request, accessInfo, lock, "URL_ROLLING_THROTTLES_LIMIT_EXCEEDED", currentRequestArrivalTimeInMillis);
                        break;
                    }
                    break;
                }
                else {
                    if (accessInfo.verifyHip(((SecurityRequestWrapper)request).getParameterForValidation(((RollingWindowAccessInfoLock)lock).getHipDigest())) != null) {
                        logAndThrowError(request, accessInfo, lock, "HIP_REQUIRED", currentRequestArrivalTimeInMillis);
                        break;
                    }
                    break;
                }
                break;
            }
            case SLIDING: {
                logAndThrowError(request, accessInfo, lock, "URL_THROTTLES_LIMIT_EXCEEDED", currentRequestArrivalTimeInMillis);
                break;
            }
        }
        accessInfo.unLock();
        if (hasDosCookie(request, "dck_" + SecurityUtil.getRequestPath(request))) {
            addDosCookie(request, response, "dck_" + SecurityUtil.getRequestPath(request), "true", SecurityUtil.getRequestPath(request), 0, false);
        }
    }
    
    private static void logAndThrowError(final HttpServletRequest request, final AccessInfo accessInfo, final AccessInfoLock lock, final String errorCode, final long currentRequestArrivalTimeInMillis) throws IAMSecurityException {
        if (currentRequestArrivalTimeInMillis == lock.getViolatedTimeInMillis()) {
            lock.logError(currentRequestArrivalTimeInMillis);
        }
        throw new IAMSecurityException(errorCode, request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), accessInfo, lock);
    }
    
    static long calculateLockFactor(final int count, final int limit, final int lockFactor) {
        long factor = 1L;
        for (int i = 0; i < count - limit; ++i) {
            factor *= lockFactor;
        }
        return factor;
    }
    
    public static String sha512Hex(final String plainText) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
            md.update(plainText.getBytes());
        }
        catch (final NoSuchAlgorithmException ex) {}
        return SecurityUtil.BASE16_ENCODE(md.digest());
    }
    
    private static void addAPIRateLimitResponseHeader(final HttpServletRequest request, final HttpServletResponse response, final SecurityFilterProperties securityFilterProperties, final ActionRule actionRule, final List<AccessInfo> accessInfos, final long currentRequestArrivalTime) {
        final JSONArray resultArray = new JSONArray();
        for (final AccessInfo accessInfo : accessInfos) {
            try {
                final ThrottlesRule throttles = accessInfo.getThrottlesRule();
                if (throttles.getWindow() == ThrottlesRule.Windows.LIVE) {
                    continue;
                }
                for (final ThrottleRule throttle : throttles.getThrottleRuleMap().values()) {
                    resultArray.put((Object)getAPIRateLimitThrottleObject(request, response, accessInfo, throttle, throttles.getWindow(), currentRequestArrivalTime));
                }
            }
            catch (final Exception ex) {
                DoSController.LOGGER.log(Level.SEVERE, "Exception occurred while adding rate limit response header. Exception: {0}, CacheConfiguration: {1}, ThrottlesRule: {2}", new Object[] { ex.getMessage(), ((DbCacheAccessInfo)accessInfo).getCacheConfiguration(), accessInfo.getThrottlesRule() });
                if (securityFilterProperties.isBlockRequestOnDosCacheException()) {
                    throw ex;
                }
                ((DbCacheAccessInfo)accessInfo).setCacheException(ex);
            }
        }
        response.addHeader("X-Rate-Limit", resultArray.toString());
    }
    
    private static JSONObject getAPIRateLimitThrottleObject(final HttpServletRequest request, final HttpServletResponse response, final AccessInfo accessInfo, final ThrottleRule throttle, final ThrottlesRule.Windows throttlesWindow, final long currentRequestArrivalTime) {
        final int remainingAccessCount = throttle.getThreshold() - accessInfo.getAccessCount(throttle);
        long retryAfter = -1L;
        final AccessInfoLock lock = accessInfo.getLock();
        if (remainingAccessCount == 0 && (lock == null || lock.getLockType() != ThrottleRule.LockType.HIP)) {
            if (lock == null || throttlesWindow == ThrottlesRule.Windows.SLIDING || lock.getViolatedThrottle() != throttle) {
                retryAfter = throttle.getDuration();
            }
            else {
                retryAfter = ((throttlesWindow == ThrottlesRule.Windows.FIXED) ? ((FixedWindowAccessInfoLock)accessInfo.getLock()).getRemainingLockPeriodInMillis(currentRequestArrivalTime) : ((RollingWindowAccessInfoLock)accessInfo.getLock()).getRemainingLockPeriodInMillis(currentRequestArrivalTime));
            }
        }
        final JSONObject resultObj = new JSONObject();
        resultObj.put(ThrottlesRule.ApiRateLimitResponseHeaderValueFields.duration.getHeader(), throttle.getDuration() / 1000L);
        resultObj.put(ThrottlesRule.ApiRateLimitResponseHeaderValueFields.remaining_count.getHeader(), remainingAccessCount);
        if (retryAfter != -1L) {
            resultObj.put(ThrottlesRule.ApiRateLimitResponseHeaderValueFields.retry_after.getHeader(), retryAfter / 1000L);
        }
        return resultObj;
    }
    
    static {
        LOGGER = Logger.getLogger(DoSController.class.getName());
        URL_ACCESSINFO_LIST = new ThreadLocal<List<AccessInfo>>();
        DYNAMIC_THROTTLES_RULE_MAP = new ThreadLocal<Map<ThrottlesRule.Windows, List<ThrottlesRule>>>();
        FRS_WINDOWS = new ThrottlesRule.Windows[] { ThrottlesRule.Windows.FIXED, ThrottlesRule.Windows.ROLLING, ThrottlesRule.Windows.SLIDING };
        LIVE_WINDOW = new ThrottlesRule.Windows[] { ThrottlesRule.Windows.LIVE };
        DoSController.host_local_address = null;
    }
}
