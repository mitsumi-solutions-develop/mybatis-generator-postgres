package io.github.mitsumi.solutions.mybatis.generator.postgres.config.parsers;

import io.github.mitsumi.solutions.mybatis.generator.postgres.config.parsers.constants.AttributesConstant;
import io.github.mitsumi.solutions.mybatis.generator.postgres.config.parsers.constants.NodeNameConstant;
import io.github.mitsumi.solutions.mybatis.generator.postgres.resolvers.GenerateKeyColumnNameResolver;
import lombok.SneakyThrows;
import org.mybatis.generator.config.*;
import org.mybatis.generator.config.xml.MyBatisGeneratorConfigurationParser;
import org.mybatis.generator.internal.util.StringUtility;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;
import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

@SuppressWarnings({
    "PMD.LocalVariableCouldBeFinal",
    "PMD.TooManyMethods",
    "PMD.CommentRequired",
    "PMD.AvoidDuplicateLiterals"
})
public class MybatisGeneratorPostgresConfigurationParser extends MyBatisGeneratorConfigurationParser {

    public MybatisGeneratorPostgresConfigurationParser(final Properties extraProperties) {
        super(extraProperties);
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    @Override
    protected void parseTable(final Context context, final Node node) {
        TableConfiguration configuration = tableConfiguration(context, node);

        final NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node childNode = nodeList.item(i);

            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            switch (childNode.getNodeName()) {
                case NodeNameConstant.PROPERTY -> parseProperty(configuration, childNode);
                case NodeNameConstant.COLUMN_OVERRIDE ->
                    configuration.addColumnOverride(parseColumnOverride(childNode));
                case NodeNameConstant.IGNORE_COLUMN -> configuration.addIgnoredColumn(parseIgnoreColumn(childNode));
                case NodeNameConstant.IGNORE_COLUMNS_BY_REGEX ->
                    configuration.addIgnoredColumnPattern(parseIgnoreColumnByRegex(childNode));
                case NodeNameConstant.GENERATED_KEY -> configuration.setGeneratedKey(
                    parseGeneratedKey(context, configuration.getTableName(), childNode)
                );
                case NodeNameConstant.DOMAIN_OBJECT_RENAMING_RULE ->
                    configuration.setDomainObjectRenamingRule(parseDomainObjectRenamingRule(childNode));
                case NodeNameConstant.DOMAIN_COLUMN_RENAMING_RULE ->
                    configuration.setColumnRenamingRule(parseColumnRenamingRule(childNode));
                default -> throw new IllegalStateException("Unexpected value: " + childNode.getNodeName());
            }
        }
    }

    private TableConfiguration tableConfiguration(final Context context, final Node node) {
        TableConfiguration configuration = new TableConfiguration(context);

        context.addTableConfiguration(configuration);

        final Properties attributes = parseAttributes(node);

        applyHasValue(attributes.getProperty("catalog"), configuration::setCatalog);

        applyHasValue(attributes.getProperty("schema"), configuration::setSchema);

        applyHasValue(attributes.getProperty("tableName"), configuration::setTableName);

        applyHasValue(attributes.getProperty("domainObjectName"), configuration::setDomainObjectName);

        applyHasValue(attributes.getProperty("alias"), configuration::setAlias);

        applyHasValue(
            attributes.getProperty("enableInsert"),
            StringUtility::isTrue,
            configuration::setInsertStatementEnabled
        );

        applyHasValue(
            attributes.getProperty("enableSelectByPrimaryKey"),
            StringUtility::isTrue,
            configuration::setSelectByPrimaryKeyStatementEnabled
        );

        applyHasValue(
            attributes.getProperty("enableSelectByExample"),
            StringUtility::isTrue,
            configuration::setSelectByExampleStatementEnabled
        );

        applyHasValue(
            attributes.getProperty("enableUpdateByPrimaryKey"),
            StringUtility::isTrue,
            configuration::setUpdateByPrimaryKeyStatementEnabled
        );

        applyHasValue(
            attributes.getProperty("enableDeleteByPrimaryKey"),
            StringUtility::isTrue,
            configuration::setDeleteByPrimaryKeyStatementEnabled
        );

        applyHasValue(
            attributes.getProperty("enableDeleteByExample"),
            StringUtility::isTrue,
            configuration::setDeleteByExampleStatementEnabled
        );

        applyHasValue(
            attributes.getProperty("enableCountByExample"),
            StringUtility::isTrue,
            configuration::setCountByExampleStatementEnabled
        );

        applyHasValue(
            attributes.getProperty("enableUpdateByExample"),
            StringUtility::isTrue,
            configuration::setUpdateByExampleStatementEnabled
        );

        applyHasValue(
            attributes.getProperty("selectByPrimaryKeyQueryId"),
            configuration::setSelectByPrimaryKeyQueryId
        );

        applyHasValue(
            attributes.getProperty("selectByExampleQueryId"),
            configuration::setSelectByExampleQueryId
        );

        applyHasValue(attributes.getProperty("modelType"), configuration::setConfiguredModelType);

        applyHasValue(
            attributes.getProperty("escapeWildcards"),
            StringUtility::isTrue,
            configuration::setWildcardEscapingEnabled
        );

        applyHasValue(
            attributes.getProperty("delimitIdentifiers"),
            StringUtility::isTrue,
            configuration::setDelimitIdentifiers
        );

        applyHasValue(
            attributes.getProperty("delimitAllColumns"),
            StringUtility::isTrue,
            configuration::setAllColumnDelimitingEnabled
        );

        applyHasValue(attributes.getProperty("mapperName"), configuration::setMapperName);

        applyHasValue(attributes.getProperty("sqlProviderName"), configuration::setSqlProviderName);

        return configuration;
    }

