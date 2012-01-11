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
package samplecode;


import com.unboundid.util.Validator;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;


/**
 * Generates a bash script with which the demo examples can be invoked.
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 28, 2011")
@CodeVersion("1.2")
public final class ScriptGenerator
{


  // a launchable class name.
  private final String className;


  // classpath to insert into finished script
  private final String classpath;


  // The directory in which the script is placed.
  private final String directory;


  // JVM options
  private final String spaceSeparatedJVMOptions;


  /**
   * @param className
   *          the name of the class to launch. {@code className} is not
   *          permitted to be {@code null}.
   * @param classpath
   *          the classpath to use in the generated script.
   * @param spaceSeparatedJVMOptions
   *          JVM options; {@code spaceSeparatedJVMOptions} is not
   *          permitted to be {@code null}.
   * @param directory
   *          the directory in which the script is dropped.
   *          {@code directory} is not permitted to be {@code null}.
   */
  public ScriptGenerator(
      final String className,final String classpath,
      final String spaceSeparatedJVMOptions,final String directory)
  {


    Validator.ensureNotNullWithMessage(className,
        "The classname is not permitted to be null.");


    Validator.ensureNotNullWithMessage(classpath,
        "The classpath is not permitted to be null.");


    Validator.ensureNotNullWithMessage(spaceSeparatedJVMOptions,
        "The spaceSeparatedJVMOptions is not permitted to be null.");


    Validator.ensureNotNullWithMessage(directory,
        "The directory is not permitted to be null.");


    this.className = className;
    this.classpath = classpath;
    this.spaceSeparatedJVMOptions = spaceSeparatedJVMOptions;
    this.directory = directory;
  }


  /**
   * Creates a file containing shell script that can be used to launch a
   * class.
   * 
   * @return {@code File} referring to a file containing shell script.
   * @throws IOException
   */
  public File generateScript() throws IOException
  {
    return getFile();
  }


  /**
   * @return the filename that is used for the generated script.
   */
  public String getFilename()
  {
    return transformClassToFilename(className);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    return generate();
  }


  /**
   * Creates a filename (UNIX)
   * 
   * @param className2
   * @param directory2
   * @return
   */
  private String createFilename(final String className,final String directory)
  {
    Validator.ensureNotNull(className,directory);
    return directory + "/" + transformClassToFilename(className);
  }


  private String generate()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append(getShellScriptFirstLine());
    builder.append(getClasspath());
    builder.append(getJVMOptions());
    builder.append(getInvocation());
    return builder.toString();
  }


  private Object getClasspath()
  {
    return terminatedWithNewLine("declare -r -x CLASSPATH=\"${SCRIPT_CLASSPATH:=" +
        classpath + "}\"");
  }


  private String getExtension()
  {
    return ".bash";
  }


  private File getFile() throws IOException
  {
    final String filename = createFilename(className,directory);
    final File script = new File(filename);
    script.createNewFile();
    script.setExecutable(true);
    final Writer writer = new FileWriter(script);
    writer.write(generate());
    writer.close();
    return script;
  }


  private String getInvocation()
  {
    return terminatedWithNewLine(String.format("java ${JVM_OPTS} %s \"$@\"",
        className));
  }


  private String getJVMOptions()
  {
    final String jvmOptionsString =
        String.format("declare -r -x JVM_OPTS=\"${SCRIPT_JVM_OPTS:=%s}\"",
            spaceSeparatedJVMOptions);
    return terminatedWithNewLine(jvmOptionsString);
  }


  private String getScriptFileExtension()
  {
    return getExtension();
  }


  private String getShellScriptFirstLine()
  {
    return terminatedWithNewLine("#! /bin/bash");
  }


  private String terminatedWithNewLine(final String string)
  {
    Validator.ensureNotNull(string);
    return string + '\n';

  }


  private String transformClassToFilename(final String className)
  {
    return className.substring(className.lastIndexOf('.') + 1) +
        getScriptFileExtension();
  }

}
