import java.util.HashSet;
import java.util.Set;

public class UserGroup {
    private String groupId;
    private Set<User> users;
    private Set<UserGroup> subGroups;
    private Long creationTime;
    public UserGroup(String groupId) {
        this.groupId = groupId;
        this.users = new HashSet<>();
        this.subGroups = new HashSet<>();
        this.creationTime = 0L;
    }
    public String getGroupId() {
        return groupId;
    }
    public Long getCreationTime() {
        return creationTime;
    }
    public void setCreationTime(Long timeCreated) {
        creationTime = timeCreated;
    }
    public void addUser(User user) {
        users.add(user);
    }
    public void addGroup(UserGroup group) {
        subGroups.add(group);
    }
}
