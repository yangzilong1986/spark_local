package mahoutdemo;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.JDBCDataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by Joe.Kwan on 2018/11/5 11:17.
 */
public class MysqlDataMovieRecommend {
    private MysqlDataMovieRecommend() throws TasteException, IOException {

    }

    public static void main(String[] args) throws TasteException, IOException {
        File resultFile = new File("F:\\tmp\\tmp", "MysqlMovieRecommed.txt");
        // Mysql Connection
        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setDatabaseName("test");
        mysqlDataSource.setServerName("localhost");
        mysqlDataSource.setUser("root");
        mysqlDataSource.setPassword("741852");
        mysqlDataSource.setAutoReconnect(true);
        mysqlDataSource.setFailOverReadOnly(false);

        JDBCDataModel dataModel = new MySQLJDBCDataModel(mysqlDataSource,
                "taste_preferences", "user_id", "item_id", "preference", null);
        DataModel model = dataModel;

        // Recommendations
        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);

        // UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.5, similarity, model, 1.0);
        UserNeighborhood neighborhood = new NearestNUserNeighborhood(10, similarity, model);
        Recommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);

        try (PrintWriter writer = new PrintWriter(resultFile)) {
            for (int userID = 1; userID <= model.getNumUsers(); userID++) {
                List<RecommendedItem> recommendedItems = recommender.recommend(userID, 3);
                String line = userID + " : ";
                for (RecommendedItem recommendedItem : recommendedItems) {
                    line += recommendedItem.getItemID() + ":" + recommendedItem.getValue() + ",";
                }
                if (line.endsWith(",")) {
                    line = line.substring(0, line.length() - 1);
                }
                writer.write(line);
                writer.write('\n');
            }
        } catch (IOException ioe) {
            resultFile.delete();
            throw ioe;
        }
        System.out.println("Recommended for " + model.getNumUsers() +
                "users and saved them to " + resultFile.getAbsolutePath());
    }
}
