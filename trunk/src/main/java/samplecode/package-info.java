/**
 * Contains sample classes, many of which illustrate the use of the
 * UnboundID LDAP SDK and their supporting classes.
 * <p>
 * The classes include demonstrations and example code for:
 * <ul>
 * <li>{@link samplecode.auth.AuthDemo}: authentication and the use if
 * the {@code Who Am I?} extended request and the
 * {@code authorization identity request control}.</li>
 * <li>{@link samplecode.listener.LdapListenerExample}: the in-memory
 * directory server.</li>
 * <li>a command line arguments processor that manages commonly used
 * command line arguments</li>
 * <li>{@link samplecode.search.PersistentSearchExample}: a
 * demonstration of change notification using persistent search</li>
 * <li>{@link samplecode.delete.LdapTreeDelete}: a demonstration of the
 * LDAP tree delete control extension</li>
 * <li>@link samplecode.SimplePagedResultRequestControlDemo}: a
 * demonstration of the simple paged results control</li>
 * </ul>
 * <p>
 * A shell-script tool generator is provided. <blockquote>
 * 
 * <pre>
 * ScriptTool.bash --className samplecode.BindDemo \
 *   --writableDirectory ~/bin --classPath <classpath>
 * </pre>
 * </blockquote>
 */
package samplecode;


