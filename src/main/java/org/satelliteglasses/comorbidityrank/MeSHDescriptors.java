package org.satelliteglasses.comorbidityrank;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Set;

/**
 * See the NIH docs. This particular representation is a subset of the fields defined
 * by NIH/NLM -- only those useful to this application are being kept. This class is
 * specific to the output of an XSLT that parses the input MeSH vocabulary. (@see
 * mesh_descriptor.xslt)
 */
@XmlRootElement(name="DescriptorRecordSet")
public class MeSHDescriptors {

    @XmlElement(name="DescriptorRecord")
    public List<MeSHDescriptor> meSHDescriptors;

    @XmlRootElement(name="DescriptorRecord")
    public static class MeSHDescriptor {
        @XmlElement(name="DescriptorUI")
        public String descriptorUI;

        @XmlElement(name="DescriptorName")
        public String descriptorName;

        @XmlElement(name="SemanticTypeList")
        public Set<SemanticTypeUI> semanticTypes;
    }

    @XmlRootElement(name="SemanticTypeUI")
    public static class SemanticTypeUI {
        @XmlElement(name="SemanticTypeUI")
        public String semanticTypeUI;
    }
}
