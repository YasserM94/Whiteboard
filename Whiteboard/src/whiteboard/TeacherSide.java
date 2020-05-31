package whiteboard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileSystemView;

public class TeacherSide extends JFrame implements ActionListener {

    // <editor-fold defaultstate="collapsed" desc="Declared Variables">
    ArrayList<Shape> drawnShapesList = new ArrayList<Shape>();

    BufferedImage bufferedImageHandImage;

    JPanel jPanelSelectedShape, jPanelSelectedColor;
    JLabel jLabelSelectedShape, jLabelSelectedColor;

    JPanel jPanelBoard, drawingPanel,
            jPanelConnectionStatus, jPanelSessionTimer, jPanelShapesCount, jPanelRaiseHand;
    JLabel jLabelConnectionStatus, jLabelSessionTimer, jLabelShapesCount, jLabelRaiseHand;
    JTextArea jTextAreaChat;
    JScrollPane jScrollPaneScrollableChat;
    JTextField jTextFieldMessageBox;
    JButton JButtonSend;

    JMenuBar jMenuBarMenuBar;
    JMenu jMenuTimer, jMenuAttendance, jMenuShapes, jMenuColor, jMenuClear, jMenuExit;
    JMenuItem jMenuItemSetTimer, jMenuIteamResetTimer, jMenuIteamTimerAnim, jMenuIteamAttendeeList, jMenuIteamExportList,
            jMenuItemShapeSquare, jMenuItemShapeOval, jMenuItemShapeLine, jMenuItemColorSelect, jMenuItemColorRandom,
            jMenuItemClearBoard, jMenuItemClearChat, jMenuItemExitSelect;

    String studentName = null;
    boolean teacherMode = false, isHandRaised = false, iAmTyping = false, isConnected = false;
    int selectedShape, xPoint, yPoint;
    int x, y, dx, dy;

    Color defaultColor = UIManager.getColor("Panel.background"); // Default Button Color
    Color btnColor = new Color(255, 255, 204);
    Color selectedColor = new Color(204, 255, 204);
    String selectedDrawShape = "Square";

    Timer timerServer;
    Timer timerAnimation;
    String timeFormatted;
    int timeMin, timeSec;
    boolean isTimerRunning = false, isTimeOver = false;

    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private ServerSocket serverSocket;
    private Socket connectionSocket;

    // </editor-fold>
    public TeacherSide() {
        super("Teacher Application");
//        getHandImage();
        initializeGUI();
        setJMenuBarAndMenuBarItems();
        setActionListener();
        setMenuBarEnabled(false);
    }

