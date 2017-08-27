# CSS

## Introduction
- **CSS** stands for Cascading Style Sheets
- CSS describes how HTML elements are to be displayed on screen, paper, or in other media
- CSS is used to define styles for your web pages, including the design, layout and variations in display for different devices and screen sizes.
- HTML was NEVER intended to contain tags for formatting a web page! --> CSS is the styling.

## CSS Syntax
- **Selector {property1: value1; property2: value2}**
  - h1 {color: blue; font-size: 12px;}
  - The selector points to the HTML element you want to style.
  - The declaration block contains one or more declarations separated by semicolons.

- Different selectors:
  - **Element selector:**
    - The element selector selects elements based on the element name.
    - You can select all ``<p>`` elements on a page like this (in this case, all `<p>` elements will be center-aligned, with a red text color):
    ```css
    p {
    text-align: center;
    color: red;
    }
    ```
  - **ID Selector**
    - The id selector uses the id attribute of an HTML element to select a specific element.
    - The id of an element should be unique within a page, so the id selector is used to select one unique element!
    - To select an element with a specific id, write a hash `#` character, followed by the id of the element.
    ```css
    #para1 {
    text-align: center;
    color: red;
    }
    ```
  - **The class Selector**
    - The class selector selects elements with a specific class attribute.
    - To select elements with a specific class, write a period (.) character, followed by the name of the class.
    ```css
    .center {
    text-align: center;
    color: red;
    }

    /*Narrowing down the search:*/
    p.center {
    text-align: center;
    color: red;
    }
    ```
  - **Combining selectors**
    - You can add multiple classes to an item by listing the classes delimited by space: `<p class="center large">This paragraph refers to two classes.</p>`
    - You can group selectors as:
      ```css
      /*This is valid for h1, h2 and p*/
      h1, h2, p {
        text-align: center;
        color: red;
      }

      /*All elements with class blue AND red.*/
      .blue.red {
        color: blue;
      }
      ```
  - **Pseudo class**
    - `:pseudo-class`
    - Pseudo-classes are keywords which allow selection based on information that lies outside of the document tree or that cannot be expressed by other selectors or combinators. (Such as state etc.) Examples include whether or not a link has been followed (`:visited`), the mouse is over an element (`:hover`), a checkbox is checked (`:checked`), etc.
      ```css
      selector:pseudo-class {
      property: value;
      }
      ```
    - Examples: `:disabled`, `:hover`, `:link` (link that has not been visited yet) and there are a ton more...
    - The `:in-range` CSS pseudo-class matches when an element has its value attribute inside the specified range limitations for this element. It allows the page to give a feedback that the value currently defined using the element is inside the range limits.
      ```css
      input:in-range {
          border: 1px solid blue;
      }

      <input type="number" min="10" max="20" value="15">
      <p>The border for this value will be blue</p>
      ```
  - **Pseudo elements**
    - `::pseudo-element`
    - Pseudo-elements are added to selectors but instead of describing a special state, they allow you to style certain parts of a document.
    ```css
    div::after {
      content: 'after';
      color: red;
      border: 1px solid red;
    }

    div {
      color: black;
      border: 1px solid black;
      padding: 1px;
    }

    div::before {
      content: 'before';
      color: green;
      border: 1px solid green;
    }

    p::after{
      content: url(smiley.gif);
    }
    ```
  - **Attribute selector**
    - `[attr]	--> <div attr>` --> Matches element with attr
    - `[attr='val'] --> <div attr="val">` --> Where attribute attr has value val
    - `[attr~='val']	<div attr="val val2 val3">` --> Where val appears in the whitespace-separated list of attr
    - `[attr^='val']	<div attr="val1 val2">` --> Where attr's value begins with val
    - `[attr$='val']	<div attr="sth aval">` --> Where the attr's value ends with val
    - `[attr*='val']	<div attr="somevalhere">` --> Where attr contains val anywhere
    - `[attr|='val']	<div attr="val-sth etc">` --> Where attr's value is exactly val, or starts with val and immediately followed by - (U+002D)
    - `[attr='val' i]	<div attr="val">` --> Where attr has value val, ignoring val's letter casing.
    - `:lang(en)` --> Element that matches :lang declaration, for example `<span lang="en">`
  - **Combinators**
    - `div > p` --> (direct) Child selector
    - `div span` --> Descendant selector (all span, desc. of div)
    - `a ~ <span>` --> General sibling selector (`<span>`s that are siblings after an `<a>`)
    - `a + span`	Adjacent Sibling selector (all `<span>`s that are immediately after an `<a>`)
