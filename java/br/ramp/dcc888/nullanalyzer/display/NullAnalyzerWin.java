package br.ramp.dcc888.nullanalyzer.display;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class NullAnalyzerWin extends JFrame {

	private static final long serialVersionUID = -3169848390539147060L;
		
	public NullAnalyzerWin() {
		super("Null Analyzer");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new FlowLayout());
		setPreferredSize(new Dimension(800, 600));		
		
		JPanel controlPanel = new JPanel(new FlowLayout());			
		
		// GridBagConstraints gbc = new GridBagConstraints();
		Label label = new Label("Save graph as image");
		controlPanel.add(label);
		
		// label.setPreferredSize(new Dimension(175, 100));
		JButton btnSave = new JButton("Save");
		
		btnSave.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				GraphSingleton.instance().saveImagePNG("OUTPUT");
			}
			
		});
		
		JButton btnEnableAuto = new JButton("Enable");
		btnEnableAuto.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {								
//				viewer.enableAutoLayout();
			}
		});
		
		JButton btnDisableAuto = new JButton("Disable");
		btnDisableAuto.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {								
//				viewer.disableAutoLayout();
			}
		});		
		
		controlPanel.add(btnSave);
		controlPanel.add(btnEnableAuto);
		controlPanel.add(btnDisableAuto);
		
		getContentPane().add(controlPanel, BorderLayout.PAGE_START);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(screenSize.width / 2, screenSize.height / 2);
//		pack();
//		setVisible(false);
	}
	
	public void appendView(Component view) {
		JPanel viewPanel = new JPanel(new FlowLayout());		
		viewPanel.add(view);	
		viewPanel.setPreferredSize(view.getMaximumSize());		
		
		getContentPane().add(viewPanel, BorderLayout.CENTER);
		
		pack();
		setVisible(true);
	}
	

}
