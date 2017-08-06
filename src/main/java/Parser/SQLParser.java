package Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import Exception.SytaxErrorsException;
import com.sun.org.apache.bcel.internal.generic.FADD;
import org.omg.PortableServer.POA;

/**
 * Created by Roy on 2017/7/23.
 */
public class SQLParser {
    private Lexer lexer;
    private AST ast;
    private List<Token> tokens;
    private int parent_match = 0;
    private final byte ICMask = (byte) 0xDF;

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public void setAst(AST ast) {
        this.ast = ast;
    }
    public AST getAst(AST ast) {
        return ast;
    }

    public void setLexer(Lexer lexer) {
        this.lexer = lexer;
    }

    public Lexer getLexer() {
        return lexer;
    }
    public class SavePoint {
        int pos;
        boolean correct;

        public SavePoint(int pos, boolean correct) {
            this.correct = correct;
            this.pos = pos;
        }
    }
    public SavePoint sql(int pos) throws Exception {
        ASTNode root = new ASTNode(true, false, "sql");
        ast.addRootNode(root);

        Token token = getToken(pos);
        String value = token.getValue().toUpperCase();
        if (value.equals("CREATE")) {
            ASTNode ddl = new ASTNode(false,false,"DDL");
            root.addChildNode(ddl);

            return DDL(pos,ddl);
        } else if (value.equals("SELECT")){
            ASTNode dql = new ASTNode(false, false, "dql");
            root.addChildNode(dql);

            return DQL(pos, dql);
        } else if (value.equals("INSERT") || value.equals("UPDATE") || value.equals("DELETE")) {
            ASTNode dml = new ASTNode(false, false, "dml");
            root.addChildNode(dml);

            return DML(pos, dml);
        }
        return null;
    }
    public SavePoint DML(int pos, ASTNode astNode) throws Exception {
        Token token = getToken(pos++);
        switch (token.getSortCode()) {
            case INSERT:
                //add insert to ast
                ASTNode insert_node = new ASTNode(false, true, token.getValue());
                astNode.addChildNode(insert_node);

                token = getToken(pos++);
                if (token.getSortCode() == SortCode.INTO) {
                    ASTNode into_node = new ASTNode(false, true, token.getValue());
                    astNode.addChildNode(into_node);

                    token = getToken(pos++);
                    if (token.getSortCode() == SortCode.IDENTIFIED) {
                        ASTNode id_node = new ASTNode(false, true, token.getValue());
                        astNode.addChildNode(id_node);

                        /** default implement
                         * Single row insert
                         INSERT INTO table VALUES (value1, [value2, ... ])

                         Multirow insert
                         INSERT INTO tablename VALUES ('value-1a', ['value-1b', ...]),
                         ('value-2a', ['value-2b', ...]),
                         ...
                         **/
                        return values_single_mutl(pos, astNode);
                    }
                         /**none default implement
                         * Single row insert
                         INSERT INTO table (column1 [, column2, column3 ... ]) VALUES (value1 [, value2, value3 ... ])
                         Multirow insert
                         INSERT INTO tablename (column-a, [column-b, ...])
                         VALUES ('value-1a', ['value-1b', ...]),
                         ('value-2a', ['value-2b', ...]),
                         ...
                         */
                        else if (token.getSortCode() == SortCode.LPARENT) {
                            //column,非缺省
                            ASTNode lp = new ASTNode(false, true, token.getValue());
                            astNode.addChildNode(lp);

                            ASTNode column_list = new ASTNode(false ,false, "insert_colum_list");
                            astNode.addChildNode(column_list);

                            SavePoint sp = ParamsList(pos, column_list);

                            pos = sp.pos;
                            if (sp.correct) {
                                token = getToken(pos++);
                                if (token.getSortCode() == SortCode.RPARENT) {
                                    ASTNode rp = new ASTNode(false, true, token.getValue());
                                    astNode.addChildNode(rp);

                                    return values_single_mutl(pos, astNode);
                                }
                            }
                        }
                    }
                break;
            case UPDATE:
                break;
            case DELETE:
                break;
            default:
                break;
        }
        return new SavePoint(pos, false);
    }

