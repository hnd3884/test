package org.apache.tika.metadata;

public interface WordPerfect
{
    public static final String WORDPERFECT_METADATA_NAME_PREFIX = "wordperfect";
    public static final Property FILE_SIZE = Property.internalText("wordperfect:FileSize");
    public static final Property FILE_ID = Property.internalText("wordperfect:FileId");
    public static final Property PRODUCT_TYPE = Property.internalInteger("wordperfect:ProductType");
    public static final Property FILE_TYPE = Property.internalInteger("wordperfect:FileType");
    public static final Property MAJOR_VERSION = Property.internalInteger("wordperfect:MajorVersion");
    public static final Property MINOR_VERSION = Property.internalInteger("wordperfect:MinorVersion");
    public static final Property ENCRYPTED = Property.internalBoolean("wordperfect:Encrypted");
}
