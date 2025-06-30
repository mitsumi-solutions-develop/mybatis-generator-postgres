package io.github.mitsumi.solutions.mybatis.generator.postgres.dynamic.sql;

import io.github.mitsumi.solutions.mybatis.generator.postgres.dynamic.sql.elements.PostgresBasicInsertMethodGenerator;
import io.github.mitsumi.solutions.mybatis.generator.postgres.dynamic.sql.elements.PostgresBasicMultipleInsertMethodGenerator;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.runtime.dynamic.sql.DynamicSqlMapperGenerator;

@SuppressWarnings("PMD.CommentRequired")
public class PostgresDynamicSqlMapperGenerator extends DynamicSqlMapperGenerator {
    public PostgresDynamicSqlMapperGenerator(final String project) {
        super(project);
    }

    @Override
    @SuppressWarnings("PMD.LocalVariableCouldBeFinal")
    protected void addBasicInsertMethod(final Interface interfaze) {
        final PostgresBasicInsertMethodGenerator generator = new PostgresBasicInsertMethodGenerator.Builder()
            .withContext(context)
            .withFragmentGenerator(fragmentGenerator)
            .withIntrospectedTable(introspectedTable)
            .withTableFieldName(tableFieldName)
            .withRecordType(recordType)
            .build();

        generate(interfaze, generator);
    }

    @Override
    protected void addBasicInsertMultipleMethod(final Interface interfaze) {
        final PostgresBasicMultipleInsertMethodGenerator generator = new PostgresBasicMultipleInsertMethodGenerator
            .Builder()
            .withContext(context)
            .withIntrospectedTable(introspectedTable)
            .withTableFieldName(tableFieldName)
            .withRecordType(recordType)
            .build();

        generate(interfaze, generator);
    }

}
