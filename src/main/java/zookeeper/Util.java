package zookeeper;

public class Util {

    public static void print(String message) {
        System.out.println(message);
    }

    public static void printErr(Throwable e) {
        System.out.println(e.getLocalizedMessage());
    }

}
