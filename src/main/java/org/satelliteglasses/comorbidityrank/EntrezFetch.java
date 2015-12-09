package org.satelliteglasses.comorbidityrank;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches PubMed records from Entrez using the remote history server. You must
 * supply an EntrezState object or IDs before running the fetch -- that's how the
 * client knows what records to bring back. This class roughly follows a Builder
 * pattern, meaning that all the "addX()" methods on this class return 'this',
 * with the values applied. Note that no error checking is performed on these
 * setters. Calling 'go()' performs the fetch and returns an EntrezState that
 * contains the fetched documents.
 */
public class EntrezFetch extends EntrezClient {

    private static final Logger LOGGER = Logger.getLogger(EntrezFetch.class);

    private static final String RESOURCE = "/efetch.fcgi";

    private final List<Integer> idList = new ArrayList<Integer>(0);

    private int maxReturnedCount = 0;

    private int startIndex = 0;

    public EntrezFetch(final EntrezDatabase db) {
        super(db);
    }

    /**
     * Appends the given ID to the list to be fetched
     */
    public EntrezFetch addSingleID(final Integer id) {
        this.idList.add(id);
        return this;
    }

    /**
     * Appends the given List of IDs to the list to be fetched
     */
    public EntrezFetch addIDs(final List<Integer> ids) {
        this.idList.addAll(ids);
        return this;
    }

    /**
     * Replaces the current list of fetched IDs with the provided, single ID
     */
    public EntrezFetch setSingleID(final Integer id) {
        this.idList.clear(); // Avoiding NPEs by using a final List
        this.idList.add(id);
        return this;
    }

    /**
     * Replaces the current list of fetched IDs with the provided List
     */
    public EntrezFetch setIDs(final List<Integer> ids) {
        this.idList.clear(); // Avoiding NPEs by using a final List
        this.idList.addAll(ids);
        return this;
    }

    public EntrezFetch setMaxReturnedCount(final int count) {
        this.maxReturnedCount = count;
        return this;
    }

    public EntrezFetch setStartIndex(final int index) {
        this.startIndex = index;
        return this;
    }

    @Override
    public EntrezState<PubMedFetchResults> go() throws IOException {
        if (this.idList.isEmpty() && ! this.isWebEnvSet()) {
            throw new IllegalStateException("IDs or a WebEnv state must be set before calling go()");
        }

        if (this.maxReturnedCount <= 0) {
            throw new IllegalStateException("A max returned count greater than zero must be set");
        }

        if (this.startIndex < 0) {
            throw new IllegalStateException("The start index must be equal to or greater than zero");
        }

        final StringBuilder queryBuilder = this.getBaseQuery();
        if (this.startIndex >= 0) queryBuilder.append("&").append("retstart=").append(this.startIndex);
        if (this.maxReturnedCount >= 0) queryBuilder.append("&").append("retmax=").append(this.maxReturnedCount);
        if ( ! this.idList.isEmpty()) queryBuilder.append("&").append("id=").append(StringUtils.join(this.idList, ","));

        final URI fetchURI;
        try {
            fetchURI = new URI(
                    ENTREZ_PROTO,
                    null,
                    ENTREZ_SERVER,
                    80,
                    ENTREZ_BASE_PATH + RESOURCE,
                    queryBuilder.toString(),
                    null);

        } catch (final URISyntaxException urise) {
            throw new IOException("Could not build the URI for the document fetch: " + urise.getMessage(), urise);
        }

        LOGGER.debug("Fetching documents with URI '" + fetchURI + "'");

        final PubMedFetchResults result = (PubMedFetchResults) XMLUtils.unmarshal(fetchURI, XMLUtils.PUBMED_FETCH_TRANSFORMER);
        return new EntrezState<PubMedFetchResults>(this.getState().webEnv, this.getState().queryKey, result);
    }
}
