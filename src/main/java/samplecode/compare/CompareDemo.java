package samplecode.compare;


import com.unboundid.ldap.sdk.*;
import com.unboundid.util.args.*;
import java.util.LinkedHashMap;
import samplecode.annotation.*;
import samplecode.tools.AbstractTool;


/**
 * Provides a demonstration of the COMPARE request using the
 * UnboundID LDAP SDK.
 *
 * @author Terry J. Gardner
 */
@Since("01-Jan-2012")
@CodeVersion("1.2")
@Launchable
public final class CompareDemo extends AbstractTool
{

   /**
    * Runs the CompareDemo program.
    *
    * @param args
    *    command-line arguments excluding JVM-specific arguments.
    */
   public static void main(final String... args)
   {
      ResultCode resultCode = new CompareDemo().runTool(args);
      if(resultCode != null && !resultCode.equals(ResultCode.SUCCESS))
      {
         System.exit(resultCode.intValue());
      }
   }


   @Override()
   public LinkedHashMap<String[],String> getExampleUsages()
   {
      final LinkedHashMap<String[],String> examples =
         new LinkedHashMap<String[],String>(1);
      final String[] args = {
         "--hostname","server.example.com",
         "--port","389",
         "--bindDN","uid=admin,dc=example,dc=com",
         "--bindPassword","password",
         "--assertion","cn",
         "--attribute","attribute-name"
      };
      final String description =
         "Demonstrates the use of the COMPARE request.";
      examples.put(args,description);

      return examples;

   }


   @Override
   protected ResultCode executeToolTasks()
   {
      // Create and transmit the CompareRequest to the server and display
      // the results of the comparison with the assertion value.
      CompareRequest req;
      try
      {
         req = getCompareRequest();
      }
      catch(Exception e)
      {
         return ResultCode.PARAM_ERROR;
      }

      ResultCode resultCode;
      try
      {
         CompareResult compareResult = getConnection().compare(req);
         err(compareResult.compareMatched() ? "matched" : "did not match");
         resultCode = ResultCode.SUCCESS;
      }
      catch(LDAPException exception)
      {
         getLogger().fatal(exception.getExceptionMessage());
         resultCode = exception.getResultCode();
      }

      return resultCode;
   }


   /**
    * {@inheritDoc}
    */
   @Override
   protected String classSpecificPropertiesResourceName()
   {
      return "CompareDemo.properties";
   }


   @Override
   public void addArguments(final ArgumentParser argumentParser)
      throws ArgumentException
   {
      final String description = "The assertion to use in the compare request.";
      argumentParser.addArgument(new StringArgument('n',"assertion",true,1,
                                                    "{assertion}",description));
   }


   private CompareRequest getCompareRequest() throws Exception
   {
      // Get the value to use for the attributeName in the compare request
      // from --attribute command line argument.
      final ArgumentParser argumentParser = commandLineOptions.getArgumentParser();
      final StringArgument attributeArgument =
         (StringArgument)argumentParser.getNamedArgument("attribute");
      final String attributeName = attributeArgument.getValue();
      if((attributeName == null) || (attributeName.length() == 0))
      {
         getLogger().fatal("The --attribute argument is required.");
         throw new Exception("The --attribute argument is required.");
      }

      // Get the value to use for the assertion in the compare request
      // from the --assertion command line argument
      final StringArgument assertionArgument =
         (StringArgument)argumentParser.getNamedArgument("assertion");
      final String assertionValue = assertionArgument.getValue();
      if((assertionValue == null) || (assertionValue.length() == 0))
      {
         getLogger().fatal("The --assertion argument is required.");
         throw new Exception("The --assertion argument is required.");
      }
      return new CompareRequest(commandLineOptions.getBaseObject(),attributeName,assertionValue);
   }

}