    /**
     * 解析insert中values单项插入和多项插入
     * @param pos
     * @param astNode
     * @return
     * @throws Exception
     */
    private SavePoint values_single_mutl(int pos, ASTNode astNode) throws Exception {
        Token token = getToken(pos++);
        if (token.getSortCode() == SortCode.VALUES) {
            ASTNode value_node = new ASTNode(false, true, token.getValue());
            astNode.addChildNode(value_node);

            token = getToken(pos++);
            for (;;) {
                if (token.getSortCode() == SortCode.LPARENT) {
                    ASTNode lp = new ASTNode(false, true, token.getValue());
                    astNode.addChildNode(lp);

                    ASTNode valuelist_node = new ASTNode(false, true, "values_list");
                    astNode.addChildNode(valuelist_node);

                    SavePoint sp = ParamsList(pos, valuelist_node);

                    pos = sp.pos;
                    if (sp.correct) {
                        token = getToken(pos++);
                        if (token.getSortCode() == SortCode.RPARENT) {
                            ASTNode rp = new ASTNode(false, true, token.getValue());
                            astNode.addChildNode(rp);

                            token = getToken(pos);
                            if (token.getSortCode() == SortCode.SEMICOLON) {
                                //以分号;结束insert语句
                                ASTNode sem = new ASTNode(false, true, token.getValue());
                                astNode.addChildNode(sem);

                                return new SavePoint(pos, false);
                            }else if (token.getSortCode() == SortCode.COMMA) {
                                //,缺省多行插入
                                ASTNode comma_node = new ASTNode(false, true, token.getValue());
                                astNode.addChildNode(comma_node);

                                token = getToken(++pos);
                                continue;
                            }
                        }
                    }
                }
                return new SavePoint(pos, false);
            }
        }
        return new SavePoint(pos,false);
    }
    public SavePoint DQL(int pos, ASTNode astNode) throws Exception {
        Token token = getToken(pos++);

        //add select to ast
        ASTNode select_node = new ASTNode(false, true, token.getValue());
        astNode.addChildNode(select_node);

        ASTNode select_list = new ASTNode(false, true, "select_list");
        astNode.addChildNode(select_list);

        SavePoint sp_slist = select_list(pos, select_list);
        if (sp_slist.correct) {
            pos = sp_slist.pos;
            token = getToken(pos++);
            if (token.getSortCode() == SortCode.FROM) {
                ASTNode from_node = new ASTNode(false, true, token.getValue());
                astNode.addChildNode(from_node);

                token = getToken(pos++);
                //table name
                if (token.getSortCode() == SortCode.IDENTIFIED) {
                    ASTNode table_node = new ASTNode(false, true, token.getValue());
                    astNode.addChildNode(table_node);

                    token = getToken(pos);
                    switch (token.getSortCode()) {
                        case WHERE:
                            return where_condition(pos,astNode);
                        case GROUP:
                            return group_condition(pos, astNode);
                        case HAVING:
                            return having_condition(pos, astNode);
                        case ORDER:
                            return group_condition(pos, astNode);
                        case SEMICOLON:
                            //直接分号结束
                            ASTNode sem_node = new ASTNode(false, true, token.getValue());
                            astNode.addChildNode(sem_node);

                            return new SavePoint(pos, true);
                        default:
                            return new SavePoint(pos, false);
                    }
                }
            }
        }
        //token
        return new SavePoint(pos, false);
    }
    //可选表达式 where
    public SavePoint where_condition(int pos, ASTNode astNode) throws Exception {
        Token token = getToken(pos++);
        if (token.getSortCode() == SortCode.WHERE) {
            ASTNode where_node = new ASTNode(false, true, token.getValue());
            astNode.addChildNode(where_node);

            //where后的限制条件
            ASTNode search_node = new ASTNode(false, false, "search_condition");
            astNode.addChildNode(search_node);

            SavePoint sp = CheckList(pos, search_node);

            pos = sp.pos;
            if (sp.correct) {
                token = getToken(pos);

                if (token.getSortCode() == SortCode.GROUP) {
                    //where -> group
                    SavePoint g_sp = group_condition(pos, astNode);
                    return g_sp;
                } else if (token.getSortCode() == SortCode.ORDER) {
                    //where -> order
                    SavePoint o_sp = order_condition(pos, astNode);
                    return o_sp;
                }else if (token.getSortCode() == SortCode.SEMICOLON) {
                    ASTNode sem_node = new ASTNode(false, true, token.getValue());
                    astNode.addChildNode(sem_node);

                    return new SavePoint(pos, true);
                }
            }
        }
        return new SavePoint(pos, false);
    }

