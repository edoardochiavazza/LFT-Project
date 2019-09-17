/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package turnilab;

/**
 *
 * @author st171424
 */
public class TurniLab {
    public static void scan(String s) {
        int state = 0;
        int i = 0;

        while (state >= 0 && i < s.length()) {
            final char ch = s.charAt(i++);

            switch (state) {
            case 0:
                if (ch == '0' || ch == '2' || ch == '4' || ch == '6' || ch == '8')
                    state = 1;
                else if (ch % 2 != 1)
                    state = 2;
                else
                    state = -1;
                break;

            case 1:
                if (ch == '0' || ch == '2' || ch == '4' || ch == '6' || ch == '8')
                    state = 1;
                else if (ch == '1' || ch == '3' || ch == '5' || ch == '7' || ch == '9')
                    state = 2;
                else if (ch > 64 && ch < 76 || ch > 96 && ch < 108)
                    state = 4;
                else if (ch > 75 && ch < 91 || ch > 107 && ch < 123)
                    state = 5;
                else
                    state = -1;
                break;

            case 2:
                if (ch == '0' || ch == '2' || ch == '4' || ch == '6' || ch == '8')
                    state = 1;
                else if (ch == '1' || ch == '3' || ch == '5' || ch == '7' || ch == '9')
                    state = 2;
                else if (ch > 64 && ch < 76 || ch > 96 && ch < 108)
                    state = 6;
                else if (ch > 75 && ch < 91 || ch > 107 && ch < 123)
                    state = 7;
                else
                    state = -1;
                break;

            case 4:
                if (ch > 64 && ch < 76 || ch > 96 && ch < 108)
                    state = 4;
                else if (ch > 75 && ch < 91 || ch > 107 && ch < 123)
                    state = 5;
                else
                    state = -1;
                break;

            case 5:
                if (ch > 64 && ch < 76 || ch > 96 && ch < 108)
                    state = 4;
                else if (ch > 75 && ch < 91 || ch > 107 && ch < 123)
                    state = 5;
                else
                    state = -1;
                break;

            case 6:
                if (ch > 64 && ch < 76 || ch > 96 && ch < 108)
                    state = 6;
                else if (ch > 75 && ch < 91 || ch > 107 && ch < 123)
                    state = 7;
                else
                    state = -1;
                break;

            case 7:
                if (ch > 64 && ch < 76 || ch > 96 && ch < 108)
                    state = 6;
                else if (ch > 75 && ch < 91 || ch > 107 && ch < 123)
                    state = 7;
                else
                    state = -1;
                break;
            }
        }
        if (state == 4)
            System.out.println("Turno 2");
        else if (state == 5)
            System.out.println("Turno 4");
        else if (state == 6)
            System.out.println("Turno 1");
        else if (state == 7)
            System.out.println("Turno 3");
        else
            System.out.println("Errore");
    }

    public static void main(String[] args) {
        scan("7I");
    }
}
