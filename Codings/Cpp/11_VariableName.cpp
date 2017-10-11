bool variableName(std::string name) {
   std::regex var_regex("^[a-zA-z_][\\w\\d]*$");
   return std::regex_match(name, var_regex);
}
