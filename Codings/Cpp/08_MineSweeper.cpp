std::vector<std::vector<int>> minesweeper(std::vector<std::vector<bool>> matrix) {
    vector < vector <int> > r (matrix.size(), vector <int> (matrix[0].size()));
    for(int i = 0; i < matrix.size(); i++) {
        for(int j = 0; j < matrix[0].size(); j++) {
            r[i][j] += i > 0 && j > 0 && matrix[i-1][j-1];
            r[i][j] += i > 0 && matrix[i-1][j];
            r[i][j] += i > 0 && j < matrix[0].size() - 1 && matrix[i-1][j+1];
            r[i][j] += j > 0 && matrix[i][j-1];
            r[i][j] += j < matrix[0].size() - 1 && matrix[i][j+1];
            r[i][j] += i < matrix.size() - 1 && j > 0 && matrix[i+1][j-1];
            r[i][j] += i < matrix.size() - 1 && matrix[i+1][j];
            r[i][j] += i < matrix.size() - 1 && j < matrix[0].size() - 1 && matrix[i+1][j+1];
        }
    }
    return r;
}
