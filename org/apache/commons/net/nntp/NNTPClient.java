package org.apache.commons.net.nntp;

import org.apache.commons.net.io.DotTerminatedMessageWriter;
import java.util.ArrayList;
import java.io.Writer;
import org.apache.commons.net.io.Util;
import java.io.StringWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.Vector;
import java.io.Reader;
import org.apache.commons.net.io.DotTerminatedMessageReader;
import org.apache.commons.net.MalformedServerReplyException;

public class NNTPClient extends NNTP
{
    private void __parseArticlePointer(final String reply, final ArticleInfo pointer) throws MalformedServerReplyException {
        final String[] tokens = reply.split(" ");
        if (tokens.length >= 3) {
            int i = 1;
            try {
                pointer.articleNumber = Long.parseLong(tokens[i++]);
                pointer.articleId = tokens[i++];
                return;
            }
            catch (final NumberFormatException ex) {}
        }
        throw new MalformedServerReplyException("Could not parse article pointer.\nServer reply: " + reply);
    }
    
    private static void __parseGroupReply(final String reply, final NewsgroupInfo info) throws MalformedServerReplyException {
        final String[] tokens = reply.split(" ");
        if (tokens.length >= 5) {
            int i = 1;
            try {
                info._setArticleCount(Long.parseLong(tokens[i++]));
                info._setFirstArticle(Long.parseLong(tokens[i++]));
                info._setLastArticle(Long.parseLong(tokens[i++]));
                info._setNewsgroup(tokens[i++]);
                info._setPostingPermission(0);
                return;
            }
            catch (final NumberFormatException ex) {}
        }
        throw new MalformedServerReplyException("Could not parse newsgroup info.\nServer reply: " + reply);
    }
    
    static NewsgroupInfo __parseNewsgroupListEntry(final String entry) {
        final String[] tokens = entry.split(" ");
        if (tokens.length < 4) {
            return null;
        }
        final NewsgroupInfo result = new NewsgroupInfo();
        int i = 0;
        result._setNewsgroup(tokens[i++]);
        try {
            final long lastNum = Long.parseLong(tokens[i++]);
            final long firstNum = Long.parseLong(tokens[i++]);
            result._setFirstArticle(firstNum);
            result._setLastArticle(lastNum);
            if (firstNum == 0L && lastNum == 0L) {
                result._setArticleCount(0L);
            }
            else {
                result._setArticleCount(lastNum - firstNum + 1L);
            }
        }
        catch (final NumberFormatException e) {
            return null;
        }
        switch (tokens[i++].charAt(0)) {
            case 'Y':
            case 'y': {
                result._setPostingPermission(2);
                break;
            }
            case 'N':
            case 'n': {
                result._setPostingPermission(3);
                break;
            }
            case 'M':
            case 'm': {
                result._setPostingPermission(1);
                break;
            }
            default: {
                result._setPostingPermission(0);
                break;
            }
        }
        return result;
    }
    
    static Article __parseArticleEntry(final String line) {
        final Article article = new Article();
        article.setSubject(line);
        final String[] parts = line.split("\t");
        if (parts.length > 6) {
            int i = 0;
            try {
                article.setArticleNumber(Long.parseLong(parts[i++]));
                article.setSubject(parts[i++]);
                article.setFrom(parts[i++]);
                article.setDate(parts[i++]);
                article.setArticleId(parts[i++]);
                article.addReference(parts[i++]);
            }
            catch (final NumberFormatException ex) {}
        }
        return article;
    }
    
