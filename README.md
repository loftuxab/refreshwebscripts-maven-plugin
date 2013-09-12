# Maven Plugin for refreshing Alfresco Web Scripts

To use it, two things has to be done in the Maven POM file.

## Usage

### 1. Add a plugin repository

```xml
<pluginRepositories>
  <pluginRepository>
    <id>oakman-refreshwebscripts-maven-plugin</id>
    <url>https://raw.github.com/oakman/refreshwebscripts-maven-plugin/mvn-repo</url>
  </pluginRepository>
</pluginRepositories>
```

### 2. Add the plugin to the build

```xml
<plugin>
  <groupId>org.redpill-linpro.alfresco</groupId>
  <artifactId>refreshwebscripts-maven-plugin</artifactId>
  <version>1.0.0</version>
  <executions>
    <execution>
      <goals>
        <goal>refresh</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

Whenever a `mvn package` command is executed, the built artifact is uploaded to the repository. 

## Configuration options

There are some configuration options that can be set.

| Option      | Default value      | Description
|:----------- |:------------------ |:-----------
| username    | admin              | The username for the Alfresco acount used for the refresh
| password    | admin              | The password for the Alfresco acount used for the refresh
| url         | *no default value* | The URL to call, typically ```http://localhost:8080/alfresco/service/index```
| method      | post               | The HTTP method used
