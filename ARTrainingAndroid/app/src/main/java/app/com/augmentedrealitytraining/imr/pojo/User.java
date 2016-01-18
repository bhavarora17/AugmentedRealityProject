package app.com.augmentedrealitytraining.imr.pojo;


public class User {

    @com.google.gson.annotations.SerializedName("id")
    private String id;
    @com.google.gson.annotations.SerializedName("firstname")
    private String firstName;
    @com.google.gson.annotations.SerializedName("lastname")
    private String lastName;
    @com.google.gson.annotations.SerializedName("email")
    private String email;
    @com.google.gson.annotations.SerializedName("password")
    private String password;

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String str) {
        firstName = str;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String str) {
        lastName = str;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String str) {
        email = str;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String str) {
        password = str;
    }


}
