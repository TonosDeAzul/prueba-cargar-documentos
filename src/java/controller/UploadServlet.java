import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet("/uploadServlet")
@MultipartConfig(maxFileSize = 16177215) // 16MB
public class UploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // Database connection settings
    private String dbURL = "jdbc:mysql://localhost:3306/prubaarchivos";
    private String dbUser = "root";
    private String dbPass = "";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Gets values of text fields
        Part filePart = request.getPart("file");
        String fileName = getFileName(filePart);
        String fileType = filePart.getContentType();
        
        InputStream inputStream = null;
        
        if (filePart != null) {
            // Obtains input stream of the upload file
            inputStream = filePart.getInputStream();
        }

        Connection conn = null; // Connection to the database
        String message = null;  // Message will be sent back to client

        try {
            // Connects to the database
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            conn = DriverManager.getConnection(dbURL, dbUser, dbPass);

            // Constructs SQL statement
            String sql = "INSERT INTO uploaded_files (file_name, file_type, file_data) values (?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, fileName);
            statement.setString(2, fileType);
            
            if (inputStream != null) {
                // Fetches input stream of the upload file for the blob column
                statement.setBlob(3, inputStream);
            }

            // Sends the statement to the database server
            int row = statement.executeUpdate();
            if (row > 0) {
                message = "File uploaded and saved into database";
            }
        } catch (Exception ex) {
            message = "ERROR: " + ex.getMessage();
            ex.printStackTrace();
        } finally {
            if (conn != null) {
                // Closes the database connection
                try {
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // Sets the message in request scope
            request.setAttribute("Message", message);

            // Forwards to the message page
            getServletContext().getRequestDispatcher("/message.jsp").forward(request, response);
        }
    }

    // Obtains the file name part from the content-disposition header
    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        for (String cd : contentDisposition.split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
}
