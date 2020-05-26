package whiteboard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.BorderFactory;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class StudentSide extends JFrame implements ActionListener {

    //
    // <editor-fold defaultstate="collapsed" desc="Declared Variables">
    ArrayList<Shape> drawnShapesList = new ArrayList<Shape>();

    BufferedImage bufferedImageHandImage;

    JPanel jPanelBoard, drawingPanel,
            jPanelConnectionStatus, jPanelSessionTimer, jPanelShapesCount, jPanelRaiseHand;
    JLabel jLabelConnectionStatus, jLabelSessionTimer, jLabelShapesCount, jLabelRaiseHand;

    JTextArea jTextAreaChat;
    JScrollPane jScrollPaneScrollableChat;
    JTextField jTextFieldMessageBox;
    JButton jbSendButton;

    JMenuBar jMenuBarMenuBar;
    JMenu jMenuShapes, jMenuColor, jMenuClear, jMenuExit;
    JMenuItem jMenuItemShapeSquare, jMenuItemShapeOval, jMenuItemShapeLine,
            jMenuItemColorSelect, jMenuItemClearBoard, jMenuItemClearChat, jMenuItemExitSelect;

    boolean studentMode = false, isHandRaised = false, iAmTyping = false;
    int selectedShape;

    Color btnColor = new Color(255, 255, 160);
    Color selectedColor = Color.GRAY;

    Timer timerClient;
    String timeFormatted;
    int timeMin, timeSec;

    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private String clientSocket;
    private Socket connectionSocket;

    // </editor-fold>
    //
    public StudentSide(String serverInfo) {
        super("Student Application");

        clientSocket = serverInfo;

        initializeGUI();
        setJMenuBarAndMenuBarItems();
        setActionListener();
        setMenuBarEnabled(false);

    }

    // Initialize all the Client Components in the GUI
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
        jPanelRaiseHand.setBackground(btnColor);
        jPanelRaiseHand.setBorder(BorderFactory.createLineBorder(Color.black));
        jLabelRaiseHand = new JLabel("<html>&nbsp;Raise<br/>&nbsp;Hand</html>");
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

        // Initialize The "Scrollable Chat Area" jTextArea and jScrollPane
        jPanelShapesCount = new JPanel();
        jPanelShapesCount.setBorder(BorderFactory.createLineBorder(Color.black));
        jLabelShapesCount = new JLabel("<html><font color='black'>Shapes Count: </font>0</html>");
        jLabelShapesCount.setForeground(Color.gray);
        jPanelShapesCount.add(jLabelShapesCount);

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
        jbSendButton = new JButton("Send");
        jbSendButton.setEnabled(false);
        jbSendButton.setBorder(BorderFactory.createLineBorder(Color.black));

        // Set The "GUI" Bounds for Position and Sizes
        jPanelConnectionStatus.setBounds(1, 1, 176, 30);
        jPanelRaiseHand.setBounds(123, 32, 54, 60);
        jPanelSessionTimer.setBounds(1, 32, 121, 30);
        jPanelShapesCount.setBounds(1, 63, 121, 29);
        jScrollPaneScrollableChat.setBounds(1, 93, 176, 232);
        jTextFieldMessageBox.setBounds(1, 326, 176, 30);
        jbSendButton.setBounds(1, 357, 176, 30);
        drawingPanel.setBounds(180, 1, 403, 386);

        // Add all the Components to the jPanel Layout
        jPanelBoard.add(jPanelConnectionStatus);
        jPanelBoard.add(jPanelRaiseHand);
        jPanelBoard.add(jPanelSessionTimer);
        jPanelBoard.add(jPanelShapesCount);
        jPanelBoard.add(jScrollPaneScrollableChat);
        jPanelBoard.add(jTextFieldMessageBox);
        jPanelBoard.add(jbSendButton);
        jPanelBoard.add(drawingPanel);

        jPanelBoard.setVisible(true);

    }

    // <editor-fold defaultstate="collapsed" desc="Connection Management Functions">
    //
    // Start the Client to Connect to the Server
    public void startTheClient() {
        try {
            connectToServer();
            initializeStreams();
            processTheConnection();
        } catch (EOFException e) {
            appendMessageToChat("\nClient Terminated Conn\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeTheConnection();
        }

    }

    // Connect to the Server
    private void connectToServer() throws IOException {
        updateStatus("<html><font color='red'>Trying To Connect..</font></html>");
        connectionSocket = new Socket(InetAddress.getByName(clientSocket), 22602);
    }

    // Initialize the Streams with the client
    private void initializeStreams() throws IOException {
        objectOutputStream = new ObjectOutputStream(connectionSocket.getOutputStream());
        objectOutputStream.flush();
        objectInputStream = new ObjectInputStream(connectionSocket.getInputStream());
        updateStatus("<html><font color='green'>Connected via Stream</font></html>");
    }

    // Process the Connection with the Client
    private void processTheConnection() throws IOException {
        updateStatus("<html>Teacher is <font color='green'>Connected</font></html>");
        ComplexObject receivedObject;
        do {
            try {
                receivedObject = (ComplexObject) objectInputStream.readObject();
                receivedObjViaStreamSwitch(receivedObject);
            } catch (ClassNotFoundException e) {
                appendMessageToChat("Unknown");
            }
        } while (true);
    }

    // Closs the Connection with the Client
    private void closeTheConnection() {
        updateStatus("<html><font color='red'>Terminating Connection..</font></html>");
        try {
            objectOutputStream.close();
            objectInputStream.close();
            connectionSocket.close();
            updateStatus("<html><font color='red'>Connection Terminated!</font></html>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Stream the Object to the Client
    private void streamObject(ComplexObject sentComplexObj) {
        try {
            objectOutputStream.writeObject(sentComplexObj);
            objectOutputStream.flush();
            //dispMessage("\nS:" + text);
        } catch (IOException e) {
            jTextAreaChat.append("\nError streamObject");
        }
    }

    // Initializes ALL the Menu Bar Items with it's options
    public void setJMenuBarAndMenuBarItems() {
        jMenuBarMenuBar = new JMenuBar();

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
        jMenuColor.add(jMenuItemColorSelect);
        jMenuBarMenuBar.add(jMenuColor);

        jMenuClear = new JMenu("Clear");
        jMenuItemClearBoard = new JMenuItem("Clear The Board");
        jMenuItemClearChat = new JMenuItem("Clear The Chat");
        jMenuClear.add(jMenuItemClearChat);
        jMenuClear.add(jMenuItemClearBoard);
        jMenuBarMenuBar.add(jMenuClear);

        jMenuExit = new JMenu("Exit");
        jMenuItemExitSelect = new JMenuItem("Exit Application");
        jMenuExit.add(jMenuItemExitSelect);
        jMenuBarMenuBar.add(jMenuExit);

        setJMenuBar(jMenuBarMenuBar);
    }

    // Add ActionListener to the Components which requires clicking action
    public void setActionListener() {
        jMenuItemShapeSquare.addActionListener(this);
        jMenuItemShapeOval.addActionListener(this);
        jMenuItemShapeLine.addActionListener(this);

        jMenuItemColorSelect.addActionListener(this);
        jMenuItemClearBoard.addActionListener(this);
        jMenuItemExitSelect.addActionListener(this);

        jTextFieldMessageBox.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent de) {
                changed();
                if (!iAmTyping) {
                    iAmTyping(true);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent de) {
                changed();
            }

            @Override
            public void changedUpdate(DocumentEvent de) {
                changed();
            }

            public void changed() {
                if (jTextFieldMessageBox.getText().equals("")) {
                    jbSendButton.setEnabled(false);
                    iAmTyping(false);
                } else {
                    jbSendButton.setEnabled(true);
                }
            }
        });

        jbSendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                streamObject(writeMessage(jTextFieldMessageBox.getText()));
                jTextFieldMessageBox.setText("");
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getX() > 131 && e.getX() < 184 && e.getY() > 85 && e.getY() < 145) {
                    raiseHand();
                }
            }
        });

    }

    // </editor-fold>
    //
    //
    // <editor-fold defaultstate="collapsed" desc="Override the actionPerformed Function">
    // Override the actionPerformed Function to customize the Clicking Results
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == jMenuItemExitSelect) {
            System.out.println("The Program has been Successfully Terminated");
            System.exit(0);
        }
    }

    // </editor-fold>
    //
    // Override the Paint Function to Draw shapes on the Whiteboard
    // <editor-fold defaultstate="collapsed" desc="Override the Paint Function">
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
                default:
                    break;
            }
        });
    }

    // </editor-fold>
    //
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
                System.out.println("Case 3");
                break;
            case 4:
                isTyping(obj.isTrueOrFalse());
                System.out.println("Case 4");
                break;
            case 5:
                clearTheBoard();
                System.out.println("Case 5");
                break;
            case 6:
                clearTheChat();
                System.out.println("Case 6");
                break;
            case 7:
                if (obj.isTrueOrFalse()) {
                    setTimer(obj.getNum());
                } else if (!obj.isTrueOrFalse()) {
                    resetTimer();
                }
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
                jbSendButton.setEnabled(btnStatus);
            }
        });
    }

    // Select a Color for the Shape to draw
    public void setColor() {
        Color initialcolor = Color.GRAY;
        selectedColor = JColorChooser.showDialog(jMenuItemColorSelect, "Select a color", initialcolor);
    }

    // Shapes Counter showing the size of the "drawnShapesList" in the "jLabelShapesCount" Label
    public void shapesCounter() {
        jLabelShapesCount.setText("Shapes Count: " + String.valueOf(drawnShapesList.size()));
        jLabelShapesCount.setForeground(Color.black);
    }

    // Draw the selected shape on the Whiteboard and return a "sentComplexObj" ComplexObject
    public void drawShape(int type, int x, int y, Color color) {
        Shape receivedShape = new Shape(type, x, y, color);
        drawnShapesList.add(receivedShape);
        shapesCounter();
        repaint();
    }

    // Write Message on the Chat Window and return a "sentComplexObj" ComplexObject
    public ComplexObject writeMessage(String message) {
        ComplexObject sentComplexObj = new ComplexObject(1, message);
        if (jTextAreaChat.getText().equals("")) {
            appendMessageToChat("Student: " + message);
        } else {
            appendMessageToChat("\nStudent: " + message);
        }
        return sentComplexObj;
    }

    // Write the Received Message from the "ComplexObject" on the Chat Window
    public void readMessage(String message) {
        if (jTextAreaChat.getText().equals("")) {
            appendMessageToChat("Teacher: " + message);
        } else {
            appendMessageToChat("\nTeacher: " + message);
        }
    }

    // Enable and Disable the "Raise Hand" notification on the Server side
    public void raiseHand() {
        if (!isHandRaised) {
            jPanelRaiseHand.setBackground(Color.green);
            jPanelRaiseHand.add(jLabelRaiseHand, BorderLayout.CENTER);
            jLabelRaiseHand.setForeground(Color.black);
            isHandRaised = true;
            ComplexObject sentComplexObj = new ComplexObject(3, true);
            streamObject(sentComplexObj);
        } else if (isHandRaised) {
            jPanelRaiseHand.setBackground(btnColor);
            jPanelRaiseHand.add(jLabelRaiseHand, BorderLayout.CENTER);
            jLabelRaiseHand.setForeground(Color.gray);
            isHandRaised = false;
            ComplexObject sentComplexObj = new ComplexObject(3, false);
            streamObject(sentComplexObj);
        }
    }

    // Chechk if the Client is Typing
    public void isTyping(boolean isTypingNow) {
        if (isTypingNow) {
            updateStatus("<html>Teacher is <font color='blue'>Typing..</font></html>");
        } else if (!isTypingNow) {
            updateStatus("<html>Teacher is <font color='green'>Connected</font></html>");
        }
    }

    // Send the Typing Object to the Client
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
        drawnShapesList = new ArrayList<Shape>();
        repaint();
        jLabelShapesCount.setText("<html><font color='black'>Shapes Count: </font>0</html>");
        jLabelShapesCount.setForeground(Color.gray);
    }

    // Clear the Chat and send the "ComplexObject" to the Client
    public void clearTheChat() {
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
    public void setTimer(int timeSelected) {

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
            default:
                break;
        }
        System.out.println("Timer is Ready");
    }

    public void startTimer(int min, int sec) {
        timeMin = min;
        timeSec = sec;
        timerClient = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                calcTime();
            }
        });
        timerClient.start();
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
            timerClient.stop();
            System.out.println("Timer Stopped");
        }
        updateTimer(timeFormatted);
    }

    public void resetTimer() {
        updateTimer("<html><font color='black'>Time Left: </font>00:00</html>");
        timerClient.stop();
        System.out.println("Timer has been Stopped Successfully");
    }

    // </editor-fold>
    //
}
