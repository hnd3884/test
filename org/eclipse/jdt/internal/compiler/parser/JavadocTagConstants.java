package org.eclipse.jdt.internal.compiler.parser;

import org.eclipse.jdt.core.compiler.CharOperation;

public interface JavadocTagConstants
{
    public static final char[] TAG_DEPRECATED = "deprecated".toCharArray();
    public static final char[] TAG_PARAM = "param".toCharArray();
    public static final char[] TAG_RETURN = "return".toCharArray();
    public static final char[] TAG_THROWS = "throws".toCharArray();
    public static final char[] TAG_EXCEPTION = "exception".toCharArray();
    public static final char[] TAG_SEE = "see".toCharArray();
    public static final char[] TAG_LINK = "link".toCharArray();
    public static final char[] TAG_LINKPLAIN = "linkplain".toCharArray();
    public static final char[] TAG_INHERITDOC = "inheritDoc".toCharArray();
    public static final char[] TAG_VALUE = "value".toCharArray();
    public static final char[] TAG_AUTHOR = "author".toCharArray();
    public static final char[] TAG_CODE = "code".toCharArray();
    public static final char[] TAG_DOC_ROOT = "docRoot".toCharArray();
    public static final char[] TAG_LITERAL = "literal".toCharArray();
    public static final char[] TAG_SERIAL = "serial".toCharArray();
    public static final char[] TAG_SERIAL_DATA = "serialData".toCharArray();
    public static final char[] TAG_SERIAL_FIELD = "serialField".toCharArray();
    public static final char[] TAG_SINCE = "since".toCharArray();
    public static final char[] TAG_VERSION = "version".toCharArray();
    public static final char[] TAG_CATEGORY = "category".toCharArray();
    public static final int TAG_DEPRECATED_LENGTH = JavadocTagConstants.TAG_DEPRECATED.length;
    public static final int TAG_PARAM_LENGTH = JavadocTagConstants.TAG_PARAM.length;
    public static final int TAG_RETURN_LENGTH = JavadocTagConstants.TAG_RETURN.length;
    public static final int TAG_THROWS_LENGTH = JavadocTagConstants.TAG_THROWS.length;
    public static final int TAG_EXCEPTION_LENGTH = JavadocTagConstants.TAG_EXCEPTION.length;
    public static final int TAG_SEE_LENGTH = JavadocTagConstants.TAG_SEE.length;
    public static final int TAG_LINK_LENGTH = JavadocTagConstants.TAG_LINK.length;
    public static final int TAG_LINKPLAIN_LENGTH = JavadocTagConstants.TAG_LINKPLAIN.length;
    public static final int TAG_INHERITDOC_LENGTH = JavadocTagConstants.TAG_INHERITDOC.length;
    public static final int TAG_VALUE_LENGTH = JavadocTagConstants.TAG_VALUE.length;
    public static final int TAG_CATEGORY_LENGTH = JavadocTagConstants.TAG_CATEGORY.length;
    public static final int TAG_AUTHOR_LENGTH = JavadocTagConstants.TAG_AUTHOR.length;
    public static final int TAG_SERIAL_LENGTH = JavadocTagConstants.TAG_SERIAL.length;
    public static final int TAG_SERIAL_DATA_LENGTH = JavadocTagConstants.TAG_SERIAL_DATA.length;
    public static final int TAG_SERIAL_FIELD_LENGTH = JavadocTagConstants.TAG_SERIAL_FIELD.length;
    public static final int TAG_SINCE_LENGTH = JavadocTagConstants.TAG_SINCE.length;
    public static final int TAG_VERSION_LENGTH = JavadocTagConstants.TAG_VERSION.length;
    public static final int TAG_CODE_LENGTH = JavadocTagConstants.TAG_CODE.length;
    public static final int TAG_LITERAL_LENGTH = JavadocTagConstants.TAG_LITERAL.length;
    public static final int TAG_DOC_ROOT_LENGTH = JavadocTagConstants.TAG_DOC_ROOT.length;
    public static final int NO_TAG_VALUE = 0;
    public static final int TAG_DEPRECATED_VALUE = 1;
    public static final int TAG_PARAM_VALUE = 2;
    public static final int TAG_RETURN_VALUE = 3;
    public static final int TAG_THROWS_VALUE = 4;
    public static final int TAG_EXCEPTION_VALUE = 5;
    public static final int TAG_SEE_VALUE = 6;
    public static final int TAG_LINK_VALUE = 7;
    public static final int TAG_LINKPLAIN_VALUE = 8;
    public static final int TAG_INHERITDOC_VALUE = 9;
    public static final int TAG_VALUE_VALUE = 10;
    public static final int TAG_CATEGORY_VALUE = 11;
    public static final int TAG_AUTHOR_VALUE = 12;
    public static final int TAG_SERIAL_VALUE = 13;
    public static final int TAG_SERIAL_DATA_VALUE = 14;
    public static final int TAG_SERIAL_FIELD_VALUE = 15;
    public static final int TAG_SINCE_VALUE = 16;
    public static final int TAG_VERSION_VALUE = 17;
    public static final int TAG_CODE_VALUE = 18;
    public static final int TAG_LITERAL_VALUE = 19;
    public static final int TAG_DOC_ROOT_VALUE = 20;
    public static final int TAG_OTHERS_VALUE = 100;
    public static final char[][] TAG_NAMES = { CharOperation.NO_CHAR, JavadocTagConstants.TAG_DEPRECATED, JavadocTagConstants.TAG_PARAM, JavadocTagConstants.TAG_RETURN, JavadocTagConstants.TAG_THROWS, JavadocTagConstants.TAG_EXCEPTION, JavadocTagConstants.TAG_SEE, JavadocTagConstants.TAG_LINK, JavadocTagConstants.TAG_LINKPLAIN, JavadocTagConstants.TAG_INHERITDOC, JavadocTagConstants.TAG_VALUE, JavadocTagConstants.TAG_CATEGORY, JavadocTagConstants.TAG_AUTHOR, JavadocTagConstants.TAG_SERIAL, JavadocTagConstants.TAG_SERIAL_DATA, JavadocTagConstants.TAG_SERIAL_FIELD, JavadocTagConstants.TAG_SINCE, JavadocTagConstants.TAG_VERSION, JavadocTagConstants.TAG_CODE, JavadocTagConstants.TAG_LITERAL, JavadocTagConstants.TAG_DOC_ROOT };
    public static final int ORDERED_TAGS_NUMBER = 3;
    public static final int PARAM_TAG_EXPECTED_ORDER = 0;
    public static final int THROWS_TAG_EXPECTED_ORDER = 1;
    public static final int SEE_TAG_EXPECTED_ORDER = 2;
    public static final int BLOCK_IDX = 0;
    public static final int INLINE_IDX = 1;
    public static final char[] HREF_TAG = { 'h', 'r', 'e', 'f' };
    public static final char[][][] BLOCK_TAGS = { { JavadocTagConstants.TAG_AUTHOR, JavadocTagConstants.TAG_DEPRECATED, JavadocTagConstants.TAG_EXCEPTION, JavadocTagConstants.TAG_PARAM, JavadocTagConstants.TAG_RETURN, JavadocTagConstants.TAG_SEE, JavadocTagConstants.TAG_VERSION, JavadocTagConstants.TAG_CATEGORY }, { JavadocTagConstants.TAG_SINCE }, { JavadocTagConstants.TAG_SERIAL, JavadocTagConstants.TAG_SERIAL_DATA, JavadocTagConstants.TAG_SERIAL_FIELD, JavadocTagConstants.TAG_THROWS }, new char[0][], new char[0][], new char[0][], new char[0][], new char[0][], new char[0][] };
    public static final char[][][] INLINE_TAGS = { new char[0][], new char[0][], { JavadocTagConstants.TAG_LINK }, { JavadocTagConstants.TAG_DOC_ROOT }, { JavadocTagConstants.TAG_INHERITDOC, JavadocTagConstants.TAG_LINKPLAIN, JavadocTagConstants.TAG_VALUE }, { JavadocTagConstants.TAG_CODE, JavadocTagConstants.TAG_LITERAL }, new char[0][], new char[0][], new char[0][] };
    public static final int INLINE_TAGS_LENGTH = JavadocTagConstants.INLINE_TAGS.length;
    public static final int BLOCK_TAGS_LENGTH = JavadocTagConstants.BLOCK_TAGS.length;
    public static final int ALL_TAGS_LENGTH = JavadocTagConstants.BLOCK_TAGS_LENGTH + JavadocTagConstants.INLINE_TAGS_LENGTH;
    public static final short TAG_TYPE_NONE = 0;
    public static final short TAG_TYPE_INLINE = 1;
    public static final short TAG_TYPE_BLOCK = 2;
    public static final short[] JAVADOC_TAG_TYPE = { 0, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1 };
    public static final char[][] PACKAGE_TAGS = { JavadocTagConstants.TAG_SEE, JavadocTagConstants.TAG_SINCE, JavadocTagConstants.TAG_SERIAL, JavadocTagConstants.TAG_AUTHOR, JavadocTagConstants.TAG_VERSION, JavadocTagConstants.TAG_CATEGORY, JavadocTagConstants.TAG_LINK, JavadocTagConstants.TAG_LINKPLAIN, JavadocTagConstants.TAG_DOC_ROOT, JavadocTagConstants.TAG_VALUE };
    public static final char[][] COMPILATION_UNIT_TAGS = new char[0][];
    public static final char[][] CLASS_TAGS = { JavadocTagConstants.TAG_SEE, JavadocTagConstants.TAG_SINCE, JavadocTagConstants.TAG_DEPRECATED, JavadocTagConstants.TAG_SERIAL, JavadocTagConstants.TAG_AUTHOR, JavadocTagConstants.TAG_VERSION, JavadocTagConstants.TAG_PARAM, JavadocTagConstants.TAG_CATEGORY, JavadocTagConstants.TAG_LINK, JavadocTagConstants.TAG_LINKPLAIN, JavadocTagConstants.TAG_DOC_ROOT, JavadocTagConstants.TAG_VALUE, JavadocTagConstants.TAG_CODE, JavadocTagConstants.TAG_LITERAL };
    public static final char[][] FIELD_TAGS = { JavadocTagConstants.TAG_SEE, JavadocTagConstants.TAG_SINCE, JavadocTagConstants.TAG_DEPRECATED, JavadocTagConstants.TAG_SERIAL, JavadocTagConstants.TAG_SERIAL_FIELD, JavadocTagConstants.TAG_CATEGORY, JavadocTagConstants.TAG_LINK, JavadocTagConstants.TAG_LINKPLAIN, JavadocTagConstants.TAG_DOC_ROOT, JavadocTagConstants.TAG_VALUE, JavadocTagConstants.TAG_CODE, JavadocTagConstants.TAG_LITERAL };
    public static final char[][] METHOD_TAGS = { JavadocTagConstants.TAG_SEE, JavadocTagConstants.TAG_SINCE, JavadocTagConstants.TAG_DEPRECATED, JavadocTagConstants.TAG_PARAM, JavadocTagConstants.TAG_RETURN, JavadocTagConstants.TAG_THROWS, JavadocTagConstants.TAG_EXCEPTION, JavadocTagConstants.TAG_SERIAL_DATA, JavadocTagConstants.TAG_CATEGORY, JavadocTagConstants.TAG_LINK, JavadocTagConstants.TAG_LINKPLAIN, JavadocTagConstants.TAG_INHERITDOC, JavadocTagConstants.TAG_DOC_ROOT, JavadocTagConstants.TAG_VALUE, JavadocTagConstants.TAG_CODE, JavadocTagConstants.TAG_LITERAL };
}
