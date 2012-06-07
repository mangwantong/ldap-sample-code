package samplecode.compare;


import com.unboundid.ldap.sdk.CompareRequest;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.util.args.StringArgument;


import samplecode.tools.AbstractTool;


public final class CompareDemo
        extends AbstractTool
{



  public static void main(final String... args)
  {
    new CompareDemo().runTool(args);
  }



  @Override
  protected String classSpecificPropertiesResourceName()
  {
    return "CompareDemo.properties";
  }



  @Override
  protected ResultCode executeToolTasks()
  {
    String dn;
    try
    {
      dn = commandLineOptions.getBaseObject();
    }
    catch(final LDAPException ldapException)
    {
      ldapException.printStackTrace();
      return ldapException.getResultCode();
    }
    final ArgumentParser argumentParser = commandLineOptions.getArgumentParser();
    final StringArgument attributeArgument =
            (StringArgument)argumentParser.getNamedArgument("attribute");
    final String attributeName = attributeArgument.getValue();
    final StringArgument assertionArgument =
            (StringArgument)argumentParser.getNamedArgument("assertion");
    final String assertionValue = assertionArgument.getValue();
    final CompareRequest req = new CompareRequest(dn,attributeName,assertionValue);
    try
    {
      getConnection().compare(req);
    }
    catch(final LDAPException exception)
    {
      // TODO Auto-generated catch block
      exception.printStackTrace();
    }


    // TODO: This block deliberately left empty
    return null;

  }

}