- Comments, using block comments: `/* Comment */`

## Inserting CSS
- There are **three** ways to insert CSS into an HTML:
  - **External style sheet**
    - Each page must include a reference to the external style sheet file inside the ``<link>`` element. The ``<link>`` element goes inside the ``<head>`` section:
    ```html
    <head>
    <link rel="stylesheet" type="text/css" href="mystyle.css">
    @import url('/css/styles.css'); <!-- using @ import -->
    @import 'https://fonts.googleapis.com/css?family=Lato'; <!-- using external url for fonts -->
    </head>
    ```
    - When someone first visits your website, their browser downloads the HTML of the current page plus the linked CSS file. Then when they navigate to another page, their browser only needs to download the HTML of that page; the CSS file is cached, so it does not need to be downloaded again. Since browsers cache the external stylesheet, your pages load faster.
  - **Internal style sheet**
    - An internal style sheet may be used if one single page has a unique style. (= no reuse)
    - Internal styles are defined within the `<style>` element, inside the `<head>` section of an HTML page:
    ```html
    <head>
    <style>
    body {
        background-color: linen;
    }
    </style>
    </head>
    ```
  - **Inline style**
    - An inline style may be used to apply a unique style for a single element.
    - To use inline styles, add the style attribute to the relevant element. The style attribute can contain any CSS property.
    ```html
    <h1 style="color:blue;margin-left:30px;">This is a heading</h1>
    ```
  - If some properties have been defined for the same selector (element) in different style sheets, the value from **the last read style sheet will be used**.
- Cascading order:
  - Generally speaking we can say that all the styles will "cascade" into a new "virtual" style sheet by the following rules, where number one has the highest priority:
    1. Inline style (inside an HTML element)
    2. External and internal style sheets (in the head section)
    3. Browser default
- You can also change the stylsheet using pure JavaScript or jQuery:
    ```js
    var el = document.getElementById("element");
    el.style.opacity = 0.5;
    el.style.fontFamily = 'sans-serif';

    $('#element').css('margin', '5px');

    var el2 = document.querySelector("#example");
    /*...*/
    ```

## Colors
- a valid color name - like "red"
- an RGB value - like "rgb(255, 0, 0)"
  - RGB color values can be specified using this formula: rgb(red, green, blue).
- a HEX value - like "#ff0000"
  - RGB values can also be specified using hexadecimal color values in the form: #RRGGBB, where RR (red), GG (green) and BB (blue) are hexadecimal values between 00 and FF (same as decimal 0-255).
  - If both "RR"/"GG"/"BB" values are the same you can shorten the hex code into `#RGB`
- RGBA() notation
  - `color: rgba(255, 0, 0, 1);` --> Adds opacity (a number from 0 - 1, where 0.0 is fully transparent and 1.0 is fully opaque)
- HLS() notation
  - `color: hsl(<hue>, <saturation>%, <lightness>%);`
- HLSA() same as with RGBA()

