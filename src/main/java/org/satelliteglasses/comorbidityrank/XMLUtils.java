package org.satelliteglasses.comorbidityrank;

import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.net.URI;

public final class XMLUtils {

    private static final XPath X_PATH = XPathFactory.newInstance().newXPath();
    private static final JAXBContext JAXB_CONTEXT;

    static {
        try {
            JAXB_CONTEXT = JAXBContext.newInstance(
                    PubMedFetchResult.class,
                    PubMedSearchResult.class);
        } catch (final Throwable thr) {
            // Shut it down. shut it down now.
            throw new Error("Could not instantiate the XML utilities: " + thr.getMessage(), thr);
        }
    }

    public static Document getDocument(final URI uri) throws IOException {
        try {
            return DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .parse(uri.toString());

        } catch (final Exception e) {
            throw new IOException(
                    "Could not create a document from URI " + uri.toString() + ": " + e.getMessage(), e);
        }
    }

    public static XPathExpression getXPathExpression(final String expression) {
        try {
            return X_PATH.compile(expression);
        } catch (final XPathException xpe) {
            throw new IllegalArgumentException(
                    "Could not create an XPath expression for '" + expression + "': " + xpe.getMessage(), xpe);
        }
    }

    public static Object unmarshal(final URI uri) throws IOException {
        try {
            final Unmarshaller unmarshaller = JAXB_CONTEXT.createUnmarshaller();
            return unmarshaller.unmarshal(uri.toURL());

        } catch (final JAXBException jaxbe) {
            throw new IOException(
                    "Could not create the JAXB unmarshaller or unmarshal data from '" + uri.toString() + "': " + jaxbe.getMessage(), jaxbe);
        }
    }
}
