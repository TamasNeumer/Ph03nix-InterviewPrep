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

## Questions:
- CSS Syntax
  - What does CSS stand for?
  - Describe the most basic formatting syntax in CSS.
  - Describe the 3 kind of selectors and give an example.
  - How do you comment out in CSS?
- Adding CSS to HTML
  - What are the 3 ways of adding CSS to HTML? Give an exact example with code!
  - What does the "cascading" mean when having multiple stylesheets defined at multiple layers?
  - Which one is the dominant stylesheet if there are multiple styles defined for the same item?
- Colors
  - What are the three ways to specify color in CSS?
