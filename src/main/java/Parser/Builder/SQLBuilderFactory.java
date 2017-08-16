package Parser.Builder;

import Parser.AstGen.SQLCreateTabAST;
import Parser.Builder.impl.*;

import javax.swing.plaf.PanelUI;

/**
 * Created by ruanxin on 2017/8/16.
 */
public class SQLBuilderFactory {
    private final static SQLBuilderFactory sbf = new SQLBuilderFactory();
    private SQLBuilderFactory(){}
    public static SQLBuilderFactory getSQLBuilderFactory() {
        return sbf;
    }

    public static SQLCreateTabBuilder createCrtTabBuilder() {
        return new SQLCreateTabBuilderImpl();
    }

    public static SQLCreateDbBuilder createDbBuilder() {
        return new SQLCreateDbBuilderImpl();
    }

    public static SQLShowDbBuilder createShowBuilder() {
        return new SQLShowDbBuilderImpl();
    }

    public static SQLInsertBuilder createInsertBuilder() {
        return new SQLInsertBuilderImpl();
    }

    public static SQLSelectBuilder createSelectBuilder(){
        return new SQLSelectBuilderImpl();
    }

    public static SQLUpdateBuilder createUpdateBuilder() {
        return new SQLUpdateBuilderImpl();
    }

    public static SQLUseDbBuilder createUseDbBuilder() {
        return new SQLUseDbBuilderImpl();
    }

    public static SQLDeleteBuilder createDeleteBuilder() {
        return new SQLDeleteBuilderImpl();
    }
}
