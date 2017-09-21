
package com.memreas.member;

import java.util.ArrayList;
import java.util.List;

public class GroupBean {

	public GroupBean() {
		super();
		this.mGroupOfFriends = new ArrayList<FriendBean>();
	}

	private String groupId;
	private String groupName;
	private List<FriendBean> mGroupOfFriends;

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public List<FriendBean> getmGroupOfFriends() {
		return mGroupOfFriends;
	}
	
	
	public void add(FriendBean friend) {
		mGroupOfFriends.add(friend);
	}

	public void remove(FriendBean friend) {
		mGroupOfFriends.remove(friend);
	}

}
