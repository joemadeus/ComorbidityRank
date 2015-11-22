package org.satelliteglasses.comorbidityrank;

import org.junit.Test;

import javax.xml.bind.Marshaller;
import java.util.ArrayList;

public class MarshalTest {

    @Test
    public void testMarshal() throws Exception {
        final PubMedFetchResults.PubMedFetchResult result = new PubMedFetchResults.PubMedFetchResult();
        result.articleTitle = "Article Title";
        result.journalTitle = "Journal Title";
        result.pmID = "00000011";
        result.publicationYear = 2015;
        result.meshHeadingList = new PubMedFetchResults.MeshHeadingList();
        result.meshHeadingList.meshDescriptors = new ArrayList<PubMedFetchResults.MeshDescriptor>(2);

        final PubMedFetchResults.MeshDescriptor meshOne = new PubMedFetchResults.MeshDescriptor();
        meshOne.conceptID = "000111";
        meshOne.descriptorName = "MeshOne";
        meshOne.isMajorTopic = false;

        PubMedFetchResults.Qualifier qualifierA = new PubMedFetchResults.Qualifier();
        qualifierA.descriptorName = "qualA";
        qualifierA.conceptID = "q000aa";
        qualifierA.isMajorTopic = false;

        PubMedFetchResults.Qualifier qualifierB = new PubMedFetchResults.Qualifier();
        qualifierB.descriptorName = "qualB";
        qualifierB.conceptID = "q000bb";
        qualifierB.isMajorTopic = true;

        meshOne.qualifiers = new PubMedFetchResults.QualifierList();
        meshOne.qualifiers.qualifierList = new ArrayList<PubMedFetchResults.Qualifier>(1);
        meshOne.qualifiers.qualifierList.add(qualifierA);
        meshOne.qualifiers.qualifierList.add(qualifierB);

        final PubMedFetchResults.MeshDescriptor meshTwo = new PubMedFetchResults.MeshDescriptor();
        meshTwo.conceptID = "000222";
        meshTwo.descriptorName = "MeshTwo";
        meshTwo.isMajorTopic = true;
        meshTwo.qualifiers = new PubMedFetchResults.QualifierList();
        meshTwo.qualifiers.qualifierList = new ArrayList<PubMedFetchResults.Qualifier>(1);

        result.meshHeadingList.meshDescriptors.add(meshOne);
        result.meshHeadingList.meshDescriptors.add(meshTwo);

        final Marshaller marshaller = XMLUtils.JAXB_CONTEXT.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(result, System.out);
    }
}
