import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/downloadFileServlet")
public class DownloadFileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database connection settings
    private String dbURL = "jdbc:mysql://localhost:3306/prubaarchivos";
    private String dbUser = "root";
    private String dbPass = "";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the file ID from the request
        String fileId = request.getParameter("fileId");
        
        Connection conn = null;

        try {
            // Connect to the database
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            conn = DriverManager.getConnection(dbURL, dbUser, dbPass);

            // Prepare SQL statement
            String sql = "SELECT file_name, file_type, file_data FROM uploaded_files WHERE id = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(fileId));

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Get file details
                String fileName = resultSet.getString("file_name");
                String fileType = resultSet.getString("file_type");
                InputStream fileData = resultSet.getBinaryStream("file_data");

                // Set response headers
                response.setContentType(fileType);
                response.setContentLength((int) resultSet.getBlob("file_data").length());
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

                // Write file data to response
                OutputStream outStream = response.getOutputStream();
                byte[] buffer = new byte[4096];
                int bytesRead = -1;

                while ((bytesRead = fileData.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }

                fileData.close();
                outStream.close();
            } else {
                // No file found for the given ID
                response.getWriter().print("File not found for the ID: " + fileId);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            response.getWriter().print("Error: " + ex.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
