# CSS

## Center element to the exact center of the WINDOW

- Use `top 50%`, and `left 50%` -> upper left corner will be in the exact center
- Apply `position: fixed` -> align stuff to the browser window!
- Move the image/video to the center by
  - translating it with its 50% height/width (elegant) -->  transform: `translate(-50%, -50%)`
  - margin-left: -50px i.e. with fixed px sizes. Not that elegant...

## "Just fill the space"

- `min-width: 100%, width: auto` (default value hence meaningless?!)
- `min-width: 100%` says that if the element has a smaller size than its container, then it will be stretched to fit.

## Stacking context

- [Link](https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_Positioning/Understanding_z_index/The_stacking_context)

- **A static context is created when**
  - When an element is the root element of a document (the `<html>` element)
  - When an element has a position value other than static and a z-index value other than auto
  - When an element has an opacity value less than 1
  - Several newer CSS properties also create stacking contexts. These include: transforms, filters, css-regions, paged media, and possibly others
  - As a general rule, it seems that **if a CSS property requires rendering in an offscreen context, it must create a new stacking context**.
- **The Z-Index does not work with static positioning. And STATIC is the DEFAULT!**

## Media query

- Basics
  - `@media (max-width:632px)` -> This one is saying for a window with a max-width of 632px that you want to apply these styles.
  - `@media screen and (max-width:632px)` -> This one is saying for a device with a `screen` and a window with max-width of 632px apply the style. (See other [media types](https://www.w3.org/TR/CSS2/media.html))
  - `@media only screen and (max-width:632px)` -> the `only` keyword hides the media queries from older browsers that don't support it at all.
- Media query for mobile devices:
  - `@media (pointer: coarse) and (hover: none){...}`
    - `pointer: coarse` -> anything where the "pointer" is not `fine` -> i.e. not precise
  
## Playing Video

```html
<video playsinline="playsinline" autoplay="autoplay" muted="muted" loop="loop">
    <source src="mp4/bg.mp4" type="video/mp4">
</video>
```

- `playsinline="playsinline"` -> Video is played within the element's playback area. (Rest of the properties self explanatory.)
- **Be mobile first** --> Instead of hiding the resource with a media query on mobile devices, set it immediately to `display: none` and only allow it above a specific size. --> Hence reduce the chance of downloading 6mb of videos...

## Overflow

- The overflow property specifies what should happen if content overflows an element's box.
- `scroll` (scroll added), `hidden` (overflow clipped)
- `visible` - The overflow is not clipped. It renders outside the element's box. **This is default**

## left 0 right 0 bottom 0 top 0 // height:100% vs bottom:0

- **height:100% vs bottom:0**
  - `height:100%` child_height = parent_height
  - `bottom: 0` => child_height = parent_height - child_margin - child_border
  - In other words height: `100%` sets the **inner height** of the child to the same height of its parent, while `bottom: 0` sets the **outer height** of the child to the same height of its parent. --> with `100% + margin: 10px` you can "overflow" your parent! In `bottom: 0` not! See: [Live JsFiddle](http://jsfiddle.net/2N4QJ/1/)
- **all props 0**
  - stretching out the content to fill its parent!

## Skew & transform origin

- SkewY -> kinna like giving it a slap and making a rectangle into a  parallelogramm
  - skewX = horizonral slap: `|__|` -> `/__/`
  - skewY = vertical slap.
- transform-origin determines from which point (or corner) the transformation origins.

## CSS selectors

- Descendant: `div p` (all `p` inside of `div`s)
- Child selector: `div > p` (all `<p>` elements that are immediate children of a `<div>` )
- Adjacent Sibling: `div + p` (`<p>` elements that are placed immediately after `<div>` elements)
- General Sibling `div ~ p` (all `<p>` elements that are siblings of `<div>`)

- Others (state etc)
  - `:hover`
  - `:lastChild` / `:firstChild`
  - `::selection` -> matches the portion of an element that is selected by a user. ()
  - `:focus` - e.g.: when you click inside a text field

## HTML Tags`<hr>` tag

- `<hr>`
  - The `<hr>` tag defines a thematic break in an HTML page and is used to separate content (or define a change) in an HTML page.
  - i.e. draws a horizontal line
- `<header>`
  - The `<header>` element represents a container for introductory content or a set of navigational links.

## Calc

- `width: calc(100% - 100px);` (or for padding etc.)
- i.e. you want the same top/bottom padding, but you have a fixed nav-bar. since navbar is 57px: `padding-bottom: calc(10rem - 56px);` (And you already have `padding-top: 10rem;`)

## Shorthand annotation

- 1 value: all props
- 2 value: top/bottom, left/right
- 3 values: top, bottom, left/right
- 4 values: top, right, bottom, left

## Dynamic transition

- CSS values can only be transitioned **to** and **from** **fixed unit values.**
- The best way to add/remove a class is to use the `toggle` function:
  - `document.querySelector('.section.collapsible').classList.toggle('collapsed');`
- **1. Max height setting**
  - Have the height on `auto`. Now the content of the `div` (e.g. text length) determines the height, until `height < maxHeight`
  - Transition is done by setting `max-height` as fix values. (E.g. form `600px` to `0px`)
- **2. scaleY()**
  - They operate on the element's visual representation as if it were simply an image, rather than a DOM element. This means, for example, that an element scaled up too far will look pixellated.
  - They do not trigger **reflows**. -> i.e. elements don't get re-organized.
  - `scaleY(0);` in collapesed state and `scaleY(1);` in full state.
  - Not really the effect you are looking for as there is no reflow.
- **3. JS**
  - The basic strategy is to manually do what the browser refuses to: calculate the full size of the element's contents, then CSS transition the element to that explicit pixel size.

```js
function collapseSection(element) {
  // get the height of the element's inner content, regardless of its actual size
  // -> measurement of the height of an element's content, including content
  // not visible on the screen due to overflow.
  var sectionHeight = element.scrollHeight;
  
  // temporarily disable all css transitions by saving transition css content
  // to temp, and then setting it to empty
  var elementTransition = element.style.transition;
  element.style.transition = '';
  
  // on the next frame (as soon as the previous style change has taken effect),
  // explicitly set the element's height to its current pixel height, so we
  // aren't transitioning out of 'auto'
  /*
  If these were sequential lines in the code, the result would be as if they'd all
  been set simultaneously since the browser doesn't re-render in parallel to
  Javascript execution (at least, for our purposes).
  */
  requestAnimationFrame(function() {
    element.style.height = sectionHeight + 'px';
    element.style.transition = elementTransition;

    // on the next frame (as soon as the previous style change has taken effect),
    // have the element transition to height: 0
    requestAnimationFrame(function() {
      element.style.height = 0 + 'px';
    });
  });
  
  // mark the section as "currently collapsed"
  // If this were a React or Angular app, this would be a state variable.
  element.setAttribute('data-collapsed', 'true');
}

function expandSection(element) {
  // get the height of the element's inner content, regardless of its actual size
  var sectionHeight = element.scrollHeight;
  
  // have the element transition to the height of its inner content
  element.style.height = sectionHeight + 'px';

  // when the next css transition finishes (which should be the one we just triggered)
  element.addEventListener('transitionend', function(e) {
    // remove this event listener so it only gets triggered once
    element.removeEventListener('transitionend', arguments.callee);

    // remove "height" from the element's inline styles, so it can return to its initial value
    // -> i.e. to auto
    element.style.height = null;
  });
  
  // mark the section as "currently not collapsed"
  element.setAttribute('data-collapsed', 'false');
}
```

## Selector precedence

- Inline is more specific than id
- Id is more specific than class
- Class is more specific than element

## BS specific

- `.fixed-top` (for navbar for example)
  - `top: 0, left : 0, right: 0, position: fixed`
- `(m)pxxx`
  - Set either the margin or padding on specific sides.
  - `mt-1` -> $spacer * .25
  - `mx-1` -> same spacer but for left/right
  - `my-1` -> same spacer but for top/bottom
  - Each value (-2,-3 etc.) would add 0.25 spacing.
  - `.mx-auto` -> set margin to auto on left/right
  - blank - for classes that set a margin or padding on all 4 sides of the element: `p-0` -> sets padding on all four
- **Navbar**
  - [See](https://getbootstrap.com/docs/4.0/components/navbar/)
  - Navbars require a wrapping .`navbar` with `.navbar-expand{-sm|-md|-lg|-xl}` for **responsive collapsing** and color scheme classes.
  - Colorschemes: `navbar-light`, `navbar-dark`
  - Navbars are responsive by default, but you can easily modify them to change that. Responsive behavior depends on our **Collapse JavaScript plugin**.
  - `fixed-top` -> Makes navbar permanently on the top. (`top/right/left: 0, position: fixed`)
    - **fixed** -> relative to the viewport, hence **even if you scroll** it stays!
  - `.navbar-brand`
    - Used for company name / brand. Bold text, that is usually added to anchors `<a>`.
  - This is followed some *boilerplate* code for the button itself that references a `div` containing an `ul` which is used to show as "list" when clicked.
- `d-flex`
  - =Display(?)
  - To create a flexbox container and transform direct children into flex items, use the d-flex class.
  - Similar is `d-block`
  - Or `d-lg-none` that makes the element disappear on large screen.
- Font awesome and images
  - `<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">` to the `<head>`
  - `<i class="fa fa-car fa-4x"></i>` 
    - `fa` - font awesome
    - `fa-car` - icon
    - `fa-4x` - size - the more the bigger
- `.container` / `.container-fluid`
  - `.container` provides a responsive fixed width container
  - The `.container-fluid` class provides a full width container, spanning the entire width of the viewport.
- `.img-fluid` - the same way the img will fill the container
- `.no-gutters` -> no (Default) padding in the grid system between elements
- "Collapsable" items can be collapesed by the following: `$('.navbar-collapse').collapse('hide');`
- **Scrollable dataspy**
  - Add `data-spy="scroll"` to the element that should be used as the scrollable area (often this is the `<body>` element).
  - Then add the `data-target` attribute with a value of the id or the class name of the navigation bar (`.navbar`). This is to make sure that the navbar is connected with the scrollable area.
  - Result: `<body data-spy="scroll" data-target="#myScrollspy" data-offset="57">` -> If you have an 57px navbar the height is also considered! (Pixels to offset from top when calculating position of scroll.)
  - More info: [Link](https://www.w3schools.com/bootstrap4/bootstrap_scrollspy.asp)
  - Or in jquery:

    ```js
    $('body').scrollspy({
            target: '#mainNav',
            offset: 57
        });
    ```
