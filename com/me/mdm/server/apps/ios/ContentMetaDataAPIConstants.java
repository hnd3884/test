package com.me.mdm.server.apps.ios;

public class ContentMetaDataAPIConstants
{
    public static final String KEY_APP_NAME = "name";
    public static final String KEY_APP_BUNDLE_ID = "bundleId";
    public static final String KEY_APP_RELEASE_DATE = "latestVersionReleaseDate";
    public static final String KEY_APP_SELLER_NAME = "artistName";
    public static final String KEY_APP_GENRE_NAME = "genreNames";
    public static final String KEY_APP_STORE_URL = "url";
    public static final String KEY_APP_DESCRIPTION = "standard";
    public static final String KEY_APP_PRICE = "price";
    public static final String KEY_APP_DEVICE_FAMILY = "deviceFamilies";
    public static final String KEY_APP_EXTERNAL_APP_VERSION_ID = "externalId";
    public static final String KEY_APP_MINIMUM_OS_VERSION = "minimumOSVersion";
    public static final String KEY_APP_VERSION = "display";
    public static final String KEY_APP_ADAM_ID = "id";
    public static final String KEY_APP_ICON = "url";
    public static final String KEY_APP_ICON_WIDTH = "100";
    public static final String KEY_APP_ICON_HEIGHT = "100";
    public static final String KEY_APP_ICON_FORMAT_JPG = "jpg";
    public static final String KEY_VERSION = "version";
    public static final String KEY_RESULT = "results";
    public static final String KEY_ARTWORK = "artwork";
    public static final String KEY_OFFERS = "offers";
    public static final String KEY_DESCRIPTION = "description";
    public static final Double KEY_DEFAULT_PRICE;
    public static final String KEY_DEFAULT_VERSION = "1";
    public static final Long KEY_DEFAULT_EXTERNAL_APP_VERSION_ID;
    
    static {
        KEY_DEFAULT_PRICE = 0.0;
        KEY_DEFAULT_EXTERNAL_APP_VERSION_ID = 0L;
    }
}
