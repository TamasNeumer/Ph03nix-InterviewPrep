# Regex

Regex is used to match patterns in a string.

## Regex tokens
Regex  |  Meaning
--|--
`[abc]`  | Single character of `a` or `b` or `c`
`[^abc]`  | Any character **except** `a, b, c`
`[a-z]`  | Any character in range of `a-z`
`[^a-z]`  | Any character **not** in range of `a-z`
`[a-zA-Z]`  | Any character in range of `a-z` or `A-Z`
`.`  |  Single character
`\s`  | Any whitespace character (space, tab, newline)
`\d`  | Any digit
`\D`  | Any **non** digit
`\w`  |  Any word character
`\W`  |  Any **non** word character
`(...)`  |   Capture everything enclosed
<code>(a&#124;b)</code>  |  Match either `a` or `b`
a?  | Zero or one of `a`  
a*  | Zero or more of `a`  
a+  | One or more of `a`
a{3}  |  Exactly 3 of `a`
a{3,6}  | Between 3 and 6 of `a`
^  |  Start of string
$  |  End of string
\b  |  Assert position at a word boundary
\n| New line
\r | Carriage return
\t | Tab
\0 | Null character

## Regex in C++
- Note that in C++ you have to escape the `/` characters or use `R` before the regex string:
  - `std::regex r(R"(Speed:\t\d*)");`
  - `std::regex r(\\t)`

#### Main classes
- **basic_regex** regular expression object
- **sub_match** identifies the sequence of characters matched by a sub-expression
- **match_results** identifies one regular expression match, including all sub-expression matches

#### Algorithms
- **regex_match** attempts to match a regular expression to an **entire** character sequence
    - `std::regex_match(fileName, text_regex)` --> Returns true/false
    ```cpp
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
    }
    ```
- **regex_search** attempts to match a regular expression to **any part** of a character sequence
  ```cpp
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
  ```
- **regex_replace** replaces occurrences of a regular expression with formatted replacement text
  ```cpp
  std::string text = "Quick brown fox";
  std::regex vowel_re("a|e|i|o|u");

  // write the results to an output iterator
  std::regex_replace(std::ostreambuf_iterator<char>(std::cout),
                     text.begin(), text.end(), vowel_re, "*");

  // construct a string holding the results
  std::cout << '\n' << std::regex_replace(text, vowel_re, "[$&]") << '\n';
  ```
