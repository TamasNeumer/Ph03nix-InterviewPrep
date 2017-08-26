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

## Greediness and laziness
Most people new to regular expressions will attempt to use <.+>. They will be surprised when they test it on a string like This is a <EM>first</EM> test. You might expect the regex to match <EM> and when continuing after that match, </EM>.

But it does not. The regex will match <EM>first</EM>.**The reason is that the `+, *, ?` are greedy**.

#### Inside the regex engine: greediness
The first token in the regex is `<`. This is a literal. As we already know, the first place where it will match is the first `<` in the string. The next token is the dot, which matches any character except newlines. The dot is repeated by the plus. The plus is greedy. Therefore, the engine will repeat the dot as many times as it can. The dot matches `E`, so the regex continues to try to match the dot with the next character. `M` is matched, and the dot is repeated once more. The next character is the `>`. You should see the problem by now. **The dot matches the `>`**, and the engine continues repeating the dot. The dot will match all remaining characters in the string. The dot fails when the engine has reached the void after the end of the string. Only at this point does the regex engine continue with the next token: `>`.

So far, `<.+` has matched `<EM>first</EM> test` and the engine has arrived at the end of the string. `>` cannot match here. The engine remembers that the plus has repeated the dot more often than is required. (Remember that the plus requires the dot to match only once.) Rather than admitting failure, the engine will backtrack. It will reduce the repetition of the plus by one, and then continue trying the remainder of the regex.

So the match of `.+` is reduced to `<EM>first</EM> tes`. The next token in the regex is still `>`. But now the next character in the string is the last `t`. Again, these cannot match, causing the engine to backtrack further. The total match so far is reduced to <`EM>first</EM> te`. But `>` still cannot match. So the engine continues backtracking until the match of `.+` is reduced to `<EM>first</EM`. Now, `>` can match the next character in the string. The last token in the regex has been matched. The engine reports that `<EM>first</EM>` has been successfully matched.

Remember that the regex engine is eager to return a match. It will not continue backtracking further to see if there is another possible match. It will report the first valid match it finds. Because of greediness, this is the leftmost longest match.

The quick fix to this problem is to **make the plus lazy** instead of greedy. Lazy quantifiers are sometimes also called "ungreedy" or "reluctant". You can do that by **putting a question mark after the plus in the regex**. You can do the same with the star, the curly braces and the question mark itself. So our example becomes `<.+?>``. Let's have another look inside the regex engine.

Again, `<` matches the first `<` in the string. The next token is the dot, this time repeated by a lazy plus. This tells the regex engine to repeat the dot as few times as possible. The minimum is one. So the engine matches the dot with `E`. The requirement has been met, and the engine continues with `>` and `M`. This fails. Again, the engine will backtrack. But this time, the backtracking will force the lazy plus to expand rather than reduce its reach. So the match of `.+` is expanded to `EM`, and the engine tries again to continue with `>`. Now, `>` is matched successfully. The last token in the regex has been matched. The engine reports that `<EM>` has been successfully matched. That's more like it.

[Source](http://www.regular-expressions.info/repeat.html)
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

## Regex in Python
- Escaping `/`
  - Backslash has to be escaped --> `//` and if you want to match the `/` then you need to esacpe it twice. --> `///`
  - Or use the raw string notation. `r"\n"`
- Regular expressions are compiled into **pattern objects**, which have methods for various operations such as searching for pattern matches or performing string substitutions.
  ```python
  import re

  p = re.compile('[a-z]+')
  ```
#### Methods of pattern object
- **match()**	Determine if the RE matches at the beginning of the string.
- **search()**	Scan through a string, looking for any location where this RE matches.
- **findall()**	Find all substrings where the RE matches, and returns them as a list.
- **finditer()**	Find all substrings where the RE matches, and returns them as an iterator.

- Then use the regex object and create a **match object**
  ```python
  import re

  p = re.compile('[a-z]+')
  m = p.match('ha ha')
  ```
#### Methods of a match object
- **group()**	Return the string matched by the RE
- **start()**	Return the starting position of the match
- **end()**	Return the ending position of the match
- **span()**	Return a tuple containing the (start, end) positions of the match
  ```python
  import re

  p = re.compile('[a-z]+')
  m = p.match('ha ha')

  print(m.group()) #ha
  print(m.start()) #0
  print(m.end())  #2

  ```
