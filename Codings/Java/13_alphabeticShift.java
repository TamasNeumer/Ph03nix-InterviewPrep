String alphabeticShift(String inputString) {
    char[] ss = inputString.toCharArray();
    for (int i = 0; i < ss.length; ++i) {
        ss[i]++;
        if (ss[i] > 'z')
            ss[i] = 'a';
    }
    return new String(ss);
}
