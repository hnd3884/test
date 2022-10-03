package org.apache.tika.metadata;

public interface RTFMetadata
{
    public static final String PREFIX_RTF_META = "rtf_meta";
    public static final String RTF_PICT_META_PREFIX = "rtf_pict:";
    public static final Property THUMBNAIL = Property.internalBoolean("rtf_meta:thumbnail");
    public static final Property EMB_APP_VERSION = Property.internalText("rtf_meta:emb_app_version");
    public static final Property EMB_CLASS = Property.internalText("rtf_meta:emb_class");
    public static final Property EMB_TOPIC = Property.internalText("rtf_meta:emb_topic");
    public static final Property EMB_ITEM = Property.internalText("rtf_meta:emb_item");
}
