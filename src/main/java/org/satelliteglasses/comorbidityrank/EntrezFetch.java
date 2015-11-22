package org.satelliteglasses.comorbidityrank;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Fetches PubMed records from Entrez using the remote history server. You must
 * supply an EntrezState object before running the fetch -- that's how the client
 * knows what records to bring back. This class roughly follows a Builder pattern,
 * meaning that all the "addX()" methods on this class return 'this', with the
 * values applied. Note that no error checking is performed on these setters.
 * Calling 'go()' performs the fetch and returns an EntrezState that contains the
 * fetched documents.
 */
public class EntrezFetch extends EntrezClient {

    private static final Logger LOGGER = Logger.getLogger(EntrezFetch.class);

    private static final String RESOURCE = "/efetch.fcgi";

    public EntrezFetch(final EntrezDatabase db) {
        super(db);
    }

    @Override
    public EntrezState<PubMedFetchResults> go() throws IOException {
        if ( ! this.isWebEnvSet()) {
            throw new IllegalStateException("A WebEnv state must be set before calling go()");
        }

        final StringBuilder queryBuilder = this.getBaseQuery();
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
