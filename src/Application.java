package src;

import javax.swing.*;
import java.awt.*;

public class Application {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(Application::showMainMenu);
	}

	public static void showMainMenu() {
		JFrame menuFrame = new JFrame("src.Minesweeper - Menu");
		menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		menuFrame.setSize(420, 570);
		menuFrame.setLocationRelativeTo(null);

		JPanel bgPanel = new JPanel() {
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				GradientPaint gp = new GradientPaint(
						0, 0, new Color(243, 246, 253),
						0, getHeight(), new Color(210, 232, 245));
				g2.setPaint(gp);
				g2.fillRect(0, 0, getWidth(), getHeight());
			}
		};
		bgPanel.setLayout(new GridBagLayout());

		// Menu content panel
		JPanel menuPanel = new JPanel();
		menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
		menuPanel.setBackground(new Color(255, 255, 255, 245));
		menuPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(30, 40, 30, 40),
				BorderFactory.createMatteBorder(1, 1, 8, 8, new Color(100, 149, 237, 32))
		));

		// Logo/icon
		JLabel logo = new JLabel();
		try {
			ImageIcon icon = new ImageIcon("src/img/gameicon.png");
			if (icon.getIconWidth() > 0)
				logo.setIcon(new ImageIcon(icon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH)));
		} catch (Exception e) { }
		logo.setAlignmentX(Component.CENTER_ALIGNMENT);

		JLabel title = new JLabel("MINESWEEPER");
		title.setFont(new Font("Arial", Font.BOLD, 34));
		title.setForeground(new Color(62, 80, 160));
		title.setAlignmentX(Component.CENTER_ALIGNMENT);

		JButton btnStart = styledButton("Start Game");
		JButton btnVolume = styledButton("Volume");
		JButton btnHowTo = styledButton("How to Play");
		JButton btnScore = styledButton("Score History");
		JButton btnExit = styledButton("Exit");

		menuPanel.add(logo);
		menuPanel.add(Box.createVerticalStrut(8));
		menuPanel.add(title);
		menuPanel.add(Box.createVerticalStrut(25));
		menuPanel.add(btnStart);
		menuPanel.add(Box.createVerticalStrut(16));
		menuPanel.add(btnVolume);
		menuPanel.add(Box.createVerticalStrut(16));
		menuPanel.add(btnHowTo);
		menuPanel.add(Box.createVerticalStrut(16));
		menuPanel.add(btnScore);
		menuPanel.add(Box.createVerticalStrut(32));
		menuPanel.add(btnExit);

		bgPanel.add(menuPanel, new GridBagConstraints());

		// Button Start
		btnStart.addActionListener(e -> {
			menuFrame.dispose();
			new Minesweeper();
		});

		// Button Volume
		btnVolume.addActionListener(e -> showVolumeDialog(menuFrame));

		// Button How to play
		btnHowTo.addActionListener(e -> {
			JOptionPane.showMessageDialog(menuFrame,
					"<html><b>How to Play:</b><br>" +
							"- Left click: open a cell<br>" +
							"- Right click: flag/unflag<br>" +
							"- Hint: ask AI for a move<br>" +
							"- Auto-play: AI solves automatically<br><br>" +
							"Choose AI mode in the game (bottom menu).<br>" +
							"Goal: Open all safe cells, avoid mines!</html>",
					"How to Play", JOptionPane.INFORMATION_MESSAGE);
		});

		btnScore.addActionListener(e -> {
			String scoreMsg = ScoreFileHandler.toStringScore("src/txt/EasyLevelTimeRecords.txt")
					+ "\n" + ScoreFileHandler.toStringScore("src/txt/MediumLevelTimeRecords.txt")
					+ "\n" + ScoreFileHandler.toStringScore("src/txt/HardLevelTimeRecords.txt");
			JOptionPane.showMessageDialog(menuFrame, scoreMsg, "Score History", JOptionPane.INFORMATION_MESSAGE);
		});

		btnExit.addActionListener(e -> System.exit(0));

		menuFrame.setContentPane(bgPanel);
		menuFrame.setVisible(true);
	}

	// Modify the volume control dialog
	private static void showVolumeDialog(JFrame parent) {
		JDialog dialog = new JDialog(parent, "Volume Control", true);
		dialog.setSize(330, 220);
		dialog.setResizable(false);
		dialog.setLocationRelativeTo(parent);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(new Color(248, 252, 255));
		panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

		JLabel volLabel = new JLabel("Volume:");
		volLabel.setFont(new Font("Arial", Font.BOLD, 17));
		volLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		JSlider volSlider = new JSlider(0, 100, Sound.getVolumePercent());
		volSlider.setMajorTickSpacing(25);
		volSlider.setPaintTicks(true);
		volSlider.setPaintLabels(true);
		volSlider.setMaximumSize(new Dimension(220, 44));
		volSlider.setAlignmentX(Component.CENTER_ALIGNMENT);

		JToggleButton muteButton = new JToggleButton("Mute");
		muteButton.setFont(new Font("Arial", Font.BOLD, 15));
		muteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		muteButton.setBackground(new Color(235,242,250));
		muteButton.setBorder(BorderFactory.createLineBorder(new Color(100, 149, 237), 1, true));
		muteButton.setSelected(Sound.isMuted != null && Sound.isMuted()); // Nếu có trạng thái mute

		volSlider.addChangeListener(e -> {
			int value = volSlider.getValue();
			Sound.setVolumePercent(value);
			if (value == 0) muteButton.setSelected(true);
			else muteButton.setSelected(false);
		});
		muteButton.addActionListener(e -> {
			boolean muted = muteButton.isSelected();
			Sound.setMuted(muted);
			if (muted) volSlider.setValue(0);
			else volSlider.setValue(Sound.getVolumePercent() == 0 ? 30 : Sound.getVolumePercent());
		});

		JButton closeBtn = new JButton("Close");
		closeBtn.setFont(new Font("Arial", Font.PLAIN, 15));
		closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		closeBtn.setBackground(new Color(220, 228, 241));
		closeBtn.addActionListener(ev -> dialog.dispose());

		panel.add(volLabel);
		panel.add(Box.createVerticalStrut(15));
		panel.add(volSlider);
		panel.add(Box.createVerticalStrut(10));
		panel.add(muteButton);
		panel.add(Box.createVerticalStrut(18));
		panel.add(closeBtn);

		dialog.setContentPane(panel);
		dialog.setVisible(true);
	}

	private static JButton styledButton(String text) {
		JButton btn = new JButton(text);
		btn.setFont(new Font("Arial", Font.BOLD, 20));
		btn.setAlignmentX(Component.CENTER_ALIGNMENT);
		btn.setFocusPainted(false);
		btn.setBackground(new Color(232, 243, 254));
		btn.setForeground(new Color(44, 57, 85));
		btn.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(100, 149, 237), 2, true),
				BorderFactory.createEmptyBorder(10, 38, 10, 38)
		));
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btn.setMaximumSize(new Dimension(260, 48));
		btn.setMinimumSize(new Dimension(200, 40));
		btn.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				btn.setBackground(new Color(189, 213, 234));
				btn.setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createLineBorder(new Color(100, 149, 237, 200), 3, true),
						BorderFactory.createEmptyBorder(11, 40, 11, 40)
				));
			}
			public void mouseExited(java.awt.event.MouseEvent evt) {
				btn.setBackground(new Color(232, 243, 254));
				btn.setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createLineBorder(new Color(100, 149, 237), 2, true),
						BorderFactory.createEmptyBorder(10, 38, 10, 38)
				));
			}
		});
		return btn;
	}
}
