package samplecode.logging;


import com.unboundid.util.StaticUtils;
import com.unboundid.util.Validator;


import java.util.List;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;


import samplecode.util.StaticData;
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;


/**
 * A log line formatter for samplecode.
 */
@Author("terrygardner@unboundid.com")
@Since("May, 2012")
@CodeVersion("3.0")
public class SampleCodeFormatter
        extends Formatter
{

  /**
   * The name of the property whose value is the maximum length of an
   * output line
   */
  public static final String PROP_NAME_LINE_LENGTH = "lineLength";



  /** formats the string contained in the log record for output */
  @Override
  public String format(final LogRecord record)
  {
    Validator.ensureNotNull(record);
    return getFormattedString(record);
  }



  private String getFormattedString(final LogRecord record)
  {
    final StringBuilder builder = new StringBuilder();
    // builder.append(getTimestamp(record.getMillis()));
    // builder.append(record.getLevel());
    final List<String> lines = StaticUtils.wrapLine(record.getMessage(),width);
    for(final String l : lines)
    {
      builder.append(l);
    }
    return builder.toString();
  }



  private SampleCodeFormatter()
  {
    try
    {
      width =
              Integer.parseInt(StaticData.getResourceBundle().getString(
                      SampleCodeFormatter.PROP_NAME_LINE_LENGTH));
    }
    catch(final Exception ex)
    {
      width = 72;
    }
  }



  private int width;
}
