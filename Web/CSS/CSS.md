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
    - You can add multiple classes to an item by listing the classes delimited by space: `<p class="center large">This paragraph refers to two classes.</p>`
    - You can group selectors as:
    ```css
    h1, h2, p {
      text-align: center;
      color: red;
    }
    ```
- Comments, using block comments: `/* Comment */`

## Inserting CSS
- There are **three** ways to insert CSS into an HTML:
  - **External style sheet**
    - Each page must include a reference to the external style sheet file inside the ``<link>`` element. The ``<link>`` element goes inside the ``<head>`` section:
    ```html
    <head>
    <link rel="stylesheet" type="text/css" href="mystyle.css">
    </head>
    ```
  - **Internal style sheet**
    - An internal style sheet may be used if one single page has a unique style. (= no reuse)
    - Internal styles are defined within the <style> element, inside the <head> section of an HTML page:
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
