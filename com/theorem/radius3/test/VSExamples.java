package com.theorem.radius3.test;

import com.theorem.radius3.A;
import com.theorem.radius3.Ascend;
import com.theorem.radius3.Cisco;
import com.theorem.radius3.Attribute;
import com.theorem.radius3.VendorSpecific;
import com.theorem.radius3.AttributeList;

public class VSExamples
{
    public final AttributeList createVS_One() {
        final VendorSpecific vendorSpecific = new VendorSpecific(529);
        vendorSpecific.addAttribute(18, 5);
        new VendorSpecific(529).addAttribute(240, 100);
        final AttributeList list = new AttributeList();
        list.addAttribute(vendorSpecific.getAttribute());
        list.addAttribute(vendorSpecific.getAttribute());
        return list;
    }
    
    public final AttributeList createVS_Two() {
        final VendorSpecific vendorSpecific = new VendorSpecific(529);
        vendorSpecific.addAttribute(18, 5);
        vendorSpecific.addAttribute(240, 100);
        final AttributeList list = new AttributeList();
        list.addAttribute(vendorSpecific.getAttribute());
        return list;
    }
    
    public final AttributeList createCisco() {
        final AttributeList list = new AttributeList();
        final VendorSpecific vendorSpecific = new VendorSpecific(9);
        vendorSpecific.addAttribute(1, "323_credit_amount=1");
        list.addAttribute(vendorSpecific.getAttribute());
        final VendorSpecific vendorSpecific2 = new VendorSpecific(9);
        vendorSpecific2.addAttribute(1, "h323-currency=2");
        list.addAttribute(vendorSpecific2.getAttribute());
        return list;
    }
    
    public final void extractAscend(final AttributeList list) {
        final VendorSpecific[] vendorSpecific = list.getVendorSpecific(529);
        for (int i = 0; i < vendorSpecific.length; ++i) {
            final VendorSpecific vendorSpecific2 = vendorSpecific[i];
            for (int size = vendorSpecific2.size(), j = 0; j < size; ++j) {
                final Attribute attribute = vendorSpecific2.getAttributeAt(j);
                switch (attribute.getTag()) {
                    case 81: {
                        attribute.getInt();
                    }
                }
            }
        }
    }
    
    public final void longTags() {
        System.out.println("Short tags, regular VSA");
        final VendorSpecific vendorSpecific = new VendorSpecific(529);
        vendorSpecific.addAttribute(99, 9);
        System.out.println(vendorSpecific);
        System.out.println();
        final VendorSpecific vendorSpecific2 = new VendorSpecific(529, true);
        vendorSpecific2.addAttribute(999, 9);
        System.out.println("Ascend VSA with long tags:\n" + vendorSpecific2);
        final Attribute attribute = vendorSpecific2.getAttribute();
        System.out.println("Ascend long tag vsa: \n" + vendorSpecific2);
        System.out.println("Ascend long tag vsa: \n" + new VendorSpecific(attribute).setLongTag());
    }
    
    public static void main(final String[] array) {
        try {
            final VSExamples vsExamples = new VSExamples();
            new Cisco();
            new Ascend();
            new A();
            vsExamples.a();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private final void a() throws Exception {
        this.longTags();
    }
}
