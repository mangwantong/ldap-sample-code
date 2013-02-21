/**
 * Contains sample classes, many of which illustrate the use of the
 * UnboundID LDAP SDK and their supporting classes.
 * <p>
 * The classes include demonstrations and example code for:
 * <ul>
 *
 * <li>{@link samplecode.add.AddExample}: demonstrates the use of the ADD request</li>
 *
 * <li>{@link samplecode.controls.AssertionRequestControlDemo}: demonstrates the use of the
 * Assertion request control</li>
 *
 * <li>{@link samplecode.auth.AuthDemo}: authentication and the use if
 * the {@code Who Am I?} extended request and the
 * {@code authorization identity request control}.</li>
 *
 * <li>{@link samplecode.bind.BindDemo}: demonstrates the use of the BIND request</li>
 *
 * <li>{@link samplecode.compare.CompareDemo}: demonstrates the use of the COMPARE request</li>
 *
 * <li>{@link samplecode.memory.LdapListenerExample}: the in-memory
 * directory server.</li>
 *
 * <li>{@link samplecode.delete.LdapTreeDelete}: a demonstration of the
 * LDAP tree delete control extension</li>
 *
 * <li>{@link samplecode.controls.MatchedValuesRequestControlExample}: demonstrates the use
 * of the Matched Values request control</li>
 *
 * <li>{@link samplecode.matchingrule.MatchingRuleDemo}: demonstrates the use of matching
 * rules</li>
 *
 * <li>{@link samplecode.modifydn.ModifyDnDemo}: demonstrates the use of the MODIFY DN
 * request</li>
 *
 * <li>{@link samplecode.modify.ModifyIncrementDemo}: demonstrates the use of the MODIFY
 * request using the Modify-Increment extension</li>
 *
 * <li>{@link samplecode.password.PasswordModifyExtendedOperationDemo}: demonstrates the use
 * of the Password Modify Extended Operation</li>
 *
 * <li>a command line arguments processor that manages commonly used
 * command line arguments</li>
 *
 * <li>{@link samplecode.search.PersistentSearchExample}: a
 * demonstration of change notification using persistent search</li>
 *
 * <li>@link samplecode.SimplePagedResultRequestControlDemo}: a
 * demonstration of the simple paged results control</li>
 *
 * </ul>
 * <p>
 * A shell-script tool generator is provided. <blockquote>
 *
 * <pre>
 * ScriptTool.bash --className samplecode.BindDemo \
 *   --writableDirectory ~/bin --classPath <classpath>
 * </pre>
 * </blockquote>
 *
 * @see <a target="blank" href="http://ff1959.wordpress.com">LDAP blog</a>
 */
package samplecode;


