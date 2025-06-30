package io.github.mitsumi.solutions.mybatis.generator.postgres.dynamic.sql.elements;

import io.github.mitsumi.solutions.mybatis.generator.postgres.resolvers.GenerateKeyResolver;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.config.GeneratedKey;
import org.mybatis.generator.runtime.dynamic.sql.elements.AbstractMethodGenerator;
import org.mybatis.generator.runtime.dynamic.sql.elements.MethodAndImports;
import org.mybatis.generator.runtime.dynamic.sql.elements.MethodParts;
import org.mybatis.generator.runtime.dynamic.sql.elements.Utils;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings({"PMD.LocalVariableCouldBeFinal", "PMD.CommentRequired", "PMD.LongVariable", "PMD.LawOfDemeter"})
public class PostgresBasicMultipleInsertMethodGenerator extends AbstractMethodGenerator {
    private final FullyQualifiedJavaType recordType;
    private final GenerateKeyResolver generateKeyResolver;

    protected PostgresBasicMultipleInsertMethodGenerator(final Builder builder) {
        super(builder);

        this.recordType = builder.recordType;
        this.generateKeyResolver = GenerateKeyResolver.build();
    }

    @Override
    public MethodAndImports generateMethodAndImports() {
        return Utils.generateMultipleRowInsert(introspectedTable) ?
            introspectedTable.getGeneratedKey().map(this::generateMethodWithGeneratedKeys).orElse(null) :
            null;
    }

    private MethodAndImports generateMethodWithGeneratedKeys(final GeneratedKey generatedKey) {
        Set<FullyQualifiedJavaType> imports = new HashSet<>();

        imports.add(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.util.SqlProviderAdapter")); //$NON-NLS-1$)
        imports.add(new FullyQualifiedJavaType("org.apache.ibatis.annotations.InsertProvider")); //$NON-NLS-1$
        imports.add(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Param")); //$NON-NLS-1$

        Parameter parm1 = new Parameter(FullyQualifiedJavaType.getStringInstance(), "insertStatement"); //$NON-NLS-1$
        parm1.addAnnotation("@Param(\"insertStatement\")"); //$NON-NLS-1$

        FullyQualifiedJavaType recordListType = FullyQualifiedJavaType.getNewListInstance();
        recordListType.addTypeArgument(recordType);
        imports.add(recordListType);

        Parameter parm2 = new Parameter(recordListType, "records"); //$NON-NLS-1$
        parm2.addAnnotation("@Param(\"records\")"); //$NON-NLS-1$

        Method method = new Method("insertMultiple"); //$NON-NLS-1$
        method.setAbstract(true);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.addParameter(parm1);
        method.addParameter(parm2);
        context.getCommentGenerator().addGeneralMethodAnnotation(method, introspectedTable, imports);
        method.addAnnotation("@InsertProvider(type=SqlProviderAdapter.class, " //$NON-NLS-1$
                             + "method=\"insertMultipleWithGeneratedKeys\")"); //$NON-NLS-1$

        MethodAndImports.Builder builder = MethodAndImports.withMethod(method)
            .withImports(imports);

        MethodParts methodParts = getGeneratedKeyAnnotation(generatedKey);
        acceptParts(builder, method, methodParts);

        return builder.build();
    }

    private MethodParts getGeneratedKeyAnnotation(final GeneratedKey originalGeneratedKey) {
        MethodParts.Builder builder = new MethodParts.Builder();

        final GeneratedKey generatedKey = generateKeyResolver.resolve(introspectedTable, originalGeneratedKey);
        introspectedTable.getColumn(generatedKey.getColumn()).ifPresent(introspectedColumn -> {
            if (generatedKey.isJdbcStandard()) {
                // only jdbc standard keys are supported for multiple insert
                builder.withImport(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Options"));
                final String options = "@Options(useGeneratedKeys=true,keyProperty=\"records." +
                                       introspectedColumn.getJavaProperty() + "\")";
                builder.withAnnotation(options);
            }
        });

        return builder.build();
    }

    @Override
    public boolean callPlugins(final Method method, final Interface interfaze) {
        return context.getPlugins().clientBasicInsertMultipleMethodGenerated(method, interfaze, introspectedTable);
    }

    public static class Builder extends BaseBuilder<Builder> {

        private FullyQualifiedJavaType recordType;

        public Builder withRecordType(final FullyQualifiedJavaType recordType) {
            this.recordType = recordType;
            return this;
        }

        @Override
        public Builder getThis() {
            return this;
        }

        public PostgresBasicMultipleInsertMethodGenerator build() {
            return new PostgresBasicMultipleInsertMethodGenerator(this);
        }
    }
}
