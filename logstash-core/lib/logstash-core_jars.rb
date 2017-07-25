# this is a generated file, to avoid over-writing it just delete this comment
begin
  require 'jar_dependencies'
rescue LoadError
  require 'org/glassfish/hk2/hk2-utils/2.5.0-b32/hk2-utils-2.5.0-b32.jar'
  require 'org/eclipse/jetty/jetty-http/9.4.6.v20170531/jetty-http-9.4.6.v20170531.jar'
  require 'org/glassfish/jersey/bundles/repackaged/jersey-guava/2.25.1/jersey-guava-2.25.1.jar'
  require 'org/glassfish/jersey/core/jersey-server/2.25.1/jersey-server-2.25.1.jar'
  require 'org/glassfish/jersey/core/jersey-common/2.25.1/jersey-common-2.25.1.jar'
  require 'org/javassist/javassist/3.20.0-GA/javassist-3.20.0-GA.jar'
  require 'org/eclipse/jetty/jetty-io/9.4.6.v20170531/jetty-io-9.4.6.v20170531.jar'
  require 'org/glassfish/hk2/hk2-locator/2.5.0-b32/hk2-locator-2.5.0-b32.jar'
  require 'org/eclipse/jetty/jetty-continuation/9.2.14.v20151106/jetty-continuation-9.2.14.v20151106.jar'
  require 'org/eclipse/jetty/jetty-security/9.4.6.v20170531/jetty-security-9.4.6.v20170531.jar'
  require 'com/fasterxml/jackson/core/jackson-databind/2.7.3/jackson-databind-2.7.3.jar'
  require 'org/glassfish/jersey/core/jersey-client/2.25.1/jersey-client-2.25.1.jar'
  require 'org/glassfish/jersey/media/jersey-media-jaxb/2.25.1/jersey-media-jaxb-2.25.1.jar'
  require 'org/glassfish/hk2/external/aopalliance-repackaged/2.5.0-b32/aopalliance-repackaged-2.5.0-b32.jar'
  require 'org/glassfish/hk2/external/javax.inject/2.5.0-b32/javax.inject-2.5.0-b32.jar'
  require 'org/glassfish/jersey/media/jersey-media-json-jackson/2.25.1/jersey-media-json-jackson-2.25.1.jar'
  require 'javax/annotation/javax.annotation-api/1.2/javax.annotation-api-1.2.jar'
  require 'org/apache/logging/log4j/log4j-core/2.6.2/log4j-core-2.6.2.jar'
  require 'org/glassfish/hk2/hk2-api/2.5.0-b32/hk2-api-2.5.0-b32.jar'
  require 'com/fasterxml/jackson/module/jackson-module-jaxb-annotations/2.8.4/jackson-module-jaxb-annotations-2.8.4.jar'
  require 'org/slf4j/slf4j-api/1.7.21/slf4j-api-1.7.21.jar'
  require 'com/fasterxml/jackson/jaxrs/jackson-jaxrs-base/2.8.4/jackson-jaxrs-base-2.8.4.jar'
  require 'javax/servlet/javax.servlet-api/3.1.0/javax.servlet-api-3.1.0.jar'
  require 'org/glassfish/hk2/osgi-resource-locator/1.0.1/osgi-resource-locator-1.0.1.jar'
  require 'org/eclipse/jetty/jetty-util/9.4.6.v20170531/jetty-util-9.4.6.v20170531.jar'
  require 'org/glassfish/jersey/ext/jersey-entity-filtering/2.25.1/jersey-entity-filtering-2.25.1.jar'
  require 'javax/ws/rs/javax.ws.rs-api/2.0.1/javax.ws.rs-api-2.0.1.jar'
  require 'com/fasterxml/jackson/jaxrs/jackson-jaxrs-json-provider/2.8.4/jackson-jaxrs-json-provider-2.8.4.jar'
  require 'org/glassfish/jersey/containers/jersey-container-servlet-core/2.25.1/jersey-container-servlet-core-2.25.1.jar'
  require 'org/apache/logging/log4j/log4j-api/2.6.2/log4j-api-2.6.2.jar'
  require 'org/eclipse/jetty/jetty-server/9.4.6.v20170531/jetty-server-9.4.6.v20170531.jar'
  require 'com/fasterxml/jackson/core/jackson-annotations/2.7.3/jackson-annotations-2.7.3.jar'
  require 'org/apache/logging/log4j/log4j-slf4j-impl/2.6.2/log4j-slf4j-impl-2.6.2.jar'
  require 'com/fasterxml/jackson/dataformat/jackson-dataformat-cbor/2.7.3/jackson-dataformat-cbor-2.7.3.jar'
  require 'javax/validation/validation-api/1.1.0.Final/validation-api-1.1.0.Final.jar'
  require 'org/glassfish/jersey/containers/jersey-container-jetty-http/2.25.1/jersey-container-jetty-http-2.25.1.jar'
  require 'org/eclipse/jetty/jetty-servlet/9.4.6.v20170531/jetty-servlet-9.4.6.v20170531.jar'
  require 'com/fasterxml/jackson/core/jackson-core/2.7.3/jackson-core-2.7.3.jar'
