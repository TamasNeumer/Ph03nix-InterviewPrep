boolean chessBoardCellColor(String cell1, String cell2) {
    return ((cell1.charAt(0)+cell1.charAt(1))%2 == (cell2.charAt(0)+cell2.charAt(1))%2);
}
