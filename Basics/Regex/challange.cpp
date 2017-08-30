#include <regex>
#include <string>
#include <iostream>

bool isIPv4Address(std::string inputString) {
	std::regex ipReggie(R"([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5]).([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5]).([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5]).([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])");
	if (std::regex_match(inputString, ipReggie)) return true;
	else return false;
}

int main() {
	std::string ip = "192.55.15.5";
	if (isIPv4Address(ip))
		std::cout << "It was" << std::endl;
	else
		std::cout << "It wasn't" << std::endl;
	return 0;
}