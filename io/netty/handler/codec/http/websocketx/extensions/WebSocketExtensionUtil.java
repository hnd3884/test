package io.netty.handler.codec.http.websocketx.extensions;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import java.util.regex.Pattern;

public final class WebSocketExtensionUtil
{
    private static final String EXTENSION_SEPARATOR = ",";
    private static final String PARAMETER_SEPARATOR = ";";
    private static final char PARAMETER_EQUAL = '=';
    private static final Pattern PARAMETER;
    
    static boolean isWebsocketUpgrade(final HttpHeaders headers) {
        return headers.contains(HttpHeaderNames.UPGRADE) && headers.containsValue(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE, true) && headers.contains(HttpHeaderNames.UPGRADE, HttpHeaderValues.WEBSOCKET, true);
    }
    
    public static List<WebSocketExtensionData> extractExtensions(final String extensionHeader) {
        final String[] rawExtensions = extensionHeader.split(",");
        if (rawExtensions.length > 0) {
            final List<WebSocketExtensionData> extensions = new ArrayList<WebSocketExtensionData>(rawExtensions.length);
            for (final String rawExtension : rawExtensions) {
                final String[] extensionParameters = rawExtension.split(";");
                final String name = extensionParameters[0].trim();
                Map<String, String> parameters;
                if (extensionParameters.length > 1) {
                    parameters = new HashMap<String, String>(extensionParameters.length - 1);
                    for (int i = 1; i < extensionParameters.length; ++i) {
                        final String parameter = extensionParameters[i].trim();
                        final Matcher parameterMatcher = WebSocketExtensionUtil.PARAMETER.matcher(parameter);
                        if (parameterMatcher.matches() && parameterMatcher.group(1) != null) {
                            parameters.put(parameterMatcher.group(1), parameterMatcher.group(3));
                        }
                    }
                }
                else {
                    parameters = Collections.emptyMap();
                }
                extensions.add(new WebSocketExtensionData(name, parameters));
            }
            return extensions;
        }
        return Collections.emptyList();
    }
    
    static String computeMergeExtensionsHeaderValue(final String userDefinedHeaderValue, final List<WebSocketExtensionData> extraExtensions) {
        final List<WebSocketExtensionData> userDefinedExtensions = (userDefinedHeaderValue != null) ? extractExtensions(userDefinedHeaderValue) : Collections.emptyList();
        for (final WebSocketExtensionData userDefined : userDefinedExtensions) {
            WebSocketExtensionData matchingExtra = null;
            int i;
            for (i = 0; i < extraExtensions.size(); ++i) {
                final WebSocketExtensionData extra = extraExtensions.get(i);
                if (extra.name().equals(userDefined.name())) {
                    matchingExtra = extra;
                    break;
                }
            }
            if (matchingExtra == null) {
                extraExtensions.add(userDefined);
            }
            else {
                final Map<String, String> mergedParameters = new HashMap<String, String>(matchingExtra.parameters());
                mergedParameters.putAll(userDefined.parameters());
                extraExtensions.set(i, new WebSocketExtensionData(matchingExtra.name(), mergedParameters));
            }
        }
        final StringBuilder sb = new StringBuilder(150);
        for (final WebSocketExtensionData data : extraExtensions) {
            sb.append(data.name());
            for (final Map.Entry<String, String> parameter : data.parameters().entrySet()) {
                sb.append(";");
                sb.append(parameter.getKey());
                if (parameter.getValue() != null) {
                    sb.append('=');
                    sb.append(parameter.getValue());
                }
            }
            sb.append(",");
        }
        if (!extraExtensions.isEmpty()) {
            sb.setLength(sb.length() - ",".length());
        }
        return sb.toString();
    }
    
    private WebSocketExtensionUtil() {
    }
    
    static {
        PARAMETER = Pattern.compile("^([^=]+)(=[\\\"]?([^\\\"]+)[\\\"]?)?$");
    }
}
