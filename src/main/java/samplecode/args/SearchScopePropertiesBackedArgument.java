package samplecode.args;


import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.util.Validator;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.ScopeArgument;


import java.util.ResourceBundle;


/**
 * Provide support for a {@link PropertiesBackedArgument} whose value is
 * an {@link SearchScope}.
 */
public class SearchScopePropertiesBackedArgument
        extends PropertiesBackedArgument<ScopeArgument,SearchScope>
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
  public static SearchScopePropertiesBackedArgument newSearchScopePropertiesBackedArgument(
          final ResourceBundle resourceBundle,final String basePropertyName)
  {
    return new SearchScopePropertiesBackedArgument(resourceBundle,basePropertyName);
  }



  @Override
  public ScopeArgument getArgument() throws ArgumentException
  {
    return new ScopeArgument(getShortIdentifier(),getLongIdentifier(),isRequired(),
            getValuePlaceholder(),getDescription(),getDefaultValue());
  }



  /** @return the {@code value} as an {@link Scope}. */
  @Override
  protected SearchScope convertString(final String value)
  {
    Validator.ensureNotNull(value);
    if(value.equalsIgnoreCase("sub"))
    {
      return SearchScope.SUB;
    }
    else if(value.equalsIgnoreCase("one"))
    {
      return SearchScope.ONE;
    }
    return SearchScope.BASE;
  }



  private SearchScopePropertiesBackedArgument(
          final ResourceBundle resourceBundle,final String basePropertyName)
  {
    super(resourceBundle,basePropertyName);
  }



}
