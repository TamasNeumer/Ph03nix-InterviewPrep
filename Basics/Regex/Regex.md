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
\b  |  Word boundary
\n| New line
\r | Carriage return
\t | Tab
\0 | Null character

## Regex in C++

#### Main classes
- **basic_regex** regular expression object
- **sub_match** identifies the sequence of characters matched by a sub-expression
- **match_results** identifies one regular expression match, including all sub-expression matches

#### Algorithms
- **regex_match** attempts to match a regular expression to an **entire** character sequence  
- **regex_search** attempts to match a regular expression to **any part** of a character sequence
- **regex_replace** replaces occurrences of a regular expression with formatted replacement text

```cpp
std::string fileNames[] = {"foo.txt", "bar.txt", "baz.dat", "zoidberg"};
std::regex txt_regex("[a-z]+\\.txt");
```
