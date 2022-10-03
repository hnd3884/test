package com.google.zxing.client.result;

public final class SMSParsedResult extends ParsedResult
{
    private final String[] numbers;
    private final String[] vias;
    private final String subject;
    private final String body;
    
    public SMSParsedResult(final String number, final String via, final String subject, final String body) {
        super(ParsedResultType.SMS);
        this.numbers = new String[] { number };
        this.vias = new String[] { via };
        this.subject = subject;
        this.body = body;
    }
    
    public SMSParsedResult(final String[] numbers, final String[] vias, final String subject, final String body) {
        super(ParsedResultType.SMS);
        this.numbers = numbers;
        this.vias = vias;
        this.subject = subject;
        this.body = body;
    }
    
    public String getSMSURI() {
        final StringBuilder result = new StringBuilder();
        result.append("sms:");
        boolean first = true;
        for (int i = 0; i < this.numbers.length; ++i) {
            if (first) {
                first = false;
            }
            else {
                result.append(',');
            }
            result.append(this.numbers[i]);
            if (this.vias != null && this.vias[i] != null) {
                result.append(";via=");
                result.append(this.vias[i]);
            }
        }
        final boolean hasBody = this.body != null;
        final boolean hasSubject = this.subject != null;
        if (hasBody || hasSubject) {
            result.append('?');
            if (hasBody) {
                result.append("body=");
                result.append(this.body);
            }
            if (hasSubject) {
                if (hasBody) {
                    result.append('&');
                }
                result.append("subject=");
                result.append(this.subject);
            }
        }
        return result.toString();
    }
    
    public String[] getNumbers() {
        return this.numbers;
    }
    
    public String[] getVias() {
        return this.vias;
    }
    
    public String getSubject() {
        return this.subject;
    }
    
    public String getBody() {
        return this.body;
    }
    
    @Override
    public String getDisplayResult() {
        final StringBuilder result = new StringBuilder(100);
        ParsedResult.maybeAppend(this.numbers, result);
        ParsedResult.maybeAppend(this.subject, result);
        ParsedResult.maybeAppend(this.body, result);
        return result.toString();
    }
}
