public class ClassErrorTest {

    public static void main(String[] args) throws InterruptedException {
        ClassErrorTest sample = new ClassErrorTest();
        sample.getClassWithInitErrors();
    }

    private void getClassWithInitErrors() throws InterruptedException {
        System.out.println("第一次new");
        Thread.sleep(500);
        try {
            //第一次new ClassWithInitErrors类，JVM会加载该类，初始化该类的静态变量或执行静态块
            new ClassWithInitErrors();
        } catch (Throwable t) {
            //因为初始化静态变量失败，所以加载类失败。
            t.printStackTrace();
        }

        Thread.sleep(500);
        System.out.println("-----------------------------------------------------");
        System.out.println("第二次new");
        Thread.sleep(500);
        try {
            //第二次new ClassWithInitErrors类，JVM不会再加载该类，而是抛出NoClassDefFoundError异常
            new ClassWithInitErrors();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        Thread.sleep(500);
        System.out.println("-----------------------------------------------------");
        System.out.println("第三次new");
        Thread.sleep(500);
        try {
            //第三次new ClassWithInitErrors类，JVM不会再加载该类，而是抛出NoClassDefFoundError异常
            new ClassWithInitErrors();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}

class ClassWithInitErrors {
    static int data = 1 / 0;
}