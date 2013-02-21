/*
 * Copyright 2008-2012 UnboundID Corp. All Rights Reserved.
 */
package samplecode.ha;

import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.util.args.StringArgument;
import samplecode.tools.AbstractTool;
import samplecode.tools.BasicToolCompletedProcessing;
import samplecode.tools.ToolCompletedProcessing;

import java.io.PrintStream;

public class ServerSetDemo extends AbstractTool {

    private static final String CLASS_SPECIFIC_PROPERTIES_RESOURCE_NAME = "ServerSetDemo.properties";
    private StringArgument serverUrlStringArgument;

    private ServerSetDemo(final PrintStream out, final PrintStream err) {
        super(out,err);
    }

    public static void main(String... args) {
        main(System.out,System.err,args);
    }

    private static void main(final PrintStream out, final PrintStream err, String... args) {
        final ServerSetDemo serverSetDemo = new ServerSetDemo(out,err);
        final ResultCode resultCode = serverSetDemo.runTool(args);
        final ToolCompletedProcessing completedProcessing =
                new BasicToolCompletedProcessing(serverSetDemo,resultCode);
        completedProcessing.displayMessage(out,err);
    }

    /**
     * Adds the arguments needed by this command-line tool to the provided
     * argument parser which are not related to connecting or authenticating to
     * the directory server.
     *
     * @param parser The argument parser to which the arguments should be added.
     * @throws com.unboundid.util.args.ArgumentException
     *          If a problem occurs while adding the arguments.
     */
    @Override
    public void addArguments(final ArgumentParser parser) throws ArgumentException {

        String description;
        String longName;
        String placeHolder;
        Character shortName;
        boolean isRequired;
        int maxOccurrences;


        // Add the argument whose value is the URL of a server to use in the server set
        shortName = null;
        longName = "server-url";
        isRequired = true;
        maxOccurrences = 0;
        placeHolder = "{URL}";
        description = "The URL of a server to use in the server set.";
        serverUrlStringArgument =
                new StringArgument(shortName,longName,isRequired,maxOccurrences,placeHolder,
                        description);
        parser.addArgument(serverUrlStringArgument);
    }

    /**
     * return the class-specific properties resource name
     */
    @Override
    protected String classSpecificPropertiesResourceName() {
        return CLASS_SPECIFIC_PROPERTIES_RESOURCE_NAME;
    }

    /**
     * executes the tasks defined in this tool
     */
    @Override
    protected ResultCode executeToolTasks() {
        return ResultCode.SUCCESS;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
