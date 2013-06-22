package samplecode.groups;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPInterface;
import com.unboundid.ldap.sdk.schema.AttributeTypeDefinition;
import com.unboundid.ldap.sdk.schema.Schema;
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;

import static com.unboundid.util.Validator.ensureNotNullWithMessage;


@Author("terry.gardner@unboundid.com")
@CodeVersion("1.0")
@SuppressWarnings("unused")
public abstract class GroupUtils {

  /**
   * The name of the attribute which represents group memberships or
   * {@code null} if none of the possible names are defined in the schema.
   */
  public static String getMemberAttribute(LDAPInterface conn, String[] names) {
    ensureNotNullWithMessage(conn,"Connection to the server was null.");

    final Schema schema;
    try {
      schema = conn.getSchema();
    } catch(LDAPException e) {
      return null;
    }

    AttributeTypeDefinition attributeTypeDefinition;
    for(final String possibleAttributeName : names) {
      attributeTypeDefinition = schema.getAttributeType(possibleAttributeName);
      if(attributeTypeDefinition != null) {
        return possibleAttributeName;
      }
    }
    return null;
  }



  /**
   * The name of the attribute which represents group memberships or
   * {@code null} if none of the possible names are defined in the schema.
   */
  public static String getMemberAttribute(LDAPInterface conn) {
    return getMemberAttribute(conn,POSSIBLE_ATTRIBUTE_NAMES);
  }



  private static final String[] POSSIBLE_ATTRIBUTE_NAMES = {
    "memberOf","isMemberOf"
  };

}
