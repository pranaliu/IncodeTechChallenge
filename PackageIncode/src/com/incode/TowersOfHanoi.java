package com.incode;

import java.awt.Rectangle;
import java.util.Optional;



import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

/**
 *  This program shows an animation of the famous Towers of Hanoi problem, for a pile
 *  of entered disks.  Three control buttons allow the user to control the animation.
 *  A "Next" button allows the user to see just one move in the solution.  Clicking
 *  the "Run" button will let the animation run on its own; while it is running,
 *  "Run" changes to "Pause", and clicking the button will pause the animation.
 *  A "Start Again" button allows the user to restart the problem from the beginning.
 *  "Submit" button allows the user to construct pile of disk that are entered
 *  Text area Disk allows user to add number of disks.
 *  
 *  The program is an example of using the wait() and notify() methods.  The
 *  wait() method is used to pause the animation between moves.  When the user
 *  clicks "Next" or "Run", the notify() method is called to notify the thread to
 *  wake up and continue.  A "status" variable is used to communicate commands to
 *  the thread.
 *  This program will run for minimum of 3 disks and maximum of 10 due to GUI constraints. 
 *  As such algorithm is scaled to infinite disks, however GUI is constrained to fit 10 disks and gain good experience.
 */
public class TowersOfHanoi extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    //-----------------------------------------------------------

    private static Color BACKGROUND_COLOR = Color.rgb(255,255,180); // 4 colors used in drawing.
    private static Color BORDER_COLOR = Color.rgb(100,0,0);
    private static Color DISK_COLOR = Color.rgb(0,0,180);
    private static Color MOVE_DISK_COLOR = Color.rgb(180,180,255);
    private static Color TEXT_COLOR = Color.rgb(255,0,0);//(255, 140, 0);

    private Canvas canvas;     // The canvas where the "towers" are drawn.
    private GraphicsContext g; // The graphics context for drawing on the canvas.

    private int status;   // Controls the execution of the thread; value is one of the following constants.

    private static final int GO = 0;       // a value for status, meaning thread is to run continuously    
    private static final int PAUSE = 1;    // a value for status, meaning thread should not run
    private static final int STEP = 2;     // a value for status, meaning thread should run one step then pause
    private static final int RESTART = 3;  // a value for status, meaning thread should start again from the beginning
    private static final int HOLD = 4;    // a value for status, meaning thread should begin after submit

    /* 
     The following variables are the data needed for the animation.  The
      three "piles" of disks are represented by the variables tower and
      towerHeight.  towerHeight[i] is the number of disks on pile number i.
      For i=0,1,2 and for j=0,1,...,towerHeight[i]-1, tower[i][j] is an integer
      representing one of the desired number(n) of disks. (The disks are numbered from 1 to n.)
      Minimum permissible disks are 3. 
      During the solution, as one disk is moved from one pile to another,
      the variable moveDisk is the number of the disk that is being moved,
      and moveTower is the number of the pile that it is currently on.
      This disk is not stored in the tower variable.  It is drawn in a
      different color from the other disks.
     */

    private int[][] tower;
    private int[] towerHeight;
    private int moveDisk;
    private int moveTower;
    static  int disks;
   


    private Button runPauseButton;  //  control buttons for controlling the animation
    private Button nextStepButton;
    private Button startOverButton;
    private Button startGame;       //  Submit number of disks for game
    private TextArea area;          //  Enter number of disks with which game should be played 
    
    /* The minimal number of moves need to counted and proved to be of efficiency 2^n - 1 */

    int stepCount ;                 // a value to count total number of moves disks took to move from source to destination.


    /**
     * Set up the GUI and event handling.
     */
    public void start (Stage stage) {
       // canvas = new Canvas(430,143); //Canvas area measurement
    	 canvas = new Canvas(500,200); //Canvas area measurement
        g = canvas.getGraphicsContext2D();
        //PRANALI Add Text area to take input number of disks
        //Setting the label
         Label label = new Label("Enter Disks");
         Font font = Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 12);
         label.setFont(font);
         //Creating a pagination
         area = new TextArea();
         //Setting number of pages
         //area.setText("Minimum 3 disks should be entered");
         area.setPrefColumnCount(1);
         area.setPrefHeight(1);
         area.setPrefWidth(1);
         
         /*Text area should only accept integer number for disks. Bind text area property to accept numeric value */
         area.textProperty().addListener(new ChangeListener<String>() {
        	    @Override
        	    public void changed(ObservableValue<? extends String> observable, String oldValue, 
        	        String newValue) {
        	        if ( !newValue.isEmpty() && (newValue.matches("\\d*") )) {
        	    
        	        	disks = Integer.parseInt(newValue);
        	            
        	        } else {
        	            area.setText(oldValue);
        	        }
        	    }
        	});
         
         startGame = new Button("Submit");
         startGame.setOnAction( e -> {
         	//if ((area.getText() != null && !area.getText().isEmpty())) {
              //   int plates = Integer.parseInt(area.getText());
                
               //  }
                 
         	//Enforce user to add disks from 3-10 numbers only
         	if (disks < 3 || disks > 10 )
         		
            {
            	 Alert alert = new Alert(Alert.AlertType.WARNING, "Please Enter disks between 3 and 10", ButtonType.OK, ButtonType.CANCEL);

            	 DialogPane root = alert.getDialogPane();

            	 Stage dialogStage = new Stage(StageStyle.UTILITY);

            	 for (ButtonType buttonType : root.getButtonTypes()) {
            	     ButtonBase button = (ButtonBase) root.lookupButton(buttonType);
            	     button.setOnAction(evt -> {
            	         root.setUserData(buttonType);
            	         dialogStage.close();
            	     });
            	 }

            	 // replace old scene root with placeholder to allow using root in other Scene
            	 root.getScene().setRoot(new Group());

            	 root.setPadding(new Insets(10, 0, 10, 0));
            	 Scene scene = new Scene(root);

            	 dialogStage.setScene(scene);
            	 dialogStage.initModality(Modality.APPLICATION_MODAL);
            	 dialogStage.setAlwaysOnTop(true);
            	 dialogStage.setResizable(false);
            	 dialogStage.showAndWait();
            	 Optional<ButtonType> result = Optional.ofNullable((ButtonType) root.getUserData());
            	 //System.out.println("result: "+result.orElse(null));
            }
         	else {
         	 
         	 setUpProblem(disks);
             runPauseButton.setDisable(false); // Disable Run button at the start of game
             nextStepButton.setDisable(false); // Disable Next button at the start of game
         	}
         });
         
       
         startGame.setMaxWidth(10000);
         startGame.setPrefWidth(10);
         startGame.setDisable(false);
    

        runPauseButton = new Button("Run");
        runPauseButton.setOnAction( e -> doStopGo());
        runPauseButton.setMaxWidth(10000);
        runPauseButton.setPrefWidth(10);
        nextStepButton = new Button("Next Step");
        nextStepButton.setOnAction( e -> doNextStep());
        nextStepButton.setMaxWidth(10000);
        nextStepButton.setPrefWidth(10);
        startOverButton = new Button("Start Again");
        startOverButton.setOnAction( e -> {
        	 setUpProblem(disks);        
             doRestart() ;
        });
        startOverButton.setMaxWidth(10000);
        startOverButton.setPrefWidth(10);
        startOverButton.setDisable(true);
   
        
        	
       
        HBox bottom = new HBox( label,area,startGame, runPauseButton, nextStepButton, startOverButton);
        bottom.setStyle("-fx-border-color: rgb(100,0,0); -fx-border-width: 4px 0 0 0");
        HBox.setHgrow(label, Priority.ALWAYS);
        HBox.setHgrow(area, Priority.ALWAYS);
        HBox.setHgrow(startGame, Priority.ALWAYS);
        HBox.setHgrow(runPauseButton, Priority.ALWAYS);
        HBox.setHgrow(nextStepButton, Priority.ALWAYS);
        HBox.setHgrow(startOverButton, Priority.ALWAYS);
        
      // PRANALI Check here how to dynamically grow border and canvas sizes
        BorderPane root = new BorderPane(canvas);
        root.setBottom(bottom);
        root.setStyle("-fx-border-color: rgb(100,0,0); -fx-border-width: 4px");
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();

        new AnimationThread().start();  // Create and start the thread that will solve
                                        // the puzzles.  The thread will immediately
                                        // block until user clicks "Run" or "Next Step".
    } // end start()


    /**
     *  Event-handling methods for the control buttons.  Changes in the
     *  value of the status variable will be seen by the animation thread,
     *  which will respond appropriately.
     */
    synchronized private void doStopGo() {
        if (status == GO) { 
        	// Animation is running.  Pause it.
            status = PAUSE;
            nextStepButton.setDisable(false);
            runPauseButton.setText("Run");
        }
        else {  // Animation is paused.  Start it running.
            status = GO;
            nextStepButton.setDisable(true);  // disabled when animation is running
            runPauseButton.setText("Pause");
        }
        notify();  // Wake up the thread so it can see the new status value!
    }
    
    synchronized private void doNextStep() {
        status = STEP;
        notify();
    }

    synchronized private void doRestart() {
        status = RESTART;
        notify();
    }
    

    



    /**
     *  The run() method for the animation thread.  Runs in an infinite loop.
     *  In the loop, the thread first sets up the initial state of the "towers"
     *  and of the buttons.  This includes setting the status to PAUSED, and
     *  calling checkStatus(), which will not return until the user clicks the
     *  "Run" button or the "Next" Button.  Once this happens, it calls
     *  the solve() method to run the recursive algorithm that solves the
     *  Towers Of Hanoi problem.  During the solution, checkStatus() is
     *  called after each move.  If the user clicks the "Start Again" button,
     *  checkStatus() will throw an IllegalStateException, which will cause
     *  the solve() method to be aborted.  The exception is caught to prevent
     *  it from crashing the thread.  
     */
    private class AnimationThread extends Thread {
        AnimationThread() {
                // The constructor sets this thread to be a Daemon thread.
                // Otherwise, the thread will keep the Java Virtual Machine
                // from exiting when the window is closed.
            setDaemon(true);
        }
        public void run() {
            while (true) {
                Platform.runLater( () -> {
                    runPauseButton.setText("Run");
                   //PR runPauseButton.setDisable(false);
                   //PR nextStepButton.setDisable(false);
                    runPauseButton.setDisable(true);
                    nextStepButton.setDisable(true);
                    startOverButton.setDisable(true);
                    startGame.setDisable(false);
                    
                });
              //PRANALI comment and call from start  setUpProblem();
            	//setUpProblem(disks);  // Sets up the initial state of the puzzle.
            	
                
                status = PAUSE;
                checkStatus(); // Returns only when user has clicked "Run" or "Next Step"
                Platform.runLater( () -> startOverButton.setDisable(false) );
                try {
                	disks = setDisks(area); //Collect input disks from text area before calling solve Tower of Hanoi problem with solve method 
                     //solve(10,0,1,2);  // Move 10 disks from pile 0 to pile 1.
                	//solve(3,0,1,2); 
               
                	stepCount = 0;
                	//solve(disks,0,1,2); // Move n disks from pile 0 to pile 2. Helper tower is last one and target is middle one.
                	solve(disks,0,2,1); // Move n disk from pile 0 to pile 1. Helper tower is middle one and target is last one.
                	//System.out.print("Step Count is:" + stepCount);
                	calcMoves();
                	     // When solution is done, give the user a chance to see it,
                        // and make them click RESTART to continue with a new solution.
                    status = PAUSE;
                    Platform.runLater( () -> { // Make sure user can only click startOver.
                        runPauseButton.setDisable(true);
                        nextStepButton.setDisable(true);
                        startOverButton.setDisable(false);
                    } );
                    checkStatus();  // Returns only when use clicks "Start Over".
                }
                catch (IllegalStateException e) {
                    // Exception was thrown because user clicked "Start Over".
                }
            }
        }
    }


    /**
     *  This method is called before starting the solution and after each
     *  move of the solution.  If the status is PAUSE, it waits until
     *  the status changes.  If the status is RESTART, it throws
     *  an IllegalStateException that will abort the solution.
     *  When this method returns, the value of status must be 
     *  RUN or STEP.
     */
    synchronized private void checkStatus() {
        while (status == PAUSE) {
            try {
                wait();
            }
            catch (InterruptedException e) {
            }
        }
        // At this point, status is RUN, STEP, or RESTART.
        if (status == RESTART)
            throw new IllegalStateException("Restart");
        // At this point, status is RUN or STEP, and solution should proceed.
    }


    /** 
     * Get value of textarea here and read it and pass it further in Run method
     *
     */
    synchronized private int setDisks(TextArea area) {
    	String plate;
    	int count;
    	
    	plate = area.getText();
    	//System.out.println("Inside setDisks reading text value:"+ plate);
    	count = Integer.parseInt(plate);
    	if(count == 0)    	
    		count = 3; // If there is no input disk number provided, default it to 3 disk game
    	//System.out.println("Inside setDisks defining plates:");
    	//System.out.print(count);
     return count; // return disk count to run Tower Of Hanoi game
     }
    /**
     * Sets up the initial state of the Towers Of Hanoi puzzle, with
     * all the disks on the first pile.  
     */
    synchronized private void setUpProblem(int plate ) {
       if(plate == 0)
    	   disks=3; //set default value of plate
    	   
    	moveDisk= 0;
       // System.out.println("In set up problem trying to print value of disks");
       // System.out.println(disks);
        //tower = new int[3][10];
        tower = new int[3][plate];
       //PR for (int i = 0; i < 10; i++)
        for (int i = 0; i < plate; i++)
            tower[0][i] = plate - i; //PR 10 - i;
        towerHeight = new int[plate];
        towerHeight[0] = plate; //PR 10;
        Platform.runLater( () -> drawInitialFrame() );
    }


    /**
     * Solves the TowersOfHanoi problem to move the specified
     * number of disks from one pile to another.
     * @param disks the number of disks to be moved
     * @param from the number of the pile where the disks are now
     * @param to the number of the pile to which the disks are to be moved
     * @param spare the number of the pile that can be used as a spare
     */
    private void solve(int disks, int from, int to, int spare) {
    	
    	 if (disks == 0)
    	    {
    	        return;
    	    }
    	if (disks == 1)
        { moveOne(from,to);
        stepCount += 1;
     
        }
        else {
            solve(disks-1, from, spare, to);
            stepCount += 1;
          
            moveOne(from,to);
            solve(disks-1, spare, to, from);
            
        
        }
    
        
    }


    /**
     * Move the disk at the top of pile number fromStack to
     * the top of pile number toStack.  (The disk changes to
     * a new color, then moves, then changes back to the standard
     * color.)  The delay() method is called to insert some short
     * delays into the animation.  After the move, if the value of
     * status was STEP, indicating that only one step was to be
     * executed before pausing, then the value of STATUS is changed
     * to PAUSE.  In any case, at the end of the method, the
     * checkStatus() method is called.
     * delay() method adds time in execution and overall user experience for performance wise but this is necessary
     * to make animation look clean for user perspective. 
     *
     * delay() can be minimized to improve overall performance
     * */
    synchronized private void moveOne(int fromStack, int toStack) {
        moveDisk = tower[fromStack][towerHeight[fromStack]-1];
        moveTower = fromStack;
       // delay(120);
       // delay(20);
        delay(40);
        towerHeight[fromStack]--;
        putDisk(MOVE_DISK_COLOR,moveDisk,moveTower,towerHeight[fromStack]);
       // delay(80);
        //delay(10);
        delay(20);
        putDisk(BACKGROUND_COLOR,moveDisk,moveTower,towerHeight[fromStack]);
       // delay(80);
       // delay(10);
        delay(20);
        moveTower = toStack;
        putDisk(MOVE_DISK_COLOR,moveDisk,moveTower,towerHeight[toStack]);
        //delay(80);
        //delay(10);
        delay(20);
        putDisk(DISK_COLOR,moveDisk,moveTower,towerHeight[toStack]);
        tower[toStack][towerHeight[toStack]] = moveDisk;
        towerHeight[toStack]++;
        moveDisk = 0;
        if (status == STEP)
            status = PAUSE;
        checkStatus();
    }


    /**
     * Simple utility method for inserting a delay of a specified
     * number of milliseconds.
     */
    synchronized private void delay(int milliseconds) {
        try {
            wait(milliseconds);
        }
        catch (InterruptedException e) {
        }
    }


    /**
     * Draw a specified disk to the off-screen canvas.  This is
     * used only during the moveOne() method, to draw the disk
     * that is being moved.  This method is called from the animation
     * thread. It uses Platform.runLater() to apply the drawing to
     * the canvas.
     * @param color the color of the disk (use background color to erase)
     * @param disk the number of the disk that is to be drawn, 1 to n
     * @param t the number of the pile on top of which the disk is drawn
     * @param h the height of the tower
     */
    private void putDisk(Color color, int disk, int t, int h) {
        Platform.runLater( () -> {
        	
            g.setFill(color);
            if (color == BACKGROUND_COLOR) {
                   // When drawing in the background color, to erase a disk, a slightly
                   // larger round rectangle is drawn. This is done to make sure that the
                   // disk is completely erased, since the anti-aliasing that was done
                   // when the disk was drawn can allow the disk color to bleed into pixels
                   // that lie outside the actual disk.
            	
                g.fillRoundRect(75+140*t - 5*disk - 6, 178-12*h - 1, 10*disk+12, 12, 10, 10);
            }
            else {
                g.fillRoundRect(75+140*t - 5*disk - 5, 178-12*h, 10*disk+10, 10, 10, 10);
            }
           
            
        });
     
    }
    
    //Add New label for showing number of total moves in Game
    private void calcMoves() {
    	Platform.runLater( () -> {
    	   String steps = String.valueOf(stepCount);
    	   //g.setFill(BORDER_COLOR);
    	  // g.fillRect(200,15,30,25);
    	   String textAdd = "Total Number of Steps performed--> ".concat(steps);
    	    g.setFill(DISK_COLOR);
    	   g.fillText(textAdd, 30, 15);
    	  // g.fillText(steps, 185,10);
    	   });
    	
      
    }


    /**
     * Called to draw the starting state of the towers, with all the
     * disks on the first base.
     * This method is called on the JavaFX application thread.
     */ 
    private void drawInitialFrame() {
        g.setFill(BACKGROUND_COLOR);
        g.fillRect(0,0,500,200);
        g.setFill(BORDER_COLOR);
        Rectangle []peg=new Rectangle[3];
       // g.fillRect(10,190,130,5);
       // g.fillRect(150,190,130,5);
       // g.fillRect(290,190,130,5);

       
        peg[0]=new Rectangle(10,190,130,5);
        peg[1]=new Rectangle(150,190,130,5);
        peg[2]=new Rectangle(290,190,130,5);
        
        
        
        for(int i=0;i<3;i++)
        { g.setFill(BORDER_COLOR);
          g.fillRect(peg[i].x,peg[i].y,peg[i].width,peg[i].height);
          g.setFill(TEXT_COLOR);
          g.fillText(""+(char)(i+65),peg[i].x+5,peg[i].y-10);
         
        }
        g.setFill(DISK_COLOR);
        for (int t = 0; (t < 3) && (t < towerHeight[t]) ; t++) { // condition added to work with less than 3 disks
      //  for (int t = 0; t < 3 ; t++) {
           for (int i = 0; i < towerHeight[t]; i++) {
                int disk = tower[t][i];
                g.fillRoundRect(75+140*t - 5*disk - 5, 178-12*i, 10*disk+10, 10, 10, 10);
                
           }
        }
        
     
       
    }


} // end class TowersOfHanoiGUI


