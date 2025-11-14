@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem
@rem SPDX-License-Identifier: Apache-2.0
@rem

@if "%DEBUG%"=="" @echo off
@rem ##########################################################################
@rem
@rem  lcboxing-api startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
@rem This is normally unused
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and LCBOXING_API_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH. 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME% 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\lcboxing-api-1.0.0.jar;%APP_HOME%\lib\javalin-5.6.3.jar;%APP_HOME%\lib\logback-classic-1.4.11.jar;%APP_HOME%\lib\HikariCP-5.0.1.jar;%APP_HOME%\lib\websocket-jetty-server-11.0.17.jar;%APP_HOME%\lib\jetty-webapp-11.0.17.jar;%APP_HOME%\lib\websocket-servlet-11.0.17.jar;%APP_HOME%\lib\jetty-servlet-11.0.17.jar;%APP_HOME%\lib\jetty-security-11.0.17.jar;%APP_HOME%\lib\websocket-core-server-11.0.17.jar;%APP_HOME%\lib\jetty-server-11.0.17.jar;%APP_HOME%\lib\websocket-jetty-common-11.0.17.jar;%APP_HOME%\lib\websocket-core-common-11.0.17.jar;%APP_HOME%\lib\jetty-http-11.0.17.jar;%APP_HOME%\lib\jetty-io-11.0.17.jar;%APP_HOME%\lib\jetty-xml-11.0.17.jar;%APP_HOME%\lib\jetty-util-11.0.17.jar;%APP_HOME%\lib\slf4j-api-2.0.9.jar;%APP_HOME%\lib\jackson-annotations-2.15.3.jar;%APP_HOME%\lib\jackson-core-2.15.3.jar;%APP_HOME%\lib\jackson-datatype-jsr310-2.15.3.jar;%APP_HOME%\lib\java-jwt-4.4.0.jar;%APP_HOME%\lib\jackson-databind-2.15.3.jar;%APP_HOME%\lib\jbcrypt-0.4.jar;%APP_HOME%\lib\javax.mail-1.6.2.jar;%APP_HOME%\lib\dotenv-java-3.0.0.jar;%APP_HOME%\lib\hibernate-validator-8.0.1.Final.jar;%APP_HOME%\lib\jakarta.el-4.0.2.jar;%APP_HOME%\lib\bcrypt-0.10.2.jar;%APP_HOME%\lib\kotlin-stdlib-jdk8-1.8.21.jar;%APP_HOME%\lib\kotlin-stdlib-jdk7-1.8.21.jar;%APP_HOME%\lib\kotlin-stdlib-1.8.21.jar;%APP_HOME%\lib\annotations-24.0.1.jar;%APP_HOME%\lib\websocket-jetty-api-11.0.17.jar;%APP_HOME%\lib\logback-core-1.4.11.jar;%APP_HOME%\lib\mysql-connector-j-8.0.33.jar;%APP_HOME%\lib\activation-1.1.jar;%APP_HOME%\lib\jakarta.validation-api-3.0.2.jar;%APP_HOME%\lib\jboss-logging-3.4.3.Final.jar;%APP_HOME%\lib\classmate-1.5.1.jar;%APP_HOME%\lib\jakarta.el-api-4.0.0.jar;%APP_HOME%\lib\bytes-1.5.0.jar;%APP_HOME%\lib\jetty-jakarta-servlet-api-5.0.2.jar;%APP_HOME%\lib\protobuf-java-3.21.9.jar;%APP_HOME%\lib\kotlin-stdlib-common-1.8.21.jar


@rem Execute lcboxing-api
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %LCBOXING_API_OPTS%  -classpath "%CLASSPATH%" com.lcboxing.Main %*

:end
@rem End local scope for the variables with windows NT shell
if %ERRORLEVEL% equ 0 goto mainEnd

:fail
rem Set variable LCBOXING_API_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
set EXIT_CODE=%ERRORLEVEL%
if %EXIT_CODE% equ 0 set EXIT_CODE=1
if not ""=="%LCBOXING_API_EXIT_CONSOLE%" exit %EXIT_CODE%
exit /b %EXIT_CODE%

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
