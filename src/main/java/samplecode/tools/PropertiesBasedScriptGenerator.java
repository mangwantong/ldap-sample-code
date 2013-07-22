/*
 * Copyright 2008-2011 UnboundID Corp. All Rights Reserved.
 */
/*
 * Copyright (C) 2008-2011 UnboundID Corp. This program is free
 * software; you can redistribute it and/or modify it under the terms of
 * the GNU General Public License (GPLv2 only) or the terms of the GNU
 * Lesser General Public License (LGPLv2.1 only) as published by the
 * Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License for more details. You
 * should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses>.
 */

package samplecode.tools;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;
import samplecode.logging.LogAware;
import samplecode.util.StaticData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ResourceBundle;

import static com.unboundid.util.Validator.ensureNotNull;


/**
 * Generates a bash script with which the demo examples can be invoked.
 * Uses a properties files which is expected to contain the following
 * properties:
 * <ul>
 * <li>ScriptTool.file-extension</li>
 * <li>ScriptTool.shell-script-first-line</li>
 * </ul>
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 28, 2011")
@CodeVersion("1.4")
public final class PropertiesBasedScriptGenerator
  implements ScriptGenerator, LogAware {

  private static final String LINE_SEPARATOR =
    System.getProperty("line.separator");


  /**
   * The key to the property whose value is the extension added to
   * the end of a shell script filename.
   */
  private static final String PROP_NAME_FILE_EXTENSION =
    "ScriptTool.file-extension";


  /**
   * The key to the property whose value is used for the first line of a shell
   * script.
   */
  private static final String PROP_NAME_SHELL_SCRIPT_FIRST_LINE =
    "ScriptTool.shell-script-first-line";



  /**
   * @param className
   *   the name of the class to launch. {@code className} is not
   *   permitted to be {@code null}.
   * @param classpath
   *   the classpath to use in the generated script.
   * @param spaceSeparatedJVMOptions
   *   JVM options; {@code spaceSeparatedJVMOptions} is not
   *   permitted to be {@code null}.
   * @param directory
   *   the directory in which the script is dropped.
   *   {@code directory} is not permitted to be {@code null}.
   */
  public PropertiesBasedScriptGenerator(final ResourceBundle resourceBundle,
                                        final String className,
                                        final String classpath,
                                        final String spaceSeparatedJVMOptions,
                                        final String directory) {
    ensureNotNull(className,classpath,spaceSeparatedJVMOptions,directory);

    this.className = className;
    this.classpath = classpath;
    this.spaceSeparatedJVMOptions = spaceSeparatedJVMOptions;
    this.directory = directory;
    this.resourceBundle = resourceBundle;
  }



  /**
   * @param className
   *   the name of the class to launch. {@code className} is not
   *   permitted to be {@code null}.
   * @param classpath
   *   the classpath to use in the generated script.
   * @param spaceSeparatedJVMOptions
   *   JVM options; {@code spaceSeparatedJVMOptions} is not
   *   permitted to be {@code null}.
   * @param directory
   *   the directory in which the script is dropped.
   *   {@code directory} is not permitted to be {@code null}.
   */
  public PropertiesBasedScriptGenerator(final String className,
                                        final String classpath,
                                        final String spaceSeparatedJVMOptions,
                                        final String directory) {
    this(StaticData.getResourceBundle(),className,classpath,spaceSeparatedJVMOptions,directory);
  }



  /**
   * Creates a file containing shell script that can be used to launch a
   * class.
   *
   * @return {@code File} referring to a file containing shell script.
   *
   * @throws IOException
   */
  @Override
  public File generateScript() throws IOException {
    return getFile();
  }



  @Override
  public String getFilename() {
    return transformClassToFilename(className);
  }



  @Override
  public Log getLogger() {
    if(logger == null) {
      logger = LogFactory.getLog(getClass());
    }
    return logger;
  }



  private String createFilename(final String className,
                                final String directory) {
    ensureNotNull(className,directory);

    final String classFilename = transformClassToFilename(className);
    return String.format("%s/%s",directory,classFilename);
  }



  private String generateContents() {
    final StringBuilder builder = new StringBuilder();
    builder.append(getShellScriptFirstLine());
    builder.append(getClasspath());
    builder.append(getJVMOptions());
    builder.append(getInvocation());
    return builder.toString();
  }



  private Object getClasspath() {
    return terminatedWithNewLine("export CLASSPATH; CLASSPATH=\"${SCRIPT_CLASSPATH:=" +
      classpath + "}\"");
  }



  private String getExtension() {
    return resourceBundle.getString(PROP_NAME_FILE_EXTENSION);
  }



  private File getFile() {
    String filename = createFilename(className,directory);
    File script = new File(filename);

    try {
      script.createNewFile();
    } catch(IOException ioException) {
      getLogger().error("Unable to create a new file from " + script + " " +
        ioException);
      return null;
    }

    if(!script.setExecutable(true)) {
      String msg =
        String.format("The attempt to set the permissions of %s failed. This " +
          "means the script will not be executable and must be invoked with a" +
          " shell command.",script);
      getLogger().error(msg);
      return null;
    }

    try {
      Writer writer = new FileWriter(script);
      writer.write(generateContents());
      writer.close();
    } catch(IOException e) {
      getLogger().error("Unable to write script " + script);
      return null;
    }

    return script;
  }



  private String getInvocation() {
    final String line = String.format("java ${JVM_OPTS} %s \"$@\"",className);
    return terminatedWithNewLine(line);
  }



  private String getJVMOptions() {
    final String jvmOptionsString =
      String.format("export JVM_OPTS; JVM_OPTS=\"${SCRIPT_JVM_OPTS:=%s}\"",
        spaceSeparatedJVMOptions);
    return terminatedWithNewLine(jvmOptionsString);
  }



  private String getScriptFileExtension() {
    return getExtension();
  }



  private String getShellScriptFirstLine() {
    final String line = resourceBundle.getString(PROP_NAME_SHELL_SCRIPT_FIRST_LINE);
    return terminatedWithNewLine(line);
  }



  private String terminatedWithNewLine(final String line) {
    ensureNotNull(line);

    final String terminated = String.format("%s%s",line,LINE_SEPARATOR);
    return terminated;
  }



  private String transformClassToFilename(final String className) {
    final int begin = className.lastIndexOf('.') + 1;
    final String extension = getScriptFileExtension();
    final String filename =
      String.format("%s%s",className.substring(begin),extension);
    return filename;
  }



  // a launchable class name.
  private final String className;


  // classpath to insert into finished script
  private final String classpath;


  // The directory in which the script is placed.
  private final String directory;


  private Log logger;


  private final ResourceBundle resourceBundle;


  // JVM options
  private final String spaceSeparatedJVMOptions;

}
