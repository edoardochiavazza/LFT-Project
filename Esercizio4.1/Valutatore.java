import java.io.*;

public class Valutatore {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Valutatore(Lexer l, BufferedReader br) {
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
        }else error("syntax error");
    }

    public void start() {
	int expr_val;
    if(look.tag == '(' || look.tag == Tag.NUM){
        expr_val = expr();
    	match(Tag.EOF);
        System.out.println(expr_val);
    }else
        error("Error in start symbol");
    }

    private int expr() {
	int term_val, exprp_val, exprp_i;
    if(look.tag == Tag.NUM || look.tag == '('){
        term_val = term();
        exprp_i = term_val;
    	exprp_val = exprp(term_val);
        return exprp_val;
    }else
        error("Error in expr");
    return -1;
    }

    private int exprp(int exprp_i) {
	int term_val, exprp_val;
	switch (look.tag) {
	case '+':
                match('+');
                term_val = term();
                exprp_val = exprp(exprp_i + term_val);
                return exprp_val;
    case '-':
                match('-');
                term_val = term();
                exprp_val = exprp(exprp_i - term_val);
                return exprp_val;

    case Tag.EOF:
    case')':
                exprp_val = exprp_i;
                return exprp_val;
    default:
               error("Error in exprp");
               break;
	}
    return -1;
    }

    private int term() {
        int termp_i, term_val;
        switch(look.tag){
            case Tag.NUM:
            termp_i = fact();
            term_val = termp(termp_i);
            return term_val;

            case '(':
            termp_i = fact();
            term_val = termp(termp_i);
            return term_val;

            default:
            error("Error in term");
        }
        return -1;
    }

    private int termp(int termp_i) {
        int fact_val, termp_val;
        switch(look.tag){
            case '*':
                match('*');
                termp_i = fact() * termp_i;
                termp_val = termp(termp_i);
                return termp_val;

            case '/':
                match('/');
                termp_i =  termp_i / fact();
                termp_val = termp(termp_i);
                return termp_val;

            case ')':
            case '+':
            case '-':
            case Tag.EOF:
                termp_val = termp_i;
                return termp_val;

            default:
            error("Error in termp");
        }
        return -1;
    }

    private int fact() {
        int fact_val;
        switch(look.tag){
            case '(':
                match('(');
                fact_val = expr();
                if(look.tag == ')')
                    match(')');
                return fact_val;

            case Tag.NUM:
                fact_val = Integer.parseInt(((NumberTok)look).lexeme);
                match(Tag.NUM);
                return fact_val;

            default:
                error("Error in fact");
        }
        return -1;
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "Prova.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Valutatore valutatore = new Valutatore(lex, br);
            valutatore.start();
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}
