package io.github.mitsumi.solutions.mybatis.generator.postgres.dynamic.sql.elements;

import io.github.mitsumi.solutions.mybatis.generator.postgres.resolvers.GenerateKeyResolver;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.config.GeneratedKey;
import org.mybatis.generator.runtime.dynamic.sql.elements.AbstractMethodGenerator;
import org.mybatis.generator.runtime.dynamic.sql.elements.FragmentGenerator;
import org.mybatis.generator.runtime.dynamic.sql.elements.MethodAndImports;
import org.mybatis.generator.runtime.dynamic.sql.elements.MethodParts;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings({"PMD.LawOfDemeter", "PMD.CommentRequired", "PMD.LongVariable"})
public final class PostgresBasicInsertMethodGenerator extends AbstractMethodGenerator {
    private final FullyQualifiedJavaType recordType;
    private final FragmentGenerator fragmentGenerator;
    private final GenerateKeyResolver generateKeyResolver;

    private PostgresBasicInsertMethodGenerator(final Builder builder) {
        super(builder);
        this.recordType = builder.recordType;
        this.fragmentGenerator = builder.fragmentGenerator;
        this.generateKeyResolver = GenerateKeyResolver.build();
    }

    @SuppressWarnings("PMD.LocalVariableCouldBeFinal")
    @Override
    public MethodAndImports generateMethodAndImports() {
        Set<FullyQualifiedJavaType> imports = new HashSet<>(Set.of(
            javaType("org.mybatis.dynamic.sql.insert.render.InsertStatementProvider"),
            javaType("org.mybatis.dynamic.sql.util.SqlProviderAdapter"),
            javaType("org.apache.ibatis.annotations.InsertProvider"),
            recordType
        ));

        FullyQualifiedJavaType parameterType = javaType("org.mybatis.dynamic.sql.insert.render.InsertStatementProvider");
        parameterType.addTypeArgument(recordType);

        Method method = new Method("insert");
        method.setAbstract(true);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.addParameter(new Parameter(parameterType, "insertStatement"));
        context.getCommentGenerator().addGeneralMethodAnnotation(method, introspectedTable, imports);
        method.addAnnotation("@InsertProvider(type=SqlProviderAdapter.class, method=\"insert\")");

        final MethodAndImports.Builder builder = MethodAndImports.withMethod(method).withImports(imports);

        introspectedTable.getGeneratedKey().ifPresent(originalGeneratedKey -> {
            final GeneratedKey generatedKey = generateKeyResolver.resolve(introspectedTable, originalGeneratedKey);
            final MethodParts methodParts = fragmentGenerator.getGeneratedKeyAnnotation(generatedKey);
            acceptParts(builder, method, methodParts);
        });

        return builder.build();
    }

    private FullyQualifiedJavaType javaType(final String typeName) {
        return new FullyQualifiedJavaType(typeName);
    }

    @Override
    public boolean callPlugins(final Method method, final Interface interfaze) {
        return context.getPlugins().clientBasicInsertMethodGenerated(method, interfaze, introspectedTable);
    }


    public static class Builder extends BaseBuilder<Builder> {

        private FullyQualifiedJavaType recordType;
        private FragmentGenerator fragmentGenerator;

        public Builder withRecordType(final FullyQualifiedJavaType recordType) {
            this.recordType = recordType;
            return this;
        }

        public Builder withFragmentGenerator(final FragmentGenerator fragmentGenerator) {
            this.fragmentGenerator = fragmentGenerator;
            return this;
        }

        @Override
        public PostgresBasicInsertMethodGenerator.Builder getThis() {
            return this;
        }

        public PostgresBasicInsertMethodGenerator build() {
            return new PostgresBasicInsertMethodGenerator(this);
        }
    }
}
