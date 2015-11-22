package org.satelliteglasses.comorbidityrank;

/**
 * The databases that Entrez knows about. Use the 'queryValue' field when retrieving
 * information from Entrez.
 */
public enum EntrezDatabase {

    PUBMED("pubmed");

    private final String queryValue;

    EntrezDatabase(final String qV) {
        this.queryValue = qV;
    }

    public String getQueryValue() {
        return this.queryValue;
    }
}
