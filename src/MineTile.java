package src;

import javax.swing.*;

public class MineTile extends JButton{				
	private int r,c;						
	public static final int TILESIZE = 70;   

	public MineTile(int r, int c){			
		this.r = r;
		this.c = c;	
	}
	public int getR(){				
		return this.r;
	}
	public int getC(){  	
		return this.c;
	}
}