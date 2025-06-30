package io.github.mitsumi.solutions.mybatis.generator.postgres.dynamic.sql;

import lombok.NoArgsConstructor;
import org.mybatis.generator.codegen.AbstractJavaClientGenerator;
import org.mybatis.generator.runtime.dynamic.sql.IntrospectedTableMyBatis3DynamicSqlImpl;

import java.util.Objects;

@SuppressWarnings("PMD.CommentRequired")
@NoArgsConstructor
public class IntrospectedTableMyBatis3PostgresDynamicSqlImpl extends IntrospectedTableMyBatis3DynamicSqlImpl {

    @Override
    protected AbstractJavaClientGenerator createJavaClientGenerator() {
        return Objects.isNull(context.getJavaClientGeneratorConfiguration()) ?
            null : new PostgresDynamicSqlMapperGenerator(getClientProject());
    }

}
