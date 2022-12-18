package sql;

/**
 * @author WJP
 * @Date: 2021/10/27 15:42
 */
public class TestSqlString {
    static String customSql = "SELECT id,username,email,`password`\n" +
            "FROM `student` a\n" +
            "WHERE a.id = 2\n" +
            "UNION ALL\n" +
            "SELECT id,username,email,`password`\n" +
            "FROM `student` b\n" +
            "WHERE b.id = 5";

    static String customSql1 = "SELECT\n" +
            "\ta.*,b.*\n" +
            "FROM\n" +
            "\t`student` a\n" +
            "JOIN `user_top` b\n" +
            "on a.id = b.id";

}

