package app.com.augmentedrealitytraining.imr.dal;

import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.query.ExecutableQuery;

import java.util.List;

/**
 * Created by saurabh on 9/14/15.
 */
class DalUtils {

    public static List executeQueryForResults(ExecutableQuery<?> executableQuery) {
        try {
            return executableQuery.execute().get();
        } catch (Exception e) {
            return null;
        }
    }

    public static Object executeQueryForSingleResult(ExecutableQuery<?> executableQuery) {
        MobileServiceList<?> results = null;
        try {
            results = executableQuery.execute().get();
        } catch (Exception e) {
            return null;
        }
        if (results.size() == 1)
            return results.get(0);
        else
            return null;
    }
}
