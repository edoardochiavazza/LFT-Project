public class CommentiMultipli { // riconosce i commenti.
    public static boolean scan(String s) {
        int state = 0;
        int i = 0;

        while (state >= 0 && i < s.length()) {
            final char ch = s.charAt(i++);

            switch (state) {
            case 0:
              if (ch == 97 || ch == 42)
                state = 0;
              else if(ch == 47)
                state = 1;
              else
                state = -1;
                break;

            case 1:
              if (ch == 97)
                state = 0;
              else if(ch == 47)
                state = 1;
              else if(ch == 42)
                state = 2;
              else
                state = -1;
                break;

            case 2:
              if(ch == 47 || ch == 97)
                state = 2;
              else if(ch == 42)
                state = 3;
              else
                state = -1;
                break;

            case 3:
              if(ch == 97)
                state = 2;
              else if(ch == 47)
                state = 0;
              else if(ch == 42)
                state = 3;
              else
                state = -1;
              break;
            }
      }
        return state == 1 || state == 0;
    }

    public static void main(String[] args) {
        System.out.println(scan(args[0]) ? "OK" : "NOPE");
    }
}
