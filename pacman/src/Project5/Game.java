package Project5;

import enigma.console.TextAttributes;
import enigma.core.Enigma;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class Game {
    private enigma.console.Console cn = Enigma.getConsole();
    private KeyListener klis;
    private char[][] maze,maze1;
    private int cursorx = 10, cursory = 10;
    private int num_x, num_y;
    private int second=00,minute =00, hour=0;
    private int probability = 0, number=0;
    private int x=0, y=0;
    int TimeCounter=0;
    private int numberArray[]=new int[20];
    private int collected_num;
    private int human_num=5;
    private int  score=0;
    int px,py;
    private boolean gameflag=true;

    
    // ------ Standard variables for mouse and keyboard ------
    private int keypr;   // key pressed?
    private int rkey;    // key   (for press/release)
    Stack right_backpack =new Stack(8);
    Stack left_backpack =new Stack(8);
    Stack tempstack= new Stack(8);
    Stack tempstack1= new Stack(8);
    Queue input = new Queue(100000);
    private int t_count=0;
    Random rnd = new Random();
    TextAttributes black =new TextAttributes(Color.WHITE,Color.BLACK); // for coloring the numbers
    TextAttributes white =new TextAttributes(Color.BLACK,Color.BLACK); // for coloring the numbers
    TextAttributes yellow =new TextAttributes(Color.YELLOW,Color.BLACK);
    TextAttributes green =new TextAttributes(Color.GREEN,Color.BLACK);
    TextAttributes red =new TextAttributes(Color.RED,Color.BLACK);
    TextAttributes blue =new TextAttributes(Color.CYAN,Color.BLACK);

    public Game() throws Exception {
        fillTheMaze();
        StartScreen();

        printMaze();
        cn.getTextWindow().setCursorPosition(px, py);
        cn.setTextAttributes(blue);
        cn.getOutputStream().print(human_num);

        klis=new KeyListener() {
            public void keyTyped(KeyEvent e) {}
            public void keyPressed(KeyEvent e) {
                if(keypr==0) {
                    keypr=1;
                    rkey=e.getKeyCode();
                }
            }
            public void keyReleased(KeyEvent e) {}
        };
        cn.getTextWindow().addKeyListener(klis);
        // ----------------------------------------------------


        while(true) {	
            if(keypr==1) {    // if keyboard button pressed
            	removepaths();
                int tempx=px,tempy=py;
               if(human_num!=1) {
                if(rkey==KeyEvent.VK_LEFT) px--;
                else if(rkey==KeyEvent.VK_RIGHT) px++;
                else if(rkey==KeyEvent.VK_UP) py--;
                else if(rkey==KeyEvent.VK_DOWN) py++;
                FindingRednumbers();

                if(isThereAvailable(py, px)) {
                    NumberCollection(py,px);
                    if(gameflag==false) {
                    	cn.getTextWindow().setCursorPosition(0, 25);
                    	cn.setTextAttributes(red);
                    	System.out.println("#######################GAME OVER#######################");
                    	break;
                    }
                    printMaze(); 
                        cn.getTextWindow().setCursorPosition(px, py);
                        cn.setTextAttributes(blue);
                        cn.getOutputStream().print(human_num);
                        num_x=px;
                        num_y=py;
                        t_count=0;
                }
                else {
                    px=tempx;
                    py=tempy;
                }
               }

                if(rkey==KeyEvent.VK_W && !left_backpack.isEmpty() && !right_backpack.isFull()) { // transfer left backpack to right backpack
                    right_backpack.push((Integer) left_backpack.peek());
                    left_backpack.pop();

                }
                if(rkey==KeyEvent.VK_Q && !right_backpack.isEmpty() && !left_backpack.isFull()) {  // transfer right backpack to left backpack
                    left_backpack.push((Integer) right_backpack.peek());
                    right_backpack.pop();

                }
                Match();

                printStack();

                keypr=0;    // last action
            }

            InputList();
            if(TimeCounter%5==0&& TimeCounter!=0) {
        		Timing();
        		//MovementOfRednumbers();
        		Selectingyellownumbers();
        		
        	}
            Location();
            if (human_num==1){
                cn.getTextWindow().setCursorPosition(num_x, num_y);
                cn.setTextAttributes(blue);
                cn.getOutputStream().print(human_num);

                if(t_count%20==0&& t_count!=0) {
                    human_num++;
                    cn.getTextWindow().setCursorPosition(num_x, num_y);
                    cn.setTextAttributes(blue);
                    cn.getOutputStream().print(human_num);


                }
            }

            Thread.sleep(200);TimeCounter++;t_count++;

        }

    }
    private void fillTheMaze() throws IOException {
        maze = new char[23][55];
        File file = new File("maze.txt");
        FileReader fReader = new FileReader(file);
        BufferedReader bReader = new BufferedReader(fReader);
        String line;
        int index = 0;
        while((line = bReader.readLine()) != null) {
            for(int i = 0;i < 55; i++) {
                maze[index][i] = line.charAt(i);
            }
            index++;
        }
        bReader.close();

    }
    private void printMaze()  {
        int cursorx = 0, cursory = 0;
        for(int i = 0;i < maze.length; i++) {
            cursorx = 0;
            for(int j = 0;j < maze[0].length; j++) {
                cn.getTextWindow().setCursorPosition(cursorx, cursory);
               
                if(maze[i][j]==' ') {
                    cn.setTextAttributes(white);
                }
                else if (maze[i][j]>48 && maze[i][j]<=51)
                    cn.setTextAttributes(green);
                else if (maze[i][j]>=52 && maze[i][j]<=54)
                    cn.setTextAttributes(yellow);
                else if (maze[i][j]>54 && maze[i][j]<=57)
                    cn.setTextAttributes(red);
                else cn.setTextAttributes(black);
                cn.getOutputStream().print(maze[i][j]);

                cursorx++;
            }
            cursory++;

            cn.getTextWindow().setCursorPosition(60,20);
            cn.getOutputStream().print("Score:  ");

            cn.getTextWindow().setCursorPosition(60,22);
            cn.getOutputStream().print("Time:  ");

            cn.getTextWindow().setCursorPosition(62,18);
            cn.getOutputStream().print("Q     w");

            cn.getTextWindow().setCursorPosition(60,17);
            cn.getOutputStream().print("Left  Right");

            cn.getTextWindow().setCursorPosition(60,7);
            cn.getOutputStream().print("Backpacks");

            int x=60; int y=8;
            for (int j = 0; j <=8 ; j++) {
                cn.getTextWindow().setCursorPosition(x,y);
                if(j==8){
                    cn.getOutputStream().print("+---+ +---+");
                }
                else  cn.getOutputStream().print("|   | |   |");
                y++;
            }
            cn.getTextWindow().setCursorPosition(60,1);
            cn.getOutputStream().print("Input");

            cn.getTextWindow().setCursorPosition(60,2);
            cn.getOutputStream().print("<<<<<<<<<<");
            cn.getTextWindow().setCursorPosition(60,4);
            cn.getOutputStream().print("<<<<<<<<<<");



        }

    }
    private void removepaths()  {
    
        for(int i = 0;i < maze.length; i++) {
            for(int j = 0;j < maze[0].length; j++) {
                
                if(maze[i][j]=='.')
                	maze[i][j]=' ';

           	}
        }

    }
    private void printStack(){
        int y=16;
        for(int i=15; i>=8;i--) {  // in order to clear the stack part in console before print again
            cn.getTextWindow().setCursorPosition(62,i);
            cn.getOutputStream().print(' ');
            cn.getTextWindow().setCursorPosition(68,i);
            cn.getOutputStream().print(' ');
        }
	cn.setTextAttributes(black);
        while (!left_backpack.isEmpty()){
            cn.getTextWindow().setCursorPosition(62,y-left_backpack.size());
            cn.getOutputStream().print(left_backpack.peek());;
            tempstack.push((Integer) left_backpack.pop());


        }
        while (!tempstack.isEmpty()){
            left_backpack.push((Integer) tempstack.pop());
        }

        while (!right_backpack.isEmpty()){
            cn.getTextWindow().setCursorPosition(68,y-right_backpack.size());
            cn.getOutputStream().print(right_backpack.peek());;
            tempstack.push((Integer) right_backpack.pop());

        }
        while (!tempstack.isEmpty()){
            right_backpack.push((Integer) tempstack.pop());
        }

    }
    private void putBackpack(int collected_num){
        if(!left_backpack.isFull()){
            left_backpack.push(collected_num);
        }
        else{
            left_backpack.pop();
            left_backpack.push(collected_num);
        }

    }
    private void Match() {
        int size=left_backpack.size()-right_backpack.size();
       int matched_num;

        if (size>0){
            for (int i = 0; i <size ; i++) {
                tempstack.push((Integer) left_backpack.pop());
            }
        }
        else if(size<0){
            for (int i = 0; i <Math.abs(size) ; i++) {
                tempstack1.push((Integer) right_backpack.pop());
            }
        }
        while (!left_backpack.isEmpty() || !right_backpack.isEmpty()) {

            if(left_backpack.peek()==right_backpack.peek()){

                matched_num= (int) left_backpack.peek();
                Score(matched_num);
                cn.getTextWindow().setCursorPosition(60,5); //
                cn.getOutputStream().print(matched_num);
                if(human_num==9)human_num=0;
                human_num++;

                left_backpack.pop();
                right_backpack.pop();
            }
            else {

                tempstack.push((Integer) left_backpack.pop());
                tempstack1.push((Integer) right_backpack.pop());
            }
        }
        while (!tempstack.isEmpty()){
            left_backpack.push((Integer) tempstack.pop());
        }
        while (!tempstack1.isEmpty()){
            right_backpack.push((Integer) tempstack1.pop());
        }


    }
    private void Score(int matched_num)
    {
            if(matched_num==1 || matched_num==2 || matched_num==3)score=score+(matched_num);
            else if(matched_num==4 || matched_num==5 || matched_num==6)score=score+(matched_num*5);
            else if(matched_num==7 || matched_num==8 || matched_num==9)score=score+(matched_num*25);
            cn.getTextWindow().setCursorPosition(69,20);
            cn.getOutputStream().print(score);
    }
    private void Timing()
    {

        second++;
        if(second==60) {
            minute++;  second=0;
        }
        if(minute==60) {
            hour++;    minute=0;
        }

        cn.getTextWindow().setCursorPosition(66,22);
        cn.getTextWindow().output(hour+": "+ minute+":"+second+" ");
    }

    public void StartScreen()
    {
        for(int i=0;i<20;i++)
        {
            if(i==0)numberArray[i]=1;  // %5
            else if(i<5)numberArray[i]=2; // %20
            else numberArray[i]=3;        // %75
        }
        for(int i=0;i<25;i++)
        {
            probability = rnd.nextInt(20);
            if(numberArray[probability]==1)  number = rnd.nextInt(3)+7;   // 7,8,9
            else if(numberArray[probability]==2)  number = rnd.nextInt(3)+4;  // 4,5,6
            else if(numberArray[probability]==3)  number = rnd.nextInt(3)+1; // 1,2,3
            input.enqueue(number);

        }
        for(int i=0;i<25;i++)
        {
            while(maze[x][y]!=' ')
            {
                x = rnd.nextInt(maze.length);
                y= rnd.nextInt(maze[0].length);

            }
            int QueueNumber=(int)input.dequeue();
            QueueNumber=QueueNumber+48;
            maze[x][y]= (char)QueueNumber;

        }
        while(maze[py][px]!=' ')
        {
            py = rnd.nextInt(maze.length);
            px= rnd.nextInt(maze[0].length);
        }
    }
    public void InputList()
    {
        while(input.size()!=10)
        {
            probability = rnd.nextInt(20);
            if(numberArray[probability]==1)  number = rnd.nextInt(3)+7;   // 7,8,9
            else if(numberArray[probability]==2)  number = rnd.nextInt(3)+4;  // 4,5,6
            else if(numberArray[probability]==3)  number = rnd.nextInt(3)+1; // 1,2,3
            input.enqueue(number);
        }
        int xCoordinate=60;
        for(int i=0 ;i<input.size();i++)                // print Queue
        {
            cn.getTextWindow().setCursorPosition(xCoordinate,3);
            cn.getTextWindow().output(input.peek()+"");
            input.enqueue(input.dequeue());
            xCoordinate++;
        }
    }
    public void Location()
    {
        while(maze[x][y]!=' ')
        {
            x = rnd.nextInt(maze.length);
            y= rnd.nextInt(maze[0].length);
        }
        if(TimeCounter%25==0 && TimeCounter!=0)
        {
            int QueueNumber=(int)input.dequeue();
            QueueNumber=QueueNumber+48;
            maze[x][y]= (char)QueueNumber;
        }
    }
     public void Selectingyellownumbers() {
    	 int oldRow=-1,oldColumn=-1;
    	for(int i=0;i<23;i++) {
    		for(int j=0;j<55;j++) {
    			if(oldRow!=-1&&oldColumn!=-1) {
    				if(i==oldRow&&j==oldColumn) {
    					oldRow=-1;
    					oldColumn=-1;
    					continue;
    				}
    			}
    			if(maze[i][j]=='4'||maze[i][j]=='5'||maze[i][j]=='6') {
    				Randomlymoving(j,i);
    			}
    			else if(maze[i][j]=='7'||maze[i][j]=='8'||maze[i][j]=='9') {
    				if(maze[i+1][j]=='.') {
    					maze[i+1][j]=maze[i][j];
    					maze[i][j]=' ';
    					oldRow=i+1;
    					oldColumn=j;
    				}
    				else if(maze[i-1][j]=='.') {
    					maze[i-1][j]=maze[i][j];
    					maze[i][j]=' ';
    					oldRow=i-1;
    					oldColumn=j;
    				}
    				else if(maze[i][j+1]=='.') {
    					maze[i][j+1]=maze[i][j];
    					maze[i][j]=' ';
    					oldRow=i;
    					oldColumn=j+1;
    				}
    				else if(maze[i][j-1]=='.') {
    					maze[i][j-1]=maze[i][j];
    					maze[i][j]=' ';
    					oldRow=i;
    					oldColumn=j-1;
    				}
    				
    			}
    			
    		}
    	}
    }
    public void Randomlymoving(int x,int y) {
    	while(true) {
    		int random1=rnd.nextInt(5);
    		
    		if(random1==1&&maze[y-1][x]==' ') {
    			maze[y-1][x]=maze[y][x];
    			maze[y][x]=' ';   			
    			break;
    		}
    		else if(random1==2&&maze[y][x+1]==' ') {
    			maze[y][x+1]=maze[y][x];
    			maze[y][x]=' '; 			
    			break;
    		}
    		else if(random1==3&&maze[y+1][x]==' ') {
    			maze[y+1][x]=maze[y][x];
    			maze[y][x]=' ';    			
    			break;
    		}
    		else if(random1==4&&maze[y][x-1]==' ') {
    			maze[y][x-1]=maze[y][x];
    			maze[y][x]=' ';    			
    			break;
    		}
    		printMaze();
    		cn.getTextWindow().setCursorPosition(px, py);
    		cn.getTextWindow().output(String.valueOf(human_num), blue);
    		printStack();
    	}
    }
    private boolean isThereAvailable(int row , int column) {
        if(maze[row][column] == '#') {
            return false;
        }
        else
            return true;


    }
    private void NumberCollection(int row , int column) {
        boolean control=false;
        if(maze[row][column]<=(char) human_num+48 && maze[row][column]!=' ') {
            control=true;
        }

        if(control==true) {
            int controlNum=(int)maze[row][column]-48;
            putBackpack(controlNum);
            maze[row][column]=' ';

        }
        else if (control==false && maze[row][column]!=' ') {
        	gameflag=false;
        	
        }
        	

    }
    private void BackupMaze() {
    	maze1=new char[23][55];
    	for(int i=0;i<maze.length;i++) {
    		for(int j=0;j<maze[0].length;j++) {
    			maze1[i][j]=maze[i][j];
    		}
    	}
    	
    }
    private void FindingRednumbers() {  	
    	int index=0;
    	int[] coordinate;
    	for(int i=0;i<maze.length;i++) {
    		for(int j=0;j<maze[0].length;j++) {
    			if(maze[i][j]=='7'||maze[i][j]=='8'||maze[i][j]=='9') {
    				BackupMaze();
    				PathFinding(i,j);
    			}	
    		}
    	}
    }
    private void PathFinding(int y,int x) {
    	Queue pathfinding=new Queue(1000);
    	String coordinate=String.valueOf(y);
    	coordinate+=" ";
    	coordinate+=String.valueOf(x);
    	int row;
		int column;
		int lastRow = 0,lastColumn = 0;
		int length = 0;
		String[] coordinate1;
		pathfinding.enqueue(coordinate);
    	while(!pathfinding.isEmpty()) {
    		coordinate1=pathfinding.peek().toString().split(" ");
    		row = Integer.valueOf(coordinate1[0]);
			column = Integer.valueOf(coordinate1[1]);
			if(maze1[row][column + 1] == ' '||maze1[row][column + 1] == '.') {
				coordinate = String.valueOf(row); 
				coordinate += " ";
				coordinate += String.valueOf(column + 1);
				pathfinding.enqueue(coordinate);
				maze1[row][column + 1] = 'r';//right
				if((py==row && px== column + 2)||(py==row+1 && px==column+1)||(py==row-1 && px==column+1)) {
					lastRow = row;
					lastColumn = column + 1;
					break;
				}
			}
			if(maze1[row + 1][column] == ' '||maze1[row + 1][column] == '.') {
				coordinate = String.valueOf(row + 1); 
				coordinate += " ";
				coordinate += String.valueOf(column);
				pathfinding.enqueue(coordinate);
				maze1[row + 1][column] = 'd';//down
				if((py==row + 2 && px== column)||(py==row+1 && px==column+1)||(py==row+1 && px==column-1)) {	
					lastRow = row + 1;
					lastColumn = column;
					break;
				}
			}
			if(maze1[row][column - 1] == ' '||maze1[row][column - 1] == '.') {
				coordinate = String.valueOf(row); 
				coordinate += " ";
				coordinate += String.valueOf(column - 1);
				pathfinding.enqueue(coordinate);
				maze1[row][column - 1] = 'l';//left
				if((py==row && px== column - 2)||(py==row+1 && px==column-1)||(py==row-1 && px==column-1)) {	
					lastRow = row;
					lastColumn = column - 1;
					break;
				}
			}
			if(maze1[row - 1][column] == ' '||maze1[row - 1][column] == '.') {
				coordinate = String.valueOf(row - 1); 
				coordinate += " ";
				coordinate += String.valueOf(column);
				pathfinding.enqueue(coordinate);
				maze1[row - 1][column] = 'u';//up
				if((py==row - 2 && px== column)||(py==row-1 && px==column+1)||(py==row-1 && px==column-1)) {
					lastRow = row - 1;
					lastColumn = column;
					break;
				}
			}
			pathfinding.dequeue();
			length++;
			
    	}
    	for(int i=0;i<length;i++) {
    		if(maze1[lastRow][lastColumn]=='r') {
    			maze[lastRow][lastColumn]='.';
    			lastColumn--;
    			
    		}
    		if(maze1[lastRow][lastColumn]=='d') {
    			maze[lastRow][lastColumn]='.';
    			if(i!=length) {
    				lastRow--;
    			}
    			
    		}
    		if(maze1[lastRow][lastColumn]=='l') {
    			maze[lastRow][lastColumn]='.';
    			if(i!=length) {
    				lastColumn++;
    			}
    			
    		}
    		if(maze1[lastRow][lastColumn]=='u') {
    			maze[lastRow][lastColumn]='.';
    			if(i!=length) {
    				lastRow++;
    			}
    			
    		}
    	}
   

    }

    
}
