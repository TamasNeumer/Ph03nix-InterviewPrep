# Bootstrap

## Bootstrap Grid System

### Mobile first strategy

- Determine what is important and design to smaller widths first.
- Base CSS address mobile device first;media queries address for tablet,desktops.
- "Progressive Enhancement" -> Add elements as screen size increases.

### The Grid

- **Grid system**
- Rows must be placed within a `.container` class for proper alignment and padding.
- Use rows to create horizontal groups of columns. Content should be placed within columns, and only columns may be immediate children of rows.
- Note that you can use `.container-fluid` - in this case the row will stretch 100% of your browser.

- **Media Queries**
  - = "conditional CSS rule". -> It simply applies some CSS based on certain conditions set forth.

    ```css
    /* Extra small devices (phones, less than 768px) */
    /* No media query since this is the default in Bootstrap */
    /* Small devices (tablets, 768px and up) */
    @media (min-width: @screen-sm-min) { ... }
    /* Medium devices (desktops, 992px and up) */
    @media (min-width: @screen-md-min) { ... }
    /* Large devices (large desktops, 1200px and up) */
    @media (min-width: @screen-lg-min) { ... }
    ```

- Basically **below** the given widths the columns will be reorganized to multiple rows. 1x2 -> 2x1. (`<div class="col-md-4" ...`). Note that in this case I had 3 `div`s of `col-md-4"`. With a 3-9 column size one column will be 3 times greater than the other.

    ```css
    <div class="col-sm-3 col-md-6 col-lg-4">....</div>
    <div class="col-sm-9 col-md-6 col-lg-8">....</div>
    ```

- **Column offset**
  - The `.col-xs=*` classes don’t support offsets, but they are easily replicated by using an empty cell.
  - To use offsets on large displays, use the `.col-md-offset-xyz` classes. These classes increase the left margin of a column by `xyz` columns where `xyz` range from 1 to 11.

- **Column ordering**
  - You can easily change the order of built-in grid columns with `.col-md-push-*` and `.col-md-pull-*` modifier classes where `*` range from 1 to 11.

## Bootstrap CSS Overview

- You need to add the viewport meta tag to the `<head>` element, to ensure proper rendering and touch zooming on mobile devices.
  - `<meta name="viewport" content="width=device-width, initial-scale=1.0">`
- You might also add `user-scalable=no` to disable zooming on mobile devices.
- To use responsive images simply add the `class="img-responsive"` class. --> (`max-width: 100%; height: auto`)

## Bootstrap Typography

### Text

- Bootstrap's global default font-size is 14px, with a line-height of 1.428.
- Inline Subheadings
  - `<h1>I'm Heading1 h1. <small>I'm secondary Heading1 h1</small></h1>`
- Bold
  - `<strong>This content is within <strong> tag</strong>`
- Italics
  - `<em>This content is within <em> tag and is rendered as italics</em>`
- Alignment
  - `<p class="text-left">`, center, right, info, warning danger (last 3 adds color)
- Abbreviation
  - `<abbr title="World Wide Web">WWW</abbr>` - you see the stuff on hover
- Block quote
  - `<blockquote>` + `<footer>` --> create stylish meaningless quotes with these!
- Highlight
  - `<mark>highlight</mark>`

### Code in text

- Inline code
  - `<code>`
- Block code
  - `<pre>`
- Keyboard "code style"
  - `<kbd>`

### Lists

- Ordered lists
  - `<ol>`
- Unordered lists
  - `<ul>`
- Definition lists
  - `<dl>`
- Inside these put lists elements with `<li>` tags.

## Bootstrap Tables

- Table tags:
  - `<table>` Wrapping element for displaying data in a tabular format
  - `<thead>` Container element for table header rows (`<tr>`) to label table columns
  - `<tbody>` Container element for table rows (`<tr>`) in the body of the table
  - `<tr>` Container element for a set of table cells (`<td>` or `<th>`) that appears on a single row
  - `<td>` Default table cell
  - `<th>` Special table cell for column (or row, depending on scope and placement) labels. Must be used within a `<thead>`
  - `<caption>` Description or summary of what the table holds.
- Classes to decorate table:
  - `.table-striped`, `.table-bordered`, `.table-hover`, `.table-condensed`, etc.
- You can add different colors to different rows by adding the following classes to the rows:
  - `active, success, danger, info, warning`
- Responsive tables
  - `.table-responsive` --> The table will then scroll horizontally on small devices (under 768px). When viewing on anything larger than 768px wide, there is no difference. **Note that the `<div>` has to have this class, in which the `<table class="table">` sits!**

## Bootstrap Forms

### Forms

