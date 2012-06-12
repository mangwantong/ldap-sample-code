#! /bin/bash

function displayCommand()
{
    echo
    echo $1
    echo
}

function getIntroductionColumnWidthArgumentIdentifier()
{
    echo --introductionColumnWidth
}

function getVerboseArgumentIdentifier()
{
    echo --verbose
}

# The naming context used by the sample code demonstrations,
# this string is a distinguished name supported by the
# directory server used to support the sample code
# demonstrations.
#
# Default:       "DC=example,DC=com"
# Overridden by: $SAMPLECODE_NAMING_CONTEXT
function getNamingContext()
{
    echo ${SAMPLECODE_NAMING_CONTEXT:="dc=example,dc=com"}
}

function getBindDn()
{
    echo ${SAMPLECODE_BIND_DN:=uid=samplecode,ou=people,$(getNamingContext)}
}

function getBindPassword()
{
    echo ${SAMPLECODE_BIND_PASSWORD:=password}
}

function getHostname()
{
    echo "${SAMPLECODE_HOSTNAME:=ldap.example.com}"
}

function getSslPort()
{
    echo "${SAMPLECODE_SSLPORT:=636}"
}

function getWorkspace()
{
    echo "${SAMPLECODE_WORKSPACE:=$HOME/samplecode}"
}

function getResourcesDirectory()
{
    echo ${SAMPLECODE_RESOURCES_DIRECTORY:=src/main/resources}
}

function getLoggingPropertiesFilename()
{
    echo ${SAMPLECODE_LOGGING_PROPERTIES_FILENAME:=$(getResourcesDirectory)/logging.properties}
}

function getCommandLineOptionsPropertiesFile()
{
    echo ${SAMPLECODE_COMMAND_LINE_OPTIONS_PROPERTIES_FILE:=$(getResourcesDirectory)/commandLineOptions.properties}
}

function getLdapConnectionArguments()
{
    echo ${ldapConnectionArguments}
}

function getNewAttributeValueArgument()
{
    echo --newAttributeValue ${SAMPLECODE_NEW_ATTRIBUTE_VALUE:=abcdef} --attribute cn --filter '(cn=def)'
}




#
# Functions supporting the AssertionRequestControlDemo
#
function getAssertionRequestControlDemoColumnWidth()
{
    echo $(getIntroductionColumnWidthArgumentIdentifier) ${SAMPLECODE_ASSERTIONREQUESTCONTROLDEMO_COLUMN_WIDTH:-96}
}

function getAssertionRequestControlDemoLdapConnectionArguments()
{
    echo ${SAMPLECODE_ASSERTIONREQUESTCONTROLDEMO_LDAP_CONNECTION_ARGUMENTS:-$(getLdapConnectionArguments)}
}

function getAssertionRequestControlDemoCommandPath()
{
    echo "${scriptDirectory}/AssertionRequestControlDemo.bash"
}

function getAssertionRequestControlDemoVerboseArgument()
{
    echo --verbose
}

function getAssertionRequestControlDemoArguments()
{
    args="";
    args="${args} $(getAssertionRequestControlDemoLdapConnectionArguments)"
    args="${args} $(getAssertionRequestControlDemoColumnWidth)"
    args="${args} $(getAssertionRequestControlDemoVerboseArgument)"
    args="${args} --newAttributeValue ${SAMPLECODE_NEW_ATTRIBUTE_VALUE=-value}"
    echo ${args}
}

function executeAssertionRequestControlDemo()
{
    command="$(getAssertionRequestControlDemoCommandPath) $(getAssertionRequestControlDemoArguments)"
    displayCommand "${command}"
    ${command}
}





#
# Functions supporting the AuthDemo
#
function getAuthDemoColumnWidth()
{
    echo $(getIntroductionColumnWidthArgumentIdentifier) ${SAMPLECODE_AUTHDEMO_COLUMN_WIDTH:-96}
}

function getAuthDemoLdapConnectionArguments()
{
    echo ${SAMPLECODE_AUTHDEMO_LDAP_CONNECTION_ARGUMENTS:-$(getLdapConnectionArguments)}
}

