/**
 * RDF4JException.java — compatibility shim
 *
 * In rdf4j 4.x the class org.eclipse.rdf4j.RDF4JException was deprecated and
 * then fully removed in rdf4j 5.x.  rdfbeans 2.2 was compiled against rdf4j 2.x
 * and still declares several RDFBeanManager methods as {@code throws RDF4JException}.
 * The Java compiler requires the class to be present on the compile-time classpath
 * even when it is only referenced in a {@code throws} clause.
 *
 * This shim provides the minimal class definition that satisfies the compiler.
 * It is never instantiated or thrown at runtime: all rdfbeans 2.2 operations that
 * formerly propagated this exception now propagate
 * {@link org.eclipse.rdf4j.common.exception.RDF4JException} from the
 * rdf4j-common-exception artifact, or a subclass thereof.
 *
 * Remove this file when the project upgrades to a version of rdfbeans that is
 * compiled against rdf4j 4.x or later.
 */
package org.eclipse.rdf4j;

/** @deprecated rdf4j 5.x compat shim — use org.eclipse.rdf4j.common.exception.RDF4JException. */
@Deprecated
public class RDF4JException extends Exception {
    private static final long serialVersionUID = 1L;

    public RDF4JException() { super(); }
    public RDF4JException(String msg) { super(msg); }
    public RDF4JException(Throwable cause) { super(cause); }
    public RDF4JException(String msg, Throwable cause) { super(msg, cause); }
}
