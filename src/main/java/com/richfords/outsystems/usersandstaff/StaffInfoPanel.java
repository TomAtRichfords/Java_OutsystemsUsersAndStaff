/*
* �Richfords Fire and Flood ltd. All rights reserved.
* Unauthorised copying or re-using this the source code or the compiled source code
* in whole or part is Prohibited.
* 
* Created by: Tom Rowland, 7 Nov 2018
*
* StaffInfoPanel.java
*
*/

package com.richfords.outsystems.usersandstaff;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.richfords.common.outsystems.objectdao.database.tables.remote.UserRemote;
import com.richfords.common.riposte.objectdao.database.tables.Staff;

public class StaffInfoPanel extends JPanel {

	ToggleButton devActive, qaActive, prdActive, allActive;
	ToggleButton devCreated, qaCreated, prdCreated, allCreated;
	JButton refresh;
	StaffDataSet staffData;
	static ImageIcon radioIcon, radioDisabledIcon, radioRolloverIcon, radioSelectedIcon, radioDisabledSelectedIcon,
			radioRolloverSelectedIcon, toggleIcon, toggleDisabledIcon, toggleRolloverIcon, toggleRolloverSelectedIcon,
			toggleSelectedIcon, buttonDisabledIcon, buttonIcon, buttonRolloverIcon, buttonPressedIcon;

	/**
	 * Create the panel.
	 */
	public StaffInfoPanel(StaffDataSet staff) {
		staffData = staff;

		if (radioIcon == null)
			createIcons();

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 155, 45, 45, 45, 45, 65, 65, 65, 65, 65 };
		gridBagLayout.rowHeights = new int[] { 10, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		gridBagLayout.rowWeights = new double[] { 0.0 };
		setLayout(gridBagLayout);
		JLabel lblName;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 5, 0, 5);
		gbc.gridx = 0;
		gbc.gridy = 0;
		if (staff != null)
			lblName = new JLabel(staffData.getStaff().getAsTrimmedString(Staff.AUTO_FULL_NAME));
		else
			lblName = new JLabel("Blank");
		lblName.setFont(new Font("Roboto", Font.PLAIN, 12));
		if (!staffData.getStaff().isNull(Staff.LEAVE_DATE)) {
			lblName.setBackground(new Color(204, 204, 204));
			lblName.setOpaque(true);
		}
		add(lblName, gbc);
		devCreated = createRadio(1, 1);
		qaCreated = createRadio(2, 2);
		prdCreated = createRadio(3, 4);
		allCreated = createRadio(4, 7);
		devActive = createToggle(5, 1);
		qaActive = createToggle(6, 2);
		prdActive = createToggle(7, 4);
		allActive = createToggle(8, 7);
		refresh = new JButton();
		refresh.setRolloverEnabled(true);
		refresh.setIcon(buttonIcon);
		refresh.setRolloverIcon(buttonRolloverIcon);
		refresh.setPressedIcon(buttonPressedIcon);
		refresh.setDisabledIcon(buttonDisabledIcon);
		refresh.setBorderPainted(false);
		refresh.setContentAreaFilled(false);

		refresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (isEnabled())
					resetUser();
			}
		});
		add(refresh, gbc(9));
		setStates();
	}

	private boolean isStaffValid() {
		return staffData != null && staffData.isStaffValid();
	}

	private boolean isStaffActive() {
		return staffData != null && staffData.isStaffActive();
	}

	private boolean isUserValid(UserRemote user) {
		return user != null && !user.isNull(UserRemote.ID);
	}

	private void setStates() {
		boolean valid = isStaffValid();
		boolean staffActive = isStaffActive();
		setStates(devActive, devCreated, valid && staffData.isUserValid(StaffDataSet.USR_DEV),
				staffData.isUserActive(StaffDataSet.USR_DEV), staffActive);
		setStates(qaActive, qaCreated, valid && staffData.isUserValid(StaffDataSet.USR_QA),
				staffData.isUserActive(StaffDataSet.USR_QA), staffActive);
		setStates(prdActive, prdCreated, valid && staffData.isUserValid(StaffDataSet.USR_PRD),
				staffData.isUserActive(StaffDataSet.USR_PRD), staffActive);
		setStates(allActive, allCreated,
				valid && staffData.isUserValid(StaffDataSet.USR_DEV) && staffData.isUserValid(StaffDataSet.USR_QA)
						&& staffData.isUserValid(StaffDataSet.USR_PRD),
				staffData.isUserActive(StaffDataSet.USR_DEV) && staffData.isUserActive(StaffDataSet.USR_QA)
						&& staffData.isUserActive(StaffDataSet.USR_PRD),
				staffActive);
		refresh.setEnabled(valid && (staffData.isUserValid(StaffDataSet.USR_DEV)
				|| staffData.isUserValid(StaffDataSet.USR_QA) || staffData.isUserValid(StaffDataSet.USR_PRD)));
	}

	private void setStates(JRadioButton activation, JRadioButton creation, boolean valid, boolean active,
			boolean staffActive) {
		activation.setEnabled(valid);
		activation.setSelected(valid && active);
		activation.setToolTipText(creation.isSelected() ? "Deactivate" : "Activate");
		creation.setSelected(valid);
		if (!creation.isSelected() && !staffActive)
			creation.setEnabled(false);
		creation.setToolTipText(valid ? "Delete" : "Create");
	}

	private ToggleButton createRadio(int gridx, int index) {
		ToggleButton radio = new ToggleButton(radioIcon, radioDisabledIcon, radioRolloverIcon, radioSelectedIcon,
				radioDisabledSelectedIcon, radioRolloverSelectedIcon);
		// radio.setHorizontalAlignment(SwingConstants.CENTER);
		// radio.setPressedIcon(radioIcon);
		// radio.setRolloverIcon(radioRolloverIcon);
		// radio.setDisabledIcon(radioDisabledIcon);
		// radio.setRolloverSelectedIcon(radioRolloverSelectedIcon);
		// radio.setSelectedIcon(radioSelectedIcon);
		// radio.setDisabledSelectedIcon(radioDisabledSelectedIcon);
		radio.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!radio.isSelected()) {
					if (ConfirmDialog.confirm("Are you certain you want to create the user?"))
						createOnOutsystems(index);
				} else {
					if (ConfirmDialog.confirm("Are you certain you want to delete the user?"))
						deleteOnOutSystems(index);
				}
			}
		});
		add(radio, gbc(gridx));
		return radio;
	}

	private ToggleButton createToggle(int gridx, int index) {
		ToggleButton toggle = new ToggleButton(toggleIcon, toggleDisabledIcon, toggleRolloverIcon, toggleSelectedIcon,
				toggleDisabledIcon, toggleRolloverSelectedIcon);
		// JRadioButton toggle = new JRadioButton(toggleIcon);
		// toggle.setHorizontalAlignment(SwingConstants.CENTER);
		// toggle.setOpaque(true);
		// toggle.setBackground(new Color(204, 204, 204));
		// toggle.setPressedIcon(toggleIcon);
		// toggle.setRolloverIcon(toggleRolloverIcon);
		// toggle.setRolloverSelectedIcon(toggleRolloverSelectedIcon);
		// toggle.setSelectedIcon(toggleSelectedIcon);
		// toggle.setDisabledIcon(toggleDisabledIcon);
		toggle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				activateOnOutSystems(index, !toggle.isSelected());
			}
		});
		add(toggle, gbc(gridx));
		return toggle;
	}

	private void createIcons() {
		radioIcon = createImageIcon("radio-plus-icon-green1.png");
		radioDisabledIcon = createImageIcon("radio-plus-icon-grey.png");
		radioRolloverIcon = createImageIcon("radio-plus-icon-green1-hover.png");
		radioSelectedIcon = createImageIcon("radio-minus-icon-red1.png");
		radioDisabledSelectedIcon = createImageIcon("radio-minus-icon-grey.png");
		radioRolloverSelectedIcon = createImageIcon("radio-minus-icon-red1-hover.png");

		buttonDisabledIcon = createImageIcon("radio-icon-grey.png");
		buttonIcon = createImageIcon("radio-icon-green1.png");
		buttonRolloverIcon = createImageIcon("radio-icon-green1-hover.png");
		buttonPressedIcon = createImageIcon("radio-icon-red1.png");

		toggleIcon = createImageIcon("toggle-icon-red1.png");
		toggleDisabledIcon = createImageIcon("toggle-icon-grey1.png");
		toggleRolloverIcon = createImageIcon("toggle-icon-red1-hover.png");
		toggleRolloverSelectedIcon = createImageIcon("toggle-icon-green1-hover.png");
		toggleSelectedIcon = createImageIcon("toggle-icon-green1.png");
	}

	public ImageIcon createImageIcon(String filename) {
		String path = "/resources/" + filename;
		return new ImageIcon(getClass().getResource(path));
	}

	private static GridBagConstraints gbc(int gridx) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.gridx = gridx;
		gbc.gridy = 0;
		return gbc;
	}

	private void createOnOutsystems(int mask) {
		if (staffData == null || !staffData.isStaffValid()) {
			setStates();
			return;
		}
		if ((mask & 1) != 0)
			staffData.createUser(0);
		if ((mask & 2) != 0)
			staffData.createUser(1);
		if ((mask & 4) != 0)
			staffData.createUser(2);
		setStates();
	}

	private void deleteOnOutSystems(int mask) {
		if (staffData == null || !staffData.isStaffValid()) {
			setStates();
			return;
		}
		if ((mask & 1) != 0)
			staffData.deleteUser(0);
		if ((mask & 2) != 0)
			staffData.deleteUser(1);
		if ((mask & 4) != 0)
			staffData.deleteUser(2);
		setStates();
	}

	private void activateOnOutSystems(int mask, boolean state) {
		if (staffData == null || !staffData.isStaffValid()) {
			setStates();
			return;
		}
		if ((mask & 1) != 0)
			staffData.activateUser(StaffDataSet.USR_DEV, state);
		if ((mask & 2) != 0)
			staffData.activateUser(StaffDataSet.USR_QA, state);
		if ((mask & 4) != 0)
			staffData.activateUser(StaffDataSet.USR_PRD, state);
		setStates();
	}

	private void resetUser() {
		if (staffData == null || !staffData.isStaffValid()) {
			setStates();
			return;
		}
		staffData.updateUser2(0);
		// staffData.updateUser(1);
		// staffData.updateUser(2);
		setStates();
	}
}
