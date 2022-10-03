package org.apache.xerces.impl.xs.assertion;

public class XSAssertConstants
{
    public static String assertList;
    public static String isAttrHaveAsserts;
    public static String isAssertProcNeededForUnionElem;
    public static String isAssertProcNeededForUnionAttr;
    
    static {
        XSAssertConstants.assertList = "ASSERT_LIST";
        XSAssertConstants.isAttrHaveAsserts = "ATTRIBUTES_HAVE_ASSERTS";
        XSAssertConstants.isAssertProcNeededForUnionElem = "ASSERT_PROC_NEEDED_FOR_UNION_ELEM";
        XSAssertConstants.isAssertProcNeededForUnionAttr = "ASSERT_PROC_NEEDED_FOR_UNION_ATTR";
    }
}
