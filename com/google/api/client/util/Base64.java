package com.google.api.client.util;

import com.google.common.io.BaseEncoding;

@Deprecated
public class Base64
{
    public static byte[] encodeBase64(final byte[] binaryData) {
        return StringUtils.getBytesUtf8(encodeBase64String(binaryData));
    }
    
    public static String encodeBase64String(final byte[] binaryData) {
        if (binaryData == null) {
            return null;
        }
        return BaseEncoding.base64().encode(binaryData);
    }
    
    public static byte[] encodeBase64URLSafe(final byte[] binaryData) {
        return StringUtils.getBytesUtf8(encodeBase64URLSafeString(binaryData));
    }
    
    public static String encodeBase64URLSafeString(final byte[] binaryData) {
        if (binaryData == null) {
            return null;
        }
        return BaseEncoding.base64Url().omitPadding().encode(binaryData);
    }
    
    public static byte[] decodeBase64(final byte[] base64Data) {
        return decodeBase64(StringUtils.newStringUtf8(base64Data));
    }
    
    public static byte[] decodeBase64(final String base64String) {
        if (base64String == null) {
            return null;
        }
        try {
            return BaseEncoding.base64().decode((CharSequence)base64String);
        }
        catch (final IllegalArgumentException e) {
            if (e.getCause() instanceof BaseEncoding.DecodingException) {
                return BaseEncoding.base64Url().decode((CharSequence)base64String.trim());
            }
            throw e;
        }
    }
    
    private Base64() {
    }
}
