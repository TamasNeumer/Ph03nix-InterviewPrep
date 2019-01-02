# Input Validation

- Edge cases to consider
  - No request body at all, when required
  -

## The endpoint

- `@RequestHeader` maps the variable from the header
- `@PathVariable` maps the variable from the path
- `@RequestBody` maps the request body to the specified java POJO
- `@Validated` triggers the validation on the `User` class

```java
@RestController
@RequestMapping("/userApi")
public class UserResource {
  @PostMapping(value = "/registerUser", produces = "application/json")
  ResponseEntity registerUser(@Validated @RequestBody RegisterUserDTO registerUserDto) {
    /* Logic */
    Long response = 200L;
    return ResponseEntity.ok(response);
  }
}
```

```java
@Builder
@Getter
@EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
public class RegisterUserDTO {

  @Size(max = 20, message = "username: cannot contain more than 20 characters")
  @NotNull(message = "username: cannot be null")
  String username;

  @NotNull(message = "username: cannot be null")
  String email;

  @NotNull(message = "username: cannot be null")
  String password;
}
```

- At this point if we send a post request to `http://localhost:8080/userApi/registerUser` the basic `@Size, '@NotNull'` validation is applied.
- Let's go further and do some custom validation! Add the following line to the `RegistreUserDTO` and create the following classes and annotations:
  - `@UserRegisterRequestConstraint(message = "Transaction Request Failed because of invalid input")`

```java
@Documented
@Constraint(validatedBy = UserRegisterRequestValidator.class)
@Target({ ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserRegisterRequestConstraint {
    String message() default "Invalid Transaction Request";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

```java
@Slf4j
public class UserRegisterRequestValidator implements
    ConstraintValidator<UserRegisterRequestConstraint, RegisterUserDTO> {

  private String defaultMsg = "Validation of User registration request failed!";

  @Override
  public void initialize(UserRegisterRequestConstraint constraintAnnotation) {
    this.defaultMsg = constraintAnnotation.message();
  }

  @Override
  public boolean isValid(RegisterUserDTO request, ConstraintValidatorContext context) {
      return request.getPassword().equals(request.getPasswordConfirm());
  }
}
```

- If you send a request with non-matching passwords we get an error.
- Imagine that we have multiple API consumers (ConsumerA and ConsumerB) and these need a slightly different validation. For example ConsumerB decides to drop the constraint on the matching password fields, however ConsumerA being keen on quality still wants to validate it.

* In the following we define the validation configuration. Our `application.yml` contains the following:

```java
@Slf4j
@Data
public abstract class InputConfiguration<T> {

  private String name = "Default Name";

  private List<KeyMatcherPair<?>> matches = Collections.emptyList();

  private List<ValidationNode> validations = Collections.emptyList();

  private List<String>  converters = Collections.emptyList();

  public boolean matches(T other) {

    if (CollectionUtils.isEmpty(matches)) {
      return true;
    }

    for (KeyMatcherPair<?> matchDefinition : matches) {

      try {

        final Object fieldValue = getTypeMapper().getForType(matchDefinition.getProperty(), other);
        final Matcher fieldMatcher = matchDefinition.getPropertyMatcher().getMatcherForKey();

        if (!fieldMatcher.test(fieldValue)) {
          return false;
        }

      } catch (JXPathNotFoundException jxPathNotFoundException) {
        logger.info("Object {} does not have property {}", other, matchDefinition.getProperty());
        return false;
      }

    }
    return true;
  }

  public TypeMapper<T> getTypeMapper() {
    // use standard behaviour via xpath notation
    return new TypeMapper<T>() {};
  }

  public abstract Rule<T, Result<Boolean, ValidationError>> getValidationRules();

  public abstract Function<T, T> getConverterDefinition();
}
```

```yml
post-transaction:
  configurations:
    # transaction request configurations
    transactions:
      - name: "Transaction Config Default"
        validations:
          - name: TRANSACTION_TYPE_MUST_MATCH_KNOWN_PATTERNS
          - name: BALANCE_TYPE_MUST_MATCH_KNOWN_PATTERNS
          - name: COIN_VALUES_MUST_BE_NUMERIC
          - name: BALANCE_TYPE_MUST_BE_UNIQUE
          - name: TRANSACTION_TYPE_MUST_BE_UNIQUE
          - name: FUTURE_TRANSACTION_ONLY_ALLOWED_FOR_SPECIFIC_TYPES
          - name: MERCHANT_DOES_NOT_MATCH_CLUSTER_CONFIG
          - name: LOYALTY_TRANSACTION_RULE
        converters:
          - FUTURE_CONVERTER

      - name: "Schema validation of VAT fields in transactionDetail of coin balances"
        validations:
          - name: SCHEMA_VALIDATION_RULE
            metaData:
              fieldNameToValidate: balances[type='coins']/transactions/transactionDetail
              jsonScheme: schemes/default/transactionDetail.json
      - name: "Schema validation of value fields of transactions in promo balances"
        validations:
          - name: SCHEMA_VALIDATION_RULE
            metaData:
              fieldNameToValidate: balances[type='promotions']/transactions/value
              jsonScheme: schemes/default/promotion-items.json
```

```java
@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "post-transaction.configurations")
public class TransactionRequestConfiguration {

  private List<PostTransactionConfig> transactions;

  public static final String FIELD_NAME_TO_VALIDATE = "fieldNameToValidate";
  public static final String JSON_SCHEME = "jsonScheme";

  @PostConstruct
  private void validateConfiguration() {
    List<String> xpathsFromConfig = getXPathsFromConfiguration();
    TransactionDTO transactionDTO = readTransactionDTOFromFile("/config/transactionDTO.json");
    validateTransactionConfiguration(transactionDTO, xpathsFromConfig);
  }

