std::string alphabeticShift(std::string inputString) {
    for(auto& currentChar : inputString){
        if(currentChar == 'z') currentChar = 'a';
        else currentChar += 1;
    }
    return inputString;
}
