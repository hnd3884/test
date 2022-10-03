package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.ws.FaultAction;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.ws.Action;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "faultAction" })
@XmlRootElement(name = "action")
public class XmlAction implements Action
{
    @XmlElement(name = "fault-action")
    protected List<XmlFaultAction> faultAction;
    @XmlAttribute(name = "input")
    protected String input;
    @XmlAttribute(name = "output")
    protected String output;
    
    public List<XmlFaultAction> getFaultAction() {
        if (this.faultAction == null) {
            this.faultAction = new ArrayList<XmlFaultAction>();
        }
        return this.faultAction;
    }
    
    public String getInput() {
        return Util.nullSafe(this.input);
    }
    
    public void setInput(final String value) {
        this.input = value;
    }
    
    public String getOutput() {
        return Util.nullSafe(this.output);
    }
    
    public void setOutput(final String value) {
        this.output = value;
    }
    
    @Override
    public String input() {
        return Util.nullSafe(this.input);
    }
    
    @Override
    public String output() {
        return Util.nullSafe(this.output);
    }
    
    @Override
    public FaultAction[] fault() {
        return new FaultAction[0];
    }
    
    @Override
    public Class<? extends Annotation> annotationType() {
        return Action.class;
    }
}
