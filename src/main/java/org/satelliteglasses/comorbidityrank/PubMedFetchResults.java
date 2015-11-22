package org.satelliteglasses.comorbidityrank;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Iterator;
import java.util.List;

/**
 * A single PubMed record. This class is specific to the format returned by
 * Entrez since it uses JAXB binding annotations.
 */
@XmlRootElement(name="PubmedArticleSet")
public class PubMedFetchResults implements Iterable<PubMedFetchResults.PubMedFetchResult> {

    private Iterator<PubMedFetchResult> articleIterator = null;

    @XmlElement(name="PubmedArticle")
    public List<PubMedFetchResult> articles;

    @Override
    public Iterator<PubMedFetchResult> iterator() {
        // Only ever return one iterator for this result. Calling iterator() on List
        // gives you new iterators every time, which is incredibly confusing and
        // possibly a serious bug.
        if (this.articleIterator == null) this.articleIterator = this.articles.iterator();
        return this.articleIterator;
    }

    /**
     * A single PubMed article. Note that these fields don't match the raw results from
     * PubMed: the fields of interest (i.e., those listed here) are extracted from that
     * document into a new XML doc, which is then parsed here. @see pubmed_fetch_results.xslt
     */
    @XmlRootElement(name="PubmedArticle")
    public static class PubMedFetchResult {

        @XmlElement(name="PMID")
        public String pmID;

        @XmlElement(name="ArticleTitle")
        public String articleTitle;

        @XmlElement(name="JournalTitle")
        public String journalTitle;

        @XmlElement(name="PublicationYear")
        public int publicationYear;

        @XmlElement(name="MeshHeadingList")
        public List<MeshDescriptor> meshDescriptors;
    }

    @XmlRootElement(name="MeshHeading")
    public static class MeshDescriptor {
        @XmlElement(name="DescriptorName")
        public String descriptorName;

        @XmlElement(name="MajorTopic")
        public boolean isMajorTopic;

        @XmlElement(name="UI")
        public String conceptID;

        @XmlElement(name="QualifierList")
        public List<MeshQualifier> qualifiers;
    }

    @XmlRootElement(name="Qualifier")
    public static class MeshQualifier {
        @XmlElement(name="QualifierName")
        public String descriptorName;

        @XmlElement(name="MajorTopic")
        public boolean isMajorTopic;

        @XmlElement(name="UI")
        public String conceptID;
    }

}