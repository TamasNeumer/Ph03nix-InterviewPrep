# Redux

#### What is redux?
"Redux is a predictable state container for JavaScript apps. It helps you write applications that behave consistently, run in different environments (client, server, and native), and are easy to test. On top of that, it provides a great developer experience, such as live code editing combined with a time traveling debugger."

Redux is probably the best libraries for scaling an application, with he least amount of code complexity.

#### Redux definitions
- **Reducer** A function that returns some data
- **Action** An object that tells the reducer how to change its data.
- **State** Data for our app to use.
- **Store** An object that holds the application data. (Holds Reducer and State)

#### 3 Principles of Redux
**Single source of truth**  
The state of your whole application is stored in an object tree within a single store.

**State is read-only**  
The only way to change the state is to emit an action, an object describing what happened.

**Changes are made with pure functions**
To specify how the state tree is transformed by actions, you write pure reducers.
- Reducers are just pure functions that take the previous state and an action, and return the next state.
- Remember to return new state objects, instead of mutating the previous state

#### Actions
Actions are payloads of information that send data from your application to your store (= database-ish thingy). They are the only source of information for the store. You send them to the store using ``store.dispatch()``.  
Other than type, the structure of an action object is really up to you.

```js
{
  type: ADD_TODO,
  text: 'Build my first Redux app'
}
```

Actions are **plain JavaScript objects**. Actions must **have a type** property that indicates the type of action being performed. Types should typically be **defined as string constants**.   
Once your app is large enough, you may want to move them into a separate module. More precisely: You don't want to use string constants all over the code. Thus you create a new file, in which you define the action types with variables. Now on, you only import these. Advantage: Linter helps you to note typos.

```js
import { ADD_TODO, REMOVE_TODO } from '../actionTypes'
```

**Action Creators**  
**Action creators are functions that create action (objects).** It's easy to conflate the terms “action” and “action creator,” so do your best to use the proper term.

```js
function addTodo(text) {
  return {
    type: ADD_TODO,
    text
  }
}

//...
dispatch(addTodo(text))
```
To actually initiate a dispatch, pass the result to the ``dispatch()`` function. Alternatively, you can create a bound action creator that automatically dispatches:  
`const boundAddTodo = text => dispatch(addTodo(text))`  


#### Reducers
Reducers describe how the state changes given an action. **The reducer is a pure function that takes the previous state and an action, and returns the next state.**
```js
(previousState, action) => newState
```

**NEVER do in reducers**  
- Mutate its arguments
- Perform side effects like API calls and routing transitions
- Call non-pure functions, e.g. Date.now() or Math.random()  

Remember, reducers are pure!

**Define the state**  
First we define how our state looks like. Simple JS object:

```js
{
  visibilityFilter: 'SHOW_ALL',
  todos: [
    {
      text: 'Consider using Redux',
      completed: true,
    },
    {
      text: 'Keep all state in a single tree',
      completed: false
    }
  ]
}
```

**The first call**
Redux will call our reducer with an undefined state for the first time (initializing). This is our chance to return the initial state of our app:

```js
import { VisibilityFilters } from './actions'

const initialState = {
  visibilityFilter: VisibilityFilters.SHOW_ALL,
  todos: []
}

function todos(state = [], action) {
  switch (action.type) {
    case ADD_TODO:
      return [
        ...state,
        {
          text: action.text,
          completed: false
        }
      ]
    case TOGGLE_TODO:
      return state.map((todo, index) => {
        if (index === action.index) {
          return Object.assign({}, todo, {
            completed: !todo.completed
          })
        }
        return todo
      })
    default:
      return state
  }
}

function visibilityFilter(state = SHOW_ALL, action) {
  switch (action.type) {
    case SET_VISIBILITY_FILTER:
      return action.filter
    default:
      return state
  }
}

function todoApp(state = {}, action) {
  return {
    visibilityFilter: visibilityFilter(state.visibilityFilter, action),
    todos: todos(state.todos, action)
  }
}
```
- Note that todos also accepts state—but it's an array! Now todoApp just gives it the slice of the state to manage, and todos knows how to update just that slice.
  - If action type was TOGGLE_TODO, we call `todos` but only pass the todos part of our state. (See state definition above.)
  - If the action was `TOGGLE_TODO`, you go through each todo of the array and check whether the current index is the same as the index to be changed. If so, you create a copy of this todo, modify the completed property and return it back to the array (containing all the todos).
