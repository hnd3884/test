package io.opencensus.resource;

import io.opencensus.internal.StringUtils;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.LinkedHashMap;
import io.opencensus.internal.Utils;
import java.util.Map;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class Resource
{
    static final int MAX_LENGTH = 255;
    private static final String OC_RESOURCE_TYPE_ENV = "OC_RESOURCE_TYPE";
    private static final String OC_RESOURCE_LABELS_ENV = "OC_RESOURCE_LABELS";
    private static final String LABEL_LIST_SPLITTER = ",";
    private static final String LABEL_KEY_VALUE_SPLITTER = "=";
    private static final String ERROR_MESSAGE_INVALID_CHARS = " should be a ASCII string with a length greater than 0 and not exceed 255 characters.";
    private static final String ERROR_MESSAGE_INVALID_VALUE = " should be a ASCII string with a length not exceed 255 characters.";
    @Nullable
    private static final String ENV_TYPE;
    private static final Map<String, String> ENV_LABEL_MAP;
    
    Resource() {
    }
    
    @Nullable
    public abstract String getType();
    
    public abstract Map<String, String> getLabels();
    
    public static Resource createFromEnvironmentVariables() {
        return createInternal(Resource.ENV_TYPE, Resource.ENV_LABEL_MAP);
    }
    
    public static Resource create(@Nullable final String type, final Map<String, String> labels) {
        return createInternal(type, Collections.unmodifiableMap((Map<? extends String, ? extends String>)new LinkedHashMap<String, String>(Utils.checkNotNull(labels, "labels"))));
    }
    
    @Nullable
    public static Resource mergeResources(final List<Resource> resources) {
        Resource currentResource = null;
        for (final Resource resource : resources) {
            currentResource = merge(currentResource, resource);
        }
        return currentResource;
    }
    
    private static Resource createInternal(@Nullable final String type, final Map<String, String> labels) {
        return new AutoValue_Resource(type, labels);
    }
    
    @Nullable
    static String parseResourceType(@Nullable final String rawEnvType) {
        if (rawEnvType != null && !rawEnvType.isEmpty()) {
            Utils.checkArgument(isValidAndNotEmpty(rawEnvType), (Object)"Type should be a ASCII string with a length greater than 0 and not exceed 255 characters.");
            return rawEnvType.trim();
        }
        return rawEnvType;
    }
    
    static Map<String, String> parseResourceLabels(@Nullable final String rawEnvLabels) {
        if (rawEnvLabels == null) {
            return Collections.emptyMap();
        }
        final Map<String, String> labels = new HashMap<String, String>();
        final String[] split;
        final String[] rawLabels = split = rawEnvLabels.split(",", -1);
        for (final String rawLabel : split) {
            final String[] keyValuePair = rawLabel.split("=", -1);
            if (keyValuePair.length == 2) {
                final String key = keyValuePair[0].trim();
                final String value = keyValuePair[1].trim().replaceAll("^\"|\"$", "");
                Utils.checkArgument(isValidAndNotEmpty(key), (Object)"Label key should be a ASCII string with a length greater than 0 and not exceed 255 characters.");
                Utils.checkArgument(isValid(value), (Object)"Label value should be a ASCII string with a length not exceed 255 characters.");
                labels.put(key, value);
            }
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends String>)labels);
    }
    
    @Nullable
    private static Resource merge(@Nullable final Resource resource, @Nullable final Resource otherResource) {
        if (otherResource == null) {
            return resource;
        }
        if (resource == null) {
            return otherResource;
        }
        final String mergedType = (resource.getType() != null) ? resource.getType() : otherResource.getType();
        final Map<String, String> mergedLabelMap = new LinkedHashMap<String, String>(otherResource.getLabels());
        for (final Map.Entry<String, String> entry : resource.getLabels().entrySet()) {
            mergedLabelMap.put(entry.getKey(), entry.getValue());
        }
        return createInternal(mergedType, Collections.unmodifiableMap((Map<? extends String, ? extends String>)mergedLabelMap));
    }
    
    private static boolean isValid(final String name) {
        return name.length() <= 255 && StringUtils.isPrintableString(name);
    }
    
    private static boolean isValidAndNotEmpty(final String name) {
        return !name.isEmpty() && isValid(name);
    }
    
    static {
        ENV_TYPE = parseResourceType(System.getenv("OC_RESOURCE_TYPE"));
        ENV_LABEL_MAP = parseResourceLabels(System.getenv("OC_RESOURCE_LABELS"));
    }
}
