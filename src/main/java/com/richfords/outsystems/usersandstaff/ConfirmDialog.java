/*
* �Richfords Fire and Flood ltd. All rights reserved.
* Unauthorised copying or re-using this the source code or the compiled source code
* in whole or part is Prohibited.
* 
* Created by: Tom Rowland, 4 Dec 2018
*
* ConfirmDialog.java
*
*/

package com.richfords.outsystems.usersandstaff;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import java.awt.Font;
import javax.swing.JLabel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Toolkit;

public class ConfirmDialog extends JDialog {

	private boolean answer;

	/**
	 * Launch the application.
	 */
	public static boolean confirm(String label) {
		ConfirmDialog dialog;
		try {
			dialog = new ConfirmDialog(label);
			dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		boolean answer=dialog.answer();
		dialog.dispose();
		return answer;
	}

	/**
	 * Create the dialog.
	 */
	public ConfirmDialog(String label) {
		setModalityType(ModalityType.APPLICATION_MODAL);
		Font font = new Font("Roboto", Font.PLAIN, 12);
		setIconImage(Toolkit.getDefaultToolkit().getImage(ConfirmDialog.class
				.getResource("/Richfords-Icon-Red-trans-on-white.png")));
		setType(Type.POPUP);
		setResizable(false);
		setBounds(100, 100, 320, 148);
		getContentPane().setLayout(new BorderLayout());
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		JLabel lblNewLabel = new JLabel(label);
		lblNewLabel.setFont(font);
		contentPanel.add(lblNewLabel);

		JPanel buttonPane = new JPanel();
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JButton okButton = new JButton("Yes");
		okButton.setFont(font);
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				answer = true;
				setVisible(false);
			}
		});

		JButton cancelButton = new JButton("No");
		cancelButton.setFont(font);
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				answer = false;
				setVisible(false);
			}
		});
	}

	public boolean answer() {
		return answer;
	}

}
