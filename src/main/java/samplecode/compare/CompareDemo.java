package samplecode.compare;


import com.unboundid.ldap.sdk.CompareRequest;
import com.unboundid.ldap.sdk.CompareResult;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.util.args.StringArgument;


import java.util.logging.Level;
import java.util.logging.Logger;


import samplecode.annotation.CodeVersion;
import samplecode.annotation.Launchable;
import samplecode.annotation.Since;
import samplecode.tools.AbstractTool;


/**
 * Provides a demonstration of the COMPARE request.
 */
@Since("01-JAN-2012")
@CodeVersion("1.1")
@Launchable
public final class CompareDemo
        extends AbstractTool
{

  private static final String ASSERTION_ARG_DESCRIPTION =
          "The assertion to use in the compare request";



  private static final boolean ASSERTION_ARG_IS_REQUIRED = true;



  private static final String ASSERTION_ARG_LONG_IDENTIFIER = "assertion";



  private static final int ASSERTION_ARG_MAX_OCCURRENCES = 1;



  private static final Character ASSERTION_ARG_SHORT_IDENTIFIER = 'n';



  private static final String ASSERTION_ARG_VALUE_PLACEHOLDER = "{assertion}";



  /**
   * Runs the CompareDemo program.
   * 
   * @param args
   *          command-line arguments excluding JVM-specific arguments.
   */
  public static void main(final String... args)
  {
    new CompareDemo().runTool(args);
  }



  /**
   * {@inheritDoc}
   * 
   * @throws ArgumentException
   */
  @Override
  public void addArguments(final ArgumentParser argumentParser) throws ArgumentException
  {
    super.addArguments(argumentParser);
    argumentParser.addArgument(new StringArgument(CompareDemo.ASSERTION_ARG_SHORT_IDENTIFIER,
            CompareDemo.ASSERTION_ARG_LONG_IDENTIFIER,CompareDemo.ASSERTION_ARG_IS_REQUIRED,
            CompareDemo.ASSERTION_ARG_MAX_OCCURRENCES,
            CompareDemo.ASSERTION_ARG_VALUE_PLACEHOLDER,CompareDemo.ASSERTION_ARG_DESCRIPTION));
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public Logger getLogger()
  {
    return Logger.getLogger(getClass().getName());
  }



  /**
   * {@inheritDoc}
   */
  @Override
  protected String classSpecificPropertiesResourceName()
  {
    return "CompareDemo.properties";
  }



  /**
   * {@inheritDoc}
   */
  @Override
  protected ResultCode executeToolTasks()
  {
    if(isVerbose())
    {
      displayArguments();
    }
    String dn;
    try
    {
      dn = commandLineOptions.getBaseObject();
      if(dn == null)
      {
        getLogger().log(Level.SEVERE,String.format("The --baseObject argument is required."));
        return ResultCode.PARAM_ERROR;
      }
    }
    catch(final LDAPException ldapException)
    {
      ldapException.printStackTrace();
      return ldapException.getResultCode();
    }
    final ArgumentParser argumentParser = commandLineOptions.getArgumentParser();
    final StringArgument attributeArgument =
            (StringArgument)argumentParser.getNamedArgument("attribute");


    /*
     * Get the value to use for the attribute in the compare request
     * from the --attribute command line argument
     */
    final String attributeName = attributeArgument.getValue();
    if((attributeName == null) || (attributeName.length() == 0))
    {
      getLogger().log(Level.SEVERE,String.format("The --attribute argument is required."));
      return ResultCode.PARAM_ERROR;
    }


    /*
     * Get the value to use for the assertion in the compare request
     * from the --assertion command line argument
     */
    final StringArgument assertionArgument =
            (StringArgument)argumentParser.getNamedArgument("assertion");
    final String assertionValue = assertionArgument.getValue();
    if((assertionValue == null) || (assertionValue.length() == 0))
    {
      getLogger().log(Level.SEVERE,String.format("The --assertion argument is required."));
      return ResultCode.PARAM_ERROR;
    }

    /*
     * Create and transmit the CompareRequest to the server and display
     * the results of the comparison with the assertion value.
     */
    final CompareRequest req = new CompareRequest(dn,attributeName,assertionValue);
    try
    {
      final CompareResult compareResult = getConnection().compare(req);
      getLogger().log(Level.INFO,compareResult.compareMatched() ? "matched" : "did not match");
    }
    catch(final LDAPException exception)
    {
      getLogger().log(Level.SEVERE,exception.getExceptionMessage());
      return exception.getResultCode();
    }


    // TODO: This block deliberately left empty
    return ResultCode.SUCCESS;

  }

}