  private void validateTransactionConfiguration(TransactionDTO transactionDTO,
      List<String> xPaths) {
    JXPathContext context = JXPathContext.newContext(transactionDTO);
    for (String xPath : xPaths) {
      try {

        context.getValue(xPath);

      } catch (JXPathNotFoundException jpnfe) {
        logger.error("XPath {} not found in object: {}", xPath, transactionDTO);
        throw new MPCardException(ERRONEOUS_TRANSACTION_VALIDATION_CONFIGURATION);
      }
    }
  }

  private List<String> getXPathsFromConfiguration() {
    List<String> xPathsFromMatchesProperties =
        transactions.stream()
            .filter(config -> (config.getMatches() != null) && !(config.getMatches().isEmpty()))
            .map(InputConfiguration::getMatches)
            .flatMap(List::stream)
            .map(KeyMatcherPair::getProperty)
            .distinct()
            .collect(Collectors.toList());

    List<String> xPathsFromMetaDataFieldToValidate =
        transactions.stream()
            .filter(
                config -> (config.getValidations() != null) && !(config.getValidations().isEmpty()))
            .map(InputConfiguration::getValidations)
            .flatMap(List::stream)
            .filter(vNode -> (vNode.getName().equals("SCHEMA_VALIDATION_RULE")))
            .filter(vNode -> (vNode.getMetaData() != null) && (vNode.getMetaData()
                .containsKey(FIELD_NAME_TO_VALIDATE)))
            .map(vNode -> vNode.getMetaData().get(FIELD_NAME_TO_VALIDATE))
            .collect(Collectors.toList());

    return ListUtils.union(xPathsFromMatchesProperties, xPathsFromMetaDataFieldToValidate);
  }

  private TransactionDTO readTransactionDTOFromFile(String path) {
    String fileContent = getJsonAsString(path);
    TransactionDTO transactionDataDTO;
    try {
      transactionDataDTO = new ObjectMapper().readValue(fileContent, TransactionDTO.class);
    } catch (IOException ioe) {
      logger.error("File {} could not be read while validating PostTransactionConfig", path);
      throw new MPCardException(ERROR_READING_TRANSACTION_DATA_DTO);
    }
    return transactionDataDTO;
  }

  private String getJsonAsString(String path) {

    StringWriter writerJson = new StringWriter();
    try {
      IOUtils.copy(new InputStreamReader(getClass().getResourceAsStream(path)), writerJson);
    } catch (IOException e) {
      throw new MPCardException("File " + path + " could not be read", e, MPCardErrorStatusCodes.FILE_PATH_NOT_FOUND);
    }
    return writerJson.toString();
  }
}
```

```java
@Slf4j
public class UserRequestValidator implements ConstraintValidator<UserRequestConstraint, UserDTO> {

  private String defaultMsg = "Validation of User Request failed!";

  private UserRequestConfiguration userRequestConfiguration;

  @Autowired
  public UserRequestValidator(
      UserRequestConfiguration userRequestConfiguration) {
    this.userRequestConfiguration = userRequestConfiguration;
  }

  @Override
  public void initialize(UserRequestConfiguration constraintAnnotation) {
    this.defaultMsg = constraintAnnotation.message();
  }

  @Override
  public boolean isValid(UserDTO request, ConstraintValidatorContext context) {

    final ValidationRule rule = readValidationRuleFromEnvironment(request);
    final Result<Boolean, ValidationError> validationResult = rule.process(request);
    logger.info("UserRequestValidation result: {}", validationResult);
    if (!validationResult.isSuccess()) {
      return handleValidationErrors(context, validationResult);
    }
    return true;
  }

  private ValidationRule readValidationRuleFromEnvironment(UserDTO request) {
    if (userRequestConfiguration != null) {
      return userRequestConfiguration
          .getTransactions().stream()
          .filter(a -> a.matches(request))
          .map(PostTransactionConfig::getValidationRules)
          .reduce(IDENTITY_RULE.newRule(new HashMap<>()), Rule::addSuccessor);
    } else {
      logger.warn("No Validations Configurations found for request, skip validations");
      return IDENTITY_RULE.newRule(new HashMap<>());
    }
  }

  private boolean handleValidationErrors(ConstraintValidatorContext context,
      Result<Boolean, ValidationError> validationResult) {
    if (validationResult.getError().getError().isEmpty()) {
      context.buildConstraintViolationWithTemplate(defaultMsg).addConstraintViolation();
    }
    context.disableDefaultConstraintViolation();
    validationResult.getError().getError().stream().forEach(
        cause -> {
          final ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder =
              context.buildConstraintViolationWithTemplate(cause.toString());
          tryAddConstraintNode(constraintViolationBuilder, cause);
          constraintViolationBuilder.addConstraintViolation();
        }
    );
    return false;
  }

  private void tryAddConstraintNode(ConstraintValidatorContext.ConstraintViolationBuilder builder,
      ValidationError.Cause cause) {
    cause.getNode()
        .ifPresent(node -> {
          try {
            builder.addPropertyNode(node);
          } catch (NullPointerException ex) {
            logger.warn("invalid validation node property provided: \'{}\'", node);
          }
        });
  }
}
```

```java
@UserRequestConstraint(message = "User request failed because of invalid input")
public class UserDTO implements Serializable {

  @NotNull(message = "name: cannot be null")
  private String name;

  @Size(max = 50, message = "email: cannot contain more than 50 characters")
  private String email;

  @Valid
  @NotNull(message = "phoneInfo: cannot be null")
  private PhoneInfo phoneInfo;
}
```