- Add a role form to the parent `<form>` element.
- Wrap labels and controls in a `<div>` with class `.form-group`. This is needed for optimum spacing.
- Add a class of `.form-control` to all textual `<input>`, `<textarea>`, and `<select>` elements.

    ```html
        <form role="form">
            <div class="form-group">
                <label for="name">Name</label>
                <input type="text" class="form-control" id="name"
                      placeholder="Enter Name">
            </div>
            <div class="form-group">
                <label for="inputfile">File input</label>
                <input type="file" id="inputfile">
                <p class="help-block">Example block-level help text here.</p>
            </div>
        </form>
    ```

- Forms are **vertical** by default. You can align them **horizontally** by applying `<form class="form-inline" role="form">`
- Using the class `.sr-only` you can hide the labels of the inline forms.
- Bootstrap natively supports the most common form controls mainly `input, textarea, checkbox, radio, select`.
- Use `.checkbox-inline` or `.radio-inline` class to a series of checkboxes or radios for controls appear on the same line.
  
    ```html
    <label class="checkbox-inline">
        <input type="checkbox" id="inlineCheckbox1" value="option1"> Option 1
    </label>
    ```

- To disable something put the `disabled` word before closing the tag:
  - `<input class="form-control" id="disabledInput" type="text" placeholder="Disabled input here..." disabled>`

- Adding images to forms
  - The `.input-group` class is a container to enhance an input by adding an icon, text or a button in front or behind it as a "help text".
  - The `.input-group-addon` class attaches an icon or help text next to the input field.
    - Example 1: Picture before the text field:

      ```html
      <div class="input-group">
        <span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
        <input id="email" type="text" class="form-control" name="email" placeholder="Email">
      </div>
      ```

    - Example 2: Adding search button after text field:
  
      ```html
      <form>
        <div class="input-group">
          <input type="text" class="form-control" placeholder="Search">
          <div class="input-group-btn">
            <button class="btn btn-default" type="submit">
              <i class="glyphicon glyphicon-search"></i>
            </button>
          </div>
        </div>
      </form>
      ```

## Bootstrap Buttons

- `<button type="button" class="btn btn-default">Default Button</button>`
  - The class can be `btn-... -> primary, success, info, warning, danger`
  - Size: `btn-.. -> lg sm xs block` (Block taking up the entire vertical space?)
- States: either add them as classes or separate properties
  - Active -> `<button type="button" class="btn btn-default btn-lg active">` -> as class
  - Disabled -> `<button type="button" class="btn btn-default btn-lg" disabled="disabled">` -> as prop.

## Bootstrap Images

- `.img-rounded` -> rounded corners
- `.img-circle` -> image as circle
- `.img-thumbnail` -> extra "border"

## Bootstrap helper classes

- Visibility
  - `show, hidden, invisible`
  - Or use the responsive style:
    - Visible only on xs, sm, md...: `.visible-xs-*`, `.visible-sm-*`, `.visible-md-*`
    - Hidden only on xs, sm, md...: `.hidden-xs`, `.hidden-sm`, `.hidden-md`
- Caret:
  - The `.caret` class creates a caret arrow icon (down arrow), which indicates that the button is a dropdown.
  - E.g.: Add the following to a button **text**: `Dropdown <span class="caret"></span>` -> the button will have "Dropdown (downarrow)"

## Bootstrap Glyphicons

- Inserted via the following syntax: `<span class="glyphicon glyphicon-name"></span>`
- Funny thing is that you can insert these into links / buttons as well:
  - `<a href="#"><span class="glyphicon glyphicon-envelope"></span></a>`

  ```html
  <button type="button" class="btn btn-default">
        <span class="glyphicon glyphicon-search"></span> Search
  </button>
  ```

## Bootstrap Dropdown

    ```html
    <div class="dropdown">
      <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">Dropdown Example
      <span class="caret"></span></button>
      <ul class="dropdown-menu">
        <li><a href="#">HTML</a></li>
        <li><a href="#">CSS</a></li>
        <li><a href="#">JavaScript</a></li>
      </ul>
    </div>
    ```

- `<li class="divider"></li>` -> horizontal divider
- `<li class="dropdown-header">Dropdown header 1</li>` -> Extra (light gray) headers to categorize parts of the list
- `<li class="active"><a href="#">HTML</a></li>` -> disable/activate items

## Button Groups

- Use button groups to group buttons and add css classes for all of them at the same time:

    ```html
    <div class="btn-group btn-group-sm">
    <button type="button" class="btn btn-default">Button 4</button>
    <button type="button" class="btn btn-default">Button 5</button>
    </div>
    ```

