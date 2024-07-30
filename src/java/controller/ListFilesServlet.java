import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/listFilesServlet")
public class ListFilesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database connection settings
    private String dbURL = "jdbc:mysql://localhost:3306/prubaarchivos";
    private String dbUser = "root";
    private String dbPass = "";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection conn = null;
        List<String> fileNames = new ArrayList<>();
        List<Integer> fileIds = new ArrayList<>();

        try {
            // Connects to the database
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            conn = DriverManager.getConnection(dbURL, dbUser, dbPass);

            // Fetches file names and IDs
            String sql = "SELECT id, file_name FROM uploaded_files";
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                fileIds.add(resultSet.getInt("id"));
                fileNames.add(resultSet.getString("file_name"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        request.setAttribute("fileNames", fileNames);
        request.setAttribute("fileIds", fileIds);
        request.getRequestDispatcher("/listFiles.jsp").forward(request, response);
    }
}
