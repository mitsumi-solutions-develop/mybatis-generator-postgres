package io.github.mitsumi.solutions.mybatis.generator.postgres.resolvers;

import io.github.mitsumi.solutions.mybatis.generator.postgres.config.parsers.constants.AttributesConstant;
import lombok.NoArgsConstructor;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.config.GeneratedKey;

import java.util.List;
import java.util.Optional;

@SuppressWarnings({"PMD.CommentRequired", "PMD.LongVariable"})
@NoArgsConstructor(staticName = "build")
public class GenerateKeyResolver {

    public GeneratedKey resolve(final IntrospectedTable introspectedTable, final GeneratedKey originalGeneratedKey) {
        return AttributesConstant.GENERATE_KEY_PRIMARY_KEY.equals(originalGeneratedKey.getColumn()) ?
            generatedKey(introspectedTable, originalGeneratedKey) : originalGeneratedKey;
    }

    private GeneratedKey generatedKey(final IntrospectedTable introspectedTable,
                                      final GeneratedKey originalGeneratedKey) {
        return new GeneratedKey(
            columnName(introspectedTable, originalGeneratedKey.getColumn()),
            originalGeneratedKey.getRuntimeSqlStatement(),
            originalGeneratedKey.isIdentity(),
            null
        );
    }

    private String columnName(final IntrospectedTable introspectedTable, final String columnName) {
        return Optional.ofNullable(introspectedTable.getPrimaryKeyColumns())
            .map(List::getFirst).map(IntrospectedColumn::getActualColumnName)
            .orElse(columnName);
    }
}
