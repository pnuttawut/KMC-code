import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Arrays;
import java.util.Random;

public class FTS1 extends Canvas implements Runnable{
	int size = 128;
	int siteWidth = 3;
	int canvasSize = size * siteWidth *3;		
    int[][] s = new int[size][size];
    int [] bi = new int[3];
    int [] bj = new int[3];
    boolean running = false;					// true when simulation is running
    boolean refresh = true;
	Button startButton = new Button(" Start ");
    Button resetButton = new Button(" Reset ");
    Image offScreenImage;						// for double-buffering
	Graphics offScreenGraphics;
	Color vSITEcolor = new Color(230,230,230);		//white smoke_0
	Color noSITEcolor = new Color(190,190,190);		//silver_1
	Color COcolor = new Color(255,102,255);			//hot pink_2
	Color Hcolor = new Color(0,191,255);			//deep sky blue_3
	Color CH3CH3color = new Color(0,128,0);			//green_4
	Color CH3COcolor = new Color(128,128,0);		//olive_5
	Color H2Ocolor = new Color(0,0,255);			//blue_6
	Color CO2color = new Color(112,128,144);		//slate gray_7---
	Color HCOcolor = new Color(148,0,211);			//dark violet_8
	Color CH3CHcolor = new Color(210,105,30);		//chocolate_9---
	Color H3COcolor = new Color(255,165,0);			//orange_10
	Color OHcolor = new Color(0,255,0);				//lime_11
	Color CH2color = new Color(218,165,32);			//golden rod_12---
	Color CH3color = new Color(255,69,0);			//orange red_13---
	Color Ocolor = new Color(255,255,0);			//yellow_14
	Color CH3CH2color = new Color(250,128,114);		//salmon_15
	Color CH3CHOcolor = new Color(255,0,255);		//magenta,fuchsia_16
	Color CH3CH2Ocolor = new Color(199,21,133);		//medium violet red_17
	Color Ccolor = new Color(138,43,226);			//blue violet_18---
	Color CHcolor = new Color(255,0,0);				//red_19
	Color CH2CH2color = new Color(128,0,0);			//Maroon_20
	
