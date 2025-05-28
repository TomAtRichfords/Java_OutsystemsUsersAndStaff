/*
* �Richfords Fire and Flood ltd. All rights reserved.
* Unauthorised copying or re-using this the source code or the compiled source code
* in whole or part is Prohibited.
* 
* Created by: Tom Rowland, 23 Nov 2018
*
* StaffInfo.java
*
*/

package com.richfords.outsystems.usersandstaff;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class StaffInfoHeader extends JPanel {

	static Font font;

	/**
	 * Create the panel.
	 */
	public StaffInfoHeader() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 155, 45, 45, 45, 45, 65, 65, 65, 65, 65 };
		gridBagLayout.rowHeights = new int[] { 10, 10 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0 };
		setLayout(gridBagLayout);
		font = new Font("Roboto", Font.BOLD, 12);

		addLabel("Name", 0, 0);
		addLabel("Dev", 1, 1);
		addLabel("QA", 2, 1);
		addLabel("Prd", 3, 1);
		addLabel("All", 4, 1);
		addLabel("Dev", 5, 1);
		addLabel("QA", 6, 1);
		addLabel("Prd", 7, 1);
		addLabel("All", 8, 1);
		addLabel("Reset", 9, 1);

		addLabel("Add/Remove user", 1, 0);
		addLabel("Activate/Deactivate User", 5, 0);

	}

	private void addLabel(String name, int x, int y) {
		JLabel lbl = new JLabel(name);
		lbl.setHorizontalAlignment(SwingConstants.CENTER);
		lbl.setFont(font);
		if (x > 4 && x < 9) {
			lbl.setBackground(new Color(204, 204, 204));
			lbl.setOpaque(true);
		}
		this.add(lbl, gbc(x, y));
	}

	private static GridBagConstraints gbc(int gridx, int gridy) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		if (gridx == 0)
			gbc.insets = new Insets(0, 5, 0, 5);
		else
			gbc.insets = new Insets(0, 0, 0, 0);
		if (gridy == 0 && gridx != 0)
			gbc.gridwidth = 4;
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		return gbc;
	}

}
