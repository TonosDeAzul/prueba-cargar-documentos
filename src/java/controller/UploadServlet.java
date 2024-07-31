package controller;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        // Get post details from request
        String title = request.getParameter("title");
        String description = request.getParameter("description");

        // Get file part
        Part filePart = request.getPart("file");
        String fileName = getFileName(filePart);
        String fileType = filePart.getContentType();
        
        InputStream inputStream = null;
        
        if (filePart != null) {
            // Obtain input stream of the uploaded file
            inputStream = filePart.getInputStream();
        }

        Connection conn = null;
        String message = null;

        try {
            // Connect to the database
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            conn = DriverManager.getConnection(dbURL, dbUser, dbPass);

            // Insert post details
            String postSql = "INSERT INTO posts (title, description) VALUES (?, ?)";
            PreparedStatement postStatement = conn.prepareStatement(postSql, Statement.RETURN_GENERATED_KEYS);
            postStatement.setString(1, title);
            postStatement.setString(2, description);
            int affectedRows = postStatement.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating post failed, no rows affected.");
            }

            // Retrieve the generated post ID
            ResultSet generatedKeys = postStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                long postId = generatedKeys.getLong(1);

                // Insert file details
                String fileSql = "INSERT INTO uploaded_files (file_name, file_type, file_data, post_id) VALUES (?, ?, ?, ?)";
                PreparedStatement fileStatement = conn.prepareStatement(fileSql);
                fileStatement.setString(1, fileName);
                fileStatement.setString(2, fileType);
                
                if (inputStream != null) {
                    fileStatement.setBlob(3, inputStream);
                }
                
                fileStatement.setLong(4, postId);

                int row = fileStatement.executeUpdate();
                if (row > 0) {
                    message = "File uploaded and saved into database with post";
                }
            }
        } catch (Exception ex) {
            message = "ERROR: " + ex.getMessage();
            ex.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            request.setAttribute("Message", message);
            getServletContext().getRequestDispatcher("/message.jsp").forward(request, response);
        }
    }

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
