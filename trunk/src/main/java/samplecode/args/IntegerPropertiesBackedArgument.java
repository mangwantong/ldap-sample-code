package samplecode.args;


import com.unboundid.util.Validator;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.IntegerArgument;


import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


/**
 * Provide support for a {@link PropertiesBackedArgument} whose value is
 * an {@link Integer}.
 */
public class IntegerPropertiesBackedArgument
        extends PropertiesBackedArgument<IntegerArgument,Integer>
{

  /**
   * Tests the IntegerPropertiesBackedArgument class
   * 
   * @throws ArgumentException
   */
  public static void main(final String... args) throws ArgumentException
  {
    final String bundleName = "samplecode";
    final Locale locale = Locale.ENGLISH;
    final ResourceBundle rb = ResourceBundle.getBundle(bundleName,locale);
    final String name = "sizeLimit";
    final IntegerPropertiesBackedArgument arg =
            IntegerPropertiesBackedArgument.newIntegerPropertiesBackedArgument(rb,name);
    final IntegerArgument integerArgument = arg.getArgument();
    System.out.println(integerArgument);
  }



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
  public static IntegerPropertiesBackedArgument newIntegerPropertiesBackedArgument(
          final ResourceBundle resourceBundle,final String basePropertyName)
  {
    return new IntegerPropertiesBackedArgument(resourceBundle,basePropertyName);
  }



  @Override
  public IntegerArgument getArgument() throws ArgumentException
  {
    /*
     * Construct an IntegerArgument using the provided information - and
     * use the upper bound and lower bound if provided.
     */
    IntegerArgument arg;
    try
    {
      final int lowerBound = getLowerBound().intValue();
      final int upperBound = getUpperBound().intValue();
      arg =
              new IntegerArgument(getShortIdentifier(),getLongIdentifier(),isRequired(),
                      getMaxOccurrences(),getValuePlaceholder(),getDescription(),upperBound,
                      lowerBound,getDefaultValue());
    }
    catch(final MissingResourceException ex)
    {
      arg =
              new IntegerArgument(getShortIdentifier(),getLongIdentifier(),isRequired(),
                      getMaxOccurrences(),getValuePlaceholder(),getDescription(),
                      getDefaultValue());
    }

    return arg;
  }



  /** @return the {@code value} as an {@link Integer}. */
  @Override
  protected Integer convertString(final String value)
  {
    Validator.ensureNotNull(value);
    return Integer.valueOf(value);
  }



  private Integer getLowerBound() throws MissingResourceException
  {
    return Integer.valueOf(getResourceBundle().getString(getBasePropertyName() + "LowerBound"));
  }



  private Integer getUpperBound() throws MissingResourceException
  {
    return Integer.valueOf(getResourceBundle().getString(getBasePropertyName() + "UpperBound"));
  }



  private IntegerPropertiesBackedArgument(
          final ResourceBundle resourceBundle,final String basePropertyName)
  {
    super(resourceBundle,basePropertyName);
  }



}
