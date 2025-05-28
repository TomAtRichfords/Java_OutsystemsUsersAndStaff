/*
* �Richfords Fire and Flood ltd. All rights reserved.
* Unauthorised copying or re-using this the source code or the compiled source code
* in whole or part is Prohibited.
* 
* Created by: Tom Rowland, 8 Nov 2018
*
* StaffData.java
*
*/

package com.richfords.outsystems.usersandstaff;

import java.util.ArrayList;
import java.util.List;

import com.richfords.common.database.objectdao.ObjectDAOBaseDatabaseActions;
import com.richfords.common.database.objectdao.SqlStatement;
import com.richfords.common.outsystems.objectdao.api.APIResponse;
import com.richfords.common.outsystems.objectdao.api.JSON;
import com.richfords.common.outsystems.objectdao.api.OutSystemsHTTP;
import com.richfords.common.outsystems.objectdao.database.tables.remote.UserRemote;
import com.richfords.common.riposte.objectdao.database.tables.Staff;

public class StaffDataSet {

	public static final int USR_DEV = 0;
	public static final int USR_QA = 1;
	public static final int USR_PRD = 2;

	Staff staff;
	UserRemote[] users;

	public StaffDataSet(Staff staff, List<UserRemote> devUsers, List<UserRemote> qaUsers, List<UserRemote> prdUsers) {
		this.staff = staff;
		String staffId = staff.getAsString(Staff.STAFF_ID);
		users = new UserRemote[3];
		users[0] = findUserByStaffId(devUsers, staffId);
		users[1] = findUserByStaffId(qaUsers, staffId);
		users[2] = findUserByStaffId(prdUsers, staffId);
		if (!staff.isNull(Staff.LEAVE_DATE))
			activateUser(0, false);
	}

	private static UserRemote findUserByStaffId(List<UserRemote> users, String staffId) {
		if (users == null || users.isEmpty() || staffId == null || staffId.length() == 0)
			return null;
		int index = users.size() - 1;
		while (index >= 0) {
			UserRemote user = users.get(index);
			if (user != null && !user.isNull(UserRemote.EXTERNAL_ID)
					&& staffId.compareTo((String) user.getDirect(UserRemote.EXTERNAL_ID)) == 0) {
				users.remove(index);
				return user;
			}
			index--;
		}
		return null;
	}

	public UserRemote getUser(int server) {
		if (server < 0 || server > 2)
			return null;
		return users[server];
	}

	public String getName() {
		return staff.getAsTrimmedString(Staff.AUTO_FULL_NAME);
	}

	public String getEmail() {
		return staff.getAsTrimmedString(Staff.EMAIL);
	}

	public long getUserId(int server) {
		if (server < 0 || server > 2 || users[server] == null)
			return 0;
		return (long) users[server].get(UserRemote.ID, 0L);
	}

	public boolean isStaffValid() {
		return staff != null && !staff.isNull(Staff.AUTO_FULL_NAME)
				&& staff.getAsTrimmedString(Staff.AUTO_FULL_NAME).length() > 0 && !staff.isNull(Staff.PIN)
				&& staff.getAsTrimmedString(Staff.PIN).length() > 0;
	}

	public boolean isStaffActive() {
		return staff != null && staff.isNull(Staff.LEAVE_DATE);
	}

	public boolean isUserValid(int server) {
		if (server < 0 || server > 2)
			return false;
		return users[server] != null && !users[server].isNull(UserRemote.ID);
	}

	public boolean isUserActive(int server) {
		if (server < 0 || server > 2)
			return false;
		return users[server] != null && users[server].is(UserRemote.IS_ACTIVE);
	}

	public Staff getStaff() {
		return staff;
	}