function getAuthDemoCommandPath()
{
    echo "${scriptDirectory}/AuthDemo.bash"
}

function getAuthDemoVerboseArgument()
{
    echo --verbose
}

function getAuthDemoArguments()
{
    args="";
    args="${args} $(getAuthDemoLdapConnectionArguments)"
    args="${args} $(getAuthDemoColumnWidth)"
    args="${args} $(getAuthDemoVerboseArgument)"
    echo ${args}
}

function executeAuthDemo()
{
    command="$(getAuthDemoCommandPath) $(getAuthDemoArguments)"
    displayCommand "${command}"
    ${command}
}






#
# Functions supporting the BindDemo
#
function getBindDemoColumnWidth()
{
    echo $(getIntroductionColumnWidthArgumentIdentifier) ${SAMPLECODE_BINDDEMO_COLUMN_WIDTH:-96}
}

function getBindDemoLdapConnectionArguments()
{
    echo ${SAMPLECODE_BINDDEMO_LDAP_CONNECTION_ARGUMENTS:-$(getLdapConnectionArguments)}
}

function getBindDemoCommandPath()
{
    echo "${scriptDirectory}/BindDemo.bash"
}

function getBindDemoVerboseArgument()
{
    echo --verbose
}

function getBindDemoArguments()
{
    args="";
    args="${args} $(getBindDemoLdapConnectionArguments)"
    args="${args} $(getBindDemoColumnWidth)"
    args="${args} $(getBindDemoVerboseArgument)"
    echo ${args}
}

function executeBindDemo()
{
    command="$(getBindDemoCommandPath) $(getBindDemoArguments)"
    displayCommand "${command}"
    ${command}
}







#
# Functions supporting the CompareDemo
#
function getCompareDemoColumnWidth()
{
    echo $(getIntroductionColumnWidthArgumentIdentifier) ${SAMPLECODE_COMPAREDEMO_COLUMN_WIDTH:-96}
}

function getCompareDemoLdapConnectionArguments()
{
    echo ${SAMPLECODE_COMPAREDEMO_LDAP_CONNECTION_ARGUMENTS:-$(getLdapConnectionArguments)}
}

function getCompareDemoCommandPath()
{
    echo "${scriptDirectory}/CompareDemo.bash"
}

function getCompareDemoVerboseArgument()
{
    echo --verbose
}

function getCompareDemoArguments()
{
    args="";
    args="${args} $(getCompareDemoLdapConnectionArguments)"
    args="${args} $(getCompareDemoColumnWidth)"
    args="${args} $(getCompareDemoVerboseArgument)"
    args="${args} --assertion ${SAMPLECODE_ASSERTION:-abcdef}"
    args="${args} --baseObject $(getNamingContext)"
    echo ${args}
}

function executeCompareDemo()
{
    command="$(getCompareDemoCommandPath) $(getCompareDemoArguments)"
    displayCommand "${command}"
    ${command}
}

function executeCompareDemo()
{
    command="$(getCompareDemoCommandPath) $(getCompareDemoArguments)"
    displayCommand "${command}"
    ${command}
}






#
# Functions supporting the EffectiveRightsEntryDemo
#
function getEffectiveRightsEntryDemoColumnWidth()
{
    echo $(getIntroductionColumnWidthArgumentIdentifier) ${SAMPLECODE_EFFECTIVERIGHTSENTRYDEMO_COLUMN_WIDTH:-96}
}

function getEffectiveRightsEntryDemoLdapConnectionArguments()
{
    echo ${SAMPLECODE_EFFECTIVERIGHTSENTRYDEMO_LDAP_CONNECTION_ARGUMENTS:-$(getLdapConnectionArguments)}
}

function getEffectiveRightsEntryDemoCommandPath()
{
    echo "${scriptDirectory}/EffectiveRightsEntryDemo.bash"
}

function getEffectiveRightsEntryDemoVerboseArgument()
{
    echo --verbose
}

