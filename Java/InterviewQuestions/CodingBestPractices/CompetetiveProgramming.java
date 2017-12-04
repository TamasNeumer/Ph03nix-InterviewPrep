import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.InputMismatchException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Built using CHelper plug-in
 * Actual solution is at the top
 *
 * @author ankur
 */
public class Main {
    public static void main(String[] args) {
        InputStream inputStream = System.in;
        OutputStream outputStream = System.out;
        InputReader in = new InputReader(inputStream);
        PrintWriter out = new PrintWriter(outputStream);
        hackerearth solver = new hackerearth();
        solver.solve(1, in, out);
        out.close();
    }

    static class hackerearth {
        public void solve(int testNumber, InputReader in, PrintWriter out) {
            // out.print(1);
            int ar[] = new int[(int) 1e6 + 2];
            for (int i = 0; i < 1e6 + 2; i++) {
                ar[i] = sum(i);
            }
            long ans[] = new long[ar.length];
            for (int i = 1; i < 1e6 + 1; i++) {
                for (int j = i; j < 1e6 + 1; j += i) {
                    if (j % i == 0)
                        ans[j] += ar[i];
                }

            }
            int q = in.nextInt();
            while (q > 0) {
                int n = in.nextInt();
                out.println(ans[n]);


                q--;
            }
        }

        int sum(int x) {
            String str = x + "";
            int ans = 0;
            for (int i = 0; i < str.length(); i++) {
                if ((str.charAt(i) - '0') % 2 != 0) {
                    ans += (str.charAt(i) - '0');
                }
            }
            return ans;
        }

    }

    static class InputReader {
        private final InputStream stream;
        private final byte[] buf = new byte[8192];
        private int curChar;
        private int snumChars;

        public InputReader(InputStream st) {
            this.stream = st;
        }

        public int read() {
            if (snumChars == -1)
                throw new InputMismatchException();
            if (curChar >= snumChars) {
                curChar = 0;
                try {
                    snumChars = stream.read(buf);
                } catch (IOException e) {
                    throw new InputMismatchException();
                }
                if (snumChars <= 0)
                    return -1;
            }
            return buf[curChar++];
        }

        public int nextInt() {
            int c = read();
            while (isSpaceChar(c)) {
                c = read();
            }
            int sgn = 1;
            if (c == '-') {
                sgn = -1;
                c = read();
            }
            int res = 0;
            do {
                res *= 10;
                res += c - '0';
                c = read();
            } while (!isSpaceChar(c));
            return res * sgn;
        }

        public boolean isSpaceChar(int c) {
            return c == ' ' || c == '\n' || c == '\r' || c == '\t' || c == -1;
        }

    }
}

Language: Java 8
