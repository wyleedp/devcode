package com.github.devcode.studio.example;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * 실행시간이 긴 작업을 구현한 프로그레스바 예제
 *   - 시작/종료 버튼
 *   - 실행이력을 확인할 수 있는 TextArea도 존재
 */
public class JProgressBarExample5 extends JPanel implements ActionListener {
    
	private static final long serialVersionUID = -8170814348736096862L;

	public final static int ONE_SECOND = 1000;
 
    private JProgressBar progressBar;
    private Timer timer;
    private JButton startButton;
    private LongTask task;
    private JTextArea taskOutput;
    private String newline = "\n";
 
    public JProgressBarExample5() {
        super(new BorderLayout());
        task = new LongTask();
 
        // Create the demo's UI.
        startButton = new JButton("Start");
        startButton.setActionCommand("start");
        startButton.addActionListener(this);
 
        progressBar = new JProgressBar(0, task.getLengthOfTask());
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
 
        taskOutput = new JTextArea(5, 20);
        taskOutput.setMargin(new Insets(5, 5, 5, 5));
        taskOutput.setEditable(false);
        taskOutput.setCursor(null); // inherit the panel's cursor
                                    // see bug 4851758
 
        JPanel panel = new JPanel();
        panel.add(startButton);
        panel.add(progressBar);
 
        add(panel, BorderLayout.PAGE_START);
        add(new JScrollPane(taskOutput), BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
 
        // Create a timer.
        timer = new Timer(ONE_SECOND, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
                progressBar.setValue(task.getCurrent());
                String s = task.getMessage();
                if (s != null) {
                    taskOutput.append(s + newline);
                    taskOutput.setCaretPosition(taskOutput.getDocument().getLength());
                }
                if (task.isDone()) {
                    Toolkit.getDefaultToolkit().beep();
                    timer.stop();
                    startButton.setEnabled(true);
                    setCursor(null); // turn off the wait cursor
                    progressBar.setValue(progressBar.getMinimum());
                }
			}
        });
    }
 
    /**
     * Called when the user presses the start button.
     */
    public void actionPerformed(ActionEvent evt) {
        startButton.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        task.go();
        timer.start();
    }
 
    /**
     * Create the GUI and show it. For thread safety, this method should be invoked
     * from the event-dispatching thread.
     */
    private static void createAndShowGUI() {
        // Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);
 
        // Create and set up the window.
        JFrame frame = new JFrame("ProgressBarDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        // Create and set up the content pane.
        JComponent newContentPane = new JProgressBarExample5();
        newContentPane.setOpaque(true); // content panes must be opaque
        frame.setContentPane(newContentPane);
 
        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }
 
    public static void main(String[] args) {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
 
class LongTask {
    private int lengthOfTask;
    private int current = 0;
    private boolean done = false;
    private boolean canceled = false;
    private String statMessage;
 
    public LongTask() {
        // Compute length of task...
        // In a real program, this would figure out
        // the number of bytes to read or whatever.
        lengthOfTask = 1000;
    }
 
    /**
     * Called from ProgressBarDemo to start the task.
     */
    public void go() {
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                current = 0;
                done = false;
                canceled = false;
                statMessage = null;
                return new ActualTask();
            }
        };
        worker.start();
    }
 
    /**
     * Called from ProgressBarDemo to find out how much work needs to be done.
     */
    public int getLengthOfTask() {
        return lengthOfTask;
    }
 
    /**
     * Called from ProgressBarDemo to find out how much has been done.
     */
    public int getCurrent() {
        return current;
    }
 
    public void stop() {
        canceled = true;
        statMessage = null;
    }
 
    /**
     * Called from ProgressBarDemo to find out if the task has completed.
     */
    public boolean isDone() {
        return done;
    }
 
    /**
     * Returns the most recent status message, or null if there is no current status
     * message.
     */
    public String getMessage() {
        return statMessage;
    }
 
    /**
     * The actual long running task. This runs in a SwingWorker thread.
     */
    class ActualTask {
        ActualTask() {
            // Fake a long task,
            // making a random amount of progress every second.
            while (!canceled && !done) {
                try {
                    Thread.sleep(1000); // sleep for a second
                    current += Math.random() * 100; // make some progress
                    if (current >= lengthOfTask) {
                        done = true;
                        current = lengthOfTask;
                    }
                    statMessage = "Completed " + current + " out of " + lengthOfTask + ".";
                } catch (InterruptedException e) {
                    System.out.println("ActualTask interrupted");
                }
            }
        }
    }
}
 
/**
 * This is the 3rd version of SwingWorker (also known as SwingWorker 3), an
 * abstract class that you subclass to perform GUI-related work in a dedicated
 * thread. For instructions on and examples of using this class, see:
 * 
 * http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html
 *
 * Note that the API changed slightly in the 3rd version: You must now invoke
 * start() on the SwingWorker after creating it.
 */
abstract class SwingWorker {
    private Object value; // see getValue(), setValue()
 
    /**
     * Class to maintain reference to current worker thread under separate
     * synchronization control.
     */
    private static class ThreadVar {
        private Thread thread;
 
        ThreadVar(Thread t) {
            thread = t;
        }
 
        synchronized Thread get() {
            return thread;
        }
 
        synchronized void clear() {
            thread = null;
        }
    }
 
    private ThreadVar threadVar;
 
    /**
     * Get the value produced by the worker thread, or null if it hasn't been
     * constructed yet.
     */
    protected synchronized Object getValue() {
        return value;
    }
 
    /**
     * Set the value produced by worker thread
     */
    private synchronized void setValue(Object x) {
        value = x;
    }
 
    /**
     * Compute the value to be returned by the <code>get</code> method.
     */
    public abstract Object construct();
 
    /**
     * Called on the event dispatching thread (not on the worker thread) after the
     * <code>construct</code> method has returned.
     */
    public void finished() {
    }
 
    /**
     * A new method that interrupts the worker thread. Call this method to force the
     * worker to stop what it's doing.
     */
    public void interrupt() {
        Thread t = threadVar.get();
        if (t != null) {
            t.interrupt();
        }
        threadVar.clear();
    }
 
    /**
     * Return the value created by the <code>construct</code> method. Returns null
     * if either the constructing thread or the current thread was interrupted
     * before a value was produced.
     * 
     * @return the value created by the <code>construct</code> method
     */
    public Object get() {
        while (true) {
            Thread t = threadVar.get();
            if (t == null) {
                return getValue();
            }
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // propagate
                return null;
            }
        }
    }
 
    /**
     * Start a thread that will call the <code>construct</code> method and then
     * exit.
     */
    public SwingWorker() {
        final Runnable doFinished = new Runnable() {
            public void run() {
                finished();
            }
        };
 
        Runnable doConstruct = new Runnable() {
            public void run() {
                try {
                    setValue(construct());
                } finally {
                    threadVar.clear();
                }
 
                SwingUtilities.invokeLater(doFinished);
            }
        };
 
        Thread t = new Thread(doConstruct);
        threadVar = new ThreadVar(t);
    }
 
    /**
     * Start the worker thread.
     */
    public void start() {
        Thread t = threadVar.get();
        if (t != null) {
            t.start();
        }
    }
}
