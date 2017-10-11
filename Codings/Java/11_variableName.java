boolean variableName(String name) {
    //return Pattern.compile("^[a-zA-z_][\\w\\d]*$").matcher(name).matches();
    return name.matches("[a-zA-Z_][a-zA-Z0-9_]*");
}
