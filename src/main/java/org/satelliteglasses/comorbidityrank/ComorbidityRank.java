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
                    .setMaxDateString("2012")
                    .setMinDateString("2010")
                    .setMaxReturned(0)
                    .setUseHistory(false)
                    .go();

        System.out.print("Found " + searchState.mostRecentResponse.count + " records.");
    }

}
