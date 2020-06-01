package whiteboard;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class WhiteboardApplication {


    public static void main(String[] args) {

        // Switch function to select between (Both, Teacher and Student) Application to start
        switch (startingMode()) {
            case 0:
                bothApplications();
                break;
            case 1:
                serverApplication();
                break;
            case 2:
                clientApplication();
                break;
            default:
                serverApplication();
                break;
        }
    }

    // The Dialog function
    public static int startingMode() {
        String[] options = {"Both", "Teacher", "Student"};
        int x = JOptionPane.showOptionDialog(null, "Run The Program in Teacher or Sudent Mode?",
                "Select Mode",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        System.out.println(options[x]);
        return x;
    }

    // Start the Teacher Application
    public static void serverApplication() {
        TeacherSide startTeacher = new TeacherSide();
        startTeacher.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startTeacher.setSize(600, 450);
        startTeacher.setVisible(true);
        startTeacher.startTheServer();
    }

    // Start the Student Application
    public static void clientApplication() {
        StudentSide startStudent = new StudentSide("127.0.0.1");
        startStudent.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startStudent.setSize(600, 450);
        startStudent.setVisible(true);
        startStudent.startTheClient();
    }

    // Start both Applications at the same time by using a Thread for each Application
    public static void bothApplications() {
        Thread threadTeacher = new Thread() {
            @Override
            public void run() {
                serverApplication();
            }
        };
        threadTeacher.start();

        Thread threadStudent = new Thread() {
            @Override
            public void run() {
                clientApplication();
            }
        };
        threadStudent.start();
    }

}
