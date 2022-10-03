package com.google.zxing.client.result;

import com.google.zxing.Result;

public final class SMTPResultParser extends ResultParser
{
    @Override
    public EmailAddressParsedResult parse(final Result result) {
        final String rawText = result.getText();
        if (!rawText.startsWith("smtp:") && !rawText.startsWith("SMTP:")) {
            return null;
        }
        String emailAddress = rawText.substring(5);
        String subject = null;
        String body = null;
        int colon = emailAddress.indexOf(58);
        if (colon >= 0) {
            subject = emailAddress.substring(colon + 1);
            emailAddress = emailAddress.substring(0, colon);
            colon = subject.indexOf(58);
            if (colon >= 0) {
                body = subject.substring(colon + 1);
                subject = subject.substring(0, colon);
            }
        }
        final String mailtoURI = "mailto:" + emailAddress;
        return new EmailAddressParsedResult(emailAddress, subject, body, mailtoURI);
    }
}
