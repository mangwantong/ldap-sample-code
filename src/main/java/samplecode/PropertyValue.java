package samplecode;


import java.util.Properties;
import java.util.logging.Logger;


import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;


/** Support for property-backed data values */
@Since("13-Jun-2012")
@CodeVersion("1.0")
@Author("Terry Gardner")
public abstract class PropertyValue<T>
{

  /**
   * @return the value associated with the {@code key} or the
   *         {@code defaultValue}.
   */
  public abstract T getValue();



  @Override
  public String toString()
  {
    return String.format("logger: %s value: %s props: %s\n",getLogger(),getValue(),properties);
  }



  protected Logger getLogger()
  {
    return logger;
  }



  /**
   * 
   * Creates a {@code PropertyValue} with default state.
   * 
   * @param properties
   *          the properties that back the data value; not permitted to
   *          be {@code null}
   * 
   * @param key
   *          the key associated with the value; not permitted to be
   *          {@code null}
   * 
   * @param defaultValue
   *          returned from {@link PropertyValue#getValue()} when the
   *          {@code key} is not associated with a value, that is, the
   *          {@code key} does not exist.
   */
  protected PropertyValue(
          final Properties properties,final String key,final T defaultValue)
  {
    if(properties == null)
    {
      throw new NullPointerException("properties was null");
    }
    else if(key == null)
    {
      throw new NullPointerException("key was null");
    }
    else if(defaultValue == null)
    {
      throw new NullPointerException("defaultValue was null");
    }
    this.properties = properties;
    this.key = key;
    this.defaultValue = defaultValue;
    logger = Logger.getLogger(getClass().getName());
  }



  /**
   * The value to return from {@link PropertyValue#getValue()} when the
   * {@code key} cannot be found.
   */
  protected final T defaultValue;



  /** The key associated with the data */
  protected final String key;



  protected final Logger logger;



  /** backs the data */
  protected final Properties properties;
}
