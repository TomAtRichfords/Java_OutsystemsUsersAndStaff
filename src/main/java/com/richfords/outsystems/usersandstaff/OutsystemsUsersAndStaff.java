/*
* �Richfords Fire and Flood ltd. All rights reserved.
* Unauthorised copying or re-using this the source code or the compiled source code
* in whole or part is Prohibited.
* 
* Created by: Tom Rowland, 7 Nov 2018
*
* OutsystemsUsersAndStaff.java
*
*/

package com.richfords.outsystems.usersandstaff;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.richfords.common.file.FileOps;

public class OutsystemsUsersAndStaff {

	private JFrame frmOusystemsUsers;
	private JScrollPane scrollPane;
	private static JFileChooser fc;
	private List<StaffDataSet> staff;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					OutsystemsUsersAndStaff window = new OutsystemsUsersAndStaff();
					window.frmOusystemsUsers.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public OutsystemsUsersAndStaff() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		// JFrame.setDefaultLookAndFeelDecorated(true);
		frmOusystemsUsers = new JFrame();

		frmOusystemsUsers.setFont(new Font("Roboto", Font.BOLD, 12));
		frmOusystemsUsers.setResizable(false);
		try {
			// Set the required look and feel
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels())
				if ("Windows".equals(info.getName())) {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					break;
				}
			SwingUtilities.updateComponentTreeUI(frmOusystemsUsers);
		} catch (Exception e) {
			e.printStackTrace();
		}
		frmOusystemsUsers.setIconImage(Toolkit.getDefaultToolkit().getImage(OutsystemsUsersAndStaff.class
				.getResource("/resources/Richfords-Icon-Red-trans-on-white.png")));
		frmOusystemsUsers.setTitle("Ousystems users");
		frmOusystemsUsers.setBounds(100, 100, 760, 760);
		frmOusystemsUsers.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmOusystemsUsers.getContentPane().setLayout(new BorderLayout(0, 0));
		JPanel panel = new JPanel();
		frmOusystemsUsers.getContentPane().add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));

		JLabel lblStatus = new JLabel("Status");
		lblStatus.setToolTipText("Information");
		panel.add(lblStatus, BorderLayout.NORTH);

		JPanel topPanel = new JPanel();
		frmOusystemsUsers.getContentPane().add(topPanel, BorderLayout.NORTH);

		JButton btnCSVUser = new JButton("Generate CSV of users");
		btnCSVUser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				generateCSV(true);
			}
		});
		topPanel.add(btnCSVUser);
		btnCSVUser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				generateCSV(false);
			}
		});

		JButton btnCSVAll = new JButton("Gnerate CSV of All");
		topPanel.add(btnCSVAll);

		JButton btnNewButton = new JButton("Refresh");
		topPanel.add(btnNewButton);

		JPanel middlePanel = new JPanel();
		// {
		// @Override
		// protected void processMouseWheelEvent(MouseWheelEvent e) {
		// // if (!isWheelScrollingEnabled()) {
		// if (getParent() != null)
		// getParent().dispatchEvent(SwingUtilities.convertMouseEvent(this, e,
		// getParent()));
		// // return;
		// // }
		// // super.processMouseWheelEvent(e);
		// }
		// };

		scrollPane = new JScrollPane(middlePanel);
		// {
		// @Override
		// protected void processMouseWheelEvent(MouseWheelEvent e) {
		// if (!isWheelScrollingEnabled()) {
		// if (getParent() != null)
		// getParent().dispatchEvent(SwingUtilities.convertMouseEvent(this, e,
		// getParent()));
		// } else
		// super.processMouseWheelEvent(e);
		// }
		// };
		scrollPane.setWheelScrollingEnabled(true);
		scrollPane.removeMouseWheelListener(scrollPane.getMouseWheelListeners()[0]);
		frmOusystemsUsers.getContentPane().add(scrollPane, BorderLayout.CENTER);
		middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));

		StaffInfoHeader h = new StaffInfoHeader();
		middlePanel.add(h);
		staff = StaffDataSet.buildFromStaff();
		for (StaffDataSet st : staff) {
			middlePanel.add(new StaffInfoPanel(st));
		}
	}

	private void generateCSV(boolean forAllEligibleStaff) {
		if (fc == null)
			fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		int returnVal = fc.showSaveDialog(frmOusystemsUsers);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String path = file.getAbsolutePath();
			if (file.isDirectory()) {
				if (path.charAt(path.length() - 1) != '\\')
					path += "\\";
				path += "Outsystems_users.csv";
			}
			StringBuilder csv = new StringBuilder();
			csv.append("Name,Email\\UserName,Dev id,Active on dev,QA id,Active on qa,Prod id,Active on prod\n");
			for (StaffDataSet st : staff) {
				if (st.isStaffActive() && st.isStaffValid())
					if (forAllEligibleStaff || st.isUserValid(0) || st.isUserValid(1) || st.isUserValid(2)) {
						csv.append(st.getName()).append(",").append(st.getEmail());
						for (int i = 0; i < 3; i++) {
							if (st.isUserValid(i)) {
								csv.append(",").append(st.getUserId(i));
								if (st.isUserActive(i))
									csv.append(",X");
								else
									csv.append(",");
							} else
								csv.append(",,");
						}
						csv.append("\n");
					}
			}
			FileOps.writeFile(path, csv.toString());
		}
	}
}
