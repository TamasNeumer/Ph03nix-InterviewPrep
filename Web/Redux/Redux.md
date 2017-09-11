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
Actions are payloads of information that send data from your application to your store. They are the only source of information for the store. You send them to the store using ``store.dispatch()``.  
Other than type, the structure of an action object is really up to you.
```js
{
  type: ADD_TODO,
  text: 'Build my first Redux app'
}
```

**Action Creators**
Action creators are functions that create actions. It's easy to conflate the terms “action” and “action creator,” so do your best to use the proper term.

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
To actually initiate a dispatch, pass the result to the ``dispatch()`` function.

#### Reducers
Readucers describe HOW the application's state changes in response to an action.
**The reducer is a pure function that takes the previous state and an action, and returns the next state.**
```js
(previousState, action) => newState
```

Things you should never do inside a reducer:
- Mutate its arguments;
- Perform side effects like API calls and routing transitions;
- Call non-pure functions, e.g. Date.now() or Math.random().  
**Given the same arguments, it should calculate the next state and return it. No surprises. No side effects. No API calls. No mutations. Just a calculation.**

**1. Initialize the state of our app**  
Redux will call our reducer with an undefined state for the first time. This is our chance to return the initial state of our app:

```js
import { VisibilityFilters } from './actions'

const initialState = {
  visibilityFilter: VisibilityFilters.SHOW_ALL,
  todos: []
}

// if state === 'undefined' => state = initialState
function todoApp(state = initialState, action) {
  switch (action.type) {
    case SET_VISIBILITY_FILTER:
      return Object.assign({}, state, {
        visibilityFilter: action.filter
      })
    case ADD_TODO:
      return Object.assign({}, state, {
        todos: [
          ...state.todos,
          {
            text: action.text,
            completed: false
          }
        ]
      })
    case TOGGLE_TODO:
      return Object.assign({}, state, {
        todos: state.todos.map((todo, index) => {
          if (index === action.index) {
            return Object.assign({}, todo, {
              completed: !todo.completed
            })
          }
          return todo
        })
      })
    default:
      return state
  }
}
```
- You **must** supply an empty object as the first parameter in order to create a new state. You can also enable the object spread operator proposal to write { ``...state``, ``...newState`` } instead.

**Reducer composition**
Reducer composition is the fundamental pattern of building Redux apps.

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
