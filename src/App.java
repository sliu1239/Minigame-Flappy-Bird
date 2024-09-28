import javax.swing.*;

public class App {                 
    public static void main(String[] args) throws Exception {
        // dimensions of the window (in pixels)
        int boardWidth = 360; // 360 pixels (width of the bg image)
        int boardHeight = 640; // 640 pixels (height of the bg image)

        JFrame frame = new JFrame("Flappy Bird"); // title for the window
        frame.setSize(boardWidth, boardHeight); // set size of window
        frame.setLocationRelativeTo(null); // place the window at center of screen
        frame.setResizable(false);// user cannot resize window
        // when user clicks x button on the window, it will terminate the program
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add JPanel onto the frame
        FlappyBird flappyBird = new FlappyBird(); // create an instance of flappy bird
        frame.add(flappyBird);
        frame.pack(); // excludes the title bar in the dimensions of the blue screen
        flappyBird.requestFocus();
        frame.setVisible(true); // set the frame visible
    
    }  
}
