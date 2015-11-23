package org.satelliteglasses.comorbidityrank;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * A utility that reads a MeSH XML vocabulary file and emits only those entries that
 * code for the T047 (Disease or Syndrome) semantic type or its children (according to
 * the UMLS definitions.) Right now, this class takes only the name of the MeSH vocab
 * file and relies on static UMLS filter definitions (specifically, it only emits
 * entries for T047 (Disease or Syndrome), T048 (Mental or Behavioral Dysfunction) and
 * T191 (Neoplastic Process.)
 */
public class MeSHPruner {

    private static final Set<String> DESIRED_TYPES = new HashSet<String>() {{
        this.add("T047"); // Disease or Syndrome
        this.add("T048"); // Mental or Behavioral Dysfunction
        this.add("T191"); // Neoplastic Process
    }};

    // Ugly details: this class does things by brute force, reading in the entire MeSH
    // vocab file, filtering to a new list, then emitting the new list. Doing this lets
    // read & write with JAXB, which is easier to code at the moment. All this said,
    // none of this should be a problem since the entire MeSH vocab file can fit into
    // memory easily -- it's only ~350MB decompressed, after all.

    public static void main(final String args[]) throws Exception {
        if (args.length != 1) {
            System.err.println("The path to the input MeSH vocabulary file must be specified.");
            System.exit(1);
        }

        final File meshFile = new File(args[0]);
        if ( ! meshFile.exists() || ! meshFile.isFile()) {
            System.err.println("The path specified must be a readable file");
            System.exit(1);
        }

        final MeSHDescriptors inputDescriptors =
                (MeSHDescriptors) XMLUtils.unmarshal(meshFile.toURI(), XMLUtils.MESH_VOCABULARY_TEMPLATE);

        final MeSHDescriptors outputDescriptors = new MeSHDescriptors();
        // Keep in mind that if I weren't using JAXB this list would be init'd in the c'tor.
        // This sh*t is fuuuugly.
        outputDescriptors.meSHDescriptors = new ArrayList<MeSHDescriptors.MeSHDescriptor>();

        for (final MeSHDescriptors.MeSHDescriptor descriptor : inputDescriptors.meSHDescriptors) {
            for (final MeSHDescriptors.SemanticTypeUI semanticTypeID : descriptor.semanticTypes) {
                if (DESIRED_TYPES.contains(semanticTypeID.semanticTypeUI)) {
                    outputDescriptors.meSHDescriptors.add(descriptor);
                    break;
                }
            }
        }

        XMLUtils.marshal(outputDescriptors);
    }
}
