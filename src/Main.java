import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Main {

    private static JLabel result;

    private static int min = -9999999;
    private static int max = -9999999;

    private static int randomNumber;

    private static int rhythmNumber;

    private static int signBefore; // sign=1 means its positive  ----  sign=-1 means its negative
    private static int signAfter;

    private static int speed;
    private static int effectNumber;
    private static int distance;
    private static boolean timerIsRunning = false;

    public static void main(String[] args) {

        JFrame frame = new JFrame("Random Generator");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);
        frame.setSize(394, 260);
        frame.setVisible(true);
        frame.getContentPane().setBackground(new Color(14, 147, 150));
        ImageIcon icon = new ImageIcon(Main.class.getResource("image/random.png"));
        frame.setIconImage(icon.getImage());

        result = new JLabel();
        result.setBounds(0, 130, 370, 60);
        result.setHorizontalAlignment(JTextField.CENTER);
        result.setFont(new Font("Tahoma", Font.BOLD, 15));
        frame.add(result);

        setSpeed();
        JButton calculate = new JButton("Calculate");
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER)
                    calculate.doClick();
                if (e.getKeyChar() == KeyEvent.VK_ESCAPE)
                    frame.dispose();
            }
        });

        JTextField minTextField = new JTextField("Min");
        minTextField.setHorizontalAlignment(JTextField.CENTER);
        minTextField.setFocusable(true);
        minTextField.setBounds(55, 40, 105, 35);
        minTextField.setBackground(new Color(53, 174, 226));
        minTextField.setFont(new Font("Bookman Old Style", Font.BOLD, 15));
        minTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if ((c >= '0' && c <= '9') || c == KeyEvent.VK_ENTER || c == KeyEvent.VK_ESCAPE || c == KeyEvent.VK_TAB || c == KeyEvent.VK_BACK_SPACE)
                    playSound();
                if (minTextField.getText().contains("Min")) // if it has the word min delete it
                    minTextField.setText("");
                if (minTextField.getText().length() == 9)
                    e.consume();
                if (c == KeyEvent.VK_ENTER)
                    calculate.doClick();
                if (e.getKeyChar() == KeyEvent.VK_ESCAPE)
                    frame.dispose();
                if (c < '0' || c > '9') // accept only numbers
                    e.consume();
            }
        });
        minTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (minTextField.getText().equals("Min")) // remove the word min
                    minTextField.setText("");
            }
        });
        frame.add(minTextField);
        minTextField.requestFocus();

        JTextField maxTextField = new JTextField("Max");
        maxTextField.setHorizontalAlignment(JTextField.CENTER);
        maxTextField.setFocusable(true);
        maxTextField.setBounds(215, 40, 105, 35);
        maxTextField.setBackground(new Color(53, 174, 226));
        maxTextField.setFont(new Font("Bookman Old Style", Font.BOLD, 15));
        maxTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (maxTextField.getText().equals("Max")) // remove the word max
                    maxTextField.setText("");
            }
        });
        maxTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if ((c >= '0' && c <= '9') || c == KeyEvent.VK_ENTER || c == KeyEvent.VK_ESCAPE || c == KeyEvent.VK_TAB || c == KeyEvent.VK_BACK_SPACE) {
                    playSound();
                }
                if (maxTextField.getText().contains("Max")) // if it has the word max delete it
                    maxTextField.setText("");
                if (maxTextField.getText().length() == 9)
                    e.consume();
                if (c < '0' || c > '9') // accept only numbers
                    e.consume();
                if (e.getKeyChar() == KeyEvent.VK_ESCAPE)
                    frame.dispose();
                if (c == KeyEvent.VK_ENTER) {
                    calculate.doClick();
                }
            }
        });
        frame.add(maxTextField);

        calculate.setBounds(120, 90, 140, 30);
        calculate.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ESCAPE)
                    frame.dispose();
                calculate.doClick();
            }
        });
        calculate.setFocusable(false);
        calculate.setBorderPainted(false); // Turns off the lines around button
        calculate.setFocusPainted(false);
        calculate.setBackground(new Color(59, 89, 182));
        calculate.setForeground(Color.WHITE);
        calculate.setFont(new Font("Tahoma", Font.BOLD, 15));
        calculate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // take the integers
                    min = Integer.parseInt(minTextField.getText());
                    max = Integer.parseInt(maxTextField.getText());
                    if (max == 2147483647) {
                        result.setText("Too big number");
                        return;
                    }

                    if (!timerIsRunning) { // run only once per time
                        timerIsRunning = true;

                        if (min > max) { // if min > max then we swap the texts because max has to be bigger
                            minTextField.setText(max + "");
                            maxTextField.setText(min + "");
                            min = Integer.parseInt(minTextField.getText());
                            max = Integer.parseInt(maxTextField.getText());
                        }
                        minTextField.setText(setTextFieldToCorrectForm(minTextField.getText()));
                        maxTextField.setText(setTextFieldToCorrectForm(maxTextField.getText()));
                        Random random = new Random();
                        do {
                            randomNumber = random.nextInt(max + 1);
                        } while (randomNumber < min);

                        try {
                            String realResult = result.getText().replace(".", "");
                            int previous = Integer.parseInt(realResult);
                            effectNumber = previous;
                            distance = randomNumber - previous;
                            setSpeed();
                            findSignBeforeCalculations();
                            Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    findSignAfterCalculations();
                                    if (distance == 0 || signAfter != signBefore) { // if its equal or if the sign changed, then leave from the timer
                                        setResultText(randomNumber);
                                        timerIsRunning = false;
                                        timer.cancel();
                                    }
                                    setSpeed();
                                    if (distance > 0) { // previous > randomNumber
                                        effectNumber += rhythmNumber;
                                        distance -= rhythmNumber;
                                        setResultText(effectNumber);
                                    } else if (distance < 0) { // previous < randomNumber
                                        effectNumber -= rhythmNumber;
                                        distance += rhythmNumber;
                                        setResultText(effectNumber);
                                    }
                                }
                            }, 0, speed);
                        } catch (Exception exception) { // first time giving random number
                            timerIsRunning = false;
                            setResultText(randomNumber);
                            //result.setText(randomNumber + "");
                            System.out.println(exception);
                        }
                    }
                } catch (Exception exception) {
                    result.setText("Please put the min and max numbers");
                    System.out.println(exception);
                }
            }
        });
        frame.add(calculate);
        frame.repaint();
    }

    public static void setSpeed() { // changes the speed of the clock compared to the distance of the previous and the current number
        int realDistance = Math.abs(distance);
        if (realDistance <= 15) {
            rhythmNumber = 1;
            speed = 30;
        } else if (realDistance <= 50) {
            rhythmNumber = 2;
            speed = 13;
        } else if (realDistance <= 150) {
            rhythmNumber = 4;
            speed = 8;
        } else if (realDistance <= 500) {
            rhythmNumber = 10;
            speed = 5;
        } else if (realDistance <= 1500) {
            rhythmNumber = 31;
            speed = 2;
        } else if (realDistance <= 5000) {
            rhythmNumber = 211;
            speed = 1;
        } else if (realDistance <= 20000) {
            rhythmNumber = 811;
            speed = 1;
        } else if (realDistance <= 50000) {
            rhythmNumber = 3111;
            speed = 1;
        } else if (realDistance <= 150000) {
            rhythmNumber = 21111;
            speed = 1;
        } else if (realDistance <= 500000) {
            rhythmNumber = 81111;
            speed = 1;
        } else if (realDistance <= 2000000) {
            rhythmNumber = 31111;
            speed = 1;
        } else if (realDistance <= 15000000) {
            rhythmNumber = 211111;
            speed = 1;
        } else if (realDistance <= 50000000) {
            rhythmNumber = 811111;
            speed = 1;
        } else if (realDistance <= 200000000) {
            rhythmNumber = 311111;
            speed = 1;
        } else if (realDistance <= 999999999) {
            rhythmNumber = 2111111;
            speed = 1;
        }
    }

    public static void findSignBeforeCalculations() { // finds the sign before the timer starts
        if (distance > 0)
            signBefore = 1;
        else if (distance < 0)
            signBefore = -1;
    }

    public static void findSignAfterCalculations() { // finds the sign while the timer is running
        if (distance > 0)
            signAfter = 1;
        else if (distance < 0)
            signAfter = -1;
    }

    public static void setResultText(int number) {
        result.setText(number + "");

        String currentNumber = result.getText();
        String newString = "";
        for (int i = 0; i < currentNumber.length(); i++) {
            if ((currentNumber.length() - i - 1) % 3 == 0) {
                newString += Character.toString(currentNumber.charAt(i)) + ".";
            } else {
                newString += Character.toString(currentNumber.charAt(i));
            }
        }
        if (newString.endsWith(".")) {
            newString = newString.substring(0, newString.length() - 1) + "";
        }
        result.setText(newString);
    }

    public static String setTextFieldToCorrectForm(String textFieldString) {
        int i = 0;
        while (i < textFieldString.length() && textFieldString.charAt(i) == '0')
            i++;

        StringBuffer sb = new StringBuffer(textFieldString);

        sb.replace(0, i, "");

        return sb.toString();  // return in String
    }

    public static void playSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/music/Typing.wav").getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            clip.start();
            gainControl.setValue(-10);
        } catch (Exception ex) {
            System.out.println("Error with playing sound.");
            ex.printStackTrace();
        }
    }
}