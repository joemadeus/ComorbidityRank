package org.satelliteglasses.comorbidityrank;

import java.util.ArrayList;
import java.util.List;

/**
 * Entry point for an application that ranks comorbidities for a disease or condition
 * based on MeSH descriptors in the PubMed corpus.
 */
public class ComorbidityRank {

    public static void main(final String[] args) throws Exception {
        final EntrezClient.EntrezState<PubMedSearchResult> searchState =
                new EntrezSearch(EntrezDatabase.PUBMED)
                    .addTerm("obesity", "MeSH Major Topic", null)
                    .setMinDateString("2000")
                    .setMaxDateString("2000")
                    .setMaxReturned(0)
                    .setUseHistory(true)
                    .go();

        System.out.println("Found " + searchState.mostRecentResponse.count + " records. WebEnv is '" + searchState.webEnv + "'");

        final EntrezClient.EntrezState<PubMedFetchResults> fetchState =
                new EntrezFetch(EntrezDatabase.PUBMED)
                    .setWebEnv(searchState)
                    .go();

        final List<String> meshDescriptors = new ArrayList<String>();
        for (final PubMedFetchResults.PubMedFetchResult result : fetchState.mostRecentResponse) {
            for (final PubMedFetchResults.PubMedMeshDescriptor meshDescriptor : result.meshHeadingList.meshDescriptors) {
                meshDescriptors.add(meshDescriptor.descriptorName);
            }
        }


        if (true) return;

        System.out.println("Retrieved these articles:");
        for (final PubMedFetchResults.PubMedFetchResult result : fetchState.mostRecentResponse) {
            System.out.println(result.articleTitle);
            for (final PubMedFetchResults.PubMedMeshDescriptor meshDescriptor : result.meshHeadingList.meshDescriptors) {
                System.out.print("     ");
                System.out.print(meshDescriptor.descriptorName);
                System.out.print(", ");
            }
            System.out.println();
        }
    }

}
