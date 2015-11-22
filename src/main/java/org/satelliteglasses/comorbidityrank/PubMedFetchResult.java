package org.satelliteglasses.comorbidityrank;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A single PubMed record. This class is specific to the format returned by
 * Entrez since it uses JAXB binding annotations.
 */
@XmlRootElement(name="PubmedArticleSet")
public class PubMedFetchResult {
}