    private NewsgroupInfo[] __readNewsgroupListing() throws IOException {
        final BufferedReader reader = new DotTerminatedMessageReader(this._reader_);
        final Vector<NewsgroupInfo> list = new Vector<NewsgroupInfo>(2048);
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                final NewsgroupInfo tmp = __parseNewsgroupListEntry(line);
                if (tmp == null) {
                    throw new MalformedServerReplyException(line);
                }
                list.addElement(tmp);
            }
        }
        finally {
            reader.close();
        }
        final int size;
        if ((size = list.size()) < 1) {
            return new NewsgroupInfo[0];
        }
        final NewsgroupInfo[] info = new NewsgroupInfo[size];
        list.copyInto(info);
        return info;
    }
    
    private BufferedReader __retrieve(final int command, final String articleId, final ArticleInfo pointer) throws IOException {
        if (articleId != null) {
            if (!NNTPReply.isPositiveCompletion(this.sendCommand(command, articleId))) {
                return null;
            }
        }
        else if (!NNTPReply.isPositiveCompletion(this.sendCommand(command))) {
            return null;
        }
        if (pointer != null) {
            this.__parseArticlePointer(this.getReplyString(), pointer);
        }
        return new DotTerminatedMessageReader(this._reader_);
    }
    
    private BufferedReader __retrieve(final int command, final long articleNumber, final ArticleInfo pointer) throws IOException {
        if (!NNTPReply.isPositiveCompletion(this.sendCommand(command, Long.toString(articleNumber)))) {
            return null;
        }
        if (pointer != null) {
            this.__parseArticlePointer(this.getReplyString(), pointer);
        }
        return new DotTerminatedMessageReader(this._reader_);
    }
    
    public BufferedReader retrieveArticle(final String articleId, final ArticleInfo pointer) throws IOException {
        return this.__retrieve(0, articleId, pointer);
    }
    
    public Reader retrieveArticle(final String articleId) throws IOException {
        return this.retrieveArticle(articleId, (ArticleInfo)null);
    }
    
    public Reader retrieveArticle() throws IOException {
        return this.retrieveArticle(null);
    }
    
    public BufferedReader retrieveArticle(final long articleNumber, final ArticleInfo pointer) throws IOException {
        return this.__retrieve(0, articleNumber, pointer);
    }
    
    public BufferedReader retrieveArticle(final long articleNumber) throws IOException {
        return this.retrieveArticle(articleNumber, null);
    }
    
    public BufferedReader retrieveArticleHeader(final String articleId, final ArticleInfo pointer) throws IOException {
        return this.__retrieve(3, articleId, pointer);
    }
    
    public Reader retrieveArticleHeader(final String articleId) throws IOException {
        return this.retrieveArticleHeader(articleId, (ArticleInfo)null);
    }
    
    public Reader retrieveArticleHeader() throws IOException {
        return this.retrieveArticleHeader(null);
    }
    
    public BufferedReader retrieveArticleHeader(final long articleNumber, final ArticleInfo pointer) throws IOException {
        return this.__retrieve(3, articleNumber, pointer);
    }
    
    public BufferedReader retrieveArticleHeader(final long articleNumber) throws IOException {
        return this.retrieveArticleHeader(articleNumber, null);
    }
    
    public BufferedReader retrieveArticleBody(final String articleId, final ArticleInfo pointer) throws IOException {
        return this.__retrieve(1, articleId, pointer);
    }
    
    public Reader retrieveArticleBody(final String articleId) throws IOException {
        return this.retrieveArticleBody(articleId, (ArticleInfo)null);
    }
    
    public Reader retrieveArticleBody() throws IOException {
        return this.retrieveArticleBody(null);
    }
    
    public BufferedReader retrieveArticleBody(final long articleNumber, final ArticleInfo pointer) throws IOException {
        return this.__retrieve(1, articleNumber, pointer);
    }
    
    public BufferedReader retrieveArticleBody(final long articleNumber) throws IOException {
        return this.retrieveArticleBody(articleNumber, null);
    }
    
    public boolean selectNewsgroup(final String newsgroup, final NewsgroupInfo info) throws IOException {
        if (!NNTPReply.isPositiveCompletion(this.group(newsgroup))) {
            return false;
        }
        if (info != null) {
            __parseGroupReply(this.getReplyString(), info);
        }
        return true;
    }
    
    public boolean selectNewsgroup(final String newsgroup) throws IOException {
        return this.selectNewsgroup(newsgroup, null);
    }
    
    public String listHelp() throws IOException {
        if (!NNTPReply.isInformational(this.help())) {
            return null;
        }
        final StringWriter help = new StringWriter();
        final BufferedReader reader = new DotTerminatedMessageReader(this._reader_);
        Util.copyReader(reader, help);
        reader.close();
        help.close();
        return help.toString();
    }
    
    public String[] listOverviewFmt() throws IOException {
        if (!NNTPReply.isPositiveCompletion(this.sendCommand("LIST", "OVERVIEW.FMT"))) {
            return null;
        }
        final BufferedReader reader = new DotTerminatedMessageReader(this._reader_);
        final ArrayList<String> list = new ArrayList<String>();
        String line;
        while ((line = reader.readLine()) != null) {
            list.add(line);
        }
        reader.close();
        return list.toArray(new String[list.size()]);
    }
    
    public boolean selectArticle(final String articleId, final ArticleInfo pointer) throws IOException {
        if (articleId != null) {
            if (!NNTPReply.isPositiveCompletion(this.stat(articleId))) {
                return false;
            }
        }
        else if (!NNTPReply.isPositiveCompletion(this.stat())) {
            return false;
        }
        if (pointer != null) {
            this.__parseArticlePointer(this.getReplyString(), pointer);
        }
        return true;
    }
    
    public boolean selectArticle(final String articleId) throws IOException {
        return this.selectArticle(articleId, (ArticleInfo)null);
    }
    
    public boolean selectArticle(final ArticleInfo pointer) throws IOException {
        return this.selectArticle(null, pointer);
    }
    
    public boolean selectArticle(final long articleNumber, final ArticleInfo pointer) throws IOException {
        if (!NNTPReply.isPositiveCompletion(this.stat(articleNumber))) {
            return false;
        }
        if (pointer != null) {
            this.__parseArticlePointer(this.getReplyString(), pointer);
        }
        return true;
    }
    
    public boolean selectArticle(final long articleNumber) throws IOException {
        return this.selectArticle(articleNumber, null);
    }
    
    public boolean selectPreviousArticle(final ArticleInfo pointer) throws IOException {
        if (!NNTPReply.isPositiveCompletion(this.last())) {
            return false;
        }
        if (pointer != null) {
            this.__parseArticlePointer(this.getReplyString(), pointer);
        }
        return true;
    }
    
    public boolean selectPreviousArticle() throws IOException {
        return this.selectPreviousArticle((ArticleInfo)null);
    }
    
    public boolean selectNextArticle(final ArticleInfo pointer) throws IOException {
        if (!NNTPReply.isPositiveCompletion(this.next())) {
            return false;
        }
        if (pointer != null) {
            this.__parseArticlePointer(this.getReplyString(), pointer);
        }
        return true;
    }
    
    public boolean selectNextArticle() throws IOException {
        return this.selectNextArticle((ArticleInfo)null);
    }
    
    public NewsgroupInfo[] listNewsgroups() throws IOException {
        if (!NNTPReply.isPositiveCompletion(this.list())) {
            return null;
        }
        return this.__readNewsgroupListing();
    }
    
    public Iterable<String> iterateNewsgroupListing() throws IOException {
        if (NNTPReply.isPositiveCompletion(this.list())) {
            return new ReplyIterator(this._reader_);
        }
        throw new IOException("LIST command failed: " + this.getReplyString());
    }
    
    public Iterable<NewsgroupInfo> iterateNewsgroups() throws IOException {
        return new NewsgroupIterator(this.iterateNewsgroupListing());
    }
    
    public NewsgroupInfo[] listNewsgroups(final String wildmat) throws IOException {
        if (!NNTPReply.isPositiveCompletion(this.listActive(wildmat))) {
            return null;
        }
        return this.__readNewsgroupListing();
    }
    
    public Iterable<String> iterateNewsgroupListing(final String wildmat) throws IOException {
        if (NNTPReply.isPositiveCompletion(this.listActive(wildmat))) {
            return new ReplyIterator(this._reader_);
        }
        throw new IOException("LIST ACTIVE " + wildmat + " command failed: " + this.getReplyString());
    }
    
    public Iterable<NewsgroupInfo> iterateNewsgroups(final String wildmat) throws IOException {
        return new NewsgroupIterator(this.iterateNewsgroupListing(wildmat));
    }
    
    public NewsgroupInfo[] listNewNewsgroups(final NewGroupsOrNewsQuery query) throws IOException {
        if (!NNTPReply.isPositiveCompletion(this.newgroups(query.getDate(), query.getTime(), query.isGMT(), query.getDistributions()))) {
            return null;
        }
        return this.__readNewsgroupListing();
    }
    
    public Iterable<String> iterateNewNewsgroupListing(final NewGroupsOrNewsQuery query) throws IOException {
        if (NNTPReply.isPositiveCompletion(this.newgroups(query.getDate(), query.getTime(), query.isGMT(), query.getDistributions()))) {
            return new ReplyIterator(this._reader_);
        }
        throw new IOException("NEWGROUPS command failed: " + this.getReplyString());
    }
    
    public Iterable<NewsgroupInfo> iterateNewNewsgroups(final NewGroupsOrNewsQuery query) throws IOException {
        return new NewsgroupIterator(this.iterateNewNewsgroupListing(query));
    }
    
    public String[] listNewNews(final NewGroupsOrNewsQuery query) throws IOException {
        if (!NNTPReply.isPositiveCompletion(this.newnews(query.getNewsgroups(), query.getDate(), query.getTime(), query.isGMT(), query.getDistributions()))) {
            return null;
        }
        final Vector<String> list = new Vector<String>();
        final BufferedReader reader = new DotTerminatedMessageReader(this._reader_);
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                list.addElement(line);
            }
        }
        finally {
            reader.close();
        }
        final int size = list.size();
        if (size < 1) {
            return new String[0];
        }
        final String[] result = new String[size];
        list.copyInto(result);
        return result;
    }
    
    public Iterable<String> iterateNewNews(final NewGroupsOrNewsQuery query) throws IOException {
        if (NNTPReply.isPositiveCompletion(this.newnews(query.getNewsgroups(), query.getDate(), query.getTime(), query.isGMT(), query.getDistributions()))) {
            return new ReplyIterator(this._reader_);
        }
        throw new IOException("NEWNEWS command failed: " + this.getReplyString());
    }
    
    public boolean completePendingCommand() throws IOException {
        return NNTPReply.isPositiveCompletion(this.getReply());
    }
    
    public Writer postArticle() throws IOException {
        if (!NNTPReply.isPositiveIntermediate(this.post())) {
            return null;
        }
        return new DotTerminatedMessageWriter(this._writer_);
    }
    
    public Writer forwardArticle(final String articleId) throws IOException {
        if (!NNTPReply.isPositiveIntermediate(this.ihave(articleId))) {
            return null;
        }
        return new DotTerminatedMessageWriter(this._writer_);
    }
    
    public boolean logout() throws IOException {
        return NNTPReply.isPositiveCompletion(this.quit());
    }
    
    public boolean authenticate(final String username, final String password) throws IOException {
        int replyCode = this.authinfoUser(username);
        if (replyCode == 381) {
            replyCode = this.authinfoPass(password);
            if (replyCode == 281) {
                return this._isAllowedToPost = true;
            }
        }
        return false;
    }
    
    private BufferedReader __retrieveArticleInfo(final String articleRange) throws IOException {
        if (!NNTPReply.isPositiveCompletion(this.xover(articleRange))) {
            return null;
        }
        return new DotTerminatedMessageReader(this._reader_);
    }
    
    public BufferedReader retrieveArticleInfo(final long articleNumber) throws IOException {
        return this.__retrieveArticleInfo(Long.toString(articleNumber));
    }
    
    public BufferedReader retrieveArticleInfo(final long lowArticleNumber, final long highArticleNumber) throws IOException {
        return this.__retrieveArticleInfo(lowArticleNumber + "-" + highArticleNumber);
    }
    
    public Iterable<Article> iterateArticleInfo(final long lowArticleNumber, final long highArticleNumber) throws IOException {
        final BufferedReader info = this.retrieveArticleInfo(lowArticleNumber, highArticleNumber);
        if (info == null) {
            throw new IOException("XOVER command failed: " + this.getReplyString());
        }
        return new ArticleIterator(new ReplyIterator(info, false));
    }
    
    private BufferedReader __retrieveHeader(final String header, final String articleRange) throws IOException {
        if (!NNTPReply.isPositiveCompletion(this.xhdr(header, articleRange))) {
            return null;
        }
        return new DotTerminatedMessageReader(this._reader_);
    }
    
    public BufferedReader retrieveHeader(final String header, final long articleNumber) throws IOException {
        return this.__retrieveHeader(header, Long.toString(articleNumber));
    }
    
    public BufferedReader retrieveHeader(final String header, final long lowArticleNumber, final long highArticleNumber) throws IOException {
        return this.__retrieveHeader(header, lowArticleNumber + "-" + highArticleNumber);
    }
    
    @Deprecated
    public Reader retrieveHeader(final String header, final int lowArticleNumber, final int highArticleNumber) throws IOException {
        return this.retrieveHeader(header, lowArticleNumber, (long)highArticleNumber);
    }
    
    @Deprecated
    public Reader retrieveArticleInfo(final int lowArticleNumber, final int highArticleNumber) throws IOException {
        return this.retrieveArticleInfo(lowArticleNumber, (long)highArticleNumber);
    }
    
    @Deprecated
    public Reader retrieveHeader(final String a, final int b) throws IOException {
        return this.retrieveHeader(a, (long)b);
    }
    
    @Deprecated
    public boolean selectArticle(final int a, final ArticlePointer ap) throws IOException {
        final ArticleInfo ai = this.__ap2ai(ap);
        final boolean b = this.selectArticle(a, ai);
        this.__ai2ap(ai, ap);
        return b;
    }
    
    @Deprecated
    public Reader retrieveArticleInfo(final int lowArticleNumber) throws IOException {
        return this.retrieveArticleInfo((long)lowArticleNumber);
    }
    
    @Deprecated
    public boolean selectArticle(final int a) throws IOException {
        return this.selectArticle((long)a);
    }
    
    @Deprecated
    public Reader retrieveArticleHeader(final int a) throws IOException {
        return this.retrieveArticleHeader((long)a);
    }
    
    @Deprecated
    public Reader retrieveArticleHeader(final int a, final ArticlePointer ap) throws IOException {
        final ArticleInfo ai = this.__ap2ai(ap);
        final Reader rdr = this.retrieveArticleHeader(a, ai);
        this.__ai2ap(ai, ap);
        return rdr;
    }
    
    @Deprecated
    public Reader retrieveArticleBody(final int a) throws IOException {
        return this.retrieveArticleBody((long)a);
    }
    
    @Deprecated
    public Reader retrieveArticle(final int articleNumber, final ArticlePointer pointer) throws IOException {
        final ArticleInfo ai = this.__ap2ai(pointer);
        final Reader rdr = this.retrieveArticle(articleNumber, ai);
        this.__ai2ap(ai, pointer);
        return rdr;
    }
    
    @Deprecated
    public Reader retrieveArticle(final int articleNumber) throws IOException {
        return this.retrieveArticle((long)articleNumber);
    }
    
    @Deprecated
    public Reader retrieveArticleBody(final int a, final ArticlePointer ap) throws IOException {
        final ArticleInfo ai = this.__ap2ai(ap);
        final Reader rdr = this.retrieveArticleBody(a, ai);
        this.__ai2ap(ai, ap);
        return rdr;
    }
    
    @Deprecated
    public Reader retrieveArticle(final String articleId, final ArticlePointer pointer) throws IOException {
        final ArticleInfo ai = this.__ap2ai(pointer);
        final Reader rdr = this.retrieveArticle(articleId, ai);
        this.__ai2ap(ai, pointer);
        return rdr;
    }
    
    @Deprecated
    public Reader retrieveArticleBody(final String articleId, final ArticlePointer pointer) throws IOException {
        final ArticleInfo ai = this.__ap2ai(pointer);
        final Reader rdr = this.retrieveArticleBody(articleId, ai);
        this.__ai2ap(ai, pointer);
        return rdr;
    }
    
    @Deprecated
    public Reader retrieveArticleHeader(final String articleId, final ArticlePointer pointer) throws IOException {
        final ArticleInfo ai = this.__ap2ai(pointer);
        final Reader rdr = this.retrieveArticleHeader(articleId, ai);
        this.__ai2ap(ai, pointer);
        return rdr;
    }
    
    @Deprecated
    public boolean selectArticle(final String articleId, final ArticlePointer pointer) throws IOException {
        final ArticleInfo ai = this.__ap2ai(pointer);
        final boolean b = this.selectArticle(articleId, ai);
        this.__ai2ap(ai, pointer);
        return b;
    }
    
    @Deprecated
    public boolean selectArticle(final ArticlePointer pointer) throws IOException {
        final ArticleInfo ai = this.__ap2ai(pointer);
        final boolean b = this.selectArticle(ai);
        this.__ai2ap(ai, pointer);
        return b;
    }
    
    @Deprecated
    public boolean selectNextArticle(final ArticlePointer pointer) throws IOException {
        final ArticleInfo ai = this.__ap2ai(pointer);
        final boolean b = this.selectNextArticle(ai);
        this.__ai2ap(ai, pointer);
        return b;
    }
    
    @Deprecated
    public boolean selectPreviousArticle(final ArticlePointer pointer) throws IOException {
        final ArticleInfo ai = this.__ap2ai(pointer);
        final boolean b = this.selectPreviousArticle(ai);
        this.__ai2ap(ai, pointer);
        return b;
    }
    
    private ArticleInfo __ap2ai(final ArticlePointer ap) {
        if (ap == null) {
            return null;
        }
        final ArticleInfo ai = new ArticleInfo();
        return ai;
    }
    
    private void __ai2ap(final ArticleInfo ai, final ArticlePointer ap) {
        if (ap != null) {
            ap.articleId = ai.articleId;
            ap.articleNumber = (int)ai.articleNumber;
        }
    }
}
