package org.satelliteglasses.comorbidityrank;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBResult;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.net.URI;

public final class XMLUtils {

    static final StreamSource PUBMED_FETCH_TEMPLATE_SOURCE = new StreamSource("src/main/resources/pubmed_fetch_results.xslt");
    static final Transformer PUBMED_FETCH_TRANSFORMER;

    static final StreamSource MESH_VOCABULARY_TEMPLATE_SOURCE = new StreamSource("src/main/resources/mesh_descriptor.xslt");
    static final Transformer MESH_VOCABULARY_TEMPLATE;

    static final JAXBContext JAXB_CONTEXT;

    private static final TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();

    static {
        try {
            JAXB_CONTEXT = JAXBContext.newInstance(
                    PubMedFetchResults.class,
                    PubMedFetchResults.PubMedFetchResult.class,
                    PubMedFetchResults.PubMedMeshDescriptor.class,
                    PubMedFetchResults.Qualifier.class,
                    PubMedSearchResult.class,
                    MeSHDescriptors.class);

            PUBMED_FETCH_TRANSFORMER = TRANSFORMER_FACTORY.newTemplates(PUBMED_FETCH_TEMPLATE_SOURCE).newTransformer();
            MESH_VOCABULARY_TEMPLATE = TRANSFORMER_FACTORY.newTemplates(MESH_VOCABULARY_TEMPLATE_SOURCE).newTransformer();

        } catch (final Throwable thr) {
            throw new Error("Could not instantiate the XML utilities: " + thr.getMessage(), thr);
        }
    }

    public static void marshal(final Object obj) throws IOException {
        try {
            final Marshaller marshaller = XMLUtils.JAXB_CONTEXT.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(obj, System.out);

        } catch (final JAXBException jaxbe) {
            throw new IOException(
                    "Could not create the JAXB marshaller or marshal the data to stdout: " + jaxbe.getMessage(), jaxbe);
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

    public static Object unmarshal(final URI uri, final Transformer transformer) throws IOException {
        try {
            JAXBResult result = new JAXBResult(JAXB_CONTEXT);
            transformer.transform(new StreamSource(uri.toString()), result);
            return result.getResult();

        } catch (final TransformerException te) {
            throw new IOException(
                    "Could not transform the contents of '" + uri.toString() + "': " + te.getMessage(), te);
        } catch (final JAXBException jaxbe) {
            throw new IOException(
                    "Could not create the JAXB unmarshaller or unmarshal data from '" + uri.toString() + "': " + jaxbe.getMessage(), jaxbe);
        }
    }
}
