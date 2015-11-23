package org.satelliteglasses.comorbidityrank;

import java.io.File;

/**
 * Entry point for an application that ranks comorbidities for a disease or condition
 * based on MeSH descriptors in the PubMed corpus.
 */
public class ComorbidityRank {

    public static void main(final String[] args) throws Exception {

        if (args.length != 3) {
            System.err.println("Usage: ComorbidRank [PubMed corpus count] [search term] [pruned MeSH descriptor file]");
            System.exit(1);
        }

        final int pubmedCount = Integer.valueOf(args[0]);
        final String searchTerm = args[1];
        final File meshFile = new File(args[2]);
        if ( ! meshFile.exists() || ! meshFile.isFile()) {
            System.err.println("The path to the MeSH descriptor file must exist and be readable");
            System.exit(1);
        }

        final EntrezClient.EntrezState<PubMedSearchResult> inputTermSearch =
                new EntrezSearch(EntrezDatabase.PUBMED)
                        .addTerm(searchTerm, "MeSH Major Topic", null)
                        .setMinDateString("2000")
                        .setMaxDateString("2012")
                        .setMaxReturned(0)
                        .setUseHistory(true)
                        .go();
        System.out.println("Found " + inputTermSearch.mostRecentResponse.count + " records pertaining to " + searchTerm + ".");

        System.out.println("Loading MeSH descriptors from " + meshFile.getAbsolutePath());
        final MeSHDescriptors meshDescriptors = (MeSHDescriptors) XMLUtils.unmarshal(meshFile.toURI());

        System.out.println("Querying PubMed for each of the " + meshDescriptors.meSHDescriptors.size() + " descriptors in the MeSH file:");
        for (final MeSHDescriptors.MeSHDescriptor meshDescriptor : meshDescriptors.meSHDescriptors) {
            final EntrezClient.EntrezState<PubMedSearchResult> descriptorTermSearch =
                    new EntrezSearch(EntrezDatabase.PUBMED)
                            .addTerm(meshDescriptor.descriptorName, "MeSH Major Topic", null)
                            .setMinDateString("2000")
                            .setMaxDateString("2012")
                            .setMaxReturned(0)
                            .setUseHistory(true)
                            .setWebEnv(inputTermSearch)
                            .go();

            final EntrezClient.EntrezState<PubMedSearchResult> inputTermAndDescriptor =
                    new EntrezSearch(EntrezDatabase.PUBMED)
                            .addTerm("#" + inputTermSearch.queryKey, null, null)
                            .addTerm("#" + descriptorTermSearch.queryKey, null, EntrezSearch.Operator.AND)
                            .setMaxReturned(0)
                            .setUseHistory(true)
                            .setWebEnv(inputTermSearch)
                            .go();

            final EntrezClient.EntrezState<PubMedSearchResult> inputTermNotDescriptor =
                    new EntrezSearch(EntrezDatabase.PUBMED)
                            .addTerm("#" + inputTermSearch.queryKey, null, null)
                            .addTerm("#" + descriptorTermSearch.queryKey, null, EntrezSearch.Operator.NOT)
                            .setMaxReturned(0)
                            .setUseHistory(true)
                            .setWebEnv(inputTermSearch)
                            .go();

            final EntrezClient.EntrezState<PubMedSearchResult> descriptorNotInputTerm =
                    new EntrezSearch(EntrezDatabase.PUBMED)
                            .addTerm("#" + descriptorTermSearch.queryKey, null, null)
                            .addTerm("#" + inputTermSearch.queryKey, null, EntrezSearch.Operator.NOT)
                            .setMaxReturned(0)
                            .setUseHistory(true)
                            .setWebEnv(inputTermSearch)
                            .go();

            final int a = inputTermAndDescriptor.mostRecentResponse.count;
            final int b = inputTermNotDescriptor.mostRecentResponse.count;
            final int c = descriptorNotInputTerm.mostRecentResponse.count;
            final int d = pubmedCount - inputTermAndDescriptor.mostRecentResponse.count;

            System.out.println(
                    String.format(
                            "%s, %d, %d, %d, %d, %f",
                            meshDescriptor.descriptorName.replaceAll(",", ""),
                            a,
                            b,
                            c,
                            d,
                            (( a / (float) (a + b)) / ( c / (float) (c + d)))
                    ));
        }
    }

}
