# XML

## XPath
#### Basics
- XPath can be used to navigate through elements and attributes in an XML document.
- In XPath, there are seven kinds of nodes: element, attribute, text, namespace, processing-instruction, comment, and document nodes.
  - Root element node: bookstore
  - Element node: author
  - Attribute node: lang="en"
```XML
<bookstore>
  <book>
    <title lang="en">Harry Potter</title>
    <author>J K. Rowling</author>
    <year>2005</year>
    <price>29.99</price>
  </book>
</bookstore>
```
- Relationships: parent, children, sibling, ancestor, descendant

#### Syntax
**Selecting Nodes**

**Expression**|**Description**  
--|--
nodename  |  Selects all nodes with the name "nodename"
/	  |  Selects from the root node
//	  |  Selects nodes in the document from the current node that match the selection no matter where they are
.  |  Selects the current node
..  |  Selects the parent of the current node
@  |  Selects attributes
	
**Predicates**

**Path Expression**  |  **Result**
--|--
/bookstore/book[1]  |  Selects the first book element that is the child of the bookstore element.
/bookstore/book[last()]  |  Selects the last book element that is the child of the bookstore element
/bookstore/book[last()-1]  |  Selects the last but one book element that is the child of the bookstore element
/bookstore/book[position()<3]  |  Selects the first two book elements that are children of the bookstore element
//title[@lang]  |  Selects all the title elements that have an attribute named lang
//title[@lang='en']  |  Selects all the title elements that have a "lang" attribute with a value of "en"
/bookstore/book[price>35.00]  |  	Selects all the book elements of the bookstore element that have a price element with a value greater than 35.00
/bookstore/book[price>35.00]/title  |  	Selects all the title elements of the book elements of the bookstore element that have a price element with a value greater than 35.00

**Selecting Unknown Nodes**

**Path Expression** | **Result**
--|--
* |  Matches any element node
@* | 	Matches any attribute node
node() |  	Matches any node of any kind
