package JDKAop;

import org.springframework.beans.factory.annotation.Autowired;

public class Test1 {
    @Autowired
    private static StudentService studentService;
    public static void main(String[] args) {
        String save=System.getProperty("sun.misc.ProxyGenerator.saveGeneratedFiles");
        System.out.println(save);
        System.setProperty("sun.misc.ProxyGenerator.saveGeneratedFiles","true");

        studentService.insertStudent();
    }
}
