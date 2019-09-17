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
        if(look.tag == Tag.ID || look.tag == Tag.PRINT || look.tag == Tag.READ || look.tag == Tag.CASE || look.tag == Tag.WHILE || look.tag == '{'){
          statlist();
          match(Tag.EOF);
        }else
          error("Error in grammar (start) after read " + look);
    }

    private void statlist(){
        if(look.tag == Tag.ID || look.tag == Tag.PRINT || look.tag == Tag.READ || look.tag == Tag.CASE || look.tag == Tag.WHILE || look.tag == '{'){
            stat();
            statlisp();
        }else
            error("Error in grammar (expr) after read " + look);
    }

    private void statlisp(){
        switch(look.tag){
            case ';':
                match(';');
                stat();
                statlisp();
                break;
            //Epsilon transition
            case Tag.ID:
            case Tag.PRINT:
            case Tag.READ:
            case Tag.CASE:
            case Tag.WHILE:
            case '}':
            case Tag.EOF:
            break;

        default:
            error("Error in grammar (expr) after read " + look);
        }
    }

    private void stat(){
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
                    error("Error: (stat -> print) Expected ) instead" + look);
                break;

            case Tag.READ:
                match(Tag.READ);
                if(look.tag == '(')
                    match('(');
                else
                    error("Error: (stat -> read) Expected ) instead" + look);
                if(look.tag == Tag.ID)
                  match(Tag.ID);
                else
                  error("Error: (stat -> read) Expected ID instead" + look);
                if(look.tag == ')')
                    match(')');
                else
                    error("Error: (stat -> read) Expected ) instead" + look);
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
                    error("Error (stat) expected } instead " + look);
                break;

            case '{':
                match('{');
                statlist();
                if(look.tag == '}')
                    match('}');
                else
                    error("Error (stat) expected } instead " + look);
                break;

            default:
              error("Error in grammar (stat) after read " + look);
        }
    }

    private void whenlist(){
        if(look.tag == Tag.WHEN){
            whenitem();
            whenlistp();
        }else
            error("Error in grammar (whenlist) expected when instead " +look);
    }

    private void whenlistp(){
        switch(look.tag){
            case Tag.WHEN:
                whenitem();
                whenlistp();
            break;
            case Tag.ELSE:
            break;

        default:
        error("Error in grammar (whenlistp)");
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
                error("Error in grammar (whenitem) expected ) instead " +look);
        }else
            error("Error in grammar (whenitem) expected when instead " +look);
    }

    private void bexpr(){
        if(look.tag == Tag.NUM || look.tag == Tag.ID){
            expr();
            match(Tag.RELOP);
            if(look.tag == Tag.RELOP)
                match(Tag.RELOP);
            expr();
        }else
            error("Error in grammar (bexpr) after read " +look);
    }

    private void expr() {
        if(look.tag == Tag.NUM || look.tag == '(' || look.tag == Tag.ID){
            term();
            exprp();
        }else
            error("Error in grammar (expr) after read " +look);
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
          error("Error in grammar (exprp) after read " +look);
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
            error("Error in grammar (term) after read " +look);
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

            //Epsilon transitions
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
            error("Error in grammar (termp) after read " +look);
        }
    }

    private void fact() {
        switch(look.tag){
            case '(':
                match('(');
                expr();
                if(look.tag == ')')
                    match(')');
                else
                    error("Error in grammar (fact) expected ) instead " +look);
                break;
            case Tag.NUM:
                match(Tag.NUM);
                break;
            case Tag.ID:
                match(Tag.ID);
                break;
            default:
                error("Error in grammar (fact) after read " +look);
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
