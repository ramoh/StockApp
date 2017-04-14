package com.rajesh;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class AppFeed extends JFrame {

    private final DefaultStyledDocument tickerDoc;
    private final DefaultStyledDocument displayDoc;
    private final Style redStyle;
    private final Style greenStyle;


    AppFeed(String title) throws HeadlessException, IOException {
        JPanel middlePanel = new JPanel();
        middlePanel.setBorder(new TitledBorder(new EtchedBorder(), title));
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));

        // create the middle panel components
        //create styles and docs
        StyleContext sc = new StyleContext();
        this.tickerDoc = new DefaultStyledDocument();
        this.displayDoc = new DefaultStyledDocument();
        this.redStyle = sc.addStyle("red", null);
        this.greenStyle = sc.addStyle("green", null);

        StyleConstants.setForeground(redStyle, new Color(255, 50, 50));
        StyleConstants.setForeground(greenStyle, Color.GREEN);

        //create ticker JPane
        final JTextPane tickerArea = new JTextPane(this.tickerDoc);
        tickerArea.setPreferredSize(new Dimension(600, 400));
        tickerArea.setBackground(Color.DARK_GRAY);
        tickerArea.setFont(new Font("Monospaced", Font.BOLD, 14));

        JScrollPane scrollTickerArea = new JScrollPane(tickerArea);
        scrollTickerArea.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        //Create Final update area
        final JTextPane displayArea = new JTextPane(this.displayDoc);
        displayArea.setPreferredSize(new Dimension(600, 40));
        displayArea.setBackground(Color.DARK_GRAY);
        displayArea.setFont(new Font("Monospaced", Font.BOLD, 18));
        updateTitle("This Area will be updated", true);

        //Add Textarea in to middle panel
        middlePanel.add(displayArea);
        middlePanel.add(scrollTickerArea);

        JFrame frame = new JFrame();
        frame.setIconImage(ImageIO.read(AppFeed.class.getResource("/images/Stock.png")));

        frame.add(middlePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override

            public void windowClosing(WindowEvent e) {
                System.out.println(" closing event called ");
                frame.setVisible(false);
                frame.removeWindowListener(this);
                frame.dispose();
                System.exit(1);
            }

        });

    }

    synchronized void update(final String textToUpdate, final boolean isGreen) {

        try {

            this.tickerDoc.insertString(0, textToUpdate, isGreen ? greenStyle : redStyle);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    synchronized void updateTitle(final String textToUpdate, final boolean isGreen) {

        try {
            this.displayDoc.remove(0, this.displayDoc.getLength());

            this.displayDoc.insertString(0, textToUpdate, isGreen ? greenStyle : redStyle);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    synchronized void update(final String textToUpdate) {

        try {
            this.tickerDoc.insertString(tickerDoc.getLength(), textToUpdate, null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

}
