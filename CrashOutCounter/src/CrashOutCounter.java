import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.sound.sampled.*;

public class CrashOutCounter extends JFrame {
    private int crashCount;
    private JLabel counterLabel;
    private final String FILE_NAME = "crash_count.txt";
    private Image backgroundImage;

    public CrashOutCounter() {
        // Load crash count from file
        crashCount = loadCrashCount();

        // Load background image
        backgroundImage = new ImageIcon(getClass().getResource("/background.jpg")).getImage();

        // Setup frame
        setTitle("Crash Out Counter");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Custom panel with background
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setLayout(new BorderLayout());
        add(panel);

        // Counter label
        counterLabel = new JLabel("Crash Outs: " + crashCount, SwingConstants.CENTER);
        counterLabel.setFont(loadCustomFont("lightbulb.ttf", 40));
        counterLabel.setForeground(Color.YELLOW);
        counterLabel.setOpaque(false);
        panel.add(counterLabel, BorderLayout.CENTER);

        // Button
        JButton crashButton = new JButton("Crash Out!");
        crashButton.setFont(new Font("Arial", Font.BOLD, 20));
        crashButton.setBackground(new Color(200, 50, 50));
        crashButton.setForeground(Color.WHITE);
        crashButton.setFocusPainted(false);
        crashButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3, true));
        crashButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Button hover effect
        crashButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                crashButton.setBackground(new Color(255, 0, 0));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                crashButton.setBackground(new Color(200, 50, 50));
            }
        });

        // Button action
        crashButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                crashCount++;
                counterLabel.setText("Crash Outs: " + crashCount);
                saveCrashCount();
                playSound("/hooray.wav"); // Play sound
            }
        });

        panel.add(crashButton, BorderLayout.SOUTH);
    }

    private int loadCrashCount() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            return Integer.parseInt(reader.readLine());
        } catch (IOException | NumberFormatException e) {
            return 0; // Default to 0 if file doesn't exist or is corrupted
        }
    }

    private void saveCrashCount() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            writer.write(String.valueOf(crashCount));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playSound(String soundFile) {
        try {
            InputStream audioSrc = getClass().getResourceAsStream(soundFile);
            if (audioSrc == null) {
                throw new FileNotFoundException("File not found: " + soundFile);
            }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new BufferedInputStream(audioSrc));
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            System.out.println("Sound error: " + e.getMessage());
        }
    }

    private Font loadCustomFont(String fontPath, float size) {
        try {
            InputStream is = getClass().getResourceAsStream("/" + fontPath);
            if (is == null) return new Font("Arial", Font.BOLD, (int) size);
            Font font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(size);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);
            return font;
        } catch (Exception e) {
            return new Font("Arial", Font.BOLD, (int) size);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CrashOutCounter().setVisible(true);
        });
    }
}
