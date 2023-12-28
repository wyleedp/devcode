package com.github.devcode.studio.example;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class JProgressBarExample3 extends JPanel {

	private static final long serialVersionUID = 6613579498641763996L;

	JProgressBar pbar;

	static final int MY_MINIMUM = 0;

	static final int MY_MAXIMUM = 100;

	public JProgressBarExample3() {
		// initialize Progress Bar
		pbar = new JProgressBar();
		pbar.setMinimum(MY_MINIMUM);
		pbar.setMaximum(MY_MAXIMUM);
		// add to JPanel
		add(pbar, BorderLayout.CENTER);
	}

	public void updateBar(int newValue) {
		pbar.setValue(newValue);
	}

	public static void main(String args[]) {

		final JProgressBarExample3 it = new JProgressBarExample3();

		JFrame frame = new JFrame("Progress Bar Example");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(it);
		frame.pack();
		frame.setVisible(true);
		
		frame.setSize(300, 70);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frm = frame.getSize();
        int xpos = (int)(screen.getWidth() / 2 - frm.getWidth() / 2);
        int ypos = (int)(screen.getHeight() / 2 - frm.getHeight() / 2);
        frame.setLocation(xpos, ypos);

		// run a loop to demonstrate raising
		for (int i = MY_MINIMUM; i <= MY_MAXIMUM; i++) {
			final int percent = i;
			try {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						it.updateBar(percent);
					}
				});
				java.lang.Thread.sleep(100);
			} catch (InterruptedException e) {
				;
			}
		}
	}
}