// This is the JPANEL to draw the game
import java.awt.*; 
import java.awt.event.*;
import java.util.ArrayList; // this will store all the pipes in the game
import java.util.Random; // this will be used to place the pipes at random positions
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {  
    int boardWidth = 360;
    int boardHeight = 640;

    // Images
    // These four variables store the image objects
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    // Adding variables for the bird's position and size
    int birdX = boardWidth/8;
    int birdY = boardHeight/2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        // Define constructor and pass in the image
        Bird(Image img) {
            this.img = img;
        }
    }

    // Pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;  // scaled by 1/6  --> actual image is 384 pixels
    int pipeHeight = 512; // the actual dimensions for the image is 6 times bigger

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        // variable to check whether our bird has passed the pipe yet or not
        boolean passed = false; // for keeping track of score

        // Define the constructor
        Pipe(Image img) {
            this.img = img;
        }
    }

    // Game logic
    // Add field for bird
    Bird bird;
    // speed of moving pipes to the left (simulates bird moving right)
    int velocityX = -4; // (i.e change the x position by negative 4 pixels every frame)
    int velocityY = 0; // want to make the bird move, in pixels per frame
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    // Timer
    Timer gameLoop;
    Timer placePipesTimer;
    boolean gameOver = false;
    boolean gameStarted = false;
    double score = 0;

    // Buttons (play and reset)
    JButton playButton, resetButton;

    // Create FlappyBird constructor
    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true); // make sure that the FlappyBird JPanel is the one that takes in key events
        addKeyListener(this); // make sure to check the 3 functions at end when we have a key pressed

        // Load images onto variables
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        // Bird
        // Create bird object and list of pipes
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        // Create buttons and add to panel
        playButton = new JButton("Play");
        resetButton = new JButton ("Reset");

        // Set button positions
        setLayout(null); // Use absolute layout to position buttons manually
        playButton.setBounds(100, 300, 160, 40);
        resetButton.setBounds(100, 360, 160, 40);

        // Initially, only show play button
        resetButton.setVisible(false);
        add(playButton);
        add(resetButton);

        // Add action listeners to buttons
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetGame();
            }
        });

        // Place pipes timer
        // recall 1000 milliseconds = 1 second
        placePipesTimer = new Timer(1500, new ActionListener() { 
        // So 1500 ms = 1.5 seconds, so every 1.5 s we are going to call an action
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        }); 


        // Game timer: need game loop so that it can continuously draw frames for the game
        gameLoop = new Timer(1000/60, this); // want 60 frames per second
    }

    // Create function that will create the pipes and add them to the array list
    public void placePipes() { 
        // math.random give value between 0-1 then * pipeHeight/2 (which gives 256) -> (0-256)
        // 512/4 = 128
        // total = 0 - 128 - (0-256) --> if math.random gives 0 then pipeHeight/4, if gives 1 then substract pipeHeight/2
        // range = 1/4 pipeHeight -> 3/4 pipeHeight   this is how much we're shifting upwards for the y position 
        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardHeight/4;
        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe); // add bottom pipes to the array list
    }

    // Draw the image onto the background
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // super will invoke the function from JPanel
        draw(g);
    }

    public void draw(Graphics g) {
        // System.out.println("draw");   (just to test)
        // background
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        // bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        // pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("GAME OVER: " + String.valueOf((int) score), 10, 35);
        }
        else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    public void move() {
        // bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        // pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;
            
            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5; // 0.5 because there are 2 pipes, so 0.5*2 = 1, 1 pt for each set of pipes
            }

            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }
        if (bird.y > boardHeight) {
            gameOver = true;
        }
    }

    public boolean collision(Bird a, Pipe b) {
        // formula for detecting collisions
        return a.x < b.x + b.width && // a's top left corner doesn't reach b's top right corner
            a.x + a.width > b.x &&    // a's top right corner doesn't reach b's top left corner
            a.y < b.y + b.height &&   // a's top left corner doesn't reach b's bottom left corner
            a.y + a.height > b.y;     // a's bottom left corner passes b's top left corner
    }

    public void startGame() {
        // Hide play button and start the game
        playButton.setVisible(false);
        resetButton.setVisible(false);
        gameOver = false;
        gameStarted = true;
        velocityY = 0;

        pipes.clear();
        score = 0;
        
        // Start the game loop and pipe timer
        placePipesTimer.start();
        gameLoop.start();
    }

    public void resetGame() {
        // Reset the game state and start a new game
        gameOver = false;
        gameStarted = false; // Prevent game from moving until play is clicked again
        bird.y = boardHeight / 2;
        pipes.clear();
        velocityY = 0;
        score = 0;
        resetButton.setVisible(false);
        playButton.setVisible(true);

        // Delay showing the play button until the first frame of the new round
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                playButton.setVisible(true);
            }
        });

        repaint(); // Redraw the game in a reset state
    }

    @Override
    public void actionPerformed(ActionEvent e) { // action performed every 16 milliseconds or 60 times a second
        if (gameStarted && !gameOver) {
            move();
            repaint(); // with the JPanel, this will call the paintComponent
        } 

        if (gameOver) {
            placePipesTimer.stop();
            gameLoop.stop(); // stops repainting and updating the frames of the game
            resetButton.setVisible(true); // show reset button when game is over
        }
    }


    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && gameStarted && !gameOver) {
            velocityY = -9;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}