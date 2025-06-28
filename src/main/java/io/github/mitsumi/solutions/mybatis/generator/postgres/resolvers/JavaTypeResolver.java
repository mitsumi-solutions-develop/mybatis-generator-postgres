package io.github.mitsumi.solutions.mybatis.generator.postgres.resolvers;

import io.github.mitsumi.solutions.mybatis.generator.postgres.resolvers.constants.PostgresTypeName;
import io.github.mitsumi.solutions.mybatis.generator.postgres.resolvers.constants.PropertyName;
import io.github.mitsumi.solutions.shared.utils.StringCaseUtils;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

import java.util.Locale;
import java.util.Properties;
import java.util.UUID;

@NoArgsConstructor
@SuppressWarnings({"PMD.LongVariable", "PMD.UseExplicitTypes", "PMD.CommentRequired"})
public class JavaTypeResolver extends JavaTypeResolverDefaultImpl {

    private String jsonModelsPackage;
    private String enumerationsPackage;

    @Override
    public FullyQualifiedJavaType calculateJavaType(final IntrospectedColumn column) {

        final var typeName = column.getActualTypeName().toLowerCase(Locale.ENGLISH);

        return switch (typeName) {
            case PostgresTypeName.JSONB -> new FullyQualifiedJavaType(jsonTypeClassName(column));
            case PostgresTypeName.UUID -> new FullyQualifiedJavaType(UUID.class.getName());
            default -> {
                if (typeName.endsWith(PostgresTypeName.ENUM)) {
                    yield new FullyQualifiedJavaType(enumTypeClassName(column));
                }
                yield super.calculateJavaType(column);
            }
        };
    }

    @Override
    public void addConfigurationProperties(final Properties properties) {
        super.addConfigurationProperties(properties);

        jsonModelsPackage = properties.getProperty(PropertyName.JSON_MODELS_PACKAGE);
        enumerationsPackage = properties.getProperty(PropertyName.ENUMERATIONS_PACKAGE);
    }

    @Override
    protected FullyQualifiedJavaType calculateTimeType(final IntrospectedColumn column,
                                                       final FullyQualifiedJavaType defaultType) {
        return useJSR310Types ? new FullyQualifiedJavaType("java.time.OffsetTime") : defaultType;
    }

    @Override
    protected FullyQualifiedJavaType calculateTimestampType(final IntrospectedColumn column,
                                                            final FullyQualifiedJavaType defaultType) {
        return useJSR310Types ?
            new FullyQualifiedJavaType("java.time.OffsetDateTime") : defaultType;
    }

    protected String jsonTypeClassName(final IntrospectedColumn column) {
        final var className = StringCaseUtils.snakeToCamel(column.getActualColumnName(), false);
        return StringUtils.isEmpty(jsonModelsPackage) ? className : jsonModelsPackage + "." + className;
    }

    protected String enumTypeClassName(final IntrospectedColumn column) {
        final var className = StringCaseUtils.snakeToCamel(
            column.getActualTypeName().toLowerCase(Locale.ENGLISH),
            false
        );
        return StringUtils.isEmpty(enumerationsPackage) ? className : enumerationsPackage + "." + className;
    }
}
