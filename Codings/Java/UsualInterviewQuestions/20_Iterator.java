/*
An Iterator has the following methods:
public interface Iterator<E> {
  boolean hasNext();
  E next();
  void remove();
}
*/

public static class ListIterator<T> implements Iterator<T> {
  private final Iterator<Iterator<T>> listIterator;
  private Iterator<T> currentIterator;

  public ListIterator(List<Iterator<T>> iterators) {
    this.listIterator = iterators.iterator();
    this.currentIterator = listIterator.next();
  }

  @Override
  public boolean hasNext() {
    if(!currentIterator.hasNext()) { // If current iterator has next -> true
      if (!listIterator.hasNext()) { // If we exhausted all of our iterators
        return false;
      }
      currentIterator = listIterator.next(); // Set currentIter and check next
      hasNext();
    }
    return true;
  }

  /*It is possible for the current Iterator to be exhausted, and therefore calling next would return
  a NoSuchElementException , but one of the following Iterator s does have elements. Before
  ­calling next , the currentIterator reference needs to be updated. The code to update the
  currentIterator reference to the correct reference was written in the hasNext method, so you
  can simply call hasNext , and ignore the result.*/

  @Override
  public T next() {
    hasNext();
    return currentIterator.next();
  }

  @Override
  public void remove() {
    hasNext();
    currentIterator.remove();
  }
... // see below for the remaining implementation
