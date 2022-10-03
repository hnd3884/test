package org.apache.tika.metadata;

public interface QuattroPro
{
    public static final String QUATTROPRO_METADATA_NAME_PREFIX = "wordperfect";
    public static final Property ID = Property.internalText("wordperfect:Id");
    public static final Property VERSION = Property.internalInteger("wordperfect:Version");
    public static final Property BUILD = Property.internalInteger("wordperfect:Build");
    public static final Property LOWEST_VERSION = Property.internalInteger("wordperfect:LowestVersion");
}
