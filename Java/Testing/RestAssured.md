# RestAssured

## Verifying response body

### Verifying with GPath

- Use [OnlineJsonViewer](http://jsonviewer.stack.hu/) to make your JSON a one liner. (Remove white spaces)
- Open the [Groovy Playground](https://groovy-playground.appspot.com/) and paste the following code. Paste your JSON also.
- Write your GPath expression and test. See the official [doku](http://docs.groovy-lang.org/latest/html/documentation/#_gpath).
- The [Groovy string functions](https://blogs.oracle.com/fadevrel/useful-groovy-string-functions) might also prove to be useful.
    ```groovy
    import groovy.json.JsonSlurper 
          
    def jsonSlurper = new JsonSlurper()
    def object = jsonSlurper.parseText(
              '{JSON GOES HERE}'
    )

    def result = object.promotions.findAll { it.setId.startsWith('63_') }
    println(result)
    ```
- Once working you copy the GPath over to Java and insert it as `.body("promotions.findAll { it.setId.startsWith('63_') }", hasSize(1))`

### Validating fields and extracting response at the same time

  ```java
      String response =
          given().log().ifValidationFails()
              .header("Authorization", "Bearer " + MPCARD_API_TOKEN)
              .header("x-api-key", System.getProperty("API_KEY"))
              .header("iss", System.getProperty("ISS"))
              .header("dataBar", dataBar)
              .accept(ContentType.JSON)
              .get(requestUrl)
              .then().log().ifValidationFails()
              .statusCode(200)
              .extract().response().asString();
  ```