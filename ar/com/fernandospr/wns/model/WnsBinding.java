package ar.com.fernandospr.wns.model;

import javax.xml.bind.annotation.XmlElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@XmlRootElement(name = "binding")
public class WnsBinding
{
    @XmlAttribute
    public String template;
    @XmlAttribute
    public String fallback;
    @XmlAttribute
    public String lang;
    @XmlAttribute
    public String baseUri;
    @XmlAttribute
    public String branding;
    @XmlAttribute
    public Boolean addImageQuery;
    @JacksonXmlElementWrapper(useWrapping = false)
    @XmlElement(name = "image")
    public List<WnsImage> images;
    @JacksonXmlElementWrapper(useWrapping = false)
    @XmlElement(name = "text")
    public List<WnsText> texts;
    @XmlAttribute
    public String contentId;
}
