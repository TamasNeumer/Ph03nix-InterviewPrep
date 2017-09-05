# ReactNative

#### Registering the application
In order to render and show the components we have to register these first.
  ```js
  ReactNative.AppRegistry.registerComponent('albums',() => App);
  ```
- You create the application name `albums` and we return the App component.
- Make sure you have `AppRegistry` imported from `react-native`
- Only the root component should use the `AppRegistry`. (Most top-level, most-parent component.)


#### Exporting components for consumption
  ```js
  // Function component
  const Header = () => {
    return <Text>Albums!</Text>;
  };
  export default Header;

  // Class component
  export default class App extends Component {
    render() {
      return (
          <Text>
            Welcome to React Native!
          </Text>
      );
    }
  }
  ```
- This way the exported components will be available in other files.
  - Import the component --> `import Header from './src/components/header';`
  - **Note:** No need for extension.

#### Styling in React Native
- Styles object containing CSS-like styles.
- Here we use camel case instead of dash. (font-size --> fontSize)

  ```js
  // Make a component
  const Header = () => {
      const { textStyle } = styles;
      return <Text style={textStyle}>Albums!</Text>;
  };

  // Styling
  const styles = {
      textStyle: {
          fontSize: 20
      }
  };

  // Or using the boilerplate
  <Text style={styles.welcome}>Welcome to React Native!</Text>

  const styles = StyleSheet.create({
    welcome: {
      fontSize: 20,
      textAlign: 'center',
      margin: 10,
    }
  });
  ```

#### View tag
- Used to move elements around the screen
- Add styling to the view tag, and warp the rest of the elements into it:
```js
  <View style={viewStyle}>
             <Text style={textStyle}>Albums!</Text>
  </View>
```
- Useful styling properties:
  - `justifyContent` (vertically)
  - `alignItems` (horizontally)
  - `paddingTop`, `shadowColor / shadowOffset / shadowOpacity` etc.
  - `flex-direction` --> (column / row)

#### Enable scrolling
- import `Scrollview`

#### Linking library
- use it to send data / interfere with other apps (write email, open website etc.)

#### The magical index.js
- Normally you include by `import what from where/file`. However you can also import by `import what from where/foldername` --> in this case the index.js will be picked up from the folder and it will be imported.
