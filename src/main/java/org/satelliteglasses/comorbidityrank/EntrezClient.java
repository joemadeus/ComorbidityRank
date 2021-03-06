package org.satelliteglasses.comorbidityrank;

import java.io.IOException;

/**
 * A base class for utilities that fetch data from Entrez/PubMed. Generic type
 * T is the class of records returned by the subclass' operation.
 */
public abstract class EntrezClient {

    static final String ENTREZ_PROTO = "http";
    static final String ENTREZ_SERVER = "eutils.ncbi.nlm.nih.gov";
    static final String ENTREZ_BASE_PATH = "/entrez/eutils";

    public class EntrezState<T> {
        public final String webEnv;
        public final int queryKey;
        public final T mostRecentResponse;

        EntrezState(final String wE, final int qK, final T r) {
            this.webEnv = wE;
            this.queryKey = qK;
            this.mostRecentResponse = r;
        }
    }

    private final EntrezDatabase database;
    private boolean useHistory = true;
    private EntrezState state = null;
    private int queryKey = -1;
    private String returnType = "xml";

    EntrezClient(final EntrezDatabase db) {
        this.database = db;
    }

    public abstract EntrezState go() throws IOException;

    StringBuilder getBaseQuery() {
        final StringBuilder builder = new StringBuilder();

        // This must be first -- no question mark appended because java.net.URI does that
        builder.append("db=").append(this.database.getQueryValue());
        builder.append("&retmode=").append(this.returnType);
        builder.append("&useHistory=").append(this.useHistory ? "y" : "n");

        if (this.state != null) builder.append("&WebEnv=").append(this.state.webEnv);
        if (this.queryKey != -1) builder.append("&query_key=").append(this.state.queryKey);

        return builder;
    }

    /**
     * Set the WebEnv state from a previous operation and set 'useHistory' to true if the
     * given parameter is not null.
     */
    public EntrezClient setWebEnv(final EntrezState s) {
        this.state = s;
        return this;
    }

    EntrezState getState() {
        return this.state;
    }

    boolean isWebEnvSet() {
        return this.state != null;
    }

    public EntrezClient setReturnType(final String type) {
        this.returnType = type;
        return this;
    }

    /**
     * Use the query history on the PubMed side.
     */
    public EntrezClient setUseHistory(final boolean uH) {
        this.useHistory = uH;
        return this;
    }

    public EntrezClient setQueryKey(final int qK) {
        this.queryKey = qK;
        return this;
    }
}
