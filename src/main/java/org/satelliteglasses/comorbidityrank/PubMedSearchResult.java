package org.satelliteglasses.comorbidityrank;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Encapsulates the Entrez history server identifier, the query key and the number
 * of documents returned from a Entrez search. This class is specific to the format
 * returned by Entrez since it uses JAXB binding annotations.
 */
@XmlRootElement(name="eSearchResult")
public class PubMedSearchResult {

    @XmlElement(name="WebEnv")
    public String webEnv;

    @XmlElement(name="QueryKey")
    public int queryKey;

    @XmlElement(name="Count")
    public int count;

}
