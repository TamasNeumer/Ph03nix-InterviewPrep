#include <vector>

std::vector<std::vector<int>> boxBlur(std::vector<std::vector<int>> image) {
    std::vector<std::vector<int>> result;
    for(auto row = 1; row < image.size() -1; row++)
    {
        std::vector<int> newRow;
        for(auto column = 1; column < image[row].size()-1; column++)
        {
            int sum = image[row-1][column-1] + image[row-1][column] + image[row-1][column+1] +
                      image[row][column-1] + image[row][column] + image[row][column+1] +
                      image[row+1][column-1] + image[row+1][column] + image[row+1][column+1];
            int value = sum / 9;
            newRow.push_back(value);
        }
        result.push_back(newRow);
    }
    return result;
}
