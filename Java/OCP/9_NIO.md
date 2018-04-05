# NIO

#### Introducing NIO.2

- **Basics**
  - Non-blocking I/O, or NIO for short.
  - The basic idea is that you load the data from a  le channel into a temporary buffer that, unlike byte streams, can be read forward and backward without blocking on the underlying resource.
- **Introducing Path**
  - A Path object represents a hierarchical path on the storage system to a file or directory. In this manner, Path is a direct replacement for the legacy `java.io.File` class, and conceptually it contains many of the same properties.
  - Unlike the `File` class, the `Path` interface contains **support for symbolic links**. A symbolic link is a special file within an operating system that serves as a reference or pointer to another file or directory.
  - The `Path` is an **interface** because the actual implementations are different for each platform.
- **Creating Paths**
  - **Path** var = Path**s**.get(String) --> watch out for the right syntax!
  - Via the factory method
    - `Path path1 = Paths.get("pandas/cuddly.png");` -- relative path
    - `Path path2 = Paths.get("c:\\zooinfo\\November\\employees.txt");` -- full oath windows
    - `Path path3 = Paths.get("/home/zoodirector");` -- full path unix
      - (If a path starts with a forward slash or drive letter, then it's an absolute path, otherwise it is relative)
  - You can also let the JVM to use the computer specific separators.
    - `Path path1 = Paths.get("pandas","cuddly.png");`
    - `Path path2 = Paths.get("c:","zooinfo","November","employees.txt");`
    - `Path path3 = Paths.get("/","home","zoodirector");`
  - A *uniform resource identifier (URI)* is a string of characters that identify a resource.
    - It begins with a schema that indicates the resource type, followed by a path value. Examples of schema values include `file://`, `http://`, `https://`, and `ftp://`.
    - URIs **must** reference absolute paths at runtime
    - Note that the constructor new `URI(String)` does throw a checked `URISyntaxException`.
    - The `Path` interface also contains a reciprocal method `toUri()` for converting a `Path` instance back to a `URI` .
  - **Accessing the Underlying FileSystem Object**
    - The `FileSystem` class has a protected constructor, so we use the **plural** `FileSystems` factory class to obtain an instance of `FileSystem`
    - `Path path1 = FileSystems.getDefault().getPath("pandas/cuddly.png")`
    - `FileSystem fileSystem = FileSystems.getFileSystem( new URI("http://www.selikoff.net")); Path path = fileSystem.getPath("duck.txt");`
  - **Working with Legacy File Instances**
    - `File file = new File("pandas/cuddly.png");`
    - `Path path = Paths.get("cuddly.png");`

#### Interacting with Paths and Files

- **Providing Optional Arguments**
  - `NOFOLLOW_LINKS`
    - Usage: Test file existing, Read file data, Copy file, Move file
    - Description: If provided, symbolic links when encountered will not be traversed. Useful for performing operations on symbolic links themselves rather than their target.
  - `FOLLOW_LINKS`
    - Usage: Traverse a directory tree
    - Description: If provided, symbolic links when encountered will be traversed.
  - `COPY_ATTRIBUTES`
    - Usage: Copy file
    - Description: If provided, all metadata about a file will be copied with it.
  - `REPLACE_EXISTING`
    - Usage: Copy file, Move file
    - Description: If provided and the target file exists, it will be replaced; otherwise, if it is not provided, an exception will be thrown if the file already exists.
  - `ATOMIC_MOVE`
    - Usage: Move file
    - Description: The operation is performed in an atomic manner within the file system, ensuring that any process using the file sees only a complete record. Method using it may throw an exception if the feature is unsupported by the file system.
  - **Using Path Objects**
    - It is usual to do method chaining:
      - `Paths.get("/zoo/../home").getParent().normalize().toAbsolutePath();`
    - `toString()`
      - `String` representation of the entire path
    - `getNameCount()`, `getName(int)`
      - `getName(int)` method returns the component of the Path as a new Path object rather than a String.
      - `getNameCount()` returns the number of "parts" in the path.

        ```java
        Path path = Paths.get("/land/hippo/harry.happy");
        System.out.println("The Path Name is: "+path);
        for(int i=0; i<path.getNameCount(); i++) {
          System.out.println(" Element "+i+" is: "+path.getName(i));
        }

        // The Path Name is: /land/hippo/harry.happy
        // Element 0 is: land
        // Element 1 is: hippo
        // Element 2 is: harry.happy
        ```

      - Notice that the root element `/` is not included in the list of names. If the Path object represents the root element itself, then the number of names in the Path object returned by `getNameCount()` will be `0` Also note that it is zero indexed!.
    - `getFileName()`
      - Returns a `Path` instance representing the filename, which is the farthest element from the root. (**Path, not a String!**) 
        - `"/zoo/armadillo/shells.txt"` --> `shells.txt`
    - `getParent()`
      - Returns a `Path` instance representing the parent path or `null` if there is no such parent.
        - `"/zoo/armadillo/shells.txt"` --> `/zoo/armadillo`
        - `"armadillo/shells.txt` --> `armadillo`
    - `getRoot()`
      - Returns the root element for the `Path` object or `null` if the `Path` object is relative.
        - `"/zoo/armadillo/shells.txt"` --> `/`
        - `"armadillo/shells.txt` --> `null`
    - `isAbsolute()`
      - Returns `true` if the path the object references is absolute and `false` if the path the object references is relative
    - `toAbsolutePath()`
      - Converts a relative `Path` object to an absolute `Path` object by joining it to the current working directory.
    - `subpath(int, int)`
      - Returns a relative subpath of the Path object, referenced by an inclusive start index and an exclusive end index.
      - `Path path = Paths.get("/mammal/carnivore/raccoon.image");`
        - `System.out.println("Subpath from 0 to 3 is: "+path.subpath(0,3));` --> mammal/carnivore/raccoon.image
        - `System.out.println("Subpath from 1 to 3 is: "+path.subpath(1,3));` --> carnivore/raccoon.image
        - `System.out.println("Subpath from 1 to 2 is: "+path.subpath(1,2));` --> carnivore
        - `System.out.println("Subpath from 0 to 4 is: "+path.subpath(0,4));` // THROWS EXCEPTION AT RUNTIME
        - `System.out.println("Subpath from 1 to 1 is: "+path.subpath(1,1));` // THROWS EXCEPTION AT RUNTIME
          - Start and end index should be different + don't go out of bounds (array)
    - `.`
      - Reference to the current directory
    - `..`
      - Reference tot he parent of the current directory
    - `relativize(Path)`
      - Computes the relative path from one `Path` object to another.
      - If both path values are relative, then the relativize() method computes the paths as if they are in the same current working directory.
        - `System.out.println(path1.relativize(path2));` --> "../birds.txt"
      - If both files are absolute it computes the relative path from one to another.
        - `System.out.println(path3.relativize(path4));` --> relative path from path3 to path4
      - Requires that both paths be absolute or both relative, and it will throw an `IllegalArgumentException` if a relative path value is mixed with an absolute path value.
    - `resolve(Path)`
      - joining an existing path to the current path
      - Given two absolute paths such as path1. resolve(path2), then path1 would be ignored and a copy of path2 would be returned.
      - Like the `relativize()` method, the `resolve()` method does not clean up path symbols, such as the parent directory `..` symbol.
    - `normalize()`
      - Like `relativize()`, the `normalize()` method does not check that the file actually exists.

        ```java
        Path path3 = Paths.get("E:\\data");
        Path path4 = Paths.get("E:\\user\\home");
        Path relativePath = path3.relativize(path4);
        System.out.println(path3.resolve(relativePath)); // E:\data\..\user\home
        System.out.println(path3.resolve(relativePath).normalize()); // E:\user\home
        ```

    - `toRealPath(Path)`
      - Takes a `Path` object that may or may not point to an existing file within the file system, and it returns a reference to a real path within the file system.
      - Throws a checked `IOException` at runtime if the file cannot be located.
      - remember that `toAbsolutePath()` does not check the file, while this function does!
      - To gain access tot he current working directory: `System.out.println(Paths.get(".").toRealPath());`

  - **Interacting with Files**
    - `boolean Files.exists(Path)`
    - `boolean Files.isSameFile(Path,Path) throws IOException`
      - Checks the equality of the two path references.
    - `Files.createDirectory(Path)`, `Files.createDirectories(Path)`
      - Directories creates the target directory along with any nonexistent parent directories leading up to the target directory in the path.
      - The directory-creation methods can throw the checked `IOException`, such as when the directory cannot be created or already exists.
    - `Files.copy(Path,Path)`
      - Copy file or directory from one location to another.
      - `IOException`, such as when the  le or directory does not exist or cannot be read.
      - Directory copies are **shallow** rather than deep, meaning that  les and subdirectories within the directory are not copied.
    - `Files.copy(InputStream, Path)`
      - `Files.copy(is, Paths.get("c:\\mammals\\wolf.txt"));`
    - `Files.copy(Path, OutputStream)`
      - `Files.copy(Paths.get("c:\\fish\\clown.xsl"), out);`
    - `Files.move(Path,Path)`
      - `Files.move(Paths.get("c:\\zoo"), Paths.get("c:\\zoo-new"));` --> renaming, while keeping all of the original contents from the source directory
      - `Files.move(Paths.get("c:\\user\\addresses.txt"), Paths.get("c:\\zoo-new\\addresses.txt"));` --> move and rename file
    - `Files.delete()`, `boolean Files.deleteIfExists()`
      - If the path represents a non-empty directory, the operation will throw the runtime `DirectoryNotEmptyException`
      - For symbolic links only the link is deleted
      - The second version checks whether the directory exists and returns `false` if not.
    - `Files.newBufferedReader(Path,Charset)`
      - Reads the file specified at the Path location using a java.io.BufferedReader object. It also requires a Charset value to determine what character encoding to use to read the file.
    - `Files.newBufferedWriter(Path,Charset)`
      - Writes to a  le specified at the Path location using a BufferedWriter.

        ```java
        Path path = Paths.get("/animals/gopher.txt");
        try (BufferedReader reader = Files.newBufferedReader(path, Charset.forName("US-ASCII"))) {
        // Read from the stream
          String currentLine = null;
          while((currentLine = reader.readLine()) != null)
            System.out.println(currentLine); 
        } catch (IOException e) {
        // Handle file I/O exception...
        }

        Path path = Paths.get("/animals/gorilla.txt"); List<String> data = new ArrayList();
        try (BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF-16"))) {
          writer.write("Hello World");
        } catch (IOException e) {
            // Handle file I/O exception...
        }
        ```

    - `Files.readAllLines()`
      - Reads all of the lines of a text file and returns the results as an ordered `List` of `String` value.
      - `final List<String> lines = Files.readAllLines(path);`

#### Understanding File Attributes
