package org.owasp.validator.html;

import java.util.ArrayList;
import org.w3c.dom.DocumentFragment;
import java.util.concurrent.Callable;
import java.util.List;

public class CleanResults
{
    private List<String> errorMessages;
    private Callable<String> cleanHTML;
    private long startOfScan;
    private long elapsedScan;
    private DocumentFragment cleanXMLDocumentFragment;
    
    public CleanResults() {
        this.errorMessages = new ArrayList<String>();
    }
    
    public CleanResults(final long startOfScan, final String cleanHTML, final DocumentFragment XMLDocumentFragment, final List<String> errorMessages) {
        this.errorMessages = new ArrayList<String>();
        this.startOfScan = startOfScan;
        this.elapsedScan = System.currentTimeMillis() - startOfScan;
        this.cleanXMLDocumentFragment = XMLDocumentFragment;
        this.cleanHTML = new Callable<String>() {
            @Override
            public String call() throws Exception {
                return cleanHTML;
            }
        };
        this.errorMessages = errorMessages;
    }
    
    public CleanResults(final long startOfScan, final Callable<String> cleanHTML, final DocumentFragment XMLDocumentFragment, final List<String> errorMessages) {
        this.errorMessages = new ArrayList<String>();
        this.elapsedScan = System.currentTimeMillis() - startOfScan;
        this.cleanXMLDocumentFragment = XMLDocumentFragment;
        this.cleanHTML = cleanHTML;
        this.errorMessages = errorMessages;
    }
    
    public DocumentFragment getCleanXMLDocumentFragment() {
        return this.cleanXMLDocumentFragment;
    }
    
    public String getCleanHTML() {
        try {
            return this.cleanHTML.call();
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public List<String> getErrorMessages() {
        return this.errorMessages;
    }
    
    public double getScanTime() {
        return this.elapsedScan / 1000.0;
    }
    
    public int getNumberOfErrors() {
        return this.errorMessages.size();
    }
    
    public long getStartOfScan() {
        return this.startOfScan;
    }
}
