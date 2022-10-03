package com.me.mdm.framework.syncml.xml;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.me.mdm.framework.syncml.requestcmds.ExecRequestCommand;
import com.me.mdm.framework.syncml.core.data.Item;
import com.me.mdm.framework.syncml.core.data.Meta;
import com.me.mdm.framework.syncml.core.data.Challenge;
import com.me.mdm.framework.syncml.requestcmds.SequenceRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.AddRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.GetRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.DeleteRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.AtomicRequestCommand;
import com.me.mdm.framework.syncml.responsecmds.ResultsResponseCommand;
import com.me.mdm.framework.syncml.requestcmds.ReplaceRequestCommand;
import com.me.mdm.framework.syncml.responsecmds.StatusResponseCommand;
import com.me.mdm.framework.syncml.requestcmds.AlertRequestCommand;
import com.me.mdm.framework.syncml.core.SyncMLResponseCommand;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import com.me.mdm.framework.syncml.core.SyncBodyMessage;
import com.me.mdm.framework.syncml.core.data.Credential;
import com.me.mdm.framework.syncml.core.data.Location;
import com.me.mdm.framework.syncml.core.SyncHeaderMessage;
import java.util.Iterator;
import org.apache.axiom.om.OMElement;
import java.io.Reader;
import org.apache.axiom.om.OMXMLBuilderFactory;
import java.io.StringReader;
import org.apache.axiom.om.util.StAXParserConfiguration;
import org.apache.axiom.om.OMAbstractFactory;
import com.me.mdm.framework.syncml.core.SyncMLMessage;

public class XML2SyncMLMessageConverter
{
    public SyncMLMessage transform(String buffer) throws SyncMLMessage2XMLConverterException {
        SyncMLMessage syncml = null;
        try {
            buffer = replaceInvalidCharacters(buffer);
            final OMElement element = OMXMLBuilderFactory.createOMBuilder(OMAbstractFactory.getOMFactory(), StAXParserConfiguration.STANDALONE, (Reader)new StringReader(buffer)).getDocumentElement();
            syncml = new SyncMLMessage();
            final Iterator childElements = element.getChildElements();
            while (childElements.hasNext()) {
                final OMElement childElement = childElements.next();
                if (childElement.getLocalName().equals("SyncHdr")) {
                    syncml.setSyncHeader(this.processHeaderElement(childElement));
                }
                else {
                    if (!childElement.getLocalName().equalsIgnoreCase("SyncBody")) {
                        continue;
                    }
                    syncml.setSyncBody(this.processSyncBodyElement(childElement));
                }
            }
        }
        catch (final Exception e) {
            throw new SyncMLMessage2XMLConverterException(e);
        }
        return syncml;
    }
    
