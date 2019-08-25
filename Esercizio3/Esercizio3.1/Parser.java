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
	if(look.tag == Tag.NUM || look.tag == '('){
        expr();
    	match(Tag.EOF);
    }
	else
        error("Error in start");
    }

    private void expr() {
    if(look.tag == Tag.NUM || look.tag == '('){
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
           break;

          default:
          error("Error in exprp");
	// ... completare ...
	   }
    }

    private void term() {
        switch(look.tag){
            case Tag.NUM:
            fact();
            termp();
            break;

            case '(':
            fact();
            termp();
            break;

            default:
            error("Error in term");
        }// ... completare ...

    }

    private void termp() {
        // ... completare ...
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
                break;
            default:
            error("Error in termp");
        }
    }

    private void fact() {
        // ... completare ...
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
            default:
                error("Error in fact");
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "Prova.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser parser = new Parser(lex, br);
            parser.start();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}
