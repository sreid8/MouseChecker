package com.sreid.mousecheck;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class MouseCheck {

	private static Display display = Display.getDefault();
	private static Shell shell = new Shell(display);
	private static Label label;
	private static Label instructions;

	private static BufferedWriter writer;

	public static void main(String[] args) {

		createContents();
		openFile();
		setListeners();
		startTracker();
	}

	private static void createContents() {
		
		
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice();
		int x = gd.getDisplayMode().getWidth();
		int y = gd.getDisplayMode().getHeight();

		shell.setBounds(0, 0, x, y);
		shell.setLayout(new GridLayout(1, false));

		label = new Label(shell, SWT.NONE);
		label.setForeground(display.getSystemColor(SWT.COLOR_RED));
		FontData[] fD = label.getFont().getFontData();
		fD[0].setHeight(32);
		label.setFont(new Font(display, fD[0]));
		
		instructions = new Label(shell, SWT.NONE);
		instructions.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
		fD = instructions.getFont().getFontData();
		fD[0].setHeight(16);
		instructions.setFont(new Font(display, fD[0]));
		instructions.setText("Now recording. Press ESC to stop recording before exiting for proper results.");
	}

	private static void startTracker() {
		shell.open();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		while (!shell.isDisposed()) {

		}
		display.dispose();
	}

	private static void openFile() {
		File outFile = new File("mouseCheckResults.txt");
		boolean cont;
		do {
			try {
				outFile.createNewFile();
				cont = false;
			} catch (IOException e) {
				// file already exists.
				outFile.delete();
				cont = true;
			}
		} while (cont == true);
		// got the file, son.
		FileWriter fw = null;
		try {
			fw = new FileWriter(outFile.getAbsoluteFile());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (fw != null) {
			writer = new BufferedWriter(fw);
		}

	}

	private static void setListeners() {
		shell.addListener(SWT.MouseHover, hoverListener);
		shell.addListener(SWT.MouseDown, clickListener);
		display.addFilter(SWT.KeyUp, escapeListener);
	}
	
	private static void removeListeners() {
		shell.removeListener(SWT.MouseHover, hoverListener);
		shell.removeListener(SWT.MouseDown, clickListener);
		instructions.setText("Recoding stopped.");
	}

	private static Listener hoverListener = new Listener() {

		@Override
		public void handleEvent(Event event) {
			PointerInfo point = MouseInfo.getPointerInfo();
			try {
				writer.write("Mouse was moved to: (" + point.getLocation().x
						+ "," + point.getLocation().y + ").");
				writer.write("\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			label.setText("EVENT DETECTED");
		}
	};

	private static Listener clickListener = new Listener() {

		@Override
		public void handleEvent(Event event) {
			PointerInfo point = MouseInfo.getPointerInfo();
			try {
				writer.write("Mouse was clicked at: (" + point.getLocation().x
						+ "," + point.getLocation().y + ").");
				writer.write("\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
			label.setText("EVENT DETECTED");
		}
	};
	
	private static Listener escapeListener = new Listener() {
		
		@Override
		public void handleEvent(Event e) {
			 if (e.widget instanceof Control) {
	                if(e.keyCode == SWT.ESC) {
	                    removeListeners();
	                    try {
							writer.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
	                }
	            }
			
		}
	};

}
