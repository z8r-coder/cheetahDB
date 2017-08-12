package Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import Exception.SytaxErrorsException;

import Support.Logging.Log;
import Support.Logging.LogFactory;
import com.sun.org.apache.bcel.internal.generic.FADD;
import com.sun.org.apache.bcel.internal.generic.INEG;
import org.omg.PortableServer.POA;

/**
 * Created by Roy on 2017/7/23.
 */
public class SQLParser {
    private AST ast;
    private List<Token> tokens;
    private int parent_match = 0;
    private final byte ICMask = (byte) 0xDF;

    private final static Log LOG = LogFactory.getLog(SQLParser.class);

    public SQLParser(List<Token> tokens, AST ast) {
        this.tokens = tokens;
        this.ast = ast;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public void setAst(AST ast) {
        this.ast = ast;
    }
    public AST getAst() {
        return ast;
    }

    public class SavePoint {
        int pos;
        boolean correct;

        public SavePoint(int pos, boolean correct) {
            this.correct = correct;
            this.pos = pos;
        }
    }
    //单条输出
    public AST managerSQL() throws Exception {
        for (int i = 0; i < tokens.size();) {
            SavePoint sp = sql(i);
            if (sp == null) {
                LOG.info("the first token is wrong");
                return null;
            }
            i = sp.pos + 1;
            if (!sp.correct) {
                throw new SytaxErrorsException(getClass().toString()
                        + "some wrong around " + getToken(sp.pos).getValue()
                        + " in " + sp.pos + " " + getToken(sp.pos).getLine());
            }
        }
        return ast;
    }
    public SavePoint sql(int pos) throws Exception {
        Token middle_token = new MiddleToken("sql_node");
        ASTNode root = new ASTNode(true, false, middle_token);
        ast.addRootNode(root);

        Token token = getToken(pos);
        String value = token.getValue().toUpperCase();
        if (value.equals("CREATE")) {
            token = getToken(pos + 1);
            if (token.getSortCode() == SortCode.TABLE) {
                Token crt_mid_token = new MiddleToken("CREATE_TABLE_NODE");
                ASTNode crt_tab = new ASTNode(false, false, crt_mid_token);
                root.addChildNode(crt_tab);

                ast.setAstType(SQLASTType.CREATE_TABLE);
                return DDL(pos,crt_tab);
            } else if (token.getSortCode() == SortCode.DATABASE) {
                Token crt_mid_token = new MiddleToken("CREATE_DATABASE_NODE");
                ASTNode crt_db = new ASTNode(false, false, crt_mid_token);
                root.addChildNode(crt_db);

                ast.setAstType(SQLASTType.CREATE_DATABASE);
                return DDL(pos, crt_db);
            }

            return new SavePoint(pos, false);
        } else if(value.equals("ALTER")) {
            Token crt_mid_token = new MiddleToken("ALTER_NODE");
            ASTNode aler_node = new ASTNode(false, false, crt_mid_token);
            root.addChildNode(aler_node);

            return DDL(pos, aler_node);
        } else if (value.equals("DROP")) {
            token = getToken(pos + 1);
            if (token.getSortCode() == SortCode.TABLE) {
                Token crt_mid_token = new MiddleToken("DROP_TABLE_NODE");
                ASTNode dp_tab_node = new ASTNode(false, false, crt_mid_token);
                root.addChildNode(dp_tab_node);

                ast.setAstType(SQLASTType.DROP_TABLE);
                return DDL(pos, dp_tab_node);
            } else if (token.getSortCode() == SortCode.DATABASE) {
                Token crt_mid_token = new MiddleToken("DROP_DATABASE_NODE");
                ASTNode dp_db_node = new ASTNode(false, false, crt_mid_token);
                root.addChildNode(dp_db_node);

                ast.setAstType(SQLASTType.DROP_DATABASE);
                return DDL(pos, dp_db_node);
            }

            return new SavePoint(pos, false);
        } else if (value.equals("SHOW")) {
            token = getToken(pos + 1);
            if (token.getSortCode() == SortCode.TABLES) {
                Token crt_mid_token = new MiddleToken("SHOW_TABLES_NODE");
                ASTNode show_tab = new ASTNode(false, false, crt_mid_token);
                root.addChildNode(show_tab);

                ast.setAstType(SQLASTType.SHOW_TABLES);
                return DDL(pos, show_tab);
            } else if (token.getSortCode() == SortCode.DATABASES){
                Token crt_mid_token = new MiddleToken("SHOW_DBS_NODE");
                ASTNode show_db = new ASTNode(false, false, crt_mid_token);
                root.addChildNode(show_db);

                ast.setAstType(SQLASTType.SHOW_DATABASES);
                return DDL(pos, show_db);
            }

            return new SavePoint(pos, false);
        } else if (value.equals("USE")) {
            Token use_mid_token = new MiddleToken("USE_DB_NODE");
            ASTNode use_node = new ASTNode(false, false, use_mid_token);
            root.addChildNode(use_node);

            ast.setAstType(SQLASTType.USE_DATABASE);
            return DDL(pos, use_node);
        } else if (value.equals("SELECT")){
            Token slt_mid_token = new MiddleToken("SELECT_NODE");
            ASTNode slt = new ASTNode(false, false, slt_mid_token);
            root.addChildNode(slt);

            return DQL(pos, slt);
        } else if (value.equals("INSERT") || value.equals("UPDATE") || value.equals("DELETE")) {
            if (value.equals("INSERT")) {
                Token ist_mid_token = new MiddleToken("INSERT_NODE");
                ASTNode insert_node = new ASTNode(false, false, ist_mid_token);
                root.addChildNode(insert_node);

                return DML(pos, insert_node);
            } else if (value.equals("UPDATE")) {
                Token udt_mid_token = new MiddleToken("UPDATE_NODE");
                ASTNode update_node = new ASTNode(false, false, udt_mid_token);
                root.addChildNode(update_node);

                return DML(pos, update_node);
            } else if (value.equals("DELETE")) {
                Token del_mid_token = new MiddleToken("DELETE_NODE");
                ASTNode delete_node = new ASTNode(false, false, del_mid_token);
                root.addChildNode(delete_node);

                return DML(pos, delete_node);
            }
        }
        return null;
    }
    public SavePoint DML(int pos, ASTNode astNode) throws Exception {
        Token token = getToken(pos++);
        switch (token.getSortCode()) {
            case INSERT:
                //add insert to ast
                ASTNode insert_node = new ASTNode(false, true, token);
                astNode.addChildNode(insert_node);

                token = getToken(pos++);
                if (token.getSortCode() == SortCode.INTO) {
                    ASTNode into_node = new ASTNode(false, true, token);
                    astNode.addChildNode(into_node);

                    token = getToken(pos++);
                    if (token.getSortCode() == SortCode.IDENTIFIED) {
                        ASTNode id_node = new ASTNode(false, true, token);
                        astNode.addChildNode(id_node);
                        /** default implement
                         * Single row insert
                         INSERT INTO table VALUES (value1, [value2, ... ])

                         Multirow insert
                         INSERT INTO tablename VALUES ('value-1a', ['value-1b', ...]),
                         ('value-2a', ['value-2b', ...]),
                         ...
                         **/
                        // TODO: 2017/8/10 由于修复bug兼容，此处没设计好
                        token = getToken(pos);
                        if (token.getSortCode() == SortCode.VALUES) {
                            ast.setAstType(SQLASTType.INSERT_DEFAULT);
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
                            ASTNode lp = new ASTNode(false, true, token);
                            astNode.addChildNode(lp);

                            Token ic_token = new MiddleToken("insert_colum_list");
                            ASTNode column_list = new ASTNode(false, false, ic_token);
                            astNode.addChildNode(column_list);

                            SavePoint sp = ParamsList(++pos, column_list);

                            pos = sp.pos;
                            if (sp.correct) {
                                token = getToken(pos++);
                                if (token.getSortCode() == SortCode.RPARENT) {
                                    ASTNode rp = new ASTNode(false, true, token);
                                    astNode.addChildNode(rp);

                                    ast.setAstType(SQLASTType.INSERT_NONE_DEFAULT);
                                    return values_single_mutl(pos, astNode);
                                }
                            }
                        }
                    }
                }
                break;
            case UPDATE:
                /**
                 * UPDATE table_name SET column_name = value [, column_name = value ...] [WHERE condition]
                 */
                //add update to ast
                ASTNode update_node = new ASTNode(false, true, token);
                astNode.addChildNode(update_node);

                token = getToken(pos++);
                if (token.getSortCode() == SortCode.IDENTIFIED) {
                    ASTNode id_node = new ASTNode(false, true, token);
                    astNode.addChildNode(id_node);

                    token = getToken(pos++);
                    if (token.getSortCode() == SortCode.SET) {
                        ASTNode set_node = new ASTNode(false, true, token);
                        astNode.addChildNode(set_node);

                        Token stl_token = new MiddleToken("set_list");
                        ASTNode set_list = new ASTNode(false, false, stl_token);
                        astNode.addChildNode(set_list);

                        SavePoint sp = set_list(pos, set_list);
                        pos = sp.pos;
                        if (sp.correct) {
                            token = getToken(pos);
                            if (token.getSortCode() == SortCode.SEMICOLON) {
                                ASTNode sem_node = new ASTNode(false, true, token);
                                astNode.addChildNode(sem_node);
                                //;结束
                                ast.setAstType(SQLASTType.UPDATE_WITHOUT_WHERE);
                                return new SavePoint(pos, true);
                            }else if (token.getSortCode() == SortCode.WHERE) {
                                ast.setAstType(SQLASTType.UPDATE_WITH_WHERE);
                                return where_condition(pos, astNode);
                            }
                        }
                    }
                }
                break;
            case DELETE:
                /**
                 * DELETE FROM table_name [WHERE condition];
                 */
                //add delete to ast
                ASTNode del_node = new ASTNode(false,true, token);
                astNode.addChildNode(del_node);

                token = getToken(pos++);
                if (token.getSortCode() == SortCode.FROM) {
                    ASTNode from_node = new ASTNode(false, true, token);
                    astNode.addChildNode(from_node);

                    token = getToken(pos++);
                    if (token.getSortCode() == SortCode.IDENTIFIED){
                        //table_name
                        ASTNode id_node = new ASTNode(false, true, token);
                        astNode.addChildNode(id_node);

                        token = getToken(pos++);
                        if (token.getSortCode() == SortCode.SEMICOLON) {
                            //delete语句以;结束
                            ASTNode sem_node = new ASTNode(false, true, token);
                            astNode.addChildNode(sem_node);

                            ast.setAstType(SQLASTType.DELETE_ALL);
                            return new SavePoint(--pos, true);
                        } else if (token.getSortCode() == SortCode.WHERE) {
                            ast.setAstType(SQLASTType.DELETE_WITH_WHERE);
                            //where语句
                            return where_condition(--pos, astNode);
                        }
                    }
                }
                break;
            default:
                break;
        }
        return new SavePoint(pos, false);
    }

    /**
     * 解析update函数，set后面多项更新和，赋值含有表达式的语句
     * @param pos
     * @param astNode
     * @return
     * @throws Exception
     */
    private SavePoint set_list(int pos, ASTNode astNode) throws Exception {
        Token token = getToken(pos++);
        for (;;) {
            if (token.getSortCode() == SortCode.IDENTIFIED) {
                ASTNode id_node = new ASTNode(false, true, token);
                astNode.addChildNode(id_node);

                token = getToken(pos++);
                if (token.getSortCode() == SortCode.EQ) {
                    ASTNode eq_node = new ASTNode(false, true, token);
                    astNode.addChildNode(eq_node);
                    
                    // TODO: 2017/8/6 column = (?) not support expression but can be string or number
                    token = getToken(pos++);
                    if (token.getSortCode() == SortCode.NUMBER ||
                            token.getSortCode() == SortCode.STRING) {
                        ASTNode ass_node = new ASTNode(false, true, token);
                        astNode.addChildNode(ass_node);

                        token = getToken(pos++);
                        //,后面还有赋值表达式
                        if (token.getSortCode() == SortCode.COMMA) {
                            ASTNode comma_node = new ASTNode(false, true, token);
                            astNode.addChildNode(comma_node);

                            token = getToken(pos++);
                            continue;
                        }else if (token.getSortCode() == SortCode.SEMICOLON) {
                            //func parse expression list semicolon need commit
                            return new SavePoint(--pos, true);
                        }else if (token.getSortCode() == SortCode.WHERE) {
                            //where子句交给上层处理
                            return new SavePoint(--pos, true);
                        }
                    }
                }
            }
            return new SavePoint(pos, false);
        }
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

        //判断该是多行插入还是单行插入.
        int count = 0;
        if (token.getSortCode() == SortCode.VALUES) {
            ASTNode value_node = new ASTNode(false, true, token);
            astNode.addChildNode(value_node);

            token = getToken(pos);
            for (;;) {
                count++;
                if (token.getSortCode() == SortCode.LPARENT) {
                    ASTNode lp = new ASTNode(false, true, token);
                    astNode.addChildNode(lp);

                    Token vl_token = new MiddleToken("values_list");
                    ASTNode valuelist_node = new ASTNode(false, false, vl_token);
                    astNode.addChildNode(valuelist_node);

                    SavePoint sp = ParamsList(++pos, valuelist_node);

                    pos = sp.pos;
                    if (sp.correct) {
                        token = getToken(pos++);
                        if (token.getSortCode() == SortCode.RPARENT) {
                            ASTNode rp = new ASTNode(false, true, token);
                            astNode.addChildNode(rp);

                            token = getToken(pos);
                            if (token.getSortCode() == SortCode.SEMICOLON) {
                                //以分号;结束insert语句
                                ASTNode sem = new ASTNode(false, true, token);
                                astNode.addChildNode(sem);

                                if (count > 1) {
                                    //多行
                                    if (ast.getAstType() == SQLASTType.INSERT_DEFAULT) {
                                        //多行缺省插入
                                        ast.setAstType(SQLASTType.INSERT_MULT_DEFAULT);
                                    } else {
                                        //多行非缺省插入
                                        ast.setAstType(SQLASTType.INSERT_MULT);
                                    }
                                } else {
                                    //单行
                                    if (ast.getAstType() == SQLASTType.INSERT_DEFAULT) {
                                        //单行缺省插入
                                        ast.setAstType(SQLASTType.INSERT_SINGLE_DEFAULT);
                                    } else {
                                        //单行非缺省插入
                                        ast.setAstType(SQLASTType.INSERT_SINGLE);
                                    }
                                }
                                return new SavePoint(pos, true);
                            }else if (token.getSortCode() == SortCode.COMMA) {
                                //多行插入
                                ASTNode comma_node = new ASTNode(false, true, token);
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

    // TODO: 2017/8/7  a.column_name语法还不支持
    public SavePoint DQL(int pos, ASTNode astNode) throws Exception {
        Token token = getToken(pos++);

        //add select to ast
        ASTNode select_node = new ASTNode(false, true, token);
        astNode.addChildNode(select_node);

        Token sl_token = new MiddleToken("select_list");
        ASTNode select_list = new ASTNode(false, false, sl_token);
        astNode.addChildNode(select_list);

        SavePoint sp_slist = select_list(pos, select_list);
        if (sp_slist.correct) {
            pos = sp_slist.pos;
            token = getToken(pos++);
            if (token.getSortCode() == SortCode.FROM) {
                ASTNode from_node = new ASTNode(false, true, token);
                astNode.addChildNode(from_node);

                token = getToken(pos++);
                //table name
                if (token.getSortCode() == SortCode.IDENTIFIED) {
                    ASTNode table_node = new ASTNode(false, true, token);
                    astNode.addChildNode(table_node);

                    token = getToken(pos);
                    switch (token.getSortCode()) {
                        case WHERE:
                            ast.setAstType(SQLASTType.SELECT_WITH_WHERE);
                            return where_condition(pos,astNode);
                        case GROUP:
                            return group_condition(pos, astNode);
                        case HAVING:
                            return having_condition(pos, astNode);
                        case ORDER:
                            return group_condition(pos, astNode);
                        case SEMICOLON:
                            //直接分号结束
                            ASTNode sem_node = new ASTNode(false, true, token);
                            astNode.addChildNode(sem_node);

                            ast.setAstType(SQLASTType.SELECT_ONLY);
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
    //可选表达式 where,生成结点 where_node
    public SavePoint where_condition(int pos, ASTNode astNode) throws Exception {
        Token token = getToken(pos++);
        if (token.getSortCode() == SortCode.WHERE) {
            //add where to ast
            ASTNode where_node = new ASTNode(false, true, token);
            astNode.addChildNode(where_node);

            //where后的限制条件
            Token wsc = new MiddleToken("where_search_condition");
            ASTNode search_node = new ASTNode(false, false, wsc);
            astNode.addChildNode(search_node);

            return search_condition(pos, search_node);
        }
        return new SavePoint(pos, false);
    }

    /**
     * 在ast中会存在一个search_condition的结点
     * where 后跟着in可进行复合语句查询,内层复合语句结尾必须带；
     * @param pos
     * @param astNode
     * @return
     * @throws Exception
     */
    public SavePoint search_condition(int pos, ASTNode astNode) throws Exception {
        Token token = getToken(pos++);
        //// TODO: 2017/8/6 层级太深，可用枚举优化
        for (;;) {
            if (token.getSortCode() == SortCode.IDENTIFIED) {
                ASTNode id_node = new ASTNode(false, true, token);
                astNode.addChildNode(id_node);

                token = getToken(pos++);
                if (token.getSortCode() == SortCode.IN) {
                    ASTNode in_node = new ASTNode(false, true, token);
                    astNode.addChildNode(in_node);

                    if (ast.getAstType() == SQLASTType.UPDATE_WITH_WHERE) {
                        ast.setAstType(SQLASTType.UPDATE_WITH_WHERE_IN);
                    } else if (ast.getAstType() == SQLASTType.DELETE_WITH_WHERE) {
                        ast.setAstType(SQLASTType.DELETE_WITH_WHERE_IN);
                    } else if (ast.getAstType() == SQLASTType.SELECT_WITH_WHERE) {
                        ast.setAstType(SQLASTType.SELECT_WITH_WHERE_IN);
                    } else {
                        return new SavePoint(pos, false);
                    }
                    token = getToken(pos++);
                    if (token.getSortCode() == SortCode.LPARENT) {
                        ASTNode lp = new ASTNode(false, true, token);
                        astNode.addChildNode(lp);

                        token = getToken(pos++);
                        if (token.getSortCode() == SortCode.SELECT) {
                            //复合查询
                            ast.setAstType(SQLASTType.SELECT_WITH_SUB);//存在子查询
                            SavePoint sp_dql = DQL(--pos, astNode);
                            pos = sp_dql.pos + 1;//最后一个是分号，则需要+1
                            if (sp_dql.correct) {
                                token = getToken(pos++);
                                if (token.getSortCode() == SortCode.RPARENT) {
                                    ASTNode rp = new ASTNode(false, true, token);
                                    astNode.addChildNode(rp);

                                    token = getToken(pos++);
                                    if (token.getSortCode() == SortCode.SEMICOLON) {
                                        ASTNode sem_node = new ASTNode(false, true, token);
                                        astNode.addChildNode(sem_node);

                                        return new SavePoint(--pos, true);
                                    } else if (token.getSortCode() == SortCode.GROUP) {
                                        return group_condition(--pos, astNode);
                                    } else if (token.getSortCode() == SortCode.ORDER) {
                                        return order_condition(--pos, astNode);
                                    } else if (token.getSortCode() == SortCode.AND ||
                                            token.getSortCode() == SortCode.OR) {
                                        ASTNode or_and_node = new ASTNode(false, true, token);
                                        astNode.addChildNode(or_and_node);

                                        token = getToken(pos++);
                                        continue;
                                    }
                                }
                            }
                        } else if (token.getSortCode() == SortCode.STRING ||
                                token.getSortCode() == SortCode.NUMBER) {
                            //限制条件查询
                            Token ivl = new MiddleToken("in_value_list");
                            ASTNode in_value_list = new ASTNode(false, false, ivl);
                            astNode.addChildNode(in_value_list);

                            SavePoint value_sp = value_list(--pos, in_value_list);
                            pos = value_sp.pos;
                            if (value_sp.correct) {
                                token = getToken(pos++);
                                if (token.getSortCode() == SortCode.RPARENT) {
                                    ASTNode rp = new ASTNode(false, true, token);
                                    astNode.addChildNode(rp);

                                    token = getToken(pos++);
                                    if (token.getSortCode() == SortCode.AND ||
                                            token.getSortCode() == SortCode.OR) {
                                        ASTNode or_and = new ASTNode(false ,true, token);
                                        astNode.addChildNode(or_and);

                                        token = getToken(pos++);
                                        continue;
                                    }else {
                                        return where_tail(--pos, astNode);
                                    }
                                }
                            }
                        }
                    }
                } else if (RelationOps.containValue(token.getValue())) {
                    //关系语句,format for column_name op (column_name or num,string)
                    ASTNode rps_node = new ASTNode(false, true, token);
                    astNode.addChildNode(rps_node);

                    token = getToken(pos++);
                    if (token.getSortCode() == SortCode.IDENTIFIED ||
                            token.getSortCode() == SortCode.NUMBER ||
                            token.getSortCode() == SortCode.STRING) {
                        ASTNode right_op = new ASTNode(false, true, token);
                        astNode.addChildNode(right_op);

                        token = getToken(pos++);
                        if (token.getSortCode() == SortCode.AND ||
                                token.getSortCode() == SortCode.OR) {
                            ASTNode and_or = new ASTNode(false, true, token);
                            astNode.addChildNode(and_or);

                            token = getToken(pos++);
                            continue;
                        }else {
                            return where_tail(--pos, astNode);
                        }
                    }
                }
            }
            break;
        }
        return new SavePoint(pos, false);
    }

    /**
     * where语句收尾
     * @param pos
     * @param astNode
     * @return
     * @throws Exception
     */
    private SavePoint where_tail(int pos, ASTNode astNode) throws Exception {
        Token token = getToken(pos++);
        if (token.getSortCode() == SortCode.SEMICOLON) {
            ASTNode sem_node = new ASTNode(false, true, token);
            astNode.addChildNode(sem_node);

            return new SavePoint(--pos, true);
        } else if (token.getSortCode() == SortCode.ORDER) {
            return order_condition(--pos, astNode);
        } else if (token.getSortCode() == SortCode.GROUP) {
            return group_condition(--pos, astNode);
        }
        return new SavePoint(pos, false);
    }
    /**
     * in_condition value list, string,num,......
     * @param pos
     * @param astNode
     * @return
     * @throws Exception
     */
    private SavePoint value_list(int pos, ASTNode astNode) throws Exception {
        Token token = getToken(pos++);
        for (;;) {
            if (token.getSortCode() == SortCode.STRING ||
                    token.getSortCode() == SortCode.NUMBER) {
                ASTNode str_num_node = new ASTNode(false, true, token);
                astNode.addChildNode(str_num_node);

                token = getToken(pos++);
                if (token.getSortCode() == SortCode.COMMA) {
                    ASTNode comma_node = new ASTNode(false, true, token);
                    astNode.addChildNode(comma_node);

                    continue;
                } else if (token.getSortCode() == SortCode.RPARENT) {
                    return new SavePoint(--pos, true);
                }
            }
            break;
        }
        return new SavePoint(pos, false);
    }
    //可选表达式 group by
    public SavePoint group_condition(int pos, ASTNode astNode) throws Exception {
        Token token = getToken(pos++);
        if (token.getSortCode() == SortCode.GROUP) {
            ASTNode g_node = new ASTNode(false, true, token);
            astNode.addChildNode(g_node);

            token = getToken(pos++);
            if (token.getSortCode() == SortCode.BY) {
                ASTNode b_node = new ASTNode(false, true, token);
                astNode.addChildNode(b_node);

                Token gb_token = new MiddleToken("group_by list");
                ASTNode group = new ASTNode(false, true, gb_token);
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
                        ASTNode sem_node = new ASTNode(false, true, token);
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
            ASTNode or_node = new ASTNode(false, true, token);
            astNode.addChildNode(or_node);

            token = getToken(pos++);
            if (token.getSortCode() == SortCode.BY) {
                ASTNode by_node = new ASTNode(false, true, token);
                astNode.addChildNode(by_node);

                token = getToken(pos++);
                if (token.getSortCode() == SortCode.ASC) {
                    //升序
                    ASTNode asc_node = new ASTNode(false, true, token);
                    astNode.addChildNode(asc_node);
                }else if (token.getSortCode() == SortCode.DESC){
                    //降序
                    ASTNode desc_node = new ASTNode(false, true, token);
                    astNode.addChildNode(desc_node);
                }else {
                    return new SavePoint(pos, false);
                }
                token = getToken(pos);
                if (token.getSortCode() == SortCode.SEMICOLON) {
                    //order by 以;结束
                    ASTNode sem_node = new ASTNode(false, true, token);
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
            ASTNode having_node = new ASTNode(false,  true, token);
            astNode.addChildNode(having_node);

            Token hsc_token = new MiddleToken("having_search_condition");
            ASTNode condition_node = new ASTNode(false, false, hsc_token);
            astNode.addChildNode(condition_node);

            SavePoint sp = having_search_condition(pos, condition_node);

            pos = sp.pos;
            if (sp.correct) {
                token = getToken(pos);
                if (token.getSortCode() == SortCode.SEMICOLON) {
                    // having 以;分号结束
                    ASTNode sem_node = new ASTNode(false ,true, token);
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
                ASTNode op_node = new ASTNode(false, true, token);
                astNode.addChildNode(op_node);

                //op右边
                SavePoint right_sp = having_left_op_right(pos, astNode);
                if (right_sp.correct) {
                    pos = right_sp.pos;
                    token = getToken(pos++);

                    if (token.getSortCode() == SortCode.OR ||
                            token.getSortCode() == SortCode.AND) {
                        ASTNode oa_node = new ASTNode(false, true, token);
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
                ASTNode count_node = new ASTNode(false, true, token);
                astNode.addChildNode(count_node);

                token = getToken(pos++);
                if (token.getSortCode() == SortCode.LPARENT) {
                    ASTNode lp = new ASTNode(false, true, token);
                    astNode.addChildNode(lp);

                    token = getToken(pos++);
                    if (token.getSortCode() == SortCode.IDENTIFIED) {
                        ASTNode id_node = new ASTNode(false, true, token);
                        astNode.addChildNode(id_node);

                        token = getToken(pos++);
                        if (token.getSortCode() == SortCode.RPARENT) {
                            ASTNode rp = new ASTNode(false, true, token);
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
                    ASTNode id_node = new ASTNode(false, true, token);
                    astNode.addChildNode(id_node);
                } else if (token.getSortCode() == SortCode.NUMBER) {
                    ASTNode num_node = new ASTNode(false, true, token);
                    astNode.addChildNode(num_node);
                } else if (token.getSortCode() == SortCode.STRING) {
                    ASTNode str_node = new ASTNode(false, true, token);
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
            ASTNode star_node = new ASTNode(false, true, token);
            astNode.addChildNode(star_node);

            return new SavePoint(pos, true);
        }else if (token.getSortCode() == SortCode.IDENTIFIED) {
            while (token.getSortCode() == SortCode.IDENTIFIED) {
                ASTNode id_node = new ASTNode(false, true, token);
                astNode.addChildNode(id_node);

                token = tokens.get(pos++);
                if (token.getSortCode() == SortCode.COMMA) {
                    ASTNode comma_node = new ASTNode(false, true, token);
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

        Token token = getToken(pos++);
        if (token.getSortCode() == SortCode.CREATE) {
            ASTNode create_node = new ASTNode(false, true,token);
            astNode.addChildNode(create_node);

            token = getToken(pos);
            String value = token.getValue();
            if (value.equals("TABLE")) {
                ASTNode table_node = new ASTNode(false, true, token);
                astNode.addChildNode(table_node);

                return CreateTable(++pos, astNode);
            }else if (value.equals("DATABASE")) {
                ASTNode database_node = new ASTNode(false, true, token);
                astNode.addChildNode(database_node);

                token = getToken(++pos);
                if (token.getSortCode() == SortCode.IDENTIFIED) {
                    ASTNode id_node = new ASTNode(false, true, token);
                    astNode.addChildNode(id_node);

                    token = getToken(++pos);
                    if (token.getSortCode() == SortCode.SEMICOLON) {
                        ASTNode se_node = new ASTNode(false, true, token);
                        astNode.addChildNode(se_node);

                        return new SavePoint(pos,true);
                    }
                }
            }
        } else if (token.getSortCode() == SortCode.DROP) {
            ASTNode drop_node = new ASTNode(false, true, token);
            astNode.addChildNode(drop_node);

            token = getToken(pos++);
            if (token.getSortCode() == SortCode.DATABASE) {
                ASTNode database_node = new ASTNode(false, true, token);
                astNode.addChildNode(database_node);

                token = getToken(pos++);
                if (token.getSortCode() == SortCode.IDENTIFIED) {
                    //database's name
                    ASTNode id_node = new ASTNode(false, true, token);
                    astNode.addChildNode(id_node);

                    token = getToken(pos);
                    if (token.getSortCode() == SortCode.SEMICOLON) {
                        ASTNode sem_node = new ASTNode(false, true, token);
                        astNode.addChildNode(sem_node);

                        return new SavePoint(pos, true);
                    }
                }
            } else if (token.getSortCode() == SortCode.TABLE) {
                ASTNode table_node = new ASTNode(false, true, token);
                astNode.addChildNode(table_node);

                token = getToken(pos++);
                if (token.getSortCode() == SortCode.IDENTIFIED) {
                    ASTNode id_node = new ASTNode(false, true, token);
                    astNode.addChildNode(id_node);

                    token = getToken(pos);
                    if (token.getSortCode() == SortCode.SEMICOLON) {
                        ASTNode sem_node = new ASTNode(false, true, token);
                        astNode.addChildNode(sem_node);

                        return new SavePoint(pos, true);
                    }
                }
            }
        } else if (token.getSortCode() == SortCode.ALTER) {
            //暂时 alert只支持对表的修改
            List<SortCode> alert_list = SQLAlterPattern.getAlterPattern();
            SortCode contextSortCode = null;
            boolean fail = false;
            for (SortCode sortCode : alert_list) {
                if (sortCode == SortCode.OPTION) {
                    if (token.getSortCode() == SortCode.ADD) {
                        ASTNode add_node  = new ASTNode(false, true, token);
                        astNode.addChildNode(add_node);

                        contextSortCode = SortCode.ADD;

                        ast.setAstType(SQLASTType.ALTER_TABLE_ADD);
                    } else if (token.getSortCode() == SortCode.DROP) {
                        ASTNode drop_node = new ASTNode(false, true, token);
                        astNode.addChildNode(drop_node);

                        contextSortCode = SortCode.DROP;

                        ast.setAstType(SQLASTType.ALTER_TABLE_DROP);
                    } else if (token.getSortCode() == SortCode.ALTER) {
                        ASTNode alter_node = new ASTNode(false, true, token);
                        astNode.addChildNode(alter_node);

                        contextSortCode = SortCode.ALTER;

                        ast.setAstType(SQLASTType.ALTER_TABLE_ALTER);
                    } else {
                        break;
                    }
                    token = getToken(pos++);
                    continue;
                }
                if (token.getSortCode() == sortCode) {
                    ASTNode tmp_node = new ASTNode(false, true, token);
                    astNode.addChildNode(tmp_node);

                    token = getToken(pos++);
                    continue;
                }

                //ruo contetSortCode = null, 则fail = true，则下面一定不会使用到contextSortCode
                fail = true;
                break;
            }
            //解析缺省 datatype
            if (!fail) {
                switch (contextSortCode) {
                    case ADD:
                    case ALTER:
                        List<SortCode> dataTypePattern = SQLDataTypePattern.getDataTypePattern();
                        for (SortCode sortCode : dataTypePattern) {
                            if (sortCode == SortCode.OPTION) {
                                if (token.getSortCode() == SortCode.VARCHAR) {
                                    ASTNode vc = new ASTNode(false, true, token);
                                    astNode.addChildNode(vc);
                                } else if (token.getSortCode() == SortCode.NUMBER) {
                                    ASTNode num = new ASTNode(false, true, token);
                                    astNode.addChildNode(num);
                                } else {
                                    return new SavePoint(pos, false);
                                }
                                token = getToken(pos++);
                                continue;
                            }

                            if (token.getSortCode() == sortCode) {
                                ASTNode tmp_token = new ASTNode(false, true, token);
                                astNode.addChildNode(tmp_token);

                                token = getToken(pos++);
                                continue;
                            }
                            return new SavePoint(pos, false);
                        }
                        break;
                    case DROP:
                        if (token.getSortCode() == SortCode.SEMICOLON) {
                            ASTNode sem_node = new ASTNode(false, true, token);
                            astNode.addChildNode(sem_node);
                            break;
                        }
                        return new SavePoint(pos, false);
                }
                return new SavePoint(--pos, true);
            }
        } else if (token.getSortCode() == SortCode.SHOW) {
            ASTNode show_node = new ASTNode(false, true, token);
            astNode.addChildNode(show_node);

            token = getToken(pos++);
            if (token.getSortCode() == SortCode.DATABASES) {
                ASTNode db_node = new ASTNode(false, true, token);
                astNode.addChildNode(db_node);

                token = getToken(pos++);
                if (token.getSortCode() == SortCode.SEMICOLON) {
                    ASTNode sem_node = new ASTNode(false, true, token);
                    astNode.addChildNode(sem_node);

                    return new SavePoint(--pos, true);
                }
            } else if (token.getSortCode() == SortCode.TABLES) {
                ASTNode tab_node = new ASTNode(false, true, token);
                astNode.addChildNode(tab_node);
                token = getToken(pos);
                if(token.getSortCode() == SortCode.SEMICOLON) {
                    ASTNode sem_node = new ASTNode(false, true, token);
                    astNode.addChildNode(sem_node);

                    return new SavePoint(pos, true);
                }
            }
        } else if (token.getSortCode() == SortCode.USE) {
            ASTNode use_node = new ASTNode(false, true, token);
            astNode.addChildNode(use_node);

            ast.setAstType(SQLASTType.USE_DATABASE);
            token = getToken(pos++);

            if (token.getSortCode() == SortCode.IDENTIFIED) {
                ASTNode db_name = new ASTNode(false, true, token);
                astNode.addChildNode(db_name);

                token = getToken(pos);
                if (token.getSortCode() == SortCode.SEMICOLON) {
                    ASTNode sem_node = new ASTNode(false, true, token);
                    astNode.addChildNode(sem_node);

                    return new SavePoint(pos, true);
                }
            }
        }

        return new SavePoint(pos, false);
    }

    public SavePoint CreateTable(int pos, ASTNode astNode) throws Exception {
        Token token = getToken(pos);
        if (token.getSortCode() == SortCode.IDENTIFIED) {
            ASTNode childNode = new ASTNode(false, true, token);
            astNode.addChildNode(childNode);
            //next
            Token tt = getToken(++pos);
            if (tt.getSortCode() == SortCode.LPARENT) {
                parent_match++;

                ASTNode cnode = new ASTNode(false, true, tt);
                astNode.addChildNode(cnode);

                Token col_token = new MiddleToken("column");
                ASTNode col_node = new ASTNode(false, false, col_token);
                astNode.addChildNode(col_node);

                SavePoint sp = column(++pos, col_node);
                if (sp.correct) {
                    pos = sp.pos;
                    token = getToken(pos);
                    if (token.getSortCode() == SortCode.RPARENT) {
                        parent_match--;
                        ASTNode rp = new ASTNode(false, true, token);
                        astNode.addChildNode(rp);

                        token = getToken(++pos);
                        if (token.getSortCode() == SortCode.SEMICOLON){
                            ASTNode semi_node = new ASTNode(false, true, token);
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
                    Token uni_token = new MiddleToken("unique_node");
                    ASTNode uni_node = new ASTNode(false, false, uni_token);
                    astNode.addChildNode(uni_node);

                    sp = ContainsColumn("UNIQUE", pos, uni_node);
                    if (sp.correct) {
                        pos = sp.pos;
                        token = getToken(pos);
                    }else {
                        return sp;
                    }
                    continue;
                case PRIMARY:
                    Token pri_token = new MiddleToken("prim_node");
                    ASTNode pri_node = new ASTNode(false, false, pri_token);
                    astNode.addChildNode(pri_node);

                    sp = ContainsColumn("PRIMARY", pos, pri_node);
                    if (sp.correct) {
                        pos = sp.pos;
                        token = getToken(pos);
                    }else {
                        return sp;
                    }
                    continue;
                case FOREIGN:
                    Token frg_token = new MiddleToken("frg_node");
                    ASTNode frg_node = new ASTNode(false, false, frg_token);
                    astNode.addChildNode(frg_node);

                    sp = ContainsColumn("FOREIGN", pos, frg_node);
                    if (sp.correct) {
                        pos = sp.pos;
                        token = getToken(pos);
                    }else {
                        return sp;
                    }
                    continue;
                case CHECK:
                    Token ck_token = new MiddleToken("check_node");
                    ASTNode check_node = new ASTNode(false, false, ck_token);
                    astNode.addChildNode(check_node);

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
            Token col_token = new MiddleToken("column_node");
            ASTNode column_node = new ASTNode(false, false, col_token);
            astNode.addChildNode(column_node);

            sp = columnPattern(pos, column_node);
            pos = sp.pos;
            if (sp.correct) {
                token = getToken(pos);
            } else {
                return sp;
            }
            continue;
        }
        return new SavePoint(pos, true);//table层次分号和右括号交给上层执行
    }

    private SavePoint columnPattern(int pos, ASTNode astNode) throws Exception {
        List<SortCode> patternlist = SQLCreateTablePattern.getCrtTabPat();
        for (SortCode sc : patternlist) {
            Token token = getToken(pos++);
            if (sc == SortCode.OPTION) {
                if (token.getSortCode() == SortCode.VARCHAR) {
                    ASTNode var_node = new ASTNode(false, true, token);
                    astNode.addChildNode(var_node);

                    continue;
                } else if (token.getSortCode() == SortCode.INTEGER) {
                    ASTNode int_node = new ASTNode(false, true, token);
                    astNode.addChildNode(int_node);

                    continue;
                } else {
                    LOG.info("Cheetah's grammar is only support Varchar and integer");
                    return new SavePoint(pos, false);
                }
            }
            if (token.getSortCode() == sc) {
                ASTNode node = new ASTNode(false, true, token);
                astNode.addChildNode(node);
            } else {
                return new SavePoint(pos, false);
            }
        }
        Token token = getToken(pos);
        if (token.getSortCode() == SortCode.NOT) {
            ASTNode not_node = new ASTNode(false, true, token);
            astNode.addChildNode(not_node);

            token = getToken(++pos);
            if (token.getSortCode() == SortCode.NULL) {
                ASTNode null_node = new ASTNode(false, true, token);
                astNode.addChildNode(null_node);

                token = getToken(++pos);
                if (token.getSortCode() == SortCode.COMMA) {
                    ASTNode comma_node = new ASTNode(false, true, token);
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
            ASTNode comma_node = new ASTNode(false, true, token);
            astNode.addChildNode(comma_node);
            pos++;

            token = getToken(pos);
            //出现,)情况
            if (token.getSortCode() == SortCode.RPARENT) {
                return new SavePoint(pos, false);
            }
        }else if (token.getSortCode() == SortCode.RPARENT) {}
        else {
            return new SavePoint(pos, false);
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
                ASTNode tmp_node = new ASTNode(false,true, token);
                astNode.addChildNode(tmp_node);
            }else {
                return new SavePoint(pos, false);
            }

            token = getToken(pos++);
        }

        if (name.equals("UNIQUE") || name.equals("PRIMARY")) {
            Token prm_token = new MiddleToken("ParamsList");
            ASTNode para_list = new ASTNode(false, false, prm_token);
            astNode.addChildNode(para_list);
            SavePoint sp = ParamsList(--pos, para_list);
            if (sp.correct) {
                pos = sp.pos;
                token = getToken(pos++);
                //接受"Para层次的)"
                if (token.getSortCode() == SortCode.RPARENT) {
                    ASTNode rp = new ASTNode(false, true, token);
                    astNode.addChildNode(rp);
                }else {
                    return new SavePoint(pos, false);
                }

                token = getToken(pos++);
                //table层次的 ")",交给上级处理
                if (token.getSortCode() == SortCode.RPARENT) {
                    return new SavePoint(--pos, true);
                }else if (token.getSortCode() == SortCode.COMMA) {
                    ASTNode comma_node = new ASTNode(false, true, token);
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
            Token ck_list = new MiddleToken("check_list");
            ASTNode check_list = new ASTNode(false, false, ck_list);
            astNode.addChildNode(check_list);

            SavePoint sp = CheckList(--pos, check_list);
            if (sp.correct) {
                pos = sp.pos;
                token = getToken(pos++);
                //接受para层面上的)
                ASTNode rp = new ASTNode(false, true, token);
                astNode.addChildNode(rp);

                token = getToken(pos++);
                //table层面的)，交给上层处理
                if (token.getSortCode() == SortCode.RPARENT) {
                    return new SavePoint(--pos, true);
                }else if (token.getSortCode() == SortCode.COMMA) {
                    ASTNode ast_comma = new ASTNode(false, true, token);
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
            token = getToken(--pos);
            //交给table层处理
            if (token.getSortCode() == SortCode.RPARENT) {
                return new SavePoint(pos, true);
            }else if (token.getSortCode() == SortCode.COMMA) {
                ASTNode ast_comma = new ASTNode(false, true, token);
                astNode.addChildNode(ast_comma);

                token = getToken(++pos);
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
            ASTNode id_node = new ASTNode(false, true, token);
            astNode.addChildNode(id_node);

            token = getToken(pos++);
            if (token.getSortCode() == SortCode.COMMA) {
                ASTNode comma_node = new ASTNode(false, true, token);
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
                ASTNode id_num_node = new ASTNode(false,true, token);
                astNode.addChildNode(id_num_node);

                token = getToken(pos++);
                //接受比较关系符
                if (RelationOps.containValue(token.getValue())) {
                    ASTNode ro = new ASTNode(false, true, token);
                    astNode.addChildNode(ro);

                    token = getToken(pos++);

                    if (token.getSortCode() == SortCode.NUMBER ||
                            token.getSortCode() == SortCode.IDENTIFIED ||
                            token.getSortCode() == SortCode.STRING) {
                        ASTNode second_node = new ASTNode(false, true, token);
                        astNode.addChildNode(second_node);

                        token = getToken(pos++);
                        //真正退出循环的出口
                        if (token.getSortCode() == SortCode.RPARENT) {
                            //右括号结尾，check
                            return new SavePoint(--pos, true);
                        } else if (token.getSortCode() == SortCode.OR ||
                                token.getSortCode() == SortCode.AND) {
                            ASTNode and_or = new ASTNode(false, true, token);
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

    /**
     * 预加载语句数据
     * @return
     */
    public String PreLoad() throws Exception {
        Token token0 = getToken(0);
        Token token1;
        switch (token0.getSortCode()) {
            case SELECT:
                return "SELECT";
            case INSERT:
                return "INSERT";
            case DELETE:
                return "DELETE";
            case UPDATE:
                return "UPDATE";
            case CREATE:
                token1 = getToken(1);
                if (token1.getSortCode() == SortCode.DATABASE) {
                    return "CREATE_DATABASE";
                } else if (token1.getSortCode() == SortCode.TABLE) {
                    return "CREATE_TABLE";
                }
                break;
            case ALTER:
                return "ALERT_TABLE";
            case DROP:
                token1 = getToken(1);
                if (token1.getSortCode() == SortCode.TABLE) {
                    return "DROP_TABLE";
                } else if (token1.getSortCode() == SortCode.DATABASE) {
                    return "DROP_DATABASE";
                }
            default:
                break;
        }
        throw new SytaxErrorsException("PreLoad error");
    }

}
