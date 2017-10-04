import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        ReferenceQueue<Person> queue = new ReferenceQueue<Person>();
        ArrayList<FinalizePerson> list = new ArrayList<FinalizePerson>();
        ArrayList<Person> people = new ArrayList<Person>();

        for (int i = 0; i< 10; i++){
            Person p = new Person();
            people.add(p);
            list.add(new FinalizePerson(p, queue));
        }

        people = null;
        System.gc();

        for (PhantomReference<Person> reference : list) {
            System.out.println(reference.isEnqueued());
        }

        Reference<? extends Person> referenceFromQueue;
        while ((referenceFromQueue = queue.poll()) != null) {
            ((FinalizePerson) referenceFromQueue).cleanup();
        }

    }
}

class FinalizePerson extends PhantomReference<Person>{

    public FinalizePerson(Person referent, ReferenceQueue<? super Person> q) {
        super(referent, q);
    }

    public void cleanup() {
        System.out.println("person is finalizing resources");
    }

}

class Person {
}
