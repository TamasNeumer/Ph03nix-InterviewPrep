# React

## What is ReactJS?

ReactJS is an open-source, component based front end library responsible only for the view layer of the application. It is open sourced and maintained by Facebook.  
React "reacts" to state changes in your components quickly and automatically to rerender the components in the HTML DOM by utilizing the virtual DOM. The virtual DOM is an in-memory representation of an actual DOM. By doing most of the processing inside the virtual DOM rather than directly in the browser's DOM, React can act quickly and only add, update, and remove components which have changed since the last render cycle occurred.  
A React application is made up of multiple components, each responsible for outputting a small, reusable piece of HTML.

React allows us to write components using a domain-specific language called JSX.

## Installing React
- `npm install --save react react-dom`
```js
var React = require('react');
var ReactDOM = require('react-dom');
ReactDOM.render(<App />, ...);
```

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

  // React element
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
- **Babel** compiles JSX down to React.createElement() calls. Thus the 2 are the same:

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

## Functional Components and Props
- Components let you split the UI into independent, reusable pieces, and think about each piece in isolation.
- Conceptually, functional components are like JavaScript functions. They accept arbitrary inputs (called "props") and return React elements describing what should appear on the screen.

```js
function WelcomeFunctionalComponent(props) {
  return <h1>Hello, {props.name}</h1>;
}

const element = <WelcomeComponent name="Sara" />;

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

Note that **Functional Components** are **stateless**. Stateless functional components are useful for dumb / presentational components. Presentational components focus on the UI rather than behavior, so it’s important to avoid using state in presentational components. Instead, state should be managed by higher-level “container” components, or via Flux/Redux/etc.

You only need a class component when you   
1. need component state or
2. need the lifecycle methods such as componentDidMount etc.

#### Rendering children via props
- To get the children of a tag (and list them / style them etc. in this case Card's children) we can use the `props.children` expression.
  ```js
  //...
  <Card>
      <Text>{props.album.title}</Text>
  </Card>
  //...

  return (
      <View style={styles.containerStyle}>
          {props.children}
      </View>
  );
  ```

## State and Lifecycle --> Class Component
### Converting a function component to class component:  

1. Create an ES6 class with the same name that extends React.Component.
2. Add a single empty method to it called render().
3. Move the body of the function into the render() method.
4. Replace props with **this.props** in the render() body.
5. Delete the remaining empty function declaration.

### Adding local state to a class:
1. Replace this.props with this.state in the render() method.
2. Add a class constructor that assigns the initial this.state. (Create vars, as shown here or assign initial state to props.)

In the example below we access the class' state and add a date object to it. Note how we pass props to the base constructor. Class components should **always call the base constructor with props**.

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

module.exports = Clock;

ReactDOM.render(
  <Clock />,
  document.getElementById('root')
);
```
- In order to use a component in another file you have to call module.exports.

**getDefaultProps() and getInitialState() -> Don't use them!**  
If the component is defined using ES6 class syntax, the functions getDefaultProps() and getInitialState() cannot be used.  
Instead, we declare our defaultProps as a static property on the class, and declare the state shape and initial state in the constructor of our class. These are both set on the instance of the class at construction time, before any other React lifecycle function is called.

Replacing `getDefaultProps()`
```js
// ES6
class MyClass extends React.Component {...}
MyClass.defaultProps = {
    randomObject: {},
    ...
}

// ES7
class MyClass extends React.Component {  
    static defaultProps = {
        randomObject: {},
        ...
    };
}
```

Replacing `getInitialState()`
```js
constructor(props){
  super(props);

  this.state = {
    count: this.props.initialCount
  };
}
```
- Why call ``super(props)``? --> "However, no reason is provided. We can speculate it is either because of subclassing or for future compatibility.""

**Forcing proptypes**  
```js
//ES6
MyClass.propTypes = {
    randomObject: React.PropTypes.object,
    callback: React.PropTypes.func.isRequired,
    ...
};

//ES7
class MyClass extends React.Component {  
     static propTypes = {
        randomObject: React.PropTypes.object,
        callback: React.PropTypes.func.isRequired,
        ...
    };
}
```

### Adding lifecycle to a class
**Mounting**  
These methods are called when an instance of a component is being created and inserted into the DOM:
- `constructor()`
  - Call `super(props)`, initialize state, bind methods
- `componentWillMount()`
    - Invoked immediately before mounting occurs.
    - This is the only lifecycle hook called on server rendering. Generally, **we recommend using the constructor() instead**.
- `render()`
  - When called, it should examine this.props and this.state and return a single React element. This element can be either a representation of a native DOM component, such as <div />, or another composite component that you've defined yourself.
  - render() will not be invoked if shouldComponentUpdate() returns false.
-  `componentDidMount() {}`
    - Runs after the component output has been rendered to the DOM.
    - Initialization that requires DOM nodes should go here.
    - Preparing timers, Fetching data, Adding event listeners, Manipulating DOM elements.
    - Setting state will cause re-rendering!  

**Updating**
- `componentWillReceiveProps(nextProps)`
  - This is the first function called on properties changes. Invoked before a mounted component receives new props
  - When component's properties change, React will call this function with the new properties. You can access to the old props with this.props and to the new props with nextProps.