    //可选表达式 group by
    public SavePoint group_condition(int pos, ASTNode astNode) throws Exception {
        Token token = getToken(pos++);
        if (token.getSortCode() == SortCode.GROUP) {
            ASTNode g_node = new ASTNode(false, true, token.getValue());
            astNode.addChildNode(g_node);

            token = getToken(pos++);
            if (token.getSortCode() == SortCode.BY) {
                ASTNode b_node = new ASTNode(false, true, token.getValue());
                astNode.addChildNode(b_node);

                ASTNode group = new ASTNode(false, true, "group_by list");
                astNode.addChildNode(group);

                SavePoint sp = ParamsList(pos, group);

                pos = sp.pos;
                if (sp.correct) {
                    token = getToken(pos);

                    if (token.getSortCode() == SortCode.HAVING) {
                        // group by -> having
                        SavePoint h_sp = having_condition(pos, astNode);
                        return h_sp;
                    }else if (token.getSortCode() == SortCode.ORDER) {
                        //group by -> order
                        SavePoint o_sp = order_condition(pos, astNode);
                        return o_sp;
                    }else if (token.getSortCode() == SortCode.SEMICOLON) {
                        //group by....;结束
                        ASTNode sem_node = new ASTNode(false, true, token.getValue());
                        astNode.addChildNode(sem_node);

                        return new SavePoint(pos, true);
                    }
                }
            }
        }
        return new SavePoint(pos, false);
    }

    public SavePoint order_condition(int pos, ASTNode astNode) throws Exception {
        Token token = getToken(pos++);
        if (token.getSortCode() == SortCode.ORDER) {
            ASTNode or_node = new ASTNode(false, true, token.getValue());
            astNode.addChildNode(or_node);

            token = getToken(pos++);
            if (token.getSortCode() == SortCode.BY) {
                ASTNode by_node = new ASTNode(false, true, token.getValue());
                astNode.addChildNode(by_node);

                token = getToken(pos++);
                if (token.getSortCode() == SortCode.ASC) {
                    //升序
                    ASTNode asc_node = new ASTNode(false, true, token.getValue());
                    astNode.addChildNode(asc_node);
                }else if (token.getSortCode() == SortCode.DESC){
                    //降序
                    ASTNode desc_node = new ASTNode(false, true, token.getValue());
                    astNode.addChildNode(desc_node);
                }else {
                    return new SavePoint(pos, false);
                }
                token = getToken(pos);
                if (token.getSortCode() == SortCode.SEMICOLON) {
                    //order by 以;结束
                    ASTNode sem_node = new ASTNode(false, true, token.getValue());
                    astNode.addChildNode(sem_node);

                    return new SavePoint(pos, true);
                }
            }
        }
        return new SavePoint(pos, false);
    }

