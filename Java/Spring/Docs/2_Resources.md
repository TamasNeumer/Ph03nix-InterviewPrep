## Resources

#### The resource interface
Some of the most important methods from the Resource interface are:
- `getInputStream()`: locates and opens the resource, returning an InputStream for reading from the resource. It is expected that each invocation returns a fresh InputStream. It is the responsibility of the caller to close the stream.
- `exists()`: returns a boolean indicating whether this resource actually exists in physical form.
- `isOpen()`: returns a boolean indicating whether this resource represents a handle with an open stream.
- `getDescription()`: returns a description for this resource, to be used for error output when working with the resource.

#### Resource implementations
**UrlResource**  
- wraps a java.net.URL
- used to access any object that is normally accessible via a URL, such as files, an HTTP target, an FTP target, etc
**ClassPathResource**
- This class represents a resource which should be obtained from the classpath.
**FileSystemResource**
- This is a Resource implementation for java.io.File handles. It obviously supports resolution as a File, and as a URL
**ServletContextResource**
- This is a Resource implementation for ServletContext resources, interpreting relative paths within the relevant web application’s root directory.
**InputStreamResource**
- A Resource implementation for a given InputStream. This should only be used if no specific Resource implementation is applicable.
**ByteArrayResource**
- This is a Resource implementation for a given byte array. It creates a ByteArrayInputStream for the given byte array. It’s useful for loading content from any given byte array, without having to resort to a single-use InputStreamResource.

#### The ResourceLoader
- The ResourceLoader interface is meant to be implemented by objects that can return (i.e. load) Resource instances.
- All application contexts implement the ResourceLoader interface, and therefore all application contexts may be used to obtain Resource instances.
- Usage: `Resource template = ctx.getResource("classpath:some/resource/path/myTemplate.txt");`

```java
public interface ResourceLoader {
  Resource getResource(String location);
}
```

Prefix  | Example  | Explanation
--|---|--
classpath:  | `classpath:com/myapp/config.xml`  |  	Loaded from the classpath.
file:   |  `file:///data/config.xml` |  Loaded as a URL, from the filesystem.
http:   |  `http://myserver/logo.png` |  	Loaded as a URL.
(none)  | `/data/config.xml`  |  Depends on the underlying ApplicationContext.

#### Resources as dependencies
- So if myBean has a template property of type Resource, it can be configured with a simple string for that resource, as follows:

```xml
<bean id="dataSource">
  <property name="url" value="${jdbc.url}" />
</bean>
```

```java
@Value( "${jdbc.url:aDefaultUrl}" )
private String jdbcUrl;
```
