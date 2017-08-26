/* Copyright (C) 2017 Tamas Neumer - All Rights Reserved*/

#include <iostream>
#include <regex>
#include <string>

int main() {
	std::string fileNames[] = {"foo.txt", "bar.txt", "baz.dat", "zoidberg"};
	std::regex text_regex("[a-z]+\\.txt"); // One-Unlimited [a-z] char and ".txt"

	// Returns true, where the fileName matches the regex in its FULL LENGTH
	for (const auto &fileName : fileNames) {
		std::cout << fileName << ":" << std::regex_match(fileName, text_regex)
		          << std::endl;
	}

	std::regex pieces_regex("([a-z]+)\\.([a-z]+)");
	std::smatch pieces_match;
	// std::smatch = std::match_results<std::string::const_iterator>

	// The first sub_match is the whole string; the i-th
	// sub_match is the i-th parenthesized expression.
	for (const auto &fname : fileNames) {
		if (std::regex_match(fname, pieces_match, pieces_regex)) {
			std::cout << fname << '\n';
			for (size_t i = 0; i < pieces_match.size(); ++i) {
				std::ssub_match sub_match = pieces_match[i];
				std::string piece = sub_match.str();
				std::cout << "  submatch " << i << ": " << piece << '\n';
			}
		}
	}
	// Prints: bar.txt (pieces_match[0]), bar (pieces_match[1]), txt (pieces_match[2])


	std::string lines[] = {"Roses are #ff0000",
	                       "violets are #0000ff",
	                       "all of my base are belong to you"
	                      };

	std::regex color_regex("#([a-f0-9]{2})"
	                       "([a-f0-9]{2})"
	                       "([a-f0-9]{2})");

	// simple match (same as before) --> Prints "Roses are #ff0000: 1"
	for (const auto &line : lines) {
		std::cout << line << ": " << std::regex_search(line, color_regex) << '\n';
	}

	// Prefix and Suffix. Prints: "Prefix: 'Roses are '" and "Suffix: ''"
	std::smatch color_match;
	for (const auto& line : lines) {
		if (std::regex_search(line, color_match, color_regex)) {
			std::cout << "matches for '" << line << "'\n";
			std::cout << "Prefix: '" << color_match.prefix() << "'\n";
			for (size_t i = 0; i < color_match.size(); ++i)
				std::cout << i << ": " << color_match[i] << '\n';
			std::cout << "Suffix: '" << color_match.suffix() << "\'\n\n";
		}
	}

	// Regex serach
	std::string s ("this subject has a submarine as a subsequence");
	std::smatch m;
	std::regex e ("\\b(sub)([^ ]*)");   // matches words beginning by "sub"
	// at the boundary find (sub), any char that is not space (" "), zero or more times

	// Find first match, cout, and then look for matches in the REMAINING words.
	while (std::regex_search (s, m, e)) {
		std::cout << "Captured word: " << m[0] << std::endl;
		s = m.suffix().str();
	}


	std::string text = "Quick brown fox";
	std::regex vowel_re("a|e|i|o|u");

	// write the results to an output iterator
	std::regex_replace(std::ostreambuf_iterator<char>(std::cout),
	                   text.begin(), text.end(), vowel_re, "*");
	std::cout << std::endl;
}