    public SavePoint having_condition(int pos, ASTNode astNode) throws Exception {
        Token token = getToken(pos++);
        if (token.getSortCode() == SortCode.HAVING) {
            ASTNode having_node = new ASTNode(false,  true, token.getValue());
            astNode.addChildNode(having_node);

            ASTNode condition_node = new ASTNode(false, false, "having_search_condition");
            astNode.addChildNode(condition_node);

            SavePoint sp = having_search_condition(pos, condition_node);

            pos = sp.pos;
            if (sp.correct) {
                token = getToken(pos);
                if (token.getSortCode() == SortCode.SEMICOLON) {
                    // having 以;分号结束
                    ASTNode sem_node = new ASTNode(false ,true, token.getValue());
                    astNode.addChildNode(sem_node);

                    return new SavePoint(pos, true);
                }else if (token.getSortCode() == SortCode.ORDER) {
                    SavePoint o_node = order_condition(pos, astNode);
                    return o_node;
                }
            }
        }
        return new SavePoint(pos, false);
    }

    public SavePoint having_search_condition(int pos, ASTNode astNode) throws Exception {
        for (;;) {
            //op 左边
            SavePoint left_sp = having_left_op_right(pos, astNode);

            if (left_sp.correct) {
                pos = left_sp.pos;
            }else {
                return new SavePoint(pos, false);
            }
            Token token = getToken(pos++);
            //r op
            if (RelationOps.containValue(token.getValue())) {
                ASTNode op_node = new ASTNode(false, true, token.getValue());
                astNode.addChildNode(op_node);

                //op右边
                SavePoint right_sp = having_left_op_right(pos, astNode);
                if (right_sp.correct) {
                    pos = right_sp.pos;
                    token = getToken(pos++);

                    if (token.getSortCode() == SortCode.OR ||
                            token.getSortCode() == SortCode.AND) {
                        ASTNode oa_node = new ASTNode(false, true, token.getValue());
                        astNode.addChildNode(oa_node);

                        continue;
                    }else if (token.getSortCode() == SortCode.ORDER) {
                        //having ->order
                        return new SavePoint(--pos, true);
                    }else if (token.getSortCode() == SortCode.SEMICOLON) {
                        //;分号结束
                        return new SavePoint(--pos, true);
                    }
                }
            }
            return new SavePoint(pos, false);
        }
    }
    //having 的 op 的左边和右边表达式
    public SavePoint having_left_op_right(int pos, ASTNode astNode) throws Exception {
        Token token = getToken(pos++);
        switch (token.getSortCode()) {
            case COUNT:
            case MIN:
            case MAX:
            case AVG:
            case SUM:
                boolean flag = false;
                ASTNode count_node = new ASTNode(false, true, token.getValue());
                astNode.addChildNode(count_node);

                token = getToken(pos++);
                if (token.getSortCode() == SortCode.LPARENT) {
                    ASTNode lp = new ASTNode(false, true, token.getValue());
                    astNode.addChildNode(lp);

                    token = getToken(pos++);
                    if (token.getSortCode() == SortCode.IDENTIFIED) {
                        ASTNode id_node = new ASTNode(false, true, token.getValue());
                        astNode.addChildNode(id_node);

                        token = getToken(pos++);
                        if (token.getSortCode() == SortCode.RPARENT) {
                            ASTNode rp = new ASTNode(false, true, token.getValue());
                            astNode.addChildNode(rp);
                            flag = true;
                        }
                    }
                }
                //解析聚合函数错误
                if (!flag) {
                    return new SavePoint(pos, false);
                }
                //解析聚合函数正确
                break;
            default:
                //非聚合函数,column
                if (token.getSortCode() == SortCode.IDENTIFIED) {
                    ASTNode id_node = new ASTNode(false, true, token.getValue());
                    astNode.addChildNode(id_node);
                } else if (token.getSortCode() == SortCode.NUMBER) {
                    ASTNode num_node = new ASTNode(false, true, token.getValue());
                    astNode.addChildNode(num_node);
                } else if (token.getSortCode() == SortCode.STRING) {
                    ASTNode str_node = new ASTNode(false, true, token.getValue());
                    astNode.addChildNode(str_node);
                } else {
                    return new SavePoint(pos, false);
                }
                break;
        }
        return new SavePoint(pos, true);
    }
    //// TODO: 2017/8/5  select 可跟聚合函数
    public SavePoint select_list(int pos, ASTNode astNode) throws Exception {
        Token token = tokens.get(pos++);
        if (token.getSortCode() == SortCode.STAR) {
            ASTNode star_node = new ASTNode(false, true, token.getValue());
            astNode.addChildNode(star_node);

            return new SavePoint(pos, true);
        }else if (token.getSortCode() == SortCode.IDENTIFIED) {
            while (token.getSortCode() == SortCode.IDENTIFIED) {
                ASTNode id_node = new ASTNode(false, true, token.getValue());
                astNode.addChildNode(id_node);

                token = tokens.get(pos++);
                if (token.getSortCode() == SortCode.COMMA) {
                    ASTNode comma_node = new ASTNode(false, true, token.getValue());
                    astNode.addChildNode(comma_node);

                    token = tokens.get(pos++);
                }else if (token.getSortCode() == SortCode.FROM) {
                    return new SavePoint(--pos, true);
                } else {
                    return new SavePoint(pos, false);
                }
            }
        }
        return new SavePoint(pos, false);
    }
    public SavePoint DDL(int pos, ASTNode astNode) throws Exception {

        //create
        Token token = getToken(pos++);
        ASTNode create_node = new ASTNode(false, true,token.getValue());
        astNode.addChildNode(create_node);

        token = getToken(pos);
        String value = token.getValue();
        if (value.equals("TABLE")) {
            ASTNode table_node = new ASTNode(false, true, token.getValue());
            astNode.addChildNode(table_node);

            return CreateTable(++pos, astNode);
        }else if (value.equals("DATABASE")) {
            ASTNode database_node = new ASTNode(false, true, token.getValue());
            astNode.addChildNode(database_node);

            token = getToken(++pos);
            if (token.getSortCode() == SortCode.IDENTIFIED) {
                ASTNode id_node = new ASTNode(false, true, token.getValue());
                astNode.addChildNode(id_node);

                token = getToken(++pos);
                if (token.getSortCode() == SortCode.SEMICOLON) {
                    ASTNode se_node = new ASTNode(false, true, token.getValue());
                    astNode.addChildNode(se_node);

                    return new SavePoint(pos,true);
                }
            }
        }
        return new SavePoint(pos, false);
    }

