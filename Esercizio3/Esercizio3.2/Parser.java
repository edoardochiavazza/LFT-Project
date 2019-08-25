import java.io.*;

public class Parser {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Parser(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    void move() {
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

    void error(String s) {
	throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t) {
	if (look.tag == t) {
	    if (look.tag != Tag.EOF) move();
	} else error("syntax error");
    }

    public void start() {
        statlist();
        match(Tag.EOF);
    }

    private void statlist(){
        //System.out.println("Look.tag in statlist = " + look.tag);
        if(look.tag == Tag.ID || look.tag == Tag.PRINT || look.tag == Tag.READ || look.tag == Tag.CASE || look.tag == Tag.WHILE || look.tag == '{'){
            stat();
            statlisp();
        }else
            error("Error in stalist");
    }

    private void statlisp(){
        //System.out.println("Look.tag in statlisp = " + look.tag);
        switch(look.tag){
            case ';':
                match(';');
                stat();
                statlisp();
                break;

            case Tag.ID:
            case Tag.PRINT:
            case Tag.READ:
            case Tag.CASE:
            case Tag.WHILE:
            case '}':
            case Tag.EOF:
            break;
        default:
            System.out.println("Error here");
            error("Error in statlisp");
        }
    }

    private void stat(){
        //System.out.println("Look.tag in stat = " + look.tag);
        switch(look.tag){
            case Tag.ID:
                match(Tag.ID);
                match(Tag.ASSIGN);
                expr();
                break;

            case Tag.PRINT:
                match(Tag.PRINT);
                match('(');
                expr();
                if(look.tag == ')')
                    match(')');
                else
                    error("Expected )");
                break;

            case Tag.READ:
                match(Tag.READ);
                match('(');
                match(Tag.ID);
                match(')');
                break;

            case Tag.CASE:
                match(Tag.CASE);
                whenlist();
                if(look.tag == Tag.ELSE){
                    match(Tag.ELSE);
                    stat();
                }else
                    error("Expected else");
                break;

            case Tag.WHILE:
                match(Tag.WHILE);
                match('(');
                bexpr();
                if(look.tag == ')'){
                    match(')');
                    stat();
                }else
                    error("Expected )");
                break;

            case '{':
                match('{');
                statlist();
                if(look.tag == '}')
                    match('}');
                else
                    error("Expected }");
                break;
        }
    }

    private void whenlist(){
        if(look.tag == Tag.WHEN){
            whenitem();
            whenlistp();
        }else
            error("Expected when");
    }

    private void whenlistp(){
        switch(look.tag){
            case Tag.WHEN:
                whenitem();
                whenlistp();
            break;
            case Tag.ELSE:
            break;
        }
    }

    private void whenitem(){
        if(look.tag == Tag.WHEN){
            match(Tag.WHEN);
            match('(');
            bexpr();
            if(look.tag == ')'){
                match(')');
                stat();
            }else
                error("Expected )");
        }else
            error("Expected when");
    }
    
    private void bexpr(){
        if(look.tag == Tag.NUM || look.tag == Tag.ID){
            expr();
            match(Tag.RELOP);
            if(look.tag == Tag.RELOP)
                match(Tag.RELOP);
            expr();
        }else
            error("Error in bexpr");
    }

    private void expr() {
        if(look.tag == Tag.NUM || look.tag == '(' || look.tag == Tag.ID){
            term();
            exprp();
        }else
            error("Error in expr");
        }

    private void exprp() {
	switch (look.tag) {
	       case '+':
           match('+');
           term();
           exprp();
           break;

           case '-':
           match('-');
           term();
           exprp();
           break;

           case Tag.EOF:
           case')':
           case Tag.RELOP:
           case ';':
           case Tag.ELSE:
           case '}':
           break;

          default:
          error("Error in exprp");
	   }
    }

    private void term() {
        switch(look.tag){
            case Tag.ID:
            case Tag.NUM:
            case '(':
            fact();
            termp();
            break;

            default:
            error("Error in term");
        }
    }

    private void termp() {
        switch(look.tag){
            case '*':
                match('*');
                fact();
                termp();
                break;
            case '/':
                match('/');
                fact();
                termp();
                break;
            case ')':
            case '+':
            case '-':
            case Tag.EOF:
            case Tag.RELOP:
            case ';':
            case Tag.ELSE:
            case '}':
                break;
            default:
            error("Error in termp");
        }
    }

    private void fact() {
        switch(look.tag){
            case '(':
                match('(');
                expr();
                if(look.tag == ')')
                    match(')');
                break;
            case Tag.NUM:
                match(Tag.NUM);
                break;
            case Tag.ID:
                match(Tag.ID);
                break;
            default:
                error("Error in fact");
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "factorial.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser parser = new Parser(lex, br);
            parser.start();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}
