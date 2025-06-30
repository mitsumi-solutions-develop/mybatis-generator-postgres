# mybatis-generator-postgres

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white) ![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white) [![Licence](https://img.shields.io/github/license/Ileriayo/markdown-badges?style=for-the-badge)](./LICENSE)

# supported

- java version: 21

# support

## generate mybatis code flowing postgres extension types

- Enum
- Json
- UUID

## generatedKey column

if defined `{primary_key}`, find table's primary column to set generatedKey column.

## generatorConfig
- use IntrospectedTableMyBatis3PostgresDynamicSqlImpl
- javaTypeResolver
  if use json, uuid type please define jsonModelsPackage, enumerationsPackage.
- generatedKey column
  if defined `{primary_key}`, find table's primary column to set generatedKey column.

```xml
<context id="tables" targetRuntime="io.github.mitsumi.solutions.mybatis.generator.postgres.dynamic.sql.IntrospectedTableMyBatis3PostgresDynamicSqlImpl">
    <javaTypeResolver type="io.github.mitsumi.solutions.mybatis.generator.postgres.resolvers.JavaTypeResolver">
        <property name="useJSR310Types" value="true"/>
        <property name="jsonModelsPackage" value="io.github.mitsumi.solutions.mybatis.postgres.generated.json.models"/>
        <property name="enumerationsPackage" value="io.github.mitsumi.solutions.mybatis.postgres.generated.enumerations"/>
    </javaTypeResolver>
    
    <table tableName="tbl_%"
           enableInsert="true"
           enableSelectByPrimaryKey="true"
           enableUpdateByPrimaryKey="true"
           enableDeleteByPrimaryKey="true">
        <generatedKey column="{primary_key}" sqlStatement="JDBC" />
    </table>
</context>
```
sample : [mybatis-generator-postgres-demo](https://github.com/mitsumi-solutions-develop/mybatis-generator-postgres-demo)

# build plugin

## maven

```xml

<build>
    <plugins>
        <plugin>
            <groupId>org.mybatis.generator</groupId>
            <artifactId>mybatis-generator-maven-plugin</artifactId>
            <version>1.4.2</version>
            <configuration>
                <configurationFile>${project.basedir}/src/main/resources/generatorConfig.xml</configurationFile>
                <overwrite>true</overwrite>
            </configuration>
            <dependencies>
                <dependency>
                    <groupId>org.postgresql</groupId>
                    <artifactId>postgresql</artifactId>
                    <version>42.6.0</version>
                </dependency>
                <dependency>
                    <groupId>io.github.mitsumi-solutions-develop</groupId>
                    <artifactId>java-shared-utils</artifactId>
                    <version>1.0.2</version>
                </dependency>
                <dependency>
                    <groupId>io.github.mitsumi-solutions-develop</groupId>
                    <artifactId>mybatis-generator-postgres</artifactId>
                    <version>1.0.1</version>
                </dependency>
                <dependency>
                    <groupId>com.softwareloop</groupId>
                    <artifactId>mybatis-generator-lombok-plugin</artifactId>
                    <version>1.0</version>
                </dependency>
            </dependencies>
        </plugin>
    </plugins>
</build>
```