    public SavePoint CreateTable(int pos, ASTNode astNode) throws Exception {
        Token token = getToken(pos);
        if (token.getSortCode() == SortCode.IDENTIFIED) {
            ASTNode childNode = new ASTNode(false, true, token.getValue());
            astNode.addChildNode(childNode);
            //next
            Token tt = getToken(++pos);
            if (tt.getSortCode() == SortCode.LPARENT) {
                parent_match++;

                ASTNode cnode = new ASTNode(false, true, tt.getValue());
                astNode.addChildNode(cnode);

                ASTNode col_node = new ASTNode(false, false, "column");
                astNode.addChildNode(col_node);
                SavePoint sp = column(++pos, col_node);
                if (sp.correct) {
                    pos = sp.pos;
                    token = getToken(pos);
                    if (token.getSortCode() == SortCode.RPARENT) {
                        parent_match--;
                        ASTNode rp = new ASTNode(false, true, token.getValue());
                        astNode.addChildNode(rp);

                        token = getToken(++pos);
                        if (token.getSortCode() == SortCode.SEMICOLON){
                            ASTNode semi_node = new ASTNode(false, true, token.getValue());
                            astNode.addChildNode(semi_node);

                            return new SavePoint(pos, true);
                        }
                    }
                }
            }
        }
        return new SavePoint(pos, false);
    }

