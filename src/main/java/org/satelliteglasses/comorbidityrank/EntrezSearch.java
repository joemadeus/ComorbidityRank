package org.satelliteglasses.comorbidityrank;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Performs a search in the Entrez PubMed database. This class roughly follows
 * a Builder pattern, meaning that all the "addX()" methods on this class return
 * 'this', with the values applied. Calling 'search()' performs a search with the
 * current values and returns a state object that lets you get the records and
 * its PubMed WebEnv ID.
 */
public class EntrezSearch extends EntrezClient<PubMedSearchResult> {

    private static final Logger LOGGER = Logger.getLogger(EntrezSearch.class);

    public enum Operator {
        AND,
        OR,
        NOT
    }

    private StringBuilder queryTerm = new StringBuilder();
    private String minDateString = null;
    private String maxDateString = null;

    public EntrezSearch(final EntrezDatabase db) {
        super(db);
    }

    @Override
    public EntrezState go() throws IOException {
        if (this.queryTerm.length() == 0) {
            throw new IllegalStateException("A query term must be set on the EntrezSearch before calling go()");
        }

        final StringBuilder queryBuilder = this.getBaseQuery();

        queryBuilder.append("&").append(this.queryTerm.toString().replace(" ", "+"));
        if (this.minDateString != null) queryBuilder.append("&").append(this.minDateString);
        if (this.maxDateString != null) queryBuilder.append("&").append(this.maxDateString);

        final URI searchURI;
        try {
            searchURI = new URI(
                    ENTREZ_PROTO,
                    null,
                    ENTREZ_SERVER,
                    80,
                    ENTREZ_BASE_PATH,
                    queryBuilder.toString(),
                    null);

        } catch (final URISyntaxException urise) {
            throw new IOException("Could not build the URI for the query: " + urise.getMessage(), urise);
        }

        LOGGER.debug("Searching with URI " + searchURI);

        final PubMedSearchResult result = (PubMedSearchResult) XMLUtils.unmarshal(searchURI);
        return new EntrezState(result.getWebEnv, result.queryKey, result);
    }

    /**
     * Add the given term to the search. 'term' must never be null or empty. 'field' may be null or
     * empty if a MeSH *and* keyword search is desired. 'op' may not be null if there have been
     * previous calls to addTerm().
     */
    public EntrezSearch addTerm(final String term, final String field, final Operator op) {
        if (term == null || term.isEmpty()) throw new IllegalArgumentException("The 'term' parameter must have a value");
        this.queryTerm.append(' ');

        if (this.queryTerm.length() == 0) {
            if (op == null) throw new IllegalArgumentException("Operator must not be null if previous terms were set");
            this.queryTerm.append(op.name());
        }

        this.queryTerm.append(term);
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
}
