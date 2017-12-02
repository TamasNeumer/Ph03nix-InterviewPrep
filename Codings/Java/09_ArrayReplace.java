int[] arrayReplace(int[] inputArray, int elemToReplace, int substitutionElem) {
    return Arrays.stream(inputArray)
    .map(o -> o == elemToReplace ? substitutionElem : o)
       .toArray();
}