    public SavePoint column(int pos, ASTNode astNode) throws Exception {
        Token token = getToken(pos);
        while (token.getSortCode() != SortCode.RPARENT) {
            //约束
            SortCode con_sort = token.getSortCode();
            SavePoint sp;

            switch (con_sort) {
                case UNIQUE:
                    sp = ContainsColumn("UNIQUE", pos, astNode);
                    if (sp.correct) {
                        pos = sp.pos;
                        token = getToken(pos);
                    }else {
                        return sp;
                    }
                    continue;
                case PRIMARY:
                    sp = ContainsColumn("PRIMARY", pos, astNode);
                    if (sp.correct) {
                        pos = sp.pos;
                        token = getToken(pos);
                    }else {
                        return sp;
                    }
                    continue;
                case FOREIGN:
                    sp = ContainsColumn("FOREIGN", pos, astNode);
                    if (sp.correct) {
                        pos = sp.pos;
                        token = getToken(pos);
                    }else {
                        return sp;
                    }
                    continue;
                case CHECK:
                    sp = ContainsColumn("CHECK", pos, astNode);
                    if (sp.correct) {
                        pos = sp.pos;
                        token = getToken(pos);
                    }else {
                        return sp;
                    }
                    continue;
                default:
                    break;
            }
            //column名
            Token lookahead = getToken(pos + 1);//观察数据类型
            SortCode sortCode = lookahead.getSortCode();

            switch (sortCode) {
                case VARCHAR:
                    sp = columnPattern(pos, "VARCHAR", astNode);
                    if (sp.correct) {
                        pos = sp.pos;
                        token = getToken(pos);
                    } else {
                        return sp;
                    }
                    break;
                case INTEGER:
                    sp = columnPattern(pos, "INTEGER", astNode);
                    if (sp.correct) {
                        pos = sp.pos;
                        token = getToken(pos);
                    }else {
                        return sp;
                    }
                    break;
                case INT:
                    sp = columnPattern(pos, "INT", astNode);
                    if (sp.correct) {
                        pos = sp.pos;
                        token = getToken(pos);
                    }else {
                        return sp;
                    }
                    break;
                case CHAR:
                    sp = columnPattern(pos, "CHAR", astNode);
                    if (sp.correct) {
                        pos = sp.pos;
                        token = getToken(pos);
                    }else {
                        return sp;
                    }
                    break;
                default:
                    return new SavePoint(pos, false);
            }
        }
        return new SavePoint(pos, true);//分号交给上层执行
    }

    private List<SortCode> getPattern(String name) {
        return SQLDDLPattern.pattern.get(name);
    }