## Background
- `background-color: red;` --> adding simple background-color
- `background-image: url(partiallytransparentimage.png);` --> adding bg image
- Background gradients (CSS3):
  - `linear-gradient() repeating-linear-gradient() radial-gradient() repeating-radial-gradient()`
  - `background: linear-gradient( <direction>?, <color-stop-1>, <color-stop-2>, ...);`
    - `<direction>` --> Could be an argument like to top, to bottom, to right or to left; or an angle as 0deg, 90deg... . The angle starts from to top and rotates clockwise. Can be specified in deg, grad, rad, or turn. If omitted, the gradient flows from top to bottom
    - `<color-stop-list>` --> List of colors, optionally followed each one by a percentage or length to display it at. For example, yellow 10%, rgba(0,0,0,.5) 40px, #fff 100%...
  - `background-repeat: repeat-x;` --> Repeating image
  - `background-position: right top;` --> position
  - `background-attachment: fixed;` --> No scrolling with page (stays fixed)
  - `background: #ffffff url("img_tree.png") no-repeat right top;` --> **The background property can be used to set one or more background related properties:**

## Border
- `border-style:` --> Provides numerous attributes (solid, dotted etc.)
- `border-width`--> (in px)
- `border-color`
- Individual side styles:
  - `border-top-style, border-right-style`, etc.
- `border: (width, style, color)` --> border shorthand
  - `p { border: 10px solid green;}`
    ```css
    padding: 25px 50px 75px 100px; /* top right bottom left; */
    padding: 25px 50px 75px; /* top left/right bottom */
    padding: 25px 50px; /* top/bottom left/right */
    padding: 25px; /* top/right/bottom/left */
    ```
- `border-radius`
  - Every corner of an element can have up to two values, for the vertical and horizontal radius of that corner (for a maximum of 8 values).
- The `border-collapse` property applies only to tables (and elements displayed as display: table or inline-table) and sets whether the table borders are collapsed into a single border or detached as in standard HTML.

## Margins
- The CSS margin properties are used to generate space around elements.
- The margin properties set the size of the white space **outside** the border.
- ` margin: 100px 150px 100px 80px;` --> Margin shorthand property (top, right, bottom, left)
- You can set the margin property to `auto` to horizontally center the element within its container.
- `inherit` --> lets the margin be inherited from the parent element.
- **Margin collapse**
  - When two margins are touching each other vertically, they are collapsed. When two margins touch horizontally, they do not collapse.
  - In this example the h1 element has a bottom margin of 50px and the h2 element has a top margin of 20px. Then, the vertical margin between h1 and h2 should have been 70px (50px + 20px). However, due to margin collapse, the actual margin ends up being 50px. --> always takes the greater value of the two.

## Paddings
- The CSS padding properties are used to generate space **around** content. The padding clears an area around the content (**inside the border**) of an element.
-Syntax similar to margin.

## Hight and Width:
- The `height` and `width` properties are used to set the height and width of an element.
- `max-width`, `max-height` set the maximum of these values.
- The problem with the <div> above occurs when the browser window is smaller than the width of the element (500px). The browser then adds a horizontal scrollbar to the page. Using max-width instead, in this situation, will improve the browser's handling of small windows.

## Box Modell
- The CSS box model is essentially a box that wraps around every HTML element. It consists of: margins, borders, padding, and the actual content. The image below illustrates the box model:

  ![CSSBoxModel](Web/CSS/res)

- Important: When you set the width and height properties of an element with CSS, you just set the width and height of the content area. To calculate the full size of an element, you must also add padding, borders and margins.
- **Box Sizing**
  - The default box model (content-box) can be counter-intuitive, since the width / height for an element will not represent its actual width or height on screen as soon as you start adding padding and border styles to the element.
  - To solve the textarea problem above, you could just change the `box-sizing` property to `padding-box` or `border-box`. `border-box` is most commonly used.
    ```css
    textarea {
      width: 100%;
      padding: 3px;
      box-sizing: border-box;
    }

    /*To apply a specific box model to every element on the page, use the following snippet:*/
    html {
    box-sizing: border-box;
    }

    *, *:before, *:after {
        box-sizing: inherit;
    }
    ```
