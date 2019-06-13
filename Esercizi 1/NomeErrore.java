public class NomeErrore { // riconosce il nome con una lettera sbagliata
    public static boolean scan(String s) {
        int state = 0;
        int i = 0;

        while (state >= 0 && i < s.length()) {
            final char ch = s.charAt(i++);

            switch (state) {
            case 0:
            if(ch == 101)
              state = 1;
            else
                state = 2;
                break;

            case 1:
              if(ch == 100)
                state = 3;
              else
                state = 4;
                break;

            case 3:
              if(ch == 111)
                state = 5;
              else
                state = 6;
                break;

            case 5:
              if(ch == 97)
                state = 7;
              else
                state = 10;
              break;

            case 7:
              if(ch == 114)
                state = 9;
              else
                state = 10;
              break;

            case 9:
              if(ch == 100)
                state = 11;
              else
                state = 12;
            break;

            case 11:
              if(ch != 0)
              state = 13;
              break;

            case 13:
              if(ch != 0)
                state = -1;
              break;

            case 2:
              if(ch == 100)
                state = 4;
              else
                state = -1;
              break;

            case 4:
              if(ch == 111)
                state = 6;
              else
                state = -1;
              break;

            case 6:
              if(ch == 97 )
                state = 8;
              else
                state = -1;
              break;

            case 8:
              if(ch == 114)
                state = 10;
              else
                state = -1;
              break;

            case 10:
              if(ch == 100)
                state = 12;
              else
                state = -1;
              break;

            case 12:
              if(ch == 111)
                state = 13;
              else
                state = -1;
              break;
            }
      }
        return state == 13;
    }

    public static void main(String[] args) {
        System.out.println(scan(args[0]) ? "Riconosciuto" : "Falzo");
    }
}