    private SavePoint columnPattern(int pos, String name, ASTNode astNode) throws Exception {
        List<SortCode> patternlist = getPattern(name);
        for (SortCode sc : patternlist) {
            Token token = getToken(pos++);
            if (token.getSortCode() == sc) {
                ASTNode node = new ASTNode(false, true, token.getValue());
                astNode.addChildNode(node);
            } else {
                throw new SytaxErrorsException(getClass().toString());
            }
        }
        Token token = getToken(pos);
        if (token.getSortCode() == SortCode.NOT) {
            ASTNode not_node = new ASTNode(false, true, token.getValue());
            astNode.addChildNode(not_node);

            token = getToken(++pos);
            if (token.getSortCode() == SortCode.NULL) {
                ASTNode null_node = new ASTNode(false, true, token.getValue());
                astNode.addChildNode(null_node);

                token = getToken(++pos);
                if (token.getSortCode() == SortCode.COMMA) {
                    ASTNode comma_node = new ASTNode(false, true, token.getValue());
                    astNode.addChildNode(comma_node);
                    pos++;

                    token = getToken(pos);
                    //出现,)情况
                    if (token.getSortCode() == SortCode.RPARENT) {
                        return new SavePoint(pos, false);
                    }
                } else if (token.getSortCode() == SortCode.RPARENT) {}
                else {
                    throw new SytaxErrorsException(getClass().toString() + ":" + token.getLine());
                }
            }else {
                return new SavePoint(pos, false);
            }
        }else if (token.getSortCode() == SortCode.COMMA) {
            ASTNode comma_node = new ASTNode(false, true, token.getValue());
            astNode.addChildNode(comma_node);
            pos++;

            token = getToken(pos);
            //出现,)情况
            if (token.getSortCode() == SortCode.RPARENT) {
                return new SavePoint(pos, false);
            }
        }else if (token.getSortCode() == SortCode.RPARENT) {}
        else {
            throw new SytaxErrorsException(getClass().toString() + ":" + token.getLine());
        }
        return new SavePoint(pos, true);
    }
    //解析约束语句
    private SavePoint ContainsColumn(String name, int pos, ASTNode astNode) throws Exception {
        Map map = SQLContrainsPattern.getContrainsPattern();
        List<SortCode> sortcode_list = (List<SortCode>) map.get(name);
        Token token = getToken(pos++);
        for (SortCode sortCode : sortcode_list) {
            if (sortCode == token.getSortCode()) {
                ASTNode tmp_node = new ASTNode(false,true, token.getValue());
                astNode.addChildNode(tmp_node);

                token = tokens.get(pos++);
            }else {
                throw new SytaxErrorsException(getClass().toString() + ":" + token.getLine());
            }
        }
        ASTNode para_list = new ASTNode(false, false, "ParamsList");
        astNode.addChildNode(para_list);
        if (name.equals("UNIQUE") || name.equals("PRIMARY")) {
            SavePoint sp = ParamsList(pos, para_list);
            if (sp.correct) {
                pos = sp.pos;
                token = getToken(pos++);
                //接受"Para层次的)"
                if (token.getSortCode() == SortCode.RPARENT) {
                    ASTNode rp = new ASTNode(false, true, token.getValue());
                    astNode.addChildNode(rp);
                }else {
                    return new SavePoint(pos, false);
                }

                token = getToken(pos++);
                //table层次的 ")",交给上级处理
                if (token.getSortCode() == SortCode.RPARENT) {
                    return new SavePoint(--pos, true);
                }else if (token.getSortCode() == SortCode.COMMA) {
                    ASTNode comma_node = new ASTNode(false, true, token.getValue());
                    astNode.addChildNode(comma_node);

                    token = getToken(pos);
                    //出现,)情况
                    if (token.getSortCode() == SortCode.RPARENT) {
                        return new SavePoint(pos, false);
                    }
                    return new SavePoint(pos, true);
                }
            }
        }else if (name.equals("CHECK")) {
            SavePoint sp = CheckList(pos, para_list);
            if (sp.correct) {
                pos = sp.pos;
                token = getToken(pos++);
                //接受para层面上的)
                ASTNode rp = new ASTNode(false, true, token.getValue());
                astNode.addChildNode(rp);

                token = getToken(pos++);
                //table层面的)，交给上层处理
                if (token.getSortCode() == SortCode.RPARENT) {
                    return new SavePoint(--pos, true);
                }else if (token.getSortCode() == SortCode.COMMA) {
                    ASTNode ast_comma = new ASTNode(false, true, token.getValue());
                    astNode.addChildNode(ast_comma);

                    token = getToken(pos);
                    //出现,)情况，错误
                    if (token.getSortCode() == SortCode.RPARENT) {
                        return new SavePoint(pos, false);
                    }
                    return new SavePoint(pos, true);
                }
            }
        }else if (name.equals("FOREIGN")){
            token = getToken(pos++);
            //交给table层处理
            if (token.getSortCode() == SortCode.RPARENT) {
                return new SavePoint(--pos, true);
            }else if (token.getSortCode() == SortCode.COMMA) {
                ASTNode ast_comma = new ASTNode(false, true, token.getValue());
                astNode.addChildNode(ast_comma);

                token = getToken(pos);
                //出现,)情况，错误
                if (token.getSortCode() == SortCode.RPARENT) {
                    return new SavePoint(pos, false);
                }
                return new SavePoint(pos, true);
            }
        }
        return new SavePoint(pos, false);
    }
    //解析参数列表         value1,value2,value3,..... value == number, identifier, string
    private SavePoint ParamsList(int pos, ASTNode astNode) throws Exception {
        Token token = getToken(pos++);

        while (token.getSortCode() == SortCode.IDENTIFIED ||
                token.getSortCode() == SortCode.NUMBER ||
                token.getSortCode() == SortCode.STRING) {
            ASTNode id_node = new ASTNode(false, true, token.getValue());
            astNode.addChildNode(id_node);

            token = getToken(pos++);
            if (token.getSortCode() == SortCode.COMMA) {
                ASTNode comma_node = new ASTNode(false, true, token.getValue());
                astNode.addChildNode(comma_node);

                token = getToken(pos++);
                continue;
            } else if (token.getSortCode() == SortCode.RPARENT) {
                //unique,primary,insert values->)
                return new SavePoint(--pos, true);
            } else if (token.getSortCode() == SortCode.SEMICOLON) {
                //;group by
                return new SavePoint(--pos, true);
            } else if (token.getSortCode() == SortCode.HAVING) {
                //group_by -> having
                return new SavePoint(--pos, true);
            } else if (token.getSortCode() == SortCode.ORDER) {
                //group_by -> order
                return new SavePoint(--pos, true);
            } else {
                return new SavePoint(pos, false);
            }
        }
        return new SavePoint(--pos, false);
    }
    //解析check语句的参数列表
    private SavePoint CheckList(int pos, ASTNode astNode) throws Exception {
        Token token = getToken(pos++);

        while (token.getSortCode() != SortCode.RPARENT) {

            if (token.getSortCode() == SortCode.IDENTIFIED ||
                    token.getSortCode() == SortCode.NUMBER ||
                    token.getSortCode() == SortCode.STRING) {
                ASTNode id_num_node = new ASTNode(false,true, token.getValue());
                astNode.addChildNode(id_num_node);

                token = getToken(pos++);
                //接受比较关系符
                if (RelationOps.containValue(token.getValue())) {
                    ASTNode ro = new ASTNode(false, true, token.getValue());
                    astNode.addChildNode(ro);

                    token = getToken(pos++);

                    if (token.getSortCode() == SortCode.NUMBER ||
                            token.getSortCode() == SortCode.IDENTIFIED ||
                            token.getSortCode() == SortCode.STRING) {
                        ASTNode second_node = new ASTNode(false, true, token.getValue());
                        astNode.addChildNode(second_node);

                        token = getToken(pos++);
                        //真正退出循环的出口
                        if (token.getSortCode() == SortCode.RPARENT) {
                            //右括号结尾，check
                            return new SavePoint(--pos, true);
                        } else if (token.getSortCode() == SortCode.OR ||
                                token.getSortCode() == SortCode.AND) {
                            ASTNode and_or = new ASTNode(false, true, token.getValue());
                            astNode.addChildNode(and_or);

                            token = getToken(pos++);
                            continue;
                        } else if (token.getSortCode() == SortCode.SEMICOLON) {
                            //分号结尾，where,having
                            return new SavePoint(--pos, true);
                        } else if (token.getSortCode() == SortCode.GROUP) {
                            //where后面增加的group by语句
                            return new SavePoint(--pos, true);
                        }else if (token.getSortCode() == SortCode.ORDER) {
                            //where后面增加的order语句
                            return new SavePoint(--pos, true);
                        }
                    }
                }
            }
            return new SavePoint(pos, false);
        }
        return new SavePoint(pos, false);
    }

    public Token getToken(int pos) throws Exception{
        if (pos >= tokens.size()) {
            throw new SytaxErrorsException(getClass().toString());
        }
        return tokens.get(pos);
    }
}