    // Initialize all the Server Components in the GUI
    public void initializeGUI() {
        // Initialize The Program JPanel
        jPanelBoard = new JPanel();
        setResizable(false); // Disable Resizing the Application Window
        add(jPanelBoard);
        jPanelBoard.setLayout(new BorderLayout());
        jPanelBoard.setLayout(null);

        // Initialize The "Drawing" JPanel
        drawingPanel = new JPanel();
        drawingPanel.setBackground(Color.white);
        drawingPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        // Initialize The "Raise Hand" JPanel and JLabel
        jPanelRaiseHand = new JPanel();
        jPanelRaiseHand.setBorder(BorderFactory.createLineBorder(Color.black));
        jLabelRaiseHand = new JLabel("<html>&nbsp;&nbsp;Hand<br/>Raised</html>");
        jLabelRaiseHand.setForeground(Color.gray);
        jLabelRaiseHand.setBorder(BorderFactory.createEmptyBorder(7, 0, 0, 0));
        jPanelRaiseHand.add(jLabelRaiseHand, BorderLayout.CENTER);

        // Initialize The "ConnectionStatus" JPanel and JLabel
        jPanelConnectionStatus = new JPanel();
        jPanelConnectionStatus.setBorder(BorderFactory.createLineBorder(Color.black));
        jLabelConnectionStatus = new JLabel("");
        jLabelConnectionStatus.setForeground(Color.black);
        jPanelConnectionStatus.add(jLabelConnectionStatus);

        // Initialize The "Session Timer" JPanel and JLabel
        jPanelSessionTimer = new JPanel();
        jPanelSessionTimer.setBorder(BorderFactory.createLineBorder(Color.black));
        jLabelSessionTimer = new JLabel("<html><font color='black'>Time Left: </font>00:00</html>");
        jLabelSessionTimer.setForeground(Color.gray);
        jPanelSessionTimer.add(jLabelSessionTimer);

        // Initialize The "Shapes Count" JPanel and JLabel
        jPanelShapesCount = new JPanel();
        jPanelShapesCount.setBorder(BorderFactory.createLineBorder(Color.black));
        jLabelShapesCount = new JLabel("<html><font color='black'>Shapes Count: </font>0</html>");
        jLabelShapesCount.setForeground(Color.gray);
        jPanelShapesCount.add(jLabelShapesCount);

        // Initialize The "Shapes Count" JPanel and JLabel
        jPanelSelectedShape = new JPanel();
        jPanelSelectedShape.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, Color.BLACK));
        jLabelSelectedShape = new JLabel("<html><font color='black'>Selected Shape: </font></html>");
        jPanelSelectedShape.add(jLabelSelectedShape);

        jPanelSelectedColor = new JPanel();
        jPanelSelectedColor.setBackground(selectedColor);
        jPanelSelectedColor.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, Color.BLACK));
        jLabelSelectedColor = new JLabel("Square");
        jPanelSelectedColor.add(jLabelSelectedColor);

        // Initialize The "Scrollable Chat Area" jTextArea and jScrollPane
        jTextAreaChat = new JTextArea();
        jTextAreaChat.setEditable(false);
        jTextAreaChat.setLineWrap(true);
        jTextAreaChat.setMargin(new Insets(2, 2, 2, 2));
        jScrollPaneScrollableChat = new JScrollPane(jTextAreaChat);
        jScrollPaneScrollableChat.setBorder(BorderFactory.createLineBorder(Color.black));

        // Initialize The "Message Box" jTextField
        jTextFieldMessageBox = new JTextField();
        jTextFieldMessageBox.setBorder(BorderFactory.createLineBorder(Color.black));

        // Initialize The "Send Button" JButton
        JButtonSend = new JButton("Send");
        JButtonSend.setEnabled(false);
        JButtonSend.setBorder(BorderFactory.createLineBorder(Color.black));

        // Set The "GUI" Bounds for Position and Sizes
        jPanelConnectionStatus.setBounds(1, 1, 176, 30);
        jPanelRaiseHand.setBounds(123, 32, 54, 60);
        jPanelSessionTimer.setBounds(1, 32, 121, 30);
        jPanelShapesCount.setBounds(1, 63, 121, 29);
        jPanelSelectedShape.setBounds(1, 93, 112, 29);
        jPanelSelectedColor.setBounds(113, 93, 64, 29);
        jScrollPaneScrollableChat.setBounds(1, 123, 176, 202);
        jTextFieldMessageBox.setBounds(1, 326, 176, 30);
        JButtonSend.setBounds(1, 357, 176, 30);
        drawingPanel.setBounds(180, 1, 403, 386);

        // Add all the Components to the jPanel Layout
        jPanelBoard.add(jPanelConnectionStatus);
        jPanelBoard.add(jPanelRaiseHand);
        jPanelBoard.add(jPanelSessionTimer);
        jPanelBoard.add(jPanelShapesCount);
        jPanelBoard.add(jPanelSelectedShape);
        jPanelBoard.add(jPanelSelectedColor);
        jPanelBoard.add(jScrollPaneScrollableChat);
        jPanelBoard.add(jTextFieldMessageBox);
        jPanelBoard.add(JButtonSend);
        jPanelBoard.add(drawingPanel);

        jPanelBoard.setVisible(true);

    }

    // <editor-fold defaultstate="collapsed" desc="Connection Management Functions">
    //
    // Start the Server to accept the connection from the Client
    public void startTheServer() {
        try {
            serverSocket = new ServerSocket(22602, 100);
            while (true) {
                try {
                    waitingForConnection();
                    initializeStreams();
                    processTheConnection();
                } catch (EOFException e) {
                    updateStatus("<html><font color='red'>Server Terminated The Connection</font></html>");
                } finally {
                    closeTheConnection();
                }
            }
        } catch (IOException e) {
            updateStatus("<html><font color='red'>Error While Starting The Server</font></html>");
        }
    }

    // Waiting for a Client to connect
    private void waitingForConnection() throws IOException {
        updateStatus("<html><font color='red'>Waiting For Connection..</font></html>");
        connectionSocket = serverSocket.accept();
        updateStatus("<html><font color='red'>Connection Received</font></html>");
    }

    // Initialize the Streams with the client
    private void initializeStreams() throws IOException {
        objectOutputStream = new ObjectOutputStream(connectionSocket.getOutputStream());
        objectOutputStream.flush();
        objectInputStream = new ObjectInputStream(connectionSocket.getInputStream());
        isConnected = true;
        updateStatus("<html><font color='red'>Connected via Stream</font></html>");
    }

    // Process the Connection with the Client
    private void processTheConnection() throws IOException {
        setMenuBarEnabled(true);
        updateStatus("<html>Student is <font color='green'>Connected</font></html>");
        do {
            try {
                ComplexObject receivedObject = (ComplexObject) objectInputStream.readObject();
                receivedObjViaStreamSwitch(receivedObject);
            } catch (ClassNotFoundException e) {
                updateStatus("<html><font color='red'>Error While Process The Connection</font></html>");
            }
        } while (isConnected);
    }

    // Closs the Connection with the Client
    private void closeTheConnection() {
        updateStatus("<html><font color='red'>Terminating Connection..</font></html>");
        enableSendButton(false);
        try {
            objectOutputStream.close();
            objectInputStream.close();
            connectionSocket.close();
        } catch (IOException e) {
            updateStatus("<html><font color='red'>Error While Closing The Connection</font></html>");
        }
    }

    // Stream the Object to the Client
    private void streamObject(ComplexObject sentComplexObj) {
        try {
            objectOutputStream.writeObject(sentComplexObj);
            objectOutputStream.flush();
        } catch (IOException e) {
            updateStatus("<html><font color='red'>Error While Streaming the Object</font></html>");
        }
    }

    // </editor-fold>
    //
    // Initializes ALL the Menu Bar Items with it's options
    public void setJMenuBarAndMenuBarItems() {
        jMenuBarMenuBar = new JMenuBar();

        jMenuTimer = new JMenu("Timer");
        jMenuItemSetTimer = new JMenuItem("Set Timer");
        jMenuIteamResetTimer = new JMenuItem("Reset Timer");
        jMenuIteamResetTimer.setEnabled(false);
        jMenuIteamTimerAnim = new JMenuItem("Test Animation");
        jMenuTimer.add(jMenuItemSetTimer);
        jMenuTimer.add(jMenuIteamResetTimer);
        jMenuTimer.add(jMenuIteamTimerAnim);
        jMenuBarMenuBar.add(jMenuTimer);

        jMenuAttendance = new JMenu("Attendance");
        jMenuIteamAttendeeList = new JMenuItem("Attendee List");
        jMenuIteamExportList = new JMenuItem("Export List");
        jMenuIteamExportList.setEnabled(false);
        jMenuAttendance.add(jMenuIteamAttendeeList);
        jMenuAttendance.add(jMenuIteamExportList);
        jMenuBarMenuBar.add(jMenuAttendance);

        jMenuShapes = new JMenu("Shapes");
        jMenuItemShapeSquare = new JMenuItem("Square");
        jMenuItemShapeOval = new JMenuItem("Oval");
        jMenuItemShapeLine = new JMenuItem("Line");
        jMenuShapes.add(jMenuItemShapeSquare);
        jMenuShapes.add(jMenuItemShapeOval);
        jMenuShapes.add(jMenuItemShapeLine);
        jMenuBarMenuBar.add(jMenuShapes);

        jMenuColor = new JMenu("Color");
        jMenuItemColorSelect = new JMenuItem("Select Color");
        jMenuItemColorRandom = new JMenuItem("Random Color");
        jMenuColor.add(jMenuItemColorSelect);
        jMenuColor.add(jMenuItemColorRandom);
        jMenuBarMenuBar.add(jMenuColor);

        jMenuClear = new JMenu("Clear");
        jMenuItemClearBoard = new JMenuItem("Clear The Board");
        jMenuItemClearChat = new JMenuItem("Clear The Chat");
        jMenuClear.add(jMenuItemClearBoard);
        jMenuClear.add(jMenuItemClearChat);
        jMenuBarMenuBar.add(jMenuClear);

        jMenuExit = new JMenu("Exit");
        jMenuItemExitSelect = new JMenuItem("Exit Application");
        jMenuExit.add(jMenuItemExitSelect);
        jMenuBarMenuBar.add(jMenuExit);

        setJMenuBar(jMenuBarMenuBar);
    }

    // Add ActionListener to the Components which requires clicking action
    public void setActionListener() {
        jMenuItemSetTimer.addActionListener(this);
        jMenuIteamResetTimer.addActionListener(this);
        jMenuIteamTimerAnim.addActionListener(this);

        jMenuIteamAttendeeList.addActionListener(this);
        jMenuIteamExportList.addActionListener(this);

        jMenuItemShapeSquare.addActionListener(this);
        jMenuItemShapeOval.addActionListener(this);
        jMenuItemShapeLine.addActionListener(this);

        jMenuItemColorSelect.addActionListener(this);
        jMenuItemColorRandom.addActionListener(this);

        jMenuItemClearBoard.addActionListener(this);
        jMenuItemClearChat.addActionListener(this);
        jMenuItemExitSelect.addActionListener(this);

        // Moniter MessageBox for Insert/Remove/Change Text input in the jTextFile
        jTextFieldMessageBox.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent de) {
                messageBoxChanged();
                if (!iAmTyping) {
                    iAmTyping(true);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent de) {
                messageBoxChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent de) {
                messageBoxChanged();
            }

            public void messageBoxChanged() {
                if (jTextFieldMessageBox.getText().equals("")) {
                    enableSendButton(false);
                    iAmTyping(false);
                } else {
                    enableSendButton(true);
                }
            }
        });

        // Add ActionListener to the "Send Button" to send text when clicking
        JButtonSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                streamObject(writeMessage(jTextFieldMessageBox.getText()));
                jTextFieldMessageBox.setText("");
            }
        });

        // Add MouseListener to the Application to Enable Drawing on the Whiteboard
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getX() > 190 && e.getX() < 503 && e.getY() > 57 && e.getY() < 379 && !isTimeOver) {
                    if (!(e.getX() == xPoint && e.getY() == yPoint)) { // Prevent drwaing in the same point
                        streamObject(drawShape(selectedShape, e.getX(), e.getY(), selectedColor));
                        xPoint = e.getX();
                        yPoint = e.getY();
                    }
                }
            }
        });

    }

    //
    // <editor-fold defaultstate="collapsed" desc="Override the actionPerformed Function">
    // Override the actionPerformed Function to customize the Clicking Results for the Menu Items
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == jMenuItemSetTimer) {
            setTimer();
            System.out.println("Set Timer");
        } else if (ae.getSource() == jMenuIteamResetTimer) {
            resetTimer();
            System.out.println("Reset Timer");
        } else if (ae.getSource() == jMenuIteamTimerAnim) {
            startTimerAnimation();
            System.out.println("Test Animation");
        }
        if (ae.getSource() == jMenuIteamAttendeeList) {
            openAttendeeList();
            System.out.println("Attendee List");
        } else if (ae.getSource() == jMenuIteamExportList) {
            try {
                writeToFile();
            } catch (IOException ex) {
                Logger.getLogger(TeacherSide.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Export List");
        }
        if (ae.getSource() == jMenuItemShapeSquare) {
            streamObject(new ComplexObject(6, 0));
            selectedShape = 0;
            updateSelectedShapeAndColor("Square");
            System.out.println("Square");
        } else if (ae.getSource() == jMenuItemShapeOval) {
            selectedShape = 1;
            streamObject(new ComplexObject(6, 1));
            updateSelectedShapeAndColor("Oval");
            System.out.println("Oval");
        } else if (ae.getSource() == jMenuItemShapeLine) {
            selectedShape = 2;
            streamObject(new ComplexObject(6, 2));
            updateSelectedShapeAndColor("Line");
            System.out.println("Line");
        }
        if (ae.getSource() == jMenuItemColorSelect) {
            setColor(0);
            System.out.println("Select Color");
        } else if (ae.getSource() == jMenuItemColorRandom) {
            setColor(1);
            System.out.println("Random Color");
        }
        if (ae.getSource() == jMenuItemClearBoard) {
            clearTheBoard();
            System.out.println("Board Cleared");
        }
        if (ae.getSource() == jMenuItemClearChat) {
            clearTheChat();
            System.out.println("Chat Cleared");
        }
        if (ae.getSource() == jMenuItemExitSelect) {
            exitApplication();
            isConnected = false;
        }
//        if (ae.getSource() == timerServer) {
//
//        }
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Override the Paint Function">
    // Override the Paint Function to Draw shapes on the Whiteboard
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        drawnShapesList.forEach((shape) -> {
            switch (shape.getType()) {
                case 0:
                    g.setColor(shape.getColor());
                    g.fillRect(shape.getX(), shape.getY(), 86, 60);
                    break;
                case 1:
                    g.setColor(shape.getColor());
                    g.fillOval(shape.getX(), shape.getY(), 86, 60);
                    break;
                case 2:
                    g.setColor(shape.getColor());
                    g.drawLine(shape.getX(), shape.getY(), shape.getX() + 66, shape.getY() + 66);
                    break;
                case 3:
//                    Graphics2D g2 = (Graphics2D) g;
                    g.setFont(new Font("TimesRoman", Font.PLAIN, 26));
//                    g2.drawString("Time is Over", shape.getX(), shape.getY());
                    drawnShapesList = new ArrayList<Shape>();
                    g.setColor(Color.red);
                    g.drawString("Time is Over", shape.getX(), shape.getY());
                    break;
                default:
                    break;
            }
        });
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="(Switch - Counter - Draw - Append - Update - Clear) Functions">
    // Switch function to check the Received Object type and then process it accordingly
    public void receivedObjViaStreamSwitch(ComplexObject obj) {
        switch (obj.getId()) {
            case 0:
                System.out.println("Case 0");
                break;
            case 1:
                readMessage(obj.getMessage());
                System.out.println("Case 1");
                break;
            case 2:
                drawShape(obj.getType(), obj.getxPoint(), obj.getyPoint(), obj.getColor());
                System.out.println("Case 2");
                break;
            case 3:
                raiseHand();
                System.out.println("Case 3");
                break;
            case 4:
                isTyping(obj.isTrueOrFalse());
                System.out.println("Case 4");
                break;
            case 5:
                System.out.println("Case 5");
                break;
            case 6:
                setStudentName(obj.getMessage());
                jMenuIteamExportList.setEnabled(true);
                System.out.println("Case 6");
                break;
            case 7:
                System.out.println("Case 7");
                break;
            case 8:
                System.out.println("Case 8");
                break;
            case 9:
                System.out.println("Case 9");
                break;
            default:
                System.out.println("Case Default");
                break;
        }
    }

    // Update the status of the Connection Status Label
    private void updateStatus(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                jLabelConnectionStatus.setText(message);
            }
        });
    }

    // Update the time of the Timer Label
    private void updateTimer(final String time) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                jLabelSessionTimer.setText(time);
            }
        });
    }

    // Update the Shape and Color of the SelectedShapeAndColor Label
    private void updateSelectedShapeAndColor(final String selectedShapeAndColor) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                selectedDrawShape = selectedShapeAndColor;
                jPanelSelectedColor.setBackground(selectedColor);
                jLabelSelectedColor.setText(selectedShapeAndColor);