## Outline:
- The CSS outline properties specify the style, color, and width of an outline. An outline is a line that is drawn around elements (**outside the borders**) to make the element "stand out".
- The outline is **NOT** a part of an element's dimensions; the element's total width and height is not affected by the width of the outline.
- Styling the outline is same as border.
- Unlike borders, outlines cannot have rounded corners.

## Text
- `text-align` property is used to set the horizontal alignment of a text.
  - Center, left, right, justify
- `text-decoration` property is used to set or remove decorations from text.
  - none, overline, line-through, underline
- `text-transform` property is used to specify uppercase and lowercase letters in a text.
  - uppercase, lowercase, capitalize (first letter of each word)
- `text-indent` property is used to specify the indentation of the first line of a text. (in "px")
- `letter-spacing` property is used to specify the space between the characters in a text.
- `line-height` property is used to specify the space between lines
- `word-spacing` property is used to specify the space between the words in a text.
- `text-shadow `property adds shadow to text.
- `text-overflow` property specifies how overflowed content that is not displayed should be signaled to the user.
  - `overflow: hidden; text-overflow: clip;`
- `word-wrap` property allows long words to be able to be broken and wrap onto the next line.
    - `word-wrap: break-word;`
- `word-break` property specifies line breaking rules
  - `word-break: keep-all; word-break: break-all;`

## Font
- In CSS, there are two types of font family names:
    - generic family - a group of font families with a similar look (like "Serif" or "Monospace")
  - font family - a specific font family (like "Times New Roman" or "Arial")
- The font-family property should hold several font names as a "fallback" system. If the browser does not support the first font, it tries the next font, and so on. Start with the font you want, and end with a generic family, to let the browser pick a similar font in the generic family, if no other fonts are available.
- The `font-style` property is mostly used to specify italic text.
  - normal, italic, oblique
- `font-size` property sets the size of the text.
  - To allow users to resize the text (in the browser menu), many developers use **em** instead of pixels. The em size unit is recommended by the W3C. 1em is equal to the current font size. The default text size in browsers is 16px. So, the default size of 1em is 16px.
  - The solution that works in all browsers, is to set a default font-size in percent for the <body> element
- `font-weight` property specifies the weight of a font:
  - normal, bold
  - Or you can specify it with a value e.g.: `font-weight: 400;`
- `text-shadow` adds shadow to text.
  ```css
  p {
      font-family: "Times New Roman", Times, serif;
  }

  body {
    font-size: 100%;
  }

  h1 {
      font-size: 2.5em;
  }

  element {
    font: [font-style] [font-variant] [font-weight] [font-size/line-height] [font-family];
  }
  ```
  - `quotes: "«" "»";` --> Add extra quotation around text.

## Fonts:
- The simplest way to add an icon to your HTML page, is with an icon library, such as Font Awesome. Add the name of the specified icon class to any inline HTML element (like <i> or <span>). All the icons in the icon libraries below, are scalable vectors that can be customized with CSS (size, color, shadow, etc.)
- **Font Awesome Icons**
  - To use the Font Awesome icons, add the following line inside the `<head>` section of your HTML page: `<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">`
  - To use simply add: `<i class="fa fa-cloud"></i>`
- **Bootstrap Icons**
  - `<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">`
    - To use simply add: `<i class="glyphicon glyphicon-cloud"></i>`
- **Google Icons**
  - `<link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">`
    - To use simply add: `<i class="material-icons">cloud</i>`

## Links
- Links can be styled with any CSS property (e.g. color, font-family, background, etc.).
- The four links states are:
  - `a:link` - a normal, unvisited link
  - `a:visited` - a link the user has visited
  - `a:hover` - a link when the user mouses over it
  - `a:active` - a link the moment it is clicked
- Links are in `a` block:
  - `<a href="https://www.w3schools.com">W3Schools.com</a>`

