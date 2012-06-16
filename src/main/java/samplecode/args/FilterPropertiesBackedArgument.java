package samplecode.args;


import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Validator;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.BooleanArgument;


import java.util.ResourceBundle;


/**
 * Provide support for a {@link PropertiesBackedArgument} whose value is
 * an {@link Filter}.
 */
public class FilterPropertiesBackedArgument
        extends PropertiesBackedArgument<BooleanArgument,Filter>
{



  /**
   * @param resourceBundle
   *          the resource bundle from which the command line argument
   *          are taken
   * 
   * @param basePropertyName
   *          the base property name
   * 
   * @return a new {@code StringPropertiesBackedArgument} object.
   */
  public static FilterPropertiesBackedArgument newFilterPropertiesBackedArgument(
          final ResourceBundle resourceBundle,final String basePropertyName)
  {
    return new FilterPropertiesBackedArgument(resourceBundle,basePropertyName);
  }



  @Override
  public BooleanArgument getArgument() throws ArgumentException
  {
    return new BooleanArgument(getShortIdentifier(),getLongIdentifier(),getMaxOccurrences(),
            getDescription());
  }



  /** @return the {@code value} as an {@link Filter}. */
  @Override
  protected Filter convertString(final String value)
  {
    Validator.ensureNotNull(value);
    Filter filter;
    try
    {
      filter = Filter.create(value);
    }
    catch(final LDAPException exception)
    {
      filter = null;
    }
    return filter;
  }



  private FilterPropertiesBackedArgument(
          final ResourceBundle resourceBundle,final String basePropertyName)
  {
    super(resourceBundle,basePropertyName);
  }



}