//                jLabelSelectedColor.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
            }
        });
    }

    // Append a message to the Chat Window
    private void appendMessageToChat(final String string) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                jTextAreaChat.append(string);
            }
        });
    }

    // Enable and Disable the Send Button
    private void enableSendButton(final boolean btnStatus) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JButtonSend.setEnabled(btnStatus);
            }
        });
    }

    // Select a Color for the Shape to draw
    public void setColor(int selection) {
        if (selection == 0) {
            Color initialcolor = Color.GRAY;
            selectedColor = JColorChooser.showDialog(jMenuItemColorSelect, "Select a color", initialcolor);
        } else if (selection == 1) {
            selectedColor = new Color((int) (Math.random() * 0x1000000));
        }
        streamObject(new ComplexObject(6, 3, selectedColor));
        updateSelectedShapeAndColor(selectedDrawShape);
    }

    // Shapes Counter showing the size of the "drawnShapesList" in the "jLabelShapesCount" Label
    public void shapesCounter() {
        jLabelShapesCount.setText("Shapes Count: " + String.valueOf(drawnShapesList.size()));
        jLabelShapesCount.setForeground(Color.black);
    }

    // Draw the selected shape on the Whiteboard and return a "sentComplexObj" ComplexObject
    public ComplexObject drawShape(int type, int x, int y, Color color) {
        ComplexObject sentComplexObj = new ComplexObject(2, type, x, y, color);
        Shape sentShape = new Shape(type, x, y, color);
        drawnShapesList.add(sentShape);
        repaint();
        if (!isTimeOver) {
            shapesCounter();
        }
        return sentComplexObj;
    }

    // Write Message on the Chat Window and return a "sentComplexObj" ComplexObject
    public ComplexObject writeMessage(String message) {
        ComplexObject sentComplexObj = new ComplexObject(1, message);
        if (jTextAreaChat.getText().equals("")) {
            appendMessageToChat("Teacher: " + message);
        } else {
            appendMessageToChat("\nTeacher: " + message);
        }
        return sentComplexObj;
    }

    // Write the Received Message from the "ComplexObject" on the Chat Window
    public void readMessage(String message) {
        if (jTextAreaChat.getText().equals("")) {
            appendMessageToChat("Student: " + message);
        } else {
            appendMessageToChat("\nStudent: " + message);
        }
    }

    // Enable and Disable the "Raise Hand" notification on the Server side
    public void raiseHand() {
        if (!isHandRaised) {
            jPanelRaiseHand.setBackground(Color.red);
            jPanelRaiseHand.add(jLabelRaiseHand, BorderLayout.CENTER);
            jLabelRaiseHand.setForeground(Color.black);
            isHandRaised = true;
        } else if (isHandRaised) {
            jPanelRaiseHand.setBackground(defaultColor);
            jPanelRaiseHand.add(jLabelRaiseHand, BorderLayout.CENTER);
            jLabelRaiseHand.setForeground(Color.gray);
            isHandRaised = false;
        }
    }

    // Check if the Student is Typing
    public void isTyping(boolean isTypingNow) {
        if (isTypingNow) {
            updateStatus("<html>Student is <font color='blue'>Typing..</font></html>");
        } else if (!isTypingNow) {
            updateStatus("<html>Student is <font color='green'>Connected</font></html>");
        }
    }

    // Send the Typing Object to the Student
    public void iAmTyping(boolean iAmTypingNow) {
        if (iAmTypingNow) {
            ComplexObject sentComplexObj = new ComplexObject(4, true);
            streamObject(sentComplexObj);
            iAmTyping = true;
        } else if (!iAmTypingNow) {
            ComplexObject sentComplexObj = new ComplexObject(4, false);
            streamObject(sentComplexObj);
            iAmTyping = false;
        }
    }

    // Clear the Whiteboard and send the "ComplexObject" to the Client
    public void clearTheBoard() {
        ComplexObject sentComplexObj = new ComplexObject(5, 1);
        streamObject(sentComplexObj);
        drawnShapesList = new ArrayList<Shape>();
        repaint();
        jLabelShapesCount.setText("<html><font color='black'>Shapes Count: </font>0</html>");
        jLabelShapesCount.setForeground(Color.gray);
    }

    // Clear the Chat and send the "ComplexObject" to the Client
    public void clearTheChat() {
        ComplexObject sentComplexObj = new ComplexObject(5, 2);
        streamObject(sentComplexObj);
        jTextAreaChat.setText(null);
    }

    // Disable the MenuBar options on the Client Side and on the Teacher Side before connecting
    public void setMenuBarEnabled(boolean status) {
        jMenuShapes.setEnabled(status);
        jMenuColor.setEnabled(status);
        jMenuClear.setEnabled(status);
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Timer Functions">
    public void setTimer() {
        int timeSelected = 9;
        if (!isTimerRunning) {
            String[] options = {"30 Sec", "1 Min", "2 Min", "5 Min"};
            timeSelected = JOptionPane.showOptionDialog(null, "Select Time",
                    "Set Timer",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            streamObject(new ComplexObject(7, timeSelected, true));
            isTimerRunning = true;
        } else if (isTimerRunning) {
            String[] options = {"OK!"};
            int x = JOptionPane.showOptionDialog(null, "Timer is Already Running..",
                    "Error",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            timeSelected = 9;
        }
        System.out.println(timeSelected);

        switch (timeSelected) {
            case 0:
                startTimer(0, 30);
                break;
            case 1:
                startTimer(1, 0);
                break;
            case 2:
                startTimer(2, 0);
                break;
            case 3:
                startTimer(5, 0);
                break;
            case 9:
                break;
            default:
                break;
        }
        System.out.println("Timer is Ready");
    }

    public void startTimer(int min, int sec) {
        timeMin = min;
        timeSec = sec;
        timerServer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                calcTime();
            }
        });
        timerServer.start();
        jMenuIteamTimerAnim.setEnabled(false);
        jMenuIteamResetTimer.setEnabled(true);
        System.out.println("Timer has Started Successfully");
    }

    public void calcTime() {
        if (timeMin > 0 || timeSec > 0) {
            if (timeSec <= 0 && timeMin > 0) {
                timeMin -= 1;
                timeSec = 59;
            } else if (timeSec > 0) {
                timeSec -= 1;
            }
            formatTime();
        }
    }

    public void formatTime() {
        String timeMinFormatted = String.valueOf(timeMin), timeSecFormatted = String.valueOf(timeSec);
        if (timeMin < 10) {
            timeMinFormatted = "0" + String.valueOf(timeMin);
        }
        if (timeSec < 10) {
            timeSecFormatted = "0" + String.valueOf(timeSec);
        }
        timeFormatted = timeMinFormatted + ":" + timeSecFormatted;
        colorTime(timeFormatted);
    }

    public void colorTime(String time) {
        if (timeMin < 1) {
            timeFormatted = ("<html><font color='black'>Time Left: </font><font color='red'>" + time + "</font></html>");
        } else {
            timeFormatted = ("<html><font color='black'>Time Left: </font><font color='green'>" + time + "</font></html>");
        }
        if (timeMin <= 0 && timeSec <= 0) {
            timeFormatted = ("<html><font color='red'>Time is Over</font></html>");
            resetTimer();
            startTimerAnimation();
            System.out.println("Timer Stopped");
        }
        updateTimer(timeFormatted);
    }

    public void resetTimer() {
        if (isTimerRunning) {
            updateTimer("<html><font color='black'>Time Left: </font>00:00</html>");
            streamObject(new ComplexObject(7, false));
            timerServer.stop();
            isTimerRunning = false;
            jMenuIteamResetTimer.setEnabled(false);
            jMenuIteamTimerAnim.setEnabled(true);
        }
        if (isTimeOver) {
            streamObject(new ComplexObject(3, false));
            stopAnimation();
            jMenuIteamResetTimer.setEnabled(false);
            jMenuIteamTimerAnim.setEnabled(true);
        }
        System.out.println("Timer has been Reset Successfully");
    }

    // Write Animation code here
    public void startTimerAnimation() {
        isTimeOver = true;
        streamObject(new ComplexObject(3, true));
        startAnimation();
        System.out.println("startTimerAnimation function has Started Successfully");
    }

    public void setAnimation() {
        if (x >= 438 || x <= 182) {
            dx = -dx;
        }
        if (y >= 434 || y <= 70) {
            dy = -dy;
        }
        x += dx;
        y += dy;
        drawShape(3, x, y, selectedColor);
    }

    public void startAnimation() {
        enableUI(false);
        clearTheBoard();
        x = 300;
        y = 300;
        dx = +2;
        dy = +2;
        timerAnimation = new Timer(25, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                setAnimation();
            }
        });
        timerAnimation.start();
        jMenuIteamResetTimer.setEnabled(true);
        System.out.println("timerAnimation has Started Successfully");
    }

    public void stopAnimation() {
        timerAnimation.stop();
        drawnShapesList = new ArrayList<Shape>();
        repaint();
        enableUI(true);
    }

    public void enableUI(boolean isEnabled) {
        isTimeOver = !isEnabled;
        jMenuAttendance.setEnabled(isEnabled);
        jMenuShapes.setEnabled(isEnabled);
        jMenuColor.setEnabled(isEnabled);
        jMenuClear.setEnabled(isEnabled);
        jMenuItemSetTimer.setEnabled(isEnabled);
        jMenuIteamTimerAnim.setEnabled(isEnabled);
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc=" Set Student Name + Attendance List + Export Attendance File">
    public void openAttendeeList() {
        if (studentName == null) {
            JOptionPane.showConfirmDialog(null, "The student didn't enter the name yet", "Attendee List",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
            System.out.println("AttendeeList is Empty");
        } else {
            JOptionPane.showConfirmDialog(null, "Student Name: " + studentName, "Attendee List",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
            System.out.println("AttendeeList has a name");
        }
    }

    public void setStudentName(String theStudentName) {
        studentName = theStudentName;
    }

    public void writeToFile() throws IOException {
        FileSystemView filesys = FileSystemView.getFileSystemView();
        File[] roots = filesys.getRoots();
        File homeDirectory = filesys.getHomeDirectory();
        Date nowDate = new Date();
        String nowOnlyDate = new SimpleDateFormat("yyyy/MM/dd").format(Calendar.getInstance().getTime());

        try (PrintWriter attendeeListfile = new PrintWriter(homeDirectory + "./Attendee List.txt")) {
            attendeeListfile.println("                " + "Attendance List" + " - " + nowOnlyDate );
            attendeeListfile.println("1. " + studentName);
            attendeeListfile.println("2. ");
            attendeeListfile.println("3. ");
            attendeeListfile.println("4. ");
            attendeeListfile.println("5. ");
            attendeeListfile.println("6. ");
            attendeeListfile.close();
        }
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Exit Confirmation and CheckBox">
    public void exitApplication() {
        JPanel exitPanel = new JPanel();
        exitPanel.setLayout(null);
        JLabel exitLabelQuestion = new JLabel("Are you sure?");
        exitLabelQuestion.setBounds(0, 4, 80, 20);
        JCheckBox exitCheckBox = new JCheckBox();
        exitCheckBox.setBounds(90, 4, 20, 20);
        JLabel exitLabelConfirm = new JLabel("Confirm");
        exitLabelConfirm.setBounds(112, 4, 50, 20);
        exitPanel.add(exitLabelQuestion);
        exitPanel.add(exitCheckBox);
        exitPanel.add(exitLabelConfirm);
        JOptionPane.showMessageDialog(null, exitPanel);
        boolean isExitChecked = exitCheckBox.isSelected();
        if (isExitChecked) {
            System.out.println("Exit is Checked");
            System.exit(0);
        } else {
            System.out.println("Exit is Not Checked");
        }
        System.out.println("The Program has been Successfully Terminated");
    }
    // </editor-fold>
    //

}