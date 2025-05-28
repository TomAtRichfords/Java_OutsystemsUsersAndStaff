/*
* �Richfords Fire and Flood ltd. All rights reserved.
* Unauthorised copying or re-using this the source code or the compiled source code
* in whole or part is Prohibited.
* 
* Created by: Tom Rowland, 4 Dec 2018
*
* ToggleButton.java
*
*/

package com.richfords.outsystems.usersandstaff;

import java.awt.event.MouseWheelEvent;

import javax.swing.ImageIcon;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class ToggleButton extends JRadioButton {
	private boolean selected;
	private ImageIcon defaultIcon, disabledIcon, rolloverIcon, selectedIcon, disabledSelectedIcon, rolloverSelectedIcon;

	public ToggleButton(ImageIcon defaultIcon, ImageIcon disabledIcon, ImageIcon rolloverIcon, ImageIcon selectedIcon,
			ImageIcon disabledSelectedIcon, ImageIcon rolloverSelectedIcon) {
		super();
		setHorizontalAlignment(SwingConstants.CENTER);
		this.defaultIcon = defaultIcon;
		this.rolloverIcon = rolloverIcon;
		this.disabledIcon = disabledIcon;
		this.rolloverSelectedIcon = rolloverSelectedIcon;
		this.selectedIcon = selectedIcon;
		if (disabledSelectedIcon == null)
			disabledSelectedIcon = this.disabledIcon;
		else
			this.disabledSelectedIcon = disabledSelectedIcon;
		setIcon(this.defaultIcon);
		setPressedIcon(this.defaultIcon);
		setRolloverIcon(this.rolloverIcon);
		setDisabledIcon(this.disabledIcon);
		// setRolloverSelectedIcon(rolloverSelectedIcon);
		// setSelectedIcon(selectedIcon);
		// setDisabledSelectedIcon(disabledSelectedIcon);
	}

	@Override
	protected void processMouseWheelEvent(MouseWheelEvent e) {
		// if (!isWheelScrollingEnabled()) {
		if (getParent() != null)
			getParent().dispatchEvent(SwingUtilities.convertMouseEvent(this, e, getParent()));
		// return;
		// }
		// super.processMouseWheelEvent(e);
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean state) {
		selected = state;
		if (selected) {
			setIcon(selectedIcon);
			setPressedIcon(selectedIcon);
			setRolloverIcon(rolloverSelectedIcon);
			setDisabledIcon(disabledSelectedIcon);
		} else {
			setIcon(defaultIcon);
			setPressedIcon(defaultIcon);
			setRolloverIcon(rolloverIcon);
			setDisabledIcon(disabledIcon);
		}
	}
}
