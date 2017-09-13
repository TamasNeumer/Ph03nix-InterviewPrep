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