function getEffectiveRightsEntryDemoArguments()
{
    args="";
    args="${args} $(getEffectiveRightsEntryDemoLdapConnectionArguments)"
    args="${args} $(getEffectiveRightsEntryDemoColumnWidth)"
    args="${args} $(getEffectiveRightsEntryDemoVerboseArgument)"
    args="${args} --baseObject $(getNamingContext)"
    args="${args} --authZid ${SAMPLECODE_AUTHZID:-uid=user.0,ou=people,$(getNamingContext)}"
    args="${args} --entry ${SAMPLECODE_ENTRY:-uid=user.0,ou=people,$(getNamingContext)}"
    args="${args} --right compare"
    args="${args} --right proxy"
    args="${args} --right read"
    args="${args} --right search"
    args="${args} --right selfwrite_add"
    args="${args} --right selfwrite_delete"
    args="${args} --right write"
    echo ${args}
}

function executeEffectiveRightsEntryDemo()
{
    command="$(getEffectiveRightsEntryDemoCommandPath) $(getEffectiveRightsEntryDemoArguments)"
    displayCommand "${command}"
    ${command}
}







#
# Functions supporting the LdapListenerExampleDemo
#
function getLdapListenerExampleDemoColumnWidth()
{
    echo $(getIntroductionColumnWidthArgumentIdentifier) ${SAMPLECODE_LDAPLISTENEREXAMPLEDEMO_COLUMN_WIDTH:-96}
}

function getLdapListenerExampleDemoLdapConnectionArguments()
{
    echo ${SAMPLECODE_LDAPLISTENEREXAMPLEDEMO_LDAP_CONNECTION_ARGUMENTS:-$(getLdapConnectionArguments)}
}

function getLdapListenerExampleDemoCommandPath()
{
    echo "${scriptDirectory}/LdapListenerExample.bash"
}

function getLdapListenerExampleDemoVerboseArgument()
{
    echo --verbose
}

function getLdapListenerExampleDemoArguments()
{
    args="";
    args="${args} $(getLdapListenerExampleDemoLdapConnectionArguments)"
    args="${args} $(getLdapListenerExampleDemoColumnWidth)"
    args="${args} $(getLdapListenerExampleDemoVerboseArgument)"
    args="${args} --assertion ${SAMPLECODE_ASSERTION:-value}"
    args="${args} --baseObject $(getNamingContext)"
    echo ${args}
}

function executeLdapListenerExampleDemo()
{
    command="$(getLdapListenerExampleDemoCommandPath) $(getLdapListenerExampleDemoArguments)"
    displayCommand "${command}"
    ${command}
}

function getLdapListenerExampleDemoArguments()
{
    args="";
    args="${args} $(getLdapListenerExampleDemoLdapConnectionArguments)"
    args="${args} $(getLdapListenerExampleDemoColumnWidth)"
    args="${args} $(getLdapListenerExampleDemoVerboseArgument)"
    args="${args} --baseObject $(getNamingContext)"
    args="${args} --ldifFile ${SAMPLECODE_IN_MEMORY_SERVER_LDIF_FILE:-testInMemoryDirectoryServer.LDIF}"
    echo ${args}
}

function executeLdapListenerExampleDemo()
{
    command="$(getLdapListenerExampleDemoCommandPath) $(getLdapListenerExampleDemoArguments)"
    displayCommand "${command}"
    ${command}
}








#
# Functions supporting the MatchingRuleDemo
#
function getMatchingRuleDemoColumnWidth()
{
    echo $(getIntroductionColumnWidthArgumentIdentifier) ${SAMPLECODE_LDAPLISTENEREXAMPLEDEMO_COLUMN_WIDTH:-96}
}

function getMatchingRuleDemoLdapConnectionArguments()
{
    echo ${SAMPLECODE_LDAPLISTENEREXAMPLEDEMO_LDAP_CONNECTION_ARGUMENTS:-$(getLdapConnectionArguments)}
}

function getMatchingRuleDemoCommandPath()
{
    echo "${scriptDirectory}/MatchingRuleDemo.bash"
}

function getMatchingRuleDemoVerboseArgument()
{
    echo --verbose
}