## Lists
- In HTML, there are two main types of lists:
  - unordered lists (`<ul>`) - the list items are marked with bullets
  - ordered lists (`<ol>`) - the list items are marked with numbers or letters
- `list-style-type` property specifies the type of list item marker.
  - cicle, square, upper-roman, lower-alpha, or '-' <-- any string
- `list-style-image` property specifies an image as the list item marker:
  - `list-style-image: url('sqpurple.gif');`
- `list-style-position` property specifies whether the list-item markers should appear inside or outside the content flow.
  - [Example](https://jsfiddle.net/pqh3cxdp/)
- `list-style-type:none` property can also be used to remove the markers/bullets. Note that the list also has default margin and padding. To remove this, add `margin:0` and `padding:0` to `<ul>` or `<ol>`
- **Shorthand:** `list-style: square inside url("sqpurple.gif");`

## Display property
- The display property specifies if/how an element is displayed.
- Every HTML element has a default display value depending on what type of element it is. The default display value for most elements is `block` or `inline`.
- **Block-level Elements**
  - A block-level element always starts on a new line and takes up the full width available (stretches out to the left and right as far as it can).
  - div, h, p form, header, footer, section
- **Inline Elements**
  - An inline element does not start on a new line and only takes up as much width as necessary.
    - span, a, img
- **inline-block**
  - `inline-block` value gives us the best of both worlds: it blends the element in with the flow of the text while allowing us to use padding, margin, height and similar properties which have no visible effect on  inline elements.


- **Overriding display**
  - You can override the display of any element. E.g.: When you want to create a horizontal menu of divs, you set `display: inline`
- **Removing elements: display vs visibility**
  - Hiding an element can be done by setting the display property to `none`. The element will be hidden, and the page will be displayed as if the element is not there.
    - `display: none`
  - Hiding can be also done by setting the visibility. However, the element will still take up the same space as before. The element will be hidden, but still affect the layout.
  - `visibility: hidden;`
## Tables
- Tables consist of the following tags:
  - Each table row is defined with the `<tr>` tag.
  - A table header is defined with the `<th>` tag.
  - A table data/cell is defined with the `<td>` tag.
  - The table is defined with the `<table>` tag.
- `border-collapse` property sets whether the table borders should be collapsed into a single border --> (border-collapse: collapse;)
- You can use the already learnt css properties on the cells/rows etc:
  - `tr:hover {background-color: #f5f5f5}`
- For zebra-striped tables, use the nth-child() selector and add a background-color to all even (or odd) table rows:
  - `tr:nth-child(even) {background-color: #f2f2f2}`
- Responsive (auto-sizing) tables:
  - dd a container element (like `<div>`) with `overflow-x:auto` around the `<table>` element to make it responsive.
- `empty-cells` property determines if cells with no content should be displayed or not.
  - `empty-cells: show/hide`
- `caption-side` property determines the vertical positioning of the <caption> element within a table
  - `caption-side: top/bottom`

## Position:
- The `position` property specifies the type of positioning method used for an element (static, relative, fixed or absolute).
- **Static**
  - HTML elements are positioned static by default. An element with position: static; is not positioned in any special way; it is always positioned according to the normal flow of the page.
- **Relative**
  - Setting the top, right, bottom, and left properties of a relatively-positioned element will cause it to be adjusted away from its normal position. Other content will not be adjusted to fit into any gap left by the element.
- **Fixed**
  - An element with position: fixed; is positioned **relative** to the viewport, which means it always stays in the same place even if the page is scrolled. The top, right, bottom, and left properties are used to position the element. A fixed element does not leave a gap in the page where it would normally have been located.
- **Absolute**
  - An element with position: absolute; is positioned **relative to the nearest positioned ancestor** (instead of positioned relative to the viewport, like fixed). However; if an absolute positioned element has no positioned ancestors, it uses the document body, and moves along with page scrolling.
- **Z-Index**
  - z-index property specifies the stack order of an element (which element should be placed in front of, or behind, the others)

## Overflow
- The CSS overflow property specifies whether to clip content or to add scrollbars when the content of an element is too big to fit in a specified area.
- Possible values:
  - `visible` - Default. The overflow is not clipped. It renders outside the element's box
  - `hidden` - The overflow is clipped, and the rest of the content will be invisible
  - `scroll` - The overflow is clipped, but a scrollbar is added to see the rest of the content
  - `auto` - If overflow is clipped, a scrollbar should be added to see the rest of the content
- `overflow-x` and `overflow-y` properties specifies whether to change the overflow of content just horizontally or vertically (or both)

## Float
- In its simplest use, the `float` property can be used to wrap text around images.
  - Float can be also used to right-align a button in a menu: `<li style="float:right"><a class="active" href="#about">About</a></li>`
- The `clear` property is used to control the behavior of floating elements.
- The `clearfix` hack is a popular way to contain floats
  - If an element is taller than the element containing it, and it is floated, it will overflow outside of its container. The `overflow:auto` clearfix works well as long as you are able to keep control of your margins and padding (else you might see scrollbars). The new, modern clearfix hack however, is safer to use, and the following code is used for most webpages:
    ```css
    .clearfix::after {
        content: "";
        clear: both;
        display: table;
    }
    ```
## Opacity
- The `opacity` property specifies the opacity/transparency of an element. The opacity property can take a value from 0.0 - 1.0. The lower value, the more transparent.
- IE8 and earlier use `filter:alpha(opacity=x)`. The x can take a value from 0 - 100. A lower value makes the element more transparent.
    ```css
    img {
        opacity: 0.5;
        filter: alpha(opacity=50); /* For IE8 and earlier */
    }

    img:hover {
        opacity: 1.0;
        filter: alpha(opacity=100); /* For IE8 and earlier */
    }
    ```
# CSS 3
CSS3 is the latest standard for CSS. CSS3 is completely backwards-compatible with earlier versions of CSS.
## Rounded Corners
- `border-radius` property, you can give any element "rounded corners".
  - `border-radius: 25px;`
  - `border-radius: 15px 50px 30px 5px;`
  - `border-bottom-left-radius: 25px;`
## Border Image
- `border-image` property allows you to specify an image to be used instead of the normal border around an element.
- The property has three parts:
  - The image to use as the border
  - Where to slice the image
  - Define whether the middle sections should be repeated or stretched.
- The border-image property takes the image and slices it into nine sections, like a tic-tac-toe board. It then places the corners at the corners, and the middle sections are repeated or stretched as you specify.
  ```css
  #borderimg {
      border: 10px solid transparent;
      padding: 15px;
      -webkit-border-image: url(border.png) 30 stretch; /* Safari 3.1-5 */
      -o-border-image: url(border.png) 30 stretch; /* Opera 11-12.1 */
      border-image: url(border.png) 30 stretch;
      /* OR:  border-image: url(border.png) 20% round; */
  }
  ```

## Background
- CSS3 allows you to add multiple background images for an element, through the `background-image` property. The different background images are separated by commas, and the images are stacked on top of each other, where the first image is closest to the viewer.
  ```css
  #example1 {
      background-image: url(img_flwr.gif), url(paper.gif);
      background-position: right bottom, left top;
      background-repeat: no-repeat, repeat;
  }
  ```
- `background-size` property allows you to specify the size of background images.
  - The `contain` keyword scales the background image to be as large as possible (but both its width and its height must fit inside the content area). As such, depending on the proportions of the background image and the background positioning area, there may be some areas of the background which are not covered by the background image.
  - The `cover` keyword scales the background image so that the content area is completely covered by the background image (both its width and height are equal to or exceed the content area). As such, some parts of the background image may not be visible in the background positioning area.
  - The background-size property also accepts multiple values for background size (using a comma-separated list), when working with multiple backgrounds.
- `background-origin` property specifies where the background image is positioned. 3 possible values:
  - `border-box` - the background image starts from the upper left corner of the border
  - `padding-box` - (default) the background image starts from the upper left corner of the padding edge
  - `content-box` - the background image starts from the upper left corner of the content

## Color
- In addition, CSS3 also introduces:
  - RGBA colors
  - HSL colors
  - HSLA colors
  - opacity

## Shadow
- `box-shadow` property applies shadow to elements.
  - horizontal, vertical (px)
  - blur (px)
  - color
  - Adding 2 shadows, second with 0px 0px: `text-shadow: 2px 2px 5px green, 0 0 10px red;`
    ```css
    #boxshadow {
        position: relative;
        box-shadow: 1px 2px 4px rgba(0, 0, 0, .5);
        padding: 10px;
        background: white;
    }

    #boxshadow img {
        width: 100%;
        border: 1px solid #8a4419;
        border-style: inset;
    }

    #boxshadow::after {
        content: '';
        position: absolute;
        z-index: -1; /* hide shadow behind image */
        box-shadow: 0 15px 20px rgba(0, 0, 0, 0.3);
        width: 70%;
        left: 15%; /* one half of the remaining 30% */
        height: 100px;
        bottom: 0;
    }
    ```
## Transform
CSS3 transforms allow you to translate, rotate, scale, and skew elements. A transformation is an effect that lets an element change shape, size and position. CSS3 supports 2D and 3D transformations.
- **2D Transformation**
  - translate()
    - `transform: translate(50px, 100px);`
  - rotate()
    - `transform: rotate(20deg);`
  - scale()
    - `transform: scale(2, 3);`
  - skewX()
    - `transform: skewX(20deg);`
  - skewY()
    - transform: skew(20deg, 30deg); // Skwing X and Y
  - matrix()
    - The matrix() method combines all the 2D transform methods into one.
    - The parameters are as follow: `matrix(scaleX(),skewY(),skewX(),scaleY(),translateX(),translateY())`
- **3D Transformation**
  - rotateZ, rotateY, rotateX

## Transition
- To create a transition effect, you must specify two things:
  - the CSS property you want to add an effect to
  - the duration of the effect
    - `transition: width 2s;`

    ```css
    div {
        width: 100px;
        height: 100px;
        background: red;
        -webkit-transition: width 2s; /* For Safari 3.1 to 6.0 */
        transition: width 2s;
    }

    div:hover {
        width: 300px;
    }
    ```
  - `transition-timing-function` property specifies the speed curve of the transition effect.
    - `ease` - specifies a transition effect with a slow start, then fast, then end slowly (this is default)
    - `linear` - specifies a transition effect with the same speed from start to end
    - `ease-in` - specifies a transition effect with a slow start
    - `ease-out` - specifies a transition effect with a slow end
    - `ease-in-out` - specifies a transition effect with a slow start and end
    - `cubic-bezier(n,n,n,n)` - lets you define your own values in a cubic-bezier function
  - `transition-delay` property specifies a delay (in seconds) for the transition effect.

## Animation
- An animation lets an element gradually change from one style to another. You can change as many CSS properties you want, as many times you want. To use CSS3 animation, you must first specify some keyframes for the animation. Keyframes hold what styles the element will have at certain times.
- When you specify CSS styles inside the `@keyframes` rule, the animation will gradually change from the current style to the new style at certain times. To get an animation to work, you must bind the animation to an element. The following example binds the "example" animation to the <div> element. The animation will last for 4 seconds, and it will gradually change the background-color of the <div> element from "red" to "yellow":
  ```css
  /* The animation code */
  @keyframes example {
      from {background-color: red;}
      to {background-color: yellow;}
  }

  /*other example*/
  @keyframes example {
    0%   {background-color: red;}
    25%  {background-color: yellow;}
    50%  {background-color: blue;}
    100% {background-color: green;}
  }

  /* The element to apply the animation to */
  div {
      width: 100px;
      height: 100px;
      background-color: red;
      animation-name: example;
      animation-duration: 4s;
  }
  ```
  - `animation-duration` property is not specified, the animation will have no effect, because the default value is 0.
  - `animation-delay` property specifies a delay for the start of an animation.
  - `animation-iteration-count` property specifies the number of times an animation should run
  - `animation-direction` property is used to let an animation run in reverse direction or alternate cycles.
  - `animation-timing-function` property specifies the speed curve of the animation.
  - Animation shorthand property:
  ```css
  div {
    animation-name: example;
    animation-duration: 5s;
    animation-timing-function: linear;
    animation-delay: 2s;
    animation-iteration-count: infinite;
    animation-direction: alternate;
  }
  div {
    animation: example 5s linear 2s infinite alternate;
  }
  ```
  - `animation-fill-mode` specifies a style for the element when the animation is not playing (when it is finished, or when it has a delay).
    - `animation-fill-mode: none|forwards|backwards|both|initial|inherit;`
# Courses
## Motion design with CSS
- **Transition**
  - Don't use `transition-property: all;` as it very hard on resources.
  - Usually animations last for 250-300ms
  - Awesome link for transition functions: http://easings.net/
  - Summary:
    - Single fire = if you want something to happen once
    - Granularity = if you would only animate one or two properties
    - Bulletproof = if transitions aren't supported the change happens anyway
- **Animations**
  - Awesome animation links: https://speckyboy.com/css-animation/
  - Awesome link to see if you "can use": http://caniuse.com
  - `steps(x)` is a timing function, that splits a block of keyframes into x equal steps, then hops between them.
    - i.e. I have a series of pics (just as the catwalk.png) where we go from 0 to -2400px in 200px steps --> steps(12)
  - Cat walk animation:
    ```css
    .tuna {
      background: url(http://stash.rachelnabors.com/animation-workshop/sprite_catwalk.png) 0 0 no-repeat;
      animation: tunaWalk 1s steps(12) infinite;
      height: 200px;
      width: 400px;
      margin: 100px auto 0;
    }

    @keyframes tunaWalk {
      0% { background-position: 0 0; }
      100% {background-position: 0 -2400px;}
    }
    ```
  - `animation-fill-mode` --> The animation-fill-mode property specifies a style for the element when the animation is not playing (when it is finished, or when it has a delay).
    - Possible values:
      - `none`	Default value. The animation will not apply any styles to the target element before or after it is executing
      - `forwards`	After the animation ends (determined by animation-iteration-count), the animation will apply the property values for the time the animation ended
      - `backwards`	The animation will apply the property values defined in the keyframe that will start the first iteration of the animation, during the period defined by animation-delay. These are either the values of the from keyframe (when animation-direction is "normal" or "alternate") or those of the to keyframe (when animation-direction is "reverse" or "alternate-reverse")
      - `both`	The animation will follow the rules for both forwards and backwards. That is, it will extend the animation properties in both directions
      - `initial`	Sets this property to its default value.
      - `inherit`	Inherits this property from its parent element.
  - `animation-play-state` --> The animation-play-state property specifies whether the animation is running or paused.
    - `animation-play-state: paused|running|initial|inherit;`
  - `animation-direction` --> The animation-direction property defines whether an animation should play in reverse direction or in alternate cycles.
    - `animation-direction: normal|reverse|alternate|alternate-reverse|initial|inherit;`
  - Summary:
    - (Safari and Android could still use -webkit- at this time. (Webkit prefix))
    - Allows looping. (Repeating)
    - Self starting, doesn't require trigger.
    - Altering: Play back and forth.
    - Grouping: Each animation can change a number of properties
- **Stateful animation**
  - TODO Evening

## Useful Links:
- CSS Shapes: https://css-tricks.com/examples/ShapesOfCSS/
- Free CSS Anim: https://greensock.com/
