import java.io.*;
import java.util.*;

public class Lexer {

    public static int line = 1;
    private char peek = ' ';


    private void readch(BufferedReader br) {
        try {
            peek = (char) br.read();
        } catch (IOException exc) {
            peek = (char) -1; // ERROR
        }
    }

    public Token lexical_scan(BufferedReader br) {
        while (peek == ' ' || peek == '\t' || peek == '\n'  || peek == '\r') {
            if (peek == '\n') line++;
            readch(br);
        }

        switch (peek) {
            case '!':
                peek = ' ';
                return Token.not;

            case '(':
                peek = ' ';
                return Token.lpt;

            case ')':
                peek = ' ';
                return Token.rpt;

            case '{':
                peek = ' ';
                return Token.lpg;

            case '}':
                peek = ' ';
                return Token.rpg;

            case '+':
                peek = ' ';
                return Token.plus;

            case '-':
                peek = ' ';
                return Token.minus;

            case '/':
                readch(br);
                if(peek == '/'){
                    while(peek != '\n'){
                        readch(br);
                    }
                }else if(peek == '*'){
                    int state = 0;
                    while(state != 2 && peek != (char) -1){
                        readch(br);
                        switch(state){
                            case 0:
                                if(peek != '*')
                                    state = 0;
                                else if(peek == '*')
                                    state = 1;
                                else
                                    state = -1;
                            break;

                            case 1:
                                if(peek == '/')
                                    state = 2;
                                else if(peek == '*')
                                    state = 1;
                                else if(peek != '/' && peek != '*')
                                    state = 0;
                            break;
                        }
                    }
                    if(state != 2){
                        System.err.println("ERROR: No closed comment");
                        return null;
                    }
                }
                else
                     peek = ' ';
                     return Token.div;

            case ';':
                peek = ' ';
                return Token.semicolon;

            case '*':
                peek = ' ';
                return Token.mult;

            case '&':
                readch(br);
                if (peek == '&') {
                    peek = ' ';
                    return Word.and;
                } else {
                    System.err.println("Erroneous character" + " after & : " + peek);
                    return null;
                }
            case '|':
                readch(br);
                if(peek == '|'){
                    peek = ' ';
                    return Word.or;
                }else{
                    System.err.println("Erroneous character" + " after & : " + peek);
                    return null;
                }

            case '<':
                readch(br);
                if(peek == '='){
                    peek = ' ';
                    return Word.le;
                }else{
                    return Word.lt;
                }

            case '>':
                readch(br);
                if(peek == '='){
                    peek = ' ';
                    return Word.ge;
                }else{
                    return Word.gt;
                }

            case '=':
                readch(br);
                if(peek == '='){
                    peek = ' ';
                    return Word.eq;
                }else{
                    System.err.println("Erroneous character"+ " after & : " + peek);
                    return null;
                }

            case ':':
                readch(br);
                if(peek == '='){
                    peek = ' ';
                    return Word.assign;
                }else{
                    System.err.println("Erroneous character"+ " after & : " + peek);
                    return null;
                    }

            case (char)-1:
                return new Token(Tag.EOF);

            default:
                String toReturn = "";
                if(Character.isLetter(peek) || Character.isDigit(peek) || peek == '_'){
                    while(Character.isDigit(peek) || Character.isLetter(peek) || peek == '_'){
                        toReturn += peek;
                        readch(br);
                    }
                    if(toReturn.compareTo("case") == 0)
                        return Word.casetok;
                    else if(toReturn.compareTo("when") == 0)
                        return Word.when;
                    else if(toReturn.compareTo("then") == 0)
                        return Word.then;
                    else if(toReturn.compareTo("else") == 0)
                        return Word.elsetok;
                    else if(toReturn.compareTo("while") == 0)
                        return Word.whiletok;
                    else if(toReturn.compareTo("do") == 0)
                        return Word.dotok;
                    else if(toReturn.compareTo("printf") == 0)
                        return Word.print;
                    else if(toReturn.compareTo("read") == 0)
                        return Word.read;
                    else
                        return new Word(Tag.ID,toReturn);
                } else if (Character.isDigit(peek)) {
                    while(Character.isDigit(peek)){
                        toReturn += peek;
                        readch(br);
                    }
                    return new NumberTok(toReturn);
                }else{
                        System.err.println("Erroneous character: "
                                + peek );
                        return null;
                }

         }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "Prova.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Token tok;
            do {
                tok = lex.lexical_scan(br);
                System.out.println("Scan: " + tok);
            } while (tok.tag != Tag.EOF);
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}
