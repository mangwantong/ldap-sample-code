package samplecode.args;


import com.unboundid.util.Validator;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.BooleanArgument;


import java.util.ResourceBundle;


/**
 * Provide support for a {@link PropertiesBackedArgument} whose value is
 * an {@link Boolean}.
 */
public class BooleanPropertiesBackedArgument
        extends PropertiesBackedArgument<BooleanArgument,Boolean>
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
  public static BooleanPropertiesBackedArgument newBooleanPropertiesBackedArgument(
          final ResourceBundle resourceBundle,final String basePropertyName)
  {
    return new BooleanPropertiesBackedArgument(resourceBundle,basePropertyName);
  }



  @Override
  public BooleanArgument getArgument() throws ArgumentException
  {
    return new BooleanArgument(getShortIdentifier(),getLongIdentifier(),getMaxOccurrences(),
            getDescription());
  }



  /** @return the {@code value} as an {@link Integer}. */
  @Override
  protected Boolean convertString(final String value)
  {
    Validator.ensureNotNull(value);
    return Boolean.valueOf(value);
  }



  private BooleanPropertiesBackedArgument(
          final ResourceBundle resourceBundle,final String basePropertyName)
  {
    super(resourceBundle,basePropertyName);
  }



}
