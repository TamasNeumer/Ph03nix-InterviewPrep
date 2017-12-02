// Best solution
int avoidObstaclesBest(std::vector<int> input) {
    for(int numberToTest = 1 ; ; ++numberToTest)
    {
        int flag = 1;
        for(int elem : input)
            flag = flag && (elem % numberToTest);
        if(flag)return numberToTest;
    }
}

// My Solution
int avoidObstacles(std::vector<int> inputArray) {
    auto i = 2;
  auto maxElement = std::max_element(inputArray.begin(), inputArray.end());
  std::cout << *maxElement << std::endl;
  while(i <= *maxElement){
      if(std::all_of(inputArray.begin(), inputArray.end(), [&](auto val){return val % i != 0;}))
          return i;
  }
  return i+1;
}
