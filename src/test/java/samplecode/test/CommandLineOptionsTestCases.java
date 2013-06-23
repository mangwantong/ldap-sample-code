package samplecode.test;

import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.LDAPCommandLineTool;
import com.unboundid.util.args.Argument;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.ArgumentParser;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import samplecode.cli.CommandLineOptions;
import samplecode.util.SampleCodeCollectionUtils;
import samplecode.util.StaticData;

import java.util.List;
import java.util.ResourceBundle;

import static org.junit.Assert.assertTrue;


public final class CommandLineOptionsTestCases {

  public CommandLineOptionsTestCases() {
    logger = Logger.getLogger(getClass());
  }



  @Before
  public void createCommandLineOptions() {
    try {
      LDAPCommandLineTool tool = new TestLDAPCommandLineTool();
      argumentParser = tool.createArgumentParser();

      final ResourceBundle resourceBundle = StaticData.getResourceBundle();
      List<Argument> argList = SampleCodeCollectionUtils.newArrayList();
      for(Argument argument : CommandLineOptions.createDefaultArguments(resourceBundle)) {
        argList.add(argument);
      }
      final int size = argList.size();
      final Argument[] arguments = argList.toArray(new Argument[size]);
      commandLineOptions = CommandLineOptions.newCommandLineOptions(argumentParser,arguments);
    } catch(ArgumentException e) {
      logger.error(e);
    }
  }



  @Test
  public void testGetAbandonOnTimeout() throws ArgumentException, LDAPException {
    final String[] args = {"--" + CommandLineOptions.ARG_NAME_ABANDON_ON_TIMEOUT};
    argumentParser.parse(args);
    final boolean value = commandLineOptions.getAbandonOnTimeout();
    assertTrue(value);
  }



  @Test
  public void testGetAuthReconnect() throws ArgumentException, LDAPException {
    final String[] args = {"--" + CommandLineOptions.ARG_NAME_AUTO_RECONNECT};
    argumentParser.parse(args);
    final boolean value = commandLineOptions.getAutoReconnect();
    assertTrue(value);
  }



  @Test
  public void testGetBindDn() throws ArgumentException, LDAPException {
    TestDataPairing<DN> tdp = new TestDataPairing<DN>("cn=rootdn",new DN("cn=rootdn"));
    final String[] args = {"--bindDN",tdp.getString(),"--bindPassword","password"};
    argumentParser.parse(args);
    final DN value = commandLineOptions.getBindDn();
    assertTrue(value.equals(tdp.getValue()));
  }



  @Test
  public void testGetBindPassword() throws ArgumentException, LDAPException {
    TestDataPairing<String> tdp = new TestDataPairing<String>("pwd","pwd");
    final String[] args = {"--bindPassword",tdp.getString()};
    argumentParser.parse(args);
    final String value = commandLineOptions.getBindPassword();
    assertTrue(value.equals(tdp.getValue()));
  }



  @Test
  public void testGetHostname() throws ArgumentException, LDAPException {
    TestDataPairing<String> tdp = new TestDataPairing<String>("hostname","hostname");
    final String[] args = {"--hostname",tdp.getString()};
    argumentParser.parse(args);
    final String value = commandLineOptions.getHostname();
    assertTrue(value.equals(tdp.getValue()));
  }



  @Test
  public void testGetNumThreads() throws ArgumentException {
    TestDataPairing<Integer> integerTestDataPairing = new TestDataPairing<Integer>("16",16);
    final String[] args = {"--numThreads",integerTestDataPairing.getString()};
    argumentParser.parse(args);
    final int value = commandLineOptions.getNumThreads();
    assertTrue(value == integerTestDataPairing.getValue());
  }



  private ArgumentParser argumentParser;


  private CommandLineOptions commandLineOptions;


  private Logger logger;

}
