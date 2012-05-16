package samplecode.logging;


import com.unboundid.util.Validator;


import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;


import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;


/**
 * A log line formatter for samplecode.
 */
@Author("terrygardner@unboundid.com")
@Since("May, 2012")
@CodeVersion("1.0")
public class SampleCodeFormatter
        extends Formatter
{

  @Override
  public String format(final LogRecord record)
  {
    return getFormattedString(record);
  }



  private String getFormattedString(final LogRecord record)
  {
    Validator.ensureNotNull(record);
    return String.format("[%s] %s %s %s\n",getTimestamp(record.getMillis()),record.getClass()
            .getName(),record.getLevel(),record.getMessage());
  }



  private String getTimestamp(final long millis)
  {
    final Date date = new Date(millis);
    return date.toString();
  }
}
