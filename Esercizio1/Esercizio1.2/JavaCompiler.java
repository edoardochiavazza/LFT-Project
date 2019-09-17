
public class JavaCompiler {
    public static boolean scan(String s) {
        int state = 0;
        int i = 0;

        while (state >= 0 && i < s.length()) {
            final char ch = s.charAt(i++);

            switch (state) {
            case 0:
                if (ch > 64 && ch < 91 || ch > 96 && ch < 123)
                    state = 2;
                else if (ch == 95)
                    state = 1;
                else
                    state = -1;
                break;

            case 1:
                if (ch > 64 && ch < 91 || ch > 96 && ch < 123 || ch > 47 && ch < 58)
                    state = 2;
                else if (ch == 95)
                    state = 1;
                else
                    state = -1;
                break;

            case 2:
                if (ch > 64 && ch < 91 || ch > 96 && ch < 123 || ch > 47 && ch < 58 || ch == 95)
                    state = 2;
                else
                    state = -1;
                break;
            }

        }
        return state == 2;
    }

    public static void main(String[] args) {
        System.out.println(scan(args[0]) ? "OK" : "NOPE");
    }
}
