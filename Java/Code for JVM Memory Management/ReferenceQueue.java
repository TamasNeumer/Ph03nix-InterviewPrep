import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.sql.Ref;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws IOException {
        Person p = new Person();
        final ReferenceQueue<Person> referenceQueue = new ReferenceQueue<Person>();
        PersonCleaner cleaner = new PersonCleaner();
        PersonWeakReference weakReference = new PersonWeakReference(p, cleaner, referenceQueue );

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    PersonWeakReference wr = (PersonWeakReference) referenceQueue.remove();
                    wr.clean();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        p = null;
        System.gc();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Press any key to continue");
        br.readLine();
        executorService.shutdown();

    }
}

final class Person {

}

class PersonCleaner {

    public void clean() {
        System.out.println("Cleaned");
    }
}

class PersonWeakReference extends WeakReference<Person> {

    PersonCleaner cleaner;
    public PersonWeakReference(Person referent, PersonCleaner cleaner, ReferenceQueue<? super Person> q) {
        super(referent, q);
        this.cleaner = cleaner;
    }

    public void clean(){
        cleaner.clean();
    }
}
