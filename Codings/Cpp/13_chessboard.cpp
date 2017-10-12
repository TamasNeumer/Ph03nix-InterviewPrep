bool chessBoardCellColor(std::string cell1, std::string cell2) {
    return (isWhite(cell1) == isWhite(cell2));
}

bool isWhite(std::string cell){
    if((cell[0] + cell[1]) %2 != 0) return false;
    else return true;
}
