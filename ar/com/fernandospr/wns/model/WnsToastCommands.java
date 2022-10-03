package ar.com.fernandospr.wns.model;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "commands")
public class WnsToastCommands
{
    @XmlAttribute
    public String scenario;
    @JacksonXmlElementWrapper(useWrapping = false)
    @XmlElement(name = "command")
    public List<WnsToastCommand> command;
    
    public WnsToastCommands(final String scenario) {
        this.scenario = scenario;
        this.command = new ArrayList<WnsToastCommand>();
    }
    
    public void addCommand(final String id, final String arguments) {
        this.command.add(new WnsToastCommand(id, arguments));
    }
}