- This is called **reducer composition**, and it's the fundamental pattern of building Redux apps.
- We don't mutate state: we create a copy using the `assign()` operation and put the additional fields in. (visibilityFilter)
- We return the previous state in the default case.

**Combining Reducers**  
Redux provides a utility called ``combineReducers()`` that does the same boilerplate logic that the todoApp above currently does.  
All combineReducers() does is generate a function that calls your reducers **with the slices of state selected according to their keys**, and combining their results into a single object again.  
 With its help, we can rewrite todoApp like this:
```js
import { combineReducers } from 'redux'

const todoApp = combineReducers({
  visibilityFilter,
  todos
})

export default todoApp

// Example:
const reducer = combineReducers({
  a: doSomethingWithA,
  b: processB,
  c: c
})

// Same as
function reducer(state = {}, action) {
  return {
    a: doSomethingWithA(state.a, action),
    b: processB(state.b, action),
    c: c(state.c, action)
  }
}
```

#### Store
The store has the following responsibilities:
- Holds application state;
- Allows access to state via getState();
- Allows state to be updated via dispatch(action);
- Registers listeners via subscribe(listener);
- Handles unregistering of listeners via the function returned by subscribe(listener).

**Creating the store**
```js
import { createStore } from 'redux'
import todoApp from './reducers'
let store = createStore(todoApp)
// Note here todoApp is a combinedReducer --> the dispatch will
// boradcast the message to the 2 reducers!

//OR:
let store = createStore(todoApp, window.STATE_FROM_SERVER)
```
- You may optionally specify the initial state as the second argument to createStore(). This is useful for hydrating the state of the client to match the state of a Redux application running on the server.

**Dispatching actions**
Dispatching via `store.dispatch()`. Example:

```js
// Every time the state changes, log it vis subscribe. (see data flow section)
// Note that subscribe() returns a function for unregistering the listener
let unsubscribe = store.subscribe(() =>
  console.log(store.getState())
)

// Dispatch some actions
store.dispatch(addTodo('Learn about actions'))

// Stop listening to state updates
unsubscribe()
```

#### Data Flow
**Strict unidirectional data flow:**  all data in an application follows the same lifecycle pattern, making the logic of your app more predictable and easier to understand. The steps in the lifecycle are the following:
1. You call `store.dispatch(action).`
2. The Redux store calls the root reducer function you gave it. (Root reducer can be combinedReducer that forwards it to multiple reducers)
3. The Redux store saves the complete state tree returned by the root reducer.
  1. This new tree is now the next state of your app! Every listener registered with store.subscribe(listener) will now be invoked; listeners may call store.getState() to get the current state.


#### Usage with React
- Install via `npm install --save react-redux`


| **Presentational Components**   |  **Container Components**
--|---|--
**Purpose**  |  How things look (markup, styles) |  How things work (data fetching, state updates)
**Aware of Redux**  | No  |  Yes
**To read data**  | Read data from props  |  Subscribe to Redux state
**To change data**  |  Invoke callbacks from props |  Dispatch Redux actions
  **Are written** |  By hand |  Usually generated by React Redux

**Presentational components in our app**
- TodoList is a list showing visible todos.
  - todos: Array is an array of todo items with { id, text, completed } shape.
  - onTodoClick(id: number) is a callback to invoke when a todo is clicked.
