import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User {
    private String userId;
    private Set<String> followers;
    private Set<String> followings;
    private List<String> newsFeed;
    private Long creationTime;
    private Long lastUpdateTime;

    public User(String userId) {
        this.userId = userId;
        this.followings = new HashSet<>();
        this.followers = new HashSet<>();
        this.newsFeed = new ArrayList<>();
        this.creationTime = 0L;
        this.lastUpdateTime = 0L;
    }
    public String getUserId() {
        return userId;
    }
    public Set<String> getFollowings() {
        return followings;
    }
    public Set<String> getFollowers() {
        return followers;
    }
    public List<String> getNewsFeed() {
        return newsFeed;
    }
    public Long getCreationTime() {
        return creationTime;
    }
    public void setCreationTime(Long timeCreated) {
        creationTime = timeCreated;
    }
    public Long getLastUpdateTime() {
        return lastUpdateTime;
    }
    public void setLastUpdateTime(Long timeUpdated) {
        lastUpdateTime = timeUpdated;
    }
    public void follow(User user) {
        followings.add(user.getUserId());
    }
    public void followedBy(User user) {
        followers.add(user.getUserId());
    }
    public void postTweet(String tweet) {
        newsFeed.add(tweet);
        for (String followerId : AdminPanel.getUser(userId).getFollowers()) {
            AdminPanel.getUser(followerId).getNewsFeed().add(tweet);
        }
    }
}
