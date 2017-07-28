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

- ReactDOM.render
  - Renders a React element into the DOM in the supplied container and returns a reference to the component (or returns null for stateless components).
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
- **All React components must act like pure ("const") functions with respect to their props.**
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

## State and Lifecycle
In the example below we access the class' state and add a date object to it.
Note how we pass props to the base constructor. Class components should always call the base constructor with props.
```js
class Clock extends React.Component {
  constructor(props) {
    super(props);
    this.state = {date: new Date()};
  }

  render() {
    return (
      <div>
        <h1>Hello, world!</h1>
        <h2>It is {this.state.date.toLocaleTimeString()}.</h2>
      </div>
    );
  }
}

ReactDOM.render(
  <Clock />,
  document.getElementById('root')
);
```
**Mounting and Unmounting**
- We want to set up a timer whenever the Clock is rendered to the DOM for the first time. This is called "mounting" in React.
  -  `componentDidMount() {}`
  -  `componentWillUnmount() {}`
- We also want to clear that timer whenever the DOM produced by the Clock is removed. This is called "unmounting" in React.

Adding class fields:
- While this.props is set up by React itself and this.state has a special meaning, you are free to add additional fields to the class manually if you need to store something that is not used for the visual output. If you don't use something in render(), it shouldn't be in the state.

```js
class Clock extends React.Component {
  constructor(props) {
    super(props);
    this.state = {date: new Date()};
  }

  componentDidMount() {
    // setInterval(function(){ alert("Hello"); }, 3000);
    this.timerID = setInterval(
      () => this.tick(),
      1000
    );
  }

  componentWillUnmount() {
    clearInterval(this.timerID);
  }

  tick() {
    this.setState({
      date: new Date()
    });
  }

  render() {
    return (
      <div>
        <h1>Hello, world!</h1>
        <h2>It is {this.state.date.toLocaleTimeString()}.</h2>
      </div>
    );
  }
}

ReactDOM.render(
  <Clock />,
  document.getElementById('root')
);
```
1) When <Clock /> is passed to ReactDOM.render(), React calls the constructor of the Clock component. Since Clock needs to display the current time, it initializes this.state with an object including the current time. We will later update this state.

2) React then calls the Clock component's render() method. This is how React learns what should be displayed on the screen. React then updates the DOM to match the Clock's render output.

3) When the Clock output is inserted in the DOM, React calls the componentDidMount() lifecycle hook. Inside it, the Clock component asks the browser to set up a timer to call tick() once a second.

4) Every second the browser calls the tick() method. Inside it, the Clock component schedules a UI update by calling setState() with an object containing the current time. **Thanks to the setState() call, React knows the state has changed, and calls render() method again** to learn what should be on the screen. This time, this.state.date in the render() method will be different, and so the render output will include the updated time. React updates the DOM accordingly.

5) If the Clock component is ever removed from the DOM, React calls the componentWillUnmount() lifecycle hook so the timer is stopped.

**Important rules on state:**
- Do Not Modify State Directly (`this.state.comment = 'Hello';`) --> Use setState
- State Updates May Be Asynchronous:
  - React may batch multiple setState() calls into a single update for performance.
```js
// Wrong
this.setState({
  counter: this.state.counter + this.props.increment,
});

// Correct
this.setState((prevState, props) => ({
  counter: prevState.counter + props.increment
}));
```

State and stateless:  
- Neither parent nor child components can know if a certain component is stateful or stateless, and they shouldn't care whether it is defined as a function or a class.
- This is why state is often called local or encapsulated. It is not accessible to any component other than the one that owns and sets it.
