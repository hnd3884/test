package com.sun.org.apache.xml.internal.dtm.ref;

public class DTMSafeStringPool extends DTMStringPool
{
    @Override
    public synchronized void removeAllElements() {
        super.removeAllElements();
    }
    
    @Override
    public synchronized String indexToString(final int i) throws ArrayIndexOutOfBoundsException {
        return super.indexToString(i);
    }
    
    @Override
    public synchronized int stringToIndex(final String s) {
        return super.stringToIndex(s);
    }
    
    public static void _main(final String[] args) {
        final String[] word = { "Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen", "Twenty", "Twenty-One", "Twenty-Two", "Twenty-Three", "Twenty-Four", "Twenty-Five", "Twenty-Six", "Twenty-Seven", "Twenty-Eight", "Twenty-Nine", "Thirty", "Thirty-One", "Thirty-Two", "Thirty-Three", "Thirty-Four", "Thirty-Five", "Thirty-Six", "Thirty-Seven", "Thirty-Eight", "Thirty-Nine" };
        final DTMStringPool pool = new DTMSafeStringPool();
        System.out.println("If no complaints are printed below, we passed initial test.");
        for (int pass = 0; pass <= 1; ++pass) {
            for (int i = 0; i < word.length; ++i) {
                final int j = pool.stringToIndex(word[i]);
                if (j != i) {
                    System.out.println("\tMismatch populating pool: assigned " + j + " for create " + i);
                }
            }
            for (int i = 0; i < word.length; ++i) {
                final int j = pool.stringToIndex(word[i]);
                if (j != i) {
                    System.out.println("\tMismatch in stringToIndex: returned " + j + " for lookup " + i);
                }
            }
            for (int i = 0; i < word.length; ++i) {
                final String w = pool.indexToString(i);
                if (!word[i].equals(w)) {
                    System.out.println("\tMismatch in indexToString: returned" + w + " for lookup " + i);
                }
            }
            pool.removeAllElements();
            System.out.println("\nPass " + pass + " complete\n");
        }
    }
}