    private SyncHeaderMessage processHeaderElement(final OMElement element) {
        SyncHeaderMessage header = null;
        try {
            if (element != null) {
                header = new SyncHeaderMessage();
                final Iterator iterator = element.getChildElements();
                while (iterator.hasNext()) {
                    final OMElement innerElement = iterator.next();
                    final String elementName = innerElement.getLocalName();
                    if (elementName.equalsIgnoreCase("Source")) {
                        final Iterator sourceIterator = innerElement.getChildElements();
                        final Location location = new Location();
                        while (sourceIterator.hasNext()) {
                            final OMElement sourceElement = sourceIterator.next();
                            if (sourceElement.getLocalName().equalsIgnoreCase("LocURI")) {
                                location.setLocUri(sourceElement.getText());
                            }
                            else {
                                if (!sourceElement.getLocalName().equalsIgnoreCase("LocName")) {
                                    continue;
                                }
                                location.setLocName(sourceElement.getText());
                            }
                        }
                        header.setSource(location);
                    }
                    else if (elementName.equalsIgnoreCase("Target")) {
                        final Iterator sourceIterator = innerElement.getChildElements();
                        final Location location = new Location();
                        while (sourceIterator.hasNext()) {
                            final OMElement sourceElement = sourceIterator.next();
                            if (sourceElement.getLocalName().equalsIgnoreCase("LocURI")) {
                                location.setLocUri(sourceElement.getText());
                            }
                            else {
                                if (!sourceElement.getLocalName().equalsIgnoreCase("LocName")) {
                                    continue;
                                }
                                location.setLocName(sourceElement.getText());
                            }
                        }
                        header.setTarget(location);
                    }
                    else if (elementName.equalsIgnoreCase("VerDTD")) {
                        header.setVerDTD(innerElement.getText());
                    }
                    else if (elementName.equalsIgnoreCase("VerProto")) {
                        header.setVerProto(innerElement.getText());
                    }
                    else if (elementName.equalsIgnoreCase("SessionID")) {
                        header.setSessionID(innerElement.getText());
                    }
                    else if (elementName.equalsIgnoreCase("MsgID")) {
                        header.setMsgID(innerElement.getText());
                    }
                    else {
                        if (!elementName.equalsIgnoreCase("Cred")) {
                            continue;
                        }
                        final Credential cred = this.processCredElement(innerElement);
                        header.setCredential(cred);
                    }
                }
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
        return header;
    }
    
    private SyncBodyMessage processSyncBodyElement(final OMElement element) {
        SyncBodyMessage syncBody = null;
        if (element != null) {
            syncBody = new SyncBodyMessage();
            final Iterator elementIterator = element.getChildElements();
            while (elementIterator.hasNext()) {
                final OMElement bodyChildElement = elementIterator.next();
                final String elementName = bodyChildElement.getLocalName();
                if (elementName.equalsIgnoreCase("Alert")) {
                    final AlertRequestCommand alert = this.processAlertElement(bodyChildElement);
                    syncBody.addRequestCmd(alert);
                }
                else if (elementName.equalsIgnoreCase("Status")) {
                    final StatusResponseCommand status = this.processStatusElement(bodyChildElement);
                    syncBody.addResponseCmd(status);
                }
                else if (elementName.equalsIgnoreCase("Replace")) {
                    final ReplaceRequestCommand replace = this.processReplaceElement(bodyChildElement);
                    syncBody.addRequestCmd(replace);
                }
                else if (elementName.equalsIgnoreCase("Results")) {
                    final ResultsResponseCommand results = this.processResultsElement(bodyChildElement);
                    syncBody.addResponseCmd(results);
                }
                else if (elementName.equalsIgnoreCase("Final")) {
                    syncBody.setFinalMessage(Boolean.TRUE);
                }
                else if (elementName.equalsIgnoreCase("Atomic")) {
                    final AtomicRequestCommand atomic = this.processAtomicElement(bodyChildElement);
                    syncBody.addRequestCmd(atomic);
                }
                else if (elementName.equalsIgnoreCase("Delete")) {
                    final DeleteRequestCommand delete = this.processDeleteElement(bodyChildElement);
                    syncBody.addRequestCmd(delete);
                }
                else if (elementName.equalsIgnoreCase("Get")) {
                    final GetRequestCommand get = this.processGetElement(bodyChildElement);
                    syncBody.addRequestCmd(get);
                }
                else if (elementName.equalsIgnoreCase("Add")) {
                    final AddRequestCommand add = this.processAddElement(bodyChildElement);
                    syncBody.addRequestCmd(add);
                }
                else {
                    if (!elementName.equalsIgnoreCase("Sequence")) {
                        continue;
                    }
                    final SequenceRequestCommand sequence = this.processSequenceElement(bodyChildElement);
                    syncBody.addRequestCmd(sequence);
                }
            }
        }
        return syncBody;
    }
    
    private StatusResponseCommand processStatusElement(final OMElement element) {
        StatusResponseCommand status = null;
        if (element != null) {
            status = new StatusResponseCommand();
            final Iterator statusIterator = element.getChildElements();
            while (statusIterator.hasNext()) {
                final OMElement statusElement = statusIterator.next();
                final String elementName = statusElement.getLocalName();
                if (elementName.equalsIgnoreCase("CmdID")) {
                    status.setCmdId(statusElement.getText());
                }
                else if (elementName.equalsIgnoreCase("MsgRef")) {
                    status.setMsgRef(statusElement.getText());
                }
                else if (elementName.equalsIgnoreCase("CmdRef")) {
                    status.setCmdRef(statusElement.getText());
                }
                else if (elementName.equalsIgnoreCase("Cmd")) {
                    status.setCmd(statusElement.getText());
                }
                else if (elementName.equalsIgnoreCase("Data")) {
                    status.setData(statusElement.getText());
                }
                else if (elementName.equalsIgnoreCase("Chal")) {
                    final Challenge chal = this.processChalElement(statusElement);
                    status.setChal(chal);
                }
                else if (elementName.equalsIgnoreCase("TargetRef")) {
                    status.setTargetRef(statusElement.getText());
                }
                else {
                    if (!elementName.equalsIgnoreCase("SourceRef")) {
                        continue;
                    }
                    status.setSourceRef(statusElement.getText());
                }
            }
        }
        return status;
    }
    
    private Challenge processChalElement(final OMElement element) {
        Challenge chal = null;
        if (element != null) {
            chal = new Challenge();
            final Iterator chalIterator = element.getChildElements();
            while (chalIterator.hasNext()) {
                final OMElement chalElement = chalIterator.next();
                if (chalElement.getLocalName().equalsIgnoreCase("Meta")) {
                    final Meta meta = this.processMetaElement(chalElement);
                    chal.setMeta(meta);
                }
            }
        }
        return chal;
    }
    
    private Credential processCredElement(final OMElement element) {
        final Credential credential = new Credential();
        if (element != null) {
            final Iterator credIterator = element.getChildElements();
            while (credIterator.hasNext()) {
                final OMElement credElement = credIterator.next();
                if (credElement.getLocalName().equalsIgnoreCase("Meta")) {
                    final Meta meta = this.processMetaElement(credElement);
                    credential.setMeta(meta);
                }
                else {
                    if (!credElement.getLocalName().equalsIgnoreCase("Data")) {
                        continue;
                    }
                    credential.setData(credElement.getText());
                }
            }
        }
        return credential;
    }
    
    private Meta processMetaElement(final OMElement element) {
        Meta meta = null;
        if (element != null) {
            meta = new Meta();
            final Iterator metaIterator = element.getChildElements();
            while (metaIterator.hasNext()) {
                final OMElement metaElement = metaIterator.next();
                if (metaElement.getLocalName().equalsIgnoreCase("Format")) {
                    meta.setFormat(metaElement.getText());
                }
                else if (metaElement.getLocalName().equalsIgnoreCase("Type")) {
                    meta.setType(metaElement.getText());
                }
                else {
                    if (!metaElement.getLocalName().equalsIgnoreCase("NextNonce")) {
                        continue;
                    }
                    meta.setNextNonce(metaElement.getText());
                }
            }
        }
        return meta;
    }
    
    private ResultsResponseCommand processResultsElement(final OMElement element) {
        ResultsResponseCommand results = null;
        if (element != null) {
            results = new ResultsResponseCommand();
            final Iterator iterator = element.getChildElements();
            while (iterator.hasNext()) {
                final OMElement innerElement = iterator.next();
                if (innerElement.getLocalName().equalsIgnoreCase("CmdID")) {
                    results.setCmdId(innerElement.getText());
                }
                else if (innerElement.getLocalName().equalsIgnoreCase("Item")) {
                    final Item alertItem = this.processItemElement(innerElement);
                    results.addResponseItem(alertItem);
                }
                else if (innerElement.getLocalName().equalsIgnoreCase("CmdRef")) {
                    results.setCmdRef(innerElement.getText());
                }
                else if (innerElement.getLocalName().equalsIgnoreCase("MsgRef")) {
                    results.setMsgRef(innerElement.getText());
                }
                else {
                    if (!innerElement.getLocalName().equalsIgnoreCase("Meta")) {
                        continue;
                    }
                    results.setMeta(this.processMetaElement(innerElement));
                }
            }
        }
        return results;
    }
    
    private ReplaceRequestCommand processReplaceElement(final OMElement element) {
        ReplaceRequestCommand replace = null;
        if (element != null) {
            replace = new ReplaceRequestCommand();
            final Iterator iterator = element.getChildElements();
            while (iterator.hasNext()) {
                final OMElement innerElement = iterator.next();
                if (innerElement.getLocalName().equalsIgnoreCase("CmdID")) {
                    replace.setRequestCmdId(innerElement.getText());
                }
                else {
                    if (!innerElement.getLocalName().equalsIgnoreCase("Item")) {
                        continue;
                    }
                    final Item alertItem = this.processItemElement(innerElement);
                    replace.addRequestItem(alertItem);
                }
            }
        }
        return replace;
    }
    
    private GetRequestCommand processGetElement(final OMElement element) {
        GetRequestCommand get = null;
        if (element != null) {
            get = new GetRequestCommand();
            final Iterator iterator = element.getChildElements();
            while (iterator.hasNext()) {
                final OMElement innerElement = iterator.next();
                if (innerElement.getLocalName().equalsIgnoreCase("CmdID")) {
                    get.setRequestCmdId(innerElement.getText());
                }
                else {
                    if (!innerElement.getLocalName().equalsIgnoreCase("Item")) {
                        continue;
                    }
                    final Item alertItem = this.processItemElement(innerElement);
                    get.addRequestItem(alertItem);
                }
            }
        }
        return get;
    }
    
    private ExecRequestCommand processExecElement(final OMElement element) {
        ExecRequestCommand exec = null;
        if (element != null) {
            exec = new ExecRequestCommand();
            final Iterator iterator = element.getChildElements();
            while (iterator.hasNext()) {
                final OMElement innerElement = iterator.next();
                if (innerElement.getLocalName().equalsIgnoreCase("CmdID")) {
                    exec.setRequestCmdId(innerElement.getText());
                }
                else {
                    if (!innerElement.getLocalName().equalsIgnoreCase("Item")) {
                        continue;
                    }
                    final Item alertItem = this.processItemElement(innerElement);
                    exec.addRequestItem(alertItem);
                }
            }
        }
        return exec;
    }
    
    private AddRequestCommand processAddElement(final OMElement element) {
        AddRequestCommand add = null;
        if (element != null) {
            add = new AddRequestCommand();
            final Iterator iterator = element.getChildElements();
            while (iterator.hasNext()) {
                final OMElement innerElement = iterator.next();
                if (innerElement.getLocalName().equalsIgnoreCase("CmdID")) {
                    add.setRequestCmdId(innerElement.getText());
                }
                else {
                    if (!innerElement.getLocalName().equalsIgnoreCase("Item")) {
                        continue;
                    }
                    final Item alertItem = this.processItemElement(innerElement);
                    add.addRequestItem(alertItem);
                }
            }
        }
        return add;
    }
    
    private DeleteRequestCommand processDeleteElement(final OMElement element) {
        DeleteRequestCommand delete = null;
        if (element != null) {
            delete = new DeleteRequestCommand();
            final Iterator iterator = element.getChildElements();
            while (iterator.hasNext()) {
                final OMElement innerElement = iterator.next();
                if (innerElement.getLocalName().equalsIgnoreCase("CmdID")) {
                    delete.setRequestCmdId(innerElement.getText());
                }
                else {
                    if (!innerElement.getLocalName().equalsIgnoreCase("Item")) {
                        continue;
                    }
                    final Item alertItem = this.processItemElement(innerElement);
                    delete.addRequestItem(alertItem);
                }
            }
        }
        return delete;
    }
    
    private AtomicRequestCommand processAtomicElement(final OMElement element) {
        AtomicRequestCommand atomic = null;
        if (element != null) {
            atomic = new AtomicRequestCommand();
            final Iterator elementIterator = element.getChildElements();
            while (elementIterator.hasNext()) {
                final OMElement bodyChildElement = elementIterator.next();
                final String elementName = bodyChildElement.getLocalName();
                if (elementName.equalsIgnoreCase("Alert")) {
                    final AlertRequestCommand alert = this.processAlertElement(bodyChildElement);
                    atomic.addRequestCmd(alert);
                }
                else if (elementName.equalsIgnoreCase("Replace")) {
                    final ReplaceRequestCommand replace = this.processReplaceElement(bodyChildElement);
                    atomic.addRequestCmd(replace);
                }
                else if (elementName.equalsIgnoreCase("Delete")) {
                    final DeleteRequestCommand replace2 = this.processDeleteElement(bodyChildElement);
                    atomic.addRequestCmd(replace2);
                }
                else if (elementName.equalsIgnoreCase("CmdID")) {
                    atomic.setRequestCmdId(bodyChildElement.getText());
                }
                else if (elementName.equalsIgnoreCase("Add")) {
                    final AddRequestCommand replace3 = this.processAddElement(bodyChildElement);
                    atomic.addRequestCmd(replace3);
                }
                else if (elementName.equalsIgnoreCase("Exec")) {
                    final ExecRequestCommand exec = this.processExecElement(bodyChildElement);
                    atomic.addRequestCmd(exec);
                }
                else {
                    if (!elementName.equalsIgnoreCase("Atomic")) {
                        continue;
                    }
                    final AtomicRequestCommand atomicElement = this.processAtomicElement(bodyChildElement);
                    atomic.addRequestCmd(atomicElement);
                }
            }
        }
        return atomic;
    }
    
    private AlertRequestCommand processAlertElement(final OMElement element) {
        AlertRequestCommand alert = null;
        if (element != null) {
            alert = new AlertRequestCommand();
            final Iterator iterator = element.getChildElements();
            while (iterator.hasNext()) {
                final OMElement innerElement = iterator.next();
                if (innerElement.getLocalName().equalsIgnoreCase("Data")) {
                    alert.setData(innerElement.getText());
                }
                else if (innerElement.getLocalName().equalsIgnoreCase("CmdID")) {
                    alert.setRequestCmdId(innerElement.getText());
                }
                else {
                    if (!innerElement.getLocalName().equalsIgnoreCase("Item")) {
                        continue;
                    }
                    final Item alertItem = this.processItemElement(innerElement);
                    alert.addRequestItem(alertItem);
                }
            }
        }
        return alert;
    }
    
    private Item processItemElement(final OMElement element) {
        Item item = null;
        if (element != null) {
            item = new Item();
            final Iterator iterator = element.getChildElements();
            while (iterator.hasNext()) {
                final OMElement innerElement = iterator.next();
                if (innerElement.getLocalName().equalsIgnoreCase("Source")) {
                    final Iterator sourceIterator = innerElement.getChildElements();
                    final Location location = new Location();
                    while (sourceIterator.hasNext()) {
                        final OMElement sourceElement = sourceIterator.next();
                        if (sourceElement.getLocalName().equalsIgnoreCase("LocURI")) {
                            location.setLocUri(sourceElement.getText());
                        }
                        else {
                            if (!sourceElement.getLocalName().equalsIgnoreCase("LocName")) {
                                continue;
                            }
                            location.setLocName(sourceElement.getText());
                        }
                    }
                    item.setSource(location);
                }
                else if (innerElement.getLocalName().equalsIgnoreCase("Target")) {
                    final Iterator sourceIterator = innerElement.getChildElements();
                    final Location location = new Location();
                    while (sourceIterator.hasNext()) {
                        final OMElement sourceElement = sourceIterator.next();
                        if (sourceElement.getLocalName().equalsIgnoreCase("LocURI")) {
                            location.setLocUri(sourceElement.getText());
                        }
                        else {
                            if (!sourceElement.getLocalName().equalsIgnoreCase("LocName")) {
                                continue;
                            }
                            location.setLocName(sourceElement.getText());
                        }
                    }
                    item.setTarget(location);
                }
                else if (innerElement.getLocalName().equalsIgnoreCase("Data")) {
                    item.setData(innerElement.getText());
                }
                else {
                    if (!innerElement.getLocalName().equalsIgnoreCase("Meta")) {
                        continue;
                    }
                    final Meta meta = this.processMetaElement(innerElement);
                    item.setMeta(meta);
                }
            }
        }
        return item;
    }
    
    private SequenceRequestCommand processSequenceElement(final OMElement element) {
        SequenceRequestCommand sequence = null;
        if (element != null) {
            sequence = new SequenceRequestCommand();
            final Iterator elementIterator = element.getChildElements();
            while (elementIterator.hasNext()) {
                final OMElement bodyChildElement = elementIterator.next();
                final String elementName = bodyChildElement.getLocalName();
                if (elementName.equalsIgnoreCase("Alert")) {
                    final AlertRequestCommand alert = this.processAlertElement(bodyChildElement);
                    sequence.addRequestCmd(alert);
                }
                else if (elementName.equalsIgnoreCase("Replace")) {
                    final ReplaceRequestCommand replace = this.processReplaceElement(bodyChildElement);
                    sequence.addRequestCmd(replace);
                }
                else if (elementName.equalsIgnoreCase("Delete")) {
                    final DeleteRequestCommand replace2 = this.processDeleteElement(bodyChildElement);
                    sequence.addRequestCmd(replace2);
                }
                else if (elementName.equalsIgnoreCase("CmdID")) {
                    sequence.setRequestCmdId(bodyChildElement.getText());
                }
                else if (elementName.equalsIgnoreCase("Add")) {
                    final AddRequestCommand replace3 = this.processAddElement(bodyChildElement);
                    sequence.addRequestCmd(replace3);
                }
                else if (elementName.equalsIgnoreCase("Exec")) {
                    final ExecRequestCommand exec = this.processExecElement(bodyChildElement);
                    sequence.addRequestCmd(exec);
                }
                else if (elementName.equalsIgnoreCase("Get")) {
                    final GetRequestCommand getElement = this.processGetElement(bodyChildElement);
                    sequence.addRequestCmd(getElement);
                }
                else {
                    if (!elementName.equalsIgnoreCase("Atomic")) {
                        continue;
                    }
                    final AtomicRequestCommand atomicElement = this.processAtomicElement(bodyChildElement);
                    sequence.addRequestCmd(atomicElement);
                }
            }
        }
        return sequence;
    }
    
    public static String replaceInvalidCharacters(final String buffer) {
        final String[] patterns = { "https://[a-zA-Z0-9.-]*(:[0-9]{1,5})?/mdm/wpserver\\?cid=[0-9]*&erid=[0-9]*&muid=[0-9]*&SCOPE=[a-zA-Z0-9/\\\\]*&authtoken=[a-zA-Z0-9]*", "https://[a-zA-Z0-9.-]*(:[0-9]{1,5})?/mdm/wpserver\\?cid=[0-9]*&erid=[0-9]*&muid=[0-9]*&authtoken=[a-zA-Z0-9]*&SCOPE=[a-zA-Z0-9/\\\\]*", "https://[a-zA-Z0-9.-]*(:[0-9]{1,5})?/mdm/client/v1/wpserver\\?cid=[0-9]*&erid=[0-9]*&muid=[0-9]*&encapiKey=[a-zA-Z0-9%.]*&SerialNumber=[a-zA-Z0-9]*", "https://[a-zA-Z0-9.-]*(:[0-9]{1,5})?/mdm/client/v1/wpserver\\?cid=[0-9]*&erid=[0-9]*&muid=[0-9]*&encapiKey=[a-zA-Z0-9%.]*", "https://[a-zA-Z0-9.-]*(:[0-9]{1,5})?/mdm/wpserver\\?cid=[0-9]*&erid=[0-9]*&muid=[0-9]*", "https://[a-zA-Z0-9.-]*(:[0-9]{1,5})?/getmFile.do\\?&fid=[0-9]*&authtoken=[a-zA-Z0-9]*&SCOPE=[a-zA-z0-9]*/MDMCloudEnrollment", "https://[a-zA-Z0-9.-]*(:[0-9]{1,5})?/getmemdmFile.do\\?&fid=[0-9]*&encapiKey=[a-zA-Z0-9.]*", "https://[a-zA-Z0-9.-]*(:[0-9]{1,5})?/api/v1/mdm/getmfiles/[0-9]*\\?&authtoken=[a-zA-Z0-9]*&SCOPE=[a-zA-z0-9]*/MDMCloudEnrollment", "https://[a-zA-Z0-9.-]*(:[0-9]{1,5})?/api/v1/mdm/getmemdmfiles/[0-9]*\\?&encapiKey=[a-zA-Z0-9.]*" };
        String replacedString = buffer;
        for (final String regExp : patterns) {
            replacedString = replaceInvalidCharacters(replacedString, regExp);
        }
        return replacedString;
    }
    
    public static String replaceInvalidCharacters(final String buffer, final String regexp) {
        final StringBuffer requestBuffer = new StringBuffer();
        final Pattern pattern = Pattern.compile(regexp);
        final Matcher matcher = pattern.matcher(buffer);
        while (matcher.find()) {
            final String match = matcher.group();
            final String replacement = match.replaceAll("&", "&amp;");
            matcher.appendReplacement(requestBuffer, replacement);
        }
        matcher.appendTail(requestBuffer);
        return requestBuffer.toString();
    }
}
