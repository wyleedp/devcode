package com.github.devcode.studio.example;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class JProgressBarExample4 {

	private void daa() {
		// Frame
		JFrame frame = new JFrame("Frame");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		frame.setSize(frame.getWidth() + 55, frame.getHeight() + 55);
		
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frm = frame.getSize();
        int xpos = (int)(screen.getWidth() / 2 - frm.getWidth() / 2);
        int ypos = (int)(screen.getHeight() / 2 - frm.getHeight() / 2);
        frame.setLocation(xpos, ypos);

		// Button
		JButton jButton = new JButton("State");
		frame.add(jButton, BorderLayout.NORTH);

		// Progress Bar
		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);

		// Text for progress bar
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(progressBar);
		panel.add(new JLabel("Please wait......."), BorderLayout.PAGE_START);

		// linking
		panel.add(progressBar);
		frame.add(panel, BorderLayout.SOUTH);

		boolean[] state = { false };
		jButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				state[0] = !state[0];
				state();
				
			}

			private void state() {
				if (state[0] == true) {
					panel.setVisible(false);
				} else {
					panel.setVisible(true);
				}

			}
		});
	}
	
	public static void main(String[] args) {
		JProgressBarExample4 progressBarExample4 = new JProgressBarExample4();
		progressBarExample4.daa();
	}

}
