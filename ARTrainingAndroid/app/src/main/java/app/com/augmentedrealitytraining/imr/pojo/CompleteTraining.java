package app.com.augmentedrealitytraining.imr.pojo;


public class CompleteTraining {
    @com.google.gson.annotations.SerializedName("id")
    private String id;
    @com.google.gson.annotations.SerializedName("email")
    private String email;
    @com.google.gson.annotations.SerializedName("trainingtitle")
        private String trainingTitle;

    public CompleteTraining(String user, String training)
    {
        email=user;
        trainingTitle=training;
    }

    public String getEmail()
    {
        return email;
    }

    public String getTrainingTitle()
    {
        return trainingTitle;
    }
}