	public boolean createUser(int server) {
		if (staff == null || staff.isNull(Staff.PIN) || staff.isNull(Staff.EMAIL) || !staff.isNull(Staff.LEAVE_DATE)
				|| server < 0 || server > 2)
			return false;
		// if (server == 0) {
		// createUser(1);
		// createUser(2);
		// createUser(3);
		// }
		if (users[server] != null)
			return activateUser(server, true);
		UserRemote user = new UserRemote();
		user.setDirect(UserRemote.NAME, staff.getAsTrimmedString(Staff.AUTO_FULL_NAME));
		user.setDirect(UserRemote.USERNAME, staff.getAsTrimmedString(Staff.EMAIL));
		user.setDirect(UserRemote.EMAIL, staff.getAsTrimmedString(Staff.EMAIL));
		user.setDirect(UserRemote.MOBILEPHONE, staff.tidyPhone(Staff.MOBILE_PHONE));
		user.setDirect(UserRemote.IS_ACTIVE, true);
		user.setDirect(UserRemote.PASSWORD, "Leig-h" + staff.getAsTrimmedString(Staff.PIN));
		user.setDirect(UserRemote.EXTERNAL_ID, staff.getAsTrimmedString(Staff.STAFF_ID));
		String serverURL = getServerURL(server);
		APIResponse res = OutSystemsHTTP.postJSON(serverURL, JSON.toArray(JSON.METHOD_INSERT_UPDATE, user));
		if (res.isSuccess())
			users[server] = (UserRemote) res.getRecord(0, UserRemote.class);
		return res.isSuccess();
		// return true;
	}

	public boolean deleteUser(int server) {
		if (staff == null || staff.isNull(Staff.PIN) || staff.isNull(Staff.EMAIL) || server < 0 || server > 2)
			return false;
		// if (server == 0) {
		// deleteUser(1);
		// deleteUser(2);
		// deleteUser(3);
		// return true;
		// }
		if (users[server] == null)
			return false;
		String serverURL = getServerURL(server);
		String json = JSON.toRemove("User", (Long) users[server].getDirect(UserRemote.ID));
		APIResponse res = OutSystemsHTTP.postJSON(serverURL, json);
		if (res.isSuccess())
			users[server] = null;
		return res.isSuccess();
		// return true;
	}

	public boolean activateUser(int server, boolean state) {
		if (staff == null || staff.isNull(Staff.PIN) || staff.isNull(Staff.EMAIL) || server < 0 || server > 2)
			return false;
		// if (server == 0) {
		// activateUser(1, state);
		// activateUser(2, state);
		// activateUser(3, state);
		// return true;
		// }
		if (users[server] == null)
			return false;
		if ((boolean) users[server].getDirect(UserRemote.IS_ACTIVE, false) == state)
			return true;
		String serverURL = getServerURL(server);
		String json = "{\n \"Table\":\"User\",\n \"Method\":\"" + (state ? "Reactivate" : "Deactivate") + "\",\n"
				+ "\"Records\":[\n  \"{\\\"IdList\\\":\\\"" + users[server].getAsString(UserRemote.ID)
				+ "\\\"}\"\n ]\n}";
		APIResponse res = OutSystemsHTTP.postJSON(serverURL, json);
		if (res.isSuccess())
			users[server].setDirect(UserRemote.IS_ACTIVE, state);
		return res.isSuccess();
		// return true;
	}

	public boolean updateUser(int server) {
		if (staff == null || staff.isNull(Staff.PIN) || staff.isNull(Staff.EMAIL) || server < 0 || server > 2
				|| users[server] == null || users[server].isNull(UserRemote.ID))
			return false;
		UserRemote user = new UserRemote();
		user.setDirect(UserRemote.NAME, staff.getAsTrimmedString(Staff.AUTO_FULL_NAME));
		user.setDirect(UserRemote.USERNAME, staff.getAsTrimmedString(Staff.EMAIL));
		user.setDirect(UserRemote.EMAIL, staff.getAsTrimmedString(Staff.EMAIL));
		user.setDirect(UserRemote.MOBILEPHONE, staff.tidyPhone(Staff.MOBILE_PHONE));
		user.setDirect(UserRemote.IS_ACTIVE, true);
		user.setDirect(UserRemote.PASSWORD, "Leig-h" + staff.getAsTrimmedString(Staff.PIN));
		user.setDirect(UserRemote.EXTERNAL_ID, staff.getAsTrimmedString(Staff.STAFF_ID));
		String serverURL = getServerURL(server);
		APIResponse res = OutSystemsHTTP.postJSON(serverURL, JSON.toArray(JSON.METHOD_UPDATE, user, new int[] {
				UserRemote.NAME, UserRemote.USERNAME, UserRemote.EMAIL, UserRemote.MOBILEPHONE, UserRemote.PASSWORD }));
		if (res.isSuccess())
			users[server] = (UserRemote) res.getRecord(0, UserRemote.class);
		return res.isSuccess();
	}

