package Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Exception.SytaxErrorsException;
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
        }else if (value.equals("SELECT")){

        }
        return null;
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
                    if (token.getSortCode() == SortCode.SEMICOLON){
                        ASTNode semi_node = new ASTNode(false, true, token.getValue());
                        astNode.addChildNode(semi_node);

                        return new SavePoint(pos, true);
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
        ASTNode rp = new ASTNode(false, true, token.getValue());
        //table层面的)
        parent_match--;
        astNode.addChildNode(rp);

        return new SavePoint(++pos, true);//分号交给上层执行
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
                ASTNode rp = new ASTNode(false, true, token.getValue());
                astNode.addChildNode(rp);

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
    //解析参数列表
    private SavePoint ParamsList(int pos, ASTNode astNode) throws Exception {
        Token token = getToken(pos++);

        while (token.getSortCode() != SortCode.RPARENT) {
            if (token.getSortCode() == SortCode.IDENTIFIED) {
                ASTNode id_node = new ASTNode(false, true, token.getValue());
                astNode.addChildNode(id_node);

                token = getToken(pos++);
                //右括号交给上层处理
                if (token.getSortCode() == SortCode.RPARENT) {
                    return new SavePoint(--pos, true);
                }else if (token.getSortCode() == SortCode.COMMA) {
                    ASTNode ast_comma = new ASTNode(false, true, token.getValue());
                    astNode.addChildNode(ast_comma);

                    token = getToken(pos++);
                }else {
                    return new SavePoint(pos, false);
                }
            }else {
                return new SavePoint(pos, false);
            }
        }
        return new SavePoint(pos, false);
    }
    //解析check语句的参数列表
    private SavePoint CheckList(int pos, ASTNode astNode) throws Exception {
        Token token = getToken(pos++);

        while (token.getSortCode() != SortCode.RPARENT) {
            if (token.getSortCode() == SortCode.IDENTIFIED ||
                    token.getSortCode() == SortCode.NUMBER) {
                ASTNode id_num_node = new ASTNode(false,true, token.getValue());
                astNode.addChildNode(id_num_node);

                token = getToken(pos++);
                if (RelationOps.containValue(token.getValue())) {
                    ASTNode ro = new ASTNode(false, true, token.getValue());
                    astNode.addChildNode(ro);

                    token = getToken(pos++);
                    // TODO: 2017/8/4 还有个字符串类型需要添加
                    if (token.getSortCode() == SortCode.NUMBER ||
                            token.getSortCode() == SortCode.IDENTIFIED) {
                        ASTNode second_node = new ASTNode(false, true, token.getValue());
                        astNode.addChildNode(second_node);

                        token = getToken(pos++);
                        if (token.getSortCode() == SortCode.RPARENT) {
                            return new SavePoint(--pos, true);
                        } else if (token.getSortCode() == SortCode.OR ||
                                token.getSortCode() == SortCode.AND) {
                            ASTNode and_or = new ASTNode(false, true, token.getValue());
                            astNode.addChildNode(and_or);

                            token = getToken(pos++);
                            continue;
                        }
                    }
                }
            }
            return new SavePoint(pos, false);
        }
        return new SavePoint(pos, false);
    }
//
//    public SavePoint CreateDatabase(int pos, ASTNode astNode) throws Exception {
//
//    }

    public Token getToken(int pos) throws Exception{
        if (pos >= tokens.size()) {
            throw new SytaxErrorsException(getClass().toString());
        }
        return tokens.get(pos);
    }
}