- `shouldComponentUpdate(nextProps, nextState)`
  - This is the second function called on properties changes and the first on state changes.
  - By default, if another component / your component change a property / a state of your component, React will render a new version of your component. In this case, this function always return true. You can override this function and choose more precisely if your component must update or not. --> (Mostly used for optimization.)
  - Use shouldComponentUpdate() to let React know if a component's output is not affected by the current change in state or props. The default behavior is to re-render on every state change, and in the vast majority of cases you should rely on the default behavior.
- `componentDidUpdate()`
  - Invoked immediately after updating occurs. This method is not called for the initial render.
  - Use this as an opportunity to operate on the DOM when the component has been updated.
- `forceUpdate()`
  - By default, when your component's state or props change, your component will re-render. If your render() method depends on some other data, you can tell React that the component needs re-rendering by calling forceUpdate().
  - Normally you should **try to avoid all uses of forceUpdate()** and only read from this.props and this.state in render().
**Unmounting**
-  `componentWillUnmount() {}`
    - This method is called before a component is unmounted from the DOM.
    - Removing event listeners, Clearing timers, Stopping sockets, Cleaning up redux states.

```js
componentDidMount() {
   document.addEventListener("click", this.closeMenu);
 }

componentWillUnmount() {
    document.removeEventListener("click", this.closeMenu);
  }

componentWillReceiveProps(nextProps){
  if (nextProps.initialCount && nextProps.initialCount > this.state.count){
    this.setState({
      count : nextProps.initialCount
    });
  }
}

componentShouldUpdate(nextProps, nextState){
  return this.props.name !== nextProps.name ||
    this.state.count !== nextState.count;
}

```

**Adding class fields**
- While this.props is set up by React itself and this.state has a special meaning, you are free to add additional fields to the class manually if you need to store something that is not used for the visual output. **If you don't use something in render(), it shouldn't be in the state**.
- Don't use the React.createClass, as it is going to be obsolete soon!
  ```js
  class Clock extends React.Component {
    constructor(props) {
      super(props);
      this.state = {date: new Date()};
    } // Look maa, no comma required in JSX based class defs.

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

  module.exports = Clock;

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
- Do Not Modify State Directly (`this.state.comment = 'Hello';`) --> Use `setState()`
- State Updates May Be Asynchronous:
  - Because this.props and this.state may be updated asynchronously, you should not rely on their values for calculating the next state.
  - React may batch multiple setState() calls into a single update for performance.
-  Neither parent nor child components can know if a certain component is stateful or stateless, and they shouldn't care whether it is defined as a function or a class.
- This is why state is often called local or encapsulated. It is not accessible to any component other than the one that owns and sets it.

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

**The data flows down**  
- If you imagine a component tree as a waterfall of props, each component's state is like an additional water source that joins it at an arbitrary point but also flows down.
```js
<FormattedDate date={this.state.date} />
```

### Nesting components
#### Nesting using props
- This is the style where A composes B and B provides an option for A to pass something to compose for a specific purpose. More structured composition.
```js
var CommentBox = reactCreateClass({
  render: function() {
    return (
      <div className="commentBox">
        <h1>Comments</h1>
        <CommentList title={ListTitle}/> //prop
        <CommentForm />
      </div>
    );
  }
});
```

#### Nesting using children
- This is the style where A composes B and A tells B to compose C. More power to parent components.
- Good if
  - B should accept to compose something different than C in the future or somewhere else
  - A should control the lifecycle of C
```js
var CommentBox = reactCreateClass({
  render: function() {
    return (
      <div className="commentBox">
        <h1>Comments</h1>
        <CommentList>
            <ListTitle/> // child
        </CommentList>
        <CommentForm />
      </div>
    );
  }
});
```
#### Nesting without children
```js
- This is the style where A composes B and B composes C.
var CommentList = reactCreateClass({
  render: function() {
    return (
      <div className="commentList">
        <ListTitle/>
        Hello, world! I am a CommentList.
      </div>
    );
  }
});
```

## The THIS keyword
- With ES6 classes this is null by default, **properties of the class do not automatically bind to the React class (component) instance.** (= ES6 React.Component doesn't auto bind methods to itself.))
```js
var React = require('react');
var ReactDOM = require('react-dom');


class MyComponent extends React.Component {
    constructor(props) {
        super(props);
    }
    handleClick() {
        console.log(this); // null!!!
    }
    render() {
        return (
            <div onClick={this.handleClick}>Test</div>
        );
    }
}

ReactDOM.render(
  <MyComponent/>,
    document.getElementById('app'));
```
There are a few ways we could bind the right this context.
#### Inline binding:
```js
<div onClick={this.handleClick.bind(this)}></div>
```
#### Class constructor binding
(Avoiding inline repetitions) Considered by many as a better approach that avoids touching JSX at all.
```js
constructor(props) {
  super(props);
  this.handleClick = this.handleClick.bind(this);
}
```
#### ES6 Anyonymous function calls (no binding required)
```js
handleClick = () => {
    console.log(this); // the React Component instance
  }
```
