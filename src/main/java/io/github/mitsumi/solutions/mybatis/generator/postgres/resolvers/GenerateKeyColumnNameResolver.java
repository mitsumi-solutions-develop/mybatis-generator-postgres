package io.github.mitsumi.solutions.mybatis.generator.postgres.resolvers;

import lombok.NoArgsConstructor;
import org.mybatis.generator.config.Context;

import java.sql.SQLException;

@NoArgsConstructor(staticName = "build")
@SuppressWarnings("PMD.CommentRequired")
public class GenerateKeyColumnNameResolver {

    @SuppressWarnings("SqlNoDataSourceInspection")
    private static final String SQL = """
        SELECT
            ccu.column_name as COLUMN_NAME
        FROM
            information_schema.table_constraints tc
            INNER JOIN information_schema.constraint_column_usage ccu ON (
                tc.table_catalog = ccu.table_catalog
                and tc.table_schema = ccu.table_schema
                and tc.table_name = ccu.table_name
                and tc.constraint_name = ccu.constraint_name
            )
        WHERE
            tc.table_name = ?
            and tc.constraint_type = 'PRIMARY KEY'
        """;

    @SuppressWarnings({"PMD.UseExplicitTypes", "PMD.LawOfDemeter"})
    public String resolve(final Context context, final String tableName) throws SQLException {

        try (var connection = context.getConnection();
             var statement = connection.prepareStatement(SQL)) {

            statement.setString(1, tableName);

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString(1);
                }
            }
        }

        throw new SQLException("Cannot resolve primary key. tableName: " + tableName);
    }
}
