package sudoku;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class CompletionPanel {

    private static Image gif;
    public void main(String[] args) {
        // Create a JFrame to hold the JPanel
        JFrame frame = new JFrame("Congratulations");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a JPanel to hold the GIF and text
        JPanel panel = new JPanel(new BorderLayout());
        URL imgUrl = getClass().getResource("./images/slash.gif");
        // Load the GIF and add it to a JLabel
        ImageIcon gifIcon = new ImageIcon(gif);
        JLabel gifLabel = new JLabel();
        gifLabel.setIcon(gifIcon);
        

        // Use a layout manager to position the two JLabels in the JPanel
        panel.add(gifLabel, BorderLayout.CENTER);

        // Set the preferred size of the JPanel to match the GIF size
        panel.setPreferredSize(new Dimension(gifIcon.getIconWidth(), gifIcon.getIconHeight()));

        // // Create a Timer to detect when the GIF animation has ended
        // Timer timer = new Timer(gifIcon.getIconHeight() * gifIcon.getIconWidth() / gifIcon.getImageLoadStatus(), new ActionListener() {
        //     public void actionPerformed(ActionEvent e) {
        //         // When the animation ends, replace the GIF JLabel with the text JLabel
        //         panel.remove(gifLabel);
        //         panel.add(textLabel, BorderLayout.CENTER);
        //         panel.revalidate();
        //         panel.repaint();
        //     }
        // });
        // timer.setRepeats(false);

        // // Show the popup and start the timer
        // JOptionPane.showMessageDialog(frame, panel);
        // timer.start();
    }

    
    public Image loadGif (){
        URL imgUrl = getClass().getResource("./images/slash.gif");
        if (imgUrl == null) {
            System.err.println("Couldn't find file: ");
        } else {
            try {
                gif = ImageIO.read(imgUrl);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return gif;
    }
}