function getMatchingRuleDemoArguments()
{
    args="";
    args="${args} $(getMatchingRuleDemoLdapConnectionArguments)"
    args="${args} $(getMatchingRuleDemoColumnWidth)"
    args="${args} $(getMatchingRuleDemoVerboseArgument)"
    args="${args} --assertion ${SAMPLECODE_ASSERTION:-abcdef}"
    args="${args} --baseObject $(getNamingContext)"
    echo ${args}
}

function executeMatchingRuleDemo()
{
    command="$(getMatchingRuleDemoCommandPath) $(getMatchingRuleDemoArguments)"
    displayCommand "${command}"
    ${command}
}

function getMatchingRuleDemoArguments()
{
    args="";
    args="${args} $(getMatchingRuleDemoLdapConnectionArguments)"
    args="${args} $(getMatchingRuleDemoColumnWidth)"
    args="${args} $(getMatchingRuleDemoVerboseArgument)"
    args="${args} --baseObject $(getNamingContext)"
    args="${args} --entryDn1 $(getNamingContext)"
    args="${args} --entryDn2 $(getNamingContext)"
    args="${args} --filter ${SAMPLECODE_FILTER:-(uid=user.0)}"
    echo ${args}
}

function executeMatchingRuleDemo()
{
    command="$(getMatchingRuleDemoCommandPath) $(getMatchingRuleDemoArguments)"
    displayCommand "${command}"
    ${command}
}








#
# Functions supporting the ModifyIncrementDemo
#
function getModifyIncrementDemoColumnWidth()
{
    echo $(getIntroductionColumnWidthArgumentIdentifier) ${SAMPLECODE_LDAPLISTENEREXAMPLEDEMO_COLUMN_WIDTH:-96}
}

function getModifyIncrementDemoLdapConnectionArguments()
{
    echo ${SAMPLECODE_LDAPLISTENEREXAMPLEDEMO_LDAP_CONNECTION_ARGUMENTS:-$(getLdapConnectionArguments)}
}

function getModifyIncrementDemoCommandPath()
{
    echo "${scriptDirectory}/ModifyIncrementDemo.bash"
}

function getModifyIncrementDemoVerboseArgument()
{
    echo --verbose
}

function getModifyIncrementDemoArguments()
{
    args="";
    args="${args} $(getModifyIncrementDemoLdapConnectionArguments)"
    args="${args} $(getModifyIncrementDemoColumnWidth)"
    args="${args} $(getModifyIncrementDemoVerboseArgument)"
    args="${args} --assertion ${SAMPLECODE_ASSERTION:-abcdef}"
    args="${args} --baseObject $(getNamingContext)"
    echo ${args}
}

function executeModifyIncrementDemo()
{
    command="$(getModifyIncrementDemoCommandPath) $(getModifyIncrementDemoArguments)"
    displayCommand "${command}"
    ${command}
}

function getModifyIncrementDemoArguments()
{
    args="";
    args="${args} $(getModifyIncrementDemoLdapConnectionArguments)"
    args="${args} $(getModifyIncrementDemoColumnWidth)"
    args="${args} $(getModifyIncrementDemoVerboseArgument)"
    args="${args} --baseObject $(getNamingContext)"
    args="${args} --attribute ${SAMPLECODE_ATTRIBUTE_TYPE:-employeeNumber}"
    args="${args} --incrementValue ${SAMPLECODE_INCREMENT_VALUE:-5}"
    args="${args} --entry ${SAMPLECODE_ENTRY:-ou=people,$(getNamingContext)}"
    echo ${args}
}

function executeModifyIncrementDemo()
{
    command="$(getModifyIncrementDemoCommandPath) $(getModifyIncrementDemoArguments)"
    displayCommand "${command}"
    ${command}
}








#
# Functions supporting the VirtualListViewDemo
#
function getVirtualListViewDemoColumnWidth()
{
    echo $(getIntroductionColumnWidthArgumentIdentifier) ${SAMPLECODE_LDAPLISTENEREXAMPLEDEMO_COLUMN_WIDTH:-96}
}

