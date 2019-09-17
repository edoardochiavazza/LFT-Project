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
      if(look.tag == Tag.ID || look.tag == Tag.PRINT || look.tag == Tag.READ || look.tag == Tag.CASE || look.tag == Tag.WHILE || look.tag == '{'){
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
      }else
            error("Error in grammar (prog) after read( with " + look);
      }

    private void statlist(int lnext_prog){
        if(look.tag == Tag.ID || look.tag == Tag.PRINT || look.tag == Tag.READ || look.tag == Tag.CASE || look.tag == Tag.WHILE || look.tag == '{'){
            int slnext = code.newLabel();
            stat(slnext);
            code.emitLabel(slnext);
            statlisp(lnext_prog);
        }else
            error("Error in grammar (statlist) after read( with " + look);
    }

    private void statlisp(int lnext_prog){
        switch(look.tag){
            case ';':
                match(';');
                int s_next = code.newLabel();
                stat(s_next);
                code.emitLabel(s_next);
                statlisp(lnext_prog);
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
            error("Error in grammar (statlisp) after read " + look);
        }
    }

    public void stat(int lnext) {
        int ltrue, lfalse;
        switch(look.tag) {
            case Tag.ID:
                int read_id_addr = st.lookupAddress(((Word)look).lexeme);
                if (read_id_addr==-1) {
                    read_id_addr = count;
                    st.insert(((Word)look).lexeme,count++);
                }
                match(Tag.ID);
                if(look.tag == Tag.ASSIGN) {
                  match(Tag.ASSIGN);
                  expr();
                  code.emit(OpCode.istore, read_id_addr);
                }else
                    error("expected assignment after ID");
                break;

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
                if (look.tag == Tag.ID) {
                     read_id_addr = st.lookupAddress(((Word)look).lexeme);
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

            case Tag.CASE:
                match(Tag.CASE);
                whenlist(lnext);
                if(look.tag == Tag.ELSE){
                    match(Tag.ELSE);
                    stat(lnext);
                }else
                    error("Expected else");
                break;

            case Tag.WHILE:
                match(Tag.WHILE);
                if(look.tag == '('){
                  match('(');
                  int begin = code.newLabel();
                  code.emitLabel(begin);
                  ltrue = code.newLabel();
                  lfalse = lnext;
                  bexpr(ltrue, lfalse);
                  if(look.tag == ')'){
                      match(')');
                      code.emitLabel(ltrue);
                      stat(begin);
                      code.emit(OpCode.GOto,begin);
                  }else
                      error("(stat -> While) Expected ) instead " + look);
                  }else error("(stat -> While) Expected ( instead " + look);
                  break;

            case '{':
                  match('{');
                  statlist(lnext);
                  if(look.tag == '}')
                      match('}');
                  else
                      error("(stat -> } ) Expected }");
                break;

            default:
              error("Error: stat procedure (input character): " + look);
	}
    }

    private void whenlist(int lnext_prog){
        if(look.tag == Tag.WHEN){
            int wlnext = code.newLabel();
            whenitem(lnext_prog, wlnext);
            code.emitLabel(wlnext);
            whenlistp(lnext_prog);
        }else
            error("Expected when in whenlist");
    }

    private void whenlistp(int lnext_prog){
        switch(look.tag){
            case Tag.WHEN:
                int wlpnext = code.newLabel();
                whenitem(lnext_prog, wlpnext);
                code.emitLabel(wlpnext);
                whenlistp(lnext_prog);
            break;

            //Epsilon transition
            case Tag.ELSE:
            break;

            default:
              error("Error: whenlistp procedure (input character): " + look.tag);
        }
    }

    private void whenitem(int lnext_prog, int wlnext){
        int b_true, b_false;
        if(look.tag == Tag.WHEN){
            match(Tag.WHEN);
            match('(');
            b_true = code.newLabel();
            b_false = wlnext;
            bexpr(b_false);
            if(look.tag == ')'){
                match(')');
                code.emitLabel(b_true);
                stat(lnext_prog);
                code.emit(OpCode.GOto, lnext_prog);
            }else
                error("Error in whenitem expected ) instead " + look);
        }else
            error("Error in whenitem, expected when instead " +look);
    }


    private void bexpr(int lfalse) {
	       if(look.tag == Tag.NUM || look.tag == Tag.ID){
		             expr();
		             if (look == Word.eq) {
			                match(Tag.RELOP);
			                expr();
                      code.emit(OpCode.if_icmpne, lfalse);
                }else if(look == Word.gt){
                      match(Tag.RELOP);
                      expr();
                      code.emit(OpCode.if_icmple, lfalse);
                }else if(look == Word.lt){
                      match(Tag.RELOP);
                      expr();
                      code.emit(OpCode.if_icmpge,lfalse);
                }else if(look == Word.le){
                      match(Tag.RELOP);
                      expr();
                      code.emit(OpCode.if_icmpgt,lfalse);
                }else if(look == Word.ne){
                      match(Tag.RELOP);
                      expr();
                      code.emit(OpCode.if_icmpeq,lfalse);
                }else if(look == Word.ge){
                      match(Tag.RELOP);
                      expr();
                      code.emit(OpCode.if_icmplt,lfalse);
                }else
                      error("Expected one of symbol of RELOP instead " +look);
        }
  }

  private void expr() {
      if(look.tag == Tag.NUM || look.tag == '(' || look.tag == Tag.ID){
          term();
          exprp();
      }else
          error("Error in grammar (expr) after read " + look);
      }

    private void exprp() {
        switch(look.tag) {
            case '+':
                match('+');
                term();
                code.emit(OpCode.iadd);
                exprp();
                break;
            case '-':
                match('-');
                term();
                code.emit(OpCode.isub);
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
               error("Error in grammar (exprp) after read " + look);
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
              error("Error in grammar (term) after read " + look);
       }
   }

   private void termp() {
       switch(look.tag){
           case '*':
               match('*');
               fact();
               code.emit(OpCode.imul);
               termp();
               break;
           case '/':
               match('/');
               fact();
               code.emit(OpCode.idiv);
               termp();
               break;
          //Epsilon transition
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
           error("Error in grammar (termp) after read " + look);
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
               code.emit(OpCode.ldc,Integer.parseInt(((NumberTok)look).lexeme));
               match(Tag.NUM);
               break;
           case Tag.ID:
               int read_id_addr = st.lookupAddress(((Word)look).lexeme);
               if (read_id_addr == -1)
                 error("undeclared variable " + ((Word)look).lexeme);
               code.emit(OpCode.iload,read_id_addr);
               match(Tag.ID);
               break;
           default:
               error("Error in grammar (fact) after read " + look);
       }
   }
   public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "Prova.pas";
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Translator tr = new Translator(lex, br);
            tr.prog();
            System.out.println("\nFile Output.j generato!");
            System.out.println("Digita 'java -jar jasmin.jar Output.j' per il file Output.class e 'java Output' per eseguirlo.\n");
            br.close();
        } catch (IOException e) {e.printStackTrace(); }
    }
}
