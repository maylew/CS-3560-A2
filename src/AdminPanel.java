import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AdminPanel {
    private static final Map<String, User> users = new HashMap<>();
    private static final Map<String, UserGroup> groups = new HashMap<>();
    private JTree treeView;
    private JFrame frame;
    private int userTotal;
    private int groupTotal;
    private int messageTotal;
    private int positiveMessageTotal;
    private DefaultMutableTreeNode currentNode;
    private String currentGroup;
    //private constructor
    AdminPanel() {
        treeView = null;
        frame = null;
        userTotal = 0;
        groupTotal = 0;
        messageTotal = 0;
        positiveMessageTotal = 0;
        currentNode = null;
        currentGroup = "";
    }
    public static User getUser(String userId) {
        return users.get(userId);
    }
    public  void createUser(String userId) {
        if (!users.containsKey(userId)) {
            users.put(userId, new User(userId));
            users.get(userId).setCreationTime(System.currentTimeMillis());
        }
        userTotal++;
    }
    public void createGroup(String groupId) {
        if (!groups.containsKey(groupId)) {
            groups.put(groupId, new UserGroup(groupId));
            groups.get(groupId).setCreationTime(System.currentTimeMillis());
        }
        groupTotal++;
    }
    public static void addUserToGroup(String userId, String groupId) {
        if (users.containsKey(userId) && groups.containsKey(groupId)) {
            groups.get(groupId).addUser(users.get(userId));
        }
    }
    public static void addGroupToGroup(String subgroupId, String groupId) {
        if (groups.containsKey(subgroupId) && groups.containsKey(groupId)) {
            groups.get(groupId).addGroup(groups.get(subgroupId));
        }
    }
    public int totalUsers() {
        return userTotal;
    }
    public int totalGroups() {
        return groupTotal;
    }
    public int totalMessages() {
        return messageTotal;
    }
    public double positiveMessagePercentage() {

        return (double) positiveMessageTotal /messageTotal * 100.0;
    }
    public void setCurrentNode (DefaultMutableTreeNode selectedNode) {
        currentNode = selectedNode;
    }
    public void setCurrentGroup (String selectedGroup) {
        currentGroup = selectedGroup;
    }
    public String getCurrentGroup() {
        return currentGroup;
    }
    public String getLastUpdatedUser() {
        long max = 0;
        String maxUser = "";
        for(User user: users.values()){
            if(max < user.getLastUpdateTime()){
                max = user.getLastUpdateTime();
                maxUser = user.getUserId();
            }
        }
        return maxUser;
    }
    void buildUI() {
        frame = new JFrame("Admin Control Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 450);

        //tree
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Root");
        treeView = new JTree(rootNode);
        treeView.setRootVisible(true);
        treeView.setShowsRootHandles(true);
        JScrollPane treeScrollPane = new JScrollPane(treeView);
        treeScrollPane.setPreferredSize(new Dimension(200, 600));
        frame.add(treeScrollPane, BorderLayout.WEST);
        setCurrentNode(rootNode);
        setCurrentGroup("Root");

        //adding groups and users
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(0, 2, 10, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField userField = new JTextField();
        userField.setToolTipText("User ID");
        JButton addUserButton = new JButton("Add User");
        addUserButton.addActionListener(e -> {
            String userId = userField.getText().trim();
            if (!userId.isEmpty()&&!users.containsKey(userId)) {
                createUser(userId);
                DefaultMutableTreeNode userNode = new DefaultMutableTreeNode(userId);
                currentNode.add(userNode);
                addUserToGroup(userId,getCurrentGroup());

                ((DefaultTreeModel) treeView.getModel()).reload();
                userField.setText("");
            }
        });

        JTextField groupField = new JTextField();
        groupField.setToolTipText("Group ID");
        JButton addGroupButton = new JButton("Add Group");
        addGroupButton.addActionListener(e -> {
            String groupId = groupField.getText().trim();
            if (!groupId.isEmpty()&&!groups.containsKey(groupId)) {
                createGroup(groupId);
                DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(groupId);
                currentNode.add(groupNode);
                addGroupToGroup(groupId,getCurrentGroup());

                ((DefaultTreeModel) treeView.getModel()).reload();
                groupField.setText("");

                //set this groupNode as the next head
                setCurrentNode(groupNode);
                setCurrentGroup(groupId);
            }
        });

        //this is just the button to open it
        JButton openUserViewButton = new JButton("Open User View");
        openUserViewButton.addActionListener(e -> {
            TreePath selectedPath = treeView.getSelectionPath();
            if (selectedPath != null) {
                Object selectedNode = selectedPath.getLastPathComponent();
                if (selectedNode instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode selectedTreeNode = (DefaultMutableTreeNode) selectedNode;
                    String selectedValue = selectedTreeNode.getUserObject().toString();
                    if (users.containsKey(selectedValue)) {
                        openUserView(selectedValue);
                    }
                }
            }
        });
       //verification button
        JButton verifyUsersButton = new JButton("Verify Users");
        verifyUsersButton.addActionListener(e -> {
            boolean verified = true;
            for(User user: users.values()){
                if(user.getUserId().contains(" ")){
                    verified = false;
                    break;
                }
            }
            showAlert("Verified", String.valueOf(verified));
        });
        //the "left" panel of buttons
        inputPanel.add(userField);
        inputPanel.add(addUserButton);
        inputPanel.add(groupField);
        inputPanel.add(addGroupButton);
        inputPanel.add(verifyUsersButton);
        inputPanel.add(openUserViewButton);

        frame.add(inputPanel, BorderLayout.CENTER);

        //the stats panel, in the A2 doc this was on the bottom, but I found it easier to put on the right
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(0, 1, 10, 5));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton totalUsersButton = new JButton("Show User Total");
        totalUsersButton.addActionListener(e -> showAlert("Total Users", String.valueOf(totalUsers())));

        JButton totalGroupsButton = new JButton("Show Group Total");
        totalGroupsButton.addActionListener(e -> showAlert("Total Groups", String.valueOf(totalGroups())));

        JButton totalMessagesButton = new JButton("Show Messages Total");
        totalMessagesButton.addActionListener(e -> showAlert("Total Tweets", String.valueOf(totalMessages())));

        JButton positiveTweetsButton = new JButton("Show Positive Percentage");
        positiveTweetsButton.addActionListener(e -> showAlert("Positive Tweets %", String.format("%.2f%%", positiveMessagePercentage())));

        JButton lastUpdatedButton = new JButton("Last Updated User");
        lastUpdatedButton.addActionListener(e -> showAlert("Last Updated User", getLastUpdatedUser()));

        // the "right" panel of buttons
        statsPanel.add(totalUsersButton);
        statsPanel.add(totalGroupsButton);
        statsPanel.add(totalMessagesButton);
        statsPanel.add(positiveTweetsButton);
        statsPanel.add(lastUpdatedButton);

        frame.add(statsPanel, BorderLayout.EAST);

        frame.setVisible(true);
    }
    private void showAlert(String title, String message) {
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    private void openUserView(String userId) {
        JFrame userFrame = new JFrame("User View - " + userId);
        userFrame.setSize(400, 500);
        userFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        userFrame.setLocationRelativeTo(frame);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //follow field and button
        JPanel followPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField followField = new JTextField(22);
        followField.setToolTipText("User ID");
        JButton followButton = new JButton("Follow User");
        followPanel.add(followField);
        followPanel.add(followButton);

        //followings
        JLabel followingsLabel = new JLabel("Following:");
        followingsLabel.setHorizontalAlignment(SwingConstants.LEFT);
        DefaultListModel<String> followingsListModel = new DefaultListModel<>();
        followingsListModel.addAll(users.get(userId).getFollowings());
        JList<String> followingsList = new JList<>(followingsListModel);
        JScrollPane followingsScrollPane = new JScrollPane(followingsList);

        followButton.addActionListener(e -> {
            String followId = followField.getText().trim();
            if (!followId.isEmpty() && users.containsKey(followId)&& !userId.equals(followId) && !users.get(userId).getFollowings().contains(followId) ) {
                users.get(userId).follow(users.get(followId));
                users.get(followId).followedBy(users.get(userId));
                followingsListModel.addElement(followId);
                followField.setText("");
            }
        });

        //post tweets
        JPanel tweetPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField tweetField = new JTextField(22);
        tweetField.setToolTipText("Tweet Message");
        JButton postTweetButton = new JButton("Post Tweet");
        tweetPanel.add(tweetField);
        tweetPanel.add(postTweetButton);

        //the news feed
        JLabel newsFeedLabel = new JLabel("News Feed:");
        newsFeedLabel.setHorizontalAlignment(SwingConstants.LEFT);
        DefaultListModel<String> newsFeedListModel = new DefaultListModel<>();
        newsFeedListModel.addAll(users.get(userId).getNewsFeed());
        JList<String> newsFeedList = new JList<>(newsFeedListModel);
        JScrollPane newsFeedScrollPane = new JScrollPane(newsFeedList);

        //will also check for messages and positive messages
        postTweetButton.addActionListener(e -> {
            String tweet = tweetField.getText().trim();
            if (!tweet.isEmpty()) {
                users.get(userId).postTweet(userId+": "+tweet);
                newsFeedListModel.addElement(userId+": "+tweet);
                tweetField.setText("");
                messageTotal++;
                users.get(userId).setLastUpdateTime(System.currentTimeMillis());
                for (String Followers :  users.get(userId).getFollowers()){
                    users.get(Followers).setLastUpdateTime(System.currentTimeMillis());
                }
                if(tweet.toLowerCase().contains("good")||tweet.toLowerCase().contains("great")||tweet.toLowerCase().contains("excellent")){
                    positiveMessageTotal++;
                }
            }
        });

         //update and creation times
        JLabel creationTime = new JLabel("Creation Time: "+ users.get(userId).getCreationTime());
        JLabel updateTime = new JLabel("Update Time: "+ users.get(userId).getLastUpdateTime());


        mainPanel.add(followPanel);
        mainPanel.add(followingsLabel);
        mainPanel.add(followingsScrollPane);
        mainPanel.add(tweetPanel);
        mainPanel.add(newsFeedLabel);
        mainPanel.add(newsFeedScrollPane);
        mainPanel.add(creationTime);
        mainPanel.add(updateTime);

        userFrame.add(mainPanel);
        userFrame.setVisible(true);
    }
}
