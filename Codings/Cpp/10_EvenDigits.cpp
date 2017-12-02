bool evenDigitsOnly(int n) {
    while(n)
    {
        if(n % 2 != 0) return false;
        n = n / 10;
    }
    return true;
}
