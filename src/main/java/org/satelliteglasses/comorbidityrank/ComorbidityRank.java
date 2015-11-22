package org.satelliteglasses.comorbidityrank;

/**
 * Entry point for an application that ranks comorbidities for a disease or condition
 * based on MeSH descriptors in the PubMed corpus.
 */
public class ComorbidityRank {

    public static void main(final String[] args) throws Exception {
        final EntrezClient.EntrezState<PubMedSearchResult> searchState =
                new EntrezSearch(EntrezDatabase.PUBMED)
                    .addTerm("obesity", "mh", null)
                    .addTerm("diabetes", "mh", EntrezSearch.Operator.AND)
                    .addTerm("cancer", "mh", EntrezSearch.Operator.AND)
                    .setMaxDateString("2000")
                    .setMinDateString("2000")
                    .setMaxReturned(0)
                    .setUseHistory(true)
                    .go();

        System.out.println("Found " + searchState.mostRecentResponse.count + " records. WebEnv is '" + searchState.webEnv + "'");

        final EntrezClient.EntrezState<PubMedFetchResults> fetchState =
                new EntrezFetch(EntrezDatabase.PUBMED)
                    .setWebEnv(searchState)
                    .go();

        System.out.println("Retrieved these articles:");
        for (final PubMedFetchResults.PubMedFetchResult result : fetchState.mostRecentResponse) {
            System.out.println(result.articleTitle);
            for (final PubMedFetchResults.MeshDescriptor meshDescriptor : result.meshHeadingList.meshDescriptors) {
                System.out.print("     ");
                System.out.print(meshDescriptor.descriptorName);
                System.out.print(", ");
            }
            System.out.println();
        }
    }

}
