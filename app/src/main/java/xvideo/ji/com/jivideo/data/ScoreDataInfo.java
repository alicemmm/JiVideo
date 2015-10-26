package xvideo.ji.com.jivideo.data;


public class ScoreDataInfo {
    private String title;
    private int operate;
    private String userId;
    private int opPoint;
    private int pointType;
    private int expenseScoreId = -1;
    private boolean isUploadSuccess;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getOperate() {
        return operate;
    }

    public void setOperate(int operate) {
        this.operate = operate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getOpPoint() {
        return opPoint;
    }

    public void setOpPoint(int opPoint) {
        this.opPoint = opPoint;
    }

    public int getPointType() {
        return pointType;
    }

    public void setPointType(int pointType) {
        this.pointType = pointType;
    }

    public int getExpenseScoreId() {
        return expenseScoreId;
    }

    public void setExpenseScoreId(int expenseScoreId) {
        this.expenseScoreId = expenseScoreId;
    }

    public boolean isUploadSuccess() {
        return isUploadSuccess;
    }

    public void setIsUploadSuccess(boolean isUploadSuccess) {
        this.isUploadSuccess = isUploadSuccess;
    }
}
