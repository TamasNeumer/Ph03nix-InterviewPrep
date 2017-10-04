public static int avoidObstaclesBest(int[] input) {
    for(int numberToTest = 1 ; ; ++numberToTest)
    {
        boolean flag = true;
        for(int elem : input) {
            int mod = elem % numberToTest;
            flag = flag && (mod != 0);
        }
        if(flag)return numberToTest;
    }
}
