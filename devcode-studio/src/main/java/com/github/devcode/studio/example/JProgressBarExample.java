package com.github.devcode.studio.example;

import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

public class JProgressBarExample {

	static int MAXIMUM = 100;
	static JFrame jFrame = new JFrame("JProgress Demo");
	// create horizontal progress bar
	static JProgressBar progressBar = new JProgressBar();

	public static void main(String[] args) {
		progressBar.setMinimum(0);
		progressBar.setMaximum(MAXIMUM);
		progressBar.setStringPainted(true);

		// add progress bar to the content pane layer
		jFrame.setLayout(new FlowLayout());
		jFrame.getContentPane().add(progressBar);

		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setSize(300, 200);
		jFrame.setVisible(true);

		// update progressbar by displaying text on it
		int currentNumber = 0;
		try {
			while (currentNumber <= MAXIMUM) {
				// set text considering the level to which the bar is colored/filled
				if (currentNumber > 30 && currentNumber < 70)
					progressBar.setString("wait for sometime");
				else if (currentNumber > 70)
					progressBar.setString("almost finished loading");
				else
					progressBar.setString("loading started");

				// color/fill the menu bar
				progressBar.setValue(currentNumber + 10);
				// delay thread
				Thread.sleep(3000);
				currentNumber += 20;
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