function getVirtualListViewDemoLdapConnectionArguments()
{
    echo ${SAMPLECODE_LDAPLISTENEREXAMPLEDEMO_LDAP_CONNECTION_ARGUMENTS:-$(getLdapConnectionArguments)}
}

function getVirtualListViewDemoCommandPath()
{
    echo "${scriptDirectory}/VirtualListViewDemo.bash"
}

function getVirtualListViewDemoVerboseArgument()
{
    echo --verbose
}

function getVirtualListViewDemoArguments()
{
    args="";
    args="${args} $(getVirtualListViewDemoLdapConnectionArguments)"
    args="${args} $(getVirtualListViewDemoColumnWidth)"
    args="${args} $(getVirtualListViewDemoVerboseArgument)"
    args="${args} --assertion ${SAMPLECODE_ASSERTION:-abcdef}"
    args="${args} --baseObject $(getNamingContext)"
    echo ${args}
}

function executeVirtualListViewDemo()
{
    command="$(getVirtualListViewDemoCommandPath) $(getVirtualListViewDemoArguments)"
    displayCommand "${command}"
    ${command}
}

function getVirtualListViewDemoArguments()
{
    args="";
    args="${args} $(getVirtualListViewDemoLdapConnectionArguments)"
    args="${args} $(getVirtualListViewDemoColumnWidth)"
    args="${args} $(getVirtualListViewDemoVerboseArgument)"
    args="${args} --baseObject $(getNamingContext)"
    echo ${args}
}

function executeVirtualListViewDemo()
{
    command="$(getVirtualListViewDemoCommandPath) $(getVirtualListViewDemoArguments)"
    displayCommand "${command}"
    ${command}
}







#
# execute each defined demonstation
#
function executeAllDemos()
{
    for function in ${functions}
    do
        echo Demonstration: ${function}
        $function
        echo Demonstration: ${function} complete
        echo
    done
}

#
# build the samplecode project
#
function build()
{
    mvn ${targets}
}

#
# Filter out lines containing INFO
#
function filterBuildOutput()
{
    perl -lane 'print if not /INFO/'
}

set -ue

declare DO_BUILD="no"
for arg in $*
do
    if [ "${arg}" = "do_build" ]; then
        DO_BUILD="yes"
    fi
done

# location of the scripts that drive the demonstrations
declare -r scriptDirectory="${HOME}/bin"

# The naming context
declare -r namingContext=$(getNamingContext)
declare -r ldapServerHostname=$(getHostname)
declare -r sslPort=$(getSslPort)
declare -r secure="--useSSL --trustAll"
declare -r ldapServerBindDn=$(getBindDn)
declare -r ldapServerBindPassword=$(getBindPassword)
declare ldapConnectionArguments=""
ldapConnectionArguments="${ldapConnectionArguments} --hostname ${ldapServerHostname}"
ldapConnectionArguments="${ldapConnectionArguments} --port ${sslPort}"
ldapConnectionArguments="${ldapConnectionArguments} ${secure}"
ldapConnectionArguments="${ldapConnectionArguments} --bindDn ${ldapServerBindDn}"
ldapConnectionArguments="${ldapConnectionArguments} --bindPassword ${ldapServerBindPassword}"

# functions that execute demonstrations
declare functions=""
declare functions="${functions} executeAssertionRequestControlDemo"
declare functions="${functions} executeAuthDemo"
declare functions="${functions} executeBindDemo"
declare functions="${functions} executeCompareDemo"
declare functions="${functions} executeEffectiveRightsEntryDemo"
declare functions="${functions} executeLdapListenerExampleDemo"
declare functions="${functions} executeMatchingRuleDemo"
declare functions="${functions} executeModifyIncrementDemo"
declare functions="${functions} executeVirtualListViewDemo"

# mvn targets
declare -r targets="clean package"

declare -r ws="${HOME}"/workspace/sample-code/trunk
cd ${ws}
if [ "${DO_BUILD}" = "yes" ]; then
    build|filterBuildOutput
fi
executeAllDemos
