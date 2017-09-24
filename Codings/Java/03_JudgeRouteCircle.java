class Solution {
     int L = 0, R = 0, U = 0, D = 0;

    public boolean judgeCircle(String moves) {
                for (char c : moves.toCharArray()) {
            switch (c) {
                case 'L':
                    L++;
                    break;
                case 'D':
                    D++;
                    break;
                case 'U':
                    U++;
                    break;
                case 'R':
                    R++;
                    break;
                default:
                    break;
            }
        }

        return ((L == R) && (D == U));
    }
}
