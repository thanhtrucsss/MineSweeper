package src;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import java.util.Stack;

public class Minesweeper{
	Stack<GameState> undoStack = new Stack<>();
	Stack<GameState> redoStack = new Stack<>();

	private ArrayList<MineTile> mineList;
	protected MineTile[][] mainBoard = new MineTile[Level.getNumRows()][Level.getNumCols()];

	Random random = new Random();
	int tilesClicked = 0;
	int score = 0;
	boolean gameOver = false;
	Display display = new Display(Level.getNumRows(), Level.getNumCols());
	public int numOfPlantedFlags;				

	private boolean firstClick;			
	
	Time time = new Time();			

	public Minesweeper(){			
		runGame();	
	
				
	}
	private void runGame(){					
		Sound.playSound(3);

		Level.setWinGame(false); 
		setFirstClick(true);				
		
		setNumOfFlags(Level.getMineCount());		

		textUpdate();								

		for(int r = 0; r< Level.getNumRows(); r++)					
			for(int c =0; c< Level.getNumCols(); c++){
				MineTile tile = new MineTile(r,c);					
				mainBoard[r][c] = tile;							
				
				
				tile.setFocusable(false);									
				tile.setMargin(new Insets(0, 0, 0, 0));		
				tile.setIcon(Display.unclickedIcon);								
				
				

				tile.addMouseListener(new MouseAdapter(){					
					@Override
					public void mousePressed (MouseEvent e){								
						if(Level.getWinGame()){									
							return; 						
						}	
						
						if(gameOver)									
							return;

						saveStateForUndo();
						redoStack.clear();

						MineTile tile = (MineTile) e.getSource();		
						if(e.getButton() == MouseEvent.BUTTON1){ 
	
							while (!gameOver || !Level.getWinGame()) {              
								
								if (mineList.contains(tile) || (numOfMinesAround(tile.getR(),tile.getC()) > 0 && getFirstClick())) { 
																	
									if (getFirstClick()) {			
										
										initializeMines(tile.getR(),tile.getC());			
													
										
									} else {					
										Sound.stopSound(3);
										Sound.playSound(1);

										revealMines();									
										System.out.println("Console: End Game");
										setFirstClick(false);							
										break; 
									}
									
								} else {	
									Sound.playSound(2);
									if(getFirstClick()) {
										time.receiveSignalA();
										display.startTimer();
									}


									checkMine(tile.getR(),tile.getC());		
									setFirstClick(false);				
									textUpdate();	

									break; 
								}
							}

							
						}else if (e.getButton() == MouseEvent.BUTTON3){  
							
							if(tile.getIcon() == Display.unclickedIcon && tile.isEnabled() && numOfPlantedFlags >0){ 
								
								Sound.playSound(5);
								tile.setIcon(Display.flagIcon);				
								plantingFlag();								

							}else if(tile.getIcon() == Display.flagIcon){		
								Sound.playSound(5);
								tile.setIcon(Display.unclickedIcon);		
								removingFlag();							
							}	
							textUpdate();					
						}
					}
				});		
				display.boardPanel.add(tile);			
			
			}

		display.setHintAction(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveStateForUndo();
				redoStack.clear();

				int mode = getAIMode();
				boolean acted = false;
				switch (mode) {
					case 0: // Smart Sequence (Auto)
						acted = autoSolveBasic() || autoSolveChainedReasoning();
						if (!acted) {
							MineTile safe = suggestBestProbabilisticMove();
							if (safe == null) safe = suggestBestEdgeMove();
							if (safe != null) {
								String xai = getXAIExplanationForHint(safe);
								JOptionPane.showMessageDialog(display.frame,
										"AI Suggestion (Auto):\n" +
												"Row " + (safe.getR() + 1) + ", Col " + (safe.getC() + 1) +
												"\nExplanation:\n" + xai);
							} else {
								JOptionPane.showMessageDialog(display.frame,
										"No safe moves or suggestions left!");
							}
						}
						break;
					case 1: // Basic Logic
						acted = autoSolveBasic();
						if (!acted)
							JOptionPane.showMessageDialog(display.frame, "No certain moves using Basic Logic!");
						break;
					case 2: // Chained Reasoning
						acted = autoSolveChainedReasoning();
						if (!acted)
							JOptionPane.showMessageDialog(display.frame, "No certain moves using Chained Reasoning!");
						break;
					case 3: // Probability
						MineTile prob = suggestBestProbabilisticMove();
						if (prob != null) {
							String xai = getXAIExplanationForHint(prob);
							JOptionPane.showMessageDialog(display.frame,
									"AI Suggestion (Probability):\n" +
											"Row " + (prob.getR() + 1) + ", Col " + (prob.getC() + 1) +
											"\nExplanation:\n" + xai);
						} else {
							JOptionPane.showMessageDialog(display.frame, "No safe moves or suggestions left!");
						}
						break;
					case 4: // Edge Guessing
						MineTile edge = suggestBestEdgeMove();
						if (edge != null) {
							String xai = getXAIExplanationForHint(edge);
							JOptionPane.showMessageDialog(display.frame,
									"AI Suggestion (Edge Guessing):\n" +
											"Row " + (edge.getR() + 1) + ", Col " + (edge.getC() + 1) +
											"\nExplanation:\n" + xai);
						} else {
							JOptionPane.showMessageDialog(display.frame, "No safe moves or suggestions left!");
						}
						break;
				}
				display.boardPanel.repaint();
			}
		});


		// Chế độ Auto-play: giải liên tục
		display.setAutoPlayAction(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveStateForUndo();
				redoStack.clear();

				int mode = getAIMode();
				int totalSteps = 0;
				boolean acted = true;
				switch (mode) {
					case 0: // Smart Sequence (Auto)
						while (autoSolveBasic() || autoSolveChainedReasoning()) {
							display.boardPanel.repaint();
							totalSteps++;
							try { Thread.sleep(80); } catch (InterruptedException ex) {}
						}
						break;
					case 1: // Basic Logic
						while (autoSolveBasic()) {
							display.boardPanel.repaint();
							totalSteps++;
							try { Thread.sleep(80); } catch (InterruptedException ex) {}
						}
						break;
					case 2: // Chained Reasoning
						while (autoSolveChainedReasoning()) {
							display.boardPanel.repaint();
							totalSteps++;
							try { Thread.sleep(80); } catch (InterruptedException ex) {}
						}
						break;
					// Probability, Edge Guessing: chỉ gợi ý, không auto-play được
				}
				if (mode >= 3 && mode <= 4) {
					MineTile move = (mode == 3) ? suggestBestProbabilisticMove() : suggestBestEdgeMove();
					if (move != null) {
						String xai = getXAIExplanationForHint(move);
						JOptionPane.showMessageDialog(display.frame,
								"AI Suggestion:\n" +
										"Row " + (move.getR() + 1) + ", Col " + (move.getC() + 1) +
										"\nExplanation:\n" + xai);
					} else {
						JOptionPane.showMessageDialog(display.frame,
								"No safe moves or suggestions left!");
					}
				}
				display.boardPanel.repaint();
			}
		});


		display.setUndoAction(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				undo();
			}
		});
		display.setRedoAction(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				redo();
			}
		});

		display.setExitToMenuAction(e -> {
			SwingUtilities.getWindowAncestor(display.boardPanel).dispose();
			Application.showMainMenu();
		});


		initializeMines(random.nextInt(Level.getNumRows()),random.nextInt(Level.getNumCols())); 

		display.levelButton(); 	
		display.customLevelButton();	
		display.playAgainButton();   
		display.rankingsButton();
		display.visible(true);

	}


	private void initializeMines(int r0, int c0){     
		mineList = new ArrayList<>();  
		mineList.removeAll(mineList);   
		int remains = Level.getMineCount();    
		System.out.println("Console: processing1 (disadvatage_first_click -> renew the board)"); 
		while(remains > 0){     

			int r = random.nextInt(Level.getNumRows());
			int c = random.nextInt(Level.getNumCols());
			
			if( (r== r0-1 && c== c0-1 )|| (r== r0-1 && c== c0 )|| ( r== r0-1 && c== c0+1 )||
				(r== r0 && c== c0-1 )  || (r==r0 && c==c0 )    || (r== r0 && c== c0+1  )  ||
				(r== r0+1 && c== c0-1) || (r==r0+1 && c==c0)   || (r== r0+1 && c==c0+1	)){
					System.out.println("Console: check2 - used to set the first-click on a advantage place");
				}
			else{
				MineTile tile = mainBoard[r][c];    
					
					tile.setEnabled(true);					
					tile.setIcon(Display.unclickedIcon);		

					if(!mineList.contains(tile)){   
						mineList.add(tile);					
						remains--;			
					}
			}
			
		} 
		System.out.println(numOfMinesAround(r0,c0));
	}


	 
	private int countTheMinePosition(int r, int c){
		return( r>=0 && r< Level.getNumRows() && c >=0 && c < Level.getNumCols() && mineList.contains(mainBoard[r][c]) ) ? 1:0;
	}	

	
	private int numOfMinesAround(int r, int c){
		return countTheMinePosition(r - 1, c - 1)+ countTheMinePosition(r - 1, c) + countTheMinePosition(r - 1, c + 1)
                	+ countTheMinePosition(r, c - 1) + countTheMinePosition(r, c + 1) + countTheMinePosition(r + 1, c - 1)
               		+ countTheMinePosition(r + 1, c) + countTheMinePosition(r + 1, c + 1);
	}

	
	private void checkMine(int r, int c){
		if(r<0 || r>=Level.getNumRows() || c < 0 || c>= Level.getNumCols() || !mainBoard[r][c].isEnabled())
			return;		
		
		MineTile tile = mainBoard[r][c]; 

		if (tile.getIcon() == Display.flagIcon)						
        		return;

		tile.setEnabled(false);   
		tilesClicked++;				
		
		int minePositions = numOfMinesAround(r,c); 

		if(minePositions > 0){ 
			tile.setIcon(Display.numberIcons[minePositions]); 
			tile.setDisabledIcon(Display.numberIcons[minePositions]); 
	
		}else{
			
			tile.setIcon(Display.nullIcon); 
			tile.setDisabledIcon(Display.nullIcon); 

			
			checkMine(r - 1, c - 1); 
			checkMine(r - 1, c);		
			checkMine(r - 1, c + 1);		
			checkMine(r, c - 1);	
			checkMine(r, c + 1);		
			checkMine(r + 1, c - 1);
			checkMine(r + 1, c);		
			checkMine(r + 1, c + 1);		
			
			
			
		}
		if (tilesClicked == Level.getNumRows() * Level.getNumCols() - mineList.size()){ 
			Sound.stopSound(3);
			Sound.playSound(4);

			
			Level.setWinGame(true); 		
			time.receiveSignalB();
			display.stopTimer();

			long newTime = time.getTimeDifferenceInSeconds();
			score = calculateScore(newTime);

			System.out.println("src.Time: "+newTime+" seconds");
			System.out.println("Score: "+score);

			ScoreFileHandler.saveScore(newTime, score);

			Display.text("Mines Cleared!, You Won. Score: " + score);
			JOptionPane.showMessageDialog(display.frame,"Congratulation! You won.\nsrc.Time: " + newTime + " seconds.\nScore: " + score);
		
		}
	}


	private void revealMines(){

		for(MineTile tile: mineList)
			tile.setIcon(Display.bombIcon);

		gameOver = true;
		display.stopTimer();

		Display.text("Game Over!, You Lose.");
		JOptionPane.showMessageDialog(display.frame, "Oops! You lose");

	}

	void textUpdate(){
		Display.text("Mines: "+Level.getMineCount()+" | Remaining Flags: "+ numOfPlantedFlags);
	}




	public void setTilesClicked(int n){
		this.tilesClicked = n;	
	}
	public void setFirstClick(boolean n){
			this.firstClick = n;
	}
	public boolean getFirstClick(){
			return this.firstClick;
	}
	public void plantingFlag(){
		numOfPlantedFlags--;
	}
	public void removingFlag(){
		numOfPlantedFlags++;
	}
	public void setNumOfFlags(int n){
		this.numOfPlantedFlags = n;
	}

	// ====== TỰ ĐỘNG GỢI Ý NƯỚC ĐI AN TOÀN HOẶC CẮM CỜ ======
	public boolean autoSolveBasic() {
		boolean acted = false;
		for (int r = 0; r < Level.getNumRows(); ++r) {
			for (int c = 0; c < Level.getNumCols(); ++c) {
				MineTile tile = mainBoard[r][c];
				// Nếu ô đã mở và có số
				if (!tile.isEnabled() && tile.getIcon() != Display.nullIcon) {
					int number = -1;
					for (int i = 1; i <= 8; ++i) {
						if (tile.getIcon() == Display.numberIcons[i]) {
							number = i;
							break;
						}
					}
					if (number == -1) continue; // Không có số trên ô này

					int flagged = 0;
					int unopened = 0;
					ArrayList<MineTile> neighbors = new ArrayList<>();

					// Duyệt 8 ô lân cận
					for (int dr = -1; dr <= 1; ++dr) {
						for (int dc = -1; dc <= 1; ++dc) {
							if (dr == 0 && dc == 0) continue;
							int nr = r + dr, nc = c + dc;
							if (nr < 0 || nr >= Level.getNumRows() || nc < 0 || nc >= Level.getNumCols()) continue;
							MineTile neighbor = mainBoard[nr][nc];
							if (neighbor.getIcon() == Display.flagIcon) flagged++;
							else if (neighbor.isEnabled() && neighbor.getIcon() == Display.unclickedIcon) {
								unopened++;
								neighbors.add(neighbor);
							}
						}
					}

					if (number - flagged == unopened && unopened > 0) {
						for (MineTile t : neighbors) {
							t.setIcon(Display.flagIcon);
							plantingFlag();
						}
						textUpdate();
						acted = true;
					}
					if (flagged == number && unopened > 0) {
						for (MineTile t : neighbors) {
							checkMine(t.getR(), t.getC());
						}
						textUpdate();
						acted = true;
					}
				}
			}
		}
		return acted;
	}


	public MineTile suggestBestProbabilisticMove() {
		double minProb = 2.0; // Xác suất tối thiểu tìm được
		MineTile bestTile = null;

		for (int r = 0; r < Level.getNumRows(); ++r) {
			for (int c = 0; c < Level.getNumCols(); ++c) {
				MineTile tile = mainBoard[r][c];
				// Chỉ xét ô chưa mở, chưa đặt cờ
				if (tile.isEnabled() && tile.getIcon() == Display.unclickedIcon) {
					// Tìm các số lân cận ô này
					int knownNeighbors = 0;
					int flaggedNeighbors = 0;
					int unknownNeighbors = 0;
					int minesLeft = 0;

					for (int dr = -1; dr <= 1; ++dr) {
						for (int dc = -1; dc <= 1; ++dc) {
							if (dr == 0 && dc == 0) continue;
							int nr = r + dr, nc = c + dc;
							if (nr < 0 || nr >= Level.getNumRows() || nc < 0 || nc >= Level.getNumCols()) continue;
							MineTile neighbor = mainBoard[nr][nc];
							// Nếu là ô số đã mở
							for (int n = 1; n <= 8; ++n) {
								if (!neighbor.isEnabled() && neighbor.getIcon() == Display.numberIcons[n]) {
									knownNeighbors++;
									// Đếm cờ quanh ô số
									int flags = 0, unknown = 0;
									for (int dr2 = -1; dr2 <= 1; ++dr2) {
										for (int dc2 = -1; dc2 <= 1; ++dc2) {
											int nx = nr + dr2, ny = nc + dc2;
											if (nx < 0 || nx >= Level.getNumRows() || ny < 0 || ny >= Level.getNumCols())
												continue;
											MineTile t2 = mainBoard[nx][ny];
											if (t2.getIcon() == Display.flagIcon) flags++;
											if (t2.isEnabled() && t2.getIcon() == Display.unclickedIcon) unknown++;
										}
									}
									// Số mìn còn lại quanh ô số
									int left = n - flags;
									if (unknown > 0) {
										double prob = (double) left / unknown;
										// Lấy giá trị lớn nhất khi có nhiều số lân cận
										if (prob < minProb) {
											minProb = prob;
											bestTile = tile;
										}
									}
								}
							}
						}
					}
				}
			}
		}

		// Nếu không tìm được, chọn ô bất kỳ còn lại (random)
		if (bestTile == null) {
			ArrayList<MineTile> candidates = new ArrayList<>();
			for (int r = 0; r < Level.getNumRows(); ++r)
				for (int c = 0; c < Level.getNumCols(); ++c) {
					MineTile tile = mainBoard[r][c];
					if (tile.isEnabled() && tile.getIcon() == Display.unclickedIcon)
						candidates.add(tile);
				}
			if (!candidates.isEmpty()) {
				bestTile = candidates.get(new Random().nextInt(candidates.size()));
			}
		}
		return bestTile;
	}

	public void saveStateForUndo() {
		undoStack.push(new GameState(this));
		// Khi có hành động mới, xóa redoStack (không còn nhánh để redo)
		redoStack.clear();
	}

	public void undo() {
		if (!undoStack.isEmpty()) {
			redoStack.push(new GameState(this)); // Lưu trạng thái hiện tại vào redo
			GameState prev = undoStack.pop();
			prev.restore(this);
			display.boardPanel.repaint();
		} else {
			JOptionPane.showMessageDialog(display.frame, "No more undo available!");
		}
	}

	public void redo() {
		if (!redoStack.isEmpty()) {
			undoStack.push(new GameState(this)); // Lưu trạng thái hiện tại vào undo
			GameState next = redoStack.pop();
			next.restore(this);
			display.boardPanel.repaint();
		} else {
			JOptionPane.showMessageDialog(display.frame, "No more redo available!");
		}
	}

	public int getAIMode() {
		return display.aiModeCombo.getSelectedIndex();
	}


	public boolean autoSolveChainedReasoning() {
		// Ý tưởng: Nếu 2 ô số kề nhau, 1 ô có số lớn hơn, kiểm tra subset các ô chưa mở.
		boolean acted = false;
		for (int r = 0; r < Level.getNumRows(); ++r) {
			for (int c = 0; c < Level.getNumCols(); ++c) {
				MineTile tile1 = mainBoard[r][c];
				if (!tile1.isEnabled()) {
					int num1 = -1;
					for (int i = 1; i <= 8; ++i)
						if (tile1.getIcon() == Display.numberIcons[i]) num1 = i;
					if (num1 == -1) continue;

					// Duyệt các ô số hàng xóm
					for (int dr = -1; dr <= 1; ++dr) {
						for (int dc = -1; dc <= 1; ++dc) {
							if (Math.abs(dr) + Math.abs(dc) != 1) continue; // chỉ trên, dưới, trái, phải
							int nr = r + dr, nc = c + dc;
							if (nr < 0 || nr >= Level.getNumRows() || nc < 0 || nc >= Level.getNumCols()) continue;
							MineTile tile2 = mainBoard[nr][nc];
							if (!tile2.isEnabled()) {
								int num2 = -1;
								for (int j = 1; j <= 8; ++j)
									if (tile2.getIcon() == Display.numberIcons[j]) num2 = j;
								if (num2 == -1 || num1 == num2) continue;

								// Lấy các ô chưa mở quanh tile1 và tile2
								ArrayList<MineTile> u1 = new ArrayList<>(), u2 = new ArrayList<>();
								for (int dr2 = -1; dr2 <= 1; ++dr2)
									for (int dc2 = -1; dc2 <= 1; ++dc2) {
										int rr = r + dr2, cc = c + dc2;
										if (rr < 0 || rr >= Level.getNumRows() || cc < 0 || cc >= Level.getNumCols()) continue;
										MineTile t = mainBoard[rr][cc];
										if (t.isEnabled() && t.getIcon() == Display.unclickedIcon) u1.add(t);
									}
								for (int dr2 = -1; dr2 <= 1; ++dr2)
									for (int dc2 = -1; dc2 <= 1; ++dc2) {
										int rr = nr + dr2, cc = nc + dc2;
										if (rr < 0 || rr >= Level.getNumRows() || cc < 0 || cc >= Level.getNumCols()) continue;
										MineTile t = mainBoard[rr][cc];
										if (t.isEnabled() && t.getIcon() == Display.unclickedIcon) u2.add(t);
									}
								// Nếu u2 chứa toàn bộ u1 (u1 là subset của u2), thử suy luận
								if (u2.containsAll(u1) && u2.size() > u1.size()) {
									int diff = num2 - num1;
									if (diff >= 0 && (u2.size() - u1.size()) == diff) {
										// Các ô trong u2 - u1 đều là mìn => cắm cờ!
										for (MineTile t : u2) {
											if (!u1.contains(t) && t.getIcon() == Display.unclickedIcon) {
												t.setIcon(Display.flagIcon);
												plantingFlag();
												acted = true;
											}
										}
										if (acted) textUpdate();
									}
								}
							}
						}
					}
				}
			}
		}
		return acted;
	}

	public MineTile suggestBestEdgeMove() {
		double minProb = 2.0;
		MineTile bestTile = null;
		int rows = Level.getNumRows(), cols = Level.getNumCols();
		ArrayList<MineTile> edgeTiles = new ArrayList<>();

		// Lấy các ô ở cạnh (edge): r==0, r==rows-1, c==0, c==cols-1
		for (int r = 0; r < rows; ++r) {
			for (int c = 0; c < cols; ++c) {
				if (mainBoard[r][c].isEnabled() && mainBoard[r][c].getIcon() == Display.unclickedIcon) {
					if (r == 0 || r == rows - 1 || c == 0 || c == cols - 1) {
						edgeTiles.add(mainBoard[r][c]);
					}
				}
			}
		}

		// Ưu tiên chọn ô edge có xác suất thấp nhất (nếu có ô số cạnh nó)
		for (MineTile tile : edgeTiles) {
			int flagged = 0, unopened = 0, num = 0;
			double prob = 1.0;
			for (int dr = -1; dr <= 1; ++dr)
				for (int dc = -1; dc <= 1; ++dc) {
					int nr = tile.getR() + dr, nc = tile.getC() + dc;
					if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
					MineTile neighbor = mainBoard[nr][nc];
					for (int i = 1; i <= 8; ++i) {
						if (!neighbor.isEnabled() && neighbor.getIcon() == Display.numberIcons[i]) {
							num = i;
							// Đếm flag quanh ô số này
							int flags = 0, unopen = 0;
							for (int dr2 = -1; dr2 <= 1; ++dr2)
								for (int dc2 = -1; dc2 <= 1; ++dc2) {
									int nx = nr + dr2, ny = nc + dc2;
									if (nx < 0 || nx >= rows || ny < 0 || ny >= cols) continue;
									MineTile t2 = mainBoard[nx][ny];
									if (t2.getIcon() == Display.flagIcon) flags++;
									if (t2.isEnabled() && t2.getIcon() == Display.unclickedIcon) unopen++;
								}
							int left = num - flags;
							if (unopen > 0) {
								prob = Math.min(prob, (double) left / unopen);
							}
						}
					}
				}
			if (prob < minProb) {
				minProb = prob;
				bestTile = tile;
			}
		}

		// Nếu không có ô edge nào đặc biệt, chọn random ô ở edge
		if (bestTile == null && !edgeTiles.isEmpty()) {
			bestTile = edgeTiles.get(new Random().nextInt(edgeTiles.size()));
		}
		// Nếu vẫn không có, chọn random ô bất kỳ còn lại
		if (bestTile == null) {
			ArrayList<MineTile> candidates = new ArrayList<>();
			for (int r = 0; r < rows; ++r)
				for (int c = 0; c < cols; ++c)
					if (mainBoard[r][c].isEnabled() && mainBoard[r][c].getIcon() == Display.unclickedIcon)
						candidates.add(mainBoard[r][c]);
			if (!candidates.isEmpty()) bestTile = candidates.get(new Random().nextInt(candidates.size()));
		}
		return bestTile;
	}

	public String getXAIExplanationForHint(MineTile tile) {
		if (tile == null) return "No explanation available.";

		int r = tile.getR(), c = tile.getC();
		StringBuilder explain = new StringBuilder();

		// 1. Lý do dựa trên xác suất (nếu là AI xác suất)
		double prob = 1.0;
		int rows = Level.getNumRows(), cols = Level.getNumCols();
		for (int dr = -1; dr <= 1; ++dr) {
			for (int dc = -1; dc <= 1; ++dc) {
				if (dr == 0 && dc == 0) continue;
				int nr = r + dr, nc = c + dc;
				if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
				MineTile neighbor = mainBoard[nr][nc];
				for (int num = 1; num <= 8; ++num) {
					if (!neighbor.isEnabled() && neighbor.getIcon() == Display.numberIcons[num]) {
						// Đếm flag và unopened quanh neighbor
						int flags = 0, unopen = 0;
						for (int dr2 = -1; dr2 <= 1; ++dr2)
							for (int dc2 = -1; dc2 <= 1; ++dc2) {
								int nx = nr + dr2, ny = nc + dc2;
								if (nx < 0 || nx >= rows || ny < 0 || ny >= cols) continue;
								MineTile t2 = mainBoard[nx][ny];
								if (t2.getIcon() == Display.flagIcon) flags++;
								if (t2.isEnabled() && t2.getIcon() == Display.unclickedIcon) unopen++;
							}
						int left = num - flags;
						if (unopen > 0) {
							double thisProb = (double) left / unopen;
							prob = Math.min(prob, thisProb);
							explain.append("Near [" + (nr+1) + "," + (nc+1) + "]: Number " + num +
									", " + left + " mine(s) in " + unopen + " unopened.\n");
						}
					}
				}
			}
		}

		if (explain.length() > 0) {
			explain.append("\nEstimated probability this tile is a mine: " + String.format("%.2f%%", prob * 100));
		} else {
			explain.append("Chosen because it is an edge/corner, or there are no clues available.");
		}
		return explain.toString();
	}

	private int calculateScore(long timeInSeconds) {
		int baseScore = 1000;
		// Càng nhanh điểm càng cao, mỗi giây trừ đi 10 điểm
		int sc = Math.max(0, baseScore - (int)timeInSeconds * 10);
		// Có thể thêm + flag đúng, hoặc hệ số theo level ở đây nếu muốn
		return sc;
	}



}