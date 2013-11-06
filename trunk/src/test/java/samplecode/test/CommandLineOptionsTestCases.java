package samplecode.test;

import com.unboundid.ldap.sdk.*;
import com.unboundid.util.LDAPCommandLineTool;
import com.unboundid.util.args.*;
import java.util.*;
import org.apache.log4j.Logger;
import org.junit.*;
import samplecode.cli.CommandLineOptions;
import samplecode.util.*;

import static org.junit.Assert.assertTrue;

public final class CommandLineOptionsTestCases
{

  private ArgumentParser argumentParser;

  private CommandLineOptions commandLineOptions;

  private Logger logger;






  @Test
  public void getAbandonOnTimeoutTestCase() throws ArgumentException, LDAPException
  {
    String[] args = {"--" + CommandLineOptions.ARG_NAME_ABANDON_ON_TIMEOUT};
    argumentParser.parse(args);
    boolean value = commandLineOptions.getAbandonOnTimeout();
    assertTrue(value);
  }






  @Test
  public void getAuthReconnectTestCase() throws ArgumentException, LDAPException
  {
    String[] args = {"--" + CommandLineOptions.ARG_NAME_AUTO_RECONNECT};
    argumentParser.parse(args);
    boolean value = commandLineOptions.getAutoReconnect();
    assertTrue(value);
  }






  @Test
  public void getBindDNTestCase() throws ArgumentException, LDAPException
  {
    TestDataPairing<DN> tdp = new TestDataPairing<DN>("cn=rootdn",new DN("cn=rootdn"));
    String[] args = {"--bindDN",tdp.getString(),"--bindPassword","password"};
    argumentParser.parse(args);
    DN value = commandLineOptions.getBindDn();
    assertTrue(value.equals(tdp.getValue()));
  }






  @Test
  public void getHostnameTestCase() throws ArgumentException, LDAPException
  {
    TestDataPairing<String> tdp = new TestDataPairing<String>("hostname","hostname");
    String[] args = {"--hostname",tdp.getString()};
    argumentParser.parse(args);
    String value = commandLineOptions.getHostname();
    assertTrue(value.equals(tdp.getValue()));
  }






  @Test
  public void getNumThreadsTestCase() throws ArgumentException
  {
    TestDataPairing<Integer> integerTestDataPairing = new TestDataPairing<Integer>("16",16);
    String[] args = {"--numThreads",integerTestDataPairing.getString()};
    argumentParser.parse(args);
    int value = commandLineOptions.getNumThreads();
    assertTrue(value == integerTestDataPairing.getValue());
  }






  @Before
  public void invokeBeforeEachTestCase()
  {
    try
    {
      LDAPCommandLineTool tool = new TestLDAPCommandLineTool();
      argumentParser = tool.createArgumentParser();

      ResourceBundle resourceBundle = StaticData.getResourceBundle();
      List<Argument> argList = SampleCodeCollectionUtils.newArrayList();
      for(Argument argument : CommandLineOptions.createDefaultArguments(resourceBundle))
      {
        argList.add(argument);
      }
      commandLineOptions = CommandLineOptions.newCommandLineOptions(argumentParser,
        argList.toArray(new Argument[argList.size()]));
    }
    catch(ArgumentException e)
    {
      getLogger().error(e);
    }
  }






  private Logger getLogger()
  {
    if(logger == null)
    {
      logger = Logger.getLogger(getClass());
    }
    return logger;
  }

}
