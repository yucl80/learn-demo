package sql;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.util.TablesNamesFinder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestJsqlParser {
    public static void main(String[] args) throws JSQLParserException {
        {
            Select stmt = (Select) CCJSqlParserUtil.parse("SELECT col1 AS a, col2 AS b, col3 AS c FROM table WHERE col1 = 10 AND col2 = 20 AND col3 = 30");

            Map<String, Expression> map = new HashMap<>();
            for (SelectItem selectItem : ((PlainSelect) stmt.getSelectBody()).getSelectItems()) {
                selectItem.accept(new SelectItemVisitorAdapter() {
                    @Override
                    public void visit(SelectExpressionItem item) {
                        map.put(item.getAlias().getName(), item.getExpression());
                    }
                });
            }

            System.out.println("map " + map);
        }
        {
            Statement statement = CCJSqlParserUtil.parse("SELECT * FROM MY_TABLE1");
            Select selectStatement = (Select) statement;
            TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
            List<String> tableList = tablesNamesFinder.getTableList(selectStatement);
        }
    }
}
