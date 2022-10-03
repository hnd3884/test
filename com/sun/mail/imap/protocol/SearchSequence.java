package com.sun.mail.imap.protocol;

import java.util.Date;
import javax.mail.Message;
import javax.mail.Flags;
import javax.mail.search.AddressTerm;
import javax.mail.search.StringTerm;
import java.io.IOException;
import javax.mail.search.SearchException;
import com.sun.mail.imap.ModifiedSinceTerm;
import javax.mail.search.MessageIDTerm;
import com.sun.mail.imap.YoungerTerm;
import com.sun.mail.imap.OlderTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.DateTerm;
import javax.mail.search.SentDateTerm;
import javax.mail.search.SizeTerm;
import javax.mail.search.BodyTerm;
import javax.mail.search.SubjectTerm;
import javax.mail.search.RecipientStringTerm;
import javax.mail.search.RecipientTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.FromTerm;
import javax.mail.search.FlagTerm;
import javax.mail.search.HeaderTerm;
import javax.mail.search.NotTerm;
import javax.mail.search.OrTerm;
import javax.mail.search.AndTerm;
import com.sun.mail.iap.Argument;
import javax.mail.search.SearchTerm;
import java.util.GregorianCalendar;
import java.util.Calendar;

public class SearchSequence
{
    private IMAPProtocol protocol;
    private static String[] monthTable;
    protected Calendar cal;
    
    public SearchSequence(final IMAPProtocol p) {
        this.cal = new GregorianCalendar();
        this.protocol = p;
    }
    
    @Deprecated
    public SearchSequence() {
        this.cal = new GregorianCalendar();
    }
    
    public Argument generateSequence(final SearchTerm term, final String charset) throws SearchException, IOException {
        if (term instanceof AndTerm) {
            return this.and((AndTerm)term, charset);
        }
        if (term instanceof OrTerm) {
            return this.or((OrTerm)term, charset);
        }
        if (term instanceof NotTerm) {
            return this.not((NotTerm)term, charset);
        }
        if (term instanceof HeaderTerm) {
            return this.header((HeaderTerm)term, charset);
        }
        if (term instanceof FlagTerm) {
            return this.flag((FlagTerm)term);
        }
        if (term instanceof FromTerm) {
            final FromTerm fterm = (FromTerm)term;
            return this.from(fterm.getAddress().toString(), charset);
        }
        if (term instanceof FromStringTerm) {
            final FromStringTerm fterm2 = (FromStringTerm)term;
            return this.from(fterm2.getPattern(), charset);
        }
        if (term instanceof RecipientTerm) {
            final RecipientTerm rterm = (RecipientTerm)term;
            return this.recipient(rterm.getRecipientType(), rterm.getAddress().toString(), charset);
        }
        if (term instanceof RecipientStringTerm) {
            final RecipientStringTerm rterm2 = (RecipientStringTerm)term;
            return this.recipient(rterm2.getRecipientType(), rterm2.getPattern(), charset);
        }
        if (term instanceof SubjectTerm) {
            return this.subject((SubjectTerm)term, charset);
        }
        if (term instanceof BodyTerm) {
            return this.body((BodyTerm)term, charset);
        }
        if (term instanceof SizeTerm) {
            return this.size((SizeTerm)term);
        }
        if (term instanceof SentDateTerm) {
            return this.sentdate((DateTerm)term);
        }
        if (term instanceof ReceivedDateTerm) {
            return this.receiveddate((DateTerm)term);
        }
        if (term instanceof OlderTerm) {
            return this.older((OlderTerm)term);
        }
        if (term instanceof YoungerTerm) {
            return this.younger((YoungerTerm)term);
        }
        if (term instanceof MessageIDTerm) {
            return this.messageid((MessageIDTerm)term, charset);
        }
        if (term instanceof ModifiedSinceTerm) {
            return this.modifiedSince((ModifiedSinceTerm)term);
        }
        throw new SearchException("Search too complex");
    }
    
    public static boolean isAscii(final SearchTerm term) {
        if (term instanceof AndTerm) {
            return isAscii(((AndTerm)term).getTerms());
        }
        if (term instanceof OrTerm) {
            return isAscii(((OrTerm)term).getTerms());
        }
        if (term instanceof NotTerm) {
            return isAscii(((NotTerm)term).getTerm());
        }
        if (term instanceof StringTerm) {
            return isAscii(((StringTerm)term).getPattern());
        }
        return !(term instanceof AddressTerm) || isAscii(((AddressTerm)term).getAddress().toString());
    }
    