	FTS1(){
		Frame wutFrame = new Frame("Cobalt Surface in Fischer-Tropsch Synthesis");	// initialize the GUI...
		wutFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);							// close button exits program
			}
		});
		Panel canvasPanel = new Panel();
		wutFrame.add(canvasPanel);
		canvasPanel.add(this);
		setSize(canvasSize,canvasSize/2);
		Panel controlPanel = new Panel();
		wutFrame.add(controlPanel,BorderLayout.SOUTH);
		controlPanel.add(new Label("     "));			// leave some space
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				running = !running;
				if (running) startButton.setLabel("Pause"); 
				else startButton.setLabel("Resume");
			}
		});
		controlPanel.add(new Label("                                                                                                       "));
		controlPanel.add(startButton);
        controlPanel.add(new Label("       "));			// leave some space
		resetButton.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e){
            refresh = true;
            if (running == false){    
            	   for (int i=0; i<size; i++) {					// initialize the lattice...
            		   for (int j=0; j<size; j++) {
                       s[i][j] = 0;
                       colorSquare(i,j);
            		   }
            	   }
               repaint();
            }
		}
		});
		controlPanel.add(resetButton);
		controlPanel.add(new Label("                                     "));
		wutFrame.pack();
		offScreenImage = createImage(canvasSize,canvasSize);
		offScreenGraphics = offScreenImage.getGraphics();
		wutFrame.setVisible(true);			//we're finally ready to show it!
		Thread t = new Thread(this);		//create a thread to run the simulation
		t.start();
	}

	public static void main(String[] args) {
		new FTS1();
	}
	
	public void run() {
		// Define N : number of time step
		//double N=0;
		int L=size;
		double pCH4 = 0;
		double pC2H6 = 0;
		double pC2H4 = 0;
		double pH2O = 0;
		double pCO2 = 0;
		double r14 = 0;
		double r15 = 0;
		double r16 = 0;
		double aCO = 0;
		double aH2 = 0;
		double dCO = 0;
		double dH2 = 0;
		double RxCH4 = 0;
		double RxC2H6 = 0;
		double RxC2H4 = 0;
		double RxH2O = 0;
		double RxCO2 = 0;
		double R14 = 0;
		double R15 = 0;
		double R16 = 0;
		double RaCO = 0;
		double RaH2 = 0;
		double RdCO = 0;
		double RdH2 = 0;
		double T = 493;			//K
		double P = 15;			//bar
		double H2pCO = 2.1;
		double y = 1/(H2pCO+1);
		double PCO = y*P;
		double PH2 = (1-y)*P;
		double t = 0;
		double delt = 0;
		System.out.println("Temp: " + T + " K, Pres: " + P + " bar, H2/CO: " + H2pCO);
		
		// Pre-exponential factors
		double A1 = 4.20E+04;
		double A2 = 2.20E+06;
		double A3 = 1.00E+13;
		double A4 = 1.90E+13;
		double A5 = 1.00E+13;
		double A6 = 1.00E+13;
		double A7 = 1.13E+12;
		double A8 = 1.00E+13;
		double A9 = 1.70E+14;
		double A10 = 2.69E+13;
		double A11 = 2.60E+13;
		double A12 = 2.60E+13;
		double A13 = 1.65E+12;
		double A14 = 5.50E+11;
		double A15 = 4.50E+12;
		double A16 = 5.50E+11;
		double A17 = 2.10E+14;
		double A18 = 6.74E+11;
		double A19 = 1.00E+13;
		double A20 = 1.10E+13;
		double A21 = 1.00E+13;
		double A22 = 1.00E+13;
		double A23 = 1.10E+14;
		double A24 = 7.70E+12;
		double A25 = 1.00E+13;
		
		// Activation Energies
		double Ea1 = 0.0;			//A1....CO(g) + * => CO*
		double Ea2 = 14.1;			//A2....H2 + 2* => 2H*
		double Ea3 = 111.6;			//D3....CO* => CO(g) + *
		double Ea4 = 75.0;			//D4....2H* => H2(g) + 2*
		double Ea5 = 43.8;			//D5....CH3CH3** => C2H6(g) + 2*
		double Ea6 = 55.3;			//D6....CH2CH2** => C2H4(g) + 2*
		double Ea7 = 51.0;			//D7....H2O* => H2O(g) + *
		double Ea8 = 27.7;			//D8i...CO2** => CO2(g) + 2*
		double Ea9 = 87.0;			//R9....CO* + H* => C* + OH*
		double Ea10 = 18.0;			//R10i..C* + H* => CH* + *
		double Ea11 = 59.6;			//R11...CH* + H* => CH2* + *
		double Ea12 = 34.0;			//R12...CH2* + H* => CH3* + *
		double Ea13 = 30.8;			//R13...CH3* + H* => CH4(g) + 2*
		double Ea14 = 25.0;			//R14...CH3* + CH* => CH3CH* + *
		double Ea15 = 5.4;			//R15i..CH3* + CH2* => CH3CH2* + *
		double Ea16 = 20.8;			//R16...CH3* + CH3* => CH3CH3* + *
		double Ea17 = 37.4;			//R17i..CH3CH* + H* => CH3CH2* + *
		double Ea18 = 29.1;			//R18...CH3CH2* + H* => CH3CH3**
		double Ea19 = 42.0;			//R19...CH3CH2* + 2* => CH2CH2** + H*
		double Ea20 = 36.5;			//R20...OH* + H* => H2O* + *
		double Ea21 = 90.9;			//R21...H2O* + * => OH* + H*
		double Ea22 = 57.1;			//R22...OH* + * => O* + H*
		double Ea23 = 108.4;		//R23...O* + H* => OH*
		double Ea24 = 62.2;			//R24...CO* + O* => CO2**
		double Ea25 = 47.7;			//R25...CO2** => CO* + O*
		
		//Rates constants
		double k1 = A1*Math.exp(-Ea1*1000/(8.314*T))*PCO;
		double k2 = A2*Math.exp(-Ea2*1000/(8.314*T))*PH2;
		double k3 = A3*Math.exp(-Ea3*1000/(8.314*T));
		double k4 = A4*Math.exp(-Ea4*1000/(8.314*T));
		double k5 = A5*Math.exp(-Ea5*1000/(8.314*T));
		double k6 = A6*Math.exp(-Ea6*1000/(8.314*T));
		double k7 = A7*Math.exp(-Ea7*1000/(8.314*T));
		double k8 = A8*Math.exp(-Ea8*1000/(8.314*T))*0;
		double k9 = A9*Math.exp(-Ea9*1000/(8.314*T));
		double k10 = A10*Math.exp(-Ea10*1000/(8.314*T))*0;
		double k11 = A11*Math.exp(-Ea11*1000/(8.314*T));
		double k12 = A12*Math.exp(-Ea12*1000/(8.314*T));
		double k13 = A13*Math.exp(-Ea13*1000/(8.314*T));
		double k14 = A14*Math.exp(-Ea14*1000/(8.314*T));
		double k15 = A15*Math.exp(-Ea15*1000/(8.314*T))*0;
		double k16 = A16*Math.exp(-Ea16*1000/(8.314*T));
		double k17 = A17*Math.exp(-Ea17*1000/(8.314*T))*0;
		double k18 = A18*Math.exp(-Ea18*1000/(8.314*T));
		double k19 = A19*Math.exp(-Ea19*1000/(8.314*T));
		double k20 = A20*Math.exp(-Ea20*1000/(8.314*T));
		double k21 = A21*Math.exp(-Ea21*1000/(8.314*T));
		double k22 = A22*Math.exp(-Ea22*1000/(8.314*T));
		double k23 = A23*Math.exp(-Ea23*1000/(8.314*T));
		double k24 = A24*Math.exp(-Ea24*1000/(8.314*T));
		double k25 = A25*Math.exp(-Ea25*1000/(8.314*T))*0;
		double ktot =k1+k2+k3+k4+k5+k6+k7+k8+k9+k10+k11+k12+k13+k14+k15+k16+k17+k18+k19+k20+k21+k22+k23+k24+k25;
		//Probabilities
		double p1 = k1/ktot;
		double p2 = k2/ktot;
		double p3 = k3/ktot;
		double p4 = k4/ktot;
		double p5 = k5/ktot;
		double p6 = k6/ktot;
		double p7 = k7/ktot;
		double p8 = k8/ktot;
		double p9 = k9/ktot;
		double p10 = k10/ktot;
		double p11 = k11/ktot;
		double p12 = k12/ktot;
		double p13 = k13/ktot;
		double p14 = k14/ktot;
		double p15 = k15/ktot;
		double p16 = k16/ktot;
		double p17 = k17/ktot;
		double p18 = k18/ktot;
		double p19 = k19/ktot;
		double p20 = k20/ktot;
		double p21 = k21/ktot;
		double p22 = k22/ktot;
		double p23 = k23/ktot;
		double p24 = k24/ktot;
		double p25 = k25/ktot;
		double ptot=p1+p2+p3+p4+p5+p6+p7+p8+p9+p10+p11+p12+p13+p14+p15+p16+p17+p18+p19+p20+p21+p22+p23+p24+p25;
		//Check!!
		System.out.println("CHECK...,ktot: " + ktot + ", ptot: " + ptot);
		System.out.println("Ea1: "+Ea1 + ", Ea2: "+Ea2 + ", Ea3: "+Ea3 + ", Ea4: "+Ea4 + ", Ea5: "+Ea5 + ", Ea6: "+Ea6 + ", Ea7: "+Ea7 + ", Ea8: "+Ea8 + ", Ea9: "+Ea9 + ", Ea10: "+Ea10 + ", Ea11: "+Ea1 + ", Ea12: "+Ea12 + ", Ea13: "+Ea13 + ", Ea14: "+Ea14 + ", Ea15: "+Ea15 + ", Ea16: "+Ea16 + ", Ea17: "+Ea17 + ", Ea18: "+Ea18 + ", Ea19: "+Ea19 + ", Ea20: "+Ea20 + ", Ea21: "+Ea21 + ", Ea22: "+Ea22 + ", Ea23: "+Ea23 + ", Ea24: "+Ea24 + ", Ea25: "+Ea25);
		System.out.println("k1: "+k1 + ", k2: "+k2 + ", k3: "+k3 + ", k4: "+k4 + ", k5: "+k5 + ", k6: "+k6 + ", k7: "+k7 + ", k8: "+k8 + ", k9: "+k9 + ", k10: "+k10 + ", k11: "+k11 + ", k12: "+k12 + ", k13: "+k13 + ", k14: "+k14 + ", k15: "+k15 + ", k16: "+k16 + ", k17: "+k17 + ", k18: "+k18 + ", k19: "+k19 + ", k20: "+k20 + ", k21: "+k21 + ", k22: "+k22 + ", k23: "+k23 + ", k24: "+k24 + ", k25: "+k25);
		// Refresh simulation
		if(refresh == true){
			for (int j=0; j<size; j++) { // initialize the lattice...
                for (int i=0; i<size; i++){
                    s[i][j] = 0;
                    colorSquare(i,j);
                }
            repaint();
            refresh = false;
            }
		}
		while(true){
			if(running){
			//Update time step
				//N=N+1;
			// Reset production rate
				double Z0=0;
				double Z2=0;
				double Z3=0;
				double Z4=0;
				double Z5=0;
				double Z6=0;
				double Z7=0;
				double Z8=0;
				double Z9=0;
				double Z10=0;
				double Z11=0;
				double Z12=0;
				double Z13=0;
				double Z14=0;
				double Z15=0;
				double Z16=0;
				double Z17=0;
				double Z18=0;
				double Z19=0;
				double Z20=0;
		//Running processes
				for (int step=0; step<(L*L*1000); step++) {		// adjust number of steps as desired
				//Update time
					double rn = Math.random();
					delt = Math.log(rn)/((L*L)*ktot);
					t = t-delt;
				//Select sites randomly
					int i = 0;
					int j = 0;
					int ib = 0;
					int jb = 0;
					i = (int) (Math.random()*size);		//column
				    j = (int) (Math.random()*size);		//row	
				//Select processes randomly
					double p = Math.random();
			//instantaneous reaction
				//D8i...CO2** => CO2(g) + 2*
					if(s[i][j] == 7){
						int c = 0;
				        if(s[(i+1)&(size-1)][j] == 7){
				        	c++;
							ib = (i+1)&(size-1); 
							jb = j;
						}
				        else if(s[(i+1)&(size-1)][(j+1)&(size-1)] == 7){
							c++;
							ib = (i+1)&(size-1); 
							jb = (j+1)&(size-1);
						}
				        else if(s[i][(j+1)&(size-1)] == 7){
							c++;
							ib = i; 
							jb = (j+1)&(size-1);
						}
				        else if(s[(i-1)&(size-1)][j] == 7){
							c++;
							ib = (i-1)&(size-1); 
							jb = j;
						}
				        else if(s[(i-1)&(size-1)][(j-1)&(size-1)] == 7){
							c++;
							ib = (i-1)&(size-1); 
							jb = (j-1)&(size-1);
						}
				        else if(s[i][(j-1)&(size-1)] == 7){
							c++;
							ib = i; 
							jb = (j-1)&(size-1);
						}
						if (c > 0){
							s[i][j] = 0;
                            colorSquare(i,j);
							s[ib][jb] = 0;
                            colorSquare(ib,jb);
                            pCO2++;
                        }
					}
				//R10i...C* + H* => CH* + *
					if(s[i][j] == 18){
						int c = 0;
						int[] arr = {1, 2, 3, 4, 5, 6};
				        int n = arr.length;
				        random (arr, n);
				        for(int k=0;k<n;k++) {
				        	if((arr[k]==1)&&(s[(i+1)&(size-1)][j] == 3)){
				        		c++;
								ib = (i+1)&(size-1); 
								jb = j;
								break;
							}
				        	else if((arr[k]==2)&&(s[(i+1)&(size-1)][(j+1)&(size-1)] == 3)){
								c++;
								ib = (i+1)&(size-1); 
								jb = (j+1)&(size-1);
								break;
							}
				        	else if((arr[k]==3)&&(s[i][(j+1)&(size-1)] == 3)){
								c++;
								ib = i; 
								jb = (j+1)&(size-1);
								break;
							}
				        	else if((arr[k]==4)&&(s[(i-1)&(size-1)][j] == 3)){
								c++;
								ib = (i-1)&(size-1); 
								jb = j;
								break;
							}
				        	else if((arr[k]==5)&&(s[(i-1)&(size-1)][(j-1)&(size-1)] == 3)){
								c++;
								ib = (i-1)&(size-1); 
								jb = (j-1)&(size-1);
								break;
							}
				        	else if((arr[k]==6)&&(s[i][(j-1)&(size-1)] == 3)){
								c++;
								ib = i; 
								jb = (j-1)&(size-1);
								break;
							}
				        }
				        if (c > 0){
                            double l = Math.random();
							if (l < 0.5){
								s[i][j] = 19;
                                colorSquare(i,j);
								s[ib][jb] = 0;
                                colorSquare(ib,jb);
							}
							else{
								s[i][j] = 0;
                                colorSquare(i,j);
								s[ib][jb] = 19;
                                colorSquare(ib,jb);
							}
                        }
					}
					else if(s[i][j] == 3){
						int c = 0;
						int[] arr = {1, 2, 3, 4, 5, 6};
				        int n = arr.length;
				        random (arr, n);
				        for(int k=0;k<n;k++) {
				        	if((arr[k]==1)&&(s[(i+1)&(size-1)][j] == 18)){
				        		c++;
								ib = (i+1)&(size-1); 
								jb = j;
								break;
							}
				        	else if((arr[k]==2)&&(s[(i+1)&(size-1)][(j+1)&(size-1)] == 18)){
								c++;
								ib = (i+1)&(size-1); 
								jb = (j+1)&(size-1);
								break;
							}
				        	else if((arr[k]==3)&&(s[i][(j+1)&(size-1)] == 18)){
								c++;
								ib = i; 
								jb = (j+1)&(size-1);
								break;
							}
				        	else if((arr[k]==4)&&(s[(i-1)&(size-1)][j] == 18)){
								c++;
								ib = (i-1)&(size-1); 
								jb = j;
								break;
							}
				        	else if((arr[k]==5)&&(s[(i-1)&(size-1)][(j-1)&(size-1)] == 18)){
								c++;
								ib = (i-1)&(size-1); 
								jb = (j-1)&(size-1);
								break;
							}
				        	else if((arr[k]==6)&&(s[i][(j-1)&(size-1)] == 18)){
								c++;
								ib = i; 
								jb = (j-1)&(size-1);
								break;
							}
				        }
						if (c > 0){
                            double l = Math.random();
							if (l < 0.5){
								s[i][j] = 19;
                                colorSquare(i,j);
								s[ib][jb] = 0;
                                colorSquare(ib,jb);
							}
							else{
								s[i][j] = 0;
                                colorSquare(i,j);
								s[ib][jb] = 19;
                                colorSquare(ib,jb);
							}
                        }
					}
				//R15i...CH3* + CH2* => CH3CH2* + *
					if(s[i][j] == 13){
						int c = 0;
						if(s[(i+1)&(size-1)][j] == 12){
				        	c++;
							ib = (i+1)&(size-1); 
							jb = j;
						}
				        else if(s[(i+1)&(size-1)][(j+1)&(size-1)] == 12){
							c++;
							ib = (i+1)&(size-1); 
							jb = (j+1)&(size-1);
						}
				        else if(s[i][(j+1)&(size-1)] == 12){
							c++;
							ib = i; 
							jb = (j+1)&(size-1);
						}
				        else if(s[(i-1)&(size-1)][j] == 12){
							c++;
							ib = (i-1)&(size-1); 
							jb = j;
						}
				        else if(s[(i-1)&(size-1)][(j-1)&(size-1)] == 12){
							c++;
							ib = (i-1)&(size-1); 
							jb = (j-1)&(size-1);
						}
				        else if(s[i][(j-1)&(size-1)] == 12){
							c++;
							ib = i; 
							jb = (j-1)&(size-1);
						}
						if (c > 0){
                            double r = Math.random();
							if (r < 0.5){
								s[i][j] = 15;
                                colorSquare(i,j);
								s[ib][jb] = 0;
                                colorSquare(ib,jb);
							}
							else{
								s[i][j] = 0;
                                colorSquare(i,j);
								s[ib][jb] = 15;
                                colorSquare(ib,jb);
							}
							r15++;
                        }
					}
					else if(s[i][j] == 12){
						int c = 0;
						if(s[(i+1)&(size-1)][j] == 13){
				        	c++;
							ib = (i+1)&(size-1); 
							jb = j;
						}
				        else if(s[(i+1)&(size-1)][(j+1)&(size-1)] == 13){
							c++;
							ib = (i+1)&(size-1); 
							jb = (j+1)&(size-1);
						}
				        else if(s[i][(j+1)&(size-1)] == 13){
							c++;
							ib = i; 
							jb = (j+1)&(size-1);
						}
				        else if(s[(i-1)&(size-1)][j] == 13){
							c++;
							ib = (i-1)&(size-1); 
							jb = j;
						}
				        else if(s[(i-1)&(size-1)][(j-1)&(size-1)] == 13){
							c++;
							ib = (i-1)&(size-1); 
							jb = (j-1)&(size-1);
						}
				        else if(s[i][(j-1)&(size-1)] == 13){
							c++;
							ib = i; 
							jb = (j-1)&(size-1);
						}
						if (c > 0){
                            double r = Math.random();
							if (r < 0.5){
								s[i][j] = 15;
                                colorSquare(i,j);
								s[ib][jb] = 0;
                                colorSquare(ib,jb);
							}
							else{
								s[i][j] = 0;
                                colorSquare(i,j);
								s[ib][jb] = 15;
                                colorSquare(ib,jb);
							}
							r15++;
                        }
					}
				//R17i...CH3CH* + H* => CH3CH2* + *
					if(s[i][j] == 9){
						int c = 0;
						int[] arr = {1, 2, 3, 4, 5, 6};
				        int n = arr.length;
				        random (arr, n);
				        for(int k=0;k<n;k++) {
				        	if((arr[k]==1)&&(s[(i+1)&(size-1)][j] == 3)){
				        		c++;
								ib = (i+1)&(size-1); 
								jb = j;
								break;
							}
				        	else if((arr[k]==2)&&(s[(i+1)&(size-1)][(j+1)&(size-1)] == 3)){
								c++;
								ib = (i+1)&(size-1); 
								jb = (j+1)&(size-1);
								break;
							}
				        	else if((arr[k]==3)&&(s[i][(j+1)&(size-1)] == 3)){
								c++;
								ib = i; 
								jb = (j+1)&(size-1);
								break;
							}
				        	else if((arr[k]==4)&&(s[(i-1)&(size-1)][j] == 3)){
								c++;
								ib = (i-1)&(size-1); 
								jb = j;
								break;
							}
				        	else if((arr[k]==5)&&(s[(i-1)&(size-1)][(j-1)&(size-1)] == 3)){
								c++;
								ib = (i-1)&(size-1); 
								jb = (j-1)&(size-1);
								break;
							}
				        	else if((arr[k]==6)&&(s[i][(j-1)&(size-1)] == 3)){
								c++;
								ib = i; 
								jb = (j-1)&(size-1);
								break;
							}
				        }
				        if (c > 0){
                            double l = Math.random();
							if (l < 0.5){
								s[i][j] = 15;
                                colorSquare(i,j);
								s[ib][jb] = 0;
                                colorSquare(ib,jb);
							}
							else{
								s[i][j] = 0;
                                colorSquare(i,j);
								s[ib][jb] = 15;
                                colorSquare(ib,jb);
							}
                        }
					}
					else if(s[i][j] == 3){
						int c = 0;
						if(s[(i+1)&(size-1)][j] == 9){
				        	c++;
							ib = (i+1)&(size-1); 
							jb = j;
						}
				        else if(s[(i+1)&(size-1)][(j+1)&(size-1)] == 9){
							c++;
							ib = (i+1)&(size-1); 
							jb = (j+1)&(size-1);
						}
				        else if(s[i][(j+1)&(size-1)] == 9){
							c++;
							ib = i; 
							jb = (j+1)&(size-1);
						}
				        else if(s[(i-1)&(size-1)][j] == 9){
							c++;
							ib = (i-1)&(size-1); 
							jb = j;
						}
				        else if(s[(i-1)&(size-1)][(j-1)&(size-1)] == 9){
							c++;
							ib = (i-1)&(size-1); 
							jb = (j-1)&(size-1);
						}
				        else if(s[i][(j-1)&(size-1)] == 9){
							c++;
							ib = i; 
							jb = (j-1)&(size-1);
						}
						if (c > 0){
                            double r = Math.random();
							if (r < 0.5){
								s[i][j] = 15;
                                colorSquare(i,j);
								s[ib][jb] = 0;
                                colorSquare(ib,jb);
							}
							else{
								s[i][j] = 0;
                                colorSquare(i,j);
								s[ib][jb] = 15;
                                colorSquare(ib,jb);
							}
                        }
					}
				//A1...CO(g) + * => CO*
					if(p<p1){
						if(s[i][j] == 0){
							s[i][j] = 2;
							colorSquare(i,j);
							aCO++;
						}	
					}
				//A2...H2 + 2* => 2H*
					else if((p>=p1)&&(p<p1+p2)){
						if(s[i][j] == 0){
							int c = 0;
							int[] arr = {1, 2, 3, 4, 5, 6};
					        int n = arr.length;
					        random (arr, n);
					        for(int k=0;k<n;k++) {
					        	if((arr[k]==1)&&(s[(i+1)&(size-1)][j] == 0)){
					        		c++;
									ib = (i+1)&(size-1); 
									jb = j;
									break;
								}
					        	else if((arr[k]==2)&&(s[(i+1)&(size-1)][(j+1)&(size-1)] == 0)){
									c++;
									ib = (i+1)&(size-1); 
									jb = (j+1)&(size-1);
									break;
								}
					        	else if((arr[k]==3)&&(s[i][(j+1)&(size-1)] == 0)){
									c++;
									ib = i; 
									jb = (j+1)&(size-1);
									break;
								}
					        	else if((arr[k]==4)&&(s[(i-1)&(size-1)][j] == 0)){
									c++;
									ib = (i-1)&(size-1); 
									jb = j;
									break;
								}
					        	else if((arr[k]==5)&&(s[(i-1)&(size-1)][(j-1)&(size-1)] == 0)){
									c++;
									ib = (i-1)&(size-1); 
									jb = (j-1)&(size-1);
									break;
								}
					        	else if((arr[k]==6)&&(s[i][(j-1)&(size-1)] == 0)){
									c++;
									ib = i; 
									jb = (j-1)&(size-1);
									break;
								}
					        }
					        if (c > 0){
					        	s[i][j] = 3;
					        	colorSquare(i,j);
					        	s[ib][jb] = 3;
					        	colorSquare(ib,jb);
					        	aH2++;
					        }
						}
					}
				//D3...CO* => CO(g) + *
					else if((p>=p1+p2)&&(p<p1+p2+p3)){
						if(s[i][j] == 2){
							s[i][j] = 0;
							colorSquare(i,j);
							dCO++;
						}
					}
				//D4...2H* => H2(g) + 2*
					else if((p>=p1+p2+p3)&&(p<p1+p2+p3+p4)){
						if(s[i][j] == 3){
							int c = 0;
							int[] arr = {1, 2, 3, 4, 5, 6};
					        int n = arr.length;
					        random (arr, n);
					        for(int k=0;k<n;k++) {
					        	if((arr[k]==1)&&(s[(i+1)&(size-1)][j] == 3)){
					        		c++;
									ib = (i+1)&(size-1); 
									jb = j;
									break;
								}
					        	else if((arr[k]==2)&&(s[(i+1)&(size-1)][(j+1)&(size-1)] == 3)){
									c++;
									ib = (i+1)&(size-1); 
									jb = (j+1)&(size-1);
									break;
								}
					        	else if((arr[k]==3)&&(s[i][(j+1)&(size-1)] == 3)){
									c++;
									ib = i; 
									jb = (j+1)&(size-1);
									break;
								}
					        	else if((arr[k]==4)&&(s[(i-1)&(size-1)][j] == 3)){
									c++;
									ib = (i-1)&(size-1); 
									jb = j;
									break;
								}
					        	else if((arr[k]==5)&&(s[(i-1)&(size-1)][(j-1)&(size-1)] == 3)){
									c++;
									ib = (i-1)&(size-1); 
									jb = (j-1)&(size-1);
									break;
								}
					        	else if((arr[k]==6)&&(s[i][(j-1)&(size-1)] == 3)){
									c++;
									ib = i; 
									jb = (j-1)&(size-1);
									break;
								}
					        }
					        if(c > 0){
					        	s[i][j] = 0;
					        	colorSquare(i,j);
					        	s[ib][jb] = 0;
					        	colorSquare(ib,jb);
					        	dH2++;
					        }
						}
					}
				//D5...CH3CH3** => C2H6(g) + 2*
					else if((p>=p1+p2+p3+p4)&&(p<p1+p2+p3+p4+p5)){
						if(s[i][j] == 4){
							int c = 0;
					        if(s[(i+1)&(size-1)][j] == 4){
					        	c++;
								ib = (i+1)&(size-1); 
								jb = j;
							}
					        else if(s[(i+1)&(size-1)][(j+1)&(size-1)] == 4){
								c++;
								ib = (i+1)&(size-1); 
								jb = (j+1)&(size-1);
							}
					        else if(s[i][(j+1)&(size-1)] == 4){
								c++;
								ib = i; 
								jb = (j+1)&(size-1);
							}
					        else if(s[(i-1)&(size-1)][j] == 4){
								c++;
								ib = (i-1)&(size-1); 
								jb = j;
							}
					        else if(s[(i-1)&(size-1)][(j-1)&(size-1)] == 4){
								c++;
								ib = (i-1)&(size-1); 
								jb = (j-1)&(size-1);
							}
					        else if(s[i][(j-1)&(size-1)] == 4){
								c++;
								ib = i; 
								jb = (j-1)&(size-1);
							}
							if (c > 0){
								s[i][j] = 0;
	                            colorSquare(i,j);
								s[ib][jb] = 0;
	                            colorSquare(ib,jb);
	                            pC2H6++;
	                        }
						}
					}
				//D6....CH2CH2** => C2H4(g) + 2*
					else if((p>=p1+p2+p3+p4+p5)&&(p<p1+p2+p3+p4+p5+p6)){
						if(s[i][j] == 20){
							int c = 0;
					        if(s[(i+1)&(size-1)][j] == 20){
					        	c++;
								ib = (i+1)&(size-1); 
								jb = j;
							}
					        else if(s[(i+1)&(size-1)][(j+1)&(size-1)] == 20){
								c++;
								ib = (i+1)&(size-1); 
								jb = (j+1)&(size-1);
							}
					        else if(s[i][(j+1)&(size-1)] == 20){
								c++;
								ib = i; 
								jb = (j+1)&(size-1);
							}
					        else if(s[(i-1)&(size-1)][j] == 20){
								c++;
								ib = (i-1)&(size-1); 
								jb = j;
							}
					        else if(s[(i-1)&(size-1)][(j-1)&(size-1)] == 20){
								c++;
								ib = (i-1)&(size-1); 
								jb = (j-1)&(size-1);
							}
					        else if(s[i][(j-1)&(size-1)] == 20){
								c++;
								ib = i; 
								jb = (j-1)&(size-1);
							}
							if (c > 0){
								s[i][j] = 0;
	                            colorSquare(i,j);
								s[ib][jb] = 0;
	                            colorSquare(ib,jb);
	                            pC2H4++;
	                        }
						}
					}
				//D7...H2O* => H2O(g) + *
					else if((p>=p1+p2+p3+p4+p5+p6)&&(p<p1+p2+p3+p4+p5+p6+p7)){
						if(s[i][j] == 6){
							s[i][j] = 0;
							colorSquare(i,j);
							pH2O++;
						}
					}
				//R9...CO* + H* => C* + OH*
					else if((p>=p1+p2+p3+p4+p5+p6+p7)&&(p<p1+p2+p3+p4+p5+p6+p7+p9)){
						if(s[i][j] == 2){
							int c = 0;
							int[] arr = {1, 2, 3, 4, 5, 6};
					        int n = arr.length;
					        random (arr, n);
					        for(int k=0;k<n;k++) {
					        	if((arr[k]==1)&&(s[(i+1)&(size-1)][j] == 3)){
					        		c++;
									ib = (i+1)&(size-1); 
									jb = j;
									break;
								}
					        	else if((arr[k]==2)&&(s[(i+1)&(size-1)][(j+1)&(size-1)] == 3)){
									c++;
									ib = (i+1)&(size-1); 
									jb = (j+1)&(size-1);
									break;
								}
					        	else if((arr[k]==3)&&(s[i][(j+1)&(size-1)] == 3)){
									c++;
									ib = i; 
									jb = (j+1)&(size-1);
									break;
								}
					        	else if((arr[k]==4)&&(s[(i-1)&(size-1)][j] == 3)){
									c++;
									ib = (i-1)&(size-1); 
									jb = j;
									break;
								}
					        	else if((arr[k]==5)&&(s[(i-1)&(size-1)][(j-1)&(size-1)] == 3)){
									c++;
									ib = (i-1)&(size-1); 
									jb = (j-1)&(size-1);
									break;
								}
					        	else if((arr[k]==6)&&(s[i][(j-1)&(size-1)] == 3)){
									c++;
									ib = i; 
									jb = (j-1)&(size-1);
									break;
								}
					        }
					        if (c > 0){
	                            double l = Math.random();
								if (l < 0.5){
									s[i][j] = 18;
	                                colorSquare(i,j);
									s[ib][jb] = 11;
	                                colorSquare(ib,jb);
								}
								else{
									s[i][j] = 11;
	                                colorSquare(i,j);
									s[ib][jb] = 18;
	                                colorSquare(ib,jb);
								}
	                        }
						}
						else if(s[i][j] == 3){
							int c = 0;
							int[] arr = {1, 2, 3, 4, 5, 6};
					        int n = arr.length;
					        random (arr, n);
					        for(int k=0;k<n;k++) {
					        	if((arr[k]==1)&&(s[(i+1)&(size-1)][j] == 2)){
					        		c++;
									ib = (i+1)&(size-1); 
									jb = j;
									break;
								}
					        	else if((arr[k]==2)&&(s[(i+1)&(size-1)][(j+1)&(size-1)] == 2)){
									c++;
									ib = (i+1)&(size-1); 
									jb = (j+1)&(size-1);
									break;
								}
					        	else if((arr[k]==3)&&(s[i][(j+1)&(size-1)] == 2)){
									c++;
									ib = i; 
									jb = (j+1)&(size-1);
									break;
								}
					        	else if((arr[k]==4)&&(s[(i-1)&(size-1)][j] == 2)){
									c++;
									ib = (i-1)&(size-1); 
									jb = j;
									break;
								}
					        	else if((arr[k]==5)&&(s[(i-1)&(size-1)][(j-1)&(size-1)] == 2)){
									c++;
									ib = (i-1)&(size-1); 
									jb = (j-1)&(size-1);
									break;
								}
					        	else if((arr[k]==6)&&(s[i][(j-1)&(size-1)] == 2)){
									c++;
									ib = i; 
									jb = (j-1)&(size-1);
									break;
								}
					        }
							if (c > 0){
	                            double l = Math.random();
								if (l < 0.5){
									s[i][j] = 18;
	                                colorSquare(i,j);
									s[ib][jb] = 11;
	                                colorSquare(ib,jb);
								}
								else{
									s[i][j] = 11;
	                                colorSquare(i,j);
									s[ib][jb] = 18;
	                                colorSquare(ib,jb);
								}
	                        }
						}
					}
				//R11...CH* + H* => CH2* + *
					else if((p>=p1+p2+p3+p4+p5+p6+p7+p9)&&(p<p1+p2+p3+p4+p5+p6+p7+p9+p11)){
						if(s[i][j] == 19){
							int c = 0;
							int[] arr = {1, 2, 3, 4, 5, 6};
							int n = arr.length;
							random (arr, n);
							for(int k=0;k<n;k++) {
								if((arr[k]==1)&&(s[(i+1)&(size-1)][j] == 3)){
									c++;
									ib = (i+1)&(size-1); 
									jb = j;
									break;
								}
								else if((arr[k]==2)&&(s[(i+1)&(size-1)][(j+1)&(size-1)] == 3)){
									c++;
									ib = (i+1)&(size-1); 
									jb = (j+1)&(size-1);
									break;
								}
								else if((arr[k]==3)&&(s[i][(j+1)&(size-1)] == 3)){
									c++;
									ib = i; 
									jb = (j+1)&(size-1);
									break;
								}
								else if((arr[k]==4)&&(s[(i-1)&(size-1)][j] == 3)){
									c++;
									ib = (i-1)&(size-1); 
									jb = j;
									break;
								}
								else if((arr[k]==5)&&(s[(i-1)&(size-1)][(j-1)&(size-1)] == 3)){
									c++;
									ib = (i-1)&(size-1); 
									jb = (j-1)&(size-1);
									break;
								}
								else if((arr[k]==6)&&(s[i][(j-1)&(size-1)] == 3)){
									c++;
									ib = i; 
									jb = (j-1)&(size-1);
									break;
								}
							}
							if (c > 0){
								double l = Math.random();
								if (l < 0.5){
									s[i][j] = 12;
									colorSquare(i,j);
									s[ib][jb] = 0;
									colorSquare(ib,jb);
								}
								else{
									s[i][j] = 0;
									colorSquare(i,j);
									s[ib][jb] = 12;
									colorSquare(ib,jb);
								}
							}
						}
						else if(s[i][j] == 3){
							int c = 0;
							int[] arr = {1, 2, 3, 4, 5, 6};
							int n = arr.length;
							random (arr, n);
							for(int k=0;k<n;k++) {
								if((arr[k]==1)&&(s[(i+1)&(size-1)][j] == 19)){
									c++;
									ib = (i+1)&(size-1); 
									jb = j;
									break;
								}
								else if((arr[k]==2)&&(s[(i+1)&(size-1)][(j+1)&(size-1)] == 19)){
									c++;
									ib = (i+1)&(size-1); 
									jb = (j+1)&(size-1);
									break;
								}
								else if((arr[k]==3)&&(s[i][(j+1)&(size-1)] == 19)){
									c++;
									ib = i; 
									jb = (j+1)&(size-1);
									break;
								}
								else if((arr[k]==4)&&(s[(i-1)&(size-1)][j] == 19)){
									c++;
									ib = (i-1)&(size-1); 
									jb = j;
									break;
								}
								else if((arr[k]==5)&&(s[(i-1)&(size-1)][(j-1)&(size-1)] == 19)){
									c++;
									ib = (i-1)&(size-1); 
									jb = (j-1)&(size-1);
									break;
								}
								else if((arr[k]==6)&&(s[i][(j-1)&(size-1)] == 19)){
									c++;
									ib = i; 
									jb = (j-1)&(size-1);
									break;
								}
							}
							if (c > 0){
								double l = Math.random();
								if (l < 0.5){
									s[i][j] = 12;
									colorSquare(i,j);
									s[ib][jb] = 0;
									colorSquare(ib,jb);
								}
								else{
									s[i][j] = 0;
									colorSquare(i,j);
									s[ib][jb] = 12;
									colorSquare(ib,jb);
								}
							}
						}
					}
				//R12...CH2* + H* => CH3* + *
					else if((p>=p1+p2+p3+p4+p5+p6+p7+p9+p11)&&(p<p1+p2+p3+p4+p5+p6+p7+p9+p11+p12)){
						if(s[i][j] == 12){
							int c = 0;
							int[] arr = {1, 2, 3, 4, 5, 6};
					        int n = arr.length;
					        random (arr, n);
					        for(int k=0;k<n;k++) {
					        	if((arr[k]==1)&&(s[(i+1)&(size-1)][j] == 3)){
					        		c++;
									ib = (i+1)&(size-1); 
									jb = j;
									break;
								}
					        	else if((arr[k]==2)&&(s[(i+1)&(size-1)][(j+1)&(size-1)] == 3)){
									c++;
									ib = (i+1)&(size-1); 
									jb = (j+1)&(size-1);
									break;
								}
					        	else if((arr[k]==3)&&(s[i][(j+1)&(size-1)] == 3)){
									c++;
									ib = i; 
									jb = (j+1)&(size-1);
									break;
								}
					        	else if((arr[k]==4)&&(s[(i-1)&(size-1)][j] == 3)){
									c++;
									ib = (i-1)&(size-1); 
									jb = j;
									break;
								}
					        	else if((arr[k]==5)&&(s[(i-1)&(size-1)][(j-1)&(size-1)] == 3)){
									c++;
									ib = (i-1)&(size-1); 
									jb = (j-1)&(size-1);
									break;
								}
					        	else if((arr[k]==6)&&(s[i][(j-1)&(size-1)] == 3)){
									c++;
									ib = i; 
									jb = (j-1)&(size-1);
									break;
								}
					        }
					        if (c > 0){
	                            double l = Math.random();
								if (l < 0.5){
									s[i][j] = 13;
	                                colorSquare(i,j);
									s[ib][jb] = 0;
	                                colorSquare(ib,jb);
								}
								else{
									s[i][j] = 0;
	                                colorSquare(i,j);
									s[ib][jb] = 13;
	                                colorSquare(ib,jb);
								}
	                        }
						}
						else if(s[i][j] == 3){
							int c = 0;
							int[] arr = {1, 2, 3, 4, 5, 6};
					        int n = arr.length;
					        random (arr, n);
					        for(int k=0;k<n;k++) {
					        	if((arr[k]==1)&&(s[(i+1)&(size-1)][j] == 12)){
					        		c++;
									ib = (i+1)&(size-1); 
									jb = j;
									break;
								}
					        	else if((arr[k]==2)&&(s[(i+1)&(size-1)][(j+1)&(size-1)] == 12)){
									c++;
									ib = (i+1)&(size-1); 
									jb = (j+1)&(size-1);
									break;
								}
					        	else if((arr[k]==3)&&(s[i][(j+1)&(size-1)] == 12)){
									c++;
									ib = i; 
									jb = (j+1)&(size-1);
									break;
								}
					        	else if((arr[k]==4)&&(s[(i-1)&(size-1)][j] == 12)){
									c++;
									ib = (i-1)&(size-1); 
									jb = j;
									break;
								}
					        	else if((arr[k]==5)&&(s[(i-1)&(size-1)][(j-1)&(size-1)] == 12)){
									c++;
									ib = (i-1)&(size-1); 
									jb = (j-1)&(size-1);
									break;
								}
					        	else if((arr[k]==6)&&(s[i][(j-1)&(size-1)] == 12)){
									c++;
									ib = i; 
									jb = (j-1)&(size-1);
									break;
								}
					        }
							if (c > 0){
	                            double l = Math.random();
								if (l < 0.5){
									s[i][j] = 13;
	                                colorSquare(i,j);
									s[ib][jb] = 0;
	                                colorSquare(ib,jb);
								}
								else{
									s[i][j] = 0;
	                                colorSquare(i,j);
									s[ib][jb] = 13;
	                                colorSquare(ib,jb);
								}
	                        }
						}
					}
				//R13...CH3* + H* => CH4(g) + 2*
					else if((p>=p1+p2+p3+p4+p5+p6+p7+p9+p11+p12)&&(p<p1+p2+p3+p4+p5+p6+p7+p9+p11+p12+p13)){
						if(s[i][j] == 13){
							int c = 0;
							int[] arr = {1, 2, 3, 4, 5, 6};
					        int n = arr.length;
					        random (arr, n);
					        for(int k=0;k<n;k++) {
					        	if((arr[k]==1)&&(s[(i+1)&(size-1)][j] == 3)){
					        		c++;
									ib = (i+1)&(size-1); 
									jb = j;
									break;
								}
					        	else if((arr[k]==2)&&(s[(i+1)&(size-1)][(j+1)&(size-1)] == 3)){
									c++;
									ib = (i+1)&(size-1); 
									jb = (j+1)&(size-1);
									break;
								}
					        	else if((arr[k]==3)&&(s[i][(j+1)&(size-1)] == 3)){
									c++;
									ib = i; 
									jb = (j+1)&(size-1);
									break;
								}
					        	else if((arr[k]==4)&&(s[(i-1)&(size-1)][j] == 3)){
									c++;
									ib = (i-1)&(size-1); 
									jb = j;
									break;
								}
					        	else if((arr[k]==5)&&(s[(i-1)&(size-1)][(j-1)&(size-1)] == 3)){
									c++;
									ib = (i-1)&(size-1); 
									jb = (j-1)&(size-1);
									break;
								}
					        	else if((arr[k]==6)&&(s[i][(j-1)&(size-1)] == 3)){
									c++;
									ib = i; 
									jb = (j-1)&(size-1);
									break;
								}
					        }
					        if (c > 0){
								s[i][j] = 0;
	                            colorSquare(i,j);
								s[ib][jb] = 0;
	                            colorSquare(ib,jb);
	                            pCH4++;
	                        }
						}
						else if(s[i][j] == 3){
							int c = 0;
							int[] arr = {1, 2, 3, 4, 5, 6};
					        int n = arr.length;
					        random (arr, n);
					        for(int k=0;k<n;k++) {
					        	if((arr[k]==1)&&(s[(i+1)&(size-1)][j] == 13)){
					        		c++;
									ib = (i+1)&(size-1); 
									jb = j;
									break;
								}
					        	else if((arr[k]==2)&&(s[(i+1)&(size-1)][(j+1)&(size-1)] == 13)){
									c++;
									ib = (i+1)&(size-1); 
									jb = (j+1)&(size-1);
									break;
								}
					        	else if((arr[k]==3)&&(s[i][(j+1)&(size-1)] == 13)){
									c++;
									ib = i; 
									jb = (j+1)&(size-1);
									break;
								}
					        	else if((arr[k]==4)&&(s[(i-1)&(size-1)][j] == 13)){
									c++;
									ib = (i-1)&(size-1); 
									jb = j;
									break;
								}
					        	else if((arr[k]==5)&&(s[(i-1)&(size-1)][(j-1)&(size-1)] == 13)){
									c++;
									ib = (i-1)&(size-1); 
									jb = (j-1)&(size-1);
									break;
								}
					        	else if((arr[k]==6)&&(s[i][(j-1)&(size-1)] == 13)){
									c++;
									ib = i; 
									jb = (j-1)&(size-1);
									break;
								}
					        }
					        if (c > 0){
								s[i][j] = 0;
	                            colorSquare(i,j);
								s[ib][jb] = 0;
	                            colorSquare(ib,jb);
	                            pCH4++;
	                        }
						}
					}
				//R14...CH3* + CH* => CH3CH* + *
					else if((p>=p1+p2+p3+p4+p5+p6+p7+p9+p11+p12+p13)&&(p<p1+p2+p3+p4+p5+p6+p7+p9+p11+p12+p13+p14)){
						if(s[i][j] == 13){
							int c = 0;
					        if(s[(i+1)&(size-1)][j] == 19){
					        	c++;
								ib = (i+1)&(size-1); 
								jb = j;
							}
					        else if(s[(i+1)&(size-1)][(j+1)&(size-1)] == 19){
								c++;
								ib = (i+1)&(size-1); 
								jb = (j+1)&(size-1);
							}
					        else if(s[i][(j+1)&(size-1)] == 19){
								c++;
								ib = i; 
								jb = (j+1)&(size-1);
							}
					        else if(s[(i-1)&(size-1)][j] == 19){
								c++;
								ib = (i-1)&(size-1); 
								jb = j;
							}
					        else if(s[(i-1)&(size-1)][(j-1)&(size-1)] == 19){
								c++;
								ib = (i-1)&(size-1); 
								jb = (j-1)&(size-1);
							}
					        else if(s[i][(j-1)&(size-1)] == 19){
								c++;
								ib = i; 
								jb = (j-1)&(size-1);
							}
					        if (c > 0){
	                            double l = Math.random();
								if (l < 0.5){
									s[i][j] = 9;
	                                colorSquare(i,j);
									s[ib][jb] = 0;
	                                colorSquare(ib,jb);
								}
								else{
									s[i][j] = 0;
	                                colorSquare(i,j);
									s[ib][jb] = 9;
	                                colorSquare(ib,jb);
								}
								r14++;
	                        }
						}
						else if(s[i][j] == 19){
							int c = 0;
					        if(s[(i+1)&(size-1)][j] == 13){
					        	c++;
								ib = (i+1)&(size-1); 
								jb = j;
							}
					        else if(s[(i+1)&(size-1)][(j+1)&(size-1)] == 13){
								c++;
								ib = (i+1)&(size-1); 
								jb = (j+1)&(size-1);
							}
					        else if(s[i][(j+1)&(size-1)] == 13){
								c++;
								ib = i; 
								jb = (j+1)&(size-1);
							}
					        else if(s[(i-1)&(size-1)][j] == 13){
								c++;
								ib = (i-1)&(size-1); 
								jb = j;
							}
					        else if(s[(i-1)&(size-1)][(j-1)&(size-1)] == 13){
								c++;
								ib = (i-1)&(size-1); 
								jb = (j-1)&(size-1);
							}
					        else if(s[i][(j-1)&(size-1)] == 13){
								c++;
								ib = i; 
								jb = (j-1)&(size-1);
							}
					        if (c > 0){
	                            double l = Math.random();
								if (l < 0.5){
									s[i][j] = 9;
	                                colorSquare(i,j);
									s[ib][jb] = 0;
	                                colorSquare(ib,jb);
								}
								else{
									s[i][j] = 0;
	                                colorSquare(i,j);
									s[ib][jb] = 9;
	                                colorSquare(ib,jb);
								}
								r14++;
	                        }
						}
					}
				//R16..CH3* + CH3* => CH3CH3**
					else if((p>=p1+p2+p3+p4+p5+p6+p7+p9+p11+p12+p13+p14)&&(p<p1+p2+p3+p4+p5+p6+p7+p9+p11+p12+p13+p14+p16)){
						if(s[i][j] == 13){
							int c = 0;
					        if(s[(i+1)&(size-1)][j] == 13){
					        	c++;
								ib = (i+1)&(size-1); 
								jb = j;
							}
					        else if(s[(i+1)&(size-1)][(j+1)&(size-1)] == 13){
								c++;
								ib = (i+1)&(size-1); 
								jb = (j+1)&(size-1);
							}
					        else if(s[i][(j+1)&(size-1)] == 13){
								c++;
								ib = i; 
								jb = (j+1)&(size-1);
							}
					        else if(s[(i-1)&(size-1)][j] == 13){
								c++;
								ib = (i-1)&(size-1); 
								jb = j;
							}
					        else if(s[(i-1)&(size-1)][(j-1)&(size-1)] == 13){
								c++;
								ib = (i-1)&(size-1); 
								jb = (j-1)&(size-1);
							}
					        else if(s[i][(j-1)&(size-1)] == 13){
								c++;
								ib = i; 
								jb = (j-1)&(size-1);
							}
					        if (c > 0){
	                            double l = Math.random();
								if (l < 0.5){
									s[i][j] = 4;
	                                colorSquare(i,j);
									s[ib][jb] = 4;
	                                colorSquare(ib,jb);
								}
								else{
									s[i][j] = 4;
	                                colorSquare(i,j);
									s[ib][jb] = 4;
	                                colorSquare(ib,jb);
								}
								r16++;
	                        }
						}
					}
				//R18...CH3CH2* + H* => CH3CH3**
					else if((p>=p1+p2+p3+p4+p5+p6+p7+p9+p11+p12+p13+p14+p16)&&(p<p1+p2+p3+p4+p5+p6+p7+p9+p11+p12+p13+p14+p16+p18)){
						if(s[i][j] == 15){
							int c = 0;
							int[] arr = {1, 2, 3, 4, 5, 6};
					        int n = arr.length;
					        random (arr, n);
					        for(int k=0;k<n;k++) {
					        	if((arr[k]==1)&&(s[(i+1)&(size-1)][j] == 3)){
					        		c++;
									ib = (i+1)&(size-1); 
									jb = j;
									break;
								}
					        	else if((arr[k]==2)&&(s[(i+1)&(size-1)][(j+1)&(size-1)] == 3)){
									c++;
									ib = (i+1)&(size-1); 
									jb = (j+1)&(size-1);
									break;
								}
					        	else if((arr[k]==3)&&(s[i][(j+1)&(size-1)] == 3)){
									c++;
									ib = i; 
									jb = (j+1)&(size-1);
									break;
								}
					        	else if((arr[k]==4)&&(s[(i-1)&(size-1)][j] == 3)){
									c++;
									ib = (i-1)&(size-1); 
									jb = j;
									break;
								}
					        	else if((arr[k]==5)&&(s[(i-1)&(size-1)][(j-1)&(size-1)] == 3)){
									c++;
									ib = (i-1)&(size-1); 
									jb = (j-1)&(size-1);
									break;
								}
					        	else if((arr[k]==6)&&(s[i][(j-1)&(size-1)] == 3)){
									c++;
									ib = i; 
									jb = (j-1)&(size-1);
									break;
								}
					        }
							if (c > 0){
	                            s[i][j] = 4;
	                            colorSquare(i,j);
								s[ib][jb] = 4;
	                            colorSquare(ib,jb);
	                        }
						}
						else if(s[i][j] == 3){
							int c = 0;
							if(s[(i+1)&(size-1)][j] == 15){
					        	c++;
								ib = (i+1)&(size-1); 
								jb = j;
							}
					        else if(s[(i+1)&(size-1)][(j+1)&(size-1)] == 15){
								c++;
								ib = (i+1)&(size-1); 
								jb = (j+1)&(size-1);
							}
					        else if(s[i][(j+1)&(size-1)] == 15){
								c++;
								ib = i; 
								jb = (j+1)&(size-1);
							}
					        else if(s[(i-1)&(size-1)][j] == 15){
								c++;
								ib = (i-1)&(size-1); 
								jb = j;
							}
					        else if(s[(i-1)&(size-1)][(j-1)&(size-1)] == 15){
								c++;
								ib = (i-1)&(size-1); 
								jb = (j-1)&(size-1);
							}
					        else if(s[i][(j-1)&(size-1)] == 15){
								c++;
								ib = i; 
								jb = (j-1)&(size-1);
							}
							if (c > 0){
	                            s[i][j] = 4;
	                            colorSquare(i,j);
								s[ib][jb] = 4;
	                            colorSquare(ib,jb);
	                        }
						}
					}
				//R19...CH3CH2* + 2* => CH2CH2** + H*
					else if((p>=p1+p2+p3+p4+p5+p6+p7+p9+p11+p12+p13+p14+p16+p18)&&(p<p1+p2+p3+p4+p5+p6+p7+p9+p11+p12+p13+p14+p16+p18+p19)){
						if(s[i][j] == 15){
							int c = 0;
							int[] arr = {1, 2, 3, 4, 5, 6};
					        int n = arr.length;
					        random (arr, n);
					        for(int k=0;k<n;k++) {
					        	if((arr[k]==1)&&(s[(i+1)&(size-1)][j] == 0)){
					        		bi[c] = (i+1)&(size-1); 
					        		bj[c] = j;
					        		c++;
					        		if(c==2) {break;};
					        	}
					        	else if((arr[k]==2)&&(s[(i+1)&(size-1)][(j+1)&(size-1)] == 0)){
					        		bi[c] = (i+1)&(size-1); 
					        		bj[c] = (j+1)&(size-1);
					        		c++;
					        		if(c==2) {break;};
					        	}
					        	else if((arr[k]==3)&&(s[i][(j+1)&(size-1)] == 0)){
					        		bi[c] = i; 
					        		bj[c] = (j+1)&(size-1);
					        		c++;
					        		if(c==2) {break;};
					        	}
					        	else if((arr[k]==4)&&(s[(i-1)&(size-1)][j] == 0)){
					        		bi[c] = (i-1)&(size-1); 
					        		bj[c] = j;
					        		c++;
					        		if(c==2) {break;};
					        	}
					        	else if((arr[k]==5)&&(s[(i-1)&(size-1)][(j-1)&(size-1)] == 0)){
					        		bi[c] = (i-1)&(size-1); 
					        		bj[c] = (j-1)&(size-1);
					        		c++;
					        		if(c==2) {break;};
					        	}
					        	else if((arr[k]==6)&&(s[i][(j-1)&(size-1)] == 0)){
					        		bi[c] = i; 
					        		bj[c] = (j-1)&(size-1);
					        		c++;
					        		if(c==2) {break;};
					        	}
					        }
							if (c<2){
	                            s[i][j] = 15;
	                            colorSquare(i,j);
	                        }
	                        else if(c==2){
	                        	double r = Math.random();
								if (r < 0.5){
									s[i][j] = 20;
	                                colorSquare(i,j);
									s[bi[0]][bj[0]] = 20;
	                                colorSquare(bi[0],bi[0]);
	                                s[bi[1]][bj[1]] = 3;
	                                colorSquare(bj[0],bj[0]);
								}
								else {
									s[i][j] = 20;
	                                colorSquare(i,j);
									s[bi[0]][bj[0]] = 3;
	                                colorSquare(bi[0],bi[0]);
	                                s[bi[1]][bj[1]] = 20;
	                                colorSquare(bj[1],bj[1]);
								}
	                        }
						}
					}
				//R20..OH* + H* => H2O* + *
					else if((p>=p1+p2+p3+p4+p5+p6+p7+p9+p11+p12+p13+p14+p16+p18+p19)&&(p<p1+p2+p3+p4+p5+p6+p7+p9+p11+p12+p13+p14+p16+p18+p19+p20)){
						if(s[i][j] == 11){
							int c = 0;
							int[] arr = {1, 2, 3, 4, 5, 6};
					        int n = arr.length;
					        random (arr, n);
					        for(int k=0;k<n;k++) {
					        	if((arr[k]==1)&&(s[(i+1)&(size-1)][j] == 3)){
					        		c++;
									ib = (i+1)&(size-1); 
									jb = j;
									break;
								}
					        	else if((arr[k]==2)&&(s[(i+1)&(size-1)][(j+1)&(size-1)] == 3)){
									c++;
									ib = (i+1)&(size-1); 
									jb = (j+1)&(size-1);
									break;
								}
					        	else if((arr[k]==3)&&(s[i][(j+1)&(size-1)] == 3)){
									c++;
									ib = i; 
									jb = (j+1)&(size-1);
									break;
								}
					        	else if((arr[k]==4)&&(s[(i-1)&(size-1)][j] == 3)){
									c++;
									ib = (i-1)&(size-1); 
									jb = j;
									break;
								}
					        	else if((arr[k]==5)&&(s[(i-1)&(size-1)][(j-1)&(size-1)] == 3)){
									c++;
									ib = (i-1)&(size-1); 
									jb = (j-1)&(size-1);
									break;
								}
					        	else if((arr[k]==6)&&(s[i][(j-1)&(size-1)] == 3)){
									c++;
									ib = i; 
									jb = (j-1)&(size-1);
									break;
								}
					        }
					        if (c > 0){
	                            double l = Math.random();
								if (l < 0.5){
									s[i][j] = 6;
	                                colorSquare(i,j);
									s[ib][jb] = 0;
	                                colorSquare(ib,jb);
								}
								else{
									s[i][j] = 0;
	                                colorSquare(i,j);
									s[ib][jb] = 6;
	                                colorSquare(ib,jb);
								}
	                        }
						}
						else if(s[i][j] == 3){
							int c = 0;
							if(s[(i+1)&(size-1)][j] == 11){
					        	c++;
								ib = (i+1)&(size-1); 
								jb = j;
							}
					        else if(s[(i+1)&(size-1)][(j+1)&(size-1)] == 11){
								c++;
								ib = (i+1)&(size-1); 
								jb = (j+1)&(size-1);
							}
					        else if(s[i][(j+1)&(size-1)] == 11){
								c++;
								ib = i; 
								jb = (j+1)&(size-1);
							}
					        else if(s[(i-1)&(size-1)][j] == 11){
								c++;
								ib = (i-1)&(size-1); 
								jb = j;
							}
					        else if(s[(i-1)&(size-1)][(j-1)&(size-1)] == 11){
								c++;
								ib = (i-1)&(size-1); 
								jb = (j-1)&(size-1);
							}
					        else if(s[i][(j-1)&(size-1)] == 11){
								c++;
								ib = i; 
								jb = (j-1)&(size-1);
							}
							if (c > 0){
	                            double l = Math.random();
								if (l < 0.5){
									s[i][j] = 6;
	                                colorSquare(i,j);
									s[ib][jb] = 0;
	                                colorSquare(ib,jb);
								}
								else{
									s[i][j] = 0;
	                                colorSquare(i,j);
									s[ib][jb] = 6;
	                                colorSquare(ib,jb);
								}
	                        }
						}
					}
				//R21...H2O* + * => OH* + H*
					else if((p>=p1+p2+p3+p4+p5+p6+p7+p9+p11+p12+p13+p14+p16+p18+p19+p20)&&(p<p1+p2+p3+p4+p5+p6+p7+p9+p11+p12+p13+p14+p16+p18+p19+p20+p21)){
						if(s[i][j] == 6){
							int c = 0;
							int[] arr = {1, 2, 3, 4, 5, 6};
					        int n = arr.length;
					        random (arr, n);
					        for(int k=0;k<n;k++) {
					        	if((arr[k]==1)&&(s[(i+1)&(size-1)][j] == 0)){
					        		c++;
									ib = (i+1)&(size-1); 
									jb = j;
									break;
								}
					        	else if((arr[k]==2)&&(s[(i+1)&(size-1)][(j+1)&(size-1)] == 0)){
									c++;
									ib = (i+1)&(size-1); 
									jb = (j+1)&(size-1);
									break;
								}
					        	else if((arr[k]==3)&&(s[i][(j+1)&(size-1)] == 0)){
									c++;
									ib = i; 
									jb = (j+1)&(size-1);
									break;
								}
					        	else if((arr[k]==4)&&(s[(i-1)&(size-1)][j] == 0)){
									c++;
									ib = (i-1)&(size-1); 
									jb = j;
									break;
								}
					        	else if((arr[k]==5)&&(s[(i-1)&(size-1)][(j-1)&(size-1)] == 0)){
									c++;
									ib = (i-1)&(size-1); 
									jb = (j-1)&(size-1);
									break;
								}
					        	else if((arr[k]==6)&&(s[i][(j-1)&(size-1)] == 0)){
									c++;
									ib = i; 
									jb = (j-1)&(size-1);
									break;
								}
					        }
							if (c > 0){
	                            double r = Math.random();
								if (r < 0.5){
									s[i][j] = 11;
	                                colorSquare(i,j);
									s[ib][jb] = 3;
	                                colorSquare(ib,jb);
								}
								else{
									s[i][j] = 3;
	                                colorSquare(i,j);
									s[ib][jb] = 11;
	                                colorSquare(ib,jb);
								}
	                        }
						}
						else if(s[i][j] == 0){
							int c = 0;
							if(s[(i+1)&(size-1)][j] == 6){
					        	c++;
								ib = (i+1)&(size-1); 
								jb = j;
							}
					        else if(s[(i+1)&(size-1)][(j+1)&(size-1)] == 6){
								c++;
								ib = (i+1)&(size-1); 
								jb = (j+1)&(size-1);
							}
					        else if(s[i][(j+1)&(size-1)] == 6){
								c++;
								ib = i; 
								jb = (j+1)&(size-1);
							}
					        else if(s[(i-1)&(size-1)][j] == 6){
								c++;
								ib = (i-1)&(size-1); 
								jb = j;
							}
					        else if(s[(i-1)&(size-1)][(j-1)&(size-1)] == 6){
								c++;
								ib = (i-1)&(size-1); 
								jb = (j-1)&(size-1);
							}
					        else if(s[i][(j-1)&(size-1)] == 6){
								c++;
								ib = i; 
								jb = (j-1)&(size-1);
							}
							if (c > 0){
	                            double r = Math.random();
								if (r < 0.5){
									s[i][j] = 11;
	                                colorSquare(i,j);
									s[ib][jb] = 3;
	                                colorSquare(ib,jb);
								}
								else{
									s[i][j] = 3;
	                                colorSquare(i,j);
									s[ib][jb] = 11;
	                                colorSquare(ib,jb);
								}
	                        }
						}
					}
				//R22...OH* + * => O* + H*
					else if((p>=p1+p2+p3+p4+p5+p6+p7+p9+p11+p12+p13+p14+p16+p18+p19+p20+p21)&&(p<p1+p2+p3+p4+p5+p6+p7+p9+p11+p12+p13+p14+p16+p18+p19+p20+p21+p22)){
						if(s[i][j] == 11){
							int c = 0;
							int[] arr = {1, 2, 3, 4, 5, 6};
					        int n = arr.length;
					        random (arr, n);
					        for(int k=0;k<n;k++) {
					        	if((arr[k]==1)&&(s[(i+1)&(size-1)][j] == 0)){
					        		c++;
									ib = (i+1)&(size-1); 
									jb = j;
									break;
								}
					        	else if((arr[k]==2)&&(s[(i+1)&(size-1)][(j+1)&(size-1)] == 0)){
									c++;
									ib = (i+1)&(size-1); 
									jb = (j+1)&(size-1);
									break;
								}
					        	else if((arr[k]==3)&&(s[i][(j+1)&(size-1)] == 0)){
									c++;
									ib = i; 
									jb = (j+1)&(size-1);
									break;
								}
					        	else if((arr[k]==4)&&(s[(i-1)&(size-1)][j] == 0)){
									c++;
									ib = (i-1)&(size-1); 
									jb = j;
									break;
								}
					        	else if((arr[k]==5)&&(s[(i-1)&(size-1)][(j-1)&(size-1)] == 0)){
									c++;
									ib = (i-1)&(size-1); 
									jb = (j-1)&(size-1);
									break;
								}
					        	else if((arr[k]==6)&&(s[i][(j-1)&(size-1)] == 0)){
									c++;
									ib = i; 
									jb = (j-1)&(size-1);
									break;
								}
					        }
							if (c > 0){
	                            double r = Math.random();
								if (r < 0.5){
									s[i][j] = 14;
	                                colorSquare(i,j);
									s[ib][jb] = 3;
	                                colorSquare(ib,jb);
								}
								else{
									s[i][j] = 3;
	                                colorSquare(i,j);
									s[ib][jb] = 14;
	                                colorSquare(ib,jb);
								}
	                        }
						}
						else if(s[i][j] == 0){
							int c = 0;
							if(s[(i+1)&(size-1)][j] == 11){
					        	c++;
								ib = (i+1)&(size-1); 
								jb = j;
							}
					        else if(s[(i+1)&(size-1)][(j+1)&(size-1)] == 11){
								c++;
								ib = (i+1)&(size-1); 
								jb = (j+1)&(size-1);
							}
					        else if(s[i][(j+1)&(size-1)] == 11){
								c++;
								ib = i; 
								jb = (j+1)&(size-1);
							}
					        else if(s[(i-1)&(size-1)][j] == 11){
								c++;
								ib = (i-1)&(size-1); 
								jb = j;
							}
					        else if(s[(i-1)&(size-1)][(j-1)&(size-1)] == 11){
								c++;
								ib = (i-1)&(size-1); 
								jb = (j-1)&(size-1);
							}
					        else if(s[i][(j-1)&(size-1)] == 11){
								c++;
								ib = i; 
								jb = (j-1)&(size-1);
							}
							if (c > 0){
	                            double r = Math.random();
								if (r < 0.5){
									s[i][j] = 14;
	                                colorSquare(i,j);
									s[ib][jb] = 3;
	                                colorSquare(ib,jb);
								}
								else{
									s[i][j] = 3;
	                                colorSquare(i,j);
									s[ib][jb] = 14;
	                                colorSquare(ib,jb);
								}
	                        }
						}
					}
				//R23...O* + H* => OH*
					else if((p>=p1+p2+p3+p4+p5+p6+p7+p9+p11+p12+p13+p14+p16+p18+p19+p20+p21+p22)&&(p<p1+p2+p3+p4+p5+p6+p7+p9+p11+p12+p13+p14+p16+p18+p19+p20+p21+p22+p23)){
						if(s[i][j] == 14){
							int c = 0;
							int[] arr = {1, 2, 3, 4, 5, 6};
					        int n = arr.length;
					        random (arr, n);
					        for(int k=0;k<n;k++) {
					        	if((arr[k]==1)&&(s[(i+1)&(size-1)][j] == 3)){
					        		c++;
									ib = (i+1)&(size-1); 
									jb = j;
									break;
								}
					        	else if((arr[k]==2)&&(s[(i+1)&(size-1)][(j+1)&(size-1)] == 3)){
									c++;
									ib = (i+1)&(size-1); 
									jb = (j+1)&(size-1);
									break;
								}
					        	else if((arr[k]==3)&&(s[i][(j+1)&(size-1)] == 3)){
									c++;
									ib = i; 
									jb = (j+1)&(size-1);
									break;
								}
					        	else if((arr[k]==4)&&(s[(i-1)&(size-1)][j] == 3)){
									c++;
									ib = (i-1)&(size-1); 
									jb = j;
									break;
								}
					        	else if((arr[k]==5)&&(s[(i-1)&(size-1)][(j-1)&(size-1)] == 3)){
									c++;
									ib = (i-1)&(size-1); 
									jb = (j-1)&(size-1);
									break;
								}
					        	else if((arr[k]==6)&&(s[i][(j-1)&(size-1)] == 3)){
									c++;
									ib = i; 
									jb = (j-1)&(size-1);
									break;
								}
					        }
							if (c > 0){
	                            double r = Math.random();
								if (r < 0.5){
									s[i][j] = 11;
	                                colorSquare(i,j);
									s[ib][jb] = 0;
	                                colorSquare(ib,jb);
								}
								else{
									s[i][j] = 0;
	                                colorSquare(i,j);
									s[ib][jb] = 11;
	                                colorSquare(ib,jb);
								}
	                        }
						}
						else if(s[i][j] == 3){
							int c = 0;
							int[] arr = {1, 2, 3, 4, 5, 6};
					        int n = arr.length;
					        random (arr, n);
					        for(int k=0;k<n;k++) {
					        	if((arr[k]==1)&&(s[(i+1)&(size-1)][j] == 14)){
					        		c++;
									ib = (i+1)&(size-1); 
									jb = j;
									break;
								}
					        	else if((arr[k]==2)&&(s[(i+1)&(size-1)][(j+1)&(size-1)] == 14)){
									c++;
									ib = (i+1)&(size-1); 
									jb = (j+1)&(size-1);
									break;
								}
					        	else if((arr[k]==3)&&(s[i][(j+1)&(size-1)] == 14)){
									c++;
									ib = i; 
									jb = (j+1)&(size-1);
									break;
								}
					        	else if((arr[k]==4)&&(s[(i-1)&(size-1)][j] == 14)){
									c++;
									ib = (i-1)&(size-1); 
									jb = j;
									break;
								}
					        	else if((arr[k]==5)&&(s[(i-1)&(size-1)][(j-1)&(size-1)] == 14)){
									c++;
									ib = (i-1)&(size-1); 
									jb = (j-1)&(size-1);
									break;
								}
					        	else if((arr[k]==6)&&(s[i][(j-1)&(size-1)] == 14)){
									c++;
									ib = i; 
									jb = (j-1)&(size-1);
									break;
								}
					        }
							if (c > 0){
	                            double r = Math.random();
								if (r < 0.5){
									s[i][j] = 11;
	                                colorSquare(i,j);
									s[ib][jb] = 0;
	                                colorSquare(ib,jb);
								}
								else{
									s[i][j] = 0;
	                                colorSquare(i,j);
									s[ib][jb] = 11;
	                                colorSquare(ib,jb);
								}
	                        }
						}
					}
				//R24...CO* + O* => CO2**
					else if((p>=p1+p2+p3+p4+p5+p6+p7+p9+p11+p12+p13+p14+p16+p18+p19+p20+p21+p22+p23)&&(p<p1+p2+p3+p4+p5+p6+p7+p9+p11+p12+p13+p14+p16+p18+p19+p20+p21+p22+p23+p24)){
						if(s[i][j] == 2){
							int c = 0;
							if(s[(i+1)&(size-1)][j] == 14){
					        	c++;
								ib = (i+1)&(size-1); 
								jb = j;
							}
					        else if(s[(i+1)&(size-1)][(j+1)&(size-1)] == 14){
								c++;
								ib = (i+1)&(size-1); 
								jb = (j+1)&(size-1);
							}
					        else if(s[i][(j+1)&(size-1)] == 14){
								c++;
								ib = i; 
								jb = (j+1)&(size-1);
							}
					        else if(s[(i-1)&(size-1)][j] == 14){
								c++;
								ib = (i-1)&(size-1); 
								jb = j;
							}
					        else if(s[(i-1)&(size-1)][(j-1)&(size-1)] == 14){
								c++;
								ib = (i-1)&(size-1); 
								jb = (j-1)&(size-1);
							}
					        else if(s[i][(j-1)&(size-1)] == 14){
								c++;
								ib = i; 
								jb = (j-1)&(size-1);
							}
							if (c > 0){
	                            s[i][j] = 7;
	                            colorSquare(i,j);
								s[ib][jb] = 7;
	                            colorSquare(ib,jb);
	                        }
						}
						else if(s[i][j] == 14){
							int c = 0;
							int[] arr = {1, 2, 3, 4, 5, 6};
					        int n = arr.length;
					        random (arr, n);
					        for(int k=0;k<n;k++) {
					        	if((arr[k]==1)&&(s[(i+1)&(size-1)][j] == 2)){
					        		c++;
									ib = (i+1)&(size-1); 
									jb = j;
									break;
								}
					        	else if((arr[k]==2)&&(s[(i+1)&(size-1)][(j+1)&(size-1)] == 2)){
									c++;
									ib = (i+1)&(size-1); 
									jb = (j+1)&(size-1);
									break;
								}
					        	else if((arr[k]==3)&&(s[i][(j+1)&(size-1)] == 2)){
									c++;
									ib = i; 
									jb = (j+1)&(size-1);
									break;
								}
					        	else if((arr[k]==4)&&(s[(i-1)&(size-1)][j] == 2)){
									c++;
									ib = (i-1)&(size-1); 
									jb = j;
									break;
								}
					        	else if((arr[k]==5)&&(s[(i-1)&(size-1)][(j-1)&(size-1)] == 2)){
									c++;
									ib = (i-1)&(size-1); 
									jb = (j-1)&(size-1);
									break;
								}
					        	else if((arr[k]==6)&&(s[i][(j-1)&(size-1)] == 2)){
									c++;
									ib = i; 
									jb = (j-1)&(size-1);
									break;
								}
					        }
					        if (c > 0){
	                            s[i][j] = 7;
	                            colorSquare(i,j);
								s[ib][jb] = 7;
	                            colorSquare(ib,jb);
	                        }
						}
					}
				//R25...CO2** => CO* + O*
					else if((p>=p1+p2+p3+p4+p5+p6+p7+p9+p11+p12+p13+p14+p16+p18+p19+p20+p21+p22+p23+p24)&&(p<p1+p2+p3+p4+p5+p6+p7+p9+p11+p12+p13+p14+p16+p18+p19+p20+p21+p22+p23+p24+p25)){
						if(s[i][j] == 7){
							int c = 0;
							if(s[(i+1)&(size-1)][j] == 7){
					        	c++;
								ib = (i+1)&(size-1); 
								jb = j;
							}
					        else if(s[(i+1)&(size-1)][(j+1)&(size-1)] == 7){
								c++;
								ib = (i+1)&(size-1); 
								jb = (j+1)&(size-1);
							}
					        else if(s[i][(j+1)&(size-1)] == 7){
								c++;
								ib = i; 
								jb = (j+1)&(size-1);
							}
					        else if(s[(i-1)&(size-1)][j] == 7){
								c++;
								ib = (i-1)&(size-1); 
								jb = j;
							}
					        else if(s[(i-1)&(size-1)][(j-1)&(size-1)] == 7){
								c++;
								ib = (i-1)&(size-1); 
								jb = (j-1)&(size-1);
							}
					        else if(s[i][(j-1)&(size-1)] == 7){
								c++;
								ib = i; 
								jb = (j-1)&(size-1);
							}
							if (c > 0){
	                            double r = Math.random();
								if (r < 0.5){
									s[i][j] = 2;
	                                colorSquare(i,j);
									s[ib][jb] = 14;
	                                colorSquare(ib,jb);
								}
								else{
									s[i][j] = 14;
	                                colorSquare(i,j);
									s[ib][jb] = 2;
	                                colorSquare(ib,jb);
								}
	                        }
						}
					}
				}
			// Calculate surface coverage
				int z0=0;
				int z2=0;
				int z3=0;
				int z4=0;
				int z5=0;
				int z6=0;
				int z7=0;
				int z8=0;
				int z9=0;
				int z10=0;
				int z11=0;
				int z12=0;
				int z13=0;
				int z14=0;
				int z15=0;
				int z16=0;
				int z17=0;
				int z18=0;
				int z19=0;
				int z20=0;
				for (int i=0; i<size; i++){					
					for (int j=0; j<size; j++){	
						if (s[i][j] == 0) z0++;
						else if (s[i][j] == 2) z2++;
						else if (s[i][j] == 3) z3++;
						else if (s[i][j] == 4) z4++; 
						else if (s[i][j] == 5) z5++;
						else if (s[i][j] == 6) z6++;
						else if (s[i][j] == 7) z7++;
						else if (s[i][j] == 8) z8++;
						else if (s[i][j] == 9) z9++;
						else if (s[i][j] == 10) z10++;
						else if (s[i][j] == 11) z11++;
						else if (s[i][j] == 12) z12++;
						else if (s[i][j] == 13) z13++;
						else if (s[i][j] == 14) z14++;
						else if (s[i][j] == 15) z15++;
						else if (s[i][j] == 16) z16++;
						else if (s[i][j] == 17) z17++;
						else if (s[i][j] == 18) z18++;
						else if (s[i][j] == 19) z19++;
						else if (s[i][j] == 20) z20++;
					}
				}
				Z0 = (double)z0/(L*L);
				Z2 = (double)z2/(L*L);
				Z3 = (double)z3/(L*L);
				Z4 = (double)z4/(L*L);
				Z5 = (double)z5/(L*L);
				Z6 = (double)z6/(L*L);
				Z7 = (double)z7/(L*L);
				Z8 = (double)z8/(L*L);
				Z9 = (double)z9/(L*L);
				Z10 = (double)z10/(L*L);
				Z11 = (double)z11/(L*L);
				Z12 = (double)z12/(L*L);
				Z13 = (double)z13/(L*L);
				Z14 = (double)z14/(L*L);
				Z15 = (double)z15/(L*L);
				Z16 = (double)z16/(L*L);
				Z17 = (double)z17/(L*L);
				Z18 = (double)z18/(L*L);
				Z19 = (double)z19/(L*L);
				Z20 = (double)z20/(L*L);
				double Ztot = Z0+Z2+Z3+Z4+Z5+Z6+Z7+Z8+Z9+Z10+Z11+Z12+Z13+Z14+Z15+Z16+Z17+Z18+Z19+Z20;
				
			// Calculate rate
				RxCH4 =  pCH4/(t*L*L);
				RxC2H6 = pC2H6/(t*L*L);
				RxC2H4 = pC2H4/(t*L*L);
				RxH2O =  pH2O/(t*L*L);
				RxCO2 =  pCO2/(t*L*L);
				R14   =  r14/(t*L*L);
				R15   =  r15/(t*L*L);
				R16   =  r16/(t*L*L);
				RaCO = aCO/(t*L*L);
				RaH2 = aH2/(t*L*L);
				RdCO = dCO/(t*L*L);
				RdH2 = dH2/(t*L*L);
				
				System.out.println(t+"  "+delt+"   "+Ztot+"  "+Z0+"  "+Z2+"  "+Z3+"  "+Z4+"  "+Z5+"  "+Z6+"  "+Z7+"  "+Z8+"  "+Z9+"  "+Z10+"  "+Z11+"  "+Z12+"  "+Z13+"  "+Z14+"  "+Z15+"  "+Z16+"  "+Z17+"  "+Z18+"  "+Z19+"  "+Z20+"  "+RxCH4+"  "+RxC2H6+"  "+RxC2H4+"  "+RxH2O+"  "+RxCO2+"  "+R14+"  "+R15+"  "+R16+"  "+RaCO+"  "+RaH2+"  "+RdCO+"  "+RdH2);
				
				repaint();		// causes update method to be called soon
			}
			try { Thread.sleep(1); } catch (InterruptedException e) {}	
		}
	}
	
	static void random( int arr[], int n){
        Random r = new Random();
        for (int i = n-1; i>0; i--){
            int j = r.nextInt(i);
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }
	
	void colorSquare(int i, int j){
		if (s[i][j] == 0) offScreenGraphics.setColor(vSITEcolor);
		else if(s[i][j] == 1) offScreenGraphics.setColor(noSITEcolor);
		else if(s[i][j] == 2) offScreenGraphics.setColor(COcolor);
		else if(s[i][j] == 3) offScreenGraphics.setColor(Hcolor);
		else if(s[i][j] == 4) offScreenGraphics.setColor(CH3CH3color);
		else if(s[i][j] == 5) offScreenGraphics.setColor(CH3COcolor);
		else if(s[i][j] == 6) offScreenGraphics.setColor(H2Ocolor);
		else if(s[i][j] == 7) offScreenGraphics.setColor(CO2color);
		else if(s[i][j] == 8) offScreenGraphics.setColor(HCOcolor);
		else if(s[i][j] == 9) offScreenGraphics.setColor(CH3CHcolor);
		else if(s[i][j] == 10) offScreenGraphics.setColor(H3COcolor);
		else if(s[i][j] == 11) offScreenGraphics.setColor(OHcolor);
		else if(s[i][j] == 12) offScreenGraphics.setColor(CH2color);
		else if(s[i][j] == 13) offScreenGraphics.setColor(CH3color);
		else if(s[i][j] == 14) offScreenGraphics.setColor(Ocolor);
		else if(s[i][j] == 15) offScreenGraphics.setColor(CH3CH2color);
		else if(s[i][j] == 16) offScreenGraphics.setColor(CH3CHOcolor);
		else if(s[i][j] == 17) offScreenGraphics.setColor(CH3CH2Ocolor);
		else if(s[i][j] == 18) offScreenGraphics.setColor(Ccolor);
		else if(s[i][j] == 19) offScreenGraphics.setColor(CHcolor);
		else if(s[i][j] == 20) offScreenGraphics.setColor(CH2CH2color);
		if(j%2==1) {
			offScreenGraphics.fillRect((i+size-j/2)*(siteWidth+1),j*(siteWidth+1),siteWidth,siteWidth);
		}
		else if(j%2==0) {
			offScreenGraphics.fillRect(((i+size-j/2)*(siteWidth+1))+2,j*(siteWidth+1),siteWidth,siteWidth);
		}
	}
	// Override default update method to skip drawing the background:
	public void update(Graphics g){
		paint(g);
	}
	// Paint method just blasts the off-screen image to the screen:
	public void paint(Graphics g){
		g.drawImage(offScreenImage,-100,0,canvasSize,canvasSize,this);
	}
}
