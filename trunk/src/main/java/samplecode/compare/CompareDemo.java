package samplecode.compare;


import com.unboundid.ldap.sdk.CompareRequest;
import com.unboundid.ldap.sdk.CompareResult;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.util.args.StringArgument;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Launchable;
import samplecode.annotation.Since;
import samplecode.tools.AbstractTool;

import java.util.LinkedHashMap;


/**
 * Provides a demonstration of the COMPARE request using the
 * UnboundID LDAP SDK.
 *
 * @author Terry J. Gardner
 */
@Since("01-Jan-2012")
@CodeVersion("1.2")
@Launchable
public final class CompareDemo extends AbstractTool {

  /**
   * Runs the CompareDemo program.
   *
   * @param args
   *   command-line arguments excluding JVM-specific arguments.
   */
  public static void main(final String... args) {
    ResultCode resultCode = new CompareDemo().runTool(args);
    if(resultCode != null && !resultCode.equals(ResultCode.SUCCESS)) {
      System.exit(resultCode.intValue());
    }
  }



  @Override()
  public LinkedHashMap<String[],String> getExampleUsages() {
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
  public void addArguments(final ArgumentParser argumentParser)
    throws ArgumentException {
    final String description = "The assertion to use in the compare request.";
    argumentParser.addArgument(new StringArgument('n',"assertion",true,1,
      "{assertion}",description));
  }


  @Override
  protected ResultCode executeToolTasks() {

    // Use the value of the --baseObject command line
    // argument as the DN.
    String dn;
    try {
      dn = commandLineOptions.getBaseObject();
      if(dn == null) {
        getLogger().fatal("The --baseObject argument is required.");
        return ResultCode.PARAM_ERROR;
      }
    } catch(final LDAPException ldapException) {
      ldapException.printStackTrace();
      return ldapException.getResultCode();
    }

    // The --attribute command line argument
    final ArgumentParser argumentParser = commandLineOptions.getArgumentParser();
    final StringArgument attributeArgument =
      (StringArgument) argumentParser.getNamedArgument("attribute");
    final String attributeName = attributeArgument.getValue();
    if((attributeName == null) || (attributeName.length() == 0)) {
      getLogger().fatal("The --attribute argument is required.");
      return ResultCode.PARAM_ERROR;
    }

    // Get the value to use for the assertion in the compare request
    // from the --assertion command line argument
    final StringArgument assertionArgument =
      (StringArgument) argumentParser.getNamedArgument("assertion");
    final String assertionValue = assertionArgument.getValue();
    if((assertionValue == null) || (assertionValue.length() == 0)) {
      getLogger().fatal("The --assertion argument is required.");
      return ResultCode.PARAM_ERROR;
    }

    // Create and transmit the CompareRequest to the server and display
    // the results of the comparison with the assertion value.
    final CompareRequest req = new CompareRequest(dn,attributeName,assertionValue);
    try {
      final CompareResult compareResult = getConnection().compare(req);
      System.out.println(compareResult.compareMatched() ? "matched" : "did " +
        "not match");
    } catch(final LDAPException exception) {
      getLogger().fatal(exception.getExceptionMessage());
      return exception.getResultCode();
    }

    return ResultCode.SUCCESS;
  }



  /**
   * {@inheritDoc}
   */
  @Override
  protected String classSpecificPropertiesResourceName() {
    return "CompareDemo.properties";
  }

}