    public static boolean isAscii(final SearchTerm[] terms) {
        for (int i = 0; i < terms.length; ++i) {
            if (!isAscii(terms[i])) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isAscii(final String s) {
        for (int l = s.length(), i = 0; i < l; ++i) {
            if (s.charAt(i) > '\u007f') {
                return false;
            }
        }
        return true;
    }
    
    protected Argument and(final AndTerm term, final String charset) throws SearchException, IOException {
        final SearchTerm[] terms = term.getTerms();
        final Argument result = this.generateSequence(terms[0], charset);
        for (int i = 1; i < terms.length; ++i) {
            result.append(this.generateSequence(terms[i], charset));
        }
        return result;
    }
    
    protected Argument or(OrTerm term, final String charset) throws SearchException, IOException {
        SearchTerm[] terms = term.getTerms();
        if (terms.length > 2) {
            SearchTerm t = terms[0];
            for (int i = 1; i < terms.length; ++i) {
                t = new OrTerm(t, terms[i]);
            }
            term = (OrTerm)t;
            terms = term.getTerms();
        }
        final Argument result = new Argument();
        if (terms.length > 1) {
            result.writeAtom("OR");
        }
        if (terms[0] instanceof AndTerm || terms[0] instanceof FlagTerm) {
            result.writeArgument(this.generateSequence(terms[0], charset));
        }
        else {
            result.append(this.generateSequence(terms[0], charset));
        }
        if (terms.length > 1) {
            if (terms[1] instanceof AndTerm || terms[1] instanceof FlagTerm) {
                result.writeArgument(this.generateSequence(terms[1], charset));
            }
            else {
                result.append(this.generateSequence(terms[1], charset));
            }
        }
        return result;
    }
    
    protected Argument not(final NotTerm term, final String charset) throws SearchException, IOException {
        final Argument result = new Argument();
        result.writeAtom("NOT");
        final SearchTerm nterm = term.getTerm();
        if (nterm instanceof AndTerm || nterm instanceof FlagTerm) {
            result.writeArgument(this.generateSequence(nterm, charset));
        }
        else {
            result.append(this.generateSequence(nterm, charset));
        }
        return result;
    }
    
    protected Argument header(final HeaderTerm term, final String charset) throws SearchException, IOException {
        final Argument result = new Argument();
        result.writeAtom("HEADER");
        result.writeString(term.getHeaderName());
        result.writeString(term.getPattern(), charset);
        return result;
    }
    
    protected Argument messageid(final MessageIDTerm term, final String charset) throws SearchException, IOException {
        final Argument result = new Argument();
        result.writeAtom("HEADER");
        result.writeString("Message-ID");
        result.writeString(term.getPattern(), charset);
        return result;
    }
    
    protected Argument flag(final FlagTerm term) throws SearchException {
        final boolean set = term.getTestSet();
        final Argument result = new Argument();
        final Flags flags = term.getFlags();
        final Flags.Flag[] sf = flags.getSystemFlags();
        final String[] uf = flags.getUserFlags();
        if (sf.length == 0 && uf.length == 0) {
            throw new SearchException("Invalid FlagTerm");
        }
        for (int i = 0; i < sf.length; ++i) {
            if (sf[i] == Flags.Flag.DELETED) {
                result.writeAtom(set ? "DELETED" : "UNDELETED");
            }
            else if (sf[i] == Flags.Flag.ANSWERED) {
                result.writeAtom(set ? "ANSWERED" : "UNANSWERED");
            }
            else if (sf[i] == Flags.Flag.DRAFT) {
                result.writeAtom(set ? "DRAFT" : "UNDRAFT");
            }
            else if (sf[i] == Flags.Flag.FLAGGED) {
                result.writeAtom(set ? "FLAGGED" : "UNFLAGGED");
            }
            else if (sf[i] == Flags.Flag.RECENT) {
                result.writeAtom(set ? "RECENT" : "OLD");
            }
            else if (sf[i] == Flags.Flag.SEEN) {
                result.writeAtom(set ? "SEEN" : "UNSEEN");
            }
        }
        for (int i = 0; i < uf.length; ++i) {
            result.writeAtom(set ? "KEYWORD" : "UNKEYWORD");
            result.writeAtom(uf[i]);
        }
        return result;
    }
    
    protected Argument from(final String address, final String charset) throws SearchException, IOException {
        final Argument result = new Argument();
        result.writeAtom("FROM");
        result.writeString(address, charset);
        return result;
    }
    
    protected Argument recipient(final Message.RecipientType type, final String address, final String charset) throws SearchException, IOException {
        final Argument result = new Argument();
        if (type == Message.RecipientType.TO) {
            result.writeAtom("TO");
        }
        else if (type == Message.RecipientType.CC) {
            result.writeAtom("CC");
        }
        else {
            if (type != Message.RecipientType.BCC) {
                throw new SearchException("Illegal Recipient type");
            }
            result.writeAtom("BCC");
        }
        result.writeString(address, charset);
        return result;
    }
    
    protected Argument subject(final SubjectTerm term, final String charset) throws SearchException, IOException {
        final Argument result = new Argument();
        result.writeAtom("SUBJECT");
        result.writeString(term.getPattern(), charset);
        return result;
    }
    
    protected Argument body(final BodyTerm term, final String charset) throws SearchException, IOException {
        final Argument result = new Argument();
        result.writeAtom("BODY");
        result.writeString(term.getPattern(), charset);
        return result;
    }
    
    protected Argument size(final SizeTerm term) throws SearchException {
        final Argument result = new Argument();
        switch (term.getComparison()) {
            case 5: {
                result.writeAtom("LARGER");
                break;
            }
            case 2: {
                result.writeAtom("SMALLER");
                break;
            }
            default: {
                throw new SearchException("Cannot handle Comparison");
            }
        }
        result.writeNumber(term.getNumber());
        return result;
    }
    
    protected String toIMAPDate(final Date date) {
        final StringBuilder s = new StringBuilder();
        this.cal.setTime(date);
        s.append(this.cal.get(5)).append("-");
        s.append(SearchSequence.monthTable[this.cal.get(2)]).append('-');
        s.append(this.cal.get(1));
        return s.toString();
    }
    
    protected Argument sentdate(final DateTerm term) throws SearchException {
        final Argument result = new Argument();
        final String date = this.toIMAPDate(term.getDate());
        switch (term.getComparison()) {
            case 5: {
                result.writeAtom("NOT SENTON " + date + " SENTSINCE " + date);
                break;
            }
            case 3: {
                result.writeAtom("SENTON " + date);
                break;
            }
            case 2: {
                result.writeAtom("SENTBEFORE " + date);
                break;
            }
            case 6: {
                result.writeAtom("SENTSINCE " + date);
                break;
            }
            case 1: {
                result.writeAtom("OR SENTBEFORE " + date + " SENTON " + date);
                break;
            }
            case 4: {
                result.writeAtom("NOT SENTON " + date);
                break;
            }
            default: {
                throw new SearchException("Cannot handle Date Comparison");
            }
        }
        return result;
    }
    
    protected Argument receiveddate(final DateTerm term) throws SearchException {
        final Argument result = new Argument();
        final String date = this.toIMAPDate(term.getDate());
        switch (term.getComparison()) {
            case 5: {
                result.writeAtom("NOT ON " + date + " SINCE " + date);
                break;
            }
            case 3: {
                result.writeAtom("ON " + date);
                break;
            }
            case 2: {
                result.writeAtom("BEFORE " + date);
                break;
            }
            case 6: {
                result.writeAtom("SINCE " + date);
                break;
            }
            case 1: {
                result.writeAtom("OR BEFORE " + date + " ON " + date);
                break;
            }
            case 4: {
                result.writeAtom("NOT ON " + date);
                break;
            }
            default: {
                throw new SearchException("Cannot handle Date Comparison");
            }
        }
        return result;
    }
    
    protected Argument older(final OlderTerm term) throws SearchException {
        if (this.protocol != null && !this.protocol.hasCapability("WITHIN")) {
            throw new SearchException("Server doesn't support OLDER searches");
        }
        final Argument result = new Argument();
        result.writeAtom("OLDER");
        result.writeNumber(term.getInterval());
        return result;
    }
    
    protected Argument younger(final YoungerTerm term) throws SearchException {
        if (this.protocol != null && !this.protocol.hasCapability("WITHIN")) {
            throw new SearchException("Server doesn't support YOUNGER searches");
        }
        final Argument result = new Argument();
        result.writeAtom("YOUNGER");
        result.writeNumber(term.getInterval());
        return result;
    }
    
    protected Argument modifiedSince(final ModifiedSinceTerm term) throws SearchException {
        if (this.protocol != null && !this.protocol.hasCapability("CONDSTORE")) {
            throw new SearchException("Server doesn't support MODSEQ searches");
        }
        final Argument result = new Argument();
        result.writeAtom("MODSEQ");
        result.writeNumber(term.getModSeq());
        return result;
    }
    
    static {
        SearchSequence.monthTable = new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
    }
}