    private void applyHasValue(final String value, final Consumer<String> consumer) {
        if (stringHasValue(value)) {
            consumer.accept(value);
        }
    }

    private <R> void applyHasValue(final String value, final Function<String, R> function, final Consumer<R> consumer) {
        if (stringHasValue(value)) {
            consumer.accept(function.apply(value));
        }
    }

    private ColumnOverride parseColumnOverride(final Node node) {
        final Properties attributes = parseAttributes(node);
        final String column = attributes.getProperty(AttributesConstant.COLUMN);

        ColumnOverride columnOverride = new ColumnOverride(column);

        applyHasValue(
            attributes.getProperty(AttributesConstant.PROPERTY),
            columnOverride::setJavaProperty
        );

        applyHasValue(
            attributes.getProperty(AttributesConstant.JAVA_TYPE),
            columnOverride::setJavaType
        );

        applyHasValue(
            attributes.getProperty(AttributesConstant.JDBC_TYPE),
            columnOverride::setJdbcType
        );

        applyHasValue(
            attributes.getProperty(AttributesConstant.TYPE_HANDLER),
            columnOverride::setTypeHandler
        );

        applyHasValue(
            attributes.getProperty(AttributesConstant.DELIMITED_COLUMN_NAME),
            StringUtility::isTrue,
            columnOverride::setColumnNameDelimited
        );

        applyHasValue(
            attributes.getProperty(AttributesConstant.IS_GENERATED_ALWAYS),
            Boolean::parseBoolean,
            columnOverride::setGeneratedAlways
        );

        final NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node childNode = nodeList.item(i);

            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            if (NodeNameConstant.PROPERTY.equals(childNode.getNodeName())) {
                parseProperty(columnOverride, childNode);
            }
        }

        return columnOverride;
    }

    private IgnoredColumn parseIgnoreColumn(final Node node) {
        final Properties attributes = parseAttributes(node);
        final String column = attributes.getProperty(AttributesConstant.COLUMN);

        IgnoredColumn ignoredColumn = new IgnoredColumn(column);

        applyHasValue(
            attributes.getProperty(AttributesConstant.DELIMITED_COLUMN_NAME),
            StringUtility::isTrue,
            ignoredColumn::setColumnNameDelimited
        );

        return ignoredColumn;
    }


    private IgnoredColumnPattern parseIgnoreColumnByRegex(final Node node) {
        final Properties attributes = parseAttributes(node);
        final String pattern = attributes.getProperty(AttributesConstant.PATTERN);

        IgnoredColumnPattern columnPattern = new IgnoredColumnPattern(pattern);

        final NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node childNode = nodeList.item(i);

            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            if (NodeNameConstant.EXCEPT.equals(childNode.getNodeName())) {
                columnPattern.addException(parseException(childNode));
            }
        }

        return columnPattern;
    }

    private DomainObjectRenamingRule parseDomainObjectRenamingRule(final Node node) {
        final Properties attributes = parseAttributes(node);
        final String searchString = attributes.getProperty(AttributesConstant.SEARCH_STRING);

        DomainObjectRenamingRule rule = new DomainObjectRenamingRule();

        rule.setSearchString(searchString);

        applyHasValue(attributes.getProperty(AttributesConstant.REPLACE_STRING), rule::setReplaceString);

        return rule;
    }

    private GeneratedKey parseGeneratedKey(final Context context, final String tableName, final Node node) {
        final Properties attributes = parseAttributes(node);

        final String column = resolveGeneratedKeyColumnName(context, tableName, attributes.getProperty("column"));
        final boolean identity = isTrue(attributes.getProperty(AttributesConstant.IDENTITY));
        final String sqlStatement = attributes.getProperty(AttributesConstant.SQL_STATEMENT);
        final String type = attributes.getProperty(AttributesConstant.TYPE);

        return new GeneratedKey(column, sqlStatement, identity, type);
    }

    private ColumnRenamingRule parseColumnRenamingRule(final Node node) {
        final Properties attributes = parseAttributes(node);
        final String searchString = attributes.getProperty(AttributesConstant.SEARCH_STRING);

        ColumnRenamingRule rule = new ColumnRenamingRule();

        rule.setSearchString(searchString);

        applyHasValue(attributes.getProperty(AttributesConstant.REPLACE_STRING), rule::setReplaceString);

        return rule;
    }

    private IgnoredColumnException parseException(final Node node) {
        final Properties attributes = parseAttributes(node);
        final String column = attributes.getProperty(AttributesConstant.COLUMN);

        IgnoredColumnException exception = new IgnoredColumnException(column);

        applyHasValue(
            attributes.getProperty(AttributesConstant.DELIMITED_COLUMN_NAME),
            StringUtility::isTrue,
            exception::setColumnNameDelimited
        );

        return exception;
    }

    @SneakyThrows
    private String resolveGeneratedKeyColumnName(final Context context,
                                                 final String tableName,
                                                 final String columnName) {
        return AttributesConstant.GENERATE_KEY_PRIMARY_KEY.equals(columnName) ?
            GenerateKeyColumnNameResolver.build().resolve(context, tableName) : columnName;
    }
}
