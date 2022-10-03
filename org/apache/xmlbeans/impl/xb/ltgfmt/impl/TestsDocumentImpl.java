package org.apache.xmlbeans.impl.xb.ltgfmt.impl;

import java.util.List;
import java.util.ArrayList;
import org.apache.xmlbeans.impl.xb.ltgfmt.TestCase;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.ltgfmt.TestsDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class TestsDocumentImpl extends XmlComplexContentImpl implements TestsDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName TESTS$0;
    
    public TestsDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public Tests getTests() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Tests target = null;
            target = (Tests)this.get_store().find_element_user(TestsDocumentImpl.TESTS$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setTests(final Tests tests) {
        this.generatedSetterHelperImpl(tests, TestsDocumentImpl.TESTS$0, 0, (short)1);
    }
    
    @Override
    public Tests addNewTests() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Tests target = null;
            target = (Tests)this.get_store().add_element_user(TestsDocumentImpl.TESTS$0);
            return target;
        }
    }
    
    static {
        TESTS$0 = new QName("http://www.bea.com/2003/05/xmlbean/ltgfmt", "tests");
    }
    
    public static class TestsImpl extends XmlComplexContentImpl implements Tests
    {
        private static final long serialVersionUID = 1L;
        private static final QName TEST$0;
        
        public TestsImpl(final SchemaType sType) {
            super(sType);
        }
        
        @Override
        public TestCase[] getTestArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(TestsImpl.TEST$0, targetList);
                final TestCase[] result = new TestCase[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public TestCase getTestArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                TestCase target = null;
                target = (TestCase)this.get_store().find_element_user(TestsImpl.TEST$0, i);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        @Override
        public int sizeOfTestArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(TestsImpl.TEST$0);
            }
        }
        
        @Override
        public void setTestArray(final TestCase[] testArray) {
            this.check_orphaned();
            this.arraySetterHelper(testArray, TestsImpl.TEST$0);
        }
        
        @Override
        public void setTestArray(final int i, final TestCase test) {
            this.generatedSetterHelperImpl(test, TestsImpl.TEST$0, i, (short)2);
        }
        
        @Override
        public TestCase insertNewTest(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                TestCase target = null;
                target = (TestCase)this.get_store().insert_element_user(TestsImpl.TEST$0, i);
                return target;
            }
        }
        
        @Override
        public TestCase addNewTest() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                TestCase target = null;
                target = (TestCase)this.get_store().add_element_user(TestsImpl.TEST$0);
                return target;
            }
        }
        
        @Override
        public void removeTest(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(TestsImpl.TEST$0, i);
            }
        }
        
        static {
            TEST$0 = new QName("http://www.bea.com/2003/05/xmlbean/ltgfmt", "test");
        }
    }
}
