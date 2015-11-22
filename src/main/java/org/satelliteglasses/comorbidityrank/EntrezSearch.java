package org.satelliteglasses.comorbidityrank;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Performs a search in the Entrez PubMed database. This class roughly follows a Builder
 * pattern, meaning that all the "addX()" methods on this class return 'this', with the
 * values applied. Note that no error checking is performed on these setters. Calling
 * 'go()' performs a search with the current values and returns a state object that
 * lets you get the records and its PubMed WebEnv ID.
 */
public class EntrezSearch extends EntrezClient {

    private static final Logger LOGGER = Logger.getLogger(EntrezSearch.class);

    private static final String RESOURCE = "/esearch.fcgi";

    public enum Operator {
        AND,
        OR,
        NOT
    }

    private StringBuilder queryTerm = new StringBuilder();
    private String minDateString = null;
    private String maxDateString = null;
    private int maxReturnedCount = -1;

    public EntrezSearch(final EntrezDatabase db) {
        super(db);
    }

    @Override
    public EntrezState<PubMedSearchResult> go() throws IOException {
        if (this.queryTerm.length() == 0) {
            throw new IllegalStateException("A query term must be set on the EntrezSearch before calling go()");
        }

        final StringBuilder queryBuilder = this.getBaseQuery();

        queryBuilder.append("&").append(this.queryTerm.toString().replace(" ", "+"));
        if (this.minDateString != null) queryBuilder.append("&").append("mindate=").append(this.minDateString);
        if (this.maxDateString != null) queryBuilder.append("&").append("maxdate=").append(this.maxDateString);
        if (this.maxReturnedCount >= 0) queryBuilder.append("&").append("retmax=").append(this.maxReturnedCount);

        final URI searchURI;
        try {
            searchURI = new URI(
                    ENTREZ_PROTO,
                    null,
                    ENTREZ_SERVER,
                    80,
                    ENTREZ_BASE_PATH + RESOURCE,
                    queryBuilder.toString(),
                    null);

        } catch (final URISyntaxException urise) {
            throw new IOException("Could not build the URI for the query: " + urise.getMessage(), urise);
        }

        LOGGER.debug("Searching with URI '" + searchURI + "'");

        final PubMedSearchResult result = (PubMedSearchResult) XMLUtils.unmarshal(searchURI);
        return new EntrezState<PubMedSearchResult>(result.webEnv, result.queryKey, result);
    }

    /**
     * Add the given term to the search. 'term' must never be null or empty. 'field' may be null or
     * empty if a MeSH *and* keyword search is desired. 'op' may not be null if there have been
     * previous calls to addTerm().
     */
    public EntrezSearch addTerm(final String term, final String field, final Operator op) {
        if (term == null || term.trim().isEmpty()) throw new IllegalArgumentException("The 'term' parameter must have a value");

        final String trimTerm = term.trim();

        if (this.queryTerm.length() != 0) {
            if (op == null) throw new IllegalArgumentException("Operator must not be null if previous terms were set");
            this.queryTerm.append(' ');
            this.queryTerm.append(op.name()).append(' ');
        } else {
            // Done here, rather than in initialization, so we can detect when we've called
            // this method at least once
            this.queryTerm.append("term=");
        }

        this.queryTerm.append(trimTerm);
        if (field != null && ! field.isEmpty()) this.queryTerm.append("[" + field + "]");

        return this;
    }

    /**
     * Set the minimum date for the query. The string must follow Entrez's guidelines for properly
     * formatted date strings. Note that this formatting is not checked here; the query, when run,
     * will simply fail if it's not in the correct format.
     */
    public EntrezSearch setMinDateString(final String mDS) {
        this.minDateString = mDS;
        return this;
    }

    /**
     * Set the maximum date for the query. The string must follow Entrez's guidelines for properly
     * formatted date strings. Note that this formatting is not checked here; the query, when run,
     * will simply fail if it's not in the correct format.
     */
    public EntrezSearch setMaxDateString(final String mDS) {
        this.maxDateString = mDS;
        return this;
    }

    public EntrezSearch setMaxReturned(final int count) {
        this.maxReturnedCount = count;
        return this;
    }
}
