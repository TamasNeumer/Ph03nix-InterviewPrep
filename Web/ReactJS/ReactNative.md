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
- width and height can be used just as in CSS
- ``flex``
  - Use flex in a component's style to have the component expand and shrink dynamically based on available space.
  - Normally you will use** flex: 1**, which tells a component to fill all available space
  - **flexDirection: [row/column]** --> determines the primary axis of its layout
  - **justifyContent: [flex-start, center, flex-end, space-around, space-between]** --> determines the distribution of children along the primary axis
  - **alignItems: [lex-start, center, flex-end, stretch]** --> determines the alignment of children along the secondary axis

#### View tag
- Used to move elements around the screen
- Add styling to the view tag, and warp the rest of the elements into it:
```js
  <View style={viewStyle}>
             <Text style={textStyle}>Albums!</Text>
  </View>
```

#### Text Input
- `onChangeText` prop that takes a function to be called every time the text changed
- ``onSubmitEditing`` prop that takes a function to be called when the text is submitted
```js
<View style={{padding: 10}}>
        <TextInput
          style={{height: 40}}
          placeholder="Type here to translate!"
          onChangeText={(text) => this.setState({text})}
        />
        <Text style={{padding: 10, fontSize: 42}}>
          {this.state.text.split(' ').map((word) => word && '🍕').join(' ')}
        </Text>
</View>
```

#### Buttons and Touchables
- use **TouchableHighlight** anywhere you would use a button or link on web. The view's background will be darkened when the user presses down on the button.
-  using **TouchableNativeFeedback** on Android to display ink surface reaction ripples that respond to the user's touch
- **TouchableOpacity** can be used to provide feedback by reducing the opacity of the button, allowing the background to be seen through while the user is pressing down.
- Use **TouchableWithoutFeedback** if you need to handle a tap gesture but you don't want any feedback to be displayed.

#### Enable scrolling
- import `Scrollview` and use it as a HTML Tag.
- Use **FlatList** to only render elements that are currently showing on the screen, not all the elements at once.
  - The FlatList component requires two props: data and renderItem. data is the source of information for the list. renderItem takes one item from the source and returns a formatted component to render.
```js
return (
      <View style={styles.container}>
        <FlatList
          data={[
            {key: 'Devin'},
            {key: 'Jackson'},
            {key: 'James'},
            {key: 'Joel'},
            {key: 'John'},
            {key: 'Jillian'},
            {key: 'Jimmy'},
            {key: 'Julie'},
          ]}
          renderItem={({item}) => <Text style={styles.item}>{item.key}</Text>}
        />
      </View>
    );
```
- Use a **SectionList** if you want to fancy a bit.
```js
return (
      <View style={styles.container}>
        <SectionList
          sections={[
            {title: 'D', data: ['Devin']},
            {title: 'J', data: ['Jackson', 'James', 'Jillian', 'Jimmy', 'Joel', 'John', 'Julie']},
          ]}
          renderItem={({item}) => <Text style={styles.item}>{item}</Text>}
          renderSectionHeader={({section}) => <Text style={styles.sectionHeader}>{section.title}</Text>}
        />
      </View>
    );
```






#### Linking library
- use it to send data / interfere with other apps (write email, open website etc.)

#### The magical index.js
- Normally you include by `import what from where/file`. However you can also import by `import what from where/foldername` --> in this case the index.js will be picked up from the folder and it will be imported.

#### Firebase
- Update different components and present these to users
- Analytics, file storage
- Authentication
