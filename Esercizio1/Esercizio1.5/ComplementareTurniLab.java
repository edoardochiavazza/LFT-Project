public class ComplementareTurniLab {

  public static boolean scan(String s) {

    int i = 0, state = 0;

    while(state >= 0 && i < s.length()) {
      char ch = s.charAt(i++);

      switch(state){

        case 0:
          if(is2turn(ch))
            state = 1;
          else if(is3turn(ch))
            state = 2;
          else
            state = -1;
        break;

        case 1:
          if(isNumber(ch))
            state = isEven(Character.getNumericValue(ch)) ? 1 : 3;
          else if(!isInAlphabet(ch))
            state = -1;
        break;

        case 2:
          if(isNumber(ch))
            state = (!isEven(Character.getNumericValue(ch))) ? 2 : 4;
          else if(!isInAlphabet(ch))
              state = -1;
        break;

        case 3:
          if(isNumber(ch))
            state = isEven(Character.getNumericValue(ch)) ? 1 : 3;
          else
            state = -1;
        break;

        case 4:
          if(isNumber(ch))
            state = isEven(Character.getNumericValue(ch)) ? 4 : 2;
      }

    }
    return state == 2 || state == 1;
  }

  public static boolean isNumber(char c) {
    return ((c >= 49) && (c <= 57));
  }

  public static boolean isInAlphabet(char c) {
    return (((c >= 65) && (c <= 90)) || ((c >= 97) && (c <= 122)));
  }

  public static boolean isEven(int i) {
    return i % 2 == 0;
  }

  public static boolean is2turn(char c) {
    return ((c >= 65) && (c <= 75));
  }

  public static boolean is3turn(char c) {
    return ((c >= 76) && (c <= 90));
  }

  public static void main(String[] args) {
    System.out.println(scan(args[0]) ? "YES" : "NOPE" );
  }

}
