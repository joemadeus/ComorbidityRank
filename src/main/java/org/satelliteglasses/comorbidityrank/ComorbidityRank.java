package org.satelliteglasses.comorbidityrank;

import org.apache.log4j.Logger;

import java.io.File;

/**
 * Entry point for an application that ranks comorbidities for a disease or condition
 * based on MeSH descriptors in the PubMed corpus.
 */
public class ComorbidityRank {

    private static final Logger LOGGER = Logger.getLogger(ComorbidityRank.class);

    public static void main(final String[] args) throws Exception {

        if (args.length != 3) {
            System.err.println("Usage: ComorbidityRank [PubMed corpus count] [search term] [pruned MeSH descriptor file]");
            System.exit(1);
        }

        final int pubmedCount = Integer.valueOf(args[0]);
        final String inputTerm = args[1];
        final File meshFile = new File(args[2]);
        if ( ! meshFile.exists() || ! meshFile.isFile()) {
            System.err.println("The path to the MeSH descriptor file must exist and be readable");
            System.exit(1);
        }

        System.out.println("Loading MeSH descriptors from " + meshFile.getAbsolutePath());
        final MeSHDescriptors meshDescriptors = (MeSHDescriptors) XMLUtils.unmarshal(meshFile.toURI());

        // Not final because we need to reset the WebEnv every 128 requests. This seems to be an
        // undocumented limitation.
        EntrezClient.EntrezState<PubMedSearchResult> inputTermSearch = doInputTermSearch(inputTerm);
        System.out.println("Found " + inputTermSearch.mostRecentResponse.count + " records pertaining to " + inputTerm + ".");

        System.out.println("Querying PubMed for each of the " + meshDescriptors.meSHDescriptors.size() + " descriptors in the MeSH file:");
        int queryCount = 1;
        for (final MeSHDescriptors.MeSHDescriptor meshDescriptor : meshDescriptors.meSHDescriptors) {
            if (queryCount > 100) {
                LOGGER.debug("Resetting the WebEnv");
                inputTermSearch = doInputTermSearch(inputTerm);
                queryCount = 1;
            }

            final EntrezClient.EntrezState<PubMedSearchResult> descriptorTermSearch =
                    new EntrezSearch(EntrezDatabase.PUBMED)
                            .addTerm(meshDescriptor.descriptorName, "MeSH Major Topic", null)
                            .setMinDateString("2000")
                            .setMaxDateString("2012")
                            .setMaxReturned(0)
                            .setUseHistory(true)
                            .setWebEnv(inputTermSearch)
                            .go();

            // For the next three queries we keep the WebEnv but set useHistory to false, since we want to
            // use previously queried search terms and descriptor terms but don't want to save these results

            final EntrezClient.EntrezState<PubMedSearchResult> inputTermAndDescriptor =
                    new EntrezSearch(EntrezDatabase.PUBMED)
                            .addTerm("#" + inputTermSearch.queryKey, null, null)
                            .addTerm("#" + descriptorTermSearch.queryKey, null, EntrezSearch.Operator.AND)
                            .setMaxReturned(0)
                            .setUseHistory(false)
                            .setWebEnv(inputTermSearch)
                            .go();

            final EntrezClient.EntrezState<PubMedSearchResult> inputTermNotDescriptor =
                    new EntrezSearch(EntrezDatabase.PUBMED)
                            .addTerm("#" + inputTermSearch.queryKey, null, null)
                            .addTerm("#" + descriptorTermSearch.queryKey, null, EntrezSearch.Operator.NOT)
                            .setMaxReturned(0)
                            .setUseHistory(false)
                            .setWebEnv(inputTermSearch)
                            .go();

            final EntrezClient.EntrezState<PubMedSearchResult> descriptorNotInputTerm =
                    new EntrezSearch(EntrezDatabase.PUBMED)
                            .addTerm("#" + descriptorTermSearch.queryKey, null, null)
                            .addTerm("#" + inputTermSearch.queryKey, null, EntrezSearch.Operator.NOT)
                            .setMaxReturned(0)
                            .setUseHistory(false)
                            .setWebEnv(inputTermSearch)
                            .go();

            queryCount += 4;

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

            Thread.sleep(750); // Throttling to keep PubMed happy(ish)
        }
    }

    private static EntrezClient.EntrezState<PubMedSearchResult> doInputTermSearch(final String inputTerm) throws Exception {
        return new EntrezSearch(EntrezDatabase.PUBMED)
                .addTerm(inputTerm, "MeSH Major Topic", null)
                .setMinDateString("2000")
                .setMaxDateString("2012")
                .setMaxReturned(0)
                .setUseHistory(true)
                .go();
    }
}
