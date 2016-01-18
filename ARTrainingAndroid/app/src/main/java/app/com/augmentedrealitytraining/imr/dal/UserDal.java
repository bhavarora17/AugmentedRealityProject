package app.com.augmentedrealitytraining.imr.dal;

import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import app.com.augmentedrealitytraining.imr.pojo.User;

/**
 * Created by saurabh on 9/14/15.
 */
public class UserDal {

    private static MobileServiceTable<User> userMobileServiceTable = IMRClient.getImrClient().getTable(User.class);

    public static void insertUser(User user) {
        userMobileServiceTable.insert(user);
    }

    public static void updateUser(User user) {
        userMobileServiceTable.update(user);
    }

    public static User getUserByEmail(String email) {
        return (User) DalUtils.executeQueryForSingleResult(userMobileServiceTable.where().field("email").eq(email));
    }

    public static User getUserByPassword(String password) {
        return (User) DalUtils.executeQueryForSingleResult(userMobileServiceTable.where().field("password").eq(password));
    }
}
