import java.lang.reflect.Field;
import sun.misc.Unsafe;

public class Main {
    private static Unsafe unsafe;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe)field.get(null);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static long addressof(Object o) throws Exception {
        Object[] array = new Object[]{o};

        long baseOffset = unsafe.arrayBaseOffset(Object[].class);
        int addressSize = unsafe.addressSize();
        long objectAddress;
        switch (addressSize) {
            case 4:
                objectAddress = unsafe.getInt(array, baseOffset);
                break;
            case 8:
                objectAddress = unsafe.getLong(array, baseOffset);
                break;
            default:
                throw new Error("unsupported address size: " + addressSize);
        }

        return (objectAddress);
    }


    public static void main(String... args) throws Exception {
        for (int i = 0; i < 32000; i++) {
            Object mine = new GCMe();
            long address = addressof(mine);
            System.out.println(address);
        }
    }

    public static void changeString(int in) {
        in = 6;
        System.out.println(in);
    }
}

class GCMe {
    long data;
    long a1;
    long a2;
    long a3;
    long a4;
    long a5;
    long a6;
    long a7;
    long a8;
    long a9;
    long a10;
    long a11;
    long a12;
    long a13;
    long a14;
    long a15;
    long a16;
    long a17;
    long a18;
    long a19;
    long a20;
    long a21;
    long a22;
    long a23;
    long a24;
    long a25;
    long a26;
    long a27;
    long a28;
    long a29;
    long a30;
    long a31;
    long a32;
    long a33;
    long a34;
    long a35;
    long a36;
    long a37;
    long a38;
    long a40;
    long a41;
}
