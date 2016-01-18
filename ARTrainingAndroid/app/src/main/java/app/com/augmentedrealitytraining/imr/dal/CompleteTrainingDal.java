package app.com.augmentedrealitytraining.imr.dal;

import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.util.List;

import app.com.augmentedrealitytraining.imr.pojo.CompleteTraining;

/**
 * Created by saurabh on 9/14/15.
 */
public class CompleteTrainingDal {

    private static MobileServiceTable<CompleteTraining> completeTrainingMobileServiceTable = IMRClient.getImrClient().getTable(CompleteTraining.class);

    public static void insertCompleteTraining(CompleteTraining training) {
        completeTrainingMobileServiceTable.insert(training);
    }

    public static List<CompleteTraining> getCompleteTrainingsByEmail(String email) {
        return DalUtils.executeQueryForResults(completeTrainingMobileServiceTable.where().field("email").eq(email));
    }
}
