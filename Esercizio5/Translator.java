import java.io.*;

public class Translator {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    SymbolTable st = new SymbolTable();
    CodeGenerator code = new CodeGenerator();
    int count=0;

    public Translator(Lexer l, BufferedReader br) {
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

    public void prog() {
	// ... completare ...
		int lnext_prog = code.newLabel();
		statlist(lnext_prog);
		code.emitLabel(lnext_prog);
		match(Tag.EOF);
		try {
			code.toJasmin();
		}
		catch(java.io.IOException e) {
			System.out.println("IO error\n");
		};
	// ... completare ...
    }

    public void stat(int lnext) {
        switch(look.tag) {
	// ... completare ...
            case Tag.PRINT:
                match(Tag.PRINT);
                match('(');
                expr();
                code.emit(OpCode.invokestatic,1);
                match(')');
                break;
            case Tag.READ:
                match(Tag.READ);
                match('(');
                if (look.tag==Tag.ID) {
                    int read_id_addr = st.lookupAddress(((Word)look).lexeme);
                    if (read_id_addr==-1) {
                        read_id_addr = count;
                        st.insert(((Word)look).lexeme,count++);
                    }
                    match(Tag.ID);
                    match(')');
                    code.emit(OpCode.invokestatic,0);
                    code.emit(OpCode.istore,read_id_addr);
                }
                else
                    error("Error in grammar (stat) after read( with " + look);
                break;
	// ... completare ...
	}
    }

    // ... completare ...

    private void b_expr(int ltrue, int lfalse) {
	// ... completare ...
		expr();
		if (look == Word.eq) {
			match(Tag.RELOP);
			expr();
			// ... completare ...
		}
	// ... completare ...
    }

    // ... completare ...

    private void exprp() {
        switch(look.tag) {
            case '+':
                match('+');
                term();
                code.emit(OpCode.iadd);
                exprp();
                break;
	// ... completare ...
     }
   }
    // ... completare ...
}
