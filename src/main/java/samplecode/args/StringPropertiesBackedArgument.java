package samplecode.args;


import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.StringArgument;

import java.util.ResourceBundle;


/**
 * A new {@link PropertiesBackedArgument} whose value is a
 * {@link String}
 */
public class StringPropertiesBackedArgument
  extends PropertiesBackedArgument<StringArgument,String> {


  /**
   * @param resourceBundle
   *   the resource bundle from which the command line argument
   *   are taken
   * @param basePropertyName
   *   the base property name
   *
   * @return a new {@code StringPropertiesBackedArgument} object.
   */
  public static StringPropertiesBackedArgument newStringPropertiesBackedArgument(
    final ResourceBundle resourceBundle, final String basePropertyName) {
    return new StringPropertiesBackedArgument(resourceBundle,basePropertyName);
  }



  @Override
  public StringArgument getArgument() throws ArgumentException {
    return new StringArgument(getShortIdentifier(),getLongIdentifier(),isRequired(),
      getMaxOccurrences(),getValuePlaceholder(),getDescription(),getDefaultValue());
  }



  @Override
  protected String convertString(final String value) {
    return value;
  }



  private StringPropertiesBackedArgument(
    final ResourceBundle resourceBundle, final String basePropertyName) {
    super(resourceBundle,basePropertyName);
  }


}