	public boolean updateUser2(int server) {
		if (staff == null || staff.isNull(Staff.PIN) || staff.isNull(Staff.EMAIL) || server < 0 || server > 2
				|| users[server] == null || users[server].isNull(UserRemote.ID))
			return false;
		UserRemote user = new UserRemote();
		user.setDirect(UserRemote.NAME, staff.getAsTrimmedString(Staff.AUTO_FULL_NAME));
		user.setDirect(UserRemote.USERNAME, staff.getAsTrimmedString(Staff.EMAIL));
		user.setDirect(UserRemote.EMAIL, staff.getAsTrimmedString(Staff.EMAIL));
		user.setDirect(UserRemote.MOBILEPHONE, staff.tidyPhone(Staff.MOBILE_PHONE));
		user.setDirect(UserRemote.IS_ACTIVE, true);
		user.setDirect(UserRemote.PASSWORD, "Mb_rfaf_1985632");// + staff.getAsTrimmedString(Staff.PIN));
		user.setDirect(UserRemote.EXTERNAL_ID, staff.getAsTrimmedString(Staff.STAFF_ID));
		String serverURL = getServerURL(server);
		APIResponse res = OutSystemsHTTP.postJSON(serverURL, JSON.toArray(JSON.METHOD_UPDATE, user));
		if (res.isSuccess())
			users[server] = (UserRemote) res.getRecord(0, UserRemote.class);
		return res.isSuccess();
	}

	private final String getServerURL(int server) {
		switch (server) {
		case USR_DEV:
			return OutSystemsHTTP.API_URL_DEV;
		case USR_QA:
			return OutSystemsHTTP.API_URL_TST;
		case USR_PRD:
			return OutSystemsHTTP.API_URL_PRD;
		}
		return null;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (staff == null)
			return "Empty StaffDataSet.";
		sb.append("Name: ").append(staff.getAsTrimmedString(Staff.AUTO_FULL_NAME)).append(", Id: ")
				.append(staff.getAsString(Staff.STAFF_ID));
		if (users == null)
			sb.append("No Uer data.");
		else {
			if (users[0] != null)
				sb.append(", ").append("Dev user id: ").append(users[0].getAsString(UserRemote.ID));
			if (users[1] != null)
				sb.append(", ").append("Tst user id: ").append(users[1].getAsString(UserRemote.ID));
			if (users[2] != null)
				sb.append(", ").append("Prd user id: ").append(users[2].getAsString(UserRemote.ID));
			sb.append(".");
		}
		return sb.toString();
	}

	public static List<StaffDataSet> buildFromStaff() {
		List<Staff> staffList = ObjectDAOBaseDatabaseActions.listFromQuery(new SqlStatement(
//				"SELECT * FROM staff WHERE deleted IS NULL AND job_id IS NOT NULL AND pin IS NOT NULL ORDER BY auto_full_name"),
				"SELECT * FROM staff WHERE deleted IS NULL AND pin IS NOT NULL ORDER BY auto_full_name"), Staff.class);
		if (staffList == null || staffList.isEmpty())
			return null;
		List<UserRemote> devStaff = null;
		List<UserRemote> tstStaff = null;
		List<UserRemote> prdStaff = null;
		APIResponse res = OutSystemsHTTP.postJSON(OutSystemsHTTP.API_URL_DEV, JSON.toFetch("user"));
		if (res.isSuccess())
			devStaff = APIResponse.parseRecords(res, UserRemote.class);
		res = OutSystemsHTTP.postJSON(OutSystemsHTTP.API_URL_TST, JSON.toFetch("user"));
		if (res.isSuccess())
			tstStaff = APIResponse.parseRecords(res, UserRemote.class);
		res = OutSystemsHTTP.postJSON(OutSystemsHTTP.API_URL_PRD, JSON.toFetch("user"));
		if (res.isSuccess())
			prdStaff = APIResponse.parseRecords(res, UserRemote.class);
		List<StaffDataSet> dataSet = new ArrayList<>();
		int s = 0;
		for (Staff staff : staffList) {
			if (((String) staff.get(Staff.SURNAME)).compareTo("Buck") == 0)
				s = 7;

			StaffDataSet data = new StaffDataSet(staff, devStaff, tstStaff, prdStaff);
			if (staff.isNull(Staff.LEAVE_DATE) || data.isUserValid(0) || data.isUserValid(1) || data.isUserValid(2))
				dataSet.add(data);
		}
		return dataSet;
	}
}
