package demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author hakan eryargi (r a f t)
 */
@SuppressWarnings("serial")
public class TestPanel extends JPanel {
	private final JTextArea textArea = new JTextArea("move mouse over center label\n");
	
	public TestPanel() {
		setBorder(BorderFactory.createTitledBorder("a titled border"));
		
		setLayout(new BorderLayout());
		setOpaque(false);
		
		JScrollPane scrollPanel = new JScrollPane(textArea);
		// scrollbars are rendered correctly but since ComponentEvents are not dispatched,
		// they are not notified of resize events
		//scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPanel.setOpaque(false);
		scrollPanel.setPreferredSize(new Dimension(300, 200));
		scrollPanel.setBorder(BorderFactory.createTitledBorder("a text area"));
		
		final JPanel panel = new JPanel(new BorderLayout());
		panel.setOpaque(false);
		panel.setBorder(BorderFactory.createTitledBorder("a border layout"));
		//panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		
		JLabel centerLabel = new JLabel("center label", JLabel.CENTER);
		centerLabel.setFont(centerLabel.getFont().deriveFont(40f).deriveFont(Font.BOLD));
		centerLabel.setForeground(Color.ORANGE);
		//centerLabel.setBorder(BorderFactory.createLineBorder(Color.MAGENTA, 2));
		
		centerLabel.addMouseMotionListener(new MouseMotionAdapter(){
			public void mouseMoved(MouseEvent e) {
				log("mouse move over label: " + e.getPoint());
			}
		});
		 
		JLabel northLabel = new JLabel("north label", JLabel.CENTER);
		northLabel.setFont(centerLabel.getFont().deriveFont(20f).deriveFont(Font.BOLD));
		northLabel.setForeground(Color.WHITE);
		
		panel.add(northLabel, BorderLayout.NORTH);
		panel.add(centerLabel, BorderLayout.CENTER);
		
		JLabel south = new JLabel("south label", JLabel.CENTER);
		//south.setOpaque(true);
		panel.add(south, BorderLayout.SOUTH);
		panel.add(new JLabel("east label"), BorderLayout.EAST);
		panel.add(new JLabel("west label"), BorderLayout.WEST);
		panel.setPreferredSize(new Dimension(400, 100));
		
		this.add(panel, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);
		
		buttonPanel.add(new JButton(new AbstractAction("clear"){
			public void actionPerformed(ActionEvent e) {
				textArea.setText("");
				log(getValue(NAME) + " click");
			}
		}));
		buttonPanel.add(new JButton(new AbstractAction("some other button"){
			public void actionPerformed(ActionEvent e) {
				log(getValue(NAME) + " click");
			}
		}));
		
		this.add(buttonPanel, BorderLayout.NORTH);
		this.add(scrollPanel, BorderLayout.SOUTH);
		
	}
	
	void log(String s) {
		System.out.println(s);
		textArea.append(s + "\n");
	}
	
}