- Todo is a single todo item.
  - text: string is the text to show.
  - completed: boolean is whether todo should appear crossed out.
  - onClick() is a callback to invoke when a todo is clicked.
- Link is a link with a callback.
  - onClick() is a callback to invoke when link is clicked.
- Footer is where we let the user change currently visible todos.
- App is the root component that renders everything else.  

They describe the look but don't know where the data comes from, or how to change it. They only render what's given to them.

```js
import React from 'react'
import PropTypes from 'prop-types'

const Todo = ({ onClick, completed, text }) => (
  <li
    onClick={onClick}
    style={{
      textDecoration: completed ? 'line-through' : 'none'
    }}
  >
    {text}
  </li>
)

Todo.propTypes = {
  onClick: PropTypes.func.isRequired,
  completed: PropTypes.bool.isRequired,
  text: PropTypes.string.isRequired
}

export default Todo
```

**Container components in our app**  
For example, the presentational ``TodoList`` component needs a container like ``VisibleTodoList`` that subscribes to the Redux store and knows how to apply the current visibility filter. To change the visibility filter, we will provide a ``FilterLink`` container component that renders a ``Link`` that dispatches an appropriate action on click:
- VisibleTodoList filters the todos according to the current visibility filter and renders a TodoList.
- FilterLink gets the current visibility filter and renders a Link.
  - filter: string is the visibility filter it represents.

Technically, a container component is just a React component that uses store.subscribe() to read a part of the Redux state tree and supply props to a presentational component it renders. You could write a container component by hand, but we suggest instead generating container components with the React Redux library's connect() function, which provides many useful optimizations to prevent unnecessary re-renders.

To use ``connect()``, you need to define a special function called ``mapStateToProps`` that tells how to transform the current Redux store state into the props you want to pass to a presentational component you are wrapping.


In addition to reading the state, container components can dispatch actions. In a similar fashion, you can define a function called ``mapDispatchToProps()`` that receives the ``dispatch()`` method and returns callback props that you want to inject into the presentational component.


Finally, we create the VisibleTodoList by calling connect() and passing these two functions:

```js
// Reducer
const getVisibleTodos = (todos, filter) => {
  switch (filter) {
    case 'SHOW_ALL':
      return todos
    case 'SHOW_COMPLETED':
      return todos.filter(t => t.completed)
    case 'SHOW_ACTIVE':
      return todos.filter(t => !t.completed)
  }
}

// map components to reducers (?)
const mapStateToProps = state => {
  return {
    todos: getVisibleTodos(state.todos, state.visibilityFilter)
  }
}

const mapDispatchToProps = dispatch => {
  return {
    onTodoClick: id => {
      dispatch(toggleTodo(id))
    }
  }
}

const VisibleTodoList = connect(
  mapStateToProps,
  mapDispatchToProps
)(TodoList)

export default VisibleTodoList
```

#### React Thunk
- Action creators must return functions (instead of action)
- This function will be called (automatically) with 'dispatch'


#### [Playground](https://stephengrider.github.io/JSPlaygrounds/)
```js
// Reducer that initializes state.
const reducer = (state = [], action) => {
	if (action.type === 'split_string') {
  	return action.payload.split('');
  } else if (action.type === 'add_character'){
    return [ ...state, action.payload];
  }

  return state;
};

// We have an instance of a store, that contains the reducer and the state,
// that was produced by the reducer.
const store = Redux.createStore(reducer);
store.getState();

// Action = specific directive to update the state with type and payload.:
const action = {
  type: 'split_string',
  payload: 'asdf'
};

// Send action to reducers inside the store.
store.dispatch(action);

// Display state
store.getState();


const action2 = {
	type: 'add_character',
  payload: 'a'
};

store.dispatch(action2);
store.getState();
```

- Why is it good? We can only change the state only one way. Either empty array, or an array with strings (letters) in it.

#### Notes
- Provider is allowed to only have one child node in react-native.
