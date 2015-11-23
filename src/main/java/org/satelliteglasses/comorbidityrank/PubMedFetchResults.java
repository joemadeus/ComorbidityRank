package org.satelliteglasses.comorbidityrank;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Iterator;
import java.util.List;

/**
 * A single PubMed record. This class is specific to the format returned by
 * pre-processing Entrez data using XSLT. (@see pubmet_fetch_results.xslt)
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
        // can create serious, hard-to-diagnose bugs.
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
        public MeshHeadingList meshHeadingList;
    }

    @XmlRootElement(name="MeshHeadingList")
    public static class MeshHeadingList {
        @XmlElement(name="MeshHeading")
        public List<PubMedMeshDescriptor> meshDescriptors;
    }

    @XmlRootElement(name="MeshHeading")
    public static class PubMedMeshDescriptor {
        @XmlElement(name="DescriptorName")
        public String descriptorName;

        @XmlElement(name="MajorTopic")
        public boolean isMajorTopic;

        @XmlElement(name="ConceptID")
        public String conceptID;

        @XmlElement(name="QualifierList")
        public QualifierList qualifiers;
    }

    @XmlRootElement(name="QualifierList")
    public static class QualifierList {
        @XmlElement(name="Qualifier")
        public List<Qualifier> qualifierList;
    }

    @XmlRootElement(name="Qualifier")
    public static class Qualifier {
        @XmlElement(name="QualifierName")
        public String descriptorName;

        @XmlElement(name="MajorTopic")
        public boolean isMajorTopic;

        @XmlElement(name="ConceptID")
        public String conceptID;
    }

}
