#! /bin/sh
export CLASSPATH; CLASSPATH="${SCRIPT_CLASSPATH:=/Users/terrygardner/workspace/sample-code/trunk/target/sample-code-3.0-jar-with-dependencies.jar}"
export JVM_OPTS; JVM_OPTS="${SCRIPT_JVM_OPTS:=-Xms32m -Xmx32m -d64}"
java ${JVM_OPTS} samplecode.auth.AuthDemo "$@"
