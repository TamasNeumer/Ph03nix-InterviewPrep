# Testing in a Nutshell

## Test Driven Development (TDD)

### What is TDD
Test-driven development (TDD) is a software development process that relies on the repetition of a very short development cycle: Requirements are turned into very specific test cases, then the software is improved to pass the new tests, only. This is opposed to software that allows software to be added that is not proven to meet requirements. [[Wikipedia]](https://en.wikipedia.org/wiki/Test-driven_development)

### The TDD cycle
1. Add a test
2. Run all tests and see if the new test fails
3. Write the code
4. Run tests
5. Refactor code

### Three Rules of TDD
- Write production code only to make a failing test pass.
- Write no more of a unit test than sufficient to fail. Compilation failures
are failures.
- Write only the production code needed to pass the one failing test.

The author of the book "TDD with C++"(Jeff Langr) notes, that in some languages (such as C++), where the build process can take a long time it is not advised to do a minor or tiny change in the unit test and then recompile everything. Thus here you can work with bigger changes.

### Test structure
- Setup: Put the Unit Under Test (UUT) or the overall test system in the state needed to run the test.
- Execution: Trigger/drive the UUT to perform the target behavior and capture all output, such as return values and output parameters. This step is usually very simple.
- Validation: Ensure the results of the test are correct. These results may include explicit outputs captured during execution or state changes in the UUT & UAT.
- Cleanup: Restore the UUT or the overall test system to the pre-test state. This restoration permits another test to execute immediately after this one. [[Wikipedia]](https://en.wikipedia.org/wiki/Test-driven_development)


### Test DRIVEN Development
In TDD tests are used to **drive** the development, i.e.: you think about a problem --> write a test case --> write the solution for production code and re-iterate.
If you write tests after your program has been written, you often tend to write tests that "fits" your already existing code - i.e. you are less likely to think of the edge-cases. This process makes the developer focus on the requirements before writing the code.

### TDD Mind-Sets for Successful Adaptation
- **Incrementalism:** At any given point, you can stop development and know that you have built everything the tests say the system does. Anything not tested is not implemented, and anything tested is implemented correctly and completely.
- **Test Behavior, Not Methods:** A common mistake for TDD newbies is to focus on testing member functions. “We have an add() member function. Let’s write TEST(ARetweetCollection, Add) .” But fully covering add behavior requires coding to a few  different scenarios.
Instead, focus on behaviors or cases that describe behaviors. What happens when you add a tweet that you’ve already added before? What if a client passes in an empty tweet? What if the user is no longer a valid Twitter user?
```
TEST(ARetweetCollection, IgnoresDuplicateTweetAdded)
TEST(ARetweetCollection, UsesOriginalTweetTextWhenEmptyTweetAdded)
TEST(ARetweetCollection, ThrowsExceptionWhenUserNotValidForAddedTweet)
```
- **Using Tests to Describe Behavior:** Think of your tests as examples that describe, or document, the behavior of your system. The full understanding of a well-written test is best gleaned by combining two things: the test name, which summarizes the behavior exhibited given a specific context, and the test statements themselves, which
demonstrate the summarized behavior for a single example.

## Behavior Driven Development (BDD)

### What is BDD?
BDD focus is on capturing the required behavior in User Stories, and from these acceptance tests (ATs) are written. BDD isn’t about fancy ways to validate your results; it’s about **sharing expected behaviors** across **all** members of the team. (Not only tech-devs!)

### Behavior specifications
Behavior-driven development specifies that tests of any unit of software should be specified in terms of the desired behavior of the unit.  
BDD specifies that business analysts and developers should collaborate in this area and should specify behavior in terms of user stories, which are each explicitly written down in a dedicated document. Each user story should, in some way, follow the following structure:

- Title: The story should have a clear, explicit title.
  - Narrative
    - A short, introductory section that specifies
      - who: (which business or project role) is the driver or primary stakeholder of the story (the actor who derives business benefit from the story)
      - what: effect the stakeholder wants the story to have
      - why: business value the stakeholder will derive from this effect

  - Acceptance criteria or scenarios
    - A description of each specific case of the narrative. Such a scenario has the following structure:
      - **Given** It starts by specifying the initial condition that is assumed to be true at the beginning of the scenario. This may consist of a single clause, or several.
      - **When** It then states which event triggers the start of the scenario.
      - **Then** Finally, it states the expected outcome, in one or more clauses.
```text
Story: Returns go to stock

As a store owner
In order to keep track of stock
I want to add items back to stock when they're returned.

Scenario 1: Refunded items should be returned to stock
Given that a customer previously bought a black sweater from me
And I have three black sweaters in stock.
When he returns the black sweater for a refund
Then I should have four black sweaters in stock.

Scenario 2: Replaced items should be returned to stock
Given that a customer previously bought a blue garment from me
And I have two blue garments in stock
And three black garments in stock.
When he returns the blue garment for a replacement in black
Then I should have three blue garments in stock
And three black garments in stock.

```
One of the key things BDD addresses is implementation detail in unit tests. A common problem with poor unit tests is they rely too much on how the tested function is implemented. This means if you update the function, even without changing the inputs and outputs, you must also update the test. This is a problem because it makes doing changes tedious.
```js
suite('Counter', function() {
  test('tick increases count to 1', function() {
    var counter = new Counter();

    counter.tick();

    assert.equal(counter.count, 1);
  });
});
```
 The test is completely dependent on the fact that the counter starts at 0. So in other words, this test is relying on two things:
 - Counter starts at 0
 - Ticking increments by 1  

 The fact the counter starts at 0 is an implementation detail that’s irrelevant to the behavior of the tick() function. Therefore it should not have any bearing on the test. The only reason we wrote the test like this is because we were thinking of the implementation, not of the behavior.

 BDD suggests to test behaviors, so instead of thinking of how the code is implemented, we spend a moment thinking of what the scenario is. Typically you phrase BDD tests in the form of “it should do something”. So when ticking a counter, it should increment count by one.

 ```js
 describe('Counter', function() {
  it('should increase count by 1 after calling tick', function() {
    var counter = new Counter();
    var expectedCount = counter.count + 1;

    counter.tick();

    assert.equal(counter.count, expectedCount);
  });
}); 
 ```
[Article on BDD](https://codeutopia.net/blog/2015/03/01/unit-testing-tdd-and-bdd/)
