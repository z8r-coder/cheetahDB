package Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import Exception.SytaxErrorsException;
import Log.CheetahASTLog;
import com.sun.org.apache.bcel.internal.generic.FADD;
import org.omg.PortableServer.POA;

/**
 * Created by Roy on 2017/7/23.
 */
public class SQLParser {
    private AST ast;
    private List<Token> tokens;
    private int parent_match = 0;
    private final byte ICMask = (byte) 0xDF;

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
                CheetahASTLog.Info("the first token is wrong");
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
        ASTNode root = new ASTNode(true, false, "sql_node");
        ast.addRootNode(root);

        Token token = getToken(pos);
        String value = token.getValue().toUpperCase();
        if (value.equals("CREATE")) {
            token = getToken(pos + 1);
            if (token.getSortCode() == SortCode.TABLE) {
                ASTNode crt_tab = new ASTNode(false, false, "CREATE_TABLE_NODE");
                root.addChildNode(crt_tab);

                return DDL(pos,crt_tab);
            } else if (token.getSortCode() == SortCode.DATABASE) {
                ASTNode crt_db = new ASTNode(false, false, "CREATE_DATABASE_NODE");
                root.addChildNode(crt_db);

                return DDL(pos, crt_db);
            }

            return new SavePoint(pos, false);
        } else if(value.equals("ALTER")) {
            ASTNode aler_node = new ASTNode(false, false, "ALTER_NODE");
            root.addChildNode(aler_node);

            return DDL(pos, aler_node);
        } else if (value.equals("DROP")) {
            token = getToken(pos + 1);
            if (token.getSortCode() == SortCode.TABLE) {
                ASTNode dp_tab_node = new ASTNode(false, false, "DROP_TABLE_NODE");
                root.addChildNode(dp_tab_node);

                return DDL(pos, dp_tab_node);
            } else if (token.getSortCode() == SortCode.DATABASE) {
                ASTNode dp_db_node = new ASTNode(false, false, "DROP_DATABASE_NODE");
                root.addChildNode(dp_db_node);

                return DDL(pos, dp_db_node);
            }

            return new SavePoint(pos, false);
        } else if (value.equals("SELECT")){
            ASTNode slt = new ASTNode(false, false, "SELECT_NODE");
            root.addChildNode(slt);

            return DQL(pos, slt);
        } else if (value.equals("INSERT") || value.equals("UPDATE") || value.equals("DELETE")) {
            if (value.equals("INSERT")) {
                ASTNode insert_node = new ASTNode(false, false, "INSERT_NODE");
                root.addChildNode(insert_node);

                return DML(pos, insert_node);
            } else if (value.equals("UPDATE")) {
                ASTNode update_node = new ASTNode(false, false, "UPDATE_NODE");
                root.addChildNode(update_node);

                return DML(pos, update_node);
            } else if (value.equals("DELETE")) {
                ASTNode delete_node = new ASTNode(false, false, "DELETE_NODE");
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
                        // TODO: 2017/8/10 由于修复bug兼容，此处没设计好
                        token = getToken(pos);
                        if (token.getSortCode() == SortCode.VALUES) {
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

                            ASTNode column_list = new ASTNode(false, false, "insert_colum_list");
                            astNode.addChildNode(column_list);

                            SavePoint sp = ParamsList(++pos, column_list);

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
                }
                break;
            case UPDATE:
                /**
                 * UPDATE table_name SET column_name = value [, column_name = value ...] [WHERE condition]
                 */
                //add update to ast
                ASTNode update_node = new ASTNode(false, true, token.getValue());
                astNode.addChildNode(update_node);

                token = getToken(pos++);
                if (token.getSortCode() == SortCode.IDENTIFIED) {
                    ASTNode id_node = new ASTNode(false, true, token.getValue());
                    astNode.addChildNode(id_node);

                    token = getToken(pos++);
                    if (token.getSortCode() == SortCode.SET) {
                        ASTNode set_node = new ASTNode(false, true, token.getValue());
                        astNode.addChildNode(set_node);

                        ASTNode set_list = new ASTNode(false, false, "set_list");
                        astNode.addChildNode(set_list);

                        SavePoint sp = set_list(pos, set_list);
                        pos = sp.pos;
                        if (sp.correct) {
                            token = getToken(pos);
                            if (token.getSortCode() == SortCode.SEMICOLON) {
                                ASTNode sem_node = new ASTNode(false, true, token.getValue());
                                astNode.addChildNode(sem_node);
                                //;结束
                                return new SavePoint(pos, true);
                            }else if (token.getSortCode() == SortCode.WHERE) {
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
                ASTNode del_node = new ASTNode(false,true, token.getValue());
                astNode.addChildNode(del_node);

                token = getToken(pos++);
                if (token.getSortCode() == SortCode.FROM) {
                    ASTNode from_node = new ASTNode(false, true, token.getValue());
                    astNode.addChildNode(from_node);

                    token = getToken(pos++);
                    if (token.getSortCode() == SortCode.IDENTIFIED){
                        //table_name
                        ASTNode id_node = new ASTNode(false, true, token.getValue());
                        astNode.addChildNode(id_node);

                        token = getToken(pos++);
                        if (token.getSortCode() == SortCode.SEMICOLON) {
                            //delete语句以;结束
                            ASTNode sem_node = new ASTNode(false, true, token.getValue());
                            astNode.addChildNode(sem_node);

                            return new SavePoint(--pos, true);
                        } else if (token.getSortCode() == SortCode.WHERE) {
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
                ASTNode id_node = new ASTNode(false, true, token.getValue());
                astNode.addChildNode(id_node);

                token = getToken(pos++);
                if (token.getSortCode() == SortCode.EQ) {
                    ASTNode eq_node = new ASTNode(false, true, token.getValue());
                    astNode.addChildNode(eq_node);
                    
                    // TODO: 2017/8/6 column = (?) not support expression but can be string or number
                    token = getToken(pos++);
                    if (token.getSortCode() == SortCode.NUMBER ||
                            token.getSortCode() == SortCode.STRING) {
                        ASTNode ass_node = new ASTNode(false, true, token.getValue());
                        astNode.addChildNode(ass_node);

                        token = getToken(pos++);
                        //,后面还有赋值表达式
                        if (token.getSortCode() == SortCode.COMMA) {
                            ASTNode comma_node = new ASTNode(false, true, token.getValue());
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
        if (token.getSortCode() == SortCode.VALUES) {
            ASTNode value_node = new ASTNode(false, true, token.getValue());
            astNode.addChildNode(value_node);

            token = getToken(pos);
            for (;;) {
                if (token.getSortCode() == SortCode.LPARENT) {
                    ASTNode lp = new ASTNode(false, true, token.getValue());
                    astNode.addChildNode(lp);

                    ASTNode valuelist_node = new ASTNode(false, false, "values_list");
                    astNode.addChildNode(valuelist_node);

                    SavePoint sp = ParamsList(++pos, valuelist_node);

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

                                return new SavePoint(pos, true);
                            }else if (token.getSortCode() == SortCode.COMMA) {
                                //多行插入
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

    // TODO: 2017/8/7  a.column_name语法还不支持
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
            //add where to ast
            ASTNode where_node = new ASTNode(false, true, token.getValue());
            astNode.addChildNode(where_node);

            //where后的限制条件
            ASTNode search_node = new ASTNode(false, false, "search_condition");
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
                ASTNode id_node = new ASTNode(false, true, token.getValue());
                astNode.addChildNode(id_node);

                token = getToken(pos++);
                if (token.getSortCode() == SortCode.IN) {
                    ASTNode in_node = new ASTNode(false, true, token.getValue());
                    astNode.addChildNode(in_node);

                    token = getToken(pos++);
                    if (token.getSortCode() == SortCode.LPARENT) {
                        ASTNode lp = new ASTNode(false, true, token.getValue());
                        astNode.addChildNode(lp);

                        token = getToken(pos++);
                        if (token.getSortCode() == SortCode.SELECT) {
                            //复合查询
                            SavePoint sp_dql = DQL(--pos, astNode);
                            pos = sp_dql.pos + 1;//最后一个是分号，则需要+1
                            if (sp_dql.correct) {
                                token = getToken(pos++);
                                if (token.getSortCode() == SortCode.RPARENT) {
                                    ASTNode rp = new ASTNode(false, true, token.getValue());
                                    astNode.addChildNode(rp);

                                    token = getToken(pos++);
                                    if (token.getSortCode() == SortCode.SEMICOLON) {
                                        ASTNode sem_node = new ASTNode(false, true, token.getValue());
                                        astNode.addChildNode(sem_node);

                                        return new SavePoint(--pos, true);
                                    } else if (token.getSortCode() == SortCode.GROUP) {
                                        return group_condition(--pos, astNode);
                                    } else if (token.getSortCode() == SortCode.ORDER) {
                                        return order_condition(--pos, astNode);
                                    } else if (token.getSortCode() == SortCode.AND ||
                                            token.getSortCode() == SortCode.OR) {
                                        ASTNode or_and_node = new ASTNode(false, true, token.getValue());
                                        astNode.addChildNode(or_and_node);

                                        token = getToken(pos++);
                                        continue;
                                    }
                                }
                            }
                        } else if (token.getSortCode() == SortCode.STRING ||
                                token.getSortCode() == SortCode.NUMBER) {
                            //限制条件查询
                            ASTNode in_value_list = new ASTNode(false, false, "in_value_list");
                            astNode.addChildNode(in_value_list);

                            SavePoint value_sp = value_list(--pos, in_value_list);
                            pos = value_sp.pos;
                            if (value_sp.correct) {
                                token = getToken(pos++);
                                if (token.getSortCode() == SortCode.RPARENT) {
                                    ASTNode rp = new ASTNode(false, true, token.getValue());
                                    astNode.addChildNode(rp);

                                    token = getToken(pos++);
                                    if (token.getSortCode() == SortCode.AND ||
                                            token.getSortCode() == SortCode.OR) {
                                        ASTNode or_and = new ASTNode(false ,true, token.getValue());
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
                    ASTNode rps_node = new ASTNode(false, true, token.getValue());
                    astNode.addChildNode(rps_node);

                    token = getToken(pos++);
                    if (token.getSortCode() == SortCode.IDENTIFIED ||
                            token.getSortCode() == SortCode.NUMBER ||
                            token.getSortCode() == SortCode.STRING) {
                        ASTNode right_op = new ASTNode(false, true, token.getValue());
                        astNode.addChildNode(right_op);

                        token = getToken(pos++);
                        if (token.getSortCode() == SortCode.AND ||
                                token.getSortCode() == SortCode.OR) {
                            ASTNode and_or = new ASTNode(false, true, token.getValue());
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
            ASTNode sem_node = new ASTNode(false, true, token.getValue());
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
                ASTNode str_num_node = new ASTNode(false, true, token.getValue());
                astNode.addChildNode(str_num_node);

                token = getToken(pos++);
                if (token.getSortCode() == SortCode.COMMA) {
                    ASTNode comma_node = new ASTNode(false, true, token.getValue());
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

        Token token = getToken(pos++);
        if (token.getSortCode() == SortCode.CREATE) {
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
        } else if (token.getSortCode() == SortCode.DROP) {
            ASTNode drop_node = new ASTNode(false, true, token.getValue());
            astNode.addChildNode(drop_node);

            token = getToken(pos++);
            if (token.getSortCode() == SortCode.DATABASE) {
                ASTNode database_node = new ASTNode(false, true, token.getValue());
                astNode.addChildNode(database_node);

                token = getToken(pos++);
                if (token.getSortCode() == SortCode.IDENTIFIED) {
                    //database's name
                    ASTNode id_node = new ASTNode(false, true, token.getValue());
                    astNode.addChildNode(id_node);

                    token = getToken(pos);
                    if (token.getSortCode() == SortCode.SEMICOLON) {
                        ASTNode sem_node = new ASTNode(false, true, token.getValue());
                        astNode.addChildNode(sem_node);

                        return new SavePoint(pos, true);
                    }
                }
            } else if (token.getSortCode() == SortCode.TABLE) {
                ASTNode table_node = new ASTNode(false, true, token.getValue());
                astNode.addChildNode(table_node);

                token = getToken(pos++);
                if (token.getSortCode() == SortCode.IDENTIFIED) {
                    ASTNode id_node = new ASTNode(false, true, token.getValue());
                    astNode.addChildNode(id_node);

                    token = getToken(pos);
                    if (token.getSortCode() == SortCode.SEMICOLON) {
                        ASTNode sem_node = new ASTNode(false, true, token.getValue());
                        astNode.addChildNode(sem_node);

                        return new SavePoint(pos, true);
                    }
                }
            }
        } else if (token.getSortCode() == SortCode.ALTER) {
            //暂时 alert只支持对表的修改
            List<SortCode> alert_list = SQLAlterPattern.getAlterPattern();

            boolean fail = false;
            for (SortCode sortCode : alert_list) {
                if (sortCode == SortCode.OPTION) {
                    if (token.getSortCode() == SortCode.ADD) {
                        ASTNode add_node  = new ASTNode(false, true, token.getValue());
                        astNode.addChildNode(add_node);
                    } else if (token.getSortCode() == SortCode.DROP) {
                        ASTNode drop_node = new ASTNode(false, true, token.getValue());
                        astNode.addChildNode(drop_node);
                    } else if (token.getSortCode() == SortCode.ALTER) {
                        ASTNode alter_node = new ASTNode(false, true, token.getValue());
                        astNode.addChildNode(alter_node);
                    } else {
                        break;
                    }
                    token = getToken(pos++);
                    continue;
                }
                if (token.getSortCode() == sortCode) {
                    ASTNode tmp_node = new ASTNode(false, true, token.getValue());
                    astNode.addChildNode(tmp_node);

                    token = getToken(pos++);
                    continue;
                }
                fail = true;
                break;
            }
            //解析缺省 datatype
            if (!fail) {
                if (token.getSortCode() == SortCode.SEMICOLON) {
                    //缺省
                    ASTNode sem_node = new ASTNode(false ,true, token.getValue());
                    astNode.addChildNode(sem_node);

                    return new SavePoint(--pos, true);
                } else if (token.getSortCode() == SortCode.NUMBER ||
                        token.getSortCode() == SortCode.STRING) {
                    //非缺省
                    ASTNode num_str_node = new ASTNode(false, true, token.getValue());
                    astNode.addChildNode(num_str_node);

                    token = getToken(pos);
                    if (token.getSortCode() == SortCode.SEMICOLON) {
                        ASTNode sem_node = new ASTNode(false, true, token.getValue());
                        astNode.addChildNode(sem_node);

                        return new SavePoint(--pos, true);
                    }
                }
            }
        } else if (token.getSortCode() == SortCode.SHOW) {
            ASTNode show_node = new ASTNode(false, true, token.getValue());
            astNode.addChildNode(show_node);

            token = getToken(pos++);
            if (token.getSortCode() == SortCode.DATABASES) {
                ASTNode db_node = new ASTNode(false, true, token.getValue());
                astNode.addChildNode(db_node);

                token = getToken(pos++);
                if (token.getSortCode() == SortCode.SEMICOLON) {
                    ASTNode sem_node = new ASTNode(false, true, token.getValue());
                    astNode.addChildNode(sem_node);

                    return new SavePoint(--pos, true);
                }
            } else if (token.getSortCode() == SortCode.TABLES) {
                ASTNode tab_node = new ASTNode(false, true, token.getValue());
                astNode.addChildNode(tab_node);

                token = getToken(pos);
                if(token.getSortCode() == SortCode.SEMICOLON) {
                    ASTNode sem_node = new ASTNode(false, true, token.getValue());
                    astNode.addChildNode(sem_node);

                    return new SavePoint(pos, true);
                }
            }
        } else if (token.getSortCode() == SortCode.USE) {
            ASTNode use_node = new ASTNode(false, true, token.getValue());
            astNode.addChildNode(use_node);

            token = getToken(pos++);
            if (token.getSortCode() == SortCode.IDENTIFIED) {
                ASTNode db_name = new ASTNode(false, true, token.getValue());
                astNode.addChildNode(db_name);

                token = getToken(pos);
                if (token.getSortCode() == SortCode.SEMICOLON) {
                    ASTNode sem_node = new ASTNode(false, true, token.getValue());
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
