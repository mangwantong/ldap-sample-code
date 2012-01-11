#! /bin/bash


set -u

if which java>/dev/null 2>&1
then
    :
else
    echo java not found. 1>&2
    exit 1
fi

# The CLASSPATH must contain the path to the
# commercial edition of the UnboundID LDAP SDK
# and the path to the samplecode classes directory
# or the samplecode jar file.
declare -r classpathComponentUnboundIDCeJar=/path/to/unboundid-ldap-sdk-ce.jar
declare -r classpathComponentSampleCode=/path/to/samplecode/target/classes
declare -x -r CLASSPATH="${SCRIPT_CLASSPATH:=${classpathComponentUnboundIDCeJar}:${classpathComponentSampleCode}}"


# JVM specific arguments
declare -r JVM_OPTS="${SCRIPT_JVM_OPTS:=-Xms32m -Xmx32m -d64}"


java ${JVM_OPTS} samplecode.tools.ScriptTool "$@"
