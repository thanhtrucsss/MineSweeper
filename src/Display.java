package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Display {
	private JLabel timerLabel = new JLabel("Time: 0s");
	private javax.swing.Timer swingTimer;
	private int secondsElapsed = 0;

	public JFrame frame = new JFrame("Minesweeper");

	public JButton levelButton = new JButton("Level");
	public JButton customLevelButton = new JButton("Custom");
	public JButton playAgainButton = new JButton("Play Again");
	public JButton rankingsButton = new JButton("Rankings");
	public JButton hintButton = new JButton("Hint");
	public JButton autoPlayButton = new JButton("Auto-play");
	public JButton undoButton = new JButton("Undo");
	public JButton redoButton = new JButton("Redo");

	// Menu AI mode
	public JComboBox<String> aiModeCombo = new JComboBox<>(new String[]{
			"Smart Sequence (Auto)", "Basic Logic", "Probability", "Edge Guessing", "Explainable AI"
	});
	public JButton exitToMenuButton = new JButton("Exit to Menu");

	public static JLabel textLabel = new JLabel();
	public JPanel boardPanel = new JPanel();
	public JPanel buttonPanel = new JPanel();

	public static ImageIcon flagIcon = new ImageIcon("src/img/flag.png");
	public static ImageIcon bombIcon = new ImageIcon("src/img/bomb.png");
	public static ImageIcon unclickedIcon = new ImageIcon("src/img/unclicked.png");
	public static ImageIcon nullIcon = new ImageIcon("src/img/null.png");
	public static ImageIcon[] numberIcons = new ImageIcon[9];
	public Display(int rows, int cols){
		Color bgMain = new Color(245, 248, 252);
		Color bgBoard = new Color(255,255,255);
		Color bgToolbar = new Color(235,242,250);
		Color borderColor = new Color(180, 195, 230);

		UIManager.put("ToolTip.background", new Color(255, 255, 204));
		UIManager.put("ToolTip.foreground", new Color(32, 32, 32));
		UIManager.put("ToolTip.font", new Font("Arial", Font.BOLD, 16));
		UIManager.put("ToolTip.border", BorderFactory.createLineBorder(Color.ORANGE, 2));

		frame.setSize(cols * MineTile.TILESIZE + 40, rows * MineTile.TILESIZE + 160);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		ImageIcon gameIcon = new ImageIcon("src/img/gameicon.png");
		frame.setIconImage(gameIcon.getImage());
		frame.getContentPane().setBackground(bgMain);

		for (int i = 1; i <= 8; i++)
			numberIcons[i] = new ImageIcon("src/img/" + i + ".png");

		JPanel statusPanel = new JPanel();
		statusPanel.setBackground(bgToolbar);
		statusPanel.setBorder(BorderFactory.createMatteBorder(0,0,2,0,borderColor));
		statusPanel.setLayout(new BorderLayout());

		timerLabel.setFont(new Font("Arial", Font.BOLD, 18));
		timerLabel.setHorizontalAlignment(JLabel.CENTER);
		timerLabel.setForeground(new Color(30, 85, 110));
		timerLabel.setOpaque(false);
		timerLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		statusPanel.add(timerLabel, BorderLayout.WEST);

		textLabel.setFont(new Font("Arial", Font.BOLD, getTextSize()));
		textLabel.setHorizontalAlignment(JLabel.CENTER);
		textLabel.setOpaque(true);
		textLabel.setBackground(Color.WHITE);
		textLabel.setForeground(new Color(55, 74, 120));
		textLabel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(190,210,240), 2, true),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)
		));
		statusPanel.add(textLabel, BorderLayout.CENTER);

		styleButton(exitToMenuButton);
		exitToMenuButton.setForeground(new Color(200, 70, 70));
		exitToMenuButton.setToolTipText("Exit to main menu");
		JPanel exitPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 7));
		exitPanel.setOpaque(false);
		exitPanel.add(exitToMenuButton);
		statusPanel.add(exitPanel, BorderLayout.EAST);

		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 18, 5));
		buttonPanel.setBackground(bgToolbar);

		JButton[] allButtons = {
				playAgainButton, levelButton, rankingsButton, customLevelButton,
				hintButton, autoPlayButton, undoButton, redoButton
		};

		for (JButton btn : allButtons) {
			styleButton(btn);
			buttonPanel.add(btn);
		}

		undoButton.setToolTipText("Undo last AI/player step.");
		redoButton.setToolTipText("Redo step if available.");

		JLabel aiLabel = new JLabel("AI Mode:");
		aiLabel.setFont(new Font("Arial", Font.BOLD, 14));
		buttonPanel.add(aiLabel);
		aiModeCombo.setFont(new Font("Arial", Font.PLAIN, 14));
		aiModeCombo.setPreferredSize(new Dimension(150, 28));
		buttonPanel.add(aiModeCombo);

		// --- BOARD ---
		boardPanel.setBackground(bgBoard);
		boardPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(borderColor, 3, true),
				BorderFactory.createEmptyBorder(16, 16, 16, 16)
		));
		boardPanel.setLayout(new GridLayout(rows, cols, 3, 3));

		frame.add(statusPanel, BorderLayout.NORTH);
		frame.add(buttonPanel, BorderLayout.SOUTH);
		frame.add(boardPanel, BorderLayout.CENTER);
	}

	private void styleButton(JButton btn) {
		btn.setFont(new Font("Arial", Font.BOLD, 15));
		btn.setFocusPainted(false);
		btn.setBackground(new Color(220, 228, 241));
		btn.setForeground(new Color(40, 44, 52));
		btn.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(100, 149, 237), 1, true),
				BorderFactory.createEmptyBorder(7, 18, 7, 18)
		));
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btn.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				btn.setBackground(new Color(189, 213, 234));
			}
			public void mouseExited(java.awt.event.MouseEvent evt) {
				btn.setBackground(new Color(220, 228, 241));
			}
		});
	}

	public int getTextSize() {
		return Level.getNumCols() < 7 ? 17 : 25;
	}

	public void visible(boolean n) {
		frame.setVisible(n);
	}
	public static void text(String x) {
		textLabel.setText(x);
	}

	public void rankingsButton() {
		rankingsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] options = {"Easy", "Medium", "Hard", "All Modes"};
				int choice = JOptionPane.showOptionDialog(frame,
						"Choose ranking to view:",
						"Rankings",
						JOptionPane.DEFAULT_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						options,
						options[0]);
				switch (choice) {
					case 0:
						JOptionPane.showMessageDialog(frame, ScoreFileHandler.toStringScore("src/txt/EasyLevelTimeRecords.txt"));
						break;
					case 1:
						JOptionPane.showMessageDialog(frame, ScoreFileHandler.toStringScore("src/txt/MediumLevelTimeRecords.txt"));
						break;
					case 2:
						JOptionPane.showMessageDialog(frame, ScoreFileHandler.toStringScore("src/txt/HardLevelTimeRecords.txt"));
						break;
					case 3:
						JOptionPane.showMessageDialog(frame, ScoreFileHandler.allModesRanking());
						break;
				}
			}
		});
	}

	public void playAgainButton() {
		playAgainButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.getWindowAncestor(boardPanel).dispose();
				new Minesweeper();
			}
		});
	}

	public void customLevelButton() {
		customLevelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String input2 = JOptionPane.showInputDialog(frame, "Enter length (5 < Length < 25): ");
				String input3 = JOptionPane.showInputDialog(frame, "Enter width (5 < Width < 15): ");
				try {
					int cols = Integer.parseInt(input2);
					int rows = Integer.parseInt(input3);
					int upperBound = cols * rows -30;
					String input1 = JOptionPane.showInputDialog(frame, "Enter number of mines (5 < Mines < "+upperBound+"): ");
					int mines = Integer.parseInt(input1);
					if(cols <= 5 || cols  >= 25 || rows <=5 || rows >= 15 || mines <= 5 || mines >= upperBound){
						JOptionPane.showMessageDialog(frame, "Cannot create a new game with these characteristics. Try again!");
					}
					else{
						Level.setLevel(mines,rows,cols);
						SwingUtilities.getWindowAncestor(boardPanel).dispose();
						new Minesweeper();
					}
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(frame, "Invalid input. Please enter integers.");
				}
			}
		});
	}

	public void levelButton() {
		levelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] options = {"Easy", "Medium", "Hard"};
				int choice = JOptionPane.showOptionDialog(frame,
						"Please, choose the level:",
						"Choose Level",
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						options,
						null);
				switch (choice) {
					case JOptionPane.YES_OPTION:
						Level.setLevelEasy();
						break;
					case JOptionPane.NO_OPTION:
						Level.setLevelMedium();
						break;
					case JOptionPane.CANCEL_OPTION:
						Level.setLevelHard();
						break;
					default:
						System.out.println("Console: No level selected");
						return;
				}
				SwingUtilities.getWindowAncestor(boardPanel).dispose();
				new Minesweeper();
			}
		});
	}

	public void setHintAction(ActionListener al) {
		hintButton.addActionListener(al);
	}

	public void setAutoPlayAction(ActionListener al) {
		autoPlayButton.addActionListener(al);
	}

	public void setUndoAction(ActionListener al) {
		undoButton.addActionListener(al);
	}
	public void setRedoAction(ActionListener al) {
		redoButton.addActionListener(al);
	}
	public void setExitToMenuAction(ActionListener al) {
		exitToMenuButton.addActionListener(al);
	}

	// Timer
	public void startTimer() {
		if (swingTimer != null) swingTimer.stop();
		secondsElapsed = 0;
		timerLabel.setText("Time: 0s");
		swingTimer = new javax.swing.Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				secondsElapsed++;
				timerLabel.setText("Time: " + secondsElapsed + "s");
			}
		});
		swingTimer.start();
	}
	public void stopTimer() {
		if (swingTimer != null) swingTimer.stop();
	}
	public int getElapsedTime() {
		return secondsElapsed;
	}


}