end

if defined? Jars
  require_jar( 'org.glassfish.hk2', 'hk2-utils', '2.5.0-b32' )
  require_jar( 'org.eclipse.jetty', 'jetty-http', '9.4.6.v20170531' )
  require_jar( 'org.glassfish.jersey.bundles.repackaged', 'jersey-guava', '2.25.1' )
  require_jar( 'org.glassfish.jersey.core', 'jersey-server', '2.25.1' )
  require_jar( 'org.glassfish.jersey.core', 'jersey-common', '2.25.1' )
  require_jar( 'org.javassist', 'javassist', '3.20.0-GA' )
  require_jar( 'org.eclipse.jetty', 'jetty-io', '9.4.6.v20170531' )
  require_jar( 'org.glassfish.hk2', 'hk2-locator', '2.5.0-b32' )
  require_jar( 'org.eclipse.jetty', 'jetty-continuation', '9.2.14.v20151106' )
  require_jar( 'org.eclipse.jetty', 'jetty-security', '9.4.6.v20170531' )
  require_jar( 'com.fasterxml.jackson.core', 'jackson-databind', '2.7.3' )
  require_jar( 'org.glassfish.jersey.core', 'jersey-client', '2.25.1' )
  require_jar( 'org.glassfish.jersey.media', 'jersey-media-jaxb', '2.25.1' )
  require_jar( 'org.glassfish.hk2.external', 'aopalliance-repackaged', '2.5.0-b32' )
  require_jar( 'org.glassfish.hk2.external', 'javax.inject', '2.5.0-b32' )
  require_jar( 'org.glassfish.jersey.media', 'jersey-media-json-jackson', '2.25.1' )
  require_jar( 'javax.annotation', 'javax.annotation-api', '1.2' )
  require_jar( 'org.apache.logging.log4j', 'log4j-core', '2.6.2' )
  require_jar( 'org.glassfish.hk2', 'hk2-api', '2.5.0-b32' )
  require_jar( 'com.fasterxml.jackson.module', 'jackson-module-jaxb-annotations', '2.8.4' )
  require_jar( 'org.slf4j', 'slf4j-api', '1.7.21' )
  require_jar( 'com.fasterxml.jackson.jaxrs', 'jackson-jaxrs-base', '2.8.4' )
  require_jar( 'javax.servlet', 'javax.servlet-api', '3.1.0' )
  require_jar( 'org.glassfish.hk2', 'osgi-resource-locator', '1.0.1' )
  require_jar( 'org.eclipse.jetty', 'jetty-util', '9.4.6.v20170531' )
  require_jar( 'org.glassfish.jersey.ext', 'jersey-entity-filtering', '2.25.1' )
  require_jar( 'javax.ws.rs', 'javax.ws.rs-api', '2.0.1' )
  require_jar( 'com.fasterxml.jackson.jaxrs', 'jackson-jaxrs-json-provider', '2.8.4' )
  require_jar( 'org.glassfish.jersey.containers', 'jersey-container-servlet-core', '2.25.1' )
  require_jar( 'org.apache.logging.log4j', 'log4j-api', '2.6.2' )
  require_jar( 'org.eclipse.jetty', 'jetty-server', '9.4.6.v20170531' )
  require_jar( 'com.fasterxml.jackson.core', 'jackson-annotations', '2.7.3' )
  require_jar( 'org.apache.logging.log4j', 'log4j-slf4j-impl', '2.6.2' )
  require_jar( 'com.fasterxml.jackson.dataformat', 'jackson-dataformat-cbor', '2.7.3' )
  require_jar( 'javax.validation', 'validation-api', '1.1.0.Final' )
  require_jar( 'org.glassfish.jersey.containers', 'jersey-container-jetty-http', '2.25.1' )
  require_jar( 'org.eclipse.jetty', 'jetty-servlet', '9.4.6.v20170531' )
  require_jar( 'com.fasterxml.jackson.core', 'jackson-core', '2.7.3' )
end
