# SASS-CSS
## Preprocessing
- Sass files need to be "converted" to traditional css files. This is done by:
  - `sass input.scss output.css`
  - `sass --watch app/sass:public/stylesheets`
    - You can watch either individual files or entire directories.

## Variables
- Sass uses the $ symbol to make something a variable.
  ```css
  $font-stack:    Helvetica, sans-serif;
  $primary-color: #333;

  body {
  font: 100% $font-stack;
  color: $primary-color;
  }
  ```

## Nesting
- Sass will let you nest your CSS selectors in a way that follows the same visual hierarchy of your HTML. Be aware that overly nested rules will result in over-qualified CSS that could prove hard to maintain and is generally considered bad practice.  
  ```css
  nav
    ul
      margin: 0
      padding: 0
      list-style: none

  /*Will become:*/
  nav ul {
    margin: 0;
    padding: 0;
    list-style: none;
  }
  ```
##  Partials
- You can create partial Sass files that contain little snippets of CSS that you can include in other Sass files. This is a great way to modularize your CSS and help keep things easier to maintain.
- **A partial is simply a Sass file named with a leading underscore.** You might name it something like `_partial.scss`. The underscore lets Sass know that the file is only a partial file and that it should not be generated into a CSS file. **Sass partials are used with the `@import` directive.**

## Import
- Sass builds on top of the current CSS @import but instead of requiring an HTTP request, Sass will take the file that you want to import and combine it with the file you're importing into so you can serve a single CSS file to the web browser.
  - `@import reset` --> Notice we're using @import 'reset'; in the base.scss file. When you import a file you don't need to include the file extension .scss. Sass is smart and will figure it out for you.

## Mixins
- A mixin lets you make groups of CSS declarations that you want to reuse throughout your site. You can even pass in values to make your mixin more flexible. A good use of a mixin is for vendor prefixes. Here's an example for border-radius.
  ```css
    =border-radius($radius)
      -webkit-border-radius: $radius
      -moz-border-radius:    $radius
      -ms-border-radius:     $radius
      border-radius:         $radius

    .box
      +border-radius(10px)
  ```

## Extend / Inheritance
-  Using @extend lets you share a set of CSS properties from one selector to another. It helps keep your Sass very DRY. In our example we're going to create a simple series of messaging for errors, warnings and successes.
```css
.message{
  border: 1px solid #ccc
  padding: 10px
  color: #333
}
.success {
  @extend .message;
  border-color: green;
}
```
## Operators
- Doing math in your CSS is very helpful. Sass has a handful of standard math operators like `+, -, *, /`, and `%`.
