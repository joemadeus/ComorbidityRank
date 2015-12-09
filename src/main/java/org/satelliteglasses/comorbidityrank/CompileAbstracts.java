package org.satelliteglasses.comorbidityrank;

import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility that downloads abstracts from PubMed for the given search term, removes
 * punctuation and changes all text to lower case. The resulting text is then added
 * the provided file. This is useful for NLP work.
 */
public class CompileAbstracts {

    private static final Logger LOGGER = Logger.getLogger(CompileAbstracts.class);

    private static final Matcher WORD_CONTINUATION = Pattern.compile("(\\w+)-\\s+(\\w+)").matcher("");
    private static final Matcher PUNCTUATION_WHITESPACE = Pattern.compile("\\s+\\p{Punct}*\\s+").matcher("");
    private static final Matcher PUNCTUATION_AFTER = Pattern.compile("(?<=\\w+)\\p{Punct}+\\s*").matcher("");
    private static final Matcher PUNCTUATION_BEFORE = Pattern.compile("\\s+\\p{Punct}+(?=\\w+)").matcher("");

    public static void main(final String[] args) throws Exception {

        if (args.length != 3) {
            System.err.println("Usage: DownloadAbstracts [search term] [number to download] [start index]");
            System.exit(1);
        }

        final String searchTerm = args[0];
        final int count = Integer.valueOf(args[1]);
        final int start = Integer.valueOf(args[2]);

        final File destinationFile = new File(searchTerm + ".txt");
        final BufferedWriter output = new BufferedWriter(new FileWriter(destinationFile));
        if (destinationFile.exists()) {
            LOGGER.warn("Appending new text to file " + destinationFile.getAbsolutePath());
        }

        final EntrezClient.EntrezState<PubMedSearchResult> searchState = new EntrezSearch(EntrezDatabase.PUBMED)
                .addTerm(searchTerm, "MeSH Major Topic", null)
                .setMinDateString("2000")
                .setMaxDateString("2012")
                .setMaxReturned(0)
                .setUseHistory(true)
                .go();
        System.out.println("Found " + searchState.mostRecentResponse.count + " records pertaining to " + searchTerm + ".");

        final EntrezClient.EntrezState<PubMedFetchResults> fetchState = new EntrezFetch(EntrezDatabase.PUBMED)
                .setStartIndex(start)
                .setMaxReturnedCount(count)
                .setWebEnv(searchState)
                .setQueryKey(searchState.queryKey)
                .go();
        System.out.println("Fetched " + fetchState.mostRecentResponse.articles.size() + " articles.");

        for (final PubMedFetchResults.PubMedFetchResult result : fetchState.mostRecentResponse) {
            LOGGER.trace("Article " + result.pmID);
            if (result.articleAbstract == null || result.articleAbstract.isEmpty()) {
                LOGGER.debug("Article " + result.pmID + " did not have an abstract");
                continue;
            }

            String transformed = result.articleAbstract.toLowerCase();
            if (LOGGER.isTraceEnabled()) LOGGER.trace(transformed);

            // TODO: Handle word continuation, if we even need to
//            transformed = WORD_CONTINUATION.reset(transformed).group(1);
//            if (LOGGER.isTraceEnabled()) LOGGER.trace(transformed);

            transformed = PUNCTUATION_WHITESPACE.reset(transformed).replaceAll(" ");
            if (LOGGER.isTraceEnabled()) LOGGER.trace(transformed);

            transformed = PUNCTUATION_AFTER.reset(transformed).replaceAll(" ");
            if (LOGGER.isTraceEnabled()) LOGGER.trace(transformed);

            transformed = PUNCTUATION_BEFORE.reset(transformed).replaceAll(" ");
            if (LOGGER.isTraceEnabled()) LOGGER.trace(transformed);


            // Output only if we haven't transformed everything away
            if (transformed.isEmpty()) LOGGER.warn("Article " + result.pmID + " didn't have an abstract after transforming");
            else output.write(transformed);
        }
    }
}
