# React

## JSX Intro
- A syntax extension to JavaScript.
- JSX produces React "elements" that we will render into to the DOM.
- `const element = <h1>Hello, world!</h1>;` is a sample JSX expression.
- You can embed JS code into into your JSX, by putting it into curly braces {}
```js
function formatName(user) {
  return user.firstName + ' ' + user.lastName;
}

const user = {
  firstName: 'Harper',
  lastName: 'Perez'
};

const element = (
  <h1>
    Hello, {formatName(user)}!
  </h1>
);
```
- After compilation, JSX expressions become regular JavaScript objects. --> you can use JSX inside of `if` statements and `for` loops.
```js
function getGreeting(user) {
  if (user) {
    return <h1>Hello, {formatName(user)}!</h1>;
  }
  return <h1>Hello, Stranger.</h1>;
}
```
- Another example of inserting `JS` code into JSX:
  - `const element = <img src={user.avatarUrl}></img>;`
  - Don't put quotes around curly braces when embedding a JavaScript expression in an attribute. Otherwise JSX will treat the attribute as a string literal rather than an expression.
- JSX Prevents Injection Attacks --> It is safe to embed user input in JSX
  - `const title = response.potentiallyMaliciousInput; const element = <h1>{title}</h1>;`
  - Everything is converted to a string before being rendered. This helps prevent XSS (cross-site-scripting) attacks.
- Babel compiles JSX down to React.createElement() calls. Thus the 2 are the same:
```js
const element = (
  <h1 className="greeting">
    Hello, world!
  </h1>
);

const element = React.createElement(
  'h1',
  {className: 'greeting'},
  'Hello, world!'
);
```
- To see how it is converted go to [https://babeljs.io/repl/](Babel)

## Rendering elements
- Unlike browser DOM elements, React elements are plain objects, and are cheap to create. React DOM takes care of updating the DOM to match the React elements.
- `<div id="root"></div>` --> We call this a "root" DOM node because everything inside it will be managed by React DOM. Applications built with just React usually have a single root DOM node. To render an element into this node you will use the following:
```js
const element = <h1>Hello, world</h1>;
ReactDOM.render(
  element,
  document.getElementById('root')
);
```
- React elements are **immutable**. Once you create an element, you can't change its children or attributes.

## ReactDOM.render
- Render a React element into the DOM in the supplied container and return a reference to the component (or returns null for stateless components).
- If the React element was previously rendered into container, this will perform an update on it and only mutate the DOM as necessary to reflect the latest React element.

## Components and Props
- Components let you split the UI into independent, reusable pieces, and think about each piece in isolation.
- Conceptually, components are like JavaScript functions. They accept arbitrary inputs (called "props") and return React elements describing what should appear on the screen.

```js
function Welcome(props) {
  return <h1>Hello, {props.name}</h1>;
}

const element = <Welcome name="Sara" />;
ReactDOM.render(
  element,
  document.getElementById('root')
);
```
Let's recap what happens in this example:

1. We call ReactDOM.render() with the <Welcome name="Sara" /> element.
2. React calls the Welcome component with {name: 'Sara'} as the props.
3. Our Welcome component returns a `<h1>Hello, Sara</h1>` element as the result.
4. React DOM efficiently updates the DOM to match `<h1>Hello, Sara</h1>`.

Some important rules on Components and Props:
- **Always** start component names with a capital letter.
- Components **must** return a **single** root element. (A div e.g.)
- **All React components must act like pure functions with respect to their props.**
```js
function Welcome(props) {
  return <h1>Hello, {props.name}</h1>;
}

function App() {
  return (
    <div>
      <Welcome name="Sara" />
      <Welcome name="Cahal" />
      <Welcome name="Edite" />
    </div>
  );
}

ReactDOM.render(
  <App />,
  document.getElementById('root')
);
```
- Extract as many small components as you can to maximize the re-usability!
- In case some props are not defined you can create "default props" by adding this function to the top of your component:
```js
//...
getDefaultProps: function() {
  return {
    name: 'React',
    message: 'Default message'
  };
},
//...
```
